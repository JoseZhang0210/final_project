<template>
  <div class="shop-page">
    <!-- =========================
         搜尋區
         ========================= -->
    <section class="shop-top">
      <div class="shop-search-wrap">
        <div class="shop-title">星澄飯店商城</div>

        <div class="shop-search">
          <input
            v-model="keyword"
            type="text"
            placeholder="搜尋商品名稱..."
            @keyup.enter="searchProducts"
          />

          <button type="button" @click="searchProducts">搜尋</button>
        </div>
      </div>
    </section>

    <!-- =========================
         商品分類
         ========================= -->
    <section class="category-section">
      <div class="category-list">
        <button
          type="button"
          class="category-button"
          :class="{
            active: selectedCategory === null,
          }"
          @click="selectCategory(null)"
        >
          全部商品
        </button>

        <button
          v-for="category in categories"
          :key="category.categoryId"
          type="button"
          class="category-button"
          :class="{
            active: selectedCategory === category.categoryId,
          }"
          @click="selectCategory(category.categoryId)"
        >
          {{ category.categoryName }}
        </button>
      </div>
    </section>

    <!-- =========================
         Banner
         ========================= -->
    <section class="shop-banner">
      <div class="banner-content">
        <span class="banner-small"> HOTEL SHOP </span>

        <h1>星澄飯店嚴選商城</h1>

        <p>精選飯店好物、質感生活用品與限定商品</p>

        <button type="button" @click="scrollToProducts">立即選購</button>
      </div>
    </section>

    <!-- =========================
         商品區
         ========================= -->
    <main ref="productSection" class="product-section">
      <div class="product-section-header">
        <div>
          <h2>
            {{ selectedCategoryName }}
          </h2>

          <p>共 {{ filteredProducts.length }} 件商品</p>
        </div>
      </div>

      <!-- API 錯誤 -->
      <div v-if="errorMessage" class="error-message">
        {{ errorMessage }}
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-message">商品資料讀取中...</div>

      <!-- 查無商品 -->
      <div
        v-else-if="!errorMessage && filteredProducts.length === 0"
        class="empty-message"
      >
        查無商品資料
      </div>

      <!-- =========================
           商品 Grid
           ========================= -->
      <div v-else-if="!errorMessage" class="product-grid">
        <article
          v-for="product in filteredProducts"
          :key="product.productId"
          class="product-card"
          @click="goProductDetail(product.productId)"
        >
          <!-- 商品圖片 -->
          <div class="product-image-wrap">
            <img
              :src="getProductImage(product)"
              :alt="product.productName"
              class="product-image"
              @error="handleImageError"
            />

            <!-- 缺貨 -->
            <span
              v-if="
                product.status === 'OUT_OF_STOCK' || Number(product.stock) <= 0
              "
              class="product-badge sold-out"
            >
              缺貨
            </span>

            <!-- 庫存少 -->
            <span
              v-else-if="Number(product.stock) <= 5"
              class="product-badge stock-low"
            >
              即將售完
            </span>
          </div>

          <!-- 商品內容 -->
          <div class="product-info">
            <div class="product-category">
              {{ product.category?.categoryName || "飯店商城" }}
            </div>

            <h3 class="product-name">
              {{ product.productName }}
            </h3>

            <p class="product-description">
              {{ product.description || "星澄飯店精選商品" }}
            </p>

            <div class="product-bottom">
              <!-- 價格 -->
              <div class="price-area">
                <span class="currency"> $ </span>

                <strong>
                  {{ formatPrice(product.price) }}
                </strong>
              </div>

              <!-- 庫存 -->
              <div class="stock-text">
                庫存
                {{ product.stock ?? 0 }}
              </div>
            </div>

            <button
              type="button"
              class="product-button"
              :disabled="!canBuy(product)"
              @click.stop="goProductDetail(product.productId)"
            >
              {{ canBuy(product) ? "查看商品" : "暫無法購買" }}
            </button>
          </div>
        </article>
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";

import { useRouter } from "vue-router";

const router = useRouter();

// ==============================
// 商品資料
// ==============================

const products = ref([]);

// ==============================
// 商品分類
// ==============================

const categories = ref([]);

// ==============================
// 搜尋文字
// ==============================

const keyword = ref("");

// ==============================
// 選擇分類
// null = 全部商品
// ==============================

