package com.hotel.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.hotel.event.BookingEvents.BookingCancelledEvent;
import com.hotel.event.BookingEvents.BookingCreatedEvent;
import com.hotel.event.BookingEvents.CheckInSuccessEvent;
import com.hotel.util.MailUtil;

/**
 * 訂房事件監聽器 (Transaction-Bound Event Listener)
 * 確保資料庫 Transaction 交易確認 Commit 成功後，才觸發非同步信件發送，徹底杜絕髒通知 (Dirty Notification)。
 */
@Component
public class BookingEventListener {

    private static final Logger log = LoggerFactory.getLogger(BookingEventListener.class);

    private final MailUtil mailUtil;

    public BookingEventListener(MailUtil mailUtil) {
        this.mailUtil = mailUtil;
    }

    /**
     * 訂房建立成功：交易提交後寄送訂房確認信
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleBookingCreated(BookingCreatedEvent event) {
        log.info("[Transaction AFTER_COMMIT] 收到訂房建立事件，開始非同步發送確認信，Booking ID: {}", event.getBookingId());
        try {
            mailUtil.sendBookingConfirmation(event.getBookingId());
        } catch (Exception e) {
            log.error("發送訂房確認信失敗 (Booking ID: {}): {}", event.getBookingId(), e.getMessage());
        }
    }

    /**
     * 入住報到完成：交易提交後寄送專屬房號與開門 QR Code 鑰匙信件
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleCheckInSuccess(CheckInSuccessEvent event) {
        log.info("[Transaction AFTER_COMMIT] 收到入住報到成功事件，開始非同步發送開門金鑰信，Booking ID: {}", event.getBookingId());
        try {
            mailUtil.sendCheckInSuccess(event.getBookingId());
        } catch (Exception e) {
            log.error("發送入住報到成功信失敗 (Booking ID: {}): {}", event.getBookingId(), e.getMessage());
        }
    }

    /**
     * 訂房取消完成：交易提交後寄送取消與退款通知信
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT, fallbackExecution = true)
    public void handleBookingCancelled(BookingCancelledEvent event) {
        log.info("[Transaction AFTER_COMMIT] 收到訂房取消事件，開始非同步發送取消退款信，Booking ID: {}", event.getBookingId());
        try {
            mailUtil.sendBookingCancellation(event.getBookingId());
        } catch (Exception e) {
            log.error("發送訂房取消通知信失敗 (Booking ID: {}): {}", event.getBookingId(), e.getMessage());
        }
    }
}
