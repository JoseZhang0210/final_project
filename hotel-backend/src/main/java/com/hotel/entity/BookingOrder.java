package com.hotel.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "booking_order")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingOrder {

    @Id
    @Column(name = "booking_order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer bookingOrderId;

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "booking_total_price")
    private Integer bookingTotalPrice;

    @Column(name = "order_status")
    private String orderStatus; // PENDING, PAID, EXPIRED, CANCELLED

    // updatable = false 確保後續更新訂單狀態時，不會覆蓋原有的鎖房起算時間
    @Column(name = "created_at", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")    
    private LocalDateTime createdAt;

    @Column(name = "payment_id", nullable = true)
    private Integer paymentId;

    @OneToMany(mappedBy = "bookingOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnoreProperties("bookingOrder") // 防止 JSON 序列化時產生循環引用
    private List<Booking> bookings = new ArrayList<>();

    public void addBooking(Booking booking) {
        bookings.add(booking);
        booking.setBookingOrder(this);
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
        booking.setBookingOrder(null);
    }
}