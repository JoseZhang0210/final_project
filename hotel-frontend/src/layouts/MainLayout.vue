<script setup>
import { storeToRefs } from 'pinia';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const { isLoggedIn } = storeToRefs(authStore);

</script>
<template>
  <div class="layout">
    <header>
      <div class="logo">星澄飯店</div>

      <nav>
        <RouterLink to="/"> 首頁 </RouterLink>

        <RouterLink to="/products"> 商城商品 </RouterLink>

        <RouterLink to="/orders"> 我的訂單 </RouterLink>

        <RouterLink to="/room-booking"> 訂房管理 </RouterLink>

        <RouterLink to="/restaurant-menu"> 餐廳 </RouterLink>

        <template v-if="!isLoggedIn">
          <RouterLink to="/register"> 註冊 </RouterLink>
          <RouterLink to="/login"> 登入 </RouterLink>
        </template>

        <template v-else>
          <RouterLink to="/logout"> 登出 </RouterLink>
          <RouterLink to="/admin"> 前往後台 </RouterLink>
        </template>
      </nav>
    </header>

    <main class="page-content">
      <RouterView />
    </main>

    <footer>
      <p>© 2026 Hotel Shop 飯店商城｜All Rights Reserved</p>
    </footer>
  </div>
</template>
