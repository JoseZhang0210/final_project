import { ref, computed, watch } from "vue";

/**
 * 後台管理通用分頁邏輯
 * @param {import("vue").Ref<Array>} itemsRef 資料陣列的 ref
 * @param {number} defaultPageSize 預設每頁筆數
 */
export function useAdminPagination(itemsRef, defaultPageSize = 10) {
  const currentPage = ref(1);
  const pageSize = ref(defaultPageSize);

  const totalPages = computed(() => {
    const list = itemsRef?.value || [];
    return Math.max(1, Math.ceil(list.length / pageSize.value));
  });

  const paginatedItems = computed(() => {
    const list = itemsRef?.value || [];
    const start = (currentPage.value - 1) * pageSize.value;
    return list.slice(start, start + pageSize.value);
  });

  const visiblePages = computed(() => {
    const pages = [];
    const maxVisible = 5;
    let start = Math.max(1, currentPage.value - 2);
    let end = Math.min(totalPages.value, start + maxVisible - 1);

    if (end - start + 1 < maxVisible) {
      start = Math.max(1, end - maxVisible + 1);
    }

    for (let page = start; page <= end; page++) {
      pages.push(page);
    }

    return pages;
  });

  function goToPage(page) {
    if (page < 1 || page > totalPages.value) return;
    currentPage.value = page;
  }

  function resetPage() {
    currentPage.value = 1;
  }

  // 監聽每頁筆數變更時重設為第一頁
  watch(pageSize, () => {
    currentPage.value = 1;
  });

  // 當總頁數變小時，自動修正目前頁碼
  watch(totalPages, (total) => {
    if (currentPage.value > total) {
      currentPage.value = total;
    }
  });

  return {
    currentPage,
    pageSize,
    totalPages,
    paginatedItems,
    visiblePages,
    goToPage,
    resetPage,
  };
}

