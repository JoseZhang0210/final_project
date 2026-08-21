<template>
  <div>
    <!-- 頁面標題 -->
    <div class="admin-page-header">
      <div>
        <h1>商品管理</h1>

        <p>管理星澄飯店商城商品、價格、庫存與商品狀態</p>
      </div>

      <button
        type="button"
        class="admin-btn admin-btn-primary"
        @click="goAddProduct"
      >
        ＋ 新增商品
      </button>
    </div>

    <!-- 商品管理卡片 -->
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

              <td class="price">${{ formatPrice(product.price) }}</td>

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
  </div>
</template>

<script setup>
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";

const router = useRouter();

// ==============================
// 資料
// ==============================

const products = ref([]);

const keyword = ref("");

const loading = ref(false);

const message = ref("");

const messageType = ref("");

// ==============================
// JWT Header
// ==============================

function getAuthHeaders() {
  const token = localStorage.getItem("token");

  const headers = {
    "Content-Type": "application/json",
  };

  if (token) {
    headers.Authorization = "Bearer " + token;
  }

  return headers;
}

// ==============================
// 查詢全部商品
// GET /api/products
// ==============================

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
        message.value = "登入狀態已失效，請重新登入";
      } else {
        message.value = "讀取商品失敗";
      }

      messageType.value = "error";

      return;
    }

    products.value = await response.json();
  } catch (error) {
    console.error("讀取商品失敗：", error);

    message.value = "無法連接後端伺服器";

    messageType.value = "error";
  } finally {
    loading.value = false;
  }
}

// ==============================
// 模糊搜尋
// GET /api/products/search
// ==============================

async function searchProducts() {
  const searchKeyword = keyword.value.trim();

  if (searchKeyword === "") {
    loadProducts();

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
      message.value = "搜尋失敗";

      messageType.value = "error";

      return;
    }

    products.value = await response.json();
  } catch (error) {
    console.error("搜尋商品失敗：", error);

    message.value = "搜尋商品失敗";

    messageType.value = "error";
  } finally {
    loading.value = false;
  }
}

// ==============================
// 顯示全部
// ==============================

function resetSearch() {
  keyword.value = "";

  loadProducts();
}

// ==============================
// 新增商品
// ==============================

function goAddProduct() {
  router.push("/admin/products/add");
}

// ==============================
// 修改商品
// ==============================

function goEditProduct(productId) {
  router.push(`/admin/products/${productId}/edit`);
}

// ==============================
// 刪除商品
// DELETE /api/products/{id}
// ==============================

async function deleteProduct(productId) {
  const result = confirm("確定要刪除這筆商品嗎？");

  if (!result) {
    return;
  }

  try {
    const response = await fetch("/api/products/" + productId, {
      method: "DELETE",

      headers: getAuthHeaders(),
    });

    if (!response.ok) {
      message.value = "刪除失敗";

      messageType.value = "error";

      return;
    }

    message.value = "商品刪除成功";

    messageType.value = "success";

    // 如果正在搜尋
    // 刪除後重新執行搜尋
    if (keyword.value.trim()) {
      await searchProducts();
    } else {
      await loadProducts();
    }
  } catch (error) {
    console.error("刪除商品失敗：", error);

    message.value = "刪除商品失敗";

    messageType.value = "error";
  }
}

// ==============================
// 價格格式
// ==============================

function formatPrice(price) {
  const number = Number(price ?? 0);

  return number.toLocaleString("zh-TW");
}

// ==============================
// Status 中文
// ==============================

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

// ==============================
// Status CSS
// ==============================

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

// ==============================
// 頁面載入
// ==============================

onMounted(() => {
  loadProducts();
});
</script>

<style scoped>
.product-search {
  display: flex;

  gap: 10px;

  margin-bottom: 22px;

  align-items: center;
}

.product-search .admin-input {
  flex: 1;
}

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

/* =========================
   商品狀態
   ========================= */

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

@media (max-width: 700px) {
  .product-search {
    flex-direction: column;

    align-items: stretch;
  }
}
</style>
