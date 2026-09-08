<template>
  <div class="product-detail-page">
    <div class="detail-ambient detail-ambient-left" aria-hidden="true"></div>
    <div class="detail-ambient detail-ambient-right" aria-hidden="true"></div>

    <main class="detail-container">
      <nav class="detail-breadcrumb" aria-label="麵包屑導覽">
        <RouterLink to="/">首頁</RouterLink>
        <svg viewBox="0 0 24 24" aria-hidden="true">
          <path d="m9 18 6-6-6-6" />
        </svg>
        <RouterLink to="/products">飯店商城</RouterLink>
        <template v-if="product">
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="m9 18 6-6-6-6" />
          </svg>
          <span aria-current="page">{{ product.productName }}</span>
        </template>
      </nav>

      <section v-if="loading" class="detail-card detail-loading" aria-live="polite">
        <span class="sr-only">商品資料載入中</span>
        <div class="skeleton skeleton-image"></div>
        <div class="skeleton-content">
          <div class="skeleton skeleton-label"></div>
          <div class="skeleton skeleton-title"></div>
          <div class="skeleton skeleton-text"></div>
          <div class="skeleton skeleton-text skeleton-text-short"></div>
          <div class="skeleton skeleton-price"></div>
        </div>
      </section>

      <section v-else-if="errorMessage" class="detail-state" role="alert">
        <div class="state-icon" aria-hidden="true">
          <svg viewBox="0 0 24 24">
            <circle cx="12" cy="12" r="9" />
            <path d="M12 8v5M12 16.5v.01" />
          </svg>
        </div>
        <p class="state-eyebrow">PRODUCT UNAVAILABLE</p>
        <h1>{{ errorMessage }}</h1>
        <p>這項商品可能已下架，或暫時無法取得資料。</p>
        <div class="state-actions">
          <button type="button" class="secondary-button" @click="loadProduct">
            重新載入
          </button>
          <RouterLink to="/products" class="primary-link">返回商城</RouterLink>
        </div>
      </section>

      <template v-else-if="product">
        <div
          class="detail-layout"
          :class="{ 'without-recommendations': recommendedProducts.length === 0 }"
        >
          <aside
            v-if="recommendedProducts.length > 0"
            class="recommendation-rail"
            aria-labelledby="recommendation-title"
          >
            <div class="rail-heading">
              <p>JUST FOR YOU</p>
              <h2 id="recommendation-title">猜你喜歡</h2>
              <span aria-hidden="true"></span>
            </div>

            <div class="recommendation-list">
              <RouterLink
                v-for="item in recommendedProducts"
                :key="item.productId"
                :to="{ name: 'product-detail', params: { id: item.productId } }"
                class="recommendation-card"
              >
                <div class="recommendation-image-wrap">
                  <img
                    :src="getProductImage(item)"
                    :alt="item.productName"
                    @error="handleImageError"
                  />
                  <span>{{ item.category?.categoryName || "星澄嚴選" }}</span>
                </div>
                <div class="recommendation-info">
                  <h3>{{ item.productName }}</h3>
                  <p>NT$ {{ formatPrice(item.price) }}</p>
                </div>
              </RouterLink>
            </div>
          </aside>

          <div class="detail-main">
        <article class="detail-card">
          <div class="product-visual">
            <span class="visual-label">STARLIGHT SELECTION</span>
            <div class="image-frame">
              <img
                :key="product.productId"
                :src="productImage"
                :alt="product.productName"
                class="detail-image"
                @error="handleImageError"
              />
            </div>
          </div>

          <div class="product-content">
            <div class="content-topline">
              <span class="category-name">
                {{ product.category?.categoryName || "星澄嚴選" }}
              </span>
              <span class="stock-badge" :class="stockBadgeClass">
                <span class="stock-dot" aria-hidden="true"></span>
                {{ stockLabel }}
              </span>
            </div>

            <div>
              <h1>{{ product.productName }}</h1>
            </div>

            <p class="product-description">
              {{ product.description || "此商品目前尚無詳細介紹。" }}
            </p>

            <div class="price-block">
              <span>售價</span>
              <p><small>NT$</small>{{ formatPrice(product.price) }}</p>
            </div>

            <dl class="product-meta">
              <div>
                <dt>商品分類</dt>
                <dd>{{ product.category?.categoryName || "未分類" }}</dd>
              </div>
              <div>
                <dt>庫存數量</dt>
                <dd>{{ normalizedStock }} 件</dd>
              </div>
              <div>
                <dt>商品狀態</dt>
                <dd>{{ statusLabel }}</dd>
              </div>
            </dl>

            <div class="purchase-panel">
              <div class="quantity-picker">
                <span id="purchase-quantity-label">購買數量</span>
                <div class="quantity-control" aria-labelledby="purchase-quantity-label">
                  <button
                    type="button"
                    aria-label="減少購買數量"
                    :disabled="!canBuy || quantity <= 1"
                    @click="decreaseQuantity"
                  >
                    <svg viewBox="0 0 24 24" aria-hidden="true">
                      <path d="M5 12h14" />
                    </svg>
                  </button>
                  <output aria-live="polite">{{ quantity }}</output>
                  <button
                    type="button"
                    aria-label="增加購買數量"
                    :disabled="!canBuy || quantity >= normalizedStock"
                    @click="increaseQuantity"
                  >
                    <svg viewBox="0 0 24 24" aria-hidden="true">
                      <path d="M12 5v14M5 12h14" />
                    </svg>
                  </button>
                </div>
              </div>

              <div class="purchase-total" aria-live="polite">
                <span>小計</span>
                <strong>NT$ {{ formatPrice(product.price * quantity) }}</strong>
              </div>

              <button
                type="button"
                class="purchase-button"
                :disabled="!canBuy || !isLoggedIn"
                @click="addToCart"
              >
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path d="M6.5 8.5h11l1 12h-13l1-12Z" />
                  <path d="M9 9V6a3 3 0 0 1 6 0v3" />
                </svg>
                {{ !isLoggedIn ? "登入後加入購物車" : canBuy ? "加入購物車" : "目前無法購買" }}
              </button>
            </div>

            <p
              v-if="purchaseMessage"
              class="purchase-message"
              :class="purchaseMessageType"
              role="status"
              aria-live="polite"
            >
              {{ purchaseMessage }}
            </p>

            <div class="detail-actions">
              <button
                type="button"
                class="wishlist-action"
                :class="{ active: inWishlist }"
                :disabled="!isLoggedIn"
                :aria-pressed="inWishlist"
                :aria-label="
                  !isLoggedIn
                    ? '請先登入會員後使用願望清單'
                    : inWishlist
                    ? `將 ${product.productName} 移出願望清單`
                    : `將 ${product.productName} 加入願望清單`
                "
                @click="handleToggleWishlist"
              >
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path
                    d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1.1-1.1a5.5 5.5 0 0 0-7.8 7.8l1.1 1.1L12 21l7.8-7.5 1.1-1.1a5.5 5.5 0 0 0-.1-7.8Z"
                  />
                </svg>
                {{ !isLoggedIn ? "登入後加入願望清單" : inWishlist ? "已收藏" : "加入願望清單" }}
              </button>

              <RouterLink to="/products" class="shop-link">
                返回商品列表
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path d="M5 12h14M13 6l6 6-6 6" />
                </svg>
              </RouterLink>
            </div>

            <p class="scope-note">
              商品資訊與庫存數量以系統最新資料為準。
            </p>
          </div>
        </article>

        <section class="product-info-tabs" aria-label="商品詳細資訊">
          <div class="product-tab-list" role="tablist" aria-label="商品資訊分頁">
            <button
              v-for="(tab, index) in productTabs"
              :id="`product-tab-${tab.key}`"
              :key="tab.key"
              type="button"
              role="tab"
              :aria-selected="activeProductTab === tab.key"
              :aria-controls="`product-panel-${tab.key}`"
              :tabindex="activeProductTab === tab.key ? 0 : -1"
              :class="{ active: activeProductTab === tab.key }"
              @click="activeProductTab = tab.key"
              @keydown="handleProductTabKeydown($event, index)"
            >
              {{ tab.label }}
            </button>
          </div>

          <div
            :id="`product-panel-${activeProductTab}`"
            class="product-tab-panel"
            role="tabpanel"
            :aria-labelledby="`product-tab-${activeProductTab}`"
            tabindex="0"
          >
            <div v-if="activeProductTab === 'features'" class="tab-copy">
              <h2>商品特色</h2>
              <p>{{ product.description || "此商品目前尚無詳細介紹。" }}</p>
            </div>

            <div
              v-else-if="activeProductTab === 'reviews'"
              class="reviews-panel"
              @click="closeReviewMenu"
            >
              <div class="reviews-heading">
                <div>
                  <p class="section-eyebrow">CUSTOMER REVIEWS</p>
                  <h2>商品評價</h2>
                </div>
                <div v-if="reviews.length" class="rating-summary" aria-label="商品平均評分">
                  <strong>{{ averageRating }}</strong>
                  <div>
                    <div class="display-stars" aria-hidden="true">
                      <svg
                        v-for="star in 5"
                        :key="star"
                        viewBox="0 0 24 24"
                        :class="{ filled: star <= Math.round(Number(averageRating)) }"
                      >
                        <path d="m12 2.7 2.83 5.73 6.32.92-4.58 4.46 1.08 6.3L12 17.14l-5.65 2.97 1.08-6.3-4.58-4.46 6.32-.92L12 2.7Z" />
                      </svg>
                    </div>
                    <span>{{ reviews.length }} 則評論</span>
                  </div>
                </div>
              </div>

              <form v-if="isLoggedIn" ref="reviewForm" class="review-form" @submit.prevent="submitReview">
                <div class="review-form-heading">
                  <div>
                    <h3>
                      {{ editingReviewId
                        ? "編輯我的評價"
                        : ownReview
                          ? "你已留下評價"
                          : "分享你的使用心得" }}
                    </h3>
                    <p>
                      {{ editingReviewId
                        ? "修改星等或評論內容後儲存。"
                        : ownReview
                          ? "若要修改內容，請從你的評論右上角選擇編輯。"
                          : "你的會員名稱會顯示在評論旁。" }}
                    </p>
                  </div>
                  <span v-if="editingReviewId" class="review-status-badge">編輯中</span>
                  <span v-else-if="ownReview" class="review-status-badge">已評價</span>
                </div>

                <fieldset class="rating-fieldset">
                  <legend>商品星等</legend>
                  <div class="rating-input" @mouseleave="hoveredRating = 0">
                    <button
                      v-for="star in 5"
                      :key="star"
                      type="button"
                      :aria-label="`${star} 星`"
                      :aria-pressed="reviewRating === star"
                      :class="{ active: star <= (hoveredRating || reviewRating) }"
                      @mouseenter="hoveredRating = star"
                      @focus="hoveredRating = star"
                      @blur="hoveredRating = 0"
                      @click="reviewRating = star"
                    >
                      <svg viewBox="0 0 24 24" aria-hidden="true">
                        <path d="m12 2.7 2.83 5.73 6.32.92-4.58 4.46 1.08 6.3L12 17.14l-5.65 2.97 1.08-6.3-4.58-4.46 6.32-.92L12 2.7Z" />
                      </svg>
                    </button>
                    <span>{{ reviewRating ? `${reviewRating} 星` : "請選擇星等" }}</span>
                  </div>
                </fieldset>

                <label class="review-comment-field" for="review-comment">
                  <span>評論內容</span>
                  <textarea
                    id="review-comment"
                    ref="reviewCommentInput"
                    v-model="reviewComment"
                    maxlength="1000"
                    rows="5"
                    placeholder="說說你喜歡這項商品的地方……"
                    required
                  ></textarea>
                  <small>{{ reviewComment.length }} / 1000</small>
                </label>

                <div class="review-form-footer">
                  <p
                    v-if="reviewMessage"
                    :class="['review-message', reviewMessageType]"
                    role="status"
                    aria-live="polite"
                  >
                    {{ reviewMessage }}
                  </p>
                  <button
                    type="submit"
                    class="review-submit-button"
                    :disabled="submittingReview || !reviewRating || !reviewComment.trim()"
                  >
                    {{ submittingReview ? "送出中…" : editingReviewId || ownReview ? "更新評價" : "送出評價" }}
                  </button>
                  <button
                    v-if="editingReviewId"
                    type="button"
                    class="review-cancel-button"
                    @click="cancelEditReview"
                  >
                    取消編輯
                  </button>
                </div>
              </form>

              <div v-else class="review-login-notice">
                <div>
                  <h3>登入後即可發表評論</h3>
                  <p>遊客可以閱讀會員評論，登入會員後才能評分與留言。</p>
                </div>
                <RouterLink to="/login">前往登入</RouterLink>
              </div>

              <div class="review-list-section">
                <h3>會員評論</h3>

                <div v-if="reviewsLoading" class="reviews-loading" aria-live="polite">
                  評價載入中…
                </div>
                <div v-else-if="reviewsError" class="reviews-error" role="alert">
                  <p>{{ reviewsError }}</p>
                  <button type="button" @click="loadReviews">重新載入</button>
                </div>
                <div v-else-if="reviews.length === 0" class="reviews-empty">
                  <p>目前尚無商品評價，成為第一位分享心得的會員吧。</p>
                </div>
                <ol v-else class="review-list">
                  <li v-for="review in reviews" :key="review.reviewId" class="review-item">
                    <article class="review-card">
                      <div class="review-card-header">
                        <div class="review-identity">
                          <div class="review-avatar" aria-hidden="true">
                            {{ getMemberInitial(review.memberName) }}
                          </div>
                          <div class="review-author">
                            <strong>{{ review.memberName }}</strong>
                            <span v-if="review.ownReview" class="own-review-label">我的評論</span>
                          </div>
                        </div>
                        <div v-if="canDeleteReview(review)" class="review-menu" @click.stop>
                            <button
                              type="button"
                              class="review-menu-trigger"
                              :aria-label="`${review.memberName} 的評論操作`"
                              aria-haspopup="true"
                              :aria-expanded="openReviewMenuId === review.reviewId"
                              :aria-controls="`review-menu-${review.reviewId}`"
                              @click="toggleReviewMenu(review.reviewId)"
                              @keydown.esc="closeReviewMenu"
                            >
                              <svg viewBox="0 0 24 24" aria-hidden="true">
                                <circle cx="5" cy="12" r="1.5" />
                                <circle cx="12" cy="12" r="1.5" />
                                <circle cx="19" cy="12" r="1.5" />
                              </svg>
                            </button>
                            <div
                              v-if="openReviewMenuId === review.reviewId"
                              :id="`review-menu-${review.reviewId}`"
                              class="review-menu-popover"
                              aria-label="評論操作"
                              @keydown.esc="closeReviewMenu"
                            >
                              <button
                                v-if="review.ownReview"
                                type="button"
                                @click="startEditReview(review)"
                              >
                                <svg viewBox="0 0 24 24" aria-hidden="true">
                                  <path d="m4 20 4.2-1 10.6-10.6a2 2 0 0 0-2.8-2.8L5.4 16.2 4 20ZM14.5 7.1l2.8 2.8" />
                                </svg>
                                編輯
                              </button>
                              <button
                                type="button"
                                class="danger"
                                :disabled="deletingReviewId === review.reviewId"
                                @click="deleteReview(review)"
                              >
                                <svg viewBox="0 0 24 24" aria-hidden="true">
                                  <path d="M4 7h16M9 7V4h6v3M6.5 7l.8 13h9.4l.8-13M10 11v5M14 11v5" />
                                </svg>
                                {{ deletingReviewId === review.reviewId ? "刪除中…" : "刪除" }}
                              </button>
                            </div>
                        </div>
                      </div>
                      <div class="review-meta-row">
                        <div class="display-stars review-stars" :aria-label="`${review.rating} 星評價`">
                          <svg
                            v-for="star in 5"
                            :key="star"
                            viewBox="0 0 24 24"
                            :class="{ filled: star <= review.rating }"
                            aria-hidden="true"
                          >
                            <path d="m12 2.7 2.83 5.73 6.32.92-4.58 4.46 1.08 6.3L12 17.14l-5.65 2.97 1.08-6.3-4.58-4.46 6.32-.92L12 2.7Z" />
                          </svg>
                        </div>
                        <span aria-hidden="true">·</span>
                        <time :datetime="review.updatedAt">{{ formatReviewDate(review.updatedAt) }}</time>
                      </div>
                      <p>{{ review.comment }}</p>
                    </article>
                  </li>
                </ol>
                <p
                  v-if="reviewListMessage"
                  :class="['review-list-message', reviewListMessageType]"
                  role="status"
                  aria-live="polite"
                >
                  {{ reviewListMessage }}
                </p>
              </div>
            </div>

            <div v-else-if="activeProductTab === 'specifications'" class="tab-copy">
              <h2>商品規格</h2>
              <dl class="specification-list">
                <div>
                  <dt>商品名稱</dt>
                  <dd>{{ product.productName }}</dd>
                </div>
                <div>
                  <dt>商品編號</dt>
                  <dd>{{ product.productId }}</dd>
                </div>
                <div>
                  <dt>商品分類</dt>
                  <dd>{{ product.category?.categoryName || "未分類" }}</dd>
                </div>
                <div>
                  <dt>庫存狀態</dt>
                  <dd>{{ statusLabel }}（{{ normalizedStock }} 件）</dd>
                </div>
              </dl>
            </div>

            <div v-else-if="activeProductTab === 'returns'" class="tab-copy">
              <h2>退／換貨須知</h2>
              <ul class="return-notice-list">
                <li>申請退換貨前，請保留商品本體、配件及原始包裝。</li>
                <li>若商品有損壞或內容不符，請拍照並聯絡飯店客服協助處理。</li>
                <li>實際受理條件與期限，以訂單資訊及客服確認結果為準。</li>
              </ul>
            </div>

            <div v-else class="tab-copy related-category-content">
              <h2>相關類別</h2>
              <p>
                此商品屬於
                <strong>{{ product.category?.categoryName || "未分類商品" }}</strong>
                類別。
              </p>
              <RouterLink to="/products" class="category-shop-link">
                查看更多商城商品
                <svg viewBox="0 0 24 24" aria-hidden="true">
                  <path d="M5 12h14M13 6l6 6-6 6" />
                </svg>
              </RouterLink>
            </div>
          </div>
        </section>

        <section
          v-if="recentlyViewedProducts.length > 0"
          class="recent-panel"
          aria-labelledby="recent-title"
        >
          <div class="recent-heading">
            <div>
              <p class="section-eyebrow">RECENTLY VIEWED</p>
              <h2 id="recent-title">最近瀏覽</h2>
            </div>
            <RouterLink to="/products">查看全部商品</RouterLink>
          </div>

          <div class="recent-list">
            <RouterLink
              v-for="item in recentlyViewedProducts"
              :key="item.productId"
              :to="{ name: 'product-detail', params: { id: item.productId } }"
              class="recent-card"
            >
              <div class="recent-image-wrap">
                <img
                  :src="getProductImage(item)"
                  :alt="item.productName"
                  @error="handleImageError"
                />
              </div>
              <div class="recent-info">
                <span>{{ item.category?.categoryName || "星澄嚴選" }}</span>
                <h3>{{ item.productName }}</h3>
                <p>NT$ {{ formatPrice(item.price) }}</p>
              </div>
            </RouterLink>
          </div>
        </section>
          </div>
        </div>
      </template>
    </main>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref, watch } from "vue";
