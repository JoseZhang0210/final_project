<template>
  <div class="checkout-container">
    <div class="step-bar">
      <div class="step completed">❶ 搜尋</div>
      <div class="step-line"></div>
      <div class="step completed">❷ 選房</div>
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
        <h3>1. 訂房聯絡人資訊</h3>
        <form class="checkout-form">
          <div class="form-group">
            <label>姓名 *</label>
            <input type="text" v-model="form.name" required placeholder="例如：王小明" />
          </div>
          <div class="form-group">
            <label>手機號碼 *</label>
            <input type="tel" v-model="form.phone" required placeholder="例如：0912345678" />
          </div>
          <div class="form-group">
            <label>特別需求備註 (選填)</label>
            <textarea v-model="form.remark" rows="3" placeholder="例如：需要嬰兒床、對某些食物過敏等"></textarea>
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
          {{ isProcessing ? '處理中，請稍候...' : '確認結帳' }}
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
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { bookingApi } from '../api/bookingApi';
import { useAuthStore } from '../stores/auth';

const route = useRoute();
const router = useRouter();
const authStore = useAuthStore();

// 從 URL Query 取得選擇的資料
const roomTypeId = ref(Number(route.query.roomTypeId));
const roomName = ref(route.query.roomName);
const checkIn = ref(route.query.checkIn);
const checkOut = ref(route.query.checkOut);
const guests = ref(Number(route.query.guests));
const totalPrice = ref(Number(route.query.price) || 0);

const isProcessing = ref(false);

const form = ref({
  name: '',
  phone: '',
  remark: ''
});

onMounted(() => {
  // 如果缺少必要參數，導回首頁
  if (!roomTypeId.value || !checkIn.value || !checkOut.value) {
    alert("缺少訂房參數，請重新選擇房型");
    router.push('/room-booking');
  }
});

async function submitCheckout() {
  if (!form.value.name || !form.value.phone) {
    alert("請填寫姓名與手機號碼！");
    return;
  }

  isProcessing.value = true;
  try {
    // 1. 建立訂單 (呼叫 Backend POST /api/bookings)
    const bookingPayload = {
      memberId: authStore.memberId, // 從登入狀態取會員 ID
      roomTypeId: roomTypeId.value,
      checkInDate: checkIn.value,
      checkOutDate: checkOut.value,
      guestNum: guests.value,   // 對應 BookingDTO.guestNum
      bookingPrice: totalPrice.value,
      bookingStatus: '待入住'
    };

    const createdBooking = await bookingApi.createBooking(bookingPayload);
    const bookingId = createdBooking.bookingId;

    // 2. 呼叫後端取得綠界 HTML 表單
    const res = await fetch('/api/payments/ecpay/checkout', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ bookingId: bookingId })
    });
    
    if (!res.ok) {
      throw new Error("取得綠界金流表單失敗");
    }

    const htmlForm = await res.text();

    // 3. 建立一個虛擬容器並 submit 表單，跳轉至綠界
    const div = document.createElement('div');
    div.innerHTML = htmlForm;
    document.body.appendChild(div);
    div.querySelector('form').submit();

  } catch (error) {
    console.error("Checkout failed:", error);
    alert("結帳發生錯誤，請稍後再試！");
    isProcessing.value = false;
  }
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;600;700&family=Inter:wght@300;400;500&display=swap');

.checkout-container {
  max-width: 1100px;
  margin: 0 auto;
  padding: 2rem 1rem;
  font-family: 'Inter', sans-serif;
  color: #2C1810;
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

.step { padding: 0 1rem; }
.step.completed { color: #2C1810; }
.step.active { color: #2C1810; font-weight: 700; }
.step-line { width: 30px; height: 1px; background: #ccc; }

.header-title {
  text-align: center;
  margin-bottom: 3rem;
}
.header-title h2 {
  font-family: 'Playfair Display', serif;
  font-size: 2.5rem;
  font-weight: 400;
  letter-spacing: 0.05em;
}
.subtitle { color: #C9A96E; font-size: 0.9rem; }

.content-grid {
  display: flex;
  gap: 3rem;
  align-items: flex-start;
}

.form-section {
  flex: 1;
}

.form-section h3 {
  font-size: 1.1rem;
  border-bottom: 1px solid #eaeaea;
  padding-bottom: 0.5rem;
  margin-bottom: 1.5rem;
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
  border-color: #C9A96E;
  background: #fdfaf6;
}

.payment-option.selected {
  border-color: #2C1810;
  background: #fff;
  box-shadow: 0 4px 12px rgba(44, 24, 16, 0.08);
}

.payment-option.selected::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: #2C1810;
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
  background: #2C1810;
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
  background: #C9A96E;
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
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}
.summary-header {
  background: #2C1810;
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
  font-family: 'Playfair Display', serif;
  font-size: 1.2rem;
  margin-bottom: 1.5rem;
  color: #2C1810;
}
.summary-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 1rem;
  font-size: 0.95rem;
}
.summary-row .label { color: #666; }
.summary-row .value { font-weight: 500; }
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
</style>
