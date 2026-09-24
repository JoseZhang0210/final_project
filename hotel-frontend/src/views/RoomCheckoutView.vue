<template>
  <div class="checkout-container">
    <div class="step-bar">
      <div class="step completed clickable" @click="goToSearch">❶ 搜尋</div>
      <div class="step-line" :class="{ active: isPaymentSuccess }"></div>
      <div class="step completed clickable" @click="goToSelection">❷ 選房</div>
      <div class="step-line" :class="{ active: isPaymentSuccess }"></div>
      <div
        class="step"
        :class="{ active: !isPaymentSuccess, completed: isPaymentSuccess }"
      >
        ❸ {{ isPaymentSuccess ? "預訂完成" : "確認 & 結帳" }}
      </div>
    </div>

    <!-- 付款成功畫面 (新分頁付款完成後，主分頁或新分頁均可完整展示) -->
    <div v-if="isPaymentSuccess" class="success-screen">
      <div class="success-card">
        <div class="success-badge">
          <span class="badge-icon">✓</span>
        </div>
        <h2 class="success-title">預訂與付款成功！</h2>
        <p class="success-subtitle">
          感謝您的預訂，我們已收到您的款項並完成訂房確認。
        </p>

        <div class="order-summary-box">
          <div class="summary-item" v-if="completedBookingId">
            <span class="item-label">訂單編號</span>
            <span class="item-value booking-id">#{{ completedBookingId }}</span>
          </div>
          <div class="summary-item" v-if="roomName">
            <span class="item-label">預訂房型</span>
            <span class="item-value">{{ roomName }}</span>
          </div>
          <div class="summary-item" v-if="checkIn">
            <span class="item-label">入住日期</span>
            <span class="item-value">{{ checkIn }}</span>
          </div>
          <div class="summary-item" v-if="checkOut">
            <span class="item-label">退房日期</span>
            <span class="item-value">{{ checkOut }}</span>
          </div>
          <div class="summary-item" v-if="guests">
            <span class="item-label">入住人數</span>
            <span class="item-value">{{ guests }} 人</span>
          </div>
          <div class="summary-item" v-if="form.name">
            <span class="item-label">訂房聯絡人</span>
            <span class="item-value">{{ form.name }} ({{ form.phone }})</span>
          </div>
          <div class="summary-divider"></div>
          <div class="summary-item total-item">
            <span class="item-label">實付金額</span>
            <span class="item-value total-price">
              NT$ {{ totalPrice.toLocaleString() }}
              <span class="paid-badge">已信用卡付款</span>
            </span>
          </div>
        </div>

        <div class="email-notice">
          <span class="notice-icon">📬</span>
          <p>
            訂房確認信與快速入住
            <strong>Check-in QR Code</strong> 已發送至您的電子信箱
            <span class="user-email">{{ form.email }}</span
            >。入住時出示 QR Code 即可快速辦理入住手續！
          </p>
        </div>

        <div class="action-buttons">
          <button class="btn-action btn-outline" @click="goToMyOrders">
            查看我的訂單
          </button>
          <button class="btn-action btn-gold" @click="goToHome">
            返回首頁
          </button>
        </div>
      </div>
    </div>

    <!-- 正常填寫結帳資料畫面 -->
    <div v-else>
      <div class="header-title">
        <h2>CHECKOUT</h2>
        <p class="subtitle">請確認您的訂房明細並完成結帳</p>
      </div>

      <div class="content-grid">
        <!-- 訂房資料填寫 -->
        <div class="form-section">
          <h3 class="section-title">1. 訂房聯絡人資訊</h3>
          <form class="checkout-form">
            <div class="form-group">
              <label>姓名 *</label>
              <input
                type="text"
                v-model="form.name"
                required
                placeholder="例如：王小明"
              />
            </div>
            <div class="form-group">
              <label>電子信箱 * (確認信與 Check-in QR Code 將寄送至此)</label>
              <input
                type="email"
                v-model="form.email"
                required
                placeholder="例如：user@example.com"
              />
            </div>
            <div class="form-group">
              <label>手機號碼 *</label>
              <input
                type="tel"
                v-model="form.phone"
                required
                placeholder="例如：0912345678"
              />
            </div>
            <div class="form-group">
              <label>特別需求備註 (選填)</label>
              <textarea
                v-model="form.remark"
                rows="3"
                placeholder="例如：需要嬰兒床、對某些食物過敏等"
              ></textarea>
            </div>
          </form>

          <h3 class="mt-4">2. 選擇付款方式</h3>
          <div class="payment-options">
            <label class="payment-option selected">
              <input type="radio" checked />
              <div class="option-content">
                <span class="icon">💳</span>
                <span>線上刷卡 (綠界科技 ECPay)</span>
              </div>
            </label>
          </div>

          <button
            class="btn-checkout"
            :disabled="isProcessing"
            @click="submitCheckout"
          >
            {{ isProcessing ? "處理中，請稍候..." : "確認結帳" }}
          </button>
        </div>

        <!-- 訂單明細 -->
        <div class="summary-sidebar">
          <div class="summary-card">
            <div class="summary-header">訂單明細</div>
            <div class="summary-content">
              <h4 v-if="roomName">{{ roomName }}</h4>
              <div class="summary-row">
                <span class="label">入住日期</span>
                <span class="value">{{ checkIn }}</span>
              </div>
              <div class="summary-row">
                <span class="label">退房日期</span>
                <span class="value">{{ checkOut }}</span>
              </div>
              <div class="summary-row">
                <span class="label">入住人數</span>
                <span class="value">{{ guests }} 人</span>
              </div>

              <div class="divider"></div>

              <div class="summary-row total">
                <span class="label">總金額</span>
                <span class="value">NT$ {{ totalPrice.toLocaleString() }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 等待付款視窗 (Modal) -->
        <div v-if="showPaymentModal" class="payment-modal-overlay">
          <div class="payment-modal">
            <h3>💳 綠界金流付款進行中</h3>
            <p>已在安全新分頁開啟綠界刷卡頁面，請在該分頁完成付款。</p>
            <div class="spinner"></div>
            <p class="modal-hint">
              付款完成後，本畫面將自動更新為預訂成功明細！
            </p>

            <!-- 開發/Demo測試用快速按鈕 -->
            <div class="dev-tools">
              <button @click="forceMockSuccess" class="btn-mock">
                ⚡ Demo 模擬付款成功
              </button>
            </div>

            <button @click="cancelPaymentWait" class="btn-cancel mt-4">
              返回修改訂單
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 美化版提示 Modal -->
    <AlertModal
      :show="alertConfig.show"
      :type="alertConfig.type"
      :title="alertConfig.title"
      :message="alertConfig.message"
      @close="closeAlert"
    />

    <!-- ECPay 隱藏表單容器 -->
    <div ref="ecpayFormContainer" style="display: none"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from "vue";
import { useRoute, useRouter } from "vue-router";
import { bookingApi } from "../api/bookingApi";
import { useAuthStore } from "../stores/auth";
import AlertModal from "../components/common/AlertModal.vue";

const route = useRoute();
const router = useRouter();

const alertConfig = ref({
  show: false,
  type: "success",
  title: "",
  message: "",
  onClose: null,
});

function showAlert(type, title, message, onClose = null) {
  alertConfig.value = { show: true, type, title, message, onClose };
}

function closeAlert() {
  const onClose = alertConfig.value.onClose;
  alertConfig.value.show = false;
  if (onClose) onClose();
}

const authStore = useAuthStore();

// 預訂完成狀態管理
const isPaymentSuccess = ref(false);
const completedBookingId = ref(null);

function goToMyOrders() {
  router.push({ name: "member-room-bookings" });
}

function goToHome() {
  router.push("/");
}

// 從 URL Query 取得選擇的資料
const roomTypeId = ref(Number(route.query.roomTypeId));
const roomName = ref(route.query.roomName);
const checkIn = ref(route.query.checkIn);
const checkOut = ref(route.query.checkOut);
const guests = ref(Number(route.query.guests));
const totalPrice = ref(Number(route.query.price) || 0);

function goToSearch() {
  router.push("/room-booking");
}

function goToSelection() {
  router.push({
    name: "room-selection",
    query: {
      checkIn: checkIn.value,
      checkOut: checkOut.value,
      guests: guests.value,
    },
  });
}

const isProcessing = ref(false);
const showPaymentModal = ref(false);
const pollingInterval = ref(null);
const currentBookingId = ref(null);
const ecpayFormContainer = ref(null);

// 跨分頁即時同步廣播頻道
let broadcastChannel = null;

// 監聽跨分頁通訊事件
function handleStorageChange(event) {
  if (event.key === "ecpay_booking_success" && event.newValue) {
    try {
      const data = JSON.parse(event.newValue);
      if (data && data.bookingId) {
        onPaymentFinished(data.bookingId);
      }
    } catch (e) {
      console.error(e);
    }
  }
}

function onPaymentFinished(bookingId) {
  if (pollingInterval.value) {
    clearInterval(pollingInterval.value);
  }
  showPaymentModal.value = false;
  completedBookingId.value = bookingId;
  isPaymentSuccess.value = true;
}

onUnmounted(() => {
  if (pollingInterval.value) {
    clearInterval(pollingInterval.value);
  }
  window.removeEventListener("storage", handleStorageChange);
  if (broadcastChannel) {
    broadcastChannel.close();
  }
});

const form = ref({
  name: "",
  email: "",
  phone: "",
  remark: "",
});

function startPolling(bookingId) {
  currentBookingId.value = bookingId;
  showPaymentModal.value = true;

  // 每 2 秒詢問一次後端狀態
  pollingInterval.value = setInterval(async () => {
    try {
      const res = await fetch(`/api/payments/ecpay/status/${bookingId}`);
      if (res.ok) {
        const data = await res.json();
        if (data.status === "已付款") {
          onPaymentFinished(bookingId);
        }
      }
    } catch (e) {
      console.error("輪詢狀態失敗", e);
    }
  }, 2000);
}

async function cancelPaymentWait() {
  if (pollingInterval.value) {
    clearInterval(pollingInterval.value);
  }
  showPaymentModal.value = false;

  // 使用者放棄結帳，刪除剛建立的訂單與付款記錄
  if (currentBookingId.value) {
    try {
      const token = localStorage.getItem("token");
      await fetch(`/api/bookings/${currentBookingId.value}`, {
        method: "DELETE",
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      console.log(`已刪除未完成之訂單 ID: ${currentBookingId.value}`);
    } catch (e) {
      console.error("刪除未完成訂單失敗", e);
    }
    currentBookingId.value = null;
  }
}

async function forceMockSuccess() {
  if (!currentBookingId.value) return;
  try {
    const res = await fetch(
      `/api/payments/ecpay/mock-pay/${currentBookingId.value}`,
      {
        method: "POST",
      },
    );
    if (res.ok) {
      onPaymentFinished(currentBookingId.value);
    } else {
      showAlert("error", "模擬失敗", "無法完成強制模擬付款！");
    }
  } catch (e) {
    console.error(e);
  }
}

onMounted(async () => {
  // 設定跨分頁監聽器
  window.addEventListener("storage", handleStorageChange);
  if (typeof BroadcastChannel !== "undefined") {
    broadcastChannel = new BroadcastChannel("ecpay_channel");
    broadcastChannel.onmessage = (event) => {
      if (event.data && event.data.type === "PAYMENT_SUCCESS") {
        onPaymentFinished(event.data.bookingId);
      }
    };
  }

  // 如果是從綠界跳轉回來且帶有成功標記 (在新分頁或直接開啟)
  if (route.query.paymentSuccess) {
    const bookingId = route.query.bookingId;
    if (bookingId) {
      completedBookingId.value = bookingId;
      try {
        // 主動通知後端更新狀態為「已付款」，並觸發寄送確認信與 QR Code (具備冪等性防護)
        await fetch(`/api/payments/ecpay/client-confirm/${bookingId}`, {
          method: "POST",
        });
      } catch (err) {
        console.warn("付款狀態同步失敗:", err);
      }

      // 跨分頁通知原本的主視窗
      try {
        localStorage.setItem(
          "ecpay_booking_success",
          JSON.stringify({ bookingId, time: Date.now() }),
        );
        if (broadcastChannel) {
          broadcastChannel.postMessage({
            type: "PAYMENT_SUCCESS",
            bookingId,
          });
        }
      } catch (err) {
        console.warn("廣播通知失敗:", err);
      }
    }

    // 還原訂單資訊
    const savedParams = localStorage.getItem("checkoutParams");
    if (savedParams) {
      try {
        const parsed = JSON.parse(savedParams);
        if (parsed.roomTypeId) roomTypeId.value = parsed.roomTypeId;
        if (parsed.roomName) roomName.value = parsed.roomName;
        if (parsed.checkIn) checkIn.value = parsed.checkIn;
        if (parsed.checkOut) checkOut.value = parsed.checkOut;
        if (parsed.guests) guests.value = parsed.guests;
        if (parsed.price) totalPrice.value = Number(parsed.price) || 0;
        if (parsed.name) form.value.name = parsed.name;
        if (parsed.email) form.value.email = parsed.email;
        if (parsed.phone) form.value.phone = parsed.phone;
        if (parsed.remark) form.value.remark = parsed.remark;
      } catch (e) {
        console.error("解析 checkoutParams 失敗", e);
      }
    }

    // 若在新開的分頁完成，嘗試自動關閉該分頁並讓主分頁接手；若不給關閉則直接展示成功畫面
    if (window.name === "ECPayTab" || window.opener) {
      setTimeout(() => {
        try {
          window.close();
        } catch (e) {}
      }, 1000);
    }

    isPaymentSuccess.value = true;
    return;
  }

  // 若已登入，自動載入會員資料填入聯絡人表單
  if (authStore.isLoggedIn) {
    const token = localStorage.getItem("token");
    if (token) {
      fetch("/api/members/me", {
        headers: { Authorization: "Bearer " + token },
      })
        .then((res) => (res.ok ? res.json() : null))
        .then((data) => {
          if (data) {
            if (data.name && !form.value.name) form.value.name = data.name;
            if (data.email && !form.value.email) form.value.email = data.email;
            if (data.phone && !form.value.phone) form.value.phone = data.phone;
          }
        })
        .catch((err) => console.warn("載入會員資料失敗:", err));
    }
  }

  // 正常進入檢查參數
  if (!roomTypeId.value || !checkIn.value || !checkOut.value) {
    showAlert("error", "缺少參數", "缺少訂房參數，請重新選擇房型", () => {
      router.push("/room-booking");
    });
  }
});

async function submitCheckout() {
  if (!form.value.name || !form.value.phone) {
    showAlert("error", "表單未完成", "請填寫姓名與手機號碼！");
    return;
  }

  isProcessing.value = true;
  try {
    let currentMemberId = 1;
    if (authStore.isLoggedIn) {
      try {
        const token = localStorage.getItem("token");
        const profileRes = await fetch("/api/members/me", {
          headers: { Authorization: "Bearer " + token },
        });
        if (profileRes.ok) {
          const profileData = await profileRes.json();
          if (profileData && profileData.memberId) {
            currentMemberId = profileData.memberId;
          }
        }
      } catch (err) {
        console.warn("無法取得會員資料，使用預設 ID", err);
      }
    }

    const bookingPayload = {
      memberId: currentMemberId,
      roomTypeId: roomTypeId.value,
      checkInDate: checkIn.value,
      checkOutDate: checkOut.value,
      guestNum: guests.value,
      bookingPrice: totalPrice.value,
      bookingStatus: "待入住",
    };

    const createdBooking = await bookingApi.createBooking(bookingPayload);
    const bookingId = createdBooking.bookingId;

    // 儲存預訂資料到 localStorage 供同步還原
    const checkoutInfo = {
      ...route.query,
      roomTypeId: roomTypeId.value,
      roomName: roomName.value,
      checkIn: checkIn.value,
      checkOut: checkOut.value,
      guests: guests.value,
      price: totalPrice.value,
      name: form.value.name,
      email: form.value.email,
      phone: form.value.phone,
      remark: form.value.remark,
    };
    localStorage.setItem("checkoutParams", JSON.stringify(checkoutInfo));

    // 取得綠界表單
    const res = await fetch("/api/payments/ecpay/checkout", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        bookingId: bookingId,
        frontendUrl: window.location.href,
      }),
    });

    if (!res.ok) {
      throw new Error("取得綠界金流表單失敗");
    }

    const htmlForm = await res.text();

    // 在新分頁 (ECPayTab) 開啟綠界付款
    ecpayFormContainer.value.innerHTML = htmlForm;
    await nextTick();
    const formElement = ecpayFormContainer.value.querySelector("form");
    if (formElement) {
      formElement.target = "ECPayTab"; // ★ 新分頁開啟綠界
      formElement.submit();
    }

    // 原分頁開始輪詢與監聽
    startPolling(bookingId);
  } catch (error) {
    console.error("Checkout failed:", error);
    showAlert("error", "結帳錯誤", "結帳發生錯誤，請稍後再試！");
  } finally {
    isProcessing.value = false;
  }
}
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;600;700&family=Inter:wght@300;400;500&display=swap");

