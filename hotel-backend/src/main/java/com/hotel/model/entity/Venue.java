package com.hotel.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 場地資料表 Entity。
 *
 * 對應資料庫中的 venue 資料表。
 * 此類別只負責「場地本身」的基本資料，
 * 不負責會員、付款或租借流程。
 */
@Entity
@Table(name = "venue")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Venue {

    /**
     * 場地編號，Primary Key。
     *
     * 注意：
     * 目前組長的 SQL 設計中 venue_id 不是 IDENTITY，
     * 所以這裡沒有使用 @GeneratedValue。
     * 新增場地時需要自行輸入 venueId。
     */
    @Id
    @Column(name = "venue_id")
    private Integer venueId;

    /**
     * 場地名稱。
     * 例如：國際宴會廳、會議室 A、戶外花園。
     */
    @Column(name = "venue_name", nullable = false, length = 50)
    private String venueName;

    /**
     * 場地最多可容納的人數。
     */
    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    /**
     * 每日租借價格。
     * 目前資料庫型態為 INTEGER。
     */
    @Column(name = "price_per_day", nullable = false)
    private Integer pricePerDay;

    /**
     * 場地本身的狀態。
     *
     * 建議值：
     * AVAILABLE   = 可租借
     * MAINTENANCE = 維修中
     * DISABLED    = 停止使用
     */
    @Column(name = "venue_status", nullable = false, length = 50)
    private String venueStatus;
}
