package com.hotel.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.dto.CreateOrderItemRequest;
import com.hotel.model.dto.OrderDTO;
import com.hotel.model.dto.OrderItemDTO;
import com.hotel.model.entity.Coupon;
import com.hotel.model.entity.CustomerOrder;
import com.hotel.model.entity.Member;
import com.hotel.model.entity.OrderItem;
import com.hotel.model.entity.OrderItemId;
import com.hotel.model.entity.Product;
import com.hotel.model.entity.Profile;
import com.hotel.model.entity.Payment;
import com.hotel.repository.PaymentRepository;
import com.hotel.repository.CouponRepository;
import com.hotel.repository.CustomerOrderRepository;
import com.hotel.repository.MemberRepository;
import com.hotel.repository.OrderItemRepository;
import com.hotel.repository.ProductRepository;
import com.hotel.repository.ProfileRepository;

@Service
public class OrderService {

        private final CustomerOrderRepository customerOrderRepository;
        private final OrderItemRepository orderItemRepository;
        private final ProductRepository productRepository;
        private final MemberRepository memberRepository;
        private final ProfileRepository profileRepository;
        private final CouponRepository couponRepository;
        private final PaymentRepository paymentRepository;

        // =====================================================
        // Constructor
        // =====================================================

        public OrderService(
                        CustomerOrderRepository customerOrderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository,
                        MemberRepository memberRepository,
                        ProfileRepository profileRepository,
                        CouponRepository couponRepository,
                        PaymentRepository paymentRepository) {

                this.customerOrderRepository = customerOrderRepository;
                this.orderItemRepository = orderItemRepository;
                this.productRepository = productRepository;
                this.memberRepository = memberRepository;
                this.profileRepository = profileRepository;
                this.couponRepository = couponRepository;
                this.paymentRepository = paymentRepository;
        }

        // =====================================================
        // 1. 查詢全部訂單 Entity
        // =====================================================

        public List<CustomerOrder> getAllOrders() {

                return customerOrderRepository
                                .findAllByOrderByOrderDateDesc();
        }

        // =====================================================
        // 2. 查詢單筆訂單 Entity
        // =====================================================

        public CustomerOrder getOrderById(
                        Integer orderId) {

                return customerOrderRepository
                                .findById(orderId)
                                .orElseThrow(
                                                () -> new IllegalArgumentException(
                                                                "找不到訂單"));
        }

        // =====================================================
        // 3. 查詢單筆訂單 DTO
        //
        // PaymentView 使用
        // GET /api/orders/{id}
        // =====================================================

        public OrderDTO getOrderDTOById(
                        Integer orderId) {

                CustomerOrder order = customerOrderRepository
                                .findById(orderId)
                                .orElse(null);

                if (order == null) {
                        return null;
                }

                return convertToDTO(order);
        }

        // =====================================================
        // 4. 查詢某張訂單商品
        // =====================================================

        public List<OrderItem> getOrderItems(
                        Integer orderId) {

                return orderItemRepository
                                .findByOrderId(orderId);
        }

        // =====================================================
        // 5. 建立訂單
        //
        // 商品
        // ↓
        // 計算 originalAmount
        // ↓
        // 驗證 Coupon
        // ↓
        // 計算 discountAmount
        // ↓
        // 計算 finalAmount
        // ↓
        // 建立 Order
        // ↓
        // 建立 OrderItem
        // ↓
        // 扣庫存
        // =====================================================

