<template>
  <div>
    <div class="dashboard-title">
      <div>
        <h1>Dashboard</h1>
        <p>歡迎使用星澄飯店後台管理系統</p>
      </div>
    </div>

    <div class="stat-grid">
      <div class="stat-card">
        <div class="stat-icon">🛍</div>

        <div>
          <span>商品數量</span>
          <strong>{{ productCount }}</strong>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">🍽</div>

        <div>
          <span>餐廳數量</span>
          <strong>{{ restaurantCount }}</strong>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">📅</div>

        <div>
          <span>餐廳訂位</span>
          <strong>{{ reservationCount }}</strong>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">👤</div>

        <div>
          <span>會員人數</span>
          <strong>{{ memberCount }}</strong>
        </div>
      </div>
    </div>

    <div class="dashboard-grid">
      <section class="dashboard-card">
        <div class="card-title">
          <h2>快速管理</h2>
        </div>

        <div class="quick-grid">
          <RouterLink to="/admin/products" class="quick-item">
            🛍
            <span>商品管理</span>
          </RouterLink>

          <RouterLink to="/admin/restaurants" class="quick-item">
            🍽
            <span>餐廳管理</span>
          </RouterLink>

          <RouterLink to="/admin/restaurant-times" class="quick-item">
            🕒
            <span>時段管理</span>
          </RouterLink>

          <RouterLink to="/admin/reservations" class="quick-item">
            📅
            <span>訂位管理</span>
          </RouterLink>
        </div>
      </section>

      <section class="dashboard-card">
        <div class="card-title">
          <h2>系統資訊</h2>
        </div>

        <div class="system-info">
          <p>
            系統名稱
            <span>星澄飯店管理系統</span>
          </p>

          <p>
            後端服務
            <span>Spring Boot</span>
          </p>

          <p>
            前端框架
            <span>Vue 3</span>
          </p>

          <p>
            資料庫
            <span>SQL Server</span>
          </p>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';

// =====================================================
// 統計數字
// =====================================================

const productCount = ref('—');
const restaurantCount = ref('—');
const reservationCount = ref('—');
const memberCount = ref('—');

// =====================================================
// JWT Header
// =====================================================

function getAuthHeaders() {
  const token = localStorage.getItem('token');
  const headers = { 'Content-Type': 'application/json' };
  if (token) {
    headers.Authorization = 'Bearer ' + token;
  }
  return headers;
}

// =====================================================
// 取得單一統計數字 (從陣列長度)
// =====================================================

async function fetchCount(url) {
  try {
    const response = await fetch(url, {
      method: 'GET',
      headers: getAuthHeaders(),
    });
    if (!response.ok) return '—';
    const data = await response.json();
    return Array.isArray(data) ? data.length : (data.total ?? data.count ?? '—');
  } catch {
    return '—';
  }
}

// =====================================================
// 初始化：一次呼叫四支 API
// =====================================================

onMounted(async () => {
  const [p, r, res, m] = await Promise.all([
    fetchCount('/api/products'),
    fetchCount('/api/restaurant'),
    fetchCount('/api/reservations'),
    fetchCount('/api/members'),
  ]);
  productCount.value = p;
  restaurantCount.value = r;
  reservationCount.value = res;
  memberCount.value = m;
});
</script>

<style scoped>
.dashboard-title {
  margin-bottom: 28px;
}

.dashboard-title h1 {
  color: #4a3b2a;

  font-size: 30px;

  margin-bottom: 6px;
}

.dashboard-title p {
  color: #777;
}

.stat-grid {
  display: grid;

  grid-template-columns: repeat(4, 1fr);

  gap: 20px;

  margin-bottom: 28px;
}

.stat-card {
  background: white;

  padding: 24px;

  border-radius: 14px;

  display: flex;
  align-items: center;

  gap: 18px;

  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.06);
}

.stat-icon {
  width: 58px;
  height: 58px;

  border-radius: 12px;

  display: flex;
  justify-content: center;
  align-items: center;

  background: #f3eadc;

  font-size: 28px;
}

.stat-card span {
  color: #777;

  font-size: 14px;
}

.stat-card strong {
  display: block;

  margin-top: 5px;

  color: #6f5328;

  font-size: 28px;
}

.dashboard-grid {
  display: grid;

  grid-template-columns: 2fr 1fr;

  gap: 24px;
}

.dashboard-card {
  background: white;

  padding: 26px;

  border-radius: 14px;

  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.06);
}

.card-title h2 {
  color: #6f5328;

  font-size: 21px;

  margin-bottom: 20px;
}

.quick-grid {
  display: grid;

  grid-template-columns: repeat(2, 1fr);

  gap: 15px;
}

.quick-item {
  padding: 24px;

  border-radius: 12px;

  background: #faf7f2;

  border: 1px solid #eee4d7;

  text-decoration: none;

  color: #4a3b2a;

  display: flex;
  flex-direction: column;

  align-items: center;

  gap: 10px;

  font-size: 25px;

  transition: 0.25s;
}

.quick-item span {
  font-size: 15px;

  font-weight: bold;
}

.quick-item:hover {
  border-color: #b58a46;

  transform: translateY(-3px);
}

.system-info p {
  display: flex;

  justify-content: space-between;

  padding: 13px 0;

  border-bottom: 1px solid #eee7dd;

  color: #777;
}

.system-info span {
  color: #4a3b2a;

  font-weight: bold;
}

@media (max-width: 1000px) {
  .stat-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .dashboard-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .stat-grid {
    grid-template-columns: 1fr;
  }

  .quick-grid {
    grid-template-columns: 1fr;
  }
}
</style>
