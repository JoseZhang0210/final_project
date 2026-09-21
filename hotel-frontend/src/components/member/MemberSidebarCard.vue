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

    <!-- ========================================= -->
    <!-- 原生 Canvas 頭像裁切 / 位置調整彈窗       -->
    <!-- ========================================= -->
    <Teleport to="body">
      <div v-if="isCropping" class="crop-modal-overlay" @click.self="cancelCrop">
        <div class="crop-modal-card" role="dialog" aria-modal="true" aria-labelledby="crop-modal-title">
          <div class="crop-modal-header">
            <h3 id="crop-modal-title">調整個人頭像</h3>
            <button type="button" class="btn-close-crop" @click="cancelCrop" aria-label="關閉">
              <X :size="18" />
            </button>
          </div>

          <div class="crop-modal-body">
            <!-- 裁切視窗預覽區 (圓形遮罩) -->
            <div
              class="crop-viewport"
              ref="viewportRef"
              @mousedown="startDrag"
              @touchstart="startTouchDrag"
              @wheel.prevent="handleWheel"
            >
              <img
                v-if="rawImageSrc"
                ref="rawImgRef"
                :src="rawImageSrc"
                alt="Crop preview"
                class="crop-preview-image"
                :style="imageTransformStyle"
                @load="initImageDimensions"
                draggable="false"
              />

              <!-- 圓形遮罩輔助框 -->
              <div class="crop-circular-guide"></div>
            </div>

            <p class="crop-hint">可拖曳圖片調整顯示位置，或使用下方滑桿縮放</p>

            <!-- 縮放控制器 -->
            <div class="crop-controls">
              <button
                type="button"
                class="btn-zoom-adjust"
                :disabled="scale <= minScale"
                @click="adjustScale(-0.1)"
                title="縮小"
              >
                <ZoomOut :size="16" />
              </button>

              <input
                type="range"
                v-model.number="scale"
                :min="minScale"
                :max="maxScale"
                step="0.02"
                class="zoom-slider"
                aria-label="圖片縮放比例"
              />

              <button
                type="button"
                class="btn-zoom-adjust"
                :disabled="scale >= maxScale"
                @click="adjustScale(0.1)"
                title="放大"
              >
                <ZoomIn :size="16" />
              </button>

              <button
                type="button"
                class="btn-reset-crop"
                @click="resetCrop"
                title="重設位置與大小"
              >
                <RotateCcw :size="15" />
              </button>
            </div>
          </div>

          <div class="crop-modal-footer">
            <button type="button" class="btn-crop-cancel" @click="cancelCrop">
              取消
            </button>
            <button
              type="button"
              class="btn-crop-confirm"
              :disabled="isGeneratingBlob"
              @click="confirmCrop"
            >
              <Check :size="16" class="inline-icon" />
              <span>{{ isGeneratingBlob ? '處理中...' : '確認並上傳' }}</span>
            </button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { computed, ref, watch, onBeforeUnmount } from "vue";
import { Camera, Upload, X, ZoomIn, ZoomOut, RotateCcw, Check } from "@lucide/vue";
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
// 原生 Canvas 圖片裁切與位置調整邏輯
// ============================================================
const VIEWPORT_SIZE = 240; // 預覽遮罩尺寸 (px)
const OUTPUT_SIZE = 400;   // 最終產出畫質尺寸 (px)

const isCropping = ref(false);
const rawImageSrc = ref("");
const rawImgRef = ref(null);
const viewportRef = ref(null);

const originalWidth = ref(0);
const originalHeight = ref(0);
const baseScale = ref(1);

const scale = ref(1);
const minScale = ref(0.6);
const maxScale = ref(3.0);
const offsetX = ref(0);
const offsetY = ref(0);

const isGeneratingBlob = ref(false);

let isDragging = false;
let startPointerX = 0;
let startPointerY = 0;
let initialOffsetX = 0;
let initialOffsetY = 0;

function handleFileSelected(event) {
  const file = event.target.files && event.target.files[0];
  if (!file) return;

  // 驗證是否為圖片
  if (!file.type.startsWith("image/")) {
    return;
  }

  // 釋放舊的 ObjectURL
  if (rawImageSrc.value && rawImageSrc.value.startsWith("blob:")) {
    URL.revokeObjectURL(rawImageSrc.value);
  }

  rawImageSrc.value = URL.createObjectURL(file);
  isCropping.value = true;

  // 清空 input 讓重複選相同檔案也能觸發 change
  event.target.value = "";
}

function initImageDimensions() {
  if (!rawImgRef.value) return;
  const nw = rawImgRef.value.naturalWidth || 400;
  const nh = rawImgRef.value.naturalHeight || 400;

  originalWidth.value = nw;
  originalHeight.value = nh;

  // 計算剛好涵蓋 240x240 視窗的 baseScale
  const minDim = Math.min(nw, nh);
  baseScale.value = VIEWPORT_SIZE / minDim;

  resetCrop();
}

