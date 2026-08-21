<script setup>
import { computed, onMounted, reactive, ref } from "vue";

// =====================================================
// API
// 跟商品、餐廳一樣使用相對路徑
// =====================================================

const API_URL = "/api/admin/members";

// =====================================================
// 會員資料
// =====================================================

const members = ref([]);

const keyword = ref("");

const loading = ref(false);

const saving = ref(false);

const message = ref("");

const messageType = ref("");

// =====================================================
// 分頁
// =====================================================

const currentPage = ref(1);

const pageSize = 10;

// =====================================================
// Modal
// =====================================================

const modalOpen = ref(false);

const editingMemberId = ref(null);

// =====================================================
// 表單
// =====================================================

const form = reactive({
  username: "",
  password: "",
  status: "ACTIVE",
});

// =====================================================
// JWT Header
// 跟商品、餐廳相同
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
// 訊息
// =====================================================

function showMessage(text, type) {
  message.value = text;

  messageType.value = type;

  setTimeout(() => {
    message.value = "";
  }, 2500);
}

// =====================================================
// 會員狀態
// =====================================================

function getStatusLabel(status) {
  const value = (status || "UNKNOWN").toUpperCase();

  if (value === "ACTIVE") {
    return "啟用";
  }

  if (value === "INACTIVE") {
    return "停用";
  }

  return value;
}

function isActiveStatus(status) {
  const normalized = (status || "").toUpperCase();

  return ["ACTIVE", "1", "ENABLE", "ENABLED"].includes(normalized);
}

// =====================================================
// 搜尋
// =====================================================

const filteredMembers = computed(() => {
  const search = keyword.value.trim().toLowerCase();

  if (!search) {
    return members.value;
  }

  return members.value.filter((member) =>
    (member.username || "").toLowerCase().includes(search),
  );
});

// =====================================================
// 總頁數
// =====================================================

const totalPages = computed(() => {
  return Math.max(
    1,

    Math.ceil(filteredMembers.value.length / pageSize),
  );
});

// =====================================================
// 當前頁
// =====================================================

const pagedMembers = computed(() => {
  if (currentPage.value > totalPages.value) {
    currentPage.value = totalPages.value;
  }

  const start = (currentPage.value - 1) * pageSize;

  return filteredMembers.value.slice(start, start + pageSize);
});

// =====================================================
// 搜尋後回第一頁
// =====================================================

function resetPage() {
  currentPage.value = 1;
}

// =====================================================
// 讀取會員
//
// GET /admin/members
// =====================================================

async function loadMembers() {
  loading.value = true;

  try {
    const response = await fetch(API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    console.log("會員 API status：", response.status);

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有會員管理權限", "error");

      return;
    }

    if (!response.ok) {
      showMessage("取得會員資料失敗", "error");

      return;
    }

    const contentType = response.headers.get("content-type") || "";

    console.log("會員 API Content-Type：", contentType);

    if (!contentType.includes("application/json")) {
      const text = await response.text();

      console.error("會員 API 回傳的不是 JSON：", text);

      showMessage("會員 API 回傳的不是 JSON，請檢查 proxy 或後端路徑", "error");

      return;
    }

    members.value = await response.json();

    console.log("會員資料：", members.value);

    currentPage.value = 1;
  } catch (error) {
    console.error("會員讀取錯誤：", error);

    showMessage("讀取會員資料失敗", "error");
  } finally {
    loading.value = false;
  }
}

// =====================================================
// 新增 Modal
// =====================================================

function openCreateModal() {
  editingMemberId.value = null;

  form.username = "";

  form.password = "";

  form.status = "ACTIVE";

  modalOpen.value = true;
}

// =====================================================
// 修改 Modal
// =====================================================

function openEditModal(member) {
  editingMemberId.value = member.id;

  form.username = member.username || "";

  form.password = "";

  form.status = member.status || "ACTIVE";

  modalOpen.value = true;
}

// =====================================================
// 關閉 Modal
// =====================================================

