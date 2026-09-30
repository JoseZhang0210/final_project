package com.hotel.service;

import com.hotel.model.dto.AddCartItemRequest;
import com.hotel.model.dto.CartItemResponse;
import com.hotel.model.dto.CartResponse;
import com.hotel.model.entity.Account;
import com.hotel.model.entity.CartItem;
import com.hotel.model.entity.Member;
import com.hotel.model.entity.Product;
import com.hotel.repository.AccountRepository;
import com.hotel.repository.CartItemRepository;
import com.hotel.repository.MemberRepository;
import com.hotel.repository.ProductRepository;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 購物車商業邏輯服務。
 *
 * <p>負責將登入帳號轉換成會員、驗證商品與庫存、維護資料庫中的購物車內容，並依商品目前的價格與庫存組裝前端需要的購物車資料。
 */
@Service
@RequiredArgsConstructor
public class CartService {

  /** 存取會員購物車明細。 */
  private final CartItemRepository cartItemRepository;

  /** 取得商品目前的價格、庫存、圖片與上下架狀態。 */
  private final ProductRepository productRepository;

  /** 依登入名稱查詢帳號。 */
  private final AccountRepository accountRepository;

  /** 將帳號對應到實際會員資料。 */
  private final MemberRepository memberRepository;

  /**
   * 查詢目前登入會員的完整購物車。
   *
   * <p>此操作只讀取資料，因此使用唯讀交易，避免不必要的資料更新檢查。
   */
  @Transactional(readOnly = true)
  public CartResponse getCart(String username) {
    // 不接受前端傳入會員編號，而是由登入帳號解析會員，避免讀取其他會員的購物車。
    return buildResponse(resolveMemberId(username));
  }

  /**
   * 將商品加入購物車。
   *
   * <p>相同商品已存在時累加數量；不存在時建立新明細。累加後的總數量不得超過商品目前庫存。
   */
  @Transactional
  public CartResponse addItem(String username, AddCartItemRequest request) {
    // 先確認操作對象是有效會員，並驗證商品編號及數量格式。
    Integer memberId = resolveMemberId(username);
    validateRequest(request);

    // 只有存在且仍在上架狀態的商品可以加入購物車。
    Product product = requirePurchasableProduct(request.getProductId());

    // 查找會員原有明細；第一次加入此商品時，以數量 0 建立新明細。
    CartItem item =
        cartItemRepository
            .findByMemberIdAndProductId(memberId, product.getProductId())
            .orElseGet(() -> new CartItem(memberId, product.getProductId(), 0, null, null));

    // 新數量等於購物車原數量加上本次加入數量，並以累加後的數量檢查庫存。
    int nextQuantity = item.getQuantity() + request.getQuantity();
    validateStock(product, nextQuantity);

    // 儲存後重新組裝購物車，讓前端立即取得最新總數量與總金額。
    item.setQuantity(nextQuantity);
    cartItemRepository.save(item);
    return buildResponse(memberId);
  }

  /**
   * 將購物車內指定商品改成新的數量。
   *
   * <p>這裡採用覆蓋數量而不是累加數量，適合購物車頁面的數量選擇器使用。
   */
  @Transactional
  public CartResponse updateQuantity(String username, Integer productId, Integer quantity) {
    Integer memberId = resolveMemberId(username);

    // 購物車不保存 0 或負數；若要移除商品應呼叫 removeItem。
    if (quantity == null || quantity < 1) {
      throw new IllegalArgumentException("購物車商品數量至少為 1");
    }

    // 商品必須仍可購買，而且指定的新數量不能超過目前庫存。
    Product product = requirePurchasableProduct(productId);
    validateStock(product, quantity);

    // 僅能修改購物車中已存在的商品，避免更新操作意外變成新增操作。
    CartItem item =
        cartItemRepository
            .findByMemberIdAndProductId(memberId, productId)
            .orElseThrow(() -> new IllegalArgumentException("購物車中找不到此商品"));

    // 覆蓋數量並回傳重新計算後的購物車。
    item.setQuantity(quantity);
    cartItemRepository.save(item);
    return buildResponse(memberId);
  }

