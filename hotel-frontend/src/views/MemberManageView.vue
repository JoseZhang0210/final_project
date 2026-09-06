<script setup>
import { computed, onMounted, reactive, ref } from "vue";

// =====================================================
// API
// =====================================================

const API_URL = "/api/members";

// =====================================================
// 資料狀態
// =====================================================

const members = ref([]);

const keyword = ref("");

const selectedStatus = ref("");

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
// 表單（帳號 + Profile 完整欄位）
// =====================================================

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
});

// =====================================================
// JWT Header
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
// 訊息提示
// =====================================================

function showMessage(text, type) {
  message.value = text;

  messageType.value = type;

  setTimeout(() => {
    message.value = "";
  }, 2500);
}

// =====================================================
// 狀態輔助
// =====================================================

function getStatusLabel(status) {
  const value = (status || "UNKNOWN").toUpperCase();

  if (value === "1") {
    return "啟用";
  }

  if (value === "0") {
    return "停用";
  }

  return value;
}

function isActiveStatus(status) {
  const normalized = (status || "").toUpperCase();

  return ["ACTIVE", "1", "ENABLE", "ENABLED"].includes(normalized);
}

// =====================================================
// 多欄位搜尋與篩選
// =====================================================

const filteredMembers = computed(() => {
  const search = keyword.value.trim().toLowerCase();
  const statusFilter = selectedStatus.value;

  return members.value.filter((member) => {
    const matchesKeyword =
      !search ||
      (member.username || "").toLowerCase().includes(search) ||
      (member.name || "").toLowerCase().includes(search) ||
      (member.email || "").toLowerCase().includes(search) ||
      (member.phone || "").includes(search);

    const matchesStatus =
      !statusFilter || String(member.status) === String(statusFilter);

    return matchesKeyword && matchesStatus;
  });
});

// =====================================================
// 總頁數
// =====================================================

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(filteredMembers.value.length / pageSize));
});

// =====================================================
// 當前頁資料
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
// GET /api/members
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

  modalOpen.value = true;
}

// =====================================================
// 修改 Modal
// =====================================================

function openEditModal(member) {
  editingMemberId.value = member.memberId ?? member.id;

  form.username = member.username || "";
  form.password = "";
  form.status = member.status || "1";
  form.name = member.name || "";
  form.email = member.email || "";
  form.phone = member.phone || "";
  form.gender = member.gender || "男";
  form.birthday = member.birthday || "";
  form.zipcode = member.zipcode || "";
  form.city = member.city || "";
  form.district = member.district || "";
  form.address = member.address || "";

  modalOpen.value = true;
}

// =====================================================
// 關閉 Modal
// =====================================================

function closeModal() {
  modalOpen.value = false;
  editingMemberId.value = null;
}

// =====================================================
// 新增 / 修改會員
// POST /api/members
// PUT  /api/members/{id}
// =====================================================

