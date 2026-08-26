<template>
  <div class="product-manage-page">
    <!-- =========================
         頁面標題
         ========================= -->
    <div class="admin-page-header">
      <div>
        <h1>商品管理</h1>
        <p>管理商城商品資料與商品分類</p>
      </div>

      <div class="product-header-actions">
        <RouterLink
          to="/admin/products/add"
          class="admin-btn admin-btn-primary"
        >
          ＋ 新增商品
        </RouterLink>

        <button
          type="button"
          class="admin-btn admin-btn-secondary"
          @click="openCategoryModal"
        >
          ⚙ 商品種類管理
        </button>
      </div>
    </div>

    <!-- =========================
         商品管理卡片
         ========================= -->
    <section class="admin-card">
      <!-- =========================
           搜尋 / 篩選
           ========================= -->
      <div class="product-search">
        <input
          v-model="keyword"
          type="text"
          class="admin-input search-input"
          placeholder="請輸入商品名稱搜尋"
          @keyup.enter="searchProducts"
        />

        <!-- 分類 -->
        <select v-model="selectedCategory" class="admin-input filter-select">
          <option value="">全部分類</option>

          <option
            v-for="category in categories"
            :key="category.categoryId"
            :value="category.categoryId"
          >
            {{ category.categoryName }}
          </option>
        </select>

        <!-- 狀態 -->
        <select v-model="selectedStatus" class="admin-input filter-select">
          <option value="">全部狀態</option>

          <option value="ACTIVE">上架中</option>

          <option value="INACTIVE">未上架</option>

          <option value="OUT_OF_STOCK">缺貨</option>

          <option value="DISCONTINUED">停售</option>
        </select>

        <button
          type="button"
          class="admin-btn admin-btn-primary"
          @click="searchProducts"
        >
          搜尋
        </button>

        <button
          type="button"
          class="admin-btn admin-btn-secondary"
          @click="resetSearch"
        >
          重設
        </button>
      </div>

      <!-- =========================
           資料控制列
           ========================= -->
      <div class="table-control-bar">
        <div class="filter-summary">
          目前共有

          <strong>
            {{ sortedProducts.length }}
          </strong>

          筆商品
        </div>

        <div class="page-size-area">
          <label> 每頁顯示 </label>

          <select v-model.number="pageSize" class="page-size-select">
            <option :value="5">5</option>

            <option :value="10">10</option>

            <option :value="20">20</option>
          </select>

          <span> 筆 </span>
        </div>
      </div>

      <!-- =========================
           訊息
           ========================= -->
      <div v-if="message" class="admin-message" :class="messageType">
        {{ message }}
      </div>

      <!-- =========================
           Loading
           ========================= -->
      <div v-if="loading" class="loading-message">商品資料讀取中...</div>

      <!-- =========================
           商品表格
           ========================= -->
      <div v-else class="admin-table-wrapper">
        <table class="admin-table">
          <thead>
            <tr>
              <th class="sortable" @click="changeSort('productId')">
                ID
                <span class="sort-icon">
                  {{ getSortIcon("productId") }}
                </span>
              </th>

              <th>圖片</th>

              <th class="sortable" @click="changeSort('productName')">
                商品名稱

                <span class="sort-icon">
                  {{ getSortIcon("productName") }}
                </span>
              </th>

              <th class="sortable" @click="changeSort('category')">
                分類

                <span class="sort-icon">
                  {{ getSortIcon("category") }}
                </span>
              </th>

              <th>商品描述</th>

              <th class="sortable" @click="changeSort('price')">
                價格

                <span class="sort-icon">
                  {{ getSortIcon("price") }}
                </span>
              </th>

              <th class="sortable" @click="changeSort('stock')">
                庫存

                <span class="sort-icon">
                  {{ getSortIcon("stock") }}
                </span>
              </th>

              <th class="sortable" @click="changeSort('status')">
                狀態

                <span class="sort-icon">
                  {{ getSortIcon("status") }}
                </span>
              </th>

              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <!-- 沒資料 -->
            <tr v-if="paginatedProducts.length === 0">
              <td colspan="9" class="empty-message">查無符合條件的商品</td>
            </tr>

            <!-- 商品 -->
            <tr v-for="product in paginatedProducts" :key="product.productId">
              <!-- ID -->
              <td>
                {{ product.productId }}
              </td>

              <!-- 圖片 -->
              <td>
                <div class="product-thumbnail-wrap">
                  <img
                    :src="getProductImage(product)"
                    :alt="product.productName"
                    class="product-thumbnail"
                    @error="handleImageError"
                  />
                </div>
              </td>

              <!-- 商品名稱 -->
              <td class="product-name">
                {{ product.productName }}
              </td>

              <!-- 分類 -->
              <td>
                <span class="category-text">
                  {{ product.category?.categoryName ?? "未分類" }}
                </span>
              </td>

              <!-- 描述 -->
              <td class="description-cell">
                {{ product.description || "" }}
              </td>

              <!-- 價格 -->
              <td class="price">
                NT$
                {{ formatPrice(product.price) }}
              </td>

              <!-- 庫存 -->
              <td>
                <div class="stock-area">
                  <span
                    class="stock-badge"
                    :class="getStockClass(product.stock)"
                  >
                    {{ getStockText(product.stock) }}
                  </span>

                  <small class="stock-number">
                    數量：
                    {{ product.stock ?? 0 }}
                  </small>
                </div>
              </td>

              <!-- 狀態 -->
              <td>
                <span
                  class="status-badge"
                  :class="getStatusClass(product.status)"
                >
                  {{ getStatusText(product.status) }}
                </span>
              </td>

              <!-- 操作 -->
              <td>
                <div class="action-buttons">
                  <button
                    type="button"
                    class="admin-btn admin-btn-status"
                    @click="toggleProductStatus(product)"
                  >
                    {{ product.status === "ACTIVE" ? "下架" : "上架" }}
                  </button>

                  <button
                    type="button"
                    class="admin-btn admin-btn-edit"
                    @click="goEditProduct(product.productId)"
                  >
                    修改
                  </button>

                  <button
                    type="button"
                    class="admin-btn admin-btn-delete"
                    @click="deleteProduct(product)"
                  >
                    刪除
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- =========================
           分頁
           ========================= -->
      <div v-if="!loading && sortedProducts.length > 0" class="pagination-area">
        <div class="pagination-info">
          第
          <strong>
            {{ currentPage }}
          </strong>
          頁 ／ 共
          <strong>
            {{ totalPages }}
          </strong>
          頁
        </div>

        <div class="pagination">
          <button
            type="button"
            class="page-button"
            :disabled="currentPage === 1"
            @click="goToPage(1)"
          >
            «
          </button>

          <button
            type="button"
            class="page-button"
            :disabled="currentPage === 1"
            @click="goToPage(currentPage - 1)"
          >
            ‹
          </button>

          <button
            v-for="page in visiblePages"
            :key="page"
            type="button"
            class="page-button"
            :class="{
              active: currentPage === page,
            }"
            @click="goToPage(page)"
          >
            {{ page }}
          </button>

          <button
            type="button"
            class="page-button"
            :disabled="currentPage === totalPages"
            @click="goToPage(currentPage + 1)"
          >
            ›
          </button>

          <button
            type="button"
            class="page-button"
            :disabled="currentPage === totalPages"
            @click="goToPage(totalPages)"
          >
            »
          </button>
        </div>
      </div>
    </section>

    <!-- =====================================================
         商品種類管理 Modal
         ===================================================== -->
    <div
      v-if="categoryModalOpen"
      class="category-modal"
      @click.self="closeCategoryModal"
    >
      <div class="category-manage-card">
        <!-- Header -->
        <div class="category-modal-header">
          <div>
            <h2>商品種類管理</h2>

            <p>新增、修改或刪除商城商品分類</p>
          </div>

          <button type="button" class="modal-close" @click="closeCategoryModal">
            ×
          </button>
        </div>

        <!-- =========================
             新增種類
             ========================= -->
        <div class="category-create-area">
          <label for="newCategoryName"> 新增商品種類 </label>

          <div class="category-create-row">
            <input
              id="newCategoryName"
              v-model="newCategoryName"
              type="text"
              placeholder="例如：房內用品"
              maxlength="50"
              @keyup.enter="addCategory"
            />

            <button
              type="button"
              class="admin-btn admin-btn-primary"
              :disabled="categorySaving"
              @click="addCategory"
            >
              {{ categorySaving ? "新增中..." : "＋ 新增" }}
            </button>
          </div>
        </div>

        <div class="category-divider"></div>

        <!-- =========================
             Category List
             ========================= -->
        <div class="category-list-area">
          <div class="category-list-title">
            <span> 目前商品種類 </span>

            <span class="category-count"> {{ categories.length }} 種 </span>
          </div>

          <!-- 沒分類 -->
          <div v-if="categories.length === 0" class="category-empty">
            尚無商品種類
          </div>

          <!-- 分類 -->
          <div
            v-for="category in categories"
            :key="category.categoryId"
            class="category-manage-row"
          >
            <!-- =========================
                 一般模式
                 ========================= -->
            <template v-if="editingCategoryId !== category.categoryId">
              <div class="category-info">
                <span class="category-id"> #{{ category.categoryId }} </span>

                <strong>
                  {{ category.categoryName }}
                </strong>
              </div>

              <div class="category-actions">
                <button
                  type="button"
                  class="category-edit-button"
                  @click="startEditCategory(category)"
                >
                  修改
                </button>

                <button
                  type="button"
                  class="category-delete-button"
                  @click="deleteCategory(category)"
                >
                  刪除
                </button>
              </div>
            </template>

            <!-- =========================
                 修改模式
                 ========================= -->
            <template v-else>
              <input
                v-model="editingCategoryName"
                type="text"
                class="category-edit-input"
                maxlength="50"
                @keyup.enter="updateCategory(category.categoryId)"
              />

              <div class="category-actions">
                <button
                  type="button"
                  class="category-save-button"
                  :disabled="categoryUpdating"
                  @click="updateCategory(category.categoryId)"
                >
                  {{ categoryUpdating ? "儲存中" : "儲存" }}
                </button>

                <button
                  type="button"
                  class="category-cancel-button"
                  @click="cancelEditCategory"
                >
                  取消
                </button>
              </div>
            </template>
          </div>
        </div>

        <!-- Footer -->
        <div class="category-modal-footer">
          <button
            type="button"
            class="admin-btn admin-btn-secondary"
            @click="closeCategoryModal"
          >
            關閉
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted, ref, watch } from "vue";

