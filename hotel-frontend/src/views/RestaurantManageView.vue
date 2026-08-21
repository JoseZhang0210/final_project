<script setup>
import { onMounted, ref } from "vue";

const API_URL = "/api/restaurant";

const restaurants = ref([]);
const formTitle = ref("新增餐廳");
const message = ref("");
const messageType = ref("");

const form = ref({
  restaurantId: null,
  restaurantName: "",
  address: "",
  phone: "",
  capacity: null,
  description: "",
});

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
}

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

async function loadRestaurants() {
  try {
    const response = await fetch(API_URL);

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
  window.scrollTo({ top: 0, behavior: "smooth" });
}

async function deleteRestaurant(id) {
  if (!window.confirm("確定要刪除這間餐廳嗎？")) {
    return;
  }

  try {
    const response = await fetch(`${API_URL}/${id}`, {
      method: "DELETE",
    });

    if (!response.ok) {
      showMessage("刪除失敗", "error");
      return;
    }

    clearForm();
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

  try {
    const response = await fetch(
      isCreate ? API_URL : `${API_URL}/${form.value.restaurantId}`,
      {
        method: isCreate ? "POST" : "PUT",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(restaurant),
      },
    );

    if (!response.ok) {
      showMessage("儲存失敗", "error");
      return;
    }

    clearForm();
    showMessage(isCreate ? "新增成功" : "修改成功", "success");
    await loadRestaurants();
  } catch (error) {
    console.error(error);
    showMessage("儲存失敗", "error");
  }
}

onMounted(loadRestaurants);
</script>

<template>
  <main class="restaurant-page">
    <section class="hero">
      <h1>餐廳資料管理</h1>
      <p>管理星澄飯店餐廳的基本資料、聯絡資訊與容納人數。</p>
    </section>

    <div class="container">
      <section class="card">
        <h2>{{ formTitle }}</h2>

        <form @submit.prevent="saveRestaurant">
          <div class="form-grid">
            <div class="form-group">
              <label for="restaurantName">餐廳名稱 *</label>
              <input
                id="restaurantName"
                v-model.trim="form.restaurantName"
                type="text"
                placeholder="請輸入餐廳名稱"
                required
              />
            </div>

            <div class="form-group">
              <label for="phone">電話</label>
              <input
                id="phone"
                v-model.trim="form.phone"
                type="text"
                placeholder="請輸入聯絡電話"
              />
            </div>

            <div class="form-group">
              <label for="address">地址</label>
              <input
                id="address"
                v-model.trim="form.address"
                type="text"
                placeholder="請輸入餐廳地址"
              />
            </div>

            <div class="form-group">
              <label for="capacity">容納人數</label>
              <input
                id="capacity"
                v-model="form.capacity"
                type="number"
                min="1"
                placeholder="請輸入最大容納人數"
              />
            </div>

            <div class="form-group full-width">
              <label for="description">餐廳介紹</label>
              <textarea
                id="description"
                v-model.trim="form.description"
                placeholder="請輸入餐廳特色與介紹"
              />
            </div>
          </div>

          <div class="form-actions">
            <button type="submit" class="btn-save">儲存</button>
            <button type="button" class="btn-clear" @click="clearForm">
              清除
            </button>
          </div>

          <p v-if="message" class="message" :class="messageType">
            {{ message }}
          </p>
        </form>
      </section>

      <section class="card">
        <h2>餐廳列表</h2>

        <div class="table-wrapper">
          <table>
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

              <tr
                v-for="restaurant in restaurants"
                :key="restaurant.restaurantId"
              >
                <td>{{ restaurant.restaurantId }}</td>
                <td>{{ restaurant.restaurantName }}</td>
                <td>{{ restaurant.address }}</td>
                <td>{{ restaurant.phone }}</td>
                <td>{{ restaurant.capacity }}</td>
                <td>{{ restaurant.description }}</td>
                <td class="actions">
                  <button
                    type="button"
                    class="btn-edit"
                    @click="editRestaurant(restaurant)"
                  >
                    修改
                  </button>

                  <button
                    type="button"
                    class="btn-delete"
                    @click="deleteRestaurant(restaurant.restaurantId)"
                  >
                    刪除
                  </button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </div>
  </main>
</template>

<style scoped>
.restaurant-page {
  min-height: 100vh;
  background: #f8f6f1;
  color: #333;
  padding-bottom: 60px;
}

.hero {
  padding: 60px 20px;
  text-align: center;
  color: white;
  background:
    linear-gradient(rgba(0, 0, 0, 0.52), rgba(0, 0, 0, 0.52)),
    url("https://images.unsplash.com/photo-1517248135467-4c7edcad34c4?auto=format&fit=crop&w=1800&q=80")
      center / cover;
}

.hero h1 {
  margin: 0 0 12px;
  font-size: 40px;
}

.hero p {
  margin: 0;
  font-size: 17px;
}

.container {
  width: min(1180px, 92%);
  margin: 42px auto;
}

.back-btn {
  display: inline-block;
  margin-bottom: 20px;
  padding: 10px 18px;
  border-radius: 8px;
  background: #eee9e1;
  color: #5c4d3d;
  font-weight: bold;
  text-decoration: none;
}

.card {
  margin-bottom: 28px;
  padding: 28px;
  border-radius: 14px;
  background: white;
  box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08);
}

.card h2 {
  margin-top: 0;
  margin-bottom: 22px;
  color: #6f5328;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.full-width {
  grid-column: 1 / -1;
}

label {
  color: #554536;
  font-size: 14px;
  font-weight: bold;
}

input,
textarea {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #d9d2c7;
  border-radius: 8px;
  font: inherit;
}

textarea {
  min-height: 100px;
  resize: vertical;
}

.form-actions,
.actions {
  display: flex;
  gap: 10px;
}

.form-actions {
  margin-top: 22px;
}

button {
  padding: 11px 18px;
  border: 0;
  border-radius: 8px;
  cursor: pointer;
  font: inherit;
  font-weight: bold;
}

.btn-save {
  background: #b58a46;
  color: white;
}

.btn-clear {
  background: #eee9e1;
  color: #5c4d3d;
}

.btn-edit {
  background: #fff3d8;
  color: #95691f;
}

.btn-delete {
  background: #fde9e7;
  color: #b3443c;
}

.message {
  margin: 15px 0 0;
  padding: 11px 13px;
  border-radius: 8px;
  font-weight: bold;
}

.success {
  background: #e5f6eb;
  color: #257641;
}

.error {
  background: #fde9e7;
  color: #b3443c;
}

.table-wrapper {
  overflow-x: auto;
}

table {
  width: 100%;
  min-width: 900px;
  border-collapse: collapse;
}

thead {
  background: #4a3b2a;
  color: white;
}

th,
td {
  padding: 14px 12px;
  border-bottom: 1px solid #eee7dd;
  text-align: left;
}

td {
  color: #555;
}

.empty-row {
  padding: 32px;
  text-align: center;
  color: #888;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .full-width {
    grid-column: auto;
  }
}
</style>
