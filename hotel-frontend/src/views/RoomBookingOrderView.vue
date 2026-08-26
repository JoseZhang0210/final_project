<script setup>
import { computed, onMounted, ref } from "vue";

const BOOKING_ORDER_API_URL = "/api/booking-orders";

const bookingOrders = ref([]);
const message = ref("");
const messageType = ref("");
const formTitle = ref("編輯訂房訂單");

// 搜尋條件
const searchCriteria = ref({
  bookingOrderId: "",
  memberId: "",
  orderStatus: "",
});

const form = ref(createEmptyForm());

function createEmptyForm() {
  return {
    bookingOrderId: null,
    memberId: "",
    orderStatus: "待付款",
    bookingTotalPrice: "",
    paymentId: "",
    createdAt: "",
  };
}

const hasMember = computed(() => {
  return String(form.value.memberId ?? "").trim() !== "";
});

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
}

function formatDateTime(dateTimeStr) {
  if (!dateTimeStr) return "";
  return dateTimeStr.replace("T", " ").slice(0, 19);
}

// 取得帶有 Bearer Token 的 Header
function getAuthHeaders() {
  const token = localStorage.getItem("token");

  const headers = {
    "Content-Type": "application/json",
  };

  if (token) {
    headers.Authorization = "Bearer " + token;
  }

  return headers;
}

// 1. 查詢訂單列表 (對應 BookingOrderController @GetMapping)
async function loadBookingOrders() {
  try {
    const params = new URLSearchParams();

    const bookingOrderId = String(
      searchCriteria.value.bookingOrderId ?? "",
    ).trim();
    if (bookingOrderId !== "") params.append("bookingOrderId", bookingOrderId);

    const memberId = String(searchCriteria.value.memberId ?? "").trim();
    if (memberId !== "") params.append("memberId", memberId);

    // 後端只接收 bookingOrderId 與 memberId
    const queryString = params.toString();
    const url = queryString
      ? `${BOOKING_ORDER_API_URL}?${queryString}`
      : BOOKING_ORDER_API_URL;

    const response = await fetch(url, {
      method: "GET",
      headers: getAuthHeaders(),
      credentials: "include",
    });

    if (!response.ok) {
      if (response.status === 403) {
        showMessage("權限不足或登入已過期 (403)", "error");
        return;
      }
      const errorData = await response.json().catch(() => ({}));
      showMessage(errorData.message || "讀取訂單資料失敗", "error");
      return;
    }

    const data = await response.json();
    let result = Array.isArray(data) ? data : data.content || [];

    // 後端 API 未支援 orderStatus 參數，由前端過濾狀態
    const orderStatus = String(searchCriteria.value.orderStatus ?? "").trim();
    if (orderStatus !== "") {
      result = result.filter(
        (order) => (order.orderStatus ?? order.order_status) === orderStatus,
      );
    }

    bookingOrders.value = result;
  } catch (error) {
    console.error("loadBookingOrders Error:", error);
    showMessage("無法連線至訂單 API", "error");
  }
}

function clearSearch() {
  searchCriteria.value = {
    bookingOrderId: "",
    memberId: "",
    orderStatus: "",
  };
  loadBookingOrders();
}

function clearForm() {
  form.value = createEmptyForm();
  formTitle.value = "新增訂房訂單";
  message.value = "";
}

// 2. 新增 / 修改 訂單 (對應 @PostMapping & @PutMapping)
async function saveBookingOrder() {
  if (!hasMember.value) {
    showMessage("請輸入會員 ID", "error");
    return;
  }

  const payload = {
    memberId: Number(form.value.memberId),
    orderStatus: form.value.orderStatus,
    bookingTotalPrice: form.value.bookingTotalPrice
      ? Number(form.value.bookingTotalPrice)
      : null,
    paymentId: form.value.paymentId ? Number(form.value.paymentId) : null,
  };

  if (form.value.createdAt) {
    payload.createdAt = form.value.createdAt;
  }

  const isEdit = form.value.bookingOrderId !== null;
  const url = isEdit
    ? `${BOOKING_ORDER_API_URL}/${form.value.bookingOrderId}`
    : BOOKING_ORDER_API_URL;

  try {
    const response = await fetch(url, {
      method: isEdit ? "PUT" : "POST",
      headers: getAuthHeaders(),
      credentials: "include",
      body: JSON.stringify(payload),
    });

    if (!response.ok) {
      if (response.status === 403) {
        showMessage("權限不足或登入已過期 (403)", "error");
        return;
      }
      const errorData = await response.json().catch(() => ({}));
      showMessage(errorData.message || "儲存失敗", "error");
      return;
    }

    showMessage(isEdit ? "修改成功" : "新增成功", "success");
    clearForm();
    await loadBookingOrders();
  } catch (error) {
    showMessage("無法連線至訂單 API", "error");
  }
}

