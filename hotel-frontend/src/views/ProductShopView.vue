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

          <h1>精品商城</h1>

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

            <!-- 價格 / 庫存 -->
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

            <!-- =========================
                 購買數量
                 ========================= -->
            <div v-if="canBuy(product)" class="quantity-area" @click.stop>
              <span class="quantity-label"> 數量 </span>

              <div class="quantity-control">
                <!-- 減少 -->
                <button
                  type="button"
                  class="quantity-button"
                  :disabled="product.buyQuantity <= 1"
                  @click.stop="decreaseQuantity(product)"
                >
                  −
                </button>

                <!-- 數量 -->
                <span class="quantity-number">
                  {{ product.buyQuantity }}
                </span>

                <!-- 增加 -->
                <button
                  type="button"
                  class="quantity-button"
                  :disabled="product.buyQuantity >= Number(product.stock)"
                  @click.stop="increaseQuantity(product)"
                >
                  ＋
                </button>
              </div>
            </div>

            <!-- =========================
                 購買
                 ========================= -->
            <button
              type="button"
              class="product-button"
              :disabled="!canBuy(product)"
              @click.stop="buyProduct(product)"
            >
              {{ canBuy(product) ? "購買" : "暫無法購買" }}
            </button>
          </div>
        </article>
      </div>
    </main>

    <!-- =========================
         最近瀏覽
         ========================= -->
    <section
      v-if="recentlyViewedProducts.length > 0"
      class="recently-viewed-section"
    >
      <div class="recently-viewed-container">
        <!-- 標題 -->
        <div class="recently-viewed-header">
          <div>
            <span class="section-eyebrow"> RECENTLY VIEWED </span>

            <h2>最近瀏覽</h2>
          </div>

          <button
            type="button"
            class="clear-recent-button"
            @click="handleClearRecentlyViewed"
          >
            清除紀錄
          </button>
        </div>

        <!-- 最近瀏覽商品 -->
        <div class="recently-viewed-list">
          <article
            v-for="product in recentlyViewedProducts"
            :key="product.productId"
            class="recent-product-card"
            @click="goProductDetail(product.productId)"
          >
            <!-- 圖片 -->
            <div class="recent-product-image-wrap">
              <img
                :src="getProductImage(product)"
                :alt="product.productName"
                class="recent-product-image"
                @error="handleImageError"
              />
            </div>

            <!-- 資訊 -->
            <div class="recent-product-info">
              <div class="recent-product-category">
                {{ product.category?.categoryName || "星澄嚴選" }}
              </div>

              <h3>
                {{ product.productName }}
              </h3>

              <div class="recent-product-bottom">
                <div class="recent-product-price">
                  NT$
                  {{ formatPrice(product.price) }}
                </div>

                <span
                  v-if="Number(product.stock) <= 0"
                  class="recent-stock sold"
                >
                  缺貨
                </span>

                <span v-else class="recent-stock">
                  庫存
                  {{ product.stock }}
                </span>
              </div>
            </div>
          </article>
        </div>
      </div>
    </section>
    <!-- =========================
     猜你喜歡
     ========================= -->
    <section v-if="recommendedProducts.length > 0" class="recommend-section">
      <div class="recommend-container">
        <div class="recommend-header">
          <div>
            <span class="section-eyebrow"> RECOMMENDED FOR YOU </span>

            <h2>猜你喜歡</h2>

            <p>根據你最近瀏覽的商品，為你推薦相似商品</p>
          </div>
        </div>

        <div class="recommend-grid">
          <article
            v-for="product in recommendedProducts"
            :key="product.productId"
            class="recommend-card"
            @click="goProductDetail(product.productId)"
          >
            <div class="recommend-image-wrap">
              <img
                :src="getProductImage(product)"
                :alt="product.productName"
                class="recommend-image"
                @error="handleImageError"
              />

              <span class="recommend-badge"> 為你推薦 </span>
            </div>

            <div class="recommend-info">
              <div class="recommend-category">
                {{ product.category?.categoryName || "星澄嚴選" }}
              </div>

              <h3>
                {{ product.productName }}
              </h3>

              <p>
                {{ product.description || "星澄飯店精選商品" }}
              </p>

              <div class="recommend-bottom">
                <div class="recommend-price">
                  NT$
                  {{ formatPrice(product.price) }}
                </div>

                <span class="recommend-stock">
                  庫存
                  {{ product.stock }}
                </span>
              </div>

              <button
                type="button"
                class="recommend-button"
                @click.stop="goProductDetail(product.productId)"
              >
                查看商品
              </button>
            </div>
          </article>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";

