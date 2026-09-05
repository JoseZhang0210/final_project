/**
 * 測試用 API 控制器
 * 提供各種後端功能的測試端點
 */
package com.hotel.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.jdbc.core.JdbcTemplate;
import com.hotel.scheduler.HotelScheduler;

import com.hotel.model.dto.EmailDTO;
import com.hotel.util.MailUtil;

import java.io.File;
import java.util.Collections;

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

    private final MailUtil mailUtil;

    // 使用構造器注入
    public TestController(MailUtil mailUtil) {
        this.mailUtil = mailUtil;
    }

    /**
     * 測試 JWT 驗證
     * 
     * @return 成功回應
     */
    @GetMapping("/jwt")
    public ResponseEntity<String> getJwt() {
        return ResponseEntity.ok("恭喜！您成功攜帶了合法的 JWT!");
    }

    /**
     * 測試发送邮件功能
     * 
     * @param to      收件人電子郵件
     * @param subject 郵件主旨
     * @param content 郵件內容
     * @return 發送結果
     */
    @GetMapping("/email")
    public ResponseEntity<String> sendTestEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String content) {

        // 建立 EmailDTO 物件
        EmailDTO emailDto = new EmailDTO(to, subject, content, true);

        // 非同步發送郵件
        mailUtil.sendEmail(emailDto);

        return ResponseEntity.ok("郵件已送入發送隊列，目標: " + to);
    }

    /**
     * 測試發送純文字郵件
     * 
     * @param to      收件人電子郵件
     * @param subject 郵件主旨
     * @param content 郵件純文字內容
     * @return 發送結果
     */
    @GetMapping("/email/text")
    public ResponseEntity<String> sendTextEmail(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String content) {

        // 建立純文字 EmailDTO 物件
        EmailDTO emailDto = new EmailDTO(to, subject, content, false);

        // 非同步發送郵件
        mailUtil.sendEmail(emailDto);

        return ResponseEntity.ok("純文字郵件已送入發送隊列，目標: " + to);
    }

    /**
     * 測試發送帶附件的郵件
     * 
     * @param to       收件人電子郵件
     * @param subject  郵件主旨
     * @param content  郵件內容
     * @param filePath 附件檔案路徑
     * @return 發送結果
     */
    @GetMapping("/email/attachment")
    public ResponseEntity<String> sendEmailWithAttachment(
            @RequestParam String to,
            @RequestParam String subject,
            @RequestParam String content,
            @RequestParam String filePath) {

        try {
            // 建立附件檔案物件
            File attachment = new File(filePath);

            if (!attachment.exists()) {
                return ResponseEntity.badRequest().body("附件檔案不存在: " + filePath);
            }

            // 建立帶附件的 EmailDTO 物件
            EmailDTO emailDto = new EmailDTO(to, subject, content, true);
            emailDto.setAttachments(Collections.singletonList(attachment));

            // 非同步發送郵件
            mailUtil.sendEmail(emailDto);

            return ResponseEntity.ok("帶附件的郵件已送入發送隊列，目標: " + to);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("發送郵件時發生錯誤: " + e.getMessage());
        }
    }
}
