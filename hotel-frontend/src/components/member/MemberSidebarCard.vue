<template>
  <div class="member-sidebar-section">
    <div class="avatar-box">
      <div
        class="avatar-large-circle"
        :class="{ 'clickable': allowUpload }"
        @click="triggerFileInput"
        title="點擊更換頭像照片"
      >
        <img
          v-if="effectiveAvatarUrl && !hasAvatarError"
          :src="effectiveAvatarUrl"
          alt="Avatar"
          class="avatar-large-img"
          @error="hasAvatarError = true"
        />
        <span v-else>{{ userInitial }}</span>

        <div v-if="allowUpload" class="avatar-overlay">
          <Camera :size="20" />
          <span class="overlay-text">{{ uploading ? '上傳中' : '更換頭像' }}</span>
        </div>
      </div>

      <input
        v-if="allowUpload"
        ref="fileInputRef"
        type="file"
        accept="image/jpeg,image/png,image/webp,image/gif"
        class="hidden-file-input"
        @change="handleFileSelected"
      />

      <button
        v-if="allowUpload"
        type="button"
        class="btn-upload-avatar-trigger"
        :disabled="uploading"
        @click="triggerFileInput"
      >
        <Upload :size="14" class="inline-icon" />
        <span>{{ uploading ? '上傳中...' : '上傳頭像' }}</span>
      </button>
    </div>

    <div class="sidebar-user-name">{{ displayName }}</div>
    <div class="sidebar-tier-tag">{{ tierText }}</div>
    <slot></slot>

    <!-- 通用原生 Canvas 圖片裁切彈窗 -->
    <ImageCropModal
      v-model="isCropping"
      :image-src="rawImageSrc"
      title="調整個人頭像"
      confirm-text="確認並上傳"
      shape="circle"
      :viewport-width="240"
      :viewport-height="240"
      :output-width="400"
      :output-height="400"
      @confirm="handleCropConfirm"
      @cancel="handleCropCancel"
    />
  </div>
</template>

<script setup>
import { computed, ref, watch, onBeforeUnmount } from "vue";
import { Camera, Upload } from "@lucide/vue";
import { useAuthStore } from "@/stores/auth";
import ImageCropModal from "@/components/common/ImageCropModal.vue";

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
const hasAvatarError = ref(false);

const effectiveAvatarUrl = computed(() => {
  return props.avatarUrl || authStore.avatarUrl || "";
});

watch(effectiveAvatarUrl, () => {
  hasAvatarError.value = false;
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

// ============================================================
// 裁切彈窗呼叫邏輯
// ============================================================
const isCropping = ref(false);
const rawImageSrc = ref("");

function handleFileSelected(event) {
  const file = event.target.files && event.target.files[0];
  if (!file) return;

  if (!file.type.startsWith("image/")) {
    return;
  }

  if (rawImageSrc.value && rawImageSrc.value.startsWith("blob:")) {
    URL.revokeObjectURL(rawImageSrc.value);
  }

  rawImageSrc.value = URL.createObjectURL(file);
  isCropping.value = true;
  event.target.value = "";
}

function handleCropConfirm(croppedFile) {
  emit("upload-avatar", croppedFile);
  cleanupBlob();
}

function handleCropCancel() {
  cleanupBlob();
}

function cleanupBlob() {
  if (rawImageSrc.value && rawImageSrc.value.startsWith("blob:")) {
    URL.revokeObjectURL(rawImageSrc.value);
    rawImageSrc.value = "";
  }
}

onBeforeUnmount(() => {
  cleanupBlob();
});
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
