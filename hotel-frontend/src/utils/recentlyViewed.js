const STORAGE_KEY = "recentlyViewedProducts";
const MAX_ITEMS = 10;

export function getRecentlyViewedIds() {
  return JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]");
}
export function addRecentlyViewed(productId) {
  let ids = getRecentlyViewedIds();

  //移除重複
  ids = ids.filters((id) => id != productId);

  //放到最前面
  ids.unshift(productId);

  //最多保留10筆
  ids = ids.slice(0, MAX_ITEMS);

  localStorage.setItem(STORAGE_KEY, JSON.stringify(ids));
}
export function clearRecentlyViewed() {
  localStorage.removeItem(STORAGE_KEY);
}
