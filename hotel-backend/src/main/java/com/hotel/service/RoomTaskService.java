package com.hotel.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.entity.RoomTask;
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

    // // Read by ID
    // @Transactional(readOnly = true)
    // public Optional<RoomTask> findById(Integer id) {
    // return roomTaskRepository.findById(id);
    // }

    public Optional<RoomTask> findById(Integer id) {
        return roomTaskRepository.findById(id);
    }

    public List<RoomTask> findByRoomId(Integer roomId) {
        return roomTaskRepository.findByRoomIdOrderByTaskIdDesc(roomId);
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

    public List<RoomTask> findByEmployeeId(Integer employeeId) {
        return roomTaskRepository.findByEmployeeId(employeeId);
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
                    if (updatedTask.getTaskType() != null) {
                        task.setTaskType(updatedTask.getTaskType());
                    }

                    // 處理狀態與完成時間邏輯
                    if (updatedTask.getTaskStatus() != null) {
                        task.setTaskStatus(updatedTask.getTaskStatus());

                        // 當狀態更新為「已完成」，自動記錄當下時間（若前端有傳入時間則以前端為主）
                        if ("已完成".equals(updatedTask.getTaskStatus())) {
                            task.setCompletedAt(updatedTask.getCompletedAt() != null
                                    ? updatedTask.getCompletedAt()
                                    : LocalDateTime.now());
                        } else {
                            // 若狀態被改回非「已完成」（如改回待處理），清空完成時間
                            task.setCompletedAt(null);
                        }
                    } else if (updatedTask.getCompletedAt() != null) {
                        task.setCompletedAt(updatedTask.getCompletedAt());
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

}
