// 當選擇 static 預設圖片時
function onSelectExistingImage(selectElem) {
  const selectedPath = selectElem.value;
  const fileInput = document.getElementById("fileInput");
  const previewImg = document.getElementById("imagePreview");
  const previewContainer = document.getElementById("previewContainer");

  if (selectedPath) {
    // 1. 清空實體檔案選取
    fileInput.value = "";
    // 2. 記錄選取的 static 路徑
    document.getElementById("selectedStaticPath").value = selectedPath;
    // 3. 顯示預覽
    previewImg.src = selectedPath;
    previewContainer.classList.remove("d-none");
  }
}

// 當使用者上傳新檔案時
function handleFileSelect(event) {
  const file = event.target.files[0];
  const selectElem = document.getElementById("existingImageSelect");
  const previewImg = document.getElementById("imagePreview");
  const previewContainer = document.getElementById("previewContainer");

  if (file) {
    // 1. 重設靜態下拉選單與隱藏欄位
    selectElem.value = "";
    document.getElementById("selectedStaticPath").value = "";
    // 2. 本地即時預覽
    const reader = new FileReader();
    reader.onload = function (e) {
      previewImg.src = e.target.result;
      previewContainer.classList.remove("d-none");
    };
    reader.readAsDataURL(file);
  }
}
