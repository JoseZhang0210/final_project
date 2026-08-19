const API_BASE_URL = "/api/rooms";
let modalInstance = null;
let deleteModalInstance = null;
let roomList = []; // 快取完整房間資料陣列
let isLocked = true; // 狀態控制：true = 唯讀, false = 可編輯
let pendingDeleteId = null; // 當前準備刪除的房間 ID

document.addEventListener("DOMContentLoaded", () => {
  modalInstance = new bootstrap.Modal(document.getElementById("roomModal"));
  deleteModalInstance = new bootstrap.Modal(
    document.getElementById("deleteConfirmModal"),
  );
  loadRooms();
});

// 1. 取得所有房間資料 (GET)
async function loadRooms() {
  try {
    hideAlert("alertError");
    const response = await fetch(API_BASE_URL);
    if (!response.ok) throw new Error("無法取得房間列表");
    roomList = await response.json();

    applyFilters();
  } catch (err) {
    showAlert("alertError", err.message);
  }
}

// 2. 搜尋與篩選邏輯
function applyFilters() {
  const keyword = document
    .getElementById("searchInput")
    .value.trim()
    .toLowerCase();
  const selectedFloor = document.getElementById("floorFilter").value;
  const selectedRoomStatus = document.getElementById("roomStatusFilter").value;

  const filteredList = roomList.filter((item) => {
    const matchKeyword =
      !keyword ||
      String(item.roomNumber).toLowerCase().includes(keyword) ||
      String(item.roomId).includes(keyword);

    const matchFloor = !selectedFloor || item.floor === parseInt(selectedFloor);
    const matchRoomStatus =
      !selectedRoomStatus || item.roomStatus === selectedRoomStatus;

    return matchKeyword && matchFloor && matchRoomStatus;
  });

  renderTable(filteredList);
}

function handleSearch(e) {
  if (e) e.preventDefault();
  applyFilters();
}

function resetSearch() {
  document.getElementById("searchInput").value = "";
  document.getElementById("floorFilter").value = "";
  document.getElementById("roomStatusFilter").value = "";
  renderTable(roomList);
}

// 渲染表格資料
function renderTable(list) {
  const tbody = document.getElementById("roomTableBody");
  if (!list || list.length === 0) {
    tbody.innerHTML = `<tr><td colspan="6" class="text-center py-4 text-muted">沒有符合條件的房間資料</td></tr>`;
    return;
  }

  tbody.innerHTML = list
    .map(
      (item) => `
                <tr>
                    <td class="ps-3 fw-bold">${item.roomId}</td>
                    <td class="fw-bold text-primary">${escapeHtml(item.roomNumber)}</td>
                    <td><span class="badge bg-secondary">型號 ${item.roomTypeId}</span></td>
                    <td>${item.floor} 樓</td>
                    <td>${getRoomStatusBadge(item.roomStatus)}</td>
                    <td class="text-center">
                        <button type="button" class="btn btn-sm btn-outline-primary me-1" onclick="onEditBtnClick(${item.roomId})">
                            <i class="fa-solid fa-pen"></i> 編輯
                        </button>
                        <button type="button" class="btn btn-sm btn-outline-danger" onclick="openDeleteConfirm(${item.roomId})">
                            <i class="fa-solid fa-trash"></i> 刪除
                        </button>
                    </td>
                </tr>
            `,
    )
    .join("");
}

// 房間狀態 Badge 顏色轉換
function getRoomStatusBadge(status) {
  switch (status) {
    case "可預訂":
      return `<span class="badge bg-success">可預訂</span>`;
    case "已預訂":
      return `<span class="badge bg-primary">已預訂</span>`;
    case "已入住":
      return `<span class="badge bg-info text-dark">已入住</span>`;
    case "退房待清潔":
      return `<span class="badge bg-warning text-dark">退房待清潔</span>`;
    case "清潔中":
      return `<span class="badge bg-secondary">清潔中</span>`;
    default:
      return `<span class="badge bg-light text-dark border">${escapeHtml(status || "未設定")}</span>`;
  }
}

