const API_BASE_URL = "/api/booking-orders";
let modalInstance = null;
let deleteModalInstance = null;
let bookingOrderList = []; // 快取完整訂單資料陣列
let isLocked = true; // 狀態控制：true = 唯讀, false = 可編輯
let pendingDeleteId = null; // 當前準備刪除的訂單 ID

document.addEventListener("DOMContentLoaded", () => {
  // 1. 初始化 Bootstrap Modal 實例
  const bookingModalEl = document.getElementById("bookingOrderModal");
  const deleteModalEl = document.getElementById("deleteConfirmModal");

  if (bookingModalEl) modalInstance = new bootstrap.Modal(bookingModalEl);
  if (deleteModalEl) deleteModalInstance = new bootstrap.Modal(deleteModalEl);

  // 2. 綁定按鈕與表單事件
  initEventListeners();

  // 3. 載入初始資料
  loadBookingOrders();
});

// 事件監聽初始化
function initEventListeners() {
  // 側邊欄 Toggle
  const sidebarToggle = document.getElementById("sidebarToggle");
  if (sidebarToggle) {
    sidebarToggle.addEventListener("click", () => {
      document.getElementById("wrapper")?.classList.toggle("toggled");
    });
  }

  // 搜尋與重設表單
  const searchForm = document.getElementById("searchForm");
  if (searchForm) {
    searchForm.addEventListener("submit", handleSearch);
  }

  const resetSearchBtn = document.getElementById("resetSearchBtn");
  if (resetSearchBtn) {
    resetSearchBtn.addEventListener("click", resetSearch);
  }

  // 新增訂單按鈕
  const openAddModalBtn = document.getElementById("openAddModalBtn");
  if (openAddModalBtn) {
    openAddModalBtn.addEventListener("click", clearForm);
  }

  // 鎖頭切換按鈕
  const lockToggleBtn = document.getElementById("lockToggleBtn");
  if (lockToggleBtn) {
    lockToggleBtn.addEventListener("click", toggleLock);
  }

  // Modal 表單儲存提交
  const modalForm = document.getElementById("modalForm");
  if (modalForm) {
    modalForm.addEventListener("submit", saveBookingOrder);
  }

  // 刪除二次確認按鈕
  const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");
  if (confirmDeleteBtn) {
    confirmDeleteBtn.addEventListener("click", executeDelete);
  }

  // 尋找 bookingTotalPrice 欄位並加入防止負數監聽
  const priceInput = document.getElementById("bookingTotalPrice");
  if (priceInput) {
    priceInput.addEventListener("input", (e) => {
      if (parseFloat(e.target.value) < 0) {
        e.target.value = 0;
      }
    });
  }
}

// 1. 取得所有訂單資料 (GET)
async function loadBookingOrders() {
  try {
    hideAlert("alertError");
    hideAlert("alertSuccess");
    const response = await fetch(API_BASE_URL);
    if (!response.ok) throw new Error("無法取得訂單列表");
    bookingOrderList = await response.json();

    applyFilters();
  } catch (err) {
    showAlert("alertError", err.message);
  }
}

// 2. 搜尋與篩選邏輯
function applyFilters() {
  const keyword = document
    .getElementById("searchInput")
    ?.value.trim()
    .toLowerCase();
  const selectedStatus = document.getElementById("statusFilter")?.value;

  const filteredList = bookingOrderList.filter((item) => {
    const matchKeyword =
      !keyword ||
      String(item.bookingOrderId).includes(keyword) ||
      String(item.memberId).includes(keyword) ||
      (item.paymentId && String(item.paymentId).includes(keyword));

    const matchStatus = !selectedStatus || item.orderStatus === selectedStatus;

    return matchKeyword && matchStatus;
  });

  renderTable(filteredList);
}

function handleSearch(e) {
  if (e) e.preventDefault();
  applyFilters();
}

function resetSearch() {
  const searchInput = document.getElementById("searchInput");
  const statusFilter = document.getElementById("statusFilter");
  if (searchInput) searchInput.value = "";
  if (statusFilter) statusFilter.value = "";
  renderTable(bookingOrderList);
}

