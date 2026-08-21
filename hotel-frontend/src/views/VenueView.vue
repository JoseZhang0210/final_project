<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";

import {
  createVenue,
  deleteVenue,
  getApiErrorMessage,
  getStoredToken,
  getVenues,
  updateVenue,
} from "../api/venueRentalApi";

const router = useRouter();

const token = ref("");
const venues = ref([]);
const loading = ref(false);
const editMode = ref(false);

const message = ref("");
const errorMessage = ref("");

const form = ref({
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

async function handleSubmit() {
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

  if (
    !Number.isInteger(pricePerDay) ||
    pricePerDay < 0
  ) {
    errorMessage.value =
      "每日價格必須是 0 以上整數";
    return;
  }

  const payload = {
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
  } catch (error) {
    errorMessage.value =
      getApiErrorMessage(error);
  } finally {
    loading.value = false;
  }
}

function startEdit(venue) {
  editMode.value = true;

  form.value = {
    venueId: venue.venueId,
    venueName: venue.venueName,
    capacity: venue.capacity,
    pricePerDay: venue.pricePerDay,
    venueStatus:
      normalizeStatus(venue.venueStatus),
  };

  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
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

function resetForm() {
  editMode.value = false;

  form.value = {
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
      <h2>
        {{
          editMode
            ? "修改場地"
            : "新增場地"
        }}
      </h2>

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
            :disabled="loading"
          />
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

        <label>
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
      </div>

      <div class="actions">
        <button
          type="button"
          :disabled="loading"
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
    </section>

    <section class="card">
      <div class="table-title">
        <h2>
          場地資料（{{ venues.length }}）
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
  color: #46392d;
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
  background: #8a642d;
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
</style>