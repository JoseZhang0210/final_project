<template>
  <div class="admin-layout">
    <!-- =========================
         左側選單
         ========================= -->
    <aside class="sidebar">
      <div class="sidebar-logo">
        星澄飯店

        <span> 管理後台 </span>
      </div>

      <nav class="sidebar-nav">
        <!-- Dashboard -->
        <RouterLink to="/admin"> 📊 Dashboard </RouterLink>

        <!-- 商品管理 -->
        <RouterLink v-if="authStore.hasPermission('PRODUCT_MANAGE')" to="/admin/products"> 🛍 商品管理 </RouterLink>

        <!-- =========================
             餐廳管理群組
             ========================= -->
        <div v-if="authStore.hasPermission('RESTAURANT_MANAGE')" class="sidebar-group">
          <button type="button" class="sidebar-group-title" @click="restaurantOpen = !restaurantOpen">
            <span> 🍽 餐廳管理 </span>

            <span class="arrow">
              {{ restaurantOpen ? "▲" : "▼" }}
            </span>
          </button>

          <div v-show="restaurantOpen" class="sidebar-submenu">
            <RouterLink to="/admin/restaurants"> 餐廳資料管理 </RouterLink>

            <RouterLink to="/admin/restaurant-times"> 餐廳時段管理 </RouterLink>

            <RouterLink to="/admin/reservations"> 餐廳訂位管理 </RouterLink>
          </div>
        </div>

        <!-- ＝＝＝＝＝訂房管理＝＝＝＝＝ -->
        <div v-if="authStore.hasAnyPermission(['ROOM_MANAGE', 'BOOKING_MANAGE'])" class="sidebar-group">
          <button type="button" class="sidebar-group-title" @click="roombookingOpen = !roombookingOpen">
            <span> 🛏 訂房管理 </span>

            <span class="arrow">
              {{ roombookingOpen ? "▲" : "▼" }}
            </span>
          </button>

          <div v-show="roombookingOpen" class="sidebar-submenu">
            <RouterLink v-if="authStore.hasPermission('BOOKING_MANAGE')" to="/admin/room-booking">訂房明細</RouterLink>
            <RouterLink v-if="authStore.hasPermission('ROOM_MANAGE')" to="/admin/room-status">房間管理</RouterLink>
            <RouterLink v-if="authStore.hasPermission('ROOM_MANAGE')" to="/admin/room-task">房務工單</RouterLink>
            <RouterLink v-if="authStore.hasPermission('ROOM_MANAGE')" to="/admin/room-types">房間類型</RouterLink>
            <RouterLink v-if="authStore.hasPermission('ROOM_MANAGE')" to="/admin/room-images">房型圖片</RouterLink>
            <RouterLink v-if="authStore.hasPermission('BOOKING_MANAGE')" to="/admin/booking-payments">付款紀錄</RouterLink>
          </div>
        </div>
        <!-- ＝＝＝＝＝＝＝＝＝＝＝＝＝＝ -->

        <!-- 帳號管理群組 -->
        <div v-if="authStore.hasAnyPermission(['MEMBER_MANAGE', 'EMPLOYEE_MANAGE'])" class="sidebar-group">
          <button type="button" class="sidebar-group-title" @click="accountOpen = !accountOpen">
            <span class="sidebar-title-with-icon">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                class="lucide-icon"
              >
                <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
                <circle cx="9" cy="7" r="4" />
                <path d="M22 21v-2a4 4 0 0 0-3-3.87" />
                <path d="M16 3.13a4 4 0 0 1 0 7.75" />
              </svg>
              帳號管理
            </span>

            <span class="arrow">
              {{ accountOpen ? "▲" : "▼" }}
            </span>
          </button>

          <div v-show="accountOpen" class="sidebar-submenu">
            <RouterLink v-if="authStore.hasPermission('MEMBER_MANAGE')" to="/admin/members">會員管理</RouterLink>
            <RouterLink v-if="authStore.hasPermission('EMPLOYEE_MANAGE')" to="/admin/employees">員工管理</RouterLink>
          </div>
        </div>
        
        <!-- 訂單管理 -->
        <RouterLink v-if="authStore.hasPermission('ORDER_MANAGE')" to="/admin/orders"> 📦 訂單管理 </RouterLink>

        <!-- 優惠券管理 -->
        <RouterLink v-if="authStore.hasPermission('COUPON_MANAGE')" to="/admin/coupons"> 🎟 優惠券管理 </RouterLink>

        <!-- 場地管理 -->
        <RouterLink v-if="authStore.hasPermission('VENUE_MANAGE')" to="/admin/venues"> 🏛️場地管理 </RouterLink>

        <!-- 場地租借管理 -->
        <RouterLink v-if="authStore.hasPermission('VENUE_MANAGE')" to="/admin/rental"> 📝場地租借管理 </RouterLink>
      </nav>

      <!-- =========================
           Sidebar 底部
           ========================= -->
      <div class="sidebar-bottom">
        <RouterLink to="/"> ← 回前台首頁 </RouterLink>
      </div>
    </aside>

    <!-- =========================
         右側內容
         ========================= -->
    <div class="admin-main">
      <!-- 後台 Header -->
      <header class="admin-header">
        <div>
          <h2>星澄飯店管理系統</h2>
        </div>

        <div class="admin-header-actions">
          <div class="admin-user">
            <span class="user-avatar-mini">
              <img v-if="authStore.avatarUrl" :src="authStore.avatarUrl" alt="Avatar" class="avatar-mini-img" />
              <span v-else>{{ userInitial }}</span>
            </span>
            <span class="user-greeting-text">{{ displayName }} 您好</span>
          </div>

          <RouterLink
            to="/"
            class="admin-home-button"
          >
            <svg
              class="admin-home-icon"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              aria-hidden="true"
            >
              <path d="m3 11 9-8 9 8" />
              <path d="M5 10v10h14V10" />
              <path d="M9 20v-6h6v6" />
            </svg>
            <span>回首頁</span>
          </RouterLink>
        </div>
      </header>

      <!-- 子頁面 -->
      <main class="admin-content">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from "vue";
