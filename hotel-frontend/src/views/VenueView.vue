<script setup>
import { computed, onMounted, ref } from 'vue'

import {
  clearSession,
  createVenue,
  deleteVenue,
  getApiErrorMessage,
  getVenues,
  loadSession,
  login,
  saveSession,
  updateVenue,
} from '../api/venueRentalApi'

// ============================================================
// JWT 登入狀態
// ============================================================

const username = ref('')
const password = ref('')
const token = ref('')
const authorities = ref([])

const venues = ref([])

const loading = ref(false)
const message = ref('')
const errorMessage = ref('')

const isLoggedIn = computed(() => Boolean(token.value))

// ============================================================
// Venue 表單
// ============================================================

const editMode = ref(false)

const form = ref({
  venueId: '',
  venueName: '',
  capacity: '',
  pricePerDay: '',
  venueStatus: 'AVAILABLE',
})

const venueStatuses = [
  'AVAILABLE',
  'MAINTENANCE',
  'DISABLED',
]

// ============================================================
// 頁面載入
// ============================================================

onMounted(async () => {
  const session = loadSession()

  username.value = session.username
  token.value = session.token
  authorities.value = session.authorities

  if (token.value) {
    try {
      await loadVenueData()
    } catch (error) {
      errorMessage.value = getApiErrorMessage(error)
    }
  }
})

// ============================================================
// JWT Login
// ============================================================

async function handleLogin() {
  message.value = ''
  errorMessage.value = ''

  if (!username.value.trim() || !password.value) {
    errorMessage.value = '請輸入帳號與密碼'
    return
  }

  loading.value = true

  try {
    const result = await login(
      username.value.trim(),
      password.value,
    )

    if (!result?.token) {
      throw new Error('登入成功但沒有取得 JWT Token')
    }

    token.value = result.token
    authorities.value = result.authorities ?? []

    saveSession(username.value.trim(), result)

    password.value = ''

    await loadVenueData()

    message.value = 'JWT 登入成功，Venue API 已連線'
  } catch (error) {
    clearLoginState()
    errorMessage.value = getApiErrorMessage(error)
  } finally {
    loading.value = false
  }
}

// ============================================================
// Venue READ
// ============================================================

async function loadVenueData() {
  if (!token.value) {
    return
  }

  try {
    venues.value = await getVenues(token.value)
  } catch (error) {
    const status = error?.response?.status

    if (status === 401 || status === 403) {
      clearLoginState()
    }

    throw error
  }
}

async function refreshVenues() {
  message.value = ''
  errorMessage.value = ''
  loading.value = true

  try {
    await loadVenueData()
    message.value = 'Venue 資料已重新整理'
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error)
  } finally {
    loading.value = false
  }
}

// ============================================================
// 表單驗證與 Payload
// ============================================================

function buildVenuePayload() {
  const venueId = Number(form.value.venueId)
  const capacity = Number(form.value.capacity)
  const pricePerDay = Number(form.value.pricePerDay)

  if (!Number.isInteger(venueId) || venueId <= 0) {
    throw new Error('場地 ID 必須是大於 0 的整數')
  }

  if (!form.value.venueName.trim()) {
    throw new Error('請輸入場地名稱')
  }

  if (!Number.isInteger(capacity) || capacity <= 0) {
    throw new Error('容納人數必須是大於 0 的整數')
  }

  if (!Number.isInteger(pricePerDay) || pricePerDay < 0) {
    throw new Error('每日價格必須是 0 或正整數')
  }

  return {
    venueId,
    venueName: form.value.venueName.trim(),
    capacity,
    pricePerDay,
    venueStatus: form.value.venueStatus,
  }
}

// ============================================================
// Venue CREATE / UPDATE
// ============================================================

async function handleSubmit() {
  message.value = ''
  errorMessage.value = ''

  if (!token.value) {
    errorMessage.value = '請先登入'
    return
  }

  loading.value = true

  try {
    const payload = buildVenuePayload()

    if (editMode.value) {
      await updateVenue(
        token.value,
        payload.venueId,
        payload,
      )

      message.value = '場地修改成功'
    } else {
      await createVenue(
        token.value,
        payload,
      )

      message.value = '場地新增成功'
    }

    resetForm()
    await loadVenueData()
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error)
  } finally {
    loading.value = false
  }
}

// ============================================================
// 進入編輯模式
// ============================================================

function startEdit(venue) {
  editMode.value = true

  form.value = {
    venueId: venue.venueId,
    venueName: venue.venueName,
    capacity: venue.capacity,
    pricePerDay: venue.pricePerDay,
    venueStatus: venue.venueStatus,
  }

  message.value = '目前正在編輯場地 ID ' + venue.venueId
  errorMessage.value = ''

  window.scrollTo({
    top: 0,
    behavior: 'smooth',
  })
}

