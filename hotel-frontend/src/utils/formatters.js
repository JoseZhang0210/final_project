/**
 * 通用格式化工具函式
 * 集中管理跨 Admin View 共用的格式化邏輯
 */

/**
 * 將數字格式化為台幣貨幣字串
 * @param {number} price
 * @returns {string} 例如 "NT$1,200"
 */
export function formatPrice(price) {
  return new Intl.NumberFormat("zh-TW", {
    style: "currency",
    currency: "TWD",
    maximumFractionDigits: 0,
  }).format(price || 0);
}

/**
 * 將 ISO 日期時間字串縮短為 "YYYY-MM-DD HH:mm" 格式
 * @param {string|null} dateTimeStr
 * @returns {string} 例如 "2024-06-01 14:30" 或 "—"（無資料時）
 */
export function formatDateTimeShort(dateTimeStr) {
  if (!dateTimeStr) return "—";
  return String(dateTimeStr).replace("T", " ").slice(0, 16);
}
