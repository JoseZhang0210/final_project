<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <div class="hotel-name">星澄飯店</div>

        <h1>會員登入</h1>

        <p class="subtitle">登入您的會員帳號， 使用星澄飯店商城與會員服務。</p>

        <form @submit.prevent="login">
          <div class="form-group">
            <label for="username"> 帳號 </label>

            <input
              id="username"
              v-model="username"
              type="text"
              placeholder="請輸入帳號"
              autocomplete="username"
              required
            />
          </div>

          <div class="form-group">
            <label for="password"> 密碼 </label>

            <input
              id="password"
              v-model="password"
              type="password"
              placeholder="請輸入密碼"
              autocomplete="current-password"
              required
            />
          </div>

          <button type="submit" class="login-button" :disabled="loading">
            {{ loading ? "登入中..." : "登入" }}
          </button>

          <div v-if="message" class="message" :class="messageType">
            {{ message }}
          </div>
        </form>

        <div class="link-area">
          還沒有會員帳號？

          <RouterLink to="/register"> 立即註冊 </RouterLink>
        </div>

        <div class="home-link">
          <RouterLink to="/"> ← 回到首頁 </RouterLink>
        </div>
      </div>
    </div>

    <!-- =========================================
         登入成功動畫
         ========================================= -->
    <Transition name="loading-fade">
      <div v-if="showLoginAnimation" class="login-loading-overlay">
        <!-- 背景裝飾 -->
        <div class="loading-decoration decoration-left"></div>
        <div class="loading-decoration decoration-right"></div>

        <!-- 中央文字 -->
        <div class="loading-content">
          <div class="loading-logo">✦</div>

          <div class="loading-hotel-name">STARLIGHT HOTEL</div>

          <h2>歡迎回來</h2>

          <p>正在為您開啟星澄飯店...</p>

          <!-- 三個 Loading 點 -->
          <div class="loading-dots">
            <span></span>
            <span></span>
            <span></span>
          </div>
        </div>

        <!-- =====================================
             小狐狸跑道
             ===================================== -->
        <div class="animal-area">
          <div class="animal-track">
            <div class="rolling-animal">🦊</div>
          </div>

          <div class="ground-line"></div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { useAuthStore } from "@/stores/auth";
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const authStore = useAuthStore();

// =========================================
// 登入表單
// =========================================

const username = ref("");
const password = ref("");

// API 登入中
const loading = ref(false);

// 登入成功動畫
const showLoginAnimation = ref(false);

// 訊息
const message = ref("");
const messageType = ref("");

// =========================================
// 延遲函式
// =========================================

function delay(ms) {
  return new Promise((resolve) => {
    setTimeout(resolve, ms);
  });
}

// =========================================
// 登入
// =========================================

async function login() {
  message.value = "";

  // 沒有輸入帳號密碼
  if (!username.value || !password.value) {
    message.value = "請輸入帳號與密碼";
    messageType.value = "error";
    return;
  }

  loading.value = true;

  try {
    // =====================================
    // 呼叫登入 API
    // =====================================

    const response = await fetch("/api/auth/login", {
      method: "POST",

      headers: {
        "Content-Type": "application/json",
      },

      body: JSON.stringify({
        username: username.value,
        password: password.value,
      }),
    });

    // =====================================
    // 登入失敗
    // =====================================

    if (!response.ok) {
      message.value = "帳號或密碼錯誤";
      messageType.value = "error";
      return;
    }

    // =====================================
    // 取得登入資料
    // =====================================

    const data = await response.json();

    console.log("登入結果：", data);

    // 儲存 JWT / 權限 / 使用者
    authStore.login(data.token, data.authorities, data.name);

    message.value = "登入成功";
    messageType.value = "success";

    // =====================================
    // 顯示登入動畫
    // =====================================

    showLoginAnimation.value = true;

    // 動畫播放 2.2 秒
    await delay(2200);

    // =====================================
    // 判斷登入後要去哪
    // =====================================

    if (data.authorities && data.authorities.includes("ROLE_ADMIN")) {
      await router.push("/admin");
    } else {
      await router.push("/");
    }
  } catch (error) {
    console.error("登入錯誤：", error);

    message.value = "無法連接後端伺服器";

    messageType.value = "error";
  } finally {
    loading.value = false;

    showLoginAnimation.value = false;
  }
}
</script>

