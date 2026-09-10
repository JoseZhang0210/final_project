<script setup>
import { onMounted, onUnmounted, ref, computed, watch } from "vue";
import { roomTaskApi } from "@/api/roomTaskApi";
import { roomApi } from "@/api/roomApi";
import { employeeApi } from "@/api/employeeApi";

// 下拉選單資料 (透過 API 動態載入)
const rooms = ref([]);
const employees = ref([]);



// 核心資料列表
const roomTasks = ref([]);
const currentTime = ref(new Date());
const loading = ref(false);

// 下拉選單選項（與資料庫值對應）
const priorities = ["一般", "重要", "緊急"];
const taskStatuses = ["待處理", "進行中", "已完成", "已取消"];
const taskTypes = ["退房清潔", "日常清潔", "設備維修", "停用維護", "補充備品", "其他"];

// 工單「進行中」時房間狀態 / 工單「已完成」時房間狀態
// 對應 AdminRoomView 的房間狀態：可預訂、已預訂、已入住、退房待清潔、清潔中、維修中、停用
const taskTypeToRoomStatus = {
  '退房清潔': { doing: '清潔中',  done: '可預訂' }, // 退房 → 清潔中 → 可預訂
  '日常清潔': { doing: '已入住',  done: '已入住' }, // 續住清潔，維持已入住
  '設備維修': { doing: '維修中',  done: '可預訂' }, // 維修 → 維修中 → 可預訂
  '停用維護': { doing: '停用',    done: '可預訂' }, // 停用 → 停用 → 可預訂
  '補充備品': { doing: null,      done: null     }, // 不改變房間狀態
  '其他':     { doing: null,      done: null     },
};

// 查詢條件狀態 (對應後端 Controller 的可查詢參數)
const searchParams = ref({
  taskId: "",
  roomId: "",
  employeeId: "",
  priority: "",
});

const message = ref("");
const messageType = ref("");
const formTitle = ref("新增/編輯房務工單");
const form = ref(createEmptyForm());
const showFormModal = ref(false);

function openAddModal() {
  clearForm();
  showFormModal.value = true;
}

function closeFormModal() {
  showFormModal.value = false;
  clearForm();
}

const filteredEmployees = computed(() => {
  if (!form.value || !form.value.taskType) return employees.value;
  if (form.value.taskType.includes("清潔")) {
    return employees.value.filter(e => Number(e.employeeId) >= 13 && Number(e.employeeId) <= 24);
  } else {
    return employees.value.filter(e => Number(e.employeeId) >= 25 && Number(e.employeeId) <= 28);
  }
});

function createEmptyForm() {
  return {
    taskId: null,
    roomId: "",
    employeeId: "",
    priority: "一般",
    taskType: "日常清潔",
    taskStatus: "待處理",
    remark: "",
    createdAt: "",
    completedAt: "",
  };
}

function showMessage(text, type) {
  message.value = text;
  messageType.value = type;
}

function clearForm() {
  form.value = createEmptyForm();
  formTitle.value = `新增房務工單`;
}

// 動態載入房間下拉選項 (GET /api/rooms)
async function loadRooms() {
  try {
    const data = await roomApi.getAllRooms();
    rooms.value = data.map((item) => ({
      roomId: item.roomId ?? item.room_id,
      roomNumber:
        item.roomNumber ?? item.room_number ?? item.roomId ?? item.room_id,
    }));
  } catch (error) {
    console.error("loadRooms Error:", error);
  }
}

// 動態載入員工下拉選項 (GET /api/employees)
async function loadEmployees() {
  try {
    const data = await employeeApi.findAllEmployees();
    employees.value = (data || []).map((item) => ({
      employeeId: item.employeeId ?? item.employee_id,
      employeeName:
        item.employeeName ??
        item.employee_name ??
        item.name ??
        `員工 ${item.employeeId ?? item.employee_id}`,
    }));
  } catch (error) {
    console.error("loadEmployees Error:", error);
  }
}

