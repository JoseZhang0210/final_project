package com.hotel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.hotel.entity.BookingOrder;
import com.hotel.service.BookingOrderService;

@Controller
public class BookingOrderController {

    private final BookingOrderService bookingOrderService;

    public BookingOrderController(BookingOrderService bookingOrderService) {
        this.bookingOrderService = bookingOrderService;
    }

    // 1. 顯示所有訂單列表
    @GetMapping("/booking-orders")
    public String showBookingOrders(Model model) {
        model.addAttribute(
                "bookingOrders",
                bookingOrderService.findAll());

        return "booking-orders/list";
    }

    // 2. 顯示新增訂單表單
    @GetMapping("/booking-orders/add")
    public String showAddForm(Model model) {
        BookingOrder bookingOrder = new BookingOrder();
        bookingOrder.setOrderStatus("PENDING"); // 預設狀態

        model.addAttribute("bookingOrder", bookingOrder);

        return "booking-orders/add";
    }

    // 3. 處理新增訂單表單提交
    @PostMapping("/booking-orders/save")
    public String saveBookingOrder(
            @ModelAttribute BookingOrder bookingOrder
    ) {
        bookingOrderService.insert(bookingOrder);
        return "redirect:/booking-orders";
    }

    // 4. 顯示編輯訂單表單
    @GetMapping("/booking-orders/edit/{id}")
    public String showEditForm(
            @PathVariable Integer id,
            Model model
    ) {
        BookingOrder bookingOrder = bookingOrderService.findById(id).orElse(null);

        if (bookingOrder == null) {
            return "redirect:/booking-orders";
        }

        model.addAttribute("bookingOrder", bookingOrder);

        return "booking-orders/edit";
    }

    // 5. 處理更新訂單表單提交
    @PostMapping("/booking-orders/update/{id}")
    public String updateBookingOrder(
            @PathVariable Integer id,
            @ModelAttribute BookingOrder formOrder
    ) {
        try {
            bookingOrderService.update(id, formOrder);
        } catch (RuntimeException e) {
            return "redirect:/booking-orders";
        }

        return "redirect:/booking-orders";
    }

    // 6. 刪除訂單
    @GetMapping("/booking-orders/delete/{id}")
    public String deleteBookingOrder(
            @PathVariable Integer id
    ) {
        bookingOrderService.deleteById(id);
        return "redirect:/booking-orders";
    }
}