<style scoped>
/* =========================================
   登入頁
   ========================================= */

.login-page {
  min-height: 100vh;

  font-family: Arial, "Microsoft JhengHei", sans-serif;

  background:
    linear-gradient(rgba(0, 0, 0, 0.45), rgba(0, 0, 0, 0.45)),
    url("https://images.unsplash.com/photo-1566073771259-6a8506099945") center /
      cover no-repeat;

  display: flex;

  justify-content: center;
  align-items: center;

  padding: 30px;
}

.login-container {
  width: 100%;

  max-width: 430px;
}

.login-card {
  background-color: rgba(255, 255, 255, 0.96);

  padding: 40px 34px;

  border-radius: 16px;

  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.22);
}

.hotel-name {
  text-align: center;

  color: #9b7435;

  font-size: 26px;

  font-weight: bold;

  letter-spacing: 2px;

  margin-bottom: 8px;
}

h1 {
  text-align: center;

  color: #4a3b2a;

  font-size: 28px;

  margin-bottom: 10px;
}

.subtitle {
  text-align: center;

  color: #777;

  font-size: 14px;

  line-height: 1.6;

  margin-bottom: 28px;
}

/* =========================================
   表單
   ========================================= */

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;

  margin-bottom: 8px;

  color: #554536;

  font-size: 14px;

  font-weight: bold;
}

input {
  width: 100%;

  padding: 12px 14px;

  border: 1px solid #d8d0c5;

  border-radius: 8px;

  font-size: 15px;

  font-family: inherit;

  transition: 0.25s;

  box-sizing: border-box;
}

input:focus {
  outline: none;

  border-color: #b58a46;

  box-shadow: 0 0 0 3px rgba(181, 138, 70, 0.14);
}

/* =========================================
   登入按鈕
   ========================================= */

.login-button {
  width: 100%;

  padding: 12px;

  border: none;

  border-radius: 8px;

  background-color: #b58a46;

  color: white;

  font-size: 15px;

  font-weight: bold;

  font-family: inherit;

  cursor: pointer;

  transition: 0.25s;
}

.login-button:hover {
  background-color: #8f692f;

  transform: translateY(-2px);
}

.login-button:disabled {
  opacity: 0.7;

  cursor: not-allowed;

  transform: none;
}

/* =========================================
   連結
   ========================================= */

.link-area {
  margin-top: 22px;

  text-align: center;

  font-size: 14px;

  color: #777;
}

.link-area a {
  color: #9b7435;

  font-weight: bold;

  text-decoration: none;
}

.link-area a:hover {
  text-decoration: underline;
}

.home-link {
  margin-top: 12px;

  text-align: center;
}

.home-link a {
  color: #666;

  text-decoration: none;

  font-size: 14px;
}

.home-link a:hover {
  color: #9b7435;
}

/* =========================================
   訊息
   ========================================= */

.message {
  margin-top: 15px;

  padding: 10px;

  border-radius: 8px;

  text-align: center;

  font-size: 14px;
}

.message.success {
  background-color: #e5f6eb;

  color: #257641;
}

.message.error {
  background-color: #fde9e7;

  color: #b3443c;
}

/* =========================================
   登入成功動畫 Overlay
   ========================================= */

