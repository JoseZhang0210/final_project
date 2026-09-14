<template>
  <div class="auth-page-wrapper">
    <div class="auth-card-container" style="max-width: 460px;">
      <div class="auth-card">
        <!-- 飯店品牌標頭 -->
        <div class="hotel-brand">
          <div class="hotel-name">星澄飯店</div>
          <div class="hotel-en">GRAND ASTER HOTEL & RESORTS</div>
        </div>

        <h1>重設會員密碼</h1>

        <p class="subtitle">
          請輸入您的註冊電子信箱以獲取驗證碼，驗證身分後即可重新設定登入密碼。
        </p>

        <form @submit.prevent="handleResetPassword">
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
          <div class="auth-button-group">
            <button
              type="submit"
              class="btn-auth-primary"
              :disabled="loading"
            >
              {{ loading ? "重設處理中..." : "確認重設密碼" }}
            </button>
          </div>
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
import { reactive, ref } from "vue";
import { useRouter } from "vue-router";
import { useToastStore } from "@/stores/toast";
import { useVerificationCode } from "@/composables/useVerificationCode";
import { validatePasswordFormat, validateConfirmPasswordFormat } from "@/composables/usePasswordValidation";
import "@/assets/auth-form.css";

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
const { sendingCode, countdown, startCountdown } = useVerificationCode();

function validatePassword() {
  errors.newPassword = validatePasswordFormat(form.newPassword);
  if (!errors.newPassword && form.confirmPassword) {
    validateConfirmPassword();
  }
  return !errors.newPassword;
}

function validateConfirmPassword() {
  errors.confirmPassword = validateConfirmPasswordFormat(form.newPassword, form.confirmPassword);
  return !errors.confirmPassword;
}

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
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email }),
    });

    const data = await response.json().catch(() => ({}));
    if (!response.ok) {
      toastStore.showToast(data.message || "發送驗證碼失敗，請稍後再試", "error");
      return;
    }

    toastStore.showToast(data.message || "驗證碼已發送至您的信箱，請於 5 分鐘內輸入！", "success");
    startCountdown(60);
  } catch (error) {
    console.error("發送驗證碼錯誤：", error);
    toastStore.showToast("無法連接伺服器，請檢查網路連線", "error");
  } finally {
    sendingCode.value = false;
  }
}

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
      headers: { "Content-Type": "application/json" },
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
</script>
