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

// =======================
// JSON 匯入與匯出邏輯
// =======================
const showImportModal = ref(false);
const importMode = ref("file"); // "file" 或 "text"
const importJsonText = ref("");
const importing = ref(false);

function openImportModal() {
  showImportModal.value = true;
  importMode.value = "file";
  importJsonText.value = "";
}

function closeImportModal() {
  showImportModal.value = false;
  importJsonText.value = "";
}

function triggerFileInput() {
  document.getElementById("jsonFileInput").click();
}

async function handleFileUpload(event) {
  const file = event.target.files[0];
  if (!file) return;

  importing.value = true;
  const formData = new FormData();
  formData.append("file", file);

  try {
    const data = await roomTypeApi.importJson(formData, true);
    closeImportModal();
    showMessage(
      `匯入完成！成功 ${data.successCount ?? 0} 筆，失敗 ${data.failureCount ?? 0} 筆`,
      "success"
    );
    await loadRoomTypes();
  } catch (error) {
    console.error("匯入檔案錯誤：", error);
    showMessage(error.message || "匯入失敗", "error");
  } finally {
    importing.value = false;
    event.target.value = ""; // 重設 input
  }
}

async function handleFileDrop(event) {
  const file = event.dataTransfer.files[0];
  if (!file || file.type !== "application/json") {
    alert("請上傳有效的 JSON 檔案");
    return;
  }
  const mockEvent = { target: { files: [file], value: "" } };
  await handleFileUpload(mockEvent);
}

async function submitJsonText() {
  if (!importJsonText.value.trim()) {
    alert("請輸入 JSON 內容");
    return;
  }
  
  importing.value = true;
  try {
    const data = await roomTypeApi.importJson(importJsonText.value, false);
    closeImportModal();
    showMessage(
      `匯入完成！成功 ${data.successCount ?? 0} 筆，失敗 ${data.failureCount ?? 0} 筆`,
      "success"
    );
    await loadRoomTypes();
  } catch (error) {
    console.error("匯入文字錯誤：", error);
    showMessage(error.message || "匯入失敗", "error");
  } finally {
    importing.value = false;
  }
}

function exportJson() {
  window.location.href = "/api/roomtypes/export/json";
}

</script>

<template>
  <main class="room-type-page">
    <header class="page-header" style="display: flex; justify-content: space-between; align-items: center;">
      <div>
        <h1>房型資料管理</h1>
        <p>管理飯店房型、床型、容納人數與每晚價格</p>
      </div>
      <div style="display: flex; gap: 10px;">
        <button type="button" class="btn secondary" @click="openImportModal">
          📥 匯入 JSON
        </button>
        <button type="button" class="btn secondary" @click="exportJson">
          📤 匯出 JSON
        </button>
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

    <!-- 匯入 Modal -->
    <div v-if="showImportModal" class="modal-overlay" @click.self="closeImportModal">
      <div class="modal-content import-modal">
        <div class="modal-header">
          <h2>📥 匯入房型資料 (JSON)</h2>
          <button type="button" class="close-btn" @click="closeImportModal">✖</button>
        </div>

        <div class="modal-body">
          <div class="import-tabs">
            <button type="button" :class="{ active: importMode === 'file' }" @click="importMode = 'file'">
              📄 上傳 JSON 檔案
            </button>
            <button type="button" :class="{ active: importMode === 'text' }" @click="importMode = 'text'">
              📝 貼上 JSON 內容
            </button>
          </div>

          <div v-if="importMode === 'file'" class="import-file-area" @dragover.prevent @drop.prevent="handleFileDrop">
            <input type="file" id="jsonFileInput" accept=".json" @change="handleFileUpload" style="display: none" />
            <div class="drop-zone" @click="triggerFileInput">
              <span class="icon">📄</span>
              <p>點擊此處選取 .json 檔案，或拖放檔案至此</p>
            </div>
          </div>

          <div v-if="importMode === 'text'" class="import-text-area">
            <textarea v-model="importJsonText" placeholder='[\n  {\n    "typeName": "標準雙人房",\n    "pricePerNight": 2000,\n    "availableRooms": 10\n  }\n]' rows="10"></textarea>
          </div>
          
          <div class="import-guide">
            <div class="guide-title">📌 匯入規則：</div>
            <ul>
              <li>支援多筆陣列 <code>[...]</code> 或單筆物件 <code>{...}</code>。</li>
              <li>若 <code>typeName</code> (房型名稱) 已存在，系統將自動<strong>更新</strong>該房型資料。</li>
              <li>若 <code>typeName</code> 不存在，系統將<strong>新增</strong>房型。</li>
              <li><code>availableRooms</code> 為房型預設總數，會同步更新。</li>
            </ul>
          </div>
        </div>

        <div class="modal-footer">
          <button type="button" class="btn secondary" @click="closeImportModal">關閉</button>
          <button v-if="importMode === 'text'" type="button" class="btn primary" :disabled="importing" @click="submitJsonText">
            {{ importing ? "處理中..." : "開始匯入" }}
          </button>
        </div>
      </div>
    </div>
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

/* Modal Styles */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex; justify-content: center; align-items: center;
  z-index: 1000;
}
.modal-content {
  background: white; border-radius: 12px;
  width: 90%; max-width: 600px; max-height: 90vh;
  display: flex; flex-direction: column;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}
.modal-header {
  padding: 16px 24px; border-bottom: 1px solid #e4e7ec;
  display: flex; justify-content: space-between; align-items: center;
}
.modal-header h2 { margin: 0; font-size: 1.25rem; color: #101828; }
.close-btn { background: none; border: none; font-size: 1.5rem; color: #667085; cursor: pointer; }
.modal-body { padding: 24px; overflow-y: auto; }
.modal-footer { padding: 16px 24px; border-top: 1px solid #e4e7ec; display: flex; justify-content: flex-end; gap: 12px; }

/* Import Tabs */
.import-tabs { display: flex; gap: 10px; margin-bottom: 20px; }
.import-tabs button {
  flex: 1; padding: 12px; border: 1px solid #e4e7ec; background: #f9fafb;
  border-radius: 8px; cursor: pointer; font-weight: 600; color: #667085;
}
.import-tabs button.active { background: #315b7d; color: white; border-color: #315b7d; }

/* Drop Zone */
.drop-zone {
  border: 2px dashed #cfd4dc; border-radius: 8px;
  padding: 40px 20px; text-align: center; cursor: pointer;
  background-color: #fcfcfd; transition: all 0.2s;
}
.drop-zone:hover { border-color: #315b7d; background-color: #f0f4f8; }
.drop-zone .icon { font-size: 48px; margin-bottom: 12px; display: block; }
.drop-zone p { color: #667085; font-weight: 500; }

/* Text Area */
.import-text-area textarea {
  width: 100%; padding: 12px; border: 1px solid #cfd4dc;
  border-radius: 8px; font-family: monospace; font-size: 14px;
}

/* Guide */
.import-guide { margin-top: 24px; padding: 16px; background-color: #f9fafb; border-radius: 8px; }
.import-guide .guide-title { font-weight: 600; color: #344054; margin-bottom: 8px; }
.import-guide ul { margin: 0; padding-left: 24px; color: #475467; font-size: 14px; line-height: 1.6; }
.import-guide code { background-color: #f2f4f7; padding: 2px 6px; border-radius: 4px; font-family: monospace; }
</style>