.checkout-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 2rem 1rem;
  font-family: "Inter", sans-serif;
  color: #2c1810;
}

.step-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 3rem;
  font-size: 0.85rem;
  font-weight: 500;
  letter-spacing: 0.05em;
  color: #888;
}

.step {
  padding: 0 1rem;
}
.step.completed {
  color: #2c1810;
}
.step.active {
  color: #2c1810;
  font-weight: 700;
}

.step.clickable {
  cursor: pointer;
  transition: opacity 0.3s;
}

.step.clickable:hover {
  opacity: 0.6;
}

.step-line {
  width: 30px;
  height: 1px;
  background: #ccc;
}

.step-line.active {
  background: #2c1810;
}

.header-title {
  text-align: center;
  margin-bottom: 3rem;
}
.header-title h2 {
  font-family: "Playfair Display", serif;
  font-size: 2.5rem;
  font-weight: 400;
  letter-spacing: 0.05em;
}
.subtitle {
  color: #c9a96e;
  font-size: 0.9rem;
}

.content-grid {
  display: flex;
  gap: 3rem;
  align-items: flex-start;
}

.form-section {
  flex: 1;
}

.section-title {
  margin: 0 0 1.5rem 0;
  padding-bottom: 0.6rem;
  border-bottom: 1px solid #eaeaea;
  font-size: 1.25rem;
  font-weight: 600;
  color: #2c1810;
}

