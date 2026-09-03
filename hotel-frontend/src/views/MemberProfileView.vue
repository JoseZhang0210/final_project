<template>
  <div class="member-profile-view">
    <div class="profile-card">
      <div class="card-header">
        <div class="header-title">
          <h2>👤 個人資料</h2>
          <span class="status-badge active">帳號狀態：正常</span>
        </div>
        <button class="edit-btn" @click="openEditModal">
          <span class="edit-icon">✏️</span> 修改個人資料
        </button>
      </div>

      <div class="card-body">
        <!-- 載入中狀態 -->
        <div v-if="loading" class="loading-state">
          <div class="spinner"></div>
          <span>資料載入中...</span>
        </div>

        <template v-else>
          <!-- 第一排：基本狀態與身分 -->
          <div class="profile-info-grid">
            <div class="info-group">
              <label>會員姓名</label>
              <div class="info-value highlight">{{ profileData.name || authStore.name || '未設定' }}</div>
            </div>

            <div class="info-group">
              <label>登入狀態</label>
              <div class="info-value text-success">✓ 已驗證登入</div>
            </div>

            <div class="info-group">
              <label>帳號權限</label>
              <div class="info-value">
                <span v-for="auth in authStore.authorities" :key="auth" class="role-tag">
                  {{ auth }}
                </span>
              </div>
            </div>
          </div>

          <!-- 第二排：詳細聯絡與個人資訊 -->
          <div class="section-subtitle">詳細資訊</div>
          <div class="profile-detail-grid">
            <div class="info-group">
              <label>電子信箱</label>
              <div class="info-value">{{ profileData.email || '未設定' }}</div>
            </div>

            <div class="info-group">
              <label>聯絡電話</label>
              <div class="info-value">{{ profileData.phone || '未設定' }}</div>
            </div>

            <div class="info-group">
              <label>性別</label>
              <div class="info-value">{{ profileData.gender || '未設定' }}</div>
            </div>

            <div class="info-group">
              <label>生日</label>
              <div class="info-value">{{ profileData.birthday || '未設定' }}</div>
            </div>

            <div class="info-group full-width">
              <label>通訊地址</label>
              <div class="info-value">{{ fullAddress || '未設定' }}</div>
            </div>
          </div>
        </template>

        <!-- 快捷服務區塊 -->
        <div class="quick-links-section">
          <h3>快捷服務</h3>
          <div class="quick-cards-grid">
            <RouterLink to="/member/orders" class="quick-card">
              <div class="quick-icon">📦</div>
              <div class="quick-text">
                <div class="quick-title">我的訂單</div>
                <div class="quick-desc">查看商城歷史訂單及出貨進度</div>
              </div>
            </RouterLink>

            <RouterLink to="/products" class="quick-card">
              <div class="quick-icon">🛍</div>
              <div class="quick-text">
                <div class="quick-title">飯店商城</div>
                <div class="quick-desc">選購星澄嚴選商品與伴手禮</div>
              </div>
            </RouterLink>

            <RouterLink to="/cart" class="quick-card">
              <div class="quick-icon">🛒</div>
              <div class="quick-text">
                <div class="quick-title">購物車</div>
                <div class="quick-desc">檢視待結帳商品</div>
              </div>
            </RouterLink>

            <RouterLink to="/restaurant-menu" class="quick-card">
              <div class="quick-icon">🍽</div>
              <div class="quick-text">
                <div class="quick-title">餐廳美饌</div>
                <div class="quick-desc">預覽主廚精選菜單與時段</div>
              </div>
            </RouterLink>
          </div>
        </div>
      </div>
    </div>

    <!-- 修改個人資料 Modal (Teleport 到 body，置中且可捲動) -->
    <Teleport to="body">
      <Transition name="modal-fade">
        <div v-if="modalOpen" class="modal-overlay" @click.self="closeEditModal">
          <div class="modal-container" role="dialog" aria-modal="true">
            <div class="modal-header">
              <div class="modal-title">
                <h3>✏️ 修改個人資料</h3>
                <p>請更新您的會員資訊，完成後點擊儲存</p>
              </div>
              <button class="modal-close-btn" @click="closeEditModal">✕</button>
            </div>

            <form class="modal-body" @submit.prevent="saveProfile">
              <div class="form-grid">
                <!-- 姓名 -->
                <div class="form-group full-width">
                  <label class="required">姓名</label>
                  <input
                    v-model="editForm.name"
                    type="text"
                    placeholder="請輸入您的真實姓名"
                    :class="{ 'has-error': formErrors.name }"
                  />
                  <span v-if="formErrors.name" class="error-msg">{{ formErrors.name }}</span>
                </div>

                <!-- 電子信箱 -->
                <div class="form-group">
                  <label>電子信箱</label>
                  <input
                    v-model="editForm.email"
                    type="email"
                    placeholder="example@hotel.com"
                    :class="{ 'has-error': formErrors.email }"
                  />
                  <span v-if="formErrors.email" class="error-msg">{{ formErrors.email }}</span>
                </div>

                <!-- 聯絡電話 -->
                <div class="form-group">
                  <label>聯絡電話</label>
                  <input
                    v-model="editForm.phone"
                    type="tel"
                    placeholder="例如：0912345678"
                    :class="{ 'has-error': formErrors.phone }"
                  />
                  <span v-if="formErrors.phone" class="error-msg">{{ formErrors.phone }}</span>
                </div>

                <!-- 性別 -->
                <div class="form-group">
                  <label>性別</label>
                  <select v-model="editForm.gender">
                    <option value="">未指定</option>
                    <option value="男">男</option>
                    <option value="女">女</option>
                    <option value="其他">其他</option>
                  </select>
                </div>

                <!-- 生日 -->
                <div class="form-group">
                  <label>生日</label>
                  <input v-model="editForm.birthday" type="date" />
                </div>

                <!-- 郵遞區號 -->
                <div class="form-group">
                  <label>郵遞區號</label>
                  <input v-model="editForm.zipcode" type="text" placeholder="例如：100" />
                </div>

                <!-- 縣市 -->
                <div class="form-group">
                  <label>縣市</label>
                  <input v-model="editForm.city" type="text" placeholder="例如：台北市" />
                </div>

                <!-- 鄉鎮市區 -->
                <div class="form-group">
                  <label>鄉鎮市區</label>
                  <input v-model="editForm.district" type="text" placeholder="例如：中正區" />
                </div>

                <!-- 詳細地址 -->
                <div class="form-group full-width">
                  <label>詳細地址</label>
                  <input v-model="editForm.address" type="text" placeholder="街道、門牌、樓層等" />
                </div>
              </div>

              <div class="modal-footer">
                <button type="button" class="btn btn-secondary" :disabled="saving" @click="closeEditModal">
                  取消
                </button>
                <button type="submit" class="btn btn-primary" :disabled="saving">
                  <span v-if="saving" class="btn-spinner"></span>
                  <span>{{ saving ? '儲存中...' : '確認儲存' }}</span>
                </button>
              </div>
            </form>
          </div>
        </div>
      </Transition>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useToastStore } from '@/stores/toast';

