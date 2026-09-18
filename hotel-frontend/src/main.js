import "./assets/hotel-common.css";
import "./assets/admin.css";
import "./assets/product/product-common.css";

import { createApp } from "vue";
import { createPinia } from "pinia";
import axios from "axios";

import App from "./App.vue";
import router from "./router";

// ============================================================
// 全域攔截 Fetch & Axios 請求
// 自動注入 ngrok-skip-browser-warning Header 避開 ngrok 免費版警告 HTML 頁面
// ============================================================
const originalFetch = window.fetch;
window.fetch = function (input, init = {}) {
  init = init || {};
  if (!init.headers) {
    init.headers = {};
  }
  if (init.headers instanceof Headers) {
    init.headers.set("ngrok-skip-browser-warning", "true");
  } else if (Array.isArray(init.headers)) {
    init.headers.push(["ngrok-skip-browser-warning", "true"]);
  } else {
    init.headers["ngrok-skip-browser-warning"] = "true";
  }
  return originalFetch(input, init);
};

// Axios 全域預設 Header
if (axios && axios.defaults) {
  axios.defaults.headers.common["ngrok-skip-browser-warning"] = "true";
}

const app = createApp(App);

app.use(createPinia());
app.use(router);

app.mount("#app");