import { useRoute } from "vue-router";
import { storeToRefs } from "pinia";

import { getAuthHeaders } from "@/utils/auth";
import { useAuthStore } from "@/stores/auth";
import { normalizeProductId } from "@/utils/productId";
import {
  addRecentlyViewed,
  getRecentlyViewedIds,
} from "@/utils/recentlyViewed";
import { getWishlistIds, toggleWishlist } from "@/utils/wishlist";

const route = useRoute();
const authStore = useAuthStore();
const { authorities, isLoggedIn } = storeToRefs(authStore);

const DEFAULT_IMAGE = "/upload/products/default-product.jpg";
const PRODUCT_API = "/api/products";
const VISIBLE_PRODUCT_STATUSES = new Set([
  "ACTIVE",
  "OUT_OF_STOCK",
  "上架",
  "上架中",
  "售罄",
]);

const product = ref(null);
const allProducts = ref([]);
const loading = ref(false);
const errorMessage = ref("");
const wishlistIds = ref(getWishlistIds());
const recentlyViewedIds = ref(getRecentlyViewedIds());
const quantity = ref(1);
const purchaseMessage = ref("");
const purchaseMessageType = ref("success");
const activeProductTab = ref("features");
const reviews = ref([]);
const reviewsLoading = ref(false);
const reviewsError = ref("");
const reviewRating = ref(0);
const hoveredRating = ref(0);
const reviewComment = ref("");
const reviewMessage = ref("");
const reviewMessageType = ref("success");
const submittingReview = ref(false);
const deletingReviewId = ref(null);
const reviewListMessage = ref("");
const reviewListMessageType = ref("success");
const editingReviewId = ref(null);
const openReviewMenuId = ref(null);
const reviewForm = ref(null);
const reviewCommentInput = ref(null);
const originalDocumentTitle = document.title;

