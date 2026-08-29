const STORAGE_KEY = "wishlistProducts";

export function getWishlistIds() {
  try {
    const data = JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]");

    return Array.isArray(data) ? data : [];
  } catch (error) {
    console.error("讀取願望清單失敗：", error);
    return [];
  }
}

export function isInWishlist(productId) {
  const ids = getWishlistIds();

  return ids.some((id) => Number(id) === Number(productId));
}

export function toggleWishlist(productId) {
  let ids = getWishlistIds();

  const id = Number(productId);

  const exists = ids.some((itemId) => Number(itemId) === id);

  if (exists) {
    ids = ids.filter((itemId) => Number(itemId) !== id);
  } else {
    ids.unshift(id);
  }

  localStorage.setItem(STORAGE_KEY, JSON.stringify(ids));

  return !exists;
}

export function clearWishlist() {
  localStorage.removeItem(STORAGE_KEY);
}
