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
