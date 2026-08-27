<script setup>
import { computed, onMounted, ref } from "vue";

const RESTAURANT_API_URL = "/api/restaurant";
const TIME_API_URL = "/api/restaurant_times";
const RESERVATION_API_URL = "/api/reservations";
const MEMBER_API_URL = "/api/members";

const restaurants = ref([]);
const allTimes = ref([]);
const timeOptions = ref([]);
const reservations = ref([]);

const message = ref("");
const messageType = ref("");
const formTitle = ref("新增訂位");

const loading = ref(false);
const saving = ref(false);
const memberLoaded = ref(false);

const form = ref(createEmptyForm());

function createEmptyForm() {
  return {
    reservationId: null,
    memberId: "",
    contactName: "",
    contactPhone: "",
    restaurantId: "",
    reservationDate: "",
    timeId: "",
    peopleCount: "",
    status: "已訂位",
  };
}

// ==============================
// JWT Header
// ==============================

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

// ==============================
// 是否為會員訂位
// ==============================

const hasMember = computed(() => {
  return String(form.value.memberId ?? "").trim() !== "";
});

// ==============================
// 訊息
// ==============================

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
}

// ==============================
// 時間格式
// ==============================

function formatTime(time) {
  return time ? time.slice(0, 5) : "";
}

// ==============================
// 餐廳名稱
// ==============================

function getRestaurantName(restaurantId) {
  const restaurant = restaurants.value.find(
    (item) => Number(item.restaurantId) === Number(restaurantId),
  );

  return restaurant ? restaurant.restaurantName : `餐廳 ID：${restaurantId}`;
}

// ==============================
// 時段名稱
// ==============================

function getTimeName(timeId) {
  const time = allTimes.value.find(
    (item) => Number(item.timeId) === Number(timeId),
  );

  if (!time) {
    return `時段 ID：${timeId}`;
  }

  return `${time.mealType}（${formatTime(time.openTime)} - ${formatTime(time.closeTime)}）`;
}

// ==============================
// 會員資料
// ==============================

function handleMemberIdInput() {
  memberLoaded.value = false;
  form.value.contactName = "";
  form.value.contactPhone = "";
}

async function loadMemberInfo() {
  const memberId = String(form.value.memberId ?? "").trim();

  memberLoaded.value = false;

  if (!memberId) {
    return;
  }

  if (!/^\d+$/.test(memberId)) {
    showMessage("會員 ID 必須為數字", "error");
    return;
  }

  try {
    const response = await fetch(`${MEMBER_API_URL}/${memberId}`, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    if (response.status === 404) {
      showMessage("查無此會員 ID", "error");
      return;
    }

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有會員資料權限", "error");
      return;
    }

    if (!response.ok) {
      showMessage("讀取會員資料失敗", "error");
      return;
    }

    const member = await response.json();

    form.value.memberId = String(member.memberId);
    form.value.contactName = member.name ?? "";
    form.value.contactPhone = member.phone ?? "";
    memberLoaded.value = true;
  } catch (error) {
    console.error(error);
    showMessage("無法連線至會員 API", "error");
  }
}

// ==============================
// 讀取餐廳
// GET /api/restaurant
// ==============================

async function loadRestaurants() {
  try {
    const response = await fetch(RESTAURANT_API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    console.log("餐廳 API status：", response.status);

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有餐廳資料權限", "error");
      return;
    }

    if (!response.ok) {
      showMessage("讀取餐廳資料失敗", "error");
      return;
    }

    restaurants.value = await response.json();
  } catch (error) {
    console.error(error);

    showMessage("無法連線至餐廳 API", "error");
  }
}

// ==============================
// 讀取全部時段
// GET /api/restaurant_times
// ==============================

