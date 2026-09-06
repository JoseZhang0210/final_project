package com.hotel.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hotel.model.dto.MemberDTO;
import com.hotel.service.MemberService;
import com.hotel.util.JsonUtils;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // =========================================
    // 1. 查詢會員列表（支援關鍵字與狀態篩選）
    // GET /api/members
    // 例如：GET /api/members
    //      GET /api/members?keyword=王小明
    //      GET /api/members?status=1
    // =========================================
    @GetMapping
    public ResponseEntity<List<MemberDTO>> findAllMembers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {

        List<MemberDTO> members = memberService.findAllMembers(keyword, status);
        return ResponseEntity.ok(members);
    }

    // =========================================
    // 2. 關鍵字搜尋會員 (別名端點)
    // GET /api/members/search
    // 例如：GET /api/members/search?keyword=小明
    // =========================================
    @GetMapping("/search")
    public ResponseEntity<List<MemberDTO>> searchMembers(@RequestParam String keyword) {
        List<MemberDTO> members = memberService.findAllMembers(keyword, null);
        return ResponseEntity.ok(members);
    }

    // =========================================
    // 3. 會員讀取自己的個人資料（使用 JWT 識別）
    // GET /api/members/me
    // =========================================
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(Authentication authentication) {
        MemberDTO dto = memberService.findByUsername(authentication.getName());
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    // =========================================
    // 4. 會員更新自己的個人資料（使用 JWT 識別）
    // PUT /api/members/me
    // =========================================
    @PutMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            @RequestBody MemberDTO memberDTO,
            Authentication authentication) {

        MemberDTO updated = memberService.updateMemberByUsername(authentication.getName(), memberDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // =========================================
    // 5. 依 ID 查詢單一會員詳細資料
    // GET /api/members/{id}
    // 例如：GET /api/members/1
    // =========================================
    @GetMapping("/{id}")
    public ResponseEntity<?> findMemberById(@PathVariable Integer id) {
        MemberDTO member = memberService.findById(id);

        if (member == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(member);
    }

    // =========================================
    // 4. 後台新增會員
    // POST /api/members
    // =========================================
    @PostMapping
    public ResponseEntity<?> createMember(@RequestBody MemberDTO memberDTO) {
        try {
            MemberDTO createdMember = memberService.createMember(memberDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdMember);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "建立會員失敗：" + e.getMessage()));
        }
    }

    // =========================================
    // 5. 修改會員詳細資料
    // PUT /api/members/{id}
    // 例如：PUT /api/members/1
    // =========================================
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMember(
            @PathVariable Integer id,
            @RequestBody MemberDTO memberDTO) {

        MemberDTO updatedMember = memberService.updateMember(id, memberDTO);

        if (updatedMember == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedMember);
    }

    // =========================================
    // 6. 快速切換會員帳號狀態 (啟用 "1" / 停用 "0")
    // PATCH /api/members/{id}/status
    // 例如：PATCH /api/members/1/status?status=0
    // =========================================
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateMemberStatus(
            @PathVariable Integer id,
            @RequestParam String status) {

        MemberDTO updatedMember = memberService.updateMemberStatus(id, status);

        if (updatedMember == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(updatedMember);
    }

    // =========================================
    // 7. 刪除會員
    // DELETE /api/members/{id}
    // 例如：DELETE /api/members/1
    // =========================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMember(@PathVariable Integer id) {
        MemberDTO existingMember = memberService.findById(id);

        if (existingMember == null) {
            return ResponseEntity.notFound().build();
        }

        try {
            boolean deleted = memberService.deleteMember(id);

            if (!deleted) {
                return ResponseEntity.notFound().build();
            }

            /*
             * 204 No Content
             * 代表刪除成功，無回傳內容
             */
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            /*
             * 若該會員已有相關訂單、預訂或付款紀錄，回傳 409 Conflict
             */
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "無法刪除：該會員已有相關訂單、預訂或付款紀錄。建議將帳號狀態變更為停用。"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "刪除會員失敗：" + e.getMessage()));
        }
    }

    // =========================================
    // 8. JSON 匯出會員資料
    // GET /api/members/export
    // 支援 RequestParam 調整匯出資料範圍：
    // - keyword: 關鍵字搜尋篩選
    // - status: 帳號狀態篩選 ("1" 啟用 / "0" 停用)
    // - ids: 指定會員 ID 清單 (例如：ids=1&ids=2 或 ids=1,2,3)
    // - minId: 最小會員 ID
    // - maxId: 最大會員 ID
    // - limit: 匯出筆數限制
    // - offset: 匯出筆數偏移
    // - download: 是否以檔案附件形式下載 (預設 true)
    // =========================================
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportMembers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) List<Integer> ids,
            @RequestParam(required = false) Integer minId,
            @RequestParam(required = false) Integer maxId,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) Integer offset,
            @RequestParam(required = false, defaultValue = "true") boolean download) {

        List<MemberDTO> members = memberService.findAllMembers(keyword, status);

        // 依 ID 列表篩選範圍
        if (ids != null && !ids.isEmpty()) {
            members = members.stream()
                    .filter(m -> m.getMemberId() != null && ids.contains(m.getMemberId()))
                    .collect(Collectors.toList());
        }

        // 依 ID 區間篩選範圍
        if (minId != null) {
            members = members.stream()
                    .filter(m -> m.getMemberId() != null && m.getMemberId() >= minId)
                    .collect(Collectors.toList());
        }

        if (maxId != null) {
            members = members.stream()
                    .filter(m -> m.getMemberId() != null && m.getMemberId() <= maxId)
                    .collect(Collectors.toList());
        }

        // 依分頁/筆數調整範圍
        if (offset != null && offset > 0) {
            members = members.stream()
                    .skip(offset)
                    .collect(Collectors.toList());
        }

        if (limit != null && limit > 0) {
            members = members.stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        // 調用 JsonUtils 將會員列表序列化為美化格式 JSON 字串
        String json = JsonUtils.toPrettyJson(members);
        byte[] jsonBytes = (json != null ? json : "[]").getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (download) {
            headers.setContentDisposition(
                    ContentDisposition.attachment().filename("members.json", StandardCharsets.UTF_8).build());
        }

        return ResponseEntity.ok()
                .headers(headers)
                .body(jsonBytes);
    }

    // =========================================
    // 9. JSON 匯入會員 (支援 JSON 字串 / 請求主體)
    // POST /api/members/import
    // =========================================
    @PostMapping(value = {"/import", "/import/json"}, consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<?> importMembersFromJson(@RequestBody String json) {
        return processImport(json);
    }

    // =========================================
    // 10. JSON 匯入會員 (支援檔案上傳)
    // POST /api/members/import (multipart/form-data)
    // =========================================
    @PostMapping(value = {"/import", "/import/file"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importMembersFromFile(@RequestParam("file") MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "請選擇要匯入的 JSON 檔案"));
        }
        try {
            String json = new String(file.getBytes(), StandardCharsets.UTF_8);
            return processImport(json);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "讀取檔案失敗：" + e.getMessage()));
        }
    }

    // =========================================
    // 匯入處理共用邏輯（調用 JsonUtils 反序列化並寫入會員資料）
    // =========================================
    private ResponseEntity<?> processImport(String json) {
        if (json == null || json.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("message", "匯入的 JSON 內容不可為空"));
        }

        List<MemberDTO> memberList;
        try {
            // 調用 JsonUtils 反序列化為 MemberDTO 列表
            memberList = JsonUtils.toList(json, MemberDTO.class);
        } catch (Exception e) {
            // 若非陣列格式，嘗試解析為單一物件
            try {
                MemberDTO single = JsonUtils.fromJson(json, MemberDTO.class);
                if (single != null) {
                    memberList = List.of(single);
                } else {
                    return ResponseEntity.badRequest().body(Map.of("message", "JSON 解析失敗：內容為空或格式錯誤"));
                }
            } catch (Exception ex) {
                return ResponseEntity.badRequest().body(Map.of("message", "JSON 解析失敗：" + ex.getMessage()));
            }
        }

        if (memberList == null || memberList.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "未解析到任何會員資料"));
        }

        int successCount = 0;
        int failureCount = 0;
        List<String> errors = new ArrayList<>();

        for (MemberDTO dto : memberList) {
            if (dto == null) {
                continue;
            }
            if (dto.getUsername() == null || dto.getUsername().isBlank()) {
                failureCount++;
                errors.add("會員資料缺少帳號 (username)");
                continue;
            }

            try {
                String username = dto.getUsername().trim();
                MemberDTO existing = memberService.findByUsername(username);

                if (existing == null && dto.getMemberId() != null) {
                    existing = memberService.findById(dto.getMemberId());
                }

                if (existing != null) {
                    // 已存在則更新會員資料
                    memberService.updateMember(existing.getMemberId(), dto);
                } else {
                    // 不存在則新增會員
                    memberService.createMember(dto);
                }
                successCount++;
            } catch (Exception e) {
                failureCount++;
                errors.add("帳號 '" + dto.getUsername() + "' 匯入失敗：" + e.getMessage());
            }
        }

        return ResponseEntity.ok(Map.of(
                "message", "匯入處理完成",
                "total", memberList.size(),
                "successCount", successCount,
                "failureCount", failureCount,
                "errors", errors));
    }
}

