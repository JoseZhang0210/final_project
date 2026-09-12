<script setup>
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";

import {
  createVenue,
  deleteVenue,
  getApiErrorMessage,
  getStoredToken,
  getVenues,
  updateVenue,
  uploadVenueImage,
} from "../api/venueRentalApi";

const router = useRouter();

const token = ref("");
const venues = ref([]);

/* ===== 既有場地快速選擇 ===== */
/* 儲存管理員從下拉選單選到的既有場地 ID。 */
const selectedVenueId = ref("");
const loading = ref(false);
const editMode = ref(false);

/*
 * 隱藏的檔案 input。
 * 點「選擇本機圖片並導入」時才開啟 Windows 選檔視窗。
 */
const venueImageFileInput = ref(null);

const message = ref("");
const errorMessage = ref("");

const form = ref({
  imageUrl: "", // 單一圖片網址可留空，不產生假照片。
  venueId: "",
  venueName: "",
  capacity: "",
  pricePerDay: "",
  venueStatus: "AVAILABLE",
});

const venueStatuses = [
  "AVAILABLE",
  "MAINTENANCE",
  "DISABLED",
];

/* 四個固定宴會廳各自的核定容量，管理畫面不可提高此上限。 */
const fixedVenueCapacityLimits = Object.freeze({
  1: 50,
  2: 100,
  3: 200,
  4: 300,
});

/* 依目前表單的場地 ID 取得固定上限；自行新增的其他場地不套用固定值。 */
const fixedCapacityLimit = computed(() =>
  fixedVenueCapacityLimits[Number(form.value.venueId)] ?? null,
);

/* 超額時保留輸入值顯示錯誤，並停用儲存按鈕。 */
const fixedCapacityExceeded = computed(() => {
  const capacity = Number(form.value.capacity);

  return fixedCapacityLimit.value !== null &&
    Number.isFinite(capacity) &&
    capacity > fixedCapacityLimit.value;
});

/* 錯誤訊息指出目前場地與真正上限。 */
const fixedCapacityWarning = computed(() => {
  if (!fixedCapacityExceeded.value) {
    return "";
  }

  const venueName = form.value.venueName.trim() ||
    `場地 ID ${form.value.venueId}`;

  return `場地「${venueName}」容量不可超過 ${fixedCapacityLimit.value} 人`;
});

onMounted(async () => {
  token.value = getStoredToken();

  if (!token.value) {
    router.push("/login");
    return;
  }

  await loadVenues();
});

async function loadVenues() {
  loading.value = true;
  errorMessage.value = "";

  try {
    venues.value =
      (await getVenues(token.value)) ?? [];
  } catch (error) {
    errorMessage.value =
      getApiErrorMessage(error);
  } finally {
    loading.value = false;
  }
}


/*
 * 開啟 Windows 本機圖片選擇視窗。
 */
function openLocalImagePicker() {
  message.value = "";
  errorMessage.value = "";

  if (
    !editMode.value ||
    !form.value.venueId
  ) {
    errorMessage.value =
      "請先選擇既有場地，再導入圖片。";
    return;
  }

  venueImageFileInput.value?.click();
}

/*
 * 使用者選完檔案後立即上傳，
 * 不需要再按第二次儲存。
 */