function resetCrop() {
  scale.value = 1.0;
  // 預設將圖片置中
  const dispW = originalWidth.value * baseScale.value;
  const dispH = originalHeight.value * baseScale.value;
  offsetX.value = (VIEWPORT_SIZE - dispW) / 2;
  offsetY.value = (VIEWPORT_SIZE - dispH) / 2;
}

const imageTransformStyle = computed(() => {
  const currentW = originalWidth.value * baseScale.value;
  const currentH = originalHeight.value * baseScale.value;

  return {
    width: `${currentW}px`,
    height: `${currentH}px`,
    transform: `translate(${offsetX.value}px, ${offsetY.value}px) scale(${scale.value})`,
    transformOrigin: "center center",
  };
});

function adjustScale(delta) {
  const newScale = Math.min(maxScale.value, Math.max(minScale.value, +(scale.value + delta).toFixed(2)));
  scale.value = newScale;
}

function handleWheel(e) {
  const delta = e.deltaY < 0 ? 0.08 : -0.08;
  adjustScale(delta);
}

// 拖曳平移 (滑鼠)
function startDrag(e) {
  if (e.button !== 0) return;
  isDragging = true;
  startPointerX = e.clientX;
  startPointerY = e.clientY;
  initialOffsetX = offsetX.value;
  initialOffsetY = offsetY.value;

  window.addEventListener("mousemove", onDragging);
  window.addEventListener("mouseup", stopDrag);
}

function onDragging(e) {
  if (!isDragging) return;
  const dx = e.clientX - startPointerX;
  const dy = e.clientY - startPointerY;
  offsetX.value = initialOffsetX + dx;
  offsetY.value = initialOffsetY + dy;
}

function stopDrag() {
  isDragging = false;
  window.removeEventListener("mousemove", onDragging);
  window.removeEventListener("mouseup", stopDrag);
}

// 拖曳平移 (觸控)
function startTouchDrag(e) {
  if (e.touches.length !== 1) return;
  isDragging = true;
  startPointerX = e.touches[0].clientX;
  startPointerY = e.touches[0].clientY;
  initialOffsetX = offsetX.value;
  initialOffsetY = offsetY.value;

  window.addEventListener("touchmove", onTouchDragging, { passive: false });
  window.addEventListener("touchend", stopTouchDrag);
}

function onTouchDragging(e) {
  if (!isDragging || e.touches.length !== 1) return;
  e.preventDefault();
  const dx = e.touches[0].clientX - startPointerX;
  const dy = e.touches[0].clientY - startPointerY;
  offsetX.value = initialOffsetX + dx;
  offsetY.value = initialOffsetY + dy;
}

function stopTouchDrag() {
  isDragging = false;
  window.removeEventListener("touchmove", onTouchDragging);
  window.removeEventListener("touchend", stopTouchDrag);
}

function cancelCrop() {
  isCropping.value = false;
  stopDrag();
  stopTouchDrag();
  if (rawImageSrc.value && rawImageSrc.value.startsWith("blob:")) {
    URL.revokeObjectURL(rawImageSrc.value);
    rawImageSrc.value = "";
  }
}

// 點擊確認：使用 Native Canvas 繪製裁切區域並輸出 File
async function confirmCrop() {
  if (!rawImgRef.value || isGeneratingBlob.value) return;
  isGeneratingBlob.value = true;

  try {
    const canvas = document.createElement("canvas");
    canvas.width = OUTPUT_SIZE;
    canvas.height = OUTPUT_SIZE;
    const ctx = canvas.getContext("2d");

    const img = rawImgRef.value;
    const currentScale = baseScale.value * scale.value;

    // 計算在 240x240 視窗中心點對應回原圖的座標與尺寸
    const viewportCenterX = VIEWPORT_SIZE / 2;
    const viewportCenterY = VIEWPORT_SIZE / 2;

    const imgCenterRenderX = offsetX.value + (originalWidth.value * baseScale.value) / 2;
    const imgCenterRenderY = offsetY.value + (originalHeight.value * baseScale.value) / 2;

    const diffX = viewportCenterX - imgCenterRenderX;
    const diffY = viewportCenterY - imgCenterRenderY;

    const cropRadiusInImg = (VIEWPORT_SIZE / 2) / currentScale;
    const cropCenterXInImg = (originalWidth.value / 2) - (diffX / currentScale);
    const cropCenterYInImg = (originalHeight.value / 2) - (diffY / currentScale);

    const sourceX = cropCenterXInImg - cropRadiusInImg;
    const sourceY = cropCenterYInImg - cropRadiusInImg;
    const sourceSize = cropRadiusInImg * 2;

    ctx.drawImage(
      img,
      sourceX,
      sourceY,
      sourceSize,
      sourceSize,
      0,
      0,
      OUTPUT_SIZE,
      OUTPUT_SIZE
    );

    const blob = await new Promise((resolve) => {
      canvas.toBlob(resolve, "image/jpeg", 0.92);
    });

    if (blob) {
      const croppedFile = new File([blob], `avatar_${Date.now()}.jpg`, {
        type: "image/jpeg",
      });
      emit("upload-avatar", croppedFile);
    }
    cancelCrop();
  } catch (err) {
    console.error("Canvas 裁切失敗：", err);
  } finally {
    isGeneratingBlob.value = false;
  }
}

