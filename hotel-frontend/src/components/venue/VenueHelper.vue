<script setup>
import { nextTick, ref } from "vue";

const isOpen = ref(false);
const userInput = ref("");
const chatBodyRef = ref(null);

const faqs = [
  {
    id: "booking",
    question: "如何預約場地？",
    answer:
      "登入會員後選擇場地、日期、活動名稱與人數，送出後建立場地預約。",
  },
  {
    id: "price",
    question: "場地如何收費？",
    answer:
      "依所選場地的每日租借價格計費，建立租借時的付款金額由後端保存，不由前端自行計算或修改。",
  },
  {
    id: "date",
    question: "哪些日期可以預約？",
    answer:
      "不可預約過去日期；同一場地同一天若已有有效租借，就不能重複預約。",
  },
  {
    id: "cancel",
    question: "如何取消預約？",
    answer:
      "尚未付款的會員預約可依會員中心既有取消流程處理；已付款或已進入付款流程的預約，需聯絡管理人員處理。",
  },
  {
    id: "payment",
    question: "如何付款？",
    answer:
      "建立租借後依目前網站既有付款流程完成付款。客服不得自行替會員付款。",
  },
  {
    id: "notification",
    question: "付款成功會通知嗎？",
    answer:
      "付款成功後系統會依會員個人資料中的 Email 寄送場地租借付款通知，並更新付款與租借狀態。",
  },
];

const fallbackAnswer =
  "目前場地小幫手可以協助您查詢：預約方式、場地費用、可預約日期、取消方式、付款流程與付款通知。";

const messages = ref([
  {
    id: 1,
    sender: "helper",
    text: "您好！我是場地小幫手 🤖，請問有什麼關於場地租借的問題需要協助？",
    showFaqButtons: true,
  },
]);

function scrollToBottom() {
  nextTick(() => {
    if (chatBodyRef.value) {
      chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight;
    }
  });
}

function toggleOpen() {
  isOpen.value = !isOpen.value;
  if (isOpen.value) {
    scrollToBottom();
  }
}

function selectFaq(faq) {
  messages.value.push({
    id: Date.now(),
    sender: "user",
    text: faq.question,
  });

  messages.value.push({
    id: Date.now() + 1,
    sender: "helper",
    text: faq.answer,
    showFaqButtons: true,
  });

  scrollToBottom();
}

function matchFaqKeyword(query) {
  const q = query.toLowerCase();

  for (const faq of faqs) {
    if (q === faq.question.toLowerCase() || faq.question.toLowerCase().includes(q)) {
      return faq;
    }
  }

  if (q.includes("預約") || q.includes("申請")) {
    return faqs.find((f) => f.id === "booking");
  }
  if (q.includes("費用") || q.includes("金額") || q.includes("價格") || q.includes("算") || q.includes("多少錢")) {
    return faqs.find((f) => f.id === "price");
  }
  if (q.includes("日期") || q.includes("時間") || q.includes("天")) {
    return faqs.find((f) => f.id === "date");
  }
  if (q.includes("取消") || q.includes("退")) {
    return faqs.find((f) => f.id === "cancel");
  }
  if (q.includes("付款") || q.includes("繳費") || q.includes("刷卡")) {
    return faqs.find((f) => f.id === "payment");
  }
  if (q.includes("通知") || q.includes("信") || q.includes("email")) {
    return faqs.find((f) => f.id === "notification");
  }

  return null;
}

function handleSend() {
  const trimmed = userInput.value.trim();
  if (!trimmed) return;

  const query = trimmed;
  userInput.value = "";

  messages.value.push({
    id: Date.now(),
    sender: "user",
    text: query,
  });

  const matched = matchFaqKeyword(query);

  if (matched) {
    messages.value.push({
      id: Date.now() + 1,
      sender: "helper",
      text: matched.answer,
      showFaqButtons: true,
    });
  } else {
    messages.value.push({
      id: Date.now() + 1,
      sender: "helper",
      text: fallbackAnswer,
      showFaqButtons: true,
    });
  }

  scrollToBottom();
}
</script>

<template>
  <div class="venue-helper-root">
    <!-- 右下角浮動按鈕 -->
    <button
      type="button"
      class="venue-helper-toggle-btn"
      :aria-expanded="isOpen"
      aria-label="開啟場地小幫手 FAQ 客服"
      @click="toggleOpen"
    >
      <span class="btn-icon">🤖</span>
      <span class="btn-text">場地小幫手</span>
    </button>

    <!-- 浮動聊天視窗 -->
    <div
      v-if="isOpen"
      class="venue-helper-window"
      role="dialog"
      aria-label="場地小幫手聊天視窗"
    >
      <header class="helper-header">
        <div class="header-info">
          <span class="header-icon">🤖</span>
          <div>
            <h3 class="header-title">場地小幫手</h3>
            <span class="header-subtitle">場地租借 FAQ 客服</span>
          </div>
        </div>
        <button
          type="button"
          class="close-btn"
          aria-label="關閉場地小幫手"
          @click="toggleOpen"
        >
          ✕
        </button>
      </header>

      <div ref="chatBodyRef" class="helper-body">
        <div
          v-for="msg in messages"
          :key="msg.id"
          class="message-group"
          :class="msg.sender"
        >
          <div class="message-bubble">
            <p class="message-text">{{ msg.text }}</p>
          </div>

          <!-- 若小幫手對話包含 FAQ 按鈕 -->
          <div v-if="msg.sender === 'helper' && msg.showFaqButtons" class="faq-chips-wrap">
            <p class="faq-chips-title">常見問題：</p>
            <div class="faq-chips">
              <button
                v-for="faq in faqs"
                :key="faq.id"
                type="button"
                class="faq-chip"
                :aria-label="`詢問：${faq.question}`"
                @click="selectFaq(faq)"
              >
                {{ faq.question }}
              </button>
            </div>
          </div>
        </div>
      </div>

      <footer class="helper-footer">
        <form class="input-form" @submit.prevent="handleSend">
          <input
            v-model="userInput"
            type="text"
            class="chat-input"
            placeholder="請輸入您的問題..."
            aria-label="輸入您的問題"
          />
          <button
            type="submit"
            class="send-btn"
            aria-label="送出問題"
          >
            送出
          </button>
        </form>
      </footer>
    </div>
  </div>
