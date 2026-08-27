<template>
  <div class="orders-page">

    <!-- =====================================================
         頁面標題
         ===================================================== -->
    <div class="page-header">

      <div>
        <h1>訂單管理</h1>
        <p>查看會員訂單與商品內容</p>
      </div>

      <div class="header-actions">

        <!-- =========================
             訂單狀態篩選
             ========================= -->
        <select v-model="selectedStatus" class="filter-select">
          <option value="ALL">
            全部狀態
          </option>

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

        <!-- =========================
             重新整理
             ========================= -->
        <button type="button" class="refresh-button" @click="loadOrders">
          重新整理
        </button>

      </div>
    </div>


    <!-- =====================================================
         Loading
         ===================================================== -->
    <div v-if="loading" class="message-box">
      訂單讀取中...
    </div>


    <!-- =====================================================
         Error
         ===================================================== -->
    <div v-else-if="errorMessage" class="error-box">
      {{ errorMessage }}
    </div>


    <!-- =====================================================
         完全沒有訂單
         ===================================================== -->
    <div v-else-if="orders.length === 0" class="message-box">
      目前沒有訂單
    </div>


    <!-- =====================================================
         篩選後沒有資料
         ===================================================== -->
    <div v-else-if="filteredOrders.length === 0" class="message-box">
      此狀態目前沒有訂單
    </div>


    <!-- =====================================================
         訂單列表
         ===================================================== -->
    <div v-else class="table-card">

      <table>

        <!-- =========================
             表頭
             ========================= -->
        <thead>
          <tr>
            <th>訂單編號</th>
            <th>會員資料</th>
            <th>購買商品</th>
            <th>總金額</th>
            <th>狀態</th>
            <th>建立時間</th>
            <th>操作</th>
          </tr>
        </thead>


        <!-- =========================
             訂單內容
             ========================= -->
        <tbody>

          <tr v-for="order in filteredOrders" :key="order.orderId">

            <!-- =================================================
                 訂單編號
                 ================================================= -->
            <td class="order-id-cell">
              #{{ order.orderId }}
            </td>


            <!-- =================================================
                 會員資料
                 ================================================= -->
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


            <!-- =================================================
                 購買商品
                 ================================================= -->
            <td class="items-cell">

              <div v-if="
                order.items &&
                order.items.length > 0
              ">

                <div v-for="item in order.items" :key="item.productId" class="order-item">

                  <!-- 商品名稱 -->
                  <div class="item-name">
                    {{ item.productName }}
                  </div>


                  <!-- 單價 -->
                  <div class="item-detail">
                    單價：
                    ${{ formatPrice(item.price) }}
                  </div>


                  <!-- =========================
                       一般模式
                       ========================= -->
                  <div v-if="
                    editingOrderId !== order.orderId
                  " class="item-detail">
                    數量：
                    {{ item.quantity }}
                  </div>


                  <!-- =========================
                       編輯模式
                       ========================= -->
                  <div v-else class="edit-quantity-area">

                    <span class="quantity-label">
                      數量：
                    </span>

                    <input v-model.number="item.quantity" type="number" min="1" class="quantity-input" />

                    <button type="button" class="save-item-button" @click="
                      updateItemQuantity(
                        order,
                        item
                      )
                      ">
                      儲存
                    </button>

                    <button type="button" class="delete-item-button" @click="
                      deleteOrderItem(
                        order,
                        item
                      )
                      ">
                      刪除
                    </button>

                  </div>


                  <!-- 小計 -->
                  <div class="item-subtotal">
                    小計：
                    ${{
                      formatPrice(
                        Number(item.price) *
                        Number(item.quantity)
                      )
                    }}
                  </div>

                </div>

              </div>


              <!-- 無商品 -->
              <div v-else class="no-item">
                無商品資料
              </div>

            </td>


            <!-- =================================================
                 總金額
                 ================================================= -->
            <td class="total-cell">

              ${{
                formatPrice(
                  calculateOrderTotal(order)
                )
              }}

            </td>


            <!-- =================================================
                 訂單狀態
                 每張訂單可以單獨修改
                 ================================================= -->
            <td class="status-cell">

              <select class="status-select" :class="getStatusClass(
                order.status
              )
                " :value="order.status" @change="
                  updateOrderStatus(
                    order,
                    $event.target.value
                  )
                  ">

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


            <!-- =================================================
                 建立時間
                 ================================================= -->
            <td class="date-cell">
              {{ formatDate(order.orderDate) }}
            </td>


            <!-- =================================================
                 操作
                 ================================================= -->
            <td class="action-cell">

              <!-- 只有待處理可以修改商品 -->
              <template v-if="
                order.status === 'PENDING'
              ">

                <!-- 尚未進入編輯 -->
                <button v-if="
                  editingOrderId !==
                  order.orderId
                " type="button" class="edit-button" @click="
                    startEdit(order)
                    ">
                  編輯商品
                </button>


                <!-- 編輯中 -->
                <button v-else type="button" class="finish-button" @click="
                  finishEdit()
                  ">
                  完成編輯
                </button>

              </template>


              <!-- 完成或取消訂單不可修改 -->
              <span v-else class="disabled-edit-text">
                無法編輯
              </span>

            </td>

          </tr>

        </tbody>

      </table>

    </div>

  </div>
