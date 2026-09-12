<script setup>
import { computed, ref } from "vue";
import { storeToRefs } from "pinia";
import { useRouter, useRoute } from "vue-router";
import { useAuthStore } from "@/stores/auth";
import { useToastStore } from "@/stores/toast";

const router = useRouter();
const route = useRoute();
const authStore = useAuthStore();
const toastStore = useToastStore();
const { isLoggedIn, name, authorities } = storeToRefs(authStore);

const cartCount = ref(0);

const displayName = computed(() => {
  return name.value || "會員";
});

const userInitial = computed(() => {
  if (name.value && name.value.trim().length > 0) {
    return name.value.trim().charAt(0);
  }
  return "客";
});

const isAdminOrEmployee = computed(() => {
  if (!authorities.value || !Array.isArray(authorities.value)) return false;
  return authorities.value.some(
    (auth) => auth && (auth.includes("ROLE_ADMIN") || auth.includes("ROLE_EMPLOYEE"))
  );
});

// =========================================
// 成果演示專用：特定帳號切換 (customer01 ↔ admin01)
// =========================================

function parseJwtPayload(token) {
  if (!token) return null;
  try {
    const parts = token.split(".");
    if (parts.length !== 3) return null;
    const base64Url = parts[1];
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );
    return JSON.parse(jsonPayload);
  } catch (e) {
    return null;
  }
}

// 取得當前使用者帳號 (sub)
const currentUsername = computed(() => {
  if (!isLoggedIn.value) return "";
  const _ = `${name.value}_${(authorities.value || []).join(",")}`;
  const token = localStorage.getItem("token");
  if (!token) return "";
  const payload = parseJwtPayload(token);
  return payload?.sub || "";
});

// 判斷是否為演示專用帳號
const isDemoAccount = computed(() => {
  return currentUsername.value === "customer01" || currentUsername.value === "admin01";
});

// 判斷目前是否為員工帳號 (admin01)
const isEmployeeMode = computed(() => {
  return currentUsername.value === "admin01";
});

const isSwitching = ref(false);

async function toggleAccount() {
  if (isSwitching.value) return;

  const targetUsername = isEmployeeMode.value ? "customer01" : "admin01";
  const targetLabel = targetUsername === "admin01" ? "員工 (admin01)" : "會員 (customer01)";

  isSwitching.value = true;
  try {
    const res = await fetch("/api/auth/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username: targetUsername,
        password: "123456",
      }),
    });

    if (!res.ok) {
      toastStore.showToast("切換失敗，請確認後端服務是否正常", "error");
      return;
    }

    const data = await res.json();
    authStore.login(data.token, data.authorities, data.name);
    toastStore.showToast(`已切換至${targetLabel}`, "success");

    // 若切換為會員且目前在後台頁面，自動導向首頁避免權限錯誤
    if (targetUsername === "customer01" && route.path.startsWith("/admin")) {
      router.push("/");
    }
  } catch (err) {
    console.error("切換帳號失敗:", err);
    toastStore.showToast("網路異常，無法切換帳號", "error");
  } finally {
    isSwitching.value = false;
  }
}
</script>

