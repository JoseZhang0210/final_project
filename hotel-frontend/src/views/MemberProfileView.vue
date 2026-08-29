<template>
  <div class="member-profile-view">
    <div class="profile-card">
      <div class="card-header">
        <h2>👤 個人資料</h2>
        <span class="status-badge active">帳號狀態：正常</span>
      </div>

      <div class="card-body">
        <div class="profile-info-grid">
          <div class="info-group">
            <label>會員姓名</label>
            <div class="info-value highlight">{{ authStore.name || '未設定' }}</div>
          </div>

          <div class="info-group">
            <label>登入狀態</label>
            <div class="info-value text-success">✓ 已驗證登入</div>
          </div>

          <div class="info-group">
            <label>帳號權限</label>
            <div class="info-value">
              <span v-for="auth in authStore.authorities" :key="auth" class="role-tag">
                {{ auth }}
              </span>
            </div>
          </div>
        </div>

        <div class="quick-links-section">
          <h3>快捷服務</h3>
          <div class="quick-cards-grid">
            <RouterLink to="/member/orders" class="quick-card">
              <div class="quick-icon">📦</div>
              <div class="quick-text">
                <div class="quick-title">我的訂單</div>
                <div class="quick-desc">查看商城歷史訂單及出貨進度</div>
              </div>
            </RouterLink>

            <RouterLink to="/products" class="quick-card">
              <div class="quick-icon">🛍</div>
              <div class="quick-text">
                <div class="quick-title">飯店商城</div>
                <div class="quick-desc">選購星澄嚴選商品與伴手禮</div>
              </div>
            </RouterLink>

            <RouterLink to="/cart" class="quick-card">
              <div class="quick-icon">🛒</div>
              <div class="quick-text">
                <div class="quick-title">購物車</div>
                <div class="quick-desc">檢視待結帳商品</div>
              </div>
            </RouterLink>

            <RouterLink to="/restaurant-menu" class="quick-card">
              <div class="quick-icon">🍽</div>
              <div class="quick-text">
                <div class="quick-title">餐廳美饌</div>
                <div class="quick-desc">預覽主廚精選菜單與時段</div>
              </div>
            </RouterLink>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from '@/stores/auth';

const authStore = useAuthStore();
</script>

<style scoped>
.member-profile-view {
  width: 100%;
}

.profile-card {
  background: #fff;
  border-radius: 14px;
  padding: 32px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.05);
  border: 1px solid #eee7dd;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee7dd;
  margin-bottom: 26px;
}

.card-header h2 {
  font-size: 22px;
  color: #4a3b2a;
  margin: 0;
}

.status-badge {
  font-size: 13px;
  font-weight: bold;
  padding: 6px 14px;
  border-radius: 20px;
}

.status-badge.active {
  background-color: #e5f6eb;
  color: #257641;
}

.profile-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 22px;
  margin-bottom: 36px;
}

.info-group {
  background: #fdfbf7;
  padding: 16px 20px;
  border-radius: 10px;
  border: 1px solid #f0e9df;
}

.info-group label {
  display: block;
  font-size: 13px;
  color: #887864;
  margin-bottom: 6px;
  font-weight: 600;
}

.info-value {
  font-size: 16px;
  color: #333;
  font-weight: 500;
}

.info-value.highlight {
  color: #b58a46;
  font-size: 18px;
  font-weight: bold;
}

.info-value.text-success {
  color: #257641;
}

.role-tag {
  display: inline-block;
  background: #ede6dc;
  color: #5c4d3d;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 6px;
  margin-right: 6px;
  margin-bottom: 4px;
}

.quick-links-section h3 {
  font-size: 18px;
  color: #4a3b2a;
  margin-bottom: 18px;
}

.quick-cards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
  gap: 16px;
}

.quick-card {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 18px 20px;
  background: #fff;
  border: 1px solid #eee7dd;
  border-radius: 12px;
  text-decoration: none;
  color: inherit;
  transition: all 0.25s ease;
}

.quick-card:hover {
  border-color: #b58a46;
  transform: translateY(-3px);
  box-shadow: 0 8px 18px rgba(181, 138, 70, 0.12);
}

.quick-icon {
  font-size: 28px;
  width: 46px;
  height: 46px;
  background: #faf6ee;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.quick-title {
  font-size: 15px;
  font-weight: bold;
  color: #4a3b2a;
  margin-bottom: 3px;
}

.quick-desc {
  font-size: 12px;
  color: #888;
  line-height: 1.4;
}
</style>

