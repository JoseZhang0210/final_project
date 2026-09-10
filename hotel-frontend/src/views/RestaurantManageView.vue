<script setup>
import { onMounted, ref } from "vue";

const API_URL = "/api/restaurant";
const restaurants = ref([]);
const formTitle = ref("新增餐廳");
const message = ref("");
const messageType = ref("");
const loading = ref(false);
const saving = ref(false);
const modalOpen = ref(false);
const form = ref(createEmptyForm());

function createEmptyForm() {
  return {
    restaurantId: null,
    restaurantName: "",
    address: "",
    phone: "",
    capacity: null,
    description: "",
  };
}

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
}

function clearForm() {
  form.value = createEmptyForm();
  formTitle.value = "新增餐廳";
}

function openCreateModal() {
  clearForm();
  modalOpen.value = true;
}

function closeModal() {
  modalOpen.value = false;
  clearForm();
}

// 呼叫後端時帶入登入 Token。
function getAuthHeaders() {
  const token = localStorage.getItem("token");
  const headers = { "Content-Type": "application/json" };

  if (token) {
    headers.Authorization = "Bearer " + token;
  }

  return headers;
}

async function loadRestaurants() {
  loading.value = true;

  try {
    const response = await fetch(API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有權限", "error");
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
  } finally {
    loading.value = false;
  }
}

function editRestaurant(restaurant) {
  form.value = {
    restaurantId: restaurant.restaurantId,
    restaurantName: restaurant.restaurantName ?? "",
    address: restaurant.address ?? "",
    phone: restaurant.phone ?? "",
    capacity: restaurant.capacity ?? null,
    description: restaurant.description ?? "",
  };

  formTitle.value = "修改餐廳";
  modalOpen.value = true;
}

async function deleteRestaurant(id) {
  if (!window.confirm("確定要刪除這間餐廳嗎？")) {
    return;
  }

  try {
    const response = await fetch(`${API_URL}/${id}`, {
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

    showMessage("餐廳已刪除", "success");
    await loadRestaurants();
  } catch (error) {
    console.error(error);
    showMessage("刪除失敗", "error");
  }
}

async function saveRestaurant() {
  const isCreate = form.value.restaurantId === null;
  const restaurant = {
    restaurantName: form.value.restaurantName,
    address: form.value.address,
    phone: form.value.phone,
    capacity:
      form.value.capacity === "" || form.value.capacity === null
        ? null
        : Number(form.value.capacity),
    description: form.value.description,
  };

  saving.value = true;

  try {
    const url = isCreate ? API_URL : `${API_URL}/${form.value.restaurantId}`;
    const response = await fetch(url, {
      method: isCreate ? "POST" : "PUT",
      headers: getAuthHeaders(),
      body: JSON.stringify(restaurant),
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
    showMessage(isCreate ? "新增成功" : "修改成功", "success");
    await loadRestaurants();
  } catch (error) {
    console.error(error);
    showMessage("儲存失敗", "error");
  } finally {
    saving.value = false;
  }
}

onMounted(loadRestaurants);
</script>

<template>
  <main class="restaurant-page">
    <div class="admin-page-header">
      <div>
        <h1>餐廳資料管理</h1>
        <p>管理星澄飯店餐廳的基本資料、聯絡資訊與容納人數</p>
      </div>
    </div>

    <div v-if="message" class="admin-message page-message" :class="messageType">
      {{ message }}
    </div>

    <section class="admin-card restaurant-list-card">
      <div class="restaurant-list-header">
        <h2>餐廳列表</h2>

        <div class="list-header-actions">
          <button type="button" class="admin-btn admin-btn-primary" @click="openCreateModal">
            ＋ 新增餐廳
          </button>

          <button type="button" class="admin-btn admin-btn-secondary" @click="loadRestaurants">
            重新整理
          </button>
        </div>
      </div>

      <div v-if="loading" class="loading-message">餐廳資料讀取中...</div>

      <div v-else class="admin-table-wrapper">
        <table class="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>餐廳名稱</th>
              <th>地址</th>
              <th>電話</th>
              <th>容納人數</th>
              <th>餐廳介紹</th>
              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="restaurants.length === 0">
              <td colspan="7" class="empty-row">目前沒有餐廳資料</td>
            </tr>

            <tr v-for="restaurant in restaurants" :key="restaurant.restaurantId">
              <td>{{ restaurant.restaurantId }}</td>
              <td class="restaurant-name">{{ restaurant.restaurantName }}</td>
              <td>{{ restaurant.address }}</td>
              <td>{{ restaurant.phone }}</td>
              <td>{{ restaurant.capacity }}</td>
              <td class="description-cell">{{ restaurant.description }}</td>
              <td>
                <div class="restaurant-actions">
                  <button type="button" class="admin-btn admin-btn-edit" @click="editRestaurant(restaurant)">
                    修改
                  </button>

                  <button type="button" class="admin-btn admin-btn-delete"
                    @click="deleteRestaurant(restaurant.restaurantId)">
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
        <section class="restaurant-modal" role="dialog" aria-modal="true">
          <header class="modal-header">
            <h2>{{ formTitle }}</h2>

            <button type="button" class="modal-close" @click="closeModal">
              ×
            </button>
          </header>

          <form class="modal-body" @submit.prevent="saveRestaurant">
            <div class="admin-form-grid">
              <div class="admin-form-group">
                <label for="restaurantName">餐廳名稱 *</label>
                <input id="restaurantName" v-model.trim="form.restaurantName" type="text" placeholder="請輸入餐廳名稱"
                  required />
              </div>

              <div class="admin-form-group">
                <label for="phone">電話</label>
                <input id="phone" v-model.trim="form.phone" type="text" placeholder="請輸入聯絡電話" />
              </div>

              <div class="admin-form-group">
                <label for="address">地址</label>
                <input id="address" v-model.trim="form.address" type="text" placeholder="請輸入餐廳地址" />
              </div>

              <div class="admin-form-group">
                <label for="capacity">容納人數</label>
                <input id="capacity" v-model="form.capacity" type="number" min="1" placeholder="請輸入最大容納人數" />
              </div>

              <div class="admin-form-group full-width">
                <label for="description">餐廳介紹</label>
                <textarea id="description" v-model.trim="form.description" placeholder="請輸入餐廳特色與介紹"></textarea>
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
.restaurant-page {
  width: 100%;
}

.admin-page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
}

.page-message {
  margin-bottom: 20px;
}

.restaurant-list-card {
  margin-top: 28px;
}

.restaurant-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 15px;
  margin-bottom: 22px;
}

.restaurant-list-header h2 {
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

.description-cell {
  max-width: 280px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.restaurant-actions {
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

.restaurant-modal {
  width: min(920px, 100%);
  max-height: calc(100vh - 48px);
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

  .admin-page-header,
  .restaurant-list-header {
    align-items: stretch;
    flex-direction: column;
  }

  .modal-overlay {
    align-items: flex-start;
    padding: 14px;
  }

  .list-header-actions {
    width: 100%;
  }

  .list-header-actions .admin-btn {
    flex: 1;
  }

  .restaurant-modal {
    max-height: calc(100vh - 28px);
  }

  .modal-header,
  .modal-body {
    padding: 20px;
  }
}
</style>
