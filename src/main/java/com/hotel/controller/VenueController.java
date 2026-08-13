package com.hotel.controller;

import org.springframework.web.bind.annotation.*;

import com.hotel.dto.ApiResponse;
import com.hotel.dto.VenueDTO;
import com.hotel.entity.Venue;
import com.hotel.service.VenueService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 場地管理 API 控制器
 * 所有方法返回 JSON 格式的 ApiResponse 資料
 */
@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    /**
     * 獲取所有場地列表
     * 
     * @return 場地列表的 JSON 回應
     */
    @GetMapping
    public ApiResponse<List<VenueDTO>> getAllVenues() {
        List<Venue> venues = venueService.findAll();
        List<VenueDTO> dtos = venues.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ApiResponse.success(dtos, "場地列表載入成功");
    }

    /**
     * 獲取單個場地詳情
     * 
     * @param id 場地 ID
     * @return 場地詳情的 JSON 回應
     */
    @GetMapping("/{id}")
    public ApiResponse<VenueDTO> getVenueById(@PathVariable Integer id) {
        Venue venue = venueService.findById(id).orElse(null);
        if (venue == null) {
            return ApiResponse.error(404, "場地不存在");
        }
        return ApiResponse.success(convertToDTO(venue), "場地詳情載入成功");
    }

    /**
     * 建立新場地
     * 
     * @param venue 場地資訊 (JSON 請求體)
     * @return 建立結果的 JSON 回應
     */
    @PostMapping
    public ApiResponse<VenueDTO> createVenue(@RequestBody Venue venue) {
        try {
            if (venue.getVenueStatus() == null || venue.getVenueStatus().isEmpty()) {
                venue.setVenueStatus("AVAILABLE");
            }
            Venue savedVenue = venueService.save(venue);
            return ApiResponse.success(convertToDTO(savedVenue), "場地建立成功");
        } catch (Exception e) {
            return ApiResponse.error(400, "場地建立失敗: " + e.getMessage());
        }
    }

    /**
     * 更新場地
     * 
     * @param id    場地 ID
     * @param venue 更新的場地資訊 (JSON 請求體)
     * @return 更新結果的 JSON 回應
     */
    @PutMapping("/{id}")
    public ApiResponse<VenueDTO> updateVenue(
            @PathVariable Integer id,
            @RequestBody Venue venue) {
        try {
            Venue existingVenue = venueService.findById(id).orElse(null);
            if (existingVenue == null) {
                return ApiResponse.error(404, "場地不存在");
            }

            existingVenue.setVenueName(venue.getVenueName());
            existingVenue.setCapacity(venue.getCapacity());
            existingVenue.setPricePerDay(venue.getPricePerDay());
            existingVenue.setVenueStatus(venue.getVenueStatus());

            Venue updatedVenue = venueService.save(existingVenue);
            return ApiResponse.success(convertToDTO(updatedVenue), "場地更新成功");
        } catch (Exception e) {
            return ApiResponse.error(400, "場地更新失敗: " + e.getMessage());
        }
    }

    /**
     * 刪除場地
     * 
     * @param id 場地 ID
     * @return 刪除結果的 JSON 回應
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteVenue(@PathVariable Integer id) {
        try {
            Venue venue = venueService.findById(id).orElse(null);
            if (venue == null) {
                return ApiResponse.error(404, "場地不存在");
            }
            venueService.deleteById(id);
            return ApiResponse.success("場地已刪除", "場地刪除成功");
        } catch (Exception e) {
            return ApiResponse.error(400, "場地刪除失敗: " + e.getMessage());
        }
    }

    /**
     * 將 Venue entity 轉換為 VenueDTO
     * 
     * @param venue Venue entity
     * @return VenueDTO
     */
    private VenueDTO convertToDTO(Venue venue) {
        if (venue == null) {
            return null;
        }
        return new VenueDTO(
                venue.getVenueId(),
                venue.getVenueName(),
                null,
                null,
                venue.getCapacity(),
                venue.getVenueStatus());
    }
}