// 取得房間號碼 (優先對照 rooms 選單，若無則顯示 ID)
function getRoomNumber(roomId) {
  if (!roomId) return "未知房間";
  const found = rooms.value.find(
    (room) => Number(room.roomId) === Number(roomId),
  );
  return found ? found.roomNumber : `房號 ID: ${roomId}`;
}

// 取得員工姓名 (目前依需求改為直接顯示員工 ID)
function getEmployeeName(employeeId) {
  if (!employeeId) return "未指派";
  return String(employeeId);
}

// 傳給後端的時間格式：ISO 8601 YYYY-MM-DDTHH:mm:ss（Java LocalDateTime 需要 T 分隔符）
function getCurrentDateTime() {
  const now = new Date();
  const tzOffset = now.getTimezoneOffset() * 60000; // 本地時區位移
  return new Date(now.getTime() - tzOffset).toISOString().slice(0, 19);
}

// 前端畫面顯示用的時間格式：只保留到幾點幾分 (長度 16)
function formatDateTimeShort(dateTimeStr) {
  if (!dateTimeStr) return "—";
  return String(dateTimeStr).slice(0, 16);
}

// 確保傳給後端的時間格式包含秒數，並將空格替換為 T（Java LocalDateTime 要求）
function ensureSecondsFormat(dateTimeStr) {
  if (!dateTimeStr) return "";
  let str = String(dateTimeStr);
  // 把空格分隔符換成 T（"2026-09-08 10:24" → "2026-09-08T10:24"）
  str = str.replace(' ', 'T');
  if (str.length === 16) {
    return str + ":00";
  }
  return str;
}

// 1. 載入與條件查詢房務工單 (GET /api/roomtask?...)
async function loadRoomTasks() {
  currentPage.value = 1;
  try {
    const params = {};
    if (searchParams.value.taskId) params.taskId = searchParams.value.taskId;
    if (searchParams.value.roomId) params.roomId = searchParams.value.roomId;
    if (searchParams.value.employeeId) params.employeeId = searchParams.value.employeeId;
    if (searchParams.value.priority) params.priority = searchParams.value.priority;

    const data = await roomTaskApi.getRoomTasks(params);
    roomTasks.value = Array.isArray(data) ? data : data ? [data] : [];
  } catch (error) {
    console.error("loadRoomTasks Error:", error);
    showMessage(error.message || "無法連線至房務工單 API", "error");
  }
}

// 重設查詢條件
function resetSearch() {
  searchParams.value = {
    taskId: "",
    roomId: "",
    employeeId: "",
    priority: "",
  };
  quickFilterType.value = "all";
  quickFilterPriority.value = "all";
  quickFilterStatus.value = "all";
  loadRoomTasks();
}

// 2. 新增或修改房務工單 (POST / PUT /api/roomtask)
async function saveRoomTask() {
  if (!form.value.roomId) {
    showMessage("請選擇房間", "error");
    return;
  }

  if (!form.value.employeeId) {
    showMessage("請選擇負責員工", "error");
    return;
  }

  const isCompleted = form.value.taskStatus === "已完成";
  const payload = {
    taskId: form.value.taskId,
    roomId: Number(form.value.roomId),
    employeeId: Number(form.value.employeeId),
    priority: form.value.priority,
    taskType: form.value.taskType,
    taskStatus: form.value.taskStatus,
    remark: form.value.remark || "",
    createdAt: ensureSecondsFormat(
      form.value.createdAt || getCurrentDateTime(),
    ),
    completedAt: isCompleted
      ? ensureSecondsFormat(form.value.completedAt || getCurrentDateTime())
      : null,
  };

  const isEdit = form.value.taskId !== null;

  try {
    if (isEdit) {
      await roomTaskApi.updateRoomTask(form.value.taskId, payload);
      showMessage("工單修改成功", "success");
    } else {
      await roomTaskApi.createRoomTask(payload);
      showMessage("工單新增成功", "success");
    }

    // 依任務狀態自動連動更新房間狀態 (新增與編輯都適用)
    const mapping = taskTypeToRoomStatus[payload.taskType];
    if (mapping) {
      let targetRoomStatus = null;
      if (payload.taskStatus === '進行中' && mapping.doing) {
        targetRoomStatus = mapping.doing;
      } else if (payload.taskStatus === '已完成' && mapping.done) {
        targetRoomStatus = mapping.done;
      }

      if (targetRoomStatus) {
        try {
          const roomData = await roomApi.getRoomById(payload.roomId);
          const updatedRoom = {
            ...(roomData[0] ?? roomData),
            roomStatus: targetRoomStatus,
          };
          await roomApi.updateRoom(payload.roomId, updatedRoom);
          console.log(`房間 ${payload.roomId} 狀態已同步更新為 ${targetRoomStatus}`);
        } catch (roomErr) {
          console.warn("房間狀態更新失敗：", roomErr);
        }
      }
    }

    clearForm();
    showFormModal.value = false;
    await loadRoomTasks();
  } catch (error) {
    console.error("saveRoomTask Error:", error);
    showMessage(error.message || (isEdit ? "工單修改失敗" : "工單新增失敗"), "error");
  }
}

