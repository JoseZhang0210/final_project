<script setup>
import { onMounted, ref } from "vue";

const API_URL = "/api/restaurant";

const restaurants = ref([]);

const formTitle = ref("新增餐廳");

const message = ref("");

const messageType = ref("");

const loading = ref(false);

const saving = ref(false);

const form = ref({
  restaurantId: null,
  restaurantName: "",
  address: "",
  phone: "",
  capacity: null,
  description: "",
});

// =====================================================
// 顯示訊息
// =====================================================

function showMessage(text, type) {
  message.value = text;

  messageType.value = type;
}

// =====================================================
// 清除表單
// =====================================================

function clearForm() {
  form.value = {
    restaurantId: null,
    restaurantName: "",
    address: "",
    phone: "",
    capacity: null,
    description: "",
  };

  formTitle.value = "新增餐廳";

  message.value = "";

  messageType.value = "";
}

// =====================================================
// JWT Header
// =====================================================

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

// =====================================================
// 讀取餐廳
// GET /api/restaurant
// =====================================================

async function loadRestaurants() {
  loading.value = true;

  try {
    const response = await fetch(API_URL, {
      method: "GET",

      headers: getAuthHeaders(),
    });

    console.log("餐廳 API status：", response.status);

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有權限", "error");

      return;
    }

    if (!response.ok) {
      showMessage("讀取餐廳資料失敗", "error");

      return;
    }

    restaurants.value = await response.json();

    console.log("餐廳資料：", restaurants.value);
  } catch (error) {
    console.error("讀取餐廳錯誤：", error);

    showMessage("讀取餐廳資料失敗", "error");
  } finally {
    loading.value = false;
  }
}

// =====================================================
// 修改餐廳
// =====================================================

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

  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
}

// =====================================================
// 刪除餐廳
// DELETE /api/restaurant/{id}
// =====================================================

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

    clearForm();

    showMessage("餐廳已刪除", "success");

    await loadRestaurants();
  } catch (error) {
    console.error("刪除餐廳錯誤：", error);

    showMessage("刪除失敗", "error");
  }
}

// =====================================================
// 新增 / 修改餐廳
//
// POST /api/restaurant
// PUT  /api/restaurant/{id}
// =====================================================

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

    console.log("儲存餐廳 status：", response.status);

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有操作權限", "error");

      return;
    }

    if (!response.ok) {
      showMessage("儲存失敗", "error");

      return;
    }

    clearForm();

    showMessage(isCreate ? "新增成功" : "修改成功", "success");

    await loadRestaurants();
  } catch (error) {
    console.error("儲存餐廳錯誤：", error);

    showMessage("儲存失敗", "error");
  } finally {
    saving.value = false;
  }
}

// =====================================================
// 頁面載入
// =====================================================

onMounted(() => {
  console.log("餐廳頁 JWT：", localStorage.getItem("token"));

  loadRestaurants();
});
</script>

<template>
  <main class="restaurant-page">
    <!-- =========================
         標題
         ========================= -->

    <div class="admin-page-header">
      <div>
        <h1>餐廳資料管理</h1>

        <p>管理星澄飯店餐廳的基本資料、聯絡資訊與容納人數</p>
      </div>
    </div>

    <!-- =========================
         餐廳表單
         ========================= -->

    <section class="admin-card restaurant-form-card">
      <h2>
        {{ formTitle }}
      </h2>

      <form @submit.prevent="saveRestaurant">
        <div class="admin-form-grid">
          <!-- 餐廳名稱 -->
          <div class="admin-form-group">
            <label for="restaurantName"> 餐廳名稱 * </label>

            <input
              id="restaurantName"
              v-model.trim="form.restaurantName"
              type="text"
              placeholder="請輸入餐廳名稱"
              required
            />
          </div>

          <!-- 電話 -->
          <div class="admin-form-group">
            <label for="phone"> 電話 </label>

            <input
              id="phone"
              v-model.trim="form.phone"
              type="text"
              placeholder="請輸入聯絡電話"
            />
          </div>

          <!-- 地址 -->
          <div class="admin-form-group">
            <label for="address"> 地址 </label>

            <input
              id="address"
              v-model.trim="form.address"
              type="text"
              placeholder="請輸入餐廳地址"
            />
          </div>

          <!-- 容納人數 -->
          <div class="admin-form-group">
            <label for="capacity"> 容納人數 </label>

            <input
              id="capacity"
              v-model="form.capacity"
              type="number"
              min="1"
              placeholder="請輸入最大容納人數"
            />
          </div>

          <!-- 餐廳介紹 -->
          <div class="admin-form-group full-width">
            <label for="description"> 餐廳介紹 </label>

            <textarea
              id="description"
              v-model.trim="form.description"
              placeholder="請輸入餐廳特色與介紹"
            ></textarea>
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
         餐廳列表
         ========================= -->

    <section class="admin-card restaurant-list-card">
      <div class="restaurant-list-header">
        <h2>餐廳列表</h2>

        <button
          type="button"
          class="admin-btn admin-btn-secondary"
          @click="loadRestaurants"
        >
          重新整理
        </button>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-message">餐廳資料讀取中...</div>

      <!-- Table -->
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
            <!-- 沒資料 -->
            <tr v-if="restaurants.length === 0">
              <td colspan="7" class="empty-row">目前沒有餐廳資料</td>
            </tr>

            <!-- 餐廳資料 -->
            <tr
              v-for="restaurant in restaurants"
              :key="restaurant.restaurantId"
            >
              <td>
                {{ restaurant.restaurantId }}
              </td>

              <td class="restaurant-name">
                {{ restaurant.restaurantName }}
              </td>

              <td>
                {{ restaurant.address }}
              </td>

              <td>
                {{ restaurant.phone }}
              </td>

              <td>
                {{ restaurant.capacity }}
              </td>

              <td class="description-cell">
                {{ restaurant.description }}
              </td>

              <td>
                <div class="restaurant-actions">
                  <button
                    type="button"
                    class="admin-btn admin-btn-edit"
                    @click="editRestaurant(restaurant)"
                  >
                    修改
                  </button>

                  <button
                    type="button"
                    class="admin-btn admin-btn-delete"
                    @click="deleteRestaurant(restaurant.restaurantId)"
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
  </main>
</template>

<style scoped>
.restaurant-page {
  width: 100%;
}

/* =========================
   Card 間距
   ========================= */

.restaurant-form-card {
  margin-bottom: 28px;
}

.restaurant-form-card h2 {
  margin-top: 0;

  margin-bottom: 22px;

  color: #6f5328;
}

/* =========================
   列表 Header
   ========================= */

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

/* =========================
   Table
   ========================= */

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

/* =========================
   Loading
   ========================= */

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

/* =========================
   RWD
   ========================= */

@media (max-width: 700px) {
  .restaurant-list-header {
    align-items: stretch;

    flex-direction: column;
  }
}
</style>
