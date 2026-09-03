package com.hotel.controller;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
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

import com.hotel.model.dto.MemberDTO;
import com.hotel.service.MemberService;

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
}