// 點擊修改：將項目載入編輯表單 (轉為 Number 以精準對應下拉選單)
function editRoomTask(task) {
  const taskId = task.taskId ?? task.task_id;
  const roomId = task.roomId ?? task.room_id ?? "";
  const employeeId = task.employeeId ?? task.employee_id ?? "";
  const priority = task.priority ?? "一般";
  const taskType = task.taskType ?? task.task_type ?? "日常清潔";
  const taskStatus = task.taskStatus ?? task.task_status ?? "待處理";
  const remark = task.remark ?? "";
  const createdAt = task.createdAt ?? task.created_at ?? "";
  const completedAt = task.completedAt ?? task.completed_at ?? "";

  form.value = {
    taskId,
    roomId: roomId !== "" ? Number(roomId) : "",
    employeeId: employeeId !== "" ? Number(employeeId) : "",
    priority,
    taskType,
    taskStatus,
    remark,
    createdAt: ensureSecondsFormat(createdAt),
    completedAt: ensureSecondsFormat(completedAt),
  };

  formTitle.value = `修改工單 ID：${taskId}`;
  showFormModal.value = true;

  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
}

// 3. 完成工單 (將狀態更新為已完成並傳給後端 PUT API)
async function completeRoomTask(task) {
  const taskId = task.taskId ?? task.task_id;
  const roomId = task.roomId ?? task.room_id;
  const employeeId = task.employeeId ?? task.employee_id;
  const priority = task.priority;
  const taskType = task.taskType ?? task.task_type;
  const remark = task.remark ?? "";
  const createdAt = task.createdAt ?? task.created_at;
  const now = getCurrentDateTime();

  const payload = {
    taskId: taskId,
    roomId: Number(roomId),
    employeeId: Number(employeeId),
    priority: priority,
    taskType: taskType,
    taskStatus: "已完成",
    remark: remark,
    createdAt: ensureSecondsFormat(createdAt),
    completedAt: now,
  };

  try {
    await roomTaskApi.updateRoomTask(taskId, payload);

    // 依任務類型自動連動更新房間狀態
    const mapping = taskTypeToRoomStatus[taskType];
    if (mapping && mapping.done) {
      try {
        // 先取得目前房間資料，再只更新 roomStatus 欄位
        const roomData = await roomApi.getRoomById(Number(roomId));
        const updatedRoom = {
          ...(roomData[0] ?? roomData),
          roomStatus: mapping.done,
        };
        await roomApi.updateRoom(Number(roomId), updatedRoom);
        showMessage(`工單 ${taskId} 已完成，房間狀態已更新為「${mapping.done}」`, "success");
      } catch (roomErr) {
        console.warn("房間狀態更新失敗：", roomErr);
        showMessage(`工單 ${taskId} 已完成（房間狀態更新失敗，請手動調整）`, "success");
      }
    } else {
      showMessage(`工單 ${taskId} 已完成`, "success");
    }

    await loadRoomTasks();
  } catch (error) {
    console.error("completeRoomTask Error:", error);
    showMessage(error.message || "更新完成狀態失敗", "error");
  }
}

