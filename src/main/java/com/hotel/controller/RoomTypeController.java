package com.hotel.controller;

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

import com.hotel.entity.RoomType;
import com.hotel.service.RoomTypeService;

@Controller
@RequestMapping("/roomtype")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Integer.class, new CustomNumberEditor(Integer.class, true));
    }

    @GetMapping("/crud")
    public String showCrudPage(Model model) {
        model.addAttribute("roomTypes", roomTypeService.findAll());
        return "roombooking/roomtype";
    }

    @PostMapping("/save")
    public String saveRoomType(@ModelAttribute RoomType roomType, RedirectAttributes redirectAttributes) {
        try {
            if (roomType.getRoomTypeId() != null) {
                roomTypeService.update(roomType.getRoomTypeId(), roomType);
                redirectAttributes.addFlashAttribute("successMsg", "房型更新成功！");
            } else {
                roomTypeService.insert(roomType);
                redirectAttributes.addFlashAttribute("successMsg", "房型新增成功！");
            }
        } catch (Exception e) {
            // 擷取外鍵約束或其他 SQL 錯誤並提示使用者
            String errorMsg = e.getMessage();
            if (errorMsg != null && errorMsg.contains("FK_room_type_image")) {
                redirectAttributes.addFlashAttribute("errorMsg", "【圖片 ID】填寫不正確：資料庫中找不到此圖片 ID！");
            } else {
                redirectAttributes.addFlashAttribute("errorMsg", "儲存失敗，請檢查欄位格式是否正確！");
            }
        }
        return "redirect:/roomtype/crud";
    }

    @GetMapping("/delete/{id}")
    public String deleteRoomType(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            roomTypeService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMsg", "房型刪除成功！");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMsg", "刪除失敗：該房型可能已被其他資料關聯！");
        }
        return "redirect:/roomtype/crud";
    }
}