const productTabs = computed(() => [
  { key: "features", label: "商品特色" },
  { key: "reviews", label: `商品評價 (${reviews.value.length})` },
  { key: "specifications", label: "商品規格" },
  { key: "returns", label: "退／換貨須知" },
  { key: "related", label: "相關類別" },
]);

const ownReview = computed(() =>
  reviews.value.find((review) => review.ownReview),
);

const canManageReviews = computed(() =>
  authorities.value.some((authority) =>
    ["ROLE_ADMIN", "ROLE_EMPLOYEE"].includes(authority),
  ),
);

const averageRating = computed(() => {
  if (!reviews.value.length) return "0.0";
  const total = reviews.value.reduce(
    (sum, review) => sum + Number(review.rating || 0),
    0,
  );
  return (total / reviews.value.length).toFixed(1);
});

const purchasableRelatedProducts = computed(() =>
  allProducts.value.filter((item) => {
    const itemId = Number(item.productId);
    const currentId = Number(product.value?.productId);
    const active = ["ACTIVE", "上架", "上架中"].includes(item.status);

    return itemId !== currentId && active && Number(item.stock ?? 0) > 0;
  }),
);

const recommendedProducts = computed(() => {
  const currentCategoryId = Number(product.value?.category?.categoryId);
  const sameCategory = purchasableRelatedProducts.value.filter(
    (item) => Number(item.category?.categoryId) === currentCategoryId,
  );
  const otherCategories = purchasableRelatedProducts.value.filter(
    (item) => Number(item.category?.categoryId) !== currentCategoryId,
  );

  return [...sameCategory, ...otherCategories].slice(0, 3);
});

