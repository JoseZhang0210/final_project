<template>
  <div class="mobile-pass-container">
    <div class="header">
      <div class="logo">🏨 StarLight Hotel</div>
      <h2>專屬貴賓通行證</h2>
    </div>

    <div class="pass-card">
      <div class="card-top">
        <div class="icon-success">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
            <polyline points="22 4 12 14.01 9 11.01"></polyline>
          </svg>
        </div>
        <h3>驗證成功！歡迎入住</h3>
      </div>
      
      <div class="divider"></div>

      <div class="card-body">
        <div class="info-group">
          <span class="label">您的專屬房號</span>
          <span class="room-number">{{ roomNumber }}</span>
        </div>

        <div class="info-group">
          <span class="label">驗證碼</span>
          <span class="verify-code">{{ verificationCode }}</span>
        </div>
        
        <p class="instruction">
          請向櫃檯人員出示此畫面，或感應您的手機完成報到手續。祝您有美好的住宿體驗！
        </p>
      </div>
      
      <div class="card-bottom">
        <button class="action-btn" @click="closeWindow">我知道了</button>
      </div>
    </div>
    
    <div class="background-decorations">
      <div class="circle top-right"></div>
      <div class="circle bottom-left"></div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();
const roomNumber = ref('讀取中...');
const verificationCode = ref('讀取中...');

onMounted(() => {
  // 從 URL 參數讀取 code 與 room
  roomNumber.value = route.query.room || '未指定';
  verificationCode.value = route.query.code || '無效的驗證碼';
});

function closeWindow() {
  alert('您已完成入住報到！');
}
</script>

<style scoped>
.mobile-pass-container {
  min-height: 100vh;
  background: #fdfaf6;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 20px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
  position: relative;
  overflow: hidden;
}

.header {
  text-align: center;
  margin-bottom: 40px;
  z-index: 10;
}

.logo {
  font-size: 1.5rem;
  font-weight: 700;
  color: #C9A96E;
  margin-bottom: 10px;
  letter-spacing: 2px;
}

.header h2 {
  font-size: 1.25rem;
  color: #333;
  margin: 0;
  font-weight: 500;
}

.pass-card {
  background: #ffffff;
  width: 100%;
  max-width: 340px;
  border-radius: 20px;
  box-shadow: 0 15px 35px rgba(0,0,0,0.08);
  position: relative;
  z-index: 10;
  overflow: hidden;
}

.card-top {
  padding: 30px 20px;
  text-align: center;
  background: linear-gradient(135deg, #C9A96E 0%, #b58a46 100%);
  color: white;
}

.icon-success {
  width: 60px;
  height: 60px;
  background: rgba(255,255,255,0.2);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 15px;
}

.icon-success svg {
  width: 32px;
  height: 32px;
  color: white;
}

.card-top h3 {
  margin: 0;
  font-size: 1.2rem;
  font-weight: 600;
}

.divider {
  height: 20px;
  background: #fff;
  position: relative;
  margin-top: -10px;
  border-radius: 10px 10px 0 0;
}

.card-body {
  padding: 10px 30px 20px;
  text-align: center;
}

.info-group {
  margin-bottom: 25px;
}

.label {
  display: block;
  font-size: 0.85rem;
  color: #888;
  margin-bottom: 5px;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.room-number {
  display: block;
  font-size: 3rem;
  font-weight: 700;
  color: #333;
  letter-spacing: 2px;
}

.verify-code {
  display: inline-block;
  background: #f5f5f5;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 1.1rem;
  font-weight: 600;
  color: #555;
  letter-spacing: 2px;
  font-family: monospace;
}

.instruction {
  font-size: 0.9rem;
  color: #666;
  line-height: 1.6;
  margin-top: 20px;
}

.card-bottom {
  padding: 0 30px 30px;
}

.action-btn {
  width: 100%;
  padding: 14px 0;
  background: #333;
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.2s;
}

.action-btn:hover {
  background: #000;
}

/* Background Decorations */
.background-decorations {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.circle {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(201,169,110,0.15) 0%, rgba(201,169,110,0) 70%);
}

.top-right {
  width: 300px;
  height: 300px;
  top: -100px;
  right: -100px;
}

.bottom-left {
  width: 400px;
  height: 400px;
  bottom: -150px;
  left: -150px;
}
</style>
