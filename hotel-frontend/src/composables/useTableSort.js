import { ref } from "vue";

/**
 * 表格排序邏輯
 * @param {string} initialKey 預設排序欄位 key
 * @param {string} initialDirection 預設排序方向 ('asc' | 'desc')
 */
export function useTableSort(initialKey = "id", initialDirection = "asc") {
  const sortKey = ref(initialKey);
  const sortDirection = ref(initialDirection);

  function changeSort(key, onResetPage) {
    if (sortKey.value === key) {
      sortDirection.value = sortDirection.value === "asc" ? "desc" : "asc";
    } else {
      sortKey.value = key;
      sortDirection.value = "asc";
    }
    if (typeof onResetPage === "function") {
      onResetPage();
    }
  }

  function getSortIcon(key) {
    if (sortKey.value !== key) {
      return "↕";
    }
    return sortDirection.value === "asc" ? "▲" : "▼";
  }

  /**
   * 對資料清單依目前欄位與方向進行排序
   * @param {Array} list 要排序的陣列
   * @param {Object} valueExtractors 各欄位的值取得器 { [key]: (item) => value }
   */
  function sortList(list, valueExtractors = {}) {
    const result = [...list];
    result.sort((a, b) => {
      const extractor = valueExtractors[sortKey.value];
      let valueA = extractor ? extractor(a) : a[sortKey.value];
      let valueB = extractor ? extractor(b) : b[sortKey.value];

      if (valueA === undefined || valueA === null) valueA = "";
      if (valueB === undefined || valueB === null) valueB = "";

      let compareResult;
      if (typeof valueA === "number" && typeof valueB === "number") {
        compareResult = valueA - valueB;
      } else {
        compareResult = String(valueA).localeCompare(String(valueB), "zh-TW");
      }

      return sortDirection.value === "asc" ? compareResult : -compareResult;
    });
    return result;
  }

  return {
    sortKey,
    sortDirection,
    changeSort,
    getSortIcon,
    sortList,
  };
}

