package com.hotel.repository;

import com.hotel.model.entity.CartItem;
import com.hotel.model.entity.CartItemId;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {

  List<CartItem> findByMemberIdOrderByCreatedAtAsc(Integer memberId);

  Optional<CartItem> findByMemberIdAndProductId(Integer memberId, Integer productId);

  void deleteByMemberIdAndProductId(Integer memberId, Integer productId);

  void deleteByMemberId(Integer memberId);
}
