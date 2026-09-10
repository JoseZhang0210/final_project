import axios from "axios";
export async function getOccupiedDates(token, from, to) { // 占用資料不含私人活動或付款資訊。
  return (await api.get("/rentals/occupied-dates", { ...authConfig(token), params: { from, to } })).data; // 只傳有限日期範圍。
}
export async function getRentalPayment(token, id) { // 讀取後端驗證過的歷史付款資訊。
  return (await api.get(`/rental-payments/rentals/${id}`, authConfig(token))).data; // 金額不從場地目前價格回推。
}
export async function checkoutRental(token, id) { // 付款請求只傳租借編號。
  return (await api.post(`/rental-payments/rentals/${id}/checkout`, null, authConfig(token))).data; // 會員與金額均由後端決定。
}

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

/*
 * 管理員替既有會員建立場地租借。
 * memberId 由後台指定，其餘系統欄位仍由後端產生。
 */
export async function createAdminRental(token, payload) {
  const response = await api.post(
    "/rentals/admin",
    payload,
    authConfig(token),
  );

  return response.data;
}


/*
 * 目前登入會員取消自己的場地預約。
 * 取消只改 Rental 狀態，不刪除歷史紀錄。
 */
export async function cancelMyRental(token, id) {
  const response = await api.post(
    `/rentals/${id}/cancel`,
    null,
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


/*
 * 從使用者電腦選擇圖片並上傳到場地。
 * FormData 交給瀏覽器自動設定 multipart boundary。
 */
export async function uploadVenueImage(
  token,
  id,
  file,
) {
  const formData = new FormData();

  formData.append(
    "file",
    file,
  );

  const response = await api.post(
    `/venues/${id}/upload-image`,
    formData,
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