<template>
  <div class="layout">
    <header>
      <RouterLink to="/" class="logo" aria-label="星澄飯店首頁">
        <img src="/images/starlight-hotel-logo-emblem.svg" alt="星澄飯店" />
      </RouterLink>
      
      <nav>
        <!-- 成果演示切換按鈕：僅在登入特定帳號 (customer01 / admin01) 時顯示 -->
        <div v-if="isLoggedIn && isDemoAccount" class="demo-toggle-wrapper">
          <div
            class="demo-toggle-switch"
            :class="{ 'is-employee': isEmployeeMode, 'is-loading': isSwitching }"
            :title="isEmployeeMode ? '點擊切換為會員 (customer01)' : '點擊切換為員工 (admin01)'"
            @click="toggleAccount"
            role="button"
            tabindex="0"
            @keydown.enter.prevent="toggleAccount"
            @keydown.space.prevent="toggleAccount"
          >
            <div class="toggle-track">
              <span class="toggle-option member" :class="{ active: !isEmployeeMode }">
                <span class="option-text">會員</span>
              </span>
              <span class="toggle-option employee" :class="{ active: isEmployeeMode }">
                <span class="option-text">員工</span>
              </span>
              <div class="toggle-thumb" :class="{ 'thumb-right': isEmployeeMode }">
                <span v-if="isSwitching" class="spinner"></span>
                <span v-else class="thumb-icon">{{ isEmployeeMode ? '👔' : '👤' }}</span>
              </div>
            </div>
          </div>
        </div>
        
        <RouterLink to="/"> 首頁 </RouterLink>

        <RouterLink to="/products"> 商城商品 </RouterLink>

        <RouterLink to="/room-booking"> 訂房管理 </RouterLink>

        <RouterLink to="/restaurant-menu"> 餐廳 </RouterLink>

        <RouterLink to="/cart">
          購物車 <span v-if="cartCount > 0"> ({{ cartCount }})</span>
        </RouterLink>
        <!-- 場地租借：進入場地租借申請頁面。 -->
        <RouterLink to="/rentals"> 場地租借 </RouterLink>


        <!-- 未登入狀態 -->
        <template v-if="!isLoggedIn">
          <RouterLink to="/register"> 註冊 </RouterLink>
          <RouterLink to="/login" class="nav-login-btn"> 登入 </RouterLink>
        </template>

        <!-- 已登入狀態：<使用者名字> 您好 下拉選單 -->
        <div v-else class="user-dropdown-container">
          <button type="button" class="user-dropdown-btn">
            <span class="user-avatar-mini">{{ userInitial }}</span>
            <span class="user-greeting-text">{{ displayName }} 您好</span>
            <span class="dropdown-arrow">▾</span>
          </button>

          <div class="user-dropdown-menu">
            <div class="dropdown-header">
              <div class="dropdown-user-name">{{ displayName }}</div>
            </div>

            <div class="dropdown-divider"></div>

            <RouterLink v-if="!isAdminOrEmployee" to="/member" class="dropdown-item">
              <span class="dropdown-icon" aria-hidden="true">
                <svg viewBox="0 0 24 24">
                  <circle cx="12" cy="8" r="4" />
                  <path d="M4.5 20a7.5 7.5 0 0 1 15 0" />
                </svg>
              </span>
              <span>會員中心</span>
            </RouterLink>

            <RouterLink v-if="!isAdminOrEmployee" to="/member/orders" class="dropdown-item">
              <span class="dropdown-icon" aria-hidden="true">
                <svg viewBox="0 0 24 24">
                  <path d="m4 7 8-4 8 4-8 4-8-4Z" />
                  <path d="M4 7v10l8 4 8-4V7M12 11v10" />
                </svg>
              </span>
              <span>我的訂單</span>
            </RouterLink>

            <RouterLink v-if="!isAdminOrEmployee" to="/member/wishlist" class="dropdown-item">
              <span class="dropdown-icon" aria-hidden="true">
                <svg viewBox="0 0 24 24">
                  <path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21l7.7-7.5 1.1-1.1a5.5 5.5 0 0 0 0-7.8Z" />
                </svg>
              </span>
              <span>願望清單</span>
            </RouterLink>

            <RouterLink v-if="isAdminOrEmployee" to="/admin" class="dropdown-item">
              <span class="dropdown-icon" aria-hidden="true">
                <svg viewBox="0 0 24 24">
                  <circle cx="12" cy="12" r="3" />
                  <path d="M19.4 15a1.7 1.7 0 0 0 .3 1.9l.1.1-2.8 2.8-.1-.1a1.7 1.7 0 0 0-1.9-.3 1.7 1.7 0 0 0-1 1.6v.2h-4V21a1.7 1.7 0 0 0-1-1.6 1.7 1.7 0 0 0-1.9.3l-.1.1L4.2 17l.1-.1a1.7 1.7 0 0 0 .3-1.9A1.7 1.7 0 0 0 3 14H2.8v-4H3a1.7 1.7 0 0 0 1.6-1 1.7 1.7 0 0 0-.3-1.9L4.2 7 7 4.2l.1.1a1.7 1.7 0 0 0 1.9.3A1.7 1.7 0 0 0 10 3v-.2h4V3a1.7 1.7 0 0 0 1 1.6 1.7 1.7 0 0 0 1.9-.3l.1-.1L19.8 7l-.1.1a1.7 1.7 0 0 0-.3 1.9 1.7 1.7 0 0 0 1.6 1h.2v4H21a1.7 1.7 0 0 0-1.6 1Z" />
                </svg>
              </span>
              <span>管理後台</span>
            </RouterLink>

            <div class="dropdown-divider"></div>

            <RouterLink to="/logout" class="dropdown-item logout-item">
              <span class="dropdown-icon" aria-hidden="true">
                <svg viewBox="0 0 24 24">
                  <path d="M10 4H5v16h5M14 8l4 4-4 4M8 12h10" />
                </svg>
              </span>
              <span>登出</span>
            </RouterLink>
          </div>
        </div>
      </nav>
    </header>

    <main class="page-content">
      <RouterView />
    </main>

    <footer>
      <p>© 2026 Hotel Shop 星澄飯店｜All Rights Reserved</p>
    </footer>
  </div>
