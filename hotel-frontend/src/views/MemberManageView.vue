<template>
  <div class="member-manage-page">
    <!-- 頁面標題列 -->
    <div class="admin-page-header">
      <div>
        <h1>會員管理</h1>
        <p>管理會員帳號、個人資料與啟用狀態</p>
      </div>

      <div class="admin-header-actions">
        <button
          type="button"
          class="admin-btn admin-btn-secondary admin-json-button"
          :disabled="exporting || importing"
          @click="openExportModal"
        >
          <Download :size="16" aria-hidden="true" />
          {{ exporting ? "匯出中..." : "匯出 JSON" }}
        </button>

        <button
          type="button"
          class="admin-btn admin-btn-secondary admin-json-button"
          :disabled="exporting || importing"
          @click="openImportModal"
        >
          <Upload :size="16" aria-hidden="true" />
          {{ importing ? "匯入中..." : "匯入 JSON" }}
        </button>

        <button
          type="button"
          class="admin-btn admin-btn-primary"
          @click="openCreateModal"
        >
          ＋ 新增會員
        </button>
      </div>
    </div>

    <!-- 會員管理卡片 -->
    <section class="admin-card">
      <!-- 搜尋 / 篩選 -->
      <div class="admin-search-bar">
        <input
          v-model="keyword"
          type="text"
          class="admin-input search-input"
          placeholder="搜尋帳號、姓名、信箱、電話..."
          @keyup.enter="resetPage"
        />

        <select v-model="selectedStatus" class="admin-input filter-select" @change="resetPage">
          <option value="">全部狀態</option>
          <option value="1">啟用</option>
          <option value="0">停用</option>
        </select>

        <button
          type="button"
          class="admin-btn admin-btn-primary"
          @click="resetPage"
        >
          搜尋
        </button>

        <button
          type="button"
          class="admin-btn admin-btn-secondary"
          @click="resetSearch"
        >
          重設
        </button>
      </div>

      <!-- 資料控制列 -->
      <div class="table-control-bar">
        <div class="filter-summary">
          目前共有
          <strong>{{ sortedMembers.length }}</strong>
          位會員
          <span v-if="sortedMembers.length !== members.length" class="total-hint">
            （全體共 {{ members.length }} 位）
          </span>
          <span v-if="selectedMemberIds.length > 0" class="selected-hint">
            已選取 <strong>{{ selectedMemberIds.length }}</strong> 位會員
            <button type="button" class="link-btn" @click="clearSelection">清除選取</button>
          </span>
        </div>

        <div class="page-size-area">
          <label> 每頁顯示 </label>
          <select v-model.number="pageSize" class="page-size-select">
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="20">20</option>
            <option :value="50">50</option>
          </select>
          <span> 筆 </span>
        </div>
      </div>

      <!-- 訊息 -->
      <div v-if="message" class="admin-message" :class="messageType">
        {{ message }}
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-message">會員資料讀取中...</div>

      <!-- 會員表格 -->
      <div v-else class="admin-table-wrapper">
        <table class="admin-table admin-table-fixed member-table">
          <colgroup>
            <col style="width: 48px;" />
            <col style="width: 75px;" />
            <col style="width: 200px;" />
            <col style="width: 230px;" />
            <col style="width: 80px;" />
            <col style="width: 90px;" />
            <col style="width: 175px;" />
          </colgroup>
          <thead>
            <tr>
              <th style="text-align: center;">
                <input
                  type="checkbox"
                  :checked="isAllSelected(paginatedMembers, (m) => m.memberId ?? m.id)"
                  @change="(ev) => toggleSelectAll(ev, paginatedMembers, (m) => m.memberId ?? m.id)"
                  title="全選 / 取消全選本頁"
                />
              </th>

              <th class="sortable" @click="changeSort('memberId', resetPage)">
                ID
                <span class="sort-icon">{{ getSortIcon("memberId") }}</span>
              </th>

              <th class="sortable" @click="changeSort('username', resetPage)">
                帳號 / 姓名
                <span class="sort-icon">{{ getSortIcon("username") }}</span>
              </th>

              <th>聯絡方式</th>

              <th class="sortable" style="text-align: center;" @click="changeSort('gender', resetPage)">
                性別
                <span class="sort-icon">{{ getSortIcon("gender") }}</span>
              </th>

              <th class="sortable" style="text-align: center;" @click="changeSort('status', resetPage)">
                狀態
                <span class="sort-icon">{{ getSortIcon("status") }}</span>
              </th>

              <th style="text-align: center;">操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="paginatedMembers.length === 0">
              <td colspan="7" class="empty-message">查無符合條件的會員</td>
            </tr>

            <tr
              v-for="member in paginatedMembers"
              :key="member.memberId ?? member.id"
              :class="{ 'row-selected': selectedMemberIds.includes(member.memberId ?? member.id) }"
            >
              <td style="text-align: center;">
                <input
                  type="checkbox"
                  :value="member.memberId ?? member.id"
                  v-model="selectedMemberIds"
                />
              </td>

              <td>{{ member.memberId ?? member.id }}</td>

              <td>
                <div class="name-cell">
                  <span class="item-name" :title="member.name">{{ member.name || "未填姓名" }}</span>
                  <span class="item-username" :title="'@' + member.username">(@{{ member.username }})</span>
                </div>
              </td>

              <td>
                <div class="contact-info">
                  <div v-if="member.phone" class="contact-item" :title="member.phone">
                    <Phone :size="14" class="lucide-icon inline-icon" />
                    <span>{{ member.phone }}</span>
                  </div>
                  <div v-if="member.email" class="contact-item" :title="member.email">
                    <Mail :size="14" class="lucide-icon inline-icon" />
                    <span>{{ member.email }}</span>
                  </div>
                  <span v-if="!member.phone && !member.email" class="text-muted">未填寫</span>
                </div>
              </td>

              <td style="text-align: center;">
                <span class="gender-text">{{ member.gender || "—" }}</span>
              </td>

              <td style="text-align: center;">
                <span
                  class="status-badge"
                  :class="isActiveStatus(member.status) ? 'status-active' : 'status-inactive'"
                >
                  {{ getStatusLabel(member.status) }}
                </span>
              </td>

              <td style="text-align: center;">
                <div class="action-buttons">
                  <button
                    type="button"
                    class="admin-btn admin-btn-status"
                    @click="toggleStatus(member)"
                  >
                    {{ isActiveStatus(member.status) ? "停用" : "啟用" }}
                  </button>

                  <button
                    type="button"
                    class="admin-btn admin-btn-edit"
                    @click="openEditModal(member)"
                  >
                    修改
                  </button>

                  <button
                    type="button"
                    class="admin-btn admin-btn-delete"
                    @click="deleteMember(member.memberId ?? member.id)"
                  >
                    刪除
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分頁元件 -->
      <AdminPagination
        :current-page="currentPage"
        :total-pages="totalPages"
        :visible-pages="visiblePages"
        :loading="loading"
        :total-count="sortedMembers.length"
        @page-change="goToPage"
      />
    </section>

    <!-- 新增 / 修改 Modal -->
    <div v-if="modalOpen" class="admin-modal-backdrop" @click.self="closeModal">
      <div class="admin-modal-card">
        <div class="admin-modal-header">
          <div>
            <h2>{{ editingMemberId === null ? "新增會員" : "修改會員資料" }}</h2>
            <p>{{ editingMemberId === null ? "建立新的會員帳號與個人檔案" : `編輯會員 #${editingMemberId} 資料` }}</p>
          </div>
          <button type="button" class="modal-close" @click="closeModal">×</button>
        </div>

        <form class="admin-modal-body" @submit.prevent="saveMember">
          <!-- 帳號設定 -->
          <div class="form-section-title">
            <Lock :size="18" class="lucide-icon section-icon" />
            <span>帳號設定</span>
          </div>
          <div class="admin-form-grid">
            <div class="admin-form-group">
              <label> 帳號 <span class="required">*</span> </label>
              <input v-model="form.username" type="text" placeholder="請輸入登入帳號" required />
            </div>

            <div class="admin-form-group">
              <label> 密碼 </label>
              <input
                v-model="form.password"
                type="password"
                autocomplete="new-password"
                :placeholder="editingMemberId === null ? '若不填則預設 123456' : '留空表示不修改密碼'"
              />
            </div>

            <div class="admin-form-group full-width">
              <label> 帳號狀態 </label>
              <select v-model="form.status">
                <option value="1">啟用</option>
                <option value="0">停用</option>
              </select>
            </div>
          </div>

          <!-- 個人基本資料 -->
          <div class="form-section-title">
            <User :size="18" class="lucide-icon section-icon" />
            <span>個人基本資料</span>
          </div>
          <div class="admin-form-grid">
            <div class="admin-form-group">
              <label> 姓名 </label>
              <input v-model="form.name" type="text" placeholder="請輸入真實姓名" />
            </div>

            <div class="admin-form-group">
              <label> 性別 </label>
              <select v-model="form.gender">
                <option value="男">男</option>
                <option value="女">女</option>
                <option value="其他">其他</option>
              </select>
            </div>

            <div class="admin-form-group">
              <label> 電子信箱 </label>
              <input v-model="form.email" type="email" placeholder="例：member@example.com" />
            </div>

            <div class="admin-form-group">
              <label> 聯絡電話 </label>
              <input v-model="form.phone" type="text" placeholder="例：0912345678" />
            </div>

            <div class="admin-form-group">
              <label> 出生日期 </label>
              <input v-model="form.birthday" type="date" />
            </div>

            <div class="admin-form-group">
              <label> 郵遞區號 </label>
              <input v-model="form.zipcode" type="text" placeholder="例：320" />
            </div>

            <div class="admin-form-group">
              <label> 縣市 </label>
              <input v-model="form.city" type="text" placeholder="例：桃園市" />
            </div>

            <div class="admin-form-group">
              <label> 鄉鎮市區 </label>
              <input v-model="form.district" type="text" placeholder="例：中壢區" />
            </div>

            <div class="admin-form-group full-width">
              <label> 詳細地址 </label>
              <input v-model="form.address" type="text" placeholder="請輸入詳細街道地址" />
            </div>

            <div class="admin-form-group full-width">
              <label> 會員頭像 </label>
              <div class="admin-avatar-upload-box">
                <div class="admin-avatar-preview-circle">
                  <img
                    v-if="form.avatarUrl"
                    :src="form.avatarUrl"
                    alt="Avatar Preview"
                    class="admin-avatar-img"
                    @error="form.avatarUrl = ''"
                  />
                  <span v-else>{{ (form.name || form.username || "客").charAt(0) }}</span>
                </div>
                <div v-if="editingMemberId !== null" class="admin-avatar-input-group">
                  <input
                    type="file"
                    accept="image/*"
                    class="admin-avatar-file-input"
                    :disabled="uploadingAvatar"
                    @change="handleAdminAvatarUpload"
                  />
                  <span class="input-hint-text">{{ uploadingAvatar ? "上傳中..." : "選擇圖片上傳更新頭像" }}</span>
                </div>
                <span v-else class="input-hint-text">（新增會員完成後即可進行頭像上傳）</span>
              </div>
            </div>
          </div>

          <div class="admin-modal-footer">
            <button
              v-if="editingMemberId === null"
              type="button"
              class="admin-btn quick-fill-btn"
              @click="fillDemoData"
            >
              一鍵帶入
            </button>
            <button type="button" class="admin-btn admin-btn-secondary" @click="closeModal">取消</button>
            <button type="submit" class="admin-btn admin-btn-primary" :disabled="saving">
              {{ saving ? "儲存中..." : "儲存" }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- JSON 匯出 Modal -->
    <AdminJsonExportModal
      :open="exportModalOpen"
      title="匯出會員資料"
      subtitle="匯出 JSON 格式的會員帳號與檔案資料"
      notice-text="💡 系統將調用後端 <code>/api/members/export</code> API，匯出包含 <code>password</code> 且排除驗證碼的格式化 <code>members.json</code> 檔案並自動下載。"
      :exporting="exporting"
      :filtered-count="sortedMembers.length"
      :total-count="members.length"
      :selected-count="selectedMemberIds.length"
      :filtered-hint="`符合關鍵字 (${keyword || '無'}) 與狀態篩選`"
      :initial-scope="exportInitialScope"
      @close="closeExportModal"
      @confirm="handleExport"
    />

    <!-- JSON 匯入 Modal -->
    <AdminJsonImportModal
      :open="importModalOpen"
      title="匯入會員資料"
      subtitle="透過 JSON 檔案或文字批次匯入會員資料"
      file-input-id="import-member-file"
      placeholder='請在此貼上 JSON 格式的會員資料，例如：&#10;[&#10;  {&#10;    "username": "hotel_guest",&#10;    "password": "user123",&#10;    "name": "陳大名",&#10;    "email": "guest@example.com",&#10;    "phone": "0988123456",&#10;    "gender": "男",&#10;    "status": "1"&#10;  }&#10;]'
      :importing="importing"
      :import-result="importResult"
      @close="closeImportModal"
      @import="handleImport"
      @error-msg="(msg) => showMessage(msg, 'error')"
    >
      <template #rules>
        <li>支援多筆陣列 <code>[...]</code> 或單筆物件 <code>{...}</code>。</li>
        <li>支援 <code>password</code> 密碼匯入（支援明文或已雜湊密碼；未填則預設為 <code>123456</code>）。</li>
        <li>若帳號已存在，系統將自動<strong>更新</strong>該會員詳細資料。</li>
        <li>若帳號不存在，系統將<strong>新增</strong>會員。</li>
        <li><code>verificationCode</code> 欄位無須匯入。</li>
      </template>
    </AdminJsonImportModal>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from "vue";
import { Download, Upload, Phone, Mail, Lock, User } from "@lucide/vue";
import { getAuthHeaders } from "@/utils/auth";
import { useAdminPagination } from "@/composables/useAdminPagination";
import { useTableSort } from "@/composables/useTableSort";
import { useTableSelection } from "@/composables/useTableSelection";
import AdminPagination from "@/components/admin/AdminPagination.vue";
import AdminJsonExportModal from "@/components/admin/AdminJsonExportModal.vue";
import AdminJsonImportModal from "@/components/admin/AdminJsonImportModal.vue";
import { useToastStore } from "@/stores/toast";
import "@/assets/admin-manage.css";

const toastStore = useToastStore();
const API_URL = "/api/members";

const members = ref([]);
const keyword = ref("");
const selectedStatus = ref("");
const loading = ref(false);
const saving = ref(false);
const message = ref("");
const messageType = ref("");

// 排序與多選
const { sortKey, sortDirection, changeSort, getSortIcon, sortList } = useTableSort("memberId", "asc");
const { selectedIds: selectedMemberIds, isAllSelected, toggleSelectAll, clearSelection } = useTableSelection();

// 表單
const modalOpen = ref(false);
const editingMemberId = ref(null);
const uploadingAvatar = ref(false);
const form = reactive({
  username: "",
  password: "",
  status: "1",
  name: "",
  email: "",
  phone: "",
  gender: "男",
  birthday: "",
  zipcode: "",
  city: "",
  district: "",
  address: "",
  avatarUrl: "",
});

// 匯出 / 匯入
const exportModalOpen = ref(false);
const exporting = ref(false);
const exportInitialScope = ref("filtered");
const importModalOpen = ref(false);
const importing = ref(false);
const importResult = ref(null);

function showMessage(text, type, duration = 3000) {
  message.value = text;
  messageType.value = type;
  setTimeout(() => {
    message.value = "";
  }, duration);
}

function getStatusLabel(status) {
  const value = String(status ?? "UNKNOWN").toUpperCase();
  if (value === "1" || value === "ACTIVE" || value === "ENABLE" || value === "ENABLED") return "啟用";
  if (value === "0" || value === "INACTIVE" || value === "DISABLE" || value === "DISABLED") return "停用";
  return value;
}

function isActiveStatus(status) {
  const normalized = String(status ?? "").toUpperCase();
  return ["ACTIVE", "1", "ENABLE", "ENABLED"].includes(normalized);
}

// 篩選與排序
const filteredMembers = computed(() => {
  const search = keyword.value.trim().toLowerCase();
  const statusFilter = selectedStatus.value;

  return members.value.filter((m) => {
    const matchesKeyword =
      !search ||
      (m.username || "").toLowerCase().includes(search) ||
      (m.name || "").toLowerCase().includes(search) ||
      (m.email || "").toLowerCase().includes(search) ||
      (m.phone || "").includes(search);

    const matchesStatus = !statusFilter || String(m.status) === String(statusFilter);

    return matchesKeyword && matchesStatus;
  });
});

const sortedMembers = computed(() => {
  return sortList(filteredMembers.value, {
    memberId: (item) => Number(item.memberId ?? item.id ?? 0),
    username: (item) => item.username || "",
    gender: (item) => item.gender || "",
    status: (item) => getStatusLabel(item.status),
  });
});

// 分頁
const {
  currentPage,
  pageSize,
  totalPages,
  paginatedItems: paginatedMembers,
  visiblePages,
  goToPage,
  resetPage,
} = useAdminPagination(sortedMembers, 10);

watch(selectedStatus, () => {
  resetPage();
});

function resetSearch() {
  keyword.value = "";
  selectedStatus.value = "";
  sortKey.value = "memberId";
  sortDirection.value = "asc";
  resetPage();
}

// 會員 CRUD
async function loadMembers() {
  loading.value = true;
  try {
    const response = await fetch(API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有權限", "error");
      return;
    }
    if (!response.ok) {
      showMessage("取得會員資料失敗", "error");
      return;
    }

    members.value = await response.json();
    resetPage();
  } catch (error) {
    console.error("會員讀取錯誤：", error);
    showMessage("讀取會員資料失敗", "error");
  } finally {
    loading.value = false;
  }
}

function fillDemoData() {
  const randomNum = Math.floor(100 + Math.random() * 900);
  form.username = `demo_user${randomNum}`;
  form.password = "123456";
  form.status = "1";
  form.name = "王小明";
  form.gender = "男";
  form.email = `demo_user${randomNum}@example.com`;
  form.phone = "0912345678";
  form.birthday = "1995-08-15";
  form.zipcode = "320";
  form.city = "桃園市";
  form.district = "中壢區";
  form.address = "中大路300號";
  toastStore.showToast("已成功一鍵帶入假資料", "success");
}

function openCreateModal() {
  editingMemberId.value = null;
  form.username = "";
  form.password = "";
  form.status = "1";
  form.name = "";
  form.email = "";
  form.phone = "";
  form.gender = "男";
  form.birthday = "";
  form.zipcode = "";
  form.city = "";
  form.district = "";
  form.address = "";
  form.avatarUrl = "";
  modalOpen.value = true;
}

function openEditModal(member) {
  editingMemberId.value = member.memberId ?? member.id;
  form.username = member.username || "";
  form.password = "";
  form.status = String(member.status ?? "1");
  form.name = member.name || "";
  form.email = member.email || "";
  form.phone = member.phone || "";
  form.gender = member.gender || "男";
  form.birthday = member.birthday || "";
  form.zipcode = member.zipcode || "";
  form.city = member.city || "";
  form.district = member.district || "";
  form.address = member.address || "";
  form.avatarUrl = member.avatarUrl || "";
  modalOpen.value = true;
}

async function handleAdminAvatarUpload(event) {
  const file = event.target.files && event.target.files[0];
  if (!file || editingMemberId.value === null) return;

  if (!file.type.startsWith("image/")) {
    showMessage("請選擇有效的圖片檔案", "error");
    return;
  }

  uploadingAvatar.value = true;
  const formData = new FormData();
  formData.append("file", file);

  try {
    const token = localStorage.getItem("token");
    const headers = {};
    if (token) headers.Authorization = "Bearer " + token;

    const res = await fetch(`${API_URL}/${editingMemberId.value}/avatar`, {
      method: "POST",
      headers,
      body: formData,
    });

    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      showMessage(err.message || "頭像上傳失敗", "error");
      return;
    }

    const updated = await res.json();
    if (updated && updated.avatarUrl) {
      form.avatarUrl = updated.avatarUrl;
      showMessage("會員頭像上傳成功", "success");
      await loadMembers();
    }
  } catch (err) {
    console.error("管理員上傳頭像錯誤：", err);
    showMessage("頭像上傳失敗", "error");
  } finally {
    uploadingAvatar.value = false;
    event.target.value = "";
  }
}

