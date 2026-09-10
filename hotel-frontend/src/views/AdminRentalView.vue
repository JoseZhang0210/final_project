<script setup>
// 移植 ab025d7 的管理表單、列表與編輯刪除流程，沿用目前既有登入。
import { computed, onMounted, ref } from 'vue'; // 使用既有響應式狀態。
import { getStoredToken, getRentals, getVenues, getMembers, createAdminRental, updateRental, deleteRental, getApiErrorMessage } from '../api/venueRentalApi'; // 僅使用既有管理 API。
const token = ref(getStoredToken()); // 不建立新的登入流程。
const rentals = ref([]); // 保存全部租借，不查詢會員專用端點。
const venues = ref([]); // 場地名稱由既有場地 API 取得。
const memberNames = ref({}); // 依會員 ID 保存會員姓名，不修改會員模組。
const loading = ref(false); // 避免重複提交管理操作。
const message = ref(''); // 顯示操作結果。
const errorMessage = ref(''); // 保留後端權限及付款保護錯誤。
const isLoggedIn = computed(() => Boolean(token.value)); // 僅控制顯示，後端負責授權。
const editMode = ref(false); // 編輯既有租借。
const createMode = ref(false); // 管理員新增租借。
const form = ref({}); // 編輯時複製完整租借欄位。
const rentalStatuses = ['PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED']; // 沿用既有租借狀態。
const venueName = id => venues.value.find(v => Number(v.venueId) === Number(id))?.venueName || `場地 ${id}`; // 缺少場地時仍保留原編號。
/*
 * 後台列表同時顯示會員姓名與會員 ID。
 */
function memberName(id) {

  const memberId = Number(id);

  if (!Number.isInteger(memberId) || memberId <= 0) {
    return '會員資料未知';
  }

  const name = memberNames.value[memberId];

  if (!name) {
    return `會員 ID ${memberId}`;
  }

  return `${name}（ID ${memberId}）`;
}

/*
 * 新增與編輯表單依會員 ID 顯示姓名。
 */
