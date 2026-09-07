package com.hotel.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.dto.BookingDTO;
import com.hotel.model.entity.Booking;
import com.hotel.model.entity.Room;
import com.hotel.model.entity.RoomType;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.RoomTaskRepository;
import com.hotel.repository.RoomTypeRepository;
import com.hotel.model.entity.RoomTask;
import com.hotel.repository.specification.BookingSpecification;
import com.hotel.service.BookingService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final RoomTaskRepository roomTaskRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, RoomRepository roomRepository,
            RoomTypeRepository roomTypeRepository, RoomTaskRepository roomTaskRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.roomTaskRepository = roomTaskRepository;
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
        // 自動配發空房
        Integer assignedRoomId = assignAvailableRoom(
                bookingDTO.getRoomTypeId(),
                bookingDTO.getCheckInDate(),
                bookingDTO.getCheckOutDate(),
                null
        );
        bookingDTO.setRoomId(assignedRoomId);

        // 後端自動計算訂單金額：房型每晚單價 × 住宿天數，防止前端傳入不正確的金額
        bookingDTO.setBookingPrice(calculateBookingPrice(bookingDTO.getRoomTypeId(),
                bookingDTO.getCheckInDate(), bookingDTO.getCheckOutDate()));

        Booking booking = convertToEntity(bookingDTO);
        Booking savedBooking = bookingRepository.save(booking);
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

        if (newBookingData.getRoomTypeId() != null && !newBookingData.getRoomTypeId().equals(existingBooking.getRoomTypeId())) {
            targetRoomTypeId = newBookingData.getRoomTypeId();
            needReassign = true;
        }
        if (newBookingData.getCheckInDate() != null && !newBookingData.getCheckInDate().equals(existingBooking.getCheckInDate())) {
            targetCheckIn = newBookingData.getCheckInDate();
            needReassign = true;
        }
        if (newBookingData.getCheckOutDate() != null && !newBookingData.getCheckOutDate().equals(existingBooking.getCheckOutDate())) {
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
        if (newBookingData.getBookingStatus() != null && !newBookingData.getBookingStatus().equals(existingBooking.getBookingStatus())) {
            String oldStatus = existingBooking.getBookingStatus();
            String newStatus = newBookingData.getBookingStatus();
            existingBooking.setBookingStatus(newStatus);
            
            // =========================
            // 狀態連動：訂單狀態 ↔ 房間狀態 ↔ 房務工單
            // =========================
            if ("已入住".equals(newStatus)) {
                Room room = roomRepository.findById(existingBooking.getRoomId()).orElse(null);
                if (room != null) {
                    room.setRoomStatus("已入住");
                    roomRepository.save(room);
                }
            } else if (("已退房".equals(newStatus) || "已完成".equals(newStatus)) && !oldStatus.equals(newStatus)) {
                Room room = roomRepository.findById(existingBooking.getRoomId()).orElse(null);
                if (room != null) {
                    room.setRoomStatus("退房待清潔");
                    roomRepository.save(room);
                    
                    // 自動產生清潔工單
                    RoomTask task = new RoomTask();
                    task.setRoomId(room.getRoomId());
                    task.setPriority("重要");
                    task.setTaskType("退房清潔");
                    task.setTaskStatus("待處理");
                    
                    java.time.LocalDateTime targetTime = existingBooking.getCheckOutDate().atTime(12, 0);
                    if (java.time.LocalDateTime.now().isBefore(targetTime)) {
                        task.setCreatedAt(java.time.LocalDateTime.now());
                    } else {
                        task.setCreatedAt(targetTime);
                    }
                    
                    task.setEmployeeId(null); // 指派給空，避免無此員工時發生 FK 錯誤
                    task.setRemark("由系統自動產生：退房清潔");
                    roomTaskRepository.save(task);
                }
            } else if ("已取消".equals(newStatus) && !oldStatus.equals(newStatus)) {
                Room room = roomRepository.findById(existingBooking.getRoomId()).orElse(null);
                if (room != null) {
                    room.setRoomStatus("可預訂");
                    roomRepository.save(room);
                }
            }
        }

        // 若日期或房型有變動，重新計算實際訂單金額
        if (needReassign) {
            existingBooking.setBookingPrice(
                    calculateBookingPrice(targetRoomTypeId, targetCheckIn, targetCheckOut));
        }

        existingBooking = bookingRepository.save(existingBooking);

        return convertToDTO(existingBooking);
    }

    @Override
    public void deleteById(Integer id) {
        if (!bookingRepository.existsById(id)) {
            throw new EntityNotFoundException("欲刪除的預約 ID: " + id + " 不存在");
        }
        bookingRepository.deleteById(id);
    }

    /**
     * 核心自動配房邏輯
     */
    private Integer assignAvailableRoom(Integer roomTypeId, LocalDate checkInDate, LocalDate checkOutDate, Integer excludeBookingId) {
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
                .filter(room -> !"停用".equals(room.getRoomStatus()) && !"維修中".equals(room.getRoomStatus()))
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
        if (roomTypeId == null || checkInDate == null || checkOutDate == null) return 0;
        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (nights <= 0) return 0;
        RoomType roomType = roomTypeRepository.findById(roomTypeId).orElse(null);
        if (roomType == null || roomType.getPricePerNight() == null) return 0;
        return roomType.getPricePerNight() * (int) nights;
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
        booking.setCreatedAt(dto.getCreatedAt());
        booking.setRoomId(dto.getRoomId());
        booking.setCheckInDate(dto.getCheckInDate());
        booking.setCheckOutDate(dto.getCheckOutDate());
        booking.setGuestNum(dto.getGuestNum());
        booking.setBookingStatus(dto.getBookingStatus());
        booking.setBookingPrice(dto.getBookingPrice());
        return booking;
    }
}
