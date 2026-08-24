package com.hotel.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.hotel.model.entity.CustomerOrder;
import com.hotel.service.OrderService;
import com.hotel.model.entity.CartItem;
import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

    private final OrderService orderService;

    public CheckoutController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public String checkout(
            @RequestParam String customerName,
            @RequestParam String phone,
            @RequestParam(required = false) String roomNumber,
            @RequestParam(required = false) String note,
            HttpSession session) {

        @SuppressWarnings("unchecked")
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        CustomerOrder order = orderService.createOrder(
                customerName,
                phone,
                roomNumber,
                note,
                cart);

        /*
         * 購買成功後清空購物車
         */
        session.removeAttribute("cart");

        /*
         * 記錄剛建立的訂單編號
         */
        session.setAttribute(
                "lastOrderId",
                order.getOrderId());

        /*
         * 購買完成後直接前往訂單管理
         */
        return "redirect:/orders";
    }
}