import { useRouter } from "vue-router";

import { getAuthHeaders } from "@/utils/auth";

const router = useRouter();

// =====================================================
// 圖片
// =====================================================

const DEFAULT_IMAGE = "/upload/products/default-product.jpg";

// =====================================================
// 商品資料
// =====================================================

const products = ref([]);

const categories = ref([]);

const keyword = ref("");

const selectedCategory = ref("");

const selectedStatus = ref("");

// =====================================================
// Loading / Message
// =====================================================

const loading = ref(false);

const message = ref("");

const messageType = ref("");

// =====================================================
// 排序
// =====================================================

const sortKey = ref("productId");

const sortDirection = ref("asc");

// =====================================================
// 分頁
// =====================================================

const currentPage = ref(1);

const pageSize = ref(10);

// =====================================================
// Category Modal
// =====================================================

const categoryModalOpen = ref(false);

const newCategoryName = ref("");

const categorySaving = ref(false);

const editingCategoryId = ref(null);

const editingCategoryName = ref("");

const categoryUpdating = ref(false);

// =====================================================
// 訊息
// =====================================================

function showMessage(text, type) {
  message.value = text;

  messageType.value = type;

  setTimeout(() => {
    message.value = "";
  }, 3000);
}

// =====================================================
// 商品
// GET /api/products
// =====================================================