function closeModal() {
  modalOpen.value = false;
  editingMemberId.value = null;
}

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
    name: form.name.trim(),
    email: form.email.trim(),
    phone: form.phone.trim(),
    gender: form.gender,
    birthday: form.birthday || null,
    zipcode: form.zipcode.trim(),
    city: form.city.trim(),
    district: form.district.trim(),
    address: form.address.trim(),
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

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有權限", "error");
      return;
    }
    if (response.status === 409) {
      showMessage("使用者帳號或信箱已存在", "error");
      return;
    }
    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      showMessage(data.message || "儲存會員失敗", "error");
      return;
    }

    closeModal();
    showMessage(isEditing ? "會員資料修改成功" : "會員新增成功", "success");
    await loadMembers();
  } catch (error) {
    console.error("會員儲存錯誤：", error);
    showMessage("儲存會員失敗", "error");
  } finally {
    saving.value = false;
  }
}

async function toggleStatus(member) {
  if (!member) return;
  const memberId = member.memberId ?? member.id;
  if (!memberId) {
    showMessage("無法取得會員 ID", "error");
    return;
  }

  const nextStatus = isActiveStatus(member.status) ? "0" : "1";
  const actionText = nextStatus === "1" ? "啟用" : "停用";

  if (!window.confirm(`確定要${actionText}會員「${member.name || member.username}」嗎？`)) {
    return;
  }

  try {
    const response = await fetch(`${API_URL}/${memberId}/status?status=${nextStatus}`, {
      method: "PATCH",
      headers: getAuthHeaders(),
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有權限", "error");
      return;
    }
    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      showMessage(data.message || "更新狀態失敗", "error");
      return;
    }

    showMessage(`會員已${actionText}`, "success");
    await loadMembers();
  } catch (error) {
    console.error("會員狀態更新錯誤：", error);
    showMessage("更新狀態失敗", "error");
  }
}

