<template>
  <div class="member-sidebar-section">
    <div class="avatar-box">
      <div
        class="avatar-large-circle"
        :class="{ 'clickable': allowUpload }"
        @click="triggerFileInput"
        title="點擊更換頭像照片"
      >
        <img v-if="effectiveAvatarUrl" :src="effectiveAvatarUrl" alt="Avatar" class="avatar-large-img" />
        <span v-else>{{ userInitial }}</span>

        <div v-if="allowUpload" class="avatar-overlay">
          <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14.5 4h-5L7 7H4a2 2 0 0 0-2 2v9a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2V9a2 2 0 0 0-2-2h-3l-2.5-3z"/>
            <circle cx="12" cy="13" r="3"/>
          </svg>
          <span class="overlay-text">{{ uploading ? '上傳中' : '更換頭像' }}</span>
        </div>
      </div>

      <input
        v-if="allowUpload"
        ref="fileInputRef"
        type="file"
        accept="image/*"
        class="hidden-file-input"
        @change="handleFileChange"
      />

      <button
        v-if="allowUpload"
        type="button"
        class="btn-upload-avatar-trigger"
        :disabled="uploading"
        @click="triggerFileInput"
      >
        <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="inline-icon">
          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
          <polyline points="17 8 12 3 7 8"/>
          <line x1="12" x2="12" y1="3" y2="15"/>
        </svg>
        <span>{{ uploading ? '上傳中...' : '上傳頭像' }}</span>
      </button>
    </div>

    <div class="sidebar-user-name">{{ displayName }}</div>
    <div class="sidebar-tier-tag">{{ tierText }}</div>
    <slot></slot>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useAuthStore } from "@/stores/auth";

const props = defineProps({
  name: {
    type: String,
    default: "",
  },
  avatarUrl: {
    type: String,
    default: "",
  },
  tierText: {
    type: String,
    default: "星澄貴賓會員",
  },
  allowUpload: {
    type: Boolean,
    default: true,
  },
  uploading: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["upload-avatar"]);

const authStore = useAuthStore();
const fileInputRef = ref(null);

const effectiveAvatarUrl = computed(() => {
  return props.avatarUrl || authStore.avatarUrl || "";
});

const displayName = computed(() => {
  return props.name || authStore.name || "星澄貴賓";
});

const userInitial = computed(() => {
  const nameVal = props.name || authStore.name || "";
  if (nameVal && nameVal.trim().length > 0) {
    return nameVal.trim().charAt(0);
  }
  return "客";
});

function triggerFileInput() {
  if (props.allowUpload && fileInputRef.value) {
    fileInputRef.value.click();
  }
}

function handleFileChange(event) {
  const file = event.target.files && event.target.files[0];
  if (file) {
    emit("upload-avatar", file);
  }
  // 寫回空值以確保重複選擇相同檔案時也能觸發 change 事件
  event.target.value = "";
}
</script>

<style scoped>
.avatar-large-circle {
  position: relative;
  overflow: hidden;
  cursor: pointer;
}

.avatar-large-circle.clickable:hover .avatar-overlay {
  opacity: 1;
}

.avatar-large-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  background: rgba(0, 0, 0, 0.55);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #ffffff;
  opacity: 0;
  transition: opacity 0.25s ease;
  gap: 2px;
}

.overlay-text {
  font-size: 10px;
  font-weight: 600;
}

.hidden-file-input {
  display: none;
}

.btn-upload-avatar-trigger {
  margin-top: 8px;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 4px 12px;
  font-size: 12px;
  font-weight: 600;
  color: #8f692f;
  background-color: #faf6ee;
  border: 1px solid #ebd8b8;
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-upload-avatar-trigger:hover:not(:disabled) {
  background-color: #b58a46;
  color: #ffffff;
  border-color: #b58a46;
}

.btn-upload-avatar-trigger:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.inline-icon {
  flex-shrink: 0;
}
</style>
