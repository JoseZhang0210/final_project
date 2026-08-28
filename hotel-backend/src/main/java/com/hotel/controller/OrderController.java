package com.hotel.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.model.dto.CreateOrderRequest;
import com.hotel.model.entity.CustomerOrder;
import com.hotel.model.entity.OrderItem;
import com.hotel.service.OrderService;

@Controller
public class OrderController {

        private final OrderService orderService;

        public OrderController(OrderService orderService) {
                this.orderService = orderService;
        }

        // 顯示全部訂單
        @GetMapping("/orders")
        public String showOrders(Model model) {

                List<CustomerOrder> orders = orderService.getAllOrders();

                Map<Integer, List<OrderItem>> orderItemsMap = new HashMap<>();

                for (CustomerOrder order : orders) {

                        List<OrderItem> items = orderService.getOrderItems(
                                        order.getOrderId());

                        orderItemsMap.put(
                                        order.getOrderId(),
                                        items);
                }

                model.addAttribute(
                                "orders",
                                orders);

                model.addAttribute(
                                "orderItemsMap",
                                orderItemsMap);

                return "orders";
        }

        @PostMapping("/orders/clear")
        public String cleanOrders() {
                orderService.clearAllOrders();

                return "redirect:/orders";
        }

        // 商品管理頁按「購買」
        @PostMapping("/orders/add")
        public CustomerOrder createOrder(
                        @RequestBody CreateOrderRequest request) {

                return orderService.createOrder(
                                request.getMemberId(),
                                request.getItems());
        }

        // 修改訂單商品數量
        @PostMapping("/orders/item/update")
        public String updateOrderItem(
                        @RequestParam Integer orderItemId,
                        @RequestParam Integer quantity) {

                orderService.updateOrderItemQuantity(
                                orderItemId,
                                quantity, quantity);

                return "redirect:/orders";
        }

        // 刪除訂單中的商品
        @PostMapping("/orders/item/delete")
        public String deleteOrderItem(
                        @RequestParam Integer orderItemId) {

                orderService.deleteOrderItem(
                                orderItemId, orderItemId);

                return "redirect:/orders";
        }

        // 確定結帳
        @PostMapping("/orders/checkout/{id}")
        public String confirmCheckout(
                        @PathVariable Integer id) {

                orderService.confirmCheckout(id);

                return "redirect:/orders";
        }

        // 查看單筆訂單明細
        @GetMapping("/orders/{id}")
        public String showOrder(
                        @PathVariable Integer id,
                        Model model) {

                CustomerOrder order = orderService.getOrderById(id);

                List<OrderItem> items = orderService.getOrderItems(id);

                model.addAttribute(
                                "order",
                                order);

                model.addAttribute(
                                "items",
                                items);

                return "order-detail";
        }
}