const authStore = useAuthStore();
const toastStore = useToastStore();

const loading = ref(false);
const saving = ref(false);
const modalOpen = ref(false);

const profileData = ref({
  memberId: null,
  name: '',
  email: '',
  phone: '',
  gender: '',
  birthday: '',
  zipcode: '',
  city: '',
  district: '',
  address: '',
});

const editForm = reactive({
  name: '',
  email: '',
  phone: '',
  gender: '',
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

// 組合完整地址顯示
const fullAddress = computed(() => {
  const parts = [
    profileData.value.zipcode,
    profileData.value.city,
    profileData.value.district,
    profileData.value.address,
  ].filter((p) => !!p && p.trim().length > 0);

  return parts.length > 0 ? parts.join(' ') : '';
});

// 產生 JWT Header
function getAuthHeaders() {
  const token = localStorage.getItem('token');
  const headers = {
    'Content-Type': 'application/json',
  };
  if (token) {
    headers.Authorization = 'Bearer ' + token;
  }
  return headers;
}

// 載入會員自己的個人資料
async function fetchProfile() {
  loading.value = true;
  try {
    const res = await fetch('/api/members/me', {
      method: 'GET',
      headers: getAuthHeaders(),
    });

    if (res.status === 401 || res.status === 403) {
      toastStore.showToast('登入狀態已過期，請重新登入', 'error');
      return;
    }

    if (!res.ok) {
      toastStore.showToast('取得個人資料失敗', 'error');
      return;
    }

    const data = await res.json();
    profileData.value = {
      memberId: data.memberId,
      name: data.name || '',
      email: data.email || '',
      phone: data.phone || '',
      gender: data.gender || '',
      birthday: data.birthday || '',
      zipcode: data.zipcode || '',
      city: data.city || '',
      district: data.district || '',
      address: data.address || '',
    };

    // 若從後端取得姓名，同步 Pinia store
    if (data.name) {
      authStore.updateName(data.name);
    }
  } catch (err) {
    console.error('取得個人資料錯誤：', err);
    toastStore.showToast('網路連線異常，請稍後再試', 'error');
  } finally {
    loading.value = false;
  }
}

// 打開 Modal 並將現有資料帶入編輯表單
function openEditModal() {
  formErrors.name = '';
  formErrors.email = '';
  formErrors.phone = '';

  editForm.name = profileData.value.name || authStore.name || '';
  editForm.email = profileData.value.email || '';
  editForm.phone = profileData.value.phone || '';
  editForm.gender = profileData.value.gender || '';
  editForm.birthday = profileData.value.birthday || '';
  editForm.zipcode = profileData.value.zipcode || '';
  editForm.city = profileData.value.city || '';
  editForm.district = profileData.value.district || '';
  editForm.address = profileData.value.address || '';

  modalOpen.value = true;
}

// 關閉 Modal
function closeEditModal() {
  if (saving.value) return;
  modalOpen.value = false;
}

// 前端驗證
function validateForm() {
  let valid = true;
  formErrors.name = '';
  formErrors.email = '';
  formErrors.phone = '';

  const nameVal = editForm.name.trim();
  if (!nameVal) {
    formErrors.name = '姓名為必填項目';
    valid = false;
  }

  const emailVal = editForm.email.trim();
  if (emailVal) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(emailVal)) {
      formErrors.email = '請輸入正確的電子信箱格式';
      valid = false;
    }
  }

  const phoneVal = editForm.phone.trim();
  if (phoneVal) {
    const phoneRegex = /^09\d{8}$/;
    if (!phoneRegex.test(phoneVal)) {
      formErrors.phone = '電話請填寫 09 開頭之 10 碼手機號碼';
      valid = false;
    }
  }

  return valid;
}

