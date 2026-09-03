package com.hotel.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.hotel.model.entity.CustomerOrder;

public interface CustomerOrderRepository
                extends JpaRepository<CustomerOrder, Integer> {

        List<CustomerOrder> findAllByOrderByOrderDateDesc();

        List<CustomerOrder> findByMemberIdOrderByOrderDateDesc(
                        Integer memberId);

        Optional<CustomerOrder> findByPaymentId(
                        Integer paymentId);

        @Query(value = """
                        SELECT
                            YEAR(order_date) AS order_year,
                            MONTH(order_date) AS order_month,
                            COUNT(*) AS order_count,
                            COALESCE(SUM(final_amount), 0) AS total_revenue
                        FROM dbo.[order]
                        WHERE order_status = 'COMPLETED'
                        GROUP BY
                            YEAR(order_date),
                            MONTH(order_date)
                        ORDER BY
                            YEAR(order_date),
                            MONTH(order_date)
                        """, nativeQuery = true)
        List<Object[]> findMonthlyOrderStatistics();

        @Query(value = """
                        SELECT
                            oi.product_id,
                            p.product_name,
                            SUM(oi.quantity) AS quantity_sold,
                            SUM(oi.subtotal) AS sales_amount
                        FROM dbo.[order] o
                        INNER JOIN dbo.order_item oi
                            ON o.order_id = oi.order_id
                        INNER JOIN dbo.product p
                            ON oi.product_id = p.product_id
                        WHERE o.order_status = 'COMPLETED'
                          AND YEAR(o.order_date) = :year
                          AND MONTH(o.order_date) = :month
                        GROUP BY
                            oi.product_id,
                            p.product_name
                        ORDER BY
                            quantity_sold DESC
                        """, nativeQuery = true)
        List<Object[]> findMonthlyProductSales(
                        @Param("year") Integer year,
                        @Param("month") Integer month);
}