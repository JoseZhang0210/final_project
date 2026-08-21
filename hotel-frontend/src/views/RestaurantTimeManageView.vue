<script setup>
import { computed, onMounted, ref } from "vue";

const RESTAURANT_API_URL = "/api/restaurant";
const TIME_API_URL = "/api/restaurant_times";

const restaurants = ref([]);
const times = ref([]);
const formTitle = ref("新增時段");
const message = ref("");
const messageType = ref("");

const form = ref({
  timeId: null,
  restaurantId: "",
  mealType: "早餐",
  openTime: "",
  closeTime: "",
});

const isEditing = computed(() => form.value.timeId !== null);

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
}

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
    const response = await fetch(RESTAURANT_API_URL);

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
  try {
    const response = await fetch(TIME_API_URL);

    if (!response.ok) {
      showMessage("讀取時段資料失敗", "error");
      return;
    }

    times.value = await response.json();
  } catch (error) {
    console.error(error);
    showMessage("讀取時段資料失敗", "error");
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
  window.scrollTo({ top: 0, behavior: "smooth" });
}

async function deleteTime(id) {
  if (!window.confirm("確定要刪除這個時段嗎？")) {
    return;
  }

  try {
    const response = await fetch(`${TIME_API_URL}/${id}`, {
      method: "DELETE",
    });

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

async function saveTime() {
  const restaurantTime = {
    restaurantId: Number(form.value.restaurantId),
    mealType: form.value.mealType,
    openTime: form.value.openTime,
    closeTime: form.value.closeTime,
  };

  try {
    const response = await fetch(
      isEditing.value ? `${TIME_API_URL}/${form.value.timeId}` : TIME_API_URL,
      {
        method: isEditing.value ? "PUT" : "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(restaurantTime),
      },
    );

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
  }
}

onMounted(async () => {
  await loadRestaurants();
  await loadTimes();
});
</script>

<template>
  <main class="time-page">
    <section class="hero">
      <h1>餐廳時段管理</h1>
      <p>管理星澄飯店各餐廳的早餐、午餐、晚餐與營業時段。</p>
    </section>

    <div class="container">
      <section class="card">
        <h2>{{ formTitle }}</h2>

        <form @submit.prevent="saveTime">
          <div class="form-grid">
            <div class="form-group">
              <label for="restaurantId">餐廳 *</label>

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

            <div class="form-group">
              <label for="mealType">餐期 *</label>

              <select id="mealType" v-model="form.mealType" required>
                <option value="早餐">早餐</option>
                <option value="午餐">午餐</option>
                <option value="晚餐">晚餐</option>
                <option value="晚間">晚間</option>
              </select>
            </div>

            <div class="form-group">
              <label for="openTime">開始時間 *</label>

              <input
                id="openTime"
                v-model="form.openTime"
                type="time"
                required
              />
            </div>

            <div class="form-group">
              <label for="closeTime">結束時間 *</label>

              <input
                id="closeTime"
                v-model="form.closeTime"
                type="time"
                required
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
        <h2>時段列表</h2>

        <div class="table-wrapper">
          <table>
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
                <td>{{ getRestaurantName(time.restaurantId) }}</td>
                <td>{{ time.mealType }}</td>
                <td>{{ formatTime(time.openTime) }}</td>
                <td>{{ formatTime(time.closeTime) }}</td>
                <td class="actions">
                  <button
                    type="button"
                    class="btn-edit"
                    @click="editTime(time)"
                  >
                    修改
                  </button>

                  <button
                    type="button"
                    class="btn-delete"
                    @click="deleteTime(time.timeId)"
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
.time-page {
  min-height: 100vh;
  padding-bottom: 60px;
  background: #f8f6f1;
  color: #333;
}

.hero {
  padding: 60px 20px;
  color: white;
  text-align: center;
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

label {
  color: #554536;
  font-size: 14px;
  font-weight: bold;
}

input,
select {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #d9d2c7;
  border-radius: 8px;
  background: white;
  font: inherit;
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
  min-width: 850px;
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

.empty-row {
  padding: 32px;
  text-align: center;
  color: #888;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