// 儲存修改資料
async function saveProfile() {
  if (!validateForm()) {
    toastStore.showToast('表單內容有誤，請檢查後重新提交', 'error');
    return;
  }

  saving.value = true;
  const payload = {
    name: editForm.name.trim(),
    email: editForm.email.trim(),
    phone: editForm.phone.trim(),
    gender: editForm.gender,
    birthday: editForm.birthday || null,
    zipcode: editForm.zipcode.trim(),
    city: editForm.city.trim(),
    district: editForm.district.trim(),
    address: editForm.address.trim(),
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

    // 更新本頁資料
    profileData.value = {
      ...profileData.value,
      name: updated.name || payload.name,
      email: updated.email || payload.email,
      phone: updated.phone || payload.phone,
      gender: updated.gender || payload.gender,
      birthday: updated.birthday || payload.birthday,
      zipcode: updated.zipcode || payload.zipcode,
      city: updated.city || payload.city,
      district: updated.district || payload.district,
      address: updated.address || payload.address,
    };

    // 同步 Pinia store 與 localStorage 使側邊欄即時更新
    if (payload.name) {
      authStore.updateName(payload.name);
    }

    modalOpen.value = false;
    toastStore.showToast('個人資料修改成功！', 'success');
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
  background: #fff;
  border-radius: 14px;
  padding: 32px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.05);
  border: 1px solid #eee7dd;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee7dd;
  margin-bottom: 26px;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 16px;
  flex-wrap: wrap;
}

.card-header h2 {
  font-size: 22px;
  color: #4a3b2a;
  margin: 0;
}

.status-badge {
  font-size: 13px;
  font-weight: bold;
  padding: 6px 14px;
  border-radius: 20px;
}

.status-badge.active {
  background-color: #e5f6eb;
  color: #257641;
}

/* 編輯按鈕 */
.edit-btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background-color: #b58a46;
  color: #fff;
  border: none;
  padding: 8px 18px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.25s ease;
  box-shadow: 0 4px 10px rgba(181, 138, 70, 0.25);
}

.edit-btn:hover {
  background-color: #9d7535;
  transform: translateY(-2px);
  box-shadow: 0 6px 14px rgba(181, 138, 70, 0.35);
}

.edit-icon {
  font-size: 16px;
}

/* 載入中狀態 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px 0;
  color: #887864;
  gap: 12px;
}

.spinner {
  width: 32px;
  height: 32px;
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

/* 資料區塊格線 */
.profile-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 18px;
  margin-bottom: 24px;
}

.section-subtitle {
  font-size: 15px;
  font-weight: 700;
  color: #6a5744;
  margin-bottom: 12px;
  padding-left: 4px;
  border-left: 3px solid #b58a46;
}

.profile-detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 18px;
  margin-bottom: 36px;
}

