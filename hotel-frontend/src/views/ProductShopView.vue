<template>
  <div class="shop-page">
    <!-- =========================
         商城 Hero
         ========================= -->
    <section class="shop-hero">
      <div class="hero-overlay"></div>

      <div class="hero-container">
        <!-- 標題 -->
        <div class="hero-content">
          <span class="hero-eyebrow"> STARLIGHT HOTEL SHOP </span>

          <h1>星澄飯店嚴選商城</h1>

          <p>將旅途中喜愛的質感帶回生活， 精選飯店用品、特色商品與限定好物。</p>
        </div>

        <!-- 搜尋 -->
        <div class="shop-search">
          <input
            v-model="keyword"
            type="text"
            placeholder="搜尋商品名稱..."
            @keyup.enter="searchProducts"
          />

          <button type="button" @click="searchProducts">搜尋</button>
        </div>

        <!-- 商品分類 -->
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
      </div>
    </section>

    <!-- =========================
         商品主要內容
         ========================= -->
    <main ref="productSection" class="product-section">
      <!-- 商品標題 -->
      <div class="product-section-header">
        <div>
          <span class="section-eyebrow"> HOTEL COLLECTION </span>

          <h2>
            {{ selectedCategoryName }}
          </h2>
        </div>

        <div class="product-count">
          共
          <strong>
            {{ filteredProducts.length }}
          </strong>
          件商品
        </div>
      </div>

      <!-- API 錯誤 -->
      <div v-if="errorMessage" class="state-message error-message">
        {{ errorMessage }}
      </div>

      <!-- Loading -->
      <div v-if="loading" class="state-message">商品資料讀取中...</div>

      <!-- 沒商品 -->
      <div
        v-else-if="!errorMessage && filteredProducts.length === 0"
        class="state-message"
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

          <!-- 商品資訊 -->
          <div class="product-info">
            <div class="product-category">
              {{ product.category?.categoryName || "星澄嚴選" }}
            </div>

            <h3 class="product-name">
              {{ product.productName }}
            </h3>

            <p class="product-description">
              {{ product.description || "星澄飯店精選商品" }}
            </p>

            <div class="product-meta">
              <div class="price-area">
                <span class="currency"> NT$ </span>

                <strong>
                  {{ formatPrice(product.price) }}
                </strong>
              </div>

              <span class="stock-text">
                庫存
                {{ product.stock ?? 0 }}
              </span>
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

import { getAuthHeaders } from "@/utils/auth";

const router = useRouter();

// =====================================================
// 圖片設定
// =====================================================

// Vue public 裡面的預設圖片
//
// hotel-frontend/
// public/
//   upload/
//     products/
//       default-product.jpg
//
// 網址：
// /upload/products/default-product.jpg

const DEFAULT_IMAGE = "/upload/products/default-product.jpg";

// =====================================================
// 商品資料
// =====================================================

const products = ref([]);

// =====================================================
// 商品分類
// =====================================================

const categories = ref([]);

// =====================================================
// 搜尋文字
// =====================================================

const keyword = ref("");

// =====================================================
// 選擇分類
// null = 全部商品
// =====================================================

const selectedCategory = ref(null);

// =====================================================
// Loading
// =====================================================

const loading = ref(false);

// =====================================================
// 錯誤訊息
// =====================================================

const errorMessage = ref("");

// =====================================================
// 商品區 DOM
// =====================================================

const productSection = ref(null);

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

    // ==========================
    // 前台只顯示上架 / 缺貨商品
    // ==========================

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
  const imageUrl = product.imageUrl?.trim();

  // ==========================
  // 沒有圖片
  // ==========================
  if (!imageUrl) {
    return DEFAULT_IMAGE;
  }

  // ==========================
  // 外部網址
  //
  // https://images.xxx.com/xxx.jpg
  // ==========================

  if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
    return imageUrl;
  }

  // ==========================
  // Spring Boot 上傳圖片
  //
  // /uploads/products/xxx.png
  //
  // 注意 uploads 有 s
  // ==========================

  if (imageUrl.startsWith("/upload/")) {
    return imageUrl;
  }

  // ==========================
  // Vue public 圖片
  //
  // /upload/products/xxx.jpg
  //
  // 注意 upload 沒有 s
  // ==========================

  if (imageUrl.startsWith("/upload/")) {
    return imageUrl;
  }

  // ==========================
  // 其他 / 開頭網址
  // ==========================

  if (imageUrl.startsWith("/")) {
    return imageUrl;
  }

  // ==========================
  // 資料庫只有存檔名
  //
  // abc.jpg
  //
  // 預設當成後端上傳圖片
  // ==========================

  return "/upload/products/" + imageUrl;
}

// =====================================================
// 圖片失敗 → 預設圖
// =====================================================

