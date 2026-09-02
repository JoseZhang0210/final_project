<template>
  <div class="hotel-booking-container">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-overlay"></div>
      <img src="https://images.unsplash.com/photo-1542314831-c6a4d14ce8a1?q=80&w=2070&auto=format&fit=crop" alt="Hotel Exterior" class="hero-bg" />
      
      <div class="hero-content">
        <h4 class="subtitle">EXPERIENCE LUXURY</h4>
        <h1 class="title">星澄飯店</h1>
        <p class="description">將飯店的舒適與質感帶回家，為生活增添美好體驗。</p>
      </div>
    </div>

    <!-- Booking Search Bar (Liquid Glass Style) -->
    <div class="search-bar-wrapper">
      <div class="search-bar glass-panel">
        <div class="search-field">
          <label>入住日期 / Check-in</label>
          <input type="date" v-model="searchData.checkIn" :min="today" @change="validateDates" />
        </div>
        
        <div class="divider"></div>
        
        <div class="search-field">
          <label>退房日期 / Check-out</label>
          <input type="date" v-model="searchData.checkOut" :min="minCheckOut" />
        </div>
        
        <div class="divider"></div>
        
        <div class="search-field">
          <label>人數 / Guests</label>
          <select v-model="searchData.guests">
            <option v-for="n in 6" :key="n" :value="n">{{ n }} 位貴賓</option>
          </select>
        </div>
        
        <button class="btn-check-rates" @click="handleSearch">
          查看房型 <br/><small>CHECK RATES</small>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '../stores/auth';

const router = useRouter();
const authStore = useAuthStore();

// Date validation
const today = new Date().toISOString().split('T')[0];
const tomorrow = new Date(Date.now() + 86400000).toISOString().split('T')[0];

const searchData = ref({
  checkIn: today,
  checkOut: tomorrow,
  guests: 2
});

const minCheckOut = computed(() => {
  if (!searchData.value.checkIn) return tomorrow;
  const inDate = new Date(searchData.value.checkIn);
  return new Date(inDate.getTime() + 86400000).toISOString().split('T')[0];
});

function validateDates() {
  if (searchData.value.checkIn >= searchData.value.checkOut) {
    searchData.value.checkOut = minCheckOut.value;
  }
}

function handleSearch() {
  if (!authStore.isLoggedIn) {
    alert("請先登入會員以進行訂房");
    router.push({ name: 'login', query: { redirect: '/room-booking/select' } });
    return;
  }

  router.push({
    name: 'room-selection',
    query: {
      checkIn: searchData.value.checkIn,
      checkOut: searchData.value.checkOut,
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

/* Liquid Glass Search Bar */
.search-bar-wrapper {
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  display: flex;
  justify-content: center;
  z-index: 10;
  transform: translateY(50%);
}

.glass-panel {
  display: flex;
  align-items: center;
  background: rgba(44, 24, 16, 0.85); /* Dark Brown */
  backdrop-filter: blur(16px);
  -webkit-backdrop-filter: blur(16px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  padding: 1.5rem 2.5rem;
  box-shadow: 0 20px 40px rgba(0,0,0,0.2);
  width: 90%;
  max-width: 1000px;
}

.search-field {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 0 1.5rem;
}

.search-field label {
  color: #C9A96E;
  font-size: 0.75rem;
  text-transform: uppercase;
  letter-spacing: 0.1em;
  margin-bottom: 0.5rem;
  font-weight: 500;
}

.search-field input,
.search-field select {
  background: transparent;
  border: none;
  color: #FFFFFF;
  font-size: 1.1rem;
  font-family: 'Inter', sans-serif;
  outline: none;
  cursor: pointer;
  padding: 0.2rem 0;
}

.search-field input::-webkit-calendar-picker-indicator {
  filter: invert(1);
  cursor: pointer;
  opacity: 0.6;
}

.search-field select option {
  background: #2C1810;
  color: #FFFFFF;
}

.divider {
  width: 1px;
  height: 40px;
  background: rgba(255,255,255,0.2);
}

.btn-check-rates {
  background: #FFFFFF;
  color: #2C1810;
  border: none;
  padding: 1rem 2rem;
  font-family: 'Playfair Display', serif;
  font-size: 1.2rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-left: 1.5rem;
  line-height: 1.2;
}

.btn-check-rates small {
  font-family: 'Inter', sans-serif;
  font-size: 0.7rem;
  letter-spacing: 0.1em;
  font-weight: 400;
}

.btn-check-rates:hover {
  background: #C9A96E;
  color: #FFFFFF;
}

@media (max-width: 900px) {
  .glass-panel {
    flex-direction: column;
    width: 90%;
    padding: 1.5rem;
    transform: translateY(10%);
    border-radius: 8px;
  }
  
  .search-field {
    width: 100%;
    margin-bottom: 1rem;
    padding: 0;
  }
  
  .divider {
    width: 100%;
    height: 1px;
    margin: 0.5rem 0 1.5rem 0;
  }
  
  .btn-check-rates {
    width: 100%;
    margin-left: 0;
    margin-top: 1rem;
  }
}
</style>
