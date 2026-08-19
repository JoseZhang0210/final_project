/**
 * 房務任務管理系統 JavaScript
 * 遵循原則：DRY, SRP, camelCase 命名規範, 防禦性程式設計
 */

// 1. 全域設定與常數定義
const API_BASE_URL = "/api/roomtasks";

const TASK_CONFIG = {
  TYPES: {
    1: "退房清潔",
    2: "續住清潔",
    3: "設備維修",
    4: "補充備品",
  },
  PRIORITIES: {
    1: '<span class="badge bg-secondary">低</span>',
    2: '<span class="badge bg-primary">普通</span>',
    3: '<span class="badge bg-danger">緊急</span>',
  },
  STATUSES: {
    1: '<span class="badge bg-warning text-dark">待處理</span>',
    2: '<span class="badge bg-info text-dark">進行中</span>',
    3: '<span class="badge bg-success">已完成</span>',
    4: '<span class="badge bg-dark">已取消</span>',
  },
};

// 2. 狀態管理
let modalInstance = null;
let deleteModalInstance = null;
let roomTaskList = []; // 暫存全量資料
let isLocked = true; // 檢視 Modal 鎖定狀態
let pendingDeleteId = null; // 待刪除的任務 ID

// 3. 初始化 DOM 事件
document.addEventListener("DOMContentLoaded", () => {
  initModals();
  loadRoomTasks();
});

function initModals() {
  const modalElem = document.getElementById("roomTaskModal");
  const deleteModalElem = document.getElementById("deleteConfirmModal");

  if (modalElem) modalInstance = new bootstrap.Modal(modalElem);
  if (deleteModalElem)
    deleteModalInstance = new bootstrap.Modal(deleteModalElem);
}

// 4. API 溝通層 (Data Access)
async function apiRequest(url, method = "GET", body = null) {
  const options = {
    method,
    headers: { "Content-Type": "application/json" },
  };
  if (body) options.body = JSON.stringify(body);

  const response = await fetch(url, options);
  const data = await response.json().catch(() => ({}));

  if (!response.ok) {
    throw new Error(data.message || `請求失敗 (HTTP ${response.status})`);
  }
  return data;
}

// 取得所有任務列表
async function loadRoomTasks() {
  try {
    hideAlert("alertError");
    roomTaskList = await apiRequest(API_BASE_URL);
    applyFilters();
  } catch (err) {
    showAlert("alertError", err.message);
  }
}

// 儲存任務 (POST / PUT)
async function saveRoomTask(e) {
  e.preventDefault();
  if (isLocked) return;

  const id = document.getElementById("taskId").value;
  const payload = getFormData();

  const method = id ? "PUT" : "POST";
  const url = id ? `${API_BASE_URL}/${id}` : API_BASE_URL;

  try {
    await apiRequest(url, method, payload);
    modalInstance.hide();
    showAlert("alertSuccess", id ? "任務更新成功！" : "新增任務成功！");
    loadRoomTasks();
  } catch (err) {
    showAlert("alertError", err.message);
  }
}

// 執行刪除 (DELETE)
async function executeDelete() {
  if (!pendingDeleteId) return;

  try {
    await apiRequest(`${API_BASE_URL}/${pendingDeleteId}`, "DELETE");
    deleteModalInstance.hide();
    showAlert("alertSuccess", "任務已成功刪除！");
    pendingDeleteId = null;
    loadRoomTasks();
  } catch (err) {
    deleteModalInstance.hide();
    showAlert("alertError", err.message);
  }
}

// 5. 搜尋與篩選邏輯
function applyFilters() {
  const keyword = getInputValue("searchInput").toLowerCase();
  const selectedType = getInputValue("taskTypeFilter");
  const selectedPriority = getInputValue("priorityFilter");
  const selectedStatus = getInputValue("statusFilter");

  const filteredList = roomTaskList.filter((item) => {
    const matchKeyword =
      !keyword ||
      (item.remark && item.remark.toLowerCase().includes(keyword)) ||
      String(item.taskId).includes(keyword) ||
      String(item.roomId).includes(keyword) ||
      String(item.employeeId).includes(keyword);

    const matchType = !selectedType || String(item.taskType) === selectedType;
    const matchPriority =
      !selectedPriority || String(item.priority) === selectedPriority;
    const matchStatus =
      !selectedStatus || String(item.taskStatus) === selectedStatus;

    return matchKeyword && matchType && matchPriority && matchStatus;
  });

  renderTable(filteredList);
}

function handleSearch(e) {
  if (e) e.preventDefault();
  applyFilters();
}

function resetSearch() {
  setInputValue("searchInput", "");
  setInputValue("taskTypeFilter", "");
  setInputValue("priorityFilter", "");
  setInputValue("statusFilter", "");
  renderTable(roomTaskList);
}

