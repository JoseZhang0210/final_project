<template>
    <v-layout>
        <!-- 側邊導覽列 -->
        <v-navigation-drawer v-model="drawer">
            <!-- 💡 綁定動態判斷後的 menuItems -->
            <v-list density="compact" item-props :items="menuItems" nav />

            <template #append>
                <v-list-item class="ma-2" link nav prepend-icon="mdi-cog-outline" title="Settings" />
            </template>
        </v-navigation-drawer>

        <!-- 上方工具列 -->
        <v-app-bar border="b" class="ps-4" flat>
            <v-app-bar-nav-icon v-if="$vuetify.display.smAndDown" @click="drawer = !drawer" />

            <v-app-bar-title>
                {{ authStore.isEmployee ? '飯店後台管理系統' : '飯店會員中心' }}
            </v-app-bar-title>

            <template #append>
                <v-btn class="text-none me-2" height="48" icon slim>
                    <v-avatar color="surface-light" image="https://vuetifyjs.com" size="32" />

                    <v-menu activator="parent">
                        <v-list density="compact" nav>
                            <v-list-item append-icon="mdi-cog-outline" link title="Settings" />

                            <v-list-item append-icon="mdi-logout" link title="Logout" @click="handleLogout" />
                        </v-list>
                    </v-menu>
                </v-btn>
            </template>
        </v-app-bar>

        <!-- 內層子頁面渲染區（MemberManagerView 或 ProductManagerView 會渲染在這裡） -->
        <v-main>
            <router-view></router-view>
        </v-main>
    </v-layout>
</template>

<script setup>
import { ref, computed } from 'vue';
import { useAuthStore } from '@/stores/auth';
import { useRouter } from 'vue-router';

const authStore = useAuthStore();
const router = useRouter();

// 控管側邊欄開關狀態
const drawer = ref(true);

/**
 * 💡 核心修正：完美對齊您的真實 router 網址路徑
 */
const menuItems = computed(() => {
    // 🖥️ 1. 如果登入者是全體員工 (EMPLOYEE)，顯示後台管理專用選單
    if (authStore.isEmployee) {
        return [
            // 💡 to 屬性精準對應您 router 中的 /admin/member 與 /admin/product
            { title: '會員管理', prependIcon: 'mdi-account-multiple-outline', to: '/admin/member' },
            { title: '商品管理', prependIcon: 'mdi-package-variant-closed', to: '/admin/product' }
        ];
    }

    // 🔑 2. 如果是客戶會員 (MEMBER)，顯示前台基本選單
    if (authStore.isMember) {
        return [
            { title: '修改個人檔案', prependIcon: 'mdi-account-circle-outline', to: '/profile/me' }
        ];
    }

    // 防呆：查無身分
    return [{ title: '請先登入', prependIcon: 'mdi-lock-outline', to: '/login' }];
});

// 🚪 執行登出動作
const handleLogout = () => {
    authStore.logout(); // 清除 Pinia 狀態與 localStorage [INDEX]
    router.push('/login'); // 強制推回登入頁面
};
</script>
