import { fetchClient } from "./apiClient";

const BASE_URL = "/api/bookings";

export const bookingApi = {
  getAllBookings() {
    return fetchClient(BASE_URL, { method: "GET" });
  },
  
  getBookingById(id) {
    return fetchClient(`${BASE_URL}/${id}`, { method: "GET" });
  },
  
  searchBookings(criteria) {
    return fetchClient(`${BASE_URL}/search`, {
      method: "POST",
      body: criteria,
    });
  },

  // Fallbacks for the older check-in and status API paths if needed
  searchByCheckInDate(date) {
    return fetchClient(`${BASE_URL}/check-in?date=${date}`, { method: "GET" });
  },

  searchByStatus(status) {
    return fetchClient(`${BASE_URL}/status?status=${encodeURIComponent(status)}`, { method: "GET" });
  },
  
  createBooking(data) {
    return fetchClient(BASE_URL, {
      method: "POST",
      body: data,
    });
  },
  
  updateBooking(id, data) {
    return fetchClient(`${BASE_URL}/${id}`, {
      method: "PUT",
      body: data,
    });
  },
  
  deleteBooking(id) {
    return fetchClient(`${BASE_URL}/${id}`, { method: "DELETE" });
  },
};
