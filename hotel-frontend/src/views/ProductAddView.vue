<template>
  <div class="product-form-page product-add-page">
    <!-- =========================
         頁面標題
         ========================= -->

    <div class="admin-page-header">
      <div>
        <h1>新增商品</h1>

        <p>建立新的商城商品資料</p>
      </div>
    </div>

    <!-- =========================
         新增商品表單
         ========================= -->

    <section class="admin-card product-form-card">
      <!-- 訊息 -->

      <div v-if="message" class="admin-message" :class="messageType">
        {{ message }}
      </div>

      <form @submit.prevent="addProduct">
        <div class="admin-form-grid">
          <!-- =========================
               商品名稱
               ========================= -->

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

          <!-- =========================
               商品分類
               ========================= -->

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

          <!-- =========================
               商品狀態
               ========================= -->

          <div class="admin-form-group">
            <label for="status"> 商品狀態 </label>

            <select id="status" v-model="product.status">
              <option value="ACTIVE">上架</option>

              <option value="INACTIVE">下架</option>

              <option value="OUT_OF_STOCK">缺貨</option>

              <option value="DISCONTINUED">停售</option>
            </select>
          </div>

          <!-- =========================
               價格
               ========================= -->

          <div class="admin-form-group">
            <label for="price"> 價格 </label>

            <input
              id="price"
              v-model.number="product.price"
              type="number"
              min="0"
              step="1"
              placeholder="請輸入商品價格"
              required
            />
          </div>

          <!-- =========================
               庫存
               ========================= -->

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

          <!-- =========================
               商品圖片
               ========================= -->

          <div class="admin-form-group full-width">
            <label> 商品圖片 </label>

            <div class="image-section">
              <!-- =========================
                   圖片網址
                   ========================= -->

              <div class="url-section">
                <span class="image-label"> 圖片網址 </span>

                <input
                  id="imageUrl"
                  v-model="product.imageUrl"
                  type="text"
                  placeholder="可輸入圖片網址，例如：https://images.pexels.com/..."
                  @input="previewUrlImage"
                />

                <small>
                  可直接輸入圖片網址，或使用下方按鈕選擇本機圖片。
                </small>
              </div>

              <!-- =========================
                   圖片按鈕
                   ========================= -->

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

              <!-- =========================
                   已選檔案
                   ========================= -->

              <div v-if="selectedImageFile" class="selected-file">
                已選擇：

                <strong>
                  {{ selectedImageFile.name }}
                </strong>
              </div>

              <!-- =========================
                   圖片預覽
                   ========================= -->

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

              <!-- =========================
                   圖片錯誤
                   ========================= -->

              <div v-if="imageError" class="image-error">
                圖片無法顯示，請確認圖片網址是否正確。
              </div>
            </div>
          </div>

          <!-- =========================
               商品描述
               ========================= -->

          <div class="admin-form-group full-width">
            <label for="description"> 商品描述 </label>

            <textarea
              id="description"
              v-model="product.description"
              rows="5"
              placeholder="請輸入商品描述"
            ></textarea>
          </div>
        </div>

        <!-- =========================
             按鈕
             ========================= -->

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
            :disabled="saving"
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
import { onBeforeUnmount, onMounted, reactive, ref } from "vue";

import { useRouter } from "vue-router";

import { getAuthHeaders } from "@/utils/auth";

const router = useRouter();

// =====================================================
// API
// =====================================================

const CATEGORY_API = "/api/categories";

const PRODUCT_API = "/api/products";

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

const categories = ref([]);

const saving = ref(false);

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

// =====================================================
// 商品資料
// =====================================================

const product = reactive({
  productName: "",

  categoryId: "",

  description: "",

  price: 0,

  stock: 0,

  imageUrl: "",

  status: "ACTIVE",
});

// =====================================================
// 顯示錯誤
// =====================================================

function showError(text) {
  message.value = text;

  messageType.value = "error";
}

// =====================================================
// 顯示成功
// =====================================================

function showSuccess(text) {
  message.value = text;

  messageType.value = "success";
}

// =====================================================
// 載入商品分類
// GET /api/categories
// =====================================================

async function loadCategories() {
  try {
    const response = await fetch(CATEGORY_API, {
      method: "GET",

      headers: getAuthHeaders(),
    });

    if (response.status === 401 || response.status === 403) {
      throw new Error("沒有權限讀取商品分類");
    }

    if (!response.ok) {
      throw new Error("分類讀取失敗");
    }

    const data = await response.json();

    if (!Array.isArray(data)) {
      throw new Error("分類資料格式錯誤");
    }

    categories.value = data;
  } catch (error) {
    console.error("分類讀取失敗：", error);

    showError(error.message || "分類資料讀取失敗");
  }
}

