<template>
  <div>
    <!-- 頁面標題 -->
    <div class="admin-page-header">
      <div>
        <h1>新增商品</h1>

        <p>建立新的商城商品資料</p>
      </div>
    </div>

    <!-- 新增商品表單 -->
    <section class="admin-card product-form-card">
      <!-- 訊息 -->
      <div v-if="message" class="admin-message" :class="messageType">
        {{ message }}
      </div>

      <form @submit.prevent="addProduct">
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
              placeholder="請輸入商品價格"
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
              placeholder="請輸入商品庫存"
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
            {{ saving ? "新增中..." : "新增商品" }}
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

import { useRouter } from "vue-router";

const router = useRouter();

// ==============================
// 狀態
// ==============================

const categories = ref([]);

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
// 載入商品分類
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
// 新增商品
// POST /api/products
// ==============================

async function addProduct() {
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

    const response = await fetch("/api/products", {
      method: "POST",

      headers: getAuthHeaders(),

      body: JSON.stringify(requestBody),
    });

    if (response.status === 201 || response.ok) {
      showSuccess("商品新增成功");

      setTimeout(() => {
        router.push("/admin/products");
      }, 800);

      return;
    }

    if (response.status === 401 || response.status === 403) {
      showError("沒有新增商品權限，請重新登入");

      return;
    }

    showError("新增失敗，錯誤代碼：" + response.status);
  } catch (error) {
    console.error("新增商品失敗：", error);

    showError("新增商品時發生錯誤");
  } finally {
    saving.value = false;
  }
}

// ==============================
// 返回商品列表
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

onMounted(() => {
  loadCategories();
});
</script>

<style scoped>
.product-form-card {
  max-width: 850px;
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
