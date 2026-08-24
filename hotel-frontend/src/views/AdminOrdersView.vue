<template>
  <div class="orders-page">
    <!-- =========================
         頁面標題
         ========================= -->
    <div class="page-header">
      <div>
        <h1>訂單管理</h1>
        <p>查看會員訂單與商品內容</p>
      </div>

      <button
        type="button"
        class="refresh-button"
        @click="loadOrders"
      >
        重新整理
      </button>
    </div>

    <!-- =========================
         Loading
         ========================= -->
    <div
      v-if="loading"
      class="message-box"
    >
      訂單讀取中...
    </div>

    <!-- =========================
         Error
         ========================= -->
    <div
      v-else-if="errorMessage"
      class="error-box"
    >
      {{ errorMessage }}
    </div>

    <!-- =========================
         沒有訂單
         ========================= -->
    <div
      v-else-if="orders.length === 0"
      class="message-box"
    >
      目前沒有訂單
    </div>

    <!-- =========================
         訂單列表
         ========================= -->
    <div
      v-else
      class="table-card"
    >
      <table>
        <thead>
          <tr>
            <th>訂單編號</th>
            <th>會員資料</th>
            <th>購買商品</th>
            <th>總金額</th>
            <th>狀態</th>
            <th>建立時間</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="order in orders"
            :key="order.orderId"
          >
            <!-- =========================
                 訂單編號
                 ========================= -->
            <td class="order-id-cell">
              #{{ order.orderId }}
            </td>

            <!-- =========================
                 會員資料
                 ========================= -->
            <td class="member-cell">
              <div class="member-name">
                {{ order.memberName || "查無姓名" }}
              </div>

              <div class="member-detail">
                電話：
                {{ order.memberPhone || "未提供" }}
              </div>

              <div class="member-detail">
                信箱：
                {{ order.memberEmail || "未提供" }}
              </div>
            </td>

            <!-- =========================
                 購買商品
                 ========================= -->
            <td class="items-cell">
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
                  <div class="item-name">
                    {{ item.productName }}
                  </div>

                  <div class="item-detail">
                    單價：
                    ${{ formatPrice(item.price) }}
                  </div>

                  <div class="item-detail">
                    數量：
                    {{ item.quantity }}
                  </div>

                  <div class="item-subtotal">
                    小計：
                    ${{ formatPrice(item.subtotal) }}
                  </div>
                </div>
              </div>

              <div
                v-else
                class="no-item"
              >
                無商品資料
              </div>
            </td>

            <!-- =========================
                 總金額
                 ========================= -->
            <td class="total-cell">
              ${{ formatPrice(order.totalAmount) }}
            </td>

            <!-- =========================
                 狀態
                 ========================= -->
            <td class="status-cell">
              <select
                class="status-select"
                :class="getStatusClass(order.status)"
                :value="order.status"
                @change="updateOrderStatus(order, $event.target.value)"
              >
                <option value="PENDING">
                  待處理
                </option>

                <option value="COMPLETED">
                  已完成
                </option>

                <option value="CANCELLED">
                  已取消
                </option>
              </select>
            </td>

            <!-- =========================
                 建立時間
                 ========================= -->
            <td class="date-cell">
              {{ formatDate(order.orderDate) }}
            </td>
          </tr>
        </tbody>
      </table>
    </div>
  </div>
</template>

<script setup>
import {
  onMounted,
  ref,
} from "vue";

// =====================================================
// 訂單資料
// =====================================================

const orders = ref([]);

// =====================================================
// Loading
// =====================================================

const loading = ref(false);

// =====================================================
// Error
// =====================================================

const errorMessage = ref("");

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
// 讀取全部訂單
//
// GET /api/orders
// =====================================================

