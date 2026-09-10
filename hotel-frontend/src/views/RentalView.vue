<script setup>
import { computed, onMounted, ref, watch } from "vue"; // 路由或場地切換時同步更新安全的畫面資料。
import { useRouter, useRoute } from "vue-router"; // 路由僅用於切換顯示，後端仍獨立驗證權限。
import { getOccupiedDates, getRentalPayment, checkoutRental } from "../api/venueRentalApi"; // 使用場地專用占用與付款 API。

import {
  cancelMyRental,
  createRental,
  getApiErrorMessage,
  getMyRentals,
  getStoredToken,
  getVenues,
} from "../api/venueRentalApi";

const router = useRouter();
const route = useRoute(); // 讀取目前前台或管理模式。
const occupied = ref([]); // 只儲存不含私人資訊的占用日期。
const payments = ref({}); // 以租借編號對應歷史付款資訊。
watch(() => route.name, () => { rentals.value = []; payments.value = {}; if (token.value) loadData(); }); // 同一元件切換前後台時先清空資料再重新查詢。
const today = () => new Intl.DateTimeFormat("en-CA", { timeZone: "Asia/Taipei", year: "numeric", month: "2-digit", day: "2-digit" }).format(new Date()); // 與後端使用相同飯店日期。
const dates = computed(() => { // 提供未來一年可選日期並禁用占用日期。
  const start = new Date(`${today()}T00:00:00Z`); // 用 UTC 計算日期避免日光節約偏移。
  return Array.from({ length: 366 }, (_, index) => { // 有限日期清單與後端查詢範圍一致。
    const day = new Date(start); day.setUTCDate(day.getUTCDate() + index); // 每個選項代表完整一天。
    return day.toISOString().slice(0, 10); // 表單僅顯示日期。
  });
});
const isOccupied = (venueId, day) => occupied.value.some(item => Number(item.venueId) === Number(venueId) && item.date === day && item.occupied); // 卡片與日期選項使用同一份占用資料。
async function pay(rental) { // 付款狀態只能由後端更新。
  loading.value = true; errorMessage.value = ""; // 防止重複按下付款按鈕。
  try { // 先取得後端驗證及簽章後的參數。
    const result = await checkoutRental(token.value, rental.rentalId); // 不傳送會員或價格。
    if (result.action !== "https://payment-stage.ecpay.com.tw/Cashier/AioCheckOut/V5") throw new Error("付款網址不是測試環境"); // 前端再限制 Stage。
    const element = document.createElement("form"); element.method = "POST"; element.action = result.action; // 使用官方要求的表單導轉。
    for (const [name, value] of Object.entries(result.parameters)) { // 只使用後端回傳的公開付款欄位。
      const input = document.createElement("input"); input.type = "hidden"; input.name = name; input.value = String(value); element.appendChild(input); // 用 DOM 屬性避免插入 HTML。
    }
    document.body.appendChild(element); element.submit(); element.remove(); // 提交到 Stage，不自行標記付款成功。
  } catch (error) { errorMessage.value = getApiErrorMessage(error); } finally { loading.value = false; } // 顯示安全錯誤並恢復按鈕。
}

const token = ref("");
const venues = ref([]);
const rentals = ref([]);

const loading = ref(false);
const message = ref("");
const errorMessage = ref("");

const form = ref({
  venueId: "",
  eventName: "",
  rentalDate: "",
  guestCount: "",
});
watch(() => form.value.venueId, () => { form.value.rentalDate = ""; }); // 表單初始化後監看場地切換，避免沿用已占用日期。

const availableVenues = computed(() =>
  venues.value.filter((venue) => {
    const status = String(venue.venueStatus || "").trim();
    return (
      status.toUpperCase() === "AVAILABLE" ||
      status === "可預約"
    );
  }),
);

const selectedVenue = computed(() =>
  venues.value.find(
    (venue) =>
      Number(venue.venueId) ===
      Number(form.value.venueId),
  ),
);


/*
 * 即時判斷目前輸入人數是否超過所選場地容量。
 * 後端仍保留相同驗證，前端警告只是改善使用體驗。
 */
