package com.hotel.service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Objects;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import com.hotel.model.dto.OrderDataExchangeDTO;
import com.hotel.model.dto.OrderDataExchangeDTO.OrderExportItemDTO;
import com.hotel.model.dto.OrderDataExchangeDTO.OrderItemDetailDTO;
import com.hotel.model.entity.Coupon;
import com.hotel.model.entity.CustomerOrder;
import com.hotel.model.entity.OrderItem;
import com.hotel.model.entity.Payment;
import com.hotel.repository.CouponRepository;
import com.hotel.repository.CustomerOrderRepository;
import com.hotel.repository.OrderItemRepository;
import com.hotel.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderDataExchangeService {

    private final CouponRepository couponRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ObjectMapper objectMapper;

    // =========================================================================
    // 1. JSON 匯出全部訂單、付款、優惠券
    // =========================================================================
    @Transactional(readOnly = true)
    public byte[] exportAllToJson() throws Exception {
        List<Coupon> coupons = couponRepository.findAll();
        List<Payment> payments = paymentRepository.findAll();
        List<CustomerOrder> orders = customerOrderRepository.findAll();

        Map<Integer, String> couponCodeMap = coupons.stream()
                .filter(Objects::nonNull)
                .filter(c -> c.getCouponId() != null && c.getCouponCode() != null)
                .collect(Collectors.toMap(c -> c.getCouponId(), c -> c.getCouponCode(), (a, b) -> a));

        Map<Integer, String> paymentTxMap = payments.stream()
                .filter(Objects::nonNull)
                .filter(p -> p.getPaymentId() != null && p.getTransactionId() != null)
                .collect(Collectors.toMap(p -> p.getPaymentId(), p -> p.getTransactionId(), (a, b) -> a));

        List<OrderExportItemDTO> orderDTOs = new ArrayList<>();
        for (CustomerOrder order : orders) {
            List<OrderItem> items = orderItemRepository.findByOrderId(order.getOrderId());
            List<OrderItemDetailDTO> itemDTOs = items.stream()
                    .map(item -> OrderItemDetailDTO.builder()
                            .productId(item.getProductId())
                            .quantity(item.getQuantity())
                            .unitPrice(item.getUnitPrice())
                            .subtotal(item.getSubtotal())
                            .build())
                    .toList();

            orderDTOs.add(OrderExportItemDTO.builder()
                    .orderId(order.getOrderId())
                    .memberId(order.getMemberId())
                    .orderDate(order.getOrderDate())
                    .originalAmount(order.getOriginalAmount())
                    .discountAmount(order.getDiscountAmount())
                    .finalAmount(order.getFinalAmount())
                    .couponId(order.getCouponId())
                    .couponCode(order.getCouponId() != null ? couponCodeMap.get(order.getCouponId()) : null)
                    .paymentId(order.getPaymentId())
                    .paymentTransactionId(order.getPaymentId() != null ? paymentTxMap.get(order.getPaymentId()) : null)
                    .orderStatus(order.getOrderStatus())
                    .items(itemDTOs)
                    .build());
        }

        OrderDataExchangeDTO exchangeDTO = OrderDataExchangeDTO.builder()
                .coupons(coupons)
                .payments(payments)
                .orders(orderDTOs)
                .build();

        return objectMapper.writerWithDefaultPrettyPrinter()
                .without(DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsBytes(exchangeDTO);
    }

    // =========================================================================
    // 2. JSON 匯入全部訂單、付款、優惠券
    // =========================================================================
    @Transactional
    public int importFromJson(InputStream inputStream) throws Exception {
        OrderDataExchangeDTO payload = objectMapper.readValue(inputStream, OrderDataExchangeDTO.class);
        int importedOrderCount = 0;

        // 1. 匯入優惠券
        if (payload.getCoupons() != null) {
            for (Coupon c : payload.getCoupons()) {
                if (c.getCouponCode() == null || c.getCouponCode().isBlank()) {
                    continue;
                }
                Coupon existing = couponRepository.findByCouponCode(c.getCouponCode().trim().toUpperCase())
                        .orElse(null);
                if (existing == null) {
                    Coupon newCoupon = new Coupon();
                    newCoupon.setCouponCode(c.getCouponCode().trim().toUpperCase());
                    newCoupon.setCouponName(c.getCouponName());
                    newCoupon.setDiscountType(c.getDiscountType());
                    newCoupon.setDiscountValue(c.getDiscountValue());
                    newCoupon.setMinimumAmount(c.getMinimumAmount() != null ? c.getMinimumAmount() : 0);
                    newCoupon.setStartDate(c.getStartDate() != null ? c.getStartDate() : LocalDateTime.now());
                    newCoupon.setEndDate(c.getEndDate() != null ? c.getEndDate() : LocalDateTime.now().plusMonths(1));
                    newCoupon.setStatus(c.getStatus() != null ? c.getStatus() : "ACTIVE");
                    couponRepository.save(newCoupon);
                }
            }
        }

        // 2. 匯入付款紀錄
        if (payload.getPayments() != null) {
            for (Payment p : payload.getPayments()) {
                if (p.getTransactionId() == null || p.getTransactionId().isBlank()) {
                    continue;
                }
                Payment existing = paymentRepository.findByTransactionId(p.getTransactionId().trim())
                        .orElse(null);
                if (existing == null) {
                    Payment newPayment = new Payment();
                    newPayment.setMemberId(p.getMemberId());
                    newPayment.setPaymentMethod(p.getPaymentMethod());
                    newPayment.setTransactionId(p.getTransactionId().trim());
                    newPayment.setTotalPrice(p.getTotalPrice() != null ? p.getTotalPrice() : 0);
                    newPayment.setPaymentStatus(p.getPaymentStatus() != null ? p.getPaymentStatus() : "PENDING");
                    newPayment.setPaymentTime(p.getPaymentTime());
                    newPayment.setCreatedAt(p.getCreatedAt() != null ? p.getCreatedAt() : LocalDateTime.now());
                    paymentRepository.save(newPayment);
                }
            }
        }

        // 3. 匯入訂單與明細
        if (payload.getOrders() != null) {
            for (OrderExportItemDTO orderItemDTO : payload.getOrders()) {
                CustomerOrder order = new CustomerOrder();
                order.setMemberId(orderItemDTO.getMemberId() != null ? orderItemDTO.getMemberId() : 1);
                order.setOrderDate(orderItemDTO.getOrderDate() != null ? orderItemDTO.getOrderDate() : LocalDateTime.now());
                order.setOriginalAmount(orderItemDTO.getOriginalAmount() != null ? orderItemDTO.getOriginalAmount() : 0);
                order.setDiscountAmount(orderItemDTO.getDiscountAmount() != null ? orderItemDTO.getDiscountAmount() : 0);
                order.setFinalAmount(orderItemDTO.getFinalAmount() != null ? orderItemDTO.getFinalAmount() : 0);
                order.setOrderStatus(orderItemDTO.getOrderStatus() != null ? orderItemDTO.getOrderStatus() : "PENDING");

                // 解析 couponId
                if (orderItemDTO.getCouponCode() != null && !orderItemDTO.getCouponCode().isBlank()) {
                    couponRepository.findByCouponCode(orderItemDTO.getCouponCode().trim().toUpperCase())
                            .ifPresent(coupon -> order.setCouponId(coupon.getCouponId()));
                } else if (orderItemDTO.getCouponId() != null) {
                    order.setCouponId(orderItemDTO.getCouponId());
                }

                // 解析 paymentId
                if (orderItemDTO.getPaymentTransactionId() != null && !orderItemDTO.getPaymentTransactionId().isBlank()) {
                    paymentRepository.findByTransactionId(orderItemDTO.getPaymentTransactionId().trim())
                            .ifPresent(payment -> order.setPaymentId(payment.getPaymentId()));
                } else if (orderItemDTO.getPaymentId() != null) {
                    order.setPaymentId(orderItemDTO.getPaymentId());
                }

                CustomerOrder savedOrder = customerOrderRepository.save(order);
                importedOrderCount++;

                // 儲存明細
                if (orderItemDTO.getItems() != null) {
                    for (OrderItemDetailDTO detailDTO : orderItemDTO.getItems()) {
                        OrderItem item = new OrderItem();
                        item.setOrderId(savedOrder.getOrderId());
                        item.setProductId(detailDTO.getProductId());
                        item.setQuantity(detailDTO.getQuantity() != null ? detailDTO.getQuantity() : 1);
                        item.setUnitPrice(detailDTO.getUnitPrice() != null ? detailDTO.getUnitPrice() : 0);
                        item.setSubtotal(detailDTO.getSubtotal() != null ? detailDTO.getSubtotal()
                                : item.getQuantity() * item.getUnitPrice());
                        orderItemRepository.save(item);
                    }
                }
            }
        }

        return importedOrderCount;
    }

    // =========================================================================
    // 3. 讀取 resources 下的種子資料 (order-seed-data.json)
    // =========================================================================
    @Transactional
    public int importFromClasspathSeed() {
        try {
            ClassPathResource resource = new ClassPathResource("data/order-seed-data.json");
            if (!resource.exists()) {
                log.warn("種子資料 data/order-seed-data.json 不存在，跳過注入。");
                return 0;
            }
            try (InputStream is = resource.getInputStream()) {
                int count = importFromJson(is);
                log.info("成功由種子資料 JSON 注入 {} 筆訂單紀錄！", count);
                return count;
            }
        } catch (Exception e) {
            log.error("匯入種子資料時發生錯誤: {}", e.getMessage(), e);
            return 0;
        }
    }

    // =========================================================================
    // 4. 優惠券匯出 CSV (Excel 相容)
    // =========================================================================
    @Transactional(readOnly = true)
    public void exportCouponsToCsv(Writer writer) {
        PrintWriter pw = new PrintWriter(writer);
        // UTF-8 BOM 方便 Excel 正確開啟中文不亂碼
        pw.write('\ufeff');
        pw.println("coupon_code,coupon_name,discount_type,discount_value,minimum_amount,start_date,end_date,status");

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<Coupon> coupons = couponRepository.findAll();
        for (Coupon c : coupons) {
            pw.printf("%s,\"%s\",%s,%d,%d,%s,%s,%s%n",
                    c.getCouponCode(),
                    c.getCouponName() != null ? c.getCouponName().replace("\"", "\"\"") : "",
                    c.getDiscountType(),
                    c.getDiscountValue(),
                    c.getMinimumAmount() != null ? c.getMinimumAmount() : 0,
                    c.getStartDate() != null ? c.getStartDate().format(dtf) : "",
                    c.getEndDate() != null ? c.getEndDate().format(dtf) : "",
                    c.getStatus());
        }
        pw.flush();
    }

    // =========================================================================
    // 5. 優惠券由 CSV 批次匯入
    // =========================================================================
    @Transactional
    public int importCouponsFromCsv(InputStream inputStream) throws Exception {
        int count = 0;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            boolean isHeader = true;
            while ((line = reader.readLine()) != null) {
                line = line.replace("\ufeff", "").trim();
                if (line.isBlank()) {
                    continue;
                }
                if (isHeader) {
                    isHeader = false;
                    if (line.toLowerCase().startsWith("coupon_code")) {
                        continue;
                    }
                }

                String[] cols = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (cols.length < 4) {
                    continue;
                }

                String code = cols[0].trim().toUpperCase();
                String name = cols[1].replace("\"", "").trim();
                String discountType = cols[2].trim().toUpperCase();
                int discountValue = Integer.parseInt(cols[3].trim());
                int minAmount = cols.length > 4 && !cols[4].isBlank() ? Integer.parseInt(cols[4].trim()) : 0;
                LocalDateTime startDate = cols.length > 5 && !cols[5].isBlank() ? LocalDateTime.parse(cols[5].trim(), dtf) : LocalDateTime.now();
                LocalDateTime endDate = cols.length > 6 && !cols[6].isBlank() ? LocalDateTime.parse(cols[6].trim(), dtf) : LocalDateTime.now().plusMonths(3);
                String status = cols.length > 7 && !cols[7].isBlank() ? cols[7].trim().toUpperCase() : "ACTIVE";

                Coupon coupon = couponRepository.findByCouponCode(code).orElse(new Coupon());
                coupon.setCouponCode(code);
                coupon.setCouponName(name);
                coupon.setDiscountType(discountType);
                coupon.setDiscountValue(discountValue);
                coupon.setMinimumAmount(minAmount);
                coupon.setStartDate(startDate);
                coupon.setEndDate(endDate);
                coupon.setStatus(status);

                couponRepository.save(coupon);
                count++;
            }
        }
        return count;
    }
}
