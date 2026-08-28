package com.hotel.controller;

import java.util.List;
import java.util.Locale;
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
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.entity.Venue;
import com.hotel.service.VenueService;

/**
 * 場地 REST API。
 */
@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private static final Map<String, String> STATUS_ALIASES =
            Map.ofEntries(
                    Map.entry("AVAILABLE", "AVAILABLE"),
                    Map.entry("MAINTENANCE", "MAINTENANCE"),
                    Map.entry("DISABLED", "DISABLED"),
                    Map.entry("可預約", "AVAILABLE"),
                    Map.entry("維護中", "MAINTENANCE"),
                    Map.entry("維修中", "MAINTENANCE"),
                    Map.entry("停用", "DISABLED"));

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    @GetMapping
    public ResponseEntity<List<Venue>> findAll() {
        return ResponseEntity.ok(
                venueService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venue> findById(
            @PathVariable Integer id) {

        return venueService.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody Venue venue) {

        String validationError =
                validateVenue(venue);

        if (validationError != null) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            validationError));
        }

        if (venueService.existsById(
                venue.getVenueId())) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message",
                            "場地 ID 已存在："
                                    + venue.getVenueId()));
        }

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(venueService.save(venue));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody Venue venue) {

        if (!venueService.existsById(id)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "找不到場地 ID：" + id));
        }

        venue.setVenueId(id);

        String validationError =
                validateVenue(venue);

        if (validationError != null) {
            return ResponseEntity.badRequest()
                    .body(Map.of(
                            "message",
                            validationError));
        }

        return ResponseEntity.ok(
                venueService.save(venue));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Integer id) {

        if (!venueService.existsById(id)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "找不到場地 ID：" + id));
        }

        try {
            venueService.deleteById(id);

            return ResponseEntity.ok(
                    Map.of(
                            "message",
                            "場地已刪除"));

        } catch (IllegalStateException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message",
                            e.getMessage()));

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message",
                            "此場地已有租借或其他關聯資料，無法刪除"));
        }
    }

    private String validateVenue(Venue venue) {

        if (venue.getVenueId() == null) {
            return "場地 ID 不可空白";
        }

        if (venue.getVenueName() == null
                || venue.getVenueName().isBlank()) {
            return "場地名稱不可空白";
        }

        if (venue.getVenueName().trim().length() > 50) {
            return "場地名稱不可超過 50 個字元";
        }

        venue.setVenueName(
                venue.getVenueName().trim());

        if (venue.getCapacity() == null
                || venue.getCapacity() <= 0) {
            return "場地容量必須大於 0";
        }

        if (venue.getPricePerDay() == null
                || venue.getPricePerDay() < 0) {
            return "每日價格不可小於 0";
        }

        if (venue.getVenueStatus() == null
                || venue.getVenueStatus().isBlank()) {
            return "場地狀態不可空白";
        }

        String statusKey =
                venue.getVenueStatus()
                        .trim()
                        .toUpperCase(Locale.ROOT);

        String normalized =
                STATUS_ALIASES.get(statusKey);

        if (normalized == null) {
            return "場地狀態只能是 AVAILABLE、MAINTENANCE 或 DISABLED";
        }

        venue.setVenueStatus(normalized);

        return null;
    }
}