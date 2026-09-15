<template>
  <div v-if="open" class="admin-modal-backdrop" @click.self="$emit('close')">
    <div class="admin-modal-card import-modal-card">
      <div class="admin-modal-header">
        <div>
          <h2>{{ title }}</h2>
          <p>{{ subtitle }}</p>
        </div>
        <button type="button" class="modal-close" @click="$emit('close')">×</button>
      </div>

      <div class="admin-modal-body">
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
            :for="fileInputId"
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
              :id="fileInputId"
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
            :placeholder="placeholder"
          ></textarea>
        </div>

        <!-- 匯入規則說明 -->
        <div class="import-guide">
          <div class="guide-title">📌 匯入規則：</div>
          <ul>
            <slot name="rules"></slot>
          </ul>
        </div>

        <!-- 匯入結果提示 -->
        <div
          v-if="importResult"
          class="import-result-box"
          :class="importResult.failureCount > 0 ? 'has-error' : 'success'"
        >
          <div class="result-header">
            <strong>匯入結果：</strong>
            <span>總計 {{ importResult.total ?? 0 }} 筆 ｜ 成功 {{ importResult.successCount ?? 0 }} 筆 ｜ 失敗 {{ importResult.failureCount ?? 0 }} 筆</span>
          </div>
          <ul v-if="importResult.errors && importResult.errors.length > 0" class="error-list">
            <li v-for="(err, idx) in importResult.errors" :key="idx">{{ err }}</li>
          </ul>
        </div>
      </div>

      <div class="admin-modal-footer">
        <button type="button" class="admin-btn admin-btn-secondary" @click="$emit('close')">
          關閉
        </button>
        <button
          type="button"
          class="admin-btn admin-btn-primary"
          :disabled="importing || isSubmitDisabled"
          @click="onImportClick"
        >
          {{ importing ? "處理中..." : "開始匯入" }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: "匯入資料",
  },
  subtitle: {
    type: String,
    default: "透過 JSON 檔案或文字批次匯入資料",
  },
  placeholder: {
    type: String,
    default: "請在此貼上 JSON 格式內容...",
  },
  importing: {
    type: Boolean,
    default: false,
  },
  importResult: {
    type: Object,
    default: null,
  },
  fileInputId: {
    type: String,
    default: "import-json-file-input",
  },
});

const emit = defineEmits(["close", "import", "error-msg"]);

const importMode = ref("file");
const importFile = ref(null);
const importJsonText = ref("");
const isDragging = ref(false);

watch(
  () => props.open,
  (val) => {
    if (val) {
      importFile.value = null;
      importJsonText.value = "";
      importMode.value = "file";
    }
  }
);

const isSubmitDisabled = computed(() => {
  if (importMode.value === "file") {
    return !importFile.value;
  }
  return !importJsonText.value.trim();
});

function onFileChange(event) {
  const file = event.target.files?.[0];
  if (file) {
    if (!file.name.endsWith(".json")) {
      emit("error-msg", "請選擇 .json 格式的檔案");
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
      emit("error-msg", "請選擇 .json 格式的檔案");
      return;
    }
    importFile.value = file;
  }
}

function onImportClick() {
  emit("import", {
    mode: importMode.value,
    file: importFile.value,
    jsonText: importJsonText.value,
  });
}
</script>