        @Transactional
        public CustomerOrder createOrder(
                        Integer memberId,
                        String couponCode,
                        List<CreateOrderItemRequest> items) {

                // ==============================
                // 會員檢查
                // ==============================

                if (memberId == null
                                || memberId <= 0) {

                        throw new IllegalArgumentException(
                                        "會員資料不能為空");
                }

                if (!memberRepository.existsById(memberId)) {

                        throw new IllegalArgumentException(
                                        "找不到會員");
                }

                // ==============================
                // 商品檢查
                // ==============================

                if (items == null
                                || items.isEmpty()) {

                        throw new IllegalArgumentException(
                                        "訂單不能沒有商品");
                }

                int originalAmount = 0;

                // ==============================
                // 先確認商品、數量、庫存
                // ==============================

                for (CreateOrderItemRequest requestItem : items) {

                        if (requestItem.getProductId() == null) {

                                throw new IllegalArgumentException(
                                                "商品編號不能為空");
                        }

                        if (requestItem.getQuantity() == null
                                        || requestItem.getQuantity() < 1) {

                                throw new IllegalArgumentException(
                                                "購買數量至少為 1");
                        }

                        Product product = productRepository
                                        .findById(
                                                        requestItem.getProductId())
                                        .orElseThrow(
                                                        () -> new IllegalArgumentException(
                                                                        "找不到商品"));

                        if (!isProductActive(product)) {

                                throw new IllegalArgumentException(
                                                product.getProductName()
                                                                + "目前無法購買");
                        }

                        if (product.getStock() == null
                                        || product.getStock() < requestItem.getQuantity()) {

                                throw new IllegalArgumentException(
                                                product.getProductName()
                                                                + "庫存不足");
                        }

                        originalAmount += product.getPrice()
                                        * requestItem.getQuantity();
                }

                // ==============================
                // 優惠券
                // ==============================

                Integer couponId = null;

                int discountAmount = 0;

                if (couponCode != null
                                && !couponCode.isBlank()) {

                        Coupon coupon = couponRepository
                                        .findByCouponCode(
                                                        couponCode.trim())
                                        .orElseThrow(
                                                        () -> new IllegalArgumentException(
                                                                        "優惠券不存在"));

                        validateCoupon(
                                        coupon,
                                        originalAmount);

                        discountAmount = calculateDiscount(
                                        coupon,
                                        originalAmount);

                        couponId = coupon.getCouponId();
                }

                // ==============================
                // 最終金額
                // ==============================

                int finalAmount = Math.max(
                                0,
                                originalAmount
                                                - discountAmount);

                // ==============================
                // 建立 Order
                // ==============================

                CustomerOrder order = new CustomerOrder();

                order.setMemberId(
                                memberId);

                order.setOrderDate(
                                LocalDateTime.now());

                order.setOriginalAmount(
                                originalAmount);

                order.setDiscountAmount(
                                discountAmount);

                order.setFinalAmount(
                                finalAmount);

                order.setCouponId(
                                couponId);

                // 尚未付款
                order.setPaymentId(
                                null);

                order.setOrderStatus(
                                "PENDING");

                order = customerOrderRepository
                                .save(order);

                // ==============================
                // 建立 OrderItem + 扣庫存
                // ==============================

                for (CreateOrderItemRequest requestItem : items) {

                        Product product = productRepository
                                        .findById(
                                                        requestItem.getProductId())
                                        .orElseThrow(
                                                        () -> new IllegalArgumentException(
                                                                        "找不到商品"));

                        Integer quantity = requestItem.getQuantity();

                        Integer unitPrice = product.getPrice();

                        Integer subtotal = unitPrice * quantity;

                        OrderItem item = new OrderItem();

                        item.setOrderId(
                                        order.getOrderId());

                        item.setProductId(
                                        product.getProductId());

                        item.setQuantity(
                                        quantity);

                        // 記錄下單當時價格
                        item.setUnitPrice(
                                        unitPrice);

                        item.setSubtotal(
                                        subtotal);

                        orderItemRepository
                                        .save(item);

                        // 扣庫存
                        product.setStock(
                                        product.getStock()
                                                        - quantity);

                        updateProductStockStatus(
                                        product);

                        productRepository
                                        .save(product);
                }

                return order;
        }

        // =====================================================
        // 6. 查詢全部訂單 DTO
        //
        // 後台訂單管理
        // =====================================================

