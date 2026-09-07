package com.hotel.service;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.hotel.model.entity.CustomerOrder;
import com.hotel.model.dto.OrderDataExchangeDTO.OrderItemDetailDTO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${mail.from:no-reply@yourdomain.com}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendOrderConfirmation(String to, String subject, String htmlBody) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new IllegalStateException("Failed to send email", e);
        }
    }

    /**
     * Build a simple HTML email body for an order.
     */
    public String buildOrderEmail(CustomerOrder order, List<OrderItemDetailDTO> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("<h2>感謝您的購買！</h2>");
        sb.append("<p>親愛的會員，您好：</p>");
        sb.append("<p>以下是您的訂單資訊：</p>");
        sb.append("<ul>");
        for (OrderItemDetailDTO i : items) {
            sb.append(String.format("<li>%s x %d – NT$%d</li>",
                    i.getProductId(), i.getQuantity(), i.getSubtotal()));
        }
        sb.append("</ul>");
        sb.append(String.format("<p>總金額：NT$%d</p>", order.getFinalAmount()));
        sb.append("<p>期待您再次光臨！</p>");
        return sb.toString();
    }
}
