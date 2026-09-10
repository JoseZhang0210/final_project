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
const modalOpen = ref(false);
const form = ref(createEmptyForm());

function createEmptyForm() {
  return {
    timeId: null,
    restaurantId: "",
    mealType: "早餐",
    openTime: "",
    closeTime: "",
  };
}

const isEditing = computed(() => form.value.timeId !== null);

function getAuthHeaders() {
  const token = localStorage.getItem("token");
  const headers = { "Content-Type": "application/json" };

  if (token) {
    headers.Authorization = "Bearer " + token;
  }

  return headers;
}

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
}

function clearForm() {
  form.value = createEmptyForm();
  formTitle.value = "新增時段";
}

function openCreateModal() {
  clearForm();
  modalOpen.value = true;
}

function closeModal() {
  modalOpen.value = false;
  clearForm();
}

function formatTime(time) {
  return time ? time.slice(0, 5) : "";
}

function getRestaurantName(restaurantId) {
  const restaurant = restaurants.value.find(
    (item) => Number(item.restaurantId) === Number(restaurantId),
  );

  return restaurant ? restaurant.restaurantName : "餐廳資料不存在";
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
    showMessage("讀取餐廳資料失敗", "error");
  }
}

async function loadTimes() {
  loading.value = true;

  try {
    const response = await fetch(TIME_API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有時段管理權限", "error");
      return;
    }

    if (!response.ok) {
      showMessage("讀取時段資料失敗", "error");
      return;
    }

    times.value = await response.json();
  } catch (error) {
    console.error(error);
    showMessage("讀取時段資料失敗", "error");
  } finally {
    loading.value = false;
  }
}

function editTime(time) {
  form.value = {
    timeId: time.timeId,
    restaurantId: String(time.restaurantId),
    mealType: time.mealType ?? "早餐",
    openTime: formatTime(time.openTime),
    closeTime: formatTime(time.closeTime),
  };

  formTitle.value = "修改時段";
  modalOpen.value = true;
}

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

    showMessage("時段已刪除", "success");
    await loadTimes();
  } catch (error) {
    console.error(error);
    showMessage("刪除失敗", "error");
  }
}

