<script setup>
import { onMounted, ref } from "vue";

const API_BASE_URL = "/api/roomtask";

// 下拉選單資料（已去除假資料，可由 API 動態載入或維持選單對照）
const rooms = ref([]);
const employees = ref([]);

// 核心資料列表
const roomTasks = ref([]);

// 下拉選單選項（與資料庫值對應）
const priorities = ["低", "中", "一般", "重要", "高", "緊急"];
const taskTypes = [
  "清潔",
  "退房清潔",
  "日常清潔",
  "補充備品",
  "設備維修",
  "維修",
  "備品補充",
  "房況檢查",
  "其他",
];
const taskStatuses = ["待處理", "進行中", "處理中", "已完成", "已取消"];

const message = ref("");
const messageType = ref("");
const formTitle = ref("新增房務工單");
const form = ref(createEmptyForm());

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
  formTitle.value = "新增房務工單";
}

function getAuthHeaders() {
  const token = localStorage.getItem("token");
  const headers = { "Content-Type": "application/json" };
  if (token) {
    headers.Authorization = "Bearer " + token;
  }
  return headers;
}

// 取得房間號碼 (優先對照 rooms 選單，若無則顯示 ID)
function getRoomNumber(roomId) {
  if (!roomId) return "未知房間";
  const found = rooms.value.find(
    (room) => Number(room.roomId) === Number(roomId),
  );
  return found ? found.roomNumber : `房號 ID: ${roomId}`;
}

// 取得員工姓名 (優先對照 employees 選單，若無則顯示 ID)
function getEmployeeName(employeeId) {
  if (!employeeId) return "未指派";
  const found = employees.value.find(
    (emp) => Number(emp.employeeId) === Number(employeeId),
  );
  return found ? found.employeeName : `員工 ID: ${employeeId}`;
}

// 傳給後端的時間格式：YYYY-MM-DD HH:mm:ss (長度 19)
function getCurrentDateTime() {
  return new Date().toLocaleString("sv-SE").slice(0, 19);
}

// 前端畫面顯示用的時間格式：只保留到幾點幾分 (長度 16)
function formatDateTimeShort(dateTimeStr) {
  if (!dateTimeStr) return "—";
  return String(dateTimeStr).slice(0, 16);
}

// 確保傳給後端的時間格式包含秒數
function ensureSecondsFormat(dateTimeStr) {
  if (!dateTimeStr) return "";
  const str = String(dateTimeStr);
  if (str.length === 16) {
    return str + ":00";
  }
  return str;
}

// 1. 載入房務工單列表 (GET /api/roomtask)
async function loadRoomTasks() {
  try {
    const response = await fetch(API_BASE_URL, {
      method: "GET",
      headers: getAuthHeaders(),
      credentials: "include",
    });

    if (!response.ok) {
      if (response.status === 403) {
        showMessage("權限不足或登入已過期 (403)", "error");
        return;
      }
      const errorData = await response.json().catch(() => ({}));
      showMessage(errorData.message || "載入房務工單失敗", "error");
      return;
    }

    const data = await response.json();
    roomTasks.value = Array.isArray(data) ? data : data ? [data] : [];
  } catch (error) {
    console.error("loadRoomTasks Error:", error);
    showMessage("無法連線至房務工單 API", "error");
  }
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
  const url = isEdit ? `${API_BASE_URL}/${form.value.taskId}` : API_BASE_URL;
  const method = isEdit ? "PUT" : "POST";

  try {
    const response = await fetch(url, {
      method,
      headers: getAuthHeaders(),
      credentials: "include",
      body: JSON.stringify(payload),
    });

    const resData = await response.json().catch(() => ({}));

    if (!response.ok) {
      if (response.status === 403) {
        showMessage("權限不足或登入已過期 (403)", "error");
        return;
      }
      showMessage(
        resData.message || (isEdit ? "工單修改失敗" : "工單新增失敗"),
        "error",
      );
      return;
    }

    showMessage(isEdit ? "工單修改成功" : "工單新增成功", "success");
    clearForm();
    await loadRoomTasks();
  } catch (error) {
    console.error("saveRoomTask Error:", error);
    showMessage("無法連線至房務工單 API", "error");
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
    const response = await fetch(`${API_BASE_URL}/${taskId}`, {
      method: "PUT",
      headers: getAuthHeaders(),
      credentials: "include",
      body: JSON.stringify(payload),
    });

    const resData = await response.json().catch(() => ({}));

    if (!response.ok) {
      if (response.status === 403) {
        showMessage("權限不足或登入已過期 (403)", "error");
        return;
      }
      showMessage(resData.message || "更新完成狀態失敗", "error");
      return;
    }

    showMessage(`工單 ${taskId} 已完成`, "success");
    await loadRoomTasks();
  } catch (error) {
    console.error("completeRoomTask Error:", error);
    showMessage("無法連線至房務工單 API", "error");
  }
}

