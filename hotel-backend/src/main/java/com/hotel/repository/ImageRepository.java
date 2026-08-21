package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Integer> {

}
