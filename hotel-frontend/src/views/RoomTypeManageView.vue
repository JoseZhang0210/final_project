<script setup>
import { ref } from "vue";

const roomTypes = ref([
  {
    roomTypeId: 1,
    typeName: "豪華雙人房",
    bedType: "一張雙人床",
    capacity: 2,
    pricePerNight: 5000,
    roomDescription: "附景觀陽台、浴缸及免費早餐",
  },
  {
    roomTypeId: 2,
    typeName: "家庭四人房",
    bedType: "兩張雙人床",
    capacity: 4,
    pricePerNight: 8000,
    roomDescription: "適合家庭旅遊，提供寬敞客廳空間",
  },
]);

const formTitle = ref("新增房型");
const message = ref("");
const messageType = ref("");

const form = ref(createEmptyForm());

function createEmptyForm() {
  return {
    roomTypeId: null,
    typeName: "",
    bedType: "",
    capacity: 1,
    pricePerNight: 0,
    roomDescription: "",
  };
}

function clearForm() {
  form.value = createEmptyForm();
  formTitle.value = "新增房型";
}

function saveRoomType() {
  if (!form.value.typeName.trim()) {
    showMessage("請輸入房型名稱", "error");
    return;
  }

  if (!form.value.bedType.trim()) {
    showMessage("請輸入床型", "error");
    return;
  }

  if (Number(form.value.capacity) < 1) {
    showMessage("容納人數至少為 1 人", "error");
    return;
  }

  if (Number(form.value.pricePerNight) < 0) {
    showMessage("每晚價格不可小於 0", "error");
    return;
  }

  if (form.value.roomTypeId === null) {
    const nextId =
      roomTypes.value.length === 0
        ? 1
        : Math.max(...roomTypes.value.map((room) => room.roomTypeId)) + 1;

    roomTypes.value.push({
      ...form.value,
      roomTypeId: nextId,
      capacity: Number(form.value.capacity),
      pricePerNight: Number(form.value.pricePerNight),
    });

    showMessage("房型新增成功", "success");
  } else {
    const index = roomTypes.value.findIndex(
      (room) => room.roomTypeId === form.value.roomTypeId,
    );

    if (index !== -1) {
      roomTypes.value[index] = {
        ...form.value,
        capacity: Number(form.value.capacity),
        pricePerNight: Number(form.value.pricePerNight),
      };
    }

    showMessage("房型修改成功", "success");
  }

  clearForm();
}

function editRoomType(roomType) {
  form.value = { ...roomType };
  formTitle.value = `修改房型：${roomType.typeName}`;

  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
}

