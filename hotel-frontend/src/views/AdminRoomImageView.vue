<script setup>
import { ref, onMounted , computed } from "vue";
import { roomImageApi } from "@/api/roomImageApi";
import { roomTypeApi } from "@/api/roomTypeApi";

const roomTypes = ref([]);
const roomImages = ref([]);

// 狀態控制
const selectedRoom = ref(null); // 目前正在管理的房型
const showUploadModal = ref(false); // 控制單純上傳素材的 Modal
const showLibraryModal = ref(false); // 控制選擇媒體庫的 Modal

const message = ref("");
const messageType = ref("");

const form = ref(createEmptyForm());

// 計算不重複的媒體庫圖片
const uniqueImages = computed(() => {
  const map = new Map();
  roomImages.value.forEach(img => {
    if (img.path && !map.has(img.path)) {
      map.set(img.path, img);
    }
  });
  return Array.from(map.values());
});

// 當前選中房型的專屬圖片
const currentRoomImages = computed(() => {
  if (!selectedRoom.value) return [];
  // 因為我們直接在 moveImage 中交換了圖片的 path 和 imageDescription
  // 所以這裡只要按照原本的順序 (例如 imageId) 顯示即可，完全不需要任何額外的排序邏輯與欄位！
  return roomImages.value.filter(img => img.roomTypeId === selectedRoom.value.roomTypeId);
});

// 新增一個方法，用來從「圖片說明」中反向抓取正確的中文房型名稱，避開亂碼問題
function getRoomNameFromImage(roomTypeId) {
  const room = roomTypes.value.find(r => r.roomTypeId === roomTypeId);
  // 如果資料庫原本的 typeName 不是亂碼，優先使用
  if (room && room.typeName && !room.typeName.includes('?')) {
    return room.typeName;
  }
  // 如果是亂碼，嘗試從該房型的第一張圖片的 imageDescription 抓取
  const images = roomImages.value.filter(img => img.roomTypeId === roomTypeId);
  if (images.length > 0 && images[0].imageDescription) {
    return images[0].imageDescription;
  }
  return room ? room.typeName : '未知房型';
}

function createEmptyForm() {
  return {
    imageId: null,
    roomTypeId: "",
    imageUrl: "",
    imageFile: null,
    imageSource: "upload", // "upload", "url", "library"
    imageDescription: "",
    isMain: false
  };
}

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
  if (text) setTimeout(() => { message.value = ""; }, 3000);
}

function clearForm() {
  form.value = createEmptyForm();
}

// ================= API 載入 =================
async function loadRoomTypes() {
  try {
    const data = await roomTypeApi.getAllRoomTypes();
    roomTypes.value = Array.isArray(data) ? data : data.content || [];
  } catch (error) {
    console.error("讀取房型失敗:", error);
  }
}

async function loadRoomImages() {
  try {
    const data = await roomImageApi.getAllImages();
    roomImages.value = Array.isArray(data) ? data : data.content || [];
  } catch (error) {
    console.error("讀取圖片失敗:", error);
    showMessage(error.message || "讀取圖片失敗", "error");
  }
}

onMounted(() => {
  loadRoomTypes();
  loadRoomImages();
});

// ================= 房型列表輔助 =================
function getRoomCover(roomTypeId) {
  const images = roomImages.value.filter(img => img.roomTypeId === roomTypeId);
  if (images.length === 0) return '';
  const mainImage = images.find(img => img.isMain);
  const path = mainImage ? mainImage.path : images[0].path;
  return path ? `http://localhost:8081${path}` : '';
}

function getRoomImageCount(roomTypeId) {
  return roomImages.value.filter(img => img.roomTypeId === roomTypeId).length;
}

// ================= 動作處理 =================
function openUploadModal() {
  clearForm();
  // 預設為上傳模式，且不強制綁定房型
  showUploadModal.value = true;
}

function manageRoomImages(room) {
  selectedRoom.value = room;
}

function openAddImageToRoomModal() {
  // 裡面只能放入媒體庫有的圖片：直接開啟媒體庫讓使用者挑選
  showLibraryModal.value = true;
}

