<template>
  <div class="member-manage-page">
    <!-- =========================
         頁面標題列
         ========================= -->
    <div class="admin-page-header">
      <div>
        <h1>會員管理</h1>
        <p>管理會員帳號、個人資料與啟用狀態</p>
      </div>

      <div class="member-header-actions">
        <button
          type="button"
          class="admin-btn admin-btn-secondary member-json-button"
          :disabled="exporting || importing"
          @click="openExportModal"
        >
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 3v12m0 0 4-4m-4 4-4-4M5 21h14a2 2 0 0 0 2-2v-3M3 16v3a2 2 0 0 0 2 2" />
          </svg>
          {{ exporting ? "匯出中..." : "匯出 JSON" }}
        </button>

        <button
          type="button"
          class="admin-btn admin-btn-secondary member-json-button"
          :disabled="exporting || importing"
          @click="openImportModal"
        >
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 21V9m0 0 4 4m-4-4-4 4M5 3h14a2 2 0 0 1 2 2v3M3 8V5a2 2 0 0 1 2-2" />
          </svg>
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

    <!-- =========================
         會員管理卡片
         ========================= -->
    <section class="admin-card">
      <!-- =========================
           搜尋 / 篩選
           ========================= -->
      <div class="member-search">
        <input
          v-model="keyword"
          type="text"
          class="admin-input search-input"
          placeholder="搜尋帳號、姓名、信箱、電話..."
          @keyup.enter="resetPage"
        />

        <!-- 狀態 -->
        <select v-model="selectedStatus" class="admin-input filter-select">
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

      <!-- =========================
           資料控制列
           ========================= -->
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

      <!-- =========================
           訊息
           ========================= -->
      <div v-if="message" class="admin-message" :class="messageType">
        {{ message }}
      </div>

      <!-- =========================
           Loading
           ========================= -->
      <div v-if="loading" class="loading-message">會員資料讀取中...</div>

      <!-- =========================
           會員表格
           ========================= -->
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

              <th class="sortable" @click="changeSort('memberId')">
                ID
                <span class="sort-icon">{{ getSortIcon("memberId") }}</span>
              </th>

              <th class="sortable" @click="changeSort('username')">
                帳號 / 姓名
                <span class="sort-icon">{{ getSortIcon("username") }}</span>
              </th>

              <th>聯絡方式</th>

              <th class="sortable" @click="changeSort('gender')">
                性別
                <span class="sort-icon">{{ getSortIcon("gender") }}</span>
              </th>

              <th class="sortable" @click="changeSort('status')">
                狀態
                <span class="sort-icon">{{ getSortIcon("status") }}</span>
              </th>

              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <!-- 沒資料 -->
            <tr v-if="paginatedMembers.length === 0">
              <td colspan="7" class="empty-message">查無符合條件的會員</td>
            </tr>

            <!-- 會員列表 -->
            <tr
              v-for="member in paginatedMembers"
              :key="member.memberId ?? member.id"
              :class="{ 'row-selected': selectedMemberIds.includes(member.memberId ?? member.id) }"
            >
              <!-- 勾選框 -->
              <td style="text-align: center;">
                <input
                  type="checkbox"
                  :value="member.memberId ?? member.id"
                  v-model="selectedMemberIds"
                />
              </td>

              <!-- ID -->
              <td>{{ member.memberId ?? member.id }}</td>

              <!-- 帳號 / 姓名 -->
              <td>
                <div class="member-name-cell">
                  <span class="member-name">{{ member.name || "未填姓名" }}</span>
                  <span class="member-username">(@{{ member.username }})</span>
                </div>
              </td>

              <!-- 聯絡方式 -->
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

              <!-- 性別 -->
              <td>
                <span class="gender-text">{{ member.gender || "—" }}</span>
              </td>

              <!-- 狀態 -->
              <td>
                <span
                  class="status-badge"
                  :class="isActiveStatus(member.status) ? 'status-active' : 'status-inactive'"
                >
                  {{ getStatusLabel(member.status) }}
                </span>
              </td>

              <!-- 操作 -->
              <td>
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

      <!-- =========================
           分頁
           ========================= -->
      <div v-if="!loading && sortedMembers.length > 0" class="pagination-area">
        <div class="pagination-info">
          第
          <strong>{{ currentPage }}</strong>
          頁 ／ 共
          <strong>{{ totalPages }}</strong>
          頁
        </div>

        <div class="pagination">
          <button
            type="button"
            class="page-button"
            :disabled="currentPage === 1"
            @click="goToPage(1)"
          >
            «
          </button>

          <button
            type="button"
            class="page-button"
            :disabled="currentPage === 1"
            @click="goToPage(currentPage - 1)"
          >
            ‹
          </button>

          <button
            v-for="page in visiblePages"
            :key="page"
            type="button"
            class="page-button"
            :class="{ active: currentPage === page }"
            @click="goToPage(page)"
          >
            {{ page }}
          </button>

          <button
            type="button"
            class="page-button"
            :disabled="currentPage === totalPages"
            @click="goToPage(currentPage + 1)"
          >
            ›
          </button>

          <button
            type="button"
            class="page-button"
            :disabled="currentPage === totalPages"
            @click="goToPage(totalPages)"
          >
            »
          </button>
        </div>
      </div>
    </section>

    <!-- =====================================================
         新增 / 修改 Modal
         ===================================================== -->
    <div v-if="modalOpen" class="member-modal" @click.self="closeModal">
      <div class="member-manage-card">
        <!-- Header -->
        <div class="member-modal-header">
          <div>
            <h2>{{ editingMemberId === null ? "新增會員" : "修改會員資料" }}</h2>
            <p>{{ editingMemberId === null ? "建立新的會員帳號與個人檔案" : `編輯會員 #${editingMemberId} 資料` }}</p>
          </div>

          <button type="button" class="modal-close" @click="closeModal">
            ×
          </button>
        </div>

        <!-- 表單內容 -->
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

          <div class="member-modal-footer">
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

    <!-- =====================================================
         匯出 Modal
         ===================================================== -->
    <div v-if="exportModalOpen" class="member-modal" @click.self="closeExportModal">
      <div class="member-manage-card export-modal-card">
        <div class="member-modal-header">
          <div>
            <h2>匯出會員資料</h2>
            <p>匯出 JSON 格式的會員帳號與檔案資料</p>
          </div>
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
                <span>符合關鍵字 ({{ keyword || '無' }}) 與狀態篩選，共 <b>{{ sortedMembers.length }}</b> 筆</span>
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
            💡 系統將調用後端 <code>/api/members/export</code> API，匯出包含 <code>password</code> 且排除驗證碼的格式化 <code>members.json</code> 檔案並自動下載。
          </div>
        </div>

        <div class="member-modal-footer">
          <button type="button" class="admin-btn admin-btn-secondary" @click="closeExportModal">
            取消
          </button>
          <button type="button" class="admin-btn admin-btn-primary" :disabled="exporting" @click="handleExport">
            {{ exporting ? "匯出中..." : "確認匯出" }}
          </button>
        </div>
      </div>
    </div>

    <!-- =====================================================
         匯入 Modal
         ===================================================== -->
    <div v-if="importModalOpen" class="member-modal" @click.self="closeImportModal">
      <div class="member-manage-card import-modal-card">
        <div class="member-modal-header">
          <div>
            <h2>匯入會員資料</h2>
            <p>透過 JSON 檔案或文字批次匯入會員資料</p>
          </div>
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
            <label
              class="file-dropzone"
              :class="{ 'dropzone-active': isDragging }"
              for="import-file-input"
              @dragover.prevent="onDragOver"
              @dragleave.prevent="onDragLeave"
              @drop.prevent="onDrop"
            >
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
              placeholder='請在此貼上 JSON 格式的會員資料，例如：&#10;[&#10;  {&#10;    "username": "hotel_guest",&#10;    "password": "user123",&#10;    "name": "陳大名",&#10;    "email": "guest@example.com",&#10;    "phone": "0988123456",&#10;    "gender": "男",&#10;    "status": "1"&#10;  }&#10;]'
            ></textarea>
          </div>

          <!-- 匯入規則說明 -->
          <div class="import-guide">
            <div class="guide-title">📌 匯入規則：</div>
            <ul>
              <li>支援多筆陣列 <code>[...]</code> 或單筆物件 <code>{...}</code>。</li>
              <li>支援 <code>password</code> 密碼匯入（支援明文或已雜湊密碼；未填則預設為 <code>123456</code>）。</li>
              <li>若帳號已存在，系統將自動<strong>更新</strong>該會員詳細資料。</li>
              <li>若帳號不存在，系統將<strong>新增</strong>會員。</li>
              <li><code>verificationCode</code> 欄位無須匯入。</li>
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

        <div class="member-modal-footer">
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

