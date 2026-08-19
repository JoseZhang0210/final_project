package com.hotel.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hotel.entity.Rental;

/**
 * Rental 的資料存取層。
 */
public interface RentalRepository extends JpaRepository<Rental, Integer> {

    /**
     * 查詢某一個場地的所有租借紀錄。
     */
    List<Rental> findByVenueId(Integer venueId);

    /**
     * 新增租借時，用來檢查：
     * 同一個場地、同一個時間，
     * 是否已經存在「不是 CANCELLED」的租借。
     *
     * 目前資料表只有 rental_date 一個時間點，
     * 所以這裡只能檢查「相同時間」。
     */
    boolean existsByVenueIdAndRentalDateAndRentalStatusNot(
            Integer venueId,
            LocalDateTime rentalDate,
            String rentalStatus);
}
