<template>
  <div class="my-orders-page">
    <div class="orders-container">

      <h1>我的訂單</h1>


      <!-- Loading -->
      <div
        v-if="loading"
        class="message-box"
      >
        訂單讀取中...
      </div>


      <!-- Error -->
      <div
        v-else-if="errorMessage"
        class="error-box"
      >
        {{ errorMessage }}
      </div>


      <!-- 無訂單 -->
      <div
        v-else-if="orders.length === 0"
        class="message-box"
      >
        目前沒有訂單
      </div>


      <!-- =====================================================
           訂單列表
           ===================================================== -->
      <div
        v-else
        class="order-list"
      >

        <div
          v-for="order in orders"
          :key="order.orderId"
          class="order-card"
        >

          <!-- =================================================
               訂單 Header
               ================================================= -->
          <div class="order-header">

            <div>
              <strong>
                訂單 #{{ order.orderId }}
              </strong>

              <div class="order-date">
                {{ formatDate(order.orderDate) }}
              </div>
            </div>


            <!-- 訂單狀態 -->
            <span
              class="status-badge"
              :class="
                getStatusClass(
                  order.orderStatus,
                  order.paymentStatus
                )
              "
            >
              {{
                getStatusText(
                  order.orderStatus,
                  order.paymentStatus
                )
              }}
            </span>

          </div>


          <!-- =================================================
               商品內容
               ================================================= -->
          <div class="order-items">

            <div
              v-if="
                order.items &&
                order.items.length > 0
              "
            >

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
                    NT$
                    {{ formatPrice(item.price) }}
                  </div>

                  <div class="product-detail">
                    數量：
                    {{ item.quantity }}
                  </div>

                </div>


                <div class="subtotal">
                  NT$
                  {{
                    formatPrice(
                      item.subtotal
                    )
                  }}
                </div>

              </div>

            </div>


            <div
              v-else
              class="no-items"
            >
              無商品資料
            </div>

          </div>


          <!-- =================================================
               下方 Footer
               
               左：去結帳 / 取消訂單
               右：商品金額 / 總金額
               ================================================= -->
          <div class="order-footer">

            <!-- ===============================================
                 左側操作
                 =============================================== -->
            <div class="order-actions">

              <!-- 尚未付款 -->
              <template
                v-if="
                  order.orderStatus === 'PENDING' &&
                  order.paymentStatus !== 'PAID'
                "
              >

                <button
                  type="button"
                  class="pay-button"
                  @click="goToPayment(order)"
                >
                  去結帳
                </button>


                <button
                  type="button"
                  class="cancel-order-button"
                  @click="cancelOrder(order)"
                >
                  取消訂單
                </button>

              </template>


              <!-- 已付款待領貨 -->
              <div
                v-else-if="
                  order.orderStatus === 'PENDING' &&
                  order.paymentStatus === 'PAID'
                "
                class="paid-message"
              >
                付款完成，請至櫃台領取訂單商品
              </div>


              <!-- 已完成 -->
              <div
                v-else-if="
                  order.orderStatus === 'COMPLETED'
                "
                class="completed-message"
              >
                訂單已完成
              </div>


              <!-- 已取消 -->
              <div
                v-else-if="
                  order.orderStatus === 'CANCELLED'
                "
                class="cancelled-message"
              >
                訂單已取消
              </div>

            </div>


            <!-- ===============================================
                 右側金額
                 =============================================== -->
            <div class="amount-summary">

              <!-- 商品金額 -->
              <div class="amount-row">

                <span>
                  商品金額
                </span>

                <span>
                  NT$
                  {{
                    formatPrice(
                      order.originalAmount
                    )
                  }}
                </span>

              </div>


              <!-- 優惠 -->
              <div
                v-if="
                  Number(
                    order.discountAmount ?? 0
                  ) > 0
                "
                class="amount-row discount-row"
              >

                <span>
                  優惠折抵
                </span>

                <span>
                  - NT$
                  {{
                    formatPrice(
                      order.discountAmount
                    )
                  }}
                </span>

              </div>


              <!-- 總金額 -->
              <div class="order-total">

                <span>
                  總金額：
                </span>

                <strong>
                  NT$
                  {{
                    formatPrice(
                      order.finalAmount
                    )
                  }}
                </strong>

              </div>

            </div>

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

