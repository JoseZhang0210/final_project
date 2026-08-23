package com.hotel.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/api/test")
public class TestController {
@GetMapping("/jwt")
public ResponseEntity<String> getJwt() {
    return ResponseEntity.ok("恭喜！您成功攜帶了合法的 JWT!");
}
}
