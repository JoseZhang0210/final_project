<script setup>
import { onBeforeUnmount, onMounted, reactive, ref } from "vue";

import { useRoute, useRouter } from "vue-router";

import { getAuthHeaders } from "@/utils/auth";

import { normalizeProductId } from "@/utils/productId";

// =====================================================
// Router
// =====================================================

const route = useRoute();

const router = useRouter();

const productId = route.params.id;

// =====================================================
// API
// =====================================================

const PRODUCT_API = "/api/products";

const CATEGORY_API = "/api/categories";

const IMAGE_UPLOAD_API = "/api/products/upload-image";

const PRODUCT_STATUSES = new Set([
  "ACTIVE",
  "INACTIVE",
  "OUT_OF_STOCK",
  "DISCONTINUED",
]);

// =====================================================
// 狀態
// =====================================================

const loading = ref(false);

const saving = ref(false);

const categories = ref([]);

const message = ref("");

const messageType = ref("");

// =====================================================
// 圖片
// =====================================================

const imagePreview = ref("");

const selectedImageFile = ref(null);

const imageError = ref(false);

const imageInput = ref(null);

let objectUrl = null;

let messageTimer = null;

// =====================================================
// 表單
// =====================================================

const form = reactive({
  productName: "",

  categoryId: "",

  description: "",

  price: 0,

  stock: 0,

  imageUrl: "",

  status: "ACTIVE",
});

// =====================================================
// 訊息
// =====================================================

function showMessage(text, type) {
  if (messageTimer) {
    clearTimeout(messageTimer);
  }

  message.value = text;

  messageType.value = type;

  messageTimer = setTimeout(() => {
    message.value = "";
    messageTimer = null;
  }, 2500);
}

// =====================================================
// 讀取分類
// =====================================================

