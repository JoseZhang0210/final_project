<script setup>
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";

import {
  createRental,
  getApiErrorMessage,
  getMyRentals,
  getRentals,
  getStoredAuthorities,
  getStoredToken,
  getVenues,
} from "../api/venueRentalApi";

const router = useRouter();

const authorities = getStoredAuthorities();

const isStaffAccount =
  authorities.includes("ROLE_ADMIN") ||
  authorities.includes("ROLE_EMPLOYEE");

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

  // 先取得場地。
  // 場地資料與會員 Rental 資料分開載入，
  // 避免 admin 沒有 member profile 時連場地下拉選單也一起失敗。
  try {
    const venueData =
      await getVenues(token.value);

    venues.value = venueData ?? [];
  } catch (error) {
    venues.value = [];
    errorMessage.value =
      getApiErrorMessage(error);
    loading.value = false;
    return;
  }

  // admin / employee 顯示全部 Rental；
  // 一般會員只顯示自己的 Rental。
  try {
    const rentalData =
      isStaffAccount
        ? await getRentals(token.value)
        : await getMyRentals(token.value);

    rentals.value = rentalData ?? [];
  } catch (error) {
    rentals.value = [];

    // 員工帳號本來就不一定具有 member profile，
    // 不讓此錯誤阻斷 Venue -> Rental 的場地連動。
    if (!isStaffAccount) {
      errorMessage.value =
        getApiErrorMessage(error);
    }
  } finally {
    loading.value = false;
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
      `參加人數不可超過場地可容納人數 ${selectedVenue.value.capacity} 人`;
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
          form.value.rentalDate,
        guestCount,
      },
    );

    message.value =
      `租借建立成功：Rental #${saved.rentalId}，Payment #${saved.paymentId}`;

    resetForm();
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

    <section class="card">
      <h2>新增租借</h2>

      <p
        v-if="isStaffAccount"
        class="hint"
      >
        管理員模式已連動場地與全部租借資料。
        目前前台送出租借仍以會員帳號建立；
        管理員代會員新增將在後台 CRUD 流程處理。
      </p>

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
              ｜可容納 {{ venue.capacity }} 人
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
          <span>租借日期與時間</span>
          <input
            v-model="form.rentalDate"
            type="datetime-local"
            :min="minimumRentalDate"
            :disabled="loading"
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
          可容納人數：
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
          :disabled="loading"
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
          <h2>
            {{
              isStaffAccount
                ? "全部租借"
                : "我的租借"
            }}
          </h2>
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
              <th>租借編號</th>
              <th>場地</th>
              <th>活動</th>
              <th>日期</th>
              <th>人數</th>
              <th>付款編號</th>
              <th>狀態</th>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="rental in rentals"
              :key="rental.rentalId"
            >
              <td>
                {{ rental.rentalId }}
              </td>
              <td>
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
              <td>
                {{ rental.paymentId }}
              </td>
              <td>
                {{
                  rentalStatusLabel(
                    rental.rentalStatus,
                  )
                }}
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
.rental-page {
  width: min(1120px, calc(100% - 32px));
  margin: 32px auto 60px;
  font-family:
    Arial,
    "Microsoft JhengHei",
    sans-serif;
}

.hero,
.card {
  background: #fff;
  border: 1px solid #e6e0d7;
  border-radius: 16px;
  box-shadow: 0 8px 24px
    rgba(62, 48, 35, 0.08);
}

.hero {
  padding: 30px;
  margin-bottom: 20px;
}

.eyebrow {
  color: #9b7435;
  font-weight: 700;
  letter-spacing: 1.5px;
  margin: 0 0 8px;
}

h1,
h2 {
  color: #46392d;
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
  background: #8a642d;
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

.hint {
  color: #75685c;
  line-height: 1.7;
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
</style>