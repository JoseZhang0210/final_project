<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <h1>尊榮會員註冊</h1>

        <p class="subtitle">
          填寫個人資料並完成信箱驗證，開啟專屬禮遇、預訂與會員服務。
        </p>

        <!-- Google 帳號帶入提示橫幅 -->
        <div v-if="isGoogleSignup" class="google-notice-banner">
          <div class="google-badge-icon">
            <svg class="google-icon" viewBox="0 0 24 24">
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

        <form @submit.prevent="register" class="register-form">
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
                    class="btn btn-send-code"
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
              <!-- 姓名 -->
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

              <!-- 性別 -->
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

              <!-- 聯絡電話 -->
              <div class="form-group">
                <label for="phone"> 聯絡電話 </label>
                <input
                  id="phone"
                  v-model.trim="form.phone"
                  type="tel"
                  placeholder="例：0912345678"
                />
              </div>

              <!-- 出生日期 -->
              <div class="form-group">
                <label for="birthday"> 出生日期 </label>
                <input
                  id="birthday"
                  v-model="form.birthday"
                  type="date"
                />
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
              <!-- 郵遞區號 -->
              <div class="form-group">
                <label for="zipcode"> 郵遞區號 </label>
                <input
                  id="zipcode"
                  v-model.trim="form.zipcode"
                  type="text"
                  placeholder="例：320"
                />
              </div>

              <!-- 縣市 -->
              <div class="form-group">
                <label for="city"> 縣市 </label>
                <input
                  id="city"
                  v-model.trim="form.city"
                  type="text"
                  placeholder="例：桃園市"
                />
              </div>

              <!-- 鄉鎮市區 -->
              <div class="form-group">
                <label for="district"> 鄉鎮市區 </label>
                <input
                  id="district"
                  v-model.trim="form.district"
                  type="text"
                  placeholder="例：中壢區"
                />
              </div>

              <!-- 詳細地址 -->
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
          <div class="button-group">
            <button
              type="submit"
              class="btn btn-register"
              :disabled="loading"
            >
              {{ loading ? "註冊處理中..." : "確認註冊" }}
            </button>

            <RouterLink to="/" class="btn btn-home"> 回首頁 </RouterLink>
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
import { onMounted, onUnmounted, reactive, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { useToastStore } from "@/stores/toast";
import { authApi } from "@/api/authApi";

const route = useRoute();
const router = useRouter();
const toastStore = useToastStore();

// =====================================================
// Google 註冊帶入狀態
// =====================================================

const isGoogleSignup = ref(false);
const googleInfo = reactive({
  email: "",
  name: "",
});

// =====================================================
// 表單與錯誤訊息狀態
// =====================================================

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
const sendingCode = ref(false);
const countdown = ref(0);
let timer = null;

// =====================================================
// 掛載時檢查是否有 Google 帶入之資料
// =====================================================

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

    // 依 Email 前綴自動產生建議帳號名稱
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

// =====================================================
// 失焦驗證 (Blur Validations)
// =====================================================

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
    const data = await authApi.checkUsername(username);
    if (data && data.exists) {
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
    const data = await authApi.checkEmail(email);
    if (data && data.exists) {
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
  if (!form.password) {
    errors.password = "請輸入密碼";
    return false;
  }
  if (form.password.length < 6) {
    errors.password = "密碼長度至少需為 6 個字元";
    return false;
  }
  errors.password = "";

  // 若確認密碼已填寫，一併再觸發比對
  if (form.confirmPassword) {
    validateConfirmPassword();
  }
  return true;
}

function validateConfirmPassword() {
  if (!form.confirmPassword) {
    errors.confirmPassword = "請輸入確認密碼";
    return false;
  }
  if (form.password !== form.confirmPassword) {
    errors.confirmPassword = "兩次輸入的密碼不一致，請重新確認";
    return false;
  }
  errors.confirmPassword = "";
  return true;
}

// =====================================================
// 發送信箱驗證碼
// POST /api/auth/send-code
// =====================================================

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
    const res = await authApi.sendCode(email);
    const msg = typeof res === "string" ? res : (res && res.message);
    toastStore.showToast(msg || "驗證碼已寄出，請至信箱收取！", "success");

    // 啟動 60 秒倒數計時
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
    toastStore.showToast("無法連接伺服器發送驗證碼，請檢查網路連線", "error");
  } finally {
    sendingCode.value = false;
  }
}