// ============================================================
// Venue DELETE
// ============================================================

async function handleDelete(venue) {
  message.value = ''
  errorMessage.value = ''

  const confirmed = window.confirm(
    '確定要刪除場地「' +
      venue.venueName +
      '」（ID ' +
      venue.venueId +
      '）嗎？',
  )

  if (!confirmed) {
    return
  }

  loading.value = true

  try {
    await deleteVenue(
      token.value,
      venue.venueId,
    )

    if (
      editMode.value &&
      Number(form.value.venueId) === venue.venueId
    ) {
      resetForm()
    }

    await loadVenueData()

    message.value = '場地刪除成功'
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error)
  } finally {
    loading.value = false
  }
}

// ============================================================
// Reset
// ============================================================

function resetForm() {
  editMode.value = false

  form.value = {
    venueId: '',
    venueName: '',
    capacity: '',
    pricePerDay: '',
    venueStatus: 'AVAILABLE',
  }
}

// ============================================================
// Logout
// ============================================================

function handleLogout() {
  clearLoginState()

  username.value = ''
  password.value = ''

  resetForm()

  message.value = '已登出'
  errorMessage.value = ''
}

function clearLoginState() {
  clearSession()

  token.value = ''
  authorities.value = []
  venues.value = []
}
</script>

<template>
  <main class="page">

    <!-- =====================================================
         Header
         ===================================================== -->
    <section class="hero">
      <div>
        <p class="eyebrow">
          HOTEL MANAGEMENT
        </p>

        <h1>場地管理</h1>

        <p>
          Vue → JWT → Spring Boot → SQL Server
        </p>
      </div>

      <div
        v-if="isLoggedIn"
        class="connection-badge"
      >
        JWT CONNECTED
      </div>
    </section>

    <!-- =====================================================
         JWT Login
         ===================================================== -->
    <section class="card">

      <div class="card-title">
        <h2>JWT 登入</h2>

        <p>
          登入後使用 Bearer Token 操作 Venue REST API。
        </p>
      </div>

      <div
        v-if="!isLoggedIn"
        class="login-grid"
      >
        <label>
          <span>帳號</span>

          <input
            v-model="username"
            type="text"
            placeholder="請輸入帳號"
            autocomplete="username"
          >
        </label>

        <label>
          <span>密碼</span>

          <input
            v-model="password"
            type="password"
            placeholder="請輸入密碼"
            autocomplete="current-password"
            @keyup.enter="handleLogin"
          >
        </label>

        <button
          type="button"
          :disabled="loading"
          @click="handleLogin"
        >
          {{ loading ? '登入中...' : 'JWT 登入' }}
        </button>
      </div>

      <div
        v-else
        class="session-panel"
      >
        <div>
          <strong>{{ username }}</strong>

          <div class="authority-list">
            <span
              v-for="authority in authorities"
              :key="authority"
              class="authority"
            >
              {{ authority }}
            </span>
          </div>
        </div>

        <div class="session-actions">

          <button
            type="button"
            class="secondary"
            :disabled="loading"
            @click="refreshVenues"
          >
            重新整理
          </button>

          <button
            type="button"
            class="danger"
            @click="handleLogout"
          >
            登出
          </button>

        </div>
      </div>

      <p
        v-if="message"
        class="success"
      >
        {{ message }}
      </p>

      <p
        v-if="errorMessage"
        class="error"
      >
        {{ errorMessage }}
      </p>

    </section>

    <!-- =====================================================
         Venue CREATE / UPDATE FORM
         ===================================================== -->
    <section
      v-if="isLoggedIn"
      class="card"
    >

      <div class="form-header">

        <div>
          <h2>
            {{ editMode ? '修改場地' : '新增場地' }}
          </h2>

          <p>
            {{
              editMode
                ? '場地 ID 在修改模式中不可變更'
                : '建立新的 Venue 資料'
            }}
          </p>
        </div>

        <span
          v-if="editMode"
          class="edit-badge"
        >
          EDIT MODE
        </span>

      </div>

      <div class="venue-form">

        <label>
          <span>場地 ID</span>

          <input
            v-model="form.venueId"
            type="number"
            min="1"
            :disabled="editMode"
            placeholder="例如：1001"
          >
        </label>

        <label>
          <span>場地名稱</span>

          <input
            v-model="form.venueName"
            type="text"
            maxlength="50"
            placeholder="例如：宴會廳"
          >
        </label>

        <label>
          <span>容納人數</span>

          <input
            v-model="form.capacity"
            type="number"
            min="1"
            placeholder="例如：150"
          >
        </label>

        <label>
          <span>每日價格</span>

          <input
            v-model="form.pricePerDay"
            type="number"
            min="0"
            placeholder="例如：6000"
          >
        </label>

        <label>
          <span>狀態</span>

          <select v-model="form.venueStatus">
            <option
              v-for="status in venueStatuses"
              :key="status"
              :value="status"
            >
              {{ status }}
            </option>
          </select>
        </label>

      </div>

      <div class="form-actions">

        <button
          type="button"
          :disabled="loading"
          @click="handleSubmit"
        >
          {{
            editMode
              ? '儲存修改'
              : '新增場地'
          }}
        </button>

        <button
          v-if="editMode"
          type="button"
          class="secondary"
          @click="resetForm"
        >
          取消
        </button>

      </div>

    </section>

    <!-- =====================================================
         Venue Table
         ===================================================== -->
    <section class="card">

      <div class="table-header">

        <div>
          <h2>Venue 資料</h2>

          <p v-if="isLoggedIn">
            共 {{ venues.length }} 筆
          </p>

          <p v-else>
            請先登入
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
        v-if="isLoggedIn && venues.length"
        class="table-wrap"
      >

        <table>

          <thead>
            <tr>
              <th>場地 ID</th>
              <th>場地名稱</th>
              <th>容納人數</th>
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

              <td>
                {{ venue.venueId }}
              </td>

              <td>
                {{ venue.venueName }}
              </td>

              <td>
                {{ venue.capacity }}
              </td>

              <td>
                NT$
                {{
                  Number(
                    venue.pricePerDay,
                  ).toLocaleString()
                }}
              </td>

              <td>
                <span class="status">
                  {{ venue.venueStatus }}
                </span>
              </td>

              <td>
                <div class="row-actions">

                  <button
                    type="button"
                    class="small secondary"
                    @click="startEdit(venue)"
                  >
                    編輯
                  </button>

                  <button
                    type="button"
                    class="small danger"
                    :disabled="loading"
                    @click="handleDelete(venue)"
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
        目前沒有 Venue 資料
      </div>

      <div
        v-else
        class="empty"
      >
        JWT 登入後顯示 Venue 資料
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

