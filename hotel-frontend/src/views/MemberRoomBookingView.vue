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
              <span class="value">{{
                getRoomTypeName(selectedBooking.roomTypeId)
              }}</span>
            </div>
            <div class="detail-item">
              <span class="label">入住人數</span>
              <span class="value">{{ selectedBooking.guestNum }} 人</span>
            </div>
            <div class="detail-item">
              <span class="label">訂單金額</span>
              <span class="value price-value"
                >NT$
                {{ (selectedBooking.bookingPrice || 0).toLocaleString() }}</span
              >
            </div>
            <div
              class="detail-item"
              v-if="selectedBooking.bookingStatus === '已入住'"
            >
              <span class="label">房號</span>
              <span class="value">{{
                getRoomNumber(selectedBooking.roomId)
              }}</span>
            </div>
            <div class="detail-item">
              <span class="label">狀態</span>
              <span
                class="value status-badge"
                :class="getStatusClass(selectedBooking.bookingStatus)"
              >
                {{ selectedBooking.bookingStatus }}
              </span>
            </div>
          </div>

          <!-- 辦理入住 -->
          <div class="service-section" v-if="canCheckIn(selectedBooking)">
            <h3>辦理入住</h3>
            <p class="service-desc">
              您可以線上產生入住 QR Code，並至櫃檯或自助機完成驗證。
            </p>
            <button class="service-btn" @click="generateQrCode">
              產生入住 QR Code
            </button>
          </div>

          <!-- 申請房務 -->
          <div
            class="service-section"
            v-if="
              selectedBooking.bookingStatus === '已入住' &&
              selectedBooking.roomId
            "
          >
            <h3>🧽 需要房務服務嗎？</h3>
            <p class="service-desc">
              由於您目前正在入住期間，您可以直接向櫃檯申請房務服務。
            </p>
            <button class="service-btn" @click="showServiceModal = true">
              申請房務服務
            </button>
          </div>

          <!-- 取消訂房 (符合 > 3 天政策) -->
          <div
            class="service-section cancel-section"
            v-if="canCancelBooking(selectedBooking)"
          >
            <h3>❌ 取消訂房服務</h3>
            <p class="service-desc">
              距離入住日尚有
              <strong>{{
                getDaysUntilCheckIn(selectedBooking.checkInDate)
              }}</strong>
              天（符合入住 3 天前免費取消政策）。取消後將為您辦理全額退款。
            </p>
            <button class="btn-cancel-booking" @click="showCancelModal = true">
              申請取消訂房並全額退款
            </button>
          </div>

          <!-- 取消政策限制提醒 (不足 3 天) -->
          <div
            class="service-section notice-section"
            v-if="
              selectedBooking.bookingStatus === '待入住' &&
              !canCancelBooking(selectedBooking)
            "
          >
            <h3>⚠️ 取消政策提醒</h3>
            <p class="service-desc">
              距離入住日僅剩
              <strong>{{
                getDaysUntilCheckIn(selectedBooking.checkInDate)
              }}</strong>
              天。依飯店取消政策，入住前 3
              天內無法於線上自主取消退款。如需特殊協助，請致電飯店客服專線：02-2345-6789。
            </p>
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
            class="booking-card clickable-card"
            @click="selectedBooking = booking"
          >
            <div class="booking-header">
              <span class="booking-id"
                >第 {{ getBookingIndex(booking) }} 次訂房</span
              >
              <div style="display: flex; gap: 8px; align-items: center">
                <span v-if="canCancelBooking(booking)" class="policy-tag"
                  >可免費取消</span
                >
                <span
                  class="status-badge"
                  :class="getStatusClass(booking.bookingStatus)"
                >
                  {{ booking.bookingStatus }}
                </span>
              </div>
            </div>
            <div class="booking-body">
              <div class="date-row">
                <span class="date-label">入住：</span> {{ booking.checkInDate }}
                <span class="date-separator">|</span>
                <span class="date-label">退房：</span>
                {{ booking.checkOutDate }}
              </div>
              <div class="room-info">
                房型：{{ getRoomTypeName(booking.roomTypeId) }}
                <span class="date-separator">|</span>
                入住人數：{{ booking.guestNum }} 人
                <span class="date-separator">|</span>
                金額：<strong style="color: #b54708"
                  >NT$
                  {{ (booking.bookingPrice || 0).toLocaleString() }}</strong
                >
                <template v-if="booking.bookingStatus === '已入住'">
                  <span class="date-separator">|</span>
                  房號：{{ getRoomNumber(booking.roomId) }}
                </template>
              </div>
            </div>
          </div>
        </div>

        <div
          v-if="
            totalPages > 1 &&
            !selectedBooking &&
            !loading &&
            bookings.length > 0
          "
          class="pagination-controls"
        >
          <button
            @click="prevPage"
            :disabled="currentPage === 1"
            class="page-btn"
          >
            <span class="icon">←</span> 上一頁
          </button>
          <span class="page-info"
            >第 {{ currentPage }} 頁 / 共 {{ totalPages }} 頁</span
          >
          <button
            @click="nextPage"
            :disabled="currentPage === totalPages"
            class="page-btn"
          >
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
              <textarea
                v-model="taskForm.remark"
                rows="3"
                placeholder="例如：需要兩條毛巾、冷氣不冷..."
              ></textarea>
            </div>
            <div class="modal-actions">
              <button
                type="button"
                class="btn-cancel"
                @click="showServiceModal = false"
              >
                取消
              </button>
              <button
                type="submit"
                class="btn-submit"
                :disabled="submittingTask"
              >
                {{ submittingTask ? "送出中..." : "確認送出" }}
              </button>
            </div>
          </form>
        </div>
      </div>

      <!-- 取消訂房確認 Modal -->
      <div v-if="showCancelModal" class="modal-overlay">
        <div class="modal-content cancel-modal-content">
          <div class="cancel-modal-header">
            <div class="cancel-icon-circle">✕</div>
            <h3>確認取消訂房</h3>
          </div>
          <p class="cancel-desc">
            您即將取消
            <strong>第 {{ getBookingIndex(selectedBooking) }} 次訂房</strong
            >（房型：{{ getRoomTypeName(selectedBooking?.roomTypeId) }}）。
          </p>
          <div class="cancel-summary-box">
            <div class="summary-line">
              <span class="label">原入住日期</span>
              <span class="value">{{ selectedBooking?.checkInDate }}</span>
            </div>
            <div class="summary-line">
              <span class="label">原退房日期</span>
              <span class="value">{{ selectedBooking?.checkOutDate }}</span>
            </div>
            <div class="summary-line">
              <span class="label">預計全額退款</span>
              <span class="value refund-price"
                >NT$
                {{
                  (selectedBooking?.bookingPrice || 0).toLocaleString()
                }}</span
              >
            </div>
          </div>
          <p class="cancel-policy-note">
            ※ 款項預計將於 3~7 個工作天內刷退回您原本付款之信用卡帳戶。
          </p>
          <div class="modal-actions">
            <button
              type="button"
              class="btn-cancel"
              @click="showCancelModal = false"
              :disabled="cancelling"
            >
              返回
            </button>
            <button
              type="button"
              class="btn-danger"
              @click="performCancelBooking"
              :disabled="cancelling"
            >
              {{ cancelling ? "處理中..." : "確認取消並退款" }}
            </button>
          </div>
        </div>
      </div>

      <!-- 入住 QR Code Modal -->
      <div v-if="showQrModal" class="modal-overlay">
        <div class="modal-content qr-modal-content">
          <h3>您的入住驗證碼</h3>
          <p class="qr-desc">請向櫃檯人員或自助報到機出示此 QR Code</p>
          <div class="qr-code-wrapper">
            <img
              :src="`https://api.qrserver.com/v1/create-qr-code/?size=200x200&data=${encodeURIComponent(ACTIVE_WIFI_IP + '/mobile-pass?code=' + verificationCode + '&room=' + getRoomNumber(selectedBooking.roomId))}`"
              alt="Check-in QR Code"
            />
          </div>
          <div class="verification-code">
            驗證碼：<strong>{{ verificationCode }}</strong>
          </div>
          <div class="modal-actions qr-actions">
            <button
              type="button"
              class="btn-cancel"
              @click="showQrModal = false"
            >
              關閉
            </button>
            <button
              type="button"
              class="btn-submit"
              @click="performCheckIn"
              :disabled="checkingIn"
            >
              {{ checkingIn ? "驗證中..." : "模擬掃描完成入住" }}
            </button>
          </div>
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
import { ref, computed, onMounted } from "vue";
import { bookingApi } from "@/api/bookingApi";