// =====================================================
// URL 圖片預覽
// =====================================================

function previewUrlImage() {
  // 使用 URL 時，
  // 清除原本選擇的本機圖片

  selectedImageFile.value = null;

  revokeObjectUrl();

  imageError.value = false;

  const url = product.imageUrl?.trim();

  if (url) {
    imagePreview.value = url;
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

  // ==========================
  // 檢查是不是圖片
  // ==========================

  if (!file.type.startsWith("image/")) {
    showError("只能選擇圖片檔案");

    event.target.value = "";

    return;
  }

  // ==========================
  // 限制 5MB
  // ==========================

  const maxSize = 5 * 1024 * 1024;

  if (file.size > maxSize) {
    showError("圖片大小不能超過 5MB");

    event.target.value = "";

    return;
  }

  selectedImageFile.value = file;

  imageError.value = false;

  // 清除 URL 欄位
  // 避免使用者不知道目前是哪一種來源

  product.imageUrl = "";

  revokeObjectUrl();

  objectUrl = URL.createObjectURL(file);

  imagePreview.value = objectUrl;
}

// =====================================================
// 預覽失敗
// =====================================================

function handlePreviewError() {
  imageError.value = true;
}

// =====================================================
// 清除圖片
// =====================================================

function clearImage() {
  product.imageUrl = "";

  selectedImageFile.value = null;

  imagePreview.value = "";

  imageError.value = false;

  if (imageInput.value) {
    imageInput.value.value = "";
  }

  revokeObjectUrl();
}

// =====================================================
// 清除 createObjectURL
// =====================================================

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
  // ==========================
  // 沒有選本機圖片
  // 直接使用輸入的 URL
  // ==========================

  if (!selectedImageFile.value) {
    return product.imageUrl?.trim() || "";
  }

  // ==========================
  // Multipart
  // ==========================

  const formData = new FormData();

  formData.append("file", selectedImageFile.value);

  // ==========================
  // 只帶 JWT
  //
  // 不要放
  // Content-Type: application/json
  //
  // FormData 的 Content-Type
  // 要讓瀏覽器自己建立
  // ==========================

  const headers = getAuthHeaders();

  delete headers["Content-Type"];

  const response = await fetch(IMAGE_UPLOAD_API, {
    method: "POST",

    headers,

    body: formData,
  });

  if (response.status === 401 || response.status === 403) {
    throw new Error("沒有圖片上傳權限，請重新登入");
  }

  if (!response.ok) {
    const text = await response.text();

    console.error("圖片上傳失敗：", text);

    throw new Error("圖片上傳失敗");
  }

  const data = await response.json();

  if (!data.imageUrl) {
    throw new Error("後端沒有回傳圖片網址");
  }

  return data.imageUrl;
}

// =====================================================
// 新增商品
// POST /api/products
// =====================================================

async function addProduct() {
  if (saving.value) {
    return;
  }

  message.value = "";

  // ==========================
  // 基本驗證
  // ==========================

  if (!product.productName.trim()) {
    showError("請輸入商品名稱");

    return;
  }

  const categoryId = Number(product.categoryId);

  if (!Number.isSafeInteger(categoryId) || categoryId <= 0) {
    showError("請選擇有效的商品分類");

    return;
  }

  const price = Number(product.price);

  if (!Number.isSafeInteger(price) || price < 0) {
    showError("商品價格必須是大於或等於 0 的整數");

    return;
  }

  const stock = Number(product.stock);

  if (!Number.isSafeInteger(stock) || stock < 0) {
    showError("商品庫存必須是大於或等於 0 的整數");

    return;
  }

  if (!PRODUCT_STATUSES.has(product.status)) {
    showError("商品狀態不正確");

    return;
  }

  saving.value = true;

  try {
    // ==========================
    // 如果有選本機圖片
    // 先上傳圖片
    // ==========================

    const finalImageUrl = await uploadImage();

    // ==========================
    // 商品 JSON
    // ==========================

    const requestBody = {
      productName: product.productName.trim(),

      category: {
        categoryId,
      },

      description: product.description?.trim() || "",

      price,

      stock,

      imageUrl: finalImageUrl,

      status: product.status,
    };

    console.log("新增商品送出資料：", requestBody);

    // ==========================
    // 新增商品
    // ==========================

    const response = await fetch(PRODUCT_API, {
      method: "POST",

      headers: getAuthHeaders(),

      body: JSON.stringify(requestBody),
    });

    if (response.status === 401 || response.status === 403) {
      showError("沒有新增商品權限，請重新登入");

      return;
    }

    if (!response.ok) {
      const text = await response.text();

      console.error("新增商品失敗：", text);

      showError("新增失敗，錯誤代碼：" + response.status);

      return;
    }

    showSuccess("商品新增成功");

    setTimeout(() => {
      router.push("/admin/products");
    }, 800);
  } catch (error) {
    console.error("新增商品失敗：", error);

    showError(error.message || "新增商品時發生錯誤");
  } finally {
    saving.value = false;
  }
}

