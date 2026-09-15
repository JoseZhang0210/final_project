<template>
  <div class="member-center-view">
    <div class="member-center-card">
      <div class="card-header">
        <div class="header-title">
          <div class="header-with-back" v-if="selectedBooking">
            <button class="back-btn" @click="selectedBooking = null">
              <span class="icon">←</span> 返回
            </button>
            <h2>訂單明細 (第 {{ getBookingIndex(selectedBooking) }} 次訂房)</h2>
          </div>
          <div v-else>
            <h2>我的訂房紀錄</h2>
            <p>查看您的預訂狀態並申請房務服務</p>
          </div>
        </div>
      </div>

      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>載入中...</p>
      </div>

      <div v-else-if="selectedBooking" class="booking-detail-view">
        <div class="detail-card">
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">入住日期</span>
              <span class="value">{{ selectedBooking.checkInDate }}</span>
            </div>
            <div class="detail-item">
              <span class="label">退房日期</span>
              <span class="value">{{ selectedBooking.checkOutDate }}</span>
            </div>
            <div class="detail-item">
              <span class="label">房型</span>
              <span class="value">{{ getRoomTypeName(selectedBooking.roomTypeId) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">入住人數</span>
              <span class="value">{{ selectedBooking.guestNum }} 人</span>
            </div>
            <div class="detail-item" v-if="selectedBooking.bookingStatus === '已入住'">
              <span class="label">房號</span>
              <span class="value">{{ getRoomNumber(selectedBooking.roomId) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">狀態</span>
              <span class="value status-badge" :class="getStatusClass(selectedBooking.bookingStatus)">
                {{ selectedBooking.bookingStatus }}
              </span>
            </div>
          </div>

          <div class="service-section" v-if="selectedBooking.bookingStatus === '已入住' && selectedBooking.roomId">
            <h3>🧽 需要房務服務嗎？</h3>
            <p class="service-desc">由於您目前正在入住期間，您可以直接向櫃檯申請房務服務。</p>
            <button class="service-btn" @click="showServiceModal = true">申請房務服務</button>
          </div>
        </div>
      </div>

      <div v-else class="booking-list-view">
        <div v-if="bookings.length === 0" class="empty-state">
          <div class="empty-icon">🛏️</div>
          <h3>空資料</h3>
          <p>您目前沒有訂房訂單</p>
        </div>
        
        <div v-else class="booking-list">
          <div 
            v-for="booking in paginatedBookings" 
            :key="booking.bookingId" 
            class="booking-card"
            :class="{ 'clickable-card': booking.bookingStatus === '已入住' }"
            @click="booking.bookingStatus === '已入住' ? (selectedBooking = booking) : null"
          >
            <div class="booking-header">
              <span class="booking-id">第 {{ getBookingIndex(booking) }} 次訂房</span>
              <span class="status-badge" :class="getStatusClass(booking.bookingStatus)">
                {{ booking.bookingStatus }}
              </span>
            </div>
            <div class="booking-body">
              <div class="date-row">
                <span class="date-label">入住：</span> {{ booking.checkInDate }}
                <span class="date-separator">|</span>
                <span class="date-label">退房：</span> {{ booking.checkOutDate }}
              </div>
              <div class="room-info">
                房型：{{ getRoomTypeName(booking.roomTypeId) }} 
                <span class="date-separator">|</span> 
                入住人數：{{ booking.guestNum }} 人
                <template v-if="booking.bookingStatus === '已入住'">
                  <span class="date-separator">|</span> 
                  房號：{{ getRoomNumber(booking.roomId) }}
                </template>
              </div>
            </div>
          </div>
        </div>
        
        <div v-if="totalPages > 1 && !selectedBooking && !loading && bookings.length > 0" class="pagination-controls">
          <button @click="prevPage" :disabled="currentPage === 1" class="page-btn">
            <span class="icon">←</span> 上一頁
          </button>
          <span class="page-info">第 {{ currentPage }} 頁 / 共 {{ totalPages }} 頁</span>
          <button @click="nextPage" :disabled="currentPage === totalPages" class="page-btn">
            下一頁 <span class="icon">→</span>
          </button>
        </div>
      </div>

      <!-- 房務申請 Modal -->
      <div v-if="showServiceModal" class="modal-overlay">
        <div class="modal-content">
          <h3>申請房務服務</h3>
          <form @submit.prevent="submitRoomTask">
            <div class="form-group">
              <label>服務類型</label>
              <select v-model="taskForm.taskType" required>
                <option value="日常清潔">日常清潔</option>
                <option value="備品補充">備品補充</option>
                <option value="設備報修">設備報修</option>
                <option value="其他">其他</option>
              </select>
            </div>
            <div class="form-group">
              <label>備註需求</label>
              <textarea v-model="taskForm.remark" rows="3" placeholder="例如：需要兩條毛巾、冷氣不冷..."></textarea>
            </div>
            <div class="modal-actions">
              <button type="button" class="btn-cancel" @click="showServiceModal = false">取消</button>
              <button type="submit" class="btn-submit" :disabled="submittingTask">
                {{ submittingTask ? '送出中...' : '確認送出' }}
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- 美化版提示 Modal -->
      <Transition name="fade">
        <div v-if="alertConfig.show" class="alert-overlay">
          <div class="alert-modal">
            <div class="alert-icon" :class="alertConfig.type">
              <span v-if="alertConfig.type === 'success'">✓</span>
              <span v-if="alertConfig.type === 'error'">✕</span>
            </div>
            <h3 class="alert-title">{{ alertConfig.title }}</h3>
            <p class="alert-message">{{ alertConfig.message }}</p>
            <button class="btn-alert" @click="closeAlert">我知道了</button>
          </div>
        </div>
      </Transition>

    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

const loading = ref(true)
const bookings = ref([])
const rooms = ref([])
const roomTypes = ref([])
const selectedBooking = ref(null)

const showServiceModal = ref(false)
const submittingTask = ref(false)
const taskForm = ref({
  taskType: '日常清潔',
  remark: ''
})

const alertConfig = ref({
  show: false,
  type: 'success',
  title: '',
  message: ''
})

function showAlert(type, title, message) {
  alertConfig.value = { show: true, type, title, message }
}

function closeAlert() {
  alertConfig.value.show = false
}

const currentPage = ref(1)
const itemsPerPage = 4

const paginatedBookings = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage
  const end = start + itemsPerPage
  return bookings.value.slice(start, end)
})

const totalPages = computed(() => {
  return Math.ceil(bookings.value.length / itemsPerPage)
})

function prevPage() {
  if (currentPage.value > 1) currentPage.value--
}

function nextPage() {
  if (currentPage.value < totalPages.value) currentPage.value++
}

onMounted(async () => {
  await loadRoomTypes()
  await loadRooms()
  await loadBookings()
})

async function loadRoomTypes() {
  try {
    const res = await fetch('/api/roomtypes')
    if (res.ok) {
      roomTypes.value = await res.json()
    }
  } catch (e) {
    console.error('Failed to load room types:', e)
  }
}

async function loadRooms() {
  try {
    const token = localStorage.getItem('token')
    const res = await fetch('/api/rooms', {
      headers: token ? { 'Authorization': `Bearer ${token}` } : {}
    })
    if (res.ok) {
      rooms.value = await res.json()
    }
  } catch (e) {
    console.error('Failed to load rooms:', e)
  }
}

function getRoomNumber(roomId) {
  if (!roomId) return '尚未分配'
  const room = rooms.value.find(r => r.roomId === roomId)
  return room ? room.roomNumber : roomId
}

function getRoomTypeName(typeId) {
  if (!typeId) return '未知房型'
  const type = roomTypes.value.find(t => t.roomTypeId === typeId)
  return type ? type.typeName : typeId
}

function getBookingIndex(booking) {
  const idx = bookings.value.findIndex(b => b.bookingId === booking.bookingId)
  if (idx === -1) return ''
  return bookings.value.length - idx
}

async function loadBookings() {
  loading.value = true
  try {
    const token = localStorage.getItem('token')
    if (!token) return

    // 1. 取得 memberId
    const meRes = await fetch('/api/members/me', {
      headers: { 'Authorization': `Bearer ${token}` }
    })
    const memberData = await meRes.json()
    const memberId = memberData.memberId

    if (!memberId) return

    // 2. 搜尋該 memberId 的訂單
    const bookingRes = await fetch('/api/bookings/search', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ memberId })
    })

    if (bookingRes.ok) {
      let data = await bookingRes.json()
      // 依建立時間或 ID 排序 (最新在最上面)
      data.sort((a, b) => b.bookingId - a.bookingId)
      bookings.value = data
    }
  } catch (e) {
    console.error('Failed to load bookings:', e)
  } finally {
    loading.value = false
  }
}

