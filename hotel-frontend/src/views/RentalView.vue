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

const demoPaymentOpen = ref(false);
const demoPaymentStep = ref("methods");
const demoPaymentRental = ref(null);
const demoPaymentMethod = ref("");
const demoPaymentBusy = ref(false);
const demoPaymentError = ref("");
const demoCreditMode = ref("single");
const demoInstallment = ref("3");
const demoWallet = ref("Jkopay");
const demoBank = ref("822");

const demoPaymentMethods = [
  {
    code: "Credit",
    icon: "💳",
    title: "信用卡",
    note: "一次付清／分期／信用卡付款驗證",
  },
  {
    code: "ApplePay",
    icon: "",
    title: "Apple Pay",
    note: "掃描 QR Code 開啟付款",
  },
  {
    code: "ATM",
    icon: "🏧",
    title: "ATM 虛擬帳號",
    note: "取得虛擬帳號後完成轉帳",
  },
  {
    code: "BARCODE",
    icon: "▥",
    title: "超商條碼",
    note: "取得三段條碼後完成繳費",
  },
  {
    code: "WebATM",
    icon: "🏦",
    title: "WebATM",
    note: "網路 ATM 授權轉帳",
  },
  {
    code: "DigitalPayment",
    icon: "📱",
    title: "電子支付",
    note: "街口支付／iPASS MONEY／LINE Pay",
  },
];

const demoCard = ref({
  number: "4311 9522 2222 2222",
  expiry: "12/27",
  cvv: "222",
  holder: "WANG SAMPLE",
  otp: "1234",
});

function resetDemoPayment() {
  demoPaymentStep.value = "methods";
  demoPaymentMethod.value = "";
  demoPaymentBusy.value = false;
  demoPaymentError.value = "";
  demoCreditMode.value = "single";
  demoInstallment.value = "3";
  demoWallet.value = "Jkopay";
  demoBank.value = "822";
  demoCard.value = {
    number: "4311 9522 2222 2222",
    expiry: "12/27",
    cvv: "222",
    holder: "WANG SAMPLE",
    otp: "1234",
  };
}

function demoPay(rental) {
  demoPaymentRental.value = rental;
  resetDemoPayment();
  demoPaymentOpen.value = true;
}

function closeDemoPayment() {
  if (demoPaymentBusy.value) return;
  demoPaymentOpen.value = false;
  demoPaymentRental.value = null;
  resetDemoPayment();
}

function demoMethodLabel(code) {
  return demoPaymentMethods.find((item) => item.code === code)?.title || code;
}

function demoPaymentAmount() {
  const rentalId = demoPaymentRental.value?.rentalId;
  return Number(payments.value?.[rentalId]?.totalPrice || 0);
}
function demoInstallmentAmount() {
  const months = Number(demoInstallment.value || 1);

  return Math.ceil(
    demoPaymentAmount() / Math.max(months, 1),
  );
}


/*
 * 成果展示用 QR Matrix。
 * 僅用來呈現付款介面外觀，不代表真實可掃描的付款憑證。
 */
function demoQrCells(seed) {
  const size = 29;
  const cells = Array(size * size).fill(false);

  const index = (row, col) => row * size + col;

  const setCell = (row, col, value = true) => {
    if (
      row >= 0 &&
      row < size &&
      col >= 0 &&
      col < size
    ) {
      cells[index(row, col)] = value;
    }
  };

  const drawFinder = (top, left) => {
    for (let row = 0; row < 7; row += 1) {
      for (let col = 0; col < 7; col += 1) {
        const outer =
          row === 0 ||
          row === 6 ||
          col === 0 ||
          col === 6;

        const inner =
          row >= 2 &&
          row <= 4 &&
          col >= 2 &&
          col <= 4;

        setCell(top + row, left + col, outer || inner);
      }
    }
  };

  const reserved = (row, col) => {
    const topLeft =
      row <= 8 &&
      col <= 8;

    const topRight =
      row <= 8 &&
      col >= size - 9;

    const bottomLeft =
      row >= size - 9 &&
      col <= 8;

    return topLeft || topRight || bottomLeft;
  };

  drawFinder(0, 0);
  drawFinder(0, size - 7);
  drawFinder(size - 7, 0);

  let hash = 2166136261;

  for (const char of String(seed)) {
    hash ^= char.charCodeAt(0);
    hash = Math.imul(hash, 16777619);
  }

  for (let row = 0; row < size; row += 1) {
    for (let col = 0; col < size; col += 1) {
      if (reserved(row, col)) {
        continue;
      }

      if (row === 6 || col === 6) {
        setCell(
          row,
          col,
          (row + col) % 2 === 0,
        );

        continue;
      }

      hash ^= row * 37 + col * 17 + 11;
      hash = Math.imul(hash, 16777619);

      const dark =
        (hash >>> 0) % 100 < 47;

      setCell(row, col, dark);
    }
  }

  return cells;
}
function selectDemoMethod(code) {
  demoPaymentMethod.value = code;
  demoPaymentError.value = "";

  const next = {
    Credit: "credit",
    ApplePay: "apple",
    ATM: "atm",
    BARCODE: "barcode",
    WebATM: "webatm",
    DigitalPayment: "digital",
  };

  demoPaymentStep.value = next[code] || "methods";
}

function submitDemoCard() {
  demoPaymentError.value = "";

  const digits = String(demoCard.value.number || "").replace(/\D/g, "");
  const expiry = String(demoCard.value.expiry || "").trim();
  const cvv = String(demoCard.value.cvv || "").trim();
  const holder = String(demoCard.value.holder || "").trim();

  if (digits.length !== 16) {
    demoPaymentError.value = "請輸入 16 碼信用卡號";
    return;
  }

  if (!/^\d{2}\/\d{2}$/.test(expiry)) {
    demoPaymentError.value = "有效期限格式需為 MM/YY";
    return;
  }

  if (!/^\d{3}$/.test(cvv)) {
    demoPaymentError.value = "安全碼需為 3 碼";
    return;
  }

  if (!holder) {
    demoPaymentError.value = "請輸入持卡人姓名";
    return;
  }

  demoPaymentStep.value = "verify";
}

async function submitDemo3DS() {
  demoPaymentError.value = "";

  if (String(demoCard.value.otp || "").trim() !== "1234") {
    demoPaymentError.value = "驗證碼為 1234";
    return;
  }

  await completeDemoPayment();
}

