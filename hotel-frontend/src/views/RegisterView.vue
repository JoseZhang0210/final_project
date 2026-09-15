<template>
  <div class="auth-page-wrapper">
    <div class="auth-card-container" style="max-width: 720px;">
      <div class="auth-card">
        <h1>尊榮會員註冊</h1>
        <p class="subtitle">
          填寫個人資料並完成信箱驗證，開啟專屬禮遇、預訂與會員服務。
        </p>

        <!-- Google 帳號帶入提示橫幅 -->
        <div v-if="isGoogleSignup" class="google-notice-banner">
          <div class="google-badge-icon">
            <svg viewBox="0 0 24 24">
              <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
              <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
              <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.06H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.94l2.85-2.22.81-.63z"/>
              <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.06l3.66 2.84c.87-2.6 3.3-4.52 6.16-4.52z"/>
            </svg>
          </div>
          <div class="google-notice-content">
            <div class="google-notice-title">Google 帳號資料已自動帶入</div>
            <div class="google-notice-desc">
              已由 Google 帶入信箱 <strong>{{ googleInfo.email }}</strong> 與姓名 <strong>{{ googleInfo.name }}</strong>。請設定您的帳號密碼與個人資料即可完成註冊。
            </div>
          </div>
        </div>

        <form @submit.prevent="register">
          <!-- 區塊 1: 帳號安全 -->
          <div class="form-section">
            <div class="section-title">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide-icon"><rect width="18" height="11" x="3" y="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
              帳號安全設定
            </div>

            <div class="form-grid">
              <!-- 帳號 -->
              <div class="form-group full-width">
                <label for="username">
                  帳號 <span class="required">*</span>
                </label>
                <input
                  id="username"
                  v-model.trim="form.username"
                  type="text"
                  placeholder="請輸入 4~20 位英數字帳號"
                  autocomplete="username"
                  :class="{ 'is-invalid': errors.username }"
                  @blur="checkUsername"
                  required
                />
                <div v-if="errors.username" class="field-error">
                  {{ errors.username }}
                </div>
              </div>

              <!-- 密碼 -->
              <div class="form-group">
                <label for="password">
                  密碼 <span class="required">*</span>
                </label>
                <input
                  id="password"
                  v-model="form.password"
                  type="password"
                  placeholder="請輸入密碼 (至少 6 碼)"
                  autocomplete="new-password"
                  :class="{ 'is-invalid': errors.password }"
                  @blur="validatePassword"
                  required
                />
                <div v-if="errors.password" class="field-error">
                  {{ errors.password }}
                </div>
              </div>

              <!-- 確認密碼 -->
              <div class="form-group">
                <label for="confirmPassword">
                  確認密碼 <span class="required">*</span>
                </label>
                <input
                  id="confirmPassword"
                  v-model="form.confirmPassword"
                  type="password"
                  placeholder="請再次輸入密碼"
                  autocomplete="new-password"
                  :class="{ 'is-invalid': errors.confirmPassword }"
                  @blur="validateConfirmPassword"
                  required
                />
                <div v-if="errors.confirmPassword" class="field-error">
                  {{ errors.confirmPassword }}
                </div>
              </div>
            </div>
          </div>

          <!-- 區塊 2: 信箱驗證 -->
          <div class="form-section">
            <div class="section-title">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide-icon"><rect width="20" height="16" x="2" y="4" rx="2"/><path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7"/></svg>
              電子信箱與身份驗證
            </div>

            <div class="form-grid">
              <!-- 電子信箱 + 發送按鈕 -->
              <div class="form-group full-width">
                <label for="email">
                  電子信箱 <span class="required">*</span>
                </label>
                <div class="email-input-group">
                  <input
                    id="email"
                    v-model.trim="form.email"
                    type="email"
                    placeholder="例：user@example.com"
                    autocomplete="email"
                    :class="{ 'is-invalid': errors.email }"
                    @blur="checkEmail"
                    required
                  />
                  <button
                    type="button"
                    class="btn-send-code"
                    :disabled="sendingCode || countdown > 0 || !form.email"
                    @click="sendVerificationCode"
                  >
                    <span v-if="sendingCode">發送中...</span>
                    <span v-else-if="countdown > 0">{{ countdown }} 秒後重新發送</span>
                    <span v-else>發送驗證碼</span>
                  </button>
                </div>
                <div v-if="errors.email" class="field-error">
                  {{ errors.email }}
                </div>
                <div v-if="isGoogleSignup" class="google-verified-tag">
                  ✓ 此電子信箱已由 Google 身份驗證通過
                </div>
                <small v-else class="field-hint">驗證信件將發送至此信箱，請留意收件匣或垃圾郵件。</small>
              </div>

              <!-- 驗證碼 -->
              <div class="form-group full-width">
                <label for="verificationCode">
                  信箱 6 位數驗證碼 <span class="required">*</span>
                </label>
                <input
                  id="verificationCode"
                  v-model.trim="form.verificationCode"
                  type="text"
                  placeholder="請輸入信件中的 6 位數驗證碼"
                  class="code-input"
                  required
                />
              </div>
            </div>
          </div>

          <!-- 區塊 3: 個人基本資料 -->
          <div class="form-section">
            <div class="section-title">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide-icon"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
              個人基本資料
            </div>

            <div class="form-grid">
              <div class="form-group">
                <label for="name">
                  真實姓名 <span class="required">*</span>
                </label>
                <input
                  id="name"
                  v-model.trim="form.name"
                  type="text"
                  placeholder="請輸入真實姓名"
                  required
                />
              </div>

              <div class="form-group">
                <label> 性別 </label>
                <div class="gender-radio-group">
                  <label class="radio-label">
                    <input type="radio" v-model="form.gender" value="男" /> 男
                  </label>
                  <label class="radio-label">
                    <input type="radio" v-model="form.gender" value="女" /> 女
                  </label>
                  <label class="radio-label">
                    <input type="radio" v-model="form.gender" value="其他" /> 其他
                  </label>
                </div>
              </div>

              <div class="form-group">
                <label for="phone"> 聯絡電話 </label>
                <input
                  id="phone"
                  v-model.trim="form.phone"
                  type="tel"
                  placeholder="例：0912345678"
                />
              </div>

              <div class="form-group">
                <label for="birthday"> 出生日期 </label>
                <input id="birthday" v-model="form.birthday" type="date" />
              </div>
            </div>
          </div>

          <!-- 區塊 4: 通訊地址 -->
          <div class="form-section">
            <div class="section-title">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="lucide-icon"><path d="M20 10c0 4.993-5.539 10.193-7.399 11.799a1 1 0 0 1-1.202 0C9.539 20.193 4 14.993 4 10a8 8 0 0 1 16 0"/><circle cx="12" cy="10" r="3"/></svg>
              通訊地址 (選填)
            </div>

            <div class="form-grid">
              <div class="form-group">
                <label for="zipcode"> 郵遞區號 </label>
                <input
                  id="zipcode"
                  v-model.trim="form.zipcode"
                  type="text"
                  placeholder="例：320"
                />
              </div>

              <div class="form-group">
                <label for="city"> 縣市 </label>
                <input
                  id="city"
                  v-model.trim="form.city"
                  type="text"
                  placeholder="例：桃園市"
                />
              </div>

              <div class="form-group">
                <label for="district"> 鄉鎮市區 </label>
                <input
                  id="district"
                  v-model.trim="form.district"
                  type="text"
                  placeholder="例：中壢區"
                />
              </div>

              <div class="form-group">
                <label for="address"> 詳細地址 </label>
                <input
                  id="address"
                  v-model.trim="form.address"
                  type="text"
                  placeholder="例：中央路一段1號"
                />
              </div>
            </div>
          </div>

          <!-- 按鈕群組 -->
          <div class="auth-button-group">
            <button
              type="submit"
              class="btn-auth-primary"
              :disabled="loading"
            >
              {{ loading ? "註冊處理中..." : "確認註冊" }}
            </button>

            <RouterLink to="/" class="btn-auth-outline"> 回首頁 </RouterLink>
          </div>
        </form>

        <div class="login-link">
          已經有會員帳號？
          <RouterLink to="/login"> 前往登入 </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useToastStore } from "@/stores/toast";
