<script setup>
import { computed, onMounted, ref } from 'vue'
import { RouterLink } from 'vue-router'

const RESTAURANT_API_URL = '/api/restaurant'
const TIME_API_URL = '/api/restaurant_times'
const RESERVATION_API_URL = '/api/reservations'

const restaurants = ref([])
const allTimes = ref([])
const timeOptions = ref([])
const reservations = ref([])

const message = ref('')
const messageType = ref('')
const formTitle = ref('新增訂位')

const form = ref(createEmptyForm())

function createEmptyForm() {
    return {
        reservationId: null,
        memberId: '',
        contactName: '',
        contactPhone: '',
        restaurantId: '',
        reservationDate: '',
        timeId: '',
        peopleCount: '',
        status: '已訂位',
    }
}

const hasMember = computed(() => {
    return String(form.value.memberId ?? '').trim() !== ''
})

function showMessage(text, type) {
    message.value = text
    messageType.value = type
}

function formatTime(time) {
    return time ? time.slice(0, 5) : ''
}

function getRestaurantName(restaurantId) {
    const restaurant = restaurants.value.find(
        (item) => Number(item.restaurantId) === Number(restaurantId),
    )

    return restaurant
        ? restaurant.restaurantName
        : `餐廳 ID：${restaurantId}`
}

function getTimeName(timeId) {
    const time = allTimes.value.find(
        (item) => Number(item.timeId) === Number(timeId),
    )

    if (!time) {
        return `時段 ID：${timeId}`
    }

    return `${time.mealType}（${formatTime(time.openTime)} - ${formatTime(time.closeTime)}）`
}

function clearContactInfo() {
    if (hasMember.value) {
        form.value.contactName = ''
        form.value.contactPhone = ''
    }
}

async function loadRestaurants() {
    try {
        const response = await fetch(RESTAURANT_API_URL)

        if (!response.ok) {
            showMessage('讀取餐廳資料失敗', 'error')
            return
        }

        restaurants.value = await response.json()
    } catch (error) {
        showMessage('無法連線至餐廳 API', 'error')
    }
}

async function loadAllTimes() {
    try {
        const response = await fetch(TIME_API_URL)

        if (response.ok) {
            allTimes.value = await response.json()
        }
    } catch (error) {
        console.error(error)
    }
}

async function loadTimeOptions(selectedTimeId = '') {
    if (!form.value.restaurantId) {
        timeOptions.value = []
        form.value.timeId = ''
        return
    }

    try {
        const response = await fetch(
            `${TIME_API_URL}/restaurant/${form.value.restaurantId}`,
        )

        if (!response.ok) {
            showMessage('讀取餐廳時段失敗', 'error')
            return
        }

        timeOptions.value = await response.json()
        form.value.timeId = selectedTimeId ? String(selectedTimeId) : ''
    } catch (error) {
        showMessage('無法連線至餐廳時段 API', 'error')
    }
}

async function loadReservations() {
    try {
        const response = await fetch(RESERVATION_API_URL)

        if (!response.ok) {
            showMessage('讀取訂位資料失敗', 'error')
            return
        }

        reservations.value = await response.json()
    } catch (error) {
        showMessage('無法連線至訂位 API', 'error')
    }
}

function clearForm() {
    form.value = createEmptyForm()
    timeOptions.value = []
    formTitle.value = '新增訂位'
    message.value = ''
}

async function saveReservation() {
    if (!hasMember.value) {
        if (
            !form.value.contactName.trim() ||
            !form.value.contactPhone.trim()
        ) {
            showMessage('非會員訂位必須填寫姓名與電話', 'error')
            return
        }
    }

    const payload = {
        memberId: hasMember.value ? Number(form.value.memberId) : null,
        contactName: hasMember.value ? null : form.value.contactName.trim(),
        contactPhone: hasMember.value ? null : form.value.contactPhone.trim(),
        restaurantId: Number(form.value.restaurantId),
        reservationDate: form.value.reservationDate,
        timeId: Number(form.value.timeId),
        peopleCount: Number(form.value.peopleCount),
        status: form.value.status,
    }

    const isEdit = form.value.reservationId !== null
    const url = isEdit
        ? `${RESERVATION_API_URL}/${form.value.reservationId}`
        : RESERVATION_API_URL

    try {
        const response = await fetch(url, {
            method: isEdit ? 'PUT' : 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(payload),
        })

        if (!response.ok) {
            showMessage('儲存失敗', 'error')
            return
        }

        showMessage(isEdit ? '修改成功' : '新增成功', 'success')
        clearForm()
        await loadReservations()
    } catch (error) {
        showMessage('無法連線至訂位 API', 'error')
    }
}

