<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <div class="hotel-name">星澄飯店</div>

        <h1>會員註冊</h1>

        <p class="subtitle">
          建立您的會員帳號，開始使用星澄飯店的商城與會員服務。
        </p>

        <form @submit.prevent="register">
          <!-- 帳號 -->
          <div class="form-group">
            <label for="username"> 帳號 </label>

            <input
              id="username"
              v-model.trim="username"
              type="text"
              placeholder="請輸入帳號"
              autocomplete="username"
              required
            />
          </div>

          <!-- 密碼 -->
          <div class="form-group">
            <label for="password"> 密碼 </label>

            <input
              id="password"
              v-model="password"
              type="password"
              placeholder="請輸入密碼"
              autocomplete="new-password"
              required
            />
          </div>

          <!-- 確認密碼 -->
          <div class="form-group">
            <label for="confirmPassword"> 確認密碼 </label>

            <input
              id="confirmPassword"
              v-model="confirmPassword"
              type="password"
              placeholder="請再次輸入密碼"
              autocomplete="new-password"
              required
            />
          </div>

          <!-- 按鈕 -->
          <div class="button-group">
            <button type="submit" class="btn btn-register" :disabled="loading">
              {{ loading ? "註冊中..." : "註冊" }}
            </button>

            <RouterLink to="/" class="btn btn-home"> 回首頁 </RouterLink>
          </div>

          <!-- 訊息 -->
          <div v-if="message" class="message" :class="messageType">
            {{ message }}
          </div>
        </form>

        <div class="login-link">
          已經有帳號？

          <RouterLink to="/login"> 前往登入 </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const username = ref("");

const password = ref("");

const confirmPassword = ref("");

const loading = ref(false);

const message = ref("");

const messageType = ref("");

// =====================================================
// 顯示訊息
// =====================================================

function showMessage(text, type) {
  message.value = text;

  messageType.value = type;
}

// =====================================================
// 註冊
// POST /api/auth/register
// =====================================================

async function register() {
  message.value = "";

  // ==========================
  // 基本驗證
  // ==========================

  if (!username.value || !password.value) {
    showMessage("請輸入帳號與密碼", "error");

    return;
  }

  if (password.value !== confirmPassword.value) {
    showMessage("兩次輸入的密碼不一致", "error");

    return;
  }

  loading.value = true;

  try {
    const response = await fetch("/api/auth/register", {
      method: "POST",

      headers: {
        "Content-Type": "application/json",
      },

      body: JSON.stringify({
        username: username.value,

        password: password.value,
      }),
    });

    console.log("註冊 API status：", response.status);

    // ==========================
    // 帳號重複
    // ==========================

    if (response.status === 409) {
      showMessage("此帳號已經存在", "error");

      return;
    }

    // ==========================
    // 其他錯誤
    // ==========================

    if (!response.ok) {
      showMessage("註冊失敗", "error");

      return;
    }

    const data = await response.json();

    console.log("註冊成功：", data);

    showMessage("註冊成功，即將前往登入頁面", "success");

    // 清空表單
    username.value = "";

    password.value = "";

    confirmPassword.value = "";

    // 1.2 秒後前往登入
    setTimeout(() => {
      router.push("/login");
    }, 1200);
  } catch (error) {
    console.error("註冊錯誤：", error);

    showMessage("無法連接後端伺服器", "error");
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.register-page {
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

.register-container {
  width: 100%;

  max-width: 430px;
}

.register-card {
  background-color: rgba(255, 255, 255, 0.96);

  padding: 38px 34px;

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

.button-group {
  display: flex;

  gap: 12px;

  margin-top: 28px;
}

.btn {
  flex: 1;

  padding: 12px;

  border: none;

  border-radius: 8px;

  font-size: 15px;

  font-weight: bold;

  text-align: center;

  text-decoration: none;

  cursor: pointer;

  transition: 0.25s;

  font-family: inherit;
}

.btn-register {
  background-color: #b58a46;

  color: white;
}

.btn-register:hover {
  background-color: #8f692f;

  transform: translateY(-2px);
}

.btn-register:disabled {
  opacity: 0.65;

  cursor: not-allowed;

  transform: none;
}

.btn-home {
  display: flex;

  justify-content: center;

  align-items: center;

  background-color: #eee9e1;

  color: #5c4d3d;
}

.btn-home:hover {
  background-color: #dfd5c7;

  transform: translateY(-2px);
}

/* =========================
   訊息
   ========================= */

.message {
  margin-top: 18px;

  padding: 11px 12px;

  border-radius: 8px;

  font-size: 14px;

  text-align: center;
}

.message.success {
  background-color: #e5f6eb;

  color: #257641;
}

.message.error {
  background-color: #fde9e7;

  color: #b3443c;
}

/* =========================
   Login Link
   ========================= */

.login-link {
  margin-top: 22px;

  text-align: center;

  font-size: 14px;

  color: #777;
}

.login-link a {
  color: #9b7435;

  font-weight: bold;

  text-decoration: none;
}

.login-link a:hover {
  text-decoration: underline;
}

@media (max-width: 520px) {
  .register-page {
    padding: 18px;
  }

  .register-card {
    padding: 30px 24px;
  }

  .button-group {
    flex-direction: column;
  }
}
</style>