async function loadOrders() {
  loading.value = true;

  errorMessage.value = "";

  try {
    console.log(
      "開始讀取訂單..."
    );

    const response =
      await fetch(
        "http://localhost:8081/api/orders",
        {
          method: "GET",

          headers:
            getAuthHeaders(),
        }
      );

    console.log(
      "訂單 API status：",
      response.status
    );

    // =========================
    // 權限錯誤
    // =========================

    if (
      response.status === 401 ||
      response.status === 403
    ) {
      throw new Error(
        "沒有權限讀取訂單資料，請確認登入狀態"
      );
    }

    // =========================
    // 其他錯誤
    // =========================

    if (!response.ok) {
      const errorText =
        await response.text();

      console.error(
        "後端錯誤內容：",
        errorText
      );

      throw new Error(
        "訂單讀取失敗，狀態碼：" +
          response.status
      );
    }

    // =========================
    // JSON
    // =========================

    const data =
      await response.json();

    console.log(
      "訂單 API 回傳資料：",
      data
    );

    orders.value =
      Array.isArray(data)
        ? data
        : [];

  } catch (error) {
    console.error(
      "訂單讀取失敗：",
      error
    );

    errorMessage.value =
      error.message ||
      "訂單資料讀取失敗";

    orders.value = [];

  } finally {
    loading.value = false;

    console.log(
      "訂單讀取結束"
    );
  }
}

// =====================================================
// 金額格式
// =====================================================