import { useRoute } from "vue-router";
import { useAuthStore } from "@/stores/auth";

const route = useRoute();
const authStore = useAuthStore();

const displayName = computed(() => {
  return authStore.name || "管理員";
});

const userInitial = computed(() => {
  if (authStore.name && authStore.name.trim().length > 0) {
    return authStore.name.trim().charAt(0);
  }
  return "管";
});

/*
 * 控制餐廳管理選單展開 / 收合
 *
 * true  = 預設展開
 * false = 預設收合
 */
const restaurantOpen = ref(true);
const roombookingOpen = ref(true);
const accountOpen = ref(true);
</script>

<style scoped>
.sidebar-title-with-icon {
  display: flex;
  align-items: center;
  gap: 8px;
}

.lucide-icon {
  display: block;
  flex-shrink: 0;
}

.admin-header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  white-space: nowrap;
}

.admin-user {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: #fdfbf7;
  border: 1px solid #ebd9bf;
  padding: 6px 14px 6px 8px;
  border-radius: 24px;
  font-size: 14px;
  font-weight: 600;
  color: #4a3b2a;
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
  flex-shrink: 0;
  overflow: hidden;
}

.avatar-mini-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.user-greeting-text {
  max-width: 180px;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

.admin-home-button {
  min-height: 44px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 10px 17px;
  box-sizing: border-box;
  border: 1px solid #9f763b;
  border-radius: 10px;
  background: linear-gradient(135deg, #a98043 0%, #87612e 100%);
  box-shadow: 0 4px 12px rgba(111, 83, 40, 0.2);
  color: #ffffff;
  font-size: 14px;
  font-weight: 700;
  line-height: 1;
  text-decoration: none;
  transition:
    background 0.2s ease,
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}

.admin-home-button:hover {
  border-color: #795421;
  background: linear-gradient(135deg, #b68d4d 0%, #76501f 100%);
  box-shadow: 0 6px 16px rgba(111, 83, 40, 0.3);
}

.admin-home-button:active {
  background: #76501f;
  box-shadow: 0 2px 7px rgba(111, 83, 40, 0.25);
}

.admin-home-button:focus-visible {
  outline: 3px solid rgba(181, 138, 70, 0.35);
  outline-offset: 3px;
}

.admin-home-icon {
  width: 18px;
  height: 18px;
  flex: 0 0 auto;
}

@media (max-width: 600px) {
  .admin-header-actions {
    gap: 8px;
  }

  .admin-home-button {
    min-height: 40px;
    padding: 8px 10px;
  }
}
</style>
