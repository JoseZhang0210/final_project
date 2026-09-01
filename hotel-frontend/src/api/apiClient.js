export function getAuthHeaders() {
  const token = localStorage.getItem("token");
  const headers = {
    "Content-Type": "application/json",
  };
  if (token) {
    headers.Authorization = "Bearer " + token;
  }
  return headers;
}

export async function fetchClient(url, options = {}) {
  const defaultOptions = {
    headers: getAuthHeaders(),
    credentials: "include",
  };

  const finalOptions = {
    ...defaultOptions,
    ...options,
    headers: {
      ...defaultOptions.headers,
      ...(options.headers || {}),
    },
  };

  if (options.body && typeof options.body !== 'string' && !(options.body instanceof FormData)) {
    finalOptions.body = JSON.stringify(options.body);
  }

  // Handle FormData separately to not set Content-Type to application/json
  if (options.body instanceof FormData) {
    delete finalOptions.headers["Content-Type"];
  }

  const response = await fetch(url, finalOptions);

  if (!response.ok) {
    if (response.status === 401) {
      throw new Error("請先登入 (401)");
    }
    if (response.status === 403) {
      throw new Error("權限不足或登入已過期 (403)");
    }
    
    // Try to parse the error message from backend
    const errorData = await response.json().catch(() => ({}));
    throw new Error(errorData.message || `請求失敗：${response.status}`);
  }

  // Some endpoints might return no content (e.g., DELETE)
  const text = await response.text();
  if (!text) return null;

  try {
    const data = JSON.parse(text);
    return Array.isArray(data) ? data : data.content || data;
  } catch (e) {
    return text; // return raw text if not json
  }
}
