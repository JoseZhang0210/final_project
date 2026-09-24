package com.hotel.constant;

public final class RoomStatus {
    private RoomStatus() {}

    public static final String AVAILABLE = "可預訂";
    public static final String BOOKED = "已預訂";
    public static final String OCCUPIED = "已入住";
    public static final String CHECKOUT_CLEANING_PENDING = "退房待清潔";
    public static final String CLEANING = "清潔中";
    public static final String MAINTENANCE = "維修中";
    public static final String DISABLED = "停用";
}
