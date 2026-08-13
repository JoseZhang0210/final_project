package com.hotel.controller;

import org.springframework.web.bind.annotation.*;

import com.hotel.dto.ApiResponse;
import com.hotel.dto.RoomTypeDTO;
import com.hotel.entity.RoomType;
import com.hotel.service.RoomTypeService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 房間類型 API 控制器
 * 所有方法返回 JSON 格式的 ApiResponse 資料
 */
@RestController
@RequestMapping("/api/room-types")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    /**
     * 獲取所有房間類型列表
     * 
     * @return 房間類型列表的 JSON 回應
     */
    @GetMapping
    public ApiResponse<List<RoomTypeDTO>> getAllRoomTypes() {
        List<RoomType> roomTypes = roomTypeService.findAll();
        List<RoomTypeDTO> dtos = roomTypes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ApiResponse.success(dtos, "房間類型列表載入成功");
    }

    /**
     * 獲取單個房間類型詳情
     * 
     * @param id 房間類型 ID
     * @return 房間類型詳情的 JSON 回應
     */
    @GetMapping("/{id}")
    public ApiResponse<RoomTypeDTO> getRoomTypeById(@PathVariable Integer id) {
        RoomType roomType = roomTypeService.findById(id).orElse(null);
        if (roomType == null) {
            return ApiResponse.error(404, "房間類型不存在");
        }
        return ApiResponse.success(convertToDTO(roomType), "房間類型詳情載入成功");
    }

    /**
     * 建立新房間類型
     * 
     * @param roomType 房間類型資訊 (JSON 請求體)
     * @return 建立結果的 JSON 回應
     */
    @PostMapping
    public ApiResponse<RoomTypeDTO> createRoomType(@RequestBody RoomType roomType) {
        try {
            RoomType savedRoomType = roomTypeService.insert(roomType);
            return ApiResponse.success(convertToDTO(savedRoomType), "房間類型建立成功");
        } catch (Exception e) {
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("FK_room_type_image")) {
                return ApiResponse.error(400, "圖片 ID 填寫不正確：資料庫中找不到此圖片 ID！");
            } else {
                return ApiResponse.error(400, "房間類型建立失敗: " + e.getMessage());
            }
        }
    }

    /**
     * 更新房間類型
     * 
     * @param id       房間類型 ID
     * @param roomType 更新的房間類型資訊 (JSON 請求體)
     * @return 更新結果的 JSON 回應
     */
    @PutMapping("/{id}")
    public ApiResponse<RoomTypeDTO> updateRoomType(
            @PathVariable Integer id,
            @RequestBody RoomType roomType) {
        try {
            roomType.setRoomTypeId(id);
            RoomType updatedRoomType = roomTypeService.update(id, roomType);
            return ApiResponse.success(convertToDTO(updatedRoomType), "房間類型更新成功");
        } catch (Exception e) {
            return ApiResponse.error(400, "房間類型更新失敗: " + e.getMessage());
        }
    }

    /**
     * 刪除房間類型
     * 
     * @param id 房間類型 ID
     * @return 刪除結果的 JSON 回應
     */
    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteRoomType(@PathVariable Integer id) {
        try {
            RoomType roomType = roomTypeService.findById(id).orElse(null);
            if (roomType == null) {
                return ApiResponse.error(404, "房間類型不存在");
            }
            roomTypeService.deleteById(id);
            return ApiResponse.success("房間類型已刪除", "房間類型刪除成功");
        } catch (Exception e) {
            return ApiResponse.error(400, "房間類型刪除失敗：該房型可能已被其他資料關聯！");
        }
    }

    /**
     * 將 RoomType entity 轉換為 RoomTypeDTO
     * 
     * @param roomType RoomType entity
     * @return RoomTypeDTO
     */
    private RoomTypeDTO convertToDTO(RoomType roomType) {
        if (roomType == null) {
            return null;
        }
        return new RoomTypeDTO(
                roomType.getRoomTypeId(),
                roomType.getTypeName(),
                roomType.getDescription(),
                roomType.getCapacity(),
                roomType.getPricePerNight() == null ? null : java.math.BigDecimal.valueOf(roomType.getPricePerNight()),
                null);
    }
}