package com.hotel.model.entity;

// Java 日期時間。
import java.time.LocalDateTime;

// JPA Entity。
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

// Lombok。
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 場地租借付款資料 Entity。
 *
 * 提供 Hibernate 建立 rental_payment
 * 資料表所需要的結構資訊。
 *
 * 現有付款流程仍使用 JdbcTemplate，
 * 不改動 RentalPaymentRepository。
 */
@Entity
@Table(
    name = "rental_payment",
    schema = "dbo"
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalPayment {

    /**
     * 場地付款編號。
     *
     * 由 SQL Server IDENTITY 自動產生。
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Integer paymentId;


    /**
     * 付款方式。
     */
    @Column(
        name = "payment_method",
        length = 50
    )
    private String paymentMethod;


    /**
     * 實際付款時間。
     */
    @Column(name = "payment_time")
    private LocalDateTime paymentTime;


    /**
     * 場地租借付款金額。
     */
    @Column(
        name = "total_price",
        nullable = false
    )
    private Integer totalPrice;


    /**
     * 付款狀態。
     */
    @Column(
        name = "payment_status",
        nullable = false,
        length = 20
    )
    private String paymentStatus;


    /**
     * 此筆付款所屬會員 ID。
     *
     * 不建立跨模組 JPA 關聯。
     */
    @Column(name = "member_id")
    private Integer memberId;


    /**
     * 綠界 MerchantTradeNo。
     */
    @Column(
        name = "merchant_trade_no",
        length = 20
    )
    private String merchantTradeNo;


    /**
     * 綠界回傳交易編號。
     */
    @Column(
        name = "ecpay_trade_no",
        length = 20
    )
    private String ecpayTradeNo;
}