// 4. 刪除房務工單 (DELETE /api/roomtask/{id})
async function deleteRoomTask(id) {
  if (!window.confirm("確定刪除這張房務工單嗎？")) {
    return;
  }

  try {
    await roomTaskApi.deleteRoomTask(id);
    showMessage("工單已刪除", "success");
    if (form.value.taskId === id) {
      clearForm();
    }
    await loadRoomTasks();
  } catch (error) {
    console.error("deleteRoomTask Error:", error);
    showMessage(error.message || "刪除失敗", "error");
  }
}

function getPriorityClass(priority) {
  return {
    low: priority === "低",
    medium: priority === "中" || priority === "一般",
    high: priority === "高" || priority === "重要",
    urgent: priority === "緊急",
  };
}

function getStatusClass(status) {
  return {
    waiting: status === "待處理",
    processing: status === "處理中" || status === "進行中",
    completed: status === "已完成",
    cancelled: status === "已取消",
  };
}

const PRIORITY_WEIGHT = { '緊急': 3, '高': 2, '重要': 2, '中': 1, '一般': 1, '低': 0 };

function getExpectedCompletionTime(task) {
  const status = task.taskStatus ?? task.task_status;
  if (status === "已完成" || status === "已取消") return null;

  const employeeId = task.employeeId ?? task.employee_id;
  if (!employeeId) return null;

  const empTasks = roomTasks.value.filter(t => {
    const s = t.taskStatus ?? t.task_status;
    const e = t.employeeId ?? t.employee_id;
    return e === employeeId && (s !== "已完成" && s !== "已取消");
  });

  empTasks.sort((a, b) => {
    const wA = PRIORITY_WEIGHT[a.priority] ?? 1;
    const wB = PRIORITY_WEIGHT[b.priority] ?? 1;
    if (wA !== wB) return wB - wA;

    const tA = new Date(a.createdAt ?? a.created_at).getTime();
    const tB = new Date(b.createdAt ?? b.created_at).getTime();
    return tA - tB;
  });

  const myId = task.taskId ?? task.task_id;
  const index = empTasks.findIndex(t => (t.taskId ?? t.task_id) === myId);
  if (index === -1) return null;

  let currentExpectedTime = 0;
  for (let i = 0; i <= index; i++) {
    const tTime = new Date(empTasks[i].createdAt ?? empTasks[i].created_at).getTime();
    if (currentExpectedTime === 0 || tTime > currentExpectedTime) {
      currentExpectedTime = tTime + 30 * 60000;
    } else {
      currentExpectedTime += 30 * 60000;
    }
  }
  return new Date(currentExpectedTime);
}

function getTaskReminder(task) {
  const expected = getExpectedCompletionTime(task);
  if (!expected) return "";

  const diffMins = Math.floor((currentTime.value.getTime() - expected.getTime()) / 60000);
  if (diffMins > 0) {
    return `⚠️ 逾時 ${diffMins} 分`;
  } else {
    return `剩餘 ${Math.abs(diffMins)} 分`;
  }
}

function isTaskLate(task) {
  const expected = getExpectedCompletionTime(task);
  if (!expected) return false;
  return currentTime.value.getTime() > expected.getTime();
}

let refreshInterval = null;

async function autoCompleteStaleTasks() {
  const staleTasks = roomTasks.value.filter((task) => {
    const status = task.taskStatus ?? task.task_status;
    const createdAt = task.createdAt ?? task.created_at;
    if (status !== '進行中' || !createdAt) return false;

    const diffMins = (currentTime.value - new Date(createdAt)) / 60000;
    return diffMins > 30; // 測試用，超過 30 分鐘就自動完成
  });

  for (const task of staleTasks) {
    console.log(`[自動完成] 工單 ${task.taskId ?? task.task_id} 已進行超過 30 分鐘，自動完成`);
    await completeRoomTask(task);
  }
}

