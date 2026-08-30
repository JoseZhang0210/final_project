<script setup>
import { computed, onMounted, ref } from "vue";
import { bookingApi } from "@/api/bookingApi";
import { roomTypeApi } from "@/api/roomTypeApi";
import { roomApi } from "@/api/roomApi";
import { bookingPaymentApi } from "@/api/bookingPaymentApi";
import { fetchClient } from "@/api/apiClient"; // for BOOKING_ORDER_API_URL
import { useRouter } from "vue-router";

const BOOKING_ORDER_API_URL = "/api/booking-orders";

// 選單資料（初始化為空陣列）
const bookingOrders = ref([]);
const roomTypes = ref([]);
const rooms = ref([]);
const payments = ref([]);
const router = useRouter();

// 核心資料
const bookings = ref([]);
const message = ref("");
const messageType = ref("");
const formTitle = ref("新增/編輯訂房明細");

// 查詢條件
const searchCriteria = ref({
  checkInDate: "",
  status: "",
});

const bookingStatuses = ["待入住", "已入住", "已完成", "已取消"];

const form = ref(createEmptyForm());

function createEmptyForm() {
  return {
    bookingId: "",
    bookingOrderId: "",
    roomTypeId: "",
    roomId: "",
    checkInDate: "",
    checkOutDate: "",
    guestNum: 1,
    bookingPrice: 0,
    bookingStatus: "待入住",
  };
}

// 支援駝峰與底線命名格式
const availableRooms = computed(() => {
  if (!form.value.roomTypeId) {
    return [];
  }

  const selectedRoomTypeId = Number(form.value.roomTypeId);

  return rooms.value.filter((room) => {
    const roomTypeId =
      room.roomTypeId ??
      room.room_type_id ??
      room.roomType?.roomTypeId;

    return Number(roomTypeId) === selectedRoomTypeId;
  });
});

const stayNights = computed(() => {
  if (!form.value.checkInDate || !form.value.checkOutDate) return 0;
  const checkIn = new Date(form.value.checkInDate);
  const checkOut = new Date(form.value.checkOutDate);
  const difference = checkOut - checkIn;
  return Math.max(0, Math.ceil(difference / (1000 * 60 * 60 * 24)));
});

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
}

function clearForm() {
  form.value = createEmptyForm();
  formTitle.value = "新增訂房明細";
  message.value = "";
}

async function loadSelectOptions() {
  try {
    const [orderData, roomTypeData, roomData, paymentData] = await Promise.all([
      fetchClient(BOOKING_ORDER_API_URL, { method: "GET" }).catch(() => []),
      roomTypeApi.getAllRoomTypes().catch(() => []),
      roomApi.getAllRooms().catch(() => []),
      bookingPaymentApi.getAllPayments().catch(() => []),
    ]);

    bookingOrders.value = orderData;
    roomTypes.value = roomTypeData;
    rooms.value = roomData;
    payments.value = Array.isArray(paymentData) ? paymentData : paymentData.content || [];

    console.log("訂單選項：", bookingOrders.value);
    console.log("房型選項：", roomTypes.value);
    console.log("房間選項：", rooms.value);
    console.log("付款紀錄：", payments.value);
  } catch (error) {
    console.error("載入下拉選單錯誤：", error);
    showMessage(error.message || "無法載入下拉選單", "error");
  }
}

// 1. 載入與條件查詢 (對應 @GetMapping)
async function loadBookings() {
  currentPage.value = 1;
  try {
    // 依據條件切換 API Endpoint
    let data;
    if (
      searchCriteria.value.memberId ||
      searchCriteria.value.roomTypeId ||
      searchCriteria.value.roomId ||
      searchCriteria.value.checkInDate ||
      searchCriteria.value.checkOutDate ||
      searchCriteria.value.bookingStatus
    ) {
      data = await bookingApi.searchBookings({
        memberId: searchCriteria.value.memberId || null,
        roomTypeId: searchCriteria.value.roomTypeId || null,
        roomId: searchCriteria.value.roomId || null,
        checkInDate: searchCriteria.value.checkInDate || null,
        checkOutDate: searchCriteria.value.checkOutDate || null,
        bookingStatus: searchCriteria.value.bookingStatus || null
      });
    } else {
      data = await bookingApi.getAllBookings();
    }

    bookings.value = Array.isArray(data) ? data : data ? [data] : [];
  } catch (error) {
    console.error("loadBookings Error:", error);
    showMessage(error.message || "無法連線至預訂 API", "error");
  }
}

function clearSearch() {
  currentPage.value = 1;
  searchCriteria.value = { memberId: "", roomTypeId: "", roomId: "", checkInDate: "", checkOutDate: "", bookingStatus: "" };
  loadBookings();
}