        public List<OrderDTO> getAllOrderDTOs() {

                List<CustomerOrder> orders = customerOrderRepository
                                .findAllByOrderByOrderDateDesc();

                List<OrderDTO> result = new ArrayList<>();

                for (CustomerOrder order : orders) {

                        result.add(
                                        convertToDTO(order));
                }

                return result;
        }

        // =====================================================
        // 7. 查詢會員自己的訂單
        //
        // MyOrdersView 使用
        // =====================================================

        public List<OrderDTO> getOrdersByMemberId(
                        Integer memberId) {

                if (memberId == null
                                || memberId <= 0) {

                        throw new IllegalArgumentException(
                                        "會員編號錯誤");
                }

                if (!memberRepository.existsById(memberId)) {

                        throw new IllegalArgumentException(
                                        "找不到會員");
                }

                List<CustomerOrder> orders = customerOrderRepository
                                .findByMemberIdOrderByOrderDateDesc(
                                                memberId);

                List<OrderDTO> result = new ArrayList<>();

                for (CustomerOrder order : orders) {

                        result.add(
                                        convertToDTO(order));
                }

                return result;
        }

        // =====================================================
        // 8. 修改訂單商品數量
        //
        // 僅 PENDING 可修改
        // =====================================================

        @Transactional
        public void updateOrderItemQuantity(
                        Integer orderId,
                        Integer productId,
                        Integer quantity) {

                if (quantity == null
                                || quantity < 1) {

                        throw new IllegalArgumentException(
                                        "數量至少為 1");
                }

                CustomerOrder order = getOrderById(
                                orderId);

                if (!"PENDING".equals(
                                order.getOrderStatus())) {

                        throw new IllegalArgumentException(
                                        "只有待處理訂單可以修改商品");
                }

                OrderItemId id = new OrderItemId(
                                orderId,
                                productId);

                OrderItem item = orderItemRepository
                                .findById(id)
                                .orElseThrow(
                                                () -> new IllegalArgumentException(
                                                                "找不到訂單商品"));

                Product product = productRepository
                                .findById(productId)
                                .orElseThrow(
                                                () -> new IllegalArgumentException(
                                                                "找不到商品"));

                int oldQuantity = item.getQuantity();

                int difference = quantity
                                - oldQuantity;

                // ==============================
                // 數量增加
                // ==============================

                if (difference > 0) {

                        if (product.getStock() == null
                                        || product.getStock() < difference) {

                                throw new IllegalArgumentException(
                                                "商品庫存不足");
                        }

                        product.setStock(
                                        product.getStock()
                                                        - difference);
                }

                // ==============================
                // 數量減少
                // ==============================

                if (difference < 0) {

                        product.setStock(
                                        product.getStock()
                                                        + Math.abs(
                                                                        difference));
                }

                updateProductStockStatus(
                                product);

                // ==============================
                // 更新 OrderItem
                // ==============================

                item.setQuantity(
                                quantity);

                item.setSubtotal(
                                item.getUnitPrice()
                                                * quantity);

                orderItemRepository
                                .save(item);

                productRepository
                                .save(product);

                // ==============================
                // 重新計算訂單金額
                // ==============================

                recalculateOrderAmount(
                                order);
        }

        // =====================================================
        // 9. 刪除訂單商品
        //
        // 僅 PENDING 可修改
        // =====================================================

