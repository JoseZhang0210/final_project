/**
 * 將 Date 物件轉換為本地時區的 YYYY-MM-DD 字串格式
 * @param {Date|String} date - 日期物件或可被解析的日期字串
 * @returns {String} YYYY-MM-DD 格式的日期字串，若傳入無效日期則回傳空字串
 */
export function formatDateToYYYYMMDD(date) {
  if (!date) return '';
  
  const d = new Date(date);
  if (isNaN(d.getTime())) return '';
  
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  
  return `${year}-${month}-${day}`;
}

/**
 * 取得今天的 YYYY-MM-DD 字串
 * @returns {String} 今天的 YYYY-MM-DD 字串
 */
export function getTodayYYYYMMDD() {
  return formatDateToYYYYMMDD(new Date());
}
