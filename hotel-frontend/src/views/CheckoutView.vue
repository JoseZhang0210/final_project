<template>
  <div class="checkout-page">
    <div class="checkout-container">
      <h1>訂單結帳</h1>

      <div v-if="cart.length === 0" class="empty-cart">
        購物車目前沒有商品
      </div>

      <template v-else>
        <section class="checkout-items">
          <article
            v-for="item in cart"
            :key="item.productId"
            class="checkout-item"
          >
            <div>
              <h3>{{ item.productName }}</h3>

              <p>
                NT$
                {{ formatPrice(item.price) }}
                ×
                {{ item.quantity }}
              </p>
            </div>

            <strong>
              NT$
              {{ formatPrice(item.price * item.quantity) }}
            </strong>
          </article>
        </section>

        <section class="coupon-section">
          <label for="couponCode">
            優惠券代碼
          </label>

          <input
            id="couponCode"
            v-model="couponCode"
            type="text"
            placeholder="沒有優惠券可留空"
          />
        </section>

        <section class="checkout-summary">
          <div class="summary-row">
            <span>商品總額</span>

            <strong>
              NT$
              {{ formatPrice(originalAmount) }}
            </strong>
          </div>

          <p class="summary-note">
            實際優惠金額將由後端建立訂單時計算。
          </p>
          
          <button
            type="button"
            class="checkout-button"
            :disabled="submitting"
            @click="createOrder"
          >
            {{ submitting ? "建立訂單中..." : "確認訂單" }}
          </button>
        </section>
      </template>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useRouter } from "vue-router";

import { getAuthHeaders } from "@/utils/auth";

const router = useRouter();

const cart = ref([]);

const couponCode = ref("");

const submitting = ref(false);

try {
  cart.value = JSON.parse(
    localStorage.getItem("cart") || "[]",
  );
} catch (error) {
  console.error("購物車資料格式錯誤：", error);

  cart.value = [];
}

const originalAmount = computed(() => {
  return cart.value.reduce((total, item) => {
    return (
      total +
      Number(item.price ?? 0) *
        Number(item.quantity ?? 0)
    );
  }, 0);
});

function formatPrice(value) {
  return Number(value ?? 0).toLocaleString("zh-TW");
}

async function createOrder() {
  if (cart.value.length === 0) {
    alert("購物車是空的");
    return;
  }

  const memberId = Number(
    localStorage.getItem("memberId"),
  );

  if (!memberId) {
    alert("找不到登入會員資料，請重新登入");
    return;
  }

  const requestBody = {
    memberId,

    couponCode:
      couponCode.value.trim() === ""
        ? null
        : couponCode.value.trim(),

    items: cart.value.map((item) => ({
      productId: Number(item.productId),
      quantity: Number(item.quantity),
    })),
  };

  console.log("建立訂單 Request：", requestBody);

  submitting.value = true;

  try {
    const response = await fetch("/api/orders", {
      method: "POST",

      headers: {
        "Content-Type": "application/json",
        ...getAuthHeaders(),
      },

      body: JSON.stringify(requestBody),
    });

    if (!response.ok) {
      const message = await response.text();

      throw new Error(
        message || `建立訂單失敗：${response.status}`,
      );
    }

    const order = await response.json();

    console.log("建立訂單成功：", order);

    localStorage.removeItem("cart");

    router.push(`/payment/${order.orderId}`);
  } catch (error) {
    console.error("建立訂單失敗：", error);

    alert(error.message || "建立訂單失敗");
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.checkout-page {
  min-height: 100vh;
  background: #f7f4ef;
  padding: 60px 20px;
}

.checkout-container {
  width: min(900px, 100%);
  margin: 0 auto;
  background: white;
  padding: 36px;
  border-radius: 12px;
}

.checkout-container h1 {
  margin-bottom: 32px;
}

.checkout-items {
  margin-bottom: 30px;
}

.checkout-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 0;
  border-bottom: 1px solid #ddd;
}

.checkout-item h3 {
  margin: 0 0 6px;
}

.checkout-item p {
  margin: 0;
}

.coupon-section {
  margin: 30px 0;
}

.coupon-section label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
}

.coupon-section input {
  width: 100%;
  box-sizing: border-box;
  padding: 12px;
}

.checkout-summary {
  margin-top: 30px;
}

.summary-row {
  display: flex;
  justify-content: space-between;
  font-size: 20px;
}

.summary-note {
  margin-top: 10px;
  color: #777;
}

.checkout-button {
  width: 100%;
  margin-top: 24px;
  padding: 14px;
  border: 0;
  cursor: pointer;
}

.checkout-button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.empty-cart {
  padding: 50px 0;
  text-align: center;
}
</style>