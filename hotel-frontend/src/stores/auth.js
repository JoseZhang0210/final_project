import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAuthStore = defineStore('auth', () => {
    const isLoggedIn = ref(!!localStorage.getItem('token'))
    const authorities = ref(
        localStorage.getItem('authorities')
            ? JSON.parse(localStorage.getItem('authorities'))
            : []
    )
    const name = ref(localStorage.getItem('name') || '')

    // 登入成功時呼叫
    function login(token, userAuthorities, userName) {
        // 確保傳進來的 userAuthorities 是一組陣列（防呆）
        const authArray = Array.isArray(userAuthorities) ? userAuthorities : [userAuthorities];

        // 儲存 JWT 字串
        localStorage.setItem('token', token)
        
        // 儲存權限：必須用 JSON.stringify() 把陣列轉成標準 JSON 字串
        localStorage.setItem('authorities', JSON.stringify(authArray))

        // 儲存使用者姓名
        const displayName = userName || ''
        localStorage.setItem('name', displayName)
        
        // 更新 Pinia 狀態
        isLoggedIn.value = true
        authorities.value = authArray // 直接賦值陣列，千萬不要用 JSON.parse()
        name.value = displayName
        
        console.log("Pinia 權限與使用者資訊更新成功：", { authorities: authorities.value, name: name.value });
    }

    // 登出時呼叫
    function logout() {
        // 清除 JWT
        localStorage.removeItem("token");
        // 清除角色 / 權限
        localStorage.removeItem("authorities");
        // 清除姓名
        localStorage.removeItem("name");

        isLoggedIn.value = false
        authorities.value = []
        name.value = ''
        console.log("已登出，JWT 已清除");
    }

    return { isLoggedIn, authorities, name, login, logout }
})