</template>

<style scoped>
.layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

header {
  background: #fff;
  padding: 16px 8%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  position: sticky;
  top: 0;
  z-index: 100;
}

.logo {
  display: inline-flex;
  align-items: center;
  flex-shrink: 0;
  text-decoration: none;
}

.logo img {
  display: block;
  width: 220px;
  height: auto;
}

nav {
  display: flex;
  align-items: center;
  gap: 24px;
}

nav a {
  color: #444;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: color 0.25s ease;
}

nav a:hover {
  color: #b58a46;
}

.nav-login-btn {
  background-color: #b58a46;
  color: #fff !important;
  padding: 8px 18px;
  border-radius: 20px;
  font-weight: 600;
  transition: all 0.25s ease;
}

.nav-login-btn:hover {
  background-color: #8f692f;
  transform: translateY(-1px);
}

/* ===============================
   使用者下拉選單 (<名字> 您好)
   =============================== */
.user-dropdown-container {
  position: relative;
  display: inline-block;
  padding-bottom: 4px; /* 擴大 hover 容錯區間避免閃退 */
}

.user-dropdown-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  background: #fdfbf7;
  border: 1px solid #ebd9bf;
  padding: 6px 14px 6px 8px;
  border-radius: 24px;
  cursor: pointer;
  font-family: inherit;
  font-size: 15px;
  font-weight: 600;
  color: #4a3b2a;
  transition: all 0.25s ease;
}

.user-avatar-mini {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #b58a46, #8f692f);
  color: #fff;
  font-size: 13px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
}

