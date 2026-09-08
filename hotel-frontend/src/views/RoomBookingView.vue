<template>
  <div class="hotel-booking-container">
    
    <!-- Top Step Bar -->
    <div class="step-bar-container">
      <div class="step-bar">
        <div class="step active">❶ 搜尋</div>
        <div class="step-line"></div>
        <div class="step">❷ 選房</div>
        <div class="step-line"></div>
        <div class="step">❸ 確認 & 結帳</div>
      </div>
    </div>

    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-overlay"></div>
      <img src="https://images.unsplash.com/photo-1566073771259-6a8506099945" alt="Hotel Exterior" class="hero-bg" />
      
      <div class="hero-content">
        <h4 class="subtitle">EXPERIENCE LUXURY</h4>
        <h1 class="title">星澄飯店</h1>
        <p class="description">將飯店的舒適與質感帶回家，為生活增添美好體驗。</p>
      </div>
    </div>

    <!-- Booking Search Bar (Fairmont Style) -->
    <div class="search-bar-wrapper">
      <div class="search-bar">
        
        <div class="search-field">
          <span class="icon">📍</span>
          <div class="field-content">
            <label>Where to?</label>
            <input type="text" value="星澄飯店" readonly class="readonly-input" />
          </div>
        </div>
        
        <div class="search-field dates-field">
          <span class="icon">📅</span>
          <div class="field-content">
            <label>What are your dates?</label>
            <div class="date-inputs">
              <VueDatePicker 
                ref="datePickerRef"
                v-model="dateRange" 
                :range="{ minRange: 1, maxRange: 30 }"
                multi-calendars 
                :min-date="today" 
                :enable-time-picker="false"
                format="yyyy/MM/dd"
                dark
                hide-input-icon
                placeholder="Select dates"
                @internal-model-change="handleInternalModelChange"
              >
                <template #action-row="{ modelValue, closePicker }">
                  <div class="custom-action-row">
                    <button class="action-btn clear-btn" @click="dateRange = null; closePicker()">Clear</button>
                    <div class="action-right">
                      <span class="nights-text" v-if="modelValue && modelValue[0] && modelValue[1]">
                        {{ calculateNights(modelValue) }} NIGHTS
                      </span>
                    </div>
                  </div>
                </template>
              </VueDatePicker>
            </div>
          </div>
        </div>
        
        <div class="search-field">
          <span class="icon">🛏️</span>
          <div class="field-content">
            <label>Rooms & Guests</label>
            <select v-model="searchData.guests">
              <option v-for="n in 4" :key="n" :value="n">1 Room - {{ n }} Guest(s)</option>
            </select>
          </div>
        </div>
        
        <button class="btn-check-rates" @click="handleSearch">
          CHECK RATES
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';
import { VueDatePicker } from '@vuepic/vue-datepicker';
import '@vuepic/vue-datepicker/dist/main.css';

const router = useRouter();
const authStore = useAuthStore();

// Date validation - fix timezone issue
const now = new Date();
const offset = now.getTimezoneOffset() * 60000;
const localNow = new Date(now.getTime() - offset);
localNow.setHours(0, 0, 0, 0); // Strip time
const today = localNow;

const tomorrowDate = new Date(localNow.getTime() + 86400000);
tomorrowDate.setHours(0, 0, 0, 0); // Strip time

const dateRange = ref([today, tomorrowDate]);

const searchData = ref({
  guests: 1
});

const datePickerRef = ref(null);
const isSelecting = ref(false);

function calculateNights(range) {
  if (!range || !range[0] || !range[1]) return 0;
  const diffTime = Math.abs(range[1] - range[0]);
  return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
}

function handleInternalModelChange(val) {
  if (val && val[0] && !val[1]) {
    isSelecting.value = true;
  }
  
  if (val && val[0] && val[1]) {
    if (isSelecting.value) {
      isSelecting.value = false;
      // 延遲 800 毫秒，讓使用者有足夠時間看到右下角顯示的入住天數
      setTimeout(() => {
        if (datePickerRef.value) {
          datePickerRef.value.selectDate();
        }
      }, 800);
    }
  }
}

