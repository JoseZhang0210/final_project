const API_BASE_URL = "/api/roomtypes";
let modalInstance = null;
let deleteModalInstance = null;
let roomTypeList = []; // 快取完整房型資料陣列
let isLocked = true; // 狀態控制：true = 唯讀, false = 可編輯
let pendingDeleteId = null; // 當前準備刪除的房型 ID

document.addEventListener("DOMContentLoaded", () => {
  // 1. 初始化 Bootstrap Modal 實例
  const roomModalEl = document.getElementById("roomTypeModal");
  const deleteModalEl = document.getElementById("deleteConfirmModal");

  if (roomModalEl) modalInstance = new bootstrap.Modal(roomModalEl);
  if (deleteModalEl) deleteModalInstance = new bootstrap.Modal(deleteModalEl);

  // 2. 綁定按鈕與表單事件
  initEventListeners();

  // 3. 載入初始資料
  loadRoomTypes();
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

  // 新增房型按鈕
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
    modalForm.addEventListener("submit", saveRoomType);
  }

  // 刪除二次確認按鈕
  const confirmDeleteBtn = document.getElementById("confirmDeleteBtn");
  if (confirmDeleteBtn) {
    confirmDeleteBtn.addEventListener("click", executeDelete);
  }
  // 尋找 price 欄位並加入防止負數監聽
  const priceInput = document.getElementById("price");
  if (priceInput) {
    priceInput.addEventListener("input", (e) => {
      // 只要輸入的值小於 0，就自動拉回 0
      if (parseFloat(e.target.value) < 0) {
        e.target.value = 0;
      }
    });
  }
}

// 1. 取得所有房型資料 (GET)
async function loadRoomTypes() {
  try {
    hideAlert("alertError");
    const response = await fetch(API_BASE_URL);
    if (!response.ok) throw new Error("無法取得房型列表");
    roomTypeList = await response.json();

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
  const selectedCapacity = document.getElementById("capacityFilter")?.value;

  const filteredList = roomTypeList.filter((item) => {
    const matchKeyword =
      !keyword ||
      (item.typeName && item.typeName.toLowerCase().includes(keyword)) ||
      (item.bedType && item.bedType.toLowerCase().includes(keyword)) ||
      (item.description && item.description.toLowerCase().includes(keyword)) ||
      String(item.roomTypeId).includes(keyword);

    let matchCapacity = true;
    if (selectedCapacity) {
      const cap = parseInt(selectedCapacity);
      if (cap === 6) {
        matchCapacity = item.capacity >= 6;
      } else {
        matchCapacity = item.capacity === cap;
      }
    }

    return matchKeyword && matchCapacity;
  });

  renderTable(filteredList);
}

function handleSearch(e) {
  if (e) e.preventDefault();
  applyFilters();
}

function resetSearch() {
  const searchInput = document.getElementById("searchInput");
  const capacityFilter = document.getElementById("capacityFilter");
  if (searchInput) searchInput.value = "";
  if (capacityFilter) capacityFilter.value = "";
  renderTable(roomTypeList);
}

// 渲染表格資料
function renderTable(list) {
  const tbody = document.getElementById("roomTypeTableBody");
  if (!tbody) return;

  if (!list || list.length === 0) {
    tbody.innerHTML = `<tr><td colspan="8" class="text-center py-4 text-muted">沒有符合條件的房型資料</td></tr>`;
    return;
  }

  tbody.innerHTML = list
    .map(
      (item) => `
        <tr>
            <td class="ps-3 fw-bold">${item.roomTypeId}</td>
            <td class="fw-bold text-primary">${escapeHtml(item.typeName)}</td>
            <td>${escapeHtml(item.bedType)}</td>
            <td class="text-success fw-bold">$${item.pricePerNight}</td>
            <td>${item.capacity} 人</td>
            <td>${item.imageId ? item.imageId : "無"}</td>
            <td class="text-muted text-truncate" style="max-width: 200px;">${escapeHtml(item.description || "")}</td>
            <td class="text-center">
                <button type="button" class="btn btn-sm btn-outline-primary me-1" onclick="onEditBtnClick(${item.roomTypeId})">
                    <i class="fa-solid fa-pen"></i> 編輯
                </button>
                <button type="button" class="btn btn-sm btn-outline-danger" onclick="openDeleteConfirm(${item.roomTypeId})">
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

// 3. 新增 / 編輯房型 (POST / PUT)
async function saveRoomType(e) {
  e.preventDefault();
  if (isLocked) return;

  const id = document.getElementById("roomTypeId").value;

  // REST API JSON Payload (對齊對應欄位)
  const payload = {
    roomTypeId: id ? parseInt(id) : null,
    typeName: document.getElementById("roomTypeName").value,
    bedType: document.getElementById("bedType").value,
    pricePerNight: parseFloat(document.getElementById("price").value),
    capacity: parseInt(document.getElementById("capacity").value),
    imageId: document.getElementById("imageId").value
      ? parseInt(document.getElementById("imageId").value)
      : null,
    description: document.getElementById("description").value,
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
    showAlert("alertError", id ? "房型更新成功！" : "新增房型成功！");
    loadRoomTypes();
  } catch (err) {
    showAlert("alertError", err.message);
  }
}

// 4. 開啟刪除二次確認 Modal
function openDeleteConfirm(id) {
  const item = roomTypeList.find((r) => r.roomTypeId === id);
  if (!item) return;

  pendingDeleteId = id;
  if (deleteModalInstance) deleteModalInstance.show();
}

// 5. 執行刪除房型 (DELETE)
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
    loadRoomTypes();
  } catch (err) {
    if (deleteModalInstance) deleteModalInstance.hide();
    showAlert("alertError", err.message);
  }
}

// 清空表單（適用於「新增房型」）
function clearForm() {
  const modalTitle = document.getElementById("roomTypeModalLabel");
  const modalForm = document.getElementById("modalForm");

  if (modalTitle) modalTitle.innerText = "新增房型";
  if (modalForm) modalForm.reset();

  document.getElementById("roomTypeId").value = "";
  document.getElementById("price").value = "2000";
  document.getElementById("capacity").value = "1";

  // 新增狀態預設：顯示鎖頭按鈕，並開啟「解鎖可編輯」狀態
  const lockBtn = document.getElementById("lockToggleBtn");
  if (lockBtn) lockBtn.classList.remove("d-none");
  setLockState(false);
}

// 按下表格中「編輯」按鈕觸發
function onEditBtnClick(id) {
  const item = roomTypeList.find((r) => r.roomTypeId === id);
  if (item) editRoomType(item);
}

// 填入資料並開啟 Modal（適用於「查看/編輯房型」）
function editRoomType(item) {
  const modalTitle = document.getElementById("roomTypeModalLabel");
  if (modalTitle) modalTitle.innerText = "房型資訊 (點擊右上角解鎖編輯)";

  document.getElementById("roomTypeId").value = item.roomTypeId;
  document.getElementById("roomTypeName").value = item.typeName || "";
  document.getElementById("bedType").value = item.bedType || "";
  document.getElementById("price").value = item.pricePerNight || 100;
  document.getElementById("capacity").value = item.capacity || 1;
  document.getElementById("imageId").value = item.imageId || "";
  document.getElementById("description").value = item.description || "";

  // 編輯狀態預設：顯示鎖頭按鈕，並設置為「唯讀鎖定」
  const lockBtn = document.getElementById("lockToggleBtn");
  if (lockBtn) lockBtn.classList.remove("d-none");
  setLockState(true);

  if (modalInstance) modalInstance.show();
}

// 錯誤/警告訊息提示
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
