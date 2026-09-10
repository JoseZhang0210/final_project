<template>
  <div class="member-profile-view">
    <div class="profile-card">
      <!-- 頂部標題列 -->
      <div class="card-header">
        <div class="header-title">
          <h2>我的檔案</h2>
          <p>管理並維護您的會員個人檔案與通訊資訊</p>
        </div>
      </div>

      <!-- 載入中狀態 -->
      <div v-if="loading" class="loading-state">
        <div class="spinner"></div>
        <p>正在載入會員個人資料...</p>
      </div>

      <!-- 個人檔案表單主體 (直接編輯，無需彈窗或打碼) -->
      <form v-else class="profile-form-layout" @submit.prevent="saveProfile">
        <!-- 左側：各欄位輸入區 -->
        <div class="form-fields-container">
          <!-- 1. 使用者帳號 (唯讀) -->
          <div class="form-row">
            <div class="row-label">使用者帳號</div>
            <div class="row-content">
              <div class="username-display">
                <span class="username-text">{{ form.username || 'guest' }}</span>
                <span class="readonly-badge">系統帳號不可變更</span>
              </div>
            </div>
          </div>

          <!-- 2. 真實姓名 -->
          <div class="form-row">
            <label class="row-label required" for="profile-name">姓名</label>
            <div class="row-content">
              <input
                id="profile-name"
                v-model="form.name"
                type="text"
                class="form-input"
                :class="{ 'has-error': formErrors.name }"
                placeholder="請輸入您的真實姓名"
                autocomplete="name"
              />
              <span v-if="formErrors.name" class="error-msg">{{ formErrors.name }}</span>
            </div>
          </div>

          <!-- 3. 電子信箱 -->
          <div class="form-row">
            <label class="row-label" for="profile-email">Email</label>
            <div class="row-content">
              <input
                id="profile-email"
                v-model="form.email"
                type="email"
                class="form-input"
                :class="{ 'has-error': formErrors.email }"
                placeholder="例如：example@hotel.com"
                autocomplete="email"
              />
              <span v-if="formErrors.email" class="error-msg">{{ formErrors.email }}</span>
            </div>
          </div>

          <!-- 4. 手機號碼 -->
          <div class="form-row">
            <label class="row-label" for="profile-phone">手機號碼</label>
            <div class="row-content">
              <input
                id="profile-phone"
                v-model="form.phone"
                type="tel"
                class="form-input"
                :class="{ 'has-error': formErrors.phone }"
                placeholder="例如：0912345678"
                autocomplete="tel"
              />
              <span v-if="formErrors.phone" class="error-msg">{{ formErrors.phone }}</span>
            </div>
          </div>

          <!-- 5. 性別 (單選按鈕) -->
          <div class="form-row">
            <div class="row-label">性別</div>
            <div class="row-content">
              <div class="gender-radio-group">
                <label class="radio-item">
                  <input v-model="form.gender" type="radio" value="男" />
                  <span class="radio-label">男性</span>
                </label>
                <label class="radio-item">
                  <input v-model="form.gender" type="radio" value="女" />
                  <span class="radio-label">女性</span>
                </label>
                <label class="radio-item">
                  <input v-model="form.gender" type="radio" value="其他" />
                  <span class="radio-label">其他</span>
                </label>
              </div>
            </div>
          </div>

          <!-- 6. 生日 -->
          <div class="form-row">
            <label class="row-label" for="profile-birthday">生日</label>
            <div class="row-content">
              <input
                id="profile-birthday"
                v-model="form.birthday"
                type="date"
                class="form-input date-input"
              />
            </div>
          </div>

          <!-- 7. 通訊地址 -->
          <div class="form-row address-row">
            <div class="row-label">通訊地址</div>
            <div class="row-content">
              <!-- 郵遞區號 / 縣市 / 鄉鎮市區 -->
              <div class="address-inputs-grid">
                <input
                  v-model="form.zipcode"
                  type="text"
                  class="form-input"
                  placeholder="郵遞區號 (例: 100)"
                />
                <input
                  v-model="form.city"
                  type="text"
                  class="form-input"
                  placeholder="縣市 (例: 台北市)"
                />
                <input
                  v-model="form.district"
                  type="text"
                  class="form-input"
                  placeholder="鄉鎮市區 (例: 中正區)"
                />
              </div>

              <!-- 詳細街道門牌 -->
              <div class="address-detail-input">
                <input
                  v-model="form.address"
                  type="text"
                  class="form-input"
                  placeholder="詳細門牌地址 (街道、巷弄、門牌、樓層等)"
                />
              </div>
            </div>
          </div>

          <!-- 8. 儲存按鈕 -->
          <div class="form-row submit-row">
            <div class="row-label"></div>
            <div class="row-content">
              <button type="submit" class="btn-submit-save" :disabled="saving">
                <span v-if="saving" class="btn-spinner"></span>
                <span>{{ saving ? '儲存中...' : '儲存' }}</span>
              </button>
            </div>
          </div>
        </div>

        <!-- 右側：會員頭像與等級卡 (參考設計) -->
        <div class="avatar-sidebar-section">
          <div class="avatar-box">
            <div class="avatar-large-circle">
              {{ userInitial }}
            </div>
          </div>
          <div class="sidebar-user-name">{{ displayName }}</div>
          <div class="sidebar-tier-tag">星澄貴賓會員</div>
          <p class="sidebar-hint-text">
            星澄飯店會員專屬資料中心<br />
            隨時更新以享有最即時的住宿禮遇
          </p>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useToastStore } from '@/stores/toast';