.info-group {
  background: #fdfbf7;
  padding: 16px 20px;
  border-radius: 10px;
  border: 1px solid #f0e9df;
}

.info-group.full-width {
  grid-column: 1 / -1;
}

.info-group label {
  display: block;
  font-size: 13px;
  color: #887864;
  margin-bottom: 6px;
  font-weight: 600;
}

.info-value {
  font-size: 15px;
  color: #333;
  font-weight: 500;
  word-break: break-word;
}

.info-value.highlight {
  color: #b58a46;
  font-size: 18px;
  font-weight: bold;
}

.info-value.text-success {
  color: #257641;
}

.role-tag {
  display: inline-block;
  background: #ede6dc;
  color: #5c4d3d;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 6px;
  margin-right: 6px;
  margin-bottom: 4px;
}

/* 快捷卡片 */
.quick-links-section h3 {
  font-size: 18px;
  color: #4a3b2a;
  margin-bottom: 18px;
}

.quick-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.quick-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  background: #fff;
  border: 1px solid #eee7dd;
  border-radius: 12px;
  text-decoration: none;
  color: inherit;
  transition: all 0.25s ease;
}

.quick-card:hover {
  border-color: #b58a46;
  transform: translateY(-3px);
  box-shadow: 0 8px 18px rgba(181, 138, 70, 0.12);
}

.quick-icon {
  font-size: 28px;
  width: 46px;
  height: 46px;
  background: #faf6ee;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.quick-title {
  font-size: 15px;
  font-weight: bold;
  color: #4a3b2a;
  margin-bottom: 3px;
}

.quick-desc {
  font-size: 12px;
  color: #888;
  line-height: 1.4;
}

/* =========================================
   Modal 樣式（居中大型，可垂直滾動）
   ========================================= */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(30, 24, 18, 0.55);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;
  padding: 20px;
}

.modal-container {
  background: #fff;
  width: 100%;
  max-width: 680px;
  max-height: 90vh;
  border-radius: 16px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  animation: modalPop 0.25s cubic-bezier(0.16, 1, 0.3, 1);
}

@keyframes modalPop {
  from {
    opacity: 0;
    transform: scale(0.94) translateY(10px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 24px 28px 18px;
  border-bottom: 1px solid #eee7dd;
  background: #faf7f2;
}

.modal-title h3 {
  margin: 0 0 4px 0;
  font-size: 20px;
  color: #4a3b2a;
}

.modal-title p {
  margin: 0;
  font-size: 13px;
  color: #887864;
}

.modal-close-btn {
  background: transparent;
  border: none;
  font-size: 20px;
  color: #888;
  cursor: pointer;
  line-height: 1;
  padding: 4px 8px;
  border-radius: 6px;
  transition: all 0.2s;
}

.modal-close-btn:hover {
  color: #333;
  background: #ebdccb;
}

.modal-body {
  padding: 24px 28px;
  overflow-y: auto;
  flex: 1;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}

.form-group {
  display: flex;
  flex-direction: column;
}

.form-group.full-width {
  grid-column: 1 / -1;
}

.form-group label {
  font-size: 13px;
  font-weight: 600;
  color: #554433;
  margin-bottom: 6px;
}

.form-group label.required::after {
  content: ' *';
  color: #c62828;
}

.form-group input,
.form-group select {
  padding: 10px 14px;
  border: 1px solid #dcd3c5;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
  background-color: #fff;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: #b58a46;
  box-shadow: 0 0 0 3px rgba(181, 138, 70, 0.15);
}

.form-group input.has-error {
  border-color: #d32f2f;
  background-color: #fff8f8;
}

.error-msg {
  font-size: 12px;
  color: #d32f2f;
  margin-top: 4px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 24px;
  border-top: 1px solid #eee7dd;
  margin-top: 10px;
}

.btn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 10px 22px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
  border: none;
}

.btn-secondary {
  background-color: #eee7dd;
  color: #5c4d3d;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #e2d8ca;
}

.btn-primary {
  background-color: #b58a46;
  color: #fff;
  box-shadow: 0 4px 10px rgba(181, 138, 70, 0.25);
}

.btn-primary:hover:not(:disabled) {
  background-color: #9d7535;
  box-shadow: 0 6px 14px rgba(181, 138, 70, 0.35);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.4);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

/* Modal 動畫 */
.modal-fade-enter-active,
.modal-fade-leave-active {
  transition: opacity 0.25s ease;
}

.modal-fade-enter-from,
.modal-fade-leave-to {
  opacity: 0;
}

@media (max-width: 640px) {
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
