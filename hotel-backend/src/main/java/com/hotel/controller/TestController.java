package com.hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;
import com.hotel.scheduler.HotelScheduler;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private HotelScheduler hotelScheduler;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/cleanup-images")
    public String cleanupImages() {
        jdbcTemplate.execute("DELETE FROM room_image WHERE image_description = 'Batch imported main image'");
        return "Cleaned up old test images successfully!";
    }

    @GetMapping("/fix")
    public String fix() {
        hotelScheduler.autoAdvanceBookingStates();
        return "OK - Fixed";
    }
}
