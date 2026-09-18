<template>
  <Transition name="fade">
    <div v-if="show" class="alert-overlay">
      <div class="alert-modal">
        <div class="alert-icon" :class="type">
          <span v-if="type === 'success'">✓</span>
          <span v-if="type === 'error'">✕</span>
        </div>
        <h3 class="alert-title">{{ title }}</h3>
        <p class="alert-message">{{ message }}</p>
        <button class="btn-alert" @click="$emit('close')">我知道了</button>
      </div>
    </div>
  </Transition>
</template>

<script setup>
defineProps({
  show: Boolean,
  type: {
    type: String,
    default: 'success'
  },
  title: String,
  message: String
});

defineEmits(['close']);
</script>

<style scoped>
.alert-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  backdrop-filter: blur(4px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.alert-modal {
  background: white;
  width: 90%;
  max-width: 320px;
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  box-shadow: 0 10px 40px rgba(0,0,0,0.1);
  animation: modalScaleUp 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.alert-icon {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  margin: 0 auto 16px;
  color: white;
}

.alert-icon.success {
  background: #C9A96E;
  box-shadow: 0 4px 12px rgba(201, 169, 110, 0.3);
}

.alert-icon.error {
  background: #e74c3c;
  box-shadow: 0 4px 12px rgba(231, 76, 60, 0.3);
}

.alert-title {
  font-size: 20px;
  font-weight: 600;
  color: #333;
  margin: 0 0 8px;
}

.alert-message {
  font-size: 15px;
  color: #666;
  margin: 0 0 24px;
  line-height: 1.5;
}

.btn-alert {
  background: #333;
  color: white;
  border: none;
  border-radius: 8px;
  padding: 12px 24px;
  font-size: 15px;
  font-weight: 500;
  width: 100%;
  cursor: pointer;
  transition: background 0.2s;
}

.btn-alert:hover {
  background: #555;
}

@keyframes modalScaleUp {
  from { opacity: 0; transform: scale(0.9); }
  to { opacity: 1; transform: scale(1); }
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>
