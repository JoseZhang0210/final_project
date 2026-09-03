package com.hotel.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.dto.CreateOrderRequest;
import com.hotel.model.dto.MonthlyOrderStatisticsDTO;
import com.hotel.model.dto.MonthlyProductSalesDTO;
import com.hotel.model.dto.OrderDTO;
import com.hotel.model.entity.CustomerOrder;
import com.hotel.service.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderApiController {

        private final OrderService orderService;

        public OrderApiController(
                        OrderService orderService) {

                this.orderService = orderService;
        }

        // =====================================================
        // 1. 查詢全部訂單
        //
        // GET /api/orders
        //
        // 後台訂單管理使用
        // =====================================================

        @GetMapping
        public ResponseEntity<List<OrderDTO>> getOrders() {

                List<OrderDTO> orders = orderService.getAllOrderDTOs();

                return ResponseEntity.ok(orders);
        }

        // =====================================================
        // 2. 查詢單一訂單
        //
        // GET /api/orders/1
        //
        // PaymentView 付款頁使用
        // =====================================================

        @GetMapping("/{id}")
        public ResponseEntity<OrderDTO> getOrderById(
                        @PathVariable Integer id) {

                OrderDTO order = orderService.getOrderDTOById(id);

                if (order == null) {
                        return ResponseEntity
                                        .notFound()
                                        .build();
                }

                return ResponseEntity.ok(order);
        }

        // =====================================================
        // 3. 建立訂單
        //
        // POST /api/orders
        //
        // 購物車按下「確認結帳」時使用
        //
        // Request：
        //
        // {
        // "memberId": 1,
        // "couponCode": null,
        // "items": [
        // {
        // "productId": 1,
        // "quantity": 2
        // }
        // ]
        // }
        //
        // 建立完成：
        //
        // order_status = PENDING
        // =====================================================

        @PostMapping
        public ResponseEntity<CustomerOrder> createOrder(
                        @RequestBody CreateOrderRequest request) {

                CustomerOrder order = orderService.createOrder(
                                request.getMemberId(),
                                request.getCouponCode(),
                                request.getItems());

                return ResponseEntity.ok(order);
        }

        // =====================================================
        // 4. 查詢會員自己的訂單
        //
        // GET /api/orders/member/1
        //
        // MyOrdersView 使用
        // =====================================================

        @GetMapping("/member/{memberId}")
        public ResponseEntity<List<OrderDTO>> getMemberOrders(
                        @PathVariable Integer memberId) {

                List<OrderDTO> orders = orderService
                                .getOrdersByMemberId(
                                                memberId);

                return ResponseEntity.ok(orders);
        }

        // =====================================================
        // 5. 後台修改訂單狀態
        //
        // PUT /api/orders/1/status?status=COMPLETED
        //
        // 合法狀態：
        // PENDING
        // COMPLETED
        // CANCELLED
        // =====================================================

        @PutMapping("/{id}/status")
        public ResponseEntity<Void> updateStatus(
                        @PathVariable Integer id,
                        @RequestParam String status) {

                orderService.updateOrderStatus(
                                id,
                                status);

                return ResponseEntity
                                .noContent()
                                .build();
        }

        // =====================================================
        // 6. 取消訂單
        //
        // PUT /api/orders/1/cancel
        //
        // PENDING 訂單取消後：
        //
        // order_status = CANCELLED
        //
        // 並補回商品庫存
        // =====================================================

        @PutMapping("/{id}/cancel")
        public ResponseEntity<Void> cancelOrder(
                        @PathVariable Integer id) {

                orderService.cancelOrder(id);

                return ResponseEntity
                                .noContent()
                                .build();
        }

        // =====================================================
        // 7. 修改訂單商品數量
        //
        // PUT /api/orders/1/items/2?quantity=3
        //
        // 原則：
        // 只有 PENDING 訂單可以修改
        // =====================================================

        @PutMapping("/{orderId}/items/{productId}")
        public ResponseEntity<Void> updateOrderItemQuantity(
                        @PathVariable Integer orderId,
                        @PathVariable Integer productId,
                        @RequestParam Integer quantity) {

                orderService.updateOrderItemQuantity(
                                orderId,
                                productId,
                                quantity);

                return ResponseEntity
                                .noContent()
                                .build();
        }

        // =====================================================
        // 8. 刪除訂單中的商品
        //
        // DELETE /api/orders/1/items/2
        //
        // 原則：
        // 只有 PENDING 訂單可以修改
        // =====================================================

        @DeleteMapping("/{orderId}/items/{productId}")
        public ResponseEntity<Void> deleteOrderItem(
                        @PathVariable Integer orderId,
                        @PathVariable Integer productId) {

                orderService.deleteOrderItem(
                                orderId,
                                productId);

                return ResponseEntity
                                .noContent()
                                .build();
        }

        // 9. 每月圖表統計
        @GetMapping("/statistics/monthly")
        public ResponseEntity<List<MonthlyOrderStatisticsDTO>> getMonthlyOrderStatistics() {

                return ResponseEntity.ok(
                                orderService
                                                .getMonthlyOrderStatistics());

        }
        // =====================================================
        // 10. 查詢指定月份商品銷售統計
        //
        // GET
        // /api/orders/statistics/products?year=2026&month=9
        // =====================================================

        @GetMapping("/statistics/products")
        public ResponseEntity<List<MonthlyProductSalesDTO>> getMonthlyProductSales(
                        @RequestParam Integer year,
                        @RequestParam Integer month) {

                List<MonthlyProductSalesDTO> result = orderService
                                .getMonthlyProductSales(
                                                year,
                                                month);

                return ResponseEntity.ok(
                                result);
        }
}