package com.hotel.service; // 場地付款與綠界介接，不依賴其他付款模組。
import java.util.*; // 組裝固定付款參數與排序映射。
import java.net.*; // 驗證付款網址及標準表單編碼。
import java.nio.charset.StandardCharsets; // 固定使用 UTF8，避免中文簽章差異。
import java.security.MessageDigest; // 計算 SHA256 與固定時間比較。
import java.time.*; // 使用飯店時區及付款時間。
import java.time.format.DateTimeFormatter; // 綠界指定日期格式。
import com.hotel.repository.RentalPaymentRepository; // 僅寫入場地付款。
import org.springframework.core.env.Environment; // 從環境讀取測試設定。
import org.springframework.stereotype.Service; // 交由框架注入依賴。
import org.springframework.transaction.annotation.Transactional; // 所有付款更新使用交易。
import org.springframework.transaction.support.TransactionSynchronization; // 在提交後通知會員。
import org.springframework.transaction.support.TransactionSynchronizationManager; // 登記一次提交後通知。
import org.springframework.security.core.Authentication; // 付款前以登入身分驗證所有權。
import org.springframework.web.server.ResponseStatusException; // 回傳安全的 HTTP 錯誤。
import org.springframework.http.HttpStatus; // 區分禁止與衝突。
@Service // 本功能僅允許綠界 Stage。
public class RentalPaymentService { // 付款參數、驗證與冪等處理集中於此。
    public static final String STAGE="https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5"; // 官方測試網址，禁止正式扣款。
    private final RentalService rentals; // 沿用既有租借與會員解析。
    private final RentalPaymentRepository payments; // 付款資料列鎖與條件更新。
    private final RentalMailService mail; // 提交後寄送付款通知。
    private final Environment environment; // 不將金鑰寫入程式或前端。
    /** 建立場地付款服務及其資料與通知依賴。 */
    public RentalPaymentService(RentalService rentals, RentalPaymentRepository payments, RentalMailService mail, Environment environment) { // 注入場地專用服務。
        this.rentals=rentals; this.payments=payments; this.mail=mail; this.environment=environment; // 保存既有依賴。
    }
    /** 讀取必要的綠界環境設定。 */
    private String setting(String name) { // 缺設定時明確拒絕，不使用正式預設值。
        String value=environment.getProperty(name); // 僅讀取指定環境變數。
        if (value==null || value.isBlank()) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"尚未設定綠界 Stage 環境"); // 不輸出秘密內容。
        return value; // 傳給後端簽章或網址驗證。
    }
    /** 確認付款介接僅使用綠界 Stage 測試環境。 */
    private void stageOnly() { // 每次介接均檢查模式，避免環境誤設。
        if (!STAGE.equals(setting("ECPAY_PAYMENT_URL")) || !"3002607".equals(setting("ECPAY_MERCHANT_ID"))) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"僅允許綠界 Stage 測試商店"); // 綠界官方公開測試商店，只能使用於 Stage 測試環境。
    }
    /** 驗證並取得付款結果回呼網址。 */
    private String returnUrl() { // 回呼需外部可達，不自行建立穿透通道。
        String value=setting("ECPAY_RETURN_URL"); // 使用操作者提供的公開網址。
        URI uri=URI.create(value); // 以結構化網址檢查而非字串前綴。
        String host=uri.getHost(); // 只驗證主機名稱，不發起網路請求。
        if (!"https".equals(uri.getScheme()) || host==null || host.equalsIgnoreCase("localhost") || host.endsWith(".local") || host.matches("[0-9.]+") || host.contains(":") || uri.getUserInfo()!=null || (uri.getPort()!=-1 && uri.getPort()!=443) || !"/api/rental-payments/ecpay/return".equals(uri.getPath()) || uri.getQuery()!=null || value.length()>200) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"需要公開 HTTPS 的精確付款回呼網址"); // 拒絕本機與字面 IP，固定回呼路徑。
        return value; // 僅回傳驗證後網址。
    }
    /** 依綠界規格計算付款參數檢查碼。 */
    public static String checksum(Map<String,String> parameters, String key, String iv) { // 依官方全方位金流規則計算檢查碼。
        try { // 標準 JDK 已支援 SHA256。
            var sorted=new TreeMap<String,String>(String.CASE_INSENSITIVE_ORDER); // 參數名稱不分大小寫排序。
            parameters.forEach((name,value)->{ if (!"CheckMacValue".equalsIgnoreCase(name)) sorted.put(name,value); }); // 檢查碼本身不加入簽章。
            StringBuilder source=new StringBuilder("HashKey=").append(key); // 官方規則先放入金鑰。
            sorted.forEach((name,value)->source.append('&').append(name).append('=').append(value)); // 保留原始參數值再整串編碼。
            source.append("&HashIV=").append(iv); // 官方規則最後加入向量。
            String encoded=URLEncoder.encode(source.toString(),StandardCharsets.UTF_8).toLowerCase(Locale.ROOT).replace("%2d","-").replace("%5f","_").replace("%2e",".").replace("%21","!").replace("%2a","*").replace("%28","(").replace("%29",")"); // 對齊官方網頁編碼字元轉換。
            return HexFormat.of().withUpperCase().formatHex(MessageDigest.getInstance("SHA-256").digest(encoded.getBytes(StandardCharsets.UTF_8))); // 最終使用大寫十六進位。
        } catch (java.security.NoSuchAlgorithmException exception) { throw new IllegalStateException("無法使用 SHA256",exception); } // 不降級成不安全簽章。
    }
    /** 查詢使用者可存取租借的付款狀態。 */
    @Transactional(readOnly=true) // 歷史付款回應不寫入資料。
    public Map<String,Object> status(Integer rentalId, Authentication authentication) { // 先驗證本人或管理員。
        var rental=rentals.findAccessible(rentalId,authentication); // 不接受前端會員編號。
        var payment=payments.read(rental.getPaymentId()); // 金額來自建立當時保存值。
        return Map.of("rentalId",rentalId,"totalPrice",payment.get("total_price"),"paymentStatus",payment.get("payment_status")); // 不公開交易金鑰或其他會員資料。
    }
    /** 建立可提交至綠界 Stage 的結帳參數。 */
    @Transactional // 交易編號配置與讀取在同一鎖定交易。
    public Map<String,Object> checkout(Integer rentalId, Authentication authentication) { // 付款只允許本人。
        var rental=rentals.findAccessible(rentalId,authentication); // 先拒絕他人租借。
        if (!rental.getMemberId().equals(rentals.resolveMemberId(authentication.getName()))) throw new ResponseStatusException(HttpStatus.FORBIDDEN,"只能支付自己的租借"); // 管理權限不等於可替他人建立付款。
        if (!Set.of("PENDING","CONFIRMED","待確認","已確認").contains(rental.getRentalStatus()) || rental.getRentalDate().toLocalDate().isBefore(LocalDate.now(ZoneId.of("Asia/Taipei")))) throw new ResponseStatusException(HttpStatus.CONFLICT,"此租借目前不可付款"); // 取消、完成及過期不建立付款。
        stageOnly(); // 僅官方測試商店及 Stage 網址。
        String callback=returnUrl(); // 沒公開回呼設定就不建立交易號。
        String back=setting("ECPAY_CLIENT_BACK_URL"); // 回到會員中心只重新查詢狀態。
        URI backUri=URI.create(back); // 防止導回任意執行協定。
        if (!Set.of("https","http").contains(backUri.getScheme()) || backUri.getHost()==null || backUri.getUserInfo()!=null || back.length()>200) throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"返回網址設定錯誤"); // 僅允許網頁網址。
        String key=setting("ECPAY_HASH_KEY"),iv=setting("ECPAY_HASH_IV"); // 金鑰僅留在後端。
        var payment=payments.lock(rental.getPaymentId()); // 鎖定後判斷重複付款。
        if (!rental.getMemberId().equals(((Number)payment.get("member_id")).intValue())) throw new ResponseStatusException(HttpStatus.CONFLICT,"付款會員關聯不一致"); // 防止錯誤付款關聯。
        if (!"待付款".equals(payment.get("payment_status"))) throw new ResponseStatusException(HttpStatus.CONFLICT,"此付款已處理"); // 已付款不能產生第二筆有效交易。
        int amount=((Number)payment.get("total_price")).intValue(); // 不採用前端或目前場地價格。
        if (amount<=0) throw new ResponseStatusException(HttpStatus.CONFLICT,"信用卡付款金額必須大於零"); // 不提交零元信用卡交易。
        String trade=(String)payment.get("merchant_trade_no"); // 重新整理沿用既有交易號。
        if (trade==null) { // 僅第一次付款準備配置識別碼。
            trade="VR"+UUID.randomUUID().toString().replace("-","").substring(0,18); // 二十字元英數且由唯一索引再防碰撞。
            payments.assign(rental.getPaymentId(),trade); // 寫入一次後不覆蓋。
        }
        var parameters=new LinkedHashMap<String,String>(); // 僅回傳公開付款參數。
        parameters.put("MerchantID",setting("ECPAY_MERCHANT_ID")); // 官方 Stage 商店識別。
        parameters.put("MerchantTradeNo",trade); // 與資料庫保存編號一致。
        parameters.put("MerchantTradeDate",LocalDateTime.now(ZoneId.of("Asia/Taipei")).format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss"))); // 官方要求的台灣交易時間格式。
        parameters.put("PaymentType","aio"); // 固定使用全方位金流。
        parameters.put("TotalAmount",Integer.toString(amount)); // 歷史付款金額轉整數字串。
        parameters.put("TradeDesc","Venue rental"); // 不傳送私人活動名稱。
        parameters.put("ItemName","Venue rental #"+rentalId); // 對應可追查的租借編號。
        parameters.put("ReturnURL",callback); // 伺服器付款通知的唯一入口。
        parameters.put("ClientBackURL",back); // 瀏覽器返回不代表付款成功。
        parameters.put("ChoosePayment","Credit"); // 僅信用卡一次付清，不加入分期參數。
        parameters.put("EncryptType","1"); // 使用 SHA256 檢查碼。
        parameters.put("CheckMacValue",checksum(parameters,key,iv)); // 後端簽章後才交給前端表單。
        return Map.of("action",STAGE,"parameters",parameters); // 不回傳金鑰或向量。
    }
    /** 驗證付款回呼並以冪等方式更新付款狀態。 */
    @Transactional // 回呼驗證及狀態更新必須原子完成。
    public void callback(Map<String,String> parameters) { // 接收表單原始參數值。
        stageOnly(); // 不接受正式環境設定。
        String supplied=parameters.getOrDefault("CheckMacValue",""); // 缺簽章視為無效。
        String computed=checksum(parameters,setting("ECPAY_HASH_KEY"),setting("ECPAY_HASH_IV")); // 將所有回呼參數納入檢查。
        if (!MessageDigest.isEqual(computed.getBytes(StandardCharsets.US_ASCII),supplied.getBytes(StandardCharsets.US_ASCII)) || !setting("ECPAY_MERCHANT_ID").equals(parameters.get("MerchantID"))) throw new IllegalArgumentException("付款通知驗證失敗"); // 驗證商店及簽章後才查資料。
        String merchant=parameters.getOrDefault("MerchantTradeNo",""); // 取得唯一特店編號。
        String trade=parameters.getOrDefault("TradeNo",""); // 取得綠界交易識別。
        if (!merchant.matches("[A-Za-z0-9]{1,20}") || !trade.matches("[A-Za-z0-9]{1,20}")) throw new IllegalArgumentException("付款交易編號無效"); // 拒絕未知格式與超長編號。
        var payment=payments.lockTrade(merchant); // 未知編號不建立任何付款資料。
        int amount=((Number)payment.get("total_price")).intValue(); // 回呼金額比對歷史保存值。
        if (!Integer.toString(amount).equals(parameters.get("TradeAmt"))) throw new IllegalArgumentException("付款金額不符"); // 金額不同不得入帳。
        if (!"0".equals(parameters.get("SimulatePaid"))) throw new IllegalArgumentException("不接受後台模擬付款通知"); // Stage 刷測試卡仍須走真實回呼流程。
        if (!"1".equals(parameters.get("RtnCode"))) return; // 失敗通知不把待付款改成已付款。
        if (!"Credit_CreditCard".equals(parameters.get("PaymentType"))) throw new IllegalArgumentException("付款方式不符"); // 本版只接受信用卡一次付清結果。
        if ("已付款".equals(payment.get("payment_status"))) { // 重送成功通知不重複更新或寄信。
            if (!trade.equals(payment.get("ecpay_trade_no"))) throw new IllegalArgumentException("付款交易識別不一致"); // 不允許不同交易冒用已付款紀錄。
            return; // 相同通知由控制器安全回應成功。
        }
        if (!"待付款".equals(payment.get("payment_status"))) throw new IllegalArgumentException("付款狀態不可轉換"); // 不覆蓋不明付款狀態。
        int id=((Number)payment.get("payment_id")).intValue(); // 只更新已匹配的付款。
        var rental=payments.rentalFor(id); // 確認租借仍存在以保存可追查關聯。
        if (((Number)rental.get("member_id")).intValue()!=((Number)payment.get("member_id")).intValue()) throw new IllegalArgumentException("付款會員關聯不符"); // 防止不一致的歷史關聯。
        var at=LocalDateTime.parse(parameters.get("PaymentDate"),DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")); // 付款時間採驗證過的通知值。
        if (payments.paid(id,trade,at)!=1) throw new IllegalStateException("付款狀態更新失敗"); // 條件更新不成功則回滾。
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() { // 通知只在交易成功後觸發一次。
            /** 在付款交易提交後寄送通知。 */
            @Override public void afterCommit() { mail.send(rental,amount,trade); } // 郵件服務自行攔截錯誤，不影響綠界回應。
        });
    }
}
