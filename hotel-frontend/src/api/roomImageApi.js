import { fetchClient } from "./apiClient";

const BASE_URL = "/api/images";

export const roomImageApi = {
  getAllImages() {
    return fetchClient(BASE_URL, { method: "GET" });
  },
  
  getImageById(id) {
    return fetchClient(`${BASE_URL}/${id}`, { method: "GET" });
  },
  
  createImage(data, isFormData = false) {
    return fetchClient(BASE_URL, {
      method: "POST",
      body: data, // If isFormData is true, apiClient will handle not setting Content-Type
    });
  },
  
  updateImage(id, data) {
    return fetchClient(`${BASE_URL}/${id}`, {
      method: "PUT",
      body: data,
    });
  },
  
  deleteImage(id) {
    return fetchClient(`${BASE_URL}/${id}`, { method: "DELETE" });
  },
};
