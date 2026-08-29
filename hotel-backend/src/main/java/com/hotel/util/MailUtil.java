package com.hotel.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.hotel.model.dto.EmailDTO;

import java.io.File;

@Component
public class MailUtil {

    private static final Logger log = LoggerFactory.getLogger(MailUtil.class);

    private final JavaMailSender mailSender;

    // 自動讀取 application.properties 裡的發信人設定
    @Value("${spring.mail.username}")
    private String fromEmail;

    MailUtil(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * 萬用非同步發信方法
     * 支援純文字、HTML、以及多個附件
     */
    @Async("mailTaskExecutor") // 指定使用自訂的執行緒池，若無設定可直接寫 @Async
    public void sendEmail(EmailDTO emailDto) {
        log.info("開始背景發送郵件，目標: {}, 主旨: {}", emailDto.getTo(), emailDto.getSubject());

        try {
            MimeMessage message = mailSender.createMimeMessage();

            // 如果有附件，第二個參數必須為 true
            boolean hasAttachments = emailDto.getAttachments() != null && !emailDto.getAttachments().isEmpty();
            MimeMessageHelper helper = new MimeMessageHelper(message, hasAttachments, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(emailDto.getTo());
            helper.setSubject(emailDto.getSubject());
            helper.setText(emailDto.getContent(), emailDto.isHtml());

            // 處理附件 (若有的話)
            if (hasAttachments) {
                for (File file : emailDto.getAttachments()) {
                    helper.addAttachment(file.getName(), file);
                }
            }

            mailSender.send(message);
            log.info("郵件發送成功！目標: {}", emailDto.getTo());

        } catch (MessagingException e) {
            log.error("郵件發送失敗！錯誤原因: ", e);
            // 這裡可以選擇將發送失敗的紀錄寫入 Log 或做進一步通知
        }
    }
}