function getOrderLabel(orderId) {
  if (!orderId) {
    return "未指定訂單";
  }

  const order = bookingOrders.value.find(
    (item) => Number(item.bookingOrderId) === Number(orderId),
  );

  if (!order) {
    return `訂單 ${orderId}`;
  }

  return `訂單 ${order.bookingOrderId}`;
}

function getRoomTypeName(roomTypeId) {
  if (!roomTypeId) return "未指定房型";
  const found = roomTypes.value.find((item) => item.roomTypeId === Number(roomTypeId));
  return found ? (found.typeName ?? found.type_name) : `編號 ${roomTypeId}`;
}

function getRoomNumber(roomId) {
  if (!roomId) return "尚未分配";
  const found = rooms.value.find((item) => item.roomId === Number(roomId));
  return found ? (found.roomNumber ?? found.room_number) : `編號 ${roomId}`;
}

function getPaymentForBooking(bookingId) {
  return payments.value.find(p => p.bookingId === bookingId || p.booking_id === bookingId);
}

function getPaymentStatus(bookingId) {
  const payment = getPaymentForBooking(bookingId);
  return payment ? (payment.paymentStatus ?? payment.payment_status) : "無付款紀錄";
}

function goToPayment(bookingId) {
  router.push({ name: 'admin-booking-payments' });
}

function changeRoomType() {
  // 1. 清空已選擇的房號
  form.value.roomId = "";
  const selectedRoomType = roomTypes.value.find(
    (item) =>
      Number(item.roomTypeId ?? item.room_type_id) ===
      Number(form.value.roomTypeId),
  );
  if (selectedRoomType && form.value.guestNum > selectedRoomType.capacity) {
    form.value.guestNum = selectedRoomType.capacity;
  }
  calculatePrice();
}

function calculatePrice() {
  // 1. 防呆：若未選擇房型或住宿天數小於等於 0，價格歸零
  if (!form.value.roomTypeId || stayNights.value <= 0) {
    form.value.bookingPrice = 0;
    return;
  }

  // 2. 尋找匹配的房型物件（相容駝峰與底線命名）
  const selectedRoomType = roomTypes.value.find(
    (item) =>
      Number(item.roomTypeId ?? item.room_type_id) ===
      Number(form.value.roomTypeId),
  );
  if (!selectedRoomType || stayNights.value === 0) {
    form.value.bookingPrice = 0;
    return;
  }
  form.value.bookingPrice = selectedRoomType.pricePerNight * stayNights.value;
}

// 2. 儲存/更新預訂 (對應 PUT /api/bookings/{id})
async function saveBooking() {
  

  if (!form.value.bookingOrderId) {
    showMessage("請選擇訂房訂單", "error");
    return;
  }

  if (!form.value.roomTypeId) {
    showMessage("請選擇房型", "error");
    return;
  }

  if (!form.value.checkInDate || !form.value.checkOutDate) {
    showMessage("請選擇入住及退房日期", "error");
    return;
  }

  if (stayNights.value < 1) {
    showMessage("退房日期必須晚於入住日期", "error");
    return;
  }

  const payload = {
    bookingId: form.value.bookingId,
    bookingOrderId: Number(form.value.bookingOrderId),
    roomTypeId: Number(form.value.roomTypeId),
    roomId: form.value.roomId ? Number(form.value.roomId) : null,
    checkInDate: form.value.checkInDate,
    checkOutDate: form.value.checkOutDate,
    guestNum: Number(form.value.guestNum),
    bookingPrice: Number(form.value.bookingPrice),
    bookingStatus: form.value.bookingStatus,
  };

  try {
    await bookingApi.updateBooking(form.value.bookingId, payload);
    showMessage("訂房明細修改成功", "success");
    clearForm();
    await loadBookings();
  } catch (error) {
    showMessage(error.message || "無法連線至預訂 API", "error");
  }
}

function editBooking(booking) {
  form.value = {
    bookingId: booking.bookingId,
    bookingOrderId: booking.bookingOrderId ?? booking.booking_order_id ?? "",
    roomTypeId: booking.roomTypeId ?? booking.room_type_id ?? "",
    roomId: booking.roomId ?? booking.room_id ?? "",
    checkInDate: booking.checkInDate ?? booking.check_in_date ?? "",
    checkOutDate: booking.checkOutDate ?? booking.check_out_date ?? "",
    guestNum: booking.guestNum ?? booking.guest_num ?? 1,
    bookingPrice: booking.bookingPrice ?? booking.booking_price ?? 0,
    bookingStatus: booking.bookingStatus ?? booking.booking_status ?? "待確認",
  };
  formTitle.value = `修改訂房明細`;

  const payment = getPaymentForBooking(booking.bookingId);
  if (payment) {
    form.value.paymentMethod = payment.paymentMethod ?? payment.payment_method;
    form.value.transactionId = payment.transactionId ?? payment.transaction_id;
    form.value.paidAt = payment.paidAt ?? payment.paid_at;
  }

  window.scrollTo({ top: 0, behavior: "smooth" });
}

