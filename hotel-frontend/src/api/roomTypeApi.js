import { fetchClient } from "./apiClient";

const BASE_URL = "/api/roomtypes";

export const roomTypeApi = {
  getAllRoomTypes() {
    return fetchClient(BASE_URL, { method: "GET" });
  },
  
  getAvailableRoomTypes(checkIn, checkOut) {
    return fetchClient(`${BASE_URL}/available?checkIn=${checkIn}&checkOut=${checkOut}`, { method: "GET" });
  },
  
  getRoomTypeById(id) {
    return fetchClient(`${BASE_URL}/${id}`, { method: "GET" });
  },
  
  createRoomType(data) {
    return fetchClient(BASE_URL, {
      method: "POST",
      body: data,
    });
  },
  
  updateRoomType(id, data) {
    return fetchClient(`${BASE_URL}/${id}`, {
      method: "PUT",
      body: data,
    });
  },
  
  deleteRoomType(id) {
    return fetchClient(`${BASE_URL}/${id}`, { method: "DELETE" });
  },
};
