import { defineStore } from "pinia";
import { ref } from "vue";

export const useAuthStore = defineStore("auth", () => {
  // =========================
  // 登入狀態
  // =========================
  const isLoggedIn = ref(
    !!localStorage.getItem("token")
  );

  // =========================
  // 權限
  // =========================
  const authorities = ref(
    localStorage.getItem("authorities")
      ? JSON.parse(
          localStorage.getItem("authorities")
        )
      : []
  );

  // =========================
  // 會員 ID
  // =========================
  const memberId = ref(
    localStorage.getItem("memberId")
      ? Number(
          localStorage.getItem("memberId")
        )
      : null
  );

  // =========================
  // 登入成功時呼叫
  // =========================
  function login(
    token,
    userAuthorities,
    userMemberId
  ) {
    // 防呆：確保權限一定是陣列
    const authArray =
      Array.isArray(userAuthorities)
        ? userAuthorities
        : userAuthorities
          ? [userAuthorities]
          : [];

    // -------------------------
    // JWT
    // -------------------------
    localStorage.setItem(
      "token",
      token
    );

    // -------------------------
    // 權限
    // -------------------------
    localStorage.setItem(
      "authorities",
      JSON.stringify(authArray)
    );

    // -------------------------
    // memberId
    // -------------------------
    const validMemberId =
    Number(userMemberId);

    if (
      Number.isInteger(validMemberId) &&
      validMemberId > 0
    ) {
      localStorage.setItem(
        "memberId",
        String(validMemberId)
      );

      memberId.value =
        validMemberId;
    } else {
      localStorage.removeItem(
        "memberId"
      );

      memberId.value =
        null;
    }
    // -------------------------
    // 更新 Pinia 狀態
    // -------------------------
    isLoggedIn.value = true;

    authorities.value =
      authArray;

    console.log(
      "Pinia 登入成功"
    );

    console.log(
      "權限：",
      authorities.value
    );

    console.log(
      "memberId：",
      memberId.value
    );
  }

  // =========================
  // 登出
  // =========================
  function logout() {
    // JWT
    localStorage.removeItem(
      "token"
    );

    // 權限
    localStorage.removeItem(
      "authorities"
    );

    // 會員 ID
    localStorage.removeItem(
      "memberId"
    );

    // 更新 Pinia
    isLoggedIn.value = false;

    authorities.value = [];

    memberId.value = null;

    console.log(
      "已登出，JWT、權限、memberId 已清除"
    );
  }

  return {
    isLoggedIn,
    authorities,
    memberId,
    login,
    logout,
  };
});