const recentlyViewedProducts = computed(() => {
  const currentId = Number(product.value?.productId);

  return recentlyViewedIds.value
    .filter((id) => Number(id) !== currentId)
    .map((id) =>
      allProducts.value.find(
        (item) => Number(item.productId) === Number(id),
      ),
    )
    .filter(Boolean)
    .slice(0, 4);
});

const normalizedStock = computed(() => {
  const stock = Number(product.value?.stock ?? 0);
  return Number.isFinite(stock) && stock > 0 ? stock : 0;
});

const productImage = computed(() => {
  return getProductImage(product.value);
});

const inWishlist = computed(() => {
  if (!isLoggedIn.value) return false;

  const id = Number(product.value?.productId);
  return wishlistIds.value.some((itemId) => Number(itemId) === id);
});

const stockLabel = computed(() => {
  if (!canBuy.value) return "暫無法購買";
  if (normalizedStock.value <= 5) return `僅剩 ${normalizedStock.value} 件`;
  return "現貨供應";
});

const stockBadgeClass = computed(() => ({
  available: canBuy.value && normalizedStock.value > 5,
  low: canBuy.value && normalizedStock.value <= 5,
  unavailable: !canBuy.value,
}));

const statusLabel = computed(() => {
  const labels = {
    ACTIVE: "販售中",
    OUT_OF_STOCK: "已售罄",
    INACTIVE: "未上架",
    DISCONTINUED: "已停產",
    上架: "販售中",
    上架中: "販售中",
    售罄: "已售罄",
  };

  return labels[product.value?.status] || product.value?.status || "未設定";
});

