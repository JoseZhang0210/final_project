import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useToastStore = defineStore('toast', () => {
  const visible = ref(false)
  const message = ref('')
  const type = ref('success') // 'success' | 'error' | 'info'

  let timer = null

  function showToast(msg, toastType = 'success', duration = 2500) {
    // 清除舊計時器（避免快速連續觸發時疊加）
    if (timer) {
      clearTimeout(timer)
    }

    message.value = msg
    type.value = toastType
    visible.value = true

    timer = setTimeout(() => {
      visible.value = false
      timer = null
    }, duration)
  }

  function hideToast() {
    visible.value = false
    if (timer) {
      clearTimeout(timer)
      timer = null
    }
  }

  return { visible, message, type, showToast, hideToast }
})
