package com.hotel.controller; // 場地付款專用入口。
import com.hotel.service.RentalPaymentService; // 商業邏輯留在服務層。
import org.springframework.web.bind.annotation.*; // 宣告精確付款路由。
import org.springframework.security.core.Authentication; // 使用現有登入資料。
import org.springframework.http.ResponseEntity; // 回呼使用純文字回應。
import org.springframework.util.MultiValueMap; // 防止重複參數造成驗證歧義。
@RestController // 回傳 JSON 或純文字而非付款成功 HTML。
@RequestMapping("/api/rental-payments") // 只屬於場地模組。
public class RentalPaymentController { // 控制器不處理付款狀態邏輯。
    private final RentalPaymentService service; // 注入專用服務。
    /** 將付款存取例外轉為原始 HTTP 狀態。 */
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class) // 保留付款所有權與設定錯誤的正確狀態。
    public ResponseEntity<?> accessError(org.springframework.web.server.ResponseStatusException exception) { // 僅影響場地付款控制器。
        return ResponseEntity.status(exception.getStatusCode()).body(java.util.Map.of("message", exception.getReason()==null ? "付款暫時無法處理" : exception.getReason())); // 不依賴共用例外處理的五百回應。
    }
    /** 建立使用指定付款服務的控制器。 */
    public RentalPaymentController(RentalPaymentService service) { this.service=service; } // 保存服務依賴。
    /** 查詢指定租借的付款狀態。 */
    @GetMapping("/rentals/{id}") // 只有本人或管理員可查历史付款。
    public Object status(@PathVariable Integer id, Authentication authentication) { return service.status(id,authentication); } // 由服务验证所有權。
    /** 建立指定租借的綠界 Stage 結帳參數。 */
    @PostMapping("/rentals/{id}/checkout") // 前端只傳租借編號，不接收價格。
    public Object checkout(@PathVariable Integer id, Authentication authentication) { return service.checkout(id,authentication); } // 返回後端簽章的 Stage 表單參數。
    /** 本機測試付款；仍要求登入會員，且 Service 會再驗證本人。 */
    @PostMapping("/rentals/{id}/stage-demo-paid")
    public Object stageDemoPaid(
        @PathVariable Integer id,
        Authentication authentication
    ) {
        return service.stageDemoPaid(id, authentication);
    }
    /** 驗證並處理綠界付款結果通知。 */
    @PostMapping(value="/ecpay/return",produces="text/plain") // 唯一不需要會員 JWT 的付款通知路徑。
    public ResponseEntity<String> callback(@RequestParam MultiValueMap<String,String> parameters) { // 綠界以表單傳遞資料。
        try { // 所有驗證失敗都不能回覆成功。
            if (parameters.values().stream().anyMatch(values->values.size()!=1)) throw new IllegalArgumentException("重複通知欄位"); // 拒絕重複鍵值歧義。
            if (parameters.keySet().stream().map(name->name.toLowerCase(java.util.Locale.ROOT)).distinct().count()!=parameters.size()) throw new IllegalArgumentException("重複通知欄位"); // 拒絕僅大小寫不同的重複簽章參數。
            service.callback(parameters.toSingleValueMap()); // 交易提交成功後才繼續。
            return ResponseEntity.ok("1|OK"); // 綠界要求的純文字成功回應。
        } catch (RuntimeException exception) { // 不將金鑰、通知全文或 SQL 錯誤回傳。
            return ResponseEntity.badRequest().body("0|Invalid notification"); // 驗證或持久化失敗讓綠界知道未成功處理。
        }
    }
}