  /** 移除目前會員購物車中的單一商品，並回傳移除後的購物車。 */
  @Transactional
  public CartResponse removeItem(String username, Integer productId) {
    Integer memberId = resolveMemberId(username);

    // Repository 同時使用 memberId 與 productId，確保不會刪除其他會員的明細。
    cartItemRepository.deleteByMemberIdAndProductId(memberId, productId);
    return buildResponse(memberId);
  }

  /** 清空目前登入會員的所有購物車明細，通常於結帳成功後或使用者手動清空時呼叫。 */
  @Transactional
  public void clear(String username) {
    cartItemRepository.deleteByMemberId(resolveMemberId(username));
  }

  /**
   * 將前端尚未登入時保存的多筆購物車資料合併到會員購物車。
   *
   * <p>同一商品在傳入清單中出現多次時會先合併數量，再與資料庫中既有數量相加。結果超過庫存時，以目前庫存作為上限。
   */
  @Transactional
  public CartResponse merge(String username, List<AddCartItemRequest> requests) {
    Integer memberId = resolveMemberId(username);

    // 沒有待合併資料時不寫入資料庫，直接回傳會員目前的購物車。
    if (requests == null || requests.isEmpty()) {
      return buildResponse(memberId);
    }

    // 使用 LinkedHashMap 合併重複商品，同時保留前端傳入商品的原始順序。
    Map<Integer, Integer> quantities = new LinkedHashMap<>();
    for (AddCartItemRequest request : requests) {
      validateRequest(request);
      quantities.merge(request.getProductId(), request.getQuantity(), Integer::sum);
    }

    // 逐項把訪客購物車數量合併到會員原有的購物車。
    for (Map.Entry<Integer, Integer> entry : quantities.entrySet()) {
      Product product = requirePurchasableProduct(entry.getKey());

      // 完全沒有庫存的商品不能被合併進購物車。
      if (product.getStock() == null || product.getStock() < 1) {
        throw new IllegalArgumentException(product.getProductName() + "庫存不足");
      }

      // 資料庫已有相同商品時沿用原明細，否則建立數量為 0 的新明細。
      CartItem item =
          cartItemRepository
              .findByMemberIdAndProductId(memberId, entry.getKey())
              .orElseGet(() -> new CartItem(memberId, entry.getKey(), 0, null, null));

      // 合併後數量不得超過目前庫存；超過時自動縮減為可購買的最大數量。
      int mergedQuantity = Math.min(item.getQuantity() + entry.getValue(), product.getStock());
      item.setQuantity(mergedQuantity);
      cartItemRepository.save(item);
    }

    // 所有明細完成合併後，再統一計算並回傳購物車結果。
    return buildResponse(memberId);
  }

  /**
   * 依 Spring Security 提供的登入名稱取得會員編號。
   *
   * <p>帳號存在但沒有對應會員資料時拒絕購物車操作，例如純員工或管理員帳號。
   */
  private Integer resolveMemberId(String username) {
    // Authentication 未建立或名稱為空時，無法確認購物車所有者。
    if (username == null || username.isBlank()) {
      throw new IllegalArgumentException("無法取得目前登入帳號");
    }

    // 去除登入名稱前後空白，再查詢帳號主檔。
    Account account = accountRepository.findByUsername(username.trim());
    if (account == null) {
      throw new IllegalArgumentException("找不到登入帳號");
    }

    // 透過 accountId 查找會員，最後只回傳後續查詢所需的 memberId。
    return memberRepository
        .findByAccountId(account.getAccountId())
        .map(Member::getMemberId)
        .orElseThrow(() -> new IllegalArgumentException("目前登入帳號不是會員"));
  }

