const STORAGE_KEY = "recentlyViewedProducts";
const MAX_ITEMS = 10;

export function getRecentlyViewedIds() {
  try {
    const data = JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]");

    // 保證一定是陣列
    return Array.isArray(data) ? data : [];
  } catch (error) {
    console.error("讀取最近瀏覽失敗：", error);

    return [];
  }
}

export function addRecentlyViewed(productId) {
  let ids = getRecentlyViewedIds();

  const id = Number(productId);

  // 移除重複商品
  ids = ids.filter((itemId) => Number(itemId) !== id);

  // 最新瀏覽放最前面
  ids.unshift(id);

  // 最多保留 10 筆
  ids = ids.slice(0, MAX_ITEMS);

  localStorage.setItem(STORAGE_KEY, JSON.stringify(ids));
}

export function clearRecentlyViewed() {
  localStorage.removeItem(STORAGE_KEY);
}
