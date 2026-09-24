package com.hotel.event;

/**
 * 訂房相關領域事件定義
 */
public final class BookingEvents {

    private BookingEvents() {
    }

    /**
     * 訂房成功建立事件
     */
    public static class BookingCreatedEvent {
        private final Integer bookingId;

        public BookingCreatedEvent(Integer bookingId) {
            this.bookingId = bookingId;
        }

        public Integer getBookingId() {
            return bookingId;
        }
    }

    /**
     * 入住報到成功事件 (Check-in Verified)
     */
    public static class CheckInSuccessEvent {
        private final Integer bookingId;

        public CheckInSuccessEvent(Integer bookingId) {
            this.bookingId = bookingId;
        }

        public Integer getBookingId() {
            return bookingId;
        }
    }

    /**
     * 訂房取消與退款事件
     */
    public static class BookingCancelledEvent {
        private final Integer bookingId;

        public BookingCancelledEvent(Integer bookingId) {
            this.bookingId = bookingId;
        }

        public Integer getBookingId() {
            return bookingId;
        }
    }
}