// ==========================================
// 手機端測試 Wifi IP 切換區 (Demo 專用)
// ==========================================
const ACTIVE_WIFI_IP = "http://172.22.45.103:5173"; // R201 Wifi
// const ACTIVE_WIFI_IP = 'http://172.22.41.173:5173'; // R301 Wifi

const currentDomain = window.location.origin;
const loading = ref(true);
const bookings = ref([]);
const rooms = ref([]);
const roomTypes = ref([]);
const selectedBooking = ref(null);
const checkingIn = ref(false);
const showQrModal = ref(false);
const verificationCode = ref("");

const showCancelModal = ref(false);
const cancelling = ref(false);

const showServiceModal = ref(false);
const submittingTask = ref(false);
const taskForm = ref({
  taskType: "日常清潔",
  remark: "",
});

const alertConfig = ref({
  show: false,
  type: "success",
  title: "",
  message: "",
});

function showAlert(type, title, message) {
  alertConfig.value = { show: true, type, title, message };
}

function closeAlert() {
  alertConfig.value.show = false;
}

const currentPage = ref(1);
const itemsPerPage = 4;

const paginatedBookings = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  const end = start + itemsPerPage;
  return bookings.value.slice(start, end);
});

const totalPages = computed(() => {
  return Math.ceil(bookings.value.length / itemsPerPage);
});