function deleteRoomType(id) {
  const roomType = roomTypes.value.find((room) => room.roomTypeId === id);

  if (!window.confirm(`確定要刪除「${roomType?.typeName}」嗎？`)) {
    return;
  }

  roomTypes.value = roomTypes.value.filter(
    (room) => room.roomTypeId !== id,
  );

  if (form.value.roomTypeId === id) {
    clearForm();
  }

  showMessage("房型已刪除", "success");
}

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
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
  <main class="room-type-page">
    <header class="page-header">
      <div>
        <h1>房型資料管理</h1>
        <p>管理飯店房型、床型、容納人數與每晚價格</p>
      </div>
    </header>

    <div
      v-if="message"
      class="message"
      :class="messageType"
    >
      {{ message }}
    </div>

    <section class="admin-card">
      <h2>{{ formTitle }}</h2>

      <form @submit.prevent="saveRoomType">
        <div class="form-grid">
          <div class="form-group">
            <label for="typeName">房型名稱 *</label>
            <input
              id="typeName"
              v-model.trim="form.typeName"
              type="text"
              placeholder="例如：豪華雙人房"
              required
            />
          </div>

          <div class="form-group">
            <label for="bedType">床型 *</label>
            <input
              id="bedType"
              v-model.trim="form.bedType"
              type="text"
              placeholder="例如：一張雙人床"
              required
            />
          </div>

          <div class="form-group">
            <label for="capacity">容納人數 *</label>
            <input
              id="capacity"
              v-model.number="form.capacity"
              type="number"
              min="1"
              required
            />
          </div>

          <div class="form-group">
            <label for="pricePerNight">每晚價格 *</label>
            <input
              id="pricePerNight"
              v-model.number="form.pricePerNight"
              type="number"
              min="0"
              required
            />
          </div>

          <div class="form-group full-width">
            <label for="roomDescription">房型說明</label>
            <textarea
              id="roomDescription"
              v-model.trim="form.roomDescription"
              rows="4"
              placeholder="請輸入房型特色及設備說明"
            ></textarea>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn primary">
            {{ form.roomTypeId === null ? "新增房型" : "儲存修改" }}
          </button>

          <button type="button" class="btn secondary" @click="clearForm">
            清除表單
          </button>
        </div>
      </form>
    </section>

    <section class="admin-card">
      <div class="table-header">
        <h2>房型列表</h2>
        <span>共 {{ roomTypes.length }} 種房型</span>
      </div>

      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>房型名稱</th>
              <th>床型</th>
              <th>人數</th>
              <th>每晚價格</th>
              <th>房型說明</th>
              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-for="roomType in roomTypes" :key="roomType.roomTypeId">
              <td>{{ roomType.roomTypeId }}</td>
              <td>{{ roomType.typeName }}</td>
              <td>{{ roomType.bedType }}</td>
              <td>{{ roomType.capacity }} 人</td>
              <td>{{ formatPrice(roomType.pricePerNight) }}</td>
              <td>{{ roomType.roomDescription || "—" }}</td>
              <td class="action-cell">
                <button
                  type="button"
                  class="btn edit"
                  @click="editRoomType(roomType)"
                >
                  修改
                </button>

                <button
                  type="button"
                  class="btn delete"
                  @click="deleteRoomType(roomType.roomTypeId)"
                >
                  刪除
                </button>
              </td>
            </tr>

            <tr v-if="roomTypes.length === 0">
              <td colspan="7" class="empty">目前沒有房型資料</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</template>

<style scoped>
.room-type-page {
  padding: 28px;
  color: #243447;
}

.page-header {
  margin-bottom: 22px;
}

.page-header h1 {
  margin: 0 0 8px;
  font-size: 30px;
}

.page-header p {
  margin: 0;
  color: #667085;
}

.admin-card {
  margin-bottom: 24px;
  padding: px;
  padding: 24px;
  background: white;
  border: 1px solid #e4e7ec;
  border-radius: 12px;
  box-shadow: 0 3px 12px rgb(0 0 0 / 6%);
}

.admin-card h2 {
  margin: 0 0 20px;
}

.message {
  margin-bottom: 20px;
  padding: 12px 16px;
  border-radius: 8px;
}

.message.success {
  color: #176b3a;
  background: #e9f8ef;
}

.message.error {
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

.full-width {
  grid-column: 1 / -1;
}

label {
  font-weight: 600;
}

input,
textarea {
  padding: 11px 12px;
  font: inherit;
  border: 1px solid #cfd4dc;
  border-radius: 7px;
}

input:focus,
textarea:focus {
  border-color: #315b7d;
  outline: none;
}

.form-actions,
.action-cell {
  display: flex;
  gap: 10px;
}

.form-actions {
  margin-top: 20px;
}

.btn {
  padding: 9px 15px;
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
  padding: 13px 12px;
  text-align: left;
  border-bottom: 1px solid #e4e7ec;
}

th {
  background: #f8fafc;
}

.empty {
  padding: 30px;
  text-align: center;
  color: #667085;
}

@media (max-width: 768px) {
  .room-type-page {
    padding: 16px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .full-width {
    grid-column: auto;
  }
}
</style>