import { useAuthStore } from "@/stores/auth";
import { useVerificationCode } from "@/composables/useVerificationCode";
import { validatePasswordFormat, validateConfirmPasswordFormat } from "@/composables/usePasswordValidation";
import "@/assets/auth-form.css";

const route = useRoute();
const router = useRouter();
const toastStore = useToastStore();
const authStore = useAuthStore();

// Google 註冊帶入狀態
const isGoogleSignup = ref(false);
const googleInfo = reactive({
  email: "",
  name: "",
});

// 表單與錯誤訊息
const form = reactive({
  username: "",
  password: "",
  confirmPassword: "",
  email: "",
  verificationCode: "",
  name: "",
  gender: "其他",
  phone: "",
  birthday: "",
  zipcode: "",
  city: "",
  district: "",
  address: "",
});

const errors = reactive({
  username: "",
  password: "",
  confirmPassword: "",
  email: "",
});

const loading = ref(false);
const { sendingCode, countdown, startCountdown } = useVerificationCode();

onMounted(() => {
  let googleData = null;
  const sessionData = sessionStorage.getItem("google_signup_data");
  if (sessionData) {
    try {
      googleData = JSON.parse(sessionData);
    } catch (e) {
      console.warn("解析 Google 暫存資料失敗：", e);
    }
  }

  const queryFrom = route.query.from;
  const queryEmail = route.query.email ? decodeURIComponent(route.query.email) : "";
  const queryName = route.query.name ? decodeURIComponent(route.query.name) : "";
  const queryCode = route.query.code || "";

  if (queryFrom === "google" || (googleData && googleData.from === "google")) {
    isGoogleSignup.value = true;
    const targetEmail = queryEmail || googleData?.email || "";
    const targetName = queryName || googleData?.name || "";
    const targetCode = queryCode || googleData?.googleVerifiedCode || "";

    if (targetEmail) {
      form.email = targetEmail;
      googleInfo.email = targetEmail;
    }
    if (targetName) {
      form.name = targetName;
      googleInfo.name = targetName;
    }
    if (targetCode) {
      form.verificationCode = targetCode;
    }

    if (targetEmail && !form.username) {
      let suggestedUsername = targetEmail.split("@")[0].replace(/[^a-zA-Z0-9_]/g, "");
      if (suggestedUsername.length < 4) {
        suggestedUsername = suggestedUsername + "1234".substring(0, 4 - suggestedUsername.length);
      } else if (suggestedUsername.length > 20) {
        suggestedUsername = suggestedUsername.substring(0, 20);
      }
      form.username = suggestedUsername;
    }

    toastStore.showToast("🌟 已為您帶入 Google 帳號資料，請設定密碼完成註冊！", "info");
  }
});

