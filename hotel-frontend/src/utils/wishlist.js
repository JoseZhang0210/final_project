import { normalizeProductId } from "@/utils/productId";

const STORAGE_KEY = "wishlistProducts";

export function getWishlistIds() {
  try {
    const data = JSON.parse(localStorage.getItem(STORAGE_KEY) || "[]");

    if (!Array.isArray(data)) {
      return [];
    }

    return [
      ...new Set(
        data
          .map(normalizeProductId)
          .filter((id) => id !== null),
      ),
    ];
  } catch (error) {
    console.error("讀取願望清單失敗：", error);
    return [];
  }
}

export function isInWishlist(productId) {
  const id = normalizeProductId(productId);

  if (id === null) {
    return false;
  }

  const ids = getWishlistIds();

  return ids.includes(id);
}

export function toggleWishlist(productId) {
  const id = normalizeProductId(productId);

  if (id === null) {
    console.warn("無效的商品 ID：", productId);
    return null;
  }

  let ids = getWishlistIds();

  const exists = ids.includes(id);

  if (exists) {
    ids = ids.filter((itemId) => itemId !== id);
  } else {
    ids.unshift(id);
  }

  localStorage.setItem(STORAGE_KEY, JSON.stringify(ids));

  return !exists;
}

export function clearWishlist() {
  localStorage.removeItem(STORAGE_KEY);
}