async function saveTime() {
  const isUpdate = isEditing.value;
  const restaurantTime = {
    restaurantId: Number(form.value.restaurantId),
    mealType: form.value.mealType,
    openTime: form.value.openTime,
    closeTime: form.value.closeTime,
  };

  saving.value = true;

  try {
    const url = isUpdate
      ? `${TIME_API_URL}/${form.value.timeId}`
      : TIME_API_URL;

    const response = await fetch(url, {
      method: isUpdate ? "PUT" : "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify(restaurantTime),
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有操作權限", "error");
      return;
    }

    if (!response.ok) {
      showMessage("儲存失敗", "error");
      return;
    }

    modalOpen.value = false;
    clearForm();
    showMessage(isUpdate ? "修改成功" : "新增成功", "success");
    await loadTimes();
  } catch (error) {
    console.error(error);
    showMessage("儲存失敗", "error");
  } finally {
    saving.value = false;
  }
}

onMounted(async () => {
  await loadRestaurants();
  await loadTimes();
});
</script>

<template>
  <main class="time-page">
    <div class="admin-page-header">
      <div>
        <h1>餐廳時段管理</h1>
        <p>管理星澄飯店各餐廳的早餐、午餐、晚餐與營業時段</p>
      </div>
    </div>

    <div v-if="message" class="admin-message page-message" :class="messageType">
      {{ message }}
    </div>

    <section class="admin-card time-list-card">
      <div class="time-list-header">
        <h2>時段列表</h2>

        <div class="list-header-actions">
          <button type="button" class="admin-btn admin-btn-primary" @click="openCreateModal">
            ＋ 新增時段
          </button>

          <button type="button" class="admin-btn admin-btn-secondary" @click="loadTimes">
            重新整理
          </button>
        </div>
      </div>

      <div v-if="loading" class="loading-message">時段資料讀取中...</div>

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
              <td>{{ time.timeId }}</td>
              <td class="restaurant-name">{{ getRestaurantName(time.restaurantId) }}</td>
              <td>{{ time.mealType }}</td>
              <td>{{ formatTime(time.openTime) }}</td>
              <td>{{ formatTime(time.closeTime) }}</td>
              <td>
                <div class="time-actions">
                  <button type="button" class="admin-btn admin-btn-edit" @click="editTime(time)">
                    修改
                  </button>

                  <button type="button" class="admin-btn admin-btn-delete" @click="deleteTime(time.timeId)">
                    刪除
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <Teleport to="body">
      <div v-if="modalOpen" class="modal-overlay" @click.self="closeModal">
        <section class="time-modal" role="dialog" aria-modal="true">
          <header class="modal-header">
            <h2>{{ formTitle }}</h2>

            <button type="button" class="modal-close" @click="closeModal">
              ×
            </button>
          </header>

          <form class="modal-body" @submit.prevent="saveTime">
            <div class="admin-form-grid">
              <div class="admin-form-group">
                <label for="restaurantId">餐廳 *</label>
                <select id="restaurantId" v-model="form.restaurantId" required>
                  <option value="">請選擇餐廳</option>
                  <option v-for="restaurant in restaurants" :key="restaurant.restaurantId"
                    :value="String(restaurant.restaurantId)">
                    {{ restaurant.restaurantName }}
                  </option>
                </select>
              </div>

              <div class="admin-form-group">
                <label for="mealType">餐期 *</label>
                <select id="mealType" v-model="form.mealType" required>
                  <option value="早餐">早餐</option>
                  <option value="午餐">午餐</option>
                  <option value="晚餐">晚餐</option>
                  <option value="晚間">晚間</option>
                </select>
              </div>

              <div class="admin-form-group">
                <label for="openTime">開始時間 *</label>
                <input id="openTime" v-model="form.openTime" type="time" required />
              </div>

              <div class="admin-form-group">
                <label for="closeTime">結束時間 *</label>
                <input id="closeTime" v-model="form.closeTime" type="time" required />
              </div>
            </div>

            <div class="admin-form-actions modal-actions">
              <button type="submit" class="admin-btn admin-btn-primary" :disabled="saving">
                {{ saving ? "儲存中..." : "儲存" }}
              </button>

              <button type="button" class="admin-btn admin-btn-secondary" @click="closeModal">
                取消
              </button>
            </div>
          </form>
        </section>
      </div>
    </Teleport>
  </main>
</template>

<style scoped>
.time-page {
  width: 100%;
}

.time-list-card {
  margin-top: 28px;
}

.page-message {
  margin-bottom: 20px;
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

.list-header-actions {
  display: flex;
  gap: 10px;
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

.modal-overlay {
  position: fixed;
  z-index: 2000;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background: rgba(37, 30, 22, 0.55);
}

.time-modal {
  width: min(920px, 100%);
  overflow: auto;
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 22px 55px rgba(0, 0, 0, 0.26);
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 28px;
  color: #fff;
  background: #4d3b28;
  border-radius: 14px 14px 0 0;
}

.modal-header h2 {
  margin: 0;
  font-size: 22px;
}

.modal-close {
  padding: 0 5px;
  border: 0;
  color: #fff;
  background: transparent;
  font-size: 32px;
  line-height: 1;
  cursor: pointer;
}

.modal-body {
  padding: 28px;
}

.modal-actions {
  margin-top: 24px;
}

@media (max-width: 700px) {
  .time-list-header {
    align-items: stretch;
    flex-direction: column;
  }

  .list-header-actions {
    width: 100%;
  }

  .list-header-actions .admin-btn {
    flex: 1;
  }

  .modal-overlay {
    align-items: flex-start;
    padding: 14px;
  }

  .time-modal {
    max-height: calc(100vh - 28px);
  }

  .modal-header,
  .modal-body {
    padding: 20px;
  }
}
</style>