async function handleLocalImageSelected(event) {
  const file =
    event.target.files?.[0];

  if (!file) {
    return;
  }

  /*
   * 記住使用者選圖當下的頁面位置。
   * 圖片導入完成後恢復相同位置，
   * 避免畫面突然跳到頁面上方。
   */
  const scrollPosition = window.scrollY;

  const allowedTypes = [
    "image/jpeg",
    "image/png",
    "image/webp",
  ];

  if (!allowedTypes.includes(
      file.type)) {

    errorMessage.value =
      "只支援 JPG、PNG、WEBP 圖片";

    event.target.value = "";
    return;
  }

  if (
    file.size >
    10 * 1024 * 1024
  ) {

    errorMessage.value =
      "圖片不可超過 10 MB";

    event.target.value = "";
    return;
  }

  loading.value = true;
  message.value = "";
  errorMessage.value = "";

  try {

    const result =
      await uploadVenueImage(
        token.value,
        Number(form.value.venueId),
        file,
      );

    /*
     * 成功後直接使用後端產生的本機 URL。
     */
    form.value.imageUrl =
      result.imageUrl;

    message.value =
      "場地主圖已從本機導入成功。";

    await loadVenues();

  } catch (error) {

    errorMessage.value =
      getApiErrorMessage(error);

  } finally {

    /*
     * 清空 input，
     * 讓同一張檔案之後仍可以重新選取。
     */
    event.target.value = "";

    loading.value = false;

    /*
     * Vue 更新圖片與場地資料後，
     * 強制回到使用者選圖前的位置。
     */
    requestAnimationFrame(() => {

      const previousScrollBehavior =
        document.documentElement.style.scrollBehavior;

      document.documentElement.style.scrollBehavior =
        "auto";

      window.scrollTo(
        0,
        scrollPosition,
      );

      document.documentElement.style.scrollBehavior =
        previousScrollBehavior;
    });

  }
}
async function handleSubmit() {
  /*
   * 先記住本次是修改還是新增，
   * resetForm 後仍可顯示正確成功訊息。
   */
  const submitWasEditMode = editMode.value;
  message.value = "";
  errorMessage.value = "";

  const venueId = Number(form.value.venueId);
  const capacity = Number(form.value.capacity);
  const pricePerDay = Number(
    form.value.pricePerDay,
  );

  if (
    !Number.isInteger(venueId) ||
    venueId <= 0
  ) {
    errorMessage.value =
      "場地 ID 必須是大於 0 的整數";
    return;
  }

  if (!form.value.venueName.trim()) {
    errorMessage.value =
      "請輸入場地名稱";
    return;
  }

  if (
    !Number.isInteger(capacity) ||
    capacity <= 0
  ) {
    errorMessage.value =
      "場地容量必須是大於 0 的整數";
    return;
  }

  if (fixedCapacityExceeded.value) {
    errorMessage.value = fixedCapacityWarning.value; // 點擊儲存時再次阻擋超額容量。
    return;
  }

  if (
    !Number.isInteger(pricePerDay) ||
    pricePerDay < 0
  ) {
    errorMessage.value =
      "每日價格必須是 0 以上整數";
    return;
  }

  const payload = {
    imageUrl: form.value.imageUrl?.trim() || null, // 空字串以空值交給後端。
    venueId,
    venueName:
      form.value.venueName.trim(),
    capacity,
    pricePerDay,
    venueStatus:
      form.value.venueStatus,
  };

  loading.value = true;

  try {
    if (editMode.value) {
      await updateVenue(
        token.value,
        venueId,
        payload,
      );
      message.value = "場地修改成功";
    } else {
      await createVenue(
        token.value,
        payload,
      );
      message.value = "場地新增成功";
    }

    resetForm();
    await loadVenues();

    /*
     * 必須放在 resetForm 與重新讀取場地之後，
     * 確保成功提示不會被清掉。
     */
    message.value = submitWasEditMode
      ? "場地修改成功"
      : "場地新增成功";
  } catch (error) {
    errorMessage.value =
      getApiErrorMessage(error);
  } finally {
    loading.value = false;
  }
}

function startEdit(venue, shouldScroll = true) {
  /* 表格點修改時，同步顯示目前選擇的既有場地。 */
  selectedVenueId.value = String(venue.venueId);
  editMode.value = true;

  form.value = {
    imageUrl: venue.imageUrl || "", // 編輯時保留既有圖片網址。
    venueId: venue.venueId,
    venueName: venue.venueName,
    capacity: venue.capacity,
    pricePerDay: venue.pricePerDay,
    venueStatus:
      normalizeStatus(venue.venueStatus),
  };

  if (shouldScroll) {
    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  }
}

async function handleDelete(venue) {
  if (
    !window.confirm(
      `確定刪除場地「${venue.venueName}」嗎？`,
    )
  ) {
    return;
  }

  loading.value = true;
  message.value = "";
  errorMessage.value = "";

  try {
    await deleteVenue(
      token.value,
      venue.venueId,
    );

    message.value = "場地刪除成功";
    await loadVenues();
  } catch (error) {
    errorMessage.value =
      getApiErrorMessage(error);
  } finally {
    loading.value = false;
  }
}

