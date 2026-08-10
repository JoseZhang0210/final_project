package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.RoomTask;

public interface RoomTaskRepository extends JpaRepository<RoomTask, Integer> {

}
