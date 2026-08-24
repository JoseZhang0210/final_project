package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.RoomImage;

public interface RoomImageRepository extends JpaRepository<RoomImage, Integer> {

    // void findByRoomType_RoomTypeId(Integer roomTypeId);
}
