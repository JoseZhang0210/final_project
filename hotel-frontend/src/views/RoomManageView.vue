<script setup>
import { onMounted, ref } from "vue";

const ROOM_API_URL = "/api/rooms";

// 暫時提供隱藏表單使用
// 後續可改成 GET /api/room-types
const roomTypes = ref([
  { roomTypeId: 1, typeName: "豪華雙人房" },
  { roomTypeId: 2, typeName: "家庭四人房" },
]);

const rooms = ref([]);
const loading = ref(false);

const message = ref("");
const messageType = ref("");
const formTitle = ref("新增房間");

const form = ref(createEmptyForm());

const roomStatuses = [
  "可入住",
  "已入住",
  "清潔中",
  "維修中",
  "停用",
];

function createEmptyForm() {
  return {
    roomId: null,
    roomNumber: "",
    roomTypeId: "",
    floor: 1,
    roomStatus: "可入住",
  };
}

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
}

function clearForm() {
  form.value = createEmptyForm();
  formTitle.value = "新增房間";
}

function getRoomTypeName(room) {
  // 後端若有回傳房型名稱，就顯示名稱
  if (room.roomType?.typeName) {
    return room.roomType.typeName;
  }

  // 沒有名稱但有房型 ID，就顯示 ID
  if (room.roomType?.roomTypeId) {
    return `房型 ID：${room.roomType.roomTypeId}`;
  }

  return "未知房型";
}

function getAuthHeaders() {
  const token = localStorage.getItem("token");

  const headers = {
    "Content-Type": "application/json",
  };

  if (token) {
    headers.Authorization = `Bearer ${token}`;
  }

  return headers;
}

// =====================================================
// 從 SQL 讀取房間
// GET /api/rooms
// =====================================================

async function loadRooms() {
  loading.value = true;
  message.value = "";

  try {
    const response = await fetch(ROOM_API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
      credentials: "include",
    });

    if (response.status === 401) {
      showMessage("請先登入後再查看房間資料", "error");
      return;
    }

    if (response.status === 403) {
      showMessage("目前帳號沒有查看房間資料的權限", "error");
      return;
    }

    if (!response.ok) {
      showMessage(`讀取房間失敗：${response.status}`, "error");
      return;
    }

    const data = await response.json();

    rooms.value = Array.isArray(data)
      ? data
      : data.content || [];

    console.log("SQL room 資料：", rooms.value);
  } catch (error) {
    console.error("讀取房間錯誤：", error);
    showMessage("無法連線至房間 API", "error");
  } finally {
    loading.value = false;
  }
}

// =====================================================
// 新增／修改房間
// 目前保留原本前端測試功能
// =====================================================

function saveRoom() {
  if (!form.value.roomNumber.trim()) {
    showMessage("請輸入房號", "error");
    return;
  }

  if (!form.value.roomTypeId) {
    showMessage("請選擇房型", "error");
    return;
  }

  const duplicate = rooms.value.some(
    (room) =>
      room.roomNumber === form.value.roomNumber.trim() &&
      room.roomId !== form.value.roomId,
  );

  if (duplicate) {
    showMessage("房號不可重複", "error");
    return;
  }

  const roomData = {
    ...form.value,
    roomNumber: form.value.roomNumber.trim(),
    roomTypeId: Number(form.value.roomTypeId),
    floor: Number(form.value.floor),
  };

  if (form.value.roomId === null) {
    const nextId =
      rooms.value.length === 0
        ? 1
        : Math.max(...rooms.value.map((room) => room.roomId)) + 1;

    rooms.value.push({
      ...roomData,
      roomId: nextId,
    });

    showMessage("房間新增成功", "success");
  } else {
    const index = rooms.value.findIndex(
      (room) => room.roomId === form.value.roomId,
    );

    if (index !== -1) {
      rooms.value[index] = roomData;
    }

    showMessage("房間修改成功", "success");
  }

  clearForm();
}

function editRoom(room) {
  form.value = { ...room };
  formTitle.value = `修改房間：${room.roomNumber}`;

  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
}

function deleteRoom(id) {
  const room = rooms.value.find(
    (item) => item.roomId === id,
  );

  if (!window.confirm(`確定刪除房間 ${room?.roomNumber} 嗎？`)) {
    return;
  }

  rooms.value = rooms.value.filter(
    (item) => item.roomId !== id,
  );

  if (form.value.roomId === id) {
    clearForm();
  }

  showMessage("房間已刪除", "success");
}

function getStatusClass(status) {
  return {
    available: status === "可入住",
    reserved: status === "已預訂",
    occupied: status === "已入住",
    "checkout-cleaning": status === "退房待清潔",
    cleaning: status === "清潔中",
    maintenance: status === "維修中",
    disabled: status === "停用",
  };
}

onMounted(() => {
  loadRooms();
});
</script>

