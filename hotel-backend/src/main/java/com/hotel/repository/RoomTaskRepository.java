package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.RoomTask;

public interface RoomTaskRepository extends JpaRepository<RoomTask, Integer> {

    List<RoomTask> findByPriority(String priority);

    List<RoomTask> findByRoomId(Integer roomId);

    // 因為 RoomTask 實體中是直接定義 Integer employeeId;，所以不需要跨表查詢
    List<RoomTask> findByEmployeeId(Integer employeeId);

}