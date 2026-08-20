<template>
    <div class="member-layout">
        <header class="navbar">
            <div class="logo">🏨 奢華尊榮飯店</div>
            <nav>
                <router-link to="/hotel/home">首頁</router-link>
                <router-link to="/hotel/booking">立即訂房</router-link>
                <router-link to="/profile/me">個人檔案</router-link>
            </nav>
            <button @click="handleLogout" class="btn-logout">登出</button>
        </header>
        <!-- 💡 會員子頁面渲染區 -->
        <main class="container">
            <router-view />
        </main>
    </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

const handleLogout = () => {
    authStore.logout();
    router.push('/login');
};
</script>