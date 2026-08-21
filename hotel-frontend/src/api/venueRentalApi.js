import axios from "axios";

const api = axios.create({
  baseURL: "/api",
});

function authConfig(token) {
  return {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  };
}

export function getStoredToken() {
  return localStorage.getItem("token") || "";
}

export function getStoredAuthorities() {
  try {
    return JSON.parse(localStorage.getItem("authorities") || "[]");
  } catch {
    return [];
  }
}

export async function getVenues(token) {
  const response = await api.get(
    "/venues",
    authConfig(token),
  );
  return response.data;
}

export async function createVenue(token, payload) {
  const response = await api.post(
    "/venues",
    payload,
    authConfig(token),
  );
  return response.data;
}

export async function updateVenue(token, id, payload) {
  const response = await api.put(
    `/venues/${id}`,
    payload,
    authConfig(token),
  );
  return response.data;
}

export async function deleteVenue(token, id) {
  const response = await api.delete(
    `/venues/${id}`,
    authConfig(token),
  );
  return response.data;
}

export async function getRentals(token) {
  const response = await api.get(
    "/rentals",
    authConfig(token),
  );
  return response.data;
}

export async function getMyRentals(token) {
  const response = await api.get(
    "/rentals/mine",
    authConfig(token),
  );
  return response.data;
}

export async function createRental(token, payload) {
  const response = await api.post(
    "/rentals",
    payload,
    authConfig(token),
  );
  return response.data;
}

export async function updateRental(token, id, payload) {
  const response = await api.put(
    `/rentals/${id}`,
    payload,
    authConfig(token),
  );
  return response.data;
}

export async function deleteRental(token, id) {
  const response = await api.delete(
    `/rentals/${id}`,
    authConfig(token),
  );
  return response.data;
}

export function getApiErrorMessage(error) {
  const data = error?.response?.data;

  if (typeof data === "string" && data.trim()) {
    return data;
  }

  if (data?.message) {
    return data.message;
  }

  if (error?.response?.status === 401) {
    return "登入已失效，請重新登入";
  }

  if (error?.response?.status === 403) {
    return "目前帳號沒有此功能權限";
  }

  return error?.message || "操作失敗";
}