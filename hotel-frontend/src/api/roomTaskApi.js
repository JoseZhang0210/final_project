import { fetchClient } from "./apiClient";

const BASE_URL = "/api/roomtask";

export const roomTaskApi = {
  getRoomTasks(params = {}) {
    const searchParams = new URLSearchParams();
    if (params.taskId) searchParams.append("taskId", params.taskId);
    if (params.roomId) searchParams.append("roomId", params.roomId);
    if (params.employeeId) searchParams.append("employeeId", params.employeeId);
    if (params.priority) searchParams.append("priority", params.priority);

    const queryString = searchParams.toString();
    const url = queryString ? `${BASE_URL}?${queryString}` : BASE_URL;

    return fetchClient(url, { method: "GET" });
  },
  
  getRoomTaskById(id) {
    return fetchClient(`${BASE_URL}/${id}`, { method: "GET" });
  },
  
  createRoomTask(data) {
    return fetchClient(BASE_URL, {
      method: "POST",
      body: data,
    });
  },
  
  updateRoomTask(id, data) {
    return fetchClient(`${BASE_URL}/${id}`, {
      method: "PUT",
      body: data,
    });
  },
  
  deleteRoomTask(id) {
    return fetchClient(`${BASE_URL}/${id}`, { method: "DELETE" });
  },
};