.mt-4 {
  margin-top: 2rem;
}

.checkout-form .form-group {
  margin-bottom: 1.5rem;
  display: flex;
  flex-direction: column;
}
.checkout-form label {
  font-size: 0.9rem;
  font-weight: 500;
  margin-bottom: 0.5rem;
}
.checkout-form input,
.checkout-form textarea {
  padding: 0.75rem;
  border: 1px solid #ccc;
  border-radius: 4px;
  font-family: inherit;
  font-size: 1rem;
}

.payment-options {
  margin-bottom: 2rem;
}
.payment-option {
  display: flex;
  align-items: center;
  justify-content: flex-start;
  gap: 1rem;
  border: 2px solid #eaeaea;
  padding: 1.2rem 1.5rem;
  border-radius: 8px;
  background: #fff;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.payment-option input[type="radio"] {
  margin: 0;
  width: 1.2rem;
  height: 1.2rem;
  flex-shrink: 0;
}

.payment-option.selected {
  border-color: #c9a96e;
  background: #fdfbf7;
}

.option-content {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  font-size: 1rem;
  font-weight: 500;
  color: #2c1810;
}

.option-content .icon {
  font-size: 1.2rem;
}

.btn-checkout {
  width: 100%;
  padding: 1rem;
  background: #2c1810;
  color: #fff;
  border: none;
  border-radius: 4px;
  font-size: 1rem;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: background 0.3s;
}
.btn-checkout:hover:not(:disabled) {
  background: #44271b;
}
.btn-checkout:disabled {
  background: #999;
  cursor: not-allowed;
}