const canBuy = computed(() => {
  const activeStatuses = new Set(["ACTIVE", "上架", "上架中"]);
  return activeStatuses.has(product.value?.status) && normalizedStock.value > 0;
});

async function loadProduct() {
  const id = normalizeProductId(route.params.id);

  product.value = null;
  errorMessage.value = "";
  quantity.value = 1;
  activeProductTab.value = "features";
  purchaseMessage.value = "";
  reviews.value = [];
  reviewsError.value = "";
  reviewRating.value = 0;
  reviewComment.value = "";
  reviewMessage.value = "";
  editingReviewId.value = null;
  openReviewMenuId.value = null;

  if (id === null) {
    errorMessage.value = "商品編號不正確";
    return;
  }

  loading.value = true;
  void loadRelatedProducts();

  try {
    const response = await fetch(`${PRODUCT_API}/${id}`, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    if (response.status === 404) {
      throw new Error("找不到這項商品");
    }

    if (response.status === 401 || response.status === 403) {
      throw new Error("請先登入後再查看商品");
    }

    if (!response.ok) {
      throw new Error(`商品載入失敗（${response.status}）`);
    }

    const data = await response.json();

    if (!VISIBLE_PRODUCT_STATUSES.has(data.status)) {
      throw new Error("這項商品目前未開放瀏覽");
    }

    product.value = data;
    void loadReviews();
    addRecentlyViewed(id);
    recentlyViewedIds.value = getRecentlyViewedIds();
    document.title = `${data.productName}｜星澄飯店商城`;
  } catch (error) {
    console.error("商品詳細資料載入失敗：", error);
    errorMessage.value = error.message || "商品載入失敗";
  } finally {
    loading.value = false;
  }
}

function handleProductTabKeydown(event, currentIndex) {
  let nextIndex = currentIndex;

  if (event.key === "ArrowRight") {
    nextIndex = (currentIndex + 1) % productTabs.value.length;
  } else if (event.key === "ArrowLeft") {
    nextIndex = (currentIndex - 1 + productTabs.value.length) % productTabs.value.length;
  } else if (event.key === "Home") {
    nextIndex = 0;
  } else if (event.key === "End") {
    nextIndex = productTabs.value.length - 1;
  } else {
    return;
  }

  event.preventDefault();
  const nextTab = productTabs.value[nextIndex];
  activeProductTab.value = nextTab.key;
  document.getElementById(`product-tab-${nextTab.key}`)?.focus();
}

async function loadReviews() {
  const productId = normalizeProductId(product.value?.productId ?? route.params.id);
  if (productId === null) return;

  reviewsLoading.value = true;
  reviewsError.value = "";

  try {
    const response = await fetch(`${PRODUCT_API}/${productId}/reviews`, {
      method: "GET",
      headers: getAuthHeaders(),
    });
    if (!response.ok) {
      throw new Error(response.status === 401 || response.status === 403
        ? "請先登入會員後查看商品評價。"
        : "目前無法載入商品評價。");
    }

    const data = await response.json();
    reviews.value = Array.isArray(data) ? data : [];
  } catch (error) {
    console.error("商品評價載入失敗：", error);
    reviewsError.value = error.message || "目前無法載入商品評價。";
  } finally {
    reviewsLoading.value = false;
  }
}

async function submitReview() {
  if (!isLoggedIn.value) return;

  const productId = normalizeProductId(product.value?.productId);
  if (productId === null || submittingReview.value) return;

  reviewMessage.value = "";
  if (!reviewRating.value) {
    reviewMessage.value = "請先選擇 1 到 5 星評分。";
    reviewMessageType.value = "error";
    return;
  }
  if (!reviewComment.value.trim()) {
    reviewMessage.value = "請輸入評論內容。";
    reviewMessageType.value = "error";
    return;
  }

  submittingReview.value = true;
  try {
    const response = await fetch(`${PRODUCT_API}/${productId}/reviews`, {
      method: "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify({
        rating: reviewRating.value,
        comment: reviewComment.value.trim(),
      }),
    });
    const data = await response.json().catch(() => ({}));
    if (!response.ok) {
      throw new Error(data.message || "評價送出失敗，請稍後再試。");
    }

    const wasUpdate = Boolean(ownReview.value);
    await loadReviews();
    resetReviewForm();
    reviewMessage.value = wasUpdate ? "評價已更新。" : "謝謝你的評價！";
    reviewMessageType.value = "success";
  } catch (error) {
    console.error("商品評價送出失敗：", error);
    reviewMessage.value = error.message || "評價送出失敗，請稍後再試。";
    reviewMessageType.value = "error";
  } finally {
    submittingReview.value = false;
  }
}

