let isLocked = true;
let deleteTargetId = null;

document.addEventListener("DOMContentLoaded", () => {
  loadImages();
});

// 1. 載入圖片資料列表
async function loadImages(keyword = "") {
  try {
    const response = await fetch(
      `/api/images?search=${encodeURIComponent(keyword)}`,
    );
    if (!response.ok) throw new Error("載入失敗");
    const images = await response.json();
    renderTable(images);
  } catch (error) {
    showAlert("alertError", "無法載入圖片列表資料！");
  }
}

// 2. 渲染表格內容
function renderTable(images) {
  const tbody = document.getElementById("imageTableBody");
  if (!tbody) return;

  if (!images || images.length === 0) {
    tbody.innerHTML = `<tr><td colspan="5" class="text-center py-4 text-muted">查無圖片資料</td></tr>`;
    return;
  }

  tbody.innerHTML = images
    .map((img) => {
      const id = img.imageId ?? img.image_id;
      const desc = img.imageDesc ?? img.image_desc;
      const path = img.path;

      return `
      <tr>
        <td class="ps-3 fw-bold">${id}</td>
        <td>
          <img src="${path}" alt="${desc}" class="img-thumbnail" style="width: 50px; height: 50px; object-fit: cover;" onerror="this.src='/images/room/roomtype.1.jpg'">
        </td>
        <td><code>${path}</code></td>
        <td>${desc}</td>
        <td class="text-center">
          <button class="btn btn-sm btn-outline-primary me-1" onclick="openEditModal(${JSON.stringify(img).replace(/"/g, "&quot;")})">
            <i class="fa-solid fa-pen"></i> 編輯
          </button>
          <button class="btn btn-sm btn-outline-danger" onclick="openDeleteModal(${id}, '${desc}')">
            <i class="fa-solid fa-trash"></i> 刪除
          </button>
        </td>
      </tr>
    `;
    })
    .join("");
}

// 3. 搜尋與重設
function handleSearch(event) {
  if (event) event.preventDefault();
  const keyword = document.getElementById("searchInput").value.trim();
  loadImages(keyword);
}

function resetSearch() {
  document.getElementById("searchInput").value = "";
  loadImages();
}

// 4. Modal 狀態鎖定 / 解鎖設定
function setFormLock(locked) {
  isLocked = locked;
  const fields = document.querySelectorAll(".editable-field");
  const btnSubmit = document.getElementById("btnSubmit");
  const lockIcon = document.getElementById("lockIcon");
  const lockText = document.getElementById("lockText");

  fields.forEach((field) => (field.disabled = isLocked));
  if (btnSubmit) btnSubmit.disabled = isLocked;

  if (lockIcon && lockText) {
    if (isLocked) {
      lockIcon.className = "fa-solid fa-lock me-1";
      lockText.innerText = "點擊解鎖";
    } else {
      lockIcon.className = "fa-solid fa-lock-open me-1";
      lockText.innerText = "已解鎖";
    }
  }
}

function toggleLock() {
  setFormLock(!isLocked);
}

// 5. 新增模式 (清空表單，預設解鎖)
function clearForm() {
  const form = document.getElementById("imageForm");
  if (form) form.reset();

  document.getElementById("imageId").value = "";
  document.getElementById("selectedStaticPath").value = "";

  const previewContainer = document.getElementById("previewContainer");
  if (previewContainer) previewContainer.classList.add("d-none");

  const imagePreview = document.getElementById("imagePreview");
  if (imagePreview) imagePreview.src = "";

  const modalTitle = document.getElementById("modalTitle");
  if (modalTitle) modalTitle.innerText = "新增圖片設定";

  setFormLock(false);
}

// 6. 編輯模式 (載入資料，預設鎖定)
function openEditModal(imgData) {
  clearForm();

  const id = imgData.imageId ?? imgData.image_id;
  const desc = imgData.imageDesc ?? imgData.image_desc;
  const path = imgData.path;

  document.getElementById("imageId").value = id;
  document.getElementById("imageDesc").value = desc;

  const select = document.getElementById("existingImageSelect");
  if (select) select.value = path;

  const previewContainer = document.getElementById("previewContainer");
  const imagePreview = document.getElementById("imagePreview");
  if (previewContainer && imagePreview) {
    imagePreview.src = path;
    previewContainer.classList.remove("d-none");
  }

  const modalTitle = document.getElementById("modalTitle");
  if (modalTitle) modalTitle.innerText = "編輯圖片設定";

  setFormLock(true);

  const modal = new bootstrap.Modal(document.getElementById("imageModal"));
  modal.show();
}