// =====================================================
// 返回商品列表
// =====================================================

function backToList() {
  router.push("/admin/products");
}

// =====================================================
// 頁面載入
// =====================================================

onMounted(() => {
  loadCategories();
});

// =====================================================
// 離開頁面
// 清除 blob 預覽網址
// =====================================================

onBeforeUnmount(() => {
  revokeObjectUrl();
});
</script>

<<<<<<< HEAD
<style scoped src="@/assets/product-form.css"></style>
=======
<style scoped>
.product-form-card {
  max-width: 850px;
}

/* =========================
   Form
   ========================= */

.admin-form-actions {
  margin-top: 28px;
}

.admin-btn:disabled {
  opacity: 0.65;

  cursor: not-allowed;

  transform: none;
}

/* =========================
   圖片區
   ========================= */

.image-section {
  padding: 18px;

  border: 1px solid #e3ddd4;

  border-radius: 12px;

  background-color: #faf8f4;
}

.url-section {
  display: flex;

  flex-direction: column;

  gap: 8px;
}

.image-label {
  color: #6f5328;

  font-size: 14px;

  font-weight: bold;
}

.url-section small {
  color: #888;

  font-size: 12px;
}

/* =========================
   圖片按鈕
   ========================= */

.image-actions {
  display: flex;

  align-items: center;

  gap: 10px;

  margin-top: 15px;
}

.image-btn {
  display: inline-flex;

  justify-content: center;

  align-items: center;

  padding: 10px 18px;

  border: none;

  border-radius: 8px;

  font-size: 14px;

  font-weight: bold;

  cursor: pointer;

  transition: 0.2s;
}

.upload-image-btn {
  background-color: #b58a46;

  color: white;
}

.upload-image-btn:hover {
  background-color: #8f692f;
}

.clear-image-btn {
  background-color: #eee9e1;

  color: #5c4d3d;
}

.clear-image-btn:hover {
  background-color: #dfd5c7;
}

/* =========================
   已選圖片
   ========================= */

.selected-file {
  margin-top: 12px;

  padding: 10px;

  border-radius: 7px;

  background-color: #f0eadf;

  color: #665744;

  font-size: 13px;
}

/* =========================
   圖片預覽
   ========================= */

.image-preview-area {
  margin-top: 20px;
}

.preview-card {
  width: 260px;

  height: 220px;

  margin-top: 10px;

  overflow: hidden;

  border: 1px solid #ddd5c9;

  border-radius: 12px;

  background-color: white;

  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
}

.preview-card img {
  width: 100%;

  height: 100%;

  display: block;

  object-fit: cover;
}

/* =========================
   圖片錯誤
   ========================= */

.image-error {
  margin-top: 10px;

  padding: 10px 12px;

  border-radius: 7px;

  background-color: #fde9e7;

  color: #b3443c;

  font-size: 13px;
}

/* =========================
   Textarea
   ========================= */

textarea {
  width: 100%;

  min-height: 120px;

  padding: 12px 14px;

  border: 1px solid #d8d0c5;

  border-radius: 8px;

  resize: vertical;

  font-family: inherit;

  font-size: 15px;
}

textarea:focus {
  outline: none;

  border-color: #b58a46;

  box-shadow: 0 0 0 3px rgba(181, 138, 70, 0.14);
}

/* =========================
   RWD
   ========================= */

@media (max-width: 700px) {
  .product-form-card {
    max-width: 100%;
  }

  .image-actions {
    flex-direction: column;

    align-items: stretch;
  }

  .image-btn {
    width: 100%;
  }

  .preview-card {
    width: 100%;
  }
}
</style>
>>>>>>> a1a75996ad225216fcf2795e933e2c45374de82f
