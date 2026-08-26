<template>
  <div>
    <!-- =========================
         頁面標題
         ========================= -->
    <div class="admin-page-header">
      <div>
        <h1>商品管理</h1>

        <p>管理商城商品資料與商品分類</p>
      </div>

      <div class="product-header-actions">
        <!-- 新增商品 -->
        <RouterLink
          to="/admin/products/add"
          class="admin-btn admin-btn-primary"
        >
          ＋ 新增商品
        </RouterLink>

        <!-- 新增種類 -->
        <button
          type="button"
          class="admin-btn admin-btn-secondary"
          @click="openCategoryModal"
        >
          ＋ 新增種類
        </button>
      </div>
    </div>

    <!-- =========================
         商品管理卡片
         ========================= -->
    <section class="admin-card">
      <!-- 搜尋 -->
      <div class="product-search">
        <input
          v-model="keyword"
          type="text"
          class="admin-input"
          placeholder="請輸入商品名稱搜尋"
          @keyup.enter="searchProducts"
        />

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
          顯示全部
        </button>
      </div>

      <!-- 訊息 -->
      <div v-if="message" class="admin-message" :class="messageType">
        {{ message }}
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-message">商品資料讀取中...</div>

      <!-- 商品表格 -->
      <div v-else class="admin-table-wrapper">
        <table class="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>商品名稱</th>
              <th>分類</th>
              <th>商品描述</th>
              <th>價格</th>
              <th>庫存</th>
              <th>狀態</th>
              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <!-- 沒資料 -->
            <tr v-if="products.length === 0">
              <td colspan="8" class="empty-message">查無商品資料</td>
            </tr>

            <!-- 商品資料 -->
            <tr v-for="product in products" :key="product.productId">
              <td>
                {{ product.productId }}
              </td>

              <td class="product-name">
                {{ product.productName }}
              </td>

              <td>
                {{ product.category?.categoryName ?? "" }}
              </td>

              <td class="description-cell">
                {{ product.description || "" }}
              </td>

              <td class="price">
                $
                {{ formatPrice(product.price) }}
              </td>

              <td>
                {{ product.stock ?? 0 }}
              </td>

              <td>
                <span
                  class="status-badge"
                  :class="getStatusClass(product.status)"
                >
                  {{ getStatusText(product.status) }}
                </span>
              </td>

              <td>
                <div class="action-buttons">
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
                    @click="deleteProduct(product.productId)"
                  >
                    刪除
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <!-- =====================================================
         新增商品種類 Modal
         ===================================================== -->

    <div
      v-if="categoryModalOpen"
      class="category-modal"
      @click.self="closeCategoryModal"
    >
      <div class="category-modal-card">
        <!-- Header -->
        <div class="category-modal-header">
          <h2>新增商品種類</h2>

          <button type="button" class="modal-close" @click="closeCategoryModal">
            ×
          </button>
        </div>

        <!-- Body -->
        <div class="category-modal-body">
          <label for="newCategoryName"> 商品種類名稱 </label>

          <input
            id="newCategoryName"
            v-model="newCategoryName"
            type="text"
            placeholder="例如：房內用品"
            maxlength="50"
            @keyup.enter="addCategory"
          />

          <p class="category-help">輸入新的商品分類名稱後按新增即可。</p>
        </div>

        <!-- Actions -->
        <div class="category-modal-actions">
          <button
            type="button"
            class="admin-btn admin-btn-secondary"
            @click="closeCategoryModal"
          >
            取消
          </button>

          <button
            type="button"
            class="admin-btn admin-btn-primary"
            :disabled="categorySaving"
            @click="addCategory"
          >
            {{ categorySaving ? "新增中..." : "新增種類" }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";

import { useRouter } from "vue-router";

import { getAuthHeaders } from "@/utils/auth";

const router = useRouter();

// =====================================================
// 商品資料
// =====================================================

const products = ref([]);

const keyword = ref("");

const loading = ref(false);

const message = ref("");

const messageType = ref("");

// =====================================================
// 商品種類 Modal
// =====================================================

const categoryModalOpen = ref(false);

const newCategoryName = ref("");

const categorySaving = ref(false);

// =====================================================
// 訊息
// =====================================================

function showMessage(text, type) {
  message.value = text;

  messageType.value = type;

  setTimeout(() => {
    message.value = "";
  }, 2500);
}

// =====================================================
// 查詢全部商品
// GET /api/products
// =====================================================

async function loadProducts() {
  loading.value = true;

  message.value = "";

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
// 模糊搜尋
// GET /api/products/search
// =====================================================

async function searchProducts() {
  const searchKeyword = keyword.value.trim();

  if (searchKeyword === "") {
    await loadProducts();

    return;
  }

  loading.value = true;

  message.value = "";

  try {
    const response = await fetch(
      "/api/products/search?keyword=" + encodeURIComponent(searchKeyword),
      {
        method: "GET",

        headers: getAuthHeaders(),
      },
    );

    if (!response.ok) {
      showMessage("搜尋失敗", "error");

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
// 顯示全部
// =====================================================

function resetSearch() {
  keyword.value = "";

  loadProducts();
}

// =====================================================
// 修改商品
// =====================================================

function goEditProduct(productId) {
  router.push(`/admin/products/${productId}/edit`);
}

// =====================================================
// 刪除商品
// DELETE /api/products/{id}
// =====================================================

async function deleteProduct(productId) {
  const result = window.confirm("確定要刪除這筆商品嗎？");

  if (!result) {
    return;
  }

  try {
    const response = await fetch(`/api/products/${productId}`, {
      method: "DELETE",

      headers: getAuthHeaders(),
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("沒有刪除商品的權限", "error");

      return;
    }

    if (!response.ok) {
      showMessage("刪除失敗", "error");

      return;
    }

    showMessage("商品刪除成功", "success");

    if (keyword.value.trim()) {
      await searchProducts();
    } else {
      await loadProducts();
    }
  } catch (error) {
    console.error("刪除商品失敗：", error);

    showMessage("刪除商品失敗", "error");
  }
}

// =====================================================
// 開啟新增種類 Modal
// =====================================================

function openCategoryModal() {
  newCategoryName.value = "";

  categoryModalOpen.value = true;

  // 等 Modal 出現後自動聚焦
  setTimeout(() => {
    document.getElementById("newCategoryName")?.focus();
  }, 50);
}

// =====================================================
// 關閉新增種類 Modal
// =====================================================

function closeCategoryModal() {
  categoryModalOpen.value = false;

  newCategoryName.value = "";
}

// =====================================================
// 新增商品種類
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
        categoryName: categoryName,
      }),
    });

    // ==========================
    // 權限問題
    // ==========================

    if (response.status === 401 || response.status === 403) {
      showMessage("沒有新增商品種類的權限", "error");

      return;
    }

    // ==========================
    // 重複
    // ==========================

    if (response.status === 409) {
      showMessage("這個商品種類已經存在", "error");

      return;
    }

    // ==========================
    // 其他錯誤
    // ==========================

    if (!response.ok) {
      const text = await response.text();

      console.error("新增商品種類失敗：", text);

      showMessage("新增商品種類失敗", "error");

      return;
    }

    const data = await response.json();

    console.log("新增種類成功：", data);

    closeCategoryModal();

    showMessage("商品種類新增成功", "success");
  } catch (error) {
    console.error("新增商品種類錯誤：", error);

    showMessage("新增商品種類時發生錯誤", "error");
  } finally {
    categorySaving.value = false;
  }
}

