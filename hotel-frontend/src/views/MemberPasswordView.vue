<template>
  <div class="member-password-view">
    <div class="password-card">
      <!-- 頂部標題列 -->
      <div class="card-header">
        <div class="header-title">
          <h2>修改密碼</h2>
          <p>定期變更登入密碼以確保您的星澄會員帳號安全</p>
        </div>
      </div>

      <!-- 表單主體佈局 -->
      <form class="password-form-layout" @submit.prevent="handleSubmit">
        <!-- 左側：輸入表單區 -->
        <div class="form-fields-container">
          <!-- 1. 目前密碼 -->
          <div class="form-row">
            <label class="row-label" for="current-password">目前密碼</label>
            <div class="row-content">
              <div class="password-input-wrapper">
                <input
                  id="current-password"
                  v-model="form.currentPassword"
                  :type="showCurrentPassword ? 'text' : 'password'"
                  class="form-input"
                  :class="{ 'has-error': formErrors.currentPassword }"
                  placeholder="請輸入您目前的登入密碼"
                  autocomplete="current-password"
                  @blur="validateCurrentPassword"
                />
                <button
                  type="button"
                  class="toggle-pwd-btn"
                  tabindex="-1"
                  :title="showCurrentPassword ? '隱藏密碼' : '顯示密碼'"
                  @click="showCurrentPassword = !showCurrentPassword"
                >
                  <!-- Lucide Eye (密碼可見時顯示) -->
                  <svg
                    v-if="showCurrentPassword"
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    class="lucide-icon lucide-eye"
                  >
                    <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
                    <circle cx="12" cy="12" r="3" />
                  </svg>
                  <!-- Lucide Eye-Off (密碼隱藏時顯示) -->
                  <svg
                    v-else
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    class="lucide-icon lucide-eye-off"
                  >
                    <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24" />
                    <path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68" />
                    <path d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61" />
                    <line x1="2" x2="22" y1="2" y2="22" />
                  </svg>
                </button>
              </div>
              <span v-if="formErrors.currentPassword" class="error-msg">{{ formErrors.currentPassword }}</span>
            </div>
          </div>

          <!-- 2. 新密碼 -->
          <div class="form-row">
            <label class="row-label" for="new-password">新密碼</label>
            <div class="row-content">
              <div class="password-input-wrapper">
                <input
                  id="new-password"
                  v-model="form.newPassword"
                  :type="showNewPassword ? 'text' : 'password'"
                  class="form-input"
                  :class="{ 'has-error': formErrors.newPassword }"
                  placeholder="請輸入新密碼 (至少 6 個字元)"
                  autocomplete="new-password"
                  @input="handleNewPasswordInput"
                  @blur="validateNewPassword"
                />
                <button
                  type="button"
                  class="toggle-pwd-btn"
                  tabindex="-1"
                  :title="showNewPassword ? '隱藏密碼' : '顯示密碼'"
                  @click="showNewPassword = !showNewPassword"
                >
                  <!-- Lucide Eye -->
                  <svg
                    v-if="showNewPassword"
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    class="lucide-icon lucide-eye"
                  >
                    <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
                    <circle cx="12" cy="12" r="3" />
                  </svg>
                  <!-- Lucide Eye-Off -->
                  <svg
                    v-else
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    class="lucide-icon lucide-eye-off"
                  >
                    <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24" />
                    <path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68" />
                    <path d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61" />
                    <line x1="2" x2="22" y1="2" y2="22" />
                  </svg>
                </button>
              </div>

              <!-- 密碼強度指示器 -->
              <div v-if="form.newPassword" class="strength-meter-container">
                <div class="strength-bar-track">
                  <div
                    class="strength-bar-fill"
                    :class="strengthClass"
                    :style="{ width: strengthWidth }"
                  ></div>
                </div>
                <span class="strength-label" :class="strengthClass">
                  強度：{{ strengthText }}
                </span>
              </div>

              <span v-if="formErrors.newPassword" class="error-msg">{{ formErrors.newPassword }}</span>
            </div>
          </div>

          <!-- 3. 確認新密碼 -->
          <div class="form-row">
            <label class="row-label" for="confirm-password">確認新密碼</label>
            <div class="row-content">
              <div class="password-input-wrapper">
                <input
                  id="confirm-password"
                  v-model="form.confirmPassword"
                  :type="showConfirmPassword ? 'text' : 'password'"
                  class="form-input"
                  :class="{ 'has-error': formErrors.confirmPassword }"
                  placeholder="請再次輸入新密碼"
                  autocomplete="new-password"
                  @input="handleConfirmPasswordInput"
                  @blur="validateConfirmPassword"
                />
                <button
                  type="button"
                  class="toggle-pwd-btn"
                  tabindex="-1"
                  :title="showConfirmPassword ? '隱藏密碼' : '顯示密碼'"
                  @click="showConfirmPassword = !showConfirmPassword"
                >
                  <!-- Lucide Eye -->
                  <svg
                    v-if="showConfirmPassword"
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    class="lucide-icon lucide-eye"
                  >
                    <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
                    <circle cx="12" cy="12" r="3" />
                  </svg>
                  <!-- Lucide Eye-Off -->
                  <svg
                    v-else
                    xmlns="http://www.w3.org/2000/svg"
                    width="18"
                    height="18"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    stroke-width="2"
                    stroke-linecap="round"
                    stroke-linejoin="round"
                    class="lucide-icon lucide-eye-off"
                  >
                    <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24" />
                    <path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68" />
                    <path d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61" />
                    <line x1="2" x2="22" y1="2" y2="22" />
                  </svg>
                </button>
              </div>
              <span v-if="formErrors.confirmPassword" class="error-msg">{{ formErrors.confirmPassword }}</span>
            </div>
          </div>

          <!-- 4. 操作按鈕列 -->
          <div class="form-row submit-row">
            <div class="row-label"></div>
            <div class="row-content buttons-group">
              <button type="submit" class="btn-submit-save" :disabled="submitting">
                <span v-if="submitting" class="btn-spinner"></span>
                <span>{{ submitting ? '處理中...' : '確認修改密碼' }}</span>
              </button>

              <button
                type="button"
                class="btn-reset-form"
                :disabled="submitting"
                @click="resetForm"
              >
                清除重填
              </button>
            </div>
          </div>
        </div>

        <!-- 右側：會員頭像與安全建議資訊 -->
        <div class="security-sidebar-section">
          <div class="avatar-box">
            <div class="avatar-large-circle">
              {{ userInitial }}
            </div>
          </div>
          <div class="sidebar-user-name">{{ displayName }}</div>
          <div class="sidebar-tier-tag">星澄貴賓會員</div>

          <div class="security-tips-card">
            <div class="tips-title">
              <span class="tips-icon">🛡️</span>
              <span>密碼設定建議</span>
            </div>
            <ul class="tips-list">
              <li>密碼長度需至少 <strong>6 個字元</strong>（建議 8 字元以上）。</li>
              <li>建議包含<strong>大小寫英文、數字或特殊符號</strong>。</li>
              <li>避免使用與<strong>個人姓名、生日或電話</strong>重複的密碼。</li>
              <li>切勿與他人共用帳號或在公共電腦儲存密碼。</li>
            </ul>
          </div>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useToastStore } from '@/stores/toast';