async function loadProducts() {
  loading.value = true;

  try {
    const response = await fetch("/api/products", {
      method: "GET",

      headers: getAuthHeaders(),
    });

    if (!response.ok) {
      if (response.status === 401 || response.status === 403) {
        showMessage("登入狀態已失效，請重新登入", "error");
      } else {
        showMessage("讀取商品失敗", "error");
      }

      return;
    }

    products.value = await response.json();
  } catch (error) {
    console.error("讀取商品失敗：", error);

    showMessage("無法連接後端伺服器", "error");
  } finally {
    loading.value = false;
  }
}

// =====================================================
// Category
// GET /api/categories
// =====================================================

async function loadCategories() {
  try {
    const response = await fetch("/api/categories", {
      method: "GET",

      headers: getAuthHeaders(),
    });

    if (!response.ok) {
      console.error("讀取商品種類失敗：", response.status);

      return;
    }

    categories.value = await response.json();
  } catch (error) {
    console.error("讀取商品種類失敗：", error);
  }
}

// =====================================================
// 篩選
// =====================================================

const filteredProducts = computed(() => {
  let result = [...products.value];

  // 分類
  if (selectedCategory.value !== "") {
    result = result.filter(
      (product) =>
        Number(product.category?.categoryId) === Number(selectedCategory.value),
    );
  }

  // 狀態
  if (selectedStatus.value !== "") {
    result = result.filter(
      (product) => product.status === selectedStatus.value,
    );
  }

  return result;
});

