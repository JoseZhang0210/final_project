package com.hotel.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.RoomTask;

public interface RoomTaskRepository extends JpaRepository<RoomTask, Integer> {

    List<RoomTask> findByRoomId(Integer roomId);

    List<RoomTask> findByTaskStatus(String taskStatus);

    List<RoomTask> findByTaskType(String taskType);

    List<RoomTask> findByPriority(String priority);

}