const selectedCategory = ref(null);

// ==============================
// Loading
// ==============================

const loading = ref(false);

// ==============================
// 錯誤訊息
// ==============================

const errorMessage = ref("");

// ==============================
// 商品區 DOM
// ==============================

const productSection = ref(null);

// =====================================================
// JWT Header
// =====================================================

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

// =====================================================
// 讀取所有商品
// GET /api/products
// =====================================================

async function loadProducts() {
  loading.value = true;

  errorMessage.value = "";

  try {
    const response = await fetch("/api/products", {
      method: "GET",

      headers: getAuthHeaders(),
    });

    console.log("商品 API status：", response.status);

    if (response.status === 401 || response.status === 403) {
      throw new Error("沒有權限讀取商品資料，請重新登入");
    }

    if (!response.ok) {
      throw new Error("商品讀取失敗，狀態碼：" + response.status);
    }

    const data = await response.json();

    console.log("前台取得商品資料：", data);

    /*
     * 前台顯示商品規則：
     *
     * ACTIVE
     * OUT_OF_STOCK
     *
     * 不顯示：
     * INACTIVE
     * DISCONTINUED
     *
     * 如果你目前資料庫 status
     * 不是英文 enum，
     * 這裡也支援「上架」「上架中」「缺貨」
     */

    products.value = data.filter((product) => {
      const status = product.status;

      return (
        status === "ACTIVE" ||
        status === "OUT_OF_STOCK" ||
        status === "上架" ||
        status === "上架中" ||
        status === "缺貨"
      );
    });

    console.log("前台實際顯示商品：", products.value);
  } catch (error) {
    console.error("商品讀取失敗：", error);

    errorMessage.value = error.message || "商品讀取失敗";

    products.value = [];
  } finally {
    loading.value = false;
  }
}

// =====================================================
// 讀取商品分類
// GET /api/categories
// =====================================================

async function loadCategories() {
  try {
    const response = await fetch("/api/categories", {
      method: "GET",

      headers: getAuthHeaders(),
    });

    console.log("分類 API status：", response.status);

    if (response.status === 401 || response.status === 403) {
      throw new Error("沒有權限讀取商品分類");
    }

    if (!response.ok) {
      throw new Error("分類讀取失敗，狀態碼：" + response.status);
    }

    categories.value = await response.json();

    console.log("商品分類：", categories.value);
  } catch (error) {
    console.error("分類讀取失敗：", error);

    categories.value = [];
  }
}

// =====================================================
// 商品篩選
// =====================================================

const filteredProducts = computed(() => {
  let result = [...products.value];

  // ==========================
  // 分類篩選
  // ==========================

  if (selectedCategory.value !== null) {
    result = result.filter(
      (product) =>
        Number(product.category?.categoryId) === Number(selectedCategory.value),
    );
  }

  // ==========================
  // 關鍵字搜尋
  // ==========================

  const searchText = keyword.value.trim().toLowerCase();

  if (searchText) {
    result = result.filter((product) => {
      const productName = (product.productName || "").toLowerCase();

      const description = (product.description || "").toLowerCase();

      return (
        productName.includes(searchText) || description.includes(searchText)
      );
    });
  }

  return result;
});

// =====================================================
// 分類標題
// =====================================================

const selectedCategoryName = computed(() => {
  if (selectedCategory.value === null) {
    return "全部商品";
  }

  const category = categories.value.find(
    (item) => Number(item.categoryId) === Number(selectedCategory.value),
  );

  if (category) {
    return category.categoryName;
  }

  return "商品列表";
});

// =====================================================
// 搜尋
// =====================================================

function searchProducts() {
  scrollToProducts();
}

// =====================================================
// 選分類
// =====================================================

function selectCategory(categoryId) {
  selectedCategory.value = categoryId;

  scrollToProducts();
}

// =====================================================
// 商品圖片
// =====================================================

function getProductImage(product) {
  if (product.imageUrl && product.imageUrl.trim() !== "") {
    return product.imageUrl;
  }

  return "/images/products/default-product.jpg";
}

function handleImageError(event) {
  const defaultImage = "/images/products/default-product.jpg";

  if (event.target.src.endsWith(defaultImage)) {
    return;
  }

  event.target.src = defaultImage;
}

