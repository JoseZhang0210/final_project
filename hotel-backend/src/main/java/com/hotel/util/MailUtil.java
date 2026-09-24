package com.hotel.util;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.hotel.model.dto.EmailDTO;
import com.hotel.model.entity.Booking;
import com.hotel.model.entity.CustomerOrder;
import com.hotel.model.entity.OrderItem;
import com.hotel.model.entity.Product;
import com.hotel.model.entity.Room;
import com.hotel.model.entity.RoomType;
import com.hotel.repository.BookingRepository;
import com.hotel.repository.CustomerOrderRepository;
import com.hotel.repository.MemberRepository;
import com.hotel.repository.OrderItemRepository;
import com.hotel.repository.ProductRepository;
import com.hotel.repository.ProfileRepository;
import com.hotel.repository.RoomRepository;
import com.hotel.repository.RoomTypeRepository;

import jakarta.mail.internet.MimeMessage;

@Component
public class MailUtil {

        private static final Logger log = LoggerFactory.getLogger(MailUtil.class);

        private final JavaMailSender mailSender;
        private final ProfileRepository profileRepository;
        private final MemberRepository memberRepository;
        private final OrderItemRepository orderItemRepository;
        private final ProductRepository productRepository;
        private final CustomerOrderRepository customerOrderRepository;
        private final BookingRepository bookingRepository;
        private final RoomTypeRepository roomTypeRepository;
        private final RoomRepository roomRepository;

        // 自動讀取 application.properties 裡的發信人設定
        @Value("${spring.mail.username}")
        private String fromEmail;

        // 自動讀取前端基礎網址 (支援本機與線上環境)
        @Value("${app.frontend.base-url:https://starlight-hotel.vercel.app}")
        private String frontendBaseUrl;

        MailUtil(JavaMailSender mailSender, ProfileRepository profileRepository, MemberRepository memberRepository,
                        OrderItemRepository orderItemRepository, ProductRepository productRepository,
                        CustomerOrderRepository customerOrderRepository, BookingRepository bookingRepository,
                        RoomTypeRepository roomTypeRepository, RoomRepository roomRepository) {
                this.mailSender = mailSender;
                this.profileRepository = profileRepository;
                this.memberRepository = memberRepository;
                this.orderItemRepository = orderItemRepository;
                this.productRepository = productRepository;
                this.customerOrderRepository = customerOrderRepository;
                this.bookingRepository = bookingRepository;
                this.roomTypeRepository = roomTypeRepository;
                this.roomRepository = roomRepository;
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
                        boolean hasAttachments = emailDto.getAttachments() != null
                                        && !emailDto.getAttachments().isEmpty();
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

                } catch (Exception e) {
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
                                .map(p -> p != null ? p.getEmail() : null)
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
         * 發送訂房確認信件 (非同步)。
         * 
         * @param bookingId 訂單編號
         */
        @Async
        public void sendBookingConfirmation(Integer bookingId) {
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new IllegalArgumentException("找不到訂房編號 " + bookingId));

                String toEmail = profileRepository.findByAccountId(
                                memberRepository.findById(booking.getMemberId())
                                                .orElseThrow(() -> new IllegalArgumentException("找不到會員"))
                                                .getAccountId())
                                .map(p -> p != null ? p.getEmail() : null)
                                .orElseThrow(() -> new IllegalArgumentException("找不到會員的 Email"));

                String subject = "【星澄飯店】訂房確認 - 訂單編號 " + bookingId;
                String htmlBody = buildBookingHtml(bookingId);

                EmailDTO dto = new EmailDTO(toEmail, subject, htmlBody, true);
                sendEmail(dto);
        }

        /**
         * 發送訂房取消與退款確認信件 (非同步)。
         * 
         * @param bookingId 訂單編號
         */
        @Async
        public void sendBookingCancellation(Integer bookingId) {
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new IllegalArgumentException("找不到訂房編號 " + bookingId));

                String toEmail = profileRepository.findByAccountId(
                                memberRepository.findById(booking.getMemberId())
                                                .orElseThrow(() -> new IllegalArgumentException("找不到會員"))
                                                .getAccountId())
                                .map(p -> p != null ? p.getEmail() : null)
                                .orElseThrow(() -> new IllegalArgumentException("找不到會員的 Email"));

                String subject = "【星澄飯店】訂房取消與退款通知 - 訂單編號 " + bookingId;
                String htmlBody = buildBookingCancellationHtml(bookingId);

