<script setup>
import { computed, ref } from "vue";

const bookingOrders = ref([
  { bookingOrderId: 1, memberName: "王小明" },
  { bookingOrderId: 2, memberName: "陳小美" },
]);

const roomTypes = ref([
  {
    roomTypeId: 1,
    typeName: "豪華雙人房",
    capacity: 2,
    pricePerNight: 5000,
  },
  {
    roomTypeId: 2,
    typeName: "家庭四人房",
    capacity: 4,
    pricePerNight: 8000,
  },
]);

const rooms = ref([
  { roomId: 1, roomNumber: "301", roomTypeId: 1 },
  { roomId: 2, roomNumber: "302", roomTypeId: 1 },
  { roomId: 3, roomNumber: "501", roomTypeId: 2 },
]);

const bookings = ref([
  {
    bookingId: 1,
    bookingOrderId: 1,
    roomTypeId: 1,
    roomId: 1,
    checkInDate: "2026-09-01",
    checkOutDate: "2026-09-03",
    guestNum: 2,
    bookingPrice: 10000,
    bookingStatus: "已確認",
  },
]);

const bookingStatuses = [
  "待確認",
  "已確認",
  "已入住",
  "已完成",
  "已取消",
];

const message = ref("");
const messageType = ref("");
const formTitle = ref("新增訂房明細");
const form = ref(createEmptyForm());

function createEmptyForm() {
  return {
    bookingId: null,
    bookingOrderId: "",
    roomTypeId: "",
    roomId: "",
    checkInDate: "",
    checkOutDate: "",
    guestNum: 1,
    bookingPrice: 0,
    bookingStatus: "待確認",
  };
}

const availableRooms = computed(() => {
  if (!form.value.roomTypeId) return [];

  return rooms.value.filter(
    (room) => room.roomTypeId === Number(form.value.roomTypeId),
  );
});

const stayNights = computed(() => {
  if (!form.value.checkInDate || !form.value.checkOutDate) {
    return 0;
  }

  const checkIn = new Date(form.value.checkInDate);
  const checkOut = new Date(form.value.checkOutDate);
  const difference = checkOut - checkIn;

  return Math.max(
    0,
    Math.ceil(difference / (1000 * 60 * 60 * 24)),
  );
});

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
}

function clearForm() {
  form.value = createEmptyForm();
  formTitle.value = "新增訂房明細";
}

function getOrderLabel(orderId) {
  const order = bookingOrders.value.find(
    (item) => item.bookingOrderId === Number(orderId),
  );

  return order
    ? `訂單 ${order.bookingOrderId}－${order.memberName}`
    : "未知訂單";
}

function getRoomTypeName(roomTypeId) {
  return (
    roomTypes.value.find(
      (item) => item.roomTypeId === Number(roomTypeId),
    )?.typeName ?? "未知房型"
  );
}

function getRoomNumber(roomId) {
  return (
    rooms.value.find((item) => item.roomId === Number(roomId))
      ?.roomNumber ?? "尚未分配"
  );
}

function changeRoomType() {
  form.value.roomId = "";

  const selectedRoomType = roomTypes.value.find(
    (item) => item.roomTypeId === Number(form.value.roomTypeId),
  );

  if (
    selectedRoomType &&
    form.value.guestNum > selectedRoomType.capacity
  ) {
    form.value.guestNum = selectedRoomType.capacity;
  }

  calculatePrice();
}

function calculatePrice() {
  const selectedRoomType = roomTypes.value.find(
    (item) => item.roomTypeId === Number(form.value.roomTypeId),
  );

  if (!selectedRoomType || stayNights.value === 0) {
    form.value.bookingPrice = 0;
    return;
  }

  form.value.bookingPrice =
    selectedRoomType.pricePerNight * stayNights.value;
}

