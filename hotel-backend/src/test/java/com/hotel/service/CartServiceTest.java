package com.hotel.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.hotel.model.dto.AddCartItemRequest;
import com.hotel.model.entity.Account;
import com.hotel.model.entity.CartItem;
import com.hotel.model.entity.Member;
import com.hotel.model.entity.Product;
import com.hotel.repository.AccountRepository;
import com.hotel.repository.CartItemRepository;
import com.hotel.repository.MemberRepository;
import com.hotel.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CartServiceTest {
  private CartItemRepository carts;
  private ProductRepository products;
  private AccountRepository accounts;
  private MemberRepository members;
  private CartService service;

  @BeforeEach
  void setUp() {
    carts = mock(CartItemRepository.class);
    products = mock(ProductRepository.class);
    accounts = mock(AccountRepository.class);
    members = mock(MemberRepository.class);
    service = new CartService(carts, products, accounts, members);

    Account account = new Account();
    account.setAccountId(1);
    account.setUsername("member");
    when(accounts.findByUsername("member")).thenReturn(account);

    Member member = new Member();
    member.setMemberId(7);
    member.setAccountId(1);
    when(members.findByAccountId(1)).thenReturn(Optional.of(member));

    when(carts.findByMemberIdOrderByCreatedAtAsc(7)).thenReturn(List.of());
  }

  @Test
  void addItemPersistsMemberProductAndQuantity() {
    when(products.findById(3)).thenReturn(Optional.of(product(3, 100, 10, "ACTIVE")));
    when(carts.findByMemberIdAndProductId(7, 3)).thenReturn(Optional.empty());
    AddCartItemRequest request = request(3, 2);
    service.addItem("member", request);
    verify(carts).save(any(CartItem.class));
  }

  @Test
  void addItemRejectsQuantityBeyondStock() {
    when(products.findById(3)).thenReturn(Optional.of(product(3, 100, 1, "ACTIVE")));
    when(carts.findByMemberIdAndProductId(7, 3)).thenReturn(Optional.empty());
    assertThrows(IllegalArgumentException.class, () -> service.addItem("member", request(3, 2)));
  }

  @Test
  void getCartUsesCurrentProductPrice() {
    when(carts.findByMemberIdOrderByCreatedAtAsc(7))
        .thenReturn(List.of(new CartItem(7, 3, 2, null, null)));
    when(products.findAllById(List.of(3))).thenReturn(List.of(product(3, 150, 5, "ACTIVE")));
    var result = service.getCart("member");
    assertEquals(300, result.getTotalAmount());
    assertEquals(2, result.getTotalQuantity());
  }

  private AddCartItemRequest request(int productId, int quantity) {
    AddCartItemRequest request = new AddCartItemRequest();
    request.setProductId(productId);
    request.setQuantity(quantity);
    return request;
  }

  private Product product(int id, int price, int stock, String status) {
    Product product = new Product();
    product.setProductId(id);
    product.setProductName("商品");
    product.setPrice(price);
    product.setStock(stock);
    product.setStatus(status);
    return product;
  }
}