async function selectFromLibrary(path) {
  if (selectedRoom.value && !showUploadModal.value) {
    // 為了房型挑選圖片：複製原本在媒體庫中的中文說明
    const selectedImg = uniqueImages.value.find(img => img.path === path);
    const formData = new FormData();
    formData.append("staticPath", path);
    formData.append("roomTypeId", selectedRoom.value.roomTypeId);
    if (selectedImg && selectedImg.imageDescription) {
      formData.append("imageDescription", selectedImg.imageDescription);
    }

    try {
      await roomImageApi.createImage(formData, true);
      showMessage("圖片已成功加入此房型", "success");
      showLibraryModal.value = false;
      await loadRoomImages();
    } catch (error) {
      console.error(error);
      showMessage(error.message || "圖片加入失敗", "error");
    }
  } else {
    // 在上傳表單中選取
    form.value.imageUrl = path;
    form.value.imageSource = "library";
    showLibraryModal.value = false;
  }
}

function handleFileChange(event) {
  const file = event.target.files[0];
  if (file) {
    form.value.imageFile = file;
    form.value.imageUrl = URL.createObjectURL(file);
  } else {
    form.value.imageFile = null;
    form.value.imageUrl = "";
  }
}

async function saveImage() {
  try {
    if (!form.value.imageUrl.trim() && form.value.imageSource !== 'upload') {
      showMessage("請提供圖片", "error");
      return;
    }

    const formData = new FormData();
    if (form.value.imageSource === "upload" && form.value.imageFile) {
      formData.append("file", form.value.imageFile);
    } else if ((form.value.imageSource === "url" || form.value.imageSource === "library") && form.value.imageUrl) {
      formData.append("staticPath", form.value.imageUrl.trim());
    } else {
      showMessage("請上傳圖片、填寫網址或從媒體庫選取", "error");
      return;
    }
    
    if (form.value.imageDescription) {
      formData.append("imageDescription", form.value.imageDescription);
    }

    // 若有選擇房型，則綁定給該房型
    if (form.value.roomTypeId) {
      formData.append("roomTypeId", form.value.roomTypeId);
    } else if (roomTypes.value.length > 0) {
      // 避免後端資料庫限制 room_type_id 不能為 null，如果是單純上傳素材，預設綁給第一個房型當作暫存
      formData.append("roomTypeId", roomTypes.value[0].roomTypeId);
    }

    await roomImageApi.createImage(formData, true);
    showMessage("圖片新增成功", "success");
    
    showUploadModal.value = false;
    await loadRoomImages();
  } catch (error) {
    console.error("saveImage error:", error);
    showMessage(error.message || "圖片儲存失敗", "error");
  }
}

async function deleteRoomImage(id) {
  if (!window.confirm("確定移除這張圖片嗎？")) {
    return;
  }
  try {
    await roomImageApi.deleteImage(id);
    showMessage("圖片已移除", "success");
    await loadRoomImages();
  } catch (error) {
    console.error("deleteRoomImage error:", error);
    showMessage(error.message || "圖片移除失敗", "error");
  }
}

async function moveImage(image, direction) {
  const currentIndex = currentRoomImages.value.findIndex(img => img.imageId === image.imageId);
  if (currentIndex === -1) return;

  const targetIndex = currentIndex + direction;
  if (targetIndex < 0 || targetIndex >= currentRoomImages.value.length) return;

  const targetImage = currentRoomImages.value[targetIndex];

  // ==========================================
  // 【不使用 display_order，也不改 SQL 的設計方法】
  // 直接「交換這兩筆 RoomImage 資料的內容 (路徑與說明)」！
  // 後端回傳時本身就有預設順序，我們將 A 圖片的內容換到 B，B 的換到 A，
  // 這樣不用任何新欄位或 localStorage，就能達到真正的永久排序！
  // ==========================================
  
  // 備份 A 的資料
  const tempPath = image.path;
  const tempDesc = image.imageDescription;
  const tempIsMain = image.isMain;

  // 將 B 的資料寫入 A
  image.path = targetImage.path;
  image.imageDescription = targetImage.imageDescription;
  image.isMain = targetImage.isMain;

  // 將備份的 A 資料寫入 B
  targetImage.path = tempPath;
  targetImage.imageDescription = tempDesc;
  targetImage.isMain = tempIsMain;

  try {
    // 同時更新兩筆圖片資料的內容，完成內容互換
    await Promise.all([
      roomImageApi.updateImage(image.imageId, image),
      roomImageApi.updateImage(targetImage.imageId, targetImage)
    ]);
    showMessage("順序更新成功", "success");
    await loadRoomImages();
  } catch (error) {
    console.error("更新順序失敗:", error);
    showMessage("更新順序失敗", "error");
  }
}