        @Transactional
        public void deleteOrderItem(
                        Integer orderId,
                        Integer productId) {

                CustomerOrder order = getOrderById(
                                orderId);

                if (!"PENDING".equals(
                                order.getOrderStatus())) {

                        throw new IllegalArgumentException(
                                        "只有待處理訂單可以修改商品");
                }

                OrderItemId id = new OrderItemId(
                                orderId,
                                productId);

                OrderItem item = orderItemRepository
                                .findById(id)
                                .orElseThrow(
                                                () -> new IllegalArgumentException(
                                                                "找不到訂單商品"));

                Product product = productRepository
                                .findById(productId)
                                .orElseThrow(
                                                () -> new IllegalArgumentException(
                                                                "找不到商品"));

                // ==============================
                // 庫存補回
                // ==============================

                product.setStock(
                                product.getStock()
                                                + item.getQuantity());

                updateProductStockStatus(
                                product);

                productRepository
                                .save(product);

                orderItemRepository
                                .delete(item);

                // ==============================
                // 是否已無商品
                // ==============================

                List<OrderItem> remainingItems = orderItemRepository
                                .findByOrderId(
                                                orderId);

                if (remainingItems.isEmpty()) {

                        order.setOriginalAmount(
                                        0);

                        order.setDiscountAmount(
                                        0);

                        order.setFinalAmount(
                                        0);

                        order.setCouponId(
                                        null);

                        customerOrderRepository
                                        .save(order);

                        return;
                }

                recalculateOrderAmount(
                                order);
        }

        // =====================================================
        // 10. 取消訂單
        //
        // 取消後補回庫存
        // =====================================================

        @Transactional
        public void cancelOrder(
                        Integer orderId) {

                CustomerOrder order = getOrderById(
                                orderId);

                if ("COMPLETED".equals(
                                order.getOrderStatus())) {

                        throw new IllegalArgumentException(
                                        "已完成訂單不能取消");
                }

                if ("CANCELLED".equals(
                                order.getOrderStatus())) {

                        throw new IllegalArgumentException(
                                        "訂單已經取消");
                }

                restoreOrderStock(
                                orderId);

                order.setOrderStatus(
                                "CANCELLED");

                customerOrderRepository
                                .save(order);
        }

        // =====================================================
        // 11. 後台修改訂單狀態
        //
        // PENDING
        // COMPLETED
        // CANCELLED
        // =====================================================

        @Transactional
        public void updateOrderStatus(
                        Integer orderId,
                        String status) {

                CustomerOrder order = getOrderById(
                                orderId);

                if (status == null
                                || status.isBlank()) {

                        throw new IllegalArgumentException(
                                        "訂單狀態不能為空");
                }

                String newStatus = status.trim()
                                .toUpperCase();

                if (!"PENDING".equals(newStatus)
                                && !"COMPLETED".equals(newStatus)
                                && !"CANCELLED".equals(newStatus)) {

                        throw new IllegalArgumentException(
                                        "不支援的訂單狀態");
                }

                String oldStatus = order.getOrderStatus();

                if (newStatus.equals(
                                oldStatus)) {

                        return;
                }

                // ==============================
                // 非取消 → 取消
                // 補回庫存
                // ==============================

                if ("CANCELLED".equals(newStatus)
                                && !"CANCELLED".equals(oldStatus)) {

                        restoreOrderStock(
                                        orderId);
                }

                // ==============================
                // 取消 → 其他
                // 重新扣庫存
                // ==============================

                if ("CANCELLED".equals(oldStatus)
                                && !"CANCELLED".equals(newStatus)) {

                        deductOrderStock(
                                        orderId);
                }

                order.setOrderStatus(
                                newStatus);

                customerOrderRepository
                                .save(order);
        }

        // =====================================================
        // 12. 清除測試訂單
        //
        // 僅測試用
        // =====================================================

        @Transactional
        public void clearAllOrders() {

                orderItemRepository
                                .deleteAll();

                customerOrderRepository
                                .deleteAll();
        }

        // =====================================================
        // 13. 優惠券驗證
        // =====================================================

        private void validateCoupon(
                        Coupon coupon,
                        int originalAmount) {

                if (!"ACTIVE".equals(
                                coupon.getStatus())) {

                        throw new IllegalArgumentException(
                                        "優惠券目前未啟用");
                }

                LocalDateTime now = LocalDateTime.now();

                if (now.isBefore(
                                coupon.getStartDate())) {

                        throw new IllegalArgumentException(
                                        "優惠券尚未開始使用");
                }

                if (now.isAfter(
                                coupon.getEndDate())) {

                        throw new IllegalArgumentException(
                                        "優惠券已過期");
                }

                if (originalAmount < coupon.getMinimumAmount()) {

                        throw new IllegalArgumentException(
                                        "未達優惠券最低消費金額");
                }
        }