const capacityExceeded = computed(() => {
  if (!selectedVenue.value) {
    return false;
  }

  const guestCount = Number(form.value.guestCount);

  if (!Number.isFinite(guestCount) || guestCount <= 0) {
    return false;
  }

  return guestCount >
    Number(selectedVenue.value.capacity);
});

/* 超額時直接顯示場地名稱與最大人數。 */
const capacityWarning = computed(() => {
  if (!capacityExceeded.value) {
    return "";
  }

  return `參加人數不可超過「${selectedVenue.value.venueName}」最大容量 ${selectedVenue.value.capacity} 人`;
});
const venueMap = computed(() => {
  const result = new Map();

  for (const venue of venues.value) {
    result.set(
      Number(venue.venueId),
      venue.venueName,
    );
  }

  return result;
});

const minimumRentalDate = computed(() => {
  const now = new Date();
  now.setMinutes(
    now.getMinutes() - now.getTimezoneOffset(),
  );
  return now.toISOString().slice(0, 16);
});

onMounted(async () => {
  token.value = getStoredToken();

  if (!token.value) {
    router.push("/login");
    return;
  }

  await loadData();
});

async function loadData() {
  loading.value = true;
  errorMessage.value = "";

  try {
    const [venueData, rentalData] =
      await Promise.all([
        getVenues(token.value),
        getMyRentals(token.value), // 此元件只供會員前台與會員中心使用，永遠只查本人租借。
      ]);

    venues.value = venueData ?? [];
    rentals.value = rentalData ?? [];
    occupied.value = await getOccupiedDates(token.value, dates.value[0], dates.value.at(-1)); // 占用資料僅包含場地與日期。
    const details = await Promise.all(rentals.value.map(rental => getRentalPayment(token.value, rental.rentalId))); // 每筆付款都由後端驗證所有權。
    payments.value = Object.fromEntries(details.map(detail => [detail.rentalId, detail])); // 保存歷史付款金額與狀態。
  } catch (error) {
    errorMessage.value =
      getApiErrorMessage(error);
  } finally {
    loading.value = false;
  }
}


/*
 * 日曆選到日期後立即檢查該場地是否已被預約。
 * 後端仍保留最後一道碰撞驗證。
 */
function validateSelectedRentalDate() {
  const venueId = Number(form.value.venueId);
  const rentalDate = form.value.rentalDate;

  if (!venueId || !rentalDate) {
    return;
  }

  if (isOccupied(venueId, rentalDate)) {
    errorMessage.value =
      "此場地於指定日期已被預約，請選擇其他日期";

    form.value.rentalDate = "";
    return;
  }

  /* 日期正常時清除先前的日期錯誤。 */
  if (
    errorMessage.value ===
    "此場地於指定日期已被預約，請選擇其他日期"
  ) {
    errorMessage.value = "";
  }
}
async function handleSubmit() {
  message.value = "";
  errorMessage.value = "";

  const venueId = Number(form.value.venueId);
  const guestCount = Number(
    form.value.guestCount,
  );

  if (
    !Number.isInteger(venueId) ||
    venueId <= 0
  ) {
    errorMessage.value = "請選擇場地";
    return;
  }

  if (!form.value.eventName.trim()) {
    errorMessage.value = "請輸入活動名稱";
    return;
  }

  if (!form.value.rentalDate) {
    errorMessage.value = "請選擇租借日期與時間";
    return;
  }
  if (isOccupied(venueId, form.value.rentalDate)) { errorMessage.value = "此場地於指定日期已被預約"; return; } // 場地切換或占用更新後仍拒絕送出不可用日期。

  if (
    !Number.isInteger(guestCount) ||
    guestCount <= 0
  ) {
    errorMessage.value =
      "參加人數必須是大於 0 的整數";
    return;
  }

  if (
    selectedVenue.value &&
    guestCount > selectedVenue.value.capacity
  ) {
    errorMessage.value =
      `參加人數不可超過「${selectedVenue.value.venueName}」最大容量 ${selectedVenue.value.capacity} 人`;
    return;
  }

  loading.value = true;

  try {
    const saved = await createRental(
      token.value,
      {
        venueId,
        eventName:
          form.value.eventName.trim(),
        rentalDate:
          `${form.value.rentalDate}T00:00:00`, // 保留既有後端日期時間格式並固定當日零時。
        guestCount,
      },
    );

    message.value = "租借申請已成功送出，請至「我的場地預約」查看狀態。";

    resetForm();
    await loadData();
  } catch (error) {
    errorMessage.value =
      getApiErrorMessage(error);
  } finally {
    loading.value = false;
  }
}