async function deleteMember(memberId) {
  if (!memberId) {
    showMessage("無法取得會員 ID", "error");
    return;
  }

  if (!window.confirm("確定要刪除此會員嗎？此動作將連動刪除帳號與個人檔案。")) {
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
    if (response.status === 409) {
      const data = await response.json().catch(() => ({}));
      showMessage(data.message || "無法刪除：該會員已有相關業務紀錄，建議將狀態改為停用", "error");
      return;
    }
    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      showMessage(data.message || "刪除會員失敗", "error");
      return;
    }

    showMessage("會員刪除成功", "success");
    await loadMembers();
  } catch (error) {
    console.error("刪除會員錯誤：", error);
    showMessage("刪除會員失敗", "error");
  }
}

// 匯出 / 匯入
function openExportModal() {
  if (selectedMemberIds.value.length > 0) {
    exportInitialScope.value = "selected";
  } else if (keyword.value || selectedStatus.value) {
    exportInitialScope.value = "filtered";
  } else {
    exportInitialScope.value = "all";
  }
  exportModalOpen.value = true;
}

function closeExportModal() {
  exportModalOpen.value = false;
}

async function handleExport({ scope, customParams }) {
  exporting.value = true;
  try {
    const params = new URLSearchParams();

    if (scope === "filtered") {
      if (keyword.value.trim()) params.append("keyword", keyword.value.trim());
      if (selectedStatus.value) params.append("status", selectedStatus.value);
    } else if (scope === "selected") {
      if (selectedMemberIds.value.length === 0) {
        showMessage("請先勾選要匯出的會員", "error");
        exporting.value = false;
        return;
      }
      params.append("ids", selectedMemberIds.value.join(","));
    } else if (scope === "custom") {
      if (customParams.minId) params.append("minId", customParams.minId);
      if (customParams.maxId) params.append("maxId", customParams.maxId);
      if (customParams.limit) params.append("limit", customParams.limit);
      if (customParams.offset) params.append("offset", customParams.offset);
      if (keyword.value.trim()) params.append("keyword", keyword.value.trim());
      if (selectedStatus.value) params.append("status", selectedStatus.value);
    }

    const queryString = params.toString() ? `?${params.toString()}` : "";
    const url = `${API_URL}/export${queryString}`;

    const token = localStorage.getItem("token");
    const headers = {};
    if (token) headers.Authorization = "Bearer " + token;

    const response = await fetch(url, { method: "GET", headers });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有權限", "error");
      return;
    }
    if (!response.ok) {
      const err = await response.json().catch(() => ({}));
      showMessage(err.message || "匯出會員失敗", "error");
      return;
    }

    const blob = await response.blob();
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = downloadUrl;
    const now = new Date();
    const dateStr = now.toISOString().slice(0, 10).replace(/-/g, "");
    link.download = `members_${dateStr}.json`;
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(downloadUrl);

    closeExportModal();
    showMessage("會員資料 JSON 匯出成功！", "success");
  } catch (error) {
    console.error("匯出錯誤：", error);
    showMessage("匯出會員資料失敗", "error");
  } finally {
    exporting.value = false;
  }
}

