<template>
  <Transition name="toast-fade">
    <div v-if="toastStore.visible" :class="['toast-wrapper', toastStore.type]">
      <div class="toast-icon">
        <span v-if="toastStore.type === 'success'">✓</span>
        <span v-else-if="toastStore.type === 'error'">✕</span>
        <span v-else>ℹ</span>
      </div>
      <div class="toast-message">
        {{ toastStore.message }}
      </div>
      <button class="toast-close" @click="toastStore.hideToast">×</button>
    </div>
  </Transition>
</template>

<script setup>
import { useToastStore } from '@/stores/toast';

const toastStore = useToastStore();
</script>

<style scoped>
.toast-wrapper {
  position: fixed;
  top: 24px;
  right: 24px;
  z-index: 9999;
  min-width: 280px;
  max-width: 420px;
  padding: 14px 18px;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 15px;
  color: #fff;
  pointer-events: auto;
}

.toast-wrapper.success {
  background-color: #2e7d32;
  border-left: 5px solid #1b5e20;
}

.toast-wrapper.error {
  background-color: #d32f2f;
  border-left: 5px solid #b71c1c;
}

.toast-wrapper.info {
  background-color: #0288d1;
  border-left: 5px solid #01579b;
}

.toast-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: bold;
  font-size: 14px;
  flex-shrink: 0;
}

.toast-message {
  flex: 1;
  line-height: 1.4;
  word-break: break-word;
}

.toast-close {
  background: transparent;
  border: none;
  color: #fff;
  font-size: 20px;
  line-height: 1;
  cursor: pointer;
  opacity: 0.8;
  padding: 0 4px;
  transition: opacity 0.2s;
}

.toast-close:hover {
  opacity: 1;
}

/* 動畫效果 */
.toast-fade-enter-active,
.toast-fade-leave-active {
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
}

.toast-fade-enter-from {
  opacity: 0;
  transform: translateY(-20px) scale(0.95);
}

.toast-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px) scale(0.95);
}
</style>

