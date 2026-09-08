package com.hotel.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.hotel.model.entity.Rental;

/**
 * Rental 資料存取層。
 */
public interface RentalRepository extends JpaRepository<Rental, Integer> {
    // 新增驗證與前台占用共用有效狀態；已完成保留歷史占用，取消不占用。
    @Query("select r from Rental r where (:venueId is null or r.venueId = :venueId) and r.rentalDate >= :from and r.rentalDate < :until and upper(trim(r.rentalStatus)) in ('PENDING','CONFIRMED','COMPLETED','待確認','待付款','已確認','已完成')")
    List<Rental> findOccupied(@Param("venueId") Integer venueId, @Param("from") LocalDateTime from, @Param("until") LocalDateTime until); // 半開日期區間涵蓋既有非零時資料。

    List<Rental> findByVenueId(Integer venueId);

    List<Rental> findByMemberIdOrderByRentalDateDesc(Integer memberId);

    boolean existsByVenueId(Integer venueId);

}
