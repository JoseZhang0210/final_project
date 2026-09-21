import { fetchClient } from "./apiClient";

export const cartApi = {
  get: () => fetchClient("/api/cart"),
  add: (productId, quantity) => fetchClient("/api/cart/items", {
    method: "POST", body: { productId, quantity },
  }),
  update: (productId, quantity) => fetchClient(`/api/cart/items/${productId}`, {
    method: "PUT", body: { quantity },
  }),
  remove: (productId) => fetchClient(`/api/cart/items/${productId}`, { method: "DELETE" }),
  clear: () => fetchClient("/api/cart", { method: "DELETE" }),
  merge: (items) => fetchClient("/api/cart/merge", { method: "POST", body: { items } }),
  checkout: (couponCode) => fetchClient("/api/orders/from-cart", {
    method: "POST", body: { couponCode: couponCode || null },
  }),
};
