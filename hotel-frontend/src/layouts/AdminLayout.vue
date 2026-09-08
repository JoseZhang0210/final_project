<template>
  <div class="admin-layout">
    <!-- =========================
         左側選單
         ========================= -->
    <aside class="sidebar">
      <div class="sidebar-logo">
        <div class="logo-title">星澄飯店</div>
        <span class="logo-sub">管理後台</span>
      </div>

      <nav class="sidebar-nav">
        <!-- Dashboard -->
        <RouterLink to="/admin" class="nav-item">
          <span class="nav-icon">📊</span>
          <span class="nav-text">Dashboard</span>
        </RouterLink>

        <!-- 商品管理 -->
        <RouterLink to="/admin/products" class="nav-item">
          <span class="nav-icon">🛍️</span>
          <span class="nav-text">商品管理</span>
        </RouterLink>

        <!-- 餐廳管理群組 -->
        <div class="sidebar-group" :class="{ 'is-open': restaurantOpen }">
          <button type="button" class="sidebar-group-title" @click="restaurantOpen = !restaurantOpen">
            <span class="group-title-content">
              <span class="nav-icon">🍽️</span>
              <span class="nav-text">餐廳管理</span>
            </span>
            <span class="arrow-icon">▼</span>
          </button>

          <div v-show="restaurantOpen" class="sidebar-submenu">
            <RouterLink to="/admin/restaurants">餐廳資料管理</RouterLink>
            <RouterLink to="/admin/restaurant-times">餐廳時段管理</RouterLink>
            <RouterLink to="/admin/reservations">餐廳訂位管理</RouterLink>
          </div>
        </div>

        <!-- 訂房管理群組 -->
        <div class="sidebar-group" :class="{ 'is-open': roombookingOpen }">
          <button type="button" class="sidebar-group-title" @click="roombookingOpen = !roombookingOpen">
            <span class="group-title-content">
              <span class="nav-icon">🛏️</span>
              <span class="nav-text">訂房管理</span>
            </span>
            <span class="arrow-icon">▼</span>
          </button>

          <div v-show="roombookingOpen" class="sidebar-submenu">
            <RouterLink to="/admin/room-booking">訂房明細</RouterLink>
            <RouterLink to="/admin/room-status">房間管理</RouterLink>
            <RouterLink to="/admin/room-task">房務工單</RouterLink>
            <RouterLink to="/admin/room-types">房間類型</RouterLink>
            <RouterLink to="/admin/room-images">房型圖片</RouterLink>
            <RouterLink to="/admin/booking-payments">付款紀錄</RouterLink>
          </div>
        </div>

        <!-- 會員管理 -->
        <RouterLink to="/admin/members" class="nav-item">
          <span class="nav-icon">👤</span>
          <span class="nav-text">會員管理</span>
        </RouterLink>

        <!-- 員工管理 -->
        <RouterLink to="/admin/employees" class="nav-item">
          <span class="nav-icon">🧑‍💼</span>
          <span class="nav-text">員工管理</span>
        </RouterLink>

        <!-- 訂單管理 -->
        <RouterLink to="/admin/orders" class="nav-item">
          <span class="nav-icon">📦</span>
          <span class="nav-text">訂單管理</span>
        </RouterLink>

        <!-- 優惠券管理 -->
        <RouterLink to="/admin/coupons" class="nav-item">
          <span class="nav-icon">🎟️</span>
          <span class="nav-text">優惠券管理</span>
        </RouterLink>

        <!-- 場地管理 -->
        <RouterLink to="/admin/venues" class="nav-item">
          <span class="nav-icon">🏛️</span>
          <span class="nav-text">場地管理</span>
        </RouterLink>

        <!-- 場地租借管理 -->
        <RouterLink to="/admin/rental" class="nav-item">
          <span class="nav-icon">📝</span>
          <span class="nav-text">場地租借管理</span>
        </RouterLink>
      </nav>

      <!-- =========================
           Sidebar 底部
           ========================= -->
      <div class="sidebar-bottom">
        <RouterLink to="/" class="back-home-btn">
          <span class="nav-icon">←</span>
          <span>回前台首頁</span>
        </RouterLink>
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
