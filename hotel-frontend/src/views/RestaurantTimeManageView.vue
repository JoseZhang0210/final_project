<script setup>
import { computed, onMounted, ref } from "vue";

const RESTAURANT_API_URL = "/api/restaurant";
const TIME_API_URL = "/api/restaurant_times";

const restaurants = ref([]);
const times = ref([]);

const formTitle = ref("新增時段");

const message = ref("");
const messageType = ref("");

const loading = ref(false);
const saving = ref(false);

const form = ref({
  timeId: null,
  restaurantId: "",
  mealType: "早餐",
  openTime: "",
  closeTime: "",
});

// ==============================
// 是否修改模式
// ==============================

const isEditing = computed(() => {
  return form.value.timeId !== null;
});

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
// 顯示訊息
// ==============================

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
}

// ==============================
// 清除表單
// ==============================

function clearForm() {
  form.value = {
    timeId: null,
    restaurantId: "",
    mealType: "早餐",
    openTime: "",
    closeTime: "",
  };

  formTitle.value = "新增時段";

  message.value = "";

  messageType.value = "";
}

// ==============================
// 時間格式
// ==============================

function formatTime(time) {
  return time ? time.slice(0, 5) : "";
}

// ==============================
// 取得餐廳名稱
// ==============================

function getRestaurantName(restaurantId) {
  const restaurant = restaurants.value.find(
    (item) => Number(item.restaurantId) === Number(restaurantId),
  );

  return restaurant ? restaurant.restaurantName : "餐廳資料不存在";
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

    showMessage("讀取餐廳資料失敗", "error");
  }
}

// ==============================
// 讀取時段
// GET /api/restaurant_times
// ==============================

async function loadTimes() {
  loading.value = true;

  try {
    const response = await fetch(TIME_API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    console.log("時段 API status：", response.status);

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有時段管理權限", "error");
      return;
    }

    if (!response.ok) {
      showMessage("讀取時段資料失敗", "error");
      return;
    }

    times.value = await response.json();

    console.log("時段資料：", times.value);
  } catch (error) {
    console.error(error);

    showMessage("讀取時段資料失敗", "error");
  } finally {
    loading.value = false;
  }
}

// ==============================
// 編輯時段
// ==============================

function editTime(time) {
  form.value = {
    timeId: time.timeId,

    restaurantId: String(time.restaurantId),

    mealType: time.mealType ?? "早餐",

    openTime: formatTime(time.openTime),

    closeTime: formatTime(time.closeTime),
  };

  formTitle.value = "修改時段";

  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
}

// ==============================
// 刪除時段
// DELETE /api/restaurant_times/{id}
// ==============================