async function checkUsername() {
  const username = form.username.trim();
  if (!username) {
    errors.username = "請輸入帳號";
    return false;
  }
  if (username.length < 4 || username.length > 20) {
    errors.username = "帳號長度需為 4~20 個字元";
    return false;
  }

  try {
    const response = await fetch(`/api/auth/check-username?username=${encodeURIComponent(username)}`);
    const data = await response.json().catch(() => ({}));
    if (data.exists) {
      errors.username = "此帳號已被註冊，請更換其他帳號名稱";
      return false;
    } else {
      errors.username = "";
      return true;
    }
  } catch (error) {
    console.error("檢查帳號重複錯誤：", error);
    errors.username = "";
    return true;
  }
}

async function checkEmail() {
  const email = form.email.trim();
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!email) {
    errors.email = "請輸入電子信箱";
    return false;
  }
  if (!emailRegex.test(email)) {
    errors.email = "請輸入正確格式的電子郵件信箱";
    return false;
  }

  try {
    const response = await fetch(`/api/auth/check-email?email=${encodeURIComponent(email)}`);
    const data = await response.json().catch(() => ({}));
    if (data.exists) {
      errors.email = "此電子信箱已被註冊，請更換其他信箱或直接登入";
      return false;
    } else {
      errors.email = "";
      return true;
    }
  } catch (error) {
    console.error("檢查信箱重複錯誤：", error);
    errors.email = "";
    return true;
  }
}