async function autoCreateTasks() {
  loading.value = true;
  message.value = "";
  try {
    const res = await fetchClient('/api/roomtask/auto-create-from-rooms', { method: 'POST' });
    showMessage(res.message || "自動建立工單成功！", "success");
    await loadRoomTasks();
  } catch (error) {
    showMessage(error.message || "自動建立工單發生錯誤", "error");
  } finally {
    loading.value = false;
  }
}

onMounted(async () => {
  await Promise.all([loadRoomTasks(), loadRooms(), loadEmployees()]);

  refreshInterval = setInterval(async () => {
    currentTime.value = new Date();
    await loadRoomTasks();
    // 每次重載後檢查是否有工單需要自動完成
    await autoCompleteStaleTasks();
  }, 60000); // 每 60 秒檢查一次
});

onUnmounted(() => {
  if (refreshInterval) {
    clearInterval(refreshInterval);
  }
});

const quickFilterType = ref("all");
const quickFilterPriority = ref("all");
const quickFilterStatus = ref("all");

const filteredTasks = computed(() => {
  return roomTasks.value.filter(task => {
    const typeMatch = quickFilterType.value === "all" || (task.taskType ?? task.task_type) === quickFilterType.value;
    const priorityMatch = quickFilterPriority.value === "all" || task.priority === quickFilterPriority.value;
    const statusMatch = quickFilterStatus.value === "all" || (task.taskStatus ?? task.task_status) === quickFilterStatus.value;
    return typeMatch && priorityMatch && statusMatch;
  });
});

const currentPage = ref(1);
const itemsPerPage = 20;
const totalPages = computed(() => Math.ceil(filteredTasks.value.length / itemsPerPage));
const sortKey = ref("taskId");
const sortOrder = ref("desc");

function toggleSort(key) {
  if (sortKey.value === key) {
    sortOrder.value = sortOrder.value === "asc" ? "desc" : "asc";
  } else {
    sortKey.value = key;
    sortOrder.value = "desc";
  }
}

const sortedTasks = computed(() => {
  return [...filteredTasks.value].sort((a, b) => {
    let valA, valB;
    if (sortKey.value === 'taskId') {
      valA = Number(a.taskId ?? a.task_id);
      valB = Number(b.taskId ?? b.task_id);
    } else if (sortKey.value === 'reminder') {
      // 提醒狀態排序：未完成的排前面，並依照 expectedTime 排序
      const expA = getExpectedCompletionTime(a);
      const expB = getExpectedCompletionTime(b);
      if (expA && expB) {
        valA = expA.getTime();
        valB = expB.getTime();
      } else if (expA && !expB) {
        return sortOrder.value === 'asc' ? -1 : 1;
      } else if (!expA && expB) {
        return sortOrder.value === 'asc' ? 1 : -1;
      } else {
        valA = Number(a.taskId ?? a.task_id);
        valB = Number(b.taskId ?? b.task_id);
      }
    } else {
      return 0;
    }
    
    if (valA < valB) return sortOrder.value === 'asc' ? -1 : 1;
    if (valA > valB) return sortOrder.value === 'asc' ? 1 : -1;
    return 0;
  });
});

const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * itemsPerPage;
  return sortedTasks.value.slice(start, start + itemsPerPage);
});
function nextPage() { if (currentPage.value < totalPages.value) currentPage.value++; }
function prevPage() { if (currentPage.value > 1) currentPage.value--; }

</script>

