<template>
  <div class="my-orders-page">
    <div class="orders-container">
      <h1>我的訂單</h1>

      <div
        v-if="loading"
        class="message-box"
      >
        訂單讀取中...
      </div>

      <div
        v-else-if="errorMessage"
        class="error-box"
      >
        {{ errorMessage }}
      </div>

      <div
        v-else-if="orders.length === 0"
        class="message-box"
      >
        目前沒有訂單
      </div>

      <div
        v-else
        class="order-list"
      >
        <div
          v-for="order in orders"
          :key="order.orderId"
          class="order-card"
        >
          <!-- 訂單上方 -->
          <div class="order-header">
            <div>
              <strong>
                訂單 #{{ order.orderId }}
              </strong>

              <div class="order-date">
                {{ formatDate(order.orderDate) }}
              </div>
            </div>

            <span
              class="status-badge"
              :class="getStatusClass(order.status)"
            >
              {{ getStatusText(order.status) }}
            </span>
          </div>

          <!-- 商品 -->
          <div class="order-items">
            <div
              v-for="item in order.items"
              :key="item.productId"
              class="order-item"
            >
              <div>
                <div class="product-name">
                  {{ item.productName }}
                </div>

                <div class="product-detail">
                  單價：
                  ${{ formatPrice(item.price) }}
                </div>

                <div class="product-detail">
                  數量：
                  {{ item.quantity }}
                </div>
              </div>

              <div class="subtotal">
                ${{ formatPrice(item.subtotal) }}
              </div>
            </div>
          </div>

          <!-- 總金額 -->
          <div class="order-total">
            總金額：
            <strong>
              ${{ formatPrice(order.totalAmount) }}
            </strong>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {
  onMounted,
  ref,
} from "vue";

const orders = ref([]);
const loading = ref(false);
const errorMessage = ref("");

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

async function loadOrders() {
  loading.value = true;
  errorMessage.value = "";

  try {
    // 目前期中測試先固定
    // 之後換成真正登入會員
    const memberId = 3;

    const response =
      await fetch(
        `http://localhost:8081/api/orders/member/${memberId}`,
        {
          method: "GET",
          headers: getAuthHeaders(),
        }
      );

    if (!response.ok) {
      throw new Error(
        "訂單讀取失敗"
      );
    }

    const data =
      await response.json();

    orders.value =
      Array.isArray(data)
        ? data
        : [];

  } catch (error) {
    console.error(error);

    errorMessage.value =
      error.message ||
      "訂單讀取失敗";

  } finally {
    loading.value = false;
  }
}

function formatPrice(price) {
  return Number(
    price ?? 0
  ).toLocaleString("zh-TW");
}

function formatDate(date) {
  if (!date) {
    return "";
  }

  return new Date(
    date
  ).toLocaleString(
    "zh-TW"
  );
}

function getStatusText(status) {
  switch (status) {
    case "PENDING":
      return "待處理";

    case "COMPLETED":
      return "已完成";

    case "CANCELLED":
      return "已取消";

    default:
      return status || "未知";
  }
}

function getStatusClass(status) {
  switch (status) {
    case "PENDING":
      return "pending";

    case "COMPLETED":
      return "completed";

    case "CANCELLED":
      return "cancelled";

    default:
      return "";
  }
}

onMounted(() => {
  loadOrders();
});
</script>

<style scoped>
.my-orders-page {
  min-height: 100vh;
  padding: 40px 0;
  background-color: #f5f5f5;
}

.orders-container {
  width: min(1000px, 92%);
  margin: auto;
}

.orders-container h1 {
  margin-bottom: 24px;
  color: #4a3b2a;
}

.order-card {
  margin-bottom: 18px;
  background-color: white;
  border: 1px solid #eeeeee;
  border-radius: 8px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  padding: 18px 20px;

  background-color: #faf8f4;

  border-bottom: 1px solid #eeeeee;
}

.order-date {
  margin-top: 5px;
  color: #888888;
  font-size: 13px;
}

.order-items {
  padding: 0 20px;
}

.order-item {
  display: flex;
  justify-content: space-between;
  align-items: center;

  padding: 18px 0;

  border-bottom: 1px solid #eeeeee;
}

.order-item:last-child {
  border-bottom: none;
}

.product-name {
  margin-bottom: 5px;
  color: #333333;
  font-weight: bold;
}

.product-detail {
  margin-top: 3px;
  color: #666666;
  font-size: 13px;
}

.subtotal {
  color: #b3443c;
  font-weight: bold;
}

.order-total {
  padding: 18px 20px;

  border-top: 1px solid #eeeeee;

  text-align: right;
}

.order-total strong {
  color: #b3443c;
  font-size: 18px;
}

.status-badge {
  padding: 6px 12px;

  border-radius: 20px;

  font-size: 13px;
  font-weight: bold;
}

.status-badge.pending {
  background-color: #fff3d8;
  color: #95691f;
}

.status-badge.completed {
  background-color: #e5f6eb;
  color: #257641;
}

.status-badge.cancelled {
  background-color: #fee2e2;
  color: #991b1b;
}

.message-box,
.error-box {
  padding: 50px;

  background-color: white;

  text-align: center;
}

.message-box {
  color: #777777;
}

.error-box {
  color: #b3443c;
}
</style>