function handleImageError(event) {
  if (event.target.dataset.fallback === "true") {
    return;
  }

  event.target.dataset.fallback = "true";

  event.target.src = DEFAULT_IMAGE;
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
// 初始化
// =====================================================

onMounted(async () => {
  await Promise.all([loadProducts(), loadCategories()]);
});
</script>

<style scoped>
/* =====================================================
   整體
   ===================================================== */

.shop-page {
  min-height: 100vh;

  background: #f8f6f1;

  color: #3f382f;
}

/* =====================================================
   Hero
   ===================================================== */

.shop-hero {
  position: relative;

  min-height: 430px;

  display: flex;

  align-items: center;

  overflow: hidden;

  background: url("https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1800&q=85")
    center / cover no-repeat;
}

.hero-overlay {
  position: absolute;

  inset: 0;

  background: linear-gradient(
    90deg,
    rgba(31, 25, 18, 0.88) 0%,
    rgba(31, 25, 18, 0.68) 42%,
    rgba(31, 25, 18, 0.22) 100%
  );
}

.hero-container {
  position: relative;

  z-index: 1;

  width: min(1180px, 90%);

  margin: 0 auto;

  padding: 72px 0 48px;
}

/* =====================================================
   Hero 文字
   ===================================================== */

.hero-content {
  width: 100%;
  max-width: 680px;
}

.hero-eyebrow {
  display: inline-block;

  margin-bottom: 13px;

  color: #dfc58c;

  font-size: 12px;

  font-weight: 700;

  letter-spacing: 4px;
}

.hero-content h1 {
  margin: 0 0 14px;

  color: white;

  font-size: clamp(36px, 5vw, 54px);

  line-height: 1.2;

  letter-spacing: 2px;
}

.hero-content p {
  max-width: 560px;

  margin: 0;

  color: rgba(255, 255, 255, 0.82);

  font-size: 16px;

  line-height: 1.9;
}

/* =====================================================
   搜尋
   ===================================================== */

.shop-search {
  width: 100%;

  max-width: 680px;

  height: 54px;

  margin-top: 30px;

  display: flex;

  overflow: hidden;

  background: rgba(255, 255, 255, 0.96);

  border-radius: 8px;

  box-shadow: 0 8px 28px rgba(0, 0, 0, 0.16);
}

.shop-search input {
  flex: 1;

  min-width: 0;

  padding: 0 20px;

  border: none;

  outline: none;

  background: transparent;

  color: #443a31;

  font-size: 15px;
}

.shop-search input::placeholder {
  color: #aaa39a;
}

.shop-search button {
  width: 110px;

  border: none;

  background: #b58a46;

  color: white;

  font-weight: 700;

  cursor: pointer;

  transition: 0.2s;
}

.shop-search button:hover {
  background: #9d7438;
}

/* =====================================================
   分類
   ===================================================== */

.category-list {
  width: 100%;
  max-width: 680px;

  margin-top: 20px;

  display: flex;

  gap: 9px;

  overflow-x: auto;

  padding-bottom: 3px;

  scrollbar-width: none;
}

.category-list::-webkit-scrollbar {
  display: none;
}

.category-button {
  flex-shrink: 0;

  padding: 9px 17px;

  border: 1px solid rgba(255, 255, 255, 0.32);

  border-radius: 30px;

  background: rgba(255, 255, 255, 0.1);

  backdrop-filter: blur(6px);

  color: rgba(255, 255, 255, 0.9);

  font-size: 13px;

  cursor: pointer;

  transition: 0.2s;
}

.category-button:hover {
  border-color: #dabe82;

  background: rgba(181, 138, 70, 0.35);

  color: white;
}

.category-button.active {
  border-color: #b58a46;

  background: #b58a46;

  color: white;
}

/* =====================================================
   商品內容
   ===================================================== */

.product-section {
  width: min(1180px, 90%);

  margin: 0 auto;

  padding: 60px 0 90px;
}

/* =====================================================
   商品標題
   ===================================================== */

.product-section-header {
  display: flex;

  justify-content: space-between;

  align-items: flex-end;

  gap: 20px;

  margin-bottom: 28px;

  padding-bottom: 17px;

  border-bottom: 1px solid #ded8cf;
}

.section-eyebrow {
  display: block;

  margin-bottom: 7px;

  color: #b58a46;

  font-size: 11px;

  font-weight: 700;

  letter-spacing: 3px;
}

.product-section-header h2 {
  margin: 0;

  color: #4a3b2a;

  font-size: 28px;

  font-weight: 700;
}

.product-count {
  color: #92887d;

  font-size: 14px;
}

.product-count strong {
  color: #9b7435;

  font-size: 17px;
}

/* =====================================================
   Grid
   ===================================================== */

.product-grid {
  display: grid;

  grid-template-columns: repeat(4, minmax(0, 1fr));

  gap: 24px;
}

/* =====================================================
   商品卡片
   ===================================================== */

.product-card {
  overflow: hidden;

  display: flex;

  flex-direction: column;

  background: white;

  border: 1px solid rgba(94, 75, 52, 0.1);

  border-radius: 12px;

  box-shadow: 0 3px 14px rgba(70, 55, 38, 0.05);

  cursor: pointer;

  transition:
    transform 0.25s ease,
    box-shadow 0.25s ease;
}

.product-card:hover {
  transform: translateY(-6px);

  box-shadow: 0 14px 35px rgba(70, 55, 38, 0.13);
}

/* =====================================================
   商品圖片
   ===================================================== */

.product-image-wrap {
  position: relative;

  width: 100%;

  aspect-ratio: 4 / 3;

  overflow: hidden;

  background: #eeeae4;
}

.product-image {
  width: 100%;

  height: 100%;

  display: block;

  object-fit: cover;

  transition: transform 0.45s ease;
}

.product-card:hover .product-image {
  transform: scale(1.055);
}

/* =====================================================
   Badge
   ===================================================== */

.product-badge {
  position: absolute;

  top: 12px;

  left: 12px;

  padding: 6px 11px;

  border-radius: 30px;

  color: white;

  font-size: 11px;

  font-weight: 700;

  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.14);
}