/* ============================================================
 * 既有場地快速選擇
 * ============================================================ */

/* 從目前 API 已載入的 venues 找到場地，再交給既有 startEdit()。 */
function selectExistingVenue() {
  const venueId = Number(selectedVenueId.value);

  /* 選回空白時改為新增模式。 */
  if (!Number.isInteger(venueId) || venueId <= 0) {
    resetForm();
    return;
  }

  const venue = venues.value.find(
    (item) => Number(item.venueId) === venueId,
  );

  if (!venue) {
    errorMessage.value = "找不到選擇的場地資料";
    return;
  }

  /* 沿用既有編輯功能，不重複另一套修改邏輯。 */
  startEdit(venue, false);
}

/* 按下新增新場地時，清除既有場地選擇並恢復新增模式。 */
function startCreateVenue() {
  selectedVenueId.value = "";
  resetForm();
}

function resetForm() {
  /* 回到新增模式時，同時取消既有場地的快速選擇。 */
  selectedVenueId.value = "";
  editMode.value = false;

  form.value = {
    imageUrl: "", // 清除表單時同步清除圖片欄位。
    venueId: "",
    venueName: "",
    capacity: "",
    pricePerDay: "",
    venueStatus: "AVAILABLE",
  };
}

function normalizeStatus(status) {
  const value =
    String(status || "").trim();

  const aliases = {
    "可預約": "AVAILABLE",
    "維護中": "MAINTENANCE",
    "維修中": "MAINTENANCE",
    "停用": "DISABLED",
  };

  return (
    aliases[value] ||
    value.toUpperCase() ||
    "AVAILABLE"
  );
}

function statusLabel(status) {
  const normalized =
    normalizeStatus(status);

  const labels = {
    AVAILABLE: "可預約",
    MAINTENANCE: "維護中",
    DISABLED: "停用",
  };

  return labels[normalized] || status;
}

function money(value) {
  return new Intl.NumberFormat(
    "zh-TW",
    {
      style: "currency",
      currency: "TWD",
      maximumFractionDigits: 0,
    },
  ).format(Number(value || 0));
}
</script>

<template>
  <main class="venue-page">
    <section class="card">
      <h1>場地管理</h1>
      <p>
        Venue CRUD。既有「可預約」中文資料
        也可正常讀取與修改。
      </p>
    </section>
    <!--
      固定保留訊息顯示高度，
      避免成功或錯誤訊息出現時整個表單上下跳動。
    -->
    <!--
      成功或錯誤訊息使用浮動 Toast。
      不占頁面排版空間，因此不會造成介面上下跳動。
    -->
    <div
      v-if="message || errorMessage"
      class="venue-toast"
      :class="{
        success: Boolean(message),
        error: !message && Boolean(errorMessage),
      }"
      aria-live="polite"
    >
      {{
        message ||
        errorMessage
      }}
    </div>

    <section class="card">
      <h2>
        {{
          editMode
            ? "修改場地"
            : "新增場地"
        }}
      </h2>

      <!-- ===== 既有場地快速選擇 ===== -->
<div class="existing-venue-picker">
  <label>
    <span>快速選擇既有場地</span>

    <select
      v-model="selectedVenueId"
      :disabled="loading"
      @change="selectExistingVenue"
    >
      <option value="">
        請選擇既有場地
      </option>

      <!-- 直接使用後端 /api/venues 回傳資料，不把場地寫死在前端。 -->
      <option
        v-for="venue in venues"
        :key="venue.venueId"
        :value="String(venue.venueId)"
      >
        {{ venue.venueName }}
      </option>
    </select>
  </label>

  <button
    v-if="!editMode"
    type="button"
    class="secondary"
    :disabled="loading"
    @click="startCreateVenue"
  >
    ＋ 新增新場地
  </button>