<template>
  <main class="task-page">
    <header class="page-header" style="display: flex; justify-content: space-between; align-items: center;">
      <div>
        <h1>房務工單管理</h1>
        <p>管理客房清潔、維修、備品補充及員工指派</p>
      </div>
      <div style="display: flex; gap: 10px;">
        <button type="button" class="btn secondary" @click="autoCreateTasks" :disabled="loading">
          掃描房間自動建立工單
        </button>
        <button class="btn primary" @click="openAddModal" style="background-color: #A67C52; border: none;">+ 新增工單</button>
      </div>
    </header>

    <div v-if="message" class="message" :class="messageType">
      {{ message }}
    </div>

    <!-- 條件查詢與列表整合區塊 -->
    <section class="admin-card">
      
      <!-- 條件查詢 -->
      <div style="display: flex; align-items: flex-end; gap: 15px; margin-bottom: 20px; flex-wrap: wrap;">
        <div class="form-group" style="flex: 1; min-width: 150px;">
          <label>工單 ID</label>
          <input v-model.number="searchParams.taskId" type="number" placeholder="任務 ID" />
        </div>
        <div class="form-group" style="flex: 1; min-width: 150px;">
          <label>房間</label>
          <select v-model="searchParams.roomId">
            <option value="">全部</option>
            <option v-for="room in rooms" :key="room.roomId" :value="room.roomId">房號 {{ room.roomNumber }}</option>
          </select>
        </div>
        <div class="form-group" style="flex: 1; min-width: 150px;">
          <label>負責員工</label>
          <select v-model="searchParams.employeeId">
            <option value="">全部</option>
            <option v-for="employee in employees" :key="employee.employeeId" :value="employee.employeeId">{{ employee.employeeName }}</option>
          </select>
        </div>
        <div class="form-group" style="flex: 1; min-width: 150px;">
          <label>優先程度</label>
          <select v-model="searchParams.priority">
            <option value="">全部</option>
            <option v-for="priority in priorities" :key="priority" :value="priority">{{ priority }}</option>
          </select>
        </div>
        <div class="form-actions" style="margin-top: 0;">
          <button type="button" class="btn primary" @click="loadRoomTasks">搜尋</button>
          <button type="button" class="btn secondary" @click="resetSearch">重設</button>
        </div>
      </div>

      <!-- 快速切換過濾 -->
      <div style="display: flex; gap: 10px; margin-bottom: 20px;">
        <select v-model="quickFilterType" class="quick-filter-select">
          <option value="all">所有類型</option>
          <option v-for="t in taskTypes" :key="t" :value="t">{{ t }}</option>
        </select>
        <select v-model="quickFilterPriority" class="quick-filter-select">
          <option value="all">所有優先程度</option>
          <option v-for="p in priorities" :key="p" :value="p">{{ p }}</option>
        </select>
        <select v-model="quickFilterStatus" class="quick-filter-select">
          <option value="all">所有狀態</option>
          <option v-for="s in taskStatuses" :key="s" :value="s">{{ s }}</option>
        </select>
      </div>

      <div class="table-header" style="margin-bottom: 10px;">
        <span style="font-weight: 500; color: #666;">共 {{ filteredTasks.length }} 張工單</span>
      </div>

    <!-- 新增 / 編輯表單小視窗 (Modal) -->
    <div v-if="showFormModal" class="modal-overlay">
      <div class="modal-content" style="max-width: 800px; width: 90%;">
        <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px;">
          <h2 style="margin: 0;">{{ formTitle }}</h2>
          <button type="button" class="btn secondary" @click="closeFormModal" style="padding: 5px 10px;">✕</button>
        </div>

        <form @submit.prevent="saveRoomTask">
          <div class="form-grid">
            <div class="form-group">
              <label>房間 *</label>
              <select v-model="form.roomId" required>
                <option value="" disabled>請選擇房間</option>
                <option v-if="form.roomId !== '' && form.roomId !== null && !rooms.some((r) => Number(r.roomId) === Number(form.roomId))" :value="form.roomId">房號 ID: {{ form.roomId }}</option>
                <option v-for="room in rooms" :key="room.roomId" :value="room.roomId">房號 {{ room.roomNumber }}</option>
              </select>
            </div>

            <div class="form-group">
              <label>負責員工 *</label>
              <select v-model="form.employeeId" required>
                <option value="" disabled>請選擇員工</option>
                <option v-if="form.employeeId !== '' && form.employeeId !== null && !employees.some((e) => Number(e.employeeId) === Number(form.employeeId))" :value="form.employeeId">員工 ID: {{ form.employeeId }}</option>
                <option v-for="employee in filteredEmployees" :key="employee.employeeId" :value="employee.employeeId">{{ employee.employeeId }}</option>
              </select>
            </div>

            <div class="form-group">
              <label>優先程度</label>
              <select v-model="form.priority">
                <option v-for="priority in priorities" :key="priority" :value="priority">{{ priority }}</option>
              </select>
            </div>

            <div class="form-group">
              <label>工單類型</label>
              <select v-model="form.taskType">
                <option v-for="type in taskTypes" :key="type" :value="type">{{ type }}</option>
              </select>
            </div>

            <div class="form-group">
              <label>工單狀態</label>
              <select v-model="form.taskStatus">
                <option v-for="status in taskStatuses" :key="status" :value="status">{{ status }}</option>
              </select>
            </div>

            <div class="form-group">
              <label>建立時間</label>
              <input v-model="form.createdAt" type="text" placeholder="YYYY-MM-DD HH:mm:ss (留空則為現在)" />
            </div>

            <div class="form-group full-width">
              <label>備註</label>
              <textarea v-model.trim="form.remark" rows="4" placeholder="請輸入房務需求或注意事項"></textarea>
            </div>
          </div>

          <div class="form-actions" style="margin-top: 20px; display: flex; justify-content: flex-end; gap: 10px;">
            <button type="button" class="btn secondary" @click="closeFormModal">取消</button>
            <button type="submit" class="btn primary">{{ form.taskId === null ? "新增工單" : "儲存修改" }}</button>
          </div>
        </form>
      </div>
    </div>



      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th @click="toggleSort('taskId')" class="sortable">
                ID <span v-if="sortKey === 'taskId'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span>
              </th>
              <th>房號</th>
              <th>負責員工</th>
              <th>類型</th>
              <th>優先程度</th>
              <th>狀態</th>
              <th @click="toggleSort('reminder')" class="sortable">
                提醒狀態 <span v-if="sortKey === 'reminder'">{{ sortOrder === 'asc' ? '▲' : '▼' }}</span>
              </th>
              <th>建立時間</th>
              <th>完成時間</th>
              <th>備註</th>
              <th>操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="roomTasks.length === 0">
              <td colspan="10" style="text-align: center">
                目前沒有房務工單資料
              </td>
            </tr>

            <tr v-for="task in paginatedData" :key="task.taskId ?? task.task_id" :class="{'late-warning': isTaskLate(task)}">
              <td>{{ task.taskId ?? task.task_id }}</td>
              <td>{{ getRoomNumber(task.roomId ?? task.room_id) }}</td>
              <td>
                {{ getEmployeeName(task.employeeId ?? task.employee_id) }}
              </td>
              <td>{{ task.taskType ?? task.task_type }}</td>

              <td>
                <span class="tag" :class="getPriorityClass(task.priority)">
                  {{ task.priority }}
                </span>
              </td>

              <td>
                <span
                  class="tag"
                  :class="getStatusClass(task.taskStatus ?? task.task_status)"
                >
                  {{ task.taskStatus ?? task.task_status }}
                </span>
              </td>

              <td>
                <span v-if="getTaskReminder(task)" :class="{'late-text': isTaskLate(task), 'safe-text': !isTaskLate(task)}" style="font-weight: bold;">
                  {{ getTaskReminder(task) }}
                </span>
                <span v-else>-</span>
              </td>

              <td>
                {{ formatDateTimeShort(task.createdAt ?? task.created_at) }}
              </td>
              <td>
                {{ formatDateTimeShort(task.completedAt ?? task.completed_at) }}
              </td>
              <td>{{ task.remark || "—" }}</td>

              <td class="actions">
                <button class="btn edit" @click="editRoomTask(task)">
                  修改
                </button>

                <button
                  v-if="(task.taskStatus ?? task.task_status) !== '已完成'"
                  class="btn finish"
                  @click="completeRoomTask(task)"
                >
                  完成
                </button>

                <button
                  class="btn delete"
                  @click="deleteRoomTask(task.taskId ?? task.task_id)"
                >
                  刪除
                </button>
              </td>
            </tr>
          </tbody>
        </table>

      <div class="pagination-container" v-if="totalPages > 1">
        <button @click="prevPage" :disabled="currentPage === 1" class="page-btn">◀ 上一頁</button>
        <span class="page-info">第 {{ currentPage }} 頁 / 共 {{ totalPages }} 頁</span>
        <button @click="nextPage" :disabled="currentPage === totalPages" class="page-btn">下一頁 ▶</button>
      </div>
  

      </div>
    </section>
  </main>
