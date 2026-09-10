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
import com.hotel.repository.MemberRepository;
import com.hotel.repository.ProfileRepository;
import com.hotel.model.entity.Profile;
import com.hotel.model.entity.OrderItem;
import com.hotel.model.entity.Product;
import com.hotel.repository.OrderItemRepository;
import com.hotel.repository.ProductRepository;
import java.util.List;

import java.io.File;

@Component
public class MailUtil {

        private static final Logger log = LoggerFactory.getLogger(MailUtil.class);

        private final JavaMailSender mailSender;
        private final ProfileRepository profileRepository;
        private final MemberRepository memberRepository;
        private final OrderItemRepository orderItemRepository;
        private final ProductRepository productRepository;

    // 自動讀取 application.properties 裡的發信人設定
    @Value("${spring.mail.username}")
    private String fromEmail;

        MailUtil(JavaMailSender mailSender, ProfileRepository profileRepository, MemberRepository memberRepository,
                        OrderItemRepository orderItemRepository, ProductRepository productRepository) {
                this.mailSender = mailSender;
                this.profileRepository = profileRepository;
                this.memberRepository = memberRepository;
                this.orderItemRepository = orderItemRepository;
                this.productRepository = productRepository;
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
         * 發送訂單確認信件 (非同步)。
         * 
         * @param orderId  訂單編號
         * @param memberId 會員編號
         */
        @Async
        public void sendOrderConfirmation(Integer orderId, Integer memberId) {
                // 1. 取得會員 Email
                String toEmail = profileRepository.findByAccountId(
                                memberRepository.findById(memberId)
                                                .orElseThrow(() -> new IllegalArgumentException("找不到會員"))
                                                .getAccountId())
                                .map(Profile::getEmail)
                                .orElseThrow(() -> new IllegalArgumentException("找不到會員的 Email"));
                // 2. 取得訂單金額 (若需要，可改為傳入 amount)
                // 這裡示範簡單取回金額，實務上可直接把 amount 當參數傳入
                // CustomerOrder order = orderRepository.findById(orderId)...
                // int amount = order.getFinalAmount();
                // 3. 組裝郵件主旨與內容
                String subject = "【星澄飯店】訂單確認 - 訂單編號 " + orderId;
                String htmlBody = buildOrderHtml(orderId, memberId);
                // 4. 建立 DTO 並發送
                EmailDTO dto = new EmailDTO(toEmail, subject, htmlBody, true);
                sendEmail(dto);
        }

        /**
         * 產生正式飯店風格的訂單確認 Email
         */
        private String buildOrderHtml(Integer orderId, Integer memberId) {

                // =========================================================
                // 1. 取得會員 Email
                // =========================================================
                String memberInfo = profileRepository.findByAccountId(
                                memberRepository.findById(memberId)
                                                .orElseThrow(() -> new IllegalArgumentException("找不到會員"))
                                                .getAccountId())
                                .map(Profile::getEmail)
                                .orElse("會員資訊不可用");

                // =========================================================
                // 2. 取得訂單商品
                // =========================================================
                List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

                // =========================================================
                // 3. 建立 HTML
                // =========================================================
                StringBuilder sb = new StringBuilder();

                sb.append("<!DOCTYPE html>");
                sb.append("<html>");
                sb.append("<head>");
                sb.append("<meta charset='UTF-8'>");

                // Email CSS
                sb.append("<style>");
                sb.append("body{margin:0;padding:0;background-color:#f4f1ea;");
                sb.append("font-family:'Microsoft JhengHei',Arial,sans-serif;color:#333333;}");

                sb.append("a{text-decoration:none;}");

                sb.append("</style>");
                sb.append("</head>");

                // =========================================================
                // BODY
                // =========================================================
                sb.append("<body>");

                // 外層背景
                sb.append("<table role='presentation' width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='background-color:#f4f1ea;padding:35px 10px;'>");

                sb.append("<tr>");
                sb.append("<td align='center'>");

                // =========================================================
                // EMAIL 主卡片
                // =========================================================
                sb.append("<table role='presentation' width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='max-width:680px;background-color:#ffffff;");
                sb.append("border-radius:12px;overflow:hidden;'>");

                // =========================================================
                // HEADER
                // =========================================================
                sb.append("<tr>");
                sb.append("<td style='background-color:#2b2219;padding:35px 25px;text-align:center;'>");

                sb.append("<div style='");
                sb.append("color:#d4af37;");
                sb.append("font-size:28px;");
                sb.append("font-weight:bold;");
                sb.append("letter-spacing:5px;");
                sb.append("'>");
                sb.append("星澄飯店");
                sb.append("</div>");

                sb.append("<div style='");
                sb.append("margin-top:8px;");
                sb.append("color:#eae5dc;");
                sb.append("font-size:13px;");
                sb.append("letter-spacing:2px;");
                sb.append("'>");
                sb.append("GRAND ASTER HOTEL & RESORTS");
                sb.append("</div>");

                sb.append("</td>");
                sb.append("</tr>");

                // =========================================================
                // GOLD LINE
                // =========================================================
                sb.append("<tr>");
                sb.append("<td style='height:4px;background-color:#d4af37;'></td>");
                sb.append("</tr>");

                // =========================================================
                // 主要內容
                // =========================================================
                sb.append("<tr>");
                sb.append("<td style='padding:40px 35px;'>");

                // =========================================================
                // 訂單成功
                // =========================================================
                sb.append("<div style='text-align:center;margin-bottom:30px;'>");

                sb.append("<div style='");
                sb.append("display:inline-block;");
                sb.append("background-color:#f4ead5;");
                sb.append("border:1px solid #e1c98a;");
                sb.append("border-radius:30px;");
                sb.append("padding:9px 22px;");
                sb.append("color:#8c692e;");
                sb.append("font-size:14px;");
                sb.append("font-weight:bold;");
                sb.append("'>");

                sb.append("✓　訂單確認成功");

                sb.append("</div>");
                sb.append("</div>");

                // =========================================================
                // 主標題
                // =========================================================
                sb.append("<h1 style='");
                sb.append("margin:0;");
                sb.append("text-align:center;");
                sb.append("font-size:24px;");
                sb.append("font-weight:bold;");
                sb.append("color:#2b2219;");
                sb.append("'>");

                sb.append("感謝您的預訂");

                sb.append("</h1>");

                sb.append("<p style='");
                sb.append("text-align:center;");
                sb.append("font-size:15px;");
                sb.append("line-height:1.8;");
                sb.append("color:#666666;");
                sb.append("margin:14px 0 32px 0;");
                sb.append("'>");

                sb.append("親愛的貴賓您好，<br>");
                sb.append("感謝您選擇星澄飯店。您的訂單已成功建立，");
                sb.append("<br>");
                sb.append("以下為本次訂單的詳細資訊，敬請查閱。");

                sb.append("</p>");

                // =========================================================
                // 訂單資訊 CARD
                // =========================================================
                sb.append("<table width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='background-color:#faf7f2;");
                sb.append("border:1px solid #eee5d8;");
                sb.append("border-radius:8px;overflow:hidden;margin-bottom:30px;'>");

                // CARD TITLE
                sb.append("<tr>");
                sb.append("<td style='");
                sb.append("padding:15px 20px;");
                sb.append("background-color:#4a3b2a;");
                sb.append("color:#ffffff;");
                sb.append("font-size:16px;");
                sb.append("font-weight:bold;");
                sb.append("'>");

                sb.append("訂單資訊");

                sb.append("</td>");
                sb.append("</tr>");

                // ORDER NUMBER
                sb.append("<tr>");
                sb.append("<td style='padding:20px;'>");

                sb.append("<div style='font-size:12px;color:#888888;margin-bottom:5px;'>");
                sb.append("訂單編號");
                sb.append("</div>");

                sb.append("<div style='");
                sb.append("font-size:22px;");
                sb.append("font-weight:bold;");
                sb.append("color:#9b7435;");
                sb.append("letter-spacing:1px;");
                sb.append("'>");

                sb.append("#").append(orderId);

                sb.append("</div>");

                sb.append("</td>");
                sb.append("</tr>");

                // EMAIL
                sb.append("<tr>");
                sb.append("<td style='padding:0 20px 20px 20px;'>");

                sb.append("<div style='font-size:12px;color:#888888;margin-bottom:5px;'>");
                sb.append("會員 Email");
                sb.append("</div>");

                sb.append("<div style='font-size:14px;color:#444444;'>");
                sb.append(memberInfo);
                sb.append("</div>");

                sb.append("</td>");
                sb.append("</tr>");

                sb.append("</table>");

                // =========================================================
                // 商品明細標題
                // =========================================================
                sb.append("<h2 style='");
                sb.append("font-size:19px;");
                sb.append("color:#2b2219;");
                sb.append("margin:0 0 15px 0;");
                sb.append("padding-bottom:10px;");
                sb.append("border-bottom:2px solid #d4af37;");
                sb.append("'>");

                sb.append("訂單明細");

                sb.append("</h2>");

                // =========================================================
                // 商品 TABLE
                // =========================================================
                sb.append("<table width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='border-collapse:collapse;font-size:14px;'>");

                // 表頭
                sb.append("<tr style='background-color:#4a3b2a;color:#ffffff;'>");

                sb.append("<th style='padding:13px 10px;text-align:left;font-weight:normal;'>");
                sb.append("商品");
                sb.append("</th>");

                sb.append("<th style='padding:13px 10px;text-align:center;font-weight:normal;'>");
                sb.append("數量");
                sb.append("</th>");

                sb.append("<th style='padding:13px 10px;text-align:right;font-weight:normal;'>");
                sb.append("單價");
                sb.append("</th>");

                sb.append("<th style='padding:13px 10px;text-align:right;font-weight:normal;'>");
                sb.append("小計");
                sb.append("</th>");

                sb.append("</tr>");

                // =========================================================
                // 商品資料
                // =========================================================
                for (int i = 0; i < items.size(); i++) {

                        OrderItem item = items.get(i);

                        Product product = productRepository.findById(item.getProductId())
                                        .orElseThrow(() -> new IllegalArgumentException("找不到商品"));

                        String rowColor = (i % 2 == 0)
                                        ? "#ffffff"
                                        : "#faf7f2";

                        sb.append("<tr style='background-color:")
                                        .append(rowColor)
                                        .append(";'>");

                        // 商品名稱
                        sb.append("<td style='");
                        sb.append("padding:14px 10px;");
                        sb.append("border-bottom:1px solid #eee8df;");
                        sb.append("color:#333333;");
                        sb.append("font-weight:bold;");
                        sb.append("'>");

                        sb.append(product.getProductName());

                        sb.append("</td>");

                        // 數量
                        sb.append("<td style='");
                        sb.append("padding:14px 10px;");
                        sb.append("border-bottom:1px solid #eee8df;");
                        sb.append("text-align:center;");
                        sb.append("color:#555555;");
                        sb.append("'>");

                        sb.append(item.getQuantity());

                        sb.append("</td>");

                        // 單價
                        sb.append("<td style='");
                        sb.append("padding:14px 10px;");
                        sb.append("border-bottom:1px solid #eee8df;");
                        sb.append("text-align:right;");
                        sb.append("color:#555555;");
                        sb.append("'>");

                        sb.append("$").append(product.getPrice());

                        sb.append("</td>");

                        // 小計
                        sb.append("<td style='");
                        sb.append("padding:14px 10px;");
                        sb.append("border-bottom:1px solid #eee8df;");
                        sb.append("text-align:right;");
                        sb.append("color:#9b7435;");
                        sb.append("font-weight:bold;");
                        sb.append("'>");

                        sb.append("$").append(item.getSubtotal());

                        sb.append("</td>");

                        sb.append("</tr>");
                }

                sb.append("</table>");

                // =========================================================
                // 訂單總金額
                // =========================================================
                sb.append("<table width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='margin-top:20px;'>");

                sb.append("<tr>");

                sb.append("<td style='");
                sb.append("text-align:right;");
                sb.append("padding:15px 10px;");
                sb.append("color:#555555;");
                sb.append("font-size:15px;");
                sb.append("'>");

                sb.append("訂單總金額");

                sb.append("</td>");

                sb.append("<td width='150' style='");
                sb.append("text-align:right;");
                sb.append("padding:15px 10px;");
                sb.append("color:#9b7435;");
                sb.append("font-size:24px;");
                sb.append("font-weight:bold;");
                sb.append("'>");

                sb.append("$");

                // 計算總金額
                java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;

                for (OrderItem item : items) {
                        if (item.getSubtotal() != null) {
                                totalAmount = totalAmount.add(
                                                java.math.BigDecimal.valueOf(item.getSubtotal()));
                        }
                }

                sb.append(totalAmount);

                sb.append("</td>");

                sb.append("</tr>");
                sb.append("</table>");

                // =========================================================
                // 重要提醒
                // =========================================================
                sb.append("<table width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='margin-top:30px;");
                sb.append("background-color:#faf7f2;");
                sb.append("border-left:4px solid #d4af37;'>");

                sb.append("<tr>");
                sb.append("<td style='padding:18px 20px;'>");

                sb.append("<div style='");
                sb.append("font-size:15px;");
                sb.append("font-weight:bold;");
                sb.append("color:#4a3b2a;");
                sb.append("margin-bottom:8px;");
                sb.append("'>");

                sb.append("入住／訂單提醒");

                sb.append("</div>");

                sb.append("<div style='");
                sb.append("font-size:13px;");
                sb.append("line-height:1.8;");
                sb.append("color:#777777;");
                sb.append("'>");

                sb.append("請妥善保存此封訂單確認信。");
                sb.append("<br>");
                sb.append("如需修改或取消訂單，請依飯店相關規定辦理。");
                sb.append("<br>");
                sb.append("若有任何問題，歡迎與星澄飯店服務團隊聯繫。");

                sb.append("</div>");

                sb.append("</td>");
                sb.append("</tr>");
                sb.append("</table>");

                // =========================================================
                // 感謝文字
                // =========================================================
                sb.append("<div style='");
                sb.append("text-align:center;");
                sb.append("margin-top:35px;");
                sb.append("'>");

                sb.append("<div style='");
                sb.append("font-size:15px;");
                sb.append("font-weight:bold;");
                sb.append("color:#4a3b2a;");
                sb.append("margin-bottom:8px;");
                sb.append("'>");

                sb.append("期待您的蒞臨");

                sb.append("</div>");

                sb.append("<div style='");
                sb.append("font-size:13px;");
                sb.append("line-height:1.7;");
                sb.append("color:#888888;");
                sb.append("'>");

                sb.append("感謝您選擇星澄飯店，");
                sb.append("<br>");
                sb.append("我們期待為您提供舒適而愉快的住宿體驗。");

                sb.append("</div>");

                sb.append("</div>");

                sb.append("</td>");
                sb.append("</tr>");

                // =========================================================
                // FOOTER
                // =========================================================
                sb.append("<tr>");
                sb.append("<td style='");
                sb.append("background-color:#2b2219;");
                sb.append("padding:25px 20px;");
                sb.append("text-align:center;");
                sb.append("'>");

                sb.append("<div style='");
                sb.append("color:#d4af37;");
                sb.append("font-size:15px;");
                sb.append("font-weight:bold;");
                sb.append("letter-spacing:2px;");
                sb.append("margin-bottom:8px;");
                sb.append("'>");

                sb.append("星澄飯店");

                sb.append("</div>");

                sb.append("<div style='");
                sb.append("color:#c8c1b8;");
                sb.append("font-size:11px;");
                sb.append("line-height:1.6;");
                sb.append("'>");

                sb.append("Grand Aster Hotel & Resorts");
                sb.append("<br>");
                sb.append("© 2026 星澄飯店. All rights reserved.");

                sb.append("</div>");

                sb.append("</td>");
                sb.append("</tr>");

                // =========================================================
                // HTML 結束
                // =========================================================
                sb.append("</table>");

                sb.append("</td>");
                sb.append("</tr>");

                sb.append("</table>");

                sb.append("</body>");
                sb.append("</html>");

                return sb.toString();
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

    /**
     * 發送忘記密碼/重設密碼信箱驗證碼
     *
     * @param toEmail 收件人電子郵箱
     * @param code    6 位數驗證碼
     */
    public void sendResetPasswordCode(String toEmail, String code) {
        String subject = "【星澄飯店】重設密碼驗證碼";
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
                + "              <h2 style='margin-top: 0; color: #4a3b2a; font-size: 20px; font-weight: bold; border-bottom: 2px solid #e8dfd3; padding-bottom: 12px;'>重設會員登入密碼</h2>"
                + "              <p style='font-size: 15px; line-height: 1.7; color: #555555; margin: 18px 0 10px 0;'>您好，</p>"
                + "              <p style='font-size: 15px; line-height: 1.7; color: #555555; margin: 0 0 24px 0;'>我們收到了您重設星澄飯店會員密碼的請求。請使用下方 6 位數驗證碼進行驗證並設定新密碼：</p>"
                + "              <div style='text-align: center; margin: 30px 0;'>"
                + "                <div style='display: inline-block; background-color: #faf7f2; border: 2px dashed #b58a46; border-radius: 10px; padding: 16px 36px;'>"
                + "                  <span style='font-size: 34px; font-weight: bold; letter-spacing: 8px; color: #9b7435; font-family: Consolas, Monaco, monospace;'>" + code + "</span>"
                + "                </div>"
                + "              </div>"
                + "              <p style='font-size: 14px; line-height: 1.6; color: #888888; text-align: center; margin: 16px 0 28px 0;'>"
                + "                ⏰ 驗證碼有效期限為 <strong>5 分鐘</strong>，請盡速完成密碼重設。"
                + "              </p>"
                + "              <hr style='border: none; border-top: 1px solid #eeeeee; margin: 24px 0;' />"
                + "              <p style='font-size: 12px; color: #999999; line-height: 1.6; margin: 0;'>"
                + "                ※ 此為系統自動發送信件，請勿直接回覆。<br/>"
                + "                ※ 若您未曾申請重設密碼，請忽略此郵件或儘速登入系統以確保帳號安全。"
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