</div>
<div class="form-grid">
        <label>
          <span>場地 ID</span>
          <input
            v-model="form.venueId"
            type="number"
            min="1"
            :disabled="editMode || loading"
          />
        </label>

        <label>
          <span>場地名稱</span>
          <input
            v-model="form.venueName"
            type="text"
            maxlength="50"
            :disabled="loading"
          />
        </label>

        <label>
          <span>容量</span>
          <input
            v-model="form.capacity"
            type="number"
            min="1"
            :max="fixedCapacityLimit ?? undefined"
            :disabled="loading"
          />
          <!-- 固定場地只要超過一人就立即顯示錯誤。 -->
          <span
            v-if="fixedCapacityWarning"
            class="capacity-warning"
          >
            {{ fixedCapacityWarning }}
          </span>
        </label>

        <label>
          <span>每日價格</span>
          <input
            v-model="form.pricePerDay"
            type="number"
            min="0"
            :disabled="loading"
          />
        </label>

        <div class="venue-status-actions">
<label class="venue-status-field">
          <span>場地狀態</span>
          <select
            v-model="form.venueStatus"
            :disabled="loading"
          >
            <option
              v-for="status in venueStatuses"
              :key="status"
              :value="status"
            >
              {{ statusLabel(status) }}
              ({{ status }})
            </option>
          </select>
        </label>

<div class="actions venue-form-actions">
        <button
          type="button"
          :disabled="loading || fixedCapacityExceeded"
          @click="handleSubmit"
        >
          {{
            editMode
              ? "儲存修改"
              : "新增場地"
          }}
        </button>

        <button
          v-if="editMode"
          type="button"
          class="secondary"
          :disabled="loading"
          @click="resetForm"
        >
          取消
        </button>
      </div>
        </div>
        <!-- 只維護單一圖片網址，不使用其他組員的上傳模組。 -->
        <label class="venue-image-field">
          <!-- 可留空使用本地預設圖。 -->
          <span>場地主圖</span>
          <!-- 後端仍會限制網址協定與長度。 -->
          <input
            v-model="form.imageUrl"
            type="text"
            placeholder="尚未導入圖片"
            readonly
          >

                    <!--
            圖片預覽框永遠保留固定 16:9 空間。
            有圖片時顯示圖片，沒有圖片時顯示提示，
            避免圖片導入前後造成整個表單上下跳動。
          -->
          <div class="venue-image-preview-frame">
            <img
              v-if="form.imageUrl"
              :src="form.imageUrl"
              class="venue-image-preview"
              alt="場地主圖預覽"
            />

            <div
              v-else
              class="venue-image-placeholder"
            >
              尚未導入圖片
            </div>
          </div>

          <!--
            真正的 file input 隱藏，
            由一鍵按鈕開啟 Windows 選檔視窗。
          -->
          <input
            ref="venueImageFileInput"
            type="file"
            class="hidden-image-input"
            accept="image/jpeg,image/png,image/webp"
            @change="handleLocalImageSelected"
          />

          <button
            type="button"
            class="local-image-button"
            :disabled="loading || !editMode"
            @click="openLocalImagePicker"
          >
            {{
              loading
                ? "圖片處理中..."
                : "選擇本機圖片並導入"
            }}
          </button>

          <span class="local-image-hint">
            支援 JPG、PNG、WEBP，最大 10 MB
          </span>

        </label>
      </div>


    </section>

    <section class="card">
      <div class="table-title">
        <h2>
          場地資料
        </h2>

        <button
          type="button"
          class="secondary"
          :disabled="loading"
          @click="loadVenues"
        >
          重新整理
        </button>
      </div>

      <div class="table-wrap">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>名稱</th>
              <th>容量</th>
              <th>每日價格</th>
              <th>狀態</th>
              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="venue in venues"
              :key="venue.venueId"
            >
              <td>{{ venue.venueId }}</td>
              <td>{{ venue.venueName }}</td>
              <td>{{ venue.capacity }}</td>
              <td>
                {{ money(venue.pricePerDay) }}
              </td>
              <td>
                {{
                  statusLabel(
                    venue.venueStatus,
                  )
                }}
              </td>
              <td class="actions">
                <button
                  type="button"
                  @click="startEdit(venue)"
                >
                  修改
                </button>

                <button
                  type="button"
                  class="danger"
                  @click="handleDelete(venue)"
                >
                  刪除
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>
  </main>
</template>

<style scoped>
.venue-page {
  width: min(1120px, calc(100% - 32px));
  margin: 32px auto 60px;
  font-family:
    Arial,
    "Microsoft JhengHei",
    sans-serif;
}