.login-loading-overlay {
  position: fixed;

  inset: 0;

  z-index: 99999;

  overflow: hidden;

  display: flex;

  align-items: center;
  justify-content: center;

  background: linear-gradient(145deg, #fbf8f2 0%, #f1e8da 50%, #e9ddcc 100%);
}

/* =========================================
   中央文字
   ========================================= */

.loading-content {
  position: relative;

  z-index: 3;

  text-align: center;

  margin-top: -80px;
}

.loading-logo {
  color: #b58a46;

  font-size: 34px;

  margin-bottom: 12px;

  animation: logoGlow 1.2s ease-in-out infinite alternate;
}

.loading-hotel-name {
  color: #a27b43;

  font-size: 13px;

  font-weight: bold;

  letter-spacing: 5px;

  margin-bottom: 15px;
}

.loading-content h2 {
  margin: 0 0 10px;

  color: #4a3b2a;

  font-size: 32px;

  letter-spacing: 3px;
}

.loading-content p {
  margin: 0;

  color: #806f60;

  font-size: 15px;
}

/* =========================================
   Loading 點點點
   ========================================= */

.loading-dots {
  margin-top: 20px;

  display: flex;

  justify-content: center;

  gap: 7px;
}

.loading-dots span {
  width: 7px;
  height: 7px;

  border-radius: 50%;

  background: #b58a46;

  animation: loadingDot 1s ease-in-out infinite;
}

.loading-dots span:nth-child(2) {
  animation-delay: 0.15s;
}

.loading-dots span:nth-child(3) {
  animation-delay: 0.3s;
}

/* =========================================
   小動物區域
   ========================================= */

.animal-area {
  position: absolute;

  left: 0;
  right: 0;

  bottom: 70px;

  height: 100px;
}

.animal-track {
  position: relative;

  width: 100%;
  height: 80px;

  overflow: hidden;
}

/* =========================================
   小狐狸
   ========================================= */

.rolling-animal {
  position: absolute;

  left: -100px;
  bottom: 8px;

  font-size: 58px;

  line-height: 1;

  transform-origin: center;

  animation:
    animalMove 2.2s linear forwards,
    animalRoll 0.45s linear infinite,
    animalBounce 0.3s ease-in-out infinite alternate;
}

/* 地面線 */
.ground-line {
  width: calc(100% - 80px);

  height: 2px;

  margin: 0 auto;

  background: linear-gradient(
    90deg,
    transparent,
    rgba(155, 116, 53, 0.35),
    transparent
  );
}

/* =========================================
   背景圓形裝飾
   ========================================= */

.loading-decoration {
  position: absolute;

  border-radius: 50%;

  filter: blur(2px);

  opacity: 0.35;
}

.decoration-left {
  width: 380px;
  height: 380px;

  left: -160px;
  top: -100px;

  background: rgba(181, 138, 70, 0.22);
}

.decoration-right {
  width: 450px;
  height: 450px;

  right: -200px;
  bottom: -180px;

  background: rgba(132, 99, 61, 0.18);
}

/* =========================================
   動畫
   ========================================= */

/* 小狐狸往右移動 */
@keyframes animalMove {
  0% {
    left: -100px;
  }

  100% {
    left: calc(100% + 100px);
  }
}

/* 小狐狸旋轉 */
@keyframes animalRoll {
  from {
    rotate: 0deg;
  }

  to {
    rotate: 360deg;
  }
}

/* 上下輕微彈跳 */
@keyframes animalBounce {
  from {
    translate: 0 0;
  }

  to {
    translate: 0 -5px;
  }
}

/* Logo 發光 */
@keyframes logoGlow {
  from {
    opacity: 0.55;

    transform: scale(0.9);
  }

  to {
    opacity: 1;

    transform: scale(1.08);
  }
}

/* 點點動畫 */
@keyframes loadingDot {
  0%,
  100% {
    opacity: 0.3;

    transform: translateY(0);
  }

  50% {
    opacity: 1;

    transform: translateY(-6px);
  }
}

/* =========================================
   Overlay 淡入淡出
   ========================================= */

.loading-fade-enter-active {
  transition: opacity 0.35s ease;
}

.loading-fade-leave-active {
  transition: opacity 0.25s ease;
}

.loading-fade-enter-from,
.loading-fade-leave-to {
  opacity: 0;
}

/* =========================================
   RWD
   ========================================= */

@media (max-width: 520px) {
  .login-page {
    padding: 18px;
  }

  .login-card {
    padding: 32px 24px;
  }

  .loading-content h2 {
    font-size: 27px;
  }

  .loading-hotel-name {
    font-size: 11px;

    letter-spacing: 3px;
  }

  .rolling-animal {
    font-size: 48px;
  }

  .animal-area {
    bottom: 45px;
  }
}
</style>
