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

          <!-- Header -->
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


          <!-- 商品 -->
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

                <div class="item-left">

                  <div class="product-name">
                    {{ item.productName }}
                  </div>

                  <div class="product-detail">
                    單價：
                    NT$
                    {{ formatPrice(item.price) }}
                  </div>


                  <!-- 一般模式 -->
                  <div
                    v-if="
                      editingOrderId !==
                      order.orderId
                    "
                    class="product-detail"
                  >
                    數量：
                    {{ item.quantity }}
                  </div>


                  <!-- 編輯模式 -->
                  <div
                    v-else
                    class="edit-quantity"
                  >

                    <span class="edit-label">
                      數量：
                    </span>

                    <button
                      type="button"
                      :disabled="
                        updatingItem ||
                        Number(item.quantity) <= 1
                      "
                      @click="
                        changeOrderItemQuantity(
                          order,
                          item,
                          Number(item.quantity) - 1
                        )
                      "
                    >
                      −
                    </button>

                    <span class="quantity-value">
                      {{ item.quantity }}
                    </span>

                    <button
                      type="button"
                      :disabled="updatingItem"
                      @click="
                        changeOrderItemQuantity(
                          order,
                          item,
                          Number(item.quantity) + 1
                        )
                      "
                    >
                      +
                    </button>

                    <button
                      type="button"
                      class="delete-item-button"
                      :disabled="updatingItem"
                      @click="
                        deleteOrderItem(
                          order,
                          item
                        )
                      "
                    >
                      刪除商品
                    </button>

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


          <!-- Footer -->
          <div class="order-footer">

            <div class="order-actions">

              <!-- 未付款 -->
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
                  class="edit-order-button"
                  @click="toggleEditOrder(order)"
                >
                  {{
                    editingOrderId ===
                    order.orderId
                      ? "完成編輯"
                      : "編輯訂單"
                  }}
                </button>


                <button
                  type="button"
                  class="cancel-order-button"
                  @click="cancelOrder(order)"
                >
                  取消訂單
                </button>

              </template>


              <!-- 已付款 -->
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


            <!-- 金額 -->
            <div class="amount-summary">

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

const editingOrderId =
  ref(null);

const updatingItem =
  ref(false);

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
// 載入訂單
// =====================================================

async function loadOrders() {

  loading.value = true;
  errorMessage.value = "";

  try {

    const token =
      localStorage.getItem(
        "token"
      );

    if (!token) {
      throw new Error(
        "請使用會員帳號登入後再查看訂單"
      );
    }

    const response =
      await fetch(
        "/api/orders/mine",
        {
          method: "GET",
          headers:
            getAuthHeaders(),
        }
      );


    if (
      response.status === 401 ||
      response.status === 403
    ) {
      throw new Error(
        "登入已失效，請重新登入"
      );
    }


    if (!response.ok) {

      const errorText =
        await response.text();

      throw new Error(
        errorText ||
        `訂單讀取失敗 (${response.status})`
      );
    }


    const data =
      await response.json();

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

    loading.value = false;
  }
}


// =====================================================
// 編輯訂單切換
// =====================================================

function toggleEditOrder(order) {

  if (
    order.orderStatus !== "PENDING"
  ) {

    alert(
      "此訂單目前無法修改"
    );

    return;
  }


  if (
    order.paymentStatus === "PAID"
  ) {

    alert(
      "訂單已付款，無法再修改"
    );

    return;
  }


  if (
    editingOrderId.value ===
    order.orderId
  ) {

    editingOrderId.value =
      null;

  } else {

    editingOrderId.value =
      order.orderId;
  }
}


// =====================================================
// 修改商品數量
//
// PUT
// /api/orders/{orderId}/items/{productId}?quantity=...
// =====================================================

async function changeOrderItemQuantity(
  order,
  item,
  quantity
) {

  if (
    order.orderStatus !== "PENDING" ||
    order.paymentStatus === "PAID"
  ) {

    alert(
      "此訂單目前無法修改"
    );

    return;
  }


  if (
    quantity <= 0
  ) {
    return;
  }


  updatingItem.value = true;


  try {

    const response =
      await fetch(
        `/api/orders/${order.orderId}/items/${item.productId}?quantity=${quantity}`,
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
        "沒有權限修改此訂單"
      );
    }


    if (!response.ok) {

      const errorText =
        await response.text();

      throw new Error(
        extractErrorMessage(
          errorText
        ) ||
        "修改商品數量失敗"
      );
    }


    await loadOrders();


  } catch (error) {

    console.error(
      "修改商品數量失敗：",
      error
    );

    alert(
      error.message ||
      "修改商品數量失敗"
    );


  } finally {

    updatingItem.value = false;
  }
}


// =====================================================
// 刪除訂單商品
//
// DELETE
// /api/orders/{orderId}/items/{productId}
// =====================================================

