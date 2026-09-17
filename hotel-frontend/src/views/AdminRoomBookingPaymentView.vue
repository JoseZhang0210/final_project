<script setup>
import { onMounted, ref, computed } from "vue";
import { bookingPaymentApi } from "@/api/bookingPaymentApi";
import { bookingApi } from "@/api/bookingApi";

const payments = ref([]);
const bookings = ref([]);
const loading = ref(false);
const message = ref("");
const messageType = ref("");

// 搜尋條件
const searchBookingId = ref("");
const searchStatus = ref("");
const paymentStatuses = ["未付款", "已付款", "已退款", "付款失敗"];

// 行內修改狀態
const editingId = ref(null);
const editingStatus = ref("");

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
  setTimeout(() => { message.value = ""; }, 3000);
}

// 讀取全部付款資料與訂房資料以取得會員 ID
async function loadPayments() {
  loading.value = true;
  message.value = "";
  try {
    const [paymentData, bookingData] = await Promise.all([
      bookingPaymentApi.getAllPayments(),
      bookingApi.getAllBookings().catch(() => []) // 容錯處理
    ]);
    payments.value = Array.isArray(paymentData) ? paymentData : paymentData.content || [];
    bookings.value = Array.isArray(bookingData) ? bookingData : bookingData.content || [];
  } catch (error) {
    showMessage(error.message || "無法連線至付款 API", "error");
  } finally {
    loading.value = false;
  }
}

function getMemberIdForPayment(bookingId) {
  const booking = bookings.value.find(b => b.bookingId === bookingId || b.booking_id === bookingId);
  return booking ? (booking.memberId ?? booking.member_id) : "—";
}

// 前端篩選後的資料
const filteredPayments = computed(() => {
  return payments.value.filter((p) => {
    const matchBookingId = searchBookingId.value
      ? String(p.bookingId).includes(String(searchBookingId.value))
      : true;
    const matchStatus = searchStatus.value
      ? p.paymentStatus === searchStatus.value
      : true;
    return matchBookingId && matchStatus;
  });
});

function clearSearch() {
  searchBookingId.value = "";
  searchStatus.value = "";
  currentPage.value = 1;
}

// 開啟行內狀態修改
function startEdit(payment) {
  editingId.value = payment.paymentId;
  editingStatus.value = payment.paymentStatus;
}

function cancelEdit() {
  editingId.value = null;
  editingStatus.value = "";
}

// 儲存付款狀態修改（觸發後端自動記錄 paidAt）
async function saveStatus(payment) {
  try {
    await bookingPaymentApi.updatePaymentStatus(payment.paymentId, {
      paymentStatus: editingStatus.value,
    });
    showMessage(`付款紀錄 #${payment.paymentId} 狀態已更新`, "success");
    cancelEdit();
    await loadPayments();
  } catch (error) {
    showMessage(error.message || "更新失敗", "error");
  }
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
    paid: status === "已付款",
    unpaid: status === "未付款" || status === "付款失敗",
    refunded: status === "已退款",
  };
}

onMounted(() => {
  loadPayments();
});

// 分頁（基於篩選後的資料）
const currentPage = ref(1);
const itemsPerPage = 20;
const totalPages = computed(() => Math.ceil(filteredPayments.value.length / itemsPerPage));

const visiblePages = computed(() => {
  const pages = [];
  const maxVisible = 5;
  let start = Math.max(1, currentPage.value - 2);
  let end = Math.min(totalPages.value, start + maxVisible - 1);
  if (end - start + 1 < maxVisible) {
    start = Math.max(1, end - maxVisible + 1);
  }
  for (let page = start; page <= end; page++) {
    pages.push(page);
  }
  return pages;
});
const sortKey = ref("paymentId");
const sortOrder = ref("desc");

function toggleSort(key) {
  if (sortKey.value === key) {
    sortOrder.value = sortOrder.value === "asc" ? "desc" : "asc";
  } else {
    sortKey.value = key;
    sortOrder.value = "desc";
  }
}