async function editReservation(reservation) {
    form.value = {
        reservationId: reservation.reservationId,
        memberId: reservation.memberId ?? '',
        contactName: reservation.contactName ?? '',
        contactPhone: reservation.contactPhone ?? '',
        restaurantId: String(reservation.restaurantId),
        reservationDate: reservation.reservationDate,
        timeId: '',
        peopleCount: reservation.peopleCount,
        status: reservation.status,
    }

    clearContactInfo()
    await loadTimeOptions(reservation.timeId)

    formTitle.value = `修改訂位 ID：${reservation.reservationId}`
    window.scrollTo({ top: 0, behavior: 'smooth' })
}

async function deleteReservation(id) {
    if (!confirm('確定要刪除這筆訂位嗎？')) {
        return
    }

    try {
        const response = await fetch(`${RESERVATION_API_URL}/${id}`, {
            method: 'DELETE',
        })

        if (!response.ok) {
            showMessage('刪除失敗', 'error')
            return
        }

        showMessage('訂位已刪除', 'success')
        clearForm()
        await loadReservations()
    } catch (error) {
        showMessage('無法連線至訂位 API', 'error')
    }
}

onMounted(async () => {
    await loadRestaurants()
    await loadAllTimes()
    await loadReservations()
})
</script>

<template>
    <div class="page">
        <section class="hero">
            <h1>餐廳訂位管理</h1>
            <p>管理飯店餐廳訂位、會員與非會員聯絡資訊及訂位狀態。</p>
        </section>

        <main class="container">
            <div class="top-actions">
                <RouterLink to="/restaurant-menu" class="back-btn">
                    ← 返回餐廳功能
                </RouterLink>
            </div>

            <section class="card">
                <h2>{{ formTitle }}</h2>

                <form @submit.prevent="saveReservation">
                    <div class="form-grid">
                        <div class="form-group">
                            <label>會員 ID（選填）</label>
                            <input v-model="form.memberId" type="number" min="1" placeholder="會員訂位可輸入會員 ID"
                                @input="clearContactInfo" />
                        </div>

                        <div class="form-group">
                            <label>訂位人姓名（非會員必填）</label>
                            <input v-model="form.contactName" type="text" placeholder="請輸入訂位人姓名"
                                :disabled="hasMember" />
                        </div>

                        <div class="form-group">
                            <label>訂位人電話（非會員必填）</label>
                            <input v-model="form.contactPhone" type="text" placeholder="請輸入聯絡電話"
                                :disabled="hasMember" />
                        </div>

                        <div class="form-group">
                            <label>餐廳 *</label>
                            <select v-model="form.restaurantId" required @change="loadTimeOptions()">
                                <option value="">請選擇餐廳</option>
                                <option v-for="restaurant in restaurants" :key="restaurant.restaurantId"
                                    :value="String(restaurant.restaurantId)">
                                    {{ restaurant.restaurantName }}
                                </option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label>訂位日期 *</label>
                            <input v-model="form.reservationDate" type="date" required />
                        </div>

                        <div class="form-group">
                            <label>訂位時段 *</label>
                            <select v-model="form.timeId" required :disabled="!form.restaurantId">
                                <option value="">
                                    {{ form.restaurantId ? '請選擇時段' : '請先選擇餐廳' }}
                                </option>

                                <option v-for="time in timeOptions" :key="time.timeId" :value="String(time.timeId)">
                                    {{ time.mealType }}（{{ formatTime(time.openTime) }} -
                                    {{ formatTime(time.closeTime) }}）
                                </option>
                            </select>
                        </div>

                        <div class="form-group">
                            <label>訂位人數 *</label>
                            <input v-model="form.peopleCount" type="number" min="1" required />
                        </div>

                        <div class="form-group">
                            <label>訂位狀態 *</label>
                            <select v-model="form.status" required>
                                <option value="已訂位">訂位</option>
                                <option value="已取消">取消</option>
                                <option value="已完成">已完成</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-actions">
                        <button type="submit" class="btn-save">儲存</button>
                        <button type="button" class="btn-clear" @click="clearForm">
                            清除
                        </button>
                    </div>

                    <p v-if="message" class="message" :class="messageType">
                        {{ message }}
                    </p>
                </form>
            </section>

            <section class="card">
                <h2>訂位列表</h2>

                <div class="table-wrapper">
                    <table>
                        <thead>
                            <tr>
                                <th>訂位 ID</th>
                                <th>會員 ID</th>
                                <th>訂位人姓名</th>
                                <th>訂位人電話</th>
                                <th>餐廳</th>
                                <th>訂位日期</th>
                                <th>時段</th>
                                <th>人數</th>
                                <th>狀態</th>
                                <th>操作</th>
                            </tr>
                        </thead>

                        <tbody>
                            <tr v-if="reservations.length === 0">
                                <td colspan="10" class="empty">目前沒有訂位資料</td>
                            </tr>

                            <tr v-for="reservation in reservations" :key="reservation.reservationId">
                                <td>{{ reservation.reservationId }}</td>
                                <td>{{ reservation.memberId ?? '' }}</td>
                                <td>{{ reservation.contactName ?? '' }}</td>
                                <td>{{ reservation.contactPhone ?? '' }}</td>
                                <td>{{ getRestaurantName(reservation.restaurantId) }}</td>
                                <td>{{ reservation.reservationDate }}</td>
                                <td>{{ getTimeName(reservation.timeId) }}</td>
                                <td>{{ reservation.peopleCount }}</td>
                                <td>{{ reservation.status }}</td>
                                <td class="actions">
                                    <button class="btn-edit" @click="editReservation(reservation)">
                                        修改
                                    </button>
                                    <button class="btn-delete" @click="deleteReservation(reservation.reservationId)">
                                        刪除
                                    </button>
                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </section>
        </main>
    </div>