// =====================================================
// 價格格式
// =====================================================

function formatPrice(price) {
  const number = Number(price ?? 0);

  return number.toLocaleString("zh-TW");
}

// =====================================================
// Status 中文
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

// =====================================================
// Status CSS
// =====================================================

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
// 頁面載入
// =====================================================

onMounted(() => {
  loadProducts();
});
</script>

<style scoped>
/* =====================================================
   Header
   ===================================================== */

.product-header-actions {
  display: flex;

  align-items: center;

  gap: 10px;

  flex-wrap: wrap;
}

/* =====================================================
   搜尋
   ===================================================== */

.product-search {
  display: flex;

  gap: 10px;

  margin-bottom: 22px;

  align-items: center;
}

.product-search .admin-input {
  flex: 1;
}

/* =====================================================
   Table
   ===================================================== */

.product-name {
  color: #5b4632;

  font-weight: bold;
}

.price {
  color: #9b7435;

  font-weight: bold;
}

.description-cell {
  max-width: 300px;

  white-space: nowrap;

  overflow: hidden;

  text-overflow: ellipsis;
}

.action-buttons {
  display: flex;

  gap: 7px;

  white-space: nowrap;
}

.loading-message,
.empty-message {
  padding: 35px;

  text-align: center;

  color: #888;
}

