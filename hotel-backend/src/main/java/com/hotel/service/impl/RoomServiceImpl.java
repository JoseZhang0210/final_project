package com.hotel.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.dto.RoomDTO;
import com.hotel.model.entity.Room;
import com.hotel.repository.RoomRepository;
import com.hotel.service.RoomService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final com.hotel.repository.BookingRepository bookingRepository;

    public RoomServiceImpl(RoomRepository roomRepository, com.hotel.repository.BookingRepository bookingRepository) {
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
            if (("維修中".equals(updatedRoomDTO.getRoomStatus()) || "停用".equals(updatedRoomDTO.getRoomStatus())) && !updatedRoomDTO.getRoomStatus().equals(existingRoom.getRoomStatus())) {
                boolean hasFutureBookings = bookingRepository.existsByRoomIdAndCheckOutDateGreaterThanEqual(id, java.time.LocalDate.now());
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
    @Transactional
    public void syncRoomStatuses() {
        java.time.LocalDate today = java.time.LocalDate.now();
        List<Room> allRooms = roomRepository.findAll();
        List<com.hotel.model.entity.Booking> todayBookings = bookingRepository.findAll().stream()
                .filter(b -> b.getRoomId() != null)
                .filter(b -> !b.getCheckInDate().isAfter(today) && !b.getCheckOutDate().isBefore(today))
                .filter(b -> !"已取消".equals(b.getBookingStatus()) && !"已完成".equals(b.getBookingStatus()))
                .collect(Collectors.toList());

        for (Room room : allRooms) {
            String currentRoomStatus = room.getRoomStatus();
            
            // 系統不應強制覆蓋實體維運狀態
            if ("維修中".equals(currentRoomStatus) || "停用".equals(currentRoomStatus) || 
                "退房待清潔".equals(currentRoomStatus) || "清潔中".equals(currentRoomStatus)) {
                continue;
            }

            Optional<com.hotel.model.entity.Booking> activeBooking = todayBookings.stream()
                    .filter(b -> b.getRoomId().equals(room.getRoomId()))
                    .findFirst();

            if (activeBooking.isPresent()) {
                String bookingStatus = activeBooking.get().getBookingStatus();
                if ("已入住".equals(bookingStatus) && !"已入住".equals(currentRoomStatus)) {
                    room.setRoomStatus("已入住");
                    roomRepository.save(room);
                } else if ("待入住".equals(bookingStatus) && !"已預訂".equals(currentRoomStatus)) {
                    room.setRoomStatus("已預訂");
                    roomRepository.save(room);
                }
            } else {
                // 如果當天沒有該房間的有效訂單，且房間狀態為「已入住」或「已預訂」，則復原為「可預訂」
                if ("已入住".equals(currentRoomStatus) || "已預訂".equals(currentRoomStatus)) {
                    room.setRoomStatus("可預訂");
                    roomRepository.save(room);
                }
            }
        }
    }
}