// 🔒 控制唯讀 / 可編輯狀態
function setLockState(locked) {
  isLocked = locked;
  const fields = document.querySelectorAll(".editable-field");
  const lockIcon = document.getElementById("lockIcon");
  const lockText = document.getElementById("lockText");
  const lockBtn = document.getElementById("lockToggleBtn");
  const submitBtn = document.getElementById("btnSubmit");
  const hint = document.getElementById("readonlyHint");

  if (isLocked) {
    fields.forEach((field) => {
      if (field.tagName === "SELECT") {
        field.setAttribute("disabled", "disabled");
      } else {
        field.setAttribute("readonly", "readonly");
      }
    });
    lockIcon.className = "fa-solid fa-lock me-1";
    lockText.innerText = "點擊解鎖";
    lockBtn.className = "btn btn-warning btn-sm fw-bold";
    submitBtn.classList.add("d-none");
    hint.classList.remove("d-none");
  } else {
    fields.forEach((field) => {
      if (field.tagName === "SELECT") {
        field.removeAttribute("disabled");
      } else {
        field.removeAttribute("readonly");
      }
    });
    lockIcon.className = "fa-solid fa-lock-open me-1";
    lockText.innerText = "編輯中";
    lockBtn.className = "btn btn-success btn-sm fw-bold";
    submitBtn.classList.remove("d-none");
    hint.classList.add("d-none");
  }
}

function toggleLock() {
  setLockState(!isLocked);
}

// 3. 新增 / 編輯房間 (POST / PUT)
async function saveRoom(e) {
  e.preventDefault();
  if (isLocked) return;

  const id = document.getElementById("roomId").value;
  const payload = {
    roomId: id ? parseInt(id) : null,
    roomNumber: document.getElementById("roomNumber").value,
    roomTypeId: parseInt(document.getElementById("roomTypeId").value),
    floor: parseInt(document.getElementById("floor").value),
    roomStatus: document.getElementById("roomStatus").value,
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

    modalInstance.hide();
    showAlert("alertSuccess", id ? "房間更新成功！" : "新增房間成功！");
    loadRooms();
  } catch (err) {
    showAlert("alertError", err.message);
  }
}

// 4. 開啟刪除二次確認 Modal
function openDeleteConfirm(id) {
  const item = roomList.find((r) => r.roomId === id);
  if (!item) return;

  pendingDeleteId = id;
  document.getElementById("deleteTargetInfo").innerText =
    `[ID: ${item.roomId}] 房號：${item.roomNumber} (${item.floor} 樓)`;
  deleteModalInstance.show();
}

// 5. 執行刪除房間 (DELETE)
async function executeDelete() {
  if (!pendingDeleteId) return;

  try {
    const response = await fetch(`${API_BASE_URL}/${pendingDeleteId}`, {
      method: "DELETE",
    });
    const result = await response.json();

    if (!response.ok) {
      throw new Error(result.message || "刪除失敗");
    }

    deleteModalInstance.hide();
    showAlert("alertSuccess", "房間已成功刪除！");
    pendingDeleteId = null;
    loadRooms();
  } catch (err) {
    deleteModalInstance.hide();
    showAlert("alertError", err.message);
  }
}

// 清空表單
function clearForm() {
  document.getElementById("modalTitle").innerText = "新增房間";
  document.getElementById("roomForm").reset();
  document.getElementById("roomId").value = "";
  document.getElementById("roomStatus").value = "可預訂";

  document.getElementById("lockToggleBtn").classList.add("d-none");
  setLockState(false);
}

// 按下編輯按鈕觸發
function onEditBtnClick(id) {
  const item = roomList.find((r) => r.roomId === id);
  if (item) editRoom(item);
}

// 填入資料並開啟
function editRoom(item) {
  document.getElementById("modalTitle").innerText = "查看 / 編輯房間資料";
  document.getElementById("roomId").value = item.roomId;
  document.getElementById("roomNumber").value = item.roomNumber || "";
  document.getElementById("roomTypeId").value = item.roomTypeId || "";
  document.getElementById("floor").value = item.floor || "";
  document.getElementById("roomStatus").value = item.roomStatus || "可預訂";

  document.getElementById("lockToggleBtn").classList.remove("d-none");
  setLockState(true);

  modalInstance.show();
}

// 提示訊息控制
function showAlert(id, msg) {
  const alertElem = document.getElementById(id);
  const msgElem = document.getElementById(id + "Msg");
  if (msgElem) msgElem.innerText = msg;
  alertElem.classList.remove("d-none");
}

function hideAlert(id) {
  document.getElementById(id).classList.add("d-none");
}

// XSS 防護
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