import {
  useRouter,
} from "vue-router";


const orders =
  ref([]);

const loading =
  ref(false);

const errorMessage =
  ref("");

const router =
  useRouter();


// =====================================================
// JWT Header
// =====================================================

function getAuthHeaders() {

  const token =
    localStorage.getItem(
      "token"
    );


  const headers = {
    "Content-Type":
      "application/json",
  };


  if (token) {

    headers.Authorization =
      "Bearer " + token;
  }


  return headers;
}


// =====================================================
// 讀取會員自己的訂單
//
// GET /api/orders/member/{memberId}
// =====================================================

async function loadOrders() {

  loading.value = true;

  errorMessage.value = "";


  try {

    // =========================
    // 取得登入會員 ID
    // =========================

    const savedMemberId =
      localStorage.getItem(
        "memberId"
      );


    const memberId =
      savedMemberId
        ? Number(savedMemberId)
        : null;


    console.log(
      "我的訂單 memberId：",
      memberId
    );


    if (
      !memberId ||
      memberId <= 0
    ) {

      throw new Error(
        "找不到登入會員資料，請使用會員帳號登入"
      );
    }


    // =========================
    // API
    // =========================

    const response =
      await fetch(
        `/api/orders/member/${memberId}`,
        {
          method: "GET",

          headers:
            getAuthHeaders(),
        }
      );


    console.log(
      "我的訂單 API status：",
      response.status
    );


    // =========================
    // JWT 權限
    // =========================

    if (
      response.status === 401 ||
      response.status === 403
    ) {

      throw new Error(
        "登入已失效，請重新登入"
      );
    }


    // =========================
    // API Error
    // =========================

    if (!response.ok) {

      const errorText =
        await response.text();


      console.error(
        "我的訂單後端錯誤：",
        errorText
      );


      throw new Error(
        errorText ||
        `訂單讀取失敗 (${response.status})`
      );
    }


    // =========================
    // JSON
    // =========================

    const data =
      await response.json();


    console.log(
      "我的訂單資料：",
      data
    );


    orders.value =
      Array.isArray(data)
        ? data
        : [];


  } catch (error) {

    console.error(
      "我的訂單讀取失敗：",
      error
    );


    errorMessage.value =
      error.message ||
      "訂單讀取失敗";


    orders.value = [];


  } finally {

    loading.value =
      false;
  }
}


// =====================================================
// 去結帳
// =====================================================

function goToPayment(order) {

  if (
    order.orderStatus !==
    "PENDING"
  ) {

    alert(
      "此訂單無法付款"
    );

    return;
  }


  if (
    order.paymentStatus ===
    "PAID"
  ) {

    alert(
      "此訂單已完成付款"
    );

    return;
  }


  router.push(
    `/payment/${order.orderId}`
  );
}


// =====================================================
// 取消訂單
//
// PUT /api/orders/{orderId}/cancel
// =====================================================

