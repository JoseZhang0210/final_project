package com.hotel.scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import jakarta.annotation.PostConstruct;

import com.hotel.model.dto.BookingDTO;
import com.hotel.model.entity.RoomTask;
import com.hotel.repository.RoomTaskRepository;
import com.hotel.service.BookingService;
import com.hotel.service.RoomTaskService;

@Component
public class HotelScheduler {

    private static final Logger log = LoggerFactory.getLogger(HotelScheduler.class);

    private final BookingService bookingService;
    private final RoomTaskService roomTaskService;
    private final RoomTaskRepository roomTaskRepository;

    public HotelScheduler(BookingService bookingService, RoomTaskService roomTaskService, RoomTaskRepository roomTaskRepository) {
        this.bookingService = bookingService;
        this.roomTaskService = roomTaskService;
        this.roomTaskRepository = roomTaskRepository;
    }

    @PostConstruct
    public void autoAdvanceBookingStates() {
        log.info("系統啟動：自動根據當前日期修正訂單狀態...");
        LocalDate today = LocalDate.now();

        List<BookingDTO> allBookings = bookingService.findAll();
        for (BookingDTO b : allBookings) {
            try {
                boolean isUpdated = false;
                BookingDTO updateDto = new BookingDTO();

                // 1. 自動修正不正確的房價 (對所有訂單生效)
                Integer correctPrice = bookingService.calculateBookingPrice(b.getRoomTypeId(), b.getCheckInDate(), b.getCheckOutDate());
                if (correctPrice != null && !correctPrice.equals(b.getBookingPrice())) {
                    log.info("自動修正：訂單 ID {} 房價錯誤 (原: {}, 新: {})", b.getBookingId(), b.getBookingPrice(), correctPrice);
                    updateDto.setBookingPrice(correctPrice);
                    isUpdated = true;
                }

                // 2. 自動修正過期狀態 (排除已完成或已取消)
                String currentStatus = b.getBookingStatus();
                if (!"已完成".equals(currentStatus) && !"已取消".equals(currentStatus)) {
                    if (today.isAfter(b.getCheckOutDate())) {
                        if ("待入住".equals(currentStatus)) {
                            log.info("自動修正：訂單 ID {} 過期未入住，轉為已取消", b.getBookingId());
                            updateDto.setBookingStatus("已取消");
                        } else {
                            log.info("自動修正：訂單 ID {} 退房日已過，轉為已完成", b.getBookingId());
                            updateDto.setBookingStatus("已完成");
                        }
                        isUpdated = true;
                    } else if (!today.isBefore(b.getCheckInDate()) && "待入住".equals(currentStatus)) {
                        log.info("自動修正：訂單 ID {} 達到入住日，轉為已入住", b.getBookingId());
                        updateDto.setBookingStatus("已入住");
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
     * 每天中午 12:00 執行自動退房程序
     * 掃描今日以前應退房但仍為「已入住」的訂單，自動更新為「已完成」
     */
    @Scheduled(cron = "0 0 12 * * *")
    public void autoCheckout() {
        log.info("開始執行每日 12:00 自動退房排程...");
        LocalDate today = LocalDate.now();

        List<BookingDTO> allBookings = bookingService.findAll();
        List<BookingDTO> toCheckout = allBookings.stream()
                .filter(b -> "已入住".equals(b.getBookingStatus()))
                .filter(b -> !b.getCheckOutDate().isAfter(today))
                .collect(Collectors.toList());

        for (BookingDTO booking : toCheckout) {
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
     * 每天凌晨 00:00 執行過期工單刪除程序
     * 刪除所有「已完成」且完成時間超過 24 小時前的工單
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
}