import { useRouter } from "vue-router";

import { getAuthHeaders } from "@/utils/auth";

import {
  getRecentlyViewedIds,
  addRecentlyViewed,
  clearRecentlyViewed,
} from "@/utils/recentlyViewed";

const router = useRouter();
// =====================================================
// 猜你喜歡
// 根據最近瀏覽最多的商品分類推薦
// =====================================================
const recommendedProducts = computed(() => {
  //沒有瀏覽紀錄就不推薦
  if (recentlyViewedProducts.value.length === 0) {
    return [];
  }
  //紀錄每個分類被瀏覽幾次
  const categoryCount = {};

  recentlyViewedProducts.value.forEach((product) => {
    const categoryId = product.category?.categoryId;

    if (!categoryId) {
      return;
    }
    categoryCount[categoryId] = (categoryCount[categoryId] || 0) + 1;
  });
  //沒有分類資料
  const categoryIds = Object.keys(categoryCount);

  if (categoryIds.length === 0) {
    return [];
  }
  //找出瀏覽最多的分類
  const favoriteCategoryId = categoryIds.reduce((maxId, currentId) => {
    return categoryCount[currentId] > categoryCount[maxId] ? currentId : maxId;
  });
  // 最近已經看過的商品 ID
  const viewedIds = recentlyViewedIds.value.map(Number); // 推薦同分類、尚未瀏覽、而且可以購買的商品
  return products.value
    .filter((product) => {
      const productCategoryId = Number(product.category?.categoryId);

      const sameCategory = productCategoryId === Number(favoriteCategoryId);

      const notViewed = !viewedIds.includes(Number(product.productId));

      return sameCategory && notViewed && canBuy(product);
    })
    .slice(0, 4);
});

// =====================================================
// 圖片設定
// =====================================================

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
// 最近瀏覽商品 ID
// =====================================================

const recentlyViewedIds = ref([]);

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

    // 前台只顯示上架 / 缺貨商品
    products.value = data
      .filter((product) => {
        const status = product.status;

        return (
          status === "ACTIVE" ||
          status === "OUT_OF_STOCK" ||
          status === "上架" ||
          status === "上架中" ||
          status === "缺貨"
        );
      })
      .map((product) => ({
        ...product,

        // 每個商品預設購買數量 1
        buyQuantity: 1,
      }));

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

  // 分類篩選
  if (selectedCategory.value !== null) {
    result = result.filter(
      (product) =>
        Number(product.category?.categoryId) === Number(selectedCategory.value),
    );
  }

  // 關鍵字搜尋
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
// 最近瀏覽商品
// =====================================================