// =====================================================
// 排序
// =====================================================

const sortedProducts = computed(() => {
  const result = [...filteredProducts.value];

  result.sort((a, b) => {
    let valueA;
    let valueB;

    switch (sortKey.value) {
      case "productName":
        valueA = a.productName || "";

        valueB = b.productName || "";

        break;

      case "category":
        valueA = a.category?.categoryName || "";

        valueB = b.category?.categoryName || "";

        break;

      case "price":
        valueA = Number(a.price ?? 0);

        valueB = Number(b.price ?? 0);

        break;

      case "stock":
        valueA = Number(a.stock ?? 0);

        valueB = Number(b.stock ?? 0);

        break;

      case "status":
        valueA = getStatusText(a.status);

        valueB = getStatusText(b.status);

        break;

      default:
        valueA = Number(a.productId ?? 0);

        valueB = Number(b.productId ?? 0);
    }

    let compareResult;

    if (typeof valueA === "number" && typeof valueB === "number") {
      compareResult = valueA - valueB;
    } else {
      compareResult = String(valueA).localeCompare(String(valueB), "zh-TW");
    }

    return sortDirection.value === "asc" ? compareResult : -compareResult;
  });

  return result;
});

// =====================================================
// 排序
// =====================================================

function changeSort(key) {
  if (sortKey.value === key) {
    sortDirection.value = sortDirection.value === "asc" ? "desc" : "asc";
  } else {
    sortKey.value = key;

    sortDirection.value = "asc";
  }

  currentPage.value = 1;
}

function getSortIcon(key) {
  if (sortKey.value !== key) {
    return "↕";
  }

  return sortDirection.value === "asc" ? "▲" : "▼";
}

// =====================================================
// 分頁
// =====================================================

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(sortedProducts.value.length / pageSize.value));
});

const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;

  return sortedProducts.value.slice(start, start + pageSize.value);
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
  if (page < 1 || page > totalPages.value) {
    return;
  }

  currentPage.value = page;
}

// =====================================================
// 搜尋
// =====================================================

async function searchProducts() {
  const searchKeyword = keyword.value.trim();

  currentPage.value = 1;

  if (searchKeyword === "") {
    await loadProducts();

    return;
  }

  loading.value = true;

  try {
    const response = await fetch(
      "/api/products/search?keyword=" + encodeURIComponent(searchKeyword),
      {
        method: "GET",

        headers: getAuthHeaders(),
      },
    );

    if (!response.ok) {
      showMessage("搜尋商品失敗", "error");

      return;
    }

    products.value = await response.json();
  } catch (error) {
    console.error("搜尋商品失敗：", error);

    showMessage("搜尋商品失敗", "error");
  } finally {
    loading.value = false;
  }
}

// =====================================================
// 重設
// =====================================================

async function resetSearch() {
  keyword.value = "";

  selectedCategory.value = "";

  selectedStatus.value = "";

  sortKey.value = "productId";

  sortDirection.value = "asc";

  currentPage.value = 1;

  await loadProducts();
}

