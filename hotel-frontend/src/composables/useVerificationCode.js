import { ref, onUnmounted } from "vue";

/**
 * 驗證碼發送與倒數計時控制
 */
export function useVerificationCode() {
  const sendingCode = ref(false);
  const countdown = ref(0);
  let timer = null;

  function startCountdown(seconds = 60) {
    countdown.value = seconds;
    if (timer) clearInterval(timer);
    timer = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) {
        clearInterval(timer);
        timer = null;
      }
    }, 1000);
  }

  function clearTimer() {
    if (timer) {
      clearInterval(timer);
      timer = null;
    }
  }

  onUnmounted(() => {
    clearTimer();
  });

  return {
    sendingCode,
    countdown,
    startCountdown,
    clearTimer,
  };
}

