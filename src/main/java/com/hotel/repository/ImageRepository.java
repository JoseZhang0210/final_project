package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.Image;

public interface ImageRepository extends JpaRepository<Image, Integer> {

}