/*
 * 會員取消自己的預約。
 *
 * 前端只送 rentalId，
 * 所有權與付款狀態由後端再次驗證。
 */
async function cancelRental(rental) {
  message.value = "";
  errorMessage.value = "";

  const confirmed = window.confirm(
    `確定要取消「${venueName(rental.venueId)}」的預約嗎？`,
  );

  if (!confirmed) {
    return;
  }

  loading.value = true;

  try {
    await cancelMyRental(
      token.value,
      rental.rentalId,
    );

    message.value = "場地預約已取消";

    /* 重新載入租借、付款與占用日期。 */
    await loadData();
  } catch (error) {
    errorMessage.value =
      getApiErrorMessage(error);
  } finally {
    loading.value = false;
  }
}
function resetForm() {
  form.value = {
    venueId: "",
    eventName: "",
    rentalDate: "",
    guestCount: "",
  };
}

function formatDateTime(value) {
  if (!value) {
    return "";
  }

  return String(value)
    .replace("T", " ")
    .substring(0, 16);
}

function venueName(venueId) {
  return (
    venueMap.value.get(Number(venueId)) ||
    `場地 #${venueId}`
  );
}


/*
 * 只有尚未完成、尚未取消，而且尚未付款的本人預約，
 * 才在會員畫面顯示取消按鈕。
 */
