package com.hotel.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.dto.RoomTaskDTO;
import com.hotel.model.entity.RoomTask;
import com.hotel.repository.RoomTaskRepository;
import com.hotel.service.RoomTaskService;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RoomTaskServiceImpl implements RoomTaskService {

    private final RoomTaskRepository roomTaskRepository;

    public RoomTaskServiceImpl(RoomTaskRepository roomTaskRepository) {
        this.roomTaskRepository = roomTaskRepository;
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

        if (updatedTaskDTO.getTaskStatus() != null) {
            existingTask.setTaskStatus(updatedTaskDTO.getTaskStatus());

            if ("已完成".equals(updatedTaskDTO.getTaskStatus())) {
                existingTask.setCompletedAt(updatedTaskDTO.getCompletedAt() != null
                        ? updatedTaskDTO.getCompletedAt()
                        : LocalDateTime.now());
            } else {
                existingTask.setCompletedAt(null);
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
