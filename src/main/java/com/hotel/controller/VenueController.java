package com.hotel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.hotel.entity.Venue;
import com.hotel.service.VenueService;

/**
 * 場地管理 Controller。
 *
 * 網址：
 * GET  /venues              顯示全部
 * GET  /venues/add          新增畫面
 * POST /venues/save         儲存新增
 * GET  /venues/edit/{id}    修改畫面
 * POST /venues/update       儲存修改
 * GET  /venues/delete/{id}  刪除
 */
@Controller
public class VenueController {

    private final VenueService venueService;

    public VenueController(VenueService venueService) {
        this.venueService = venueService;
    }

    /**
     * 顯示全部場地。
     */
    @GetMapping("/venues")
    public String list(Model model) {
        model.addAttribute("venues", venueService.findAll());
        return "venues/list";
    }

    /**
     * 顯示新增場地表單。
     */
    @GetMapping("/venues/add")
    public String addForm(Model model) {

        Venue venue = new Venue();

        // 預設場地狀態為 AVAILABLE。
        venue.setVenueStatus("AVAILABLE");

        model.addAttribute("venue", venue);
        return "venues/add";
    }

    /**
     * 儲存新增場地。
     */
    @PostMapping("/venues/save")
    public String save(@ModelAttribute Venue venue) {
        venueService.save(venue);
        return "redirect:/venues";
    }

    /**
     * 顯示修改場地表單。
     */
    @GetMapping("/venues/edit/{id}")
    public String editForm(
            @PathVariable Integer id,
            Model model) {

        Venue venue = venueService.findById(id).orElse(null);

        if (venue == null) {
            return "redirect:/venues";
        }

        model.addAttribute("venue", venue);
        return "venues/edit";
    }

    /**
     * 儲存修改後的場地。
     */
    @PostMapping("/venues/update")
    public String update(@ModelAttribute Venue venue) {
        venueService.save(venue);
        return "redirect:/venues";
    }

    /**
     * 刪除場地。
     */
    @GetMapping("/venues/delete/{id}")
    public String delete(@PathVariable Integer id) {
        venueService.deleteById(id);
        return "redirect:/venues";
    }
}
