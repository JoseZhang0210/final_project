<template>
  <div class="member-center-view">
    <div class="member-center-card">
      <!-- 頂部標題列 -->
      <div class="card-header">
        <div class="header-title">
          <h2>修改密碼</h2>
          <p>定期變更登入密碼以確保您的星澄會員帳號安全</p>
        </div>
      </div>

      <!-- 表單主體佈局 -->
      <form class="member-form-layout" @submit.prevent="handleSubmit">
        <!-- 左側：輸入表單區 -->
        <div class="form-fields-container">
          <!-- 1. 目前密碼 -->
          <div class="form-row">
            <label class="row-label" for="current-password">目前密碼</label>
            <div class="row-content">
              <PasswordInput
                id="current-password"
                v-model="form.currentPassword"
                placeholder="請輸入您目前的登入密碼"
                autocomplete="current-password"
                :has-error="!!formErrors.currentPassword"
                @blur="validateCurrentPassword"
              />
              <span v-if="formErrors.currentPassword" class="error-msg">{{ formErrors.currentPassword }}</span>
            </div>
          </div>

          <!-- 2. 新密碼 -->
          <div class="form-row align-top">
            <label class="row-label" for="new-password">新密碼</label>
            <div class="row-content">
              <PasswordInput
                id="new-password"
                v-model="form.newPassword"
                placeholder="請輸入新密碼 (至少 6 個字元)"
                autocomplete="new-password"
                :has-error="!!formErrors.newPassword"
                @input="handleNewPasswordInput"
                @blur="validateNewPassword"
              />

              <!-- 密碼強度指示器 -->
              <div v-if="form.newPassword" class="strength-meter-container">
                <div class="strength-bar-track">
                  <div
                    class="strength-bar-fill"
                    :class="strengthDetails.className"
                    :style="{ width: strengthDetails.width }"
                  ></div>
                </div>
                <span class="strength-label" :class="strengthDetails.className">
                  強度：{{ strengthDetails.text }}
                </span>
              </div>

              <span v-if="formErrors.newPassword" class="error-msg">{{ formErrors.newPassword }}</span>
            </div>
          </div>

          <!-- 3. 確認新密碼 -->
          <div class="form-row">
            <label class="row-label" for="confirm-password">確認新密碼</label>
            <div class="row-content">
              <PasswordInput
                id="confirm-password"
                v-model="form.confirmPassword"
                placeholder="請再次輸入新密碼"
                autocomplete="new-password"
                :has-error="!!formErrors.confirmPassword"
                @input="handleConfirmPasswordInput"
                @blur="validateConfirmPassword"
              />
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
        <MemberSidebarCard :name="authStore.name">
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
        </MemberSidebarCard>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from "vue";
import { useAuthStore } from "@/stores/auth";
import { useToastStore } from "@/stores/toast";
import {
  validatePasswordFormat,
  validateConfirmPasswordFormat,
  calculatePasswordStrength,
  getStrengthDetails,
} from "@/composables/usePasswordValidation";
import MemberSidebarCard from "@/components/member/MemberSidebarCard.vue";
import PasswordInput from "@/components/common/PasswordInput.vue";
import "@/assets/member-center.css";

const authStore = useAuthStore();
const toastStore = useToastStore();

const submitting = ref(false);

const form = reactive({
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const formErrors = reactive({
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
});

// 密碼強度
const passwordStrength = computed(() => {
  return calculatePasswordStrength(form.newPassword);
});

const strengthDetails = computed(() => {
  return getStrengthDetails(passwordStrength.value);
});

function handleNewPasswordInput() {
  if (formErrors.newPassword) validateNewPassword();
  if (form.confirmPassword) validateConfirmPassword();
}

function handleConfirmPasswordInput() {
  if (formErrors.confirmPassword) validateConfirmPassword();
}

function validateCurrentPassword() {
  if (!form.currentPassword) {
    formErrors.currentPassword = "請輸入目前的登入密碼";
    return false;
  }
  formErrors.currentPassword = "";
  return true;
}

function validateNewPassword() {
  const err = validatePasswordFormat(form.newPassword);
  if (err) {
    formErrors.newPassword = err;
    return false;
  }
  if (form.currentPassword && form.newPassword === form.currentPassword) {
    formErrors.newPassword = "新密碼不可與目前密碼相同";
    return false;
  }
  formErrors.newPassword = "";
  return true;
}

function validateConfirmPassword() {
  const err = validateConfirmPasswordFormat(form.newPassword, form.confirmPassword);
  if (err) {
    formErrors.confirmPassword = err;
    return false;
  }
  formErrors.confirmPassword = "";
  return true;
}

function validateAll() {
  const isCurrValid = validateCurrentPassword();
  const isNewValid = validateNewPassword();
  const isConfirmValid = validateConfirmPassword();
  return isCurrValid && isNewValid && isConfirmValid;
}

function resetForm() {
  form.currentPassword = "";
  form.newPassword = "";
  form.confirmPassword = "";
  formErrors.currentPassword = "";
  formErrors.newPassword = "";
  formErrors.confirmPassword = "";
}

async function handleSubmit() {
  if (!validateAll()) {
    toastStore.showToast("表單內容有誤，請檢查後重新提交", "error");
    return;
  }

  submitting.value = true;
  const token = localStorage.getItem("token");

  try {
    const res = await fetch("/api/members/me/password", {
      method: "PUT",
      headers: {
        "Content-Type": "application/json",
        Authorization: token ? "Bearer " + token : "",
      },
      body: JSON.stringify({
        currentPassword: form.currentPassword,
        newPassword: form.newPassword,
        confirmPassword: form.confirmPassword,
      }),
    });

    if (res.status === 401 || res.status === 403) {
      toastStore.showToast("登入狀態已過期，請重新登入", "error");
      return;
    }

    const data = await res.json().catch(() => ({}));
    if (!res.ok) {
      toastStore.showToast(data.message || "修改密碼失敗，請稍後再試", "error");
      return;
    }

    toastStore.showToast(data.message || "密碼修改成功！", "success");
    resetForm();
  } catch (err) {
    console.error("修改密碼錯誤：", err);
    toastStore.showToast("網路連線異常，請稍後再試", "error");
  } finally {
    submitting.value = false;
  }
}
</script>
