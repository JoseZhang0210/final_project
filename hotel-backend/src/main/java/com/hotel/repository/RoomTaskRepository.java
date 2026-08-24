package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.RoomTask;

public interface RoomTaskRepository extends JpaRepository<RoomTask, Integer> {

    List<RoomTask> findByRoomIdOrderByTaskIdDesc(Integer roomId);

    List<RoomTask> findByTaskStatus(String taskStatus);

    List<RoomTask> findByTaskType(String taskType);

    List<RoomTask> findByPriority(String priority);

    List<RoomTask> findByEmployeeId(Integer employeeId);

    List<RoomTask> findByRoom_RoomIdOrderByTaskIdDesc(Integer roomId);

    List<RoomTask> findByEmployee_EmployeeId(Integer employeeId);

}
