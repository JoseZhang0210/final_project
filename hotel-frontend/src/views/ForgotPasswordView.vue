<template>
  <div class="forgot-page">
    <div class="forgot-container">
      <div class="forgot-card">
        <!-- 飯店品牌標頭 -->
        <div class="hotel-brand">
          <div class="hotel-name">星澄飯店</div>
          <div class="hotel-en">GRAND ASTER HOTEL & RESORTS</div>
        </div>

        <h1>重設會員密碼</h1>

        <p class="subtitle">
          請輸入您的註冊電子信箱以獲取驗證碼，驗證身分後即可重新設定登入密碼。
        </p>

        <form @submit.prevent="handleResetPassword" class="forgot-form">
          <!-- 電子信箱 + 發送按鈕 -->
          <div class="form-group">
            <label for="email">
              註冊電子信箱 <span class="required">*</span>
            </label>
            <div class="email-input-group">
              <input
                id="email"
                v-model.trim="form.email"
                type="email"
                placeholder="例：user@example.com"
                autocomplete="email"
                :disabled="sendingCode || loading"
                required
              />
              <button
                type="button"
                class="btn-send-code"
                :disabled="sendingCode || countdown > 0 || !form.email"
                @click="sendVerificationCode"
              >
                <span v-if="sendingCode">發送中...</span>
                <span v-else-if="countdown > 0">{{ countdown }} 秒後重試</span>
                <span v-else>發送驗證碼</span>
              </button>
            </div>
            <small class="field-hint">驗證信件將寄送至此信箱，請留意收件匣或垃圾郵件。</small>
          </div>

          <!-- 6 位數驗證碼 -->
          <div class="form-group">
            <label for="code">
              信箱 6 位數驗證碼 <span class="required">*</span>
            </label>
            <input
              id="code"
              v-model.trim="form.code"
              type="text"
              maxlength="6"
              placeholder="請輸入信件中的 6 位數驗證碼"
              class="code-input"
              :disabled="loading"
              required
            />
          </div>

          <!-- 新密碼 -->
          <div class="form-group">
            <label for="newPassword">
              新密碼 <span class="required">*</span>
            </label>
            <input
              id="newPassword"
              v-model="form.newPassword"
              type="password"
              placeholder="請輸入新密碼 (至少 6 碼)"
              autocomplete="new-password"
              :class="{ 'is-invalid': errors.newPassword }"
              @blur="validatePassword"
              :disabled="loading"
              required
            />
            <div v-if="errors.newPassword" class="field-error">
              {{ errors.newPassword }}
            </div>
          </div>

          <!-- 確認新密碼 -->
          <div class="form-group">
            <label for="confirmPassword">
              確認新密碼 <span class="required">*</span>
            </label>
            <input
              id="confirmPassword"
              v-model="form.confirmPassword"
              type="password"
              placeholder="請再次輸入新密碼"
              autocomplete="new-password"
              :class="{ 'is-invalid': errors.confirmPassword }"
              @blur="validateConfirmPassword"
              :disabled="loading"
              required
            />
            <div v-if="errors.confirmPassword" class="field-error">
              {{ errors.confirmPassword }}
            </div>
          </div>

          <!-- 提交按鈕 -->
          <button
            type="submit"
            class="submit-button"
            :disabled="loading"
          >
            {{ loading ? "重設處理中..." : "確認重設密碼" }}
          </button>
        </form>

        <!-- 導覽連結 -->
        <div class="link-area">
          想起來密碼了？
          <RouterLink to="/login">返回登入</RouterLink>
        </div>

        <div class="home-link">
          <RouterLink to="/">← 回到首頁</RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onUnmounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { useToastStore } from "@/stores/toast";

const router = useRouter();
const toastStore = useToastStore();

const form = reactive({
  email: "",
  code: "",
  newPassword: "",
  confirmPassword: "",
});

const errors = reactive({
  newPassword: "",
  confirmPassword: "",
});

const loading = ref(false);
const sendingCode = ref(false);
const countdown = ref(0);
let timer = null;

// =====================================================
// 密碼校驗
// =====================================================

function validatePassword() {
  if (!form.newPassword) {
    errors.newPassword = "請輸入新密碼";
    return false;
  }
  if (form.newPassword.length < 6) {
    errors.newPassword = "密碼長度至少需為 6 個字元";
    return false;
  }
  errors.newPassword = "";

  if (form.confirmPassword) {
    validateConfirmPassword();
  }
  return true;
}

function validateConfirmPassword() {
  if (!form.confirmPassword) {
    errors.confirmPassword = "請輸入確認新密碼";
    return false;
  }
  if (form.newPassword !== form.confirmPassword) {
    errors.confirmPassword = "兩次輸入的密碼不一致，請重新確認";
    return false;
  }
  errors.confirmPassword = "";
  return true;
}

// =====================================================
// 發送重設驗證碼
// POST /api/auth/forgot-password/send-code
// =====================================================