// =====================================================
// 圖片
// =====================================================

function getProductImage(product) {
  const imageUrl = product.imageUrl?.trim();

  if (!imageUrl) {
    return DEFAULT_IMAGE;
  }

  if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
    return imageUrl;
  }

  if (imageUrl.startsWith("/")) {
    return imageUrl;
  }

  return "/upload/products/" + imageUrl;
}

function handleImageError(event) {
  if (event.target.dataset.fallback === "true") {
    return;
  }

  event.target.dataset.fallback = "true";

  event.target.src = DEFAULT_IMAGE;
}

// =====================================================
// 修改商品
// =====================================================

function goEditProduct(productId) {
  router.push(`/admin/products/${productId}/edit`);
}

// =====================================================
// 刪除商品
// =====================================================

async function deleteProduct(product) {
  const result = window.confirm(`確定要刪除「${product.productName}」嗎？`);

  if (!result) {
    return;
  }

  try {
    const response = await fetch(`/api/products/${product.productId}`, {
      method: "DELETE",

      headers: getAuthHeaders(),
    });

    if (!response.ok) {
      showMessage("刪除商品失敗", "error");

      return;
    }

    showMessage("商品刪除成功", "success");

    if (keyword.value.trim()) {
      await searchProducts();
    } else {
      await loadProducts();
    }
  } catch (error) {
    console.error("刪除商品錯誤：", error);

    showMessage("刪除商品失敗", "error");
  }
}

// =====================================================
// 快速上下架
// =====================================================

async function toggleProductStatus(product) {
  const newStatus = product.status === "ACTIVE" ? "INACTIVE" : "ACTIVE";

  const actionText = newStatus === "ACTIVE" ? "上架" : "下架";

  if (!window.confirm(`確定要${actionText}「${product.productName}」嗎？`)) {
    return;
  }

  try {
    const response = await fetch(`/api/products/${product.productId}/status`, {
      method: "PATCH",

      headers: getAuthHeaders(),

      body: JSON.stringify({
        status: newStatus,
      }),
    });

    if (!response.ok) {
      showMessage("修改商品狀態失敗", "error");

      return;
    }

    product.status = newStatus;

    showMessage(`商品已${actionText}`, "success");
  } catch (error) {
    console.error(error);

    showMessage("修改商品狀態失敗", "error");
  }
}

// =====================================================
// 開啟 Category 管理
// =====================================================

function openCategoryModal() {
  newCategoryName.value = "";

  cancelEditCategory();

  categoryModalOpen.value = true;

  setTimeout(() => {
    document.getElementById("newCategoryName")?.focus();
  }, 50);
}

function closeCategoryModal() {
  categoryModalOpen.value = false;

  newCategoryName.value = "";

  cancelEditCategory();
}

// =====================================================
// Category - 新增
// POST /api/categories
// =====================================================

async function addCategory() {
  const categoryName = newCategoryName.value.trim();

  if (!categoryName) {
    showMessage("請輸入商品種類名稱", "error");

    return;
  }

  categorySaving.value = true;

  try {
    const response = await fetch("/api/categories", {
      method: "POST",

      headers: getAuthHeaders(),

      body: JSON.stringify({
        categoryName,
      }),
    });

    if (response.status === 409) {
      showMessage("這個商品種類已經存在", "error");

      return;
    }

    if (!response.ok) {
      showMessage("新增商品種類失敗", "error");

      return;
    }

    newCategoryName.value = "";

    await loadCategories();

    showMessage("商品種類新增成功", "success");
  } catch (error) {
    console.error("新增種類失敗：", error);

    showMessage("新增商品種類失敗", "error");
  } finally {
    categorySaving.value = false;
  }
}

// =====================================================
// Category - 開始修改
// =====================================================

function startEditCategory(category) {
  editingCategoryId.value = category.categoryId;

  editingCategoryName.value = category.categoryName;
}

// =====================================================
// Category - 取消修改
// =====================================================

function cancelEditCategory() {
  editingCategoryId.value = null;

  editingCategoryName.value = "";
}

