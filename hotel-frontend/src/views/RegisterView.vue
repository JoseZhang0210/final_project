<template>
  <div class="register-page">
    <div class="register-container">
      <div class="register-card">
        <!-- 飯店品牌標頭 -->
        <div class="hotel-brand">
          <div class="hotel-name">星澄飯店</div>
          <div class="hotel-en">GRAND ASTER HOTEL & RESORTS</div>
        </div>

        <h1>尊榮會員註冊</h1>

        <p class="subtitle">
          填寫個人資料並完成信箱驗證，開啟專屬禮遇、預訂與會員服務。
        </p>

        <!-- 訊息提示 -->
        <div v-if="message" class="message-banner" :class="messageType">
          <span class="message-icon">{{ messageType === 'success' ? '✓' : '⚠️' }}</span>
          <span>{{ message }}</span>
        </div>

        <form @submit.prevent="register" class="register-form">
          <!-- 區塊 1: 帳號安全 -->
          <div class="form-section">
            <div class="section-title">
              <span class="section-icon">🔐</span> 帳號安全設定
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
                  required
                />
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
                  required
                />
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
                  required
                />
              </div>
            </div>
          </div>

          <!-- 區塊 2: 信箱驗證 -->
          <div class="form-section">
            <div class="section-title">
              <span class="section-icon">✉️</span> 電子信箱與身份驗證
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
                <small class="field-hint">驗證信件將發送至此信箱，請留意收件匣或垃圾郵件。</small>
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
                  maxlength="6"
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
              <span class="section-icon">👤</span> 個人基本資料
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
                <label for="gender"> 性別 </label>
                <select id="gender" v-model="form.gender">
                  <option value="男">男</option>
                  <option value="女">女</option>
                  <option value="其他">其他</option>
                </select>
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
              <span class="section-icon">📍</span> 通訊地址 (選填)
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
import { onUnmounted, reactive, ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

// =====================================================
// 表單狀態
// =====================================================

const form = reactive({
  username: "",
  password: "",
  confirmPassword: "",
  email: "",
  verificationCode: "",
  name: "",
  gender: "男",
  phone: "",
  birthday: "",
  zipcode: "",
  city: "",
  district: "",
  address: "",
});

const loading = ref(false);
const sendingCode = ref(false);
const countdown = ref(0);
let timer = null;

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
// 發送信箱驗證碼
// POST /api/auth/send-code
// =====================================================

async function sendVerificationCode() {
  const email = form.email.trim();
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

  if (!email || !emailRegex.test(email)) {
    showMessage("請輸入正確格式的電子郵件信箱", "error");
    return;
  }

  sendingCode.value = true;
  message.value = "";

  try {
    const response = await fetch("/api/auth/send-code", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ email: email }),
    });

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      showMessage(data.message || "發送驗證碼失敗，請稍後再試", "error");
      return;
    }

    showMessage(data.message || "驗證碼已寄出，請至信箱收取！", "success");

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
    showMessage("無法連接伺服器發送驗證碼，請檢查網路連線", "error");
  } finally {
    sendingCode.value = false;
  }
}

// =====================================================
// 註冊提交
// POST /api/auth/register
// =====================================================

async function register() {
  message.value = "";

  // ==========================
  // 前端欄位校驗
  // ==========================

  if (!form.username.trim()) {
    showMessage("請輸入帳號", "error");
    return;
  }

  if (!form.password) {
    showMessage("請輸入密碼", "error");
    return;
  }

  if (form.password.length < 6) {
    showMessage("密碼長度至少需為 6 個字元", "error");
    return;
  }

  if (form.password !== form.confirmPassword) {
    showMessage("兩次輸入的密碼不一致，請重新確認", "error");
    return;
  }

  if (!form.email.trim()) {
    showMessage("請輸入電子郵件信箱", "error");
    return;
  }

  if (!form.verificationCode.trim()) {
    showMessage("請輸入收到的信箱 6 位數驗證碼", "error");
    return;
  }

  if (!form.name.trim()) {
    showMessage("請輸入真實姓名", "error");
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
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(payload),
    });

    console.log("註冊 API 回傳 status：", response.status);

    const data = await response.json().catch(() => ({}));

    if (response.status === 409) {
      showMessage("此帳號已被註冊，請更換其他帳號名稱", "error");
      return;
    }

    if (!response.ok) {
      showMessage(data.message || "註冊失敗，請確認欄位資訊與驗證碼", "error");
      return;
    }

    showMessage("🎉 註冊成功！即將前往登入頁面...", "success");

    // 1.5 秒後跳轉至登入頁
    setTimeout(() => {
      router.push("/login");
    }, 1500);
  } catch (error) {
    console.error("註冊錯誤：", error);
    showMessage("無法連接後端伺服器，請稍後再試", "error");
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

.hotel-brand {
  text-align: center;
  margin-bottom: 6px;
}

.hotel-name {
  color: #9b7435;
  font-size: 28px;
  font-weight: 800;
  letter-spacing: 3px;
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
  color: #3b2c1d;
  font-size: 24px;
  margin: 14px 0 8px 0;
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

.section-icon {
  font-size: 16px;
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
}
</style>

