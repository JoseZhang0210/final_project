package com.hotel.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        return "roombooking/bookingorder";
    }

    // 2. 處理新增 / 更新
    @PostMapping("/save")
    public String saveBookingOrder(@ModelAttribute BookingOrder bookingOrder, RedirectAttributes redirectAttributes) {
        try {
            // 1. 若為新增訂單，設定建立時間
            if (bookingOrder.getCreatedAt() == null) {
                bookingOrder.setCreatedAt(new Date());
            }

            // 2. 關鍵：手動維護 JPA 雙向關聯外鍵 (booking_order_id)
            if (bookingOrder.getBookings() != null) {
                // 過濾掉全空的明細，並將 parent 指向主表
                List<Booking> validBookings = bookingOrder.getBookings().stream()
                        .filter(b -> b.getCheckInDate() != null && b.getCheckOutDate() != null)
                        .peek(b -> b.setBookingOrder(bookingOrder))
                        .collect(Collectors.toList());

                bookingOrder.getBookings().clear();
                bookingOrder.getBookings().addAll(validBookings);
            }

            // 3. 根據有無 ID 判斷呼叫 insert 或 update
            if (bookingOrder.getBookingOrderId() == null) {
                bookingOrderService.insert(bookingOrder);
            } else {
                bookingOrderService.update(bookingOrder.getBookingOrderId(), bookingOrder);
            }

            redirectAttributes.addFlashAttribute("successMsg", "訂單儲存成功！");
        } catch (Exception e) {
            e.printStackTrace();
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
