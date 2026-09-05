<template>
  <div class="selection-container">
    
    <!-- Top Step Bar -->
    <div class="step-bar">
      <div class="step completed clickable" @click="goToSearch">❶ 搜尋</div>
      <div class="step-line"></div>
      <div class="step active">❷ 選房</div>
      <div class="step-line"></div>
      <div class="step">❸ 確認 & 結帳</div>
    </div>
    
    <!-- Header Title -->
    <div class="header-title">
      <h2>CHOOSE your ROOM</h2>
      <p class="subtitle">為您推薦的房型 / Popular dates are booking quickly</p>
    </div>

    <!-- Main Content Grid -->
    <div class="content-grid">
      
      <!-- Left: Room List -->
      <div class="room-list">
        <div class="list-header">
          <h3>星澄飯店 • {{ rooms.length }} TYPES OF ROOMS</h3>
        </div>

        <div v-if="isSameDayBooking" class="same-day-notice">
          <span class="icon">⚠️</span> 
          <span>今日入住請留意：部分房間可能仍在清潔中，統一於 <strong>下午 3 點</strong> 後方可辦理入住，敬請見諒。</span>
        </div>

        <div v-if="loading" class="loading-state">
          正在查詢可用房型...
        </div>
        <div v-else-if="rooms.length === 0" class="empty-state">
          非常抱歉，該日期區間已無可用房型。
        </div>
        
        <div v-for="room in rooms" :key="room.roomTypeId" class="room-card">
          <div class="room-image-area">
            <img :src="room.mainImageUrl ? `http://localhost:8081${room.mainImageUrl}` : 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?q=80&w=2070&auto=format&fit=crop'" :alt="room.typeName" />
            <div class="image-count">☐ 照片</div>
          </div>
          
          <div class="room-info">
            <h4 class="room-name">{{ room.typeName }}</h4>
            <div class="features">
              <span class="feature"><i class="icon">🛏️</i> {{ room.bedType }}</span>
              <span class="feature"><i class="icon">👥</i> {{ guests }} 人入住</span>
            </div>
            <p class="room-desc">{{ room.roomDescription }}</p>
          </div>
          
          <div class="room-price-area">
            <div class="price-header">FROM</div>
            <div class="price-amount">NT${{ room.pricePerNight.toLocaleString() }}</div>
            <div class="price-note">for your stay per night</div>
            <p class="tax-note">Taxes and fees included</p>
            <button 
              class="btn-select" 
              :disabled="room.availableRooms <= 0"
              @click="selectRoom(room)"
            >
              {{ room.availableRooms > 0 ? '選擇此房型' : '已客滿' }}
            </button>
          </div>
        </div>
      </div>

      <!-- Right: Summary Sidebar -->
      <div class="summary-sidebar">
        <div class="summary-card">
          <img src="https://images.unsplash.com/photo-1542314831-c6a4d14ce8a1?q=80&w=500&auto=format&fit=crop" class="summary-img" alt="Hotel" />
          <div class="summary-content">
            <h4>星澄飯店</h4>
            <div class="summary-dates">
              <div class="date-col">
                <span class="label">Check-in</span>
                <span class="value">{{ checkIn }}</span>
              </div>
              <div class="date-col">
                <span class="label">Check-out</span>
                <span class="value">{{ checkOut }}</span>
              </div>
            </div>
            <div class="summary-guests">
              <span class="label">Choose your room for</span>
              <span class="value">{{ guests }} Guest(s)</span>
            </div>
          </div>
        </div>
        
        <div class="guarantee-card">
          <h4>BEST PRICE GUARANTEE</h4>
          <p>Book direct for our best price, guaranteed. If you find a lower rate for your stay, we'll match it.</p>
        </div>
      </div>

    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { roomTypeApi } from '../api/roomTypeApi';

const route = useRoute();
const router = useRouter();

const checkIn = ref(route.query.checkIn || '');
const checkOut = ref(route.query.checkOut || '');
const guests = ref(Number(route.query.guests) || 2);

const rooms = ref([]);
const loading = ref(true);

const isSameDayBooking = computed(() => {
  if (!checkIn.value) return false;
  // Use local date string format for accurate today comparison
  const today = new Date();
  const tzOffset = today.getTimezoneOffset() * 60000;
  const localToday = new Date(today.getTime() - tzOffset).toISOString().split('T')[0];
  return checkIn.value === localToday;
});

onMounted(async () => {
  if (!checkIn.value || !checkOut.value) {
    router.push('/room-booking');
    return;
  }
  await fetchRooms();
});

async function fetchRooms() {
  loading.value = true;
  try {
    const data = await roomTypeApi.getAvailableRoomTypes(checkIn.value, checkOut.value);
    // 隱藏客滿房型，且只顯示容量足夠的房型
    rooms.value = data.filter(r => r.capacity >= guests.value && r.availableRooms > 0);
  } catch (error) {
    console.error("無法取得房型資料", error);
  } finally {
    loading.value = false;
  }
}

function selectRoom(room) {
  router.push({
    name: 'room-checkout',
    query: {
      roomTypeId: room.roomTypeId,
      roomName: room.typeName,
      checkIn: checkIn.value,
      checkOut: checkOut.value,
      guests: guests.value,
      price: room.pricePerNight
    }
  });
}