</template>

<style scoped>
.page {
    min-height: 100vh;
    font-family: Arial, "Microsoft JhengHei", sans-serif;
    background: #f8f6f1;
    color: #333;
}

.hero {
    padding: 60px 20px;
    text-align: center;
    color: white;
    background:
        linear-gradient(rgba(0, 0, 0, 0.52), rgba(0, 0, 0, 0.52)),
        url("https://images.unsplash.com/photo-1517248135467-4c7edcad34c4") center / cover no-repeat;
}

.hero h1 {
    margin: 0 0 12px;
    font-size: 40px;
}

.hero p {
    margin: 0;
    color: #f2ede5;
}

.container {
    width: min(1300px, 94%);
    margin: 42px auto 70px;
}

.top-actions {
    margin-bottom: 20px;
}

.back-btn {
    display: inline-block;
    padding: 10px 18px;
    border-radius: 8px;
    background: #eee9e1;
    color: #5c4d3d;
    text-decoration: none;
    font-weight: bold;
}

.card {
    margin-bottom: 28px;
    padding: 28px;
    border-radius: 14px;
    background: white;
    box-shadow: 0 6px 24px rgba(0, 0, 0, 0.08);
}

.card h2 {
    margin: 0 0 22px;
    color: #6f5328;
}

.form-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 18px;
}

.form-group {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

label {
    color: #554536;
    font-size: 14px;
    font-weight: bold;
}

input,
select {
    width: 100%;
    padding: 12px 14px;
    border: 1px solid #d9d2c7;
    border-radius: 8px;
    font: inherit;
}

input:disabled,
select:disabled {
    background: #f1eee8;
    cursor: not-allowed;
}

.form-actions,
.actions {
    display: flex;
    gap: 10px;
}

.form-actions {
    margin-top: 22px;
}

button {
    border: 0;
    border-radius: 8px;
    padding: 11px 18px;
    cursor: pointer;
    font: inherit;
    font-weight: bold;
}

.btn-save {
    background: #b58a46;
    color: white;
}

.btn-clear {
    background: #eee9e1;
    color: #5c4d3d;
}

.btn-edit {
    background: #fff3d8;
    color: #95691f;
}

.btn-delete {
    background: #fde9e7;
    color: #b3443c;
}

.message {
    margin-top: 15px;
    padding: 11px 13px;
    border-radius: 8px;
    font-weight: bold;
}

.success {
    background: #e5f6eb;
    color: #257641;
}

.error {
    background: #fde9e7;
    color: #b3443c;
}

.table-wrapper {
    overflow-x: auto;
}

table {
    width: 100%;
    min-width: 1180px;
    border-collapse: collapse;
}

thead {
    background: #4a3b2a;
    color: white;
}

th,
td {
    padding: 14px 12px;
    text-align: left;
    border-bottom: 1px solid #eee7dd;
}

.empty {
    text-align: center;
    color: #888;
}

@media (max-width: 768px) {
    .hero h1 {
        font-size: 31px;
    }

    .form-grid {
        grid-template-columns: 1fr;
    }
}
</style>