function formMemberName(id) {

  const memberId = Number(id);

  if (!Number.isInteger(memberId) || memberId <= 0) {
    return '';
  }

  return memberNames.value[memberId]
    || '查無此會員';
}
onMounted(refreshRentals); // 管理員進頁即載入全部租借。
async function loadRentalData() { // 沿用舊版管理資料重新載入入口。
  // 租借、場地與會員資料並行載入。
  const [all, places, members] = await Promise.all([
    getRentals(token.value),
    getVenues(token.value),
    getMembers(token.value),
  ]);

  rentals.value = all ?? [];
  venues.value = places ?? [];

  // 建立 memberId -> 姓名對照表。
  memberNames.value = Object.fromEntries(
    (members ?? [])
      .filter(member =>
        Number.isInteger(Number(member.memberId)),
      )
      .map(member => [
        Number(member.memberId),
        String(member.name || '').trim()
          || '未設定姓名',
      ]),
  );
} // 結束管理資料載入。
async function refreshRentals() { // 恢復重新整理功能。
  if (!token.value) { errorMessage.value = '請先使用網站登入'; return; } // 不在管理畫面重做登入。
  loading.value = true; errorMessage.value = ''; // 鎖定操作並清除舊錯誤。
  try { await loadRentalData(); } catch (error) { errorMessage.value = getApiErrorMessage(error); } // 顯示實際管理 API 錯誤。
  finally { loading.value = false; } // 無論成功失敗都恢復按鈕。
} // 結束重新整理。
function startCreate() {

  // 新增與編輯模式不可同時存在。
  editMode.value = false;
  createMode.value = true;

  // 新增時只準備管理員需要輸入的欄位。
  form.value = {
    memberId: '',
    venueId: '',
    eventName: '',
    rentalDate: '',
    guestCount: '',
  };

  message.value = '目前正在新增租借';
  errorMessage.value = '';

  window.scrollTo({
    top: 0,
    behavior: 'smooth',
  });
}
async function handleCreate() {

  if (!createMode.value || loading.value) {
    return;
  }

  const payload = {
    memberId: Number(form.value.memberId),
    venueId: Number(form.value.venueId),
    eventName: String(form.value.eventName || '').trim(),
    rentalDate: form.value.rentalDate,
    guestCount: Number(form.value.guestCount),
  };

  if (!Number.isInteger(payload.memberId)
      || payload.memberId <= 0) {
    errorMessage.value = '請輸入正確的會員 ID';
    return;
  }

  if (!Number.isInteger(payload.venueId)
      || payload.venueId <= 0) {
    errorMessage.value = '請選擇場地';
    return;
  }

  if (!payload.eventName
      || payload.eventName.length > 50
      || !payload.rentalDate
      || !Number.isInteger(payload.guestCount)
      || payload.guestCount <= 0) {
    errorMessage.value = '請填寫活動名稱、日期與正整數人數';
    return;
  }

  loading.value = true;
  message.value = '';
  errorMessage.value = '';

  try {
    await createAdminRental(
      token.value,
      payload,
    );

    resetForm();
    await loadRentalData();

    message.value = '租借新增成功';

  } catch (error) {
    errorMessage.value = getApiErrorMessage(error);

  } finally {
    loading.value = false;
  }
}
async function handleSubmit() { // 恢復舊版儲存編輯流程。
  if (!editMode.value || loading.value) return; // 只允許編輯既有租借。
  loading.value = true; message.value = ''; errorMessage.value = ''; // 清除上一筆操作結果。
  try { // 由前端基本驗證與後端完整驗證共同保護資料。
    const payload = { ...form.value, eventName: form.value.eventName.trim(), guestCount: Number(form.value.guestCount) }; // 保留原有會員、場地及付款關聯。
    if (!payload.eventName || payload.eventName.length > 50 || !payload.rentalDate || !Number.isInteger(payload.guestCount) || payload.guestCount <= 0) throw new Error('請填寫活動名稱、日期與正整數人數'); // 延續舊版必要欄位檢查。
    await updateRental(token.value, payload.rentalId, payload); // 呼叫既有管理更新 API。
    resetForm(); await loadRentalData(); message.value = '租借修改成功'; // 成功後清空編輯並重載資料。
  } catch (error) { errorMessage.value = getApiErrorMessage(error); } // 保留碰撞或權限拒絕訊息。
  finally { loading.value = false; } // 結束管理寫入狀態。
} // 結束儲存修改。
function startEdit(rental) { // 移植舊版選取資料並捲至表單的操作。
  // 編輯既有資料時關閉新增模式。
  createMode.value = false;
  editMode.value = true;
  const aliases = { '待確認': 'PENDING', '待付款': 'PENDING', '已確認': 'CONFIRMED', '已取消': 'CANCELLED', '已完成': 'COMPLETED' }; // 相容 seed 與歷史中文狀態。
  form.value = { ...rental, rentalDate: String(rental.rentalDate).substring(0, 16), rentalStatus: aliases[rental.rentalStatus] || rental.rentalStatus }; // 複製資料，避免尚未儲存就改動表格。
  message.value = '目前正在編輯租借 ID ' + rental.rentalId; errorMessage.value = ''; // 提示目前編輯對象。
  window.scrollTo({ top: 0, behavior: 'smooth' }); // 沿用舊版操作位置。
} // 結束開始編輯。
async function handleDelete(rental) { // 保留舊版刪除確認。
  if (loading.value || !window.confirm(`確定要刪除租借「${rental.eventName}」（ID ${rental.rentalId}）嗎？`)) return; // 取消時不送出請求。
  loading.value = true; message.value = ''; errorMessage.value = ''; // 暫停其他管理操作。
  try { // 已進入付款流程的租借仍由後端拒絕刪除。
    await deleteRental(token.value, rental.rentalId); // 使用既有管理刪除 API。
    if (form.value.rentalId === rental.rentalId) resetForm(); // 清除已刪除項目的編輯表單。
    await loadRentalData(); message.value = '租借刪除成功'; // 更新列表與結果提示。
  } catch (error) { errorMessage.value = getApiErrorMessage(error); } // 顯示後端付款保護或權限錯誤。
  finally { loading.value = false; } // 恢復管理操作。
} // 結束刪除流程。
function resetForm() {

  // 離開新增與編輯模式。
  editMode.value = false;
  createMode.value = false;

  // 清空目前表單中的租借資料。
  form.value = {};

  // 取消操作後移除目前狀態提示。
  message.value = '';
}
function formatDateTime(value) { return value ? String(value).replace('T', ' ') : ''; } // 沿用舊版表格時間顯示。

// ------------------------------------------------------------
// 後台租借狀態顯示繁體中文；送往後端的值仍維持原英文狀態。
// ------------------------------------------------------------
function rentalStatusLabel(status) {
  const labels = {
    PENDING: "待確認",
    CONFIRMED: "已確認",
    CANCELLED: "已取消",
    COMPLETED: "已完成",
  };

  return labels[status] || status || "-";
}
</script>