async function loadAllTimes() {
  try {
    const response = await fetch(TIME_API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    console.log("全部時段 API status：", response.status);

    if (!response.ok) {
      return;
    }

    allTimes.value = await response.json();
  } catch (error) {
    console.error(error);
  }
}

// ==============================
// 根據餐廳讀取時段
// GET /api/restaurant_times/restaurant/{id}
// ==============================

async function loadTimeOptions(selectedTimeId = "") {
  if (!form.value.restaurantId) {
    timeOptions.value = [];
    form.value.timeId = "";
    return;
  }

  try {
    const response = await fetch(
      `${TIME_API_URL}/restaurant/${form.value.restaurantId}`,
      {
        method: "GET",
        headers: getAuthHeaders(),
      },
    );

    console.log("餐廳時段 API status：", response.status);

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有時段資料權限", "error");
      return;
    }

    if (!response.ok) {
      showMessage("讀取餐廳時段失敗", "error");
      return;
    }

    timeOptions.value = await response.json();

    form.value.timeId = selectedTimeId ? String(selectedTimeId) : "";
  } catch (error) {
    console.error(error);

    showMessage("無法連線至餐廳時段 API", "error");
  }
}

// ==============================
// 讀取訂位
// GET /api/reservations
// ==============================

async function loadReservations() {
  loading.value = true;

  try {
    const response = await fetch(RESERVATION_API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    console.log("訂位 API status：", response.status);

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有訂位管理權限", "error");
      return;
    }

    if (!response.ok) {
      showMessage("讀取訂位資料失敗", "error");
      return;
    }

    reservations.value = await response.json();

    console.log("訂位資料：", reservations.value);
  } catch (error) {
    console.error(error);

    showMessage("無法連線至訂位 API", "error");
  } finally {
    loading.value = false;
  }
}

// ==============================
// 清除表單
// ==============================

function clearForm() {
  form.value = createEmptyForm();
  timeOptions.value = [];
  memberLoaded.value = false;
  formTitle.value = "新增訂位";
  message.value = "";
  messageType.value = "";
}

// ==============================
// 儲存訂位
// POST /api/reservations
// PUT  /api/reservations/{id}
// ==============================

async function saveReservation() {
  if (hasMember.value && !memberLoaded.value) {
    showMessage("請先輸入有效的會員 ID", "error");
    return;
  }

  if (!hasMember.value) {
    if (!form.value.contactName.trim() || !form.value.contactPhone.trim()) {
      showMessage("非會員訂位必須填寫姓名與電話", "error");
      return;
    }
  }

  const payload = {
    memberId: hasMember.value ? Number(form.value.memberId) : null,

    contactName: form.value.contactName.trim() || null,

    contactPhone: form.value.contactPhone.trim() || null,

    restaurantId: Number(form.value.restaurantId),

    reservationDate: form.value.reservationDate,

    timeId: Number(form.value.timeId),

    peopleCount: Number(form.value.peopleCount),

    status: form.value.status,
  };

  const isEdit = form.value.reservationId !== null;

  const url = isEdit
    ? `${RESERVATION_API_URL}/${form.value.reservationId}`
    : RESERVATION_API_URL;

  saving.value = true;

  try {
    const response = await fetch(url, {
      method: isEdit ? "PUT" : "POST",

      headers: getAuthHeaders(),

      body: JSON.stringify(payload),
    });

    console.log("儲存訂位 status：", response.status);

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有操作權限", "error");
      return;
    }

    if (!response.ok) {
      showMessage("儲存失敗", "error");
      return;
    }

    showMessage(isEdit ? "修改成功" : "新增成功", "success");

    clearForm();

    await loadReservations();
  } catch (error) {
    console.error(error);

    showMessage("無法連線至訂位 API", "error");
  } finally {
    saving.value = false;
  }
}

// ==============================
// 編輯訂位
// ==============================

async function editReservation(reservation) {
  form.value = {
    reservationId: reservation.reservationId,

    memberId: reservation.memberId ?? "",

    contactName: reservation.contactName ?? "",

    contactPhone: reservation.contactPhone ?? "",

    restaurantId: String(reservation.restaurantId),

    reservationDate: reservation.reservationDate,

    timeId: "",

    peopleCount: reservation.peopleCount,

    status: reservation.status,
  };

  memberLoaded.value = false;

  if (hasMember.value) {
    await loadMemberInfo();
  }

  await loadTimeOptions(reservation.timeId);

  formTitle.value = `修改訂位 ID：${reservation.reservationId}`;

  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
}

// ==============================
// 刪除訂位
// DELETE /api/reservations/{id}
// ==============================