.summary-sidebar {
  width: 350px;
}
.summary-card {
  border: 1px solid #eaeaea;
  border-radius: 8px;
  overflow: hidden;
  background: #fff;
}
.summary-header {
  background: #fdfbf7;
  padding: 1rem 1.5rem;
  font-size: 0.9rem;
  font-weight: 600;
  letter-spacing: 0.05em;
  border-bottom: 1px solid #eaeaea;
}
.summary-content {
  padding: 1.5rem;
}
.summary-content h4 {
  font-size: 1.1rem;
  margin-bottom: 1rem;
  font-weight: 600;
}
.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 0.8rem;
  font-size: 0.9rem;
}
.summary-row.total {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c1810;
  margin-top: 1rem;
}
.divider {
  height: 1px;
  background: #eaeaea;
  margin: 1.2rem 0;
}

/* ================= 付款中 Modal ================= */
.payment-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(4px);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.payment-modal {
  background: #fff;
  padding: 2.5rem;
  border-radius: 12px;
  text-align: center;
  max-width: 460px;
  width: 90%;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.payment-modal h3 {
  color: #2c1810;
  margin-bottom: 0.8rem;
  font-size: 1.4rem;
}

.modal-hint {
  font-size: 0.85rem;
  color: #888;
  margin-top: 1rem;
}

.spinner {
  margin: 1.5rem auto;
  width: 44px;
  height: 44px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #c9a96e;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% {
    transform: rotate(0deg);
  }
  100% {
    transform: rotate(360deg);
  }
}

