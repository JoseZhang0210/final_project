<script setup>
import { onMounted, ref , computed } from "vue";
import { bookingPaymentApi } from "@/api/bookingPaymentApi";

const payments = ref([]);
const loading = ref(false);

const formTitle = ref("新增付款紀錄");
const message = ref("");
const messageType = ref("");

const form = ref(createEmptyForm());

const paymentMethods = ["信用卡", "現金", "轉帳", "LinePay"];
const paymentStatuses = ["未付款", "已付款", "已退款", "付款失敗"];

function createEmptyForm() {
  return {
    paymentId: null,
    bookingId: "",
    amount: 0,
    paymentMethod: "信用卡",
    paymentStatus: "未付款",
    transactionId: "",
  };
}

function clearForm() {
  form.value = createEmptyForm();
  formTitle.value = "新增付款紀錄";
}

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
}

// 載入付款資料
async function loadPayments() {
  currentPage.value = 1;
  loading.value = true;
  message.value = "";

  try {
    const data = await bookingPaymentApi.getAllPayments();
    payments.value = Array.isArray(data) ? data : data.content || [];
    console.log("SQL payment 資料：", payments.value);
  } catch (error) {
    console.error("讀取付款錯誤：", error);
    showMessage(error.message || "無法連線至付款 API", "error");
  } finally {
    loading.value = false;
  }
}

// 新增與修改付款
async function savePayment() {
  if (!form.value.bookingId) {
    showMessage("請輸入關聯訂單 ID", "error");
    return;
  }

  if (Number(form.value.amount) < 0) {
    showMessage("金額不可小於 0", "error");
    return;
  }

  const isEdit = form.value.paymentId !== null;
  const payload = {
    paymentId: form.value.paymentId,
    bookingId: Number(form.value.bookingId),
    amount: Number(form.value.amount),
    paymentMethod: form.value.paymentMethod,
    paymentStatus: form.value.paymentStatus,
    transactionId: form.value.transactionId || "",
  };

  try {
    if (isEdit) {
      await bookingPaymentApi.updatePaymentStatus(form.value.paymentId, payload);
      showMessage("付款紀錄修改成功", "success");
    } else {
      await bookingPaymentApi.createPayment(payload);
      showMessage("付款紀錄新增成功", "success");
    }
    
    clearForm();
    await loadPayments();
  } catch (error) {
    console.error("savePayment Error:", error);
    showMessage(error.message || "無法連線至付款 API", "error");
  }
}

function editPayment(payment) {
  form.value = {
    paymentId: payment.paymentId ?? payment.payment_id,
    bookingId: payment.bookingId ?? payment.booking_id ?? "",
    amount: payment.amount ?? 0,
    paymentMethod: payment.paymentMethod ?? payment.payment_method ?? "信用卡",
    paymentStatus: payment.paymentStatus ?? payment.payment_status ?? "未付款",
    transactionId: payment.transactionId ?? payment.transaction_id ?? "",
  };

  formTitle.value = `修改付款紀錄 ID：${form.value.paymentId}`;

  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
}

function formatPrice(price) {
  return new Intl.NumberFormat("zh-TW", {
    style: "currency",
    currency: "TWD",
    maximumFractionDigits: 0,
  }).format(price || 0);
}

function formatDateTimeShort(dateTimeStr) {
  if (!dateTimeStr) return "—";
  return String(dateTimeStr).replace("T", " ").slice(0, 16);
}

function getStatusClass(status) {
  return {
    available: status === "已付款",
    maintenance: status === "未付款" || status === "付款失敗",
    disabled: status === "已退款",
  };
}

onMounted(() => {
  loadPayments();
});

const currentPage = ref(1);
const itemsPerPage = 20;
const totalPages = computed(() => Math.ceil(payments.value.length / itemsPerPage));
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return payments.value.slice(start, start + itemsPerPage);
});
function nextPage() { if (currentPage.value < totalPages.value) currentPage.value++; }
function prevPage() { if (currentPage.value > 1) currentPage.value--; }