async function deleteReservation(id) {
  if (!confirm("確定要刪除這筆訂位嗎？")) {
    return;
  }

  try {
    const response = await fetch(`${RESERVATION_API_URL}/${id}`, {
      method: "DELETE",
      headers: getAuthHeaders(),
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有刪除權限", "error");
      return;
    }

    if (!response.ok) {
      showMessage("刪除失敗", "error");
      return;
    }

    showMessage("訂位已刪除", "success");

    clearForm();

    await loadReservations();
  } catch (error) {
    console.error(error);

    showMessage("無法連線至訂位 API", "error");
  }
}

// ==============================
// 初始化
// ==============================

onMounted(async () => {
  console.log("訂位頁 JWT：", localStorage.getItem("token"));

  await loadRestaurants();
  await loadAllTimes();
  await loadReservations();
});
</script>

<template>
  <div class="reservation-page">
    <!-- 頁面標題 -->
    <div class="admin-page-header">
      <div>
        <h1>餐廳訂位管理</h1>

        <p>管理飯店餐廳訂位、會員與非會員聯絡資訊及訂位狀態</p>
      </div>
    </div>

    <!-- =========================
         新增 / 修改訂位
         ========================= -->

    <section class="admin-card reservation-form-card">
      <h2>
        {{ formTitle }}
      </h2>

      <form @submit.prevent="saveReservation">
        <div class="admin-form-grid">
          <!-- 會員 -->
          <div class="admin-form-group">
            <label> 會員 ID（選填） </label>

            <input
              v-model="form.memberId"
              type="number"
              min="1"
              placeholder="會員訂位可輸入會員 ID"
              @input="handleMemberIdInput"
              @blur="loadMemberInfo"
            />
          </div>

          <!-- 姓名 -->
          <div class="admin-form-group">
            <label> 訂位人姓名（非會員必填） </label>

            <input
              v-model="form.contactName"
              type="text"
              placeholder="請輸入訂位人姓名"
              :disabled="memberLoaded"
            />
          </div>

          <!-- 電話 -->
          <div class="admin-form-group">
            <label> 訂位人電話（非會員必填） </label>

            <input
              v-model="form.contactPhone"
              type="text"
              placeholder="請輸入聯絡電話"
              :disabled="memberLoaded"
            />
          </div>

          <!-- 餐廳 -->
          <div class="admin-form-group">
            <label> 餐廳 * </label>

            <select
              v-model="form.restaurantId"
              required
              @change="loadTimeOptions()"
            >
              <option value="">請選擇餐廳</option>

              <option
                v-for="restaurant in restaurants"
                :key="restaurant.restaurantId"
                :value="String(restaurant.restaurantId)"
              >
                {{ restaurant.restaurantName }}
              </option>
            </select>
          </div>

          <!-- 日期 -->
          <div class="admin-form-group">
            <label> 訂位日期 * </label>

            <input v-model="form.reservationDate" type="date" required />
          </div>

          <!-- 時段 -->
          <div class="admin-form-group">
            <label> 訂位時段 * </label>

            <select
              v-model="form.timeId"
              required
              :disabled="!form.restaurantId"
            >
              <option value="">
                {{ form.restaurantId ? "請選擇時段" : "請先選擇餐廳" }}
              </option>

              <option
                v-for="time in timeOptions"
                :key="time.timeId"
                :value="String(time.timeId)"
              >
                {{ time.mealType }}
                （{{ formatTime(time.openTime) }}
                -
                {{ formatTime(time.closeTime) }}）
              </option>
            </select>
          </div>

          <!-- 人數 -->
          <div class="admin-form-group">
            <label> 訂位人數 * </label>

            <input v-model="form.peopleCount" type="number" min="1" required />
          </div>

          <!-- 狀態 -->
          <div class="admin-form-group">
            <label> 訂位狀態 * </label>

            <select v-model="form.status" required>
              <option value="已訂位">訂位</option>

              <option value="已取消">取消</option>

              <option value="已完成">已完成</option>
            </select>
          </div>
        </div>

        <!-- 按鈕 -->
        <div class="admin-form-actions">
          <button
            type="submit"
            class="admin-btn admin-btn-primary"
            :disabled="saving"
          >
            {{ saving ? "儲存中..." : "儲存" }}
          </button>

          <button
            type="button"
            class="admin-btn admin-btn-secondary"
            @click="clearForm"
          >
            清除
          </button>
        </div>

        <!-- 訊息 -->
        <div v-if="message" class="admin-message" :class="messageType">
          {{ message }}
        </div>
      </form>
    </section>

    <!-- =========================
         訂位列表
         ========================= -->

    <section class="admin-card">
      <div class="reservation-list-header">
        <h2>訂位列表</h2>

        <button
          type="button"
          class="admin-btn admin-btn-secondary"
          @click="loadReservations"
        >
          重新整理
        </button>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-message">訂位資料讀取中...</div>

      <!-- Table -->
      <div v-else class="admin-table-wrapper">
        <table class="admin-table">
          <thead>
            <tr>
              <th>訂位 ID</th>
              <th>會員 ID</th>
              <th>訂位人姓名</th>
              <th>訂位人電話</th>
              <th>餐廳</th>
              <th>訂位日期</th>
              <th>時段</th>
              <th>人數</th>
              <th>狀態</th>
              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="reservations.length === 0">
              <td colspan="10" class="empty-row">目前沒有訂位資料</td>
            </tr>

            <tr
              v-for="reservation in reservations"
              :key="reservation.reservationId"
            >
              <td>
                {{ reservation.reservationId }}
              </td>

              <td>
                {{ reservation.memberId ?? "" }}
              </td>

              <td>
                {{ reservation.contactName ?? "" }}
              </td>

              <td>
                {{ reservation.contactPhone ?? "" }}
              </td>

              <td>
                {{ getRestaurantName(reservation.restaurantId) }}
              </td>

              <td>
                {{ reservation.reservationDate }}
              </td>

              <td>
                {{ getTimeName(reservation.timeId) }}
              </td>

              <td>
                {{ reservation.peopleCount }}
              </td>

              <td>
                <span
                  class="reservation-status"
                  :class="{
                    'status-booked': reservation.status === '已訂位',

                    'status-cancelled': reservation.status === '已取消',

                    'status-completed': reservation.status === '已完成',
                  }"
                >
                  {{ reservation.status }}
                </span>
              </td>

              <td>
                <div class="reservation-actions">
                  <button
                    type="button"
                    class="admin-btn admin-btn-edit"
                    @click="editReservation(reservation)"
                  >
                    修改
                  </button>

                  <button
                    type="button"
                    class="admin-btn admin-btn-delete"
                    @click="deleteReservation(reservation.reservationId)"
                  >
                    刪除
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </div>
</template>

