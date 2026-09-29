package com.hotel.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.constant.BookingStatus;
import com.hotel.constant.RoomStatus;
import com.hotel.event.BookingEvents.BookingCancelledEvent;
import com.hotel.event.BookingEvents.BookingCreatedEvent;
import com.hotel.event.BookingEvents.CheckInSuccessEvent;
import com.hotel.model.dto.BookingDTO;
import com.hotel.model.entity.Booking;
import com.hotel.model.entity.Room;
import com.hotel.model.entity.RoomType;
import com.hotel.repository.BookingPaymentRepository;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.RoomTypeRepository;
import com.hotel.repository.specification.BookingSpecification;
import com.hotel.service.BookingService;
import com.hotel.service.RoomTaskService;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

/**
 * 訂房業務邏輯實作類 (Booking Service Implementation)
 * 整合交易安全控制、併發防排房衝突與事件驅動非同步通知
 */
@Slf4j
@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final BookingPaymentRepository bookingPaymentRepository;
    private final RoomTaskService roomTaskService;
    private final ApplicationEventPublisher eventPublisher;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            RoomRepository roomRepository,
            RoomTypeRepository roomTypeRepository,
            BookingPaymentRepository bookingPaymentRepository,
            RoomTaskService roomTaskService,
            ApplicationEventPublisher eventPublisher) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.bookingPaymentRepository = bookingPaymentRepository;
        this.roomTaskService = roomTaskService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> findAll() {
        return bookingRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BookingDTO> findById(Integer bookingId) {
        return bookingRepository.findById(bookingId).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDTO> searchByCriteria(BookingDTO criteria) {
        List<Booking> bookings = bookingRepository.findAll(BookingSpecification.findByCriteria(criteria));
        return bookings.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Override
    public BookingDTO insert(BookingDTO bookingDTO) {
        // 1. 自動配發可用房間 (執行緒安全)
        Integer assignedRoomId = assignAvailableRoom(
                bookingDTO.getRoomTypeId(),
                bookingDTO.getCheckInDate(),
                bookingDTO.getCheckOutDate(),
                null);
        bookingDTO.setRoomId(assignedRoomId);

        // 2. 後端自動計算訂單金額：房型每晚單價 × 住宿天數 (防止前端金額篡改)
        bookingDTO.setBookingPrice(calculateBookingPrice(
                bookingDTO.getRoomTypeId(),
                bookingDTO.getCheckInDate(),
                bookingDTO.getCheckOutDate()));

        // 3. 儲存訂房紀錄
        Booking booking = convertToEntity(bookingDTO);
        Booking savedBooking = bookingRepository.save(booking);

        // 4. 發布訂房建立事件 (由 TransactionalEventListener 在 AFTER_COMMIT 成功提交後非同步發送確認信)
        eventPublisher.publishEvent(new BookingCreatedEvent(savedBooking.getBookingId()));

        return convertToDTO(savedBooking);
    }

    @Override
    public BookingDTO updateBooking(Integer id, BookingDTO newBookingData) {
        Booking existingBooking = bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("修改失敗：找不到 ID 為 " + id + " 的預訂資料"));

        boolean needReassign = false;
        Integer targetRoomTypeId = existingBooking.getRoomTypeId();
        LocalDate targetCheckIn = existingBooking.getCheckInDate();
        LocalDate targetCheckOut = existingBooking.getCheckOutDate();

        if (newBookingData.getRoomTypeId() != null
                && !newBookingData.getRoomTypeId().equals(existingBooking.getRoomTypeId())) {
            targetRoomTypeId = newBookingData.getRoomTypeId();
            needReassign = true;
        }
        if (newBookingData.getCheckInDate() != null
                && !newBookingData.getCheckInDate().equals(existingBooking.getCheckInDate())) {
            targetCheckIn = newBookingData.getCheckInDate();
            needReassign = true;
        }
        if (newBookingData.getCheckOutDate() != null
                && !newBookingData.getCheckOutDate().equals(existingBooking.getCheckOutDate())) {
            targetCheckOut = newBookingData.getCheckOutDate();
            needReassign = true;
        }

        // 若日期或房型有變動，重新執行自動配房 (排除自己)
        if (needReassign) {
            Integer assignedRoomId = assignAvailableRoom(targetRoomTypeId, targetCheckIn, targetCheckOut, id);
            existingBooking.setRoomId(assignedRoomId);
            existingBooking.setRoomTypeId(targetRoomTypeId);
            existingBooking.setCheckInDate(targetCheckIn);
            existingBooking.setCheckOutDate(targetCheckOut);
        } else if (newBookingData.getRoomId() != null) {
            existingBooking.setRoomId(newBookingData.getRoomId());
        }

        if (newBookingData.getGuestNum() != null) {
            existingBooking.setGuestNum(newBookingData.getGuestNum());
        }
        if (newBookingData.getBookingPrice() != null) {
            existingBooking.setBookingPrice(newBookingData.getBookingPrice());
        }
        if (newBookingData.getBookingStatus() != null
                && !newBookingData.getBookingStatus().equals(existingBooking.getBookingStatus())) {
            String oldStatus = existingBooking.getBookingStatus();
            String newStatus = newBookingData.getBookingStatus();
            existingBooking.setBookingStatus(newStatus);

            handleStatusTransition(existingBooking, oldStatus, newStatus);
        }

        // 若日期或房型有變動，重新計算實際訂單金額
        if (needReassign) {
            existingBooking.setBookingPrice(
                    calculateBookingPrice(targetRoomTypeId, targetCheckIn, targetCheckOut));
        }

        existingBooking = bookingRepository.save(existingBooking);
        return convertToDTO(existingBooking);
    }

    /**
     * 處理訂房狀態流轉 (Check-in, Check-out, Cancel)
     */
    private void handleStatusTransition(Booking existingBooking, String oldStatus, String newStatus) {
        if (BookingStatus.CHECKED_IN.equals(newStatus) && !newStatus.equals(oldStatus)) {
            // 1. 轉移至「已入住」
            Room room = roomRepository.findById(existingBooking.getRoomId()).orElse(null);
            if (room != null) {
                room.setRoomStatus(RoomStatus.OCCUPIED);
                roomRepository.save(room);
            }

            // 發布報到成功事件 (AFTER_COMMIT 後寄送專屬房號與開門 QR Code 郵件)
            eventPublisher.publishEvent(new CheckInSuccessEvent(existingBooking.getBookingId()));

        } else if ((BookingStatus.CHECKED_OUT.equals(newStatus) || BookingStatus.COMPLETED.equals(newStatus))
                && !oldStatus.equals(newStatus)) {
            // 2. 轉移至「已退房/已完成」
            Room room = roomRepository.findById(existingBooking.getRoomId()).orElse(null);
            if (room != null) {
                room.setRoomStatus(RoomStatus.CLEANING);
                roomRepository.save(room);

                LocalDateTime targetTime = existingBooking.getCheckOutDate().atTime(12, 0);
                LocalDateTime createdAt = LocalDateTime.now().isBefore(targetTime)
                        ? LocalDateTime.now()
                        : targetTime;

                // 委派至 RoomTaskService 建立清潔工單 (模組職責解耦)
                roomTaskService.createCheckoutCleaningTask(room.getRoomId(), createdAt, "由系統自動產生：退房清潔");
            }
        } else if (BookingStatus.CANCELLED.equals(newStatus) && !oldStatus.equals(newStatus)) {
            // 3. 轉移至「已取消」
            Room room = roomRepository.findById(existingBooking.getRoomId()).orElse(null);
            if (room != null) {
                if (BookingStatus.CHECKED_IN.equals(oldStatus)) {
                    room.setRoomStatus(RoomStatus.CLEANING);
                    roomRepository.save(room);
                    roomTaskService.createCheckoutCleaningTask(room.getRoomId(), LocalDateTime.now(),
                            "由系統自動產生：入住後取消，執行退房清潔");
                } else {
                    String currentRoomStatus = room.getRoomStatus();
                    if (!RoomStatus.MAINTENANCE.equals(currentRoomStatus)
                            && !RoomStatus.DISABLED.equals(currentRoomStatus) &&
                            !RoomStatus.CHECKOUT_CLEANING_PENDING.equals(currentRoomStatus)
                            && !RoomStatus.CLEANING.equals(currentRoomStatus)) {
                        room.setRoomStatus(RoomStatus.AVAILABLE);
                        roomRepository.save(room);
                    }
                }
            }
        }
    }

    @Override
    public void deleteById(Integer id) {
        if (!bookingRepository.existsById(id)) {
            throw new EntityNotFoundException("欲刪除的預約 ID: " + id + " 不存在");
        }

        // 刪除關聯的付款記錄
        bookingPaymentRepository.findByBookingId(id).ifPresent(bookingPaymentRepository::delete);
        bookingRepository.deleteById(id);
    }

    /**
     * 核心自動配房邏輯 (加入執行緒同步鎖定，杜絕高併發重複排房)
     */
    private synchronized Integer assignAvailableRoom(
            Integer roomTypeId, LocalDate checkInDate, LocalDate checkOutDate, Integer excludeBookingId) {
        if (roomTypeId == null || checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("建立訂單失敗：必須提供房型、入住日期與退房日期");
        }
        if (checkInDate.isAfter(checkOutDate) || checkInDate.isEqual(checkOutDate)) {
            throw new IllegalArgumentException("建立訂單失敗：退房日期必須晚於入住日期");
        }

        // 1. 找出特定日期區間內，該房型已被預訂的房間 ID
        List<Integer> bookedRoomIds = bookingRepository.findBookedRoomIds(roomTypeId, checkInDate, checkOutDate);

        // 若是 update，需排除掉自己原本佔用的房間
        if (excludeBookingId != null) {
            Booking self = bookingRepository.findById(excludeBookingId).orElse(null);
            if (self != null && self.getRoomId() != null) {
                bookedRoomIds.remove(self.getRoomId());
            }
        }

        // 2. 找出該房型的所有房間
        List<Room> allRooms = roomRepository.findByRoomTypeId(roomTypeId);
        if (allRooms.isEmpty()) {
            throw new IllegalArgumentException("該房型不存在或尚未設定實體房間");
        }

        // 3. 過濾出尚未被預訂，且狀態為「可入住」或「可預訂」的可用房間
        List<Room> availableRooms = allRooms.stream()
                .filter(room -> !bookedRoomIds.contains(room.getRoomId()))
                .filter(room -> !RoomStatus.DISABLED.equals(room.getRoomStatus())
                        && !RoomStatus.MAINTENANCE.equals(room.getRoomStatus()))
                .collect(Collectors.toList());

        if (availableRooms.isEmpty()) {
            throw new IllegalArgumentException("該房型在指定日期區間內已無可用房間，請選擇其他房型或日期");
        }

        // 4. 自動分發第一間空房
        return availableRooms.get(0).getRoomId();
    }

    /**
     * 計算訂單金額 = 房型每晚單價 × 住宿天數
     */
    @Override
    public Integer calculateBookingPrice(Integer roomTypeId, LocalDate checkInDate, LocalDate checkOutDate) {
        if (roomTypeId == null || checkInDate == null || checkOutDate == null)
            return 0;
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (nights <= 0)
            return 0;
        RoomType roomType = roomTypeRepository.findById(roomTypeId).orElse(null);
        if (roomType == null || roomType.getPricePerNight() == null)
            return 0;
        return roomType.getPricePerNight() * (int) nights;
    }

    @Override
    public void autoAssignRoomsForToday() {
        LocalDate today = LocalDate.now();
        List<Booking> todayActiveBookings = bookingRepository.findActiveBookingsForDate(today);

        List<Booking> unassignedBookings = todayActiveBookings.stream()
                .filter(b -> b.getRoomId() == null)
                .collect(Collectors.toList());

        for (Booking b : unassignedBookings) {
            try {
                Integer roomId = assignAvailableRoom(b.getRoomTypeId(), b.getCheckInDate(), b.getCheckOutDate(),
                        b.getBookingId());
                b.setRoomId(roomId);
                bookingRepository.save(b);
                log.info("為訂單 ID {} 自動分配了房間 ID {}", b.getBookingId(), roomId);
            } catch (Exception e) {
                log.warn("無法為訂單 ID {} 自動分配房間：{}", b.getBookingId(), e.getMessage());
            }
        }
    }

    @Override
    public BookingDTO cancelBooking(Integer bookingId, Integer currentMemberId, boolean isAdmin) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + bookingId + " 的預訂紀錄"));

        if (!isAdmin && currentMemberId != null && !currentMemberId.equals(booking.getMemberId())) {
            throw new IllegalArgumentException("無權限取消其他會員的預訂");
        }

        if (!BookingStatus.PENDING.equals(booking.getBookingStatus())) {
            throw new IllegalStateException("只有「待入住」狀態的訂單可以線上取消");
        }

        LocalDate today = LocalDate.now(ZoneId.of("Asia/Taipei"));
        long daysUntilCheckIn = ChronoUnit.DAYS.between(today, booking.getCheckInDate());

        if (daysUntilCheckIn <= 3) {
            throw new IllegalStateException("入住前 3 天內無法線上取消訂房，請洽飯店客服專線");
        }

        String oldStatus = booking.getBookingStatus();
        booking.setBookingStatus(BookingStatus.CANCELLED);
        handleStatusTransition(booking, oldStatus, BookingStatus.CANCELLED);

        Booking savedBooking = bookingRepository.save(booking);

        // 處理關聯付款紀錄
        bookingPaymentRepository.findByBookingId(bookingId).ifPresent(payment -> {
            if ("已付款".equals(payment.getPaymentStatus())) {
                payment.setPaymentStatus("已退款");
                bookingPaymentRepository.save(payment);
            } else if ("待付款".equals(payment.getPaymentStatus()) || "未付款".equals(payment.getPaymentStatus())) {
                payment.setPaymentStatus("已取消");
                bookingPaymentRepository.save(payment);
            }
        });

        // 發布訂房取消事件 (AFTER_COMMIT 後非同步寄送取消退款確認信)
        eventPublisher.publishEvent(new BookingCancelledEvent(bookingId));

        return convertToDTO(savedBooking);
    }

    private BookingDTO convertToDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setBookingId(booking.getBookingId());
        dto.setMemberId(booking.getMemberId());
        dto.setRoomTypeId(booking.getRoomTypeId());
        dto.setCreatedAt(booking.getCreatedAt());
        dto.setRoomId(booking.getRoomId());
        dto.setCheckInDate(booking.getCheckInDate());
        dto.setCheckOutDate(booking.getCheckOutDate());
        dto.setGuestNum(booking.getGuestNum());
        dto.setBookingStatus(booking.getBookingStatus());
        dto.setBookingPrice(booking.getBookingPrice());
        return dto;
    }

    private Booking convertToEntity(BookingDTO dto) {
        Booking booking = new Booking();
        booking.setMemberId(dto.getMemberId());
        booking.setRoomTypeId(dto.getRoomTypeId());
        booking.setCreatedAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : LocalDateTime.now());
        booking.setRoomId(dto.getRoomId());
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setGuestNum(dto.getGuestNum());
        booking.setBookingStatus(dto.getBookingStatus());
        booking.setBookingPrice(dto.getBookingPrice());
        return booking;
    }
}
