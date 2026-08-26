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
        <div class="sidebar-group">
          <button type="button" class="sidebar-group-title" @click="roombookingOpen = !roombookingOpen">
            <span> 🛏 訂房管理 </span>

            <span class="arrow">
              {{ roombookingOpen ? "▲" : "▼" }}
            </span>
          </button>

          <div v-show="roombookingOpen" class="sidebar-submenu">
            <RouterLink to="/admin/room-booking-order"> 預定訂房 </RouterLink>
            <RouterLink to="/admin/room-booking">訂房明細</RouterLink>
            <RouterLink to="/admin/room-status"> 房間狀態 </RouterLink>
            <RouterLink to="/admin/room-task">房務工單</RouterLink>
            <RouterLink to="/admin/room-types">房間類型</RouterLink>
            <RouterLink to="/admin/room-images">房型圖片</RouterLink>
          </div>
        </div>
        <!-- ＝＝＝＝＝＝＝＝＝＝＝＝＝＝ -->

        <!-- 會員管理 -->
        <RouterLink to="/admin/members"> 👤 會員管理 </RouterLink>

        <!-- 員工管理 -->
        <RouterLink to="/admin/employees"> 🧑‍💼 員工管理 </RouterLink>

        <!-- 場地管理 -->
        <RouterLink to="/admin/venues"> 🏛️場地管理 </RouterLink>

        <!-- 場地租借管理 -->
        <RouterLink to="/admin/rental"> 📝場地租借管理 </RouterLink>
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
const roombookingOpen = ref(true);
</script>

<style></style>