function formatPrice(price) {
  const number =
    Number(price ?? 0);

  return number.toLocaleString(
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
// 訂單狀態中文
// =====================================================

function getStatusText(status) {
  switch (status) {
    case "PENDING":
      return "待處理";

    case "CONFIRMED":
      return "已確認";

    case "PAID":
      return "已付款";

    case "SHIPPED":
      return "已出貨";

    case "COMPLETED":
      return "已完成";

    case "CANCELLED":
      return "已取消";

    default:
      return status || "未知狀態";
  }
}

// =====================================================
// 訂單狀態 CSS
// =====================================================

function getStatusClass(status) {
  switch (status) {
    case "PENDING":
      return "status-pending";

    case "CONFIRMED":
      return "status-confirmed";

    case "PAID":
      return "status-paid";

    case "SHIPPED":
      return "status-shipped";

    case "COMPLETED":
      return "status-completed";

    case "CANCELLED":
      return "status-cancelled";

    default:
      return "status-default";
  }
}


// 修改訂單狀態


async function updateOrderStatus(
  order,
  newStatus
) {
  // 沒有改變就不用送 API
  if (newStatus === order.status) {
    return;
  }

  const oldStatus =
    order.status;

  try {
    const response =
      await fetch(
        `http://localhost:8081/api/orders/${order.orderId}/status?status=${newStatus}`,
        {
          method: "PUT",
          headers: getAuthHeaders(),
        }
      );

    if (
      response.status === 401 ||
      response.status === 403
    ) {
      throw new Error(
        "沒有權限修改訂單狀態"
      );
    }

    if (!response.ok) {
      const errorText =
        await response.text();

      console.error(
        "修改訂單狀態失敗：",
        errorText
      );

      throw new Error(
        "修改訂單狀態失敗"
      );
    }

    // 前端同步
    order.status =
      newStatus;

    alert(
      `訂單 #${order.orderId} 狀態已改為 ${getStatusText(newStatus)}`
    );

  } catch (error) {
    console.error(
      "更新狀態失敗：",
      error
    );

    // API 失敗就恢復原本值
    order.status =
      oldStatus;

    alert(
      error.message ||
      "更新訂單狀態失敗"
    );

    // 重新讀取確保畫面跟 DB 一致
    await loadOrders();
  }
}
// =====================================================
// 頁面初始化
// =====================================================

onMounted(() => {
  loadOrders();
});
</script>

<style scoped>
/* =====================================================
   頁面
   ===================================================== */

.orders-page {
  width: 100%;
  padding: 20px;
}

/* =====================================================
   Header
   ===================================================== */

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;

  margin-bottom: 25px;
}

.page-header h1 {
  margin: 0 0 6px;

  color: #3f3328;

  font-size: 28px;
}

.page-header p {
  margin: 0;

  color: #888888;

  font-size: 14px;
}

/* =====================================================
   重新整理
   ===================================================== */

.refresh-button {
  padding: 9px 16px;

  border: none;
  border-radius: 6px;

  background-color: #b58a46;

  color: white;

  font-weight: bold;

  cursor: pointer;
}

.refresh-button:hover {
  background-color: #8f692f;
}

/* =====================================================
   Table Card
   ===================================================== */

.table-card {
  overflow-x: auto;

  background-color: white;

  border-radius: 10px;

  box-shadow:
    0 4px 16px
    rgba(0, 0, 0, 0.06);
}

/* =====================================================
   Table
   ===================================================== */

table {
  width: 100%;

  border-collapse: collapse;
}

thead {
  background-color: #4a3b2a;

  color: white;
}

th,
td {
  padding: 16px 14px;

  border-bottom:
    1px solid #eeeeee;
}

th {
  text-align: center;

  white-space: nowrap;
}

td {
  vertical-align: top;
}

tbody tr:hover {
  background-color: #fcfaf6;
}

/* =====================================================
   訂單編號
   ===================================================== */

.order-id-cell {
  width: 90px;

  text-align: center;

  font-weight: bold;
}

/* =====================================================
   會員資料
   ===================================================== */

.member-cell {
  width: 230px;
  min-width: 200px;

  text-align: left;

  line-height: 1.7;
}

.member-name {
  margin-bottom: 6px;

  color: #333333;

  font-size: 15px;

  font-weight: bold;
}

.member-detail {
  color: #666666;

  font-size: 13px;

  word-break: break-word;
}

/* =====================================================
   商品內容
   ===================================================== */

.items-cell {
  width: 330px;
  min-width: 280px;

  text-align: left;
}

.order-item {
  padding: 10px 0;

  border-bottom:
    1px solid #eeeeee;
}

.order-item:first-child {
  padding-top: 0;
}

.order-item:last-child {
  padding-bottom: 0;

  border-bottom: none;
}

.item-name {
  margin-bottom: 5px;

  color: #4a3b2a;

  font-size: 14px;

  font-weight: bold;
}

.item-detail {
  margin-bottom: 2px;

  color: #666666;

  font-size: 13px;
}

.item-subtotal {
  margin-top: 4px;

  color: #b3443c;

  font-size: 13px;

  font-weight: bold;
}

.no-item {
  color: #999999;
}

/* =====================================================
   總金額
   ===================================================== */

.total-cell {
  width: 120px;

  text-align: center;

  color: #b3443c;

  font-size: 16px;

  font-weight: bold;
}

/* =====================================================
   狀態
   ===================================================== */

.status-cell {
  width: 120px;

  text-align: center;
}

.status-select {
  min-width: 100px;

  padding: 8px 10px;

  border: 1px solid #dddddd;

  border-radius: 6px;

  font-size: 13px;

  font-weight: bold;

  text-align: center;

  cursor: pointer;

  outline: none;
}

/* 待處理 */
.status-select.status-pending {
  background-color: #fff3d8;

  color: #2b1f0a;
}

/* 已完成 */
.status-select.status-completed {
  background-color: #e5f6eb;

  color: #0a3f1d;
}

/* 已取消 */
.status-select.status-cancelled {
  background-color: #fee2e2;

  color: #5f0202;
}



/* 待處理 */
.status-pending {
  background-color: #fff3d8;

  color: #95691f;
}

/* 已確認 */
.status-confirmed {
  background-color: #dbeafe;

  color: #1e40af;
}

/* 已付款 */
.status-paid {
  background-color: #dcfce7;

  color: #166534;
}

/* 已出貨 */
.status-shipped {
  background-color: #ede9fe;

  color: #5b21b6;
}

/* 已完成 */
.status-completed {
  background-color: #e5f6eb;

  color: #257641;
}

/* 已取消 */
.status-cancelled {
  background-color: #fee2e2;

  color: #991b1b;
}

/* 未知 */
.status-default {
  background-color: #eeeeee;

  color: #555555;
}

/* =====================================================
   建立時間
   ===================================================== */

.date-cell {
  width: 180px;
  min-width: 160px;

  text-align: center;

  color: #555555;

  font-size: 13px;
}

/* =====================================================
   Loading / Error
   ===================================================== */

.message-box,
.error-box {
  padding: 40px;

  background-color: white;

  border-radius: 8px;

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

@media (max-width: 768px) {
  .orders-page {
    padding: 10px;
  }

  .page-header {
    align-items: flex-start;

    flex-direction: column;

    gap: 15px;
  }

  th,
  td {
    padding: 10px 8px;
  }
}
</style>