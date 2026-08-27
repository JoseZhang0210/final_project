<template>
  <div class="cart-page">
    <div class="cart-container">
      <h1>購物車</h1>

      <div v-if="cartItems.length === 0" class="empty-cart">
        購物車目前沒有商品
      </div>

      <div v-else>
        <div v-for="item in cartItems" :key="item.productId" class="cart-item">
          <div class="item-info">
            <h3>{{ item.productName }}</h3>

            <p>
              單價：
              ${{ formatPrice(item.price) }}
            </p>
          </div>

          <div class="quantity-control">
            <button type="button" @click="decreaseQuantity(item)">
              −
            </button>

            <span>
              {{ item.quantity }}
            </span>

            <button type="button" :disabled="item.quantity >= item.stock" @click="increaseQuantity(item)">
              ＋
            </button>
          </div>

          <div class="subtotal">
            小計：
            ${{ formatPrice(item.price * item.quantity) }}
          </div>

          <button type="button" class="delete-button" @click="removeItem(item.productId)">
            刪除
          </button>
        </div>

        <div class="cart-summary">
          <h2>
            總金額：
            ${{ formatPrice(totalAmount) }}
          </h2>

          <button type="button" class="checkout-button" :disabled="submitting" @click="submitOrder">
            {{ submitting ? "送出中..." : "確認結帳" }}
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

const cartItems = ref([]);

const submitting = ref(false);

// =====================================================
// JWT
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
    cartItems.value =
      JSON.parse(savedCart);
  } catch (error) {
    console.error(
      "購物車資料錯誤：",
      error
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
    JSON.stringify(cartItems.value)
  );
}

// =====================================================
// 增加數量
// =====================================================

function increaseQuantity(item) {
  if (
    Number(item.quantity) <
    Number(item.stock)
  ) {
    item.quantity++;

    saveCart();
  }
}

// =====================================================
// 減少數量
// =====================================================

function decreaseQuantity(item) {
  if (item.quantity > 1) {
    item.quantity--;

    saveCart();
  }
}

// =====================================================
// 刪除商品
// =====================================================

function removeItem(productId) {
  cartItems.value =
    cartItems.value.filter(
      (item) =>
        Number(item.productId) !==
        Number(productId)
    );

  saveCart();
}

// =====================================================
// 總金額
// =====================================================

const totalAmount = computed(() => {
  return cartItems.value.reduce(
    (total, item) => {
      return (
        total +
        Number(item.price) *
        Number(item.quantity)
      );
    },
    0
  );
});

// =====================================================
// 價格格式
// =====================================================

function formatPrice(price) {
  return Number(
    price ?? 0
  ).toLocaleString("zh-TW");
}

// =====================================================
// 確認結帳
// =====================================================

async function submitOrder() {
  // ==========================
  // 檢查購物車
  // ==========================

  if (cartItems.value.length === 0) {
    alert("購物車沒有商品");
    return;
  }

  // ==========================
  // 取得目前登入會員 ID
  // ==========================

  const memberId = 3
  //   -----等會員token儲存改好後再把下面的打開-------
  //     Number(localStorage.getItem("memberId"));

  //   console.log(
  //     "目前登入會員 memberId：",
  //     memberId
  //   );

  //   // 找不到會員 ID
  //   if (!memberId || memberId <= 0) {
  //     alert("找不到登入會員資料，請重新登入");
  //     return;
  //   }
  //   -----等會員token儲存改好後再把上面的打開-------

  submitting.value = true;

  try {
    // ==========================
    // 建立訂單 JSON
    // ==========================

    const requestBody = {
      memberId: memberId,

      items: cartItems.value.map(
        (item) => ({
          productId:
            Number(item.productId),

          quantity:
            Number(item.quantity),
        })
      ),
    };

    console.log(
      "準備送出的訂單：",
      requestBody
    );

    // ==========================
    // POST 建立訂單
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
              requestBody
            ),
        }
      );

    console.log(
      "建立訂單 API status：",
      response.status
    );

    // ==========================
    // JWT 權限問題
    // ==========================

    if (
      response.status === 401 ||
      response.status === 403
    ) {
      throw new Error(
        "登入已失效，請重新登入"
      );
    }

    // ==========================
    // 後端錯誤
    // ==========================

    if (!response.ok) {
      const errorText =
        await response.text();

      console.error(
        "建立訂單後端錯誤：",
        errorText
      );

      throw new Error(
        `建立訂單失敗 (${response.status})`
      );
    }

    // ==========================
    // 建立成功
    // ==========================

    const order =
      await response.json();

    console.log(
      "建立訂單成功：",
      order
    );

    alert(
      `訂單 #${order.orderId} 建立成功`
    );

    // ==========================
    // 清空購物車
    // ==========================

    cartItems.value = [];

    localStorage.removeItem(
      "cart"
    );

    // ==========================
    // 回商城
    // ==========================

    router.push(
      "/products"
    );

  } catch (error) {
    console.error(
      "結帳失敗：",
      error
    );

    alert(
      error.message ||
      "結帳失敗"
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
  min-width: 130px;

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
