<template>
  <div class="member-center-view">
    <div class="member-center-card">
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

      <!-- 個人檔案表單主體 -->
      <form v-else class="member-form-layout" @submit.prevent="saveProfile">
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
                <label class="radio-label">
                  <input v-model="form.gender" type="radio" value="男" /> 男
                </label>
                <label class="radio-label">
                  <input v-model="form.gender" type="radio" value="女" /> 女
                </label>
                <label class="radio-label">
                  <input v-model="form.gender" type="radio" value="其他" /> 其他
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
                class="form-input"
                style="cursor: pointer;"
              />
            </div>
          </div>

          <!-- 7. 通訊地址 -->
          <div class="form-row align-top">
            <div class="row-label">通訊地址</div>
            <div class="row-content">
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

              <div>
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

        <!-- 右側：會員頭像與等級卡 -->
        <MemberSidebarCard
          :name="form.name || authStore.name"
          :avatar-url="form.avatarUrl || authStore.avatarUrl"
          :uploading="uploadingAvatar"
          @upload-avatar="handleAvatarUpload"
        >
          <p class="sidebar-hint-text">
            星澄飯店會員專屬資料中心<br />
            點擊大頭貼可隨時更新您的個人頭像
          </p>
        </MemberSidebarCard>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue";
import { useAuthStore } from "@/stores/auth";
import { useToastStore } from "@/stores/toast";
import { getAuthHeaders } from "@/utils/auth";
import MemberSidebarCard from "@/components/member/MemberSidebarCard.vue";
import "@/assets/member-center.css";

const authStore = useAuthStore();
const toastStore = useToastStore();

const loading = ref(false);
const saving = ref(false);
const uploadingAvatar = ref(false);

const form = reactive({
  memberId: null,
  accountId: null,
  username: "",
  name: "",
  email: "",
  phone: "",
  gender: "男",
  birthday: "",
  zipcode: "",
  city: "",
  district: "",
  address: "",
  avatarUrl: "",
});

const formErrors = reactive({
  name: "",
  email: "",
  phone: "",
});

async function fetchProfile() {
  loading.value = true;
  try {
    const res = await fetch("/api/members/me", {
      method: "GET",
      headers: getAuthHeaders(),
    });

    if (res.status === 401 || res.status === 403) {
      toastStore.showToast("登入狀態已過期，請重新登入", "error");
      return;
    }

    if (!res.ok) {
      toastStore.showToast("取得個人資料失敗", "error");
      return;
    }

    const data = await res.json();
    form.memberId = data.memberId ?? null;
    form.accountId = data.accountId ?? null;
    form.username = data.username || "";
    form.name = data.name || "";
    form.email = data.email || "";
    form.phone = data.phone || "";
    form.gender = data.gender || "男";
    form.birthday = data.birthday || "";
    form.zipcode = data.zipcode || "";
    form.city = data.city || "";
    form.district = data.district || "";
    form.address = data.address || "";
    form.avatarUrl = data.avatarUrl || "";

    if (data.name) {
      authStore.updateName(data.name);
    }
    if (data.avatarUrl) {
      authStore.updateAvatarUrl(data.avatarUrl);
    }
  } catch (err) {
    console.error("取得個人資料錯誤：", err);
    toastStore.showToast("網路連線異常，請稍後再試", "error");
  } finally {
    loading.value = false;
  }
}

function validateForm() {
  let valid = true;
  formErrors.name = "";
  formErrors.email = "";
  formErrors.phone = "";

  const nameVal = form.name.trim();
  if (!nameVal) {
    formErrors.name = "姓名為必填項目";
    valid = false;
  }

  const emailVal = form.email.trim();
  if (emailVal) {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(emailVal)) {
      formErrors.email = "請輸入正確的電子信箱格式";
      valid = false;
    }
  }

  const phoneVal = form.phone.trim();
  if (phoneVal) {
    const phoneRegex = /^09\d{8}$/;
    if (!phoneRegex.test(phoneVal)) {
      formErrors.phone = "電話請填寫 09 開頭之 10 碼手機號碼";
      valid = false;
    }
  }

  return valid;
}

