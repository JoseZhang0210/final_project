<template>
  <div class="cart-page">
    <div class="cart-container">
      <h1>購物車</h1>

      <!-- 空購物車 -->
      <div
        v-if="cartItems.length === 0"
        class="empty-cart"
      >
        購物車目前沒有商品
      </div>

      <!-- 購物車內容 -->
      <div v-else>
        <div
          v-for="item in cartItems"
          :key="item.productId"
          class="cart-item"
        >
          <!-- 商品資訊 -->
          <div class="item-info">
            <h3>
              {{ item.productName }}
            </h3>

            <p>
              單價：
              NT$
              {{ formatPrice(item.price) }}
            </p>
          </div>

          <!-- 數量 -->
          <div class="quantity-control">
            <button
              type="button"
              :disabled="Number(item.quantity) <= 1"
              @click="decreaseQuantity(item)"
            >
              −
            </button>

            <span>
              {{ item.quantity }}
            </span>

            <button
              type="button"
              :disabled="
                Number(item.quantity) >=
                Number(item.stock)
              "
              @click="increaseQuantity(item)"
            >
              ＋
            </button>
          </div>

          <!-- 小計 -->
          <div class="subtotal">
            小計：
            NT$
            {{
              formatPrice(
                Number(item.price) *
                  Number(item.quantity),
              )
            }}
          </div>

          <!-- 刪除 -->
          <button
            type="button"
            class="delete-button"
            @click="removeItem(item.productId)"
          >
            刪除
          </button>
        </div>

        <!-- 總金額 -->
        <div class="cart-summary">
          <h2>
            總金額：
            NT$
            {{ formatPrice(totalAmount) }}
          </h2>

          <button
            type="button"
            class="checkout-button"
            :disabled="submitting"
            @click="submitOrder"
          >
            {{
              submitting
                ? "建立訂單中..."
                : "確認結帳"
            }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  computed,
  onMounted,
  ref,
} from "vue";

import { useRouter } from "vue-router";

const router = useRouter();

// =====================================================
// 購物車
// =====================================================

const cartItems = ref([]);

// =====================================================
// 是否正在建立訂單
// =====================================================

const submitting = ref(false);

// =====================================================
// JWT Header
// =====================================================

function getAuthHeaders() {
  const token =
    localStorage.getItem("token");

  const headers = {
    "Content-Type": "application/json",
  };

  if (token) {
    headers.Authorization =
      "Bearer " + token;
  }

  return headers;
}

// =====================================================
// 載入購物車
// =====================================================

function loadCart() {
  const savedCart =
    localStorage.getItem("cart");

  if (!savedCart) {
    cartItems.value = [];
    return;
  }

  try {
    const parsedCart =
      JSON.parse(savedCart);

    if (!Array.isArray(parsedCart)) {
      console.warn(
        "購物車資料不是陣列：",
        parsedCart,
      );

      cartItems.value = [];
      return;
    }

    cartItems.value =
      parsedCart.map((item) => ({
        ...item,

        productId:
          Number(item.productId),

        price:
          Number(item.price ?? 0),

        quantity:
          Math.max(
            1,
            Number(item.quantity ?? 1),
          ),

        stock:
          Number(item.stock ?? 0),
      }));

  } catch (error) {
    console.error(
      "購物車資料錯誤：",
      error,
    );

    cartItems.value = [];
  }
}

// =====================================================
// 儲存購物車
// =====================================================

function saveCart() {
  localStorage.setItem(
    "cart",
    JSON.stringify(cartItems.value),
  );
}

// =====================================================
// 增加數量
// =====================================================

function increaseQuantity(item) {
  const quantity =
    Number(item.quantity ?? 1);

  const stock =
    Number(item.stock ?? 0);

  if (quantity >= stock) {
    return;
  }

  item.quantity =
    quantity + 1;

  saveCart();
}

// =====================================================
// 減少數量
// =====================================================

function decreaseQuantity(item) {
  const quantity =
    Number(item.quantity ?? 1);

  if (quantity <= 1) {
    return;
  }

  item.quantity =
    quantity - 1;

  saveCart();
}

// =====================================================
// 刪除商品
// =====================================================

function removeItem(productId) {
  cartItems.value =
    cartItems.value.filter(
      (item) =>
        Number(item.productId) !==
        Number(productId),
    );

  saveCart();
}

// =====================================================
// 總金額
// =====================================================

const totalAmount =
  computed(() => {
    return cartItems.value.reduce(
      (total, item) => {
        return (
          total +
          Number(item.price ?? 0) *
            Number(item.quantity ?? 0)
        );
      },
      0,
    );
  });

// =====================================================
// 價格格式
// =====================================================

function formatPrice(price) {
  return Number(
    price ?? 0,
  ).toLocaleString("zh-TW");
}

// =====================================================
// 確認結帳
// =====================================================