async function deleteReview(review) {
  const productId = normalizeProductId(product.value?.productId);
  if (productId === null || !canDeleteReview(review) || deletingReviewId.value !== null) return;

  const confirmed = window.confirm(
    `確定要刪除「${review.memberName}」的這則評論嗎？此操作無法復原。`,
  );
  if (!confirmed) return;

  closeReviewMenu();
  deletingReviewId.value = review.reviewId;
  reviewListMessage.value = "";

  try {
    const response = await fetch(
      `${PRODUCT_API}/${productId}/reviews/${review.reviewId}`,
      {
        method: "DELETE",
        headers: getAuthHeaders(),
      },
    );
    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      throw new Error(data.message || "評論刪除失敗，請稍後再試。");
    }

    reviews.value = reviews.value.filter(
      (item) => item.reviewId !== review.reviewId,
    );
    if (review.ownReview) {
      resetReviewForm();
    }
    reviewListMessage.value = "評論已刪除。";
    reviewListMessageType.value = "success";
  } catch (error) {
    console.error("商品評論刪除失敗：", error);
    reviewListMessage.value = error.message || "評論刪除失敗，請稍後再試。";
    reviewListMessageType.value = "error";
  } finally {
    deletingReviewId.value = null;
  }
}

async function startEditReview(review) {
  if (!review?.ownReview) return;

  editingReviewId.value = review.reviewId;
  reviewRating.value = Number(review.rating);
  hoveredRating.value = 0;
  reviewComment.value = review.comment || "";
  reviewMessage.value = "";
  closeReviewMenu();

  await nextTick();
  reviewForm.value?.scrollIntoView({ behavior: "smooth", block: "center" });
  reviewCommentInput.value?.focus({ preventScroll: true });
}