.dev-tools {
  margin-top: 1.5rem;
  padding-top: 1.2rem;
  border-top: 1px dashed #eee;
}

.btn-mock {
  background: #c9a96e;
  color: white;
  border: none;
  padding: 0.75rem 1.2rem;
  border-radius: 6px;
  cursor: pointer;
  width: 100%;
  font-weight: 600;
  font-size: 0.95rem;
  transition: background 0.3s;
}
.btn-mock:hover {
  background: #b5894b;
}

.btn-cancel {
  background: transparent;
  color: #666;
  border: 1px solid #ccc;
  padding: 0.6rem;
  border-radius: 6px;
  cursor: pointer;
  width: 100%;
  margin-top: 0.8rem;
  font-size: 0.9rem;
}
.btn-cancel:hover {
  background: #f5f5f5;
}

/* ================= 付款成功畫面專屬樣式 ================= */
.success-screen {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 1rem 0 3rem 0;
}

.success-card {
  background: #ffffff;
  border-radius: 16px;
  padding: 3rem 2.5rem;
  max-width: 650px;
  width: 100%;
  box-shadow: 0 12px 36px rgba(44, 24, 16, 0.08);
  border: 1px solid #f0eae1;
  text-align: center;
}

.success-badge {
  width: 64px;
  height: 64px;
  background: #2e7d32;
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 2rem;
  font-weight: bold;
  margin: 0 auto 1.5rem auto;
  box-shadow: 0 4px 14px rgba(46, 125, 50, 0.3);
}

