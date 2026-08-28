package com.hotel.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hotel.model.dto.CreateOrderItemRequest;
import com.hotel.model.dto.OrderDTO;
import com.hotel.model.dto.OrderItemDTO;
import com.hotel.model.entity.CartItem;
import com.hotel.model.entity.CustomerOrder;
import com.hotel.model.entity.Member;
import com.hotel.model.entity.OrderItem;
import com.hotel.model.entity.OrderItemId;
import com.hotel.model.entity.Product;
import com.hotel.model.entity.Profile;

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

        // =====================================================
        // Constructor
        // =====================================================

        public OrderService(
                        CustomerOrderRepository customerOrderRepository,
                        OrderItemRepository orderItemRepository,
                        ProductRepository productRepository,
                        MemberRepository memberRepository,
                        ProfileRepository profileRepository) {

                this.customerOrderRepository = customerOrderRepository;
                this.orderItemRepository = orderItemRepository;
                this.productRepository = productRepository;
                this.memberRepository = memberRepository;
                this.profileRepository = profileRepository;
        }

        // =====================================================
        // 1. 查詢全部訂單
        // =====================================================

        public List<CustomerOrder> getAllOrders() {

                return customerOrderRepository
                                .findAllByOrderByOrderDateDesc();
        }

        // =====================================================
        // 2. 查詢單筆訂單
        // =====================================================

        public CustomerOrder getOrderById(Integer orderId) {

                return customerOrderRepository
                                .findById(orderId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "找不到訂單"));
        }

        // =====================================================
        // 3. 查詢某張訂單的商品
        // =====================================================

        public List<OrderItem> getOrderItems(Integer orderId) {

                return orderItemRepository
                                .findByOrderId(orderId);
        }

        // =====================================================
        // 4. 建立訂單
        //
        // 一張 order
        // 可以有多筆 order_item
        // =====================================================

        @Transactional
        public CustomerOrder createOrder(
                        Integer memberId,
                        List<CreateOrderItemRequest> items) {

                // ==========================
                // 檢查會員
                // ==========================

                if (memberId == null) {
                        throw new IllegalArgumentException(
                                        "會員資料不能為空");
                }

                // 確認會員真的存在
                if (!memberRepository.existsById(memberId)) {
                        throw new IllegalArgumentException(
                                        "找不到會員");
                }

                // ==========================
                // 檢查訂單商品
                // ==========================

                if (items == null || items.isEmpty()) {
                        throw new IllegalArgumentException(
                                        "訂單不能沒有商品");
                }

                // ==========================
                // 先檢查所有商品與庫存
                // ==========================

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
                                        .findById(requestItem.getProductId())
                                        .orElseThrow(() -> new IllegalArgumentException(
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
                }

                // ==========================
                // 建立訂單主檔
                // ==========================

                CustomerOrder order = new CustomerOrder();

                order.setMemberId(memberId);

                order.setOrderDate(
                                java.time.LocalDateTime.now());

                // 尚未完成
                order.setOrdered(false);

                // 尚未付款
                order.setPaymentId(null);

                // 新訂單預設待處理
                order.setOrderStatus("PENDING");

                order = customerOrderRepository.save(order);

                // ==========================
                // 建立多筆 order_item
                // ==========================

                for (CreateOrderItemRequest requestItem : items) {

                        Product product = productRepository
                                        .findById(
                                                        requestItem.getProductId())
                                        .orElseThrow(() -> new IllegalArgumentException(
                                                        "找不到商品"));

                        Integer quantity = requestItem.getQuantity();

                        OrderItem item = new OrderItem();

                        item.setOrderId(
                                        order.getOrderId());

                        item.setProductId(
                                        product.getProductId());

                        item.setQuantity(quantity);

                        orderItemRepository.save(item);

                        // ==========================
                        // 扣庫存
                        // ==========================

                        product.setStock(
                                        product.getStock()
                                                        - quantity);

                        if (product.getStock() == 0) {
                                product.setStatus(
                                                "OUT_OF_STOCK");
                        }

                        productRepository.save(product);
                }

                return order;
        }

        // =====================================================
        // 5. Vue 後台訂單 DTO
        //
        // order
        // ↓
        // member
        // ↓
        // profile
        //
        // 同時取得：
        // order_item
        // ↓
        // product
        // =====================================================

        public List<OrderDTO> getAllOrderDTOs() {

                List<CustomerOrder> orders = customerOrderRepository
                                .findAllByOrderByOrderDateDesc();

                List<OrderDTO> result = new ArrayList<>();

                for (CustomerOrder order : orders) {

                        // =================================================
                        // 會員資料
                        // =================================================

                        String memberName = "查無會員資料";

                        String memberPhone = "";

                        String memberEmail = "";

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

                        // =================================================
                        // 訂單商品
                        // =================================================

                        List<OrderItem> orderItems = orderItemRepository
                                        .findByOrderId(
                                                        order.getOrderId());

                        List<OrderItemDTO> itemDTOs = new ArrayList<>();

                        int totalAmount = 0;

                        for (OrderItem item : orderItems) {

                                Product product = productRepository
                                                .findById(
                                                                item.getProductId())
                                                .orElse(null);

                                if (product == null) {
                                        continue;
                                }

                                int price = product.getPrice();

                                int quantity = item.getQuantity();

                                int subtotal = price * quantity;

                                totalAmount += subtotal;

                                OrderItemDTO itemDTO = new OrderItemDTO(
                                                product.getProductId(),
                                                product.getProductName(),
                                                price,
                                                quantity,
                                                subtotal);

                                itemDTOs.add(itemDTO);
                        }

                        // =================================================
                        // 建立 DTO
                        // =================================================

                        OrderDTO dto = new OrderDTO(
                                        order.getOrderId(),

                                        memberName,
                                        memberPhone,
                                        memberEmail,

                                        totalAmount,

                                        order.getOrderStatus(),

                                        order.getOrderDate(),

                                        itemDTOs);

                        result.add(dto);
                }

                return result;
        }

        // =====================================================
        // 6. 修改訂單商品數量
        // =====================================================

        @Transactional
        public void updateOrderItemQuantity(
                        Integer orderId,
                        Integer productId,
                        Integer quantity) {

                if (quantity == null || quantity < 1) {
                        throw new IllegalArgumentException(
                                        "數量至少為 1");
                }

                CustomerOrder order = getOrderById(orderId);

                if ("COMPLETED".equals(
                                order.getOrderStatus())
                                ||
                                "CANCELLED".equals(
                                                order.getOrderStatus())) {

                        throw new IllegalArgumentException(
                                        "此訂單目前無法修改");
                }

                OrderItemId id = new OrderItemId(
                                orderId,
                                productId);

                OrderItem item = orderItemRepository
                                .findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "找不到訂單商品"));

                Product product = productRepository
                                .findById(productId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "找不到商品"));

                int oldQuantity = item.getQuantity();

                int difference = quantity - oldQuantity;

                // 數量增加
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

                // 數量減少
                if (difference < 0) {

                        product.setStock(
                                        product.getStock()
                                                        + Math.abs(difference));
                }

                // 商品狀態
                if (product.getStock() == 0) {

                        product.setStatus(
                                        "OUT_OF_STOCK");

                } else if ("OUT_OF_STOCK".equals(
                                product.getStatus())) {

                        product.setStatus(
                                        "ACTIVE");
                }

                item.setQuantity(quantity);

                orderItemRepository.save(item);

                productRepository.save(product);
        }

        // =====================================================
        // 7. 刪除訂單商品
        // =====================================================

        @Transactional
        public void deleteOrderItem(
                        Integer orderId,
                        Integer productId) {

                CustomerOrder order = getOrderById(orderId);

                if ("COMPLETED".equals(
                                order.getOrderStatus())
                                ||
                                "CANCELLED".equals(
                                                order.getOrderStatus())) {

                        throw new IllegalArgumentException(
                                        "此訂單目前無法修改");
                }

                OrderItemId id = new OrderItemId(
                                orderId,
                                productId);

                OrderItem item = orderItemRepository
                                .findById(id)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "找不到訂單商品"));

                Product product = productRepository
                                .findById(productId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "找不到商品"));

                // 庫存補回
                product.setStock(
                                product.getStock()
                                                + item.getQuantity());

                if ("OUT_OF_STOCK".equals(
                                product.getStatus())) {

                        product.setStatus(
                                        "ACTIVE");
                }

                productRepository.save(product);

                orderItemRepository.delete(item);
        }

        // =====================================================
        // 8. 管理員確認訂單
        //
        // PENDING → CONFIRMED
        // =====================================================

        @Transactional
        public void confirmOrder(Integer orderId) {

                CustomerOrder order = getOrderById(orderId);

                if (!"PENDING".equals(
                                order.getOrderStatus())) {

                        throw new IllegalArgumentException(
                                        "只有待處理訂單可以確認");
                }

                order.setOrderStatus(
                                "CONFIRMED");

                customerOrderRepository.save(order);
        }

        // =====================================================
        // 9. 設定已付款
        //
        // CONFIRMED → PAID
        // =====================================================

        @Transactional
        public void markOrderPaid(Integer orderId) {

                CustomerOrder order = getOrderById(orderId);

                if (!"CONFIRMED".equals(
                                order.getOrderStatus())) {

                        throw new IllegalArgumentException(
                                        "只有已確認訂單可以設定為已付款");
                }

                // 期中作業先不串真正金流
                // paymentId 欄位仍然保留

                order.setOrderStatus(
                                "PAID");

                customerOrderRepository.save(order);
        }

        // =====================================================
        // 10. 設定已出貨
        //
        // PAID → SHIPPED
        // =====================================================

        @Transactional
        public void shipOrder(Integer orderId) {

                CustomerOrder order = getOrderById(orderId);

                if (!"PAID".equals(
                                order.getOrderStatus())) {

                        throw new IllegalArgumentException(
                                        "只有已付款訂單可以出貨");
                }

                order.setOrderStatus(
                                "SHIPPED");

                customerOrderRepository.save(order);
        }

        // =====================================================
        // 11. 完成訂單
        //
        // SHIPPED → COMPLETED
        // =====================================================

        @Transactional
        public void completeOrder(Integer orderId) {

                CustomerOrder order = getOrderById(orderId);

                if (!"SHIPPED".equals(
                                order.getOrderStatus())) {

                        throw new IllegalArgumentException(
                                        "只有已出貨訂單可以完成");
                }

                order.setOrderStatus(
                                "COMPLETED");

                order.setOrdered(true);

                customerOrderRepository.save(order);
        }

        // =====================================================
        // 12. 取消訂單
        // =====================================================

        @Transactional
        public void cancelOrder(Integer orderId) {

                CustomerOrder order = getOrderById(orderId);

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

                List<OrderItem> items = orderItemRepository
                                .findByOrderId(orderId);

                // ==========================
                // 補回庫存
                // ==========================

                for (OrderItem item : items) {

                        Product product = productRepository
                                        .findById(
                                                        item.getProductId())
                                        .orElse(null);

                        if (product == null) {
                                continue;
                        }

                        product.setStock(
                                        product.getStock()
                                                        + item.getQuantity());

                        if ("OUT_OF_STOCK".equals(
                                        product.getStatus())) {

                                product.setStatus(
                                                "ACTIVE");
                        }

                        productRepository.save(product);
                }

                order.setOrderStatus(
                                "CANCELLED");

                customerOrderRepository.save(order);
        }

        // =====================================================
        // 13. 清除測試訂單
        // =====================================================

        @Transactional
        public void clearAllOrders() {

                orderItemRepository.deleteAll();

                customerOrderRepository.deleteAll();
        }

        // =====================================================
        // 14. 判斷商品是否可以購買
        // =====================================================

        private boolean isProductActive(
                        Product product) {

                String status = product.getStatus();

                return "ACTIVE".equals(status)
                                || "上架".equals(status)
                                || "上架中".equals(status);
        }

        public void confirmCheckout(Integer id) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'confirmCheckout'");
        }

        public CustomerOrder createOrder(String customerName, String phone, String roomNumber, String note,
                        List<CartItem> cart) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'createOrder'");
        }

        @Transactional
        public void updateOrderStatus(
                        Integer orderId,
                        String status) {

                CustomerOrder order = getOrderById(orderId);

                if (!"PENDING".equals(status) &&
                                !"COMPLETED".equals(status) &&
                                !"CANCELLED".equals(status)) {
                        throw new IllegalArgumentException(
                                        "不支援的訂單狀態");
                }

                order.setOrderStatus(status);

                // 已完成
                if ("COMPLETED".equals(status)) {
                        order.setOrdered(true);
                } else {
                        order.setOrdered(false);
                }

                customerOrderRepository.save(order);
        }

        public List<OrderDTO> getOrdersByMemberId(
                        Integer memberId) {

                List<CustomerOrder> orders = customerOrderRepository
                                .findByMemberIdOrderByOrderDateDesc(
                                                memberId);

                List<OrderDTO> result = new ArrayList<>();

                for (CustomerOrder order : orders) {

                        List<OrderItem> orderItems = orderItemRepository
                                        .findByOrderId(
                                                        order.getOrderId());

                        List<OrderItemDTO> itemDTOs = new ArrayList<>();

                        int totalAmount = 0;

                        for (OrderItem item : orderItems) {

                                Product product = productRepository
                                                .findById(
                                                                item.getProductId())
                                                .orElse(null);

                                if (product == null) {
                                        continue;
                                }

                                int price = product.getPrice();

                                int quantity = item.getQuantity();

                                int subtotal = price * quantity;

                                totalAmount += subtotal;

                                itemDTOs.add(
                                                new OrderItemDTO(
                                                                product.getProductId(),
                                                                product.getProductName(),
                                                                price,
                                                                quantity,
                                                                subtotal));
                        }

                        OrderDTO dto = new OrderDTO(
                                        order.getOrderId(),

                                        null,
                                        null,
                                        null,

                                        totalAmount,
                                        order.getOrderStatus(),
                                        order.getOrderDate(),
                                        itemDTOs);

                        result.add(dto);
                }

                return result;
        }
}