</script>

<template>
  <main class="payment-page">
    <header class="page-header">
      <div>
        <h1>付款紀錄管理</h1>
        <p>管理飯店各筆訂單的付款狀態與交易金額</p>
      </div>
      <button type="button" class="refresh-button" :disabled="loading" @click="loadPayments">
        {{ loading ? "讀取中…" : "重新整理" }}
      </button>
    </header>

    <div v-if="message" class="message" :class="messageType">
      {{ message }}
    </div>

    <!-- 表單區塊 -->
    <section class="admin-card">
      <h2>{{ formTitle }}</h2>

      <form @submit.prevent="savePayment">
        <div class="form-grid">
          <div class="form-group">
            <label for="bookingId">關聯訂單 ID *</label>
            <input
              id="bookingId"
              v-model.number="form.bookingId"
              type="number"
              placeholder="例如：1001"
              required
            />
          </div>

          <div class="form-group">
            <label for="amount">付款金額 *</label>
            <input
              id="amount"
              v-model.number="form.amount"
              type="number"
              min="0"
              required
            />
          </div>

          <div class="form-group">
            <label for="paymentMethod">付款方式 *</label>
            <select id="paymentMethod" v-model="form.paymentMethod">
              <option v-for="method in paymentMethods" :key="method" :value="method">
                {{ method }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="paymentStatus">付款狀態 *</label>
            <select id="paymentStatus" v-model="form.paymentStatus">
              <option v-for="status in paymentStatuses" :key="status" :value="status">
                {{ status }}
              </option>
            </select>
          </div>

          <div class="form-group full-width">
            <label for="transactionId">交易序號 (Transaction ID)</label>
            <input
              id="transactionId"
              v-model.trim="form.transactionId"
              type="text"
              placeholder="例如：TXN123456789"
            />
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn primary">
            {{ form.paymentId === null ? "新增紀錄" : "儲存修改" }}
          </button>

          <button type="button" class="btn secondary" @click="clearForm">
            清除表單
          </button>
        </div>
      </form>
    </section>

    <!-- 列表區塊 -->
    <section class="admin-card">
      <div class="table-header">
        <h2>付款列表</h2>
        <span>共 {{ payments.length }} 筆紀錄</span>
      </div>

      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>訂單 ID</th>
              <th>金額</th>
              <th>付款方式</th>
              <th>狀態</th>
              <th>交易序號</th>
              <th>建立時間</th>
              <th>付款時間</th>
              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="loading">
              <td colspan="9" class="empty">資料讀取中……</td>
            </tr>
            <tr v-for="payment in paginatedData" :key="payment.paymentId" v-else>
              <td>{{ payment.paymentId }}</td>
              <td>{{ payment.bookingId }}</td>
              <td>{{ formatPrice(payment.amount) }}</td>
              <td>{{ payment.paymentMethod }}</td>
              <td>
                <span class="status" :class="getStatusClass(payment.paymentStatus)">
                  {{ payment.paymentStatus }}
                </span>
              </td>
              <td>{{ payment.transactionId || "—" }}</td>
              <td>{{ formatDateTimeShort(payment.createdAt) }}</td>
              <td>{{ formatDateTimeShort(payment.paidAt) }}</td>
              <td class="action-cell">
                <button
                  type="button"
                  class="btn edit"
                  @click="editPayment(payment)"
                >
                  修改
                </button>
              </td>
            </tr>

            <tr v-if="!loading && payments.length === 0">
              <td colspan="9" class="empty">目前沒有付款紀錄</td>
            </tr>
          </tbody>
        </table>

      <div class="pagination-container" v-if="totalPages > 1">
        <button @click="prevPage" :disabled="currentPage === 1" class="page-btn">◀ 上一頁</button>
        <span class="page-info">第 {{ currentPage }} 頁 / 共 {{ totalPages }} 頁</span>
        <button @click="nextPage" :disabled="currentPage === totalPages" class="page-btn">下一頁 ▶</button>
      </div>
  

      </div>
    </section>
  </main>
</template>

<style scoped>
.payment-page {
  padding: 28px;
  color: #243447;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 22px;
  padding: 24px;
  background: white;
  box-shadow: 0 3px 12px rgb(0 0 0 / 6%);
}

.page-header h1 {
  margin: 0 0 8px;
  font-size: 30px;
}

.page-header p {
  margin: 0;
  color: #667085;
}

.refresh-button {
  padding: 10px 16px;
  color: white;
  background: #315b7d;
  border: none;
  border-radius: 7px;
  cursor: pointer;
}

.refresh-button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
}