</template>


<script setup>

import {
  computed,
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
// 狀態篩選
//
// ALL
// PENDING
// COMPLETED
// CANCELLED
// =====================================================

const selectedStatus = ref("ALL");


// =====================================================
// 目前正在編輯哪一張訂單
//
// null = 沒有編輯
// =====================================================

const editingOrderId = ref(null);


// =====================================================
// 狀態篩選後的訂單
// =====================================================

const filteredOrders = computed(() => {

  if (
    selectedStatus.value === "ALL"
  ) {
    return orders.value;
  }

  return orders.value.filter(
    (order) =>
      order.status ===
      selectedStatus.value
  );
});


// =====================================================
// JWT Header
// =====================================================

function getAuthHeaders() {

  const token =
    localStorage.getItem("token");

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
        "/api/orders",
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
    // 權限問題
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
// 計算訂單總金額
//
// 使用目前畫面商品資料重新計算
// 修改數量時畫面也會立即更新
// =====================================================

function calculateOrderTotal(order) {

  if (
    !order.items ||
    order.items.length === 0
  ) {
    return 0;
  }

  return order.items.reduce(
    (total, item) => {

      return (
        total +
        Number(item.price ?? 0) *
        Number(item.quantity ?? 0)
      );

    },
    0
  );
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

    case "COMPLETED":
      return "status-completed";

    case "CANCELLED":
      return "status-cancelled";

    default:
      return "status-default";
  }
}


// =====================================================
// 修改訂單狀態
//
// PUT
// /api/orders/{orderId}/status?status=...
// =====================================================

async function updateOrderStatus(
  order,
  newStatus
) {

  // 沒有改變
  if (
    newStatus === order.status
  ) {
    return;
  }

  const oldStatus =
    order.status;


  try {

    const response =
      await fetch(
        `/api/orders/${order.orderId}/status?status=${newStatus}`,
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


    // =========================
    // 更新前端
    // =========================

    order.status =
      newStatus;


    // 如果不是待處理
    // 自動離開編輯模式

    if (
      newStatus !== "PENDING" &&
      editingOrderId.value ===
      order.orderId
    ) {

      editingOrderId.value =
        null;
    }


    alert(
      `訂單 #${order.orderId} 狀態已改為 ${getStatusText(newStatus)}`
    );


  } catch (error) {

    console.error(
      "更新狀態失敗：",
      error
    );


    order.status =
      oldStatus;


    alert(
      error.message ||
      "更新訂單狀態失敗"
    );


    await loadOrders();
  }
}


// =====================================================
// 開始編輯訂單商品
// =====================================================

function startEdit(order) {

  editingOrderId.value =
    order.orderId;
}


// =====================================================
// 完成編輯
// =====================================================

function finishEdit() {

  editingOrderId.value =
    null;
}


// =====================================================
// 修改訂單商品數量
//
// PUT
// /api/orders/{orderId}/items/{productId}
// ?quantity=2
// =====================================================

async function updateItemQuantity(
  order,
  item
) {

  const quantity =
    Number(item.quantity);


  // =========================
  // 基本檢查
  // =========================

  if (
    !Number.isInteger(quantity) ||
    quantity < 1
  ) {

    alert(
      "數量必須是大於 0 的整數"
    );

    await loadOrders();

    return;
  }


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
        "沒有權限修改訂單商品"
      );
    }


    if (!response.ok) {

      const errorText =
        await response.text();

      console.error(
        "修改商品數量失敗：",
        errorText
      );

      throw new Error(
        "修改商品數量失敗"
      );
    }


    alert(
      `${item.productName} 數量修改成功`
    );


    // 重新讀資料
    await loadOrders();


    // 保持這張訂單的編輯狀態
    editingOrderId.value =
      order.orderId;


  } catch (error) {

    console.error(
      "修改商品失敗：",
      error
    );


    alert(
      error.message ||
      "修改商品失敗"
    );


    await loadOrders();
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

  const confirmed =
    confirm(
      `確定要從訂單 #${order.orderId} 刪除「${item.productName}」嗎？`
    );


  if (!confirmed) {
    return;
  }


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
        "沒有權限刪除訂單商品"
      );
    }


    if (!response.ok) {

      const errorText =
        await response.text();

      console.error(
        "刪除商品失敗：",
        errorText
      );

      throw new Error(
        "刪除商品失敗"
      );
    }


    alert(
      `${item.productName} 已從訂單刪除`
    );


    await loadOrders();


    // 保持編輯模式
    editingOrderId.value =
      order.orderId;


  } catch (error) {

    console.error(
      "刪除商品失敗：",
      error
    );


    alert(
      error.message ||
      "刪除商品失敗"
    );


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
d
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

  justify-content:
    space-between;

  align-items: center;

  gap: 20px;

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
   Header 右側操作
   ===================================================== */

.header-actions {
  display: flex;

  align-items: center;

  gap: 10px;
}


/* =====================================================
   狀態篩選
   ===================================================== */

.filter-select {
  min-width: 130px;

  padding: 9px 12px;

  border:
    1px solid #dddddd;

  border-radius: 6px;

  background-color: white;

  color: #444444;

  font-size: 14px;

  cursor: pointer;

  outline: none;
}


/* =====================================================
   重新整理
   ===================================================== */

.refresh-button {
  padding: 9px 16px;

  border: none;

  border-radius: 6px;

  background-color:
    #b58a46;

  color: white;

  font-weight: bold;

  cursor: pointer;
}


.refresh-button:hover {
  background-color:
    #8f692f;
}


/* =====================================================
   Table Card
   ===================================================== */

.table-card {
  overflow-x: auto;

  background-color: white;

  border-radius: 10px;

  box-shadow:
    0 4px 16px rgba(0, 0, 0, 0.06);
}


/* =====================================================
   Table
   ===================================================== */

table {
  width: 100%;

  border-collapse: collapse;
}


thead {
  background-color:
    #4a3b2a;

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
  background-color:
    #fcfaf6;
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

  min-width: 210px;

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
   商品
   ===================================================== */

.items-cell {
  width: 360px;

  min-width: 330px;

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
   編輯商品
   ===================================================== */

.edit-quantity-area {
  display: flex;

  align-items: center;

  flex-wrap: wrap;

  gap: 6px;

  margin: 7px 0;
}


.quantity-label {
  color: #666666;

  font-size: 13px;
}


.quantity-input {
  width: 65px;

  padding: 6px 7px;

  border:
    1px solid #cccccc;

  border-radius: 4px;

  font-size: 13px;

  text-align: center;

  outline: none;
}


.quantity-input:focus {
  border-color:
    #b58a46;
}


.save-item-button {
  padding: 6px 10px;

  border: none;

  border-radius: 4px;

  background-color:
    #b58a46;

  color: white;

  cursor: pointer;
}


.save-item-button:hover {
  background-color:
    #8f692f;
}


.delete-item-button {
  padding: 6px 10px;

  border:
    1px solid #b3443c;

  border-radius: 4px;

  background-color: white;

  color: #b3443c;

  cursor: pointer;
}


.delete-item-button:hover {
  background-color:
    #fde9e7;
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

  border:
    1px solid #dddddd;

  border-radius: 6px;

  font-size: 13px;

  font-weight: bold;

  text-align: center;

  cursor: pointer;

  outline: none;
}


/* 待處理 */
.status-pending {
  background-color:
    #fff3d8;

  color: #95691f;
}


/* 已完成 */
.status-completed {
  background-color:
    #e5f6eb;

  color: #257641;
}


/* 已取消 */
.status-cancelled {
  background-color:
    #fee2e2;

  color: #991b1b;
}


/* 未知 */
.status-default {
  background-color:
    #eeeeee;

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
   操作
   ===================================================== */

.action-cell {
  width: 120px;

  min-width: 110px;

  text-align: center;
}


.edit-button,
.finish-button {
  padding: 8px 12px;

  border: none;

  border-radius: 5px;

  color: white;

  font-size: 13px;

  font-weight: bold;

  cursor: pointer;
}


.edit-button {
  background-color:
    #b58a46;
}


.edit-button:hover {
  background-color:
    #8f692f;
}


.finish-button {
  background-color:
    #666666;
}


.finish-button:hover {
  background-color:
    #444444;
}


.disabled-edit-text {
  color: #aaaaaa;

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

  background-color:
    #fde9e7;
}


/* =====================================================
   RWD
   ===================================================== */

@media (max-width: 768px) {

  .orders-page {
    padding: 10px;
  }


  .page-header {
    align-items:
      flex-start;

    flex-direction:
      column;
  }


  .header-actions {
    width: 100%;

    flex-wrap: wrap;
  }


  th,
  td {
    padding: 10px 8px;
  }
}
</style>