function handleSearch() {
  if (!authStore.isLoggedIn) {
    alert("請先登入會員以進行訂房");
    router.push({ name: 'login', query: { redirect: '/room-booking/select' } });
    return;
  }

  if (!dateRange.value || !dateRange.value[0] || !dateRange.value[1]) {
    alert("請選擇完整的入住與退房日期");
    return;
  }

  const checkIn = new Date(dateRange.value[0].getTime() - offset).toISOString().split('T')[0];
  const checkOut = new Date(dateRange.value[1].getTime() - offset).toISOString().split('T')[0];

  router.push({
    name: 'room-selection',
    query: {
      checkIn: checkIn,
      checkOut: checkOut,
      guests: searchData.value.guests
    }
  });
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;600;700&family=Inter:wght@300;400;500&display=swap');

.hotel-booking-container {
  min-height: calc(100vh - 80px);
  position: relative;
  font-family: 'Inter', sans-serif;
  background-color: #fcfcfc;
}

/* Step Bar */
.step-bar-container {
  background-color: #fcfcfc;
  padding: 2rem 0;
}

.step-bar {
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.85rem;
  font-weight: 500;
  letter-spacing: 0.05em;
  color: #888;
}

.step {
  padding: 0 1rem;
}

.step.completed {
  color: #2C1810;
}

.step.active {
  color: #2C1810;
  font-weight: 700;
}

.step-line {
  width: 30px;
  height: 1px;
  background: #ccc;
}

.hero-section {
  position: relative;
  height: 70vh;
  min-height: 500px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.hero-bg {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  z-index: 1;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(to bottom, rgba(44, 24, 16, 0.4) 0%, rgba(0,0,0,0.6) 100%);
  z-index: 2;
}

.hero-content {
  position: relative;
  z-index: 3;
  text-align: center;
  color: #FFFFFF;
  margin-top: -10vh;
}

.subtitle {
  font-family: 'Inter', sans-serif;
  font-size: 0.9rem;
  letter-spacing: 0.3em;
  font-weight: 500;
  margin-bottom: 1rem;
  color: #C9A96E;
}

.title {
  font-family: 'Playfair Display', serif;
  font-size: 4.5rem;
  font-weight: 700;
  margin-bottom: 1.5rem;
  letter-spacing: 0.05em;
  text-shadow: 0 4px 12px rgba(0,0,0,0.3);
}

.description {
  font-size: 1.1rem;
  font-weight: 300;
  opacity: 0.9;
  letter-spacing: 0.05em;
}

/* Fairmont Style Search Bar */
.search-bar-wrapper {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  z-index: 10;
  background: linear-gradient(to right, rgba(60, 60, 60, 0.95), rgba(80, 80, 80, 0.95));
  padding: 1.5rem 0 1rem 0;
}

.search-bar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 90%;
  max-width: 1200px;
}

.search-field {
  flex: 1;
  display: flex;
  align-items: flex-start;
  padding: 0 1.5rem;
  color: #fff;
}

.search-field .icon {
  font-size: 1.2rem;
  margin-right: 1rem;
  margin-top: 0.2rem;
  opacity: 0.8;
  filter: grayscale(1) brightness(2);
}

.field-content {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.search-field label {
  color: #ccc;
  font-size: 0.8rem;
  margin-bottom: 0.3rem;
  font-weight: 400;
}

.search-field input,
.search-field select {
  background: transparent;
  border: none;
  color: #FFFFFF;
  font-size: 1rem;
  font-family: 'Inter', sans-serif;
  outline: none;
  cursor: pointer;
  padding: 0;
}

.readonly-input {
  cursor: default !important;
  opacity: 0.9;
}

.date-inputs {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  width: 100%;
}

/* Custom VueDatePicker styling to match dark theme */
:deep(.dp__theme_dark) {
  --dp-background-color: transparent;
  --dp-text-color: #ffffff;
  --dp-hover-color: #484848;
  --dp-hover-text-color: #ffffff;
  --dp-primary-color: #C9A96E;
  --dp-primary-text-color: #000;
  --dp-border-color: transparent;
  --dp-menu-border-color: #333;
}
:deep(.dp__input) {
  font-family: 'Inter', sans-serif;
  font-size: 1rem;
  padding: 0;
  font-weight: 500;
  letter-spacing: 0.02em;
}
:deep(.dp__main) {
  width: 100%;
}
/* 隱藏時間選擇的時鐘按鈕與預覽文字 */
:deep([data-test-id="open-time-picker-btn"]),
:deep(.dp__selection_preview) {
  display: none !important;
}

.search-field select option {
  background: #444;
  color: #FFFFFF;
}

.btn-check-rates {
  background: #FFFFFF;
  color: #000000;
  border: none;
  padding: 1rem 2.5rem;
  font-family: 'Inter', sans-serif;
  font-size: 0.9rem;
  font-weight: 600;
  letter-spacing: 0.1em;
  cursor: pointer;
  transition: background 0.3s ease;
  margin-left: 1rem;
}

.btn-check-rates:hover {
  background: #f0f0f0;
}

.custom-action-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
  padding: 10px 20px;
  background-color: transparent;
  border-top: 1px solid #444;
}

.action-right {
  display: flex;
  align-items: center;
}

.action-btn {
  background: none;
  border: none;
  cursor: pointer;
  font-family: 'Inter', sans-serif;
  font-size: 0.95rem;
}

.clear-btn {
  text-decoration: underline;
  color: #fff;
  font-weight: 500;
  transition: opacity 0.2s;
}
.clear-btn:hover {
  opacity: 0.7;
}

.nights-text {
  font-size: 0.85rem;
  color: #fff;
  letter-spacing: 0.05em;
  font-weight: 500;
}

@media (max-width: 900px) {
  .search-bar {
    flex-direction: column;
    width: 90%;
  }
  
  .search-field {
    width: 100%;
    margin-bottom: 1rem;
    padding: 0;
  }
  
  .btn-check-rates {
    width: 100%;
    margin-left: 0;
    margin-top: 1rem;
  }
}
</style>