async function submitRoomTask() {
  if (!selectedBooking.value || !selectedBooking.value.roomId) return
  submittingTask.value = true
  
  try {
    const token = localStorage.getItem('token')
    const randomEmployeeId = Math.floor(Math.random() * (24 - 13 + 1)) + 13;
    const payload = {
      roomId: selectedBooking.value.roomId,
      taskType: taskForm.value.taskType,
      remark: taskForm.value.remark || '無',
      priority: '一般',
      taskStatus: '待處理',
      employeeId: randomEmployeeId
    }

    const res = await fetch('/api/roomtask', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify(payload)
    })

    if (res.ok) {
      showAlert('success', '需求已建立', '您房務需求已建立,請您稍後,感謝您')
      showServiceModal.value = false
      taskForm.value = { taskType: '日常清潔', remark: '' }
    } else {
      showAlert('error', '申請失敗', '申請失敗，請稍後再試')
    }
  } catch (e) {
    console.error('Submit task failed:', e)
  } finally {
    submittingTask.value = false
  }
}

function getStatusClass(status) {
  switch (status) {
    case '已入住': return 'status-active'
    case '已退房': return 'status-done'
    case '已取消': return 'status-cancel'
    case '待入住': return 'status-pending'
    default: return 'status-default'
  }
}
</script>