import { memberApi } from '@/api/memberApi';

const authStore = useAuthStore();
const toastStore = useToastStore();

// =========================================
// 狀態管理
// =========================================
const submitting = ref(false);

const showCurrentPassword = ref(false);
const showNewPassword = ref(false);
const showConfirmPassword = ref(false);

const form = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});

const formErrors = reactive({
  currentPassword: '',
  newPassword: '',
  confirmPassword: '',
});

// =========================================
// 計算屬性
// =========================================
const displayName = computed(() => {
  return authStore.name || '星澄貴賓';
});

const userInitial = computed(() => {
  const currentName = authStore.name || '';
  if (currentName && currentName.trim().length > 0) {
    return currentName.trim().charAt(0);
  }
  return '客';
});

// 密碼強度計算 (0: 無, 1: 弱, 2: 中, 3: 強, 4: 極強)
const passwordStrength = computed(() => {
  const pwd = form.newPassword;
  if (!pwd) return 0;

  let score = 0;
  if (pwd.length >= 6) score += 1;
  if (pwd.length >= 8) score += 1;
  if (/[0-9]/.test(pwd) && /[a-zA-Z]/.test(pwd)) score += 1;
  if (/[^a-zA-Z0-9]/.test(pwd)) score += 1;

  return score;
});

const strengthWidth = computed(() => {
  switch (passwordStrength.value) {
    case 1:
      return '25%';
    case 2:
      return '50%';
    case 3:
      return '75%';
    case 4:
      return '100%';
    default:
      return '0%';
  }
});

const strengthText = computed(() => {
  switch (passwordStrength.value) {
    case 1:
      return '弱 (建議加入英文或數字)';
    case 2:
      return '中等 (建議加入符號或延長)';
    case 3:
      return '良好';
    case 4:
      return '極強';
    default:
      return '';
  }
});