const sortedFilteredPayments = computed(() => {
  return [...filteredPayments.value].sort((a, b) => {
    let valA, valB;
    if (sortKey.value === 'paymentId') {
      valA = Number(a.paymentId);
      valB = Number(b.paymentId);
    } else if (sortKey.value === 'bookingId') {
      valA = Number(a.bookingId);
      valB = Number(b.bookingId);
    } else {
      return 0;
    }
    
    if (valA < valB) return sortOrder.value === 'asc' ? -1 : 1;
    if (valA > valB) return sortOrder.value === 'asc' ? 1 : -1;
    return 0;
  });
});

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return sortedFilteredPayments.value.slice(start, start + itemsPerPage);
});
function nextPage() {
  if (currentPage.value < totalPages.value) currentPage.value++;
}
function prevPage() {
  if (currentPage.value > 1) currentPage.value--;
}
</script>

<template>
  <main class="payment-page">
    <header class="page-header">
      <div>
        <h1>付款紀錄管理</h1>
        <p>查詢並管理各筆訂單的付款狀態。付款紀錄由系統於訂房時自動建立，不可手動新增。</p>
      </div>
      <button type="button" class="refresh-button" :disabled="loading" @click="loadPayments">
        {{ loading ? "讀取中…" : "重新整理" }}
      </button>
    </header>

    <div v-if="message" class="message" :class="messageType">
      {{ message }}
    </div>

    <!-- 搜尋區塊 -->
    <section class="admin-card">
      <h2>搜尋付款紀錄</h2>
      <div class="search-grid">
        <div class="form-group">
          <label for="searchBookingId">訂單 ID</label>
          <input
            id="searchBookingId"
            v-model="searchBookingId"
            type="number"
            placeholder="輸入訂單 ID"
            @input="currentPage = 1"
          />
        </div>
        <div class="form-group">
          <label for="searchStatus">付款狀態</label>
          <select id="searchStatus" v-model="searchStatus" @change="currentPage = 1">
            <option value="">全部狀態</option>
            <option v-for="s in paymentStatuses" :key="s" :value="s">{{ s }}</option>
          </select>
        </div>
        <div class="form-group align-end">
          <button type="button" class="btn secondary" @click="clearSearch">清除搜尋</button>
        </div>
      </div>
    </section>

    <!-- 列表區塊 -->
    <section class="admin-card">
      <div class="table-header">
        <h2>付款列表</h2>
        <span>共 {{ filteredPayments.length }} 筆紀錄</span>
      </div>

      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th @click="toggleSort('paymentId')" class="sortable">
                付款 ID <span v-if="sortKey === 'paymentId'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span>
              </th>
              <th @click="toggleSort('bookingId')" class="sortable">
                訂單 ID <span v-if="sortKey === 'bookingId'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span>
              </th>
              <th>會員ID</th>
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
            <template v-else>
              <tr v-if="paginatedData.length === 0">
                <td colspan="9" class="empty">查無符合條件的付款紀錄</td>
              </tr>
              <tr v-for="payment in paginatedData" :key="payment.paymentId">
                <td>{{ payment.paymentId }}</td>
                <td>{{ payment.bookingId }}</td>
                <td>{{ getMemberIdForPayment(payment.bookingId) }}</td>
                <td>{{ formatPrice(payment.amount) }}</td>
                <td>{{ payment.paymentMethod }}</td>
                <td>
                  <!-- 行內狀態修改 -->
                  <template v-if="editingId === payment.paymentId">
                    <select v-model="editingStatus" class="inline-select">
                      <option v-for="s in paymentStatuses" :key="s" :value="s">{{ s }}</option>
                    </select>
                  </template>
                  <template v-else>
                    <span class="status" :class="getStatusClass(payment.paymentStatus)">
                      {{ payment.paymentStatus }}
                    </span>
                  </template>
                </td>
                <td>{{ payment.transactionId || "—" }}</td>
                <td>{{ formatDateTimeShort(payment.createdAt) }}</td>
                <td>{{ formatDateTimeShort(payment.paidAt) }}</td>
                <td class="action-cell">
                  <template v-if="editingId === payment.paymentId">
                    <button type="button" class="btn save" @click="saveStatus(payment)">儲存</button>
                    <button type="button" class="btn secondary-sm" @click="cancelEdit">取消</button>
                  </template>
                  <template v-else>
                    <button type="button" class="btn edit" @click="startEdit(payment)">改狀態</button>
                  </template>
                </td>
              </tr>
            </template>
          </tbody>
        </table>
      </div>

      <div class="pagination-area" v-if="totalPages > 1">
        <div class="pagination-info">
          第 <strong>{{ currentPage }}</strong> 頁 ／ 共 <strong>{{ totalPages }}</strong> 頁
        </div>

        <div class="pagination">
          <button type="button" class="page-button" :disabled="currentPage === 1" @click="currentPage = 1">«</button>
          <button type="button" class="page-button" :disabled="currentPage === 1" @click="prevPage">‹</button>
          
          <button 
            v-for="page in visiblePages" 
            :key="page" 
            type="button" 
            class="page-button" 
            :class="{ active: currentPage === page }" 
            @click="currentPage = page"
          >
            {{ page }}
          </button>
          
          <button type="button" class="page-button" :disabled="currentPage === totalPages" @click="nextPage">›</button>
          <button type="button" class="page-button" :disabled="currentPage === totalPages" @click="currentPage = totalPages">»</button>
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