.card {
  background: #fff;
  border: 1px solid #e6e0d7;
  border-radius: 16px;
  padding: 26px;
  margin-bottom: 20px;
  box-shadow: 0 8px 24px
    rgba(62, 48, 35, 0.08);
}

h1,
h2 {
  color: #4a3b2a; /* 場地管理標題沿用飯店深棕色。 */
}

.form-grid {
  display: grid;
  grid-template-columns:
    repeat(2, minmax(0, 1fr));
  gap: 18px;
}

label {
  display: grid;
  gap: 8px;
  color: #5f5145;
  font-weight: 700;
}

input,
select {
  width: 100%;
  box-sizing: border-box;
  padding: 11px 12px;
  border: 1px solid #cfc5b8;
  border-radius: 8px;
  background: #fff;
  font: inherit;
}

.actions,
.table-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.actions {
  margin-top: 20px;
}

td.actions {
  margin-top: 0;
}

.table-title {
  justify-content: space-between;
}

button {
  border: 0;
  border-radius: 8px;
  padding: 9px 15px;
  background: #b58a46; /* 管理操作使用飯店金色按鈕。 */
  color: #fff;
  cursor: pointer;
  font-weight: 700;
}

button.secondary {
  background: #6c757d;
}

button.danger {
  background: #a33b3b;
}

.notice {
  padding: 12px 16px;
  border-radius: 8px;
}

.success {
  background: #e9f7ee;
  color: #21663a;
}

.error {
  background: #fff0f0;
  color: #a12626;
}

.table-wrap {
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 12px 10px;
  border-bottom: 1px solid #ece7df;
  text-align: left;
  white-space: nowrap;
}

th {
  background: #faf8f5;
  color: #6a5948;
}

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}

/* ===== Venue 後台 RWD 與可讀性修正 ===== */

/* 場地頁必須配合 AdminLayout 可用寬度，禁止向右撐出畫面。 */
.venue-page {
  width: 100% !important;
  max-width: 1120px;
  min-width: 0;
  box-sizing: border-box;
  margin-left: auto;
  margin-right: auto;
}

/* 二欄表單允許欄位縮小，避免右側欄位超出畫面。 */
.form-grid {
  grid-template-columns: repeat(2, minmax(0, 1fr)) !important;
}

/* 每一個 label 本身也允許縮小。 */
.form-grid label {
  min-width: 0;
}

/* 所有輸入與下拉都維持白底深字。 */
input,
select {
  width: 100%;
  min-width: 0;
  color: #2f2a24 !important;
  background: #ffffff !important;
  opacity: 1 !important;
}

/* 下拉項目展開後也維持清楚文字。 */
select option {
  color: #2f2a24 !important;
  background: #ffffff !important;
}

/* 場地資料表過寬時使用水平捲動，不撐破後台版面。 */
.table-wrap {
  width: 100%;
  max-width: 100%;
  min-width: 0;
  overflow-x: auto;
}

/* 窄螢幕改回單欄表單。 */
@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr !important;
  }
}

/* ===== 恢復場地 ID 數字上下箭頭 ===== */

/* 場地 ID 為 number 欄位，保留瀏覽器原生上下調整箭頭。 */
input[type="number"] {
  appearance: auto !important;
  -moz-appearance: auto !important;
}

/* Chrome / Edge 顯示數字欄位右側上下箭頭。 */
input[type="number"]::-webkit-inner-spin-button,
input[type="number"]::-webkit-outer-spin-button {
  -webkit-appearance: auto !important;
  appearance: auto !important;
  opacity: 1 !important;
}

/* ===== 既有場地快速選擇 ===== */

/* 既有場地下拉與新增按鈕排在同一區域。 */
.existing-venue-picker {
  display: flex;
  align-items: end;
  gap: 12px;
  margin-bottom: 20px;
}

/* 下拉欄位優先使用剩餘空間。 */
.existing-venue-picker label {
  flex: 1;
  min-width: 0;
}

/* 保證下拉選單使用白底深字。 */
.existing-venue-picker select,
.existing-venue-picker select option {
  color: #2f2a24 !important;
  background: #ffffff !important;
  opacity: 1 !important;
}

