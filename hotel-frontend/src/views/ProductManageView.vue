<template>
  <div class="product-manage-page">
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
<style>
/* =========================================================
   ProductManageView
   商品管理
   ========================================================= */

/* =========================================================
   Header
   ========================================================= */

.product-manage-page .product-header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

/* =========================================================
   搜尋 / 篩選
   ========================================================= */

.product-manage-page .product-search {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.product-manage-page .search-input {
  flex: 1;
  min-width: 280px;
}

.product-manage-page .filter-select {
  width: 160px;
  min-width: 150px;
  flex: none !important;
  background-color: white;
  cursor: pointer;
}

/* =========================================================
   表格控制列
   ========================================================= */

.product-manage-page .table-control-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.product-manage-page .filter-summary {
  color: #8b8176;
  font-size: 13px;
}

.product-manage-page .filter-summary strong {
  margin: 0 3px;
  color: #b58a46;
  font-size: 16px;
}

.product-manage-page .page-size-area {
  display: flex;
  align-items: center;
  gap: 7px;
  color: #756a60;
  font-size: 13px;
}

.product-manage-page .page-size-select {
  padding: 6px 10px;

  border: 1px solid #d8d0c5;
  border-radius: 6px;

  background-color: white;

  cursor: pointer;
}

/* =========================================================
   Table
   ========================================================= */

.product-manage-page .product-name {
  color: #5b4632;
  font-weight: bold;
}

.product-manage-page .category-text {
  color: #675b50;
}

.product-manage-page .price {
  color: #9b7435;
  font-weight: bold;
  white-space: nowrap;
}

.product-manage-page .description-cell {
  max-width: 240px;

  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-manage-page .action-buttons {
  display: flex;
  gap: 7px;
  white-space: nowrap;
}

.product-manage-page .loading-message,
.product-manage-page .empty-message {
  padding: 35px;

  text-align: center;

  color: #888;
}

/* =========================================================
   排序表頭
   ========================================================= */

.product-manage-page .sortable {
  cursor: pointer;
  user-select: none;
  transition: background-color 0.2s;
}

.product-manage-page .sortable:hover {
  background-color: #eee8df;
}

.product-manage-page .sort-icon {
  display: inline-block;

  margin-left: 4px;

  color: #b58a46;

  font-size: 10px;
}

/* =========================================================
   商品圖片
   ========================================================= */

.product-manage-page .product-thumbnail-wrap {
  width: 68px;
  height: 68px;

  overflow: hidden;

  border: 1px solid #e5ded5;
  border-radius: 8px;

  background-color: #f5f2ed;
}

.product-manage-page .product-thumbnail {
  width: 100%;
  height: 100%;

  display: block;

  object-fit: cover;

  transition: transform 0.25s;
}

.product-manage-page .product-thumbnail:hover {
  transform: scale(1.08);
}

/* =========================================================
   庫存
   ========================================================= */

.product-manage-page .stock-area {
  display: flex;
  flex-direction: column;
  align-items: center;

  gap: 5px;

  min-width: 95px;
}

.product-manage-page .stock-badge {
  display: inline-block;

  padding: 5px 9px;

  border-radius: 20px;

  font-size: 11px;
  font-weight: bold;

  white-space: nowrap;
}

/* 庫存正常 */

.product-manage-page .stock-normal {
  background-color: #e7f5eb;
  color: #267243;
}

/* 庫存偏低 */

.product-manage-page .stock-low {
  background-color: #fff2d8;
  color: #a36b16;
}

/* 無庫存 */

.product-manage-page .stock-empty {
  background-color: #fde8e6;
  color: #b3443c;
}

.product-manage-page .stock-number {
  color: #92887d;
  font-size: 11px;
}

/* =========================================================
   商品狀態
   ========================================================= */

.product-manage-page .status-badge {
  display: inline-block;

  padding: 5px 10px;

  border-radius: 20px;

  font-size: 12px;
  font-weight: bold;

  white-space: nowrap;
}

/* 上架 */

.product-manage-page .status-active {
  background-color: #e5f6eb;
  color: #257641;
}

/* 未上架 */

.product-manage-page .status-inactive {
  background-color: #eee9e1;
  color: #6f655a;
}

/* 缺貨 */

.product-manage-page .status-out {
  background-color: #fff3d8;
  color: #95691f;
}

/* 停售 */

.product-manage-page .status-discontinued {
  background-color: #fde9e7;
  color: #b3443c;
}

/* =========================================================
   快速上下架
   ========================================================= */

.product-manage-page .admin-btn-status {
  border: none;

  background-color: #edf2fb;

  color: #3e6091;
}

.product-manage-page .admin-btn-status:hover {
  background-color: #dce7f7;
}

/* =========================================================
   分頁
   ========================================================= */

.product-manage-page .pagination-area {
  display: flex;

  justify-content: space-between;
  align-items: center;

  gap: 20px;

  margin-top: 24px;
  padding-top: 20px;

  border-top: 1px solid #eee7de;
}

.product-manage-page .pagination-info {
  color: #857a70;

  font-size: 13px;
}

.product-manage-page .pagination-info strong {
  color: #9b7435;
}

.product-manage-page .pagination {
  display: flex;

  align-items: center;

  gap: 6px;
}

.product-manage-page .page-button {
  min-width: 36px;
  height: 36px;

  padding: 0 10px;

  border: 1px solid #ded5c9;
  border-radius: 6px;

  background-color: white;

  color: #625649;

  cursor: pointer;

  transition: 0.2s;
}

.product-manage-page .page-button:hover:not(:disabled) {
  border-color: #b58a46;

  color: #9b7435;
}

.product-manage-page .page-button.active {
  border-color: #b58a46;

  background-color: #b58a46;

  color: white;

  font-weight: bold;
}

.product-manage-page .page-button:disabled {
  background-color: #f2f0ec;

  color: #bbb5ad;

  cursor: not-allowed;
}

/* =========================================================
   Category Modal 背景
   ========================================================= */

.product-manage-page .category-modal {
  position: fixed;

  inset: 0;

  z-index: 2000;

  display: flex;

  justify-content: center;
  align-items: center;

  padding: 20px;

  background-color: rgba(47, 42, 36, 0.55);
}

/* =========================================================
   Category Modal 卡片
   ========================================================= */

.product-manage-page .category-manage-card {
  width: min(650px, 94vw);

  max-height: 82vh;

  display: flex;

  flex-direction: column;

  overflow: hidden;

  background-color: white;

  border-radius: 14px;

  box-shadow: 0 18px 55px rgba(0, 0, 0, 0.25);
}

/* =========================================================
   Category Modal Header
   ========================================================= */

.product-manage-page .category-modal-header {
  display: flex;

  justify-content: space-between;
  align-items: center;

  padding: 20px 24px;

  background-color: #4a3b2a;

  color: white;
}

.product-manage-page .category-modal-header h2 {
  margin: 0;

  font-size: 21px;
}

.product-manage-page .category-modal-header p {
  margin: 5px 0 0;

  color: rgba(255, 255, 255, 0.7);

  font-size: 12px;
}

.product-manage-page .modal-close {
  padding: 0 6px;

  border: none;

  background: transparent;

  color: white;

  font-size: 28px;
  line-height: 1;

  cursor: pointer;
}

/* =========================================================
   Category 新增
   ========================================================= */

.product-manage-page .category-create-area {
  padding: 22px 24px 18px;
}

.product-manage-page .category-create-area label {
  display: block;

  margin-bottom: 9px;

  color: #554536;

  font-size: 13px;
  font-weight: bold;
}

.product-manage-page .category-create-row {
  display: flex;

  gap: 10px;
}

.product-manage-page .category-create-row input {
  flex: 1;

  min-width: 0;

  padding: 11px 13px;

  border: 1px solid #d8d0c5;
  border-radius: 7px;

  font-size: 14px;
}

.product-manage-page .category-create-row input:focus,
.product-manage-page .category-edit-input:focus {
  outline: none;

  border-color: #b58a46;

  box-shadow: 0 0 0 3px rgba(181, 138, 70, 0.13);
}

.product-manage-page .category-divider {
  height: 1px;

  margin: 0 24px;

  background-color: #eee8df;
}

/* =========================================================
   Category List
   ========================================================= */

.product-manage-page .category-list-area {
  flex: 1;

  overflow-y: auto;

  padding: 18px 24px;
}

.product-manage-page .category-list-title {
  display: flex;

  justify-content: space-between;
  align-items: center;

  margin-bottom: 12px;

  color: #574b40;

  font-weight: bold;
}

.product-manage-page .category-count {
  padding: 4px 9px;

  border-radius: 15px;

  background-color: #f2ece3;

  color: #9b7435;

  font-size: 11px;
}

.product-manage-page .category-manage-row {
  min-height: 58px;

  display: flex;

  justify-content: space-between;
  align-items: center;

  gap: 15px;

  padding: 10px 2px;

  border-bottom: 1px solid #eee9e2;
}

.product-manage-page .category-info {
  display: flex;

  align-items: center;

  gap: 12px;
}

.product-manage-page .category-id {
  width: 42px;

  color: #aaa198;

  font-size: 12px;
}

.product-manage-page .category-info strong {
  color: #51463b;

  font-size: 14px;
}

/* =========================================================
   Category 操作按鈕
   ========================================================= */

.product-manage-page .category-actions {
  display: flex;

  gap: 7px;
}

.product-manage-page .category-actions button {
  padding: 6px 11px;

  border: none;
  border-radius: 5px;

  font-size: 12px;

  cursor: pointer;

  transition: 0.2s;
}

.product-manage-page .category-edit-button {
  background-color: #edf2fb;

  color: #3d6092;
}

.product-manage-page .category-edit-button:hover {
  background-color: #dce7f7;
}

.product-manage-page .category-delete-button {
  background-color: #fdebea;

  color: #b24842;
}

.product-manage-page .category-delete-button:hover {
  background-color: #f8d8d5;
}

.product-manage-page .category-save-button {
  background-color: #e5f6eb;

  color: #257641;
}

.product-manage-page .category-save-button:hover {
  background-color: #d3eddd;
}

.product-manage-page .category-cancel-button {
  background-color: #eeeae4;

  color: #6f655a;
}

.product-manage-page .category-cancel-button:hover {
  background-color: #e3ddd4;
}

/* =========================================================
   Category 修改輸入框
   ========================================================= */

.product-manage-page .category-edit-input {
  flex: 1;

  padding: 9px 11px;

  border: 1px solid #d8d0c5;
  border-radius: 6px;

  font-size: 14px;
}

.product-manage-page .category-empty {
  padding: 35px;

  color: #999;

  text-align: center;
}

/* =========================================================
   Category Modal Footer
   ========================================================= */

.product-manage-page .category-modal-footer {
  display: flex;

  justify-content: flex-end;

  padding: 15px 24px 20px;

  border-top: 1px solid #eee8df;
}

/* =========================================================
   ProductManageView RWD
   ========================================================= */

@media (max-width: 1100px) {
  .product-manage-page .admin-table {
    min-width: 1150px;
  }
}

@media (max-width: 800px) {
  .product-manage-page .search-input {
    width: 100%;

    flex-basis: 100%;
  }

  .product-manage-page .table-control-bar {
    align-items: flex-start;

    flex-direction: column;
  }
}

@media (max-width: 700px) {
  .product-manage-page .admin-page-header {
    align-items: stretch;

    flex-direction: column;
  }

  .product-manage-page .product-header-actions {
    width: 100%;
  }

  .product-manage-page .product-header-actions .admin-btn {
    flex: 1;

    text-align: center;
  }

  .product-manage-page .product-search {
    flex-direction: column;

    align-items: stretch;
  }

  .product-manage-page .search-input,
  .product-manage-page .filter-select {
    width: 100%;

    min-width: 0;
  }

  .product-manage-page .pagination-area {
    flex-direction: column;

    align-items: flex-start;
  }

  .product-manage-page .pagination {
    max-width: 100%;

    overflow-x: auto;

    padding-bottom: 5px;
  }
}

@media (max-width: 600px) {
  .product-manage-page .category-create-row {
    flex-direction: column;
  }

  .product-manage-page .category-manage-row {
    align-items: stretch;

    flex-direction: column;
  }

  .product-manage-page .category-actions {
    justify-content: flex-end;
  }

  .product-manage-page .category-edit-input {
    width: 100%;

    box-sizing: border-box;
  }
}
</style>