<!-- 以下表單、列表及 scoped 樣式移植自 ab025d7，僅調整現行登入與唯讀關聯欄位。 -->
<template>
  <main class="page">

    <section class="hero">
      <div>
        <p class="eyebrow">
          HOTEL MANAGEMENT
        </p>

        <h1>租借管理</h1>

        <p>
          查看與管理所有會員的場地租借
        </p>
      </div>

      <div
        v-if="isLoggedIn"
        class="connection-badge"
      >
        場地租借管理
      </div>
    </section>

    <!-- 沿用網站登入，僅顯示管理結果與重新整理，不重建登入功能。 -->
    <section class="card">
      <div class="actions">
        <button
          type="button"
          :disabled="loading"
          @click="refreshRentals"
        >
          重新整理
        </button>

        <button
          v-if="isLoggedIn"
          type="button"
          class="secondary"
          :disabled="loading"
          @click="startCreate"
        >
          新增資料
        </button>
      </div>
      <p v-if="message" class="success">{{ message }}</p>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    </section>
    <!-- CRUD FORM -->
    <section
      v-if="isLoggedIn && (editMode || createMode)"
      class="card"
    >

      <div class="form-header">
        <div>
          <h2>
          {{ createMode ? '新增租借' : '修改租借' }}
        </h2>

          <p class="description">
            {{ createMode ? '替既有會員新增場地租借' : '修改活動、日期、人數與狀態；原會員、場地與付款關聯保留' }}
          </p>
        </div>

        <span
          v-if="editMode"
          class="edit-badge"
        >
          EDIT MODE
        </span>
      </div>

      <div class="rental-form">

        <label v-if="editMode">
          <span>租借 ID</span>
          <input
            v-model="form.rentalId"
            type="number"
            :disabled="editMode"
          >
        </label>

        <label>
  <!-- 後台編輯租借時直接選擇場地，不要求管理者記住場地 ID。 -->
  <span>場地</span>

  <select
    v-model="form.venueId"
    :disabled="loading"
  >
    <!-- 空值用於提示尚未選擇場地。 -->
    <option value="">
      請選擇場地
    </option>

    <!-- 場地資料沿用既有 getVenues() API，不新增其他資料來源。 -->
    <option
      v-for="venue in venues"
      :key="venue.venueId"
      :value="venue.venueId"
    >
      {{ venue.venueName }}
    </option>
  </select>
</label>

        <label>
          <span>會員 ID</span>
          <input
            v-model="form.memberId"
            type="number"
            min="1"
            :readonly="editMode"
            :disabled="loading"
          >

          <span
            v-if="form.memberId"
            class="description"
          >
            會員姓名：{{ formMemberName(form.memberId) }}
          </span>
        </label>

        <label>
          <span>活動名稱</span>
          <input
            v-model="form.eventName" :disabled="loading"
            type="text"
            maxlength="50"
          >
        </label>

        <label>
          <span>租借日期</span>
          <input
            v-model="form.rentalDate" :disabled="loading"
            type="datetime-local"
          >
        </label>

        <label>
          <span>人數</span>
          <input
            v-model="form.guestCount" :disabled="loading"
            type="number"
            min="1"
          >
        </label>

        <label v-if="editMode">
          <span>付款 ID</span>
          <input
            v-model="form.paymentId" readonly
            type="number"
          >
        </label>

        <label v-if="editMode">
          <span>狀態</span>

          <select v-model="form.rentalStatus" :disabled="loading">
            <option
              v-for="status in rentalStatuses"
              :key="status"
              :value="status"
            >
              {{ rentalStatusLabel(status) }}
            </option>
          </select>
        </label>

      </div>

      <div class="actions form-actions">

        <button
          v-if="createMode"
          type="button"
          :disabled="loading"
          @click="handleCreate"
        >
          新增租借
        </button>

        <button
          v-if="editMode"
          type="button"
          :disabled="loading"
          @click="handleSubmit"
        >
          儲存修改
        </button>

        <button
          v-if="createMode || editMode"
          type="button"
          class="secondary"
          :disabled="loading"
          @click="resetForm"
        >
          取消
        </button>

      </div>

    </section>

    <!-- TABLE -->
    <section class="card">

      <div class="table-header">

        <div>
          <h2>Rental 資料</h2>

          <p class="description">
            {{
              isLoggedIn
                ? '共 ' + rentals.length + ' 筆'
                : '請先登入'
            }}
          </p>
        </div>

        <span
          v-if="isLoggedIn"
          class="status-ok"
        >
          API CONNECTED
        </span>

      </div>

      <div
        v-if="isLoggedIn && rentals.length"
        class="table-wrap"
      >

        <table>

          <thead>
            <tr>
              <th>租借 ID</th>
              <th>場地</th>
              <th>會員</th>
              <th>活動</th>
              <th>日期</th>
              <th>人數</th>
              <th>paymentId</th>
              <th>狀態</th>
              <th>操作</th>
            </tr>
          </thead>

          <tbody>

            <tr
              v-for="rental in rentals"
              :key="rental.rentalId"
            >

              <td>{{ rental.rentalId }}</td>
              <td>{{ venueName(rental.venueId) }}</td>
              <td>{{ memberName(rental.memberId) }}</td>
              <td>{{ rental.eventName }}</td>
              <td>{{ formatDateTime(rental.rentalDate) }}</td>
              <td>{{ rental.guestCount }}</td>
              <td>{{ rental.paymentId }}</td>

              <td>
                <span class="status" :class="`status-${String(rental.rentalStatus || '').toLowerCase()}`"> <!-- 狀態以文字及顏色共同辨識。 -->
                  {{ rentalStatusLabel(rental.rentalStatus) }}
                </span>
              </td>

              <td>
                <div class="actions">

                  <button
                    type="button"
                    class="small secondary"
                    :disabled="loading" @click="startEdit(rental)"
                  >
                    編輯
                  </button>

                  <button
                    type="button"
                    class="small danger"
                    :disabled="loading" @click="handleDelete(rental)"
                  >
                    刪除
                  </button>

                </div>
              </td>

            </tr>

          </tbody>

        </table>

      </div>

      <div
        v-else-if="isLoggedIn"
        class="empty"
      >
        目前沒有 Rental 資料
      </div>

      <div
        v-else
        class="empty"
      >
        請先使用網站登入
      </div>

    </section>

  </main>