/* 小畫面時改成上下排列。 */
@media (max-width: 720px) {
  .existing-venue-picker {
    align-items: stretch;
    flex-direction: column;
  }
}

/* ===== Venue 本機圖片一鍵導入 ===== */

/* 真正的 file input 不直接顯示。 */
.hidden-image-input {
  display: none !important;
}

/* 本機圖片一鍵導入按鈕。 */
.local-image-button {
  width: auto;
  margin-top: 8px;
  padding: 9px 14px;
  border: 0;
  border-radius: 8px;
  background: #b58a46;
  color: #ffffff;
  font-weight: 700;
  cursor: pointer;
}

/* 停用狀態。 */
.local-image-button:disabled {
  opacity: 0.55;
  cursor: not-allowed;
}

/* 圖片格式提示。 */
.local-image-hint {
  display: block;
  margin-top: 6px;
  color: #77695b;
  font-size: 12px;
  font-weight: 400;
}

/* 後台圖片預覽。 */
.venue-image-preview {
  display: block;
  width: 220px;
  max-width: 100%;
  height: 130px;
  margin-top: 10px;
  object-fit: cover;
  border: 1px solid #ded6ca;
  border-radius: 10px;
  background: #f7f4ef;
}

/* ===== 場地圖片統一 16:9 Cover ===== */

/*
 * 場地圖片統一使用 16:9。
 * cover 會保持圖片比例並填滿圖片框，
 * 超出框框的少量邊緣自動裁切。
 */
.venue-card img,
.venue-image,
.venue-image img,
.venue-image-preview,
.rental-thumb {
  width: 100% !important;
  aspect-ratio: 16 / 9 !important;
  object-fit: cover !important;
  object-position: center center !important;
  display: block;
}

/* 後台圖片預覽不超過合理寬度。 */
.venue-image-preview {
  max-width: 420px;
  height: auto;
}

/* 預約清單中的縮圖維持較小尺寸，但比例仍固定 16:9。 */
.rental-thumb {
  width: 96px !important;
  height: auto !important;
}

/* ===== 場地狀態取消多餘留白 ===== */

/* 場地狀態不跟著右側圖片區塊撐高。 */
.venue-status-field {
  align-self: start !important;
  justify-content: flex-start !important;
  height: auto !important;
  min-height: 0 !important;
}

/* 下拉選單直接接在標題下方。 */
.venue-status-field select {
  margin-top: 8px !important;
}

/* ===== Venue 圖片預覽固定尺寸，避免跳動 ===== */

/*
 * 不論是否已有圖片，
 * 預覽區永遠保留完全相同的 16:9 空間。
 */
.venue-image-preview-frame {
  position: relative;
  width: 100%;
  aspect-ratio: 16 / 9;
  margin-top: 10px;
  overflow: hidden;
  border: 1px solid #ded6ca;
  border-radius: 10px;
  background: #f7f4ef;
  box-sizing: border-box;
}

/*
 * 真正圖片填滿固定框。
 * cover 保持原比例，必要時只裁切少量邊緣。
 */
.venue-image-preview-frame .venue-image-preview {
  position: absolute;
  inset: 0;
  width: 100% !important;
  height: 100% !important;
  max-width: none !important;
  margin: 0 !important;
  aspect-ratio: auto !important;
  object-fit: cover !important;
  object-position: center center !important;
  border: 0 !important;
  border-radius: 0 !important;
}

/*
 * 尚未導入圖片時，
 * placeholder 使用與實際圖片完全相同的空間。
 */
.venue-image-placeholder {
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #776d62;
  font-size: 14px;
  background: #f7f4ef;
}

/* ===== Venue 最終防跳動版面 ===== */

/*
 * 成功／錯誤訊息改成浮動 Toast。
 * 不占頁面高度，因此不會推動表單。
 */
.venue-toast {
  position: fixed;
  top: 20px;
  right: 20px;
  z-index: 5000;

  width: auto;
  max-width: min(440px, calc(100vw - 40px));

  padding: 13px 18px;

  border: 1px solid transparent;
  border-radius: 10px;

  font-size: 15px;
  font-weight: 700;
  line-height: 1.5;

  box-sizing: border-box;

  box-shadow:
    0 8px 24px
    rgba(0, 0, 0, 0.14);
}