<template>
  <main class="room-page">
    <header class="page-header">
      <div>
        <h1>房間狀態管理</h1>
        <p>管理實際房號、所屬房型、樓層及目前狀態</p>
      </div>

      <button type="button" class="refresh-button" :disabled="loading" @click="loadRooms">
        {{ loading ? "讀取中…" : "重新整理" }}
      </button>
    </header>

    <div v-if="message" class="message" :class="messageType">
      {{ message }}
    </div>

    <!--
      新增與修改表單暫時隱藏。
      要恢復時，移除 v-if="false"。
    -->
    <section v-if="false" class="admin-card">
      <h2>{{ formTitle }}</h2>

      <form @submit.prevent="saveRoom">
        <div class="form-grid">
          <div class="form-group">
            <label for="roomNumber">房號 *</label>

            <input id="roomNumber" v-model.trim="form.roomNumber" type="text" placeholder="例如：301" required />
          </div>

          <div class="form-group">
            <label for="roomType">房型 *</label>

            <select id="roomType" v-model="form.roomTypeId" required>
              <option value="" disabled>
                請選擇房型
              </option>

              <option v-for="roomType in roomTypes" :key="roomType.roomTypeId" :value="roomType.roomTypeId">
                {{ roomType.typeName }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="floor">樓層 *</label>

            <input id="floor" v-model.number="form.floor" type="number" min="1" required />
          </div>

          <div class="form-group">
            <label for="roomStatus">房間狀態 *</label>

            <select id="roomStatus" v-model="form.roomStatus">
              <option v-for="status in roomStatuses" :key="status" :value="status">
                {{ status }}
              </option>
            </select>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn primary">
            {{ form.roomId === null ? "新增房間" : "儲存修改" }}
          </button>

          <button type="button" class="btn secondary" @click="clearForm">
            清除表單
          </button>
        </div>
      </form>
    </section>

    <section class="admin-card">
      <div class="table-header">
        <h2>房間列表</h2>
        <span>共 {{ rooms.length }} 間</span>
      </div>

      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>房號</th>
              <th>房型</th>
              <th>樓層</th>
              <th>房間狀態</th>

              <!-- 操作欄暫時隱藏 -->
              <th v-if="false">操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="loading">
              <td colspan="5" class="empty">
                房間資料讀取中……
              </td>
            </tr>

            <tr v-for="room in rooms" v-else :key="room.roomId">
              <td>{{ room.roomId }}</td>
              <td>{{ room.roomNumber }}</td>
              <td>{{ getRoomTypeName(room) }}</td>
              <td>{{ room.floor }} 樓</td>

              <td>
                <span class="status" :class="getStatusClass(room.roomStatus)">
                  {{ room.roomStatus }}
                </span>
              </td>

              <!-- 修改及刪除按鈕暫時隱藏 -->
              <td v-if="false" class="actions">
                <button type="button" class="btn edit" @click="editRoom(room)">
                  修改
                </button>

                <button type="button" class="btn delete" @click="deleteRoom(room.roomId)">
                  刪除
                </button>
              </td>
            </tr>

            <tr v-if="!loading && rooms.length === 0">
              <td colspan="5" class="empty">
                SQL 目前沒有房間資料
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</template>

<style scoped>
.room-page {
  padding: 28px;
  color: #243447;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 22px;
  padding: 24px;
  background: white;
  box-shadow: 0 3px 12px rgb(0 0 0 / 6%);
}

.page-header h1 {
  margin: 0 0 8px;
}

.page-header p {
  margin: 0;
  color: #667085;
}

.admin-card {
  margin-bottom: 24px;
  padding: 24px;
  background: white;
  border: 1px solid #e4e7ec;
  border-radius: 12px;
  box-shadow: 0 3px 12px rgb(0 0 0 / 6%);
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

.refresh-button {
  padding: 10px 16px;
  color: white;
  background: #315b7d;
  border: none;
  border-radius: 7px;
  cursor: pointer;
}

.refresh-button:disabled {
  cursor: not-allowed;
  opacity: 0.6;
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
  overflow: hidden;
  border-collapse: collapse;
  border-radius: 8px;
}

th,
td {
  padding: 14px 16px;
  text-align: left;
  border-bottom: 1px solid #e4e7ec;
}

th {
  color: #ffffff;
  font-weight: 700;
  background-color: #4b3c34;
  border-bottom: 2px solid #3b2f29;
}

td {
  color: #344054;
  background-color: #ffffff;
}

tbody tr:hover td {
  background-color: #faf7f2;
}

.empty {
  padding: 35px;
  text-align: center;
  color: #667085;
}

.status {
  display: inline-block;
  min-width: 64px;
  padding: 6px 12px;
  color: #475467;
  text-align: center;
  white-space: nowrap;
  background-color: #f2f4f7;
  border-radius: 999px;
}

/* 可入住：綠色 */
.status.available {
  color: #087443;
  background-color: #e7f8ef;
}

/* 已預訂：紫色 */
.status.reserved {
  color: #6941c6;
  background-color: #f0eaff;
}

/* 已入住：藍色 */
.status.occupied {
  color: #175cd3;
  background-color: #eaf2ff;
}

/* 退房待清潔：橘色 */
.status.checkout-cleaning {
  color: #b54708;
  background-color: #fff0df;
}

/* 清潔中：黃色 */
.status.cleaning {
  color: #9a6700;
  background-color: #fff4ce;
}

/* 維修中：紅色 */
.status.maintenance {
  color: #b42318;
  background-color: #feeceb;
}

/* 停用：灰色 */
.status.disabled {
  color: #475467;
  background-color: #e4e7ec;
}

.available {
  color: #176b3a;
  background: #e9f8ef;
}

.occupied {
  color: #175cd3;
  background: #eaf2ff;
}

.cleaning {
  color: #9a6700;
  background: #fff4ce;
}

.maintenance {
  color: #b42318;
  background: #feeceb;
}

.disabled {
  color: #475467;
  background: #e4e7ec;
}

@media (max-width: 768px) {
  .room-page {
    padding: 16px;
  }

  .page-header {
    align-items: flex-start;
    flex-direction: column;
    gap: 16px;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>