        // =====================================================
        // 14. 計算優惠券折扣
        // =====================================================

        private int calculateDiscount(
                        Coupon coupon,
                        int originalAmount) {

                int discountAmount;

                // 百分比
                if ("PERCENT".equals(
                                coupon.getDiscountType())) {

                        discountAmount = originalAmount
                                        * coupon.getDiscountValue()
                                        / 100;
                }

                // 固定金額
                else if ("FIXED".equals(
                                coupon.getDiscountType())) {

                        discountAmount = coupon.getDiscountValue();
                }

                else {

                        throw new IllegalArgumentException(
                                        "優惠券折扣類型錯誤");
                }

                return Math.min(
                                discountAmount,
                                originalAmount);
        }

        // =====================================================
        // 15. 修改商品後重新計算訂單金額
        // =====================================================

        private void recalculateOrderAmount(
                        CustomerOrder order) {

                List<OrderItem> items = orderItemRepository
                                .findByOrderId(
                                                order.getOrderId());

                int originalAmount = items.stream()
                                .mapToInt(
                                                OrderItem::getSubtotal)
                                .sum();

                int discountAmount = 0;

                // ==============================
                // 原訂單有優惠券
                // ==============================

                if (order.getCouponId() != null) {

                        Coupon coupon = couponRepository
                                        .findById(
                                                        order.getCouponId())
                                        .orElse(null);

                        if (coupon != null) {

                                boolean couponStillValid = "ACTIVE".equals(
                                                coupon.getStatus())
                                                && !LocalDateTime.now()
                                                                .isBefore(
                                                                                coupon.getStartDate())
                                                && !LocalDateTime.now()
                                                                .isAfter(
                                                                                coupon.getEndDate())
                                                && originalAmount >= coupon.getMinimumAmount();

                                if (couponStillValid) {

                                        discountAmount = calculateDiscount(
                                                        coupon,
                                                        originalAmount);

                                } else {

                                        order.setCouponId(
                                                        null);
                                }

                        } else {

                                order.setCouponId(
                                                null);
                        }
                }

                order.setOriginalAmount(
                                originalAmount);

                order.setDiscountAmount(
                                discountAmount);

                order.setFinalAmount(
                                Math.max(
                                                0,
                                                originalAmount
                                                                - discountAmount));

                customerOrderRepository
                                .save(order);
        }

        // =====================================================
        // 16. 取消訂單補回庫存
        // =====================================================

        private void restoreOrderStock(
                        Integer orderId) {

                List<OrderItem> items = orderItemRepository
                                .findByOrderId(
                                                orderId);

                for (OrderItem item : items) {

                        Product product = productRepository
                                        .findById(
                                                        item.getProductId())
                                        .orElse(null);

                        if (product == null) {
                                continue;
                        }

                        int currentStock = product.getStock() == null
                                        ? 0
                                        : product.getStock();

                        product.setStock(
                                        currentStock
                                                        + item.getQuantity());

                        updateProductStockStatus(
                                        product);

                        productRepository
                                        .save(product);
                }
        }

        // =====================================================
        // 17. CANCELLED → 其他狀態
        //
        // 重新扣庫存
        // =====================================================

