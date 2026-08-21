package com.hotel.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotel.entity.Rental;

/**
 * Rental 資料存取層。
 */
public interface RentalRepository extends JpaRepository<Rental, Integer> {

    List<Rental> findByVenueId(Integer venueId);

    List<Rental> findByMemberIdOrderByRentalDateDesc(Integer memberId);

    boolean existsByVenueId(Integer venueId);

    /**
     * 同場地、同時間，只要不是取消狀態就視為撞期。
     *
     * 同時相容目前專題資料中可能存在的：
     * CANCELLED / 已取消
     */
    @Query("""
            select count(r)
            from Rental r
            where r.venueId = :venueId
              and r.rentalDate = :rentalDate
              and upper(coalesce(r.rentalStatus, '')) <> 'CANCELLED'
              and coalesce(r.rentalStatus, '') <> '已取消'
            """)
    long countActiveCollisions(
            @Param("venueId") Integer venueId,
            @Param("rentalDate") LocalDateTime rentalDate);

    /**
     * 修改 Rental 時排除目前這一筆。
     */
    @Query("""
            select count(r)
            from Rental r
            where r.venueId = :venueId
              and r.rentalDate = :rentalDate
              and r.rentalId <> :rentalId
              and upper(coalesce(r.rentalStatus, '')) <> 'CANCELLED'
              and coalesce(r.rentalStatus, '') <> '已取消'
            """)
    long countActiveCollisionsExcludingRental(
            @Param("venueId") Integer venueId,
            @Param("rentalDate") LocalDateTime rentalDate,
            @Param("rentalId") Integer rentalId);
}