// 3. 刪除預訂 (對應 DELETE /api/bookings/{id})
async function deleteBooking(id) {
  if (!window.confirm("確定刪除這筆訂房明細嗎？")) {
    return;
  }

  try {
    await bookingApi.deleteBooking(id);
    showMessage("訂房明細已刪除", "success");
    if (form.value.bookingId === id) {
      clearForm();
    }
    await loadBookings();
  } catch (error) {
    showMessage(error.message || "刪除失敗", "error");
  }
}

function formatPrice(price) {
  return new Intl.NumberFormat("zh-TW", {
    style: "currency",
    currency: "TWD",
    maximumFractionDigits: 0,
  }).format(price || 0);
}

onMounted(async () => {
  await loadSelectOptions();
  await loadBookings();
});

const currentPage = ref(1);
const itemsPerPage = 20;
const totalPages = computed(() => Math.ceil(bookings.value.length / itemsPerPage));
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return bookings.value.slice(start, start + itemsPerPage);
});
function nextPage() { if (currentPage.value < totalPages.value) currentPage.value++; }
function prevPage() { if (currentPage.value > 1) currentPage.value--; }

</script>

<template>
  <main class="booking-page">
    <header class="page-header">
      <h1>訂房明細管理</h1>
      <p>管理入住日期、退房日期、房型、房號及訂房狀態</p>
    </header>

    <div v-if="message" class="message" :class="messageType">
      {{ message }}
    </div>

    <!-- 條件查詢區塊 -->
    <section class="admin-card">
      <h2>條件查詢</h2>
      <div class="form-grid">
        <div class="form-group">
          <label>會員 ID</label>
          <input v-model="searchCriteria.memberId" type="number" placeholder="輸入會員 ID" />
        </div>
        <div class="form-group">
          <label>房型</label>
          <select v-model="searchCriteria.roomTypeId">
            <option value="">全部房型</option>
            <option v-for="roomType in roomTypes" :key="roomType.roomTypeId" :value="roomType.roomTypeId">
              {{ roomType.typeName }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label>房號</label>
          <select v-model="searchCriteria.roomId">
            <option value="">全部房號</option>
            <option v-for="room in rooms" :key="room.roomId" :value="room.roomId">
              {{ room.roomNumber }}
            </option>
          </select>
        </div>
        <div class="form-group">
          <label>入住日期</label>
          <input v-model="searchCriteria.checkInDate" type="date" />
        </div>
        <div class="form-group">
          <label>退房日期</label>
          <input v-model="searchCriteria.checkOutDate" type="date" />
        </div>
        <div class="form-group">
          <label>預訂狀態</label>
          <select v-model="searchCriteria.bookingStatus">
            <option value="">全部狀態</option>
            <option v-for="status in bookingStatuses" :key="status" :value="status">
              {{ status }}
            </option>
          </select>
        </div>
      </div>
      <div class="form-actions" style="margin-top: 15px">
        <button type="button" class="btn primary" @click="loadBookings">
          查詢
        </button>
        <button type="button" class="btn secondary" @click="clearSearch">
          重設查詢
        </button>
      </div>
    </section>

    <!-- 表單區塊 -->
    <section class="admin-card">
      <h2>{{ formTitle }}</h2>

      <form @submit.prevent="saveBooking">
        <div class="form-grid">
          <div class="form-group">
            <label>訂房訂單 *</label>
            <select v-model="form.bookingOrderId" required>
              <option value="" disabled>請選擇訂單</option>
              <option v-for="order in bookingOrders" :key="order.bookingOrderId" :value="order.bookingOrderId">
                訂單 {{ order.bookingOrderId }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>房型 *</label>
            <select v-model="form.roomTypeId" required @change="changeRoomType">
              <option value="" disabled>請選擇房型</option>
              <option v-for="roomType in roomTypes" :key="roomType.roomTypeId" :value="roomType.roomTypeId">
                {{ roomType.typeName }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>分配房號</label>
            <select v-model.number="form.roomId">
              <option value="">尚未分配</option>
              <option v-for="room in availableRooms" :key="room.roomId" :value="room.roomId">
                房號 {{ room.roomNumber }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>入住人數 *</label>
            <input v-model.number="form.guestNum" type="number" min="1" required />
          </div>

          <div class="form-group">
            <label>入住日期 *</label>
            <input v-model="form.checkInDate" type="date" required @change="calculatePrice" />
          </div>

          <div class="form-group">
            <label>退房日期 *</label>
            <input v-model="form.checkOutDate" type="date" required @change="calculatePrice" />
          </div>

          <div class="form-group">
            <label>住宿晚數</label>
            <input :value="stayNights" type="number" disabled />
          </div>

          <div class="form-group">
            <label>訂房價格</label>
            <input v-model.number="form.bookingPrice" type="number" min="0" />
          </div>

          <div class="form-group">
            <label>訂房狀態</label>
            <select v-model="form.bookingStatus">
              <option v-for="status in bookingStatuses" :key="status" :value="status">
                {{ status }}
              </option>
            </select>
          </div>
          
          <div class="form-group" v-if="form.bookingId && getPaymentForBooking(form.bookingId)">
            <label>付款方式 (唯讀)</label>
            <input :value="form.paymentMethod" type="text" disabled />
          </div>
          
          <div class="form-group" v-if="form.bookingId && getPaymentForBooking(form.bookingId)">
            <label>交易序號 (唯讀)</label>
            <input :value="form.transactionId || '無'" type="text" disabled />
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn primary">
            {{ form.bookingId === null ? "新增明細" : "儲存修改" }}
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
        <h2>訂房明細列表</h2>
        <span>共 {{ bookings.length }} 筆</span>
      </div>

      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>訂單</th>
              <th>房型</th>
              <th>房號</th>
              <th>入住</th>
              <th>退房</th>
              <th>人數</th>
              <th>價格</th>
              <th>狀態</th>
              <th>付款狀態</th>
              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="bookings.length === 0">
              <td colspan="10" style="text-align: center">
                目前沒有訂房明細資料
              </td>
            </tr>
            <tr v-for="booking in paginatedData" :key="booking.bookingId">
              <td>{{ booking.bookingId }}</td>
              <td>
                {{
                  getOrderLabel(
                    booking.bookingOrderId ?? booking.booking_order_id,
                  )
                }}
              </td>
              <td>
                {{
                  getRoomTypeName(booking.roomTypeId ?? booking.room_type_id)
                }}
              </td>
              <td>{{ getRoomNumber(booking.roomId ?? booking.room_id) }}</td>
              <td>{{ booking.checkInDate ?? booking.check_in_date }}</td>
              <td>{{ booking.checkOutDate ?? booking.check_out_date }}</td>
              <td>{{ booking.guestNum ?? booking.guest_num }} 人</td>
              <td>
                {{ formatPrice(booking.bookingPrice ?? booking.booking_price) }}
              </td>
              <td>{{ booking.bookingStatus ?? booking.booking_status }}</td>
              <td>
                <span :class="{'success': getPaymentStatus(booking.bookingId) === '已付款', 'error': getPaymentStatus(booking.bookingId) === '無付款紀錄' || getPaymentStatus(booking.bookingId) === '未付款'}">
                  {{ getPaymentStatus(booking.bookingId) }}
                </span>
              </td>

              <td class="actions">
                <button class="btn primary" @click="goToPayment(booking.bookingId)">
                  付款
                </button>
                <button class="btn edit" @click="editBooking(booking)">
                  修改
                </button>
                <button class="btn delete" @click="deleteBooking(booking.bookingId)">
                  刪除
                </button>
              </td>
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
.booking-page {
  padding: 28px;
  color: #243447;
}

.page-header,
.admin-card {
  margin-bottom: 24px;
  padding: 24px;
  background: white;
  border: 1px solid #e4e7ec;
  border-radius: 12px;
  box-shadow: 0 3px 12px rgb(0 0 0 / 6%);
}

.page-header h1 {
  margin: 0 0 8px;
}

.page-header p {
  margin: 0;
  color: #667085;
}

.message {
  margin-bottom: 20px;
  padding: 12px 16px;
  border-radius: 8px;
}

.success {
  color: #176b3a;
  background: #e9f8ef;
}

.error {
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

input,
select {
  padding: 11px 12px;
  font: inherit;
  border: 1px solid #cfd4dc;
  border-radius: 7px;
}

.form-actions,
.actions {
  display: flex;
  gap: 8px;
}

.form-actions {
  margin-top: 20px;
}

.btn {
  padding: 8px 13px;
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

.table-wrapper table thead th {
  background-color: #4a3b32 !important;
  /* 深棕色背景 */
  color: #ffffff !important;
  /* 純白文字 */
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  min-width: 85px;
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e4e7ec;
}

th {
  background: #f8fafc;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
.pagination-container { display: flex; justify-content: center; align-items: center; margin-top: 20px; gap: 15px; } .page-btn { padding: 8px 16px; background-color: #3b82f6; color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: 500; transition: background-color 0.2s; } .page-btn:hover:not(:disabled) { background-color: #2563eb; } .page-btn:disabled { background-color: #d1d5db; cursor: not-allowed; } .page-info { font-weight: 500; color: #374151; }
</style>
