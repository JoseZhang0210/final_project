<script setup>
import { computed, onMounted, ref } from 'vue'

import {
  clearSession,
  createRental,
  deleteRental,
  getApiErrorMessage,
  getRentals,
  loadSession,
  login,
  saveSession,
  updateRental,
} from '../api/venueRentalApi'

// ============================================================
// JWT
// ============================================================

const username = ref('')
const password = ref('')
const token = ref('')
const authorities = ref([])

const rentals = ref([])

const loading = ref(false)
const message = ref('')
const errorMessage = ref('')

const isLoggedIn = computed(() => Boolean(token.value))

// ============================================================
// Rental Form
// ============================================================

const editMode = ref(false)

const form = ref({
  rentalId: '',
  venueId: '',
  memberId: '',
  eventName: '',
  rentalDate: '',
  guestCount: '',
  paymentId: '',
  rentalStatus: 'PENDING',
})

const rentalStatuses = [
  'PENDING',
  'CONFIRMED',
  'CANCELLED',
  'COMPLETED',
]

// ============================================================
// Page Init
// ============================================================

onMounted(async () => {
  const session = loadSession()

  username.value = session.username
  token.value = session.token
  authorities.value = session.authorities

  if (token.value) {
    try {
      await loadRentalData()
    } catch (error) {
      errorMessage.value = getApiErrorMessage(error)
    }
  }
})

// ============================================================
// Login
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

    await loadRentalData()

    message.value = 'JWT 登入成功，Rental API 已連線'
  } catch (error) {
    clearLoginState()
    errorMessage.value = getApiErrorMessage(error)
  } finally {
    loading.value = false
  }
}

// ============================================================
// READ
// ============================================================

async function loadRentalData() {
  if (!token.value) {
    return
  }

  try {
    rentals.value = await getRentals(token.value)
  } catch (error) {
    const status = error?.response?.status

    if (status === 401 || status === 403) {
      clearLoginState()
    }

    throw error
  }
}

async function refreshRentals() {
  message.value = ''
  errorMessage.value = ''
  loading.value = true

  try {
    await loadRentalData()
    message.value = 'Rental 資料已重新整理'
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error)
  } finally {
    loading.value = false
  }
}

// ============================================================
// Payload
// ============================================================

function buildRentalPayload() {
  const rentalId = Number(form.value.rentalId)
  const venueId = Number(form.value.venueId)
  const memberId = Number(form.value.memberId)
  const guestCount = Number(form.value.guestCount)
  const paymentId = Number(form.value.paymentId)

  if (!Number.isInteger(rentalId) || rentalId <= 0) {
    throw new Error('租借 ID 必須是大於 0 的整數')
  }

  if (!Number.isInteger(venueId) || venueId <= 0) {
    throw new Error('場地 ID 必須是大於 0 的整數')
  }

  if (!Number.isInteger(memberId) || memberId <= 0) {
    throw new Error('會員 ID 必須是大於 0 的整數')
  }

  if (!form.value.eventName.trim()) {
    throw new Error('請輸入活動名稱')
  }

  if (!form.value.rentalDate) {
    throw new Error('請選擇租借日期')
  }

  if (!Number.isInteger(guestCount) || guestCount <= 0) {
    throw new Error('人數必須是大於 0 的整數')
  }

  if (!Number.isInteger(paymentId) || paymentId <= 0) {
    throw new Error('付款 ID 必須是大於 0 的整數')
  }

  return {
    rentalId,
    venueId,
    memberId,
    eventName: form.value.eventName.trim(),
    rentalDate: form.value.rentalDate,
    guestCount,
    paymentId,
    rentalStatus: form.value.rentalStatus,
  }
}

// ============================================================
// CREATE / UPDATE
// ============================================================

async function handleSubmit() {
  message.value = ''
  errorMessage.value = ''

  loading.value = true

  try {
    const payload = buildRentalPayload()

    if (editMode.value) {
      await updateRental(
        token.value,
        payload.rentalId,
        payload,
      )

      message.value = '租借修改成功'
    } else {
      await createRental(
        token.value,
        payload,
      )

      message.value = '租借新增成功'
    }

    resetForm()
    await loadRentalData()
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error)
  } finally {
    loading.value = false
  }
}

// ============================================================
// EDIT
// ============================================================

function startEdit(rental) {
  editMode.value = true

  form.value = {
    rentalId: rental.rentalId,
    venueId: rental.venueId,
    memberId: rental.memberId,
    eventName: rental.eventName,
    rentalDate: toDateTimeLocal(rental.rentalDate),
    guestCount: rental.guestCount,
    paymentId: rental.paymentId,
    rentalStatus: rental.rentalStatus,
  }

  message.value =
    '目前正在編輯租借 ID ' + rental.rentalId

  errorMessage.value = ''

  window.scrollTo({
    top: 0,
    behavior: 'smooth',
  })
}

