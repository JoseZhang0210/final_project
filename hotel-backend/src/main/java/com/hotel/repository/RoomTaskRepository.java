package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.RoomTask;

public interface RoomTaskRepository extends JpaRepository<RoomTask, Integer> {

    List<RoomTask> findByPriority(String priority);

    List<RoomTask> findByRoomId(Integer roomId);

    // 因為 RoomTask 實體中是直接定義 Integer employeeId;，所以不需要跨表查詢
    List<RoomTask> findByEmployeeId(Integer employeeId);

    // 找出目前工作量最少的房務專員 (employee_id 13~24)
    @org.springframework.data.jpa.repository.Query(value = 
        "SELECT TOP 1 e.employee_id " +
        "FROM employee e " +
        "LEFT JOIN room_task rt ON e.employee_id = rt.employee_id AND rt.task_status IN (N'待處理', N'進行中') " +
        "WHERE e.employee_id BETWEEN 13 AND 24 " +
        "GROUP BY e.employee_id " +
        "ORDER BY COUNT(rt.task_id) ASC", 
        nativeQuery = true)
    Integer findLeastLoadedHousekeeper();

}