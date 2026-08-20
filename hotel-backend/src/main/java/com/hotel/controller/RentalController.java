package com.hotel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.entity.Rental;
import com.hotel.service.RentalService;

/**
 * 場地租借 REST API。
 *
 * GET    /api/rentals
 * GET    /api/rentals/{id}
 * GET    /api/rentals?venueId=1001
 * POST   /api/rentals
 * PUT    /api/rentals/{id}
 * DELETE /api/rentals/{id}
 */
@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    /**
     * 查詢全部 Rental。
     *
     * 若有 venueId：
     * GET /api/rentals?venueId=1001
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
     * 查詢單筆。
     */
    @GetMapping("/{id}")
    public ResponseEntity<Rental> findById(
            @PathVariable Integer id) {

        return rentalService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    /**
     * 新增 Rental。
     */
    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody Rental rental) {

        try {

            Rental savedRental =
                    rentalService.create(rental);

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
     * 修改 Rental。
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

        // URL ID 為準。
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

    /**
     * 刪除 Rental。
     */
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