function validatePassword() {
  errors.password = validatePasswordFormat(form.password);
  if (!errors.password && form.confirmPassword) {
    validateConfirmPassword();
  }
  return !errors.password;
}

function validateConfirmPassword() {
  errors.confirmPassword = validateConfirmPasswordFormat(form.password, form.confirmPassword);
  return !errors.confirmPassword;
}

async function sendVerificationCode() {
  const isEmailValid = await checkEmail();
  if (!isEmailValid) {
    if (errors.email) {
      toastStore.showToast(errors.email, "error");
    }
    return;
  }

  const email = form.email.trim();
  sendingCode.value = true;

  try {
    const response = await fetch("/api/auth/send-code", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email }),
    });

    const data = await response.json().catch(() => ({}));
    if (!response.ok) {
      toastStore.showToast(data.message || "發送驗證碼失敗，請稍後再試", "error");
      return;
    }

    toastStore.showToast(data.message || "驗證碼已寄出，請至信箱收取！", "success");
    startCountdown(60);
  } catch (error) {
    console.error("發送驗證碼錯誤：", error);
    toastStore.showToast("無法連接伺服器發送驗證碼，請檢查網路連線", "error");
  } finally {
    sendingCode.value = false;
  }
}

async function register() {
  const isUsernameValid = await checkUsername();
  const isEmailValid = await checkEmail();
  const isPasswordValid = validatePassword();
  const isConfirmPasswordValid = validateConfirmPassword();

  if (!isUsernameValid || !isEmailValid || !isPasswordValid || !isConfirmPasswordValid) {
    toastStore.showToast("請檢查表單輸入是否有誤", "error");
    return;
  }

  if (!form.email.trim()) {
    toastStore.showToast("請輸入電子郵件信箱", "error");
    return;
  }
  if (!form.verificationCode.trim()) {
    toastStore.showToast("請輸入收到的信箱 6 位數驗證碼", "error");
    return;
  }
  if (!form.name.trim()) {
    toastStore.showToast("請輸入真實姓名", "error");
    return;
  }

  loading.value = true;
  const payload = {
    username: form.username.trim(),
    password: form.password,
    email: form.email.trim(),
    verificationCode: form.verificationCode.trim(),
    name: form.name.trim(),
    gender: form.gender,
    phone: form.phone.trim(),
    birthday: form.birthday || null,
    zipcode: form.zipcode.trim(),
    city: form.city.trim(),
    district: form.district.trim(),
    address: form.address.trim(),
  };

  try {
    const response = await fetch("/api/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    const data = await response.json().catch(() => ({}));

    if (response.status === 409) {
      if (data.message && data.message.includes("信箱")) {
        errors.email = "此電子信箱已被註冊，請更換其他信箱或直接登入";
      } else {
        errors.username = data.message || "此帳號已被註冊，請更換其他帳號名稱";
      }
      toastStore.showToast(data.message || "此帳號或信箱已被註冊", "error");
      return;
    }

    if (!response.ok) {
      toastStore.showToast(data.message || "註冊失敗，請確認欄位資訊與驗證碼", "error");
      return;
    }

    sessionStorage.removeItem("google_signup_data");

    if (data.token && data.authorities) {
      authStore.login(data.token, data.authorities, data.name);
    } else {
      try {
        const loginResponse = await fetch("/api/auth/login", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            username: payload.username,
            password: payload.password,
          }),
        });
        if (loginResponse.ok) {
          const loginData = await loginResponse.json();
          authStore.login(loginData.token, loginData.authorities, loginData.name);
        }
      } catch (loginError) {
        console.error("自動登入失敗：", loginError);
      }
    }

    toastStore.showToast("🎉 註冊成功！已為您自動登入，即將前往首頁...", "success");
    const targetPath = route.query.redirect || "/";
    setTimeout(() => {
      router.push(targetPath);
    }, 1500);
  } catch (error) {
    console.error("註冊錯誤：", error);
    toastStore.showToast("無法連接後端伺服器，請稍後再試", "error");
  } finally {
    loading.value = false;
  }
}
</script>
