package com.hotel.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.dto.ApiResponse;

/**
 * 餐廳頁面 API 控制器
 * 所有方法返回 JSON 格式的 ApiResponse 資料
 */
@RestController
@RequestMapping("/api/restaurants")
public class RestaurantPageController {

    /**
     * 獲取餐廳列表頁面資料
     * 
     * @return 餐廳列表的 JSON 回應
     */
    @GetMapping("/list")
    public ApiResponse<Map<String, Object>> showRestaurantList() {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "餐廳列表");
        data.put("message", "餐廳管理系統");
        return ApiResponse.success(data, "餐廳列表載入成功");
    }

    /**
     * 獲取餐廳營業時段列表資料
     * 
     * @return 營業時段列表的 JSON 回應
     */
    @GetMapping("/times")
    public ApiResponse<Map<String, Object>> showRestaurantTimeList() {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "餐廳營業時段");
        data.put("message", "管理餐廳營業時段");
        return ApiResponse.success(data, "營業時段列表載入成功");
    }

    /**
     * 獲取訂位管理頁面資料
     * 
     * @return 訂位列表的 JSON 回應
     */
    @GetMapping("/reservations")
    public ApiResponse<Map<String, Object>> showReservationList() {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "訂位管理");
        data.put("message", "客戶訂位管理系統");
        return ApiResponse.success(data, "訂位列表載入成功");
    }
}