// 6. 渲染與 UI 控制
function renderTable(list) {
  const tbody = document.getElementById("roomTaskTableBody");
  if (!tbody) return;

  if (!list || list.length === 0) {
    tbody.innerHTML = `<tr><td colspan="10" class="text-center py-4 text-muted">沒有符合條件的任務資料</td></tr>`;
    return;
  }

  tbody.innerHTML = list
    .map(
      (item) => `
        <tr>
            <td class="ps-3 fw-bold">${item.taskId}</td>
            <td class="fw-bold text-primary">房號 ${item.roomId}</td>
            <td>員工 #${item.employeeId}</td>
            <td>${TASK_CONFIG.TYPES[item.taskType] || item.taskType}</td>
            <td>${TASK_CONFIG.PRIORITIES[item.priority] || item.priority}</td>
            <td>${TASK_CONFIG.STATUSES[item.taskStatus] || item.taskStatus}</td>
            <td class="small">${formatDate(item.createdAt)}</td>
            <td class="small">${formatDate(item.completedAt)}</td>
            <td class="text-muted text-truncate" style="max-width: 180px;" title="${escapeHtml(item.remark)}">
                ${escapeHtml(item.remark)}
            </td>
            <td class="text-center">
                <button type="button" class="btn btn-sm btn-outline-primary me-1" onclick="onEditBtnClick(${item.taskId})">
                    <i class="fa-solid fa-pen"></i> 編輯
                </button>
                <button type="button" class="btn btn-sm btn-outline-danger" onclick="openDeleteConfirm(${item.taskId})">
                    <i class="fa-solid fa-trash"></i> 刪除
                </button>
            </td>
        </tr>
    `,
    )
    .join("");
}

// Modal 編輯模式鎖定 / 解鎖
function setLockState(locked) {
  isLocked = locked;
  const fields = document.querySelectorAll(".editable-field");
  const lockIcon = document.getElementById("lockIcon");
  const lockText = document.getElementById("lockText");
  const lockBtn = document.getElementById("lockToggleBtn");
  const submitBtn = document.getElementById("btnSubmit");
  const hint = document.getElementById("readonlyHint");

  fields.forEach((field) => field.toggleAttribute("disabled", isLocked));

  if (lockIcon)
    lockIcon.className = isLocked
      ? "fa-solid fa-lock me-1"
      : "fa-solid fa-lock-open me-1";
  if (lockText) lockText.innerText = isLocked ? "點擊解鎖" : "編輯中";
  if (lockBtn)
    lockBtn.className = isLocked
      ? "btn btn-warning btn-sm fw-bold"
      : "btn btn-success btn-sm fw-bold";
  if (submitBtn) submitBtn.classList.toggle("d-none", isLocked);
  if (hint) hint.classList.toggle("d-none", !isLocked);
}

function toggleLock() {
  setLockState(!isLocked);
}

// 表單操作
function clearForm() {
  document.getElementById("modalTitle").innerText = "新增房務任務";
  document.getElementById("roomTaskForm").reset();
  setInputValue("taskId", "");
  setInputValue("createdAt", new Date().toISOString().slice(0, 16));

  document.getElementById("lockToggleBtn").classList.add("d-none");
  setLockState(false);
}

function onEditBtnClick(id) {
  const item = roomTaskList.find((r) => r.taskId === id);
  if (!item) return;

  document.getElementById("modalTitle").innerText = "查看 / 編輯房務任務";
  setInputValue("taskId", item.taskId);
  setInputValue("roomId", item.roomId);
  setInputValue("employeeId", item.employeeId);
  setInputValue("taskType", item.taskType || "1");
  setInputValue("priority", item.priority || "2");
  setInputValue("taskStatus", item.taskStatus || "1");
  setInputValue("createdAt", formatToInputDate(item.createdAt));
  setInputValue("completedAt", formatToInputDate(item.completedAt));
  setInputValue("remark", item.remark || "");

  document.getElementById("lockToggleBtn").classList.remove("d-none");
  setLockState(true);
  modalInstance.show();
}

function openDeleteConfirm(id) {
  const item = roomTaskList.find((r) => r.taskId === id);
  if (!item) return;

  pendingDeleteId = id;
  const taskTypeName = TASK_CONFIG.TYPES[item.taskType] || "任務";
  document.getElementById("deleteTargetInfo").innerText =
    `[任務 ID: ${item.taskId}] 房間 ${item.roomId} - ${taskTypeName}`;
  deleteModalInstance.show();
}

// 7. 工具函式 (Helper Functions)
function getFormData() {
  const id = getInputValue("taskId");
  return {
    taskId: id ? parseInt(id) : null,
    roomId: parseInt(getInputValue("roomId")),
    employeeId: parseInt(getInputValue("employeeId")),
    taskType: getInputValue("taskType"),
    priority: getInputValue("priority"),
    taskStatus: getInputValue("taskStatus"),
    createdAt: getInputValue("createdAt") || null,
    completedAt: getInputValue("completedAt") || null,
    remark: getInputValue("remark"),
  };
}

function getInputValue(id) {
  const elem = document.getElementById(id);
  return elem ? elem.value.trim() : "";
}

function setInputValue(id, val) {
  const elem = document.getElementById(id);
  if (elem) elem.value = val ?? "";
}

function showAlert(id, msg) {
  const alertElem = document.getElementById(id);
  const msgElem = document.getElementById(`${id}Msg`);
  if (msgElem) msgElem.innerText = msg;
  if (alertElem) alertElem.classList.remove("d-none");
}

function hideAlert(id) {
  const alertElem = document.getElementById(id);
  if (alertElem) alertElem.classList.add("d-none");
}

function formatDate(dateStr) {
  if (!dateStr) return "-";
  const dt = new Date(dateStr);
  return isNaN(dt.getTime())
    ? dateStr
    : dt.toLocaleString("zh-TW", { hour12: false });
}

function formatToInputDate(dateStr) {
  if (!dateStr) return "";
  const dt = new Date(dateStr);
  return isNaN(dt.getTime()) ? "" : dt.toISOString().slice(0, 16);
}

function escapeHtml(str) {
  return String(str || "").replace(/[&<>"']/g, function (m) {
    return {
      "&": "&amp;",
      "<": "&lt;",
      ">": "&gt;",
      '"': "&quot;",
      "'": "&#39;",
    }[m];
  });
}