async function saveMember() {
  const username = form.username.trim();

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

  if (form.password.trim()) {
    payload.password = form.password.trim();
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
      const data = await response.json().catch(() => ({}));
      showMessage(data.message || "儲存會員失敗", "error");
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
// PATCH /api/members/{id}/status
// =====================================================

async function toggleStatus(member) {
  if (!member) return;

  const memberId = member.memberId ?? member.id;
  if (!memberId) {
    console.error("無法取得會員 ID：", member);
    showMessage("無法取得會員 ID", "error");
    return;
  }

  const nextStatus = isActiveStatus(member.status) ? "0" : "1";

  try {
    const response = await fetch(
      `${API_URL}/${memberId}/status?status=${nextStatus}`,
      {
        method: "PATCH",
        headers: getAuthHeaders(),
      }
    );

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
// DELETE /api/members/{id}
// =====================================================

async function deleteMember(memberOrId) {
  const memberId =
    typeof memberOrId === "object" && memberOrId !== null
      ? memberOrId.memberId ?? memberOrId.id
      : memberOrId;

  if (!memberId) {
    showMessage("無法取得會員 ID", "error");
    return;
  }

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

    if (response.status === 409) {
      const data = await response.json().catch(() => ({}));
      showMessage(
        data.message || "無法刪除：該會員已有相關訂單或付款紀錄，建議改為停用",
        "error"
      );
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
    totalPages.value
  );
}

// =====================================================
// 表格勾選選取
// =====================================================

const selectedMemberIds = ref([]);

const isAllSelected = computed(() => {
  if (pagedMembers.value.length === 0) return false;
  return pagedMembers.value.every((m) =>
    selectedMemberIds.value.includes(m.memberId ?? m.id)
  );
});

function toggleSelectAll(event) {
  const checked = event.target.checked;
  const pageIds = pagedMembers.value.map((m) => m.memberId ?? m.id);
  if (checked) {
    const newSet = new Set([...selectedMemberIds.value, ...pageIds]);
    selectedMemberIds.value = Array.from(newSet);
  } else {
    selectedMemberIds.value = selectedMemberIds.value.filter(
      (id) => !pageIds.includes(id)
    );
  }
}

function clearSelection() {
  selectedMemberIds.value = [];
}

// =====================================================
// JSON 匯出 (Export)
// =====================================================

const exportModalOpen = ref(false);
const exporting = ref(false);
const exportScope = ref("filtered"); // 'filtered', 'all', 'selected', 'custom'
const exportCustom = reactive({
  minId: "",
  maxId: "",
  limit: "",
  offset: "",
});

function openExportModal() {
  if (selectedMemberIds.value.length > 0) {
    exportScope.value = "selected";
  } else if (keyword.value || selectedStatus.value) {
    exportScope.value = "filtered";
  } else {
    exportScope.value = "all";
  }
  exportModalOpen.value = true;
}

function closeExportModal() {
  exportModalOpen.value = false;
}

async function handleExport() {
  exporting.value = true;
  try {
    const params = new URLSearchParams();

    if (exportScope.value === "filtered") {
      if (keyword.value.trim()) params.append("keyword", keyword.value.trim());
      if (selectedStatus.value) params.append("status", selectedStatus.value);
    } else if (exportScope.value === "selected") {
      if (selectedMemberIds.value.length === 0) {
        showMessage("請先勾選要匯出的會員", "error");
        exporting.value = false;
        return;
      }
      params.append("ids", selectedMemberIds.value.join(","));
    } else if (exportScope.value === "custom") {
      if (exportCustom.minId) params.append("minId", exportCustom.minId);
      if (exportCustom.maxId) params.append("maxId", exportCustom.maxId);
      if (exportCustom.limit) params.append("limit", exportCustom.limit);
      if (exportCustom.offset) params.append("offset", exportCustom.offset);
      if (keyword.value.trim()) params.append("keyword", keyword.value.trim());
      if (selectedStatus.value) params.append("status", selectedStatus.value);
    }
    // 'all' 不帶篩選條件，匯出全部

    const queryString = params.toString() ? `?${params.toString()}` : "";
    const url = `${API_URL}/export${queryString}`;

    const token = localStorage.getItem("token");
    const headers = {};
    if (token) {
      headers.Authorization = "Bearer " + token;
    }

    const response = await fetch(url, {
      method: "GET",
      headers: headers,
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有會員管理權限", "error");
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

// =====================================================
// JSON 匯入 (Import)
// =====================================================

const importModalOpen = ref(false);
const importing = ref(false);
const importMode = ref("file"); // 'file' or 'text'
const importFile = ref(null);
const importJsonText = ref("");
const importResult = ref(null);
const fileInputRef = ref(null);

function openImportModal() {
  importFile.value = null;
  importJsonText.value = "";
  importResult.value = null;
  importMode.value = "file";
  importModalOpen.value = true;
}

function closeImportModal() {
  importModalOpen.value = false;
  importResult.value = null;
}

function onFileChange(event) {
  const file = event.target.files?.[0];
  if (file) {
    if (!file.name.endsWith(".json")) {
      showMessage("請選擇 .json 格式的檔案", "error");
      event.target.value = "";
      return;
    }
    importFile.value = file;
  }
}

async function handleImport() {
  importResult.value = null;
  const token = localStorage.getItem("token");

  try {
    importing.value = true;
    let response;

    if (importMode.value === "file") {
      if (!importFile.value) {
        showMessage("請先選擇要匯入的 JSON 檔案", "error");
        importing.value = false;
        return;
      }

      const formData = new FormData();
      formData.append("file", importFile.value);

      const headers = {};
      if (token) {
        headers.Authorization = "Bearer " + token;
      }

      response = await fetch(`${API_URL}/import`, {
        method: "POST",
        headers: headers,
        body: formData,
      });
    } else {
      if (!importJsonText.value.trim()) {
        showMessage("請輸入要匯入的 JSON 內容", "error");
        importing.value = false;
        return;
      }

      const headers = {
        "Content-Type": "application/json",
      };
      if (token) {
        headers.Authorization = "Bearer " + token;
      }

      response = await fetch(`${API_URL}/import`, {
        method: "POST",
        headers: headers,
        body: importJsonText.value.trim(),
      });
    }

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有會員管理權限", "error");
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

    // 重新載入列表
    await loadMembers();
  } catch (error) {
    console.error("匯入錯誤：", error);
    showMessage("匯入失敗：" + error.message, "error");
  } finally {
    importing.value = false;
  }
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
        <p>管理會員帳號、個人資料與啟用狀態</p>
      </div>
    </div>

    <!-- =========================
         會員列表
         ========================= -->

    <section class="admin-card">
      <div class="member-list-header">
        <div>
          <h2>會員列表</h2>
          <p>可搜尋、新增、修改個人資料、啟用、停用與刪除會員</p>
        </div>

        <div class="member-toolbar">
          <!-- 狀態篩選 -->
          <select
            v-model="selectedStatus"
            class="admin-input filter-select"
            @change="resetPage"
          >
            <option value="">全部狀態</option>
            <option value="1">啟用</option>
            <option value="0">停用</option>
          </select>

          <!-- 關鍵字搜尋 -->
          <input
            v-model="keyword"
            type="text"
            class="admin-input member-search"
            placeholder="搜尋帳號、姓名、信箱、電話..."
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
            @click="openImportModal"
          >
            📥 匯入 JSON
          </button>

          <button
            type="button"
            class="admin-btn admin-btn-secondary"
            @click="openExportModal"
          >
            📤 匯出 JSON
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
        <strong>{{ filteredMembers.length }}</strong>
        位會員
        <span v-if="filteredMembers.length !== members.length" class="total-hint">
          （全體共 {{ members.length }} 位）
        </span>
        <span v-if="selectedMemberIds.length > 0" class="selected-hint">
          已選取 <strong>{{ selectedMemberIds.length }}</strong> 位會員
          <button type="button" class="link-btn" @click="clearSelection">清除選取</button>
        </span>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-message">會員資料讀取中...</div>

      <!-- Table -->
      <div v-else class="admin-table-wrapper">
        <table class="admin-table">
          <thead>
            <tr>
              <th style="width: 44px; text-align: center;">
                <input
                  type="checkbox"
                  :checked="isAllSelected"
                  @change="toggleSelectAll"
                  title="全選 / 取消全選本頁"
                />
              </th>
              <th>ID</th>
              <th>帳號 / 姓名</th>
              <th>聯絡方式</th>
              <th>性別</th>
              <th>狀態</th>
              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="pagedMembers.length === 0">
              <td colspan="7" class="empty-row">目前沒有符合條件的會員資料</td>
            </tr>

            <tr
              v-for="member in pagedMembers"
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
                <div class="member-name-cell">
                  <span class="member-name">{{ member.name || "未填姓名" }}</span>
                  <span class="member-username">(@{{ member.username }})</span>
                </div>
              </td>

              <td>
                <div class="contact-info">
                  <div v-if="member.phone" class="contact-item">
                    📞 {{ member.phone }}
                  </div>
                  <div v-if="member.email" class="contact-item">
                    ✉️ {{ member.email }}
                  </div>
                  <span v-if="!member.phone && !member.email" class="text-muted">
                    未填寫
                  </span>
                </div>
              </td>

              <td>
                <span class="gender-text">{{ member.gender || "—" }}</span>
              </td>

              <td>
                <span
                  class="status-badge"
                  :class="isActiveStatus(member.status) ? 'status-active' : 'status-inactive'"
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
                    :class="isActiveStatus(member.status) ? 'status-disable-btn' : 'status-enable-btn'"
                    @click="toggleStatus(member)"
                  >
                    {{ isActiveStatus(member.status) ? "停用" : "啟用" }}
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

        <span>{{ currentPage }} / {{ totalPages }}</span>

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
         新增 / 修改 Modal
         ========================= -->

    <div v-if="modalOpen" class="member-modal" @click.self="closeModal">
      <div class="member-modal-card">
        <div class="member-modal-header">
          <h2>
            {{ editingMemberId === null ? "新增會員" : "修改會員資料" }}
          </h2>

          <button type="button" class="modal-close" @click="closeModal">
            ×
          </button>
        </div>

        <form class="member-form" @submit.prevent="saveMember">
          <!-- 區塊 1: 帳號設定 -->
          <div class="form-section-title">🔐 帳號設定</div>
          <div class="admin-form-grid">
            <div class="admin-form-group">
              <label> 帳號 <span class="required">*</span> </label>
              <input
                v-model="form.username"
                type="text"
                placeholder="請輸入登入帳號"
                required
              />
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

          <!-- 區塊 2: 個人基本資料 -->
          <div class="form-section-title">👤 個人基本資料</div>
          <div class="admin-form-grid">
            <div class="admin-form-group">
              <label> 姓名 </label>
              <input
                v-model="form.name"
                type="text"
                placeholder="請輸入真實姓名"
              />
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
              <input
                v-model="form.email"
                type="email"
                placeholder="例：member@example.com"
              />
            </div>

            <div class="admin-form-group">
              <label> 聯絡電話 </label>
              <input
                v-model="form.phone"
                type="text"
                placeholder="例：0912345678"
              />
            </div>

            <div class="admin-form-group">
              <label> 出生日期 </label>
              <input v-model="form.birthday" type="date" />
            </div>

            <div class="admin-form-group">
              <label> 郵遞區號 </label>
              <input
                v-model="form.zipcode"
                type="text"
                placeholder="例：320"
              />
            </div>

            <div class="admin-form-group">
              <label> 縣市 </label>
              <input
                v-model="form.city"
                type="text"
                placeholder="例：桃園市"
              />
            </div>

            <div class="admin-form-group">
              <label> 鄉鎮市區 </label>
              <input
                v-model="form.district"
                type="text"
                placeholder="例：中壢區"
              />
            </div>

            <div class="admin-form-group full-width">
              <label> 詳細地址 </label>
              <input
                v-model="form.address"
                type="text"
                placeholder="請輸入詳細街道地址"
              />
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

    <!-- =========================
         匯出 Modal
         ========================= -->
    <div v-if="exportModalOpen" class="member-modal" @click.self="closeExportModal">
      <div class="member-modal-card export-modal-card">
        <div class="member-modal-header">
          <h2>📤 匯出會員資料 (JSON)</h2>
          <button type="button" class="modal-close" @click="closeExportModal">×</button>
        </div>

        <div class="member-modal-body">
          <div class="form-section-title">選擇匯出資料範圍</div>

          <div class="export-scope-options">
            <!-- 選項 1: 目前篩選結果 -->
            <label class="scope-option">
              <input type="radio" v-model="exportScope" value="filtered" />
              <div class="scope-info">
                <strong>目前搜尋與篩選結果</strong>
                <span>符合關鍵字 ({{ keyword || '無' }}) 與狀態篩選，共 <b>{{ filteredMembers.length }}</b> 筆</span>
              </div>
            </label>

            <!-- 選項 2: 全部會員 -->
            <label class="scope-option">
              <input type="radio" v-model="exportScope" value="all" />
              <div class="scope-info">
                <strong>全體會員資料</strong>
                <span>匯出系統內所有會員，共 <b>{{ members.length }}</b> 筆</span>
              </div>
            </label>

            <!-- 選項 3: 已勾選項目 -->
            <label class="scope-option" :class="{ disabled: selectedMemberIds.length === 0 }">
              <input type="radio" v-model="exportScope" value="selected" :disabled="selectedMemberIds.length === 0" />
              <div class="scope-info">
                <strong>表格中已勾選的會員</strong>
                <span>目前已選取 <b>{{ selectedMemberIds.length }}</b> 筆資料</span>
              </div>
            </label>

            <!-- 選項 4: 自訂範圍 -->
            <label class="scope-option">
              <input type="radio" v-model="exportScope" value="custom" />
              <div class="scope-info">
                <strong>自訂數值範圍 / 筆數限制</strong>
                <span>自訂 ID 區間或分頁偏移量進行精準匯出</span>
              </div>
            </label>
          </div>

          <!-- 自訂範圍參數面板 -->
          <div v-if="exportScope === 'custom'" class="custom-range-panel">
            <div class="admin-form-grid">
              <div class="admin-form-group">
                <label>最小 ID (minId)</label>
                <input v-model.number="exportCustom.minId" type="number" placeholder="例如：1" />
              </div>
              <div class="admin-form-group">
                <label>最大 ID (maxId)</label>
                <input v-model.number="exportCustom.maxId" type="number" placeholder="例如：100" />
              </div>
              <div class="admin-form-group">
                <label>筆數限制 (limit)</label>
                <input v-model.number="exportCustom.limit" type="number" placeholder="例如：50" />
              </div>
              <div class="admin-form-group">
                <label>位移筆數 (offset)</label>
                <input v-model.number="exportCustom.offset" type="number" placeholder="例如：0" />
              </div>
            </div>
          </div>

          <div class="export-notice">
            💡 系統將調用後端 <code>/api/members/export</code> API，產出格式化的 <code>members.json</code> 檔案並自動下載。
          </div>
        </div>

        <div class="admin-form-actions modal-footer">
          <button type="button" class="admin-btn admin-btn-secondary" @click="closeExportModal">
            取消
          </button>
          <button type="button" class="admin-btn admin-btn-primary" :disabled="exporting" @click="handleExport">
            {{ exporting ? "匯出中..." : "確認匯出" }}
          </button>
        </div>
      </div>
    </div>

    <!-- =========================
         匯入 Modal
         ========================= -->
    <div v-if="importModalOpen" class="member-modal" @click.self="closeImportModal">
      <div class="member-modal-card import-modal-card">
        <div class="member-modal-header">
          <h2>📥 匯入會員資料 (JSON)</h2>
          <button type="button" class="modal-close" @click="closeImportModal">×</button>
        </div>

        <div class="member-modal-body">
          <!-- 模式切換 Tabs -->
          <div class="import-tabs">
            <button
              type="button"
              class="import-tab-btn"
              :class="{ active: importMode === 'file' }"
              @click="importMode = 'file'"
            >
              📁 上傳 JSON 檔案
            </button>
            <button
              type="button"
              class="import-tab-btn"
              :class="{ active: importMode === 'text' }"
              @click="importMode = 'text'"
            >
              📝 貼上 JSON 內容
            </button>
          </div>

          <!-- 檔案上傳模式 -->
          <div v-if="importMode === 'file'" class="file-upload-area">
            <label class="file-dropzone" for="import-file-input">
              <div class="dropzone-content">
                <span class="upload-icon">📄</span>
                <span v-if="!importFile" class="dropzone-text">
                  點擊此處選取 <strong>.json</strong> 檔案，或拖放檔案至此
                </span>
                <span v-else class="dropzone-filename">
                  已選取：<strong>{{ importFile.name }}</strong> ({{ (importFile.size / 1024).toFixed(1) }} KB)
                </span>
              </div>
              <input
                id="import-file-input"
                ref="fileInputRef"
                type="file"
                accept=".json,application/json"
                style="display: none;"
                @change="onFileChange"
              />
            </label>
          </div>

          <!-- 文字貼上模式 -->
          <div v-else class="text-upload-area">
            <textarea
              v-model="importJsonText"
              class="import-textarea"
              rows="9"
              placeholder='請在此貼上 JSON 格式的會員資料，例如：&#10;[&#10;  {&#10;    "username": "hotel_guest",&#10;    "name": "陳大名",&#10;    "email": "guest@example.com",&#10;    "phone": "0988123456",&#10;    "gender": "男",&#10;    "status": "1"&#10;  }&#10;]'
            ></textarea>
          </div>

          <!-- 匯入規則說明 -->
          <div class="import-guide">
            <div class="guide-title">📌 匯入規則：</div>
            <ul>
              <li>支援多筆陣列 <code>[...]</code> 或單筆物件 <code>{...}</code>。</li>
              <li>若帳號已存在，系統將自動<strong>更新</strong>該會員詳細資料。</li>
              <li>若帳號不存在，系統將<strong>新增</strong>會員（密碼預設為 <code>123456</code>）。</li>
            </ul>
          </div>

          <!-- 匯入結果提示 -->
          <div v-if="importResult" class="import-result-box" :class="importResult.failureCount > 0 ? 'has-error' : 'success'">
            <div class="result-header">
              <strong>匯入結果：</strong>
              <span>總計 {{ importResult.total ?? 0 }} 筆 ｜ 成功 {{ importResult.successCount ?? 0 }} 筆 ｜ 失敗 {{ importResult.failureCount ?? 0 }} 筆</span>
            </div>
            <ul v-if="importResult.errors && importResult.errors.length > 0" class="error-list">
              <li v-for="(err, idx) in importResult.errors" :key="idx">{{ err }}</li>
            </ul>
          </div>
        </div>

        <div class="admin-form-actions modal-footer">
          <button type="button" class="admin-btn admin-btn-secondary" @click="closeImportModal">
            關閉
          </button>
          <button
            type="button"
            class="admin-btn admin-btn-primary"
            :disabled="importing || (importMode === 'file' && !importFile) || (importMode === 'text' && !importJsonText.trim())"
            @click="handleImport"
          >
            {{ importing ? "處理中..." : "開始匯入" }}
          </button>
        </div>
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

.filter-select {
  width: 120px;
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  background-color: white;
  color: #4a3b2a;
}

.member-search {
  width: 240px;
}

/* =========================
   Summary
   ========================= */

.member-summary {
  margin-bottom: 18px;
  color: #6d6258;
  font-size: 14px;
}

.member-summary strong {
  color: #9b7435;
  font-size: 16px;
}

.total-hint {
  color: #999;
  font-size: 13px;
  margin-left: 6px;
}

/* =========================
   Table
   ========================= */

.member-name-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.member-username {
  color: #8c7b6d;
  font-size: 12px;
}

.member-name {
  color: #5b4632;
  font-weight: bold;
  font-size: 15px;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
  font-size: 13px;
  color: #555;
}

.contact-item {
  white-space: nowrap;
}

.gender-text {
  color: #555;
  font-size: 14px;
}

.text-muted {
  color: #aaa;
  font-size: 13px;
}

.empty-row {
  padding: 40px !important;
  text-align: center !important;
  color: #888 !important;
}

/* =========================
   Status Badge
   ========================= */

.status-badge {
  display: inline-block;
  min-width: 65px;
  padding: 5px 10px;
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
  width: min(640px, 94vw);
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: white;
  border-radius: 14px;
  box-shadow: 0 16px 50px rgba(0, 0, 0, 0.22);
}

.member-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 24px;
  background-color: #4a3b2a;
  color: white;
  flex-shrink: 0;
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
  overflow-y: auto;
}

.form-section-title {
  font-size: 15px;
  font-weight: bold;
  color: #6f5328;
  margin: 16px 0 10px;
  padding-bottom: 6px;
  border-bottom: 2px solid #f3ede2;
}

.form-section-title:first-child {
  margin-top: 0;
}

.required {
  color: #b3443c;
}

.admin-form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 14px 16px;
}

.admin-form-group {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.admin-form-group.full-width {
  grid-column: 1 / -1;
}

.admin-form-group label {
  font-size: 13px;
  font-weight: 600;
  color: #4a3b2a;
}

.admin-form-group input,
.admin-form-group select {
  padding: 9px 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 14px;
  outline: none;
  transition: border-color 0.2s;
}

.admin-form-group input:focus,
.admin-form-group select:focus {
  border-color: #b58a46;
}

.admin-form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 18px;
  border-top: 1px solid #eee;
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

  .filter-select,
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

  .admin-form-grid {
    grid-template-columns: 1fr;
  }
}

/* =========================
   選取與選中狀態
   ========================= */

.row-selected {
  background-color: #fdf8ef !important;
}

.selected-hint {
  margin-left: 12px;
  color: #9b7435;
  font-size: 13px;
  background-color: #fdf8ef;
  padding: 3px 10px;
  border-radius: 6px;
  border: 1px solid #f2e3cb;
}

.link-btn {
  margin-left: 6px;
  background: none;
  border: none;
  color: #8c7b6d;
  text-decoration: underline;
  cursor: pointer;
  font-size: 12px;
  padding: 0;
}

.link-btn:hover {
  color: #b3443c;
}

/* =========================
   匯出 / 匯入 Modal 共用樣式
   ========================= */

.export-modal-card,
.import-modal-card {
  width: min(620px, 94vw);
}

.member-modal-body {
  padding: 22px 24px;
  overflow-y: auto;
  max-height: calc(85vh - 140px);
}

.modal-footer {
  margin-top: 0;
  padding: 16px 24px;
  border-top: 1px solid #eee;
  background-color: #faf8f5;
}

/* =========================
   匯出資料範圍選項
   ========================= */

.export-scope-options {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-bottom: 16px;
}

.scope-option {
  display: flex;
  align-items: flex-start;
  gap: 12px;
  padding: 12px 14px;
  border: 1.5px solid #e8e2d7;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.2s;
  background-color: #fdfcfb;
}

.scope-option:hover {
  border-color: #b58a46;
  background-color: #fbf8f2;
}

.scope-option input[type="radio"] {
  margin-top: 3px;
  accent-color: #b58a46;
}

.scope-option.disabled {
  opacity: 0.55;
  cursor: not-allowed;
  border-color: #eee;
  background-color: #f7f7f7;
}

.scope-info {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.scope-info strong {
  font-size: 14px;
  color: #4a3b2a;
}

.scope-info span {
  font-size: 12px;
  color: #777;
}

.scope-info b {
  color: #9b7435;
}

.custom-range-panel {
  background-color: #fbf9f5;
  border: 1px solid #eee5d8;
  border-radius: 8px;
  padding: 14px;
  margin-bottom: 16px;
}

.export-notice {
  font-size: 12px;
  color: #6d6258;
  background-color: #f5f1eb;
  padding: 10px 14px;
  border-radius: 8px;
  line-height: 1.6;
}

.export-notice code {
  background-color: #e8e0d4;
  padding: 2px 5px;
  border-radius: 4px;
  font-family: monospace;
}

/* =========================
   匯入 Modal 專用樣式
   ========================= */

.import-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 18px;
}

.import-tab-btn {
  flex: 1;
  padding: 10px 14px;
  border: 1.5px solid #ddd;
  border-radius: 8px;
  background-color: #fbf9f5;
  color: #666;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.import-tab-btn:hover {
  border-color: #b58a46;
  color: #4a3b2a;
}

.import-tab-btn.active {
  border-color: #b58a46;
  background-color: #b58a46;
  color: white;
}

.file-upload-area {
  margin-bottom: 16px;
}

.file-dropzone {
  display: block;
  border: 2px dashed #caa96e;
  border-radius: 10px;
  padding: 32px 20px;
  text-align: center;
  cursor: pointer;
  background-color: #fdfbf7;
  transition: all 0.2s;
}

.file-dropzone:hover {
  border-color: #95691f;
  background-color: #fbf4e6;
}

.dropzone-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.upload-icon {
  font-size: 38px;
}

.dropzone-text {
  font-size: 14px;
  color: #6f5328;
}

.dropzone-filename {
  font-size: 14px;
  color: #257641;
}

.text-upload-area {
  margin-bottom: 16px;
}

.import-textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-family: Consolas, Monaco, monospace;
  font-size: 13px;
  line-height: 1.5;
  outline: none;
  resize: vertical;
  box-sizing: border-box;
}

.import-textarea:focus {
  border-color: #b58a46;
}

.import-guide {
  background-color: #f9f7f3;
  border-left: 4px solid #b58a46;
  padding: 10px 14px;
  border-radius: 4px;
  margin-bottom: 14px;
}

.guide-title {
  font-weight: bold;
  font-size: 13px;
  color: #5b4632;
  margin-bottom: 4px;
}

.import-guide ul {
  margin: 0;
  padding-left: 20px;
  font-size: 12px;
  color: #666;
  line-height: 1.6;
}

.import-guide code {
  background-color: #eee7dc;
  padding: 1px 4px;
  border-radius: 3px;
  font-family: monospace;
}

.import-result-box {
  padding: 14px 16px;
  border-radius: 8px;
  font-size: 13px;
  margin-top: 14px;
}

.import-result-box.success {
  background-color: #e5f6eb;
  border: 1px solid #c0e7cc;
  color: #257641;
}

.import-result-box.has-error {
  background-color: #fde9e7;
  border: 1px solid #f9c7c2;
  color: #b3443c;
}

.result-header {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  gap: 6px;
  font-size: 13px;
}

.error-list {
  margin: 8px 0 0;
  padding-left: 20px;
  font-size: 12px;
  max-height: 120px;
  overflow-y: auto;
}
</style>
