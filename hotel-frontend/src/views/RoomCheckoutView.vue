<template>
  <div class="checkout-container">
    <div class="step-bar">
      <div class="step completed clickable" @click="goToSearch">❶ 搜尋</div>
      <div class="step-line"></div>
      <div class="step completed clickable" @click="goToSelection">❷ 選房</div>
      <div class="step-line"></div>
      <div class="step active">❸ 確認 & 結帳</div>
    </div>

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
          <h3>💳 付款中...</h3>
          <p>請在彈出的安全視窗中完成結帳</p>

          <div class="spinner"></div>

          <button @click="cancelPaymentWait" class="btn-cancel mt-4">
            返回修改訂單
          </button>
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

onUnmounted(() => {
  if (pollingInterval.value) {
    clearInterval(pollingInterval.value);
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

  // 每 3 秒詢問一次後端狀態
  pollingInterval.value = setInterval(async () => {
    try {
      const res = await fetch(`/api/payments/ecpay/status/${bookingId}`);
      if (res.ok) {
        const data = await res.json();
        if (data.status === "已付款") {
          clearInterval(pollingInterval.value);
          showPaymentModal.value = false;
          showAlert(
            "success",
            "付款成功",
            "感謝您的預訂！即將為您跳轉至首頁。",
            () => {
              router.push("/");
            },
          );
        }
      }
    } catch (e) {
      console.error("輪詢狀態失敗", e);
    }
  }, 3000);
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
    currentBookingId.value = null; // 清空
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
      // 輪詢會自動抓到並跳轉，或者我們直接跳轉
      clearInterval(pollingInterval.value);
      showAlert(
        "success",
        "開發模式：付款成功",
        "已強制模擬付款成功！即將為您跳轉至首頁。",
        () => {
          router.push("/");
        },
      );
    } else {
      showAlert("error", "模擬失敗", "無法完成強制模擬付款！");
    }
  } catch (e) {
    console.error(e);
  }
}

async function forceMockFail() {
  if (!currentBookingId.value) return;
  try {
    const res = await fetch(
      `/api/payments/ecpay/mock-fail/${currentBookingId.value}`,
      {
        method: "POST",
      },
    );
    if (res.ok) {
      clearInterval(pollingInterval.value);
      showAlert(
        "error",
        "開發模式：付款失敗",
        "已強制模擬付款失敗！即將為您跳轉至首頁。",
        () => {
          router.push("/");
        },
      );
    } else {
      showAlert("error", "模擬失敗", "無法完成強制模擬付款！");
    }
  } catch (e) {
    console.error(e);
  }
}

