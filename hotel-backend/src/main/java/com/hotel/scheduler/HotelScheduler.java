package com.hotel.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.hotel.model.dto.BookingDTO;
import com.hotel.model.dto.BookingPaymentDTO;
import com.hotel.model.dto.RoomTaskDTO;
import com.hotel.model.entity.Booking;
import com.hotel.model.entity.RoomTask;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomTaskRepository;
import com.hotel.service.BookingPaymentService;
import com.hotel.service.BookingService;
import com.hotel.service.RoomService;
import com.hotel.service.RoomTaskService;

import jakarta.annotation.PostConstruct;

/**
 * 飯店自動化排程任務 (Hotel Automated Scheduler)
 * 負責定時推進訂單狀態、同步空房、執行退房清潔與逾期未付款清理
 */
@Component
public class HotelScheduler {

    private static final Logger log = LoggerFactory.getLogger(HotelScheduler.class);

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final RoomTaskService roomTaskService;
    private final RoomTaskRepository roomTaskRepository;
    private final RoomService roomService;
    private final BookingPaymentService bookingPaymentService;

    public HotelScheduler(
            BookingService bookingService,
            BookingRepository bookingRepository,
            RoomTaskService roomTaskService,
            RoomTaskRepository roomTaskRepository,
            RoomService roomService,
            BookingPaymentService bookingPaymentService) {
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
        this.roomTaskService = roomTaskService;
        this.roomTaskRepository = roomTaskRepository;
        this.roomService = roomService;
        this.bookingPaymentService = bookingPaymentService;
    }

    /**
     * 每 5 分鐘自動根據當前日期與時間推進活躍訂單狀態 (使用精確條件查詢，消除歷史全表掃描)
     */
    @PostConstruct
    @Scheduled(cron = "0 */5 * * * *")
    public void autoAdvanceBookingStates() {
        log.info("系統排程：自動根據當前日期修正活躍訂單狀態...");
        LocalDate today = LocalDate.now();
        int currentHour = LocalDateTime.now().getHour();

        // 僅查詢未完成且未取消的活躍訂單，大幅降低資料庫與記憶體負擔
        List<Booking> activeBookings = bookingRepository.findActiveIncompleteBookings();
        for (Booking b : activeBookings) {
            try {
                boolean isUpdated = false;
                BookingDTO updateDto = new BookingDTO();

                // 1. 自動修正不正確的房價 (防止價格計算不一致)
                Integer correctPrice = bookingService.calculateBookingPrice(b.getRoomTypeId(), b.getCheckInDate(),
                        b.getCheckOutDate());
                if (correctPrice != null && !correctPrice.equals(b.getBookingPrice())) {
                    log.info("自動修正：訂單 ID {} 房價錯誤 (原: {}, 新: {})", b.getBookingId(), b.getBookingPrice(), correctPrice);
                    updateDto.setBookingPrice(correctPrice);
                    isUpdated = true;
                }

                // 2. 自動修正過期狀態 (退房日已過或今日中午 12:00 後)
                String currentStatus = b.getBookingStatus();
                if (!"已完成".equals(currentStatus) && !"已取消".equals(currentStatus)) {
                    boolean isCheckoutOverdue = today.isAfter(b.getCheckOutDate()) ||
                            (today.isEqual(b.getCheckOutDate()) && currentHour >= 12);

                    if (isCheckoutOverdue) {
                        log.info("自動修正：訂單 ID {} 退房時間已過，轉為已完成", b.getBookingId());
                        updateDto.setBookingStatus("已完成");
                        isUpdated = true;
                    }
                }

                // 3. 執行更新
                if (isUpdated) {
                    bookingService.updateBooking(b.getBookingId(), updateDto);
                }
            } catch (Exception e) {
                log.error("自動修正訂單狀態失敗：ID " + b.getBookingId(), e);
            }
        }
    }

    /**
     * 系統啟動時與每 5 分鐘自動對齊房間與訂房狀態
     */
    @PostConstruct
    @Scheduled(cron = "0 */5 * * * *")
    public void autoSyncRoomStatuses() {
        log.info("排程執行：自動為未分配房間的當日訂單分配空房...");
        try {
            bookingService.autoAssignRoomsForToday();
        } catch (Exception e) {
            log.error("自動分配房間失敗", e);
        }

        log.info("排程執行：自動同步房間狀態與今日訂單...");
        try {
            roomService.syncRoomStatuses();
            log.info("自動同步房間狀態完成");
        } catch (Exception e) {
            log.error("自動同步房間狀態失敗", e);
        }
    }

