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
import com.hotel.repository.RoomTaskRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.service.RoomTaskService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RoomTaskServiceImpl implements RoomTaskService {

    private final RoomTaskRepository roomTaskRepository;
    private final RoomRepository roomRepository;

    public RoomTaskServiceImpl(RoomTaskRepository roomTaskRepository, RoomRepository roomRepository) {
        this.roomTaskRepository = roomTaskRepository;
        this.roomRepository = roomRepository;
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
                
                // 連動房間：可預訂
                if (existingTask.getRoomId() != null) {
                    Room room = roomRepository.findById(existingTask.getRoomId()).orElse(null);
                    if (room != null) {
                        room.setRoomStatus("可預訂");
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