function closeModal() {
  modalOpen.value = false;

  editingMemberId.value = null;

  form.username = "";

  form.password = "";

  form.status = "ACTIVE";
}

// =====================================================
// 新增 / 修改會員
//
// POST /admin/members
// PUT  /admin/members/{id}
// =====================================================

async function saveMember() {
  const username = form.username.trim();

  const password = form.password.trim();

  if (!username) {
    showMessage("帳號不能為空", "error");

    return;
  }

  saving.value = true;

  const isEditing = editingMemberId.value !== null;

  const payload = {
    username: username,

    status: form.status,
  };

  if (password) {
    payload.password = password;
  }

  try {
    const url = isEditing ? `${API_URL}/${editingMemberId.value}` : API_URL;

    const method = isEditing ? "PUT" : "POST";

    const response = await fetch(url, {
      method: method,

      headers: getAuthHeaders(),

      body: JSON.stringify(payload),
    });

    console.log("會員儲存 status：", response.status);

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有會員管理權限", "error");

      return;
    }

    if (response.status === 409) {
      showMessage("帳號已存在", "error");

      return;
    }

    if (!response.ok) {
      showMessage("儲存會員失敗", "error");

      return;
    }

    closeModal();

    showMessage(isEditing ? "會員修改成功" : "會員新增成功", "success");

    await loadMembers();
  } catch (error) {
    console.error("會員儲存錯誤：", error);

    showMessage("儲存會員失敗", "error");
  } finally {
    saving.value = false;
  }
}

// =====================================================
// 啟用 / 停用
//
// PATCH /admin/members/{id}/status
// =====================================================