.user-greeting-text {
  max-width: 140px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

.dropdown-arrow {
  font-size: 11px;
  color: #9b7435;
  transition: transform 0.25s ease;
}

/* Hover 觸發樣式 */
.user-dropdown-container:hover .user-dropdown-btn {
  background-color: #faf4e8;
  border-color: #b58a46;
  box-shadow: 0 4px 12px rgba(181, 138, 70, 0.15);
}

.user-dropdown-container:hover .dropdown-arrow {
  transform: rotate(180deg);
}

.user-dropdown-container:hover .user-dropdown-menu {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

/* 下拉選單主體 */
.user-dropdown-menu {
  position: absolute;
  top: 100%;
  right: 0;
  margin-top: 6px;
  min-width: 200px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.12);
  border: 1px solid #eee7dd;
  padding: 8px 0;
  opacity: 0;
  visibility: hidden;
  transform: translateY(-8px);
  transition: all 0.25s cubic-bezier(0.16, 1, 0.3, 1);
  z-index: 1000;
}

.dropdown-header {
  padding: 12px 18px 8px;
}

.dropdown-user-name {
  font-size: 15px;
  font-weight: bold;
  color: #4a3b2a;
}

.dropdown-user-tag {
  font-size: 12px;
  color: #95691f;
  margin-top: 2px;
}

.dropdown-divider {
  height: 1px;
  background-color: #eee7dd;
  margin: 6px 0;
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 18px;
  color: #444;
  text-decoration: none;
  min-height: 44px;
  font-size: 15px;
  line-height: 1.4;
  font-weight: 500;
  transition: all 0.2s ease;
}

.dropdown-item .dropdown-icon {
  width: 20px;
  height: 20px;
  flex: 0 0 20px;
  display: grid;
  place-items: center;
}

.dropdown-item .dropdown-icon svg {
  width: 18px;
  height: 18px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.dropdown-item:hover {
  background-color: #faf6ee;
  color: #b58a46;
  padding-left: 22px;
}

.logout-item {
  color: #666;
}

.logout-item:hover {
  background-color: #fde9e7;
  color: #b3443c;
}

/* ===============================
   演示用身分切換 Toggle Button
   =============================== */
.demo-toggle-wrapper {
  display: inline-flex;
  align-items: center;
}

.demo-toggle-switch {
  position: relative;
  cursor: pointer;
  user-select: none;
  border-radius: 20px;
  background: #f3ede2;
  border: 1.5px solid #dfd3c3;
  padding: 3px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.08);
  outline: none;
}

.demo-toggle-switch:focus-visible {
  box-shadow: 0 0 0 2px #b58a46, inset 0 1px 3px rgba(0, 0, 0, 0.08);
}

.demo-toggle-switch:hover {
  border-color: #b58a46;
  box-shadow: 0 2px 8px rgba(181, 138, 70, 0.18), inset 0 1px 3px rgba(0, 0, 0, 0.08);
}

.demo-toggle-switch.is-employee {
  background: #e2e8f0;
  border-color: #cbd5e1;
}

.demo-toggle-switch.is-employee:hover {
  border-color: #475569;
  box-shadow: 0 2px 8px rgba(71, 85, 105, 0.2), inset 0 1px 3px rgba(0, 0, 0, 0.08);
}

.demo-toggle-switch.is-loading {
  opacity: 0.75;
  cursor: wait;
  pointer-events: none;
}

.toggle-track {
  position: relative;
  display: flex;
  align-items: center;
  width: 140px;
  height: 28px;
}

.toggle-option {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 600;
  z-index: 2;
  color: #7c6f62;
  transition: color 0.3s ease;
  padding: 0 4px;
}

.demo-toggle-switch.is-employee .toggle-option {
  color: #64748b;
}

.toggle-option.active {
  color: #ffffff !important;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.25);
}

.option-icon {
  font-size: 13px;
  line-height: 1;
}

.option-text {
  font-size: 13px;
  letter-spacing: 0.5px;
}

.toggle-thumb {
  position: absolute;
  top: 0;
  left: 0;
  width: 68px;
  height: 28px;
  border-radius: 15px;
  background: linear-gradient(135deg, #c59b56, #a17835);
  box-shadow: 0 2px 6px rgba(161, 120, 53, 0.35);
  transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1), background 0.3s ease, box-shadow 0.3s ease;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: center;
}

.toggle-thumb.thumb-right {
  transform: translateX(72px);
  background: linear-gradient(135deg, #334155, #1e293b);
  box-shadow: 0 2px 6px rgba(30, 41, 59, 0.35);
}

.thumb-icon {
  display: none;
}

.spinner {
  width: 14px;
  height: 14px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.6s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.page-content {
  flex: 1;
}

footer {
  background: #2f2a24;
  color: #ddd;
  text-align: center;
  padding: 24px;
  margin-top: auto;
}

@media (max-width: 768px) {
  header {
    padding: 14px 5%;
    flex-direction: column;
    gap: 12px;
  }

  nav {
    flex-wrap: wrap;
    justify-content: center;
    gap: 14px;
  }

  .logo img {
    width: 196px;
  }
}
</style>