async function deleteOrderItem(
  order,
  item
) {

  if (
    order.orderStatus !== "PENDING" ||
    order.paymentStatus === "PAID"
  ) {

    alert(
      "此訂單目前無法修改"
    );

    return;
  }


  if (
    order.items?.length <= 1
  ) {

    alert(
      "訂單至少需要保留一項商品。如果不需要此訂單，請直接取消訂單。"
    );

    return;
  }


  const confirmed =
    window.confirm(
      `確定要從訂單 #${order.orderId} 刪除「${item.productName}」嗎？`
    );


  if (!confirmed) {
    return;
  }


  updatingItem.value = true;


  try {

    const response =
      await fetch(
        `/api/orders/${order.orderId}/items/${item.productId}`,
        {
          method: "DELETE",
          headers:
            getAuthHeaders(),
        }
      );


    if (
      response.status === 401 ||
      response.status === 403
    ) {

      throw new Error(
        "沒有權限修改此訂單"
      );
    }


    if (!response.ok) {

      const errorText =
        await response.text();

      throw new Error(
        extractErrorMessage(
          errorText
        ) ||
        "刪除商品失敗"
      );
    }


    await loadOrders();


  } catch (error) {

    console.error(
      "刪除訂單商品失敗：",
      error
    );

    alert(
      error.message ||
      "刪除商品失敗"
    );


  } finally {

    updatingItem.value = false;
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
// =====================================================

async function cancelOrder(order) {

  if (
    order.orderStatus !==
    "PENDING"
  ) {

    alert(
      "此訂單無法取消"
    );

    return;
  }


  if (
    order.paymentStatus ===
    "PAID"
  ) {

    alert(
      "此訂單已付款，無法取消"
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
        extractErrorMessage(
          errorText
        ) ||
        "取消訂單失敗"
      );
    }


    editingOrderId.value =
      null;


    alert(
      `訂單 #${order.orderId} 已取消`
    );


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
// Backend error
// =====================================================

function extractErrorMessage(
  text
) {

  if (!text) {
    return "";
  }


  try {

    const data =
      JSON.parse(text);

    return (
      data.message ||
      data.error ||
      ""
    );


  } catch {

    return text;
  }
}


// =====================================================
// 金額
// =====================================================

function formatPrice(price) {

  return Number(
    price ?? 0
  ).toLocaleString(
    "zh-TW"
  );
}


// =====================================================
// 日期
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
// 狀態文字
// =====================================================

function getStatusText(
  orderStatus,
  paymentStatus
) {

  if (
    orderStatus ===
    "CANCELLED"
  ) {
    return "已取消";
  }


  if (
    orderStatus ===
    "COMPLETED"
  ) {
    return "已完成";
  }


  if (
    orderStatus === "PENDING" &&
    paymentStatus === "PAID"
  ) {

    return "付款完成，請至櫃台領取訂單商品";
  }


  if (
    orderStatus ===
    "PENDING"
  ) {

    return "待付款";
  }


  return "未知";
}


// =====================================================
// 狀態 CSS
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
    orderStatus === "PENDING" &&
    paymentStatus === "PAID"
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
  gap: 20px;
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
  gap: 20px;
  padding: 18px 0;
  border-bottom: 1px solid #eeeeee;
}

.order-item:last-child {
  border-bottom: none;
}

.item-left {
  flex: 1;
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
   編輯數量
   ===================================================== */

.edit-quantity {
  display: flex;
  align-items: center;
  gap: 5px;
  margin-top: 6px;
  flex-wrap: wrap;
}

.edit-label {
  font-size: 12px;
  color: #666666;
}

.edit-quantity button {
  min-width: 28px;
  height: 26px;
  padding: 0 7px;

  border: 1px solid #cccccc;
  border-radius: 4px;

  background-color: white;

  font-size: 12px;

  cursor: pointer;
}

.quantity-value {
  min-width: 20px;

  font-size: 13px;
  font-weight: bold;

  text-align: center;
}

.delete-item-button {
  width: auto !important;
  height: 26px !important;

  padding: 0 8px !important;

  border: 1px solid #b3443c !important;
  border-radius: 4px;

  background-color: white !important;

  color: #b3443c;

  font-size: 12px;
}
.delete-item-button:hover:not(:disabled) {
  background-color: #fde9e7;
}


/* =====================================================
   Footer
   ===================================================== */

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 30px;
  padding: 18px 20px;
  border-top: 1px solid #eeeeee;
  background-color: #fcfbf9;
}

.order-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}


/* =====================================================
   操作按鈕
   ===================================================== */

.pay-button,
.edit-order-button,
.cancel-order-button {
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
}

.pay-button {
  border: none;
  background-color: #b58a46;
  color: white;
}

.pay-button:hover {
  background-color: #8f692f;
}

.edit-order-button {
  border: 1px solid #8a6d3b;
  background-color: white;
  color: #8a6d3b;
}

.edit-order-button:hover {
  background-color: #f7f1e8;
}

.cancel-order-button {
  border: 1px solid #b3443c;
  background-color: white;
  color: #b3443c;
}

.cancel-order-button:hover {
  background-color: #fde9e7;
}


/* =====================================================
   金額
   ===================================================== */

.amount-summary {
  width: 260px;
  margin-left: auto;
}

.amount-row {
  display: flex;
  justify-content: space-between;
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
  justify-content: space-between;
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
  padding: 7px 13px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: bold;
  line-height: 1.5;
  text-align: center;
}

.status-badge.pending {
  background-color: #fff3d8;
  color: #95691f;
}

.status-badge.paid {
  background-color: #e8f1ff;
  color: #2f5f9f;
}

.status-badge.completed {
  background-color: #e5f6eb;
  color: #257641;
}

.status-badge.cancelled {
  background-color: #fee2e2;
  color: #991b1b;
}


/* =====================================================
   狀態訊息
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

.cancelled-message {
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
  background-color: white;
  text-align: center;
}

.message-box {
  color: #777777;
}

.error-box {
  color: #b3443c;
  background-color: #fde9e7;
}


/* =====================================================
   RWD
   ===================================================== */

@media (max-width: 650px) {

  .order-footer {
    align-items: stretch;
    flex-direction: column;
  }

  .order-actions {
    width: 100%;
  }

  .amount-summary {
    width: 100%;
    margin-left: 0;
  }

  .order-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .status-badge {
    max-width: 100%;
  }

  .amount-row,
  .order-total {
    justify-content: space-between;
  }

  .edit-quantity {
    align-items: flex-start;
  }
}
</style>