  /**
   * 將資料庫購物車明細轉換成前端需要的完整回應。
   *
   * <p>購物車只保存商品編號與數量；價格、庫存、圖片及狀態均以商品資料表的最新內容為準。
   */
  private CartResponse buildResponse(Integer memberId) {
    // 依加入時間排序讀取購物車，維持前端顯示順序穩定。
    List<CartItem> cartItems = cartItemRepository.findByMemberIdOrderByCreatedAtAsc(memberId);

    // 一次批次查詢所有商品，避免逐筆查詢造成 N+1 資料庫效能問題。
    Map<Integer, Product> products =
        productRepository.findAllById(cartItems.stream().map(CartItem::getProductId).toList())
            .stream().collect(Collectors.toMap(Product::getProductId, Function.identity()));

    // 將每筆明細轉換成回應 DTO；商品已被刪除時 toResponse 會回傳 null 並在此排除。
    List<CartItemResponse> items =
        cartItems.stream()
            .map(item -> toResponse(item, products.get(item.getProductId())))
            .filter(Objects::nonNull)
            .toList();

    // 使用有效明細計算購物車總件數及總金額。
    int totalQuantity = items.stream().mapToInt(CartItemResponse::getQuantity).sum();
    int totalAmount = items.stream().mapToInt(CartItemResponse::getSubtotal).sum();
    return new CartResponse(items, totalQuantity, totalAmount);
  }

  /** 將單筆購物車 Entity 與最新商品資料組合成前端回應 DTO。 */
  private CartItemResponse toResponse(CartItem cartItem, Product product) {
    // 商品資料不存在時無法顯示名稱與價格，因此略過這筆失效明細。
    if (product == null) return null;

    // 小計永遠以商品目前價格乘以購物車數量重新計算，不採用前端傳入金額。
    int subtotal = product.getPrice() * cartItem.getQuantity();

    // 商品必須仍上架且庫存足夠，前端才可將此明細視為可結帳。
    boolean available =
        isActive(product)
            && product.getStock() != null
            && product.getStock() >= cartItem.getQuantity();

    // 回傳商品顯示資訊、數量、即時庫存及可購買狀態。
    return new CartItemResponse(
        product.getProductId(),
        product.getProductName(),
        product.getPrice(),
        cartItem.getQuantity(),
        subtotal,
        product.getStock(),
        product.getImageUrl(),
        product.getStatus(),
        available);
  }

  /** 驗證新增或合併購物車時的商品編號與數量格式。 */
  private void validateRequest(AddCartItemRequest request) {
    // 請求本身及商品編號都必須存在，否則無法確認要操作的商品。
    if (request == null || request.getProductId() == null) {
      throw new IllegalArgumentException("商品編號不能為空");
    }

    // 數量至少為 1，避免建立沒有實際意義的零數量或負數明細。
    if (request.getQuantity() == null || request.getQuantity() < 1) {
      throw new IllegalArgumentException("購物車商品數量至少為 1");
    }
  }

  /** 查詢商品並確認商品目前允許購買。 */
  private Product requirePurchasableProduct(Integer productId) {
    // 商品編號必須對應到實際存在的商品資料。
    Product product =
        productRepository
            .findById(productId)
            .orElseThrow(() -> new IllegalArgumentException("找不到商品"));

    // 下架或非可銷售狀態的商品不能新增、更新或合併進購物車。
    if (!isActive(product)) {
      throw new IllegalArgumentException(product.getProductName() + "目前無法購買");
    }
    return product;
  }

  /** 確認商品庫存足以供應要求的購物車總數量。 */
  private void validateStock(Product product, int quantity) {
    // null 庫存視為無庫存；庫存小於需求量時拒絕操作。
    if (product.getStock() == null || product.getStock() < quantity) {
      throw new IllegalArgumentException(product.getProductName() + "庫存不足");
    }
  }

  /** 判斷商品狀態是否屬於專案目前支援的可銷售狀態。 */
  private boolean isActive(Product product) {
    String status = product.getStatus();

    // 同時相容英文狀態及資料庫中既有的中文上架狀態。
    return "ACTIVE".equals(status) || "上架".equals(status) || "上架中".equals(status);
  }
}
