<template>
  <div class="logout-page">
    <div class="logout-container">
      <div class="logout-card">
        <div class="hotel-name">星澄飯店</div>

        <div class="logout-icon">✓</div>

        <h1>您已成功登出</h1>

        <div class="divider"></div>

        <p class="message">
          感謝您使用星澄飯店服務， 期待再次為您提供舒適與美好的體驗。
        </p>

        <div class="button-group">
          <RouterLink to="/" class="btn btn-home"> 回到首頁 </RouterLink>

          <RouterLink to="/login" class="btn btn-login"> 重新登入 </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from "vue";

onMounted(() => {
  // ==============================
  // 清除 JWT
  // ==============================

  localStorage.removeItem("token");

  // ==============================
  // 清除角色 / 權限
  // ==============================

  localStorage.removeItem("authorities");

  console.log("已登出，JWT 已清除");
});
</script>

<style scoped>
.logout-page {
  min-height: 100vh;

  font-family: Arial, "Microsoft JhengHei", sans-serif;

  background:
    linear-gradient(rgba(0, 0, 0, 0.45), rgba(0, 0, 0, 0.45)),
    url("https://images.unsplash.com/photo-1566073771259-6a8506099945") center /
      cover no-repeat;

  display: flex;

  justify-content: center;

  align-items: center;

  padding: 30px;
}

.logout-container {
  width: 100%;

  max-width: 430px;
}

.logout-card {
  background-color: rgba(255, 255, 255, 0.96);

  padding: 42px 36px;

  border-radius: 16px;

  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.22);

  text-align: center;
}

.hotel-name {
  color: #9b7435;

  font-size: 26px;

  font-weight: bold;

  letter-spacing: 2px;

  margin-bottom: 22px;
}

.logout-icon {
  width: 70px;

  height: 70px;

  margin: 0 auto 22px;

  display: flex;

  justify-content: center;

  align-items: center;

  border-radius: 50%;

  background-color: #f3eadc;

  color: #9b7435;

  font-size: 32px;
}

h1 {
  color: #4a3b2a;

  font-size: 27px;

  margin-bottom: 14px;
}

.message {
  color: #777;

  font-size: 15px;

  line-height: 1.7;

  margin-bottom: 28px;
}

.button-group {
  display: flex;

  gap: 12px;
}

.btn {
  flex: 1;

  display: inline-flex;

  justify-content: center;

  align-items: center;

  padding: 12px 18px;

  border-radius: 8px;

  font-size: 15px;

  font-weight: bold;

  text-decoration: none;

  transition: 0.25s;
}

.btn-home {
  background-color: #b58a46;

  color: white;
}

.btn-home:hover {
  background-color: #8f692f;

  transform: translateY(-2px);
}

.btn-login {
  background-color: #eee9e1;

  color: #5c4d3d;
}

.btn-login:hover {
  background-color: #dfd5c7;

  transform: translateY(-2px);
}

.divider {
  width: 50px;

  height: 2px;

  margin: 0 auto 22px;

  background-color: #d5b06f;
}

@media (max-width: 520px) {
  .logout-page {
    padding: 18px;
  }

  .logout-card {
    padding: 34px 24px;
  }

  .button-group {
    flex-direction: column;
  }
}
</style>
