package com.hotel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.hotel.entity.Rental;
import com.hotel.service.RentalService;
import com.hotel.service.VenueService;

/**
 * 場地租借 Controller。
 */
@Controller
public class RentalController {

    private final RentalService rentalService;
    private final VenueService venueService;

    public RentalController(
            RentalService rentalService,
            VenueService venueService) {

        this.rentalService = rentalService;
        this.venueService = venueService;
    }

    /**
     * 顯示全部租借紀錄。
     */
    @GetMapping("/rentals")
    public String list(Model model) {
        model.addAttribute("rentals", rentalService.findAll());
        return "rentals/list";
    }

    /**
     * 顯示新增租借表單。
     */
    @GetMapping("/rentals/add")
    public String addForm(Model model) {

        Rental rental = new Rental();
        rental.setRentalStatus("PENDING");

        model.addAttribute("rental", rental);

        // 提供場地下拉選單。
        model.addAttribute("venues", venueService.findAll());

        return "rentals/add";
    }

    /**
     * 儲存新增租借。
     */
    @PostMapping("/rentals/save")
    public String save(
            @ModelAttribute Rental rental,
            Model model) {

        try {
            rentalService.create(rental);
            return "redirect:/rentals";

        } catch (IllegalArgumentException e) {

            // 驗證失敗時回到新增頁並顯示錯誤訊息。
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("rental", rental);
            model.addAttribute("venues", venueService.findAll());

            return "rentals/add";
        }
    }

    /**
     * 顯示修改租借表單。
     */
    @GetMapping("/rentals/edit/{id}")
    public String editForm(
            @PathVariable Integer id,
            Model model) {

        Rental rental = rentalService.findById(id).orElse(null);

        if (rental == null) {
            return "redirect:/rentals";
        }

        model.addAttribute("rental", rental);
        model.addAttribute("venues", venueService.findAll());

        return "rentals/edit";
    }

    /**
     * 儲存修改後的租借資料。
     */
    @PostMapping("/rentals/update")
    public String update(
            @ModelAttribute Rental rental,
            Model model) {

        try {
            rentalService.update(rental);
            return "redirect:/rentals";

        } catch (IllegalArgumentException e) {

            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("rental", rental);
            model.addAttribute("venues", venueService.findAll());

            return "rentals/edit";
        }
    }

    /**
     * 刪除租借紀錄。
     */
    @GetMapping("/rentals/delete/{id}")
    public String delete(@PathVariable Integer id) {
        rentalService.deleteById(id);
        return "redirect:/rentals";
    }
}