</template>

<style scoped>
.page {
  width: min(1200px, calc(100% - 40px));
  margin: 0 auto;
  padding: 42px 0 70px;
}

.hero,
.session-panel,
.form-header,
.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.hero {
  margin-bottom: 24px;
  align-items: flex-end;
}

.eyebrow {
  margin: 0 0 8px;
  color: #7c3aed;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 0.14em;
}

h1 {
  margin: 0;
  color: #0f172a;
  font-size: 38px;
}

h2 {
  margin: 0 0 5px;
  color: #0f172a;
}

.description,
.hero p:last-child {
  margin: 0;
  color: #64748b;
}

.card {
  margin-bottom: 20px;
  padding: 24px;
  border: 1px solid #e6e0d7; /* 沿用星澄飯店卡片的暖灰邊框。 */
  border-radius: 16px;
  background: white;
  box-shadow: 0 8px 24px rgba(62, 48, 35, 0.08); /* 使用場地管理相同的柔和棕色陰影。 */
}

.login-grid {
  margin-top: 20px;
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  align-items: end;
  gap: 14px;
}

.rental-form {
  margin-top: 20px;
  display: grid;
  grid-template-columns:
    repeat(4, minmax(0, 1fr));
  gap: 14px;
}

label {
  display: grid;
  gap: 7px;
  color: #5f5145; /* 表單標籤改用飯店深棕色。 */
  font-size: 14px;
  font-weight: 700;
}

input,
select {
  width: 100%;
  min-height: 42px;
  box-sizing: border-box;
  padding: 0 12px;
  border: 1px solid #cfc5b8; /* 輸入框邊線與 Venue 管理一致。 */
  border-radius: 9px;
  background: white;
  color: #0f172a;
}

input:disabled {
  background: #f1f5f9;
}

button {
  min-height: 42px;
  padding: 0 18px;
  border: 0;
  border-radius: 9px;
  background: #b58a46; /* 主要管理操作使用星澄飯店金色。 */
  color: white;
  font-weight: 800;
  cursor: pointer;
}

button.secondary {
  background: #6c757d; /* 次要操作使用中性灰色。 */
}

button.danger {
  background: #a33b3b; /* 刪除操作保留清楚的危險色。 */
}

button.small {
  min-height: 34px;
  padding: 0 12px;
  font-size: 13px;
}

.actions,
.authority-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.session-panel {
  margin-top: 20px;
}

.form-actions {
  margin-top: 18px;
}

.authority-list {
  margin-top: 8px;
}

.authority {
  padding: 4px 8px;
  border-radius: 999px;
  background: #ede9fe;
  color: #5b21b6;
  font-size: 12px;
  font-weight: 800;
}

.connection-badge,
.status-ok {
  padding: 7px 10px;
  border-radius: 999px;
  background: #dcfce7;
  color: #166534;
  font-size: 12px;
  font-weight: 800;
}

