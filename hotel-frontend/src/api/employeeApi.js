import { fetchClient } from "./apiClient";

const BASE_URL = "/api/employees";

export const employeeApi = {
  findAllEmployees(params = {}) {
    let url = BASE_URL;
    const query = new URLSearchParams();
    if (params.keyword) query.append("keyword", params.keyword);
    if (params.status) query.append("status", params.status);
    if (params.departmentId) query.append("departmentId", params.departmentId);
    const queryString = query.toString();
    if (queryString) {
      url += `?${queryString}`;
    }
    return fetchClient(url, { method: "GET" });
  },

  searchEmployees(keyword) {
    const encoded = encodeURIComponent(keyword || "");
    return fetchClient(`${BASE_URL}/search?keyword=${encoded}`, {
      method: "GET",
    });
  },

  findEmployeeById(id) {
    return fetchClient(`${BASE_URL}/${id}`, { method: "GET" });
  },

  createEmployee(data) {
    return fetchClient(BASE_URL, {
      method: "POST",
      body: data,
    });
  },

  updateEmployee(id, data) {
    return fetchClient(`${BASE_URL}/${id}`, {
      method: "PUT",
      body: data,
    });
  },

  updateEmployeeStatus(id, status) {
    return fetchClient(`${BASE_URL}/${id}/status?status=${encodeURIComponent(status)}`, {
      method: "PATCH",
    });
  },

  deleteEmployee(id) {
    return fetchClient(`${BASE_URL}/${id}`, { method: "DELETE" });
  },

  async exportEmployees(params = {}) {
    let url = `${BASE_URL}/export`;
    const query = new URLSearchParams();
    if (params.keyword) query.append("keyword", params.keyword);
    if (params.status) query.append("status", params.status);
    if (params.departmentId) query.append("departmentId", params.departmentId);
    if (params.ids) {
      const idsStr = Array.isArray(params.ids) ? params.ids.join(",") : params.ids;
      query.append("ids", idsStr);
    }
    if (params.minId !== undefined && params.minId !== "") query.append("minId", params.minId);
    if (params.maxId !== undefined && params.maxId !== "") query.append("maxId", params.maxId);
    if (params.limit !== undefined && params.limit !== "") query.append("limit", params.limit);
    if (params.offset !== undefined && params.offset !== "") query.append("offset", params.offset);

    const queryString = query.toString();
    if (queryString) {
      url += `?${queryString}`;
    }

    const token = localStorage.getItem("token");
    const headers = {};
    if (token) {
      headers["Authorization"] = "Bearer " + token;
    }

    const response = await fetch(url, {
      method: "GET",
      headers,
    });

    if (!response.ok) {
      throw new Error(`匯出失敗：${response.status}`);
    }

    return await response.blob();
  },

  importEmployeesJson(json) {
    return fetchClient(`${BASE_URL}/import`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: typeof json === "string" ? json : JSON.stringify(json),
    });
  },

  importEmployeesFile(formData) {
    return fetchClient(`${BASE_URL}/import`, {
      method: "POST",
      body: formData,
    });
  },
};