function prevPage() {
  if (currentPage.value > 1) currentPage.value--;
}

function nextPage() {
  if (currentPage.value < totalPages.value) currentPage.value++;
}

onMounted(async () => {
  await loadRoomTypes();
  await loadRooms();
  await loadBookings();
});

async function loadRoomTypes() {
  try {
    const res = await fetch("/api/roomtypes");
    if (res.ok) {
      roomTypes.value = await res.json();
    }
  } catch (e) {
    console.error("Failed to load room types:", e);
  }
}

async function loadRooms() {
  try {
    const token = localStorage.getItem("token");
    const res = await fetch("/api/rooms", {
      headers: token ? { Authorization: `Bearer ${token}` } : {},
    });
    if (res.ok) {
      rooms.value = await res.json();
    }
  } catch (e) {
    console.error("Failed to load rooms:", e);
  }
}

function getRoomNumber(roomId) {
  if (!roomId) return "尚未分配";
  const room = rooms.value.find((r) => r.roomId === roomId);
  return room ? room.roomNumber : roomId;
}

function getRoomTypeName(typeId) {
  if (!typeId) return "未知房型";
  const type = roomTypes.value.find((t) => t.roomTypeId === typeId);
  return type ? type.typeName : typeId;
}

function getBookingIndex(booking) {
  const idx = bookings.value.findIndex(
    (b) => b.bookingId === booking.bookingId,
  );
  if (idx === -1) return "";
  return bookings.value.length - idx;
}

async function loadBookings() {
  loading.value = true;
  try {
    const token = localStorage.getItem("token");
    if (!token) return;

    // 1. 取得 memberId
    const meRes = await fetch("/api/members/me", {
      headers: { Authorization: `Bearer ${token}` },
    });
    const memberData = await meRes.json();
    const memberId = memberData.memberId;

    if (!memberId) return;

    // 2. 搜尋該 memberId 的訂單
    const bookingRes = await fetch("/api/bookings/search", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify({ memberId }),
    });

    if (bookingRes.ok) {
      let data = await bookingRes.json();
      // 依建立時間或 ID 排序 (最新在最上面)
      data.sort((a, b) => b.bookingId - a.bookingId);
      bookings.value = data;
    }
  } catch (e) {
    console.error("Failed to load bookings:", e);
  } finally {
    loading.value = false;
  }
}

async function submitRoomTask() {
  if (!selectedBooking.value || !selectedBooking.value.roomId) return;
  submittingTask.value = true;

  try {
    const token = localStorage.getItem("token");
    const randomEmployeeId = Math.floor(Math.random() * (24 - 13 + 1)) + 13;
    const payload = {
      roomId: selectedBooking.value.roomId,
      taskType: taskForm.value.taskType,
      remark: taskForm.value.remark || "無",
      priority: "一般",
      taskStatus: "待處理",
      employeeId: randomEmployeeId,
    };

    const res = await fetch("/api/roomtask", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
      body: JSON.stringify(payload),
    });

    if (res.ok) {
      showAlert("success", "需求已建立", "您房務需求已建立,請您稍後,感謝您");
      showServiceModal.value = false;
      taskForm.value = { taskType: "日常清潔", remark: "" };
    } else {
      showAlert("error", "申請失敗", "申請失敗，請稍後再試");
    }
  } catch (e) {
    console.error("Submit task failed:", e);
  } finally {
    submittingTask.value = false;
  }
}

function canCheckIn(booking) {
  if (!booking || booking.bookingStatus !== "待入住") return false;

  const now = new Date();
  const checkInDateObj = new Date(booking.checkInDate + "T15:00:00");

  return now >= checkInDateObj;
}

