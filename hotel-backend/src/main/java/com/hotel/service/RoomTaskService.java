package com.hotel.service;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.entity.RoomTask;
import com.hotel.repository.RoomTaskRepository;

@Service
@Transactional
public class RoomTaskService {

    private final RoomTaskRepository roomTaskRepository;

    public RoomTaskService(RoomTaskRepository roomTaskRepository) {
        this.roomTaskRepository = roomTaskRepository;
    }

    // Create
    public RoomTask insert(RoomTask roomTask) {
        return roomTaskRepository.save(roomTask);
    }

    // Read All
    @Transactional(readOnly = true)
    public List<RoomTask> findAll() {
        return roomTaskRepository.findAll();
    }

    // Read by ID
    @Transactional(readOnly = true)
    public Optional<RoomTask> findById(Integer id) {
        return roomTaskRepository.findById(id);
    }

    public List<RoomTask> findByTaskStatus(String taskStatus) {
        return roomTaskRepository.findByTaskStatus(taskStatus);
    }

    public List<RoomTask> findByTaskType(String taskType) {
        return roomTaskRepository.findByTaskType(taskType);
    }

    public List<RoomTask> findByPriority(String priority) {
        return roomTaskRepository.findByPriority(priority);
    }

    // Update
    public RoomTask update(Integer id, RoomTask updatedTask) {
        return roomTaskRepository.findById(id)
                .map(task -> {
                    if (updatedTask.getRoomId() != null) {
                        task.setRoomId(updatedTask.getRoomId());
                    }
                    if (updatedTask.getEmployeeId() != null) {
                        task.setEmployeeId(updatedTask.getEmployeeId());
                    }
                    if (updatedTask.getRemark() != null) {
                        task.setRemark(updatedTask.getRemark());
                    }
                    if (updatedTask.getPriority() != null) {
                        task.setPriority(updatedTask.getPriority());
                    }
                    if (updatedTask.getCreatedAt() != null) {
                        task.setCreatedAt(updatedTask.getCreatedAt());
                    }
                    if (updatedTask.getCompletedAt() != null) {
                        task.setCompletedAt(updatedTask.getCompletedAt());
                    }
                    if (updatedTask.getTaskType() != null) {
                        task.setTaskType(updatedTask.getTaskType());
                    }
                    if (updatedTask.getTaskStatus() != null) {
                        task.setTaskStatus(updatedTask.getTaskStatus());
                    }
                    return roomTaskRepository.save(task);
                })
                .orElseThrow(() -> new RuntimeException("RoomTask not found with id: " + id));
    }

    // Delete
    public boolean deleteById(Integer id) {
        if (roomTaskRepository.existsById(id)) {
            roomTaskRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<RoomTask> findByRoomId(Integer roomId) {
        return roomTaskRepository.findByRoomId(roomId);
    }

}
