package com.hotel.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {

        return "index";

    }

    @GetMapping("/register")
    public String register() {

        return "register";

    }
    // ********************************

    // @GetMapping("/hotelhome")
    // public String hotelhome() {

    // return "roombooking/hotelhome";

    // }

    // @GetMapping("/booking")
    // public String booking() {

    // return "roombooking/serenestay";

    // }

    // @GetMapping("/roomlist")
    // public String roomlist() {

    // return "roombooking/roomlist";

    // }

    // @GetMapping("/bookingcheck")
    // public String bookingcheck() {

    // return "roombooking/bookingcheck";

    // }

    // @GetMapping("/bookingorder")
    // public String bookingorder() {

    // return "roombooking/bookingorder";

    // }

    // @GetMapping("/roomtype")
    // public String roomtype() {
    // return "forward:/roomtype.html"; // 轉發到 static/roomtype.html
    // }
    // ********************************************

}