// ============================================================
// DELETE
// ============================================================

async function handleDelete(rental) {
  message.value = ''
  errorMessage.value = ''

  const confirmed = window.confirm(
    '確定要刪除租借「' +
      rental.eventName +
      '」（ID ' +
      rental.rentalId +
      '）嗎？',
  )

  if (!confirmed) {
    return
  }

  loading.value = true

  try {
    await deleteRental(
      token.value,
      rental.rentalId,
    )

    if (
      editMode.value &&
      Number(form.value.rentalId) === rental.rentalId
    ) {
      resetForm()
    }

    await loadRentalData()

    message.value = '租借刪除成功'
  } catch (error) {
    errorMessage.value = getApiErrorMessage(error)
  } finally {
    loading.value = false
  }
}

// ============================================================
// Helpers
// ============================================================

function resetForm() {
  editMode.value = false

  form.value = {
    rentalId: '',
    venueId: '',
    memberId: '',
    eventName: '',
    rentalDate: '',
    guestCount: '',
    paymentId: '',
    rentalStatus: 'PENDING',
  }
}

function formatDateTime(value) {
  if (!value) {
    return ''
  }

  return String(value).replace('T', ' ')
}

function toDateTimeLocal(value) {
  if (!value) {
    return ''
  }

  return String(value).substring(0, 16)
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
  rentals.value = []
}
</script>

<template>
  <main class="page">

    <section class="hero">
      <div>
        <p class="eyebrow">
          HOTEL MANAGEMENT
        </p>

        <h1>租借管理</h1>

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

    <!-- JWT -->
    <section class="card">

      <div>
        <h2>JWT 登入</h2>
        <p class="description">
          Venue 與 Rental 共用目前分頁的 JWT Session。
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
            autocomplete="username"
          >
        </label>

        <label>
          <span>密碼</span>

          <input
            v-model="password"
            type="password"
            autocomplete="current-password"
            @keyup.enter="handleLogin"
          >
        </label>

        <button
          type="button"
          :disabled="loading"
          @click="handleLogin"
        >
          JWT 登入
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

        <div class="actions">
          <button
            type="button"
            class="secondary"
            @click="refreshRentals"
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

    <!-- CRUD FORM -->
    <section
      v-if="isLoggedIn"
      class="card"
    >

      <div class="form-header">
        <div>
          <h2>
            {{ editMode ? '修改租借' : '新增租借' }}
          </h2>

          <p class="description">
            Rental REST API CRUD
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

        <label>
          <span>租借 ID</span>
          <input
            v-model="form.rentalId"
            type="number"
            :disabled="editMode"
          >
        </label>

        <label>
          <span>場地 ID</span>
          <input
            v-model="form.venueId"
            type="number"
          >
        </label>

        <label>
          <span>會員 ID</span>
          <input
            v-model="form.memberId"
            type="number"
          >
        </label>

        <label>
          <span>活動名稱</span>
          <input
            v-model="form.eventName"
            type="text"
            maxlength="50"
          >
        </label>

        <label>
          <span>租借日期</span>
          <input
            v-model="form.rentalDate"
            type="datetime-local"
          >
        </label>

        <label>
          <span>人數</span>
          <input
            v-model="form.guestCount"
            type="number"
            min="1"
          >
        </label>

        <label>
          <span>付款 ID</span>
          <input
            v-model="form.paymentId"
            type="number"
          >
        </label>

        <label>
          <span>狀態</span>

          <select v-model="form.rentalStatus">
            <option
              v-for="status in rentalStatuses"
              :key="status"
              :value="status"
            >
              {{ status }}
            </option>
          </select>
        </label>

      </div>

      <div class="actions form-actions">

        <button
          type="button"
          :disabled="loading"
          @click="handleSubmit"
        >
          {{
            editMode
              ? '儲存修改'
              : '新增租借'
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
              <th>付款</th>
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
              <td>{{ rental.venueId }}</td>
              <td>{{ rental.memberId }}</td>
              <td>{{ rental.eventName }}</td>
              <td>{{ formatDateTime(rental.rentalDate) }}</td>
              <td>{{ rental.guestCount }}</td>
              <td>{{ rental.paymentId }}</td>

              <td>
                <span class="status">
                  {{ rental.rentalStatus }}
                </span>
              </td>

              <td>
                <div class="actions">

                  <button
                    type="button"
                    class="small secondary"
                    @click="startEdit(rental)"
                  >
                    編輯
                  </button>

                  <button
                    type="button"
                    class="small danger"
                    @click="handleDelete(rental)"
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
        JWT 登入後顯示 Rental 資料
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
}

input:disabled {
  background: #f1f5f9;
}

button {
  min-height: 42px;
  padding: 0 18px;
  border: 0;
  border-radius: 9px;
  background: #7c3aed;
  color: white;
  font-weight: 800;
  cursor: pointer;
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
</style>
