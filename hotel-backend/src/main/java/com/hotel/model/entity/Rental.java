package com.hotel.model.entity;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 場地租借資料表 Entity。
 *
 * 對應資料庫中的 rental 資料表。
 * 一筆 Rental 代表「一位會員對某一個場地的一次租借紀錄」。
 */
@Entity
@Table(name = "rental")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rental {

    /**
     * 租借編號，Primary Key。
     *
     * 目前組長的 SQL 設計中 rental_id 不是 IDENTITY，
     * 因此沒有使用 @GeneratedValue。
     */
    @Id
    @Column(name = "rental_id")
    private Integer rentalId;

    /**
     * 場地編號。
     * 資料庫外來鍵：
     * rental.venue_id -> venue.venue_id
     *
     * 目前先使用 Integer，
     * 避免與其他組員 Entity 寫法互相影響。
     */
    @Column(name = "venue_id", nullable = false)
    private Integer venueId;

    /**
     * 會員編號。
     * 資料庫外來鍵：
     * rental.member_id -> member.member_id
     */
    @Column(name = "member_id", nullable = false)
    private Integer memberId;

    /**
     * 活動名稱。
     * 例如：婚宴、研討會、公司尾牙。
     */
    @Column(name = "event_name", nullable = false, length = 50)
    private String eventName;

    /**
     * 租借日期與時間。
     *
     * HTML datetime-local 的格式例如：
     * 2026-08-20T18:00
     */
    @Column(name = "rental_date", nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime rentalDate;

    /**
     * 預計參加活動的人數。
     */
    @Column(name = "guest_count", nullable = false)
    private Integer guestCount;

    /**
     * 付款編號。
     * 資料庫外來鍵：
     * rental.payment_id -> payment.payment_id
     *
     * 目前 SQL 設定 NOT NULL，因此表單必須輸入。
     */
    @Column(name = "payment_id", nullable = false)
    private Integer paymentId;

    /**
     * 此筆租借紀錄的處理狀態。
     *
     * 建議值：
     * PENDING   = 待確認
     * CONFIRMED = 已確認
     * CANCELLED = 已取消
     * COMPLETED = 已完成
     */
    @Column(name = "rental_status", length = 50)
    private String rentalStatus;
}