// =====================================================
// 商品價格格式
// =====================================================

function formatPrice(price) {
  return Number(price ?? 0).toLocaleString("zh-TW");
}

// =====================================================
// 是否可以購買
// =====================================================

function canBuy(product) {
  const status = product.status;

  const active =
    status === "ACTIVE" || status === "上架" || status === "上架中";

  return active && Number(product.stock ?? 0) > 0;
}

// =====================================================
// 商品詳細頁
// =====================================================

function goProductDetail(productId) {
  router.push(`/products/${productId}`);
}

// =====================================================
// 捲動到商品區
// =====================================================

function scrollToProducts() {
  setTimeout(() => {
    productSection.value?.scrollIntoView({
      behavior: "smooth",

      block: "start",
    });
  }, 50);
}

// =====================================================
// 頁面初始化
// =====================================================

onMounted(async () => {
  console.log("目前 JWT：", localStorage.getItem("token"));

  await Promise.all([loadProducts(), loadCategories()]);
});
</script>

<style scoped>
/* =====================================================
   整體商城
   ===================================================== */

.shop-page {
  min-height: 100vh;

  background-color: #f5f5f5;

  color: #333;
}

/* =====================================================
   搜尋
   ===================================================== */

.shop-top {
  background-color: white;

  border-bottom: 1px solid #eeeeee;
}

.shop-search-wrap {
  width: min(1200px, 92%);

  min-height: 92px;

  margin: 0 auto;

  display: flex;

  align-items: center;

  gap: 50px;
}

.shop-title {
  flex-shrink: 0;

  color: #9b7435;

  font-size: 27px;

  font-weight: bold;

  letter-spacing: 2px;
}

.shop-search {
  width: 100%;

  max-width: 650px;

  display: flex;

  overflow: hidden;

  background-color: white;

  border: 2px solid #b58a46;

  border-radius: 6px;
}

.shop-search input {
  flex: 1;

  min-width: 0;

  padding: 14px 18px;

  border: none;

  outline: none;

  font-size: 15px;
}

.shop-search button {
  width: 95px;

  border: none;

  background-color: #b58a46;

  color: white;

  font-size: 15px;

  font-weight: bold;

  cursor: pointer;

  transition: 0.2s;
}

.shop-search button:hover {
  background-color: #8f692f;
}

/* =====================================================
   商品分類
   ===================================================== */

.category-section {
  background-color: white;

  border-bottom: 1px solid #eeeeee;
}

.category-list {
  width: min(1200px, 92%);

  margin: auto;

  padding: 12px 0;

  display: flex;

  gap: 7px;

  overflow-x: auto;
}

.category-button {
  flex-shrink: 0;

  padding: 9px 17px;

  border: none;

  border-radius: 20px;

  background-color: transparent;

  color: #555;

  font-size: 14px;

  cursor: pointer;

  transition: 0.2s;
}

.category-button:hover {
  background-color: #f3eadc;

  color: #8f692f;
}

.category-button.active {
  background-color: #b58a46;

  color: white;
}

/* =====================================================
   Banner
   ===================================================== */

.shop-banner {
  min-height: 320px;

  display: flex;

  align-items: center;

  background:
    linear-gradient(90deg, rgba(25, 20, 15, 0.78), rgba(25, 20, 15, 0.25)),
    url("https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1800&q=85")
      center / cover no-repeat;
}

.banner-content {
  width: min(1200px, 92%);

  margin: auto;

  color: white;
}

.banner-small {
  color: #e5c887;

  font-size: 13px;

  font-weight: bold;

  letter-spacing: 3px;
}

.banner-content h1 {
  margin: 12px 0;

  color: white;

  font-size: 42px;
}

.banner-content p {
  margin-bottom: 24px;

  color: #eeeeee;

  font-size: 17px;
}

.banner-content button {
  padding: 12px 24px;

  border: none;

  border-radius: 5px;

  background-color: #b58a46;

  color: white;

  font-weight: bold;

  cursor: pointer;
}

.banner-content button:hover {
  background-color: #8f692f;
}

/* =====================================================
   商品區
   ===================================================== */

.product-section {
  width: min(1200px, 92%);

  margin: 38px auto 70px;
}

.product-section-header {
  margin-bottom: 20px;
}

