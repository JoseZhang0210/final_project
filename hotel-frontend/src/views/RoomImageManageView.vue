<script setup>
import { ref } from "vue";

const roomTypes = ref([
  { roomTypeId: 1, typeName: "豪華雙人房" },
  { roomTypeId: 2, typeName: "家庭四人房" },
]);

const roomImages = ref([]);

const message = ref("");
const messageType = ref("");
const formTitle = ref("新增房型圖片");

const form = ref(createEmptyForm());

function createEmptyForm() {
  return {
    imageId: null,
    roomTypeId: "",
    imageUrl: "",
    imageDescription: "",
    isMain: false,
    displayOrder: 0,
  };
}

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
}

function clearForm() {
  form.value = createEmptyForm();
  formTitle.value = "新增房型圖片";
}

function getRoomTypeName(roomTypeId) {
  const roomType = roomTypes.value.find(
    (item) => item.roomTypeId === Number(roomTypeId),
  );

  return roomType?.typeName ?? "未知房型";
}

function saveRoomImage() {
  if (!form.value.roomTypeId) {
    showMessage("請選擇房型", "error");
    return;
  }

  if (!form.value.imageUrl.trim()) {
    showMessage("請輸入圖片網址", "error");
    return;
  }

  if (form.value.isMain) {
    roomImages.value.forEach((image) => {
      if (
        image.roomTypeId === Number(form.value.roomTypeId) &&
        image.imageId !== form.value.imageId
      ) {
        image.isMain = false;
      }
    });
  }

  const imageData = {
    ...form.value,
    roomTypeId: Number(form.value.roomTypeId),
    imageUrl: form.value.imageUrl.trim(),
    displayOrder: Number(form.value.displayOrder),
  };

  if (form.value.imageId === null) {
    const nextId =
      roomImages.value.length === 0
        ? 1
        : Math.max(...roomImages.value.map((image) => image.imageId)) + 1;

    roomImages.value.push({
      ...imageData,
      imageId: nextId,
    });

    showMessage("圖片新增成功", "success");
  } else {
    const index = roomImages.value.findIndex(
      (image) => image.imageId === form.value.imageId,
    );

    if (index !== -1) {
      roomImages.value[index] = imageData;
    }

    showMessage("圖片修改成功", "success");
  }

  clearForm();
}

function editRoomImage(image) {
  form.value = { ...image };
  formTitle.value = `修改圖片 ID：${image.imageId}`;

  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
}

function deleteRoomImage(id) {
  if (!window.confirm("確定刪除這張房型圖片嗎？")) {
    return;
  }

  roomImages.value = roomImages.value.filter(
    (image) => image.imageId !== id,
  );

  if (form.value.imageId === id) {
    clearForm();
  }

  showMessage("圖片已刪除", "success");
}

function handleImageError(event) {
  event.target.style.display = "none";
}
</script>

