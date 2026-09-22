package com.hotel.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.hotel.model.dto.RoomTaskDTO;

/**
 * 客房房務任務服務介面 (Room Task Service)
 */
public interface RoomTaskService {
    List<RoomTaskDTO> findAll();

    Optional<RoomTaskDTO> findOptionalById(Integer id);

    List<RoomTaskDTO> findByRoomId(Integer roomId);

    List<RoomTaskDTO> findByPriority(String priority);

    List<RoomTaskDTO> findByEmployeeId(Integer employeeId);

    RoomTaskDTO insert(RoomTaskDTO roomTaskDTO);

    RoomTaskDTO update(Integer id, RoomTaskDTO updatedTaskDTO);

    void deleteById(Integer id);

    // 自動掃描退房待清潔的房間並建立工單
    int autoCreateTasksFromRooms();

    // 建立退房清潔工單 (供訂房退房/取消狀態聯動調用，實現模組解耦)
    RoomTaskDTO createCheckoutCleaningTask(Integer roomId, LocalDateTime createdAt, String remark);
}