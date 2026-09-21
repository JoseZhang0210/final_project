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
      <!-- Lucide Eye / EyeOff -->
      <Eye
        v-if="showPassword"
        :size="18"
        class="lucide-icon lucide-eye"
      />
      <EyeOff
        v-else
        :size="18"
        class="lucide-icon lucide-eye-off"
      />
    </button>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { Eye, EyeOff } from "@lucide/vue";

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

