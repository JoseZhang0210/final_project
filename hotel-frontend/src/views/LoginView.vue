<template>
  <div class="login-page">
    <div class="login-container">
      <div class="login-card">
        <div class="hotel-name">星澄飯店</div>

        <h1>會員登入</h1>

        <p class="subtitle">登入您的會員帳號， 使用星澄飯店商城與會員服務。</p>

        <form @submit.prevent="login">
          <div class="form-group">
            <label for="username"> 帳號 </label>

            <input
              id="username"
              v-model="username"
              type="text"
              placeholder="請輸入帳號"
              required
            />
          </div>

          <div class="form-group">
            <label for="password"> 密碼 </label>

            <input
              id="password"
              v-model="password"
              type="password"
              placeholder="請輸入密碼"
              required
            />
          </div>

          <button type="submit" class="login-button" :disabled="loading">
            {{ loading ? "登入中..." : "登入" }}
          </button>

          <div v-if="message" class="message" :class="messageType">
            {{ message }}
          </div>
        </form>

        <div class="link-area">
          還沒有會員帳號？

          <RouterLink to="/register"> 立即註冊 </RouterLink>
        </div>

        <div class="home-link">
          <RouterLink to="/"> ← 回到首頁 </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useAuthStore } from "@/stores/auth";
import { ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

const username = ref("");
const password = ref("");

const loading = ref(false);

const message = ref("");
const messageType = ref("");

const authStore = useAuthStore()

async function login() {
  message.value = "";

  if (!username.value || !password.value) {
    message.value = "請輸入帳號與密碼";
    messageType.value = "error";
    return;
  }

  loading.value = true;

  try {
    const response = await fetch("/api/auth/login", {
      method: "POST",

      headers: {
        "Content-Type": "application/json",
      },

      body: JSON.stringify({
        username: username.value,
        password: password.value,
      }),
    });

    if (!response.ok) {
      message.value = "帳號或密碼錯誤";
      messageType.value = "error";

      return;
    }

    const data = await response.json();

    console.log("登入結果：", data);


    authStore.login(data.token, data.authorities, data.name, data.memberId);

    message.value = "登入成功";
    messageType.value = "success";

    // 判斷是不是管理員
    if (data.authorities && data.authorities.includes("ROLE_ADMIN")) {
      router.push("/admin");
    } else {
      router.push("/");
    }
  } catch (error) {
    console.error("登入錯誤：", error);

    message.value = "無法連接後端伺服器";
    messageType.value = "error";
  } finally {
    loading.value = false;
  }
}
</script>

<style scoped>
.login-page {
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

.login-container {
  width: 100%;

  max-width: 430px;
}

.login-card {
  background-color: rgba(255, 255, 255, 0.96);

  padding: 40px 34px;

  border-radius: 16px;

  box-shadow: 0 12px 35px rgba(0, 0, 0, 0.22);
}

.hotel-name {
  text-align: center;

  color: #9b7435;

  font-size: 26px;

  font-weight: bold;

  letter-spacing: 2px;

  margin-bottom: 8px;
}

h1 {
  text-align: center;

  color: #4a3b2a;

  font-size: 28px;

  margin-bottom: 10px;
}

.subtitle {
  text-align: center;

  color: #777;

  font-size: 14px;

  line-height: 1.6;

  margin-bottom: 28px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;

  margin-bottom: 8px;

  color: #554536;

  font-size: 14px;

  font-weight: bold;
}

input {
  width: 100%;

  padding: 12px 14px;

  border: 1px solid #d8d0c5;

  border-radius: 8px;

  font-size: 15px;

  font-family: inherit;

  transition: 0.25s;

  box-sizing: border-box;
}

input:focus {
  outline: none;

  border-color: #b58a46;

  box-shadow: 0 0 0 3px rgba(181, 138, 70, 0.14);
}

.login-button {
  width: 100%;

  padding: 12px;

  border: none;

  border-radius: 8px;

  background-color: #b58a46;

  color: white;

  font-size: 15px;

  font-weight: bold;

  font-family: inherit;

  cursor: pointer;

  transition: 0.25s;
}

.login-button:hover {
  background-color: #8f692f;

  transform: translateY(-2px);
}

.login-button:disabled {
  opacity: 0.7;

  cursor: not-allowed;

  transform: none;
}

.link-area {
  margin-top: 22px;

  text-align: center;

  font-size: 14px;

  color: #777;
}

.link-area a {
  color: #9b7435;

  font-weight: bold;

  text-decoration: none;
}

.link-area a:hover {
  text-decoration: underline;
}

.home-link {
  margin-top: 12px;

  text-align: center;
}

.home-link a {
  color: #666;

  text-decoration: none;

  font-size: 14px;
}

.home-link a:hover {
  color: #9b7435;
}

.message {
  margin-top: 15px;

  padding: 10px;

  border-radius: 8px;

  text-align: center;

  font-size: 14px;
}

.message.success {
  background-color: #e5f6eb;

  color: #257641;
}

.message.error {
  background-color: #fde9e7;

  color: #b3443c;
}

@media (max-width: 520px) {
  .login-page {
    padding: 18px;
  }

  .login-card {
    padding: 32px 24px;
  }
}
</style>
