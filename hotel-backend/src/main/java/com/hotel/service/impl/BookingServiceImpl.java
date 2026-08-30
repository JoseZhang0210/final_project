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
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.specification.BookingSpecification;
import com.hotel.service.BookingService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public BookingServiceImpl(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
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
        if (newBookingData.getBookingStatus() != null) {
            existingBooking.setBookingStatus(newBookingData.getBookingStatus());
        }

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
