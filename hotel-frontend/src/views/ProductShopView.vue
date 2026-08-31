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
          tabindex="0"
          role="link"
          @click="goProductDetail(product.productId)"
          @keydown.enter.self="goProductDetail(product.productId)"
        >
          <!-- 商品圖片 -->
          <div class="product-image-wrap">
            <img
              :src="getProductImage(product)"
              :alt="product.productName"
              class="product-image"
              @error="handleImageError"
            />

            <button
              type="button"
              class="wishlist-button"
              :class="{ active: isProductInWishlist(product.productId) }"
              :aria-label="
                isProductInWishlist(product.productId)
                  ? `將 ${product.productName} 移出願望清單`
                  : `將 ${product.productName} 加入願望清單`
              "
              :title="
                isProductInWishlist(product.productId)
                  ? '移出願望清單'
                  : '加入願望清單'
              "
              @click.stop="handleToggleWishlist(product.productId)"
            >
              <span aria-hidden="true">
                {{ isProductInWishlist(product.productId) ? "♥" : "♡" }}
              </span>
            </button>

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
            tabindex="0"
            role="link"
            @click="goProductDetail(product.productId)"
            @keydown.enter.self="goProductDetail(product.productId)"
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
            tabindex="0"
            role="link"
            @click="goProductDetail(product.productId)"
            @keydown.enter.self="goProductDetail(product.productId)"
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
import { computed, nextTick, onMounted, ref } from "vue";

import { useRouter } from "vue-router";

import { getAuthHeaders } from "@/utils/auth";

import {
  getRecentlyViewedIds,
  addRecentlyViewed,
  clearRecentlyViewed,
} from "@/utils/recentlyViewed";

import { getWishlistIds, toggleWishlist } from "@/utils/wishlist";

import { normalizeProductId } from "@/utils/productId";

const router = useRouter();

const ACTIVE_PRODUCT_STATUSES = new Set(["ACTIVE", "上架", "上架中"]);

const VISIBLE_PRODUCT_STATUSES = new Set([
  ...ACTIVE_PRODUCT_STATUSES,
  "OUT_OF_STOCK",
  "缺貨",
]);
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
// 願望清單商品 ID
// =====================================================

const wishlistIds = ref([]);

const wishlistIdSet = computed(
  () => new Set(wishlistIds.value.map(Number)),
);

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

        return VISIBLE_PRODUCT_STATUSES.has(status);
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
  const active = ACTIVE_PRODUCT_STATUSES.has(product.status);

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
  const id = normalizeProductId(productId);

  if (id === null) {
    console.warn("無效的商品 ID：", productId);
    return;
  }

  // 存最近瀏覽
  addRecentlyViewed(id);

  // 更新最近瀏覽畫面
  recentlyViewedIds.value = getRecentlyViewedIds();

  // 進入詳細頁
  router.push(`/products/${id}`);
}

// =====================================================
// 清除最近瀏覽
// =====================================================

function handleClearRecentlyViewed() {
  clearRecentlyViewed();

  recentlyViewedIds.value = [];
}

// =====================================================
// 願望清單
// =====================================================

function isProductInWishlist(productId) {
  const id = normalizeProductId(productId);

  return id !== null && wishlistIdSet.value.has(id);
}

function handleToggleWishlist(productId) {
  const id = normalizeProductId(productId);

  if (id === null) {
    console.warn("無效的商品 ID：", productId);
    return;
  }

  toggleWishlist(id);

  wishlistIds.value = getWishlistIds();
}

// =====================================================
// 捲動到商品區
// =====================================================

async function scrollToProducts() {
  await nextTick();

  productSection.value?.scrollIntoView({
    behavior: "smooth",
    block: "start",
  });
}

// =====================================================
// 初始化
// =====================================================

onMounted(async () => {
  await Promise.all([loadProducts(), loadCategories()]);

  recentlyViewedIds.value = getRecentlyViewedIds();

  wishlistIds.value = getWishlistIds();
});
</script>

<style scoped src="@/assets/product-shop.css"></style>