function getDaysUntilCheckIn(checkInDateStr) {
  if (!checkInDateStr) return 0;
  const today = new Date();
  today.setHours(0, 0, 0, 0);
  const checkIn = new Date(checkInDateStr);
  checkIn.setHours(0, 0, 0, 0);
  const diffTime = checkIn.getTime() - today.getTime();
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
}

function canCancelBooking(booking) {
  if (!booking || booking.bookingStatus !== "待入住") return false;
  return getDaysUntilCheckIn(booking.checkInDate) > 3;
}

async function performCancelBooking() {
  if (!selectedBooking.value) return;
  cancelling.value = true;

  try {
    const token = localStorage.getItem("token");
    let memberId = null;
    if (token) {
      try {
        const meRes = await fetch("/api/members/me", {
          headers: { Authorization: `Bearer ${token}` },
        });
        if (meRes.ok) {
          const memberData = await meRes.json();
          memberId = memberData.memberId;
        }
      } catch (err) {
        console.warn("取得會員 ID 失敗，使用備用參數", err);
      }
    }

    const res = await bookingApi.cancelBooking(
      selectedBooking.value.bookingId,
      { memberId },
    );

    if (res && res.success) {
      showCancelModal.value = false;
      showAlert(
        "success",
        "訂房取消成功",
        "您的預訂已成功取消，全額退款手續已啟動，並已發送確認信件至您的信箱！",
      );

      if (res.booking) {
        selectedBooking.value = res.booking;
        const index = bookings.value.findIndex(
          (b) => b.bookingId === res.booking.bookingId,
        );
        if (index !== -1) {
          bookings.value[index] = res.booking;
        }
      } else {
        await loadBookings();
      }
    } else {
      showAlert(
        "error",
        "取消失敗",
        res?.message || "無法取消訂房，請稍後再試",
      );
    }
  } catch (e) {
    console.error("Cancel booking failed:", e);
    showAlert(
      "error",
      "取消失敗",
      e.message || "取消訂房發生錯誤，請稍後再試或聯繫客服",
    );
  } finally {
    cancelling.value = false;
  }
}

async function generateQrCode() {
  if (!selectedBooking.value) return;

  try {
    const bookingInfo = `${selectedBooking.value.bookingId}-${selectedBooking.value.memberId || "M"}-${selectedBooking.value.checkInDate}`;
    let numericCode = "";

    if (window.crypto && window.crypto.subtle) {
      const msgBuffer = new TextEncoder().encode(bookingInfo);
      const hashBuffer = await crypto.subtle.digest("SHA-256", msgBuffer);
      const hashArray = Array.from(new Uint8Array(hashBuffer));
      const hashHex = hashArray
        .map((b) => b.toString(16).padStart(2, "0"))
        .join("");
      numericCode = BigInt("0x" + hashHex.substring(0, 12)).toString();
    } else {
      // 在非 HTTPS 環境下 (例如手機連內網 IP 測試時)，crypto.subtle 會是 undefined
      // 我們使用簡單的隨機數作為 Fallback
      numericCode = Math.floor(
        10000000000000 + Math.random() * 90000000000000,
      ).toString();
    }

    verificationCode.value = numericCode;
    showQrModal.value = true;
  } catch (err) {
    console.error("產生 QR Code 時發生錯誤:", err);
    verificationCode.value = Math.floor(
      10000000000000 + Math.random() * 90000000000000,
    ).toString();
    showQrModal.value = true;
  }
}

async function performCheckIn() {
  if (!selectedBooking.value) return;
  checkingIn.value = true;

  try {
    const token = localStorage.getItem("token");

    // 找出該房型的空房 (此處為前端模擬分配房號，實際應由後端處理)
    const availableRooms = rooms.value.filter(
      (r) => r.roomTypeId === selectedBooking.value.roomTypeId,
    );
    let assignedRoomId = selectedBooking.value.roomId;

    if (!assignedRoomId) {
      if (availableRooms.length > 0) {
        const randIndex = Math.floor(Math.random() * availableRooms.length);
        assignedRoomId = availableRooms[randIndex].roomId;
      } else {
        assignedRoomId = Math.floor(Math.random() * 100) + 1;
      }
    }

    const payload = {
      ...selectedBooking.value,
      roomId: assignedRoomId,
      bookingStatus: "已入住",
    };

    const res = await fetch(
      `/api/bookings/${selectedBooking.value.bookingId}`,
      {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(payload),
      },
    );

    if (res.ok) {
      const updatedBooking = await res.json();

      const index = bookings.value.findIndex(
        (b) => b.bookingId === selectedBooking.value.bookingId,
      );
      if (index !== -1) {
        bookings.value[index] = updatedBooking;
      }
      selectedBooking.value = updatedBooking;

      showQrModal.value = false;
      showAlert(
        "success",
        "入住成功",
        `已完成驗證，為您分配的房號為 ${getRoomNumber(assignedRoomId)}`,
      );
    } else {
      showAlert("error", "入住失敗", "驗證失敗，請聯絡櫃檯人員");
    }
  } catch (e) {
    console.error("Check-in failed:", e);
    showAlert("error", "系統錯誤", "驗證發生錯誤");
  } finally {
    checkingIn.value = false;
  }
}

