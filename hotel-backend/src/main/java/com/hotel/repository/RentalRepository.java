package com.hotel.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.Rental;

/**
 * Rental 資料存取層。
 */
public interface RentalRepository
        extends JpaRepository<Rental, Integer> {

    /**
     * 查詢指定場地的租借紀錄。
     */
    List<Rental> findByVenueId(Integer venueId);

    /**
     * 判斷指定 Venue 是否已經存在 Rental。
     *
     * Venue 刪除前使用，
     * 不完全依賴 SQL Server FK 才發現關聯資料。
     */
    boolean existsByVenueId(Integer venueId);

    /**
     * 新增時檢查：
     * 同一場地、同一時間是否已有非 CANCELLED 租借。
     */
    boolean existsByVenueIdAndRentalDateAndRentalStatusNot(
            Integer venueId,
            LocalDateTime rentalDate,
            String rentalStatus);

    /**
     * 修改時檢查：
     * 同一場地、同一時間是否已有其他非 CANCELLED 租借。
     *
     * RentalIdNot 用來排除正在修改的自己。
     */
    boolean existsByVenueIdAndRentalDateAndRentalStatusNotAndRentalIdNot(
            Integer venueId,
            LocalDateTime rentalDate,
            String rentalStatus,
            Integer rentalId);
}