                EmailDTO dto = new EmailDTO(toEmail, subject, htmlBody, true);
                sendEmail(dto);
        }

        /**
         * 發送入住報到成功與開門 QR Code 郵件 (非同步)。
         * 時效為訂單入住日 15:00 起至退房日 11:00。
         *
         * @param bookingId 訂單編號
         */
        @Async
        public void sendCheckInSuccess(Integer bookingId) {
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new IllegalArgumentException("找不到訂房編號 " + bookingId));

                String toEmail = profileRepository.findByAccountId(
                                memberRepository.findById(booking.getMemberId())
                                                .orElseThrow(() -> new IllegalArgumentException("找不到會員"))
                                                .getAccountId())
                                .map(p -> p != null ? p.getEmail() : null)
                                .orElseThrow(() -> new IllegalArgumentException("找不到會員的 Email"));

                String subject = "【星澄飯店】入住報到完成通知 - 您的專屬房號與開門 QR Code (#" + bookingId + ")";
                String htmlBody = buildCheckInSuccessHtml(bookingId);

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
                                .map(p -> p != null ? p.getEmail() : null)
                                .orElse("會員資訊不可用");

                // =========================================================
                // 2. 取得訂單商品
                // =========================================================
                List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
                if (items.isEmpty()) {
                        throw new IllegalStateException("訂單 " + orderId + " 沒有商品明細，停止寄送確認信");
                }
                CustomerOrder order = customerOrderRepository.findById(orderId)
                                .orElseThrow(() -> new IllegalArgumentException("找不到訂單 " + orderId));

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

                        sb.append("$").append(item.getUnitPrice());

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
                sb.append("<tr><td style='text-align:right;padding:8px 10px;'>商品總額</td>");
                sb.append("<td style='text-align:right;padding:8px 10px;'>$")
                                .append(order.getOriginalAmount()).append("</td></tr>");
                sb.append("<tr><td style='text-align:right;padding:8px 10px;'>優惠折扣</td>");
                sb.append("<td style='text-align:right;padding:8px 10px;'>-$")
                                .append(order.getDiscountAmount()).append("</td></tr>");

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

                sb.append(order.getFinalAmount());

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
         * 產生正式飯店風格的訂房確認 Email
         */
        private String buildBookingHtml(Integer bookingId) {
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new IllegalArgumentException("找不到訂房編號 " + bookingId));

                RoomType roomType = roomTypeRepository.findById(booking.getRoomTypeId())
                                .orElseThrow(() -> new IllegalArgumentException("找不到房型"));

                String memberInfo = profileRepository.findByAccountId(
                                memberRepository.findById(booking.getMemberId())
                                                .orElseThrow(() -> new IllegalArgumentException("找不到會員"))
                                                .getAccountId())
                                .map(p -> p != null ? p.getEmail() : null)
                                .orElse("會員資訊不可用");

                StringBuilder sb = new StringBuilder();

                sb.append("<!DOCTYPE html>");
                sb.append("<html>");
                sb.append("<head>");
                sb.append("<meta charset='UTF-8'>");
                sb.append("<style>");
                sb.append("body{margin:0;padding:0;background-color:#f4f1ea;");
                sb.append("font-family:'Microsoft JhengHei',Arial,sans-serif;color:#333333;}");
                sb.append("a{text-decoration:none;}");
                sb.append("</style>");
                sb.append("</head>");
                sb.append("<body>");

                sb.append("<table role='presentation' width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='background-color:#f4f1ea;padding:35px 10px;'>");
                sb.append("<tr>");
                sb.append("<td align='center'>");

                sb.append("<table role='presentation' width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='max-width:680px;background-color:#ffffff;");
                sb.append("border-radius:12px;overflow:hidden;'>");

                // HEADER
                sb.append("<tr>");
                sb.append("<td style='background-color:#2b2219;padding:35px 25px;text-align:center;'>");
                sb.append("<div style='color:#d4af37;font-size:28px;font-weight:bold;letter-spacing:5px;'>星澄飯店</div>");
                sb.append(
                                "<div style='margin-top:8px;color:#eae5dc;font-size:13px;letter-spacing:2px;'>GRAND ASTER HOTEL & RESORTS</div>");
                sb.append("</td>");
                sb.append("</tr>");

                sb.append("<tr>");
                sb.append("<td style='height:4px;background-color:#d4af37;'></td>");
                sb.append("</tr>");

                // CONTENT
                sb.append("<tr>");
                sb.append("<td style='padding:40px 35px;'>");

                sb.append("<div style='text-align:center;margin-bottom:30px;'>");
                sb.append(
                                "<div style='display:inline-block;background-color:#f4ead5;border:1px solid #e1c98a;border-radius:30px;padding:9px 22px;color:#8c692e;font-size:14px;font-weight:bold;'>");
                sb.append("✓　訂房確認成功");
                sb.append("</div>");
                sb.append("</div>");

                sb.append("<h1 style='margin:0;text-align:center;font-size:24px;font-weight:bold;color:#2b2219;'>感謝您的預訂</h1>");
                sb.append("<p style='text-align:center;font-size:15px;line-height:1.8;color:#666666;margin:14px 0 32px 0;'>");
                sb.append("親愛的貴賓您好，<br>");
                sb.append("感謝您選擇星澄飯店。您的訂房已成功保留，<br>");
                sb.append("以下為本次訂單的詳細資訊，敬請查閱。");
                sb.append("</p>");

                // ORDER INFO CARD
                sb.append("<table width='100%' cellspacing='0' cellpadding='0'");
                sb.append(
                                " style='background-color:#faf7f2;border:1px solid #eee5d8;border-radius:8px;overflow:hidden;margin-bottom:30px;'>");

                sb.append("<tr>");
                sb.append(
                                "<td colspan='2' style='padding:15px 20px;background-color:#4a3b2a;color:#ffffff;font-size:16px;font-weight:bold;'>");
                sb.append("訂單資訊");
                sb.append("</td>");
                sb.append("</tr>");

                sb.append("<tr>");
                sb.append("<td style='padding:20px;width:50%;'>");
                sb.append("<div style='font-size:12px;color:#888888;margin-bottom:5px;'>訂單編號</div>");
                sb.append("<div style='font-size:22px;font-weight:bold;color:#9b7435;letter-spacing:1px;'>#")
                                .append(bookingId)
                                .append("</div>");
                sb.append("</td>");
                sb.append("<td style='padding:20px;width:50%;'>");
                sb.append("<div style='font-size:12px;color:#888888;margin-bottom:5px;'>會員 Email</div>");
                sb.append("<div style='font-size:14px;color:#444444;'>").append(memberInfo).append("</div>");
                sb.append("</td>");
                sb.append("</tr>");

                sb.append("</table>");

                // BOOKING DETAILS TITLE
                sb.append(
                                "<h2 style='font-size:19px;color:#2b2219;margin:0 0 15px 0;padding-bottom:10px;border-bottom:2px solid #d4af37;'>");
                sb.append("入住明細");
                sb.append("</h2>");

                // BOOKING TABLE
                sb.append("<table width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='border-collapse:collapse;font-size:14px;margin-bottom:30px;'>");

                // Check-in / Check-out
                sb.append("<tr style='background-color:#ffffff;'>");
                sb.append("<td style='padding:14px 10px;border-bottom:1px solid #eee8df;color:#333333;width:25%;'>入住日期</td>");
                sb.append(
                                "<td style='padding:14px 10px;border-bottom:1px solid #eee8df;color:#555555;font-weight:bold;width:25%;'>")
                                .append(booking.getCheckInDate()).append(" 15:00</td>");
                sb.append("<td style='padding:14px 10px;border-bottom:1px solid #eee8df;color:#333333;width:25%;'>退房日期</td>");
                sb.append(
                                "<td style='padding:14px 10px;border-bottom:1px solid #eee8df;color:#555555;font-weight:bold;width:25%;'>")
                                .append(booking.getCheckOutDate()).append(" 11:00</td>");
                sb.append("</tr>");

                // Room Type & Guest Num
                sb.append("<tr style='background-color:#faf7f2;'>");
                sb.append("<td style='padding:14px 10px;border-bottom:1px solid #eee8df;color:#333333;'>房型</td>");
                sb.append("<td colspan='3' style='padding:14px 10px;border-bottom:1px solid #eee8df;color:#555555;'>")
                                .append(roomType.getTypeName()).append("</td>");
                sb.append("</tr>");

                sb.append("<tr style='background-color:#ffffff;'>");
                sb.append("<td style='padding:14px 10px;border-bottom:1px solid #eee8df;color:#333333;'>入住人數</td>");
                sb.append("<td colspan='3' style='padding:14px 10px;border-bottom:1px solid #eee8df;color:#555555;'>")
                                .append(booking.getGuestNum()).append(" 人</td>");
                sb.append("</tr>");

                sb.append("</table>");

                // PRICE
                sb.append("<table width='100%' cellspacing='0' cellpadding='0' style='margin-top:20px;'>");
                sb.append("<tr>");
                sb.append("<td style='text-align:right;padding:15px 10px;color:#555555;font-size:15px;'>付款總金額</td>");
                sb.append(
                                "<td width='150' style='text-align:right;padding:15px 10px;color:#9b7435;font-size:24px;font-weight:bold;'>$")
                                .append(booking.getBookingPrice()).append("</td>");
                sb.append("</tr>");
                sb.append("</table>");

                // =========================================================
                // CHECK-IN QR PASS (專屬快速入住通行證)
                // =========================================================
                String checkInCode = "CK" + bookingId
                                + String.format("%04d", Math.abs((bookingId * 37 + 1013) % 10000));
                String baseUrl = (frontendBaseUrl != null && !frontendBaseUrl.isBlank()) ? frontendBaseUrl
                                : "https://starlight-hotel.vercel.app";
                String encodedRoomType = java.net.URLEncoder.encode(roomType.getTypeName(),
                                java.nio.charset.StandardCharsets.UTF_8);
                String qrDataUrl = baseUrl + "/mobile-pass?code=" + checkInCode + "&booking=" + bookingId
                                + "&checkIn=" + booking.getCheckInDate()
                                + "&checkOut=" + booking.getCheckOutDate()
                                + "&roomType=" + encodedRoomType;

                sb.append("<table width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='margin-top:28px;background-color:#faf7f2;border:1px dashed #d4af37;border-radius:10px;overflow:hidden;'>");
                sb.append("<tr>");
                sb.append("<td style='padding:28px 20px;text-align:center;'>");

                sb.append("<div style='display:inline-block;background-color:#2b2219;color:#d4af37;font-size:12px;font-weight:bold;letter-spacing:1px;padding:5px 16px;border-radius:20px;margin-bottom:12px;'>");
                sb.append("MOBILE CHECK-IN PASS");
                sb.append("</div>");

                sb.append("<div style='font-size:18px;font-weight:bold;color:#4a3b2a;margin-bottom:6px;'>");
                sb.append("專屬快速入住 QR Code 通行證");
                sb.append("</div>");

                sb.append("<div style='font-size:13px;color:#666666;line-height:1.6;margin-bottom:18px;'>");
                sb.append("抵達飯店時，請向櫃檯人員或自助報到機出示此 QR Code 即可完成快速核驗入住。");
                sb.append("</div>");

                sb.append("<div style='display:inline-block;padding:12px;background:#ffffff;border:1px solid #e8dfd3;border-radius:8px;box-shadow:0 2px 8px rgba(0,0,0,0.05);'>");
                sb.append("<img src='https://api.qrserver.com/v1/create-qr-code/?size=160x160&data=")
                                .append(java.net.URLEncoder.encode(qrDataUrl, java.nio.charset.StandardCharsets.UTF_8))
                                .append("' alt='Check-in QR Code' width='160' height='160' style='display:block;' />");
                sb.append("</div>");

                sb.append("<div style='margin-top:14px;font-size:13px;color:#888888;'>");
                sb.append("入住快速驗證碼：<strong style='color:#9b7435;font-size:16px;letter-spacing:2px;font-family:Consolas,Monaco,monospace;'>")
                                .append(checkInCode).append("</strong>");
                sb.append("</div>");

                sb.append("</td>");
                sb.append("</tr>");
                sb.append("</table>");

                // REMINDER
                sb.append("<table width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='margin-top:30px;background-color:#faf7f2;border-left:4px solid #d4af37;'>");
                sb.append("<tr><td style='padding:18px 20px;'>");
                sb.append("<div style='font-size:15px;font-weight:bold;color:#4a3b2a;margin-bottom:8px;'>入住提醒</div>");
                sb.append("<div style='font-size:13px;line-height:1.8;color:#777777;'>");
                sb.append("請記得於入住當日攜帶有照片之身分證件。<br>若需延遲入住或有其他特殊需求，歡迎隨時與櫃檯聯繫。");
                sb.append("</div>");
                sb.append("</td></tr></table>");

                // FOOTER GREETING
                sb.append("<div style='text-align:center;margin-top:35px;'>");
                sb.append("<div style='font-size:15px;font-weight:bold;color:#4a3b2a;margin-bottom:8px;'>期待您的蒞臨</div>");
                sb.append("<div style='font-size:13px;line-height:1.7;color:#888888;'>感謝您選擇星澄飯店，<br>我們期待為您提供舒適而愉快的住宿體驗。</div>");
                sb.append("</div>");

                sb.append("</td></tr>");

                // FOOTER BAR
                sb.append("<tr>");
                sb.append("<td style='background-color:#2b2219;padding:25px 20px;text-align:center;'>");
                sb.append(
                                "<div style='color:#d4af37;font-size:15px;font-weight:bold;letter-spacing:2px;margin-bottom:8px;'>星澄飯店</div>");
                sb.append(
                                "<div style='color:#c8c1b8;font-size:11px;line-height:1.6;'>Grand Aster Hotel & Resorts<br>© 2026 星澄飯店. All rights reserved.</div>");
                sb.append("</td></tr>");

                sb.append("</table>");
                sb.append("</td></tr></table>");
                sb.append("</body></html>");

                return sb.toString();
        }

        /**
         * 產生正式飯店風格的訂房取消與退款通知 Email
         */
        private String buildBookingCancellationHtml(Integer bookingId) {
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new IllegalArgumentException("找不到訂房編號 " + bookingId));

                RoomType roomType = roomTypeRepository.findById(booking.getRoomTypeId())
                                .orElseThrow(() -> new IllegalArgumentException("找不到房型"));

                String memberInfo = profileRepository.findByAccountId(
                                memberRepository.findById(booking.getMemberId())
                                                .orElseThrow(() -> new IllegalArgumentException("找不到會員"))
                                                .getAccountId())
                                .map(p -> p != null ? p.getEmail() : null)
                                .orElse("會員資訊不可用");

                StringBuilder sb = new StringBuilder();

                sb.append("<!DOCTYPE html>");
                sb.append("<html>");
                sb.append("<head>");
                sb.append("<meta charset='UTF-8'>");
                sb.append("<style>");
                sb.append("body{margin:0;padding:0;background-color:#f4f1ea;");
                sb.append("font-family:'Microsoft JhengHei',Arial,sans-serif;color:#333333;}");
                sb.append("a{text-decoration:none;}");
                sb.append("</style>");
                sb.append("</head>");
                sb.append("<body>");

                sb.append("<table role='presentation' width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='background-color:#f4f1ea;padding:35px 10px;'>");
                sb.append("<tr>");
                sb.append("<td align='center'>");

                sb.append("<table role='presentation' width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='max-width:680px;background-color:#ffffff;");
                sb.append("border-radius:12px;overflow:hidden;'>");

                // HEADER
                sb.append("<tr>");
                sb.append("<td style='background-color:#2b2219;padding:35px 25px;text-align:center;'>");
                sb.append("<div style='color:#d4af37;font-size:28px;font-weight:bold;letter-spacing:5px;'>星澄飯店</div>");
                sb.append("<div style='margin-top:8px;color:#eae5dc;font-size:13px;letter-spacing:2px;'>GRAND ASTER HOTEL & RESORTS</div>");
                sb.append("</td>");
                sb.append("</tr>");

                sb.append("<tr>");
                sb.append("<td style='height:4px;background-color:#c62828;'></td>");
                sb.append("</tr>");

                // CONTENT
                sb.append("<tr>");
                sb.append("<td style='padding:40px 35px;'>");

                sb.append("<div style='text-align:center;margin-bottom:30px;'>");
                sb.append("<div style='display:inline-block;background-color:#ffebee;border:1px solid #ffcdd2;border-radius:30px;padding:9px 22px;color:#c62828;font-size:14px;font-weight:bold;'>");
                sb.append("✕　訂房已取消／全額退款處理中");
                sb.append("</div>");
                sb.append("</div>");

                sb.append("<h1 style='margin:0;text-align:center;font-size:24px;font-weight:bold;color:#2b2219;'>訂房取消與退款通知</h1>");
                sb.append("<p style='text-align:center;font-size:15px;line-height:1.8;color:#666666;margin:14px 0 32px 0;'>");
                sb.append("親愛的貴賓您好，<br>");
                sb.append("您於星澄飯店的預訂已成功取消。<br>");
                sb.append("因您的取消申請符合「入住 3 天前免費取消」政策，系統已為您啟動全額退款流程。");
                sb.append("</p>");

                // ORDER INFO CARD
                sb.append("<table width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='background-color:#faf7f2;border:1px solid #eee5d8;border-radius:8px;overflow:hidden;margin-bottom:30px;'>");

                sb.append("<tr>");
                sb.append("<td colspan='2' style='padding:15px 20px;background-color:#4a3b2a;color:#ffffff;font-size:16px;font-weight:bold;'>");
                sb.append("取消訂單資訊");
                sb.append("</td>");
                sb.append("</tr>");

                sb.append("<tr>");
                sb.append("<td style='padding:20px;width:50%;'>");
                sb.append("<div style='font-size:12px;color:#888888;margin-bottom:5px;'>訂單編號</div>");
                sb.append("<div style='font-size:22px;font-weight:bold;color:#9b7435;letter-spacing:1px;'>#")
                                .append(bookingId).append("</div>");
                sb.append("</td>");
                sb.append("<td style='padding:20px;width:50%;'>");
                sb.append("<div style='font-size:12px;color:#888888;margin-bottom:5px;'>會員 Email</div>");
                sb.append("<div style='font-size:14px;color:#444444;'>").append(memberInfo).append("</div>");
                sb.append("</td>");
                sb.append("</tr>");

                sb.append("</table>");

                // BOOKING DETAILS TITLE
                sb.append("<h2 style='font-size:19px;color:#2b2219;margin:0 0 15px 0;padding-bottom:10px;border-bottom:2px solid #d4af37;'>");
                sb.append("已取消明細");
                sb.append("</h2>");

                // BOOKING TABLE
                sb.append("<table width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='border-collapse:collapse;font-size:14px;margin-bottom:30px;'>");

                sb.append("<tr style='background-color:#ffffff;'>");
                sb.append("<td style='padding:14px 10px;border-bottom:1px solid #eee8df;color:#333333;width:25%;'>原入住日期</td>");
                sb.append("<td style='padding:14px 10px;border-bottom:1px solid #eee8df;color:#555555;font-weight:bold;width:25%;'>")
                                .append(booking.getCheckInDate()).append("</td>");
                sb.append("<td style='padding:14px 10px;border-bottom:1px solid #eee8df;color:#333333;width:25%;'>原退房日期</td>");
                sb.append("<td style='padding:14px 10px;border-bottom:1px solid #eee8df;color:#555555;font-weight:bold;width:25%;'>")
                                .append(booking.getCheckOutDate()).append("</td>");
                sb.append("</tr>");

                sb.append("<tr style='background-color:#faf7f2;'>");
                sb.append("<td style='padding:14px 10px;border-bottom:1px solid #eee8df;color:#333333;'>取消房型</td>");
                sb.append("<td colspan='3' style='padding:14px 10px;border-bottom:1px solid #eee8df;color:#555555;'>")
                                .append(roomType.getTypeName()).append("</td>");
                sb.append("</tr>");

                sb.append("</table>");

                // REFUND AMOUNT
                sb.append("<table width='100%' cellspacing='0' cellpadding='0' style='margin-top:20px;'>");
                sb.append("<tr>");
                sb.append("<td style='text-align:right;padding:15px 10px;color:#555555;font-size:15px;'>應退款總金額</td>");
                sb.append("<td width='150' style='text-align:right;padding:15px 10px;color:#2e7d32;font-size:24px;font-weight:bold;'>$")
                                .append(booking.getBookingPrice()).append("</td>");
                sb.append("</tr>");
                sb.append("</table>");

                // REFUND REMINDER
                sb.append("<table width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='margin-top:30px;background-color:#faf7f2;border-left:4px solid #d4af37;'>");
                sb.append("<tr><td style='padding:18px 20px;'>");
                sb.append("<div style='font-size:15px;font-weight:bold;color:#4a3b2a;margin-bottom:8px;'>退款說明與提醒</div>");
                sb.append("<div style='font-size:13px;line-height:1.8;color:#777777;'>");
                sb.append("• 信用卡退刷款項通常預計於 3～7 個工作天內依各發卡銀行結帳週期入帳。<br>");
                sb.append("• 若您對退款進度有任何疑問，歡迎隨時與星澄飯店客服中心聯繫 (服務專線：02-2345-6789)。");
                sb.append("</div>");
                sb.append("</td></tr></table>");

                sb.append("</td></tr>");

                // FOOTER BAR
                sb.append("<tr>");
                sb.append("<td style='background-color:#2b2219;padding:25px 20px;text-align:center;'>");
                sb.append("<div style='color:#d4af37;font-size:15px;font-weight:bold;letter-spacing:2px;margin-bottom:8px;'>星澄飯店</div>");
                sb.append("<div style='color:#c8c1b8;font-size:11px;line-height:1.6;'>Grand Aster Hotel & Resorts<br>© 2026 星澄飯店. All rights reserved.</div>");
                sb.append("</td></tr>");

                sb.append("</table>");
                sb.append("</td></tr></table>");
                sb.append("</body></html>");

                return sb.toString();
        }

        /**
         * 產生正式飯店風格的入住報到成功與開門 QR Code Email
         */
        private String buildCheckInSuccessHtml(Integer bookingId) {
                Booking booking = bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new IllegalArgumentException("找不到訂房編號 " + bookingId));

                RoomType roomType = roomTypeRepository.findById(booking.getRoomTypeId())
                                .orElseThrow(() -> new IllegalArgumentException("找不到房型"));

                String memberInfo = profileRepository.findByAccountId(
                                memberRepository.findById(booking.getMemberId())
                                                .orElseThrow(() -> new IllegalArgumentException("找不到會員"))
                                                .getAccountId())
                                .map(p -> p != null ? p.getEmail() : null)
                                .orElse("會員資訊不可用");

                String roomNumber = "尚未指派";
                if (booking.getRoomId() != null) {
                        Room room = roomRepository.findById(booking.getRoomId()).orElse(null);
                        if (room != null && room.getRoomNumber() != null) {
                                roomNumber = room.getRoomNumber();
                        }
                }

                String verificationCode = "CK" + bookingId
                                + String.format("%04d", Math.abs((bookingId * 37 + 1013) % 10000));
                String roomKeyData = "STARLIGHT-KEY:ROOM-" + roomNumber + ":" + verificationCode;

                String baseUrl = (frontendBaseUrl != null && !frontendBaseUrl.isBlank()) ? frontendBaseUrl
                                : "https://starlight-hotel.vercel.app";
                String encodedRoomType = java.net.URLEncoder.encode(roomType.getTypeName(),
                                java.nio.charset.StandardCharsets.UTF_8);
                String mobilePassUrl = baseUrl + "/mobile-pass?code=" + verificationCode + "&booking=" + bookingId
                                + "&checkIn=" + booking.getCheckInDate()
                                + "&checkOut=" + booking.getCheckOutDate()
                                + "&roomType=" + encodedRoomType
                                + "&room="
                                + java.net.URLEncoder.encode(roomNumber, java.nio.charset.StandardCharsets.UTF_8);

                String memberBookingUrl = baseUrl + "/member/room-booking";

                StringBuilder sb = new StringBuilder();

                sb.append("<!DOCTYPE html>");
                sb.append("<html>");
                sb.append("<head>");
                sb.append("<meta charset='UTF-8'>");
                sb.append("<style>");
                sb.append("body{margin:0;padding:0;background-color:#f4f1ea;");
                sb.append("font-family:'Microsoft JhengHei',Arial,sans-serif;color:#333333;}");
                sb.append("a{text-decoration:none;}");
                sb.append("</style>");
                sb.append("</head>");
                sb.append("<body>");

                sb.append("<table role='presentation' width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='background-color:#f4f1ea;padding:35px 10px;'>");
                sb.append("<tr>");
                sb.append("<td align='center'>");

                sb.append("<table role='presentation' width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='max-width:680px;background-color:#ffffff;");
                sb.append("border-radius:12px;overflow:hidden;box-shadow:0 4px 15px rgba(0,0,0,0.08);'>");

                // HEADER
                sb.append("<tr>");
                sb.append("<td style='background-color:#2b2219;padding:35px 25px;text-align:center;'>");
                sb.append("<div style='color:#d4af37;font-size:28px;font-weight:bold;letter-spacing:5px;'>星澄飯店</div>");
                sb.append("<div style='margin-top:8px;color:#eae5dc;font-size:13px;letter-spacing:2px;'>GRAND ASTER HOTEL & RESORTS</div>");
                sb.append("</td>");
                sb.append("</tr>");

                sb.append("<tr>");
                sb.append("<td style='height:4px;background-color:#d4af37;'></td>");
                sb.append("</tr>");

                // CONTENT
                sb.append("<tr>");
                sb.append("<td style='padding:40px 35px;'>");

                // STATUS BADGE
                sb.append("<div style='text-align:center;margin-bottom:24px;'>");
                sb.append("<div style='display:inline-block;background-color:#e8f5e9;border:1px solid #c8e6c9;border-radius:30px;padding:9px 24px;color:#2e7d32;font-size:14px;font-weight:bold;'>");
                sb.append("🎉 入住報到完成 (Check-in Verified)");
                sb.append("</div>");
                sb.append("</div>");

                sb.append("<h1 style='margin:0;text-align:center;font-size:24px;font-weight:bold;color:#2b2219;'>歡迎入住！您的客房智慧電子鑰匙已啟用</h1>");
                sb.append("<p style='text-align:center;font-size:15px;line-height:1.8;color:#666666;margin:14px 0 28px 0;'>");
                sb.append("親愛的貴賓您好，<br>");
                sb.append("您已成功完成星澄飯店的報到手續。我們已為您備妥專屬客房，<br>");
                sb.append("下方為您的房號及開門感應專用 QR Code，祝您住宿愉快！");
                sb.append("</p>");

                // ROOM INFO SUMMARY
                sb.append("<table width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='background-color:#faf7f2;border:1px solid #eee5d8;border-radius:10px;overflow:hidden;margin-bottom:28px;'>");
                sb.append("<tr>");
                sb.append("<td style='padding:18px 24px;width:50%;border-right:1px solid #eee5d8;text-align:center;'>");
                sb.append("<div style='font-size:13px;color:#888888;margin-bottom:6px;'>專屬入住房號</div>");
                sb.append("<div style='font-size:28px;font-weight:bold;color:#9b7435;letter-spacing:2px;'>")
                                .append(roomNumber).append(" <span style='font-size:16px;'>號房</span></div>");
                sb.append("</td>");
                sb.append("<td style='padding:18px 24px;width:50%;text-align:center;'>");
                sb.append("<div style='font-size:13px;color:#888888;margin-bottom:6px;'>預訂房型</div>");
                sb.append("<div style='font-size:16px;font-weight:bold;color:#4a3b2a;'>")
                                .append(roomType.getTypeName()).append("</div>");
                sb.append("</td>");
                sb.append("</tr>");
                sb.append("</table>");

                // DOOR KEY QR PASS CARD (開門感應專用 QR CODE)
                sb.append("<table width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='background-color:#faf7f2;border:2px solid #d4af37;border-radius:12px;overflow:hidden;margin-bottom:30px;'>");
                sb.append("<tr>");
                sb.append("<td style='padding:28px 20px;text-align:center;'>");

                sb.append("<div style='display:inline-block;background-color:#2b2219;color:#d4af37;font-size:12px;font-weight:bold;letter-spacing:1px;padding:5px 16px;border-radius:20px;margin-bottom:12px;'>");
                sb.append("ROOM ACCESS KEY");
                sb.append("</div>");

                sb.append("<div style='font-size:19px;font-weight:bold;color:#4a3b2a;margin-bottom:6px;'>");
                sb.append("🔑 房門開門感應專用 QR Code");
                sb.append("</div>");

                sb.append("<div style='font-size:13px;color:#666666;line-height:1.6;margin-bottom:18px;'>");
                sb.append("抵達房門前，請將此 QR Code 對準房門智慧感應區或掃描器即可開門。");
                sb.append("</div>");

                // QR CODE IMAGE
                sb.append("<div style='display:inline-block;padding:12px;background:#ffffff;border:1px solid #e8dfd3;border-radius:10px;box-shadow:0 3px 10px rgba(0,0,0,0.06);'>");
                sb.append("<img src='https://api.qrserver.com/v1/create-qr-code/?size=180x180&data=")
                                .append(java.net.URLEncoder.encode(roomKeyData,
                                                java.nio.charset.StandardCharsets.UTF_8))
                                .append("' alt='Room Access QR Key' width='180' height='180' style='display:block;' />");
                sb.append("</div>");

                // KEY CODE DATA
                sb.append("<div style='margin-top:14px;font-size:13px;color:#888888;'>");
                sb.append("開門金鑰識別碼：<strong style='color:#9b7435;font-size:15px;letter-spacing:1px;font-family:Consolas,Monaco,monospace;'>")
                                .append(roomKeyData).append("</strong>");
                sb.append("</div>");

                // TIME VALIDITY ALERT (下午3點至退房)
                sb.append("<div style='margin-top:16px;background-color:#ffffff;border:1px solid #e0d5c1;border-radius:8px;padding:12px 16px;display:inline-block;text-align:left;max-width:480px;'>");
                sb.append("<div style='font-size:13px;color:#4a3b2a;font-weight:bold;margin-bottom:4px;'>⏳ 電子鑰匙有效期限：</div>");
                sb.append("<div style='font-size:13px;color:#777777;line-height:1.6;'>");
                sb.append("自 <strong style='color:#2e7d32;'>").append(booking.getCheckInDate())
                                .append(" 15:00</strong> 起，至 <strong style='color:#c62828;'>")
                                .append(booking.getCheckOutDate()).append(" 11:00</strong> 退房時止。");
                sb.append("<br><span style='font-size:12px;color:#999999;'>※ 超出此時段該金鑰將自動失效以維護客房安全。</span>");
                sb.append("</div>");
                sb.append("</div>");

                // BUTTON: OPEN MOBILE PASS
                sb.append("<div style='margin-top:22px;'>");
                sb.append("<a href='").append(mobilePassUrl).append(
                                "' style='display:inline-block;background-color:#9b7435;color:#ffffff;font-size:14px;font-weight:bold;padding:11px 26px;border-radius:6px;box-shadow:0 2px 6px rgba(155,116,53,0.3);'>");
                sb.append("📱 開啟手機版智慧房卡通行證");
                sb.append("</a>");
                sb.append("</div>");

                sb.append("</td>");
                sb.append("</tr>");
                sb.append("</table>");

                // ROOM SERVICE GUIDANCE (客房房務申請引導)
                sb.append("<table width='100%' cellspacing='0' cellpadding='0'");
                sb.append(" style='margin-top:20px;background-color:#f9fbf9;border:1px solid #c8e6c9;border-left:4px solid #43a047;border-radius:8px;margin-bottom:28px;'>");
                sb.append("<tr><td style='padding:20px;'>");
                sb.append("<div style='font-size:15px;font-weight:bold;color:#2e7d32;margin-bottom:8px;'>🧽 住宿期間房務服務申請</div>");
                sb.append("<div style='font-size:13px;line-height:1.8;color:#555555;margin-bottom:14px;'>");
                sb.append("入住期間如需補充毛巾浴巾、洗沐備品、預約客房清掃或設備報修，請直接登入星澄飯店官方網站會員中心之「訂房清單」即可一鍵新增房務申請，我們的房務團隊將以最快速度為您服務！");
                sb.append("</div>");
                sb.append("<a href='").append(memberBookingUrl).append(
                                "' style='display:inline-block;background-color:#2e7d32;color:#ffffff;font-size:13px;font-weight:bold;padding:9px 20px;border-radius:6px;'>");
                sb.append("🛎️ 前往會員中心申請房務服務");
                sb.append("</a>");
                sb.append("</td></tr></table>");

                // FOOTER GREETING
                sb.append("<div style='text-align:center;margin-top:30px;'>");
                sb.append("<div style='font-size:15px;font-weight:bold;color:#4a3b2a;margin-bottom:8px;'>祝您住宿愉快</div>");
                sb.append("<div style='font-size:13px;line-height:1.7;color:#888888;'>若有任何即時需求，歡迎隨時透過房內分機聯繫總機櫃檯。</div>");
                sb.append("</div>");

                sb.append("</td></tr>");

                // FOOTER BAR
                sb.append("<tr>");
                sb.append("<td style='background-color:#2b2219;padding:25px 20px;text-align:center;'>");
                sb.append("<div style='color:#d4af37;font-size:15px;font-weight:bold;letter-spacing:2px;margin-bottom:8px;'>星澄飯店</div>");
                sb.append("<div style='color:#c8c1b8;font-size:11px;line-height:1.6;'>Grand Aster Hotel & Resorts<br>© 2026 星澄飯店. All rights reserved.</div>");
                sb.append("</td></tr>");

                sb.append("</table>");
                sb.append("</td></tr></table>");
                sb.append("</body></html>");

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
                                + "                  <span style='font-size: 34px; font-weight: bold; letter-spacing: 8px; color: #9b7435; font-family: Consolas, Monaco, monospace;'>"
                                + code + "</span>"
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
                                + "                  <span style='font-size: 34px; font-weight: bold; letter-spacing: 8px; color: #9b7435; font-family: Consolas, Monaco, monospace;'>"
                                + code + "</span>"
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
