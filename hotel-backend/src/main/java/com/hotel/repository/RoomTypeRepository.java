package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.RoomType;

public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {

}
