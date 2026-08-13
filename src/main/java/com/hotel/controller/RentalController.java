package com.hotel.controller;

import org.springframework.web.bind.annotation.*;

import com.hotel.dto.ApiResponse;
import com.hotel.dto.RentalDTO;
import com.hotel.entity.Rental;
import com.hotel.service.RentalService;
import com.hotel.service.VenueService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 場地租借 API 控制器
 * 所有方法返回 JSON 格式的 ApiResponse 資料
 */
@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;
    private final VenueService venueService;

    public RentalController(
            RentalService rentalService,
            VenueService venueService) {
        this.rentalService = rentalService;
        this.venueService = venueService;
    }

    /**
     * 獲取所有租借紀錄列表
     * 
     * @return 租借紀錄列表的 JSON 回應
     */
    @GetMapping
    public ApiResponse<List<RentalDTO>> getAllRentals() {
        List<Rental> rentals = rentalService.findAll();
        List<RentalDTO> dtos = rentals.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ApiResponse.success(dtos, "租借紀錄列表載入成功");
    }

    /**
     * 獲取單個租借紀錄詳情
     * 
     * @param id 租借紀錄 ID
     * @return 租借紀錄詳情的 JSON 回應
     */
    @GetMapping("/{id}")
    public ApiResponse<RentalDTO> getRentalById(@PathVariable Integer id) {
        Rental rental = rentalService.findById(id).orElse(null);
        if (rental == null) {
            return ApiResponse.error(404, "租借紀錄不存在");
        }
        return ApiResponse.success(convertToDTO(rental), "租借紀錄詳情載入成功");
    }

    /**
     * 建立新租借紀錄
     * 
     * @param rental 租借資訊 (JSON 請求體)
     * @return 建立結果的 JSON 回應
     */
    @PostMapping
    public ApiResponse<RentalDTO> createRental(@RequestBody Rental rental) {
        try {
            if (rental.getRentalStatus() == null || rental.getRentalStatus().isEmpty()) {
                rental.setRentalStatus("PENDING");
            }
            Rental savedRental = rentalService.create(rental);
            return ApiResponse.success(convertToDTO(savedRental), "租借紀錄建立成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, "租借紀錄建立失敗: " + e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(400, "租借紀錄建立失敗: " + e.getMessage());
        }
    }

    /**
     * 更新租借紀錄
     * 
     * @param id     租借紀錄 ID
     * @param rental 更新的租借資訊 (JSON 請求體)
     * @return 更新結果的 JSON 回應
     */
    @PutMapping("/{id}")
    public ApiResponse<RentalDTO> updateRental(
            @PathVariable Integer id,
            @RequestBody Rental rental) {
        try {
            rental.setRentalId(id);
            Rental updatedRental = rentalService.update(rental);
            return ApiResponse.success(convertToDTO(updatedRental), "租借紀錄更新成功");
        } catch (IllegalArgumentException e) {
            return ApiResponse.error(400, "租借紀錄更新失敗: " + e.getMessage());
        } catch (Exception e) {
            return ApiResponse.error(400, "租借紀錄更新失敗: " + e.getMessage());
        }
    }

    /**
     * 刪除租借紀錄
     * 
     * @param id 租借紀錄 ID
     * @return 刪除結果的 JSON 回應
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteRental(@PathVariable Integer id) {
        try {
            Rental rental = rentalService.findById(id).orElse(null);
            if (rental == null) {
                return ApiResponse.error(404, "租借紀錄不存在");
            }
            rentalService.deleteById(id);
            return ApiResponse.success("租借紀錄已刪除", "租借紀錄刪除成功");
        } catch (Exception e) {
            return ApiResponse.error(400, "租借紀錄刪除失敗: " + e.getMessage());
        }
    }

    /**
     * 將 Rental entity 轉換為 RentalDTO
     * 
     * @param rental Rental entity
     * @return RentalDTO
     */
    private RentalDTO convertToDTO(Rental rental) {
        if (rental == null) {
            return null;
        }
        return new RentalDTO(
                rental.getRentalId(),
                rental.getEventName(),
                null,
                null,
                rental.getGuestCount(),
                rental.getRentalStatus());
    }
}