async function toggleStatus(member) {
  const nextStatus = isActiveStatus(member.status) ? "INACTIVE" : "ACTIVE";

  try {
    const response = await fetch(`${API_URL}/${member.id}/status`, {
      method: "PATCH",

      headers: getAuthHeaders(),

      body: JSON.stringify({
        status: nextStatus,
      }),
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有會員管理權限", "error");

      return;
    }

    if (!response.ok) {
      showMessage("更新會員狀態失敗", "error");

      return;
    }

    showMessage("會員狀態已更新", "success");

    await loadMembers();
  } catch (error) {
    console.error("會員狀態更新錯誤：", error);

    showMessage("更新會員狀態失敗", "error");
  }
}

// =====================================================
// 刪除會員
//
// DELETE /admin/members/{id}
// =====================================================

async function deleteMember(memberId) {
  if (!window.confirm("確定要刪除此會員嗎？")) {
    return;
  }

  try {
    const response = await fetch(`${API_URL}/${memberId}`, {
      method: "DELETE",

      headers: getAuthHeaders(),
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有刪除權限", "error");

      return;
    }

    if (!response.ok) {
      showMessage("刪除會員失敗", "error");

      return;
    }

    showMessage("會員已刪除", "success");

    await loadMembers();
  } catch (error) {
    console.error("刪除會員錯誤：", error);

    showMessage("刪除會員失敗", "error");
  }
}

// =====================================================
// 分頁
// =====================================================

function changePage(step) {
  currentPage.value = Math.min(
    Math.max(1, currentPage.value + step),
    totalPages.value,
  );
}

// =====================================================
// 初始化
// =====================================================

onMounted(() => {
  console.log("會員頁 JWT：", localStorage.getItem("token"));

  loadMembers();
});
</script>

<template>
  <div class="member-page">
    <!-- 頁面標題 -->
    <div class="admin-page-header">
      <div>
        <h1>會員管理</h1>

        <p>管理會員帳號、啟用狀態與登入資料</p>
      </div>
    </div>

    <!-- =========================
         會員列表
         ========================= -->

    <section class="admin-card">
      <div class="member-list-header">
        <div>
          <h2>會員列表</h2>

          <p>可搜尋、新增、修改、啟用、停用與刪除會員</p>
        </div>

        <div class="member-toolbar">
          <input
            v-model="keyword"
            type="text"
            class="admin-input member-search"
            placeholder="搜尋會員帳號..."
            @input="resetPage"
          />

          <button
            type="button"
            class="admin-btn admin-btn-primary"
            @click="openCreateModal"
          >
            ＋ 新增會員
          </button>

          <button
            type="button"
            class="admin-btn admin-btn-secondary"
            @click="loadMembers"
          >
            重新整理
          </button>
        </div>
      </div>

      <!-- 訊息 -->
      <div v-if="message" class="admin-message" :class="messageType">
        {{ message }}
      </div>

      <!-- 統計 -->
      <div class="member-summary">
        共

        <strong>
          {{ filteredMembers.length }}
        </strong>

        位會員
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-message">會員資料讀取中...</div>

      <!-- Table -->
      <div v-else class="admin-table-wrapper">
        <table class="admin-table">
          <thead>
            <tr>
              <th>ID</th>

              <th>帳號</th>

              <th>狀態</th>

              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="pagedMembers.length === 0">
              <td colspan="4" class="empty-row">目前沒有符合條件的會員資料</td>
            </tr>

            <tr v-for="member in pagedMembers" :key="member.id">
              <td>
                {{ member.id }}
              </td>

              <td class="member-name">
                {{ member.username || "N/A" }}
              </td>

              <td>
                <span
                  class="status-badge"
                  :class="
                    isActiveStatus(member.status)
                      ? 'status-active'
                      : 'status-inactive'
                  "
                >
                  {{ getStatusLabel(member.status) }}
                </span>
              </td>

              <td>
                <div class="member-actions">
                  <button
                    type="button"
                    class="admin-btn admin-btn-edit"
                    @click="openEditModal(member)"
                  >
                    修改
                  </button>

                  <button
                    type="button"
                    class="admin-btn"
                    :class="
                      isActiveStatus(member.status)
                        ? 'status-disable-btn'
                        : 'status-enable-btn'
                    "
                    @click="toggleStatus(member)"
                  >
                    {{ isActiveStatus(member.status) ? "停用" : "啟用" }}
                  </button>

                  <button
                    type="button"
                    class="admin-btn admin-btn-delete"
                    @click="deleteMember(member.id)"
                  >
                    刪除
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分頁 -->
      <div class="member-pagination">
        <button
          type="button"
          class="admin-btn admin-btn-secondary"
          :disabled="currentPage <= 1"
          @click="changePage(-1)"
        >
          上一頁
        </button>

        <span>
          {{ currentPage }}

          /

          {{ totalPages }}
        </span>

        <button
          type="button"
          class="admin-btn admin-btn-secondary"
          :disabled="currentPage >= totalPages"
          @click="changePage(1)"
        >
          下一頁
        </button>
      </div>
    </section>

    <!-- =========================
         Modal
         ========================= -->

    <div v-if="modalOpen" class="member-modal" @click.self="closeModal">
      <div class="member-modal-card">
        <div class="member-modal-header">
          <h2>
            {{ editingMemberId === null ? "新增會員" : "修改會員" }}
          </h2>

          <button type="button" class="modal-close" @click="closeModal">
            ×
          </button>
        </div>

        <form class="member-form" @submit.prevent="saveMember">
          <div class="admin-form-grid">
            <div class="admin-form-group full-width">
              <label> 帳號 </label>

              <input
                v-model="form.username"
                type="text"
                placeholder="請輸入會員帳號"
                required
              />
            </div>

            <div class="admin-form-group full-width">
              <label> 密碼 </label>

              <input
                v-model="form.password"
                type="password"
                autocomplete="new-password"
                :placeholder="
                  editingMemberId === null
                    ? '若不填則預設 123456'
                    : '留空表示不修改密碼'
                "
              />
            </div>

            <div class="admin-form-group full-width">
              <label> 狀態 </label>

              <select v-model="form.status">
                <option value="ACTIVE">啟用</option>

                <option value="INACTIVE">停用</option>
              </select>
            </div>
          </div>

          <div class="admin-form-actions">
            <button
              type="button"
              class="admin-btn admin-btn-secondary"
              @click="closeModal"
            >
              取消
            </button>

            <button
              type="submit"
              class="admin-btn admin-btn-primary"
              :disabled="saving"
            >
              {{ saving ? "儲存中..." : "儲存" }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<style scoped>
.member-page {
  width: 100%;
}

/* =========================
   Header
   ========================= */

.member-list-header {
  display: flex;

  justify-content: space-between;

  align-items: center;

  gap: 20px;

  margin-bottom: 20px;

  flex-wrap: wrap;
}

.member-list-header h2 {
  margin: 0 0 6px;

  color: #6f5328;
}

.member-list-header p {
  margin: 0;

  color: #777;

  font-size: 14px;
}

/* =========================
   Toolbar
   ========================= */

.member-toolbar {
  display: flex;

  align-items: center;

  gap: 10px;

  flex-wrap: wrap;
}

.member-search {
  width: 250px;
}

/* =========================
   Summary
   ========================= */

.member-summary {
  margin-bottom: 18px;

  color: #6d6258;
}

.member-summary strong {
  color: #9b7435;
}

/* =========================
   Table
   ========================= */

.member-name {
  color: #5b4632;

  font-weight: bold;
}

.empty-row {
  padding: 40px !important;

  text-align: center !important;

  color: #888 !important;
}

/* =========================
   Status
   ========================= */

.status-badge {
  display: inline-block;

  min-width: 70px;

  padding: 6px 11px;

  border-radius: 999px;

  text-align: center;

  font-size: 12px;

  font-weight: bold;
}

.status-active {
  background-color: #e5f6eb;

  color: #257641;
}

.status-inactive {
  background-color: #fde9e7;

  color: #b3443c;
}

/* =========================
   Actions
   ========================= */

.member-actions {
  display: flex;

  gap: 7px;

  flex-wrap: wrap;
}

.status-disable-btn {
  background-color: #fff3d8;

  color: #95691f;
}

.status-disable-btn:hover {
  background-color: #efd59a;
}

.status-enable-btn {
  background-color: #e5f6eb;

  color: #257641;
}

.status-enable-btn:hover {
  background-color: #257641;

  color: white;
}

/* =========================
   Pagination
   ========================= */

.member-pagination {
  display: flex;

  justify-content: flex-end;

  align-items: center;

  gap: 12px;

  margin-top: 20px;
}

.member-pagination span {
  min-width: 70px;

  text-align: center;

  color: #6f6256;

  font-weight: bold;
}

.member-pagination button:disabled {
  opacity: 0.5;

  cursor: not-allowed;

  transform: none;
}

/* =========================
   Loading
   ========================= */

.loading-message {
  padding: 45px;

  text-align: center;

  color: #888;
}

/* =========================
   Modal
   ========================= */

.member-modal {
  position: fixed;

  inset: 0;

  z-index: 2000;

  display: flex;

  justify-content: center;

  align-items: center;

  padding: 20px;

  background-color: rgba(47, 42, 36, 0.55);
}

.member-modal-card {
  width: min(500px, 92vw);

  overflow: hidden;

  background-color: white;

  border-radius: 14px;

  box-shadow: 0 16px 50px rgba(0, 0, 0, 0.22);
}

.member-modal-header {
  display: flex;

  justify-content: space-between;

  align-items: center;

  padding: 20px 24px;

  background-color: #4a3b2a;

  color: white;
}

.member-modal-header h2 {
  margin: 0;

  font-size: 21px;
}

.modal-close {
  padding: 0 6px;

  border: none;

  background: transparent;

  color: white;

  font-size: 27px;

  cursor: pointer;
}

.member-form {
  padding: 24px;
}

/* =========================
   RWD
   ========================= */

@media (max-width: 700px) {
  .member-list-header {
    align-items: stretch;

    flex-direction: column;
  }

  .member-toolbar {
    align-items: stretch;

    flex-direction: column;
  }

  .member-search {
    width: 100%;
  }

  .member-toolbar .admin-btn {
    width: 100%;
  }

  .member-actions {
    flex-direction: column;
  }

  .member-pagination {
    justify-content: center;
  }
}
</style>
