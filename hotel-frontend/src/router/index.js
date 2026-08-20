import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/auth'; // 💡 匯入您的 Pinia Store

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    // 🚪 登入頁面路由（新增）
    {
      path: "/login",
      name: "Login",
      component: () => import("@/views/LoginView.vue"), // 💡 對應您的無樣式純邏輯版 LoginView
    },

    // 🖥️ 管理員布局群組
    {
      path: "/admin",
      component: () => import("@/layouts/EmployeeLayout.vue"),
      meta: { requiresAuth: true, role: 'ROLE_EMPLOYEE' }, // 💡 標記此群組需要員工權限
      children: [
        // 會員管理畫面
        {
          path: "member",
          component: () => import("@/views/admin/MemberManagerView.vue"),
        },
        // 商品管理畫面
        {
          path: "product",
          component: () => import("@/views/admin/ProductManagerView.vue"),
        },
      ],
    },

    // 🔑 使用者布局群組
    {
      path: "/",
      component: () => import("@/layouts/MemberLayout.vue"),
      meta: { requiresAuth: true, role: 'ROLE_MEMBER' }, // 💡 標記此群組需要會員權限
    },

    // 找不到 404
    {
      path: "/:pathMatch(.*)*",
      component: () => import("@/views/NotFound.vue"),
    },
  ],
});

/**
 * 💡 門禁警衛：全局路由守衛 (beforeEach)
 * 負責在每次切換網頁時，抽查 Token 與大分類身分標籤 [INDEX]
 */
router.beforeEach((to, from, next) => {

  // 測試用，放行所有role
  return next();

  const authStore = useAuthStore();
  
  // 1. 檢查目標頁面是不是「需要登入」的受保護區域 (meta.requiresAuth)
  if (to.matched.some(record => record.meta.requiresAuth)) {
    
    // 如果沒登入（沒有 Token），直接強制攔截並踢回登入頁
    if (!authStore.token) {
      return next('/login');
    }
    
    // 2. 🧠 核心：檢查目標群組要求的角色 (ROLE_EMPLOYEE 或 ROLE_MEMBER)
    const requiredRole = to.matched.find(record => record.meta.role)?.meta.role;
    
    if (requiredRole) {
      // 翻查 Pinia 當前儲存的 authorities 陣列
      const hasRole = authStore.authorities.includes(requiredRole);
      
      if (!hasRole) {
        alert('您的帳號權限不足，無法進入此區塊！');
        
        // 防呆分流：如果會員想強闖後台，強制把他退回會員首頁（/）；如果是員工，送回後台首頁（/admin/member）
        return next(authStore.isEmployee ? '/admin/member' : '/');
      }
    }
  } else {
    // 3. 額外防呆：如果使用者「已經登入成功」，卻還想手動打網址跑回 /login 頁面
    if (to.path === '/login' && authStore.token) {
      // 直接幫他過濾跳轉到他該去的 Layout 畫面 [INDEX]
      return next(authStore.isEmployee ? '/admin/member' : '/');
    }
  }
  
  // 驗證無誤，大方放行
  next();
});

export default router;
