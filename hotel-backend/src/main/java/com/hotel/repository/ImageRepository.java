package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.RoomImage;

public interface ImageRepository extends JpaRepository<RoomImage, Integer> {

}