onMounted(() => {
  // 如果是從綠界跳轉回來且帶有成功標記
  if (route.query.paymentSuccess) {
    // 透過 window.name 完美判斷這是不是我們開的「綠界新分頁」
    if (window.name === "ECPayPopup" || window.opener) {
      // 這是彈出視窗！嘗試自動關掉
      window.close();

      // 如果瀏覽器不給關，就顯示專屬的提示，千萬不要還原畫面，以免產生「兩個 Vue 畫面」的錯覺
      setTimeout(() => {
        showAlert(
          "success",
          "結帳完畢",
          "綠界金流處理成功！\n\n請直接「關閉此分頁」，並回到您原本的訂房視窗查看結果。",
        );
      }, 300);
      return;
    }

    // ==========================================
    // 若程式走到這裡，代表使用者是用 target="_self" (同一個分頁) 跳轉，
    // 這時我們才需要還原畫面！
    // ==========================================
    const savedParams = localStorage.getItem("checkoutParams");
    if (savedParams) {
      const parsed = JSON.parse(savedParams);
      roomTypeId.value = parsed.roomTypeId;
      roomName.value = parsed.roomName;
      checkIn.value = parsed.checkIn;
      checkOut.value = parsed.checkOut;
      guests.value = parsed.guests;
      totalPrice.value = Number(parsed.price) || 0;

      // 將網址列改寫回原本帶著所有參數的樣子，這樣重整才不會不見
      router.replace({
        path: "/room-checkout",
        query: {
          roomTypeId: parsed.roomTypeId,
          roomName: parsed.roomName,
          checkIn: parsed.checkIn,
          checkOut: parsed.checkOut,
          guests: parsed.guests,
          price: parsed.price,
          paymentSuccess: "true",
        },
      });
    }

    // 延遲一下確保畫面已渲染，再顯示成功訊息
    setTimeout(() => {
      showAlert(
        "success",
        "付款成功",
        "感謝您的預訂！即將為您跳轉至首頁。",
        () => {
          router.push("/");
        },
      );
    }, 100);
    return; // 成功還原畫面，結束
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

  // 以下為正常進入此頁面 (沒有 paymentSuccess) 的邏輯：檢查是否有缺少必要參數
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
    // 0. 獲取當前登入使用者的會員 ID
    let currentMemberId = 1; // 預設值 (給未登入或測試用)
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

    // 1. 建立訂單 (呼叫 Backend POST /api/bookings)
    const bookingPayload = {
      memberId: currentMemberId, // 使用抓取到的會員 ID
      roomTypeId: roomTypeId.value,
      checkInDate: checkIn.value,
      checkOutDate: checkOut.value,
      guestNum: guests.value, // 對應 BookingDTO.guestNum
      bookingPrice: totalPrice.value,
      bookingStatus: "待入住",
    };

    const createdBooking = await bookingApi.createBooking(bookingPayload);
    const bookingId = createdBooking.bookingId;

    // 在跳轉前，把當前的路由參數存入 localStorage，以便結帳回來後恢復畫面
    localStorage.setItem("checkoutParams", JSON.stringify(route.query));

    // 2. 呼叫後端取得綠界 HTML 表單
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

    // 3. 將表單寫入預設的隱藏容器，再觸發 Submit
    ecpayFormContainer.value.innerHTML = htmlForm;
    await nextTick();
    const formElement = ecpayFormContainer.value.querySelector("form");
    if (formElement) {
      formElement.target = "ECPayPopup"; // ★ 關鍵：新開分頁，並給予專屬名稱，方便回來時辨識！
      formElement.submit();
    }

    // 4. 開始在本地輪詢付款狀態
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

.payment-option:hover {
  border-color: #c9a96e;
  background: #fdfaf6;
}

.payment-option.selected {
  border-color: #2c1810;
  background: #fff;
  box-shadow: 0 4px 12px rgba(44, 24, 16, 0.08);
}

.payment-option.selected::before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: #2c1810;
}

.option-content {
  display: flex;
  align-items: center;
  gap: 0.8rem;
  font-weight: 500;
  font-size: 1.05rem;
  color: #333;
  flex: 1;
}

.option-content .icon {
  font-size: 1.5rem;
}

.btn-checkout {
  background: #2c1810;
  color: #fff;
  border: none;
  width: 100%;
  padding: 1.25rem;
  font-size: 1.1rem;
  font-weight: 600;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: background 0.3s;
}
.btn-checkout:hover:not(:disabled) {
  background: #c9a96e;
}
.btn-checkout:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.summary-sidebar {
  width: 350px;
}
.summary-card {
  border: 1px solid #eaeaea;
  background: #fff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
.summary-header {
  background: #2c1810;
  color: #fff;
  padding: 1rem;
  text-align: center;
  font-weight: 500;
  letter-spacing: 0.05em;
}
.summary-content {
  padding: 1.5rem;
}
.summary-content h4 {
  font-family: "Playfair Display", serif;
  font-size: 1.2rem;
  margin-bottom: 1.5rem;
  color: #2c1810;
}
.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
  font-size: 0.95rem;
}
.summary-row .label {
  color: #666;
}
.summary-row .value {
  font-weight: 500;
}
.divider {
  height: 1px;
  background: #eaeaea;
  margin: 1.5rem 0;
}
.total {
  font-size: 1.2rem;
  font-weight: 700;
  color: #b54708;
}

@media (max-width: 900px) {
  .content-grid {
    flex-direction: column-reverse;
  }
  .summary-sidebar {
    width: 100%;
  }
}

/* Modal Styles */
.payment-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.payment-modal {
  background: #fff;
  padding: 2.5rem;
  border-radius: 12px;
  text-align: center;
  max-width: 450px;
  width: 90%;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.payment-modal h3 {
  color: #2c1810;
  margin-bottom: 1rem;
}

.spinner {
  margin: 2rem auto;
  width: 40px;
  height: 40px;
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
  margin-top: 2rem;
  padding-top: 1.5rem;
  border-top: 1px dashed #ccc;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.btn-mock {
  background: #c9a96e;
  color: white;
  border: none;
  padding: 0.8rem 1.2rem;
  border-radius: 6px;
  cursor: pointer;
  width: 100%;
  font-weight: 600;
}
.btn-mock:hover {
  background: #b54708;
}

.btn-mock-fail {
  background: #dc3545;
  color: white;
  border: none;
  padding: 0.8rem 1.2rem;
  border-radius: 6px;
  cursor: pointer;
  width: 100%;
  font-weight: 600;
}
.btn-mock-fail:hover {
  background: #c82333;
}

.btn-cancel {
  background: transparent;
  color: #666;
  border: 1px solid #ccc;
  padding: 0.6rem;
  border-radius: 6px;
  cursor: pointer;
  width: 100%;
  margin-top: 0.5rem;
}
.btn-cancel:hover {
  background: #f5f5f5;
}
</style>