const strengthClass = computed(() => {
  switch (passwordStrength.value) {
    case 1:
      return 'strength-weak';
    case 2:
      return 'strength-medium';
    case 3:
      return 'strength-good';
    case 4:
      return 'strength-strong';
    default:
      return '';
  }
});

// =========================================
// 表單驗證與輸入監聽
// =========================================
function handleNewPasswordInput() {
  if (formErrors.newPassword) {
    validateNewPassword();
  }
  if (form.confirmPassword) {
    validateConfirmPassword();
  }
}

function handleConfirmPasswordInput() {
  if (formErrors.confirmPassword) {
    validateConfirmPassword();
  }
}

function validateCurrentPassword() {
  if (!form.currentPassword) {
    formErrors.currentPassword = '請輸入目前的登入密碼';
    return false;
  }
  formErrors.currentPassword = '';
  return true;
}

function validateNewPassword() {
  if (!form.newPassword) {
    formErrors.newPassword = '請輸入新密碼';
    return false;
  }
  if (form.newPassword.length < 6) {
    formErrors.newPassword = '新密碼長度至少需為 6 個字元';
    return false;
  }
  if (form.currentPassword && form.newPassword === form.currentPassword) {
    formErrors.newPassword = '新密碼不可與目前密碼相同';
    return false;
  }
  formErrors.newPassword = '';
  return true;
}

function validateConfirmPassword() {
  if (!form.confirmPassword) {
    formErrors.confirmPassword = '請再次輸入新密碼以供確認';
    return false;
  }
  if (form.confirmPassword !== form.newPassword) {
    formErrors.confirmPassword = '兩次輸入的新密碼不一致';
    return false;
  }
  formErrors.confirmPassword = '';
  return true;
}

function validateAll() {
  const isCurrValid = validateCurrentPassword();
  const isNewValid = validateNewPassword();
  const isConfirmValid = validateConfirmPassword();
  return isCurrValid && isNewValid && isConfirmValid;
}

function resetForm() {
  form.currentPassword = '';
  form.newPassword = '';
  form.confirmPassword = '';
  formErrors.currentPassword = '';
  formErrors.newPassword = '';
  formErrors.confirmPassword = '';
}

// =========================================
// 送出表單
// =========================================
async function handleSubmit() {
  if (!validateAll()) {
    toastStore.showToast('表單內容有誤，請檢查後重新提交', 'error');
    return;
  }

  submitting.value = true;

  try {
    const res = await memberApi.changeMyPassword({
      currentPassword: form.currentPassword,
      newPassword: form.newPassword,
      confirmPassword: form.confirmPassword,
    });

    const msg = typeof res === "string" ? res : (res && res.message);
    toastStore.showToast(msg || '密碼修改成功！', 'success');
    resetForm();
  } catch (err) {
    console.error('修改密碼錯誤：', err);
    toastStore.showToast(err.message || '網路連線異常，請稍後再試', 'error');
  } finally {
    submitting.value = false;
  }
}
</script>

<style scoped>
.member-password-view {
  width: 100%;
}

.password-card {
  background: #ffffff;
  border-radius: 14px;
  padding: 32px 36px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.04);
  border: 1px solid #eee7dd;
}

/* 頂部標題 */
.card-header {
  padding-bottom: 18px;
  margin-bottom: 28px;
  border-bottom: 1px solid #f0e9df;
}

.header-title h2 {
  font-size: 22px;
  font-weight: 700;
  color: #4a3b2a;
  margin: 0 0 4px 0;
}

.header-title p {
  font-size: 13px;
  color: #8c7d6e;
  margin: 0;
}

/* 表單佈局：左表單 + 右資訊 */
.password-form-layout {
  display: flex;
  gap: 48px;
  align-items: flex-start;
}

.form-fields-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 22px;
}

/* 每一列表單行 */
.form-row {
  display: flex;
  align-items: flex-start;
  min-height: 40px;
}

.row-label {
  width: 130px;
  font-size: 14px;
  font-weight: 600;
  color: #6a5744;
  text-align: right;
  padding-right: 28px;
  padding-top: 10px;
  flex-shrink: 0;
}