// =====================================================
// 註冊提交
// POST /api/auth/register
// =====================================================

async function register() {
  // 觸發前端欄位失焦校驗
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
    await authApi.register(payload);
    const response = await fetch("/api/auth/register", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    console.log("註冊 API 回傳 status：", response.status);

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

    // 註冊成功，清除暫存
    sessionStorage.removeItem("google_signup_data");

    toastStore.showToast("🎉 註冊成功！即將前往登入頁面...", "success");

    // 1.5 秒後跳轉至登入頁
    setTimeout(() => {
      router.push("/login");
    }, 1500);
  } catch (error) {
    console.error("註冊錯誤：", error);
    toastStore.showToast("無法連接後端伺服器，請稍後再試", "error");
  } finally {
    loading.value = false;
  }
}

// 元件卸載時清除計時器
onUnmounted(() => {
  if (timer) clearInterval(timer);
});
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  font-family: "Segoe UI", Arial, "Microsoft JhengHei", sans-serif;
  background:
    linear-gradient(rgba(20, 16, 12, 0.65), rgba(20, 16, 12, 0.65)),
    url("https://images.unsplash.com/photo-1566073771259-6a8506099945") center /
      cover no-repeat fixed;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px 20px;
  box-sizing: border-box;
}

.register-container {
  width: 100%;
  max-width: 720px;
}

.register-card {
  background-color: rgba(255, 255, 255, 0.97);
  backdrop-filter: blur(8px);
  padding: 40px 45px;
  border-radius: 18px;
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.6);
}

h1 {
  text-align: center;
  color: #3b2c1d;
  font-size: 24px;
  margin: 0 0 8px 0;
  font-weight: 700;
}

.subtitle {
  text-align: center;
  color: #665b50;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 24px;
}

/* =========================
   區塊樣式
   ========================= */

.form-section {
  margin-bottom: 24px;
  background-color: #faf7f2;
  border: 1px solid #ede4d6;
  border-radius: 12px;
  padding: 18px 22px;
}

.section-title {
  font-size: 15px;
  font-weight: 700;
  color: #5c472a;
  margin-bottom: 14px;
  display: flex;
  align-items: center;
  gap: 8px;
  border-bottom: 1px dashed #ded4c3;
  padding-bottom: 8px;
}

.lucide-icon {
  display: inline-block;
  vertical-align: middle;
}

.gender-radio-group {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 8px 0;
  min-height: 42px;
  box-sizing: border-box;
}

.radio-label {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #333333;
  cursor: pointer;
  font-weight: normal;
  margin-bottom: 0;
}

.radio-label input[type="radio"] {
  width: auto;
  margin: 0;
  cursor: pointer;
  accent-color: #b58a46;
}

.form-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 14px 18px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group.full-width {
  grid-column: 1 / -1;
}

label {
  display: block;
  margin-bottom: 6px;
  color: #4a3b2a;
  font-size: 13.5px;
  font-weight: 600;
}

.required {
  color: #c93b2b;
  font-weight: bold;
}

input,
select {
  width: 100%;
  padding: 10px 13px;
  border: 1px solid #d4cbbd;
  border-radius: 8px;
  font-size: 14px;
  font-family: inherit;
  background-color: #ffffff;
  color: #333333;
  transition: all 0.2s ease;
  box-sizing: border-box;
}

input:focus,
select:focus {
  outline: none;
  border-color: #b58a46;
  background-color: #fff;
  box-shadow: 0 0 0 3px rgba(181, 138, 70, 0.18);
}

input.is-invalid {
  border-color: #c93b2b;
}

input.is-invalid:focus {
  box-shadow: 0 0 0 3px rgba(201, 59, 43, 0.18);
}

