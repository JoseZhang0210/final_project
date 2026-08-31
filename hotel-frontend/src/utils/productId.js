export function normalizeProductId(productId) {
  if (
    productId === null ||
    productId === undefined ||
    String(productId).trim() === ""
  ) {
    return null;
  }

  const id = Number(productId);

  if (!Number.isSafeInteger(id) || id <= 0) {
    return null;
  }

  return id;
}
