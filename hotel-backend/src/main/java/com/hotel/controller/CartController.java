package com.hotel.controller;

import com.hotel.model.dto.AddCartItemRequest;
import com.hotel.model.dto.CartResponse;
import com.hotel.model.dto.MergeCartRequest;
import com.hotel.model.dto.UpdateCartItemRequest;
import com.hotel.service.CartService;
import java.security.Principal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;

  @GetMapping
  public CartResponse getCart(Principal principal) {
    return cartService.getCart(principal.getName());
  }

  @PostMapping("/items")
  public CartResponse addItem(
      @RequestBody AddCartItemRequest request, Principal principal) {
    return cartService.addItem(principal.getName(), request);
  }

  @PutMapping("/items/{productId}")
  public CartResponse updateItem(
      @PathVariable Integer productId,
      @RequestBody UpdateCartItemRequest request,
      Principal principal) {
    return cartService.updateQuantity(principal.getName(), productId, request.getQuantity());
  }

  @DeleteMapping("/items/{productId}")
  public CartResponse removeItem(@PathVariable Integer productId, Principal principal) {
    return cartService.removeItem(principal.getName(), productId);
  }

  @DeleteMapping
  public ResponseEntity<Void> clear(Principal principal) {
    cartService.clear(principal.getName());
    return ResponseEntity.noContent().build();
  }

  @PostMapping("/merge")
  public CartResponse merge(@RequestBody MergeCartRequest request, Principal principal) {
    return cartService.merge(principal.getName(), request != null ? request.getItems() : null);
  }
}