async function sendVerificationCode() {
  const email = form.email.trim();
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  if (!email || !emailRegex.test(email)) {
    toastStore.showToast("請輸入正確格式的電子信箱", "error");
    return;
  }

  sendingCode.value = true;

  try {
    const response = await fetch("/api/auth/forgot-password/send-code", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email: email }),
    });

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      toastStore.showToast(data.message || "發送驗證碼失敗，請稍後再試", "error");
      return;
    }

    toastStore.showToast(data.message || "驗證碼已發送至您的信箱，請於 5 分鐘內輸入！", "success");

    // 啟動 60 秒冷卻倒數
    countdown.value = 60;
    if (timer) clearInterval(timer);
    timer = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) {
        clearInterval(timer);
        timer = null;
      }
    }, 1000);
  } catch (error) {
    console.error("發送驗證碼錯誤：", error);
    toastStore.showToast("無法連接伺服器，請檢查網路連線", "error");
  } finally {
    sendingCode.value = false;
  }
}

// =====================================================
// 重設密碼送出
// POST /api/auth/forgot-password/reset
// =====================================================

async function handleResetPassword() {
  if (!form.email.trim()) {
    toastStore.showToast("請輸入電子信箱", "error");
    return;
  }

  if (!form.code.trim()) {
    toastStore.showToast("請輸入信箱 6 位數驗證碼", "error");
    return;
  }

  const isPasswordValid = validatePassword();
  const isConfirmValid = validateConfirmPassword();

  if (!isPasswordValid || !isConfirmValid) {
    toastStore.showToast("請檢查密碼輸入是否有誤", "error");
    return;
  }

  loading.value = true;

  try {
    const response = await fetch("/api/auth/forgot-password/reset", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        email: form.email.trim(),
        code: form.code.trim(),
        newPassword: form.newPassword,
      }),
    });

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      toastStore.showToast(data.message || "密碼重設失敗，請確認驗證碼是否正確", "error");
      return;
    }

    toastStore.showToast("🎉 密碼重設成功！即將前往登入頁面...", "success");

    // 1.5 秒後跳轉至登入頁
    setTimeout(() => {
      router.push("/login");
    }, 1500);
  } catch (error) {
    console.error("重設密碼錯誤：", error);
    toastStore.showToast("無法連接後端伺服器，請稍後再試", "error");
  } finally {
    loading.value = false;
  }
}

onUnmounted(() => {
  if (timer) clearInterval(timer);
});
</script>

<style scoped>
.forgot-page {
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
  box-sizing: border-box;
}

.forgot-container {
  width: 100%;
  max-width: 460px;
}

.forgot-card {
  background-color: rgba(255, 255, 255, 0.96);
  padding: 40px 34px;
  border-radius: 16px;
  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.22);
}

.hotel-brand {
  text-align: center;
  margin-bottom: 6px;
}

.hotel-name {
  color: #9b7435;
  font-size: 26px;
  font-weight: bold;
  letter-spacing: 2px;
}

.hotel-en {
  color: #998369;
  font-size: 11px;
  letter-spacing: 2px;
  margin-top: 2px;
  font-weight: 600;
}

h1 {
  text-align: center;
  color: #4a3b2a;
  font-size: 26px;
  margin: 12px 0 8px 0;
  font-weight: bold;
}

.subtitle {
  text-align: center;
  color: #777;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 24px;
}

.form-group {
  margin-bottom: 18px;
}

label {
  display: block;
  margin-bottom: 8px;
  color: #554536;
  font-size: 14px;
  font-weight: bold;
}

.required {
  color: #c93b2b;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 12px 14px;
  border: 1px solid #d8d0c5;
  border-radius: 8px;
  font-size: 14.5px;
  font-family: inherit;
  transition: 0.25s;
  box-sizing: border-box;
  background-color: #fff;
}

input:focus {
  outline: none;
  border-color: #b58a46;
  box-shadow: 0 0 0 3px rgba(181, 138, 70, 0.14);
}

input.is-invalid {
  border-color: #c93b2b;
}

.field-error {
  color: #c93b2b;
  font-size: 12.5px;
  margin-top: 5px;
  font-weight: 500;
}

.code-input {
  letter-spacing: 4px;
  font-weight: bold;
  font-size: 16px;
  text-transform: uppercase;
}

.email-input-group {
  display: flex;
  gap: 10px;
}

.email-input-group input {
  flex: 1;
}

.btn-send-code {
  white-space: nowrap;
  padding: 10px 14px;
  font-size: 13.5px;
  background-color: #8c6d3b;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
  transition: background-color 0.2s, transform 0.1s;
}

.btn-send-code:hover:not(:disabled) {
  background-color: #6e542c;
}

.btn-send-code:disabled {
  background-color: #c2b7a6;
  cursor: not-allowed;
}

.field-hint {
  display: block;
  font-size: 12px;
  color: #887a6d;
  margin-top: 5px;
}

.submit-button {
  width: 100%;
  padding: 13px;
  border: none;
  border-radius: 8px;
  background: linear-gradient(135deg, #b58a46 0%, #946c2b 100%);
  color: white;
  font-size: 15px;
  font-weight: bold;
  font-family: inherit;
  cursor: pointer;
  transition: 0.25s;
  margin-top: 10px;
  box-shadow: 0 4px 12px rgba(181, 138, 70, 0.28);
}

.submit-button:hover:not(:disabled) {
  background: linear-gradient(135deg, #a17838 0%, #825d22 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(181, 138, 70, 0.38);
}

.submit-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

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

@media (max-width: 520px) {
  .forgot-page {
    padding: 18px;
  }

  .forgot-card {
    padding: 32px 24px;
  }
}
</style>