</template>

<style scoped>
.task-page {
  padding: 28px;
  color: #243447;
}

.late-text {
  color: #e74c3c;
}
.safe-text {
  color: #27ae60;
}

.page-header {
  margin-bottom: 24px;
}

.admin-card {
  margin-bottom: 24px;
  padding: 24px;
  background: white;
  border: 1px solid #e4e7ec;
  border-radius: 12px;
  box-shadow: 0 3px 12px rgb(0 0 0 / 6%);
}

.page-header h1 {
  margin: 0 0 8px;
}

.page-header p {
  margin: 0;
  color: #667085;
}

.message {
  margin-bottom: 20px;
  padding: 12px 16px;
  border-radius: 8px;
}

.success {
  color: #176b3a;
  background: #e9f8ef;
}

.error {
  color: #b42318;
  background: #feeceb;
}

.form-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 18px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.full-width {
  grid-column: 1 / -1;
}

thead {
  background: #4a3b2a;
  color: white;
}

input,
select,
textarea {
  padding: 11px 12px;
  font: inherit;
  border: 1px solid #cfd4dc;
  border-radius: 7px;
}

.form-actions,
.actions {
  display: flex;
  gap: 8px;
}

.form-actions {
  margin-top: 20px;
}

.btn {
  padding: 8px 13px;
  color: white;
  border: none;
  border-radius: 7px;
  cursor: pointer;
}