async function completeDemoPayment() {
  const rental = demoPaymentRental.value;

  if (!rental) {
    demoPaymentError.value = "找不到待付款租借";
    return;
  }

  demoPaymentBusy.value = true;
  demoPaymentError.value = "";

  try {
    const response = await fetch(
      `/api/rental-payments/rentals/${rental.rentalId}/stage-demo-paid`,
      {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token.value}`,
        },
      },
    );

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      throw new Error(data?.message || "付款狀態更新失敗");
    }

    await loadData();
    demoPaymentStep.value = "success";
    errorMessage.value = "";
    message.value = `${demoMethodLabel(demoPaymentMethod.value)} 付款完成`;
  } catch (error) {
    demoPaymentError.value =
      error?.message || "付款狀態更新失敗";
  } finally {
    demoPaymentBusy.value = false;
  }
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
    message.value = "";
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

    errorMessage.value = "";
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
    PENDING: "待付款",
    CONFIRMED: "已付款",
    "待確認": "待付款",
    "已確認": "已付款",
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
    <div
      v-if="message || errorMessage"
      class="form-submit-notices"
    >
      <p
        v-if="message && !errorMessage"
        class="notice success form-submit-success"
      >
        {{ message }}
      </p>

      <p
        v-if="errorMessage"
        class="notice error form-submit-error"
      >
        {{ errorMessage }}
      </p>
    </div>
    </section>

    <section class="card">
      <!-- 僅供綠界 Stage 測試使用，不是真實信用卡資料。 -->
      <div class="ecpay-stage-test-guide">
        <div class="stage-test-title">
          <strong>綠界 AIO 多元付款</strong>
          <span>付款流程展示</span>
        </div>

        <div class="stage-test-grid">
          <div>
            <span>信用卡號</span>
            <code>4311 9522 2222 2222</code>
          </div>

          <div>
            <span>有效期限</span>
            <code>12 / 27</code>
          </div>

          <div>
            <span>CVV</span>
            <code>222</code>
          </div>

          <div>
            <span>持卡人姓名</span>
            <code>TEST USER</code>
          </div>

          <div>
            <span>手機號碼</span>
            <code>0911222333</code>
          </div>

          <div>
            <span>電子信箱</span>
            <code>test@example.com</code>
          </div>

          <div>
            <span>3D 驗證碼</span>
            <code>1234</code>
          </div>
        </div>

        <p class="stage-test-note">
          ※ 此區僅供 ECPay Stage 測試，不是真實信用卡資料。
        </p>
      </div>
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
                <!-- 本機 Tampermonkey 會辨識此 class，只有此按鈕啟用自動填表。 -->
                <button
                  v-if="payments[rental.rentalId]?.paymentStatus === '待付款' && ['PENDING', 'CONFIRMED', '待確認', '已確認'].includes(rental.rentalStatus) && String(rental.rentalDate).slice(0,10) >= today()"
                  type="button"
                  class="stage-test-pay-button"
                  :disabled="loading"
                  @click="demoPay(rental)"
                >
                  多元付款
                </button> <!-- 僅本人有效且待付款的租借可以結帳。 -->
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
          <div
      v-if="demoPaymentOpen"
      class="demo-payment-overlay"
      role="dialog"
      aria-modal="true"
      aria-label="綠界多元付款"
      @click.self="closeDemoPayment"
    >
      <section class="demo-payment-modal">
        <header class="demo-payment-brand">
          <div>
            <p class="demo-payment-kicker">ECPAY AIO PAYMENT</p>
            <h2>選擇付款方式</h2>
          </div>

        </header>

        <p class="demo-payment-note">
          支援多種付款方式，請選擇適合的付款管道。
        </p>

        <div v-if="demoPaymentRental" class="demo-order-summary">
          <div>
            <span>場地</span>
            <strong>{{ venueName(demoPaymentRental.venueId) }}</strong>
          </div>
          <div>
            <span>租借日期</span>
            <strong>{{ formatDateTime(demoPaymentRental.rentalDate) }}</strong>
          </div>
          <div>
            <span>付款金額</span>
            <strong class="demo-total">
              {{ money(payments[demoPaymentRental.rentalId]?.totalPrice) }}
            </strong>
          </div>
        </div>

        <div v-if="demoPaymentStep === 'methods'">
          <div class="demo-method-grid">
            <button
              v-for="method in demoPaymentMethods"
              :key="method.code"
              type="button"
              class="demo-method-card"
              @click="selectDemoMethod(method.code)"
            >
              <span class="demo-method-icon">
                <svg
                  v-if="method.code === 'ApplePay'"
                  class="apple-logo-svg apple-logo-method"
                  viewBox="0 0 64 64"
                  aria-hidden="true"
                >
                  <path
                    d="M39.5 8c-3.3.2-7.2 2.2-9.2 4.7-1.8 2.2-3.2 5.5-2.5 8.6 3.6.3 7.2-1.8 9.1-4.2 1.8-2.3 3.1-5.5 2.6-9.1ZM47.7 34.1c-.1-7.7 6.3-11.4 6.6-11.6-3.6-5.2-9.2-5.9-11.2-6-4.8-.5-9.4 2.8-11.8 2.8-2.5 0-6.3-2.7-10.3-2.6-5.3.1-10.2 3.1-12.9 7.8-5.5 9.5-1.4 23.6 3.9 31.3 2.6 3.8 5.7 8 9.8 7.8 3.9-.2 5.4-2.5 10.2-2.5 4.7 0 6.1 2.5 10.2 2.4 4.2-.1 6.9-3.8 9.5-7.6 3-4.4 4.2-8.7 4.3-8.9-.1 0-8.2-3.1-8.3-12.9Z"
                  />
                </svg>

                <template v-else>
                  {{ method.icon }}
                </template>
              </span>
              <span class="demo-method-copy">
                <strong>{{ method.title }}</strong>
                <small>{{ method.note }}</small>
              </span>
              <span class="demo-method-arrow">›</span>
            </button>
          </div>

          <div class="demo-credit-features">
            <strong>信用卡延伸功能：</strong>
            <span>一次付清</span>
            <span>3 / 6 / 12 / 18 / 24 / 30 期</span>

            <span>信用卡付款驗證</span>
            <span>定期定額（API 能力展示）</span>
          </div>

          <div class="demo-payment-actions">
            <button type="button" class="secondary" @click="closeDemoPayment">
              返回
            </button>
          </div>
        </div>

        <div v-else-if="demoPaymentStep === 'credit'" class="demo-payment-step">
          <div class="demo-step-top">
            <button type="button" class="demo-back-link" @click="demoPaymentStep = 'methods'">
              ← 付款方式
            </button>
            <strong>信用卡</strong>
          </div>
          <div class="demo-brand-strip">
            <span class="demo-brand-badge is-visa">VISA</span>
            <span class="demo-brand-badge is-master">MasterCard</span>
            <span class="demo-brand-badge is-jcb">JCB</span>

          </div>

          <div class="demo-segmented">
            <button
              type="button"
              class="demo-credit-mode-button"
              :class="{ active: demoCreditMode === 'single' }"
              @click="demoCreditMode = 'single'"
            >
              一次付清
            </button>
            <button
              type="button"
              class="demo-credit-mode-button"
              :class="{ active: demoCreditMode === 'installment' }"
              @click="demoCreditMode = 'installment'"
            >
              分期付款
            </button>

          </div>
<div
            v-if="demoCreditMode === 'installment'"
            class="demo-installment-panel"
          >
            <label class="demo-installment-select">
              分期期數

              <select v-model="demoInstallment">
                <option value="3">3 期</option>
                <option value="6">6 期</option>
                <option value="12">12 期</option>
                <option value="18">18 期</option>
                <option value="24">24 期</option>
                <option value="30">30 期</option>
              </select>
            </label>

            <div class="demo-installment-summary">
              <div>
                <span>付款總額</span>
                <strong>{{ money(demoPaymentAmount()) }}</strong>
              </div>

              <div>
                <span>分期期數</span>
                <strong>{{ demoInstallment }} 期</strong>
              </div>

              <div>
                <span>每期約</span>
                <strong>{{ money(demoInstallmentAmount()) }}</strong>
              </div>
            </div>

            <p class="demo-installment-note">
              分期金額為成果展示估算，實際金額依發卡銀行規則為準。
            </p>
          </div>
<label>
            信用卡號
            <input
              v-model="demoCard.number"
              inputmode="numeric"
              autocomplete="off"
              placeholder="4311 9522 2222 2222"
            />
          </label>

          <div class="demo-card-grid">
            <label>
              有效期限
              <input
                v-model="demoCard.expiry"
                autocomplete="off"
                placeholder="MM/YY"
              />
            </label>

            <label>
              安全碼 CVV
              <input
                v-model="demoCard.cvv"
                type="password"
                inputmode="numeric"
                autocomplete="off"
                maxlength="3"
                placeholder="222"
              />
            </label>
          </div>

          <label>
            持卡人姓名
            <input
              v-model="demoCard.holder"
              autocomplete="off"
              placeholder="WANG SAMPLE"
            />
          </label>

          <p v-if="demoPaymentError" class="demo-payment-error">
            {{ demoPaymentError }}
          </p>

          <div class="demo-payment-actions">
            <button type="button" class="secondary" @click="demoPaymentStep = 'methods'">
              上一步
            </button>
            <button type="button" @click="submitDemoCard">
              下一步：信用卡付款驗證
            </button>
          </div>
        </div>

        <div v-else-if="demoPaymentStep === 'verify'" class="demo-payment-step">
          <div class="demo-step-top">
            <button type="button" class="demo-back-link" @click="demoPaymentStep = 'credit'">
              ← 信用卡資料
            </button>
            <strong>信用卡付款驗證 驗證</strong>
          </div>

          <div class="demo-verify-box">
            <div class="demo-shield">✓</div>
            <div>
              <h3>銀行安全驗證</h3>
              <p>驗證碼：1234</p>
            </div>
          </div>

          <label>
            驗證碼
            <input
              v-model="demoCard.otp"
              inputmode="numeric"
              autocomplete="off"
              maxlength="4"
              placeholder="1234"
            />
          </label>

          <p v-if="demoPaymentError" class="demo-payment-error">
            {{ demoPaymentError }}
          </p>

          <div class="demo-payment-actions">
            <button
              type="button"
              class="secondary"
              :disabled="demoPaymentBusy"
              @click="demoPaymentStep = 'credit'"
            >
              上一步
            </button>
            <button
              type="button"
              :disabled="demoPaymentBusy"
              @click="submitDemo3DS"
            >
              {{ demoPaymentBusy ? "授權中..." : "確認付款" }}
            </button>
          </div>
        </div>        <div v-else-if="demoPaymentStep === 'apple'" class="demo-payment-step">
          <div class="demo-step-top">
            <button
              type="button"
              class="demo-back-link"
              @click="demoPaymentStep = 'methods'"
            >
              ← 付款方式
            </button>

            <strong>Apple Pay</strong>
          </div>

          <div class="demo-qr-showcase apple-pay-showcase">

            <div
              class="demo-apple-code"
              aria-label="Apple Pay 展示付款碼"
            >
              <div class="demo-apple-code-ring ring-one"></div>
              <div class="demo-apple-code-ring ring-two"></div>
              <div class="demo-apple-code-ring ring-three"></div>

              <div class="demo-apple-code-center">
                <svg
                  class="apple-logo-svg apple-logo-center"
                  viewBox="0 0 64 64"
                  aria-label="Apple"
                >
                  <path
                    d="M39.5 8c-3.3.2-7.2 2.2-9.2 4.7-1.8 2.2-3.2 5.5-2.5 8.6 3.6.3 7.2-1.8 9.1-4.2 1.8-2.3 3.1-5.5 2.6-9.1ZM47.7 34.1c-.1-7.7 6.3-11.4 6.6-11.6-3.6-5.2-9.2-5.9-11.2-6-4.8-.5-9.4 2.8-11.8 2.8-2.5 0-6.3-2.7-10.3-2.6-5.3.1-10.2 3.1-12.9 7.8-5.5 9.5-1.4 23.6 3.9 31.3 2.6 3.8 5.7 8 9.8 7.8 3.9-.2 5.4-2.5 10.2-2.5 4.7 0 6.1 2.5 10.2 2.4 4.2-.1 6.9-3.8 9.5-7.6 3-4.4 4.2-8.7 4.3-8.9-.1 0-8.2-3.1-8.3-12.9Z"
                  />
                </svg>

                <span>Pay</span>
              </div>
            </div>

            <div class="provider-logo provider-apple">
              <svg
                class="apple-logo-svg apple-logo-wordmark"
                viewBox="0 0 64 64"
                aria-hidden="true"
              >
                <path
                  d="M39.5 8c-3.3.2-7.2 2.2-9.2 4.7-1.8 2.2-3.2 5.5-2.5 8.6 3.6.3 7.2-1.8 9.1-4.2 1.8-2.3 3.1-5.5 2.6-9.1ZM47.7 34.1c-.1-7.7 6.3-11.4 6.6-11.6-3.6-5.2-9.2-5.9-11.2-6-4.8-.5-9.4 2.8-11.8 2.8-2.5 0-6.3-2.7-10.3-2.6-5.3.1-10.2 3.1-12.9 7.8-5.5 9.5-1.4 23.6 3.9 31.3 2.6 3.8 5.7 8 9.8 7.8 3.9-.2 5.4-2.5 10.2-2.5 4.7 0 6.1 2.5 10.2 2.4 4.2-.1 6.9-3.8 9.5-7.6 3-4.4 4.2-8.7 4.3-8.9-.1 0-8.2-3.1-8.3-12.9Z"
                />
              </svg>

              <span>Pay</span>
            </div>

            <p>
              使用手機掃描付款碼開啟付款
            </p>

            <strong class="demo-qr-amount">
              {{ money(demoPaymentAmount()) }}
            </strong>

          </div>

          <div class="demo-payment-actions">
            <button
              type="button"
              class="secondary"
              @click="demoPaymentStep = 'methods'"
            >
              返回
            </button>

            <button
              type="button"
              class="demo-apple-button"
              :disabled="demoPaymentBusy"
              @click="completeDemoPayment"
            >
              完成 Apple Pay 付款
            </button>
          </div>
        </div>

        <div v-else-if="demoPaymentStep === 'atm'" class="demo-payment-step">
          <div class="demo-step-top">
            <button type="button" class="demo-back-link" @click="demoPaymentStep = 'methods'">
              ← 付款方式
            </button>
            <strong>ATM 虛擬帳號</strong>
          </div>

          <div class="demo-code-panel">
            <span>銀行代碼</span>
            <strong>822</strong>
            <span>虛擬帳號</span>
            <strong>9988 2609 1100 01</strong>
            <span>繳費期限</span>
            <strong>7 天內</strong>
          </div>

          <p class="demo-inline-info">
            真實流程是「取號成功」後等待消費者完成 ATM 轉帳，付款通知屬非同步。
          </p>

          <div class="demo-payment-actions">
            <button type="button" class="secondary" @click="demoPaymentStep = 'methods'">返回</button>
            <button type="button" :disabled="demoPaymentBusy" @click="completeDemoPayment">
               ATM 已轉帳
            </button>
          </div>
        </div>

        <div v-else-if="demoPaymentStep === 'barcode'" class="demo-payment-step">
          <div class="demo-step-top">
            <button type="button" class="demo-back-link" @click="demoPaymentStep = 'methods'">
              ← 付款方式
            </button>
            <strong>超商條碼</strong>
          </div>

          <div class="demo-barcode-sheet">
            <div v-for="code in ['110260911', '8229988260911', '500000000052']" :key="code">
              <div class="demo-bars"></div>
              <code>{{ code }}</code>
            </div>
          </div>

          <p class="demo-inline-info">
            條碼為圖樣，不可拿到超商實際繳費。
          </p>

          <div class="demo-payment-actions">
            <button type="button" class="secondary" @click="demoPaymentStep = 'methods'">返回</button>
            <button type="button" :disabled="demoPaymentBusy" @click="completeDemoPayment">
              條碼已繳費
            </button>
          </div>
        </div>        <div v-else-if="demoPaymentStep === 'webatm'" class="demo-payment-step">
          <div class="demo-step-top">
            <button
              type="button"
              class="demo-back-link"
              @click="demoPaymentStep = 'methods'"
            >
              ← 付款方式
            </button>

            <strong>WebATM</strong>
          </div>

          <label>
            付款銀行

            <select v-model="demoBank">
              <option value="822">822 中國信託</option>
              <option value="013">013 國泰世華</option>
              <option value="012">012 台北富邦</option>
            </select>
          </label>

          <div class="demo-code-panel">
            <span>銀行代碼</span>
            <strong>{{ demoBank }}</strong>

            <span>展示用虛擬帳號</span>
            <strong>8888 {{ demoBank }} 2609 1100 02</strong>

            <span>轉帳金額</span>
            <strong>{{ money(demoPaymentAmount()) }}</strong>

            <span>付款識別碼</span>
            <strong>
              WEBATM-{{ demoPaymentRental ? demoPaymentRental.rentalId : 'ORDER' }}
            </strong>
          </div>

          <div class="demo-bank-screen">
            <span>WebATM 安全付款</span>

            <strong>
              請確認付款資訊後完成銀行授權
            </strong>

            <small>
              上方帳號為成果展示資訊，不連線實際銀行帳戶。
            </small>
          </div>

          <div class="demo-payment-actions">
            <button
              type="button"
              class="secondary"
              @click="demoPaymentStep = 'methods'"
            >
              返回
            </button>

            <button
              type="button"
              :disabled="demoPaymentBusy"
              @click="completeDemoPayment"
            >
              完成 WebATM 付款
            </button>
          </div>
        </div>        <div v-else-if="demoPaymentStep === 'digital'" class="demo-payment-step">

          <div class="demo-step-top">
            <button
              type="button"
              class="demo-back-link"
              @click="demoPaymentStep = 'methods'"
            >
              ← 付款方式
            </button>

            <strong>電子支付</strong>
          </div>

          <div class="demo-wallet-options demo-wallet-options-three">

            <button
              type="button"
              :class="{ active: demoWallet === 'Jkopay' }"
              @click="demoWallet = 'Jkopay'"
            >
              街口支付 Jkopay
            </button>

            <button
              type="button"
              :class="{ active: demoWallet === 'iPASS MONEY' }"
              @click="demoWallet = 'iPASS MONEY'"
            >
              iPASS MONEY
            </button>

            <button
              type="button"
              :class="{ active: demoWallet === 'LINE Pay' }"
              @click="demoWallet = 'LINE Pay'"
            >
              LINE Pay
            </button>

          </div>

          <div
            class="demo-qr-showcase digital-payment-showcase"
            :class="{ 'linepay-showcase': demoWallet === 'LINE Pay' }"
          >

            <!-- 街口支付 Logo -->
            <div
              v-if="demoWallet === 'Jkopay'"
              class="provider-logo provider-jkopay"
            >
              <div class="jkopay-main">
                <span class="jkopay-mark">街</span>
                <span>街口支付</span>
              </div>

              <small>JKOPAY</small>
            </div>

            <!-- iPASS MONEY Logo -->
            <div
              v-else-if="demoWallet === 'iPASS MONEY'"
              class="provider-logo provider-ipass"
            >
              <span class="ipass-main">iPASS</span>

              <span class="ipass-money">
                M<span class="ipass-money-dot">●</span>NEY
              </span>
            </div>

            <!-- LINE Pay Logo -->
            <div
              v-else
              class="provider-logo provider-linepay"
            >
              <span class="linepay-line">LINE</span>
              <span class="linepay-pay">Pay</span>
            </div>

            <!-- 三種品牌各自產生不同展示 QR -->
            <div
              class="demo-faux-qr provider-payment-qr"
              :class="{
                'jkopay-qr': demoWallet === 'Jkopay',
                'ipass-qr': demoWallet === 'iPASS MONEY',
                'linepay-qr': demoWallet === 'LINE Pay'
              }"
              aria-label="電子支付展示 QR Code"
            >

              <span
                v-for="(cell, index) in demoQrCells(
                  demoWallet === 'Jkopay'
                    ? 'JKOPAY-DEMO'
                    : demoWallet === 'iPASS MONEY'
                      ? 'IPASS-MONEY-DEMO'
                      : 'LINE-PAY-DEMO'
                )"
                :key="index"
                class="demo-qr-cell"
                :class="{ dark: cell }"
              ></span>

              <div
                v-if="demoWallet === 'Jkopay'"
                class="qr-center-logo qr-center-jkopay"
              >
                街
              </div>

              <div
                v-else-if="demoWallet === 'iPASS MONEY'"
                class="qr-center-logo qr-center-ipass"
              >
                iPASS
              </div>

              <div
                v-else
                class="qr-center-logo qr-center-linepay"
              >
                Pay
              </div>

            </div>

            <strong class="provider-payment-name">
              {{
                demoWallet === 'Jkopay'
                  ? '街口支付 Jkopay'
                  : demoWallet === 'iPASS MONEY'
                    ? 'iPASS MONEY'
                    : 'LINE Pay'
              }}
            </strong>

            <p>掃描 QR Code 完成付款</p>

            <strong class="demo-qr-amount">
              {{ money(demoPaymentAmount()) }}
            </strong>

          </div>

          <div class="demo-payment-actions">

            <button
              type="button"
              class="secondary"
              @click="demoPaymentStep = 'methods'"
            >
              返回
            </button>

            <button
              type="button"
              :disabled="demoPaymentBusy"
              @click="completeDemoPayment"
            >
              使用 {{ demoWallet }} 付款
            </button>

          </div>

        </div>

        <div v-else class="demo-payment-success">
          <div class="demo-success-icon">✓</div>
          <p class="demo-payment-kicker">PAYMENT COMPLETE</p>
          <h2>付款成功</h2>
          <p>
            使用 {{ demoMethodLabel(demoPaymentMethod) }} 完成付款。
          </p>
          <div class="demo-success-summary">
            <span>付款方式</span>
            <strong>{{ demoMethodLabel(demoPaymentMethod) }}</strong>
            <span>付款金額</span>
            <strong>{{ money(demoPaymentAmount()) }}</strong>
            <span>訂單狀態</span>
            <strong>已付款</strong>
          </div>
          <button type="button" @click="closeDemoPayment">
            返回預約紀錄
          </button>
        </div>

        <p v-if="demoPaymentError && !['credit', 'verify'].includes(demoPaymentStep)" class="demo-payment-error">
          {{ demoPaymentError }}
        </p>
      </section>
    </div>
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

/* ===== 會員中心場地租借專用寬版 ===== */

/* 只放寬目前的場地租借頁，不修改共用 MemberLayout.vue。 */
:global(.member-container:has(.rental-page)) {
  width: 100% !important;
  max-width: none !important;
  padding-left: 32px !important;
  padding-right: 32px !important;
  box-sizing: border-box;
}

@media (max-width: 860px) {
  :global(.member-container:has(.rental-page)) {
    padding-left: 16px !important;
    padding-right: 16px !important;
  }
}

/* ===== ECPay Stage 測試資料 ===== */

.ecpay-stage-test-guide {
  margin: 0 0 18px;
  padding: 16px 18px;
  border: 1px dashed #c59a53;
  border-radius: 12px;
  background: #fffaf0;
}

.stage-test-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  margin-bottom: 12px;
  color: #6b4e24;
}

.stage-test-title span {
  padding: 3px 9px;
  border-radius: 999px;
  background: #f3dfb7;
  color: #74531f;
  font-size: 12px;
  font-weight: 700;
}

.stage-test-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 10px;
}

.stage-test-grid > div {
  display: flex;
  flex-direction: column;
  gap: 5px;
  padding: 10px 12px;
  border: 1px solid #eadcc6;
  border-radius: 9px;
  background: #ffffff;
}

.stage-test-grid span {
  color: #756656;
  font-size: 12px;
}

.stage-test-grid code {
  color: #49351c;
  font-family: Consolas, monospace;
  font-size: 14px;
  font-weight: 700;
  user-select: all;
}

.stage-test-note {
  margin: 10px 0 0;
  color: #8b6b3c;
  font-size: 12px;
}

/* ECPay Stage 本機快速測試按鈕。 */
.stage-test-pay-button {
  margin-left: 6px;
  background: #28785a;
  color: #ffffff;
}

.stage-test-pay-button:hover:not(:disabled) {
  background: #1f6047;
}

@media (max-width: 900px) {
  .stage-test-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 560px) {
  .stage-test-grid {
    grid-template-columns: 1fr;
  }
}

/* 測試付款按鈕固定使用綠色，與正式付款功能清楚區分。 */
.stage-test-pay-button {
  margin: 4px 4px 4px 6px !important;
  background: #28785a !important;
  color: #ffffff !important;
  border: 0;
}

.stage-test-pay-button:hover:not(:disabled) {
  background: #1f6047 !important;
}

/* Stage 測試資料區塊預設隱藏；測試資料改由本機 Tampermonkey 自動帶入。 */
.ecpay-stage-test-guide {
  display: none !important;
}


/* ===== ECPay AIO presentation demo ===== */
.demo-payment-overlay {
  position: fixed;
  inset: 0;
  z-index: 3000;
  display: grid;
  place-items: center;
  padding: 20px;
  background: rgba(28, 22, 17, 0.62);
  backdrop-filter: blur(5px);
}

.demo-payment-modal {
  width: min(720px, 100%);
  max-height: calc(100vh - 40px);
  overflow-y: auto;
  padding: 26px;
  border: 1px solid #e2d7c7;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 28px 80px rgba(30, 23, 17, 0.32);
}

.demo-payment-brand {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
}

.demo-payment-brand h2,
.demo-payment-success h2 {
  margin: 3px 0 0;
}

.demo-payment-kicker {
  margin: 0;
  color: #9b7435;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 1.35px;
}

.demo-mode-badge {
  flex: 0 0 auto;
  padding: 6px 10px;
  border-radius: 999px;
  background: #fff3d8;
  color: #8b611b;
  font-size: 12px;
  font-weight: 800;
}

.demo-payment-note,
.demo-inline-info {
  padding: 10px 12px;
  border-radius: 9px;
  background: #f8f4ed;
  color: #65584b;
  font-size: 13px;
  line-height: 1.55;
}

.demo-payment-note {
  margin: 14px 0;
}

.demo-inline-info {
  margin: 0;
}

.demo-order-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
  margin: 14px 0 18px;
  padding: 14px;
  border: 1px solid #ece4d8;
  border-radius: 12px;
  background: #fff;
}

.demo-order-summary div {
  display: grid;
  gap: 4px;
}

.demo-order-summary span,
.demo-code-panel span,
.demo-bnpl-plan span,
.demo-success-summary span {
  color: #817367;
  font-size: 12px;
}

.demo-order-summary strong {
  color: #44372b;
  font-size: 13px;
  overflow-wrap: anywhere;
}

.demo-order-summary .demo-total {
  color: #9b2f2f;
  font-size: 17px;
}

.demo-method-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.demo-method-card {
  display: grid;
  grid-template-columns: 38px 1fr auto;
  align-items: center;
  gap: 10px;
  min-height: 72px;
  padding: 12px 14px;
  border: 1px solid #e8dfd4;
  background: #fff;
  color: #493c30;
  text-align: left;
  box-shadow: none;
}

.demo-method-card:hover {
  border-color: #b58a46;
  background: #fffaf2;
}

.demo-method-icon {
  display: grid;
  place-items: center;
  width: 36px;
  height: 36px;
  border-radius: 10px;
  background: #f3ede4;
  font-size: 20px;
}

.demo-method-copy {
  display: grid;
  gap: 3px;
}

.demo-method-copy strong {
  font-size: 14px;
}

.demo-method-copy small {
  color: #7f7164;
  font-weight: 500;
  line-height: 1.35;
}

.demo-method-arrow {
  color: #a58b6a;
  font-size: 24px;
}

.demo-credit-features {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin: 14px 0 0;
  padding: 10px 12px;
  border-radius: 9px;
  background: #f7f2ea;
  color: #645648;
  font-size: 12px;
}

.demo-credit-features span {
  padding: 3px 7px;
  border-radius: 999px;
  background: #fff;
  border: 1px solid #e8dfd4;
}

.demo-payment-step {
  display: grid;
  gap: 14px;
}

.demo-step-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.demo-back-link {
  padding: 7px 9px;
  background: transparent;
  color: #775f45;
}

.demo-back-link:hover {
  background: #f6f0e8;
}

.demo-segmented,
.demo-wallet-options {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 6px;
}

.demo-segmented button,
.demo-wallet-options button {
  padding: 9px;
  border: 1px solid #dfd5c7;
  background: #f8f5f0;
  color: #65584b;
}

.demo-segmented button.active,
.demo-wallet-options button.active {
  border-color: #28785a;
  background: #eaf6ef;
  color: #236449;
}

.demo-card-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
}

.demo-payment-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 4px;
}

.demo-payment-error {
  margin: 10px 0 0;
  padding: 9px 11px;
  border-radius: 8px;
  background: #fff0f0;
  color: #a12626;
  font-size: 13px;
  font-weight: 700;
}

.demo-verify-box,
.demo-wallet-panel,
.demo-bank-screen {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 16px;
  border-radius: 12px;
  background: #eef8f2;
}

.demo-verify-box h3,
.demo-verify-box p,
.demo-wallet-panel p {
  margin: 0;
}

.demo-verify-box p,
.demo-wallet-panel p,
.demo-bank-screen small {
  color: #557064;
  font-size: 13px;
}

.demo-shield,
.demo-success-icon {
  display: grid;
  place-items: center;
  flex: 0 0 auto;
  border-radius: 50%;
  background: #28785a;
  color: #fff;
  font-weight: 900;
}

.demo-shield {
  width: 42px;
  height: 42px;
}

.demo-success-icon {
  width: 64px;
  height: 64px;
  font-size: 30px;
}

.demo-wallet-panel {
  display: grid;
  justify-items: center;
  padding: 26px;
  text-align: center;
}

.demo-wallet-panel.apple {
  background: #f3f3f3;
  color: #111;
}

.demo-wallet-logo {
  font-size: 30px;
  font-weight: 800;
}

.demo-apple-button {
  min-width: 140px;
  background: #000;
  color: #fff;
}

.demo-code-panel,
.demo-bnpl-plan,
.demo-success-summary {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 10px 18px;
  padding: 18px;
  border: 1px solid #e7ded2;
  border-radius: 12px;
  background: #fbfaf8;
}

.demo-code-panel.centered {
  grid-template-columns: 1fr;
  justify-items: center;
  text-align: center;
}

.demo-big-code {
  color: #2e5e4a;
  font-size: 20px;
  letter-spacing: 1px;
}

.demo-barcode-sheet {
  display: grid;
  gap: 14px;
  padding: 18px;
  border: 1px solid #ddd;
  border-radius: 12px;
  background: #fff;
}

.demo-barcode-sheet > div {
  display: grid;
  gap: 5px;
  justify-items: center;
}

.demo-bars {
  width: min(420px, 100%);
  height: 52px;
  background:
    repeating-linear-gradient(
      90deg,
      #111 0 2px,
      #fff 2px 4px,
      #111 4px 5px,
      #fff 5px 8px,
      #111 8px 12px,
      #fff 12px 14px
    );
}

.demo-bank-screen {
  display: grid;
  gap: 6px;
  justify-items: center;
  padding: 26px;
  text-align: center;
}

.demo-bank-screen strong {
  font-size: 18px;
}

.demo-qr-wrap {
  display: grid;
  justify-items: center;
  gap: 10px;
  padding: 20px;
  border: 1px solid #e3dbd0;
  border-radius: 12px;
  background: #fff;
  text-align: center;
}

.demo-qr {
  display: grid;
  place-items: center;
  width: 180px;
  aspect-ratio: 1;
  border: 12px solid #fff;
  outline: 1px solid #ddd;
  color: #fff;
  font-weight: 900;
  letter-spacing: 1px;
  background:
    linear-gradient(90deg, #111 25%, transparent 25% 50%, #111 50% 75%, transparent 75%),
    linear-gradient(#111 25%, transparent 25% 50%, #111 50% 75%, transparent 75%);
  background-size: 24px 24px;
  text-shadow: 0 1px 3px #000;
}

.demo-qr-wx {
  background-color: #19a85b;
}

.demo-qr-tw {
  background-color: #184f8b;
}

.demo-payment-success {
  display: grid;
  justify-items: center;
  gap: 14px;
  padding: 8px 0 2px;
  text-align: center;
}

.demo-payment-success > p {
  margin: 0;
  color: #5f5145;
}

.demo-success-summary {
  width: min(430px, 100%);
  text-align: left;
}

@media (max-width: 720px) {
  .demo-payment-modal {
    padding: 18px;
  }

  .demo-method-grid,
  .demo-order-summary,
  .demo-card-grid {
    grid-template-columns: 1fr;
  }

  .demo-payment-brand {
    flex-direction: column;
  }

  .demo-segmented,
  .demo-wallet-options {
    grid-template-columns: 1fr;
  }
}
/* ===== Rental form warning placement ===== */
.form-submit-notices {
  display: grid;
  gap: 8px;
  width: 100%;
  margin-top: 12px;
  box-sizing: border-box;
}

.form-submit-success,
.form-submit-error {
  width: 100%;
  margin: 0;
  box-sizing: border-box;
}

/* 成果展示時隱藏舊 Stage 資料卡。 */
.ecpay-stage-test-guide {
  display: none !important;
}

/* QR Code 風格成果展示。 */
.demo-qr-showcase {
  display: grid;
  justify-items: center;
  gap: 10px;
  padding: 24px 18px;
  border: 1px solid #e7ded2;
  border-radius: 16px;
  background: #fbfaf8;
  text-align: center;
}

.demo-qr-showcase p {
  margin: 0;
  color: #6d6054;
  font-size: 13px;
}

.demo-qr-amount {
  color: #40352b;
  font-size: 20px;
}

.demo-faux-qr {
  position: relative;
  display: grid;
  place-items: center;
  width: 176px;
  height: 176px;
  padding: 12px;
  overflow: hidden;
  border: 10px solid #fff;
  border-radius: 14px;
  background:
    repeating-conic-gradient(
      #2f2924 0 25%,
      #ffffff 0 50%
    )
    0 0 / 18px 18px;
  box-shadow:
    0 0 0 1px #ddd2c4,
    0 10px 30px rgba(52, 42, 33, 0.12);
}

.demo-faux-qr::before,
.demo-faux-qr::after {
  content: "";
  position: absolute;
  width: 42px;
  height: 42px;
  border: 8px solid #2f2924;
  background: #fff;
}

.demo-faux-qr::before {
  top: 12px;
  left: 12px;
}

.demo-faux-qr::after {
  top: 12px;
  right: 12px;
}

.demo-faux-qr span {
  position: relative;
  z-index: 2;
  display: grid;
  place-items: center;
  min-width: 62px;
  min-height: 48px;
  padding: 7px 10px;
  border-radius: 10px;
  background: #fff;
  color: #2f2924;
  font-size: 18px;
  font-weight: 900;
  box-shadow: 0 2px 12px rgba(48, 38, 30, 0.18);
}

.apple-qr span {
  font-size: 29px;
}

.jkopay-qr span {
  font-size: 17px;
}

.ipass-qr span {
  font-size: 15px;
}

@media (max-width: 520px) {
  .demo-faux-qr {
    width: 148px;
    height: 148px;
  }
}
/* ===== QR Matrix 成果展示 ===== */
.demo-faux-qr {
  display: grid;
  grid-template-columns: repeat(29, 1fr);
  grid-template-rows: repeat(29, 1fr);
  gap: 0;

  width: 184px;
  height: 184px;

  padding: 12px;

  overflow: hidden;

  border: 1px solid #d8cdbf;
  border-radius: 14px;

  background: #fff;

  box-shadow:
    0 10px 28px rgba(55, 43, 32, 0.12);
}

.demo-faux-qr::before,
.demo-faux-qr::after {
  display: none !important;
}

.demo-faux-qr .demo-qr-cell {
  display: block;

  width: 100%;
  min-width: 0;
  height: 100%;
  min-height: 0;

  padding: 0;
  margin: 0;

  border: 0;
  border-radius: 0;

  background: transparent;

  box-shadow: none;
}

.demo-faux-qr .demo-qr-cell.dark {
  background: #292521;
}

.apple-qr,
.jkopay-qr,
.ipass-qr {
  background-color: #fff;
}

@media (max-width: 520px) {
  .demo-faux-qr {
    width: 158px;
    height: 158px;
    padding: 10px;
  }
}

/* ===== 品牌字樣 / Wordmark ===== */
.demo-brand-strip {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin: 10px 0 14px;
}

.demo-brand-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 32px;
  padding: 0 12px;
  border-radius: 999px;
  border: 1px solid #d8ccbd;
  background: #fff;
  color: #44372b;
  font-size: 13px;
  font-weight: 800;
  letter-spacing: 0.2px;
}

.demo-brand-badge.is-visa {
  color: #173f8a;
}

.demo-brand-badge.is-master {
  color: #9a3d1b;
}

.demo-brand-badge.is-jcb {
  color: #0b6f57;
}

.demo-brand-badge.is-union {
  color: #b23c30;
}

.demo-brand-wordmark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-height: 36px;
  padding: 0 14px;
  border-radius: 999px;
  background: #fff;
  border: 1px solid #ddd2c4;
  color: #3f342a;
  font-size: 18px;
  font-weight: 900;
  letter-spacing: 0.3px;
  box-shadow: 0 2px 8px rgba(48, 38, 30, 0.08);
}

.apple-pay-wordmark {
  font-size: 20px;
}

.jkopay-wordmark {
  color: #0f7b58;
}

.ipass-wordmark {
  color: #15704f;
}

@media (max-width: 520px) {
  .demo-brand-wordmark {
    font-size: 16px;
    min-height: 34px;
    padding: 0 12px;
  }
}

/* ==========================================================
   Provider branded payment display
   ========================================================== */

.provider-logo {
  display: flex;
  align-items: center;
  justify-content: center;
}

/* ---------------- Apple Pay ---------------- */

.apple-pay-showcase {
  gap: 14px;
}

.demo-apple-code {
  position: relative;
  width: 190px;
  height: 190px;
  flex: 0 0 auto;
  border-radius: 50%;
  overflow: hidden;
  background: #fff;
  box-shadow:
    0 0 0 1px #e1dbd4,
    0 12px 32px rgba(32, 28, 24, 0.12);
}

.demo-apple-code::before {
  content: "";
  position: absolute;
  inset: 10px;
  border-radius: 50%;

  background:
    repeating-conic-gradient(
      from 3deg,
      #111 0deg 8deg,
      transparent 8deg 14deg,
      #999 14deg 21deg,
      transparent 21deg 29deg
    );

  -webkit-mask:
    repeating-radial-gradient(
      circle at center,
      transparent 0 12px,
      #000 12px 18px,
      transparent 18px 25px,
      #000 25px 31px,
      transparent 31px 39px,
      #000 39px 46px,
      transparent 46px 54px,
      #000 54px 61px,
      transparent 61px 69px,
      #000 69px 76px,
      transparent 76px
    );

  mask:
    repeating-radial-gradient(
      circle at center,
      transparent 0 12px,
      #000 12px 18px,
      transparent 18px 25px,
      #000 25px 31px,
      transparent 31px 39px,
      #000 39px 46px,
      transparent 46px 54px,
      #000 54px 61px,
      transparent 61px 69px,
      #000 69px 76px,
      transparent 76px
    );
}

.demo-apple-code-ring {
  position: absolute;
  border-radius: 50%;
  pointer-events: none;
}

.demo-apple-code-ring.ring-one {
  inset: 20px;
  border: 4px dashed rgba(0, 0, 0, 0.18);
}

.demo-apple-code-ring.ring-two {
  inset: 42px;
  border: 3px dashed rgba(0, 0, 0, 0.22);
}

.demo-apple-code-ring.ring-three {
  inset: 65px;
  border: 3px dashed rgba(0, 0, 0, 0.18);
}

.demo-apple-code-center {
  position: absolute;
  z-index: 4;
  top: 50%;
  left: 50%;

  display: flex;
  align-items: center;
  justify-content: center;
  gap: 3px;

  width: 72px;
  height: 72px;

  transform: translate(-50%, -50%);

  border-radius: 50%;

  background: #050505;
  color: #fff;

  font-size: 20px;
  font-weight: 800;

  box-shadow:
    0 4px 14px rgba(0, 0, 0, 0.22);
}

.demo-apple-symbol {
  font-size: 23px;
}

.provider-apple {
  gap: 3px;
  color: #111;
  font-size: 25px;
  font-weight: 800;
}

.provider-apple-symbol {
  font-size: 29px;
}

/* ---------------- Jkopay ---------------- */

.provider-jkopay {
  display: grid;
  justify-items: center;
  gap: 2px;

  min-width: 190px;

  padding: 10px 18px;

  border-radius: 12px;

  background: #ef202d;
  color: #fff;

  box-shadow:
    0 5px 16px rgba(239, 32, 45, 0.18);
}

.jkopay-main {
  display: flex;
  align-items: center;
  gap: 7px;

  font-size: 23px;
  font-weight: 900;
}

.jkopay-mark {
  display: inline-grid;
  place-items: center;

  width: 31px;
  height: 31px;

  border: 3px solid #fff;
  border-radius: 8px;

  font-size: 19px;
  line-height: 1;
}

.provider-jkopay small {
  color: #fff;
  font-size: 11px;
  font-weight: 800;
  letter-spacing: 5px;
}

/* ---------------- iPASS MONEY ---------------- */

.provider-ipass {
  display: grid;
  justify-items: center;
  gap: 0;
}

.ipass-main {
  display: inline-flex;
  align-items: center;

  padding: 4px 13px 5px;

  border-radius: 2px 2px 15px 2px;

  background: #2bc72f;
  color: #fff;

  font-size: 31px;
  font-weight: 700;
  line-height: 1;
}

.ipass-money {
  margin-top: 2px;

  color: #050505;

  font-size: 28px;
  font-weight: 900;
  letter-spacing: 1px;
  line-height: 1;
}

.ipass-money-dot {
  display: inline-block;
  margin: 0 1px;

  color: #2bbf75;
  font-size: 18px;
}

/* ---------------- QR ---------------- */

.provider-payment-qr {
  position: relative;
}

.qr-center-logo {
  position: absolute;
  z-index: 20;

  top: 50%;
  left: 50%;

  display: grid;
  place-items: center;

  transform: translate(-50%, -50%);

  background: #fff;

  box-shadow:
    0 0 0 4px #fff,
    0 2px 7px rgba(0, 0, 0, 0.15);
}

.qr-center-jkopay {
  width: 36px;
  height: 36px;

  border-radius: 8px;

  background: #ef202d;
  color: #fff;

  font-size: 22px;
  font-weight: 900;
}

.qr-center-ipass {
  min-width: 50px;
  height: 29px;

  padding: 0 5px;

  border-radius: 5px;

  color: #1aa93a;

  font-size: 12px;
  font-weight: 900;
}

.provider-payment-name {
  color: #3c342d;
  font-size: 16px;
  font-weight: 800;
}

.digital-payment-showcase {
  gap: 11px;
}

/*
 * 中央品牌標誌要浮在 QR Matrix 上，
 * 因此不能套用 QR cell 的尺寸規則。
 */
.demo-faux-qr > .qr-center-logo {
  width: auto;
  min-width: 36px;
}

/* 手機 */
@media (max-width: 520px) {

  .demo-apple-code {
    width: 164px;
    height: 164px;
  }

  .demo-apple-code-center {
    width: 64px;
    height: 64px;
  }

  .provider-jkopay {
    min-width: 165px;
  }

  .jkopay-main {
    font-size: 20px;
  }

  .ipass-main {
    font-size: 27px;
  }

  .ipass-money {
    font-size: 24px;
  }
}

/* ===== Credit Installment V2 ===== */

/*
 * 銀聯移除後，同一列自動改成兩欄。
 * :has() 在目前 Chromium / Edge / Chrome 皆支援。
 */
.demo-payment-step *:has(> .demo-credit-mode-button) {
  grid-template-columns: repeat(2, minmax(0, 1fr)) !important;
}

.demo-credit-mode-button {
  width: 100%;
  min-height: 40px;

  border: 1px solid #dfd3c4;
  border-radius: 9px;

  background: #faf7f2;
  color: #5d5147;

  cursor: pointer;

  transition:
    background 0.15s ease,
    border-color 0.15s ease,
    color 0.15s ease;
}

.demo-credit-mode-button:hover {
  border-color: #af9366;
}

.demo-credit-mode-button.active {
  border-color: #178565;
  background: #e7f5ef;
  color: #126b53;
  font-weight: 700;
}

.demo-installment-panel {
  display: grid;
  gap: 14px;

  margin: 14px 0 18px;
  padding: 16px;

  border: 1px solid #e2d6c7;
  border-radius: 12px;

  background: #fbf8f3;
}

.demo-installment-select {
  display: grid;
  gap: 7px;

  color: #55493f;
  font-size: 13px;
  font-weight: 700;
}

.demo-installment-select select {
  width: 100%;
  min-height: 42px;

  padding: 8px 11px;

  border: 1px solid #d4c5b4;
  border-radius: 8px;

  background: #fff;
  color: #40362e;
}

.demo-installment-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 10px;
}

.demo-installment-summary > div {
  display: grid;
  gap: 4px;

  padding: 11px;

  border: 1px solid #e9ded2;
  border-radius: 9px;

  background: #fff;
}

.demo-installment-summary span {
  color: #817367;
  font-size: 12px;
}

.demo-installment-summary strong {
  color: #3f342b;
  font-size: 15px;
}

.demo-installment-note {
  margin: 0;

  color: #77695e;
  font-size: 12px;
  line-height: 1.6;
}

@media (max-width: 620px) {
  .demo-installment-summary {
    grid-template-columns: 1fr;
  }
}

/* ===== Apple Pay SVG Logo ===== */

.apple-logo-svg {
  display: inline-block;
  flex: 0 0 auto;

  width: 24px;
  height: 28px;

  fill: currentColor;

  vertical-align: middle;
}

.apple-logo-method {
  width: 27px;
  height: 31px;

  color: #111;
}

.demo-apple-code-center .apple-logo-center {
  width: 25px;
  height: 29px;

  color: #fff;
}

.provider-apple {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 4px;

  color: #111;
  font-size: 25px;
  font-weight: 800;
}

.provider-apple .apple-logo-wordmark {
  width: 27px;
  height: 31px;

  color: #111;
}

/* 避免既有 Apple 私用字型 CSS 影響 SVG */
.demo-apple-symbol,
.provider-apple-symbol {
  font-family: inherit;
}

/* ===== LINE Pay V2 ===== */

.demo-wallet-options-three {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 8px;
}

.demo-wallet-options-three button {
  min-width: 0;
}

.provider-linepay {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 7px;

  min-width: 190px;

  padding: 10px 20px;

  border-radius: 10px;

  background: #06c755;
  color: #fff;

  font-size: 27px;
  font-weight: 900;

  box-shadow:
    0 6px 18px rgba(6, 199, 85, 0.2);
}

.linepay-line {
  font-weight: 900;
  letter-spacing: -1px;
}

.linepay-pay {
  font-weight: 800;
}

.linepay-qr {
  background-color: #fff;
}

.qr-center-linepay {
  min-width: 48px;
  height: 36px;

  padding: 0 8px;

  border-radius: 6px;

  background: #06c755;
  color: #fff;

  font-size: 18px;
  font-weight: 900;

  box-shadow:
    0 0 0 5px #fff,
    0 2px 8px rgba(0, 0, 0, 0.14);
}

.linepay-showcase {
  border-color: #bdeed0;
}

.linepay-showcase .provider-payment-name {
  color: #079447;
}

@media (max-width: 620px) {

  .demo-wallet-options-three {
    grid-template-columns: 1fr;
  }

  .provider-linepay {
    min-width: 165px;
    font-size: 23px;
  }
}
</style>