.sold-out {
  background: rgba(69, 65, 60, 0.9);
}

.stock-low {
  background: #a95647;
}

/* =====================================================
   商品資訊
   ===================================================== */

.product-info {
  flex: 1;

  display: flex;

  flex-direction: column;

  padding: 19px 18px 18px;
}

.product-category {
  margin-bottom: 8px;

  color: #b58a46;

  font-size: 11px;

  font-weight: 700;

  letter-spacing: 1px;
}

.product-name {
  min-height: 46px;

  margin: 0 0 9px;

  color: #40382f;

  font-size: 17px;

  line-height: 1.45;

  display: -webkit-box;

  -webkit-line-clamp: 2;

  -webkit-box-orient: vertical;

  overflow: hidden;
}

.product-description {
  min-height: 42px;

  margin: 0 0 18px;

  color: #8a8178;

  font-size: 13px;

  line-height: 1.65;

  display: -webkit-box;

  -webkit-line-clamp: 2;

  -webkit-box-orient: vertical;

  overflow: hidden;
}

/* =====================================================
   價格 / 庫存
   ===================================================== */

.product-meta {
  margin-top: auto;

  display: flex;

  justify-content: space-between;

  align-items: flex-end;

  gap: 10px;

  margin-bottom: 16px;
}

.price-area {
  display: flex;

  align-items: baseline;

  gap: 5px;

  color: #9c6934;
}

.currency {
  font-size: 12px;

  font-weight: 700;
}

.price-area strong {
  font-size: 25px;

  line-height: 1;

  font-weight: 700;
}

.stock-text {
  color: #a39b92;

  font-size: 11px;
}

/* =====================================================
   查看商品
   ===================================================== */

.product-button {
  width: 100%;

  padding: 11px 14px;

  border: 1px solid #b58a46;

  border-radius: 6px;

  background: transparent;

  color: #9b7435;

  font-size: 13px;

  font-weight: 700;

  cursor: pointer;

  transition: 0.2s;
}

.product-button:hover:not(:disabled) {
  background: #b58a46;

  color: white;
}

.product-button:disabled {
  border-color: #ddd7ce;

  background: #efede9;

  color: #aaa39a;

  cursor: not-allowed;
}

/* =====================================================
   Loading / Empty / Error
   ===================================================== */

.state-message {
  padding: 70px 20px;

  border: 1px solid #e6e0d8;

  border-radius: 10px;

  background: rgba(255, 255, 255, 0.65);

  color: #91887e;

  text-align: center;
}

.error-message {
  border-color: #e9c5c0;

  background: #fff4f2;

  color: #a65349;
}

/* =====================================================
   RWD
   ===================================================== */

@media (max-width: 1050px) {
  .product-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (max-width: 760px) {
  .shop-hero {
    min-height: 400px;
  }

  .hero-container {
    position: relative;

    z-index: 1;

    width: min(1180px, 90%);

    margin: 0 auto;

    padding: 72px 0 48px;
  }

  .hero-overlay {
    background: rgba(31, 25, 18, 0.72);
  }

  .hero-content p {
    font-size: 14px;
  }

  .shop-search {
    max-width: none;
  }

  .product-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));

    gap: 14px;
  }

  .product-section {
    padding: 40px 0 65px;
  }

  .product-section-header {
    align-items: flex-start;

    flex-direction: column;

    gap: 8px;
  }
}

@media (max-width: 480px) {
  .hero-content h1 {
    font-size: 31px;
  }

  .shop-search {
    height: 48px;
  }

  .shop-search button {
    width: 78px;
  }

  .category-button {
    padding: 8px 14px;
  }

  .product-grid {
    gap: 10px;
  }

  .product-info {
    padding: 13px;
  }

  .product-name {
    min-height: 40px;

    font-size: 14px;
  }

  .product-description {
    display: none;
  }

  .price-area strong {
    font-size: 20px;
  }

  .product-button {
    padding: 9px;
  }
}
</style>