<script setup>
import { computed, onMounted, reactive, ref, watch } from "vue";

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
// 排序
// =====================================================

const sortKey = ref("memberId");

const sortDirection = ref("asc");

// =====================================================
// 分頁
// =====================================================

const currentPage = ref(1);

const pageSize = ref(10);

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

function showMessage(text, type, duration = 3000) {
  message.value = text;

  messageType.value = type;

  setTimeout(() => {
    message.value = "";
  }, duration);
}

// =====================================================
// 狀態輔助
// =====================================================

function getStatusLabel(status) {
  const value = String(status ?? "UNKNOWN").toUpperCase();

  if (value === "1" || value === "ACTIVE" || value === "ENABLE" || value === "ENABLED") {
    return "啟用";
  }

  if (value === "0" || value === "INACTIVE" || value === "DISABLE" || value === "DISABLED") {
    return "停用";
  }

  return value;
}

function isActiveStatus(status) {
  const normalized = String(status ?? "").toUpperCase();

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
// 排序
// =====================================================

const sortedMembers = computed(() => {
  const result = [...filteredMembers.value];

  result.sort((a, b) => {
    let valueA;
    let valueB;

    switch (sortKey.value) {
      case "username":
        valueA = a.username || "";
        valueB = b.username || "";
        break;

      case "name":
        valueA = a.name || "";
        valueB = b.name || "";
        break;

      case "gender":
        valueA = a.gender || "";
        valueB = b.gender || "";
        break;

      case "status":
        valueA = getStatusLabel(a.status);
        valueB = getStatusLabel(b.status);
        break;

      default:
        valueA = Number(a.memberId ?? a.id ?? 0);
        valueB = Number(b.memberId ?? b.id ?? 0);
    }

    let compareResult;

    if (typeof valueA === "number" && typeof valueB === "number") {
      compareResult = valueA - valueB;
    } else {
      compareResult = String(valueA).localeCompare(String(valueB), "zh-TW");
    }

    return sortDirection.value === "asc" ? compareResult : -compareResult;
  });

  return result;
});

function changeSort(key) {
  if (sortKey.value === key) {
    sortDirection.value = sortDirection.value === "asc" ? "desc" : "asc";
  } else {
    sortKey.value = key;
    sortDirection.value = "asc";
  }

  currentPage.value = 1;
}

function getSortIcon(key) {
  if (sortKey.value !== key) {
    return "↕";
  }

  return sortDirection.value === "asc" ? "▲" : "▼";
}

// =====================================================
// 分頁
// =====================================================

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(sortedMembers.value.length / pageSize.value));
});