function cancelEditReview() {
  resetReviewForm();
  reviewMessage.value = "";
}

function resetReviewForm() {
  editingReviewId.value = null;
  reviewRating.value = 0;
  hoveredRating.value = 0;
  reviewComment.value = "";
}

function toggleReviewMenu(reviewId) {
  openReviewMenuId.value = openReviewMenuId.value === reviewId ? null : reviewId;
}

function closeReviewMenu() {
  openReviewMenuId.value = null;
}

function handleDocumentPointerDown(event) {
  if (!event.target.closest?.(".review-menu")) {
    closeReviewMenu();
  }
}

function canDeleteReview(review) {
  return canManageReviews.value || Boolean(review?.ownReview);
}

function getMemberInitial(memberName) {
  return String(memberName || "會員").trim().charAt(0).toUpperCase();
}

function formatReviewDate(value) {
  if (!value) return "";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "";
  return new Intl.DateTimeFormat("zh-TW", {
    year: "numeric",
    month: "long",
    day: "numeric",
  }).format(date);
}

async function loadRelatedProducts() {
  if (allProducts.value.length > 0) return;

  try {
    const response = await fetch(PRODUCT_API, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    if (!response.ok) return;

    const data = await response.json();
    allProducts.value = Array.isArray(data) ? data : [];
  } catch (error) {
    console.error("推薦商品載入失敗：", error);
  }
}

function getProductImage(item) {
  const imageUrl = item?.imageUrl?.trim();

  if (!imageUrl) return DEFAULT_IMAGE;
  if (/^https?:\/\//i.test(imageUrl) || imageUrl.startsWith("/")) {
    return imageUrl;
  }

  return `/upload/products/${imageUrl}`;
}

function handleImageError(event) {
  if (event.target.dataset.fallback === "true") return;

  event.target.dataset.fallback = "true";
  event.target.src = DEFAULT_IMAGE;
}

function handleToggleWishlist() {
  if (!isLoggedIn.value) return;

  const id = normalizeProductId(product.value?.productId);
  if (id === null) return;

  toggleWishlist(id);
  wishlistIds.value = getWishlistIds();
}

function increaseQuantity() {
  if (canBuy.value && quantity.value < normalizedStock.value) {
    quantity.value += 1;
    purchaseMessage.value = "";
  }
}

function decreaseQuantity() {
  if (quantity.value > 1) {
    quantity.value -= 1;
    purchaseMessage.value = "";
  }
}

function showPurchaseMessage(message, type = "success") {
  purchaseMessage.value = message;
  purchaseMessageType.value = type;
}

function addToCart() {
  if (!isLoggedIn.value) {
    showPurchaseMessage("請先登入會員後再加入購物車。", "error");
    return;
  }

  if (!product.value || !canBuy.value) {
    showPurchaseMessage("此商品目前無法購買。", "error");
    return;
  }

  let cart = [];

  try {
    const savedCart = localStorage.getItem("cart");
    const parsedCart = savedCart ? JSON.parse(savedCart) : [];
    cart = Array.isArray(parsedCart) ? parsedCart : [];
  } catch (error) {
    console.error("購物車資料讀取失敗：", error);
  }

  const productId = Number(product.value.productId);
  const selectedQuantity = Number(quantity.value);
  const existingItem = cart.find(
    (item) => Number(item.productId) === productId,
  );
  const quantityInCart = Number(existingItem?.quantity ?? 0);
  const nextQuantity = quantityInCart + selectedQuantity;

  if (nextQuantity > normalizedStock.value) {
    const availableQuantity = Math.max(
      normalizedStock.value - quantityInCart,
      0,
    );

    showPurchaseMessage(
      availableQuantity > 0
        ? `購物車內已有 ${quantityInCart} 件，本次最多還能加入 ${availableQuantity} 件。`
        : `購物車內已有此商品的可購買上限 ${normalizedStock.value} 件。`,
      "error",
    );
    return;
  }

  if (existingItem) {
    existingItem.quantity = nextQuantity;
    existingItem.productName = product.value.productName;
    existingItem.price = product.value.price;
    existingItem.stock = product.value.stock;
    existingItem.imageUrl = product.value.imageUrl ?? null;
  } else {
    cart.push({
      productId: product.value.productId,
      productName: product.value.productName,
      price: product.value.price,
      quantity: selectedQuantity,
      stock: product.value.stock,
      imageUrl: product.value.imageUrl ?? null,
    });
  }

  localStorage.setItem("cart", JSON.stringify(cart));
  showPurchaseMessage(
    `已將 ${product.value.productName} × ${selectedQuantity} 加入購物車。`,
  );
  quantity.value = 1;
}

function formatPrice(price) {
  return Number(price ?? 0).toLocaleString("zh-TW");
}

watch(() => route.params.id, loadProduct, { immediate: true });

onMounted(() => {
  document.addEventListener("pointerdown", handleDocumentPointerDown);
});

onBeforeUnmount(() => {
  document.removeEventListener("pointerdown", handleDocumentPointerDown);
  document.title = originalDocumentTitle;
});
</script>

<style scoped src="@/assets/product-detail.css"></style>
