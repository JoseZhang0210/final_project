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

    // 3-2. Read by ID (找不到時拋出例外)
    @Transactional(readOnly = true)
    public RoomTask findById(Integer id) {
        return roomTaskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的任務資料"));
    }

    // 3-3. 依房間 ID 查詢任務 (按任務 ID 降序排列)
    @Transactional(readOnly = true)
    public List<RoomTask> findByRoomId(Integer roomId) {
        return roomTaskRepository.findByRoomIdOrderByTaskIdDesc(roomId);
    }

    // 3-4. 依任務狀態查詢
    @Transactional(readOnly = true)
    public List<RoomTask> findByTaskStatus(String taskStatus) {
        return roomTaskRepository.findByTaskStatus(taskStatus);
    }

    // 3-5. 依任務類型查詢
    @Transactional(readOnly = true)
    public List<RoomTask> findByTaskType(String taskType) {
        return roomTaskRepository.findByTaskType(taskType);
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

    // 4. Update - 更新任務狀態與欄位 (利用 Dirty Checking)
    public RoomTask update(Integer id, RoomTask updatedTask) {
        RoomTask existingTask = roomTaskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到 ID 為 " + id + " 的任務資料"));

        // 修正：依照 Entity 關聯更新 Room 與 Employee 物件，而非不存在的 Id 欄位
        if (updatedTask.getRoom() != null) {
            existingTask.setRoom(updatedTask.getRoom());
        }
        if (updatedTask.getEmployee() != null) {
            existingTask.setEmployee(updatedTask.getEmployee());
        }
        if (updatedTask.getRemark() != null) {
            existingTask.setRemark(updatedTask.getRemark());
        }
        if (updatedTask.getPriority() != null) {
            existingTask.setPriority(updatedTask.getPriority());
        }
        if (updatedTask.getTaskType() != null) {
            existingTask.setTaskType(updatedTask.getTaskType());
        }

        // 處理狀態與完成時間邏輯
        if (updatedTask.getTaskStatus() != null) {
            existingTask.setTaskStatus(updatedTask.getTaskStatus());

            // 當狀態更新為「已完成」，自動記錄當下時間（若前端有傳入時間則以前端為主）
            if ("已完成".equals(updatedTask.getTaskStatus())) {
                existingTask.setCompletedAt(updatedTask.getCompletedAt() != null
                        ? updatedTask.getCompletedAt()
                        : LocalDateTime.now());
            } else {
                // 若狀態改回非「已完成」（如改回處理中/待處理），清空完成時間
                existingTask.setCompletedAt(null);
            }
        } else if (updatedTask.getCompletedAt() != null) {
            existingTask.setCompletedAt(updatedTask.getCompletedAt());
        }

        // 交易結束時 JPA 會自動比對並發送 Update SQL，無需 call save()
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