.admin-card {
  margin-bottom: 24px;
  padding: 24px;
  background: white;
  border: 1px solid #e4e7ec;
  border-radius: 12px;
  box-shadow: 0 3px 12px rgb(0 0 0 / 6%);
}

.admin-card h2 {
  margin: 0 0 20px;
}

.message {
  margin-bottom: 20px;
  padding: 12px 16px;
  border-radius: 8px;
}

.message.success {
  color: #176b3a;
  background: #e9f8ef;
}

.message.error {
  color: #b42318;
  background: #feeceb;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.full-width {
  grid-column: 1 / -1;
}

label {
  font-weight: 600;
}

input,
select,
textarea {
  padding: 11px 12px;
  font: inherit;
  border: 1px solid #cfd4dc;
  border-radius: 7px;
}

input:focus,
select:focus,
textarea:focus {
  border-color: #315b7d;
  outline: none;
}

.form-actions,
.action-cell {
  display: flex;
  gap: 10px;
}

.form-actions {
  margin-top: 20px;
}

.btn {
  padding: 9px 15px;
  color: white;
  border: none;
  border-radius: 7px;
  cursor: pointer;
}

.primary {
  background: #315b7d;
}

.secondary {
  color: #344054;
  background: #e4e7ec;
}

.edit {
  background: #d59032;
}

.delete {
  background: #c84040;
}

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.table-wrapper {
  overflow-x: auto;
}

table {
  width: 100%;
  overflow: hidden;
  border-collapse: collapse;
  border-radius: 8px;
}

th,
td {
  padding: 13px 12px;
  text-align: left;
  border-bottom: 1px solid #e4e7ec;
  white-space: nowrap;
}

th {
  color: #ffffff;
  font-weight: 700;
  background-color: #4b3c34;
  border-bottom: 2px solid #3b2f29;
}

td {
  color: #344054;
  background-color: #ffffff;
}

tbody tr:hover td {
  background-color: #faf7f2;
}

.empty {
  padding: 30px;
  text-align: center;
  color: #667085;
}

.status {
  display: inline-block;
  min-width: 64px;
  padding: 6px 12px;
  color: #475467;
  text-align: center;
  white-space: nowrap;
  background-color: #f2f4f7;
  border-radius: 999px;
}

.status.available {
  color: #087443;
  background-color: #e7f8ef;
}

.status.maintenance {
  color: #b42318;
  background-color: #feeceb;
}

.status.disabled {
  color: #475467;
  background-color: #e4e7ec;
}

@media (max-width: 768px) {
  .payment-page {
    padding: 16px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .full-width {
    grid-column: auto;
  }
}
.pagination-container { display: flex; justify-content: center; align-items: center; margin-top: 20px; gap: 15px; } .page-btn { padding: 8px 16px; background-color: #3b82f6; color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: 500; transition: background-color 0.2s; } .page-btn:hover:not(:disabled) { background-color: #2563eb; } .page-btn:disabled { background-color: #d1d5db; cursor: not-allowed; } .page-info { font-weight: 500; color: #374151; }
</style>
