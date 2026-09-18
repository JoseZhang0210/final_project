package com.hotel.constant;

public final class BookingStatus {
    private BookingStatus() {}

    public static final String PENDING = "待入住";
    public static final String CHECKED_IN = "已入住";
    public static final String CHECKED_OUT = "已退房";
    public static final String CANCELLED = "已取消";
    public static final String COMPLETED = "已完成";
}