onBeforeUnmount(() => {
  stopDrag();
  stopTouchDrag();
  if (rawImageSrc.value && rawImageSrc.value.startsWith("blob:")) {
    URL.revokeObjectURL(rawImageSrc.value);
  }
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

/* ========================================= */
/* 裁切彈窗樣式 (Crop Modal Styles)           */
/* ========================================= */
.crop-modal-overlay {
  position: fixed;
  inset: 0;
  background-color: rgba(20, 16, 12, 0.65);
  backdrop-filter: blur(3px);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
  animation: fadeIn 0.2s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.crop-modal-card {
  background: #ffffff;
  border-radius: 16px;
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.22);
  width: 100%;
  max-width: 420px;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.crop-modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #eee7dd;
}

.crop-modal-header h3 {
  margin: 0;
  font-size: 17px;
  font-weight: 700;
  color: #4a3b2a;
}

.btn-close-crop {
  background: transparent;
  border: none;
  color: #8c7d6e;
  cursor: pointer;
  padding: 4px;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background 0.2s, color 0.2s;
}

.btn-close-crop:hover {
  background: #f7f3eb;
  color: #4a3b2a;
}

.crop-modal-body {
  padding: 24px 20px 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

/* 裁切視窗與圓形遮罩 */
.crop-viewport {
  width: 240px;
  height: 240px;
  position: relative;
  overflow: hidden;
  background-color: #2b251f;
  border-radius: 50%;
  cursor: grab;
  user-select: none;
  touch-action: none;
  box-shadow: inset 0 0 12px rgba(0, 0, 0, 0.35), 0 4px 14px rgba(0, 0, 0, 0.12);
}

.crop-viewport:active {
  cursor: grabbing;
}

.crop-preview-image {
  position: absolute;
  top: 0;
  left: 0;
  pointer-events: none;
}

.crop-circular-guide {
  position: absolute;
  inset: 0;
  border-radius: 50%;
  border: 2px solid rgba(255, 255, 255, 0.85);
  box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.25);
  pointer-events: none;
}

.crop-hint {
  font-size: 12px;
  color: #8c7d6e;
  margin: 12px 0 14px;
  text-align: center;
}

/* 縮放控制器 */
.crop-controls {
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  padding: 0 10px;
}

.btn-zoom-adjust,
.btn-reset-crop {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  border: 1px solid #dfd3c3;
  background: #faf6ee;
  color: #6d5b4b;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  flex-shrink: 0;
}

.btn-zoom-adjust:hover:not(:disabled),
.btn-reset-crop:hover {
  background: #b58a46;
  color: #ffffff;
  border-color: #b58a46;
}

.btn-zoom-adjust:disabled {
  opacity: 0.4;
  cursor: not-allowed;
}

.zoom-slider {
  flex: 1;
  max-width: 190px;
  accent-color: #b58a46;
  cursor: pointer;
}

.crop-modal-footer {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
  padding: 14px 20px;
  border-top: 1px solid #eee7dd;
  background: #fdfbf7;
}

.btn-crop-cancel {
  padding: 8px 18px;
  font-size: 14px;
  font-weight: 600;
  color: #6d5b4b;
  background: #ffffff;
  border: 1px solid #dfd3c3;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.btn-crop-cancel:hover {
  background: #f7f3eb;
  color: #4a3b2a;
}

.btn-crop-confirm {
  padding: 8px 18px;
  font-size: 14px;
  font-weight: 600;
  color: #ffffff;
  background: linear-gradient(135deg, #b58a46, #8f692f);
  border: none;
  border-radius: 8px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  box-shadow: 0 2px 8px rgba(181, 138, 70, 0.25);
  transition: all 0.2s ease;
}

.btn-crop-confirm:hover:not(:disabled) {
  background: linear-gradient(135deg, #c49851, #9f7535);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(181, 138, 70, 0.35);
}

.btn-crop-confirm:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
</style>