.primary {
  background: #315b7d;
}

.secondary {
  color: #344054;
  background: #e4e7ec;
}

.edit {
  background: #d59032;
}

.finish {
  background: #20875a;
}

.delete {
  background: #c84040;
}

.table-wrapper td::before {
    display: none;
  }


.sortable {
  cursor: pointer;
  user-select: none;
}

.sortable:hover {
  background-color: rgba(0,0,0,0.05);
}

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.table-wrapper table thead th {
  background-color: #4a3b32 !important; /* 深棕色背景 */
  color: #ffffff !important; /* 純白文字 */
  overflow-x: auto;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  min-width: 90px;
  padding: 12px;
  text-align: left;
  border-bottom: 1px solid #e4e7ec;
}

th {
  background: #f8fafc;
}

.tag {
  display: inline-block;
  padding: 5px 9px;
  border-radius: 20px;
}

.low,
.completed {
  color: #176b3a;
  background: #e9f8ef;
}

.medium,
.waiting {
  color: #9a6700;
  background: #fff4ce;
}

.high,
.processing {
  color: #b54708;
  background: #ffead5;
}

.urgent,
.cancelled {
  color: #b42318;
  background: #feeceb;
}

tr.late-warning td {
  background-color: #fff1f0 !important;
  border-bottom: 1px solid #ffa39e;
}
tr.late-warning:hover td {
  background-color: #ffccc7 !important;
}

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .full-width {
    grid-column: auto;
  }
}
.pagination-container { display: flex; justify-content: center; align-items: center; margin-top: 20px; gap: 15px; } .page-btn { padding: 8px 16px; background-color: #3b82f6; color: white; border: none; border-radius: 6px; cursor: pointer; font-weight: 500; transition: background-color 0.2s; } .page-btn:hover:not(:disabled) { background-color: #2563eb; } .page-btn:disabled { background-color: #d1d5db; cursor: not-allowed; } .page-info { font-weight: 500; color: #374151; }

.quick-filter-select {
  padding: 6px 10px;
  border: 1px solid #d0d5dd;
  border-radius: 6px;
  font-size: 14px;
  background-color: #ffffff;
  color: #344054;
  outline: none;
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s;
}
.quick-filter-select:focus {
  border-color: #98a2b3;
  box-shadow: 0 0 0 3px rgba(152, 162, 179, 0.1);
}

/* Modal 樣式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #fff;
  padding: 2rem;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
}

.modal-actions button {
  min-width: 100px;
}
</style>
