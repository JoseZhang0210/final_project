package com.hotel.controller;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import com.hotel.model.dto.RoomTypeDTO;
import com.hotel.service.RoomTypeService;
import com.hotel.util.JsonUtils;

@RestController
@RequestMapping("/api/roomtypes")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @GetMapping
    public ResponseEntity<List<RoomTypeDTO>> getAllRoomTypes() {
        return ResponseEntity.ok(roomTypeService.findAll());
    }

    // 前台訂房專用：依日期區間查詢各房型剩餘可用數量
    @GetMapping("/available")
    public ResponseEntity<List<RoomTypeDTO>> getAvailableRoomTypes(
            @RequestParam String checkIn,
            @RequestParam String checkOut) {
        LocalDate checkInDate = LocalDate.parse(checkIn);
        LocalDate checkOutDate = LocalDate.parse(checkOut);
        return ResponseEntity.ok(roomTypeService.findAllWithAvailability(checkInDate, checkOutDate));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomTypeDTO> getRoomTypeById(@PathVariable Integer id) {
        return roomTypeService.findOptionalById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new jakarta.persistence.EntityNotFoundException("找不到 ID 為 " + id + " 的房型資料"));
    }

    @PostMapping
    public ResponseEntity<RoomTypeDTO> createRoomType(@RequestBody RoomTypeDTO roomTypeDTO) {
        roomTypeDTO.setRoomTypeId(null);
        return ResponseEntity.status(HttpStatus.CREATED).body(roomTypeService.insert(roomTypeDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomTypeDTO> updateRoomType(@PathVariable Integer id, @RequestBody RoomTypeDTO roomTypeDTO) {
        return ResponseEntity.ok(roomTypeService.update(id, roomTypeDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteRoomType(@PathVariable Integer id) {
        roomTypeService.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "房型刪除成功！"));
    }

    // =========================================
    // JSON 匯出 API
    // GET /api/roomtypes/export/json
    // =========================================
    @GetMapping("/export/json")
    public ResponseEntity<byte[]> exportRoomTypesToJson() {
        List<RoomTypeDTO> roomTypes = roomTypeService.findAll();
        
        // 匯出時過濾掉不需要的欄位 (如 todayAvailableRooms 等) 以保持乾淨
        // JsonUtils.toPrettyJson() 會處理序列化
        String json = JsonUtils.toPrettyJson(roomTypes);
        byte[] jsonBytes = (json != null ? json : "[]").getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setContentDisposition(
                ContentDisposition.attachment().filename("room_types.json", StandardCharsets.UTF_8).build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(jsonBytes);
    }

    // =========================================
    // JSON 匯入 API (支援字串內容)
    // POST /api/roomtypes/import/json
    // =========================================
    @PostMapping(value = {"/import", "/import/json"}, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<?> importRoomTypesFromJson(@RequestBody String json) {
        return processImport(json);
    }

    // =========================================
    // JSON 匯入 API (支援檔案上傳)
    // POST /api/roomtypes/import/json/file
    // =========================================
    @PostMapping(value = "/import/json/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importRoomTypesFromJsonFile(@RequestParam("file") MultipartFile file) {
        try {
            String json = new String(file.getBytes(), StandardCharsets.UTF_8);
            return processImport(json);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "讀取檔案失敗：" + e.getMessage()));
        }
    }

    private ResponseEntity<?> processImport(String json) {
        if (json == null || json.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "匯入的 JSON 內容不可為空"));
        }

        try {
            List<RoomTypeDTO> roomTypeList;
            
            // 判斷是單筆物件還是陣列
            if (json.trim().startsWith("[")) {
                roomTypeList = JsonUtils.toList(json, RoomTypeDTO.class);
            } else {
                RoomTypeDTO single = JsonUtils.fromJson(json, RoomTypeDTO.class);
                roomTypeList = (single != null) ? List.of(single) : List.of();
            }

            if (roomTypeList.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("message", "JSON 格式錯誤或無效資料"));
            }

            Map<String, Object> result = roomTypeService.importRoomTypes(roomTypeList);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "解析 JSON 失敗：" + e.getMessage()));
        }
    }
}