.edit-badge {
  padding: 7px 10px;
  border-radius: 999px;
  background: #fef3c7;
  color: #92400e;
  font-size: 12px;
  font-weight: 800;
}

.success {
  margin-bottom: 0;
  color: #166534;
  font-weight: 700;
}

.error {
  margin-bottom: 0;
  color: #b91c1c;
  font-weight: 700;
}

.table-wrap {
  margin-top: 18px;
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 13px 12px;
  border-bottom: 1px solid #e2e8f0;
  color: #0f172a;
  text-align: left;
  white-space: nowrap;
}

th {
  color: #475569;
  font-size: 13px;
}

.status {
  display: inline-block;
  padding: 5px 9px;
  border-radius: 999px;
  background: #f3eee7; /* 未辨識狀態使用中性暖灰底。 */
  color: #5f5145; /* 未辨識狀態仍保持清楚對比。 */
  font-size: 12px;
  font-weight: 700;
}

/* 待確認狀態使用金棕色，對應尚待管理處理。 */
.status-pending,
.status-待確認,
.status-待付款 {
  background: #fff3d8;
  color: #95691f;
}

/* 已確認狀態使用綠色，表示租借已核准。 */
.status-confirmed,
.status-已確認 {
  background: #e5f6eb;
  color: #257641;
}

/* 已取消狀態使用紅色，與危險操作語意一致。 */
.status-cancelled,
.status-已取消 {
  background: #fde9e7;
  color: #a33b3b;
}

/* 已完成狀態使用沉穩棕色，表示歷史紀錄。 */
.status-completed,
.status-已完成 {
  background: #eee7de;
  color: #5f5145;
}

.empty {
  padding: 32px 5px 10px;
  color: #64748b;
  text-align: center;
}

@media (max-width: 950px) {
  .rental-form {
    grid-template-columns:
      repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 760px) {
  .hero,
  .session-panel,
  .form-header {
    align-items: stretch;
    flex-direction: column;
  }

  .login-grid,
  .rental-form {
    grid-template-columns: 1fr;
  }
}

/* ===== Venue Rental 本機後台可讀性修正 ===== */

/* Hero 使用較深遮罩，避免背景照片造成文字無法辨識。 */
.hero {
  position: relative;
  overflow: hidden;
}

/* 在既有 Hero 背景上增加半透明深色遮罩。 */
.hero::before {
  content: "";
  position: absolute;
  inset: 0;
  background: rgba(20, 16, 12, 0.58);
  pointer-events: none;
  z-index: 0;
}

/* Hero 內容必須位於遮罩上方。 */
.hero > * {
  position: relative;
  z-index: 1;
}

/* Hero 上方英文眉題使用飯店金色。 */
.hero .eyebrow {
  color: #d5b06f !important;
  opacity: 1 !important;
}

/* Hero 主標題改為清楚的白色。 */
.hero h1 {
  color: #ffffff !important;
  opacity: 1 !important;
  text-shadow: 0 2px 5px rgba(0, 0, 0, 0.45);
}

/* Hero 說明文字使用高對比淺色。 */
.hero p:not(.eyebrow) {
  color: #f2ede5 !important;
  opacity: 1 !important;
}

/* 表格限制在目前後台內容區域內，過寬時允許水平捲動。 */
.table-wrap {
  width: 100%;
  max-width: 100%;
  min-width: 0;
  overflow-x: auto;
}

/* Rental 欄位較多，因此保留合理最小寬度避免欄位全部擠壓。 */
.table-wrap table {
  width: 100%;
  min-width: 900px;
  border-collapse: collapse;
}

/* 後台 Rental 表頭統一深咖啡背景。 */
.table-wrap thead,
.table-wrap th {
  background: #4a3b2a !important;
}

/* 表頭必須使用白字，避免深底深字。 */
.table-wrap th {
  color: #ffffff !important;
  opacity: 1 !important;
  white-space: nowrap;
}

/* 一般資料列使用清楚的深色文字。 */
.table-wrap td {
  color: #2f2a24 !important;
  opacity: 1 !important;
}

/* 表單 input 與 select 強制使用白底深字。 */
input,
select {
  color: #2f2a24 !important;
  background: #ffffff !important;
  opacity: 1 !important;
}

/* 原生下拉選單展開後的項目同樣保持高對比。 */
select option {
  color: #2f2a24 !important;
  background: #ffffff !important;
}

/* 小畫面仍保持完整寬度，不讓元件撐破 AdminLayout。 */
.page {
  width: 100%;
  max-width: 1120px;
  min-width: 0;
  box-sizing: border-box;
  margin-left: auto;
  margin-right: auto;
}
</style>