async function cancelOrder(order) {

  // =========================
  // 只有 PENDING 可取消
  // =========================

  if (
    order.orderStatus !==
    "PENDING"
  ) {

    alert(
      "此訂單無法取消"
    );

    return;
  }


  // =========================
  // 已付款不允許直接取消
  // =========================

  if (
    order.paymentStatus ===
    "PAID"
  ) {

    alert(
      "此訂單已付款，無法直接取消"
    );

    return;
  }


  const confirmed =
    confirm(
      `確定要取消訂單 #${order.orderId} 嗎？`
    );


  if (!confirmed) {
    return;
  }


  try {

    const response =
      await fetch(
        `/api/orders/${order.orderId}/cancel`,
        {
          method: "PUT",

          headers:
            getAuthHeaders(),
        }
      );


    if (
      response.status === 401 ||
      response.status === 403
    ) {

      throw new Error(
        "沒有權限取消此訂單"
      );
    }


    if (!response.ok) {

      const errorText =
        await response.text();


      throw new Error(
        errorText ||
        "取消訂單失敗"
      );
    }


    alert(
      `訂單 #${order.orderId} 已取消`
    );


    // 重新讀取
    await loadOrders();


  } catch (error) {

    console.error(
      "取消訂單失敗：",
      error
    );


    alert(
      error.message ||
      "取消訂單失敗"
    );
  }
}


// =====================================================
// 金額格式
// =====================================================

function formatPrice(price) {

  return Number(
    price ?? 0
  ).toLocaleString(
    "zh-TW"
  );
}


// =====================================================
// 日期格式
// =====================================================

function formatDate(date) {

  if (!date) {
    return "";
  }


  const parsedDate =
    new Date(date);


  if (
    Number.isNaN(
      parsedDate.getTime()
    )
  ) {

    return date;
  }


  return parsedDate.toLocaleString(
    "zh-TW",
    {
      year: "numeric",

      month: "2-digit",

      day: "2-digit",

      hour: "2-digit",

      minute: "2-digit",
    }
  );
}


// =====================================================
// 訂單顯示文字
// =====================================================

function getStatusText(
  orderStatus,
  paymentStatus
) {

  // 已取消
  if (
    orderStatus ===
    "CANCELLED"
  ) {

    return "已取消";
  }


  // 已完成
  if (
    orderStatus ===
    "COMPLETED"
  ) {

    return "已完成";
  }


  // 已付款，但商品尚未交付
  if (
    orderStatus ===
      "PENDING" &&
    paymentStatus ===
      "PAID"
  ) {

    return "付款完成，請至櫃台領取訂單商品";
  }


  // 尚未付款
  if (
    orderStatus ===
    "PENDING"
  ) {

    return "待付款";
  }


  return "未知";
}


// =====================================================
// 訂單 CSS
// =====================================================

function getStatusClass(
  orderStatus,
  paymentStatus
) {

  if (
    orderStatus ===
    "CANCELLED"
  ) {

    return "cancelled";
  }


  if (
    orderStatus ===
    "COMPLETED"
  ) {

    return "completed";
  }


  if (
    orderStatus ===
      "PENDING" &&
    paymentStatus ===
      "PAID"
  ) {

    return "paid";
  }


  return "pending";
}


// =====================================================
// 初始化
// =====================================================

onMounted(() => {

  loadOrders();
});

</script>


<style scoped>

/* =====================================================
   頁面
   ===================================================== */

.my-orders-page {
  min-height: 100vh;

  padding: 40px 0;

  background-color:
    #f5f5f5;
}


.orders-container {
  width:
    min(1000px, 92%);

  margin: auto;
}


.orders-container h1 {
  margin-bottom: 24px;

  color: #4a3b2a;
}


/* =====================================================
   訂單卡片
   ===================================================== */

.order-card {
  margin-bottom: 18px;

  background-color: white;

  border:
    1px solid #eeeeee;

  border-radius: 8px;

  overflow: hidden;
}


/* =====================================================
   Header
   ===================================================== */

.order-header {
  display: flex;

  justify-content:
    space-between;

  align-items: center;

  gap: 20px;

  padding: 18px 20px;

  background-color:
    #faf8f4;

  border-bottom:
    1px solid #eeeeee;
}


.order-date {
  margin-top: 5px;

  color: #888888;

  font-size: 13px;
}


/* =====================================================
   商品
   ===================================================== */

.order-items {
  padding:
    0 20px;
}


