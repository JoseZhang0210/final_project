<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";

import {
  getApiErrorMessage,
  getStoredToken,
  getVenues,
  updateVenue,
} from "../api/venueRentalApi";

const router = useRouter();

const token = ref("");
const venues = ref([]);
const loading = ref(false);

const message = ref("");
const errorMessage = ref("");

const statusDrafts = ref({});

const venueStatuses = [
  "AVAILABLE",
  "MAINTENANCE",
  "DISABLED",
];

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
    const data =
      (await getVenues(token.value)) ?? [];

    venues.value = [...data].sort(
      (a, b) =>
        Number(a.venueId) -
        Number(b.venueId),
    );

    const nextDrafts = {};

    for (const venue of venues.value) {
      nextDrafts[venue.venueId] =
        normalizeStatus(
          venue.venueStatus,
        );
    }

    statusDrafts.value = nextDrafts;
  } catch (error) {
    errorMessage.value =
      getApiErrorMessage(error);
  } finally {
    loading.value = false;
  }
}

async function saveStatus(venue) {
  message.value = "";
  errorMessage.value = "";
  loading.value = true;

  try {
    const venueStatus =
      statusDrafts.value[
        venue.venueId
      ];

    await updateVenue(
      token.value,
      venue.venueId,
      {
        venueId: venue.venueId,
        venueName: venue.venueName,
        capacity: venue.capacity,
        pricePerDay:
          venue.pricePerDay,
        venueStatus,
      },
    );

    message.value =
      `${venue.venueName} 狀態已更新為「${statusLabel(venueStatus)}」`;

    await loadVenues();
  } catch (error) {
    errorMessage.value =
      getApiErrorMessage(error);
  } finally {
    loading.value = false;
  }
}

function normalizeStatus(status) {
  const value =
    String(status || "").trim();

  const aliases = {
    可預約: "AVAILABLE",
    維護中: "MAINTENANCE",
    維修中: "MAINTENANCE",
    停用: "DISABLED",
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

  return (
    labels[normalized] ||
    status
  );
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

      <p class="intro">
        場地固定為 A～D 四廳。
        場地 ID、名稱、可容納人數與每日價格
        由系統鎖定，管理員只需要調整場地狀態。
      </p>
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
      <div class="table-title">
        <div>
          <h2>
            固定場地（{{ venues.length }}）
          </h2>

          <p class="hint">
            可預約 / 維護中 / 停用
          </p>
        </div>

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
              <th>場地 ID</th>
              <th>場地名稱</th>
              <th>可容納人數</th>
              <th>每日價格</th>
              <th>場地狀態</th>
              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="venue in venues"
              :key="venue.venueId"
            >
              <td>{{ venue.venueId }}</td>

              <td>
                <strong>
                  {{ venue.venueName }}
                </strong>
              </td>

              <td>
                {{ venue.capacity }} 人
              </td>

              <td>
                {{ money(venue.pricePerDay) }}
              </td>

              <td>
                <select
                  v-model="
                    statusDrafts[
                      venue.venueId
                    ]
                  "
                  :disabled="loading"
                >
                  <option
                    v-for="status in venueStatuses"
                    :key="status"
                    :value="status"
                  >
                    {{ statusLabel(status) }}
                  </option>
                </select>
              </td>

              <td>
                <button
                  type="button"
                  :disabled="loading"
                  @click="saveStatus(venue)"
                >
                  儲存狀態
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <p
        v-if="
          !loading &&
          venues.length === 0
        "
        class="empty"
      >
        尚未取得固定場地資料。
      </p>
    </section>
  </main>
</template>

<style scoped>
.venue-page {
  width:
    min(
      1120px,
      calc(100% - 32px)
    );
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
  box-shadow:
    0 8px 24px
    rgba(62, 48, 35, 0.08);
}

h1,
h2 {
  color: #46392d;
}

.intro,
.hint {
  color: #75685c;
  line-height: 1.7;
}

.table-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

button {
  border: 0;
  border-radius: 8px;
  padding: 9px 15px;
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

select {
  min-width: 150px;
  padding: 9px 10px;
  border: 1px solid #cfc5b8;
  border-radius: 8px;
  background: #fff;
  font: inherit;
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

.empty {
  color: #777;
  text-align: center;
  padding: 24px 0 4px;
}

@media (max-width: 720px) {
  .table-title {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>