</template>

<style scoped>
.venue-helper-root {
  font-family: Arial, "Microsoft JhengHei", sans-serif;
}

.venue-helper-toggle-btn {
  position: fixed;
  bottom: 24px;
  right: 24px;
  z-index: 999;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  border: none;
  border-radius: 999px;
  background: #b58a46;
  color: #ffffff;
  font-size: 15px;
  font-weight: 700;
  box-shadow: 0 6px 20px rgba(74, 59, 42, 0.25);
  cursor: pointer;
  transition: all 0.2s ease;
}

.venue-helper-toggle-btn:hover {
  background: #9b7435;
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(74, 59, 42, 0.35);
}

.btn-icon {
  font-size: 18px;
}

.venue-helper-window {
  position: fixed;
  bottom: 84px;
  right: 24px;
  z-index: 1000;
  width: 360px;
  max-width: calc(100vw - 32px);
  height: 520px;
  max-height: calc(100vh - 120px);
  display: flex;
  flex-direction: column;
  background: #ffffff;
  border: 1px solid #e6e0d7;
  border-radius: 16px;
  box-shadow: 0 12px 36px rgba(62, 48, 35, 0.2);
  overflow: hidden;
}

.helper-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  background: #4a3b2a;
  color: #ffffff;
}

.header-info {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-icon {
  font-size: 22px;
}

.header-title {
  margin: 0;
  font-size: 16px;
  font-weight: 700;
  color: #ffffff;
}

.header-subtitle {
  font-size: 12px;
  color: #d8c8b4;
}

.close-btn {
  background: transparent;
  border: none;
  color: #ffffff;
  font-size: 18px;
  padding: 4px 8px;
  cursor: pointer;
  border-radius: 6px;
  opacity: 0.85;
}

.close-btn:hover {
  opacity: 1;
  background: rgba(255, 255, 255, 0.15);
}

.helper-body {
  flex: 1;
  padding: 16px;
  overflow-y: auto;
  background: #faf8f5;
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.message-group {
  display: flex;
  flex-direction: column;
  max-width: 85%;
}

.message-group.user {
  align-self: flex-end;
  align-items: flex-end;
}

.message-group.helper {
  align-self: flex-start;
  align-items: flex-start;
}

.message-bubble {
  padding: 10px 14px;
  border-radius: 14px;
  font-size: 14px;
  line-height: 1.5;
  word-break: break-word;
}

.message-group.user .message-bubble {
  background: #b58a46;
  color: #ffffff;
  border-bottom-right-radius: 2px;
}

.message-group.helper .message-bubble {
  background: #ffffff;
  color: #4a3b2a;
  border: 1px solid #e6e0d7;
  border-bottom-left-radius: 2px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
}

.message-text {
  margin: 0;
}

.faq-chips-wrap {
  margin-top: 8px;
  width: 100%;
}

.faq-chips-title {
  margin: 0 0 6px;
  font-size: 12px;
  color: #77695e;
  font-weight: 700;
}

.faq-chips {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.faq-chip {
  padding: 6px 12px;
  border: 1px solid #dcd3c5;
  border-radius: 999px;
  background: #ffffff;
  color: #5f5145;
  font-size: 12px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.15s ease;
  text-align: left;
}

.faq-chip:hover {
  background: #f4ede2;
  border-color: #b58a46;
  color: #4a3b2a;
}

.helper-footer {
  padding: 12px 14px;
  background: #ffffff;
  border-top: 1px solid #e6e0d7;
}

.input-form {
  display: flex;
  gap: 8px;
}

.chat-input {
  flex: 1;
  padding: 9px 12px;
  border: 1px solid #cfc5b8;
  border-radius: 8px;
  font-size: 13px;
  outline: none;
}

.chat-input:focus {
  border-color: #b58a46;
}

.send-btn {
  padding: 9px 14px;
  border: none;
  border-radius: 8px;
  background: #b58a46;
  color: #ffffff;
  font-size: 13px;
  font-weight: 700;
  cursor: pointer;
  white-space: nowrap;
}

.send-btn:hover {
  background: #9b7435;
}

@media (max-width: 480px) {
  .venue-helper-toggle-btn {
    bottom: 16px;
    right: 16px;
    padding: 10px 16px;
    font-size: 14px;
  }

  .venue-helper-window {
    bottom: 72px;
    right: 16px;
    width: calc(100vw - 32px);
    height: 460px;
  }
}
</style>
