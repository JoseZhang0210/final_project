<script setup>
import { computed, onMounted, ref } from "vue";

const RESTAURANT_API_URL = "/api/restaurant";
const TIME_API_URL = "/api/restaurant_times";
const RESERVATION_API_URL = "/api/reservations";
const MEMBER_API_URL = "/api/members";
const BACKUP_API_URL = "/api/restaurant-backup";

const importInput = ref(null);
const importing = ref(false);
const exporting = ref(false);
const exportStartDate = ref("");
const exportEndDate = ref("");

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
const testingSms = ref(null);

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

// 呼叫後端時帶入登入 Token。
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

const hasMember = computed(() => {
  return String(form.value.memberId ?? "").trim() !== "";
});

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
}

function formatTime(time) {
  return time ? time.slice(0, 5) : "";
}

function getRestaurantName(restaurantId) {
  const restaurant = restaurants.value.find(
    (item) => Number(item.restaurantId) === Number(restaurantId),
  );

  return restaurant ? restaurant.restaurantName : `餐廳 ID：${restaurantId}`;
}

function getTimeName(timeId) {
  const time = allTimes.value.find(
    (item) => Number(item.timeId) === Number(timeId),
  );

  if (!time) {
    return `時段 ID：${timeId}`;
  }

  return `${time.mealType}（${formatTime(time.openTime)} - ${formatTime(time.closeTime)}）`;
}

// 輸入會員 ID 後，自動帶入姓名與電話。
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

async function loadRestaurants() {
  try {
    const response = await fetch(RESTAURANT_API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
    });

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

async function loadAllTimes() {
  try {
    const response = await fetch(TIME_API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    if (!response.ok) {
      return;
    }

    allTimes.value = await response.json();
  } catch (error) {
    console.error(error);
  }
}

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

async function loadReservations() {
  loading.value = true;

  try {
    const response = await fetch(RESERVATION_API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有訂位管理權限", "error");
      return;
    }

    if (!response.ok) {
      showMessage("讀取訂位資料失敗", "error");
      return;
    }

    reservations.value = await response.json();

  } catch (error) {
    console.error(error);

    showMessage("無法連線至訂位 API", "error");
  } finally {
    loading.value = false;
  }
}

function clearForm() {
  form.value = createEmptyForm();
  timeOptions.value = [];
  memberLoaded.value = false;
  formTitle.value = "新增訂位";
  message.value = "";
  messageType.value = "";
}

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

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有操作權限", "error");
      return;
    }

    if (!response.ok) {
      showMessage("儲存失敗", "error");
      return;
    }

    clearForm();
    showMessage(isEdit ? "修改成功" : "新增成功", "success");

    await loadReservations();
  } catch (error) {
    console.error(error);

    showMessage("無法連線至訂位 API", "error");
  } finally {
    saving.value = false;
  }
}

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

    clearForm();
    showMessage("訂位已刪除", "success");

    await loadReservations();
  } catch (error) {
    console.error(error);

    showMessage("無法連線至訂位 API", "error");
  }
}

async function sendTestSms(reservation) {
  if (!reservation.contactPhone) {
    showMessage("此訂位沒有聯絡電話，無法測試簡訊", "error");
    return;
  }

  testingSms.value = reservation.reservationId;

  try {
    const response = await fetch(
      `${RESERVATION_API_URL}/${reservation.reservationId}/sms`,
      {
        method: "POST",
        headers: getAuthHeaders(),
      },
    );
    const result = await response.json().catch(() => ({}));

    if (!response.ok) {
      showMessage(result.message || "測試簡訊發送失敗", "error");
      return;
    }

    showMessage("測試簡訊已發送，請查看 Spring Boot Console", "success");
  } catch (error) {
    console.error(error);
    showMessage("無法連線至測試簡訊 API", "error");
  } finally {
    testingSms.value = null;
  }
}

// 匯入與匯出餐廳、時段、訂位資料。
function openImportDialog() {
  importInput.value?.click();
}