.hero {
  margin-bottom: 24px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
}

.eyebrow {
  margin: 0 0 8px;
  color: #2563eb;
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

.hero p:last-child,
.card-title p,
.form-header p,
.table-header p {
  margin: 0;
  color: #64748b;
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

.card {
  margin-bottom: 20px;
  padding: 24px;
  border: 1px solid #e2e8f0;
  border-radius: 16px;
  background: white;
  box-shadow: 0 5px 20px rgb(15 23 42 / 5%);
}

.login-grid {
  margin-top: 20px;
  display: grid;
  grid-template-columns: 1fr 1fr auto;
  align-items: end;
  gap: 14px;
}

.venue-form {
  margin-top: 20px;
  display: grid;
  grid-template-columns:
    repeat(5, minmax(0, 1fr));
  gap: 14px;
}

label {
  display: grid;
  gap: 7px;
  color: #334155;
  font-size: 14px;
  font-weight: 700;
}

input,
select {
  width: 100%;
  min-height: 42px;
  box-sizing: border-box;
  padding: 0 12px;
  border: 1px solid #cbd5e1;
  border-radius: 9px;
  background: white;
  color: #0f172a;
  font-size: 15px;
}

input:disabled {
  background: #f1f5f9;
  color: #64748b;
}

button {
  min-height: 42px;
  padding: 0 18px;
  border: 0;
  border-radius: 9px;
  background: #2563eb;
  color: white;
  font-weight: 800;
  cursor: pointer;
}

button:disabled {
  opacity: 0.6;
  cursor: wait;
}

button.secondary {
  background: #475569;
}

button.danger {
  background: #b91c1c;
}

button.small {
  min-height: 34px;
  padding: 0 12px;
  font-size: 13px;
}

.session-panel,
.form-header,
.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
}

.session-panel {
  margin-top: 20px;
}

.session-actions,
.authority-list,
.form-actions,
.row-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.authority-list {
  margin-top: 8px;
}

.form-actions {
  margin-top: 18px;
}

.authority {
  padding: 4px 8px;
  border-radius: 999px;
  background: #e0e7ff;
  color: #3730a3;
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
  background: #f1f5f9;
  font-size: 12px;
  font-weight: 700;
}

.empty {
  padding: 32px 5px 10px;
  color: #64748b;
  text-align: center;
}

@media (max-width: 950px) {
  .venue-form {
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
  .venue-form {
    grid-template-columns: 1fr;
  }

  h1 {
    font-size: 30px;
  }
}
</style>
