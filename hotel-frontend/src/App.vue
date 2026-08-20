<script setup>
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header class="topbar">
    <div class="topbar-inner">
      <strong>Hotel System</strong>

      <nav>
        <RouterLink to="/">首頁</RouterLink>
        <RouterLink to="/venues">場地管理</RouterLink>
        <RouterLink to="/rentals">租借管理</RouterLink>
        <RouterLink to="/about">關於</RouterLink>
      </nav>
    </div>
  </header>

  <RouterView />
</template>

<style scoped>
.topbar {
  border-bottom: 1px solid #e2e8f0;
  background: #0f172a;
  color: white;
}

.topbar-inner {
  width: min(1000px, calc(100% - 40px));
  min-height: 64px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

nav {
  display: flex;
  gap: 8px;
}

nav a {
  padding: 8px 12px;
  border-radius: 8px;
  color: #cbd5e1;
  text-decoration: none;
}

nav a.router-link-active {
  background: #334155;
  color: white;
}
</style>