const paginatedMembers = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;

  return sortedMembers.value.slice(start, start + pageSize.value);
});

const visiblePages = computed(() => {
  const pages = [];
  const maxVisible = 5;
  let start = Math.max(1, currentPage.value - 2);
  let end = Math.min(totalPages.value, start + maxVisible - 1);

  if (end - start + 1 < maxVisible) {
    start = Math.max(1, end - maxVisible + 1);
  }

  for (let page = start; page <= end; page++) {
    pages.push(page);
  }

  return pages;
});

function goToPage(page) {
  if (page < 1 || page > totalPages.value) {
    return;
  }

  currentPage.value = page;
}

function resetPage() {
  currentPage.value = 1;
}

function resetSearch() {
  keyword.value = "";
  selectedStatus.value = "";
  sortKey.value = "memberId";
  sortDirection.value = "asc";
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
    showMessage("無法取得會員 ID", "error");
    return;
  }

  const nextStatus = isActiveStatus(member.status) ? "0" : "1";
  const actionText = nextStatus === "1" ? "啟用" : "停用";

  if (!window.confirm(`確定要${actionText}會員「${member.name || member.username}」嗎？`)) {
    return;
  }

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

    showMessage(`會員已${actionText}`, "success");
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

    showMessage("會員刪除成功", "success");
    await loadMembers();
  } catch (error) {
    console.error("刪除會員錯誤：", error);
    showMessage("刪除會員失敗", "error");
  }
}