<style scoped>
.reservation-page {
  width: 100%;
}

.reservation-form-card {
  margin-bottom: 28px;
}

.reservation-form-card h2 {
  margin-top: 0;
  margin-bottom: 22px;
  color: #6f5328;
}

.reservation-list-header {
  display: flex;

  justify-content: space-between;

  align-items: center;

  gap: 15px;

  margin-bottom: 22px;
}

.reservation-list-header h2 {
  margin: 0;

  color: #6f5328;
}

.reservation-actions {
  display: flex;

  gap: 7px;

  white-space: nowrap;
}

.empty-row {
  padding: 38px !important;

  text-align: center !important;

  color: #888 !important;
}

.loading-message {
  padding: 40px;

  text-align: center;

  color: #888;
}

/* 訂位狀態 */

.reservation-status {
  display: inline-block;

  padding: 5px 10px;

  border-radius: 20px;

  font-size: 12px;

  font-weight: bold;

  white-space: nowrap;
}

.status-booked {
  background-color: #e5f6eb;

  color: #257641;
}

.status-cancelled {
  background-color: #fde9e7;

  color: #b3443c;
}

.status-completed {
  background-color: #eee9e1;

  color: #5c4d3d;
}

input:disabled,
select:disabled {
  background-color: #f1eee8;

  cursor: not-allowed;
}

.admin-btn:disabled {
  opacity: 0.6;

  cursor: not-allowed;

  transform: none;
}

@media (max-width: 700px) {
  .reservation-list-header {
    align-items: stretch;

    flex-direction: column;
  }
}
</style>
