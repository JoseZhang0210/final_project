import { fetchClient } from "./apiClient";

const BASE_URL = "/api/auth";

export const authApi = {
  checkUsername(username) {
    const encoded = encodeURIComponent(username || "");
    return fetchClient(`${BASE_URL}/check-username?username=${encoded}`, {
      method: "GET",
    });
  },

  checkEmail(email) {
    const encoded = encodeURIComponent(email || "");
    return fetchClient(`${BASE_URL}/check-email?email=${encoded}`, {
      method: "GET",
    });
  },

  sendCode(email) {
    return fetchClient(`${BASE_URL}/send-code`, {
      method: "POST",
      body: { email },
    });
  },

  register(data) {
    return fetchClient(`${BASE_URL}/register`, {
      method: "POST",
      body: data,
    });
  },

  login(data) {
    return fetchClient(`${BASE_URL}/login`, {
      method: "POST",
      body: data,
    });
  },

  refreshToken() {
    return fetchClient(`${BASE_URL}/refresh`, {
      method: "POST",
    });
  },

  sendForgotPasswordCode(data) {
    return fetchClient(`${BASE_URL}/forgot-password/send-code`, {
      method: "POST",
      body: typeof data === "string" ? { email: data } : data,
    });
  },

  resetPassword(data) {
    return fetchClient(`${BASE_URL}/forgot-password/reset`, {
      method: "POST",
      body: data,
    });
  },
};
