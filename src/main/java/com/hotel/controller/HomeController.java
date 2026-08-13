package com.hotel.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.dto.ApiResponse;

/**
 * 首頁控制器 - 轉換為 REST API
 * 所有端點返回 JSON 格式的資料
 */
@RestController
@RequestMapping("/api/home")
public class HomeController {

    /**
     * 獲取首頁儀表板資料
     * 
     * @return JSON 格式的首頁資料
     */
    @GetMapping("/dashboard")
    public ApiResponse<Map<String, Object>> dashboard() {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "飯店首頁");
        data.put("message", "歡迎使用飯店管理系統");
        return ApiResponse.success(data, "首頁資料載入成功");
    }

    /**
     * 獲取註冊頁面初始資料
     * 
     * @return JSON 格式的註冊頁面資料
     */
    @GetMapping("/register-info")
    public ApiResponse<Map<String, Object>> registerInfo() {
        Map<String, Object> data = new HashMap<>();
        data.put("title", "會員註冊");
        data.put("message", "請填寫以下資訊進行註冊");
        return ApiResponse.success(data, "註冊頁面資料載入成功");
    }

    /**
     * 獲取房間預訂配置資料
     * 
     * @return JSON 格式的房間預訂配置
     */
    @GetMapping("/config")
    public ApiResponse<Map<String, Object>> bookingConfig() {
        Map<String, Object> data = new HashMap<>();
        data.put("hotelName", "SereneStay 飯店");
        data.put("supportEmail", "support@serenestay.com");
        return ApiResponse.success(data, "配置資料載入成功");
    }

    /**
     * 獲取房間預訂首頁資料
     * 
     * @return JSON 格式的房間預訂首頁資料
     */
    @GetMapping("/booking-home")
    public ApiResponse<Map<String, Object>> hotelHome() {
        Map<String, Object> data = new HashMap<>();
        data.put("hotelName", "SereneStay 飯店");
        data.put("features", new String[] { "豪華房間", "免費 WiFi", "健身房", "餐廳" });
        return ApiResponse.success(data, "飯店首頁資料載入成功");
    }

    /**
     * 獲取預訂流程首頁
     * 
     * @return JSON 格式的預訂流程資料
     */
    @GetMapping("/booking-flow")
    public ApiResponse<Map<String, Object>> bookingFlow() {
        Map<String, Object> data = new HashMap<>();
        data.put("step", 1);
        data.put("message", "開始預訂您的房間");
        return ApiResponse.success(data, "預訂流程資料載入成功");
    }

    /**
     * 獲取房間列表
     * 
     * @return JSON 格式的房間列表
     */
    @GetMapping("/rooms")
    public ApiResponse<Map<String, Object>> roomList() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalRooms", 0);
        data.put("availableRooms", 0);
        data.put("rooms", new Object[] {});
        return ApiResponse.success(data, "房間列表載入成功");
    }

    /**
     * 獲取預訂檢查頁面資料
     * 
     * @return JSON 格式的預訂檢查資料
     */
    @GetMapping("/booking-check")
    public ApiResponse<Map<String, Object>> bookingCheck() {
        Map<String, Object> data = new HashMap<>();
        data.put("reservationStatus", "pending");
        data.put("message", "請確認您的預訂資訊");
        return ApiResponse.success(data, "預訂檢查資料載入成功");
    }

    /**
     * 獲取房間類型 CRUD 頁面資料
     * 
     * @return JSON 格式的房間類型資料
     */
    @GetMapping("/room-types-crud")
    public ApiResponse<Map<String, Object>> roomTypeCRUD() {
        Map<String, Object> data = new HashMap<>();
        data.put("totalRoomTypes", 0);
        data.put("roomTypes", new Object[] {});
        return ApiResponse.success(data, "房間類型資料載入成功");
    }
}