    /**
     * 每天中午 12:00 執行自動退房程序
     */
    @Scheduled(cron = "0 0 12 * * *")
    public void autoCheckout() {
        log.info("開始執行每日 12:00 自動退房排程...");
        LocalDate today = LocalDate.now();

        List<Booking> activeBookings = bookingRepository.findActiveIncompleteBookings();
        List<Booking> toCheckout = activeBookings.stream()
                .filter(b -> "已入住".equals(b.getBookingStatus()))
                .filter(b -> !b.getCheckOutDate().isAfter(today))
                .collect(Collectors.toList());

        for (Booking booking : toCheckout) {
            log.info("自動退房處理：訂單 ID {}", booking.getBookingId());
            BookingDTO updateDto = new BookingDTO();
            updateDto.setBookingStatus("已完成");
            try {
                bookingService.updateBooking(booking.getBookingId(), updateDto);
            } catch (Exception e) {
                log.error("自動退房處理失敗：訂單 ID " + booking.getBookingId(), e);
            }
        }
        log.info("每日 12:00 自動退房排程執行完畢，共處理 {} 筆。", toCheckout.size());
    }

    /**
     * 每天下午 14:45 自動完成退房清潔工單，避免與 15:00 入住狀態衝突
     */
    @Scheduled(cron = "0 45 14 * * *")
    public void autoCompleteCheckoutTasks() {
        log.info("開始執行每日 14:45 自動完成退房清潔排程...");

        List<RoomTask> allTasks = roomTaskRepository.findAll();
        List<RoomTask> tasksToComplete = allTasks.stream()
                .filter(t -> "退房清潔".equals(t.getTaskType()))
                .filter(t -> !"已完成".equals(t.getTaskStatus()) && !"已取消".equals(t.getTaskStatus()))
                .collect(Collectors.toList());

        for (RoomTask task : tasksToComplete) {
            log.info("自動完成清潔工單：ID {}", task.getTaskId());
            try {
                RoomTaskDTO updateDto = new RoomTaskDTO();
                updateDto.setTaskStatus("已完成");
                roomTaskService.update(task.getTaskId(), updateDto);
            } catch (Exception e) {
                log.error("自動完成清潔工單失敗：ID " + task.getTaskId(), e);
            }
        }
        log.info("每日 14:45 自動完成退房清潔排程執行完畢，共處理 {} 筆。", tasksToComplete.size());
    }

    /**
     * 每天凌晨 00:00 執行過期工單刪除程序 (刪除完成時間超過 24 小時的已完成工單)
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void cleanupOldTasks() {
        log.info("開始執行每日 00:00 過期工單清理排程...");
        LocalDateTime threshold = LocalDateTime.now().minusHours(24);

        List<RoomTask> allTasks = roomTaskRepository.findAll();
        List<RoomTask> toDelete = allTasks.stream()
                .filter(t -> "已完成".equals(t.getTaskStatus()))
                .filter(t -> t.getCompletedAt() != null && t.getCompletedAt().isBefore(threshold))
                .collect(Collectors.toList());

        for (RoomTask task : toDelete) {
            log.info("自動刪除過期工單：ID {}", task.getTaskId());
            try {
                roomTaskService.deleteById(task.getTaskId());
            } catch (Exception e) {
                log.error("自動刪除過期工單失敗：ID " + task.getTaskId(), e);
            }
        }
        log.info("每日 00:00 過期工單清理排程執行完畢，共刪除 {} 筆。", toDelete.size());
    }

    /**
     * 每分鐘自動取消超過 15 分鐘未付款的「已預訂」訂單
     */
    @Scheduled(cron = "0 * * * * *")
    public void cancelUnpaidBookings() {
        log.info("排程執行：自動取消 15 分鐘未付款訂單...");
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(15);
        List<Booking> activeBookings = bookingRepository.findActiveIncompleteBookings();

        int canceledCount = 0;
        for (Booking b : activeBookings) {
            // 只處理「已預訂」狀態，且建立時間超過 15 分鐘的訂單
            if ("已預訂".equals(b.getBookingStatus()) && b.getCreatedAt() != null
                    && b.getCreatedAt().isBefore(threshold)) {
                try {
                    BookingPaymentDTO payment = bookingPaymentService.findByBookingId(b.getBookingId());
                    // 如果沒有付款紀錄，或者付款紀錄不是「已付款」，就自動取消
                    if (payment == null || !"已付款".equals(payment.getPaymentStatus())) {
                        BookingDTO updateDto = new BookingDTO();
                        updateDto.setBookingStatus("已取消");
                        bookingService.updateBooking(b.getBookingId(), updateDto);
                        log.info("自動取消逾時未付訂單：Booking ID = {}", b.getBookingId());
                        canceledCount++;
                    }
                } catch (Exception e) {
                    log.warn("無法確認訂單付款狀態或無法取消 (Booking ID: {}): {}", b.getBookingId(), e.getMessage());
                }
            }
        }
        if (canceledCount > 0) {
            log.info("自動取消 15 分鐘未付款訂單完成，共取消 {} 筆。", canceledCount);
        }
    }
}