const recentlyViewedProducts = computed(() => {
  return recentlyViewedIds.value
    .map((id) =>
      products.value.find(
        (product) => Number(product.productId) === Number(id),
      ),
    )
    .filter(Boolean);
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

  // 沒圖片
  if (!imageUrl) {
    return DEFAULT_IMAGE;
  }

  // 外部網址
  if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
    return imageUrl;
  }

  // 已經是完整網站路徑
  if (imageUrl.startsWith("/")) {
    return imageUrl;
  }

  // DB 只有檔名
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
// 增加數量
// =====================================================

function increaseQuantity(product) {
  const stock = Number(product.stock ?? 0);

  const currentQuantity = Number(product.buyQuantity ?? 1);

  if (currentQuantity < stock) {
    product.buyQuantity = currentQuantity + 1;
  }
}

// =====================================================
// 減少數量
// =====================================================

function decreaseQuantity(product) {
  const currentQuantity = Number(product.buyQuantity ?? 1);

  if (currentQuantity > 1) {
    product.buyQuantity = currentQuantity - 1;
  }
}

// =====================================================
// 加入購物車
// =====================================================

function buyProduct(product) {
  if (!canBuy(product)) {
    alert("此商品目前無法購買");

    return;
  }

  const quantity = Number(product.buyQuantity ?? 1);

  const stock = Number(product.stock ?? 0);

  if (quantity < 1) {
    alert("購買數量至少為 1");

    return;
  }

  if (quantity > stock) {
    alert("購買數量不能超過庫存");

    return;
  }

  const savedCart = localStorage.getItem("cart");

  let cart = [];

  if (savedCart) {
    try {
      cart = JSON.parse(savedCart);
    } catch (error) {
      console.error("購物車資料格式錯誤：", error);

      cart = [];
    }
  }

  const existingItem = cart.find(
    (item) => Number(item.productId) === Number(product.productId),
  );

  if (existingItem) {
    const newQuantity = Number(existingItem.quantity) + quantity;

    if (newQuantity > stock) {
      alert(`購物車中的總數量不能超過庫存 ${stock}`);

      return;
    }

    existingItem.quantity = newQuantity;
  } else {
    cart.push({
      productId: product.productId,

      productName: product.productName,

      price: product.price,

      quantity,

      stock: product.stock,

      imageUrl: product.imageUrl ?? null,
    });
  }

  localStorage.setItem("cart", JSON.stringify(cart));

  alert(`${product.productName} × ${quantity} 已加入購物車`);

  product.buyQuantity = 1;
}

// =====================================================
// 商品詳細頁
// =====================================================

function goProductDetail(productId) {
  // 存最近瀏覽
  addRecentlyViewed(productId);

  // 更新最近瀏覽畫面
  recentlyViewedIds.value = getRecentlyViewedIds();

  // 進入詳細頁
  router.push(`/products/${productId}`);
}

// =====================================================
// 清除最近瀏覽
// =====================================================

function handleClearRecentlyViewed() {
  clearRecentlyViewed();

  recentlyViewedIds.value = [];
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

  recentlyViewedIds.value = getRecentlyViewedIds();
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
   商品 Grid
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
   數量
   ===================================================== */

.quantity-area {
  display: flex;

  justify-content: space-between;

  align-items: center;

  margin-bottom: 14px;

  padding-top: 12px;

  border-top: 1px solid #eee7dd;
}

.quantity-label {
  color: #7f756a;

  font-size: 12px;
}

.quantity-control {
  display: flex;
  align-items: center;

  height: 32px;

  border: 1px solid #d9d0c4;
  border-radius: 6px;

  overflow: hidden;
}

.quantity-button {
  width: 32px;
  height: 30px;

  display: flex;
  align-items: center;
  justify-content: center;

  padding: 0;

  border: none;
  background: #f7f4ef;

  color: #806438;

  font-size: 16px;
  line-height: 1;

  cursor: pointer;
}
.quantity-button:hover:not(:disabled) {
  background: #eadfcf;
}

.quantity-button:disabled {
  color: #c2bcb4;

  cursor: not-allowed;
}

.quantity-number {
  min-width: 38px;
  height: 30px;

  display: flex;
  align-items: center;
  justify-content: center;

  text-align: center;

  color: #4a4036;

  font-size: 13px;
  font-weight: 700;

  line-height: 1;
}

/* =====================================================
   購買按鈕
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
   最近瀏覽
   ===================================================== */

.recently-viewed-section {
  padding: 55px 0 70px;

  background: #f1ede6;

  border-top: 1px solid #e0d9cf;
}

.recently-viewed-container {
  width: min(1180px, 90%);

  margin: 0 auto;
}

.recently-viewed-header {
  display: flex;

  justify-content: space-between;

  align-items: flex-end;

  gap: 20px;

  margin-bottom: 24px;
}

.recently-viewed-header h2 {
  margin: 0;

  color: #4a3b2a;

  font-size: 26px;
}

.clear-recent-button {
  padding: 8px 15px;

  border: 1px solid #c7bba9;

  border-radius: 6px;

  background: transparent;

  color: #82776a;

  font-size: 12px;

  cursor: pointer;

  transition: 0.2s;
}

.clear-recent-button:hover {
  border-color: #a95647;

  background: white;

  color: #a95647;
}

/* =====================================================
   最近瀏覽列表
   ===================================================== */

.recently-viewed-list {
  display: grid;

  grid-template-columns: repeat(5, minmax(0, 1fr));

  gap: 18px;
}

/* =====================================================
   最近瀏覽卡片
   ===================================================== */

.recent-product-card {
  overflow: hidden;

  background: white;

  border: 1px solid rgba(94, 75, 52, 0.1);

  border-radius: 10px;

  cursor: pointer;

  transition:
    transform 0.2s ease,
    box-shadow 0.2s ease;
}

.recent-product-card:hover {
  transform: translateY(-4px);

  box-shadow: 0 10px 28px rgba(70, 55, 38, 0.12);
}

/* =====================================================
   最近瀏覽圖片
   ===================================================== */

.recent-product-image-wrap {
  width: 100%;

  aspect-ratio: 4 / 3;

  overflow: hidden;

  background: #eeeae4;
}

.recent-product-image {
  width: 100%;

  height: 100%;

  display: block;

  object-fit: cover;

  transition: transform 0.3s ease;
}

.recent-product-card:hover .recent-product-image {
  transform: scale(1.05);
}

/* =====================================================
   最近瀏覽商品資訊
   ===================================================== */

.recent-product-info {
  padding: 14px;
}

.recent-product-category {
  margin-bottom: 6px;

  color: #b58a46;

  font-size: 10px;

  font-weight: 700;

  letter-spacing: 0.5px;
}

.recent-product-info h3 {
  min-height: 40px;

  margin: 0 0 12px;

  color: #40382f;

  font-size: 14px;

  line-height: 1.45;

  display: -webkit-box;

  -webkit-line-clamp: 2;

  -webkit-box-orient: vertical;

  overflow: hidden;
}

.recent-product-bottom {
  display: flex;

  justify-content: space-between;

  align-items: center;

  gap: 8px;
}

.recent-product-price {
  color: #9c6934;

  font-size: 15px;

  font-weight: 700;
}

.recent-stock {
  color: #9a9188;

  font-size: 10px;
}

.recent-stock.sold {
  color: #a95647;

  font-weight: 700;
}
/* =====================================================
   猜你喜歡
   ===================================================== */

.recommend-section {
  padding: 60px 0 75px;

  background: #f8f6f1;
}

.recommend-container {
  width: min(1180px, 90%);

  margin: 0 auto;
}

.recommend-header {
  margin-bottom: 26px;
}

.recommend-header h2 {
  margin: 0 0 8px;

  color: #4a3b2a;

  font-size: 28px;
}

.recommend-header p {
  margin: 0;

  color: #91887e;

  font-size: 13px;
}

/* =====================================================
   推薦 Grid
   ===================================================== */

.recommend-grid {
  display: grid;

  grid-template-columns: repeat(4, minmax(0, 1fr));

  gap: 22px;
}

/* =====================================================
   推薦卡片
   ===================================================== */

.recommend-card {
  overflow: hidden;

  background: white;

  border: 1px solid rgba(94, 75, 52, 0.1);

  border-radius: 12px;

  box-shadow: 0 4px 16px rgba(70, 55, 38, 0.06);

  cursor: pointer;

  transition:
    transform 0.25s ease,
    box-shadow 0.25s ease;
}

.recommend-card:hover {
  transform: translateY(-5px);

  box-shadow: 0 14px 34px rgba(70, 55, 38, 0.13);
}

/* =====================================================
   推薦圖片
   ===================================================== */

.recommend-image-wrap {
  position: relative;

  aspect-ratio: 4 / 3;

  overflow: hidden;

  background: #eeeae4;
}

.recommend-image {
  width: 100%;

  height: 100%;

  display: block;

  object-fit: cover;

  transition: transform 0.35s ease;
}

.recommend-card:hover .recommend-image {
  transform: scale(1.05);
}

.recommend-badge {
  position: absolute;

  top: 12px;

  left: 12px;

  padding: 6px 11px;

  border-radius: 20px;

  background: #b58a46;

  color: white;

  font-size: 10px;

  font-weight: 700;
}

/* =====================================================
   推薦內容
   ===================================================== */

.recommend-info {
  padding: 17px;
}

.recommend-category {
  margin-bottom: 7px;

  color: #b58a46;

  font-size: 10px;

  font-weight: 700;
}

.recommend-info h3 {
  min-height: 42px;

  margin: 0 0 8px;

  color: #40382f;

  font-size: 16px;

  line-height: 1.45;
}

.recommend-info p {
  min-height: 42px;

  margin: 0 0 16px;

  color: #8a8178;

  font-size: 12px;

  line-height: 1.6;

  display: -webkit-box;

  -webkit-line-clamp: 2;

  -webkit-box-orient: vertical;

  overflow: hidden;
}

.recommend-bottom {
  display: flex;

  justify-content: space-between;

  align-items: center;

  margin-bottom: 14px;
}

.recommend-price {
  color: #9c6934;

  font-size: 18px;

  font-weight: 700;
}

.recommend-stock {
  color: #9a9188;

  font-size: 10px;
}

.recommend-button {
  width: 100%;

  padding: 10px;

  border: none;

  border-radius: 6px;

  background: #b58a46;

  color: white;

  font-size: 12px;

  font-weight: 700;

  cursor: pointer;

  transition: 0.2s;
}

.recommend-button:hover {
  background: #9d7438;
}
/* =====================================================
   RWD
   ===================================================== */

@media (max-width: 1050px) {
  .product-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }

  .recently-viewed-list {
    grid-template-columns: repeat(4, minmax(0, 1fr));
  }
}

@media (max-width: 760px) {
  .shop-hero {
    min-height: 400px;
  }

  .hero-container {
    width: min(1180px, 90%);

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

  .recently-viewed-list {
    grid-template-columns: repeat(2, minmax(0, 1fr));

    gap: 14px;
  }

  .recently-viewed-header {
    align-items: center;
  }

  .recently-viewed-section {
    padding: 45px 0 55px;
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
    grid-template-columns: repeat(2, minmax(0, 1fr));

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

  .quantity-area {
    align-items: flex-start;

    flex-direction: column;

    gap: 8px;
  }

  .product-button {
    padding: 9px;
  }

  .recently-viewed-section {
    padding: 38px 0 45px;
  }

  .recently-viewed-header {
    align-items: flex-start;

    flex-direction: column;

    gap: 12px;
  }

  .recently-viewed-header h2 {
    font-size: 23px;
  }

  .recently-viewed-list {
    grid-template-columns: repeat(2, minmax(0, 1fr));

    gap: 10px;
  }

  .recent-product-info {
    padding: 11px;
  }

  .recent-product-info h3 {
    min-height: 36px;

    font-size: 13px;
  }

  .recent-product-price {
    font-size: 13px;
  }

  .recent-product-bottom {
    align-items: flex-start;

    flex-direction: column;

    gap: 4px;
  }

  .clear-recent-button {
    padding: 7px 12px;

    font-size: 11px;
  }
}
</style>