function handleImageError(event) {
  event.target.style.display = "none";
}

</script>

<template>
  <main class="image-page">
    <header class="page-header" style="display: flex; justify-content: space-between; align-items: center;">
      <div>
        <h1>房型圖片管理</h1>
        <p>以房型為核心的圖片管理。您可以上傳素材至總庫，或管理各房型的專屬圖片與排序。</p>
      </div>
      <div style="display: flex; gap: 10px;">
        <button class="btn secondary" @click="showLibraryModal = true">🖼️ 查看總媒體庫</button>
        <button class="btn primary add-btn" @click="openUploadModal">📤 上傳圖片至總庫</button>
      </div>
    </header>

    <!-- 固定於右上角的提示訊息 -->
    <div v-if="message" class="toast-message message" :class="messageType">
      {{ message }}
    </div>

    <!-- 房型列表 -->
    <section class="admin-card">
      <div class="tabs" style="display: flex; gap: 10px; margin-bottom: 20px; border-bottom: 1px solid #e4e7ec; padding-bottom: 10px;">
        <button type="button" class="tab-btn active">房型列表</button>
        <button type="button" class="tab-btn" @click="showLibraryModal = true">查看總媒體庫 ({{ uniqueImages.length }})</button>
      </div>

      <div v-if="roomTypes.length === 0" class="empty">目前沒有任何房型</div>
      <div v-else class="image-grid" style="grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));">
        <article v-for="room in roomTypes" :key="room.roomTypeId" class="image-card room-card">
          <div class="image-wrapper">
            <img :src="getRoomCover(room.roomTypeId)" alt="房型封面" @error="handleImageError" />
            <div v-if="!getRoomCover(room.roomTypeId)" class="no-image">尚無封面</div>
          </div>
          <div class="image-info">
            <h3>{{ getRoomNameFromImage(room.roomTypeId) }}</h3>
            <p style="color: #667085; font-size: 14px; margin-bottom: 15px;">已綁定 {{ getRoomImageCount(room.roomTypeId) }} 張圖片</p>
            <div class="actions">
              <button class="btn primary add-btn" style="width: 100%; display: flex; justify-content: center; gap: 8px; align-items: center;" @click="manageRoomImages(room)">
                🏠 管理房型圖片
              </button>
            </div>
          </div>
        </article>
      </div>
    </section>

    <!-- 房型專屬圖片管理 Modal -->
    <div v-if="selectedRoom" class="modal-overlay" @click.self="selectedRoom = null">
      <div class="modal-content" style="max-width: 800px;">
        <div class="modal-header">
          <h2>管理圖片：{{ getRoomNameFromImage(selectedRoom.roomTypeId) }}</h2>
          <button class="close-btn" @click="selectedRoom = null">×</button>
        </div>

        <div class="modal-body">
          <div style="margin-bottom: 20px; display: flex; justify-content: space-between; align-items: center; background: #f9fafb; padding: 15px; border-radius: 8px;">
            <p style="margin: 0; color: #475467;">您可以在此為【{{ getRoomNameFromImage(selectedRoom.roomTypeId) }}】新增圖片，或使用箭頭調整輪播順序。</p>
            <button class="btn primary add-btn" @click="openAddImageToRoomModal">+ 新增圖片到此房型</button>
          </div>

          <div v-if="currentRoomImages.length === 0" class="empty" style="background: white; border: 1px dashed #cfd4dc;">此房型目前沒有任何圖片。</div>
          <div v-else class="image-grid" style="grid-template-columns: repeat(2, 1fr);">
            <article v-for="(image, index) in currentRoomImages" :key="image.imageId" class="image-card">
              <div class="image-wrapper" style="height: 160px;">
                <img :src="image.path ? `http://localhost:8081${image.path}` : ''" @error="handleImageError" />
              </div>
              <div class="image-info">
                <p style="min-height: 20px;">{{ image.imageDescription || "沒有說明" }}</p>
                <div class="sort-actions" style="display: flex; gap: 10px; margin-bottom: 12px; justify-content: center;">
                  <button class="btn secondary small-btn" style="flex:1;" :disabled="index === 0" @click="moveImage(image, -1)">⬆️ 上移</button>
                  <button class="btn secondary small-btn" style="flex:1;" :disabled="index === currentRoomImages.length - 1" @click="moveImage(image, 1)">⬇️ 下移</button>
                </div>
                <div class="actions">
                  <button class="btn delete" style="width: 100%;" @click="deleteRoomImage(image.imageId)">從此房型移除</button>
                </div>
              </div>
            </article>
          </div>
        </div>
      </div>
    </div>

    <!-- 上傳圖片 Modal (適用於: 上傳素材) -->
    <div v-if="showUploadModal" class="modal-overlay" style="z-index: 1050;" @click.self="showUploadModal = false">
      <div class="modal-content form-modal" style="max-width: 500px;">
        <div class="modal-header">
          <h2>上傳素材至總媒體庫</h2>
          <button class="close-btn" @click="showUploadModal = false">×</button>
        </div>

        <form @submit.prevent="saveImage" class="modal-body">
          <div class="form-group full-width">
            <label style="font-weight: bold; margin-bottom: 10px; display: block;">請選擇圖片來源</label>
            <div class="radio-group" style="display: flex; flex-direction: column; gap: 12px; margin-bottom: 20px;">
              <label class="radio-label">
                <input type="radio" v-model="form.imageSource" value="library" /> 從總媒體庫選取 (推薦)
              </label>
              <label class="radio-label">
                <input type="radio" v-model="form.imageSource" value="upload" /> 從電腦上傳新檔案
              </label>
              <label class="radio-label">
                <input type="radio" v-model="form.imageSource" value="url" /> 使用外部網址
              </label>
            </div>

            <!-- 媒體庫選取區 -->
            <div v-if="form.imageSource === 'library'" style="margin-bottom: 15px;">
              <div style="display: flex; gap: 10px; align-items: center;">
                <input v-model.trim="form.imageUrl" type="text" placeholder="選取的圖片路徑" readonly style="flex: 1; background: #f9fafb;" />
                <button type="button" class="btn secondary" @click="showLibraryModal = true">開啟媒體庫挑選</button>
              </div>
            </div>

            <!-- 外部網址輸入區 -->
            <div v-if="form.imageSource === 'url'" style="margin-bottom: 15px;">
              <input id="imageUrl" v-model.trim="form.imageUrl" type="url" placeholder="例如：https://example.com/room.jpg" :required="form.imageSource === 'url'" style="width: 100%" />
            </div>
            
            <!-- 檔案上傳區 -->
            <div v-if="form.imageSource === 'upload'" style="margin-bottom: 15px;">
              <input id="imageFile" type="file" accept="image/*" @change="handleFileChange" :required="form.imageSource === 'upload'" style="width: 100%" />
            </div>
          </div>

          <div class="form-group full-width" style="margin-bottom: 15px;">
            <label for="imageDescription" style="display: block; margin-bottom: 5px;">圖片說明 (選填)</label>
            <input id="imageDescription" v-model.trim="form.imageDescription" type="text" placeholder="例如：海景窗戶" style="width: 100%" />
          </div>

          <div v-if="form.imageUrl" class="preview-area" style="margin-top: 15px; border-radius: 8px; overflow: hidden; height: 180px;">
            <img :src="form.imageUrl.startsWith('/') ? `http://localhost:8081${form.imageUrl}` : form.imageUrl" alt="預覽" @error="handleImageError" style="width: 100%; height: 100%; object-fit: cover;" />
          </div>

          <div class="form-actions" style="margin-top: 25px; justify-content: flex-end; border-top: 1px solid #eee; padding-top: 15px; display: flex; gap: 10px;">
            <button type="button" class="btn secondary" @click="showUploadModal = false">取消</button>
            <button type="submit" class="btn primary add-btn">
              確定新增
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 總媒體庫 Modal (可獨立開啟或在表單中開啟) -->
    <div v-if="showLibraryModal" class="modal-overlay" style="z-index: 1100;" @click.self="showLibraryModal = false">
      <div class="modal-content" style="max-width: 900px;">
        <div class="modal-header">
          <h2>總媒體庫 ({{ uniqueImages.length }} 張)</h2>
          <button class="close-btn" @click="showLibraryModal = false">×</button>
        </div>
        
        <div v-if="uniqueImages.length === 0" class="empty">目前總媒體庫沒有任何圖片。您可以點擊「上傳圖片至總庫」來新增。</div>
        <div v-else class="modal-grid" style="grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));">
          <div v-for="(img, idx) in uniqueImages" :key="'m-lib-'+idx" class="modal-img-card" style="height: 180px; display: flex; flex-direction: column;">
            <div style="flex: 1; overflow: hidden; position: relative; cursor: pointer;" @click="selectFromLibrary(img.path)">
              <img :src="img.path ? `http://localhost:8081${img.path}` : ''" @error="handleImageError" />
              <div class="hover-overlay">
                <span>點擊選取</span>
              </div>
            </div>
            <div style="padding: 10px; background: white; border-top: 1px solid #eee; display: flex; justify-content: space-between; align-items: center;">
              <span style="font-size: 14px; color: #475467; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; max-width: 120px;" :title="img.imageDescription">{{ img.imageDescription || "無說明" }}</span>
              <button class="btn delete small-btn" style="padding: 4px 8px;" @click.stop="deleteRoomImage(img.imageId)">刪除</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  
  </main>
