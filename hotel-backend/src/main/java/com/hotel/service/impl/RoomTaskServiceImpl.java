package com.hotel.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.dto.RoomTaskDTO;
import com.hotel.model.entity.RoomTask;
import com.hotel.model.entity.Room;
import com.hotel.model.entity.Booking;
import com.hotel.repository.RoomTaskRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.BookingRepository;
import com.hotel.service.RoomTaskService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RoomTaskServiceImpl implements RoomTaskService {

    private final RoomTaskRepository roomTaskRepository;
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    public RoomTaskServiceImpl(RoomTaskRepository roomTaskRepository, RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomTaskRepository = roomTaskRepository;
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTaskDTO> findAll() {
        return roomTaskRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoomTaskDTO> findOptionalById(Integer id) {
        return roomTaskRepository.findById(id).map(this::convertToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTaskDTO> findByRoomId(Integer roomId) {
        return roomTaskRepository.findByRoomId(roomId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTaskDTO> findByPriority(String priority) {
        return roomTaskRepository.findByPriority(priority).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomTaskDTO> findByEmployeeId(Integer employeeId) {
        return roomTaskRepository.findByEmployeeId(employeeId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RoomTaskDTO insert(RoomTaskDTO roomTaskDTO) {
        RoomTask roomTask = convertToEntity(roomTaskDTO);
        if (roomTask.getCreatedAt() == null) {
            roomTask.setCreatedAt(LocalDateTime.now());
        }
        RoomTask saved = roomTaskRepository.save(roomTask);
        return convertToDTO(saved);
    }

    @Override
    public RoomTaskDTO update(Integer id, RoomTaskDTO updatedTaskDTO) {
        RoomTask existingTask = roomTaskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的任務資料"));

        if (updatedTaskDTO.getPriority() != null) {
            existingTask.setPriority(updatedTaskDTO.getPriority());
        }
        if (updatedTaskDTO.getTaskType() != null) {
            existingTask.setTaskType(updatedTaskDTO.getTaskType());
        }
        if (updatedTaskDTO.getRemark() != null) {
            existingTask.setRemark(updatedTaskDTO.getRemark());
        }

        if (updatedTaskDTO.getTaskStatus() != null && !updatedTaskDTO.getTaskStatus().equals(existingTask.getTaskStatus())) {
            String newStatus = updatedTaskDTO.getTaskStatus();
            existingTask.setTaskStatus(newStatus);

            if ("已完成".equals(newStatus)) {
                existingTask.setCompletedAt(updatedTaskDTO.getCompletedAt() != null
                        ? updatedTaskDTO.getCompletedAt()
                        : LocalDateTime.now());
                
                // 連動房間：根據工單類型決定狀態
                if (existingTask.getRoomId() != null) {
                    Room room = roomRepository.findById(existingTask.getRoomId()).orElse(null);
                    if (room != null) {
                        String taskType = existingTask.getTaskType();
                        if ("退房清潔".equals(taskType) || "維修保養".equals(taskType)) {
                            java.time.LocalDate today = java.time.LocalDate.now();
                            boolean hasBookingToday = bookingRepository.findAll().stream()
                                    .filter(b -> b.getRoomId() != null && b.getRoomId().equals(room.getRoomId()))
                                    .filter(b -> !b.getCheckInDate().isAfter(today) && !b.getCheckOutDate().isBefore(today))
                                    .anyMatch(b -> "待入住".equals(b.getBookingStatus()) || "已入住".equals(b.getBookingStatus()));
                                    
                            if (hasBookingToday) {
                                room.setRoomStatus("已預訂");
                            } else {
                                room.setRoomStatus("可預訂");
                            }
                        } else if ("日常清潔".equals(taskType) || "備品補充".equals(taskType)) {
                            room.setRoomStatus("已入住");
                        } else {
                            room.setRoomStatus("可預訂"); // 預設防呆
                        }
                        roomRepository.save(room);
                    }
                }
            } else {
                existingTask.setCompletedAt(null);
                
                // 連動房間：清潔中
                if ("清潔中".equals(newStatus) || "處理中".equals(newStatus) || "進行中".equals(newStatus)) {
                    if (existingTask.getRoomId() != null) {
                        Room room = roomRepository.findById(existingTask.getRoomId()).orElse(null);
                        if (room != null) {
                            room.setRoomStatus("清潔中");
                            roomRepository.save(room);
                        }
                    }
                }
            }
        }

        return convertToDTO(existingTask);
    }

    @Override
    public void deleteById(Integer id) {
        if (!roomTaskRepository.existsById(id)) {
            throw new EntityNotFoundException("欲刪除的任務 ID: " + id + " 不存在");
        }
        roomTaskRepository.deleteById(id);
    }

    private RoomTaskDTO convertToDTO(RoomTask task) {
        RoomTaskDTO dto = new RoomTaskDTO();
        dto.setTaskId(task.getTaskId());
        dto.setRoomId(task.getRoomId());
        dto.setEmployeeId(task.getEmployeeId());
        dto.setPriority(task.getPriority());
        dto.setTaskType(task.getTaskType());
        dto.setTaskStatus(task.getTaskStatus());
        dto.setRemark(task.getRemark());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setCompletedAt(task.getCompletedAt());
        return dto;
    }

    @Override
    public int autoCreateTasksFromRooms() {
        int createdCount = 0;
        // 找出所有退房待清潔的房間
        List<Room> roomsToClean = roomRepository.findAll().stream()
                .filter(r -> "退房待清潔".equals(r.getRoomStatus()))
                .collect(Collectors.toList());

        for (Room room : roomsToClean) {
            // 檢查是否已經有這個房間且還沒完成的退房清潔工單
            boolean hasPendingTask = roomTaskRepository.findByRoomId(room.getRoomId()).stream()
                    .anyMatch(t -> "退房清潔".equals(t.getTaskType()) && 
                                  ("待處理".equals(t.getTaskStatus()) || "進行中".equals(t.getTaskStatus())));

            if (!hasPendingTask) {
                RoomTask task = new RoomTask();
                task.setRoomId(room.getRoomId());
                
                // 自動指派給工作量最少的房務專員，若無則預設 13
                Integer leastLoadedEmployee = roomTaskRepository.findLeastLoadedHousekeeper();
                task.setEmployeeId(leastLoadedEmployee != null ? leastLoadedEmployee : 13);
                
                task.setPriority("一般");
                task.setTaskType("退房清潔");
                task.setTaskStatus("進行中");
                task.setRemark("系統自動偵測房間狀態產生");
                task.setCreatedAt(LocalDateTime.now());
                roomTaskRepository.save(task);
                createdCount++;
            }
        }
        return createdCount;
    }

    private RoomTask convertToEntity(RoomTaskDTO dto) {
        RoomTask task = new RoomTask();
        task.setRoomId(dto.getRoomId());
        task.setEmployeeId(dto.getEmployeeId());
        task.setPriority(dto.getPriority());
        task.setTaskType(dto.getTaskType());
        task.setTaskStatus(dto.getTaskStatus());
        task.setRemark(dto.getRemark());
        task.setCreatedAt(dto.getCreatedAt());
        task.setCompletedAt(dto.getCompletedAt());
        return task;
    }
}