<template>
  <main class="image-page">
    <header class="page-header">
      <h1>房型圖片管理</h1>
      <p>管理各房型的圖片、封面圖片及顯示順序</p>
    </header>

    <div v-if="message" class="message" :class="messageType">
      {{ message }}
    </div>

    <section class="admin-card">
      <h2>{{ formTitle }}</h2>

      <form @submit.prevent="saveRoomImage">
        <div class="form-grid">
          <div class="form-group">
            <label for="roomType">所屬房型 *</label>

            <select
              id="roomType"
              v-model="form.roomTypeId"
              required
            >
              <option value="" disabled>請選擇房型</option>

              <option
                v-for="roomType in roomTypes"
                :key="roomType.roomTypeId"
                :value="roomType.roomTypeId"
              >
                {{ roomType.typeName }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label for="displayOrder">顯示順序</label>

            <input
              id="displayOrder"
              v-model.number="form.displayOrder"
              type="number"
              min="0"
            />
          </div>

          <div class="form-group full-width">
            <label for="imageUrl">圖片網址 *</label>

            <input
              id="imageUrl"
              v-model.trim="form.imageUrl"
              type="url"
              placeholder="https://example.com/room.jpg"
              required
            />
          </div>

          <div class="form-group full-width">
            <label for="imageDescription">圖片說明</label>

            <input
              id="imageDescription"
              v-model.trim="form.imageDescription"
              type="text"
              placeholder="例如：豪華雙人房臥室"
            />
          </div>

          <label class="checkbox-row full-width">
            <input v-model="form.isMain" type="checkbox" />
            設為此房型的主要封面圖片
          </label>
        </div>

        <div v-if="form.imageUrl" class="preview-area">
          <p>圖片預覽</p>

          <img
            :src="form.imageUrl"
            alt="房型圖片預覽"
            @error="handleImageError"
          />
        </div>

        <div class="form-actions">
          <button type="submit" class="btn primary">
            {{ form.imageId === null ? "新增圖片" : "儲存修改" }}
          </button>

          <button type="button" class="btn secondary" @click="clearForm">
            清除表單
          </button>
        </div>
      </form>
    </section>

    <section class="admin-card">
      <div class="table-header">
        <h2>圖片列表</h2>
        <span>共 {{ roomImages.length }} 張圖片</span>
      </div>

      <div v-if="roomImages.length === 0" class="empty">
        目前沒有房型圖片
      </div>

      <div v-else class="image-grid">
        <article
          v-for="image in roomImages"
          :key="image.imageId"
          class="image-card"
        >
          <div class="image-wrapper">
            <img
              :src="image.imageUrl"
              :alt="image.imageDescription || '房型圖片'"
              @error="handleImageError"
            />

            <span v-if="image.isMain" class="main-badge">
              封面
            </span>
          </div>

          <div class="image-info">
            <h3>{{ getRoomTypeName(image.roomTypeId) }}</h3>

            <p>{{ image.imageDescription || "沒有圖片說明" }}</p>

            <small>顯示順序：{{ image.displayOrder }}</small>

            <div class="actions">
              <button class="btn edit" @click="editRoomImage(image)">
                修改
              </button>

              <button
                class="btn delete"
                @click="deleteRoomImage(image.imageId)"
              >
                刪除
              </button>
            </div>
          </div>
        </article>
      </div>
    </section>
  </main>
</template>

<style scoped>
.image-page {
  padding: 28px;
  color: #243447;
}

.page-header,
.admin-card {
  margin-bottom: 24px;
  padding: 24px;
  background: white;
  border: 1px solid #e4e7ec;
  border-radius: 12px;
  box-shadow: 0 3px 12px rgb(0 0 0 / 6%);
}

.page-header h1 {
  margin: 0 0 8px;
}

.page-header p {
  margin: 0;
  color: #667085;
}

.message {
  margin-bottom: 20px;
  padding: 12px 16px;
  border-radius: 8px;
}

.success {
  color: #176b3a;
  background: #e9f8ef;
}

.error {
  color: #b42318;
  background: #feeceb;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.full-width {
  grid-column: 1 / -1;
}

input,
select {
  padding: 11px 12px;
  font: inherit;
  border: 1px solid #cfd4dc;
  border-radius: 7px;
}

.checkbox-row {
  display: flex;
  align-items: center;
  gap: 8px;
}

.preview-area img,
.image-wrapper img {
  width: 100%;
  object-fit: cover;
  border-radius: 8px;
}

.preview-area {
  width: 360px;
  margin-top: 20px;
}

.preview-area img {
  height: 220px;
}

.form-actions,
.actions {
  display: flex;
  gap: 10px;
  margin-top: 20px;
}

.btn {
  padding: 9px 15px;
  color: white;
  border: none;
  border-radius: 7px;
  cursor: pointer;
}

.primary {
  background: #315b7d;
}

.secondary {
  color: #344054;
  background: #e4e7ec;
}

.edit {
  background: #d59032;
}

.delete {
  background: #c84040;
}

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.image-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.image-card {
  overflow: hidden;
  border: 1px solid #e4e7ec;
  border-radius: 10px;
}

.image-wrapper {
  position: relative;
  height: 190px;
  background: #f2f4f7;
}

.image-wrapper img {
  height: 100%;
}

.main-badge {
  position: absolute;
  top: 10px;
  right: 10px;
  padding: 5px 10px;
  color: white;
  background: #315b7d;
  border-radius: 20px;
}

.image-info {
  padding: 16px;
}

.image-info h3 {
  margin: 0 0 8px;
}

.image-info p {
  min-height: 44px;
  color: #667085;
}

.empty {
  padding: 40px;
  text-align: center;
  color: #667085;
}

@media (max-width: 900px) {
  .image-grid {
    grid-template-columns: 1fr;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }

  .full-width {
    grid-column: auto;
  }
}
</style>