<script setup>
import { useRouter } from "vue-router";

const router = useRouter();

async function logout() {
    try {
    const response = await fetch("/api/logout", {
        method: "POST",
        credentials: "include",
    });

    if (response.ok) {
        router.push("/login");
    } else {
        alert("登出失敗");
    }
    } catch (error) {
    console.error(error);

    alert("登出失敗");
    }
}
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

        <RouterLink to="/register"> 註冊 </RouterLink>

        <RouterLink to="/login"> 登入 </RouterLink>

        <button type="button" class="logout-button" @click="logout">
          登出
        </button>
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