.success-title {
  font-family: "Playfair Display", serif;
  font-size: 2rem;
  color: #2c1810;
  margin-bottom: 0.5rem;
  font-weight: 600;
}

.success-subtitle {
  color: #666;
  font-size: 1rem;
  margin-bottom: 2rem;
}

.order-summary-box {
  background: #faf8f5;
  border: 1px solid #ede5d8;
  border-radius: 12px;
  padding: 1.5rem;
  text-align: left;
  margin-bottom: 1.5rem;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.6rem 0;
  font-size: 0.95rem;
}

.item-label {
  color: #777;
}

.item-value {
  color: #2c1810;
  font-weight: 500;
}

.item-value.booking-id {
  color: #b54708;
  font-weight: 700;
  letter-spacing: 0.05em;
}

.summary-divider {
  height: 1px;
  background: #e4dacf;
  margin: 0.8rem 0;
}

.total-item {
  padding-top: 0.8rem;
}

.total-item .item-label {
  font-size: 1.1rem;
  font-weight: 600;
  color: #2c1810;
}

.total-price {
  font-size: 1.25rem;
  font-weight: 700;
  color: #b54708;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.paid-badge {
  font-size: 0.75rem;
  background: #e8f5e9;
  color: #2e7d32;
  padding: 0.2rem 0.6rem;
  border-radius: 20px;
  font-weight: 600;
}

.email-notice {
  background: #f0f7ff;
  border: 1px solid #cce3ff;
  border-radius: 10px;
  padding: 1.2rem;
  display: flex;
  align-items: flex-start;
  gap: 0.8rem;
  text-align: left;
  margin-bottom: 2rem;
  font-size: 0.9rem;
  color: #1e40af;
  line-height: 1.5;
}

.notice-icon {
  font-size: 1.4rem;
  flex-shrink: 0;
}

.user-email {
  font-weight: 600;
  text-decoration: underline;
}

.action-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.btn-action {
  padding: 0.9rem 1.8rem;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  flex: 1;
}

.btn-outline {
  background: white;
  color: #2c1810;
  border: 1.5px solid #2c1810;
}

.btn-outline:hover {
  background: #2c1810;
  color: white;
}

.btn-gold {
  background: #c9a96e;
  color: white;
  border: 1.5px solid #c9a96e;
}

.btn-gold:hover {
  background: #b5894b;
}
</style>
