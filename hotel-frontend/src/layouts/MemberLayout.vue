<template>
  <div class="member-layout-wrapper">
    <!-- 會員中心 Banner -->
    <div class="member-hero">
      <div class="hero-content">
        <h1>會員中心</h1>
        <p>歡迎光臨星澄飯店會員專區，在此管理您的個人資訊與訂單紀錄</p>
      </div>
    </div>

    <!-- 會員中心主區域：側邊欄 + 子路由內容 -->
    <div class="member-container">
      <aside class="member-sidebar">
        <div class="member-profile-summary">
          <div class="avatar-circle">
            {{ userInitial }}
          </div>
          <div class="user-info">
            <div class="greeting-name">{{ displayName }}</div>
            <div class="user-role-badge">星澄貴賓會員</div>
          </div>
        </div>

        <nav class="member-nav">
          <RouterLink to="/member/profile" class="nav-item" active-class="active">
            <span class="icon">👤</span>
            <span>個人資料</span>
          </RouterLink>

          <RouterLink to="/member/orders" class="nav-item" active-class="active">
            <span class="icon">📦</span>
            <span>我的訂單</span>
          </RouterLink>

          <RouterLink to="/products" class="nav-item">
            <span class="icon">🛍</span>
            <span>飯店商城</span>
          </RouterLink>

          <RouterLink to="/logout" class="nav-item logout-item">
            <span class="icon">🚪</span>
            <span>登出帳號</span>
          </RouterLink>
        </nav>
      </aside>

      <main class="member-main-content">
        <RouterView />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue';
import { storeToRefs } from 'pinia';
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
const { name } = storeToRefs(authStore);

const displayName = computed(() => {
  return name.value || '貴賓';
});

const userInitial = computed(() => {
  if (name.value && name.value.trim().length > 0) {
    return name.value.trim().charAt(0);
  }
  return '客';
});
</script>

<style scoped>
.member-layout-wrapper {
  background-color: #f8f6f1;
  min-height: calc(100vh - 160px);
  padding-bottom: 60px;
}

.member-hero {
  background: linear-gradient(rgba(47, 42, 36, 0.75), rgba(47, 42, 36, 0.75)),
              url('https://images.unsplash.com/photo-1566073771259-6a8506099945') center/cover no-repeat;
  color: #fff;
  text-align: center;
  padding: 46px 20px;
  margin-bottom: 36px;
}

.member-hero h1 {
  font-size: 34px;
  letter-spacing: 2px;
  margin-bottom: 10px;
  color: #fff;
}

.member-hero p {
  font-size: 15px;
  color: #e5dec9;
  max-width: 600px;
  margin: 0 auto;
}

.member-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  gap: 28px;
  align-items: flex-start;
}

/* 側邊欄 */
.member-sidebar {
  width: 280px;
  background: #fff;
  border-radius: 14px;
  padding: 24px 20px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.05);
  border: 1px solid #eee7dd;
  flex-shrink: 0;
}

.member-profile-summary {
  display: flex;
  align-items: center;
  gap: 14px;
  padding-bottom: 20px;
  margin-bottom: 20px;
  border-bottom: 1px solid #eee7dd;
}

.avatar-circle {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  background: linear-gradient(135deg, #b58a46 0%, #8f692f 100%);
  color: #fff;
  font-size: 22px;
  font-weight: bold;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(181, 138, 70, 0.3);
}

.user-info {
  overflow: hidden;
}

.greeting-name {
  font-size: 17px;
  font-weight: bold;
  color: #4a3b2a;
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

.user-role-badge {
  display: inline-block;
  margin-top: 4px;
  font-size: 12px;
  color: #95691f;
  background-color: #fff3d8;
  padding: 2px 8px;
  border-radius: 10px;
}

.member-nav {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  border-radius: 8px;
  color: #555;
  text-decoration: none;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.25s ease;
}

.nav-item .icon {
  font-size: 18px;
}

.nav-item:hover {
  background-color: #faf6ee;
  color: #b58a46;
  transform: translateX(4px);
}

.nav-item.active {
  background-color: #b58a46;
  color: #fff;
}

.logout-item:hover {
  background-color: #fde9e7;
  color: #b3443c;
}

/* 主內容區 */
.member-main-content {
  flex: 1;
  min-width: 0;
}

@media (max-width: 860px) {
  .member-container {
    flex-direction: column;
  }

  .member-sidebar {
    width: 100%;
  }

  .member-nav {
    flex-direction: row;
    flex-wrap: wrap;
  }

  .nav-item {
    flex: 1 1 calc(50% - 8px);
    justify-content: center;
  }
}
</style>

