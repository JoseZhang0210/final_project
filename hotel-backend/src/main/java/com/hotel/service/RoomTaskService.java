package com.hotel.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.entity.RoomTask;
import com.hotel.repository.RoomTaskRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
@Transactional
public class RoomTaskService {

    private final RoomTaskRepository roomTaskRepository;

    public RoomTaskService(RoomTaskRepository roomTaskRepository) {
        this.roomTaskRepository = roomTaskRepository;
    }

    // 1. Create - 新增房務任務
    public RoomTask insert(RoomTask roomTask) {
        // 若建立時間未帶入，預設填入當下時間
        if (roomTask.getCreatedAt() == null) {
            roomTask.setCreatedAt(LocalDateTime.now());
        }
        return roomTaskRepository.save(roomTask);
    }

    // 2. Read All - 取得所有任務
    @Transactional(readOnly = true)
    public List<RoomTask> findAll() {
        return roomTaskRepository.findAll();
    }

    // 3-1. Read Optional by ID
    @Transactional(readOnly = true)
    public Optional<RoomTask> findOptionalById(Integer id) {
        return roomTaskRepository.findById(id);
    }

    // 3-3. 依房間 ID 查詢任務
    @Transactional(readOnly = true)
    public List<RoomTask> findByRoomId(Integer roomId) {
        return roomTaskRepository.findByRoomId(roomId);
    }

    // 3-6. 依優先級查詢
    @Transactional(readOnly = true)
    public List<RoomTask> findByPriority(String priority) {
        return roomTaskRepository.findByPriority(priority);
    }

    // 3-7. 依員工 ID 查詢
    @Transactional(readOnly = true)
    public List<RoomTask> findByEmployeeId(Integer employeeId) {
        return roomTaskRepository.findByEmployeeId(employeeId);
    }

    // 4. Update - 更新任務狀態與欄位 (限定欄位)
    public RoomTask update(Integer id, RoomTask updatedTask) {
        RoomTask existingTask = roomTaskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的任務資料"));

        // 1. 只針對允許更新的欄位進行變更
        if (updatedTask.getPriority() != null) {
            existingTask.setPriority(updatedTask.getPriority());
        }
        if (updatedTask.getTaskType() != null) {
            existingTask.setTaskType(updatedTask.getTaskType());
        }
        if (updatedTask.getRemark() != null) {
            existingTask.setRemark(updatedTask.getRemark());
        }

        // 2. 處理狀態變更與 completedAt 的邏輯
        if (updatedTask.getTaskStatus() != null) {
            existingTask.setTaskStatus(updatedTask.getTaskStatus());

            if ("已完成".equals(updatedTask.getTaskStatus())) {
                existingTask.setCompletedAt(updatedTask.getCompletedAt() != null
                        ? updatedTask.getCompletedAt()
                        : LocalDateTime.now());
            } else {
                existingTask.setCompletedAt(null);
            }
        }

        // 交易結束時 JPA Dirty Checking 會自動更新異動欄位
        return existingTask;
    }

    // 5. Delete By Id - 刪除任務
    public void deleteById(Integer id) {
        if (!roomTaskRepository.existsById(id)) {
            throw new EntityNotFoundException("欲刪除的任務 ID: " + id + " 不存在");
        }
        roomTaskRepository.deleteById(id);
    }
}