function getStatusClass(status) {
  switch (status) {
    case "已入住":
      return "status-active";
    case "已退房":
      return "status-done";
    case "已取消":
      return "status-cancel";
    case "待入住":
      return "status-pending";
    default:
      return "status-default";
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
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
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
.loading-state,
.empty-state {
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
@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

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
  border-color: #c9a96e;
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
.status-active {
  background: #e6f7e9;
  color: #2e7d32;
}
.status-done {
  background: #f0f0f0;
  color: #666;
}
.status-cancel {
  background: #ffebee;
  color: #c62828;
}
.status-pending {
  background: #fff3e0;
  color: #ef6c00;
}
.status-default {
  background: #e3f2fd;
  color: #1565c0;
}

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
  background: #c9a96e;
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
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.5);
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
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
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
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 1rem;
  font-family: inherit;
  resize: none;
}
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #c9a96e;
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
  background: #c9a96e;
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

.qr-modal-content {
  text-align: center;
}
.qr-desc {
  color: #666;
  margin-bottom: 20px;
}
.qr-code-wrapper {
  margin: 20px auto;
  padding: 15px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 8px;
  display: inline-block;
}
.qr-code-wrapper img {
  display: block;
}
.verification-code {
  font-size: 1.1rem;
  color: #333;
  margin-bottom: 20px;
}
.verification-code strong {
  color: #c9a96e;
  letter-spacing: 2px;
}
.qr-actions {
  justify-content: center;
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
  border-color: #c9a96e;
  color: #c9a96e;
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
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.1);
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
  background: #c9a96e;
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
  from {
    opacity: 0;
    transform: scale(0.9);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 價格與政策標籤樣式 */
.price-value {
  color: #b54708 !important;
  font-weight: 700 !important;
}

.policy-tag {
  background: #e8f5e9;
  color: #2e7d32;
  font-size: 0.75rem;
  font-weight: 600;
  padding: 3px 8px;
  border-radius: 12px;
  border: 1px solid #c8e6c9;
}

/* 取消與提醒區塊樣式 */
.cancel-section {
  background: #fff8f8 !important;
  border: 1px solid #ffcdd2 !important;
  margin-top: 20px;
}
.cancel-section h3 {
  color: #c62828 !important;
}

.notice-section {
  background: #fffbf0 !important;
  border: 1px solid #ffe082 !important;
  margin-top: 20px;
}
.notice-section h3 {
  color: #d97706 !important;
}

.btn-cancel-booking {
  background: #fff;
  color: #c62828;
  border: 1.5px solid #c62828;
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}
.btn-cancel-booking:hover {
  background: #c62828;
  color: #fff;
}

/* 取消 Modal 樣式 */
.cancel-modal-content {
  max-width: 480px;
}

.cancel-modal-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.cancel-icon-circle {
  width: 36px;
  height: 36px;
  background: #ffebee;
  color: #c62828;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  font-weight: bold;
}

.cancel-modal-header h3 {
  margin: 0;
  color: #2c1810;
  font-size: 1.3rem;
}

.cancel-desc {
  color: #555;
  font-size: 0.95rem;
  line-height: 1.6;
  margin-bottom: 20px;
}

.cancel-summary-box {
  background: #faf7f2;
  border: 1px solid #eee5d8;
  border-left: 4px solid #c9a96e;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 16px;
}

.summary-line {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 0.95rem;
}
.summary-line:last-child {
  margin-bottom: 0;
  padding-top: 8px;
  border-top: 1px dashed #ddd;
}
.summary-line .label {
  color: #777;
}
.summary-line .value {
  color: #333;
  font-weight: 500;
}
.refund-price {
  color: #2e7d32 !important;
  font-size: 1.15rem !important;
  font-weight: 700 !important;
}

.cancel-policy-note {
  font-size: 0.85rem;
  color: #888;
  line-height: 1.5;
  margin-bottom: 24px;
}

.btn-danger {
  background: #c62828;
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 600;
  transition: background 0.2s;
}
.btn-danger:hover:not(:disabled) {
  background: #b71c1c;
}
.btn-danger:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>