.row-content {
  flex: 1;
  max-width: 460px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

/* 密碼輸入框組合（含眼睛切換按鈕） */
.password-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.form-input {
  width: 100%;
  padding: 10px 42px 10px 14px;
  border: 1px solid #dcd3c5;
  border-radius: 6px;
  font-size: 14px;
  color: #333333;
  background-color: #ffffff;
  transition: all 0.2s ease;
}

.form-input:focus {
  outline: none;
  border-color: #b58a46;
  box-shadow: 0 0 0 3px rgba(181, 138, 70, 0.15);
}

.form-input.has-error {
  border-color: #d32f2f;
  background-color: #fff8f8;
}

.toggle-pwd-btn {
  position: absolute;
  right: 10px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8c7d6e;
  transition: color 0.2s, opacity 0.2s;
  user-select: none;
}

.toggle-pwd-btn:hover {
  color: #4a3b2a;
}

.lucide-icon {
  display: block;
}

.error-msg {
  font-size: 12px;
  color: #d32f2f;
  margin-top: 2px;
  font-weight: 500;
}

/* 密碼強度指示器 */
.strength-meter-container {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-top: 4px;
}

.strength-bar-track {
  width: 100%;
  height: 5px;
  background-color: #ede8df;
  border-radius: 4px;
  overflow: hidden;
}

.strength-bar-fill {
  height: 100%;
  transition: all 0.3s ease;
  border-radius: 4px;
}

.strength-weak {
  background-color: #e53935;
  color: #e53935;
}

.strength-medium {
  background-color: #fb8c00;
  color: #fb8c00;
}

.strength-good {
  background-color: #43a047;
  color: #43a047;
}

.strength-strong {
  background-color: #2e7d32;
  color: #2e7d32;
}

.strength-label {
  font-size: 12px;
  font-weight: 600;
}

/* 按鈕區域 */
.submit-row {
  margin-top: 10px;
}

.buttons-group {
  flex-direction: row;
  align-items: center;
  gap: 14px;
}

.btn-submit-save {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #b58a46;
  color: #ffffff;
  border: none;
  padding: 10px 32px;
  border-radius: 6px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 10px rgba(181, 138, 70, 0.25);
  min-width: 150px;
}

.btn-submit-save:hover:not(:disabled) {
  background: #9d7535;
  transform: translateY(-1px);
  box-shadow: 0 6px 14px rgba(181, 138, 70, 0.35);
}

.btn-submit-save:disabled {
  opacity: 0.65;
  cursor: not-allowed;
}

.btn-reset-form {
  background: transparent;
  color: #7d6e5d;
  border: 1px solid #dcd3c5;
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-reset-form:hover:not(:disabled) {
  background: #f7f3ed;
  color: #4a3b2a;
}

.btn-spinner {
  width: 15px;
  height: 15px;
  border: 2px solid rgba(255, 255, 255, 0.4);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 右側安全提示專區 */
.security-sidebar-section {
  width: 240px;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-left: 36px;
  border-left: 1px solid #f0e9df;
  text-align: center;
  flex-shrink: 0;
}

.avatar-box {
  margin-bottom: 14px;
}

.avatar-large-circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #b58a46 0%, #8f692f 100%);
  color: #ffffff;
  font-size: 30px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 16px rgba(181, 138, 70, 0.3);
  border: 3px solid #ffffff;
}

.sidebar-user-name {
  font-size: 16px;
  font-weight: 700;
  color: #4a3b2a;
  margin-bottom: 6px;
  word-break: break-word;
}

.sidebar-tier-tag {
  font-size: 12px;
  font-weight: 700;
  color: #8f692f;
  background: #fdf5e6;
  border: 1px solid #f1ddbc;
  padding: 3px 12px;
  border-radius: 20px;
  margin-bottom: 18px;
}

.security-tips-card {
  width: 100%;
  background: #faf7f2;
  border: 1px solid #eee5d8;
  border-radius: 10px;
  padding: 16px;
  text-align: left;
}

.tips-title {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 700;
  color: #5d4a36;
  margin-bottom: 10px;
}

.tips-icon {
  font-size: 15px;
}

.tips-list {
  margin: 0;
  padding-left: 18px;
  font-size: 12px;
  color: #7a6a57;
  line-height: 1.6;
}

.tips-list li {
  margin-bottom: 6px;
}

.tips-list li:last-child {
  margin-bottom: 0;
}

/* =========================================
   響應式設計 (RWD)
   ========================================= */
@media (max-width: 860px) {
  .password-card {
    padding: 24px 20px;
  }

  .password-form-layout {
    flex-direction: column-reverse;
    gap: 28px;
  }

  .security-sidebar-section {
    width: 100%;
    border-left: none;
    border-bottom: 1px solid #f0e9df;
    padding-left: 0;
    padding-bottom: 20px;
  }

  .form-row {
    flex-direction: column;
    align-items: flex-start;
    gap: 6px;
  }

  .row-label {
    width: 100%;
    text-align: left;
    padding-right: 0;
    padding-top: 0;
  }

  .row-content {
    width: 100%;
    max-width: 100%;
  }

  .buttons-group {
    flex-direction: column;
    width: 100%;
  }

  .btn-submit-save,
  .btn-reset-form {
    width: 100%;
  }
}
</style>
