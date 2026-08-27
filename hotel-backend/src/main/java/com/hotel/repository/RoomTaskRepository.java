package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotel.model.entity.RoomTask;

public interface RoomTaskRepository extends JpaRepository<RoomTask, Integer> {

    List<RoomTask> findByPriority(String priority);

    // 透過 @Query 跨表存取 Room.roomId
    @Query("SELECT rt FROM RoomTask rt WHERE rt.room.roomId = :roomId")
    List<RoomTask> findByRoomId(@Param("roomId") Integer roomId);

    // 透過 @Query 跨表存取 Employee.employeeId
    @Query("SELECT rt FROM RoomTask rt WHERE rt.employee.employeeId = :employeeId")
    List<RoomTask> findByEmployeeId(@Param("employeeId") Integer employeeId);

}