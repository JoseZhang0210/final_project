package com.hotel.service; // 場地付款專用測試，不建立資料庫帳號。
import org.junit.jupiter.api.*; // 使用既有測試框架。
import static org.junit.jupiter.api.Assertions.*; // 驗證拒絕與成功結果。
import static org.mockito.Mockito.*; // 以模擬物件避免污染測試資料庫。
import java.util.*; // 組装回呼與付款欄位。
import java.time.*; // 建立未來租借日期。
import com.hotel.repository.RentalPaymentRepository; // 模擬付款持久化入口。
import com.hotel.model.entity.Rental; // 沿用現有租借實體。
import org.springframework.mock.env.MockEnvironment; // 測試設定不使用正式秘密。
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // 模擬既有會員登入。
import org.springframework.transaction.support.TransactionSynchronizationManager; // 驗證提交後才寄信。
class RentalPaymentServiceTest { // 測試簽章、金額、所有權與重送。
    private final RentalService rentals=mock(RentalService.class); // 不存取真實會員資料。
    private final RentalPaymentRepository repository=mock(RentalPaymentRepository.class); // 不寫入假付款。
    private final RentalMailService mail=mock(RentalMailService.class); // 不寄出測試郵件。
    private final String key="pwFHCqoQZGmho4w6",iv="EkRm7iFT261dpevs"; // 綠界官方公開測試憑證，只能使用於 Stage 測試環境。
    private final MockEnvironment env=new MockEnvironment(); // 每個測試使用獨立設定。
    private RentalPaymentService service; // 受測付款服務。
    private Map<String,Object> payment; // 模擬既有待付款資料。
    @BeforeEach void setup() { // 每次重建隔離的付款情境。
        env.withProperty("ECPAY_PAYMENT_URL",RentalPaymentService.STAGE).withProperty("ECPAY_MERCHANT_ID","3002607").withProperty("ECPAY_HASH_KEY",key).withProperty("ECPAY_HASH_IV",iv).withProperty("ECPAY_RETURN_URL","https://hotel.example.com/api/rental-payments/ecpay/return").withProperty("ECPAY_CLIENT_BACK_URL","https://hotel.example.com/member/rentals"); // 官方測試設定不觸發外部網路。
        service=new RentalPaymentService(rentals,repository,mail,env); // 使用模擬依賴建立服務。
        payment=new HashMap<>(Map.of("payment_id",1,"member_id",1,"total_price",5000,"payment_status","待付款","merchant_trade_no","VRexisting123")); // 模擬已準備交易號的付款。
        when(repository.lockTrade("VRexisting123")).thenReturn(payment); // 只有已知特店編號存在。
        when(repository.rentalFor(1)).thenReturn(Map.of("rental_id",1,"member_id",1)); // 付款與租借會員一致。
        when(repository.paid(eq(1),eq("260905000001"),any())).thenAnswer(call->{ payment.put("payment_status","已付款"); payment.put("ecpay_trade_no","260905000001"); return 1; }); // 模擬條件更新後的已付款狀態。
    }
    private Map<String,String> callback() { // 建立固定的有效測試通知。
        var values=new HashMap<>(Map.of("MerchantID","3002607","MerchantTradeNo","VRexisting123","TradeNo","260905000001","TradeAmt","5000","RtnCode","1","SimulatePaid","0","PaymentType","Credit_CreditCard","PaymentDate","2026/09/05 12:00:00")); // 不包含卡號或私人會員資料。
        sign(values); return values; // 加入有效簽章供各情境變化。
    }
    private void sign(Map<String,String> values) { values.put("CheckMacValue",RentalPaymentService.checksum(values,key,iv)); } // 使用官方規則產生測試回呼。
    @Test void officialChecksumVector() { // 固定官方範例可抓出編碼或排序實作錯誤。
        var values=Map.of("TradeDesc","促銷方案","PaymentType","aio","MerchantTradeDate","2023/03/12 15:30:23","MerchantTradeNo","ecpay20230312153023","MerchantID","3002607","ReturnURL","https://www.ecpay.com.tw/receive.php","ItemName","Apple iphone 15","TotalAmount","30000","ChoosePayment","ALL","EncryptType","1"); // 來源為綠界官方檢查碼文件。
        assertEquals("6C51C9E6888DE861FD62FB1DD17029FC742634498FD813DC43D4243B5685B840",RentalPaymentService.checksum(values,key,iv)); // 比對官方已知答案而非自己計算的答案。
    }
    @Test void invalidSignatureDoesNotReadOrWrite() { // 錯誤簽章不能碰付款資料。
        var values=callback(); values.put("CheckMacValue","INVALID"); // 模擬未授權通知。
        assertThrows(IllegalArgumentException.class,()->service.callback(values)); // 必須拒絕。
        verifyNoInteractions(repository,mail); // 不查資料也不寄信。
    }
    @Test void wrongAmountDoesNotPay() { // 即使簽章正確，金額仍需獨立核對。
        var values=callback(); values.put("TradeAmt","1"); sign(values); // 模擬不相符但有簽章的通知。
        assertThrows(IllegalArgumentException.class,()->service.callback(values)); // 金額不符必須拒絕。
        verify(repository,never()).paid(any(),any(),any()); // 不更新付款。
    }
    @Test void unknownTradeDoesNotPay() { // 未知編號不得自動建立付款。
        var values=callback(); values.put("MerchantTradeNo","unknown"); sign(values); // 使用不存在的交易號。
        when(repository.lockTrade("unknown")).thenThrow(new org.springframework.dao.EmptyResultDataAccessException(1)); // 模擬資料庫沒有紀錄。
        assertThrows(RuntimeException.class,()->service.callback(values)); // 未知交易必須失敗。
        verify(repository,never()).paid(any(),any(),any()); // 不建立或更新付款。
    }
    @Test void duplicateSuccessPaysAndMailsOnceAfterCommit() { // 重送只處理一次，信件等待提交。
        TransactionSynchronizationManager.initSynchronization(); // 模擬有效交易同步生命週期。
        try { // 測試後一定清除執行緒交易狀態。
            service.callback(callback()); service.callback(callback()); // 綠界重送相同成功通知。
            verify(repository,times(1)).paid(eq(1),eq("260905000001"),any()); // 付款僅更新一次。
            verifyNoInteractions(mail); // 提交前絕不寄信。
            TransactionSynchronizationManager.getSynchronizations().forEach(sync->sync.afterCommit()); // 模擬交易確實成功提交。
            verify(mail,times(1)).send(any(),eq(5000),eq("260905000001")); // 相同通知只產生一次信件。
            assertEquals("已付款",payment.get("payment_status")); // 統一沿用中文付款狀態。
        } finally { TransactionSynchronizationManager.clearSynchronization(); } // 不污染其他測試。
    }
    @Test void simulationDoesNotPay() { // 後台模擬通知不視為真實 Stage 刷卡流程。
        var values=callback(); values.put("SimulatePaid","1"); sign(values); // 模擬後台測試按鈕。
        assertThrows(IllegalArgumentException.class,()->service.callback(values)); // 不標記付款成功。
        verify(repository,never()).paid(any(),any(),any()); // 不更新持久化資料。
    }
    @Test void checkoutUsesSavedAmountAndReusesTrade() { // 付款只讀歷史金額並重用交易號。
        var authentication=new UsernamePasswordAuthenticationToken("customer01",null,List.of()); // 模擬既有會員。
        Rental rental=new Rental(); rental.setMemberId(1); rental.setPaymentId(1); rental.setRentalStatus("PENDING"); rental.setRentalDate(LocalDate.now().plusDays(10).atStartOfDay()); // 只在記憶體建立測試租借。
        when(rentals.findAccessible(1,authentication)).thenReturn(rental); when(rentals.resolveMemberId("customer01")).thenReturn(1); when(repository.lock(1)).thenReturn(payment); // 對應本人與待付款。
        var result=service.checkout(1,authentication); var parameters=(Map<?,?>)result.get("parameters"); // 檢查公開表單參數。
        assertEquals("5000",parameters.get("TotalAmount")); assertEquals("VRexisting123",parameters.get("MerchantTradeNo")); // 使用歷史金額與原編號。
        verify(repository,never()).assign(any(),any()); // 重新準備不配置第二個交易編號。
        payment.put("payment_status","已付款"); // 模擬回呼完成後再次按付款。
        assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service.checkout(1,authentication)); // 已付款不可重付。
    }
    @Test void checkoutRejectsOtherMember() { // 即使能取得實體仍須驗證付款本人。
        var authentication=new UsernamePasswordAuthenticationToken("customer01",null,List.of()); // 使用既有會員名稱。
        Rental rental=new Rental(); rental.setMemberId(2); // 他人的租借資料。
        when(rentals.findAccessible(2,authentication)).thenReturn(rental); when(rentals.resolveMemberId("customer01")).thenReturn(1); // 模擬錯誤暴露實體的上游。
        assertThrows(org.springframework.web.server.ResponseStatusException.class,()->service.checkout(2,authentication)); // 付款服務仍獨立拒絕。
        verifyNoInteractions(repository); // 不讀寫他人付款。
    }
}
