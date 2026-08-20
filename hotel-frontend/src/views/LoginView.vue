<template>
    <div>
        <h2>飯店管理系統 - 登入</h2>

        <!-- 登入表單 -->
        <form @submit.prevent="handleSubmit">
            <div>
                <label for="username">帳號：</label>
                <input id="username" v-model.trim="username" type="text" placeholder="請輸入您的帳號" required
                    :disabled="isLoading" />
            </div>

            <div>
                <label for="password">密碼：</label>
                <input id="password" v-model="password" type="password" placeholder="請輸入您的密碼" required
                    :disabled="isLoading" />
            </div>

            <!-- 錯誤訊息提示區 -->
            <div v-if="errorMessage" style="color: red;">
                ⚠️ {{ errorMessage }}
            </div>

            <!-- 登入按鈕 -->
            <button type="submit" :disabled="isLoading">
                <span v-if="isLoading">安全登入中...</span>
                <span v-else>登入系統</span>
            </button>
        </form>
    </div>
</template>

<script setup>
import { ref } from 'vue';
import { useAuthStore } from '@/stores/auth'; // 💡 匯入您的 Setup Store
import { useRouter } from 'vue-router';

// 宣告響應式狀態
const username = ref('');
const password = ref('');
const isLoading = ref(false);
const errorMessage = ref('');

const authStore = useAuthStore();
const router = useRouter();

// 處理表單送出
const handleSubmit = async () => {
    isLoading.value = true;
    errorMessage.value = '';

    try {
        // 💡 呼叫 Pinia Store 的 Action 進行後端 HTTP 請求
        const success = await authStore.login(username.value, password.value);

        if (success) {
            // 💡 完美對齊您最新版 router 的路徑設計進行分流跳轉
            if (authStore.isEmployee) {
                router.push('/admin/member'); // 員工進入後台，預設導向會員管理畫面
            } else if (authStore.isMember) {
                router.push('/');              // 客戶/會員進入前台，導向根路徑外殼
            } else {
                errorMessage.value = '登入成功，但查無有效身分角色標籤，請聯絡系統管理員。';
                authStore.logout();
            }
        }
    } catch (error) {
        // 捕捉後端拋出的 BadCredentialsException 等異常狀態碼
        if (error.response && error.response.status === 401) {
            errorMessage.value = '帳號或密碼輸入錯誤，請重新確認。';
        } else if (error.response && error.response.status === 403) {
            errorMessage.value = '此帳號目前已被停用 (Status=0)，無法登入。';
        } else {
            errorMessage.value = '伺服器連線異常，請稍後再試。';
        }
        console.error('Login process error:', error);
    } finally {
        isLoading.value = false;
    }
};
</script>