// 渲染表格資料
function renderTable(list) {
  const tbody = document.getElementById("bookingOrderTableBody");
  if (!tbody) return;

  if (!list || list.length === 0) {
    tbody.innerHTML = `<tr><td colspan="7" class="text-center py-4 text-muted">沒有符合條件的訂單資料</td></tr>`;
    return;
  }

  tbody.innerHTML = list
    .map(
      (item) => `
        <tr>
            <td class="ps-3 fw-bold">${item.bookingOrderId}</td>
            <td>${item.memberId}</td>
            <td class="text-success fw-bold">$${item.bookingTotalPrice}</td>
            <td>${getStatusBadge(item.orderStatus)}</td>
            <td>${item.paymentId ? item.paymentId : "無"}</td>
            <td class="text-muted">${formatDateTime(item.createdAt)}</td>
            <td class="text-center">
                <button type="button" class="btn btn-sm btn-outline-primary me-1" onclick="onEditBtnClick(${item.bookingOrderId})">
                    <i class="fa-solid fa-pen"></i> 編輯
                </button>
                <button type="button" class="btn btn-sm btn-outline-danger" onclick="openDeleteConfirm(${item.bookingOrderId})">
                    <i class="fa-solid fa-trash"></i> 刪除
                </button>
            </td>
        </tr>
      `,
    )
    .join("");
}

// 🔒 控制唯讀 / 可編輯狀態
function setLockState(locked) {
  isLocked = locked;
  const lockIcon = document.getElementById("lockIcon");
  const lockText = document.getElementById("lockStatusText");
  const lockBtn = document.getElementById("lockToggleBtn");
  const modalForm = document.getElementById("modalForm");
  const saveSubmitBtn = document.getElementById("saveSubmitBtn");

  if (!modalForm) return;

  const inputs = modalForm.querySelectorAll("input, textarea, select");

  if (isLocked) {
    // 🔒 鎖定 (唯讀狀態)
    inputs.forEach((input) => {
      input.setAttribute("readonly", "readonly");
      if (input.tagName === "SELECT")
        input.setAttribute("disabled", "disabled");
    });

    if (lockIcon) lockIcon.className = "fa-solid fa-lock";
    if (lockText) lockText.innerText = "唯讀中";
    if (lockBtn)
      lockBtn.className =
        "btn btn-sm btn-outline-danger d-flex align-items-center gap-1";
    if (saveSubmitBtn) saveSubmitBtn.disabled = true;
  } else {
    // 🔓 解鎖 (可編輯狀態)
    inputs.forEach((input) => {
      input.removeAttribute("readonly");
      input.removeAttribute("disabled");
    });

    if (lockIcon) lockIcon.className = "fa-solid fa-lock-open";
    if (lockText) lockText.innerText = "編輯中";
    if (lockBtn)
      lockBtn.className =
        "btn btn-sm btn-outline-success d-flex align-items-center gap-1";
    if (saveSubmitBtn) saveSubmitBtn.disabled = false;
  }
}

function toggleLock() {
  setLockState(!isLocked);
}

// 3. 新增 / 編輯訂單 (POST / PUT)
async function saveBookingOrder(e) {
  e.preventDefault();
  if (isLocked) return;

  const id = document.getElementById("bookingOrderId").value;
  const createdAtVal = document.getElementById("createdAt").value;

  // REST API JSON Payload
  const payload = {
    bookingOrderId: id ? parseInt(id) : null,
    memberId: parseInt(document.getElementById("memberId").value),
    bookingTotalPrice: parseFloat(
      document.getElementById("bookingTotalPrice").value,
    ),
    orderStatus: document.getElementById("orderStatus").value,
    paymentId: document.getElementById("paymentId").value
      ? parseInt(document.getElementById("paymentId").value)
      : null,
    createdAt: createdAtVal ? new Date(createdAtVal).toISOString() : null,
  };

  const method = id ? "PUT" : "POST";
  const url = id ? `${API_BASE_URL}/${id}` : API_BASE_URL;

  try {
    const response = await fetch(url, {
      method: method,
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload),
    });

    const result = await response.json();

    if (!response.ok) {
      throw new Error(result.message || "儲存失敗");
    }

    if (modalInstance) modalInstance.hide();
    showAlert("alertSuccess", id ? "訂單更新成功！" : "新增訂單成功！");
    loadBookingOrders();
  } catch (err) {
    showAlert("alertError", err.message);
  }
}

// 4. 開啟刪除二次確認 Modal
function openDeleteConfirm(id) {
  const item = bookingOrderList.find((b) => b.bookingOrderId === id);
  if (!item) return;

  pendingDeleteId = id;
  if (deleteModalInstance) deleteModalInstance.show();
}