async function loadCategories() {
  try {
    const response = await fetch(CATEGORY_API, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    if (!response.ok) {
      console.error("分類讀取失敗：", response.status);

      return;
    }

    const data = await response.json();

    if (!Array.isArray(data)) {
      throw new Error("分類資料格式錯誤");
    }

    categories.value = data;
  } catch (error) {
    console.error("分類讀取錯誤：", error);

    showMessage(error.message || "分類讀取失敗", "error");
  }
}

// =====================================================
// 讀取商品
// =====================================================

async function loadProduct() {
  const id = normalizeProductId(productId);

  if (id === null) {
    showMessage("商品 ID 不正確", "error");
    return;
  }

  loading.value = true;

  try {
    const response = await fetch(`${PRODUCT_API}/${id}`, {
      method: "GET",

      headers: getAuthHeaders(),
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有權限", "error");

      return;
    }

    if (!response.ok) {
      showMessage("找不到商品資料", "error");

      return;
    }

    const product = await response.json();

    console.log("商品資料：", product);

    form.productName = product.productName || "";

    form.categoryId = product.category?.categoryId ?? product.categoryId ?? "";

    form.description = product.description || "";

    form.price = product.price ?? 0;

    form.stock = product.stock ?? 0;

    form.imageUrl = product.imageUrl || "";

    form.status = product.status || "ACTIVE";

    // 原本圖片直接顯示
    imagePreview.value = form.imageUrl;
  } catch (error) {
    console.error("讀取商品錯誤：", error);

    showMessage("讀取商品失敗", "error");
  } finally {
    loading.value = false;
  }
}

// =====================================================
// URL 即時預覽
// =====================================================

function previewUrlImage() {
  selectedImageFile.value = null;

  revokeObjectUrl();

  imageError.value = false;

  if (form.imageUrl && form.imageUrl.trim()) {
    imagePreview.value = form.imageUrl.trim();
  } else {
    imagePreview.value = "";
  }
}

// =====================================================
// 選擇本機圖片
// =====================================================

function handleImageSelect(event) {
  const file = event.target.files?.[0];

  if (!file) {
    return;
  }

  // 必須是圖片
  if (!file.type.startsWith("image/")) {
    showMessage("只能選擇圖片檔案", "error");

    event.target.value = "";

    return;
  }

  // 最大 5MB
  const maxSize = 5 * 1024 * 1024;

  if (file.size > maxSize) {
    showMessage("圖片大小不能超過 5MB", "error");

    event.target.value = "";

    return;
  }

  selectedImageFile.value = file;

  imageError.value = false;

  form.imageUrl = "";

  revokeObjectUrl();

  objectUrl = URL.createObjectURL(file);

  imagePreview.value = objectUrl;
}

// =====================================================
// 圖片預覽失敗
// =====================================================

function handlePreviewError() {
  imageError.value = true;
}

// =====================================================
// 清除圖片
// =====================================================

function clearImage() {
  form.imageUrl = "";

  selectedImageFile.value = null;

  imagePreview.value = "";

  imageError.value = false;

  if (imageInput.value) {
    imageInput.value.value = "";
  }

  revokeObjectUrl();
}

function revokeObjectUrl() {
  if (objectUrl) {
    URL.revokeObjectURL(objectUrl);

    objectUrl = null;
  }
}

// =====================================================
// 上傳圖片
// =====================================================

async function uploadImage() {
  // 沒有選本機圖片
  // 直接使用 URL
  if (!selectedImageFile.value) {
    return form.imageUrl;
  }

  const formData = new FormData();

  formData.append("file", selectedImageFile.value);

  const headers = getAuthHeaders();

  delete headers["Content-Type"];

  const response = await fetch(IMAGE_UPLOAD_API, {
    method: "POST",

    headers,

    body: formData,
  });

  if (response.status === 401 || response.status === 403) {
    throw new Error("登入狀態失效或沒有上傳權限");
  }

  if (!response.ok) {
    throw new Error("圖片上傳失敗");
  }

  const data = await response.json();

  return data.imageUrl;
}

// =====================================================
// 儲存修改
// =====================================================

async function updateProduct() {
  if (saving.value) {
    return;
  }

  const id = normalizeProductId(productId);

  if (id === null) {
    showMessage("商品 ID 不正確", "error");
    return;
  }

  if (!form.productName.trim()) {
    showMessage("請輸入商品名稱", "error");

    return;
  }

  const categoryId = Number(form.categoryId);

  if (!Number.isSafeInteger(categoryId) || categoryId <= 0) {
    showMessage("請選擇有效的商品分類", "error");

    return;
  }

  const price = Number(form.price);

  if (!Number.isSafeInteger(price) || price < 0) {
    showMessage("商品價格必須是大於或等於 0 的整數", "error");

    return;
  }

  const stock = Number(form.stock);

  if (!Number.isSafeInteger(stock) || stock < 0) {
    showMessage("商品庫存必須是大於或等於 0 的整數", "error");

    return;
  }

  if (!PRODUCT_STATUSES.has(form.status)) {
    showMessage("商品狀態不正確", "error");

    return;
  }

  saving.value = true;

  try {
    // ==========================
    // 有本機圖片先上傳
    // ==========================

    const finalImageUrl = await uploadImage();

    form.imageUrl = finalImageUrl || "";

    // ==========================
    // 商品資料
    // ==========================

    const payload = {
      productName: form.productName.trim(),

      description: form.description,

      price,

      stock,

      imageUrl: form.imageUrl,

      status: form.status,

      category: {
        categoryId,
      },
    };

    console.log("送出商品資料：", payload);

    const response = await fetch(`${PRODUCT_API}/${id}`, {
      method: "PUT",

      headers: getAuthHeaders(),

      body: JSON.stringify(payload),
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有修改權限", "error");

      return;
    }

    if (!response.ok) {
      const text = await response.text();

      console.error("修改商品失敗：", text);

      showMessage("商品修改失敗", "error");

      return;
    }

    showMessage("商品修改成功", "success");

    setTimeout(() => {
      router.push("/admin/products");
    }, 800);
  } catch (error) {
    console.error("修改商品錯誤：", error);

    showMessage(error.message || "商品修改失敗", "error");
  } finally {
    saving.value = false;
  }
}

// =====================================================
// 返回
// =====================================================

function goBack() {
  if (saving.value) {
    return;
  }

  router.push("/admin/products");
}

// =====================================================
// 初始化
// =====================================================

onMounted(async () => {
  await loadCategories();

  await loadProduct();
});

onBeforeUnmount(() => {
  revokeObjectUrl();

  if (messageTimer) {
    clearTimeout(messageTimer);
  }
});
</script>

<template>
  <div class="product-form-page product-edit-page">
    <!-- =========================
         Header
         ========================= -->

    <div class="admin-page-header">
      <div>
        <h1>修改商品</h1>

        <p>修改商品基本資料、價格、庫存與商品狀態</p>
      </div>
    </div>

    <!-- =========================
         訊息
         ========================= -->

    <div v-if="message" class="admin-message" :class="messageType">
      {{ message }}
    </div>

    <!-- =========================
         Loading
         ========================= -->

    <div v-if="loading" class="admin-card loading-box">商品資料讀取中...</div>

    <!-- =========================
         Form
         ========================= -->

    <form
      v-else
      class="admin-card product-form"
      @submit.prevent="updateProduct"
    >
      <div class="admin-form-grid">
        <!-- 商品名稱 -->

        <div class="admin-form-group full-width">
          <label> 商品名稱 </label>

          <input
            v-model="form.productName"
            type="text"
            placeholder="請輸入商品名稱"
            required
          />
        </div>

        <!-- 商品分類 -->

        <div class="admin-form-group">
          <label> 商品分類 </label>

          <select v-model="form.categoryId" required>
            <option value="">請選擇分類</option>

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
          <label> 商品狀態 </label>

          <select v-model="form.status">
            <option value="ACTIVE">上架</option>

            <option value="INACTIVE">下架</option>

            <option value="OUT_OF_STOCK">缺貨</option>

            <option value="DISCONTINUED">停售</option>
          </select>
        </div>

        <!-- 價格 -->

        <div class="admin-form-group">
          <label> 價格 </label>

          <input
            v-model.number="form.price"
            type="number"
            min="0"
            step="1"
            placeholder="請輸入價格"
            required
          />
        </div>

        <!-- 庫存 -->

        <div class="admin-form-group">
          <label> 庫存 </label>

          <input
            v-model.number="form.stock"
            type="number"
            min="0"
            step="1"
            placeholder="請輸入庫存"
            required
          />
        </div>

        <!-- =========================
             商品圖片
             ========================= -->

        <div class="admin-form-group full-width">
          <label> 商品圖片 </label>

          <div class="image-section">
            <!-- URL -->

            <div class="url-section">
              <span class="image-label"> 圖片網址 </span>

              <input
                v-model="form.imageUrl"
                type="text"
                placeholder="例如：https://images.example.com/product.jpg"
                @input="previewUrlImage"
              />

              <small> 可直接輸入圖片 URL，或使用下方按鈕上傳本機圖片 </small>
            </div>

            <!-- Upload -->

            <div class="image-actions">
              <label class="image-btn upload-image-btn">
                📁 選擇圖片

                <input
                  ref="imageInput"
                  type="file"
                  accept="image/*"
                  hidden
                  @change="handleImageSelect"
                />
              </label>

              <button
                type="button"
                class="image-btn clear-image-btn"
                @click="clearImage"
              >
                清除圖片
              </button>
            </div>

            <!-- 本機檔名 -->

            <div v-if="selectedImageFile" class="selected-file">
              已選擇：

              <strong>
                {{ selectedImageFile.name }}
              </strong>
            </div>

            <!-- Preview -->

            <div v-if="imagePreview" class="image-preview-area">
              <span class="image-label"> 圖片預覽 </span>

              <div class="preview-card">
                <img
                  :src="imagePreview"
                  alt="商品圖片預覽"
                  @error="handlePreviewError"
                />
              </div>
            </div>

            <!-- Error -->

            <div v-if="imageError" class="image-error">
              圖片無法顯示， 請確認圖片網址是否為真正的圖片網址。
            </div>
          </div>
        </div>

        <!-- 商品描述 -->

        <div class="admin-form-group full-width">
          <label> 商品描述 </label>

          <textarea
            v-model="form.description"
            rows="5"
            placeholder="請輸入商品描述"
          ></textarea>
        </div>
      </div>

      <!-- =========================
           Buttons
           ========================= -->

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
          :disabled="saving"
          @click="goBack"
        >
          返回商品列表
        </button>
      </div>
    </form>
  </div>
</template>

<style scoped src="@/assets/product-form.css"></style>
