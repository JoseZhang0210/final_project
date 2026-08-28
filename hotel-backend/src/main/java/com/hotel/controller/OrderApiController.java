package com.hotel.controller;

import java.util.List;

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

    // 查詢全部訂單
    @GetMapping
    public List<OrderDTO> getOrders() {

        return orderService
                .getAllOrderDTOs();
    }

    // 建立一張多商品訂單
    @PostMapping
    public CustomerOrder createOrder(
            @RequestBody CreateOrderRequest request) {

        return orderService.createOrder(
                request.getMemberId(),
                request.getItems());
    }

    // 管理員確認訂單
    @PutMapping("/{id}/confirm")
    public void confirmOrder(
            @PathVariable Integer id) {

        orderService.confirmOrder(id);
    }

    // 模擬已付款
    @PutMapping("/{id}/paid")
    public void markPaid(
            @PathVariable Integer id) {

        orderService.markOrderPaid(id);
    }

    // 出貨
    @PutMapping("/{id}/ship")
    public void shipOrder(
            @PathVariable Integer id) {

        orderService.shipOrder(id);
    }

    // 完成
    @PutMapping("/{id}/complete")
    public void completeOrder(
            @PathVariable Integer id) {

        orderService.completeOrder(id);
    }

    @PutMapping("/{id}/status")
    public void updateStatus(
            @PathVariable Integer id,
            @RequestParam String status) {

        orderService.updateOrderStatus(
                id,
                status);
    }

    @GetMapping("/member/{memberId}")
    public List<OrderDTO> getMemberOrders(
            @PathVariable Integer memberId) {

        return orderService
                .getOrdersByMemberId(memberId);
    }

    // 修改訂單商品數量
    @PutMapping("/{orderId}/items/{productId}")
    public void updateOrderItemQuantity(
            @PathVariable Integer orderId,
            @PathVariable Integer productId,
            @RequestParam Integer quantity) {

        orderService.updateOrderItemQuantity(
                orderId,
                productId,
                quantity);
    }

    // 刪除訂單商品
    @DeleteMapping("/{orderId}/items/{productId}")
    public void deleteOrderItem(
            @PathVariable Integer orderId,
            @PathVariable Integer productId) {

        orderService.deleteOrderItem(
                orderId,
                productId);
    }

}