// 7. 下拉選單與檔案選擇選擇邏輯
function onSelectExistingImage(selectElem) {
  const selectedPath = selectElem.value;
  document.getElementById("selectedStaticPath").value = selectedPath;

  const previewContainer = document.getElementById("previewContainer");
  const imagePreview = document.getElementById("imagePreview");

  if (selectedPath) {
    document.getElementById("fileInput").value = ""; // 清空檔案選擇
    if (imagePreview) imagePreview.src = selectedPath;
    if (previewContainer) previewContainer.classList.remove("d-none");
  }
}

function handleFileSelect(event) {
  const file = event.target.files[0];
  const select = document.getElementById("existingImageSelect");
  const previewContainer = document.getElementById("previewContainer");
  const imagePreview = document.getElementById("imagePreview");

  if (file) {
    if (select) select.value = ""; // 清空下拉選項
    document.getElementById("selectedStaticPath").value = "";

    const reader = new FileReader();
    reader.onload = function (e) {
      if (imagePreview) imagePreview.src = e.target.result;
      if (previewContainer) previewContainer.classList.remove("d-none");
    };
    reader.readAsDataURL(file);
  }
}

// 8. 儲存 / 上傳圖片 (整合 uploadimagesroompic 與 saveImage)
async function uploadimagesroompic(event) {
  if (event) event.preventDefault();

  const fileInput = document.getElementById("fileInput");
  const imageDesc = document.getElementById("imageDesc").value;
  const existingSelect = document.getElementById("existingImageSelect");
  const existingPath = existingSelect ? existingSelect.value : "";
  const formData = new FormData(document.getElementById("imageForm"));

  try {
    let response;

    // 情況 A：使用者選擇上傳新檔案
    if (fileInput && fileInput.files[0]) {
      response = await fetch("/api/images/uploadimagesroompic", {
        method: "POST",
        body: formData,
      });
    }
    // 情況 B：使用者選擇預設圖片或標準表單送出
    else {
      response = await fetch("/api/images/uploadimagesroompic", {
        method: "POST",
        body: formData,
      });
    }

    if (response.ok) {
      const savedImage = await response.json();

      // 如果有上傳新檔案，動態加入下拉選單
      if (savedImage && savedImage.path && existingSelect) {
        const optionExists = Array.from(existingSelect.options).some(
          (opt) => opt.value === savedImage.path,
        );
        if (!optionExists) {
          const option = document.createElement("option");
          option.value = savedImage.path;
          option.textContent = `${savedImage.imageDesc || "新上傳圖"} (${savedImage.path})`;
          existingSelect.appendChild(option);
        }
        existingSelect.value = savedImage.path;
      }

      // 關閉 Modal
      const modalElem = document.getElementById("imageModal");
      const modal = bootstrap.Modal.getInstance(modalElem);
      if (modal) modal.hide();

      showAlert("alertSuccess", "圖片資料儲存成功！");
      loadImages(); // 重新整理表格
    } else {
      const errorMsg = await response.text();
      showAlert("alertError", "儲存失敗：" + (errorMsg || "請檢查輸入內容！"));
    }
  } catch (err) {
    console.error(err);
    showAlert("alertError", "網路連線異常，無法儲存資料！");
  }
}

// 提供 saveImage 作為別名，確保相容性
const saveImage = uploadimagesroompic;

// 9. 刪除邏輯
function openDeleteModal(id, desc) {
  deleteTargetId = id;
  const infoElem = document.getElementById("deleteTargetInfo");
  if (infoElem) infoElem.innerText = `ID: ${id} - ${desc}`;

  const modal = new bootstrap.Modal(
    document.getElementById("deleteConfirmModal"),
  );
  modal.show();
}

async function executeDelete() {
  if (!deleteTargetId) return;

  try {
    const response = await fetch(`/api/images/${deleteTargetId}`, {
      method: "DELETE",
    });

    const modalElem = document.getElementById("deleteConfirmModal");
    const modal = bootstrap.Modal.getInstance(modalElem);
    if (modal) modal.hide();

    if (response.ok) {
      showAlert("alertSuccess", "圖片已成功刪除！");
      loadImages();
    } else {
      showAlert("alertError", "刪除失敗！");
    }
  } catch (err) {
    showAlert("alertError", "刪除操作發生錯誤！");
  }
}

// 10. Alert 訊息框控制器
function showAlert(alertId, message) {
  const alertElem = document.getElementById(alertId);
  const msgElem = document.getElementById(alertId + "Msg");
  if (alertElem && msgElem) {
    msgElem.innerText = message;
    alertElem.classList.remove("d-none");
    setTimeout(() => hideAlert(alertId), 4000);
  }
}

function hideAlert(alertId) {
  const alertElem = document.getElementById(alertId);
  if (alertElem) alertElem.classList.add("d-none");
}