/* 成功提示。 */
.venue-toast.success {
  color: #17643a;
  background: #eaf7ef;
  border-color: #b9e0c9;
}

/* 錯誤提示。 */
.venue-toast.error {
  color: #a02f2f;
  background: #fff0f0;
  border-color: #efc0c0;
}

/*
 * 左側場地狀態與操作按鈕放在同一個 Grid cell。
 * 因此右側圖片再高，也不會把按鈕推到底部。
 */
.venue-status-actions {
  display: flex;
  flex-direction: column;
  align-items: flex-start;

  gap: 18px;

  min-width: 0;
  align-self: start;
}

/* 場地狀態欄位維持合理寬度。 */
.venue-status-actions .venue-status-field {
  width: 100%;
  max-width: 220px;

  margin: 0;
}

/* 儲存／取消直接接在場地狀態下面。 */
.venue-status-actions .venue-form-actions {
  margin-top: 0 !important;

  display: flex;
  align-items: center;
  flex-wrap: wrap;

  gap: 10px;
}

/* 場地主圖欄位維持右欄並允許正常縮放。 */
.venue-image-field {
  min-width: 0;
  align-self: start;
}

/*
 * 二欄 Grid 上緣對齊。
 */
.form-grid {
  align-items: start;
}

/*
 * 手機改單欄時，
 * 狀態／按鈕與圖片自然依序往下排列。
 */
@media (max-width: 720px) {

  .venue-status-actions {
    width: 100%;
  }

  .venue-status-actions .venue-status-field {
    max-width: none;
  }

  .venue-toast {
    top: 12px;
    right: 12px;
    left: 12px;

    max-width: none;
  }
}

/* ===== Venue 驗收 UI 最終修正 ===== */

/* 固定場地容量超額時使用明顯紅字，不影響其他管理頁。 */
.capacity-warning {
  color: #a12626;
  font-size: 14px;
  font-weight: 700;
  line-height: 1.5;
}

/*
 * 成功或錯誤訊息固定顯示在右上角。
 * 不參與頁面排版，因此完全不留白。
 */
.notice-slot,
.venue-toast {
  position: fixed !important;
  top: 18px !important;
  right: 18px !important;
  z-index: 9999 !important;

  width: auto !important;
  max-width: min(440px, calc(100vw - 36px)) !important;

  min-height: 0 !important;
  height: auto !important;

  margin: 0 !important;
  padding: 0 !important;

  display: block !important;
}

/* notice-slot 裡的提示本身也禁止產生外距。 */
.notice-slot .notice {
  width: auto !important;
  min-width: 280px;

  margin: 0 !important;

  box-shadow:
    0 8px 24px
    rgba(0, 0, 0, 0.14);
}

/*
 * 編輯模式時操作按鈕保持在場地狀態下方，
 * 不因右側圖片高度產生大片空白。
 */
.venue-status-actions {
  align-self: start !important;
}

/* 手機版 Toast 左右保留安全距離。 */
@media (max-width: 720px) {

  .notice-slot,
  .venue-toast {
    top: 12px !important;
    left: 12px !important;
    right: 12px !important;

    max-width: none !important;
  }

  .notice-slot .notice {
    min-width: 0;
    width: 100% !important;
  }
}

/* ===== Venue Admin Full Width Override ===== */

/*
 * 場地管理使用後台全部可用寬度。
 * 解除原本 1120px 的中央窄版限制。
 */
.venue-page {
  width: 100% !important;
  max-width: none !important;
  margin-left: 0 !important;
  margin-right: 0 !important;
}

.venue-page > .card {
  width: 100% !important;
  max-width: none !important;
}

/* ===== Venue Admin Edge To Edge ===== */

/*
 * 抵消 AdminLayout .admin-content 左右各 30px padding。
 * 只作用於場地管理頁。
 */
.venue-page {
  width: calc(100% + 60px) !important;
  max-width: none !important;
  margin-left: -30px !important;
  margin-right: -30px !important;
}

/* 所有場地管理卡片完整使用頁面寬度。 */
.venue-page > .card {
  width: 100% !important;
  max-width: none !important;
  box-sizing: border-box;
}
</style>