.product-section-header h2 {
  margin: 0 0 5px;

  color: #4a3b2a;

  font-size: 25px;
}

.product-section-header p {
  margin: 0;

  color: #888888;

  font-size: 14px;
}

/* =====================================================
   Grid
   ===================================================== */

.product-grid {
  display: grid;

  grid-template-columns: repeat(4, minmax(0, 1fr));

  gap: 16px;
}

/* =====================================================
   商品卡片
   ===================================================== */

.product-card {
  overflow: hidden;

  background-color: white;

  border: 1px solid #eeeeee;

  cursor: pointer;

  transition:
    transform 0.25s,
    box-shadow 0.25s;
}

.product-card:hover {
  transform: translateY(-4px);

  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

/* =====================================================
   商品圖片
   ===================================================== */

.product-image-wrap {
  position: relative;

  width: 100%;

  aspect-ratio: 1 / 1;

  overflow: hidden;

  background-color: #f7f7f7;
}

.product-image {
  width: 100%;

  height: 100%;

  object-fit: cover;

  transition: transform 0.35s;
}

.product-card:hover .product-image {
  transform: scale(1.04);
}

/* =====================================================
   商品狀態 Badge
   ===================================================== */

.product-badge {
  position: absolute;

  top: 10px;

  left: 10px;

  padding: 5px 9px;

  border-radius: 4px;

  color: white;

  font-size: 12px;

  font-weight: bold;
}

.sold-out {
  background-color: #777777;
}

.stock-low {
  background-color: #b3443c;
}

/* =====================================================
   商品資訊
   ===================================================== */

.product-info {
  padding: 15px;
}

.product-category {
  margin-bottom: 6px;

  color: #999999;

  font-size: 12px;
}

.product-name {
  min-height: 44px;

  margin: 0 0 8px;

  color: #333333;

  font-size: 16px;

  line-height: 1.4;

  display: -webkit-box;

  -webkit-line-clamp: 2;

  -webkit-box-orient: vertical;

  overflow: hidden;
}

.product-description {
  min-height: 38px;

  margin: 0 0 13px;

  color: #777777;

  font-size: 13px;

  line-height: 1.5;

  display: -webkit-box;

  -webkit-line-clamp: 2;

  -webkit-box-orient: vertical;

  overflow: hidden;
}

/* =====================================================
   價格
   ===================================================== */

.product-bottom {
  display: flex;

  justify-content: space-between;

  align-items: end;

  gap: 10px;

  margin-bottom: 14px;
}

.price-area {
  color: #b3443c;
}

.currency {
  font-size: 15px;

  font-weight: bold;
}

.price-area strong {
  font-size: 25px;

  font-weight: bold;
}

.stock-text {
  color: #999999;

  font-size: 12px;
}

/* =====================================================
   查看商品
   ===================================================== */

.product-button {
  width: 100%;

  padding: 10px;

  border: 1px solid #b58a46;

  background-color: white;

  color: #9b7435;

  font-weight: bold;

  cursor: pointer;

  transition: 0.2s;
}

.product-button:hover {
  background-color: #b58a46;

  color: white;
}

.product-button:disabled {
  border-color: #cccccc;

  background-color: #eeeeee;

  color: #999999;

  cursor: not-allowed;
}

/* =====================================================
   Loading / Empty / Error
   ===================================================== */

.loading-message,
.empty-message,
.error-message {
  padding: 60px 20px;

  background-color: white;

  text-align: center;
}

.loading-message,
.empty-message {
  color: #888888;
}

.error-message {
  color: #b3443c;

  background-color: #fde9e7;
}

/* =====================================================
   RWD
   ===================================================== */

@media (max-width: 1000px) {
  .product-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 760px) {
  .shop-search-wrap {
    flex-direction: column;

    align-items: stretch;

    gap: 15px;

    padding: 20px 0;
  }

  .shop-title {
    text-align: center;
  }

  .shop-search {
    max-width: none;
  }

  .shop-banner {
    min-height: 280px;
  }

  .banner-content h1 {
    font-size: 32px;
  }

  .product-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 480px) {
  .product-grid {
    gap: 10px;
  }

  .product-info {
    padding: 11px;
  }

  .product-name {
    font-size: 14px;
  }

  .product-description {
    display: none;
  }

  .price-area strong {
    font-size: 20px;
  }
}
</style>
