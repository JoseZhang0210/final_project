<script setup>
import { onMounted, ref , computed } from "vue";
import { roomTypeApi } from "@/api/roomTypeApi";

const roomTypes = ref([]);
const loading = ref(false);

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
    availableRooms: 0,
    roomDescription: "",
  };
}

function clearForm() {
  form.value = createEmptyForm();
  formTitle.value = "新增房型";
}

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
}

// 2. 新增與修改房型 (POST / PUT /api/roomtypes)
async function saveRoomType() {
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

  const isEdit = form.value.roomTypeId !== null;
  const payload = {
    roomTypeId: form.value.roomTypeId,
    typeName: form.value.typeName,
    bedType: form.value.bedType,
    capacity: Number(form.value.capacity),
    pricePerNight: Number(form.value.pricePerNight),
    availableRooms: Number(form.value.availableRooms),
    roomDescription: form.value.roomDescription || "",
  };

  try {
    if (isEdit) {
      await roomTypeApi.updateRoomType(form.value.roomTypeId, payload);
      showMessage("房型修改成功", "success");
    } else {
      await roomTypeApi.createRoomType(payload);
      showMessage("房型新增成功", "success");
    }
    
    clearForm();
    await loadRoomTypes();
  } catch (error) {
    console.error("saveRoomType Error:", error);
    showMessage(error.message || "無法連線至房型 API", "error");
  }
}

// 點擊修改：將資料填入表單
function editRoomType(roomType) {
  form.value = {
    roomTypeId: roomType.roomTypeId ?? roomType.room_type_id,
    typeName: roomType.typeName ?? roomType.type_name ?? "",
    bedType: roomType.bedType ?? roomType.bed_type ?? "",
    capacity: roomType.capacity ?? 1,
    pricePerNight: roomType.pricePerNight ?? roomType.price_per_night ?? 0,
    availableRooms: roomType.availableRooms ?? roomType.available_rooms ?? 0,
    roomDescription:
      roomType.roomDescription ?? roomType.room_description ?? "",
  };

  formTitle.value = `修改房型：${form.value.typeName}`;

  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
}

// 3. 刪除房型 (DELETE /api/roomtypes/{id})
async function deleteRoomType(id) {
  const roomType = roomTypes.value.find(
    (room) => (room.roomTypeId ?? room.room_type_id) === id,
  );
  const typeName = roomType
    ? (roomType.typeName ?? roomType.type_name)
    : `ID ${id}`;

  if (!window.confirm(`確定要刪除「${typeName}」嗎？`)) {
    return;
  }

  try {
    await roomTypeApi.deleteRoomType(id);
    showMessage("房型已刪除", "success");
    if (form.value.roomTypeId === id) {
      clearForm();
    }
    await loadRoomTypes();
  } catch (error) {
    console.error("deleteRoomType Error:", error);
    showMessage(error.message || "無法連線至房型 API", "error");
  }
}

function getAvailableRoomsClass(count) {
  if (count > 5) return "available";
  if (count > 0) return "checkout-cleaning";
  return "maintenance";
}

function formatPrice(price) {
  return new Intl.NumberFormat("zh-TW", {
    style: "currency",
    currency: "TWD",
    maximumFractionDigits: 0,
  }).format(price || 0);
}

async function loadRoomTypes() {
  currentPage.value = 1;
  loading.value = true;
  message.value = "";

  try {
    const data = await roomTypeApi.getAllRoomTypes();
    roomTypes.value = Array.isArray(data) ? data : data.content || [];
    console.log("SQL room_type 資料：", roomTypes.value);
  } catch (error) {
    console.error("讀取房型錯誤：", error);
    showMessage(error.message || "無法連線至房型 API", "error");
  } finally {
    loading.value = false;
  }
}

onMounted(() => {
  loadRoomTypes();
});

const currentPage = ref(1);
const itemsPerPage = 20;
const totalPages = computed(() => Math.ceil(roomTypes.value.length / itemsPerPage));
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return roomTypes.value.slice(start, start + itemsPerPage);
});
function nextPage() { if (currentPage.value < totalPages.value) currentPage.value++; }
function prevPage() { if (currentPage.value > 1) currentPage.value--; }

</script>

<template>
  <main class="room-type-page">
    <header class="page-header">
      <div>
        <h1>房型資料管理</h1>
        <p>管理飯店房型、床型、容納人數與每晚價格</p>
      </div>
    </header>

    <div v-if="message" class="message" :class="messageType">
      {{ message }}
    </div>

    <!--v-if="false" 隱藏-->
    <section v-if="false" class="admin-card">
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

          <div class="form-group">
            <label for="availableRooms">剩餘數量 (Available Rooms) *</label>
            <input
              id="availableRooms"
              v-model.number="form.availableRooms"
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
              <th>預設房間數</th>
              <th>今日可用數</th>
              <th>房型說明</th>
              <!--v-if="false" 隱藏-->
              <th v-if="false">操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="loading">
              <td colspan="6" class="empty">房型資料讀取中……</td>
            </tr>
            <tr v-for="roomType in paginatedData" :key="roomType.roomTypeId">
              <td>{{ roomType.roomTypeId }}</td>
              <td>{{ roomType.typeName }}</td>
              <td>{{ roomType.bedType }}</td>
              <td>{{ roomType.capacity }} 人</td>
              <td>{{ formatPrice(roomType.pricePerNight) }}</td>
              <td>{{ roomType.availableRooms ?? 0 }} 間</td>
              <td><span class="status" :class="getAvailableRoomsClass(roomType.todayAvailableRooms ?? 0)">{{ roomType.todayAvailableRooms ?? 0 }} 間</span></td>
              <td>{{ roomType.roomDescription || "—" }}</td>
              <!--v-if="false" 隱藏-->
              <td v-if="false" class="action-cell">
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

            <tr v-if="!loading && roomTypes.length === 0">
              <td colspan="6" class="empty">目前沒有房型資料</td>
            </tr>
          </tbody>
        </table>

      <div class="pagination-container" v-if="totalPages > 1">
        <button @click="prevPage" :disabled="currentPage === 1" class="page-btn">◀ 上一頁</button>
        <span class="page-info">第 {{ currentPage }} 頁 / 共 {{ totalPages }} 頁</span>
        <button @click="nextPage" :disabled="currentPage === totalPages" class="page-btn">下一頁 ▶</button>
      </div>
  

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

.status {
  display: inline-block;
  min-width: 64px;
  padding: 6px 12px;
  color: #475467;
  text-align: center;
  white-space: nowrap;
  background-color: #f2f4f7;
  border-radius: 999px;
  font-weight: 500;
  font-size: 0.9em;
}
.status.available { color: #087443; background-color: #e7f8ef; }
.status.checkout-cleaning { color: #b54708; background-color: #fff0df; }
.status.maintenance { color: #b42318; background-color: #feeceb; }

.pagination-container { display: flex; justify-content: center; align-items: center; margin-top: 20px; gap: 15px; } .page-btn { padding: 8px 16px; background-color: #3b82f6; color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: 500; transition: background-color 0.2s; } .page-btn:hover:not(:disabled) { background-color: #2563eb; } .page-btn:disabled { background-color: #d1d5db; cursor: not-allowed; } .page-info { font-weight: 500; color: #374151; }
</style>
