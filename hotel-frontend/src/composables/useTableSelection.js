import { ref } from "vue";

/**
 * 表格多選勾選控制
 */
export function useTableSelection() {
  const selectedIds = ref([]);

  /**
   * 檢查本頁項目是否已全部選取
   * @param {Array} pageItems 本頁項目列表
   * @param {Function} getId 取得 ID 的方法
   */
  function isAllSelected(pageItems, getId = (item) => item.id) {
    if (!pageItems || pageItems.length === 0) return false;
    return pageItems.every((item) => selectedIds.value.includes(getId(item)));
  }

  /**
   * 全選 / 取消全選本頁項目
   * @param {Event} event Checkbox change event
   * @param {Array} pageItems 本頁項目列表
   * @param {Function} getId 取得 ID 的方法
   */
  function toggleSelectAll(event, pageItems, getId = (item) => item.id) {
    const checked = event.target.checked;
    const pageIds = pageItems.map(getId);
    if (checked) {
      const set = new Set([...selectedIds.value, ...pageIds]);
      selectedIds.value = Array.from(set);
    } else {
      selectedIds.value = selectedIds.value.filter((id) => !pageIds.includes(id));
    }
  }

  /**
   * 清除所有選取
   */
  function clearSelection() {
    selectedIds.value = [];
  }

  return {
    selectedIds,
    isAllSelected,
    toggleSelectAll,
    clearSelection,
  };
}

