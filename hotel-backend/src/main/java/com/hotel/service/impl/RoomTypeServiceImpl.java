package com.hotel.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.dto.RoomTypeDTO;
import com.hotel.model.entity.Room;
import com.hotel.model.entity.RoomType;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.RoomTypeRepository;
import com.hotel.service.RoomTypeService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;
    private final com.hotel.repository.RoomImageRepository roomImageRepository;

    public RoomTypeServiceImpl(RoomTypeRepository roomTypeRepository, RoomRepository roomRepository,
            BookingRepository bookingRepository, com.hotel.repository.RoomImageRepository roomImageRepository) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
        this.roomImageRepository = roomImageRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeDTO> findAll() {
        return roomTypeRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTypeDTO> findAllWithAvailability(LocalDate checkIn, LocalDate checkOut) {
        return roomTypeRepository.findAll().stream()
                .map(rt -> convertToDTOWithDates(rt, checkIn, checkOut))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoomTypeDTO> findOptionalById(Integer id) {
        return roomTypeRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    public RoomTypeDTO insert(RoomTypeDTO roomTypeDTO) {
        RoomType roomType = convertToEntity(roomTypeDTO);
        RoomType saved = roomTypeRepository.save(roomType);
        return convertToDTO(saved);
    }

    @Override
    public RoomTypeDTO update(Integer id, RoomTypeDTO updatedRoomTypeDTO) {
        RoomType existingRoomType = roomTypeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的房型資料"));

        if (updatedRoomTypeDTO.getTypeName() != null) {
            existingRoomType.setTypeName(updatedRoomTypeDTO.getTypeName());
        }
        if (updatedRoomTypeDTO.getBedType() != null) {
            existingRoomType.setBedType(updatedRoomTypeDTO.getBedType());
        }
        if (updatedRoomTypeDTO.getCapacity() != null) {
            existingRoomType.setCapacity(updatedRoomTypeDTO.getCapacity());
        }
        if (updatedRoomTypeDTO.getRoomDescription() != null) {
            existingRoomType.setRoomDescription(updatedRoomTypeDTO.getRoomDescription());
        }
        if (updatedRoomTypeDTO.getPricePerNight() != null) {
            existingRoomType.setPricePerNight(updatedRoomTypeDTO.getPricePerNight());
        }
        if (updatedRoomTypeDTO.getAvailableRooms() != null) {
            existingRoomType.setAvailableRooms(updatedRoomTypeDTO.getAvailableRooms());
        }

        return convertToDTO(existingRoomType);
    }

    @Override
    public void deleteById(Integer id) {
        if (!roomTypeRepository.existsById(id)) {
            throw new EntityNotFoundException("欲刪除的房型 ID: " + id + " 不存在");
        }
        roomTypeRepository.deleteById(id);
    }

    // 查詢指定日期區間可用房間數
    private Integer calculateAvailableRooms(Integer roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        List<Integer> bookedRoomIds = bookingRepository.findBookedRoomIds(roomTypeId, checkIn, checkOut);
        List<Room> allRooms = roomRepository.findByRoomTypeId(roomTypeId);
        
        return (int) allRooms.stream()
                .filter(room -> !bookedRoomIds.contains(room.getRoomId()))
                .filter(room -> !"停用".equals(room.getRoomStatus()) && !"維修中".equals(room.getRoomStatus()))
                .count();
    }

    // 目前日可用數（給後台用）
    // 目前日可用數（給內部備用，若有需要）
    private Integer calculateAvailableRoomsToday(Integer roomTypeId) {
        LocalDate today = LocalDate.now();
        return calculateAvailableRooms(roomTypeId, today, today.plusDays(1));
    }

    private RoomTypeDTO convertToDTO(RoomType roomType) {
        RoomTypeDTO dto = new RoomTypeDTO();
        dto.setRoomTypeId(roomType.getRoomTypeId());
        dto.setTypeName(roomType.getTypeName());
        dto.setBedType(roomType.getBedType());
        dto.setCapacity(roomType.getCapacity());
        dto.setRoomDescription(roomType.getRoomDescription());
        dto.setPricePerNight(roomType.getPricePerNight());
        // 後台管理列表：直接顯示資料庫中的原始設定數量
        dto.setAvailableRooms(roomType.getAvailableRooms());
        // 動態計算今日可用數 (從今日到明日)
        dto.setTodayAvailableRooms(calculateAvailableRooms(roomType.getRoomTypeId(), LocalDate.now(), LocalDate.now().plusDays(1)));
        
        // 載入主圖
        List<com.hotel.model.entity.RoomImage> images = roomImageRepository.findByRoomTypeId(roomType.getRoomTypeId());
        if (!images.isEmpty()) {
            dto.setMainImageUrl(images.get(0).getPath());
        }
        
        return dto;
    }

    private RoomTypeDTO convertToDTOWithDates(RoomType roomType, LocalDate checkIn, LocalDate checkOut) {
        RoomTypeDTO dto = new RoomTypeDTO();
        dto.setRoomTypeId(roomType.getRoomTypeId());
        dto.setTypeName(roomType.getTypeName());
        dto.setBedType(roomType.getBedType());
        dto.setCapacity(roomType.getCapacity());
        dto.setRoomDescription(roomType.getRoomDescription());
        dto.setPricePerNight(roomType.getPricePerNight());
        dto.setAvailableRooms(calculateAvailableRooms(roomType.getRoomTypeId(), checkIn, checkOut));
        
        // 載入主圖
        List<com.hotel.model.entity.RoomImage> images = roomImageRepository.findByRoomTypeId(roomType.getRoomTypeId());
        if (!images.isEmpty()) {
            dto.setMainImageUrl(images.get(0).getPath());
        }
        
        return dto;
    }

    private RoomType convertToEntity(RoomTypeDTO dto) {
        RoomType roomType = new RoomType();
        roomType.setTypeName(dto.getTypeName());
        roomType.setBedType(dto.getBedType());
        roomType.setCapacity(dto.getCapacity());
        roomType.setRoomDescription(dto.getRoomDescription());
        roomType.setPricePerNight(dto.getPricePerNight());
        roomType.setAvailableRooms(dto.getAvailableRooms());
        return roomType;
    }
}