        private void deductOrderStock(
                        Integer orderId) {

                List<OrderItem> items = orderItemRepository
                                .findByOrderId(
                                                orderId);

                // ==============================
                // 先檢查全部商品庫存
                // ==============================

                for (OrderItem item : items) {

                        Product product = productRepository
                                        .findById(
                                                        item.getProductId())
                                        .orElseThrow(
                                                        () -> new IllegalArgumentException(
                                                                        "找不到商品"));

                        if (product.getStock() == null
                                        || product.getStock() < item.getQuantity()) {

                                throw new IllegalArgumentException(
                                                product.getProductName()
                                                                + "庫存不足，無法恢復訂單");
                        }
                }

                // ==============================
                // 確認後再扣庫存
                // ==============================

                for (OrderItem item : items) {

                        Product product = productRepository
                                        .findById(
                                                        item.getProductId())
                                        .orElseThrow(
                                                        () -> new IllegalArgumentException(
                                                                        "找不到商品"));

                        product.setStock(
                                        product.getStock()
                                                        - item.getQuantity());

                        updateProductStockStatus(
                                        product);

                        productRepository
                                        .save(product);
                }
        }

        // =====================================================
        // 18. 更新商品庫存狀態
        // =====================================================

        private void updateProductStockStatus(
                        Product product) {

                if (product.getStock() != null
                                && product.getStock() <= 0) {

                        product.setStatus(
                                        "OUT_OF_STOCK");

                } else if ("OUT_OF_STOCK".equals(
                                product.getStatus())) {

                        product.setStatus(
                                        "ACTIVE");
                }
        }

        // =====================================================
        // 19. 商品是否可以購買
        // =====================================================

        private boolean isProductActive(
                        Product product) {

                String status = product.getStatus();

                return "ACTIVE".equals(status)
                                || "上架".equals(status)
                                || "上架中".equals(status);
        }

        // =====================================================
        // 20. CustomerOrder → OrderDTO
        //
        // GET 全部訂單
        // GET 單筆訂單
        // GET 我的訂單
        // 全部共用這個方法
        // =====================================================

        private OrderDTO convertToDTO(
                        CustomerOrder order) {

                // =====================================================
                // 1. 會員資料
                // =====================================================

                String memberName = "查無會員資料";

                String memberPhone = "";

                String memberEmail = "";

                if (order.getMemberId() != null) {

                        Member member = memberRepository
                                        .findById(
                                                        order.getMemberId())
                                        .orElse(null);

                        if (member != null) {

                                Profile profile = profileRepository
                                                .findByAccountId(
                                                                member.getAccountId())
                                                .orElse(null);

                                if (profile != null) {

                                        memberName = profile.getName();

                                        memberPhone = profile.getPhone();

                                        memberEmail = profile.getEmail();
                                }
                        }
                }

                // =====================================================
                // 2. 訂單商品
                // =====================================================

                List<OrderItem> orderItems = orderItemRepository
                                .findByOrderId(
                                                order.getOrderId());

                List<OrderItemDTO> itemDTOs = new ArrayList<>();

                for (OrderItem item : orderItems) {

                        Product product = productRepository
                                        .findById(
                                                        item.getProductId())
                                        .orElse(null);

                        if (product == null) {
                                continue;
                        }

                        Integer unitPrice = item.getUnitPrice();

                        Integer quantity = item.getQuantity();

                        Integer subtotal = item.getSubtotal();

                        OrderItemDTO itemDTO = new OrderItemDTO(
                                        item.getProductId(),
                                        product.getProductName(),
                                        unitPrice,
                                        quantity,
                                        subtotal);

                        itemDTOs.add(
                                        itemDTO);
                }

                // =====================================================
                // 3. 付款狀態
                // =====================================================

                String paymentStatus = null;

                if (order.getPaymentId() != null) {

                        Payment payment = paymentRepository
                                        .findById(
                                                        order.getPaymentId())
                                        .orElse(null);

                        if (payment != null) {

                                paymentStatus = payment.getPaymentStatus();
                        }
                }

                // =====================================================
                // 4. 建立 OrderDTO
                // =====================================================

                return new OrderDTO(

                                order.getOrderId(),

                                memberName,
                                memberPhone,
                                memberEmail,

                                order.getOriginalAmount(),
                                order.getDiscountAmount(),
                                order.getFinalAmount(),

                                order.getOrderStatus(),

                                paymentStatus,

                                order.getOrderDate(),

                                itemDTOs);
        }

}