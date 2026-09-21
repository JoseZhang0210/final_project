<template>
  <Teleport to="body">
    <div v-if="modelValue" class="crop-modal-overlay" @click.self="closeModal">
      <div class="crop-modal-card" role="dialog" aria-modal="true" :aria-labelledby="titleId">
        <div class="crop-modal-header">
          <h3 :id="titleId">{{ title }}</h3>
          <button type="button" class="btn-close-crop" @click="closeModal" aria-label="關閉">
            <X :size="18" />
          </button>
        </div>

        <div class="crop-modal-body">
          <!-- 裁切視窗預覽區 -->
          <div
            class="crop-viewport"
            :class="{ 'is-circle': shape === 'circle' }"
            :style="{ width: `${viewportWidth}px`, height: `${viewportHeight}px` }"
            @mousedown="startDrag"
            @touchstart="startTouchDrag"
            @wheel.prevent="handleWheel"
          >
            <img
              v-if="imageSrc"
              ref="rawImgRef"
              :src="imageSrc"
              alt="Crop preview"
              class="crop-preview-image"
              :style="imageTransformStyle"
              @load="initImageDimensions"
              draggable="false"
            />

            <!-- 裁切形狀導引遮罩 -->
            <div
              class="crop-guide-mask"
              :class="{ 'is-circle': shape === 'circle' }"
            ></div>
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
          <button type="button" class="btn-crop-cancel" @click="closeModal">
            取消
          </button>
          <button
            type="button"
            class="btn-crop-confirm"
            :disabled="isGeneratingBlob"
            @click="handleConfirm"
          >
            <Check :size="16" class="inline-icon" />
            <span>{{ isGeneratingBlob ? '處理中...' : confirmText }}</span>
          </button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { computed, ref, watch, onBeforeUnmount } from "vue";
import { X, ZoomIn, ZoomOut, RotateCcw, Check } from "@lucide/vue";

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false,
  },
  imageSrc: {
    type: String,
    default: "",
  },
  title: {
    type: String,
    default: "調整圖片",
  },
  confirmText: {
    type: String,
    default: "確認並上傳",
  },
  shape: {
    type: String,
    default: "circle", // 'circle' | 'square' | 'rect'
  },
  viewportWidth: {
    type: Number,
    default: 240,
  },
  viewportHeight: {
    type: Number,
    default: 240,
  },
  outputWidth: {
    type: Number,
    default: 400,
  },
  outputHeight: {
    type: Number,
    default: 400,
  },
  outputType: {
    type: String,
    default: "image/jpeg",
  },
  outputQuality: {
    type: Number,
    default: 0.92,
  },
});

const emit = defineEmits(["update:modelValue", "confirm", "cancel"]);

const titleId = `crop-title-${Math.random().toString(36).slice(2, 8)}`;
const rawImgRef = ref(null);

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

function initImageDimensions() {
  if (!rawImgRef.value) return;
  const nw = rawImgRef.value.naturalWidth || props.outputWidth;
  const nh = rawImgRef.value.naturalHeight || props.outputHeight;

  originalWidth.value = nw;
  originalHeight.value = nh;

  // 計算可完整填滿視窗的 baseScale
  const scaleX = props.viewportWidth / nw;
  const scaleY = props.viewportHeight / nh;
  baseScale.value = Math.max(scaleX, scaleY);

  resetCrop();
}

function resetCrop() {
  scale.value = 1.0;
  const dispW = originalWidth.value * baseScale.value;
  const dispH = originalHeight.value * baseScale.value;
  offsetX.value = (props.viewportWidth - dispW) / 2;
  offsetY.value = (props.viewportHeight - dispH) / 2;
}

watch(
  () => props.imageSrc,
  () => {
    if (props.imageSrc) {
      setTimeout(() => initImageDimensions(), 20);
    }
  }
);

watch(
  () => props.modelValue,
  (val) => {
    if (val) {
      setTimeout(() => initImageDimensions(), 20);
    } else {
      stopDrag();
      stopTouchDrag();
    }
  }
);

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

function closeModal() {
  stopDrag();
  stopTouchDrag();
  emit("update:modelValue", false);
  emit("cancel");
}

async function handleConfirm() {
  if (!rawImgRef.value || isGeneratingBlob.value) return;
  isGeneratingBlob.value = true;

  try {
    const canvas = document.createElement("canvas");
    canvas.width = props.outputWidth;
    canvas.height = props.outputHeight;
    const ctx = canvas.getContext("2d");

    const img = rawImgRef.value;
    const currentScale = baseScale.value * scale.value;

    const viewportCenterX = props.viewportWidth / 2;
    const viewportCenterY = props.viewportHeight / 2;

    const imgCenterRenderX = offsetX.value + (originalWidth.value * baseScale.value) / 2;
    const imgCenterRenderY = offsetY.value + (originalHeight.value * baseScale.value) / 2;

    const diffX = viewportCenterX - imgCenterRenderX;
    const diffY = viewportCenterY - imgCenterRenderY;

    const cropHalfWidthInImg = (props.viewportWidth / 2) / currentScale;
    const cropHalfHeightInImg = (props.viewportHeight / 2) / currentScale;

    const cropCenterXInImg = (originalWidth.value / 2) - (diffX / currentScale);
    const cropCenterYInImg = (originalHeight.value / 2) - (diffY / currentScale);

    const sourceX = cropCenterXInImg - cropHalfWidthInImg;
    const sourceY = cropCenterYInImg - cropHalfHeightInImg;
    const sourceWidth = cropHalfWidthInImg * 2;
    const sourceHeight = cropHalfHeightInImg * 2;

    ctx.drawImage(
      img,
      sourceX,
      sourceY,
      sourceWidth,
      sourceHeight,
      0,
      0,
      props.outputWidth,
      props.outputHeight
    );

    const blob = await new Promise((resolve) => {
      canvas.toBlob(resolve, props.outputType, props.outputQuality);
    });

    if (blob) {
      const ext = props.outputType.includes("png") ? "png" : "jpg";
      const croppedFile = new File([blob], `cropped_${Date.now()}.${ext}`, {
        type: props.outputType,
      });
      emit("confirm", croppedFile);
      emit("update:modelValue", false);
    }
  } catch (err) {
    console.error("Canvas 裁切失敗：", err);
  } finally {
    isGeneratingBlob.value = false;
  }
}

onBeforeUnmount(() => {
  stopDrag();
  stopTouchDrag();
});
</script>

<style scoped>
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

.crop-viewport {
  position: relative;
  overflow: hidden;
  background-color: #2b251f;
  cursor: grab;
  user-select: none;
  touch-action: none;
  box-shadow: inset 0 0 12px rgba(0, 0, 0, 0.35), 0 4px 14px rgba(0, 0, 0, 0.12);
}

.crop-viewport.is-circle {
  border-radius: 50%;
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

.crop-guide-mask {
  position: absolute;
  inset: 0;
  border: 2px solid rgba(255, 255, 255, 0.85);
  box-shadow: 0 0 0 9999px rgba(0, 0, 0, 0.25);
  pointer-events: none;
}

.crop-guide-mask.is-circle {
  border-radius: 50%;
}

.crop-hint {
  font-size: 12px;
  color: #8c7d6e;
  margin: 12px 0 14px;
  text-align: center;
}

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

.inline-icon {
  flex-shrink: 0;
}
</style>