// =====================================================
// 表格勾選選取
// =====================================================

const selectedMemberIds = ref([]);

const isAllSelected = computed(() => {
  if (paginatedMembers.value.length === 0) return false;
  return paginatedMembers.value.every((m) =>
    selectedMemberIds.value.includes(m.memberId ?? m.id)
  );
});

function toggleSelectAll(event) {
  const checked = event.target.checked;
  const pageIds = paginatedMembers.value.map((m) => m.memberId ?? m.id);
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
const isDragging = ref(false);

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

function onDragOver() {
  isDragging.value = true;
}

function onDragLeave() {
  isDragging.value = false;
}

function onDrop(event) {
  isDragging.value = false;
  const file = event.dataTransfer?.files?.[0];
  if (file) {
    if (!file.name.endsWith(".json")) {
      showMessage("請選擇 .json 格式的檔案", "error");
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

    await loadMembers();
  } catch (error) {
    console.error("匯入錯誤：", error);
    showMessage("匯入失敗：" + error.message, "error");
  } finally {
    importing.value = false;
  }
}

// =====================================================
// Watch
// =====================================================

watch([selectedStatus], () => {
  currentPage.value = 1;
});

watch(pageSize, () => {
  currentPage.value = 1;
});

watch(totalPages, (total) => {
  if (currentPage.value > total) {
    currentPage.value = total;
  }
});

// =====================================================
// 初始化
// =====================================================

onMounted(() => {
  loadMembers();
});
</script>

<style scoped>
/* =========================================================
   MemberManageView
   會員管理
   ========================================================= */

.member-manage-page {
  width: 100%;
}

/* =========================================================
   Header
   ========================================================= */

.member-header-actions {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.member-json-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
}

.member-json-button svg {
  width: 17px;
  height: 17px;
  fill: none;
  stroke: currentColor;
  stroke-width: 1.8;
  stroke-linecap: round;
  stroke-linejoin: round;
}

.member-json-button:disabled {
  cursor: wait;
  opacity: 0.58;
  transform: none;
}

/* =========================================================
   搜尋 / 篩選
   ========================================================= */

.member-search {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  flex-wrap: wrap;
}

.search-input {
  flex: 1;
  min-width: 280px;
}

.filter-select {
  width: 160px;
  min-width: 150px;
  flex: none !important;
  background-color: white;
  cursor: pointer;
}

/* =========================================================
   表格控制列
   ========================================================= */

.table-control-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
}

.filter-summary {
  color: #8b8176;
  font-size: 13px;
}

.filter-summary strong {
  margin: 0 3px;
  color: #b58a46;
  font-size: 16px;
}

.total-hint {
  color: #999;
  font-size: 13px;
  margin-left: 6px;
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

.page-size-area {
  display: flex;
  align-items: center;
  gap: 7px;
  color: #756a60;
  font-size: 13px;
}

.page-size-select {
  padding: 6px 10px;
  border: 1px solid #d8d0c5;
  border-radius: 6px;
  background-color: white;
  cursor: pointer;
}

/* =========================================================
   Table
   ========================================================= */

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

.action-buttons {
  display: flex;
  gap: 7px;
  white-space: nowrap;
}

.loading-message,
.empty-message {
  padding: 35px;
  text-align: center;
  color: #888;
}

.row-selected {
  background-color: #fdf8ef !important;
}

/* =========================================================
   排序表頭
   ========================================================= */

.sortable {
  cursor: pointer;
  user-select: none;
  transition: background-color 0.2s;
}

.sortable:hover {
  background-color: #eee8df;
}

.sort-icon {
  display: inline-block;
  margin-left: 4px;
  color: #b58a46;
  font-size: 10px;
}

/* =========================================================
   狀態 Badge
   ========================================================= */

.status-badge {
  display: inline-block;
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
  white-space: nowrap;
}

.status-active {
  background-color: #e5f6eb;
  color: #257641;
}

.status-inactive {
  background-color: #fde9e7;
  color: #b3443c;
}

/* =========================================================
   快速啟用/停用按鈕
   ========================================================= */

.admin-btn-status {
  border: none;
  background-color: #edf2fb;
  color: #3e6091;
}

.admin-btn-status:hover {
  background-color: #dce7f7;
}

/* =========================================================
   分頁
   ========================================================= */

.pagination-area {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  margin-top: 24px;
  padding-top: 20px;
  border-top: 1px solid #eee7de;
}

.pagination-info {
  color: #857a70;
  font-size: 13px;
}

.pagination-info strong {
  color: #9b7435;
}

.pagination {
  display: flex;
  align-items: center;
  gap: 6px;
}

.page-button {
  min-width: 36px;
  height: 36px;
  padding: 0 10px;
  border: 1px solid #ded5c9;
  border-radius: 6px;
  background-color: white;
  color: #625649;
  cursor: pointer;
  transition: 0.2s;
}

.page-button:hover:not(:disabled) {
  border-color: #b58a46;
  color: #9b7435;
}

.page-button.active {
  border-color: #b58a46;
  background-color: #b58a46;
  color: white;
  font-weight: bold;
}

.page-button:disabled {
  background-color: #f2f0ec;
  color: #bbb5ad;
  cursor: not-allowed;
}

/* =========================================================
   Modal 共用背景與卡片 (與 ProductManageView 一致)
   ========================================================= */

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

.member-manage-card {
  width: min(650px, 94vw);
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: white;
  border-radius: 14px;
  box-shadow: 0 18px 55px rgba(0, 0, 0, 0.25);
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

.member-modal-header p {
  margin: 5px 0 0;
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
}

.modal-close {
  padding: 0 6px;
  border: none;
  background: transparent;
  color: white;
  font-size: 28px;
  line-height: 1;
  cursor: pointer;
}

.member-form {
  padding: 22px 24px;
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

.member-modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 15px 24px 20px;
  border-top: 1px solid #eee8df;
}

/* =========================================================
   匯出 / 匯入 Modal 專用
   ========================================================= */

.export-modal-card,
.import-modal-card {
  width: min(620px, 94vw);
}

.member-modal-body {
  padding: 22px 24px;
  overflow-y: auto;
  max-height: calc(85vh - 140px);
}

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

.file-dropzone:hover,
.file-dropzone.dropzone-active {
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

/* =========================================================
   RWD 響應式佈局 (與 ProductManageView 一致)
   ========================================================= */

@media (max-width: 800px) {
  .search-input {
    width: 100%;
    flex-basis: 100%;
  }

  .table-control-bar {
    align-items: flex-start;
    flex-direction: column;
  }
}

@media (max-width: 700px) {
  .admin-page-header {
    align-items: stretch;
    flex-direction: column;
  }

  .member-header-actions {
    width: 100%;
  }

  .member-header-actions .admin-btn {
    flex: 1;
    text-align: center;
  }

  .member-search {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input,
  .filter-select {
    width: 100%;
    min-width: 0;
  }

  .pagination-area {
    flex-direction: column;
    align-items: flex-start;
  }

  .pagination {
    max-width: 100%;
    overflow-x: auto;
    padding-bottom: 5px;
  }

  .action-buttons {
    flex-direction: column;
  }
}
</style>
