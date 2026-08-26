package com.hotel.controller;

import java.util.List;
import java.util.Locale;
import java.util.Map;

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

        return venueService
                .findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody(required = false) Venue venue) {

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of(
                        "message",
                        "場地固定為 A～D 四廳，由系統自動建立，不能新增"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable Integer id,
            @RequestBody Venue venue) {

        if (!venueService.isFixedId(id)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "場地 ID 只能是 1、2、3、4"));
        }

        if (venue == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            "場地資料不可空白"));
        }

        String status =
                normalizeStatus(
                        venue.getVenueStatus());

        if (status == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            "場地狀態只能是 AVAILABLE、MAINTENANCE 或 DISABLED"));
        }

        try {
            return ResponseEntity.ok(
                    venueService.updateStatus(
                            id,
                            status));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(
                            "message",
                            e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Integer id) {

        if (!venueService.isFixedId(id)) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "message",
                            "場地 ID 只能是 1、2、3、4"));
        }

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(Map.of(
                        "message",
                        "A～D 四個固定場地不可刪除，只能修改狀態"));
    }

    private String normalizeStatus(String status) {

        if (status == null || status.isBlank()) {
            return null;
        }

        String key =
                status.trim().toUpperCase(Locale.ROOT);

        return STATUS_ALIASES.get(key);
    }
}