// 帶入編輯資料
function editBookingOrder(order) {
  const orderId = order.bookingOrderId ?? order.booking_order_id;
  form.value = {
    bookingOrderId: orderId,
    memberId: order.memberId ?? order.member_id ?? "",
    orderStatus: order.orderStatus ?? order.order_status ?? "待付款",
    bookingTotalPrice:
      order.bookingTotalPrice ?? order.booking_total_price ?? "",
    paymentId: order.paymentId ?? order.payment_id ?? "",
    createdAt: order.createdAt ?? order.created_at ?? "",
  };

  formTitle.value = `修改訂單`;
  window.scrollTo({ top: 0, behavior: "smooth" });
}

// 3. 刪除訂單 (對應 @DeleteMapping)
async function deleteBookingOrder(id) {
  if (!confirm("確定要刪除這筆訂購訂單嗎？")) {
    return;
  }

  try {
    const response = await fetch(`${BOOKING_ORDER_API_URL}/${id}`, {
      method: "DELETE",
      headers: getAuthHeaders(),
      credentials: "include",
    });

    const resData = await response.json().catch(() => ({}));

    if (!response.ok) {
      if (response.status === 403) {
        showMessage("權限不足或登入已過期 (403)", "error");
        return;
      }
      showMessage(resData.message || "刪除失敗", "error");
      return;
    }

    showMessage(resData.message || "訂單已成功刪除！", "success");
    clearForm();
    await loadBookingOrders();
  } catch (error) {
    showMessage("無法連線至訂單 API", "error");
  }
}

onMounted(async () => {
  await loadBookingOrders();
});
</script>

<template>
  <div class="page">
    <section class="hero">
      <h1>訂房訂單管理</h1>
      <p>管理顧客訂房訂單狀態、會員資料與訂單總金額。</p>
    </section>

    <main class="container">
      <!-- 查詢條件區塊 -->
      <section class="card">
        <h2>條件查詢</h2>
        <div class="form-grid">
          <div class="form-group">
            <label>訂單 ID</label>
            <input v-model="searchCriteria.bookingOrderId" type="number" min="1" placeholder="搜尋訂單 ID" />
          </div>

          <div class="form-group">
            <label>會員 ID</label>
            <input v-model="searchCriteria.memberId" type="number" min="1" placeholder="搜尋會員 ID" />
          </div>

          <div class="form-group">
            <label>訂單狀態</label>
            <select v-model="searchCriteria.orderStatus">
              <option value="">全部狀態</option>
              <option value="待付款">待付款</option>
              <option value="訂單完成">訂單完成</option>
              <option value="訂單取消">訂單取消</option>
            </select>
          </div>
        </div>

        <div class="form-actions">
          <button type="button" class="btn-save" @click="loadBookingOrders">
            查詢
          </button>
          <button type="button" class="btn-clear" @click="clearSearch">
            重設查詢
          </button>
        </div>
      </section>

      <!-- 表單編輯/新增區塊 -->
      <section class="card">
        <h2>{{ formTitle }}</h2>

        <form @submit.prevent="saveBookingOrder">
          <div class="form-grid">
            <div class="form-group">
              <label>會員 ID *</label>
              <input v-model="form.memberId" type="number" min="1" required placeholder="請輸入會員 ID" />
            </div>

            <div class="form-group">
              <label>訂單總金額</label>
              <input v-model="form.bookingTotalPrice" type="number" min="0" step="0.01" placeholder="請輸入總金額" />
            </div>

            <div class="form-group">
              <label>付款方式 ID</label>
              <input v-model="form.paymentId" type="number" min="1" placeholder="請輸入付款方式 ID" />
            </div>

            <div class="form-group">
              <label>訂單狀態 *</label>
              <select v-model="form.orderStatus" required>
                <option value="待付款">待付款</option>
                <option value="訂單完成">訂單完成</option>
                <option value="訂單取消">訂單取消</option>
              </select>
            </div>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn-save">儲存</button>
            <button type="button" class="btn-clear" @click="clearForm">
              清除
            </button>
          </div>

          <p v-if="message" class="message" :class="messageType">
            {{ message }}
          </p>
        </form>
      </section>

      <!-- 列表區塊 -->
      <section class="card">
        <h2>訂單列表</h2>

        <div class="table-wrapper">
          <table>
            <thead>
              <tr>
                <th>訂單 ID</th>
                <th>會員 ID</th>
                <th>總金額</th>
                <th>訂單狀態</th>
                <th>建立時間</th>
                <th>付款方式 ID</th>
                <th>操作</th>
              </tr>
            </thead>

            <tbody>
              <tr v-if="bookingOrders.length === 0">
                <td colspan="7" class="empty">目前沒有訂單資料</td>
              </tr>

              <tr v-for="order in bookingOrders" :key="order.bookingOrderId ?? order.booking_order_id">
                <td>{{ order.bookingOrderId ?? order.booking_order_id }}</td>
                <td>{{ order.memberId ?? order.member_id ?? "" }}</td>
                <td>
                  {{
                    (order.bookingTotalPrice ?? order.booking_total_price) !=
                      null
                      ? `$${Number(order.bookingTotalPrice ?? order.booking_total_price).toLocaleString()}`
                      : "-"
                  }}
                </td>
                <td>{{ order.orderStatus ?? order.order_status }}</td>
                <td>
                  {{ formatDateTime(order.createdAt ?? order.created_at) }}
                </td>
                <td>{{ order.paymentId ?? order.payment_id ?? "-" }}</td>
                <td class="actions">
                  <button class="btn-edit" @click="editBookingOrder(order)">
                    修改
                  </button>
                  <button class="btn-delete" @click="
                    deleteBookingOrder(
                      order.bookingOrderId ?? order.booking_order_id,
                    )
                    ">
                    刪除
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
.page {
  min-height: 100vh;
  font-family: Arial, "Microsoft JhengHei", sans-serif;
  background: #f8f6f1;
  color: #333;
}