async function submitOrder() {
  // ==========================
  // 1. 檢查購物車
  // ==========================

  if (
    cartItems.value.length === 0
  ) {
    alert("購物車沒有商品");
    return;
  }

  // ==========================
  // 2. 取得登入會員 ID
  // ==========================
const savedMemberId =
  localStorage.getItem("memberId");

const memberId =
  savedMemberId
    ? Number(savedMemberId)
    : null;

console.log(
  "目前登入會員 memberId：",
  memberId
);

if (!memberId || memberId <= 0) {
  alert(
    "此帳號沒有會員資料，請使用會員帳號登入後再結帳"
  );
  return;
}
  // ==========================
  // 3. 檢查購買數量
  // ==========================

  const invalidItem =
    cartItems.value.find(
      (item) => {
        const quantity =
          Number(
            item.quantity ?? 0,
          );

        const stock =
          Number(
            item.stock ?? 0,
          );

        return (
          quantity <= 0 ||
          quantity > stock
        );
      },
    );

  if (invalidItem) {
    alert(
      `${invalidItem.productName} 的購買數量不正確，請重新確認`,
    );

    return;
  }

  submitting.value = true;

  try {
    // ==========================
    // 4. 建立 Request JSON
    // ==========================

    const requestBody = {
      memberId,

      // 目前沒有使用優惠券
      couponCode: null,

      items:
        cartItems.value.map(
          (item) => ({
            productId:
              Number(
                item.productId,
              ),

            quantity:
              Number(
                item.quantity,
              ),
          }),
        ),
    };

    console.log(
      "準備送出的訂單：",
      requestBody,
    );

    // ==========================
    // 5. POST /api/orders
    // ==========================

    const response =
      await fetch(
        "/api/orders",
        {
          method: "POST",

          headers:
            getAuthHeaders(),

          body:
            JSON.stringify(
              requestBody,
            ),
        },
      );

    console.log(
      "建立訂單 API status：",
      response.status,
    );

    // ==========================
    // 6. JWT 錯誤
    // ==========================

    if (
      response.status === 401 ||
      response.status === 403
    ) {
      throw new Error(
        "登入已失效，請重新登入",
      );
    }

    // ==========================
    // 7. 後端錯誤
    // ==========================

    if (!response.ok) {
      const errorText =
        await response.text();

      console.error(
        "建立訂單後端錯誤：",
        errorText,
      );

      throw new Error(
        errorText ||
          `建立訂單失敗 (${response.status})`,
      );
    }

    // ==========================
    // 8. 建立訂單成功
    // ==========================

    const order =
      await response.json();

    console.log(
      "建立訂單成功：",
      order,
    );

    // 防止後端沒有回 orderId
    if (!order?.orderId) {
      throw new Error(
        "訂單已建立，但沒有取得 orderId",
      );
    }

    // ==========================
    // 9. 清空購物車
    // ==========================

    cartItems.value = [];

    localStorage.removeItem(
      "cart",
    );

    // ==========================
    // 10. 跳付款頁
    // ==========================

    await router.push(
      `/payment/${order.orderId}`,
    );

  } catch (error) {
    console.error(
      "結帳失敗：",
      error,
    );

    alert(
      error.message ||
        "結帳失敗",
    );

  } finally {
    submitting.value = false;
  }
}

// =====================================================
// 初始化
// =====================================================

onMounted(() => {
  loadCart();
});
</script>

<style scoped>
.cart-page {
  min-height: 100vh;
  padding: 40px 0;
  background-color: #f5f5f5;
}

.cart-container {
  width: min(1000px, 92%);
  margin: auto;
}

.cart-container h1 {
  margin-bottom: 24px;
  color: #4a3b2a;
}

.cart-item {
  display: grid;

  grid-template-columns:
    1fr auto auto auto;

  align-items: center;

  gap: 24px;

  margin-bottom: 12px;

  padding: 20px;

  background-color: white;

  border: 1px solid #eeeeee;
}

.item-info h3 {
  margin: 0 0 8px;
}

.item-info p {
  margin: 0;

  color: #777777;
}

.quantity-control {
  display: flex;

  align-items: center;

  border: 1px solid #dddddd;
}

.quantity-control button {
  width: 38px;

  height: 36px;

  border: none;

  background-color: #f7f7f7;

  cursor: pointer;
}

.quantity-control button:disabled {
  color: #cccccc;

  cursor: not-allowed;
}

.quantity-control span {
  min-width: 42px;

  text-align: center;
}

.subtotal {
  min-width: 150px;

  color: #b3443c;

  font-weight: bold;
}

.delete-button {
  padding: 8px 14px;

  border: 1px solid #b3443c;

  background-color: white;

  color: #b3443c;

  cursor: pointer;
}

.cart-summary {
  margin-top: 24px;

  padding: 24px;

  background-color: white;

  text-align: right;
}

.checkout-button {
  margin-top: 12px;

  padding: 12px 28px;

  border: none;

  background-color: #b58a46;

  color: white;

  font-size: 16px;

  font-weight: bold;

  cursor: pointer;
}

.checkout-button:disabled {
  background-color: #cccccc;

  cursor: not-allowed;
}

.empty-cart {
  padding: 60px;

  background-color: white;

  text-align: center;

  color: #888888;
}

@media (max-width: 760px) {
  .cart-item {
    grid-template-columns: 1fr;

    gap: 14px;
  }

  .cart-summary {
    text-align: left;
  }
}
</style>