async function exportBackup() {
  if (
    exportStartDate.value &&
    exportEndDate.value &&
    exportStartDate.value > exportEndDate.value
  ) {
    showMessage("匯出起始日期不可晚於結束日期", "error");
    return;
  }

  exporting.value = true;

  try {
    const params = new URLSearchParams();

    if (exportStartDate.value) {
      params.set("startDate", exportStartDate.value);
    }

    if (exportEndDate.value) {
      params.set("endDate", exportEndDate.value);
    }

    const queryString = params.toString();
    const exportUrl = queryString
      ? `${BACKUP_API_URL}/export?${queryString}`
      : `${BACKUP_API_URL}/export`;

    const response = await fetch(exportUrl, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    if (!response.ok) {
      showMessage("匯出失敗", "error");
      return;
    }

    const backupData = await response.json();
    const blob = new Blob([JSON.stringify(backupData, null, 2)], {
      type: "application/json;charset=utf-8",
    });

    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");

    link.href = url;
    link.download = `餐廳訂位備份_${new Date().toISOString().slice(0, 10)}.json`;
    link.click();

    URL.revokeObjectURL(url);
    showMessage("餐廳、時段與訂位資料已匯出", "success");
  } catch (error) {
    console.error(error);
    showMessage("無法連線至匯出 API", "error");
  } finally {
    exporting.value = false;
  }
}

async function importBackup(event) {
  const file = event.target.files?.[0];

  if (!file) {
    return;
  }

  try {
    const backupData = JSON.parse(await file.text());

    if (!confirm("匯入只會新增不存在的資料，確定要繼續嗎？")) {
      return;
    }

    importing.value = true;

    const response = await fetch(`${BACKUP_API_URL}/import`, {
      method: "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify(backupData),
    });

    const result = await response.json().catch(() => ({}));

    if (!response.ok) {
      showMessage(result.message || "匯入失敗，請確認 JSON 格式", "error");
      return;
    }

    const skippedCount =
      result.skippedRestaurants + result.skippedTimes + result.skippedReservations;
    const convertedCount = result.memberConvertedToGuest ?? 0;

    showMessage(
      `匯入完成：新增 ${result.addedRestaurants} 間餐廳、${result.addedTimes} 個時段、${result.addedReservations} 筆訂位；略過 ${skippedCount} 筆重複或格式異常資料${convertedCount ? `；${convertedCount} 筆不存在的會員已改為訪客訂位。` : "。"}`,
      "success",
    );

    await loadRestaurants();
    await loadAllTimes();
    await loadReservations();
  } catch (error) {
    console.error(error);
    showMessage("JSON 檔案格式錯誤或無法讀取", "error");
  } finally {
    importing.value = false;
    event.target.value = "";
  }
}

onMounted(async () => {
  await loadRestaurants();
  await loadAllTimes();
  await loadReservations();
});
</script>

<template>
  <div class="reservation-page">
    <div class="admin-page-header">
      <div>
        <h1>餐廳訂位管理</h1>

        <p>管理飯店餐廳訂位、會員與非會員聯絡資訊及訂位狀態</p>
      </div>
      <div class="backup-actions">
        <input ref="importInput" type="file" accept="application/json,.json" hidden @change="importBackup" />

        <label class="backup-date">
          起始日期
          <input v-model="exportStartDate" type="date" />
        </label>

        <label class="backup-date">
          結束日期
          <input v-model="exportEndDate" type="date" />
        </label>

        <button type="button" class="admin-btn admin-btn-secondary" :disabled="exporting" @click="exportBackup">
          {{ exporting ? "匯出中..." : "匯出 JSON" }}
        </button>

        <button type="button" class="admin-btn admin-btn-primary" :disabled="importing" @click="openImportDialog">
          {{ importing ? "匯入中..." : "匯入 JSON" }}
        </button>
      </div>
    </div>

    <section class="admin-card reservation-form-card">
      <h2>
        {{ formTitle }}
      </h2>

      <form @submit.prevent="saveReservation">
        <div class="admin-form-grid">
          <div class="admin-form-group">
            <label> 會員 ID（選填） </label>

            <input v-model="form.memberId" type="number" min="1" placeholder="會員訂位可輸入會員 ID" @input="handleMemberIdInput"
              @blur="loadMemberInfo" />
          </div>

          <div class="admin-form-group">
            <label> 訂位人姓名（非會員必填） </label>

            <input v-model="form.contactName" type="text" placeholder="請輸入訂位人姓名" :disabled="memberLoaded" />
          </div>

          <div class="admin-form-group">
            <label> 訂位人電話（非會員必填） </label>

            <input v-model="form.contactPhone" type="text" placeholder="請輸入聯絡電話" :disabled="memberLoaded" />
          </div>

          <div class="admin-form-group">
            <label> 餐廳 * </label>

            <select v-model="form.restaurantId" required @change="loadTimeOptions()">
              <option value="">請選擇餐廳</option>

              <option v-for="restaurant in restaurants" :key="restaurant.restaurantId"
                :value="String(restaurant.restaurantId)">
                {{ restaurant.restaurantName }}
              </option>
            </select>
          </div>

          <div class="admin-form-group">
            <label> 訂位日期 * </label>

            <input v-model="form.reservationDate" type="date" required />
          </div>

          <div class="admin-form-group">
            <label> 訂位時段 * </label>

            <select v-model="form.timeId" required :disabled="!form.restaurantId">
              <option value="">
                {{ form.restaurantId ? "請選擇時段" : "請先選擇餐廳" }}
              </option>

              <option v-for="time in timeOptions" :key="time.timeId" :value="String(time.timeId)">
                {{ time.mealType }}
                （{{ formatTime(time.openTime) }}
                -
                {{ formatTime(time.closeTime) }}）
              </option>
            </select>
          </div>

          <div class="admin-form-group">
            <label> 訂位人數 * </label>

            <input v-model="form.peopleCount" type="number" min="1" required />
          </div>

          <div class="admin-form-group">
            <label> 訂位狀態 * </label>

            <select v-model="form.status" required>
              <option value="已訂位">訂位</option>

              <option value="已取消">取消</option>

              <option value="已完成">已完成</option>
            </select>
          </div>
        </div>

        <div class="admin-form-actions">
          <button type="submit" class="admin-btn admin-btn-primary" :disabled="saving">
            {{ saving ? "儲存中..." : "儲存" }}
          </button>

          <button type="button" class="admin-btn admin-btn-secondary" @click="clearForm">
            清除
          </button>
        </div>

        <div v-if="message" class="admin-message" :class="messageType">
          {{ message }}
        </div>
      </form>
    </section>

    <section class="admin-card">
      <div class="reservation-list-header">
        <h2>訂位列表</h2>

        <button type="button" class="admin-btn admin-btn-secondary" @click="loadReservations">
          重新整理
        </button>
      </div>

      <div v-if="loading" class="loading-message">訂位資料讀取中...</div>

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

            <tr v-for="reservation in reservations" :key="reservation.reservationId">
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
                <span class="reservation-status" :class="{
                  'status-booked': reservation.status === '已訂位',

                  'status-cancelled': reservation.status === '已取消',

                  'status-completed': reservation.status === '已完成',
                }">
                  {{ reservation.status }}
                </span>
              </td>

              <td>
                <div class="reservation-actions">
                  <button type="button" class="admin-btn admin-btn-edit" @click="editReservation(reservation)">
                    修改
                  </button>

                  <button type="button" class="admin-btn admin-btn-delete"
                    @click="deleteReservation(reservation.reservationId)">
                    刪除
                  </button>

                  <button type="button" class="admin-btn admin-btn-secondary"
                    :disabled="testingSms === reservation.reservationId" @click="sendTestSms(reservation)">
                    {{ testingSms === reservation.reservationId ? "發送中..." : "測試簡訊" }}
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

.admin-page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
}

.backup-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.backup-date {
  display: grid;
  gap: 4px;
  color: #6f5328;
  font-size: 12px;
  font-weight: bold;
}

.backup-date input {
  min-height: 36px;
  padding: 6px 8px;
  border: 1px solid #d8cbb9;
  border-radius: 6px;
  font: inherit;
}

@media (max-width: 700px) {
  .admin-page-header {
    align-items: flex-start;
    flex-direction: column;
  }
}

@media (max-width: 700px) {
  .reservation-list-header {
    align-items: stretch;

    flex-direction: column;
  }
}
</style>
