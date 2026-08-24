<script setup>
import { ref } from "vue";

const rooms = ref([
  { roomId: 1, roomNumber: "301" },
  { roomId: 2, roomNumber: "501" },
]);

const employees = ref([
  { employeeId: 1, employeeName: "王小明" },
  { employeeId: 2, employeeName: "陳小美" },
]);

const roomTasks = ref([
  {
    taskId: 1,
    roomId: 2,
    employeeId: 1,
    priority: "高",
    taskType: "清潔",
    taskStatus: "處理中",
    remark: "房客退房後進行完整清潔",
    createdAt: "2026-08-24 10:30",
    completedAt: "",
  },
]);

const priorities = ["低", "中", "高", "緊急"];
const taskTypes = ["清潔", "維修", "備品補充", "房況檢查", "其他"];
const taskStatuses = ["待處理", "處理中", "已完成", "已取消"];

const message = ref("");
const messageType = ref("");
const formTitle = ref("新增房務工單");
const form = ref(createEmptyForm());

function createEmptyForm() {
  return {
    taskId: null,
    roomId: "",
    employeeId: "",
    priority: "中",
    taskType: "清潔",
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

function getRoomNumber(roomId) {
  return (
    rooms.value.find((room) => room.roomId === Number(roomId))
      ?.roomNumber ?? "未知房間"
  );
}

function getEmployeeName(employeeId) {
  return (
    employees.value.find(
      (employee) => employee.employeeId === Number(employeeId),
    )?.employeeName ?? "未指派"
  );
}

function getCurrentDateTime() {
  return new Date().toLocaleString("sv-SE").slice(0, 16);
}

function saveRoomTask() {
  if (!form.value.roomId) {
    showMessage("請選擇房間", "error");
    return;
  }

  if (!form.value.employeeId) {
    showMessage("請選擇負責員工", "error");
    return;
  }

  const taskData = {
    ...form.value,
    roomId: Number(form.value.roomId),
    employeeId: Number(form.value.employeeId),
    createdAt: form.value.createdAt || getCurrentDateTime(),
    completedAt:
      form.value.taskStatus === "已完成"
        ? form.value.completedAt || getCurrentDateTime()
        : "",
  };

  if (form.value.taskId === null) {
    const nextId =
      roomTasks.value.length === 0
        ? 1
        : Math.max(...roomTasks.value.map((task) => task.taskId)) + 1;

    roomTasks.value.push({
      ...taskData,
      taskId: nextId,
    });

    showMessage("工單新增成功", "success");
  } else {
    const index = roomTasks.value.findIndex(
      (task) => task.taskId === form.value.taskId,
    );

    if (index !== -1) {
      roomTasks.value[index] = taskData;
    }

    showMessage("工單修改成功", "success");
  }

  clearForm();
}

function editRoomTask(task) {
  form.value = { ...task };
  formTitle.value = `修改工單 ID：${task.taskId}`;

  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
}

function completeRoomTask(task) {
  task.taskStatus = "已完成";
  task.completedAt = getCurrentDateTime();
  showMessage(`工單 ${task.taskId} 已完成`, "success");
}

function deleteRoomTask(id) {
  if (!window.confirm("確定刪除這張房務工單嗎？")) {
    return;
  }

  roomTasks.value = roomTasks.value.filter(
    (task) => task.taskId !== id,
  );

  if (form.value.taskId === id) {
    clearForm();
  }

  showMessage("工單已刪除", "success");
}

function getPriorityClass(priority) {
  return {
    low: priority === "低",
    medium: priority === "中",
    high: priority === "高",
    urgent: priority === "緊急",
  };
}

function getStatusClass(status) {
  return {
    waiting: status === "待處理",
    processing: status === "處理中",
    completed: status === "已完成",
    cancelled: status === "已取消",
  };
}
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
              <option
                v-for="type in taskTypes"
                :key="type"
                :value="type"
              >
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
            <tr v-for="task in roomTasks" :key="task.taskId">
              <td>{{ task.taskId }}</td>
              <td>{{ getRoomNumber(task.roomId) }}</td>
              <td>{{ getEmployeeName(task.employeeId) }}</td>
              <td>{{ task.taskType }}</td>

              <td>
                <span
                  class="tag"
                  :class="getPriorityClass(task.priority)"
                >
                  {{ task.priority }}
                </span>
              </td>

              <td>
                <span
                  class="tag"
                  :class="getStatusClass(task.taskStatus)"
                >
                  {{ task.taskStatus }}
                </span>
              </td>

              <td>{{ task.createdAt }}</td>
              <td>{{ task.completedAt || "—" }}</td>
              <td>{{ task.remark || "—" }}</td>

              <td class="actions">
                <button class="btn edit" @click="editRoomTask(task)">
                  修改
                </button>

                <button
                  v-if="task.taskStatus !== '已完成'"
                  class="btn finish"
                  @click="completeRoomTask(task)"
                >
                  完成
                </button>

                <button
                  class="btn delete"
                  @click="deleteRoomTask(task.taskId)"
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