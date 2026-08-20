import { defineStore } from 'pinia';
import { ref, computed } from 'vue'; // 💡 引入 ref 與 computed
import axios from 'axios';

export const useAuthStore = defineStore('auth', () => {
  // === State (用 ref 定義) ===
  const token = ref(localStorage.getItem('token') || null);
  const authorities = ref(JSON.parse(localStorage.getItem('authorities')) || []);

  // === Getters (用 computed 定義) ===
  // 💡 大分類標籤：一秒判斷 Layout 外殼
  const isEmployee = computed(() => authorities.value.includes('ROLE_EMPLOYEE'));
  const isMember = computed(() => authorities.value.includes('ROLE_MEMBER'));
  
  // 💡 按鈕級別控制：回傳一個函式，判斷有沒有特定特權
  const hasPermission = computed(() => {
    return (permission) => authorities.value.includes(permission);
  });

  // === Actions (用普通的 function 定義) ===
  // 🚪 登入
  const login = async (username, password) => {
    try {
      const response = await axios.post('/api/auth/login', { username, password });
      
      // 更新 ref 的值
      token.value = response.data.token;
      authorities.value = response.data.authorities || [];
      
      // 同步到瀏覽器快取
      localStorage.setItem('token', token.value);
      localStorage.setItem('authorities', JSON.stringify(authorities.value));
      
      return true;
    } catch (error) {
      console.error('登入失敗：', error);
      throw error;
    }
  };

  // 🚪 登出
  const logout = () => {
    token.value = null;
    authorities.value = [];
    localStorage.removeItem('token');
    localStorage.removeItem('authorities');
  };

  // 💡 關鍵：必須把外頁面需要用到的變數和方法 return 出去
  return {
    token,
    authorities,
    isEmployee,
    isMember,
    hasPermission,
    login,
    logout
  };
});
