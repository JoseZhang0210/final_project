package com.hotel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hotel.model.dto.BookingDTO;
import com.hotel.service.BookingService;

/**
 * 訂房管理控制器 (Booking Controller)
 * 負責處理與訂房相關的所有 HTTP 請求 (CRUD 操作)
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    // 透過建構子注入 BookingService
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * 建立新的訂房紀錄
     * 
     * @param bookingDTO 包含訂房資訊的資料傳輸物件
     * @return 建立成功後的訂房紀錄與 HTTP 201 Created 狀態碼
     */
    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody BookingDTO bookingDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.insert(bookingDTO));
    }

    /**
     * 取得所有訂房紀錄
     * 
     * @return 包含所有訂房紀錄的列表與 HTTP 200 OK 狀態碼
     */
    @GetMapping
    public ResponseEntity<List<BookingDTO>> getAllBookings() {
        return ResponseEntity.ok(bookingService.findAll());
    }

    /**
     * 根據 ID 取得單筆訂房紀錄
     * 
     * @param id 欲查詢的訂房 ID
     * @return 查詢到的訂房紀錄。若找不到，則拋出 EntityNotFoundException 異常
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getById(@PathVariable Integer id) {
        return bookingService.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("找不到 ID 為 " + id + " 的預訂紀錄"));
    }

    /**
     * 搜尋訂房紀錄
     * // 利用 DTO 進行動態多欄位搜尋
     * 
     * @param criteria 包含搜尋條件的資料傳輸物件
     * @return 符合條件的訂房紀錄列表
     */
    @PostMapping("/search")
    public ResponseEntity<List<BookingDTO>> searchBookings(@RequestBody BookingDTO criteria) {
        return ResponseEntity.ok(bookingService.searchByCriteria(criteria));
    }

    /**
     * 更新指定 ID 的訂房紀錄
     * 
     * @param id 欲更新的訂房 ID
     * @param bookingDTO 包含更新資訊的資料傳輸物件
     * @return 更新後的訂房紀錄
     */
    @PutMapping("/{id}")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable Integer id, @RequestBody BookingDTO bookingDTO) {
        return ResponseEntity.ok(bookingService.updateBooking(id, bookingDTO));
    }

    /**
     * 刪除指定 ID 的訂房紀錄
     * 
     * @param id 欲刪除的訂房 ID
     * @return 刪除成功的回應訊息
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBooking(@PathVariable Integer id) {
        bookingService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "預訂已成功刪除！"));
    }
}