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
        <RouterLink to="/admin/products"> 🛍 商品管理 </RouterLink>

        <!-- =========================
             餐廳管理群組
             ========================= -->
        <div class="sidebar-group">
          <button
            type="button"
            class="sidebar-group-title"
            @click="restaurantOpen = !restaurantOpen"
          >
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

        <!-- 會員管理 -->
        <RouterLink to="/admin/members"> 👤 會員管理 </RouterLink>
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

        <div class="admin-user">管理員</div>
      </header>

      <!-- 子頁面 -->
      <main class="admin-content">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";

/*
 * 控制餐廳管理選單展開 / 收合
 *
 * true  = 預設展開
 * false = 預設收合
 */
const restaurantOpen = ref(true);
</script>

<style></style>
