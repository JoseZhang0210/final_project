<template>
  <div>
    <!-- 頁面標題 -->
    <div class="admin-page-header">
      <div>
        <h1>修改商品</h1>

        <p>修改商品基本資料、價格、庫存與商品狀態</p>
      </div>
    </div>

    <!-- 修改表單 -->
    <section class="admin-card product-form-card">
      <!-- 訊息 -->
      <div v-if="message" class="admin-message" :class="messageType">
        {{ message }}
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-message">商品資料讀取中...</div>

      <form v-else @submit.prevent="updateProduct">
        <div class="admin-form-grid">
          <!-- 商品名稱 -->
          <div class="admin-form-group full-width">
            <label for="productName"> 商品名稱 </label>

            <input
              id="productName"
              v-model="product.productName"
              type="text"
              placeholder="請輸入商品名稱"
              required
            />
          </div>

          <!-- 商品分類 -->
          <div class="admin-form-group">
            <label for="category"> 商品分類 </label>

            <select id="category" v-model="product.categoryId" required>
              <option value="">請選擇商品分類</option>

              <option
                v-for="category in categories"
                :key="category.categoryId"
                :value="category.categoryId"
              >
                {{ category.categoryName }}
              </option>
            </select>
          </div>

          <!-- 商品狀態 -->
          <div class="admin-form-group">
            <label for="status"> 商品狀態 </label>

            <select id="status" v-model="product.status">
              <option value="ACTIVE">上架</option>

              <option value="INACTIVE">下架</option>

              <option value="OUT_OF_STOCK">缺貨</option>

              <option value="DISCONTINUED">停售</option>
            </select>
          </div>

          <!-- 價格 -->
          <div class="admin-form-group">
            <label for="price"> 價格 </label>

            <input
              id="price"
              v-model.number="product.price"
              type="number"
              min="0"
              step="0.01"
              required
            />
          </div>

          <!-- 庫存 -->
          <div class="admin-form-group">
            <label for="stock"> 庫存 </label>

            <input
              id="stock"
              v-model.number="product.stock"
              type="number"
              min="0"
              required
            />
          </div>

          <!-- 圖片網址 -->
          <div class="admin-form-group full-width">
            <label for="imageUrl"> 圖片網址 </label>

            <input
              id="imageUrl"
              v-model="product.imageUrl"
              type="text"
              placeholder="請輸入商品圖片網址"
            />
          </div>

          <!-- 商品描述 -->
          <div class="admin-form-group full-width">
            <label for="description"> 商品描述 </label>

            <textarea
              id="description"
              v-model="product.description"
              placeholder="請輸入商品描述"
            ></textarea>
          </div>
        </div>

        <!-- 按鈕 -->
        <div class="admin-form-actions">
          <button
            type="submit"
            class="admin-btn admin-btn-primary"
            :disabled="saving"
          >
            {{ saving ? "儲存中..." : "儲存修改" }}
          </button>

          <button
            type="button"
            class="admin-btn admin-btn-secondary"
            @click="backToList"
          >
            返回商品列表
          </button>
        </div>
      </form>
    </section>
  </div>
</template>

<script setup>
import { onMounted, reactive, ref } from "vue";

import { useRoute, useRouter } from "vue-router";

const route = useRoute();

const router = useRouter();

// ==============================
// Product ID
// ==============================

const productId = route.params.id;

// ==============================
// 狀態
// ==============================

const categories = ref([]);

const loading = ref(true);

const saving = ref(false);

const message = ref("");

const messageType = ref("");

// ==============================
// 商品資料
// ==============================

const product = reactive({
  productName: "",

  categoryId: "",

  description: "",

  price: 0,

  stock: 0,

  imageUrl: "",

  status: "ACTIVE",
});

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
// 讀取分類
// GET /api/categories
// ==============================

async function loadCategories() {
  try {
    const response = await fetch("/api/categories", {
      method: "GET",

      headers: getAuthHeaders(),
    });

    if (!response.ok) {
      throw new Error("分類讀取失敗");
    }

    categories.value = await response.json();
  } catch (error) {
    console.error("分類讀取失敗：", error);

    showError("分類資料讀取失敗");
  }
}

// ==============================
// 讀取單一商品
// GET /api/products/{id}
// ==============================

async function loadProduct() {
  try {
    const response = await fetch(`/api/products/${productId}`, {
      method: "GET",

      headers: getAuthHeaders(),
    });

    if (response.status === 404) {
      showError("找不到這筆商品資料");

      return;
    }

    if (!response.ok) {
      throw new Error("商品資料讀取失敗");
    }

    const data = await response.json();

    product.productName = data.productName ?? "";

    product.description = data.description ?? "";

    product.price = Number(data.price ?? 0);

    product.stock = Number(data.stock ?? 0);

    product.imageUrl = data.imageUrl ?? "";

    product.status = data.status ?? "ACTIVE";

    if (data.category) {
      product.categoryId = data.category.categoryId;
    }
  } catch (error) {
    console.error("商品資料讀取失敗：", error);

    showError("讀取商品資料時發生錯誤");
  }
}

// ==============================
// 修改商品
// PUT /api/products/{id}
// ==============================

async function updateProduct() {
  message.value = "";

  saving.value = true;

  try {
    const requestBody = {
      productName: product.productName,

      category: {
        categoryId: Number(product.categoryId),
      },

      description: product.description,

      price: Number(product.price),

      stock: Number(product.stock),

      imageUrl: product.imageUrl,

      status: product.status,
    };

    const response = await fetch(`/api/products/${productId}`, {
      method: "PUT",

      headers: getAuthHeaders(),

      body: JSON.stringify(requestBody),
    });

    if (response.ok) {
      showSuccess("商品修改成功");

      setTimeout(() => {
        router.push("/admin/products");
      }, 800);

      return;
    }

    if (response.status === 404) {
      showError("找不到這筆商品");

      return;
    }

    if (response.status === 401 || response.status === 403) {
      showError("沒有權限修改商品，請重新登入");

      return;
    }

    showError("修改失敗，錯誤代碼：" + response.status);
  } catch (error) {
    console.error("修改商品失敗：", error);

    showError("修改商品時發生錯誤");
  } finally {
    saving.value = false;
  }
}

// ==============================
// 回商品列表
// ==============================

function backToList() {
  router.push("/admin/products");
}

// ==============================
// 顯示錯誤
// ==============================

function showError(text) {
  message.value = text;

  messageType.value = "error";
}

// ==============================
// 顯示成功
// ==============================

function showSuccess(text) {
  message.value = text;

  messageType.value = "success";
}

// ==============================
// 頁面載入
// ==============================

onMounted(async () => {
  if (!productId) {
    showError("網址中沒有商品 ID");

    loading.value = false;

    return;
  }

  await loadCategories();

  await loadProduct();

  loading.value = false;
});
</script>

<style scoped>
.product-form-card {
  max-width: 850px;
}

.loading-message {
  padding: 40px;

  text-align: center;

  color: #888;
}

.admin-form-actions {
  margin-top: 28px;
}

.admin-btn:disabled {
  opacity: 0.65;

  cursor: not-allowed;

  transform: none;
}

@media (max-width: 700px) {
  .product-form-card {
    max-width: 100%;
  }
}
</style>
