<template>
  <div class="orders-page">

    <!-- =====================================================
         頁面標題
         ===================================================== -->
    <div class="page-header">

      <div>
        <h1>訂單管理</h1>
        <p>查看會員訂單、付款狀態與商品交付情形</p>
      </div>

      <div class="header-actions">

        <!-- 訂單狀態篩選 -->
        <select
          v-model="selectedStatus"
          class="filter-select"
        >
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

        <button
          type="button"
          class="refresh-button"
          @click="loadOrders"
        >
          重新整理
        </button>

      </div>
    </div>


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


    <!-- 沒有訂單 -->
    <div
      v-else-if="orders.length === 0"
      class="message-box"
    >
      目前沒有訂單
    </div>


    <!-- 篩選後沒有資料 -->
    <div
      v-else-if="filteredOrders.length === 0"
      class="message-box"
    >
      此狀態目前沒有訂單
    </div>


    <!-- =====================================================
         訂單列表
         ===================================================== -->
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
            <th>原始金額</th>
            <th>優惠</th>
            <th>總金額</th>
            <th>付款狀態</th>
            <th>訂單狀態</th>
            <th>建立時間</th>
            <th>操作</th>
          </tr>
        </thead>


        <tbody>

          <tr
            v-for="order in filteredOrders"
            :key="order.orderId"
          >

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
                 商品
                 ================================================= -->
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
                    NT$
                    {{ formatPrice(item.price) }}
                  </div>


                  <!-- 一般顯示 -->
                  <div
                    v-if="
                      editingOrderId !==
                      order.orderId
                    "
                    class="item-detail"
                  >
                    數量：
                    {{ item.quantity }}
                  </div>


                  <!-- 編輯模式 -->
                  <div
                    v-else
                    class="edit-quantity-area"
                  >

                    <span class="quantity-label">
                      數量：
                    </span>

                    <input
                      v-model.number="item.quantity"
                      type="number"
                      min="1"
                      class="quantity-input"
                    />

                    <button
                      type="button"
                      class="save-item-button"
                      @click="
                        updateItemQuantity(
                          order,
                          item
                        )
                      "
                    >
                      儲存
                    </button>

                    <button
                      type="button"
                      class="delete-item-button"
                      @click="
                        deleteOrderItem(
                          order,
                          item
                        )
                      "
                    >
                      刪除
                    </button>

                  </div>


                  <div class="item-subtotal">
                    小計：
                    NT$
                    {{
                      formatPrice(
                        item.subtotal ??
                        (
                          Number(item.price) *
                          Number(item.quantity)
                        )
                      )
                    }}
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


            <!-- =================================================
                 原始金額
                 ================================================= -->
            <td class="amount-cell">
              NT$
              {{
                formatPrice(
                  order.originalAmount
                )
              }}
            </td>


            <!-- =================================================
                 優惠
                 ================================================= -->
            <td class="discount-cell">

              <template
                v-if="
                  Number(
                    order.discountAmount ?? 0
                  ) > 0
                "
              >
                - NT$
                {{
                  formatPrice(
                    order.discountAmount
                  )
                }}
              </template>

              <template v-else>
                -
              </template>

            </td>


            <!-- =================================================
                 最終金額
                 ================================================= -->
            <td class="total-cell">
              NT$
              {{
                formatPrice(
                  order.finalAmount
                )
              }}
            </td>


            <!-- =================================================
                 付款狀態
                 ================================================= -->
            <td class="payment-cell">

              <span
                class="payment-badge"
                :class="
                  getPaymentClass(
                    order.paymentStatus
                  )
                "
              >
                {{
                  getPaymentText(
                    order.paymentStatus
                  )
                }}
              </span>

            </td>


            <!-- =================================================
                 訂單處理狀態
                 ================================================= -->
            <td class="status-cell">

              <span
                class="status-badge"
                :class="
                  getStatusClass(
                    order.orderStatus
                  )
                "
              >
                {{
                  getStatusText(
                    order.orderStatus
                  )
                }}
              </span>

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

              <!-- ===============================================
                   PENDING + 尚未付款
                   可以修改訂單
                   =============================================== -->
              <template
                v-if="
                  order.orderStatus === 'PENDING' &&
                  order.paymentStatus !== 'PAID'
                "
              >

                <button
                  v-if="
                    editingOrderId !==
                    order.orderId
                  "
                  type="button"
                  class="edit-button"
                  @click="startEdit(order)"
                >
                  編輯商品
                </button>


                <button
                  v-else
                  type="button"
                  class="finish-button"
                  @click="finishEdit"
                >
                  完成編輯
                </button>


                <button
                  type="button"
                  class="cancel-button"
                  @click="cancelOrder(order)"
                >
                  取消訂單
                </button>

              </template>


              <!-- ===============================================
                   PENDING + 已付款
                   等管理員交付商品
                   =============================================== -->
              <template
                v-else-if="
                  order.orderStatus === 'PENDING' &&
                  order.paymentStatus === 'PAID'
                "
              >

                <div class="paid-note">
                  已付款
                </div>

                <button
                  type="button"
                  class="complete-button"
                  @click="completeOrder(order)"
                >
                  完成訂單
                </button>

              </template>


              <!-- ===============================================
                   已完成
                   =============================================== -->
              <template
                v-else-if="
                  order.orderStatus ===
                  'COMPLETED'
                "
              >

                <span class="completed-text">
                  已完成交付
                </span>

              </template>


              <!-- ===============================================
                   已取消
                   =============================================== -->
              <template v-else>

                <span class="disabled-edit-text">
                  已取消
                </span>

              </template>

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