async function handleAvatarUpload(file) {
  if (!file) return;

  // 驗證檔案類型與大小 (大小不超過 5MB)
  if (!file.type.startsWith("image/")) {
    toastStore.showToast("請選擇有效的圖片檔案 (JPG, PNG, WEBP 等)", "error");
    return;
  }
  if (file.size > 5 * 1024 * 1024) {
    toastStore.showToast("頭像圖片大小不能超過 5MB", "error");
    return;
  }

  uploadingAvatar.value = true;
  const formData = new FormData();
  formData.append("file", file);

  try {
    const token = localStorage.getItem("token");
    const headers = {};
    if (token) {
      headers.Authorization = "Bearer " + token;
    }

    const res = await fetch("/api/members/me/avatar", {
      method: "POST",
      headers,
      body: formData,
    });

    if (res.status === 401 || res.status === 403) {
      toastStore.showToast("登入狀態已過期，請重新登入", "error");
      return;
    }

    if (!res.ok) {
      const errData = await res.json().catch(() => ({}));
      toastStore.showToast(errData.message || "頭像上傳失敗，請稍後再試", "error");
      return;
    }

    const updated = await res.json();
    if (updated && updated.avatarUrl) {
      form.avatarUrl = updated.avatarUrl;
      authStore.updateAvatarUrl(updated.avatarUrl);
      toastStore.showToast("大頭貼更新成功！", "success");
    }
  } catch (err) {
    console.error("上傳大頭貼錯誤：", err);
    toastStore.showToast("網路連線異常，請稍後再試", "error");
  } finally {
    uploadingAvatar.value = false;
  }
}

async function saveProfile() {
  if (!validateForm()) {
    toastStore.showToast("表單內容有誤，請檢查後重新提交", "error");
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
    const res = await fetch("/api/members/me", {
      method: "PUT",
      headers: getAuthHeaders(),
      body: JSON.stringify(payload),
    });

    if (res.status === 401 || res.status === 403) {
      toastStore.showToast("登入狀態已過期，請重新登入", "error");
      return;
    }

    if (!res.ok) {
      const errData = await res.json().catch(() => ({}));
      toastStore.showToast(errData.message || "修改資料失敗，請稍後再試", "error");
      return;
    }

    const updated = await res.json();
    form.name = updated.name || payload.name;
    form.email = updated.email || payload.email;
    form.phone = updated.phone || payload.phone;
    form.gender = updated.gender || payload.gender;
    form.birthday = updated.birthday || payload.birthday;
    form.zipcode = updated.zipcode || payload.zipcode;
    form.city = updated.city || payload.city;
    form.district = updated.district || payload.district;
    form.address = updated.address || payload.address;

    if (payload.name) {
      authStore.updateName(payload.name);
    }

    toastStore.showToast("個人資料儲存成功！", "success");
  } catch (err) {
    console.error("儲存個人資料錯誤：", err);
    toastStore.showToast("網路連線異常，請稍後再試", "error");
  } finally {
    saving.value = false;
  }
}

onMounted(() => {
  fetchProfile();
});
</script>

<style scoped>
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

.gender-radio-group {
  display: flex;
  align-items: center;
  gap: 24px;
  padding: 6px 0;
}

.radio-label {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #4a3b2a;
  cursor: pointer;
  user-select: none;
}

.radio-label input[type="radio"] {
  accent-color: #b58a46;
  width: 17px;
  height: 17px;
  cursor: pointer;
  margin: 0;
}

.address-inputs-grid {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 10px;
  margin-bottom: 10px;
}

@media (max-width: 860px) {
  .address-inputs-grid {
    grid-template-columns: 1fr;
  }
}
</style>
