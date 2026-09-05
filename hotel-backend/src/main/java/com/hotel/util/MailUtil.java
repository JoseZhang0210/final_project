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

    /**
     * 發送會員註冊信箱驗證碼
     *
     * @param toEmail 收件人電子郵箱
     * @param code    6 位數驗證碼
     */
    public void sendVerificationCode(String toEmail, String code) {
        String subject = "【星澄飯店】會員註冊信箱驗證碼";
        String htmlContent = "<!DOCTYPE html>"
                + "<html>"
                + "<head><meta charset='UTF-8'></head>"
                + "<body style='margin: 0; padding: 0; background-color: #f4f1ea; font-family: \"Microsoft JhengHei\", Arial, sans-serif; color: #333333;'>"
                + "  <table role='presentation' width='100%' cellspacing='0' cellpadding='0' style='background-color: #f4f1ea; padding: 30px 10px;'>"
                + "    <tr>"
                + "      <td align='center'>"
                + "        <table role='presentation' width='100%' style='max-width: 560px; background-color: #ffffff; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.08);' cellspacing='0' cellpadding='0'>"
                + "          <!-- Header -->"
                + "          <tr>"
                + "            <td style='background: linear-gradient(135deg, #2b2219 0%, #4a3b2a 100%); padding: 32px 24px; text-align: center;'>"
                + "              <h1 style='margin: 0; color: #d4af37; font-size: 26px; letter-spacing: 3px; font-weight: bold;'>星澄飯店</h1>"
                + "              <p style='margin: 6px 0 0 0; color: #eae5dc; font-size: 13px; letter-spacing: 1px;'>Grand Aster Hotel & Resorts</p>"
                + "            </td>"
                + "          </tr>"
                + "          <!-- Body -->"
                + "          <tr>"
                + "            <td style='padding: 36px 32px;'>"
                + "              <h2 style='margin-top: 0; color: #4a3b2a; font-size: 20px; font-weight: bold; border-bottom: 2px solid #e8dfd3; padding-bottom: 12px;'>會員註冊信箱驗證</h2>"
                + "              <p style='font-size: 15px; line-height: 1.7; color: #555555; margin: 18px 0 10px 0;'>您好，</p>"
                + "              <p style='font-size: 15px; line-height: 1.7; color: #555555; margin: 0 0 24px 0;'>感謝您註冊星澄飯店會員！請使用下方 6 位數驗證碼以完成信箱驗證與帳號啟用：</p>"
                + "              <div style='text-align: center; margin: 30px 0;'>"
                + "                <div style='display: inline-block; background-color: #faf7f2; border: 2px dashed #b58a46; border-radius: 10px; padding: 16px 36px;'>"
                + "                  <span style='font-size: 34px; font-weight: bold; letter-spacing: 8px; color: #9b7435; font-family: Consolas, Monaco, monospace;'>" + code + "</span>"
                + "                </div>"
                + "              </div>"
                + "              <p style='font-size: 14px; line-height: 1.6; color: #888888; text-align: center; margin: 16px 0 28px 0;'>"
                + "                ⏰ 驗證碼有效期限為 <strong>5 分鐘</strong>，請盡速完成輸入。"
                + "              </p>"
                + "              <hr style='border: none; border-top: 1px solid #eeeeee; margin: 24px 0;' />"
                + "              <p style='font-size: 12px; color: #999999; line-height: 1.6; margin: 0;'>"
                + "                ※ 此為系統自動發送信件，請勿直接回覆。<br/>"
                + "                ※ 若您未曾申請星澄飯店會員帳號，請忽略此郵件，您的信箱安全無虞。"
                + "              </p>"
                + "            </td>"
                + "          </tr>"
                + "          <!-- Footer -->"
                + "          <tr>"
                + "            <td style='background-color: #fbf9f6; padding: 18px 24px; text-align: center; border-top: 1px solid #f0ebe1;'>"
                + "              <p style='margin: 0; font-size: 12px; color: #8c8275;'>&copy; 2026 星澄飯店 Grand Aster Hotel. All rights reserved.</p>"
                + "            </td>"
                + "          </tr>"
                + "        </table>"
                + "      </td>"
                + "    </tr>"
                + "  </table>"
                + "</body>"
                + "</html>";

        EmailDTO emailDto = new EmailDTO(toEmail, subject, htmlContent, true);
        sendEmail(emailDto);
    }
}
