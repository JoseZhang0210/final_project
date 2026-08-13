package com.hotel.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RestaurantPageController {

    @GetMapping("/restaurants")
    public String showRestaurantPage() {
        return "restaurants/list";
    }

    @GetMapping("/restaurant-times")
    public String showRestaurantTimePage() {
        return "restaurant-times/list";
    }

    @GetMapping("/reservation-manage")
    public String showReservationPage() {
        return "reservations/list";
    }

    @GetMapping("/restaurant-menu")
    public String showRestaurantMenu() {
        return "restaurants/menu";
    }
}