const orders =
  ref([]);

const loading =
  ref(false);

const errorMessage =
  ref("");

const selectedStatus =
  ref("ALL");

const editingOrderId =
  ref(null);


// =====================================================
// 篩選
// =====================================================

const filteredOrders =
  computed(() => {

    if (
      selectedStatus.value ===
      "ALL"
    ) {
      return orders.value;
    }

    return orders.value.filter(
      (order) =>
        order.orderStatus ===
        selectedStatus.value
    );
  });


// =====================================================
// JWT
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
// 讀取全部訂單
// =====================================================

async function loadOrders() {

  loading.value = true;

  errorMessage.value = "";

  try {

    const response =
      await fetch(
        "/api/orders",
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
        "沒有權限讀取訂單資料"
      );
    }


    if (!response.ok) {

      const errorText =
        await response.text();

      throw new Error(
        errorText ||
        "訂單讀取失敗"
      );
    }


    const data =
      await response.json();


    console.log(
      "後台訂單資料：",
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
// 訂單狀態
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
      return "未知";
  }
}


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
// 付款狀態
// =====================================================

function getPaymentText(status) {

  switch (status) {

    case "PAID":
      return "已付款";

    case "PENDING":
      return "待付款";

    case "FAILED":
      return "付款失敗";

    case "REFUNDED":
      return "已退款";

    default:
      return "尚未付款";
  }
}


function getPaymentClass(status) {

  switch (status) {

    case "PAID":
      return "payment-paid";

    case "PENDING":
      return "payment-pending";

    case "FAILED":
      return "payment-failed";

    case "REFUNDED":
      return "payment-refunded";

    default:
      return "payment-none";
  }
}


// =====================================================
// 開始編輯
// =====================================================