function canCancelRental(rental) {
  const rentalStatus =
    String(rental.rentalStatus || "").trim();

  const paymentStatus =
    String(
      payments[rental.rentalId]?.paymentStatus || "",
    ).trim().toUpperCase();

  const activeRental = [
    "PENDING",
    "CONFIRMED",
    "待確認",
    "已確認",
  ].includes(rentalStatus);

  const alreadyPaid = [
    "已付款",
    "PAID",
    "SUCCESS",
  ].includes(paymentStatus);

  return activeRental && !alreadyPaid;
}
function rentalStatusLabel(status) {
  const labels = {
    PENDING: "待確認",
    CONFIRMED: "已確認",
    CANCELLED: "已取消",
    COMPLETED: "已完成",
  };

  return labels[status] || status || "-";
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
  <main class="rental-page">
    <section class="hero">
      <div>
        <p class="eyebrow">VENUE RENTAL V2.0</p>
        <h1>場地租借</h1>
        <p>
          選擇場地與活動資料即可完成申請。
          租借編號、會員編號與付款編號由系統自動處理。
        </p>
      </div>
    </section>

    <p
      v-if="message"
      class="notice success"
    >
      {{ message }}
    </p>

    <p
      v-if="errorMessage"
      class="notice error"
    >
      {{ errorMessage }}
    </p>

    <!-- 真實場地規格與占用資訊，不編造介紹或照片。 -->
    <section class="venue-cards">
      <!-- 每個場地保留自己的圖片與日期。 -->
      <article v-for="venue in venues" :key="venue.venueId" class="card">
        <!-- 無圖片或載入失敗使用本地中性預設圖。 -->
        <img class="venue-image" :src="venue.imageUrl || '/images/venue-placeholder.svg'" :alt="venue.venueName" @error="$event.target.src = '/images/venue-placeholder.svg'" />
        <!-- 顯示資料庫中的名稱與規格。 -->
        <h2>{{ venue.venueName }}</h2>
        <!-- 不編造不存在的場地介紹。 -->
        <p>場地說明尚未設定</p>
        <!-- 容量與價格都以後端場地資料為準。 -->
        <p>{{ venue.capacity }} 人｜{{ money(venue.pricePerDay) }}／日</p>
        <!-- 顏色與文字共同傳達場地狀態。 -->
        <span class="status-badge" :class="availableVenues.some(item => item.venueId === venue.venueId) ? 'available' : 'unavailable'">{{ venue.venueStatus }}</span> <!-- 可預約用綠色，其他狀態用紅色並保留文字。 -->
        <!-- 有限範圍的已預約日期不含私人租借欄位。 -->
        <details><summary>查看未來一年已預約日期</summary>
          <!-- 每個日期僅顯示占用結果。 -->
          <p>{{ occupied.filter(item => Number(item.venueId) === Number(venue.venueId)).map(item => item.date).join('、') || '目前無已預約日期' }}</p>
        </details>
        <!-- 停用場地無法選取，最終仍由後端驗證。 -->
        <button type="button" :disabled="loading || !availableVenues.some(item => item.venueId === venue.venueId)" @click="form.venueId = venue.venueId; form.rentalDate = ''">選擇場地</button> <!-- 此頁固定提供會員選擇場地。 -->
      </article>
    </section>
    <!-- 會員前台固定顯示新增表單，後台使用獨立管理元件。 -->
    <section class="card">
      <h2>新增租借</h2>

      <div class="form-grid">
        <label>
          <span>場地</span>

          <select
            v-model="form.venueId"
            :disabled="loading"
          >
            <option value="">
              請選擇場地
            </option>

            <option
              v-for="venue in availableVenues"
              :key="venue.venueId"
              :value="venue.venueId"
            >
              {{ venue.venueName }}
              ｜容量 {{ venue.capacity }}
              ｜{{ money(venue.pricePerDay) }}/日
            </option>
          </select>
        </label>

        <label>
          <span>活動名稱</span>
          <input
            v-model="form.eventName"
            type="text"
            maxlength="50"
            placeholder="例如：公司會議"
            :disabled="loading"
          />
        </label>

        <label>
          <!-- 本版為整日租借，禁用已占用日期。 -->
          <span>整日租借日期</span>
          <!-- 必須先選場地才能選擇可用日期。 -->
          <!-- 使用瀏覽器原生日曆點選日期，不再列出長日期清單。 -->
<input
  v-model="form.rentalDate"
  type="date"
  :min="today()"
  :disabled="loading || !form.venueId"
  @change="validateSelectedRentalDate"
/>
        </label>

        <label>
          <span>參加人數</span>
          <input
            v-model="form.guestCount"
            type="number"
            min="1"
            :max="
              selectedVenue
                ? selectedVenue.capacity
                : undefined
            "
            :disabled="loading"
          />

          <!-- 輸入人數超過場地容量時立即提示。 -->
          <p
            v-if="capacityWarning"
            class="capacity-warning"
          >
            ⚠ {{ capacityWarning }}
          </p>
        </label>
      </div>

      <div
        v-if="selectedVenue"
        class="venue-summary"
      >
        <strong>
          {{ selectedVenue.venueName }}
        </strong>

        <span>
          容量：
          {{ selectedVenue.capacity }} 人
        </span>

        <span>
          每日價格：
          {{ money(selectedVenue.pricePerDay) }}
        </span>
      </div>

      <div class="actions">
        <button
          type="button"
          :disabled="loading || capacityExceeded"
          @click="handleSubmit"
        >
          {{
            loading
              ? "處理中..."
              : "送出租借申請"
          }}
        </button>

        <button
          type="button"
          class="secondary"
          :disabled="loading"
          @click="resetForm"
        >
          清除
        </button>
      </div>
    </section>

    <section class="card">
      <div class="table-title">
        <div>
          <!-- 顯示模式不同，資料來源也由後端權限保護。 -->
          <h2>我的場地預約</h2> <!-- 前台只列出目前會員的資料。 -->
          <p>
            共 {{ rentals.length }} 筆
          </p>
        </div>

        <button
          type="button"
          class="secondary"
          :disabled="loading"
          @click="loadData"
        >
          重新整理
        </button>
      </div>

      <div
        v-if="rentals.length"
        class="table-wrap"
      >
        <table>
          <thead>
            <tr>
              <th class="technical-id">租借編號</th>
              <th>場地</th>
              <th>活動</th>
              <th>日期</th>
              <th>人數</th>
              <th class="technical-id">付款編號</th>
              <th>狀態</th>
              <!-- 付款金額永遠讀取歷史保存值。 -->
              <th>歷史金額／付款</th>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="rental in rentals"
              :key="rental.rentalId"
            >
              <td class="technical-id">{{ rental.rentalId }}</td>
              <td>
                <!-- 租借紀錄同樣提供場地圖片或本地預設圖。 -->
                <img class="rental-thumb" :src="venues.find(item => item.venueId === rental.venueId)?.imageUrl || '/images/venue-placeholder.svg'" alt="場地圖片" @error="$event.target.src = '/images/venue-placeholder.svg'" />
                {{ venueName(rental.venueId) }}
              </td>
              <td>
                {{ rental.eventName }}
              </td>
              <td>
                {{
                  formatDateTime(
                    rental.rentalDate,
                  )
                }}
              </td>
              <td>
                {{ rental.guestCount }}
              </td>
              <td class="technical-id">{{ rental.paymentId }}</td>
              <td>
                {{
                  rentalStatusLabel(
                    rental.rentalStatus,
                  )
                }}
              </td>
              <!-- 僅待付款且有效的本人租借提供付款按鈕。 -->
              <td>
                <!-- 金額與狀態皆來自付款 API。 -->
                <p>{{ money(payments[rental.rentalId]?.totalPrice) }}｜{{ payments[rental.rentalId]?.paymentStatus || '尚未載入' }}</p>
                <!-- 已付款、取消、完成及過去日期不再次開啟付款。 -->
                <button v-if="payments[rental.rentalId]?.paymentStatus === '待付款' && ['PENDING', 'CONFIRMED', '待確認', '已確認'].includes(rental.rentalStatus) && String(rental.rentalDate).slice(0,10) >= today()" :disabled="loading" @click="pay(rental)">前往綠界付款</button> <!-- 僅本人有效且待付款的租借可以結帳。 -->
                <!--
                  只有本人、有效狀態且尚未付款時顯示取消。
                  真正的付款與所有權限制仍由後端驗證。
                -->
                <button
                  v-if="canCancelRental(rental)" type="button"
                  class="cancel-rental-button"
                  :disabled="loading"
                  @click="cancelRental(rental)"
                >
                  取消預約
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <p
        v-else-if="!loading"
        class="empty"
      >
        目前沒有租借紀錄。
      </p>
    </section>
  </main>
</template>

<style scoped>
.venue-cards { display: grid; grid-template-columns: repeat(auto-fit, minmax(240px, 1fr)); gap: 16px; } /* 響應式場地卡片，不影響共用樣式。 */
.venue-image { width: 100%; height: auto; object-fit: cover; border-radius: 14px; } /* 高度交給下方十六比九規則計算，避免固定高度破壞比例。 */
.rental-thumb { width: 64px; height: 44px; object-fit: cover; border-radius: 8px; } /* 紀錄縮圖不撐大表格。 */
.status-badge { display: inline-block; background: #fff3d8; color: #95691f; border-radius: 14px; padding: 4px 10px; } /* 文字與狀態底色共同表達資訊。 */
.status-badge.available { background: #e5f6eb; color: #257641; } /* 可預約狀態使用飯店既有成功色。 */
.status-badge.unavailable { background: #fde9e7; color: #b3443c; } /* 維護與停用狀態不可操作，以紅色輔助辨識。 */
.venue-cards details { margin: 12px 0; overflow-wrap: anywhere; } /* 長日期清單在卡片中換行。 */
.rental-page {
  width: 100%;
  max-width: none;
  margin: 32px 0 60px;
  font-family:
    Arial,
    "Microsoft JhengHei",
    sans-serif;
}

.hero,
.card {
  background: #fff;
  border: 1px solid #e6e0d7;
  border-radius: 14px; /* 與飯店既有卡片圓角一致。 */
  box-shadow: 0 8px 24px
    rgba(62, 48, 35, 0.08);
}

.hero {
  padding: 30px;
  margin-bottom: 20px;
}

/* 僅設定場地頁的說明段落，排除金棕色眉題並避免繼承淺色文字。 */
.hero p:not(.eyebrow) {
  margin: 0; /* 移除段落預設外距。 */
  color: #6a5948; /* 在白色背景上提供清楚的深棕色說明文字。 */
  opacity: 1; /* 確保說明文字完整顯示。 */
  line-height: 1.7; /* 保持兩行說明的閱讀間距。 */
}

.eyebrow {
  color: #9b7435;
  font-weight: 700;
  letter-spacing: 1.5px;
  margin: 0 0 8px;
}

h1,
h2 {
  color: #4a3b2a; /* 標題沿用飯店深棕色。 */
}

h1 {
  margin: 0 0 10px;
}

.card {
  padding: 26px;
  margin-bottom: 20px;
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

.venue-summary {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
  margin-top: 18px;
  padding: 14px;
  border-radius: 10px;
  background: #f7f2ea;
}

.actions,
.table-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.actions {
  margin-top: 22px;
}

.table-title {
  justify-content: space-between;
}

button {
  border: 0;
  border-radius: 8px;
  padding: 10px 18px;
  background: #b58a46; /* 主要按鈕沿用飯店金色。 */
  color: #fff;
  cursor: pointer;
  font-weight: 700;
}

button.secondary {
  background: #6c757d;
}

button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
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
  border-bottom: 1px solid #ece7df;
  padding: 12px 10px;
  text-align: left;
  white-space: nowrap;
}

th {
  color: #6a5948;
  background: #faf8f5;
}

.empty {
  color: #777;
  text-align: center;
  padding: 28px 0 8px;
}

@media (max-width: 720px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}

/* ===== 會員取消與容量警告 ===== */

/* 超過場地容量時使用明顯紅色提示。 */
.capacity-warning {
  margin: 2px 0 0;
  color: #a12626;
  font-size: 14px;
  font-weight: 700;
  line-height: 1.5;
}

/* 客人取消預約使用危險操作配色。 */
.cancel-rental-button {
  margin-top: 8px;
  margin-left: 6px;
  background: #a33b3b;
  color: #ffffff;
}

/* 滑鼠移到取消按鈕時加深。 */
.cancel-rental-button:hover:not(:disabled) {
  background: #812d2d;
}

/* ===== Rental 客戶端緊縮版面 ===== */

/* 整體頁面縮窄，避免資訊過度拉開。 */
.rental-page {
  width: 100% !important;
  max-width: none !important;
  margin: 20px 0 40px !important;
}

/* 卡片內距縮小。 */
.card {
  padding: 18px !important;
  margin-bottom: 16px !important;
}

/* 新增租借欄位間距縮小。 */
.form-grid {
  gap: 12px 16px !important;
}

/* 表單高度略為縮小。 */
input,
select {
  padding: 9px 11px !important;
}

/* 客人不需要看到資料庫技術 ID，但資料本身仍保留。 */
.technical-id {
  display: none !important;
}

/*
 * 表格固定在目前卡片寬度內。
 * 不再要求使用者向右拖動。
 */
.table-wrap {
  width: 100%;
  max-width: 100%;
  overflow-x: visible !important;
}

.table-wrap table {
  width: 100%;
  table-layout: fixed;
}

/* 允許欄位內容換行，避免撐寬整張表。 */
.table-wrap th,
.table-wrap td {
  padding: 8px 6px !important;
  font-size: 13px;
  white-space: normal !important;
  overflow-wrap: anywhere;
  vertical-align: middle;
}

/* 場地圖片縮小，保留辨識用途。 */
.rental-thumb {
  width: 46px !important;
  height: 34px !important;
  object-fit: cover;
  border-radius: 6px;
}

/* 付款與操作區按鈕允許換行排列。 */
.table-wrap td:last-child button {
  padding: 7px 9px;
  margin: 3px 3px 3px 0;
  font-size: 12px;
}

/* 金額資訊不要撐寬表格。 */
.table-wrap td:last-child p {
  margin: 0 0 4px;
  line-height: 1.35;
  white-space: normal;
}

/* 小螢幕再縮成單欄表單。 */
@media (max-width: 720px) {
  .rental-page {
    width: calc(100% - 16px) !important;
  }

  .card {
    padding: 14px !important;
  }

  .form-grid {
    grid-template-columns: 1fr !important;
  }

  .table-wrap th,
  .table-wrap td {
    font-size: 12px;
    padding: 7px 4px !important;
  }
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
</style>