.search-grid {
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  gap: 18px;
  align-items: end;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.align-end {
  align-self: flex-end;
}

label {
  font-weight: 600;
}

input,
select {
  padding: 11px 12px;
  font: inherit;
  border: 1px solid #cfd4dc;
  border-radius: 7px;
}

input:focus,
.inline-select:focus {
  border-color: #243447;
}

.sortable {
  cursor: pointer;
  user-select: none;
}

.sortable:hover {
  background-color: rgba(0,0,0,0.05);
}

.inline-select {
  padding: 6px 8px;
  font-size: 13px;
  border-radius: 6px;
}

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
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
  padding: 5px 12px;
  text-align: center;
  white-space: nowrap;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
  color: #475467;
  background-color: #f2f4f7;
}

.status.paid {
  color: #087443;
  background-color: #e7f8ef;
}

.status.unpaid {
  color: #b42318;
  background-color: #feeceb;
}

.status.refunded {
  color: #475467;
  background-color: #e4e7ec;
}

.action-cell {
  display: flex;
  gap: 6px;
}

.btn {
  padding: 7px 12px;
  color: white;
  border: none;
  border-radius: 7px;
  cursor: pointer;
  font-size: 13px;
  white-space: nowrap;
}

.save {
  background: #2e7d32;
}

.edit {
  background: #d59032;
}

.secondary {
  color: #344054;
  background: #e4e7ec;
}

.secondary-sm {
  padding: 7px 10px;
  color: #344054;
  background: #e4e7ec;
  border: none;
  border-radius: 7px;
  cursor: pointer;
  font-size: 13px;
}

.pagination-area { display: flex; justify-content: space-between; align-items: center; gap: 20px; margin-top: 24px; padding-top: 20px; border-top: 1px solid #eee7de; }
.pagination-info { color: #857a70; font-size: 13px; }
.pagination-info strong { color: #9b7435; }
.pagination { display: flex; align-items: center; gap: 6px; }
.page-button { min-width: 36px; height: 36px; padding: 0 10px; border: 1px solid #ded5c9; border-radius: 6px; background-color: white; color: #625649; cursor: pointer; transition: 0.2s; }
.page-button:hover:not(:disabled) { border-color: #b58a46; color: #9b7435; }
.page-button.active { border-color: #b58a46; background-color: #b58a46; color: white; font-weight: bold; }
.page-button:disabled { background-color: #f2f0ec; color: #bbb5ad; cursor: not-allowed; }

@media (max-width: 768px) {
  .payment-page {
    padding: 16px;
  }

  .search-grid {
    grid-template-columns: 1fr;
  }
}
</style>