function openImportModal() {
  importResult.value = null;
  importModalOpen.value = true;
}

function closeImportModal() {
  importModalOpen.value = false;
  importResult.value = null;
}

async function handleImport({ mode, file, jsonText }) {
  importResult.value = null;
  const token = localStorage.getItem("token");

  try {
    importing.value = true;
    let response;

    if (mode === "file") {
      const formData = new FormData();
      formData.append("file", file);
      const headers = {};
      if (token) headers.Authorization = "Bearer " + token;
      response = await fetch(`${API_URL}/import`, {
        method: "POST",
        headers,
        body: formData,
      });
    } else {
      const headers = { "Content-Type": "application/json" };
      if (token) headers.Authorization = "Bearer " + token;
      response = await fetch(`${API_URL}/import`, {
        method: "POST",
        headers,
        body: jsonText.trim(),
      });
    }

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有權限", "error");
      return;
    }

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      showMessage(data.message || "匯入失敗", "error");
      importResult.value = data;
      return;
    }

    importResult.value = data;
    const isSuccess = (data.failureCount ?? 0) === 0;
    showMessage(
      `匯入完成！成功 ${data.successCount ?? 0} 筆，失敗 ${data.failureCount ?? 0} 筆`,
      isSuccess ? "success" : "error"
    );

    await loadMembers();
  } catch (error) {
    console.error("匯入錯誤：", error);
    showMessage("匯入失敗：" + error.message, "error");
  } finally {
    importing.value = false;
  }
}

onMounted(async () => {
  await loadMembers();
});
</script>

<style scoped>
.member-manage-page {
  width: 100%;
}

.quick-fill-btn {
  margin-right: auto;
  padding: 8px 16px;
  border: 1px solid #b58a46;
  border-radius: 6px;
  background-color: #fff8ee;
  color: #b58a46;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.quick-fill-btn:hover {
  background-color: #b58a46;
  color: #ffffff;
}
</style>
