package com.hotel.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.constant.BookingStatus;
import com.hotel.constant.RoomStatus;
import com.hotel.model.dto.RoomDTO;
import com.hotel.model.entity.Booking;
import com.hotel.model.entity.Room;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.service.RoomService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public RoomServiceImpl(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> findAll() {
        return roomRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomDTO> findByFloor(Integer floor) {
        return roomRepository.findByFloor(floor).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoomDTO> findByRoomNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber).map(this::convertToDTO);
    }

    @Override
    public RoomDTO insert(RoomDTO roomDTO) {
        Room room = convertToEntity(roomDTO);
        Room saved = roomRepository.save(room);
        return convertToDTO(saved);
    }

    @Override
    public RoomDTO update(Integer id, RoomDTO updatedRoomDTO) {
        Room existingRoom = roomRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的房間資料"));

        if (updatedRoomDTO.getRoomStatus() != null) {
            if ((RoomStatus.MAINTENANCE.equals(updatedRoomDTO.getRoomStatus())
                    || RoomStatus.DISABLED.equals(updatedRoomDTO.getRoomStatus()))
                    && !updatedRoomDTO.getRoomStatus().equals(existingRoom.getRoomStatus())) {
                boolean hasFutureBookings = bookingRepository.existsByRoomIdAndCheckOutDateGreaterThanEqual(id,
                        LocalDate.now());
                if (hasFutureBookings) {
                    throw new IllegalStateException("該房間有未來的預訂，請為顧客更換房間或免費升等");
                }
            }
            existingRoom.setRoomStatus(updatedRoomDTO.getRoomStatus());
        }

        if (updatedRoomDTO.getRoomTypeId() != null) {
            existingRoom.setRoomTypeId(updatedRoomDTO.getRoomTypeId());
        }

        return convertToDTO(existingRoom);
    }

    @Override
    public void deleteById(Integer id) {
        if (!roomRepository.existsById(id)) {
            throw new EntityNotFoundException("欲刪除的房間 ID: " + id + " 不存在");
        }
        roomRepository.deleteById(id);
    }

    private RoomDTO convertToDTO(Room room) {
        RoomDTO dto = new RoomDTO();
        dto.setRoomId(room.getRoomId());
        dto.setRoomNumber(room.getRoomNumber());
        dto.setRoomTypeId(room.getRoomTypeId());
        dto.setFloor(room.getFloor());
        dto.setRoomStatus(room.getRoomStatus());
        return dto;
    }

    private Room convertToEntity(RoomDTO dto) {
        Room room = new Room();
        room.setRoomNumber(dto.getRoomNumber());
        room.setRoomTypeId(dto.getRoomTypeId());
        room.setFloor(dto.getFloor());
        room.setRoomStatus(dto.getRoomStatus());
        return room;
    }

    @Override
    public void syncRoomStatuses() {
        LocalDate today = LocalDate.now();
        List<Room> allRooms = roomRepository.findAll();
        // 使用高效 JPQL 查詢當日有效訂房，消除全表掃描
        List<Booking> todayBookings = bookingRepository.findActiveBookingsForDate(today);

        for (Room room : allRooms) {
            String currentRoomStatus = room.getRoomStatus();

            // 系統不應強制覆蓋實體維運狀態
            if (RoomStatus.MAINTENANCE.equals(currentRoomStatus) || RoomStatus.DISABLED.equals(currentRoomStatus) ||
                    RoomStatus.CHECKOUT_CLEANING_PENDING.equals(currentRoomStatus)
                    || RoomStatus.CLEANING.equals(currentRoomStatus)) {
                continue;
            }

            Optional<Booking> activeBooking = todayBookings.stream()
                    .filter(b -> b.getRoomId().equals(room.getRoomId()))
                    .findFirst();

            if (activeBooking.isPresent()) {
                String bookingStatus = activeBooking.get().getBookingStatus();
                if (BookingStatus.CHECKED_IN.equals(bookingStatus) && !RoomStatus.OCCUPIED.equals(currentRoomStatus)) {
                    room.setRoomStatus(RoomStatus.OCCUPIED);
                    roomRepository.save(room);
                } else if (BookingStatus.PENDING.equals(bookingStatus)
                        && !RoomStatus.BOOKED.equals(currentRoomStatus)) {
                    room.setRoomStatus(RoomStatus.BOOKED);
                    roomRepository.save(room);
                }
            } else {
                // 如果當天沒有該房間的有效訂單，且房間狀態為「已入住」或「已預訂」，則復原為「可預訂」
                if (RoomStatus.OCCUPIED.equals(currentRoomStatus) || RoomStatus.BOOKED.equals(currentRoomStatus)) {
                    room.setRoomStatus(RoomStatus.AVAILABLE);
                    roomRepository.save(room);
                }
            }
        }
    }
}
