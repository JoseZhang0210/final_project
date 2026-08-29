<script setup>
import { computed, ref } from "vue";
import { storeToRefs } from "pinia";
import { useAuthStore } from "@/stores/auth";

const authStore = useAuthStore();
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
</script>

<template>
  <div class="layout">
    <header>
      <RouterLink to="/" class="logo">星澄飯店</RouterLink>

      <nav>
        <RouterLink to="/"> 首頁 </RouterLink>

        <RouterLink to="/products"> 商城商品 </RouterLink>

        <RouterLink to="/room-booking"> 訂房管理 </RouterLink>

        <RouterLink to="/restaurant-menu"> 餐廳 </RouterLink>

        <RouterLink to="/cart">
          購物車 <span v-if="cartCount > 0"> ({{ cartCount }})</span>
        </RouterLink>

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

            <RouterLink to="/member" class="dropdown-item">
              <span class="dropdown-icon">👤</span>
              <span>會員中心</span>
            </RouterLink>

            <RouterLink to="/member/orders" class="dropdown-item">
              <span class="dropdown-icon">📦</span>
              <span>我的訂單</span>
            </RouterLink>

            <RouterLink v-if="isAdminOrEmployee" to="/admin" class="dropdown-item">
              <span class="dropdown-icon">⚙️</span>
              <span>管理後台</span>
            </RouterLink>

            <div class="dropdown-divider"></div>

            <RouterLink to="/logout" class="dropdown-item logout-item">
              <span class="dropdown-icon">🚪</span>
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
  position: relative;
  z-index: 100;
}

.logo {
  font-size: 26px;
  font-weight: bold;
  color: #9b7435;
  text-decoration: none;
  letter-spacing: 1.5px;
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
  font-size: 14px;
  font-weight: 500;
  transition: all 0.2s ease;
}

.dropdown-item .dropdown-icon {
  font-size: 16px;
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
}
</style>