import { memberApi } from '@/api/memberApi';

const authStore = useAuthStore();
const toastStore = useToastStore();

// =========================================
// 狀態管理
// =========================================
const loading = ref(false);
const saving = ref(false);

const form = reactive({
  memberId: null,
  accountId: null,
  username: '',
  name: '',
  email: '',
  phone: '',
  gender: '男',
  birthday: '',
  zipcode: '',
  city: '',
  district: '',
  address: '',
});

const formErrors = reactive({
  name: '',
  email: '',
  phone: '',
});

// =========================================
// 計算屬性
// =========================================
// 顯示名稱
const displayName = computed(() => {
  return form.name || authStore.name || '星澄貴賓';
});

// 頭像首字縮寫
const userInitial = computed(() => {
  const currentName = form.name || authStore.name || '';
  if (currentName && currentName.trim().length > 0) {
    return currentName.trim().charAt(0);
  }
  return '客';
});

// =========================================
// =========================================
// API 資料載入
// =========================================
async function fetchProfile() {
  loading.value = true;
  try {
    const data = await memberApi.getMyProfile();
    if (data) {
      form.memberId = data.memberId ?? null;
      form.accountId = data.accountId ?? null;
      form.username = data.username || '';
      form.name = data.name || '';
      form.email = data.email || '';
      form.phone = data.phone || '';
      form.gender = data.gender || '男';
      form.birthday = data.birthday || '';
      form.zipcode = data.zipcode || '';
      form.city = data.city || '';
      form.district = data.district || '';
      form.address = data.address || '';

      if (data.name) {
        authStore.updateName(data.name);
      }
    }
  } catch (err) {
    console.error('取得個人資料錯誤：', err);
    toastStore.showToast(err.message || '網路連線異常，請稍後再試', 'error');
  } finally {
    loading.value = false;
  }
}

// =========================================
// 表單驗證
// =========================================
function validateForm() {
  let valid = true;
  formErrors.name = '';
  formErrors.email = '';
  formErrors.phone = '';

  const nameVal = form.name.trim();
  if (!nameVal) {
    formErrors.name = '姓名為必填項目';
    valid = false;
  }

  const emailVal = form.email.trim();
  if (emailVal) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(emailVal)) {
      formErrors.email = '請輸入正確的電子信箱格式';
      valid = false;
    }
  }

  const phoneVal = form.phone.trim();
  if (phoneVal) {
    const phoneRegex = /^09\d{8}$/;
    if (!phoneRegex.test(phoneVal)) {
      formErrors.phone = '電話請填寫 09 開頭之 10 碼手機號碼';
      valid = false;
    }
  }

  return valid;
}

// =========================================
// 儲存修改個人資料
// =========================================
async function saveProfile() {
  if (!validateForm()) {
    toastStore.showToast('表單內容有誤，請檢查後重新提交', 'error');
    return;
  }

  saving.value = true;
  const payload = {
    name: form.name.trim(),
    email: form.email.trim(),
    phone: form.phone.trim(),
    gender: form.gender,
    birthday: form.birthday || null,
    zipcode: form.zipcode.trim(),
    city: form.city.trim(),
    district: form.district.trim(),
    address: form.address.trim(),
  };

  try {
    const res = await fetch('/api/members/me', {
      method: 'PUT',
      headers: getAuthHeaders(),
      body: JSON.stringify(payload),
    });

    if (res.status === 401 || res.status === 403) {
      toastStore.showToast('登入狀態已過期，請重新登入', 'error');
      return;
    }

    if (!res.ok) {
      const errData = await res.json().catch(() => ({}));
      toastStore.showToast(errData.message || '修改資料失敗，請稍後再試', 'error');
      return;
    }

    const updated = await res.json();

    // 更新表單數據
    form.name = updated.name || payload.name;
    form.email = updated.email || payload.email;
    form.phone = updated.phone || payload.phone;
    form.gender = updated.gender || payload.gender;
    form.birthday = updated.birthday || payload.birthday;
    form.zipcode = updated.zipcode || payload.zipcode;
    form.city = updated.city || payload.city;
    form.district = updated.district || payload.district;
    form.address = updated.address || payload.address;

    // 同步 Pinia store 與 localStorage 使側邊欄即時更新
    if (payload.name) {
      authStore.updateName(payload.name);
    }

    toastStore.showToast('個人資料儲存成功！', 'success');
  } catch (err) {
    console.error('儲存個人資料錯誤：', err);
    toastStore.showToast('網路連線異常，請稍後再試', 'error');
  } finally {
    saving.value = false;
  }
}

