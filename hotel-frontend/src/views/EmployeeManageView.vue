<script setup>
import { computed, onMounted, reactive, ref } from "vue";

// =====================================================
// API 端點
// =====================================================

const API_URL = "/api/employees";
const DEPT_API_URL = "/api/departments";

// 系統預設基礎部門（若 department 資料表完全為空時自動建立）
const DEFAULT_DEPARTMENTS = ["櫃檯部", "客房部", "餐飲部", "行政部"];

// =====================================================
// 資料狀態
// =====================================================

const departments = ref([]);

const employees = ref([]);

const keyword = ref("");

const selectedDepartment = ref("");

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

const editingEmployeeId = ref(null);

// =====================================================
// 表單
// =====================================================

const form = reactive({
  username: "",
  password: "",
  status: "1",
  departmentId: "",
  customDepartmentName: "",
  position: "",
  isAdmin: false,
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
// 狀態與工具輔助
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

function getDepartmentName(deptId, deptName) {
  if (deptName) return deptName;
  const dept = departments.value.find((d) => d.id === Number(deptId));
  return dept ? dept.name : "未指定";
}

// =====================================================
// 部門動態載入與自動建立邏輯
// =====================================================

// 1. 讀取部門列表（若資料庫為空則自動補齊預設部門）
async function loadDepartments() {
  try {
    const response = await fetch(DEPT_API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    if (response.ok) {
      const data = await response.json();
      if (Array.isArray(data) && data.length > 0) {
        departments.value = data.map((d) => ({
          id: d.departmentId ?? d.id,
          name: d.departmentName ?? d.name,
        }));
        return;
      }
    }

    // 若後端回傳空陣列（代表 department table 尚未有任何資料），自動建立預設部門
    console.log("偵測到部門資料表為空，自動建立預設部門...");
    await autoSeedDefaultDepartments();
  } catch (error) {
    console.error("載入部門失敗，使用備用預設部門：", error);
    if (departments.value.length === 0) {
      departments.value = DEFAULT_DEPARTMENTS.map((name, idx) => ({
        id: idx + 1,
        name,
      }));
    }
  }
}

// 2. 自動在 department table 建立預設部門
async function autoSeedDefaultDepartments() {
  const createdList = [];
  for (const name of DEFAULT_DEPARTMENTS) {
    try {
      const res = await fetch(DEPT_API_URL, {
        method: "POST",
        headers: getAuthHeaders(),
        body: JSON.stringify({ departmentName: name }),
      });
      if (res.ok) {
        const item = await res.json();
        createdList.push({
          id: item.departmentId ?? item.id,
          name: item.departmentName ?? item.name ?? name,
        });
      }
    } catch (e) {
      console.error(`自動建立預設部門 ${name} 失敗：`, e);
    }
  }

  if (createdList.length > 0) {
    departments.value = createdList;
  } else {
    departments.value = DEFAULT_DEPARTMENTS.map((name, idx) => ({
      id: idx + 1,
      name,
    }));
  }
}

// 3. 確保指定名稱之部門存在於 department table 中（若無則自動呼叫 API 建立）
async function ensureDepartmentExists(deptName) {
  if (!deptName || !deptName.trim()) {
    return null;
  }

  const cleanName = deptName.trim();

  // 先在既有列表檢查
  const found = departments.value.find(
    (d) => d.name.toLowerCase() === cleanName.toLowerCase()
  );
  if (found) {
    return found.id;
  }

  // 若不存在，立即呼叫 POST /api/departments 建立
  try {
    const res = await fetch(DEPT_API_URL, {
      method: "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify({ departmentName: cleanName }),
    });

    if (res.ok) {
      const newDept = await res.json();
      const newId = newDept.departmentId ?? newDept.id;
      const newName = newDept.departmentName ?? newDept.name ?? cleanName;

      // 加入前端列表
      departments.value.push({ id: newId, name: newName });
      return newId;
    }
  } catch (err) {
    console.error("自動建立新部門失敗：", err);
  }

  return null;
}

// =====================================================
// 多條件搜尋與篩選
// =====================================================

const filteredEmployees = computed(() => {
  const search = keyword.value.trim().toLowerCase();
  const deptFilter = selectedDepartment.value;
  const statusFilter = selectedStatus.value;

  return employees.value.filter((emp) => {
    // 關鍵字搜尋
    const matchesKeyword =
      !search ||
      (emp.username || "").toLowerCase().includes(search) ||
      (emp.name || "").toLowerCase().includes(search) ||
      (emp.position || "").toLowerCase().includes(search) ||
      (emp.email || "").toLowerCase().includes(search) ||
      (emp.phone || "").includes(search) ||
      (emp.departmentName || "").toLowerCase().includes(search);

    // 部門篩選
    const matchesDept =
      !deptFilter || String(emp.departmentId) === String(deptFilter);

    // 狀態篩選
    const matchesStatus =
      !statusFilter || String(emp.status) === String(statusFilter);

    return matchesKeyword && matchesDept && matchesStatus;
  });
});

// =====================================================
// 總頁數
// =====================================================

const totalPages = computed(() => {
  return Math.max(1, Math.ceil(filteredEmployees.value.length / pageSize));
});

// =====================================================
// 當前頁資料
// =====================================================

const pagedEmployees = computed(() => {
  if (currentPage.value > totalPages.value) {
    currentPage.value = totalPages.value;
  }

  const start = (currentPage.value - 1) * pageSize;

  return filteredEmployees.value.slice(start, start + pageSize);
});

// =====================================================
// 篩選後回第一頁
// =====================================================

function resetPage() {
  currentPage.value = 1;
}

// =====================================================
// 讀取員工資料
// GET /api/employees
// =====================================================

async function loadEmployees() {
  loading.value = true;

  try {
    const response = await fetch(API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    console.log("員工 API status：", response.status);

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有員工管理權限", "error");
      return;
    }

    if (!response.ok) {
      showMessage("取得員工資料失敗", "error");
      return;
    }

    const contentType = response.headers.get("content-type") || "";

    if (!contentType.includes("application/json")) {
      const text = await response.text();
      console.error("員工 API 回傳的不是 JSON：", text);
      showMessage("員工 API 回傳的不是 JSON，請檢查 proxy 或後端路徑", "error");
      return;
    }

    employees.value = await response.json();
    console.log("員工資料：", employees.value);
    currentPage.value = 1;
  } catch (error) {
    console.error("員工讀取錯誤：", error);
    showMessage("讀取員工資料失敗", "error");
  } finally {
    loading.value = false;
  }
}

// =====================================================
// 新增 Modal
// =====================================================

function openCreateModal() {
  editingEmployeeId.value = null;

  form.username = "";
  form.password = "";
  form.status = "1";
  form.departmentId = departments.value.length > 0 ? departments.value[0].id : "__NEW__";
  form.customDepartmentName = "";
  form.position = "";
  form.isAdmin = false;
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

function openEditModal(employee) {
  editingEmployeeId.value = employee.employeeId ?? employee.id;

  form.username = employee.username || "";
  form.password = "";
  form.status = employee.status || "1";

  // 部門匹配處理
  const matchedDept = departments.value.find(
    (d) => d.id === employee.departmentId || (employee.departmentName && d.name === employee.departmentName)
  );

  if (matchedDept) {
    form.departmentId = matchedDept.id;
    form.customDepartmentName = "";
  } else if (employee.departmentName) {
    form.departmentId = "__NEW__";
    form.customDepartmentName = employee.departmentName;
  } else {
    form.departmentId = departments.value.length > 0 ? departments.value[0].id : "__NEW__";
    form.customDepartmentName = "";
  }

  form.position = employee.position || "";
  form.isAdmin = employee.isAdmin === true || employee.admin === true;
  form.name = employee.name || "";
  form.email = employee.email || "";
  form.phone = employee.phone || "";
  form.gender = employee.gender || "男";
  form.birthday = employee.birthday || "";
  form.zipcode = employee.zipcode || "";
  form.city = employee.city || "";
  form.district = employee.district || "";
  form.address = employee.address || "";

  modalOpen.value = true;
}

// =====================================================
// 關閉 Modal
// =====================================================

function closeModal() {
  modalOpen.value = false;
  editingEmployeeId.value = null;
}

// =====================================================
// 新增 / 修改員工
// POST /api/employees
// PUT  /api/employees/{id}
// =====================================================

async function saveEmployee() {
  const username = form.username.trim();
  const password = form.password.trim();

  if (!username) {
    showMessage("帳號不能為空", "error");
    return;
  }

  // 處理部門（若選擇自訂或 table 缺少對應值時自動建立）
  let finalDepartmentId = null;
  let finalDepartmentName = "";

  if (form.departmentId === "__NEW__" || !form.departmentId) {
    if (!form.customDepartmentName || !form.customDepartmentName.trim()) {
      showMessage("請輸入自訂部門名稱", "error");
      return;
    }
    finalDepartmentName = form.customDepartmentName.trim();
    // 自動在 department table 建立該部門
    finalDepartmentId = await ensureDepartmentExists(finalDepartmentName);
  } else {
    finalDepartmentId = Number(form.departmentId);
    const deptObj = departments.value.find((d) => d.id === finalDepartmentId);
    finalDepartmentName = deptObj ? deptObj.name : "";
  }

  saving.value = true;

  const isEditing = editingEmployeeId.value !== null;

  const payload = {
    username: username,
    status: form.status,
    departmentId: finalDepartmentId,
    departmentName: finalDepartmentName,
    position: form.position.trim(),
    isAdmin: form.isAdmin,
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
    const url = isEditing ? `${API_URL}/${editingEmployeeId.value}` : API_URL;
    const method = isEditing ? "PUT" : "POST";

    const response = await fetch(url, {
      method: method,
      headers: getAuthHeaders(),
      body: JSON.stringify(payload),
    });

    console.log("員工儲存 status：", response.status);

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有員工管理權限", "error");
      return;
    }

    if (response.status === 409) {
      showMessage("使用者帳號已存在", "error");
      return;
    }

    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      showMessage(data.message || "儲存員工失敗", "error");
      return;
    }

    closeModal();
    showMessage(isEditing ? "員工資料修改成功" : "員工新增成功", "success");

    // 同步重新讀取部門與員工列表
    await loadDepartments();
    await loadEmployees();
  } catch (error) {
    console.error("員工儲存錯誤：", error);
    showMessage("儲存員工失敗", "error");
  } finally {
    saving.value = false;
  }
}

// =====================================================
// 啟用 / 停用
// PATCH /api/employees/{id}/status
// =====================================================

async function toggleStatus(employee) {
  if (!employee) return;

  const employeeId = employee.employeeId ?? employee.id;
  if (!employeeId) {
    console.error("無法取得員工 ID：", employee);
    showMessage("無法取得員工 ID", "error");
    return;
  }

  const nextStatus = isActiveStatus(employee.status) ? "0" : "1";

  try {
    const response = await fetch(
      `${API_URL}/${employeeId}/status?status=${nextStatus}`,
      {
        method: "PATCH",
        headers: getAuthHeaders(),
      }
    );

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有員工管理權限", "error");
      return;
    }

    if (!response.ok) {
      showMessage("更新員工狀態失敗", "error");
      return;
    }

    showMessage("員工狀態已更新", "success");
    await loadEmployees();
  } catch (error) {
    console.error("員工狀態更新錯誤：", error);
    showMessage("更新員工狀態失敗", "error");
  }
}

// =====================================================
// 刪除員工
// DELETE /api/employees/{id}
// =====================================================

async function deleteEmployee(employeeOrId) {
  const employeeId =
    typeof employeeOrId === "object" && employeeOrId !== null
      ? employeeOrId.employeeId ?? employeeOrId.id
      : employeeOrId;

  if (!employeeId) {
    showMessage("無法取得員工 ID", "error");
    return;
  }

  if (!window.confirm("確定要刪除此員工嗎？此動作將連動刪除帳號與個人檔案。")) {
    return;
  }

  try {
    const response = await fetch(`${API_URL}/${employeeId}`, {
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
        data.message || "無法刪除：該員工已有相關業務紀錄，建議將狀態改為停用",
        "error"
      );
      return;
    }

    if (!response.ok) {
      showMessage("刪除員工失敗", "error");
      return;
    }

    showMessage("員工已刪除", "success");
    await loadEmployees();
  } catch (error) {
    console.error("刪除員工錯誤：", error);
    showMessage("刪除員工失敗", "error");
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
// 初始化
// =====================================================

onMounted(async () => {
  console.log("員工頁 JWT：", localStorage.getItem("token"));
  await loadDepartments();
  await loadEmployees();
});
</script>

<template>
  <div class="employee-page">
    <!-- 頁面標題 -->
    <div class="admin-page-header">
      <div>
        <h1>員工管理</h1>
        <p>管理員工帳號、職位部門、個人資料與啟用狀態</p>
      </div>
    </div>

    <!-- =========================
         員工列表
         ========================= -->
    <section class="admin-card">
      <div class="employee-list-header">
        <div>
          <h2>員工列表</h2>
          <p>可搜尋、篩選部門、新增、修改、啟用、停用與刪除員工</p>
        </div>

        <div class="employee-toolbar">
          <!-- 部門篩選 -->
          <select
            v-model="selectedDepartment"
            class="admin-input filter-select"
            @change="resetPage"
          >
            <option value="">全部部門</option>
            <option
              v-for="dept in departments"
              :key="dept.id"
              :value="dept.id"
            >
              {{ dept.name }}
            </option>
          </select>

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
            class="admin-input employee-search"
            placeholder="搜尋帳號、姓名、職位..."
            @input="resetPage"
          />

          <button
            type="button"
            class="admin-btn admin-btn-primary"
            @click="openCreateModal"
          >
            ＋ 新增員工
          </button>

          <button
            type="button"
            class="admin-btn admin-btn-secondary"
            @click="() => { loadDepartments(); loadEmployees(); }"
          >
            重新整理
          </button>
        </div>
      </div>

      <!-- 訊息提示 -->
      <div v-if="message" class="admin-message" :class="messageType">
        {{ message }}
      </div>

      <!-- 統計摘要 -->
      <div class="employee-summary">
        共
        <strong>{{ filteredEmployees.length }}</strong>
        位員工
        <span v-if="filteredEmployees.length !== employees.length" class="total-hint">
          （全體共 {{ employees.length }} 位）
        </span>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-message">員工資料讀取中...</div>

      <!-- 表格 -->
      <div v-else class="admin-table-wrapper">
        <table class="admin-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>員工姓名 / 帳號</th>
              <th>部門</th>
              <th>職稱</th>
              <th>角色權限</th>
              <th>聯絡電話 / 信箱</th>
              <th>狀態</th>
              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="pagedEmployees.length === 0">
              <td colspan="8" class="empty-row">目前沒有符合條件的員工資料</td>
            </tr>

            <tr
              v-for="employee in pagedEmployees"
              :key="employee.employeeId ?? employee.id"
            >
              <td>{{ employee.employeeId ?? employee.id }}</td>

              <td>
                <div class="employee-name-cell">
                  <span class="employee-name">{{ employee.name || "未填姓名" }}</span>
                  <span class="employee-username">(@{{ employee.username }})</span>
                </div>
              </td>

              <td>
                <span class="department-tag">
                  {{ getDepartmentName(employee.departmentId, employee.departmentName) }}
                </span>
              </td>

              <td>
                <span class="position-text">{{ employee.position || "—" }}</span>
              </td>

              <td>
                <span
                  class="role-badge"
                  :class="employee.isAdmin ? 'role-admin' : 'role-staff'"
                >
                  {{ employee.isAdmin ? "👑 管理員" : "一般員工" }}
                </span>
              </td>

              <td>
                <div class="contact-info">
                  <div v-if="employee.phone" class="contact-item">
                    📞 {{ employee.phone }}
                  </div>
                  <div v-if="employee.email" class="contact-item">
                    ✉️ {{ employee.email }}
                  </div>
                  <span v-if="!employee.phone && !employee.email" class="text-muted">
                    未填寫
                  </span>
                </div>
              </td>

              <td>
                <span
                  class="status-badge"
                  :class="isActiveStatus(employee.status) ? 'status-active' : 'status-inactive'"
                >
                  {{ getStatusLabel(employee.status) }}
                </span>
              </td>

              <td>
                <div class="employee-actions">
                  <button
                    type="button"
                    class="admin-btn admin-btn-edit"
                    @click="openEditModal(employee)"
                  >
                    修改
                  </button>

                  <button
                    type="button"
                    class="admin-btn"
                    :class="isActiveStatus(employee.status) ? 'status-disable-btn' : 'status-enable-btn'"
                    @click="toggleStatus(employee)"
                  >
                    {{ isActiveStatus(employee.status) ? "停用" : "啟用" }}
                  </button>

                  <button
                    type="button"
                    class="admin-btn admin-btn-delete"
                    @click="deleteEmployee(employee)"
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
      <div class="employee-pagination">
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
    <div v-if="modalOpen" class="employee-modal" @click.self="closeModal">
      <div class="employee-modal-card">
        <div class="employee-modal-header">
          <h2>
            {{ editingEmployeeId === null ? "新增員工" : "修改員工" }}
          </h2>

          <button type="button" class="modal-close" @click="closeModal">
            ×
          </button>
        </div>

        <form class="employee-form" @submit.prevent="saveEmployee">
          <!-- 區塊 1: 帳號與權限 -->
          <div class="form-section-title">🔐 帳號與權限</div>
          <div class="admin-form-grid">
            <div class="admin-form-group">
              <label> 使用者帳號 <span class="required">*</span> </label>
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
                :placeholder="editingEmployeeId === null ? '若不填則預設 123456' : '留空表示不修改密碼'"
              />
            </div>

            <div class="admin-form-group">
              <label> 帳號狀態 </label>
              <select v-model="form.status">
                <option value="1">啟用</option>
                <option value="0">停用</option>
              </select>
            </div>

            <div class="admin-form-group">
              <label> 權限類型 </label>
              <select v-model="form.isAdmin">
                <option :value="false">一般員工</option>
                <option :value="true">管理員 (Admin)</option>
              </select>
            </div>
          </div>

          <!-- 區塊 2: 職務與部門 -->
          <div class="form-section-title">🏢 職務與部門</div>
          <div class="admin-form-grid">
            <div class="admin-form-group" :class="{ 'full-width': form.departmentId !== '__NEW__' }">
              <label> 所屬部門 </label>
              <select v-model="form.departmentId">
                <option
                  v-for="dept in departments"
                  :key="dept.id"
                  :value="dept.id"
                >
                  {{ dept.name }}
                </option>
                <option value="__NEW__">＋ 自訂/新增部門...</option>
              </select>
            </div>

            <!-- 當選擇自訂部門時，展開輸入框 -->
            <div v-if="form.departmentId === '__NEW__'" class="admin-form-group">
              <label> 自訂新部門名稱 <span class="required">*</span> </label>
              <input
                v-model="form.customDepartmentName"
                type="text"
                placeholder="請輸入新部門名稱 (例：資訊部)"
                required
              />
            </div>

            <div class="admin-form-group full-width">
              <label> 職稱 </label>
              <input
                v-model="form.position"
                type="text"
                placeholder="例：經理、櫃檯人員、房務人員、工程師"
              />
            </div>
          </div>

          <!-- 區塊 3: 基本個人資料 -->
          <div class="form-section-title">👤 個人基本資料</div>
          <div class="admin-form-grid">
            <div class="admin-form-group">
              <label> 員工姓名 </label>
              <input
                v-model="form.name"
                type="text"
                placeholder="請輸入姓名"
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
                placeholder="例：employee@hotel.com"
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
  </div>
</template>

<style scoped>
.employee-page {
  width: 100%;
}

/* =========================
   Header
   ========================= */

.employee-list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  margin-bottom: 20px;
  flex-wrap: wrap;
}

.employee-list-header h2 {
  margin: 0 0 6px;
  color: #6f5328;
}

.employee-list-header p {
  margin: 0;
  color: #777;
  font-size: 14px;
}

/* =========================
   Toolbar
   ========================= */

.employee-toolbar {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.filter-select {
  width: 130px;
  padding: 8px 12px;
  border-radius: 8px;
  border: 1px solid #ddd;
  background-color: white;
  color: #4a3b2a;
}

.employee-search {
  width: 220px;
}

/* =========================
   Summary
   ========================= */

.employee-summary {
  margin-bottom: 18px;
  color: #6d6258;
  font-size: 14px;
}

.employee-summary strong {
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

.employee-name-cell {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.employee-name {
  color: #5b4632;
  font-weight: bold;
  font-size: 15px;
}

.employee-username {
  color: #8c7b6d;
  font-size: 12px;
}

.department-tag {
  display: inline-block;
  padding: 4px 10px;
  background-color: #f3ede2;
  color: #6f5328;
  border-radius: 6px;
  font-size: 13px;
  font-weight: 600;
}

.position-text {
  color: #444;
  font-weight: 500;
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
   Role Badge
   ========================= */

.role-badge {
  display: inline-block;
  padding: 4px 9px;
  border-radius: 999px;
  font-size: 12px;
  font-weight: bold;
}

.role-admin {
  background-color: #fff4e5;
  color: #b25e02;
  border: 1px solid #ffd8a8;
}

.role-staff {
  background-color: #f1f3f5;
  color: #495057;
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

.employee-actions {
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

.employee-pagination {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  gap: 12px;
  margin-top: 20px;
}

.employee-pagination span {
  min-width: 70px;
  text-align: center;
  color: #6f6256;
  font-weight: bold;
}

.employee-pagination button:disabled {
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

.employee-modal {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background-color: rgba(47, 42, 36, 0.55);
}

.employee-modal-card {
  width: min(680px, 94vw);
  max-height: 90vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: white;
  border-radius: 14px;
  box-shadow: 0 16px 50px rgba(0, 0, 0, 0.22);
}

.employee-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 24px;
  background-color: #4a3b2a;
  color: white;
  flex-shrink: 0;
}

.employee-modal-header h2 {
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

.employee-form {
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

@media (max-width: 768px) {
  .employee-list-header {
    align-items: stretch;
    flex-direction: column;
  }

  .employee-toolbar {
    align-items: stretch;
    flex-direction: column;
  }

  .filter-select,
  .employee-search {
    width: 100%;
  }

  .employee-toolbar .admin-btn {
    width: 100%;
  }

  .employee-actions {
    flex-direction: column;
  }

  .employee-pagination {
    justify-content: center;
  }

  .admin-form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