// 4. 刪除房務工單 (DELETE /api/roomtask/{id})
async function deleteRoomTask(id) {
  if (!window.confirm("確定刪除這張房務工單嗎？")) {
    return;
  }

  try {
    const response = await fetch(`${API_BASE_URL}/${id}`, {
      method: "DELETE",
      headers: getAuthHeaders(),
      credentials: "include",
    });

    const resData = await response.json().catch(() => ({}));

    if (!response.ok) {
      if (response.status === 403) {
        showMessage("權限不足或登入已過期 (403)", "error");
        return;
      }
      showMessage(resData.message || "刪除失敗", "error");
      return;
    }

    showMessage(resData.message || "工單已刪除", "success");
    if (form.value.taskId === id) {
      clearForm();
    }
    await loadRoomTasks();
  } catch (error) {
    console.error("deleteRoomTask Error:", error);
    showMessage("無法連線至房務工單 API", "error");
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

onMounted(async () => {
  await loadRoomTasks();
});
</script>

<template>
  <main class="task-page">
    <header class="page-header">
      <h1>房務工單管理</h1>
      <p>管理客房清潔、維修、備品補充及員工指派</p>
    </header>

    <div v-if="message" class="message" :class="messageType">
      {{ message }}
    </div>

    <section class="admin-card">
      <h2>{{ formTitle }}</h2>

      <form @submit.prevent="saveRoomTask">
        <div class="form-grid">
          <div class="form-group">
            <label>房間 *</label>

            <select v-model="form.roomId" required>
              <option value="" disabled>請選擇房間</option>

              <!-- 防護選項：當選單陣列無此 ID 時，自動填入目前 ID 避免顯示空白 -->
              <option
                v-if="
                  form.roomId !== '' &&
                  form.roomId !== null &&
                  !rooms.some((r) => Number(r.roomId) === Number(form.roomId))
                "
                :value="form.roomId"
              >
                房號 ID: {{ form.roomId }}
              </option>

              <option
                v-for="room in rooms"
                :key="room.roomId"
                :value="room.roomId"
              >
                房號 {{ room.roomNumber }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>負責員工 *</label>

            <select v-model="form.employeeId" required>
              <option value="" disabled>請選擇員工</option>

              <!-- 防護選項：當選單陣列無此 ID 時，自動填入目前 ID 避免顯示空白 -->
              <option
                v-if="
                  form.employeeId !== '' &&
                  form.employeeId !== null &&
                  !employees.some(
                    (e) => Number(e.employeeId) === Number(form.employeeId),
                  )
                "
                :value="form.employeeId"
              >
                員工 ID: {{ form.employeeId }}
              </option>

              <option
                v-for="employee in employees"
                :key="employee.employeeId"
                :value="employee.employeeId"
              >
                {{ employee.employeeName }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>優先程度</label>

            <select v-model="form.priority">
              <option
                v-for="priority in priorities"
                :key="priority"
                :value="priority"
              >
                {{ priority }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>工單類型</label>

            <select v-model="form.taskType">
              <option v-for="type in taskTypes" :key="type" :value="type">
                {{ type }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>工單狀態</label>

            <select v-model="form.taskStatus">
              <option
                v-for="status in taskStatuses"
                :key="status"
                :value="status"
              >
                {{ status }}
              </option>
            </select>
          </div>

          <div class="form-group full-width">
            <label>備註</label>

            <textarea
              v-model.trim="form.remark"
              rows="4"
              placeholder="請輸入房務需求或注意事項"
            ></textarea>
          </div>
        </div>

        <div class="form-actions">
          <button type="submit" class="btn primary">
            {{ form.taskId === null ? "新增工單" : "儲存修改" }}
          </button>

          <button type="button" class="btn secondary" @click="clearForm">
            清除表單
          </button>
        </div>
      </form>
    </section>

    <section class="admin-card">
      <div class="table-header">
        <h2>房務工單列表</h2>
        <span>共 {{ roomTasks.length }} 張工單</span>
      </div>

      <div class="table-wrapper">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>房號</th>
              <th>負責員工</th>
              <th>類型</th>
              <th>優先程度</th>
              <th>狀態</th>
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

            <tr v-for="task in roomTasks" :key="task.taskId ?? task.task_id">
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
      </div>
    </section>
  </main>
</template>

<style scoped>
.task-page {
  padding: 28px;
  color: #243447;
}

.page-header,
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

.table-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.table-wrapper {
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

@media (max-width: 768px) {
  .form-grid {
    grid-template-columns: 1fr;
  }

  .full-width {
    grid-column: auto;
  }
}
</style>
