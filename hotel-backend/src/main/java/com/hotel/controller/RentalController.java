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
import com.hotel.entity.Rental;
import com.hotel.service.RentalService;

/**
 * 場地租借 REST API。
 */
@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

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
            Integer venueId) {

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

    @GetMapping("/{id}")
    public ResponseEntity<Rental> findById(
            @PathVariable Integer id) {

        return rentalService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
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
    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody Rental rental) {

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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Integer id) {

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