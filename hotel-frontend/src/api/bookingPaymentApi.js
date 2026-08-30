import { fetchClient } from "./apiClient";

const BASE_URL = "/api/booking-payments";

export const bookingPaymentApi = {
  getAllPayments() {
    return fetchClient(BASE_URL, { method: "GET" });
  },
  
  getPaymentById(id) {
    return fetchClient(`${BASE_URL}/${id}`, { method: "GET" });
  },
  
  createPayment(data) {
    return fetchClient(BASE_URL, {
      method: "POST",
      body: data,
    });
  },
  
  updatePaymentStatus(id, data) {
    return fetchClient(`${BASE_URL}/${id}/status`, {
      method: "PUT",
      body: data,
    });
  },
};
