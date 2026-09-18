package com.hotel.constant;

public final class RoomTaskStatus {
    private RoomTaskStatus() {}

    // Task Types
    public static final String TYPE_CHECKOUT_CLEANING = "退房清潔";
    public static final String TYPE_DAILY_CLEANING = "日常清潔";
    public static final String TYPE_MAINTENANCE = "維修保養";
    public static final String TYPE_SUPPLY_REFILL = "備品補充";

    // Task Statuses
    public static final String STATUS_PENDING = "待處理";
    public static final String STATUS_IN_PROGRESS = "進行中";
    public static final String STATUS_PROCESSING = "處理中";
    public static final String STATUS_COMPLETED = "已完成";
    
    // Priorities
    public static final String PRIORITY_NORMAL = "一般";
}