function goToSearch() {
  router.push('/room-booking');
}
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@400;600;700&family=Inter:wght@300;400;500&display=swap');

.selection-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem 1rem;
  font-family: 'Inter', sans-serif;
  color: #2C1810;
  background-color: #fcfcfc;
}

.same-day-notice {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background-color: #fff4e5;
  color: #b54708;
  padding: 1rem;
  border-radius: 8px;
  margin-bottom: 2rem;
  border-left: 4px solid #f79009;
  font-size: 0.95rem;
}

/* Step Bar */
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
  color: #2C1810;
}

.step.active {
  color: #2C1810;
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

/* Header */
.header-title {
  text-align: center;
  margin-bottom: 3rem;
}

.header-title h2 {
  font-family: 'Playfair Display', serif;
  font-size: 2.5rem;
  font-weight: 400;
  letter-spacing: 0.05em;
  margin-bottom: 0.5rem;
}

.header-title .subtitle {
  color: #C9A96E;
  font-size: 0.9rem;
  letter-spacing: 0.05em;
}

/* Layout */
.content-grid {
  display: flex;
  gap: 2rem;
  align-items: flex-start;
}

.room-list {
  flex: 1;
}

.summary-sidebar {
  width: 320px;
  position: sticky;
  top: 2rem;
}

/* List Header */
.list-header {
  border-bottom: 1px solid #eee;
  padding-bottom: 1rem;
  margin-bottom: 2rem;
}

.list-header h3 {
  font-size: 1rem;
  font-weight: 600;
  letter-spacing: 0.1em;
  text-transform: uppercase;
}

/* Room Card */
.room-card {
  display: flex;
  border: 1px solid #eaeaea;
  background: #fff;
  margin-bottom: 2rem;
  transition: box-shadow 0.3s ease;
}

.room-card:hover {
  box-shadow: 0 8px 24px rgba(0,0,0,0.06);
}

.room-image-area {
  width: 300px;
  position: relative;
}

.room-image-area img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-count {
  position: absolute;
  top: 1rem;
  right: 1rem;
  background: rgba(0,0,0,0.6);
  color: #fff;
  padding: 0.25rem 0.5rem;
  font-size: 0.75rem;
}

.room-info {
  flex: 1;
  padding: 2rem;
  border-right: 1px solid #eaeaea;
}

.room-name {
  font-family: 'Playfair Display', serif;
  font-size: 1.5rem;
  margin-bottom: 1.5rem;
}

.features {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
  color: #555;
  font-size: 0.9rem;
}

.feature {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.room-desc {
  font-size: 0.9rem;
  color: #666;
  line-height: 1.6;
  margin-bottom: 1.5rem;
}

.details-link {
  color: #2C1810;
  text-decoration: underline;
  font-size: 0.9rem;
}

.room-price-area {
  width: 250px;
  padding: 2rem;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  text-align: center;
  background: #fafafa;
}

.price-header {
  font-size: 0.75rem;
  color: #888;
  letter-spacing: 0.1em;
}

.price-amount {
  font-size: 1.8rem;
  font-weight: 600;
  margin: 0.5rem 0;
}

.price-note {
  font-size: 0.8rem;
  color: #666;
  margin-bottom: 1.5rem;
}

.tax-note {
  font-size: 0.75rem;
  color: #999;
  margin-bottom: 1.5rem;
}

.btn-select {
  background: #2C1810;
  color: #fff;
  border: none;
  width: 100%;
  padding: 1rem;
  font-size: 0.9rem;
  font-weight: 500;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: background 0.3s ease;
}

.btn-select:hover:not(:disabled) {
  background: #C9A96E;
}

.btn-select:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* Summary Sidebar */
.summary-card {
  border: 1px solid #eaeaea;
  background: #fff;
  margin-bottom: 1.5rem;
}

.summary-img {
  width: 100%;
  height: 180px;
  object-fit: cover;
}

.summary-content {
  padding: 1.5rem;
}

.summary-content h4 {
  font-family: 'Playfair Display', serif;
  font-size: 1.2rem;
  margin-bottom: 1.5rem;
  border-bottom: 1px solid #eee;
  padding-bottom: 1rem;
}

.summary-dates {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1.5rem;
}

.date-col {
  display: flex;
  flex-direction: column;
}

.summary-guests {
  display: flex;
  flex-direction: column;
  border-top: 1px solid #eee;
  padding-top: 1rem;
}

.label {
  font-size: 0.75rem;
  color: #888;
  margin-bottom: 0.25rem;
}

.value {
  font-size: 0.95rem;
  font-weight: 500;
}

.guarantee-card {
  background: #f9f9f9;
  padding: 1.5rem;
  border: 1px solid #eee;
}

.guarantee-card h4 {
  font-size: 0.85rem;
  font-weight: 600;
  margin-bottom: 0.5rem;
  letter-spacing: 0.05em;
}

.guarantee-card p {
  font-size: 0.8rem;
  color: #666;
  line-height: 1.5;
}

@media (max-width: 1024px) {
  .content-grid {
    flex-direction: column-reverse;
  }
  .summary-sidebar {
    width: 100%;
    position: static;
  }
  .room-card {
    flex-direction: column;
  }
  .room-image-area {
    width: 100%;
    height: 250px;
  }
  .room-price-area {
    width: 100%;
    border-top: 1px solid #eaeaea;
  }
}
</style>
