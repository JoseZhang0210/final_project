<template>
  <div v-if="open" class="admin-modal-backdrop" @click.self="$emit('close')">
    <div class="admin-modal-card export-modal-card">
      <div class="admin-modal-header">
        <div>
          <h2>{{ title }}</h2>
          <p>{{ subtitle }}</p>
        </div>
        <button type="button" class="modal-close" @click="$emit('close')">×</button>
      </div>

      <div class="admin-modal-body">
        <div class="form-section-title">選擇匯出資料範圍</div>

        <div class="export-scope-options">
          <!-- 選項 1: 目前篩選結果 -->
          <label class="scope-option">
            <input type="radio" v-model="scope" value="filtered" />
            <div class="scope-info">
              <strong>目前搜尋與篩選結果</strong>
              <span>{{ filteredHint }}，共 <b>{{ filteredCount }}</b> 筆</span>
            </div>
          </label>

          <!-- 選項 2: 全部資料 -->
          <label class="scope-option">
            <input type="radio" v-model="scope" value="all" />
            <div class="scope-info">
              <strong>全體資料</strong>
              <span>匯出系統內所有資料，共 <b>{{ totalCount }}</b> 筆</span>
            </div>
          </label>

          <!-- 選項 3: 已勾選項目 -->
          <label class="scope-option" :class="{ disabled: selectedCount === 0 }">
            <input type="radio" v-model="scope" value="selected" :disabled="selectedCount === 0" />
            <div class="scope-info">
              <strong>表格中已勾選的資料</strong>
              <span>目前已選取 <b>{{ selectedCount }}</b> 筆資料</span>
            </div>
          </label>

          <!-- 選項 4: 自訂範圍 -->
          <label class="scope-option">
            <input type="radio" v-model="scope" value="custom" />
            <div class="scope-info">
              <strong>自訂數值範圍 / 筆數限制</strong>
              <span>自訂 ID 區間或分頁偏移量進行精準匯出</span>
            </div>
          </label>
        </div>

        <!-- 自訂範圍參數面板 -->
        <div v-if="scope === 'custom'" class="custom-range-panel">
          <div class="admin-form-grid">
            <div class="admin-form-group">
              <label>最小 ID (minId)</label>
              <input v-model.number="customParams.minId" type="number" placeholder="例如：1" />
            </div>
            <div class="admin-form-group">
              <label>最大 ID (maxId)</label>
              <input v-model.number="customParams.maxId" type="number" placeholder="例如：100" />
            </div>
            <div class="admin-form-group">
              <label>筆數限制 (limit)</label>
              <input v-model.number="customParams.limit" type="number" placeholder="例如：50" />
            </div>
            <div class="admin-form-group">
              <label>位移筆數 (offset)</label>
              <input v-model.number="customParams.offset" type="number" placeholder="例如：0" />
            </div>
          </div>
        </div>

        <div v-if="noticeText" class="export-notice" v-html="noticeText"></div>
      </div>

      <div class="admin-modal-footer">
        <button type="button" class="admin-btn admin-btn-secondary" @click="$emit('close')">
          取消
        </button>
        <button
          type="button"
          class="admin-btn admin-btn-primary"
          :disabled="exporting"
          @click="onConfirm"
        >
          {{ exporting ? "匯出中..." : "確認匯出" }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, watch } from "vue";

const props = defineProps({
  open: {
    type: Boolean,
    default: false,
  },
  title: {
    type: String,
    default: "匯出資料",
  },
  subtitle: {
    type: String,
    default: "匯出 JSON 格式資料",
  },
  noticeText: {
    type: String,
    default: "",
  },
  exporting: {
    type: Boolean,
    default: false,
  },
  filteredCount: {
    type: Number,
    default: 0,
  },
  totalCount: {
    type: Number,
    default: 0,
  },
  selectedCount: {
    type: Number,
    default: 0,
  },
  filteredHint: {
    type: String,
    default: "",
  },
  initialScope: {
    type: String,
    default: "filtered",
  },
});

const emit = defineEmits(["close", "confirm"]);

const scope = ref(props.initialScope);
const customParams = reactive({
  minId: "",
  maxId: "",
  limit: "",
  offset: "",
});

watch(
  () => props.open,
  (val) => {
    if (val) {
      if (props.selectedCount > 0) {
        scope.value = "selected";
      } else {
        scope.value = props.initialScope || "all";
      }
    }
  }
);

function onConfirm() {
  emit("confirm", {
    scope: scope.value,
    customParams: { ...customParams },
  });
}
</script>

