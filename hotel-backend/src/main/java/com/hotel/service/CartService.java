package com.hotel.service;

import com.hotel.model.dto.AddCartItemRequest;
import com.hotel.model.dto.CartItemResponse;
import com.hotel.model.dto.CartResponse;
import com.hotel.model.entity.CartItem;
import com.hotel.model.entity.Product;
import com.hotel.repository.CartItemRepository;
import com.hotel.repository.ProductRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartService {

  private final CartItemRepository cartItemRepository;
  private final ProductRepository productRepository;
  private final MemberIdentityService memberIdentityService;

  @Transactional(readOnly = true)
  public CartResponse getCart(String username) {
    Integer memberId = memberIdentityService.requireMemberId(username);
    return buildResponse(memberId);
  }

  @Transactional
  public CartResponse addItem(String username, AddCartItemRequest request) {
    Integer memberId = memberIdentityService.requireMemberId(username);
    validateRequest(request);

    Product product = requirePurchasableProduct(request.getProductId());
    CartItem item =
        cartItemRepository
            .findByMemberIdAndProductId(memberId, product.getProductId())
            .orElseGet(() -> new CartItem(memberId, product.getProductId(), 0, null, null));

    int nextQuantity = item.getQuantity() + request.getQuantity();
    validateStock(product, nextQuantity);

    item.setQuantity(nextQuantity);
    cartItemRepository.save(item);
    return buildResponse(memberId);
  }

  @Transactional
  public CartResponse updateQuantity(String username, Integer productId, Integer quantity) {
    Integer memberId = memberIdentityService.requireMemberId(username);
    if (quantity == null || quantity < 1) {
      throw new IllegalArgumentException("購物車商品數量至少為 1");
    }

    Product product = requirePurchasableProduct(productId);
    validateStock(product, quantity);

    CartItem item =
        cartItemRepository
            .findByMemberIdAndProductId(memberId, productId)
            .orElseThrow(() -> new IllegalArgumentException("購物車中找不到此商品"));
    item.setQuantity(quantity);
    cartItemRepository.save(item);
    return buildResponse(memberId);
  }

  @Transactional
  public CartResponse removeItem(String username, Integer productId) {
    Integer memberId = memberIdentityService.requireMemberId(username);
    cartItemRepository.deleteByMemberIdAndProductId(memberId, productId);
    return buildResponse(memberId);
  }

  @Transactional
  public void clear(String username) {
    Integer memberId = memberIdentityService.requireMemberId(username);
    cartItemRepository.deleteByMemberId(memberId);
  }

  @Transactional
  public CartResponse merge(String username, List<AddCartItemRequest> requests) {
    Integer memberId = memberIdentityService.requireMemberId(username);
    if (requests == null || requests.isEmpty()) {
      return buildResponse(memberId);
    }

    Map<Integer, Integer> quantities = new LinkedHashMap<>();
    for (AddCartItemRequest request : requests) {
      validateRequest(request);
      quantities.merge(request.getProductId(), request.getQuantity(), Integer::sum);
    }

    for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {
      Product product = requirePurchasableProduct(entry.getKey());
      if (product.getStock() == null || product.getStock() < 1) {
        throw new IllegalArgumentException(product.getProductName() + "庫存不足");
      }

      CartItem item =
          cartItemRepository
              .findByMemberIdAndProductId(memberId, entry.getKey())
              .orElseGet(() -> new CartItem(memberId, entry.getKey(), 0, null, null));

      int mergedQuantity = Math.min(item.getQuantity() + entry.getValue(), product.getStock());
      item.setQuantity(mergedQuantity);
      cartItemRepository.save(item);
    }

    return buildResponse(memberId);
  }

  private CartResponse buildResponse(Integer memberId) {
    List<CartItem> cartItems = cartItemRepository.findByMemberIdOrderByCreatedAtAsc(memberId);
    List<Integer> productIds = cartItems.stream().map(CartItem::getProductId).toList();
    Map<Integer, Product> products =
        productRepository.findAllById(productIds).stream()
            .collect(Collectors.toMap(Product::getProductId, Function.identity()));

    List<CartItemResponse> items = new ArrayList<>();
    int totalQuantity = 0;
    int totalAmount = 0;

    for (CartItem cartItem : cartItems) {
      Product product = products.get(cartItem.getProductId());
      if (product == null) {
        continue;
      }

      int subtotal = product.getPrice() * cartItem.getQuantity();
      boolean available =
          isActive(product)
              && product.getStock() != null
              && product.getStock() >= cartItem.getQuantity();

      items.add(
          new CartItemResponse(
              product.getProductId(),
              product.getProductName(),
              product.getPrice(),
              cartItem.getQuantity(),
              subtotal,
              product.getStock(),
              product.getImageUrl(),
              product.getStatus(),
              available));
      totalQuantity += cartItem.getQuantity();
      totalAmount += subtotal;
    }

    return new CartResponse(items, totalQuantity, totalAmount);
  }

  private void validateRequest(AddCartItemRequest request) {
    if (request == null || request.getProductId() == null) {
      throw new IllegalArgumentException("商品編號不能為空");
    }
    if (request.getQuantity() == null || request.getQuantity() < 1) {
      throw new IllegalArgumentException("購物車商品數量至少為 1");
    }
  }

  private Product requirePurchasableProduct(Integer productId) {
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("找不到商品"));
    if (!isActive(product)) {
      throw new IllegalArgumentException(product.getProductName() + "目前無法購買");
    }
    return product;
  }

  private void validateStock(Product product, int quantity) {
    if (product.getStock() == null || product.getStock() < quantity) {
      throw new IllegalArgumentException(product.getProductName() + "庫存不足");
    }
  }

  private boolean isActive(Product product) {
    String status = product.getStatus();
    return "ACTIVE".equals(status) || "上架".equals(status) || "上架中".equals(status);
  }
}
