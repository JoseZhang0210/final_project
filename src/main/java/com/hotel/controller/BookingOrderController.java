package com.hotel.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hotel.entity.Booking;
import com.hotel.entity.BookingOrder;
import com.hotel.service.BookingOrderService;

@Controller
@RequestMapping("/bookingorder")
public class BookingOrderController {

    private final BookingOrderService bookingOrderService;

    public BookingOrderController(BookingOrderService bookingOrderService) {
        this.bookingOrderService = bookingOrderService;
    }

    // 自動處理空白數字轉 null 以及日期格式解析
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, true));
    }

    // 1. 顯示管理頁面
    @GetMapping("/crud")
    public String showCrudPage(Model model) {
        model.addAttribute("orders", bookingOrderService.findAll());
        return "roombooking/roomtypeCRUD";
    }

    // 2. 處理新增 / 更新
    @PostMapping("/save")
    public String saveBookingOrder(@ModelAttribute BookingOrder bookingOrder, RedirectAttributes redirectAttributes) {
        try {
            // 建立雙向關聯
            if (bookingOrder.getBookings() != null) {
                for (Booking booking : bookingOrder.getBookings()) {
                    booking.setBookingOrder(bookingOrder);
                }
            }

            if (bookingOrder.getBookingOrderId() != null && bookingOrder.getBookingOrderId() > 0) {
                bookingOrderService.update(bookingOrder.getBookingOrderId(), bookingOrder);
                redirectAttributes.addFlashAttribute("successMsg", "訂單更新成功！");
            } else {
                if (bookingOrder.getCreatedAt() == null) {
                    bookingOrder.setCreatedAt(new Date());
                }
                bookingOrderService.insert(bookingOrder);
                redirectAttributes.addFlashAttribute("successMsg", "訂單新增成功！");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "儲存失敗：" + e.getMessage());
        }
        return "redirect:/bookingorder/crud";
    }

    // 3. 刪除訂單
    @GetMapping("/delete/{id}")
    public String deleteBookingOrder(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            bookingOrderService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMsg", "訂單刪除成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "刪除失敗：" + e.getMessage());
        }
        return "redirect:/bookingorder/crud";
    }
}