// =====================================================
// Category - 修改
// PUT /api/categories/{id}
// =====================================================

async function updateCategory(categoryId) {
  const categoryName = editingCategoryName.value.trim();

  if (!categoryName) {
    showMessage("商品種類名稱不能為空", "error");

    return;
  }

  categoryUpdating.value = true;

  try {
    const response = await fetch(`/api/categories/${categoryId}`, {
      method: "PUT",

      headers: getAuthHeaders(),

      body: JSON.stringify({
        categoryName,
      }),
    });

    if (response.status === 409) {
      showMessage("這個商品種類已經存在", "error");

      return;
    }

    if (!response.ok) {
      showMessage("修改商品種類失敗", "error");

      return;
    }

    cancelEditCategory();

    // Category 更新
    await loadCategories();

    // Product 重新讀取
    // 讓商品表格分類名稱同步更新
    await loadProducts();

    showMessage("商品種類修改成功", "success");
  } catch (error) {
    console.error("修改種類失敗：", error);

    showMessage("修改商品種類失敗", "error");
  } finally {
    categoryUpdating.value = false;
  }
}

// =====================================================
// Category - 刪除
// DELETE /api/categories/{id}
// =====================================================

async function deleteCategory(category) {
  const result = window.confirm(
    `確定要刪除商品種類「${category.categoryName}」嗎？`,
  );

  if (!result) {
    return;
  }

  try {
    const response = await fetch(`/api/categories/${category.categoryId}`, {
      method: "DELETE",

      headers: getAuthHeaders(),
    });

    // 有商品正在使用
    if (response.status === 409) {
      let errorMessage = "此商品種類仍有商品使用，無法刪除";

      try {
        const data = await response.json();

        if (data.message) {
          errorMessage = data.message;
        }
      } catch {
        // 忽略 JSON 解析錯誤
      }

      showMessage(errorMessage, "error");

      return;
    }

    if (!response.ok) {
      showMessage("刪除商品種類失敗", "error");

      return;
    }

    // 如果目前正在篩選
    // 被刪掉的分類
    if (Number(selectedCategory.value) === Number(category.categoryId)) {
      selectedCategory.value = "";
    }

    await loadCategories();

    showMessage("商品種類刪除成功", "success");
  } catch (error) {
    console.error("刪除種類失敗：", error);

    showMessage("刪除商品種類失敗", "error");
  }
}

// =====================================================
// 價格
// =====================================================

function formatPrice(price) {
  return Number(price ?? 0).toLocaleString("zh-TW");
}

// =====================================================
// 庫存
// =====================================================

function getStockClass(stock) {
  const amount = Number(stock ?? 0);

  if (amount <= 0) {
    return "stock-empty";
  }

  if (amount <= 5) {
    return "stock-low";
  }

  return "stock-normal";
}

function getStockText(stock) {
  const amount = Number(stock ?? 0);

  if (amount <= 0) {
    return "已售完";
  }

  if (amount <= 5) {
    return "⚠ 庫存偏低";
  }

  return "庫存正常";
}

// =====================================================
// Status
// =====================================================

function getStatusText(status) {
  switch (status) {
    case "ACTIVE":
      return "上架中";

    case "INACTIVE":
      return "未上架";

    case "OUT_OF_STOCK":
      return "缺貨";

    case "DISCONTINUED":
      return "停售";

    default:
      return status || "";
  }
}

function getStatusClass(status) {
  switch (status) {
    case "ACTIVE":
      return "status-active";

    case "INACTIVE":
      return "status-inactive";

    case "OUT_OF_STOCK":
      return "status-out";

    case "DISCONTINUED":
      return "status-discontinued";

    default:
      return "";
  }
}

// =====================================================
// Watch
// =====================================================

watch([selectedCategory, selectedStatus], () => {
  currentPage.value = 1;
});

watch(pageSize, () => {
  currentPage.value = 1;
});

watch(totalPages, (total) => {
  if (currentPage.value > total) {
    currentPage.value = total;
  }
});

// =====================================================
// 初始化
// =====================================================

onMounted(async () => {
  await Promise.all([loadProducts(), loadCategories()]);
});
</script>