</template>

<style scoped>
.image-page { padding: 28px; color: #243447; }
.page-header, .admin-card { margin-bottom: 24px; padding: 24px; background: white; border: 1px solid #e4e7ec; border-radius: 12px; box-shadow: 0 3px 12px rgb(0 0 0 / 6%); }
.page-header h1 { margin: 0 0 8px; }
.page-header p { margin: 0; color: #667085; }

.toast-message { position: fixed; top: 20px; right: 20px; z-index: 2000; box-shadow: 0 4px 12px rgba(0,0,0,0.15); font-weight: bold; }
.message { padding: 12px 20px; border-radius: 8px; }
.success { color: #176b3a; background: #e9f8ef; border-left: 4px solid #176b3a; }
.error { color: #b42318; background: #feeceb; border-left: 4px solid #b42318; }

.form-group { display: flex; flex-direction: column; gap: 7px; }
input, select { padding: 11px 12px; font: inherit; border: 1px solid #cfd4dc; border-radius: 7px; }
.btn { padding: 9px 15px; color: white; border: none; border-radius: 7px; cursor: pointer; font-weight: 500;}
.primary { background: #315b7d; }
.secondary { color: #344054; background: #e4e7ec; }
.secondary:hover { background: #d0d5dd; }
.add-btn { background-color: #A67C52; padding: 10px 20px; font-size: 15px; border-radius: 8px;}
.add-btn:hover { background-color: #8c6742; }
.delete { background: #c84040; }
.delete:hover { background: #b03535; }
.small-btn { padding: 6px 10px; font-size: 13px; }

.image-grid { display: grid; gap: 20px; }
.image-card { overflow: hidden; border: 1px solid #e4e7ec; border-radius: 10px; background: white; }
.room-card:hover { border-color: #A67C52; box-shadow: 0 4px 12px rgba(166,124,82,0.15); transition: 0.2s; }
.image-wrapper { position: relative; height: 190px; background: #f2f4f7; display: flex; align-items: center; justify-content: center; }
.image-wrapper img { width: 100%; height: 100%; object-fit: cover; }
.no-image { color: #98a2b3; font-weight: bold; }
.image-info { padding: 16px; }
.image-info h3 { margin: 0 0 8px; font-size: 18px;}
.empty { padding: 40px; text-align: center; color: #667085; font-size: 16px; background: #f9fafb; border-radius: 8px;}

.modal-overlay { position: fixed; top: 0; left: 0; width: 100vw; height: 100vh; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
.modal-content { background: white; padding: 24px; border-radius: 12px; width: 80%; max-width: 800px; max-height: 85vh; overflow-y: auto; box-shadow: 0 10px 25px rgba(0,0,0,0.2); }
.modal-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 1px solid #eee; padding-bottom: 15px;}
.modal-header h2 { margin: 0; font-size: 20px; color: #243447;}
.close-btn { background: none; border: none; font-size: 28px; cursor: pointer; color: #999; line-height: 1;}
.close-btn:hover { color: #333; }

.modal-grid { display: grid; gap: 15px; margin: 20px 0; }
.modal-img-card { position: relative; height: 140px; border-radius: 8px; overflow: hidden; border: 2px solid transparent; transition: 0.2s; background: #f2f4f7; }
.modal-img-card:hover { border-color: #315b7d; }
.modal-img-card img { width: 100%; height: 100%; object-fit: cover; }
.hover-overlay { position: absolute; inset: 0; background: rgba(0,0,0,0.4); display: flex; justify-content: center; align-items: center; opacity: 0; transition: 0.2s; color: white; font-weight: bold; }
.modal-img-card:hover .hover-overlay { opacity: 1; }

button:disabled { opacity: 0.5; cursor: not-allowed; }
</style>
