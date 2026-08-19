const API_BASE_URL = "/api/bookings";
let modalInstance = null;
let deleteModalInstance = null;
let bookingList = []; // 快取完整訂單資料陣列
let isLocked = true; // 狀態控制：true = 唯讀, false = 可編輯
let pendingDeleteId = null; // 當前準備刪除的 Booking ID

document.addEventListener("DOMContentLoaded", () => {
  modalInstance = new bootstrap.Modal(document.getElementById("bookingModal"));
  deleteModalInstance = new bootstrap.Modal(
    document.getElementById("deleteConfirmModal"),
  );
  loadBookings();
});

// 1. 取得所有訂單資料 (GET)
async function loadBookings() {
  try {
    hideAlert("alertError");
    const response = await fetch(API_BASE_URL);
    if (!response.ok) throw new Error("無法取得訂單列表");
    bookingList = await response.json();

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
  const selectedStatus = document.getElementById("statusFilter").value;

  const filteredList = bookingList.filter((item) => {
    const matchKeyword =
      !keyword ||
      String(item.bookingId).includes(keyword) ||
      String(item.bookingOrderId).includes(keyword) ||
      String(item.roomId).includes(keyword);

    const matchStatus =
      !selectedStatus || item.bookingStatus === selectedStatus;

    return matchKeyword && matchStatus;
  });

  renderTable(filteredList);
}

function handleSearch(e) {
  if (e) e.preventDefault();
  applyFilters();
}

function resetSearch() {
  document.getElementById("searchInput").value = "";
  document.getElementById("statusFilter").value = "";
  renderTable(bookingList);
}

// 渲染表格資料
function renderTable(list) {
  const tbody = document.getElementById("bookingTableBody");
  if (!list || list.length === 0) {
    tbody.innerHTML = `<tr><td colspan="9" class="text-center py-4 text-muted">沒有符合條件的訂單資料</td></tr>`;
    return;
  }

  tbody.innerHTML = list
    .map(
      (item) => `
                <tr>
                    <td class="ps-3 fw-bold">${item.bookingId}</td>
                    <td class="text-primary fw-bold">#${item.bookingOrderId}</td>
                    <td><span class="badge bg-light text-dark border">房間編號: ${item.roomId}</span></td>
                    <td><span class="badge bg-secondary">房型編號: ${item.roomTypeId}</span></td>
                    <td>${formatDisplayDate(item.checkInDate)}</td>
                    <td>${formatDisplayDate(item.checkOutDate)}</td>
                    <td><i class="fa-solid fa-user me-1 text-muted"></i>${item.guestNum} 人</td>
                    <td>${getStatusBadge(item.bookingStatus)}</td>
                    <td class="text-center">
                        <button type="button" class="btn btn-sm btn-outline-primary me-1" onclick="onEditBtnClick(${item.bookingId})">
                            <i class="fa-solid fa-pen"></i> 編輯
                        </button>
                        <button type="button" class="btn btn-sm btn-outline-danger" onclick="openDeleteConfirm(${item.bookingId})">
                            <i class="fa-solid fa-trash"></i> 刪除
                        </button>
                    </td>
                </tr>
            `,
    )
    .join("");
}

// 訂單狀態 Badge 顏色轉換
function getStatusBadge(status) {
  switch (status) {
    case "處理中":
      return `<span class="badge bg-warning text-dark"><i class="fa-solid fa-clock me-1"></i>處理中</span>`;
    case "訂單完成":
      return `<span class="badge bg-success"><i class="fa-solid fa-check-circle me-1"></i>訂單完成</span>`;
    case "訂單取消":
      return `<span class="badge bg-danger"><i class="fa-solid fa-times-circle me-1"></i>訂單取消</span>`;
    default:
      return `<span class="badge bg-secondary">${escapeHtml(status || "未知")}</span>`;
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

// 3. 新增 / 編輯訂單 (POST / PUT)
async function saveBooking(e) {
  e.preventDefault();
  if (isLocked) return;

  const id = document.getElementById("bookingId").value;
  const payload = {
    bookingId: id ? parseInt(id) : null,
    bookingOrderId: parseInt(document.getElementById("bookingOrderId").value),
    roomId: parseInt(document.getElementById("roomId").value),
    roomTypeId: parseInt(document.getElementById("roomTypeId").value),
    checkInDate: document.getElementById("checkInDate").value,
    checkOutDate: document.getElementById("checkOutDate").value,
    guestNum: parseInt(document.getElementById("guestNum").value),
    bookingStatus: document.getElementById("bookingStatus").value,
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
    showAlert("alertSuccess", id ? "訂單更新成功！" : "新增訂單成功！");
    loadBookings();
  } catch (err) {
    showAlert("alertError", err.message);
  }
}

// 4. 開啟刪除二次確認 Modal
function openDeleteConfirm(id) {
  const item = bookingList.find((b) => b.bookingId === id);
  if (!item) return;

  pendingDeleteId = id;
  document.getElementById("deleteTargetInfo").innerText =
    `[訂單編號: ${item.bookingId}] 預定單號 #${item.bookingOrderId} (房間編號: ${item.roomId})`;
  deleteModalInstance.show();
}

// 5. 執行刪除訂單 (DELETE)
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
    showAlert("alertSuccess", "訂單已成功刪除！");
    pendingDeleteId = null;
    loadBookings();
  } catch (err) {
    deleteModalInstance.hide();
    showAlert("alertError", err.message);
  }
}

// 清空表單
function clearForm() {
  document.getElementById("modalTitle").innerText = "新增訂單";
  document.getElementById("bookingForm").reset();
  document.getElementById("bookingId").value = "";
  document.getElementById("bookingStatus").value = "處理中";

  document.getElementById("lockToggleBtn").classList.add("d-none");
  setLockState(false);
}

// 按下編輯按鈕觸發
function onEditBtnClick(id) {
  const item = bookingList.find((b) => b.bookingId === id);
  if (item) editBooking(item);
}

// 填入資料並開啟 Modal
function editBooking(item) {
  document.getElementById("modalTitle").innerText = "查看 / 編輯訂單資料";
  document.getElementById("bookingId").value = item.bookingId;
  document.getElementById("bookingOrderId").value = item.bookingOrderId || "";
  document.getElementById("roomId").value = item.roomId || "";
  document.getElementById("roomTypeId").value = item.roomTypeId || "";
  document.getElementById("checkInDate").value = formatDateOnly(
    item.checkInDate,
  );
  document.getElementById("checkOutDate").value = formatDateOnly(
    item.checkOutDate,
  );
  document.getElementById("guestNum").value = item.guestNum || 1;
  document.getElementById("bookingStatus").value =
    item.bookingStatus || "處理中";

  document.getElementById("lockToggleBtn").classList.remove("d-none");
  setLockState(true);

  modalInstance.show();
}

// 轉化為 date 輸入框格式 (YYYY-MM-DD)
function formatDateOnly(dateStr) {
  if (!dateStr) return "";
  const d = new Date(dateStr);
  if (isNaN(d.getTime())) return dateStr.split("T")[0] || dateStr;
  const pad = (num) => String(num).padStart(2, "0");
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}

// 顯示表格用的日期格式化
function formatDisplayDate(dateStr) {
  if (!dateStr) return "—";
  return formatDateOnly(dateStr);
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
