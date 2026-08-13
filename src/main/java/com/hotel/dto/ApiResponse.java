package com.hotel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 統一的 API 回應封裝類
 * 所有 REST API 都返回此格式的 JSON 資料
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String status; // "success" 或 "error"
    private int code; // HTTP 狀態碼
    private T data; // 實際回應資料
    private String message; // 提示訊息

    public ApiResponse() {
    }

    public ApiResponse(String status, int code, T data, String message) {
        this.status = status;
        this.code = code;
        this.data = data;
        this.message = message;
    }

    // 成功回應建構子
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>("success", 200, data, "");
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>("success", 200, data, message);
    }

    // 錯誤回應建構子
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>("error", code, null, message);
    }

    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>("error", 400, null, message);
    }

    // Getter & Setter
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
