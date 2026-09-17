<template>
  <div class="password-input-wrapper">
    <input
      :id="id"
      :value="modelValue"
      :type="showPassword ? 'text' : 'password'"
      class="form-input"
      :class="{ 'has-error': hasError }"
      :placeholder="placeholder"
      :autocomplete="autocomplete"
      @input="onInput"
      @blur="$emit('blur')"
    />
    <button
      type="button"
      class="toggle-pwd-btn"
      tabindex="-1"
      :title="showPassword ? '隱藏密碼' : '顯示密碼'"
      @click="showPassword = !showPassword"
    >
      <!-- Lucide Eye (密碼可見時顯示) -->
      <svg
        v-if="showPassword"
        xmlns="http://www.w3.org/2000/svg"
        width="18"
        height="18"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
        class="lucide-icon lucide-eye"
      >
        <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
        <circle cx="12" cy="12" r="3" />
      </svg>
      <!-- Lucide Eye-Off (密碼隱藏時顯示) -->
      <svg
        v-else
        xmlns="http://www.w3.org/2000/svg"
        width="18"
        height="18"
        viewBox="0 0 24 24"
        fill="none"
        stroke="currentColor"
        stroke-width="2"
        stroke-linecap="round"
        stroke-linejoin="round"
        class="lucide-icon lucide-eye-off"
      >
        <path d="M9.88 9.88a3 3 0 1 0 4.24 4.24" />
        <path d="M10.73 5.08A10.43 10.43 0 0 1 12 5c7 0 10 7 10 7a13.16 13.16 0 0 1-1.67 2.68" />
        <path d="M6.61 6.61A13.526 13.526 0 0 0 2 12s3 7 10 7a9.74 9.74 0 0 0 5.39-1.61" />
        <line x1="2" x2="22" y1="2" y2="22" />
      </svg>
    </button>
  </div>
</template>

<script setup>
import { ref } from "vue";

defineProps({
  modelValue: {
    type: String,
    default: "",
  },
  id: {
    type: String,
    default: "",
  },
  placeholder: {
    type: String,
    default: "",
  },
  autocomplete: {
    type: String,
    default: "current-password",
  },
  hasError: {
    type: Boolean,
    default: false,
  },
});

const emit = defineEmits(["update:modelValue", "input", "blur"]);

const showPassword = ref(false);

function onInput(event) {
  emit("update:modelValue", event.target.value);
  emit("input", event);
}
</script>

<style scoped>
.password-input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  width: 100%;
}

.password-input-wrapper .form-input {
  width: 100%;
  padding-right: 42px;
}

.toggle-pwd-btn {
  position: absolute;
  right: 10px;
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #8c7d6e;
  transition: color 0.2s, opacity 0.2s;
  user-select: none;
}

.toggle-pwd-btn:hover {
  color: #4a3b2a;
}

.lucide-icon {
  display: block;
}
</style>