async function deleteTime(id) {
  if (!window.confirm("確定要刪除這個時段嗎？")) {
    return;
  }

  try {
    const response = await fetch(`${TIME_API_URL}/${id}`, {
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

    showMessage("時段已刪除", "success");

    await loadTimes();
  } catch (error) {
    console.error(error);

    showMessage("刪除失敗", "error");
  }
}

// ==============================
// 儲存時段
// POST /api/restaurant_times
// PUT  /api/restaurant_times/{id}
// ==============================

async function saveTime() {
  const restaurantTime = {
    restaurantId: Number(form.value.restaurantId),

    mealType: form.value.mealType,

    openTime: form.value.openTime,

    closeTime: form.value.closeTime,
  };

  saving.value = true;

  try {
    const url = isEditing.value
      ? `${TIME_API_URL}/${form.value.timeId}`
      : TIME_API_URL;

    const response = await fetch(url, {
      method: isEditing.value ? "PUT" : "POST",

      headers: getAuthHeaders(),

      body: JSON.stringify(restaurantTime),
    });

    console.log("儲存時段 status：", response.status);

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有操作權限", "error");
      return;
    }

    if (!response.ok) {
      showMessage("儲存失敗", "error");
      return;
    }

    const successText = isEditing.value ? "修改成功" : "新增成功";

    clearForm();

    showMessage(successText, "success");

    await loadTimes();
  } catch (error) {
    console.error(error);

    showMessage("儲存失敗", "error");
  } finally {
    saving.value = false;
  }
}

// ==============================
// 初始化
// ==============================

onMounted(async () => {
  console.log("餐廳時段頁 JWT：", localStorage.getItem("token"));

  await loadRestaurants();
  await loadTimes();
});
</script>

<template>
  <div class="time-page">
    <!-- 頁面標題 -->
    <div class="admin-page-header">
      <div>
        <h1>餐廳時段管理</h1>

        <p>管理星澄飯店各餐廳的早餐、午餐、晚餐與營業時段</p>
      </div>
    </div>

    <!-- =========================
         新增 / 修改時段
         ========================= -->

    <section class="admin-card time-form-card">
      <h2>
        {{ formTitle }}
      </h2>

      <form @submit.prevent="saveTime">
        <div class="admin-form-grid">
          <!-- 餐廳 -->
          <div class="admin-form-group">
            <label for="restaurantId"> 餐廳 * </label>

            <select id="restaurantId" v-model="form.restaurantId" required>
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

          <!-- 餐期 -->
          <div class="admin-form-group">
            <label for="mealType"> 餐期 * </label>

            <select id="mealType" v-model="form.mealType" required>
              <option value="早餐">早餐</option>

              <option value="午餐">午餐</option>

              <option value="晚餐">晚餐</option>

              <option value="晚間">晚間</option>
            </select>
          </div>

          <!-- 開始時間 -->
          <div class="admin-form-group">
            <label for="openTime"> 開始時間 * </label>

            <input id="openTime" v-model="form.openTime" type="time" required />
          </div>

          <!-- 結束時間 -->
          <div class="admin-form-group">
            <label for="closeTime"> 結束時間 * </label>

            <input
              id="closeTime"
              v-model="form.closeTime"
              type="time"
              required
            />
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
         時段列表
         ========================= -->

    <section class="admin-card">
      <div class="time-list-header">
        <h2>時段列表</h2>

        <button
          type="button"
          class="admin-btn admin-btn-secondary"
          @click="loadTimes"
        >
          重新整理
        </button>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-message">時段資料讀取中...</div>

      <!-- Table -->
      <div v-else class="admin-table-wrapper">
        <table class="admin-table">
          <thead>
            <tr>
              <th>時段 ID</th>
              <th>餐廳</th>
              <th>餐期</th>
              <th>開始時間</th>
              <th>結束時間</th>
              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="times.length === 0">
              <td colspan="6" class="empty-row">目前沒有餐廳時段資料</td>
            </tr>

            <tr v-for="time in times" :key="time.timeId">
              <td>
                {{ time.timeId }}
              </td>

              <td class="restaurant-name">
                {{ getRestaurantName(time.restaurantId) }}
              </td>

              <td>
                {{ time.mealType }}
              </td>

              <td>
                {{ formatTime(time.openTime) }}
              </td>

              <td>
                {{ formatTime(time.closeTime) }}
              </td>

              <td>
                <div class="time-actions">
                  <button
                    type="button"
                    class="admin-btn admin-btn-edit"
                    @click="editTime(time)"
                  >
                    修改
                  </button>

                  <button
                    type="button"
                    class="admin-btn admin-btn-delete"
                    @click="deleteTime(time.timeId)"
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
.time-page {
  width: 100%;
}

.time-form-card {
  margin-bottom: 28px;
}

.time-form-card h2 {
  margin-top: 0;

  margin-bottom: 22px;

  color: #6f5328;
}

.time-list-header {
  display: flex;

  justify-content: space-between;

  align-items: center;

  gap: 15px;

  margin-bottom: 22px;
}

.time-list-header h2 {
  margin: 0;

  color: #6f5328;
}

.restaurant-name {
  color: #5b4632;

  font-weight: bold;
}

.time-actions {
  display: flex;

  gap: 7px;

  white-space: nowrap;
}

.empty-row {
  padding: 35px !important;

  text-align: center !important;

  color: #888 !important;
}

.loading-message {
  padding: 40px;

  text-align: center;

  color: #888;
}

.admin-btn:disabled {
  opacity: 0.6;

  cursor: not-allowed;

  transform: none;
}

@media (max-width: 700px) {
  .time-list-header {
    align-items: stretch;

    flex-direction: column;
  }
}
</style>