// 5. 執行刪除訂單 (DELETE)
async function executeDelete() {
  if (!pendingDeleteId) return;

  try {
    const response = await fetch(`${API_BASE_URL}/${pendingDeleteId}`, {
      method: "DELETE",
    });

    if (!response.ok) {
      const result = await response.json();
      throw new Error(result.message || "刪除失敗");
    }

    if (deleteModalInstance) deleteModalInstance.hide();
    pendingDeleteId = null;
    showAlert("alertSuccess", "訂單已成功刪除！");
    loadBookingOrders();
  } catch (err) {
    if (deleteModalInstance) deleteModalInstance.hide();
    showAlert("alertError", err.message);
  }
}

// 清空表單（適用於「新增訂單」）
function clearForm() {
  const modalTitle = document.getElementById("bookingOrderModalLabel");
  const modalForm = document.getElementById("modalForm");

  if (modalTitle) modalTitle.innerText = "新增訂單";
  if (modalForm) modalForm.reset();

  document.getElementById("bookingOrderId").value = "";
  document.getElementById("orderStatus").value = "PENDING";

  // 帶入目前時間預設值 (格式: YYYY-MM-DDTHH:mm)
  const now = new Date();
  now.setMinutes(now.getMinutes() - now.getTimezoneOffset());
  document.getElementById("createdAt").value = now.toISOString().slice(0, 16);

  // 新增狀態預設：開啟「解鎖可編輯」狀態
  const lockBtn = document.getElementById("lockToggleBtn");
  if (lockBtn) lockBtn.classList.remove("d-none");
  setLockState(false);
}

// 按下表格中「編輯」按鈕觸發
function onEditBtnClick(id) {
  const item = bookingOrderList.find((b) => b.bookingOrderId === id);
  if (item) editBookingOrder(item);
}

// 填入資料並開啟 Modal（適用於「檢視/編輯訂單」）
function editBookingOrder(item) {
  const modalTitle = document.getElementById("bookingOrderModalLabel");
  if (modalTitle) modalTitle.innerText = "訂單資訊 (點擊右上角解鎖編輯)";

  document.getElementById("bookingOrderId").value = item.bookingOrderId;
  document.getElementById("memberId").value = item.memberId || "";
  document.getElementById("bookingTotalPrice").value =
    item.bookingTotalPrice || 0;
  document.getElementById("orderStatus").value = item.orderStatus || "PENDING";
  document.getElementById("paymentId").value = item.paymentId || "";

  if (item.createdAt) {
    const date = new Date(item.createdAt);
    date.setMinutes(date.getMinutes() - date.getTimezoneOffset());
    document.getElementById("createdAt").value = date
      .toISOString()
      .slice(0, 16);
  } else {
    document.getElementById("createdAt").value = "";
  }

  // 編輯狀態預設：設置為「唯讀鎖定」
  const lockBtn = document.getElementById("lockToggleBtn");
  if (lockBtn) lockBtn.classList.remove("d-none");
  setLockState(true);

  if (modalInstance) modalInstance.show();
}

// 訂單狀態 Badge 轉換
function getStatusBadge(status) {
  switch (status) {
    case "PAID":
      return `<span class="badge bg-success">PAID (已付款)</span>`;
    case "PENDING":
      return `<span class="badge bg-warning text-dark">PENDING (待付款)</span>`;
    case "COMPLETED":
      return `<span class="badge bg-info text-dark">COMPLETED (已完成)</span>`;
    case "CANCELLED":
      return `<span class="badge bg-secondary">CANCELLED (已取消)</span>`;
    default:
      return `<span class="badge bg-light text-dark">${escapeHtml(status)}</span>`;
  }
}

// 時間格式化 (YYYY-MM-DD HH:mm:ss)
function formatDateTime(datetimeStr) {
  if (!datetimeStr) return "無記錄";
  const date = new Date(datetimeStr);
  return isNaN(date.getTime())
    ? datetimeStr
    : date.toLocaleString("zh-TW", { hour12: false });
}

// 訊息提示控制
function showAlert(id, msg) {
  const alertElem = document.getElementById(id);
  if (alertElem) {
    alertElem.innerText = msg;
    alertElem.classList.remove("d-none");
  }
}

function hideAlert(id) {
  const alertElem = document.getElementById(id);
  if (alertElem) alertElem.classList.add("d-none");
}

// XSS 防護過濾
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
