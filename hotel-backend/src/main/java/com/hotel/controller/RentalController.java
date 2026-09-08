package com.hotel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.dto.RentalCreateRequest;
import com.hotel.model.entity.Rental;
import com.hotel.service.RentalService;

/**
 * 場地租借 REST API。
 */
@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    /** 將租借存取例外轉為原始 HTTP 狀態。 */
    @org.springframework.web.bind.annotation.ExceptionHandler(org.springframework.web.server.ResponseStatusException.class) // 避免共用例外處理把權限錯誤改成伺服器錯誤。
    public ResponseEntity<?> accessError(org.springframework.web.server.ResponseStatusException exception) { // 僅處理本控制器的權限與不存在結果。
        return ResponseEntity.status(exception.getStatusCode()).body(Map.of("message", exception.getReason() == null ? "租借存取失敗" : exception.getReason())); // 保留指定的四零三或四零四。
    }

    /** 建立使用指定租借服務的控制器。 */
    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    /**
     * 管理用途：查詢全部 Rental，
     * 或使用 venueId 篩選。
     */
    @GetMapping
    public ResponseEntity<List<Rental>> findAll(
            @RequestParam(required = false)
            Integer venueId, Authentication authentication) { // 管理列表需讀取登入權限。
        RentalService.requireManager(authentication); // 一般會員不能取得全部資料。

        if (venueId != null) {
            return ResponseEntity.ok(
                    rentalService.findByVenueId(venueId));
        }

        return ResponseEntity.ok(
                rentalService.findAll());
    }

    /**
     * 會員用途：只讀取目前登入會員自己的 Rental。
     */
    @GetMapping("/mine")
    public ResponseEntity<?> findMine(
            Authentication authentication) {

        try {
            return ResponseEntity.ok(
                    rentalService.findMine(
                            authentication.getName()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()));
        }
    }

    /** 依權限取得單筆租借。 */
    @GetMapping("/{id}")
    public ResponseEntity<Rental> findById(
            @PathVariable Integer id, Authentication authentication) { // 取得身分供單筆所有權驗證。
        return ResponseEntity.ok(rentalService.findAccessible(id, authentication)); // 由服務層拒絕非本人與不存在資料。
    }

    /**
     * V2.0 新增 Rental。
     *
     * request 不包含：
     * rentalId / memberId / paymentId / rentalStatus
     */
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody RentalCreateRequest request,
            Authentication authentication) {

        try {
            Rental savedRental =
                    rentalService.createForCurrentUser(
                            request,
                            authentication.getName());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(savedRental);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message",
                            "會員、付款或場地資料不存在，或資料違反資料庫限制"));
        }
    }

    /**
     * 保留管理端完整修改功能。
     */

    /**
     * 會員取消自己的場地預約。
     *
     * 不使用 DELETE，避免刪除租借及付款歷史。
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelMine(
            @PathVariable Integer id,
            Authentication authentication) {

        try {
            Rental cancelled =
                    rentalService.cancelForCurrentUser(
                            id,
                            authentication.getName());

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "預約已取消",
                            "rental",
                            cancelled));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()));
        }
    }
    /** 由管理員更新指定租借。 */
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody Rental rental, Authentication authentication) { // 更新只允許管理員。
        RentalService.requireManager(authentication); // 先驗證權限再查詢資料。

        if (rentalService.findById(id).isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "找不到租借 ID：" + id));
        }

        rental.setRentalId(id);

        try {
            return ResponseEntity.ok(
                    rentalService.update(rental));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message",
                            "會員、付款或場地資料不存在，或資料違反資料庫限制"));
        }
    }

    /** 查詢指定日期範圍內的場地占用日期。 */
    @GetMapping("/occupied-dates") // 提供不含私人欄位的占用日期。
    public ResponseEntity<?> occupied(@RequestParam(required = false) Integer venueId, @RequestParam java.time.LocalDate from, @RequestParam java.time.LocalDate to) { // 日期範圍由服務層限制。
        try { // 將輸入錯誤轉成明確回應。
            return ResponseEntity.ok(rentalService.occupied(venueId, from, to)); // 回傳最小占用資料。
        } catch (IllegalArgumentException exception) { // 不將錯誤日期視為伺服器故障。
            return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage())); // 提示使用者修正日期。
        }
    }

    /** 由管理員刪除指定租借紀錄。 */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Integer id, Authentication authentication) { // 刪除需驗證管理權限。
        RentalService.requireManager(authentication); // 會員不能刪除任何租借。

        if (!rentalService.deleteById(id)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "找不到租借 ID：" + id));
        }

        return ResponseEntity.ok(
                Map.of(
                        "message",
                        "租借紀錄已刪除"));
    }
}