<style scoped>
.member-center-view {
  width: 100%;
}
.member-center-card {
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.05);
  padding: 40px;
  min-height: 500px;
}
.card-header {
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 20px;
  margin-bottom: 30px;
}
.header-title h2 {
  font-size: 1.5rem;
  color: #333;
  margin: 0 0 8px 0;
  font-weight: 600;
}
.header-title p {
  color: #888;
  margin: 0;
  font-size: 0.95rem;
}
.header-with-back {
  display: flex;
  align-items: center;
  gap: 15px;
}
.back-btn {
  background: none;
  border: none;
  color: #887864;
  font-size: 1rem;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 8px 12px;
  border-radius: 6px;
  transition: background 0.2s;
}
.back-btn:hover {
  background: #fdfaf6;
  color: #b58a46;
}

/* Loading & Empty State */
.loading-state, .empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80px 0;
  color: #887864;
  gap: 14px;
}
.spinner {
  width: 34px;
  height: 34px;
  border: 3px solid #eee7dd;
  border-top-color: #b58a46;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}
@keyframes spin { to { transform: rotate(360deg); } }

.empty-icon {
  font-size: 3rem;
  opacity: 0.5;
}
.empty-state h3 {
  margin: 0;
  color: #555;
}

/* Booking List */
.booking-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.booking-card {
  border: 1px solid #eaeaea;
  border-radius: 10px;
  padding: 20px;
  background: #fafafa;
}
.booking-card.clickable-card {
  cursor: pointer;
  transition: all 0.2s ease;
}
.booking-card.clickable-card:hover {
  border-color: #C9A96E;
  box-shadow: 0 4px 12px rgba(201, 169, 110, 0.15);
  transform: translateY(-2px);
  background: #fff;
}
.booking-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.booking-id {
  font-weight: 600;
  color: #333;
  font-size: 1.1rem;
}
.booking-body {
  color: #666;
  font-size: 0.95rem;
}
.date-row {
  margin-bottom: 6px;
}
.date-label {
  color: #999;
}
.date-separator {
  margin: 0 10px;
  color: #ddd;
}

/* Status Badges */
.status-badge {
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 600;
}
.status-active { background: #e6f7e9; color: #2e7d32; }
.status-done { background: #f0f0f0; color: #666; }
.status-cancel { background: #ffebee; color: #c62828; }
.status-pending { background: #fff3e0; color: #ef6c00; }
.status-default { background: #e3f2fd; color: #1565c0; }

/* Detail View */
.detail-card {
  border: 1px solid #eaeaea;
  border-radius: 10px;
  padding: 24px;
}
.detail-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
  margin-bottom: 30px;
}
.detail-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}
.detail-item .label {
  color: #888;
  font-size: 0.9rem;
}
.detail-item .value {
  font-size: 1.1rem;
  color: #333;
  font-weight: 500;
}

/* Service Section */
.service-section {
  background: #fdfaf6;
  border: 1px solid #f0e6d2;
  border-radius: 8px;
  padding: 24px;
  text-align: center;
}
.service-section h3 {
  margin: 0 0 10px 0;
  color: #887864;
}
.service-desc {
  color: #666;
  margin-bottom: 20px;
}
.service-btn {
  background: #C9A96E;
  color: #fff;
  border: none;
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: background 0.2s;
}
.service-btn:hover {
  background: #b58a46;
}

/* Modal */
.modal-overlay {
  position: fixed;
  top: 0; left: 0; width: 100vw; height: 100vh;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}
.modal-content {
  background: #fff;
  padding: 30px;
  border-radius: 12px;
  width: 90%;
  max-width: 450px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.2);
}
.modal-content h3 {
  margin: 0 0 20px 0;
  color: #333;
}
.form-group {
  margin-bottom: 20px;
}
.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}
.form-group select, .form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  font-family: inherit;
  resize: none;
}
.form-group select:focus, .form-group textarea:focus {
  outline: none;
  border-color: #C9A96E;
}
.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
}
.btn-cancel {
  background: #f0f0f0;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  color: #666;
}
.btn-submit {
  background: #C9A96E;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  color: #fff;
  font-weight: 500;
}
.btn-submit:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.pagination-controls {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 20px;
  margin-top: 24px;
}

.page-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background-color: transparent;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  color: #333;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  border-color: #C9A96E;
  color: #C9A96E;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
  background-color: #f9f9f9;
}

.page-info {
  font-size: 14px;
  color: #666;
}

/* 美化提示 Modal */
.alert-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.alert-modal {
  background: white;
  width: 90%;
  max-width: 320px;
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  animation: modalScaleUp 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.alert-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  margin: 0 auto 16px;
  color: white;
}

.alert-icon.success {
  background: #C9A96E;
  box-shadow: 0 4px 12px rgba(201, 169, 110, 0.3);
}

.alert-icon.error {
  background: #e74c3c;
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.3);
}

.alert-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
}

.alert-message {
  font-size: 15px;
  color: #666;
  margin: 0 0 24px;
  line-height: 1.5;
}

.btn-alert {
  background: #333;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 500;
  width: 100%;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-alert:hover {
  background: #555;
}

@keyframes modalScaleUp {
  from { opacity: 0; transform: scale(0.9); }
  to { opacity: 1; transform: scale(1); }
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