/* =====================================================
   商品狀態
   ===================================================== */

.status-badge {
  display: inline-block;

  padding: 5px 10px;

  border-radius: 20px;

  font-size: 12px;

  font-weight: bold;
}

.status-active {
  background-color: #e5f6eb;

  color: #257641;
}

.status-inactive {
  background-color: #eee9e1;

  color: #6f655a;
}

.status-out {
  background-color: #fff3d8;

  color: #95691f;
}

.status-discontinued {
  background-color: #fde9e7;

  color: #b3443c;
}

/* =====================================================
   新增種類 Modal
   ===================================================== */

.category-modal {
  position: fixed;

  inset: 0;

  z-index: 2000;

  display: flex;

  justify-content: center;

  align-items: center;

  padding: 20px;

  background-color: rgba(47, 42, 36, 0.55);
}

.category-modal-card {
  width: min(460px, 92vw);

  overflow: hidden;

  background-color: white;

  border-radius: 14px;

  box-shadow: 0 16px 50px rgba(0, 0, 0, 0.22);
}

.category-modal-header {
  display: flex;

  justify-content: space-between;

  align-items: center;

  padding: 20px 24px;

  background-color: #4a3b2a;

  color: white;
}

.category-modal-header h2 {
  margin: 0;

  font-size: 21px;
}

.modal-close {
  padding: 0 6px;

  border: none;

  background: transparent;

  color: white;

  font-size: 28px;

  line-height: 1;

  cursor: pointer;
}

/* =====================================================
   Modal Body
   ===================================================== */

.category-modal-body {
  padding: 24px;
}

.category-modal-body label {
  display: block;

  margin-bottom: 8px;

  color: #554536;

  font-size: 14px;

  font-weight: bold;
}

.category-modal-body input {
  width: 100%;

  padding: 12px 14px;

  border: 1px solid #d8d0c5;

  border-radius: 8px;

  box-sizing: border-box;

  font-size: 15px;

  font-family: inherit;

  transition: 0.2s;
}

.category-modal-body input:focus {
  outline: none;

  border-color: #b58a46;

  box-shadow: 0 0 0 3px rgba(181, 138, 70, 0.14);
}

.category-help {
  margin: 9px 0 0;

  color: #888;

  font-size: 12px;
}

/* =====================================================
   Modal Buttons
   ===================================================== */

.category-modal-actions {
  display: flex;

  justify-content: flex-end;

  gap: 10px;

  padding: 0 24px 24px;
}

.category-modal-actions .admin-btn:disabled {
  opacity: 0.6;

  cursor: not-allowed;
}

/* =====================================================
   RWD
   ===================================================== */

@media (max-width: 700px) {
  .admin-page-header {
    align-items: stretch;

    flex-direction: column;
  }

  .product-header-actions {
    width: 100%;
  }

  .product-header-actions .admin-btn {
    flex: 1;

    text-align: center;
  }

  .product-search {
    flex-direction: column;

    align-items: stretch;
  }

  .category-modal-actions {
    flex-direction: column-reverse;
  }

  .category-modal-actions .admin-btn {
    width: 100%;
  }
}
</style>