function saveBooking() {
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

  const selectedRoomType = roomTypes.value.find(
    (item) => item.roomTypeId === Number(form.value.roomTypeId),
  );

  if (Number(form.value.guestNum) > selectedRoomType.capacity) {
    showMessage(
      `此房型最多入住 ${selectedRoomType.capacity} 人`,
      "error",
    );
    return;
  }

  const bookingData = {
    ...form.value,
    bookingOrderId: Number(form.value.bookingOrderId),
    roomTypeId: Number(form.value.roomTypeId),
    roomId: form.value.roomId
      ? Number(form.value.roomId)
      : null,
    guestNum: Number(form.value.guestNum),
    bookingPrice: Number(form.value.bookingPrice),
  };

  if (form.value.bookingId === null) {
    const nextId =
      bookings.value.length === 0
        ? 1
        : Math.max(...bookings.value.map((item) => item.bookingId)) + 1;

    bookings.value.push({
      ...bookingData,
      bookingId: nextId,
    });

    showMessage("訂房明細新增成功", "success");
  } else {
    const index = bookings.value.findIndex(
      (item) => item.bookingId === form.value.bookingId,
    );

    if (index !== -1) {
      bookings.value[index] = bookingData;
    }

    showMessage("訂房明細修改成功", "success");
  }

  clearForm();
}

function editBooking(booking) {
  form.value = { ...booking };
  formTitle.value = `修改訂房明細 ID：${booking.bookingId}`;

  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
}

function deleteBooking(id) {
  if (!window.confirm("確定刪除這筆訂房明細嗎？")) {
    return;
  }

  bookings.value = bookings.value.filter(
    (item) => item.bookingId !== id,
  );

  if (form.value.bookingId === id) {
    clearForm();
  }

  showMessage("訂房明細已刪除", "success");
}

function formatPrice(price) {
  return new Intl.NumberFormat("zh-TW", {
    style: "currency",
    currency: "TWD",
    maximumFractionDigits: 0,
  }).format(price);
}
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

    <section class="admin-card">
      <h2>{{ formTitle }}</h2>

      <form @submit.prevent="saveBooking">
        <div class="form-grid">
          <div class="form-group">
            <label>訂房訂單 *</label>

            <select v-model="form.bookingOrderId" required>
              <option value="" disabled>請選擇訂單</option>

              <option
                v-for="order in bookingOrders"
                :key="order.bookingOrderId"
                :value="order.bookingOrderId"
              >
                訂單 {{ order.bookingOrderId }}－{{ order.memberName }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>房型 *</label>

            <select
              v-model="form.roomTypeId"
              required
              @change="changeRoomType"
            >
              <option value="" disabled>請選擇房型</option>

              <option
                v-for="roomType in roomTypes"
                :key="roomType.roomTypeId"
                :value="roomType.roomTypeId"
              >
                {{ roomType.typeName }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>分配房號</label>

            <select v-model="form.roomId">
              <option value="">尚未分配</option>

              <option
                v-for="room in availableRooms"
                :key="room.roomId"
                :value="room.roomId"
              >
                房號 {{ room.roomNumber }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>入住人數 *</label>

            <input
              v-model.number="form.guestNum"
              type="number"
              min="1"
              required
            />
          </div>

          <div class="form-group">
            <label>入住日期 *</label>

            <input
              v-model="form.checkInDate"
              type="date"
              required
              @change="calculatePrice"
            />
          </div>

          <div class="form-group">
            <label>退房日期 *</label>

            <input
              v-model="form.checkOutDate"
              type="date"
              required
              @change="calculatePrice"
            />
          </div>

          <div class="form-group">
            <label>住宿晚數</label>
            <input :value="stayNights" type="number" disabled />
          </div>

          <div class="form-group">
            <label>訂房價格</label>

            <input
              v-model.number="form.bookingPrice"
              type="number"
              min="0"
            />
          </div>

          <div class="form-group">
            <label>訂房狀態</label>

            <select v-model="form.bookingStatus">
              <option
                v-for="status in bookingStatuses"
                :key="status"
                :value="status"
              >
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
            <tr v-for="booking in bookings" :key="booking.bookingId">
              <td>{{ booking.bookingId }}</td>
              <td>{{ getOrderLabel(booking.bookingOrderId) }}</td>
              <td>{{ getRoomTypeName(booking.roomTypeId) }}</td>
              <td>{{ getRoomNumber(booking.roomId) }}</td>
              <td>{{ booking.checkInDate }}</td>
              <td>{{ booking.checkOutDate }}</td>
              <td>{{ booking.guestNum }} 人</td>
              <td>{{ formatPrice(booking.bookingPrice) }}</td>
              <td>{{ booking.bookingStatus }}</td>

              <td class="actions">
                <button class="btn edit" @click="editBooking(booking)">
                  修改
                </button>

                <button
                  class="btn delete"
                  @click="deleteBooking(booking.bookingId)"
                >
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

.table-wrapper {
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