function startEdit(order) {

  // 已付款不能再修改商品
  if (
    order.paymentStatus ===
    "PAID"
  ) {

    alert(
      "訂單已付款，不能再修改商品"
    );

    return;
  }


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
// 修改商品數量
// =====================================================

async function updateItemQuantity(
  order,
  item
) {

  if (
    order.paymentStatus ===
    "PAID"
  ) {

    alert(
      "訂單已付款，不能修改商品"
    );

    return;
  }


  const quantity =
    Number(
      item.quantity
    );


  if (
    !Number.isInteger(
      quantity
    ) ||
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


    if (!response.ok) {

      const errorText =
        await response.text();

      throw new Error(
        errorText ||
        "修改商品數量失敗"
      );
    }


    alert(
      `${item.productName} 數量修改成功`
    );


    await loadOrders();


    editingOrderId.value =
      order.orderId;


  } catch (error) {

    console.error(
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
// =====================================================

async function deleteOrderItem(
  order,
  item
) {

  if (
    order.paymentStatus ===
    "PAID"
  ) {

    alert(
      "訂單已付款，不能刪除商品"
    );

    return;
  }


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


    if (!response.ok) {

      const errorText =
        await response.text();

      throw new Error(
        errorText ||
        "刪除商品失敗"
      );
    }


    alert(
      `${item.productName} 已從訂單刪除`
    );


    await loadOrders();


    editingOrderId.value =
      order.orderId;


  } catch (error) {

    console.error(
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
// 取消訂單
// =====================================================

async function cancelOrder(order) {

  if (
    order.paymentStatus ===
    "PAID"
  ) {

    alert(
      "此訂單已付款，不能直接取消"
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


    editingOrderId.value =
      null;


    await loadOrders();


  } catch (error) {

    console.error(
      error
    );


    alert(
      error.message ||
      "取消訂單失敗"
    );
  }
}


// =====================================================
// 管理員確認商品已交付
//
// PENDING + PAID
// ↓
// COMPLETED
// =====================================================

async function completeOrder(order) {

  if (
    order.orderStatus !==
    "PENDING"
  ) {

    alert(
      "只有待處理訂單可以完成"
    );

    return;
  }


  if (
    order.paymentStatus !==
    "PAID"
  ) {

    alert(
      "會員尚未付款，不能完成訂單"
    );

    return;
  }


  const confirmed =
    confirm(
      `確認已將訂單 #${order.orderId} 的商品交付給會員嗎？`
    );


  if (!confirmed) {
    return;
  }


  try {

    const response =
      await fetch(
        `/api/orders/${order.orderId}/status?status=COMPLETED`,
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
        "沒有權限完成訂單"
      );
    }


    if (!response.ok) {

      const errorText =
        await response.text();

      throw new Error(
        errorText ||
        "完成訂單失敗"
      );
    }


    alert(
      `訂單 #${order.orderId} 已完成`
    );


    editingOrderId.value =
      null;


    await loadOrders();


  } catch (error) {

    console.error(
      "完成訂單失敗：",
      error
    );


    alert(
      error.message ||
      "完成訂單失敗"
    );
  }
}


// =====================================================
// 初始化
// =====================================================

onMounted(() => {

  loadOrders();
});
</script>


<style scoped>
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

.header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-select {
  min-width: 130px;
  padding: 9px 12px;
  border: 1px solid #dddddd;
  border-radius: 6px;
  background-color: white;
  color: #444444;
  font-size: 14px;
}

.refresh-button {
  padding: 9px 16px;
  border: none;
  border-radius: 6px;
  background-color: #b58a46;
  color: white;
  font-weight: bold;
  cursor: pointer;
}


/* =====================================================
   Table
   ===================================================== */

.table-card {
  overflow-x: auto;
  background-color: white;
  border-radius: 10px;
  box-shadow:
    0 4px 16px
    rgba(0, 0, 0, 0.06);
}

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
  padding: 16px 12px;
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
   訂單 / 會員
   ===================================================== */

.order-id-cell {
  text-align: center;
  font-weight: bold;
}

.member-cell {
  min-width: 210px;
  line-height: 1.7;
}

.member-name {
  margin-bottom: 6px;
  color: #333333;
  font-weight: bold;
}

.member-detail {
  color: #666666;
  font-size: 13px;
}


/* =====================================================
   商品
   ===================================================== */

.items-cell {
  min-width: 310px;
}

.order-item {
  padding: 10px 0;
  border-bottom:
    1px solid #eeeeee;
}

.order-item:last-child {
  border-bottom: none;
}

.item-name {
  margin-bottom: 5px;
  color: #4a3b2a;
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
   編輯
   ===================================================== */

.edit-quantity-area {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 6px;
  margin: 7px 0;
}

.quantity-input {
  width: 65px;
  padding: 6px;
  border:
    1px solid #cccccc;
  border-radius: 4px;
  text-align: center;
}

.save-item-button,
.delete-item-button {
  padding: 6px 9px;
  border-radius: 4px;
  cursor: pointer;
}

.save-item-button {
  border: none;
  background-color: #b58a46;
  color: white;
}

.delete-item-button {
  border:
    1px solid #b3443c;
  background-color: white;
  color: #b3443c;
}


/* =====================================================
   金額
   ===================================================== */

.amount-cell,
.discount-cell,
.total-cell {
  text-align: center;
  white-space: nowrap;
}

.discount-cell {
  color: #3f7d56;
}

.total-cell {
  color: #b3443c;
  font-size: 16px;
  font-weight: bold;
}


/* =====================================================
   Payment
   ===================================================== */

.payment-cell,
.status-cell {
  min-width: 105px;
  text-align: center;
}

.payment-badge,
.status-badge {
  display: inline-block;
  padding: 7px 11px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: bold;
  white-space: nowrap;
}

.payment-paid {
  background-color: #e8f1ff;
  color: #2f5f9f;
}

.payment-pending,
.payment-none {
  background-color: #fff3d8;
  color: #95691f;
}

.payment-failed {
  background-color: #fee2e2;
  color: #991b1b;
}

.payment-refunded {
  background-color: #eeeeee;
  color: #555555;
}


/* =====================================================
   Order Status
   ===================================================== */

.status-pending {
  background-color: #fff3d8;
  color: #95691f;
}

.status-completed {
  background-color: #e5f6eb;
  color: #257641;
}

.status-cancelled {
  background-color: #fee2e2;
  color: #991b1b;
}

.status-default {
  background-color: #eeeeee;
  color: #555555;
}


/* =====================================================
   日期
   ===================================================== */

.date-cell {
  min-width: 155px;
  text-align: center;
  color: #555555;
  font-size: 13px;
}


/* =====================================================
   操作
   ===================================================== */

.action-cell {
  min-width: 135px;
  text-align: center;
}

.edit-button,
.finish-button,
.cancel-button,
.complete-button {
  width: 100%;
  margin-bottom: 7px;
  padding: 8px 10px;
  border-radius: 5px;
  font-size: 13px;
  font-weight: bold;
  cursor: pointer;
}

.edit-button {
  border: none;
  background-color: #b58a46;
  color: white;
}

.finish-button {
  border: none;
  background-color: #666666;
  color: white;
}

.cancel-button {
  border:
    1px solid #b3443c;
  background-color: white;
  color: #b3443c;
}

.complete-button {
  border: none;
  background-color: #3f7d56;
  color: white;
}

.complete-button:hover {
  background-color: #306345;
}

.paid-note {
  margin-bottom: 7px;
  color: #2f5f9f;
  font-size: 12px;
  font-weight: bold;
}

.completed-text {
  color: #257641;
  font-weight: bold;
}

.disabled-edit-text {
  color: #aaaaaa;
  font-size: 13px;
}


/* =====================================================
   Message
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

@media (
  max-width: 768px
) {

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