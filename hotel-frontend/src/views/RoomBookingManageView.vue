<script setup>
import { computed, onMounted, ref } from "vue";

const BOOKING_API_URL = "/api/bookings";
const BOOKING_ORDER_API_URL = "/api/booking-orders";
const ROOM_TYPE_API_URL = "/api/roomtypes";
const ROOM_API_URL = "/api/rooms";

// 選單資料（初始化為空陣列）
const bookingOrders = ref([]);
const roomTypes = ref([]);
const rooms = ref([]);

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

function getAuthHeaders() {
  const token = localStorage.getItem("token");
  const headers = { "Content-Type": "application/json" };
  if (token) {
    headers.Authorization = "Bearer " + token;
  }
  return headers;
}

async function fetchList(url, errorMessage) {
  const response = await fetch(url, {
    method: "GET",
    headers: getAuthHeaders(),
    credentials: "include",
  });

  if (response.status === 401 || response.status === 403) {
    throw new Error("登入已過期或沒有權限");
  }

  if (!response.ok) {
    throw new Error(`${errorMessage}：${response.status}`);
  }

  const data = await response.json();

  return Array.isArray(data)
    ? data
    : data?.content || [];
}

async function loadSelectOptions() {
  try {
    const [orderData, roomTypeData, roomData] = await Promise.all([
      fetchList(BOOKING_ORDER_API_URL, "讀取訂單失敗"),
      fetchList(ROOM_TYPE_API_URL, "讀取房型失敗"),
      fetchList(ROOM_API_URL, "讀取房間失敗"),
    ]);

    bookingOrders.value = orderData;
    roomTypes.value = roomTypeData;
    rooms.value = roomData;

    console.log("訂單選項：", bookingOrders.value);
    console.log("房型選項：", roomTypes.value);
    console.log("房間選項：", rooms.value);
  } catch (error) {
    console.error("載入下拉選單錯誤：", error);
    showMessage(error.message || "無法載入下拉選單", "error");
  }
}

// 1. 載入與條件查詢 (對應 @GetMapping)
async function loadBookings() {
  try {
    let url = BOOKING_API_URL;

    // 依據條件切換 API Endpoint；若無條件則直接請求 /api/bookings (getAllBookings)
    if (searchCriteria.value.checkInDate) {
      url = `${BOOKING_API_URL}/check-in?date=${searchCriteria.value.checkInDate}`;
    } else if (searchCriteria.value.status) {
      url = `${BOOKING_API_URL}/status?status=${encodeURIComponent(searchCriteria.value.status)}`;
    }

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
      showMessage(errorData.message || "讀取預訂資料失敗", "error");
      return;
    }

    const data = await response.json();
    bookings.value = Array.isArray(data) ? data : data ? [data] : [];
  } catch (error) {
    console.error("loadBookings Error:", error);
    showMessage("無法連線至預訂 API", "error");
  }
}

function clearSearch() {
  searchCriteria.value = { checkInDate: "", status: "" };
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
  return (
    roomTypes.value.find((item) => item.roomTypeId === Number(roomTypeId))
      ?.typeName ?? `房型 ${roomTypeId}`
  );
  return found ? (found.typeName ?? found.type_name) : `編號 ${roomTypeId}`;
}

function getRoomNumber(roomId) {
  if (!roomId) return "尚未分配";
  return (
    rooms.value.find((item) => item.roomId === Number(roomId))?.roomNumber ??
    `房號 ${roomId}`
  );
  return found ? (found.roomNumber ?? found.room_number) : `編號 ${roomId}`;
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
  if (form.value.bookingId === null) {
    showMessage("後端尚未開放新增預訂功能 (POST 已註解)", "error");
    return;
  }

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
    const response = await fetch(`${BOOKING_API_URL}/${form.value.bookingId}`, {
      method: "PUT",
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
      showMessage(errorData.message || "更新失敗", "error");
      return;
    }

    showMessage("訂房明細修改成功", "success");
    clearForm();
    await loadBookings();
  } catch (error) {
    showMessage("無法連線至預訂 API", "error");
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
  window.scrollTo({ top: 0, behavior: "smooth" });
}

// 3. 刪除預訂 (對應 DELETE /api/bookings/{id})
async function deleteBooking(id) {
  if (!window.confirm("確定刪除這筆訂房明細嗎？")) {
    return;
  }

  try {
    const response = await fetch(`${BOOKING_API_URL}/${id}`, {
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

    showMessage(resData.message || "訂房明細已刪除", "success");
    if (form.value.bookingId === id) {
      clearForm();
    }
    await loadBookings();
  } catch (error) {
    showMessage("無法連線至預訂 API", "error");
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
          <label>入住日期</label>
          <input v-model="searchCriteria.checkInDate" type="date" />
        </div>
        <div class="form-group">
          <label>預訂狀態</label>
          <select v-model="searchCriteria.status">
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
              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="bookings.length === 0">
              <td colspan="10" style="text-align: center">
                目前沒有訂房明細資料
              </td>
            </tr>
            <tr v-for="booking in bookings" :key="booking.bookingId">
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

              <td class="actions">
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
</style>