.field-error {
  color: #c93b2b;
  font-size: 12.5px;
  margin-top: 4px;
  font-weight: 500;
}

.code-input {
  letter-spacing: 4px;
  font-weight: bold;
  font-size: 16px;
  text-transform: uppercase;
}

/* 信箱與發送按鈕組合 */
.email-input-group {
  display: flex;
  gap: 10px;
}

.email-input-group input {
  flex: 1;
}

.btn-send-code {
  white-space: nowrap;
  padding: 10px 16px;
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
  font-size: 12px;
  color: #887a6d;
  margin-top: 5px;
}

/* =========================
   按鈕群組
   ========================= */

.button-group {
  display: flex;
  gap: 14px;
  margin-top: 28px;
}

.btn {
  flex: 1;
  padding: 13px;
  border: none;
  border-radius: 9px;
  font-size: 15px;
  font-weight: bold;
  text-align: center;
  text-decoration: none;
  cursor: pointer;
  transition: all 0.25s;
  font-family: inherit;
}

.btn-register {
  background: linear-gradient(135deg, #b58a46 0%, #946c2b 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(181, 138, 70, 0.3);
}

.btn-register:hover:not(:disabled) {
  background: linear-gradient(135deg, #a17838 0%, #825d22 100%);
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(181, 138, 70, 0.4);
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
  background-color: #ebe4d8;
  color: #554433;
}

.btn-home:hover {
  background-color: #ddd4c5;
  transform: translateY(-2px);
}

/* =========================
   訊息提示
   ========================= */

.message-banner {
  margin-bottom: 20px;
  padding: 12px 16px;
  border-radius: 9px;
  font-size: 14px;
  display: flex;
  align-items: center;
  gap: 10px;
  line-height: 1.5;
}

.message-banner.success {
  background-color: #eaf6ee;
  color: #1b6d39;
  border: 1px solid #bce2c7;
}

.message-banner.error {
  background-color: #fdf0ee;
  color: #b83328;
  border: 1px solid #f6c8c4;
}

.message-icon {
  font-weight: bold;
  font-size: 16px;
}

/* =========================
   Login Link
   ========================= */

.login-link {
  margin-top: 24px;
  text-align: center;
  font-size: 14.5px;
  color: #6c6052;
}

.login-link a {
  color: #9b7435;
  font-weight: bold;
  text-decoration: none;
  margin-left: 4px;
}

.login-link a:hover {
  text-decoration: underline;
}

/* =========================
   Google 註冊提示與標籤
   ========================= */

.google-notice-banner {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  background: linear-gradient(135deg, #f0f7ff 0%, #e8f3fe 100%);
  border: 1px solid #c4defc;
  border-radius: 12px;
  padding: 16px 20px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(66, 133, 244, 0.08);
}

.google-badge-icon {
  width: 24px;
  height: 24px;
  flex-shrink: 0;
  margin-top: 2px;
}

.google-icon {
  width: 24px;
  height: 24px;
}

.google-notice-content {
  flex: 1;
}

.google-notice-title {
  font-size: 15px;
  font-weight: 700;
  color: #1a73e8;
  margin-bottom: 4px;
}

.google-notice-desc {
  font-size: 13.5px;
  color: #3c4043;
  line-height: 1.5;
}

.google-verified-tag {
  display: inline-flex;
  align-items: center;
  gap: 5px;
  background-color: #e6f4ea;
  color: #137333;
  font-size: 12.5px;
  font-weight: 600;
  padding: 4px 10px;
  border-radius: 6px;
  border: 1px solid #ceead6;
  margin-top: 6px;
}

/* =========================
   響應式設計
   ========================= */

@media (max-width: 680px) {
  .register-card {
    padding: 30px 20px;
  }

  .form-grid {
    grid-template-columns: 1fr;
    gap: 12px;
  }

  .email-input-group {
    flex-direction: column;
  }

  .button-group {
    flex-direction: column;
  }

  .google-notice-banner {
    padding: 12px 14px;
    gap: 10px;
  }
}
</style>