.order-item {
  display: flex;

  justify-content:
    space-between;

  align-items: center;

  gap: 20px;

  padding:
    18px 0;

  border-bottom:
    1px solid #eeeeee;
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

  font-size: 16px;

  font-weight: bold;

  white-space: nowrap;
}


.no-items {
  padding: 24px 0;

  color: #999999;

  text-align: center;
}


/* =====================================================
   Footer
   ===================================================== */

.order-footer {
  display: flex;

  justify-content:
    space-between;

  align-items: center;

  gap: 30px;

  padding: 18px 20px;

  border-top:
    1px solid #eeeeee;

  background-color:
    #fcfbf9;
}


/* =====================================================
   左邊按鈕
   ===================================================== */

.order-actions {
  display: flex;

  align-items: center;

  gap: 10px;

  flex-wrap: wrap;
}


.pay-button,
.cancel-order-button {
  padding:
    10px 20px;

  border-radius: 6px;

  font-size: 14px;

  font-weight: bold;

  cursor: pointer;
}


.pay-button {
  border: none;

  background-color:
    #b58a46;

  color: white;
}


.pay-button:hover {
  background-color:
    #8f692f;
}


.cancel-order-button {
  border:
    1px solid #b3443c;

  background-color:
    white;

  color: #b3443c;
}


.cancel-order-button:hover {
  background-color:
    #fde9e7;
}


/* =====================================================
   右邊金額
   ===================================================== */

.amount-summary {
  width: 260px;

  margin-left: auto;
}


.amount-row {
  display: flex;

  justify-content:
    space-between;

  gap: 30px;

  margin-bottom: 8px;

  color: #777777;

  font-size: 13px;
}


.discount-row {
  color: #3e7c56;
}


.order-total {
  display: flex;

  justify-content:
    space-between;

  align-items: center;

  gap: 20px;

  margin-top: 10px;
}


.order-total strong {
  color: #b3443c;

  font-size: 20px;
}


/* =====================================================
   狀態
   ===================================================== */

.status-badge {
  max-width: 360px;

  padding:
    7px 13px;

  border-radius: 20px;

  font-size: 13px;

  font-weight: bold;

  line-height: 1.5;

  text-align: center;
}


/* 待付款 */
.status-badge.pending {
  background-color:
    #fff3d8;

  color: #95691f;
}


/* 已付款，等待領貨 */
.status-badge.paid {
  background-color:
    #e8f1ff;

  color: #2f5f9f;
}


/* 已完成 */
.status-badge.completed {
  background-color:
    #e5f6eb;

  color: #257641;
}


/* 已取消 */
.status-badge.cancelled {
  background-color:
    #fee2e2;

  color: #991b1b;
}


/* =====================================================
   左下訊息
   ===================================================== */

.paid-message {
  color: #2f5f9f;

  font-size: 14px;

  font-weight: bold;
}


.completed-message {
  color: #257641;

  font-size: 14px;

  font-weight: bold;
}


.cancellㄋed-message {
  color: #991b1b;

  font-size: 14px;

  font-weight: bold;
}


/* =====================================================
   Message
   ===================================================== */

.message-box,
.error-box {
  padding: 50px;

  background-color:
    white;

  text-align: center;
}


.message-box {
  color: #777777;
}


.error-box {
  color: #b3443c;

  background-color:
    #fde9e7;
}


/* =====================================================
   RWD
   ===================================================== */

@media (
  max-width: 650px
) {

  .order-footer {
    align-items:
      stretch;

    flex-direction:
      column;
  }


  .order-actions {
    width: 100%;
  }


  .amount-summary {
    width: 100%;

    margin-left: 0;
  }


  .order-header {
    align-items:
      flex-start;

    flex-direction:
      column;
  }


  .status-badge {
    max-width: 100%;
  }


  .amount-row,
  .order-total {
    justify-content:
      space-between;
  }
}

</style>