<template>
  <section class="product-view wishlist-page">
    <header class="wishlist-header">
      <div>
        <span class="wishlist-eyebrow">MY COLLECTION</span>
        <h2>我的願望清單</h2>
        <p>收藏喜愛的飯店選物，之後可以快速回來查看。</p>
      </div>

      <div class="wishlist-header-actions">
        <span v-if="!loading" class="wishlist-count">
          共 <strong>{{ wishlistProducts.length }}</strong> 件商品
        </span>
        <button
          v-if="wishlistProducts.length > 0"
          type="button"
          class="clear-wishlist-button"
          @click="removeAll"
        >
          清空願望清單
        </button>
      </div>
    </header>

    <div v-if="loading" class="wishlist-state" role="status">
      <div class="wishlist-spinner" aria-hidden="true"></div>
      <p>正在載入願望清單...</p>
    </div>

    <div v-else-if="errorMessage" class="wishlist-state wishlist-error" role="alert">
      <svg viewBox="0 0 24 24" aria-hidden="true">
        <circle cx="12" cy="12" r="9" />
        <path d="M12 8v5M12 16.5v.01" />
      </svg>
      <h3>願望清單載入失敗</h3>
      <p>{{ errorMessage }}</p>
      <button type="button" class="primary-action" @click="loadProducts">
        重新載入
      </button>
    </div>

    <div v-else-if="wishlistProducts.length === 0" class="wishlist-state">
      <div class="empty-heart" aria-hidden="true">
        <svg viewBox="0 0 24 24">
          <path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21l7.7-7.5 1.1-1.1a5.5 5.5 0 0 0 0-7.8Z" />
        </svg>
      </div>
      <h3>願望清單目前是空的</h3>
      <p>前往飯店商城，將喜歡的商品加入收藏吧。</p>
      <RouterLink to="/products" class="primary-action">瀏覽飯店商城</RouterLink>
    </div>

    <div v-else class="wishlist-grid">
      <article
        v-for="product in wishlistProducts"
        :key="product.productId"
        class="wishlist-card"
      >
        <RouterLink
          :to="`/products/${product.productId}`"
          class="wishlist-image-link"
          :aria-label="`查看 ${product.productName}`"
        >
          <img
            :src="getProductImage(product)"
            :alt="product.productName"
            @error="handleImageError"
          />
          <span :class="['stock-badge', { unavailable: !canBuy(product) }]">
            {{ canBuy(product) ? `庫存 ${product.stock}` : "暫時無法購買" }}
          </span>
        </RouterLink>

        <div class="wishlist-card-content">
          <div class="wishlist-card-heading">
            <div>
              <span class="wishlist-category">
                {{ product.category?.categoryName || "星澄嚴選" }}
              </span>
              <h3>{{ product.productName }}</h3>
            </div>

            <button
              type="button"
              class="remove-wishlist-button"
              :aria-label="`將 ${product.productName} 移出願望清單`"
              title="移出願望清單"
              @click="removeProduct(product)"
            >
              <svg viewBox="0 0 24 24" aria-hidden="true">
                <path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21l7.7-7.5 1.1-1.1a5.5 5.5 0 0 0 0-7.8Z" />
              </svg>
            </button>
          </div>

          <p class="wishlist-description">
            {{ product.description || "精選飯店商品，為旅程留下美好回憶。" }}
          </p>

          <div class="wishlist-card-footer">
            <strong>NT$ {{ formatPrice(product.price) }}</strong>
            <RouterLink :to="`/products/${product.productId}`" class="detail-button">
              查看商品
            </RouterLink>
          </div>
        </div>
      </article>
    </div>
  </section>
</template>

<script setup>
import { computed, onMounted, ref } from "vue";

import { getAuthHeaders } from "@/utils/auth";
import {
  clearWishlist,
  getWishlistIds,
  toggleWishlist,
} from "@/utils/wishlist";

const DEFAULT_IMAGE = "/upload/products/default-product.jpg";
const ACTIVE_PRODUCT_STATUSES = new Set(["ACTIVE", "上架", "正常"]);

const loading = ref(false);
const errorMessage = ref("");
const products = ref([]);
const wishlistIds = ref(getWishlistIds());

const wishlistProducts = computed(() =>
  wishlistIds.value
    .map((productId) =>
      products.value.find(
        (product) => Number(product.productId) === Number(productId),
      ),
    )
    .filter(Boolean),
);

async function loadProducts() {
  wishlistIds.value = getWishlistIds();
  errorMessage.value = "";

  if (wishlistIds.value.length === 0) {
    products.value = [];
    return;
  }

  loading.value = true;

  try {
    const response = await fetch("/api/products", {
      method: "GET",
      headers: getAuthHeaders(),
    });

    if (!response.ok) {
      throw new Error(`商品讀取失敗，狀態碼：${response.status}`);
    }

    const data = await response.json();
    products.value = Array.isArray(data) ? data : [];
  } catch (error) {
    console.error("願望清單讀取失敗：", error);
    errorMessage.value = error.message || "目前無法讀取商品資料";
    products.value = [];
  } finally {
    loading.value = false;
  }
}

function removeProduct(product) {
  if (!window.confirm(`確定將「${product.productName}」移出願望清單嗎？`)) {
    return;
  }

  toggleWishlist(product.productId);
  wishlistIds.value = getWishlistIds();
}

function removeAll() {
  if (!window.confirm("確定要清空願望清單嗎？")) {
    return;
  }

  clearWishlist();
  wishlistIds.value = [];
}

function getProductImage(product) {
  const imageUrl = product.imageUrl?.trim();

  if (!imageUrl) {
    return DEFAULT_IMAGE;
  }

  if (
    imageUrl.startsWith("http://") ||
    imageUrl.startsWith("https://") ||
    imageUrl.startsWith("/")
  ) {
    return imageUrl;
  }

  return `/upload/products/${imageUrl}`;
}

function handleImageError(event) {
  if (event.target.dataset.fallback === "true") {
    return;
  }

  event.target.dataset.fallback = "true";
  event.target.src = DEFAULT_IMAGE;
}

function canBuy(product) {
  return (
    ACTIVE_PRODUCT_STATUSES.has(product.status) && Number(product.stock ?? 0) > 0
  );
}

function formatPrice(price) {
  return Number(price ?? 0).toLocaleString("zh-TW");
}

onMounted(loadProducts);
</script>

<style scoped src="@/assets/product/product-wishlist.css"></style>
