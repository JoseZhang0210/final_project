package com.hotel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.model.entity.Venue;

/**
 * Venue 的資料存取層。
 *
 * JpaRepository 已經提供常用 CRUD：
 * save()、findAll()、findById()、deleteById() 等。
 */
public interface VenueRepository extends JpaRepository<Venue, Integer> {
}
