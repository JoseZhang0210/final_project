package com.hotel.controller;

import com.hotel.model.dto.AddCartItemRequest;
import com.hotel.model.dto.CartResponse;
import com.hotel.model.dto.MergeCartRequest;
import com.hotel.model.dto.UpdateCartItemRequest;
import com.hotel.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
public class CartController {

  private final CartService cartService;

  public CartController(CartService cartService) {
    this.cartService = cartService;
  }

  @GetMapping
  public CartResponse getCart(Authentication authentication) {
    return cartService.getCart(username(authentication));
  }

  @PostMapping("/items")
  public CartResponse addItem(
      @RequestBody AddCartItemRequest request, Authentication authentication) {
    return cartService.addItem(username(authentication), requireBody(request));
  }

  @PutMapping("/items/{productId}")
  public CartResponse updateItem(
      @PathVariable Integer productId,
      @RequestBody UpdateCartItemRequest request,
      Authentication authentication) {
    return cartService.updateQuantity(
        username(authentication), productId, requireBody(request).getQuantity());
  }

  @DeleteMapping("/items/{productId}")
  public CartResponse removeItem(@PathVariable Integer productId, Authentication authentication) {
    return cartService.removeItem(username(authentication), productId);
  }

  @DeleteMapping
  public ResponseEntity<Void> clear(Authentication authentication) {
    cartService.clear(username(authentication));
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/merge")
  public CartResponse merge(@RequestBody MergeCartRequest request, Authentication authentication) {
    return cartService.merge(username(authentication), requireBody(request).getItems());
  }

  private String username(Authentication authentication) {
    if (authentication == null || authentication.getName() == null) {
      throw new IllegalArgumentException("請先登入會員");
    }
    return authentication.getName();
  }

  private <T> T requireBody(T request) {
    if (request == null) {
      throw new IllegalArgumentException("請提供請求內容");
    }
    return request;
  }
}