onMounted(() => {
  fetchProfile();
});
</script>

<style scoped>
.member-profile-view {
  width: 100%;
}

.profile-card {
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

/* 載入中狀態 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 0;
  color: #887864;
  gap: 14px;
}

.spinner {
  width: 34px;
  height: 34px;
  border: 3px solid #eee7dd;
  border-top-color: #b58a46;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 表單佈局：左表單 + 右頭像 */
.profile-form-layout {
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

/* 每一列表單行 (Label 左邊，Content 右邊) */
.form-row {
  display: flex;
  align-items: center;
  min-height: 40px;
}

.form-row.address-row {
  align-items: flex-start;
}

.row-label {
  width: 130px;
  font-size: 14px;
  font-weight: 600;
  color: #6a5744;
  text-align: right;
  padding-right: 28px;
  flex-shrink: 0;
}

.row-label.required::after {
  content: ' *';
  color: #c62828;
}

.row-content {
  flex: 1;
  max-width: 460px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

/* 唯讀帳號顯示 */
.username-display {
  display: flex;
  align-items: center;
  gap: 12px;
}

.username-text {
  font-size: 15px;
  font-weight: 700;
  color: #4a3b2a;
}

.readonly-badge {
  font-size: 11px;
  color: #8c7d6e;
  background: #f7f3eb;
  padding: 2px 8px;
  border-radius: 4px;
  border: 1px solid #ebdccb;
}

/* 標準輸入框 */
.form-input {
  width: 100%;
  padding: 10px 14px;
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

.date-input {
  cursor: pointer;
}

.error-msg {
  font-size: 12px;
  color: #d32f2f;
  margin-top: 2px;
  font-weight: 500;
}

/* 性別單選群組 */
.gender-radio-group {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 6px 0;
}

.radio-item {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #4a3b2a;
  cursor: pointer;
  user-select: none;
}

.radio-item input[type='radio'] {
  accent-color: #b58a46;
  width: 17px;
  height: 17px;
  cursor: pointer;
  margin: 0;
}

.radio-label {
  font-weight: 500;
}

/* 地址輸入組合 */
.address-inputs-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 10px;
  margin-bottom: 10px;
}

.address-detail-input {
  width: 100%;
}

/* 儲存按鈕行 */
.submit-row {
  margin-top: 10px;
}

.btn-submit-save {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  background: #b58a46;
  color: #ffffff;
  border: none;
  padding: 10px 36px;
  border-radius: 6px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.2s ease;
  box-shadow: 0 4px 10px rgba(181, 138, 70, 0.25);
  min-width: 120px;
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

.btn-spinner {
  width: 15px;
  height: 15px;
  border: 2px solid rgba(255, 255, 255, 0.4);
  border-top-color: #ffffff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

/* 右側頭像專區 */
.avatar-sidebar-section {
  width: 200px;
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
  width: 88px;
  height: 88px;
  border-radius: 50%;
  background: linear-gradient(135deg, #b58a46 0%, #8f692f 100%);
  color: #ffffff;
  font-size: 34px;
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
  margin-bottom: 12px;
}

.sidebar-hint-text {
  font-size: 12px;
  color: #a39587;
  line-height: 1.5;
  margin: 0;
}

/* =========================================
   響應式設計 (RWD)
   ========================================= */
@media (max-width: 860px) {
  .profile-card {
    padding: 24px 20px;
  }

  .profile-form-layout {
    flex-direction: column-reverse;
    gap: 28px;
  }

  .avatar-sidebar-section {
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
  }

  .row-content {
    width: 100%;
    max-width: 100%;
  }

  .address-inputs-grid {
    grid-template-columns: 1fr;
  }

  .btn-submit-save {
    width: 100%;
  }
}
</style>