.hero {
  padding: 60px 20px;
  text-align: center;
  color: white;
  background:
    linear-gradient(rgba(0, 0, 0, 0.52), rgba(0, 0, 0, 0.52)),
    url("https://images.unsplash.com/photo-1566073771259-6a8506099945") center / cover no-repeat;
}

.hero h1 {
  margin: 0 0 12px;
  font-size: 40px;
}

.hero p {
  margin: 0;
  color: #f2ede5;
}

.container {
  width: min(1300px, 94%);
  margin: 42px auto 70px;
}

.card {
  margin-bottom: 28px;
  padding: 28px;
  border-radius: 14px;
  background: white;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08);
}

.card h2 {
  margin: 0 0 22px;
  color: #6f5328;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

label {
  color: #554536;
  font-size: 14px;
  font-weight: bold;
}

input,
select {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #d9d2c7;
  border-radius: 8px;
  font: inherit;
}

input:disabled,
select:disabled {
  background: #f1eee8;
  cursor: not-allowed;
}

.form-actions,
.actions {
  display: flex;
  gap: 10px;
}

.form-actions {
  margin-top: 22px;
}

button {
  border: 0;
  border-radius: 8px;
  padding: 11px 18px;
  cursor: pointer;
  font: inherit;
  font-weight: bold;
}

.btn-save {
  background: #b58a46;
  color: white;
}

.btn-clear {
  background: #eee9e1;
  color: #5c4d3d;
}

.btn-edit {
  background: #fff3d8;
  color: #95691f;
}

.btn-delete {
  background: #fde9e7;
  color: #b3443c;
}

.message {
  margin-top: 15px;
  padding: 11px 13px;
  border-radius: 8px;
  font-weight: bold;
}

.success {
  background: #e5f6eb;
  color: #257641;
}

.error {
  background: #fde9e7;
  color: #b3443c;
}

.table-wrapper {
  overflow-x: auto;
}

table {
  width: 100%;
  min-width: 900px;
  border-collapse: collapse;
}

thead {
  background: #4a3b2a;
  color: white;
}

th,
td {
  padding: 14px 12px;
  text-align: left;
  border-bottom: 1px solid #eee7dd;
}

.empty {
  text-align: center;
  color: #888;
}

@media (max-width: 768px) {
  .hero h1 {
    font-size: 31px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
