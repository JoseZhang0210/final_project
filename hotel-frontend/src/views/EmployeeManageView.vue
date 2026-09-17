<template>
  <div class="employee-manage-page">
    <!-- 頁面標題列 -->
    <div class="admin-page-header">
      <div>
        <h1>員工管理</h1>
        <p>管理員工帳號、職位部門、個人資料與啟用狀態</p>
      </div>

      <div class="admin-header-actions">
        <button
          type="button"
          class="admin-btn admin-btn-secondary admin-json-button"
          :disabled="exporting || importing"
          @click="openExportModal"
        >
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 3v12m0 0 4-4m-4 4-4-4M5 21h14a2 2 0 0 0 2-2v-3M3 16v3a2 2 0 0 0 2 2" />
          </svg>
          {{ exporting ? "匯出中..." : "匯出 JSON" }}
        </button>

        <button
          type="button"
          class="admin-btn admin-btn-secondary admin-json-button"
          :disabled="exporting || importing"
          @click="openImportModal"
        >
          <svg viewBox="0 0 24 24" aria-hidden="true">
            <path d="M12 21V9m0 0 4 4m-4-4-4 4M5 3h14a2 2 0 0 1 2 2v3M3 8V5a2 2 0 0 1 2-2" />
          </svg>
          {{ importing ? "匯入中..." : "匯入 JSON" }}
        </button>

        <button
          type="button"
          class="admin-btn admin-btn-primary"
          @click="openCreateModal"
        >
          ＋ 新增員工
        </button>

        <button
          type="button"
          class="admin-btn admin-btn-secondary admin-icon-btn"
          @click="openDepartmentModal"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="17"
            height="17"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="lucide-icon"
          >
            <path d="M6 22V4a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v18Z" />
            <path d="M6 12H4a2 2 0 0 0-2 2v6a2 2 0 0 0 2 2h2" />
            <path d="M18 9h2a2 2 0 0 1 2 2v9a2 2 0 0 1-2 2h-2" />
            <path d="M10 6h4" />
            <path d="M10 10h4" />
            <path d="M10 14h4" />
            <path d="M10 18h4" />
          </svg>
          <span>部門管理</span>
        </button>

        <button
          type="button"
          class="admin-btn admin-btn-secondary admin-icon-btn"
          @click="openPermissionModal"
        >
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="17"
            height="17"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
            class="lucide-icon"
          >
            <path d="M12.22 2h-.44a2 2 0 0 0-2 2v.18a2 2 0 0 1-1 1.73l-.43.25a2 2 0 0 1-2 0l-.15-.08a2 2 0 0 0-2.73.73l-.22.38a2 2 0 0 0 .73 2.73l.15.1a2 2 0 0 1 1 1.72v.51a2 2 0 0 1-1 1.74l-.15.09a2 2 0 0 0-.73 2.73l.22.38a2 2 0 0 0 2.73.73l.15-.08a2 2 0 0 1 2 0l.43.25a2 2 0 0 1 1 1.73V20a2 2 0 0 0 2 2h.44a2 2 0 0 0 2-2v-.18a2 2 0 0 1 1-1.73l.43-.25a2 2 0 0 1 2 0l.15.08a2 2 0 0 0 2.73-.73l.22-.38a2 2 0 0 0-.73-2.73l-.15-.08a2 2 0 0 1-1-1.74v-.5a2 2 0 0 1 1-1.74l.15-.09a2 2 0 0 0 .73-2.73l-.22-.38a2 2 0 0 0-2.73-.73l-.15.08a2 2 0 0 1-2 0l-.43-.25a2 2 0 0 1-1-1.73V4a2 2 0 0 0-2-2z" />
            <circle cx="12" cy="12" r="3" />
          </svg>
          <span>權限種類管理</span>
        </button>
      </div>
    </div>

    <!-- 員工管理卡片 -->
    <section class="admin-card">
      <!-- 搜尋 / 篩選 -->
      <div class="admin-search-bar">
        <input
          v-model="keyword"
          type="text"
          class="admin-input search-input"
          placeholder="搜尋帳號、姓名、部門、職位、權限、信箱、電話..."
          @keyup.enter="resetPage"
        />

        <select
          v-model="selectedDepartment"
          class="admin-input filter-select"
          @change="resetPage"
        >
          <option value="">全部部門</option>
          <option
            v-for="dept in departments"
            :key="dept.id"
            :value="dept.id"
          >
            {{ dept.name }}
          </option>
        </select>

        <select
          v-model="selectedStatus"
          class="admin-input filter-select"
          @change="resetPage"
        >
          <option value="">全部狀態</option>
          <option value="1">啟用</option>
          <option value="0">停用</option>
        </select>

        <button
          type="button"
          class="admin-btn admin-btn-primary"
          @click="resetPage"
        >
          搜尋
        </button>

        <button
          type="button"
          class="admin-btn admin-btn-secondary"
          @click="resetSearch"
        >
          重設
        </button>
      </div>

      <!-- 資料控制列 -->
      <div class="table-control-bar">
        <div class="filter-summary">
          目前共有
          <strong>{{ sortedEmployees.length }}</strong>
          位員工
          <span v-if="sortedEmployees.length !== employees.length" class="total-hint">
            （全體共 {{ employees.length }} 位）
          </span>
          <span v-if="selectedEmployeeIds.length > 0" class="selected-hint">
            已選取 <strong>{{ selectedEmployeeIds.length }}</strong> 位員工
            <button type="button" class="link-btn" @click="clearSelection">清除選取</button>
          </span>
        </div>

        <div class="page-size-area">
          <label> 每頁顯示 </label>
          <select v-model.number="pageSize" class="page-size-select">
            <option :value="5">5</option>
            <option :value="10">10</option>
            <option :value="20">20</option>
            <option :value="50">50</option>
          </select>
          <span> 筆 </span>
        </div>
      </div>

      <!-- Loading -->
      <div v-if="loading" class="loading-message">員工資料讀取中...</div>

      <!-- 員工表格 -->
      <div v-else class="admin-table-wrapper">
        <table class="admin-table admin-table-fixed employee-table">
          <colgroup>
            <col style="width: 48px;" />
            <col style="width: 65px;" />
            <col style="width: 160px;" />
            <col style="width: 150px;" />
            <col style="width: 185px;" />
            <col style="width: 200px;" />
            <col style="width: 75px;" />
            <col style="width: 85px;" />
            <col style="width: 165px;" />
          </colgroup>
          <thead>
            <tr>
              <th style="text-align: center;">
                <input
                  type="checkbox"
                  :checked="isAllSelected(paginatedEmployees, (e) => e.employeeId ?? e.id)"
                  @change="(ev) => toggleSelectAll(ev, paginatedEmployees, (e) => e.employeeId ?? e.id)"
                  title="全選 / 取消全選本頁"
                />
              </th>

              <th class="sortable" @click="changeSort('employeeId', resetPage)">
                ID
                <span class="sort-icon">{{ getSortIcon("employeeId") }}</span>
              </th>

              <th class="sortable" @click="changeSort('username', resetPage)">
                帳號 / 姓名
                <span class="sort-icon">{{ getSortIcon("username") }}</span>
              </th>

              <th class="sortable" @click="changeSort('department', resetPage)">
                部門 / 職位
                <span class="sort-icon">{{ getSortIcon("department") }}</span>
              </th>

              <th>權限</th>
              <th>聯絡方式</th>

              <th class="sortable" style="text-align: center;" @click="changeSort('gender', resetPage)">
                性別
                <span class="sort-icon">{{ getSortIcon("gender") }}</span>
              </th>

              <th class="sortable" style="text-align: center;" @click="changeSort('status', resetPage)">
                狀態
                <span class="sort-icon">{{ getSortIcon("status") }}</span>
              </th>

              <th style="text-align: center;">操作</th>
            </tr>
          </thead>

          <tbody>
            <tr v-if="paginatedEmployees.length === 0">
              <td colspan="9" class="empty-message">查無符合條件的員工</td>
            </tr>

            <tr
              v-for="employee in paginatedEmployees"
              :key="employee.employeeId ?? employee.id"
              :class="{ 'row-selected': selectedEmployeeIds.includes(employee.employeeId ?? employee.id) }"
            >
              <td style="text-align: center;">
                <input
                  type="checkbox"
                  :value="employee.employeeId ?? employee.id"
                  v-model="selectedEmployeeIds"
                />
              </td>

              <td>{{ employee.employeeId ?? employee.id }}</td>

              <td>
                <div class="name-cell">
                  <span class="item-name" :title="employee.name">
                    {{ employee.name || "未填姓名" }}
                    <span v-if="isSelf(employee)" class="self-tag">(本人)</span>
                  </span>
                  <span class="item-username" :title="'@' + employee.username">(@{{ employee.username }})</span>
                </div>
              </td>

              <td>
                <div class="dept-pos-cell">
                  <span class="dept-badge" :title="getDepartmentName(employee.departmentId, employee.departmentName)">
                    {{ getDepartmentName(employee.departmentId, employee.departmentName) }}
                  </span>
                  <span class="position-text" :title="employee.position">{{ employee.position || "未設定職位" }}</span>
                </div>
              </td>

              <td class="perms-td">
                <div
                  v-if="employee.permissionNames && employee.permissionNames.length > 0"
                  class="employee-perms-container"
                  :title="employee.permissionNames.join('、')"
                >
                  <div class="employee-perms-tags">
                    <span
                      v-for="(pName, pIdx) in employee.permissionNames.slice(0, 3)"
                      :key="pIdx"
                      class="perm-badge"
                    >
                      {{ pName }}
                    </span>
                    <span
                      v-if="employee.permissionNames.length > 3"
                      class="perm-more-badge"
                    >
                      +{{ employee.permissionNames.length - 3 }}
                    </span>
                  </div>

                  <!-- 懸停顯示完整權限 Tooltip Popover -->
                  <div class="perms-hover-popover">
                    <div class="perms-popover-header">
                      <span>全部權限</span>
                      <span class="perms-popover-count">{{ employee.permissionNames.length }} 項</span>
                    </div>
                    <div class="perms-popover-tags">
                      <span
                        v-for="(pName, pIdx) in employee.permissionNames"
                        :key="pIdx"
                        class="perm-badge"
                      >
                        {{ pName }}
                      </span>
                    </div>
                  </div>
                </div>
                <span v-else class="text-muted">—</span>
              </td>

              <td>
                <div class="contact-info">
                  <div v-if="employee.phone" class="contact-item" :title="employee.phone">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="14"
                      height="14"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      class="lucide-icon inline-icon"
                    >
                      <path d="M22 16.92v3a2 2 0 0 1-2.18 2 19.79 19.79 0 0 1-8.63-3.07 19.5 19.5 0 0 1-6-6 19.79 19.79 0 0 1-3.07-8.67A2 2 0 0 1 4.11 2h3a2 2 0 0 1 2 1.72 12.84 12.84 0 0 0 .7 2.81 2 2 0 0 1-.45 2.11L8.09 9.91a16 16 0 0 0 6 6l1.27-1.27a2 2 0 0 1 2.11-.45 12.84 12.84 0 0 0 2.81.7A2 2 0 0 1 22 16.92z" />
                    </svg>
                    <span>{{ employee.phone }}</span>
                  </div>
                  <div v-if="employee.email" class="contact-item" :title="employee.email">
                    <svg
                      xmlns="http://www.w3.org/2000/svg"
                      width="14"
                      height="14"
                      viewBox="0 0 24 24"
                      fill="none"
                      stroke="currentColor"
                      stroke-width="2"
                      stroke-linecap="round"
                      stroke-linejoin="round"
                      class="lucide-icon inline-icon"
                    >
                      <rect width="20" height="16" x="2" y="4" rx="2" />
                      <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7" />
                    </svg>
                    <span>{{ employee.email }}</span>
                  </div>
                  <span v-if="!employee.phone && !employee.email" class="text-muted">未填寫</span>
                </div>
              </td>

              <td style="text-align: center;">
                <span class="gender-text">{{ employee.gender || "—" }}</span>
              </td>

              <td style="text-align: center;">
                <span
                  class="status-badge"
                  :class="isActiveStatus(employee.status) ? 'status-active' : 'status-inactive'"
                >
                  {{ getStatusLabel(employee.status) }}
                </span>
              </td>

              <td>
                <div class="action-buttons">
                  <button
                    type="button"
                    class="admin-btn admin-btn-status"
                    @click="toggleStatus(employee)"
                  >
                    {{ isActiveStatus(employee.status) ? "停用" : "啟用" }}
                  </button>

                  <button
                    type="button"
                    class="admin-btn admin-btn-edit"
                    @click="openEditModal(employee)"
                  >
                    修改
                  </button>

                  <button
                    type="button"
                    class="admin-btn admin-btn-delete"
                    @click="deleteEmployee(employee)"
                  >
                    刪除
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分頁元件 -->
      <AdminPagination
        :current-page="currentPage"
        :total-pages="totalPages"
        :visible-pages="visiblePages"
        :loading="loading"
        :total-count="sortedEmployees.length"
        @page-change="goToPage"
      />
    </section>

    <!-- 新增 / 修改 Modal -->
    <div v-if="modalOpen" class="admin-modal-backdrop" @click.self="closeModal">
      <div class="admin-modal-card">
        <div class="admin-modal-header">
          <div>
            <h2>{{ editingEmployeeId === null ? "新增員工" : "修改員工資料" }}</h2>
            <p>{{ editingEmployeeId === null ? "建立新的員工帳號、設定職位與個人檔案" : `編輯員工 #${editingEmployeeId} 資料` }}</p>
          </div>
          <button type="button" class="modal-close" @click="closeModal">×</button>
        </div>

        <form class="admin-modal-body" @submit.prevent="saveEmployee">
          <!-- 帳號設定 -->
          <div class="form-section-title">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              class="lucide-icon section-icon"
            >
              <rect width="18" height="11" x="3" y="11" rx="2" ry="2" />
              <path d="M7 11V7a5 5 0 0 1 10 0v4" />
            </svg>
            <span>帳號設定</span>
          </div>
          <div class="admin-form-grid">
            <div class="admin-form-group">
              <label> 帳號 <span class="required">*</span> </label>
              <input v-model="form.username" type="text" placeholder="請輸入登入帳號" required />
            </div>

            <div class="admin-form-group">
              <label> 密碼 </label>
              <input
                v-model="form.password"
                type="password"
                autocomplete="new-password"
                :placeholder="editingEmployeeId === null ? '若不填則預設 123456' : '留空表示不修改密碼'"
              />
            </div>

            <div class="admin-form-group full-width">
              <label> 帳號狀態 </label>
              <select
                v-model="form.status"
                :disabled="isEditingSelf"
                :title="isEditingSelf ? '無法變更目前登入中帳號之狀態' : ''"
              >
                <option value="1">啟用</option>
                <option value="0">停用</option>
              </select>
            </div>
          </div>

          <!-- 部門與職位 -->
          <div class="form-section-title">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              class="lucide-icon section-icon"
            >
              <rect width="16" height="20" x="4" y="2" rx="2" ry="2" />
              <path d="M9 22v-4h6v4" />
              <path d="M8 6h.01" />
              <path d="M16 6h.01" />
              <path d="M12 6h.01" />
              <path d="M12 10h.01" />
              <path d="M12 14h.01" />
              <path d="M16 10h.01" />
              <path d="M16 14h.01" />
              <path d="M8 10h.01" />
              <path d="M8 14h.01" />
            </svg>
            <span>部門與職位設定</span>
          </div>
          <div class="admin-form-grid">
            <div class="admin-form-group">
              <label> 所屬部門 </label>
              <select v-model="form.departmentId">
                <option v-for="dept in departments" :key="dept.id" :value="dept.id">
                  {{ dept.name }}
                </option>
                <option value="__NEW__">➕ 自訂新部門...</option>
              </select>
            </div>

            <div v-if="form.departmentId === '__NEW__'" class="admin-form-group">
              <label> 輸入自訂部門名稱 <span class="required">*</span> </label>
              <input v-model="form.customDepartmentName" type="text" placeholder="例如：營運發展部" required />
            </div>

            <div class="admin-form-group" :class="{ 'full-width': form.departmentId !== '__NEW__' }">
              <label> 職位名稱 </label>
              <input v-model="form.position" type="text" placeholder="例如：櫃檯經理、行政主管" />
            </div>
          </div>

          <!-- 個人基本資料 -->
          <div class="form-section-title">
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="18"
              height="18"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              class="lucide-icon section-icon"
            >
              <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
              <circle cx="12" cy="7" r="4" />
            </svg>
            <span>個人基本資料</span>
          </div>
          <div class="admin-form-grid">
            <div class="admin-form-group">
              <label> 姓名 </label>
              <input v-model="form.name" type="text" placeholder="請輸入真實姓名" />
            </div>

            <div class="admin-form-group">
              <label> 性別 </label>
              <select v-model="form.gender">
                <option value="男">男</option>
                <option value="女">女</option>
                <option value="其他">其他</option>
              </select>
            </div>

            <div class="admin-form-group">
              <label> 電子信箱 </label>
              <input v-model="form.email" type="email" placeholder="例：staff@hotel.com" />
            </div>

            <div class="admin-form-group">
              <label> 聯絡電話 </label>
              <input v-model="form.phone" type="text" placeholder="例：0912345678" />
            </div>

            <div class="admin-form-group">
              <label> 出生日期 </label>
              <input v-model="form.birthday" type="date" />
            </div>

            <div class="admin-form-group">
              <label> 郵遞區號 </label>
              <input v-model="form.zipcode" type="text" placeholder="例：320" />
            </div>

            <div class="admin-form-group">
              <label> 縣市 </label>
              <input v-model="form.city" type="text" placeholder="例：桃園市" />
            </div>

            <div class="admin-form-group">
              <label> 鄉鎮市區 </label>
              <input v-model="form.district" type="text" placeholder="例：中壢區" />
            </div>

            <div class="admin-form-group full-width">
              <label> 詳細地址 </label>
              <input v-model="form.address" type="text" placeholder="請輸入詳細街道地址" />
            </div>

            <div class="admin-form-group full-width">
              <label> 員工頭像 </label>
              <div class="admin-avatar-upload-box">
                <div class="admin-avatar-preview-circle">
                  <img v-if="form.avatarUrl" :src="form.avatarUrl" alt="Avatar Preview" class="admin-avatar-img" />
                  <span v-else>{{ (form.name || form.username || "員").charAt(0) }}</span>
                </div>
                <div v-if="editingEmployeeId !== null" class="admin-avatar-input-group">
                  <input
                    type="file"
                    accept="image/*"
                    class="admin-avatar-file-input"
                    :disabled="uploadingAvatar"
                    @change="handleAdminAvatarUpload"
                  />
                  <span class="input-hint-text">{{ uploadingAvatar ? "上傳中..." : "選擇圖片上傳更新頭像" }}</span>
                </div>
                <span v-else class="input-hint-text">（新增員工完成後即可進行頭像上傳）</span>
              </div>
            </div>
          </div>

          <!-- 權限設定 -->
          <div class="form-section-title perm-section-header">
            <div class="section-title-content">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                width="18"
                height="18"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                stroke-width="2"
                stroke-linecap="round"
                stroke-linejoin="round"
                class="lucide-icon section-icon"
              >
                <path d="M20 13c0 5-3.5 7.5-7.66 8.95a1 1 0 0 1-.67-.01C7.5 20.5 4 18 4 13V6a1 1 0 0 1 1-1c2 0 4.5-1.2 6.24-2.72a1.17 1.17 0 0 1 1.52 0C14.51 3.81 17 5 19 5a1 1 0 0 1 1 1z" />
              </svg>
              <span>權限設定</span>
            </div>
            <div class="perm-header-actions">
              <button type="button" class="link-action-btn" @click="selectAllPermissions">全選</button>
              <button type="button" class="link-action-btn" @click="clearAllPermissions">清空</button>
            </div>
          </div>
          <div class="perm-selector-wrapper">
            <div v-if="permissions.length === 0" class="perm-empty-tip">
              目前系統尚無權限種類，可點擊頁面上方「⚙ 權限種類管理」進行新增。
            </div>
            <div v-else class="perm-checkbox-grid">
              <label
                v-for="perm in permissions"
                :key="perm.permissionId"
                class="perm-checkbox-card"
                :class="{ active: form.permissionIds.includes(perm.permissionId) }"
              >
                <input
                  type="checkbox"
                  :value="perm.permissionId"
                  v-model="form.permissionIds"
                />
                <div class="perm-card-content">
                  <span class="perm-card-name">{{ perm.permissionName }}</span>
                  <span class="perm-card-code">{{ perm.permissionCode }}</span>
                </div>
              </label>
            </div>
          </div>

          <div class="admin-modal-footer">
            <button type="button" class="admin-btn admin-btn-secondary" @click="closeModal">取消</button>
            <button type="submit" class="admin-btn admin-btn-primary" :disabled="saving">
              {{ saving ? "儲存中..." : "儲存" }}
            </button>
          </div>
        </form>
      </div>
    </div>

    <!-- 權限種類管理 Modal -->
    <div
      v-if="permissionModalOpen"
      class="category-modal"
      @click.self="closePermissionModal"
    >
      <div class="category-manage-card">
        <div class="category-modal-header">
          <div>
            <h2>權限種類管理</h2>
            <p>新增、修改或刪除系統員工權限種類</p>
          </div>
          <button type="button" class="modal-close" @click="closePermissionModal">×</button>
        </div>

        <div class="category-create-area">
          <label> 新增權限種類 </label>
          <div class="perm-create-row">
            <input
              id="newPermissionName"
              v-model="newPermissionName"
              type="text"
              placeholder="權限名稱，例如：訂單管理"
              maxlength="50"
              @keyup.enter="addPermission"
            />
            <input
              v-model="newPermissionCode"
              type="text"
              placeholder="權限代碼，例如：ORDER_MANAGE"
              maxlength="50"
              @keyup.enter="addPermission"
            />
            <button
              type="button"
              class="admin-btn admin-btn-primary"
              :disabled="permissionSaving"
              @click="addPermission"
            >
              {{ permissionSaving ? "新增中..." : "＋ 新增" }}
            </button>
          </div>
        </div>

        <div class="category-divider"></div>

        <div class="category-list-area">
          <div class="category-list-title">
            <span> 目前權限種類 </span>
            <span class="category-count"> {{ permissions.length }} 種 </span>
          </div>

          <div v-if="permissions.length === 0" class="category-empty">尚無權限種類</div>

          <div
            v-for="perm in permissions"
            :key="perm.permissionId"
            class="category-manage-row"
          >
            <template v-if="editingPermissionId !== perm.permissionId">
              <div class="category-info perm-info-row">
                <span class="category-id"> #{{ perm.permissionId }} </span>
                <strong class="perm-title">{{ perm.permissionName }}</strong>
                <span class="perm-code-tag">{{ perm.permissionCode }}</span>
              </div>
              <div class="category-actions">
                <button type="button" class="category-edit-button" @click="startEditPermission(perm)">修改</button>
                <button type="button" class="category-delete-button" @click="deletePermission(perm)">刪除</button>
              </div>
            </template>

            <template v-else>
              <div class="perm-edit-row">
                <input
                  v-model="editingPermissionName"
                  type="text"
                  class="category-edit-input"
                  placeholder="權限名稱"
                  maxlength="50"
                  @keyup.enter="updatePermission(perm.permissionId)"
                />
                <input
                  v-model="editingPermissionCode"
                  type="text"
                  class="category-edit-input"
                  placeholder="權限代碼"
                  maxlength="50"
                  @keyup.enter="updatePermission(perm.permissionId)"
                />
              </div>
              <div class="category-actions">
                <button
                  type="button"
                  class="category-save-button"
                  :disabled="permissionUpdating"
                  @click="updatePermission(perm.permissionId)"
                >
                  {{ permissionUpdating ? "儲存中" : "儲存" }}
                </button>
                <button type="button" class="category-cancel-button" @click="cancelEditPermission">取消</button>
              </div>
            </template>
          </div>
        </div>

        <div class="category-modal-footer">
          <button type="button" class="admin-btn admin-btn-secondary" @click="closePermissionModal">關閉</button>
        </div>
      </div>
    </div>

    <!-- 部門管理 Modal -->
    <div
      v-if="departmentModalOpen"
      class="category-modal"
      @click.self="closeDepartmentModal"
    >
      <div class="category-manage-card">
        <div class="category-modal-header">
          <div>
            <h2>部門管理</h2>
            <p>新增、修改或刪除系統員工部門</p>
          </div>
          <button type="button" class="modal-close" @click="closeDepartmentModal">×</button>
        </div>

        <div class="category-create-area">
          <label> 新增部門 </label>
          <div class="perm-create-row">
            <input
              id="newDepartmentName"
              v-model="newDepartmentName"
              type="text"
              placeholder="部門名稱，例如：行銷部"
              maxlength="50"
              @keyup.enter="addDepartment"
            />
            <button
              type="button"
              class="admin-btn admin-btn-primary"
              :disabled="departmentSaving"
              @click="addDepartment"
            >
              {{ departmentSaving ? "新增中..." : "＋ 新增" }}
            </button>
          </div>
        </div>

        <div class="category-divider"></div>

        <div class="category-list-area">
          <div class="category-list-title">
            <span> 目前部門種類 </span>
            <span class="category-count"> {{ departments.length }} 種 </span>
          </div>

          <div v-if="departments.length === 0" class="category-empty">尚無部門資料</div>

          <div
            v-for="dept in departments"
            :key="dept.id"
            class="category-manage-row"
          >
            <template v-if="editingDepartmentId !== dept.id">
              <div class="category-info perm-info-row">
                <span class="category-id"> #{{ dept.id }} </span>
                <strong class="perm-title">{{ dept.name }}</strong>
              </div>
              <div class="category-actions">
                <button type="button" class="category-edit-button" @click="startEditDepartment(dept)">修改</button>
                <button type="button" class="category-delete-button" @click="deleteDepartment(dept)">刪除</button>
              </div>
            </template>

            <template v-else>
              <div class="perm-edit-row">
                <input
                  v-model="editingDepartmentName"
                  type="text"
                  class="category-edit-input"
                  placeholder="部門名稱"
                  maxlength="50"
                  @keyup.enter="updateDepartment(dept.id)"
                />
              </div>
              <div class="category-actions">
                <button
                  type="button"
                  class="category-save-button"
                  :disabled="departmentUpdating"
                  @click="updateDepartment(dept.id)"
                >
                  {{ departmentUpdating ? "儲存中" : "儲存" }}
                </button>
                <button type="button" class="category-cancel-button" @click="cancelEditDepartment">取消</button>
              </div>
            </template>
          </div>
        </div>

        <div class="category-modal-footer">
          <button type="button" class="admin-btn admin-btn-secondary" @click="closeDepartmentModal">關閉</button>
        </div>
      </div>
    </div>

    <!-- JSON 匯出 Modal -->
    <AdminJsonExportModal
      :open="exportModalOpen"
      title="匯出員工資料"
      subtitle="匯出 JSON 格式的員工帳號、職位部門與檔案資料"
      notice-text="💡 系統將調用後端 <code>/api/employees/export</code> API，匯出包含 <code>password</code>、部門與職位完整資訊的格式化 <code>employees.json</code> 檔案並自動下載。"
      :exporting="exporting"
      :filtered-count="sortedEmployees.length"
      :total-count="employees.length"
      :selected-count="selectedEmployeeIds.length"
      :filtered-hint="`符合關鍵字 (${keyword || '無'})、部門與狀態篩選`"
      :initial-scope="exportInitialScope"
      @close="closeExportModal"
      @confirm="handleExport"
    />

    <!-- JSON 匯入 Modal -->
    <AdminJsonImportModal
      :open="importModalOpen"
      title="匯入員工資料"
      subtitle="透過 JSON 檔案或文字批次匯入員工資料"
      file-input-id="import-employee-file"
      placeholder='請在此貼上 JSON 格式的員工資料，例如：&#10;[&#10;  {&#10;    "username": "hotel_staff",&#10;    "password": "staff123",&#10;    "name": "李專員",&#10;    "departmentName": "櫃檯部",&#10;    "position": "資深接待",&#10;    "email": "staff@hotel.com",&#10;    "phone": "0912345678",&#10;    "gender": "女",&#10;    "status": "1"&#10;  }&#10;]'
      :importing="importing"
      :import-result="importResult"
      @close="closeImportModal"
      @import="handleImport"
      @error-msg="(msg) => showMessage(msg, 'error')"
    >
      <template #rules>
        <li>支援多筆陣列 <code>[...]</code> 或單筆物件 <code>{...}</code>。</li>
        <li>支援 <code>departmentName</code> 或 <code>departmentId</code> 自動關聯與自動建立新部門。</li>
        <li>支援 <code>password</code> 密碼匯入（支援明文或已雜湊密碼；未填則預設為 <code>123456</code>）。</li>
        <li>若帳號已存在，系統將自動<strong>更新</strong>該員工資料。</li>
        <li>若帳號不存在，系統將<strong>新增</strong>員工。</li>
      </template>
    </AdminJsonImportModal>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref, watch } from "vue";
import { useAuthStore } from "@/stores/auth";
import { useToastStore } from "@/stores/toast";
import { getAuthHeaders } from "@/utils/auth";
import { useAdminPagination } from "@/composables/useAdminPagination";
import { useTableSort } from "@/composables/useTableSort";
import { useTableSelection } from "@/composables/useTableSelection";
import AdminPagination from "@/components/admin/AdminPagination.vue";
import AdminJsonExportModal from "@/components/admin/AdminJsonExportModal.vue";
import AdminJsonImportModal from "@/components/admin/AdminJsonImportModal.vue";
import "@/assets/admin-manage.css";

const authStore = useAuthStore();
const toastStore = useToastStore();

const API_URL = "/api/employees";
const DEPT_API_URL = "/api/departments";
const PERM_API_URL = "/api/permissions";

const departments = ref([]);
const employees = ref([]);
const permissions = ref([]);
const keyword = ref("");
const selectedDepartment = ref("");
const selectedStatus = ref("");
const loading = ref(false);
const saving = ref(false);

// 權限種類 Modal
const permissionModalOpen = ref(false);
const newPermissionName = ref("");
const newPermissionCode = ref("");
const permissionSaving = ref(false);
const editingPermissionId = ref(null);
const editingPermissionName = ref("");
const editingPermissionCode = ref("");
const permissionUpdating = ref(false);

// 部門管理 Modal
const departmentModalOpen = ref(false);
const newDepartmentName = ref("");
const departmentSaving = ref(false);
const editingDepartmentId = ref(null);
const editingDepartmentName = ref("");
const departmentUpdating = ref(false);

// 排序與多選
const { sortKey, sortDirection, changeSort, getSortIcon, sortList } = useTableSort("employeeId", "asc");
const { selectedIds: selectedEmployeeIds, isAllSelected, toggleSelectAll, clearSelection } = useTableSelection();

// 表單
const modalOpen = ref(false);
const editingEmployeeId = ref(null);
const uploadingAvatar = ref(false);
const form = reactive({
  username: "",
  password: "",
  status: "1",
  departmentId: "",
  customDepartmentName: "",
  position: "",
  permissionIds: [],
  name: "",
  avatarUrl: "",
  email: "",
  phone: "",
  gender: "男",
  birthday: "",
  zipcode: "",
  city: "",
  district: "",
  address: "",
});

// 匯出 / 匯入
const exportModalOpen = ref(false);
const exporting = ref(false);
const exportInitialScope = ref("filtered");
const importModalOpen = ref(false);
const importing = ref(false);
const importResult = ref(null);

function showMessage(text, type = "success", duration = 3000) {
  toastStore.showToast(text, type === "error" ? "error" : "success", duration);
}

function getStatusLabel(status) {
  const value = String(status ?? "UNKNOWN").toUpperCase();
  if (value === "1" || value === "ACTIVE" || value === "ENABLE" || value === "ENABLED") return "啟用";
  if (value === "0" || value === "INACTIVE" || value === "DISABLE" || value === "DISABLED") return "停用";
  return value;
}

function isActiveStatus(status) {
  const normalized = String(status ?? "").toUpperCase();
  return ["ACTIVE", "1", "ENABLE", "ENABLED"].includes(normalized);
}

function getDepartmentName(deptId, deptName) {
  if (deptName) return deptName;
  const dept = departments.value.find((d) => d.id === Number(deptId));
  return dept ? dept.name : "未指定";
}

const currentUsername = computed(() => {
  const token = localStorage.getItem("token");
  if (!token) return "";
  try {
    const payloadPart = token.split(".")[1];
    if (!payloadPart) return "";
    const base64 = payloadPart.replace(/-/g, "+").replace(/_/g, "/");
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join("")
    );
    const payload = JSON.parse(jsonPayload);
    return payload.sub || payload.username || "";
  } catch (err) {
    return "";
  }
});

function isSelf(employee) {
  if (!employee) return false;
  const username = (currentUsername.value || "").trim().toLowerCase();
  if (!username) return false;

  if (typeof employee === "object") {
    const empUser = String(employee.username || "").trim().toLowerCase();
    return !!empUser && empUser === username;
  } else {
    const found = employees.value.find((e) => (e.employeeId ?? e.id) === employee);
    if (found) {
      return String(found.username || "").trim().toLowerCase() === username;
    }
  }
  return false;
}

const isEditingSelf = computed(() => {
  if (editingEmployeeId.value === null) return false;
  const editingEmp = employees.value.find((e) => (e.employeeId ?? e.id) === editingEmployeeId.value);
  if (editingEmp && isSelf(editingEmp)) return true;
  return (
    !!form.username &&
    !!currentUsername.value &&
    form.username.trim().toLowerCase() === currentUsername.value.toLowerCase()
  );
});

// 篩選與排序
const filteredEmployees = computed(() => {
  const search = keyword.value.trim().toLowerCase();
  const deptFilter = selectedDepartment.value;
  const statusFilter = selectedStatus.value;

  return employees.value.filter((emp) => {
    const matchesKeyword =
      !search ||
      (emp.username || "").toLowerCase().includes(search) ||
      (emp.name || "").toLowerCase().includes(search) ||
      (emp.position || "").toLowerCase().includes(search) ||
      (emp.email || "").toLowerCase().includes(search) ||
      (emp.phone || "").includes(search) ||
      (emp.departmentName || "").toLowerCase().includes(search) ||
      (Array.isArray(emp.permissionNames) &&
        emp.permissionNames.some((p) => (p || "").toLowerCase().includes(search)));

    const matchesDept = !deptFilter || String(emp.departmentId) === String(deptFilter);
    const matchesStatus = !statusFilter || String(emp.status) === String(statusFilter);

    return matchesKeyword && matchesDept && matchesStatus;
  });
});

const sortedEmployees = computed(() => {
  return sortList(filteredEmployees.value, {
    employeeId: (item) => Number(item.employeeId ?? item.id ?? 0),
    username: (item) => item.username || "",
    department: (item) => getDepartmentName(item.departmentId, item.departmentName),
    gender: (item) => item.gender || "",
    status: (item) => getStatusLabel(item.status),
  });
});

// 分頁
const {
  currentPage,
  pageSize,
  totalPages,
  paginatedItems: paginatedEmployees,
  visiblePages,
  goToPage,
  resetPage,
} = useAdminPagination(sortedEmployees, 10);

watch([selectedDepartment, selectedStatus], () => {
  resetPage();
});

function resetSearch() {
  keyword.value = "";
  selectedDepartment.value = "";
  selectedStatus.value = "";
  sortKey.value = "employeeId";
  sortDirection.value = "asc";
  resetPage();
}

// 部門 API
async function loadDepartments() {
  try {
    const response = await fetch(DEPT_API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    if (response.ok) {
      const data = await response.json();
      if (Array.isArray(data)) {
        departments.value = data.map((d) => ({
          id: d.departmentId ?? d.id,
          name: d.departmentName ?? d.name,
        }));
      }
    }
  } catch (error) {
    console.error("載入部門失敗：", error);
  }
}

async function ensureDepartmentExists(deptName) {
  if (!deptName || !deptName.trim()) return null;
  const cleanName = deptName.trim();
  const found = departments.value.find((d) => d.name.toLowerCase() === cleanName.toLowerCase());
  if (found) return found.id;

  try {
    const res = await fetch(DEPT_API_URL, {
      method: "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify({ departmentName: cleanName }),
    });

    if (res.ok) {
      const newDept = await res.json();
      const newId = newDept.departmentId ?? newDept.id;
      const newName = newDept.departmentName ?? newDept.name ?? cleanName;
      departments.value.push({ id: newId, name: newName });
      return newId;
    }
  } catch (err) {
    console.error("自動建立新部門失敗：", err);
  }
  return null;
}

// 員工 CRUD
async function loadEmployees() {
  loading.value = true;
  try {
    const response = await fetch(API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有員工管理權限", "error");
      return;
    }
    if (!response.ok) {
      showMessage("取得員工資料失敗", "error");
      return;
    }

    const contentType = response.headers.get("content-type") || "";
    if (!contentType.includes("application/json")) {
      showMessage("員工 API 回傳的不是 JSON，請檢查 proxy 或後端路徑", "error");
      return;
    }

    employees.value = await response.json();
    resetPage();
  } catch (error) {
    console.error("員工讀取錯誤：", error);
    showMessage("讀取員工資料失敗", "error");
  } finally {
    loading.value = false;
  }
}

function openCreateModal() {
  editingEmployeeId.value = null;
  form.username = "";
  form.password = "";
  form.status = "1";
  form.departmentId = departments.value.length > 0 ? departments.value[0].id : "__NEW__";
  form.customDepartmentName = "";
  form.position = "";
  form.permissionIds = [];
  form.name = "";
  form.avatarUrl = "";
  form.email = "";
  form.phone = "";
  form.gender = "男";
  form.birthday = "";
  form.zipcode = "";
  form.city = "";
  form.district = "";
  form.address = "";
  modalOpen.value = true;
}

function openEditModal(employee) {
  editingEmployeeId.value = employee.employeeId ?? employee.id;
  form.username = employee.username || "";
  form.password = "";
  form.status = String(employee.status ?? "1");

  const matchedDept = departments.value.find(
    (d) => d.id === employee.departmentId || (employee.departmentName && d.name === employee.departmentName)
  );

  if (matchedDept) {
    form.departmentId = matchedDept.id;
    form.customDepartmentName = "";
  } else if (employee.departmentName) {
    form.departmentId = "__NEW__";
    form.customDepartmentName = employee.departmentName;
  } else {
    form.departmentId = departments.value.length > 0 ? departments.value[0].id : "__NEW__";
    form.customDepartmentName = "";
  }

  form.position = employee.position || "";
  form.permissionIds = Array.isArray(employee.permissionIds) ? [...employee.permissionIds] : [];
  form.name = employee.name || "";
  form.avatarUrl = employee.avatarUrl || "";
  form.email = employee.email || "";
  form.phone = employee.phone || "";
  form.gender = employee.gender || "男";
  form.birthday = employee.birthday || "";
  form.zipcode = employee.zipcode || "";
  form.city = employee.city || "";
  form.district = employee.district || "";
  form.address = employee.address || "";
  modalOpen.value = true;
}

async function handleAdminAvatarUpload(event) {
  const file = event.target.files && event.target.files[0];
  if (!file || editingEmployeeId.value === null) return;

  if (!file.type.startsWith("image/")) {
    showMessage("請選擇有效的圖片檔案 (JPG, PNG, WEBP 等)", "error");
    return;
  }

  if (file.size > 5 * 1024 * 1024) {
    showMessage("圖片檔案大小不能超過 5MB", "error");
    return;
  }

  uploadingAvatar.value = true;
  const formData = new FormData();
  formData.append("file", file);

  try {
    const token = localStorage.getItem("token");
    const headers = {};
    if (token) headers.Authorization = "Bearer " + token;

    const res = await fetch(`${API_URL}/${editingEmployeeId.value}/avatar`, {
      method: "POST",
      headers,
      body: formData,
    });

    if (!res.ok) {
      const err = await res.json().catch(() => ({}));
      showMessage(err.message || "頭像上傳失敗", "error");
      return;
    }

    const updated = await res.json();
    if (updated && updated.avatarUrl) {
      form.avatarUrl = updated.avatarUrl;
      if (isEditingSelf.value) {
        authStore.updateAvatarUrl(updated.avatarUrl);
      }
      showMessage("員工頭像上傳成功", "success");
      await loadEmployees();
    }
  } catch (err) {
    console.error("管理員上傳員工頭像錯誤：", err);
    showMessage("頭像上傳失敗", "error");
  } finally {
    uploadingAvatar.value = false;
    event.target.value = "";
  }
}

function closeModal() {
  modalOpen.value = false;
  editingEmployeeId.value = null;
}

async function saveEmployee() {
  const username = form.username.trim();
  const password = form.password.trim();

  if (!username) {
    showMessage("帳號不能為空", "error");
    return;
  }

  let finalDepartmentId = null;
  let finalDepartmentName = "";

  if (form.departmentId === "__NEW__" || !form.departmentId) {
    if (!form.customDepartmentName || !form.customDepartmentName.trim()) {
      showMessage("請輸入自訂部門名稱", "error");
      return;
    }
    finalDepartmentName = form.customDepartmentName.trim();
    finalDepartmentId = await ensureDepartmentExists(finalDepartmentName);
  } else {
    finalDepartmentId = Number(form.departmentId);
    const deptObj = departments.value.find((d) => d.id === finalDepartmentId);
    finalDepartmentName = deptObj ? deptObj.name : "";
  }

  const isEditing = editingEmployeeId.value !== null;
  if (isEditing && isEditingSelf.value && form.status !== "1") {
    showMessage("無法停用目前登入中的帳號", "error");
    return;
  }

  saving.value = true;
  const payload = {
    username: username,
    status: form.status,
    departmentId: finalDepartmentId,
    departmentName: finalDepartmentName,
    position: form.position.trim(),
    permissionIds: Array.isArray(form.permissionIds) ? form.permissionIds : [],
    name: form.name.trim(),
    avatarUrl: form.avatarUrl || null,
    email: form.email.trim(),
    phone: form.phone.trim(),
    gender: form.gender,
    birthday: form.birthday || null,
    zipcode: form.zipcode.trim(),
    city: form.city.trim(),
    district: form.district.trim(),
    address: form.address.trim(),
  };

  if (password) {
    payload.password = password;
  }

  try {
    const url = isEditing ? `${API_URL}/${editingEmployeeId.value}` : API_URL;
    const method = isEditing ? "PUT" : "POST";

    const response = await fetch(url, {
      method: method,
      headers: getAuthHeaders(),
      body: JSON.stringify(payload),
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有員工管理權限", "error");
      return;
    }
    if (response.status === 409) {
      showMessage("使用者帳號已存在", "error");
      return;
    }
    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      showMessage(data.message || "儲存員工失敗", "error");
      return;
    }

    closeModal();
    showMessage(isEditing ? "員工資料修改成功" : "員工新增成功", "success");
    await loadDepartments();
    await loadEmployees();
  } catch (error) {
    console.error("員工儲存錯誤：", error);
    showMessage("儲存員工失敗", "error");
  } finally {
    saving.value = false;
  }
}

async function toggleStatus(employee) {
  if (!employee) return;
  const employeeId = employee.employeeId ?? employee.id;
  if (!employeeId) {
    showMessage("無法取得員工 ID", "error");
    return;
  }

  const nextStatus = isActiveStatus(employee.status) ? "0" : "1";
  const actionText = nextStatus === "1" ? "啟用" : "停用";

  if (isSelf(employee) && nextStatus === "0") {
    showMessage("無法停用目前登入中的自身帳號", "error");
    return;
  }

  if (!window.confirm(`確定要${actionText}員工「${employee.name || employee.username}」嗎？`)) {
    return;
  }

  try {
    const response = await fetch(`${API_URL}/${employeeId}/status?status=${nextStatus}`, {
      method: "PATCH",
      headers: getAuthHeaders(),
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有員工管理權限", "error");
      return;
    }
    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      showMessage(data.message || "更新員工狀態失敗", "error");
      return;
    }

    showMessage(`員工已${actionText}`, "success");
    await loadEmployees();
  } catch (error) {
    console.error("員工狀態更新錯誤：", error);
    showMessage("更新員工狀態失敗", "error");
  }
}

async function deleteEmployee(employeeOrId) {
  const employeeId =
    typeof employeeOrId === "object" && employeeOrId !== null
      ? employeeOrId.employeeId ?? employeeOrId.id
      : employeeOrId;

  if (!employeeId) {
    showMessage("無法取得員工 ID", "error");
    return;
  }

  const targetEmployee =
    typeof employeeOrId === "object" && employeeOrId !== null
      ? employeeOrId
      : employees.value.find((e) => (e.employeeId ?? e.id) === employeeId);

  if (targetEmployee && isSelf(targetEmployee)) {
    showMessage("無法刪除目前登入中的自身帳號", "error");
    return;
  }

  if (!window.confirm("確定要刪除此員工嗎？此動作將連動刪除帳號與個人檔案。")) {
    return;
  }

  try {
    const response = await fetch(`${API_URL}/${employeeId}`, {
      method: "DELETE",
      headers: getAuthHeaders(),
    });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有刪除權限", "error");
      return;
    }
    if (response.status === 409) {
      const data = await response.json().catch(() => ({}));
      showMessage(data.message || "無法刪除：該員工已有相關業務紀錄，建議將狀態改為停用", "error");
      return;
    }
    if (!response.ok) {
      const data = await response.json().catch(() => ({}));
      showMessage(data.message || "刪除員工失敗", "error");
      return;
    }

    showMessage("員工刪除成功", "success");
    await loadEmployees();
  } catch (error) {
    console.error("刪除員工錯誤：", error);
    showMessage("刪除員工失敗", "error");
  }
}

function selectAllPermissions() {
  form.permissionIds = permissions.value.map((p) => p.permissionId);
}

function clearAllPermissions() {
  form.permissionIds = [];
}

// 權限種類 CRUD
async function loadPermissions() {
  try {
    const response = await fetch(PERM_API_URL, {
      method: "GET",
      headers: getAuthHeaders(),
    });
    if (response.ok) {
      permissions.value = await response.json();
    }
  } catch (error) {
    console.error("讀取權限種類失敗：", error);
  }
}

function openPermissionModal() {
  newPermissionName.value = "";
  newPermissionCode.value = "";
  cancelEditPermission();
  permissionModalOpen.value = true;
  setTimeout(() => {
    document.getElementById("newPermissionName")?.focus();
  }, 50);
}

function closePermissionModal() {
  permissionModalOpen.value = false;
  newPermissionName.value = "";
  newPermissionCode.value = "";
  cancelEditPermission();
}

async function addPermission() {
  const permName = newPermissionName.value.trim();
  const permCode = newPermissionCode.value.trim().toUpperCase();

  if (!permName) {
    showMessage("請輸入權限名稱", "error");
    return;
  }
  if (!permCode) {
    showMessage("請輸入權限代碼", "error");
    return;
  }

  permissionSaving.value = true;
  try {
    const response = await fetch(PERM_API_URL, {
      method: "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify({
        permissionName: permName,
        permissionCode: permCode,
      }),
    });

    if (response.status === 409) {
      const data = await response.json().catch(() => ({}));
      showMessage(data.message || "這個權限名稱或代碼已經存在", "error");
      return;
    }
    if (!response.ok) {
      showMessage("新增權限種類失敗", "error");
      return;
    }

    newPermissionName.value = "";
    newPermissionCode.value = "";
    await loadPermissions();
    showMessage("權限種類新增成功", "success");
  } catch (error) {
    console.error("新增權限失敗：", error);
    showMessage("新增權限種類失敗", "error");
  } finally {
    permissionSaving.value = false;
  }
}

function startEditPermission(perm) {
  editingPermissionId.value = perm.permissionId;
  editingPermissionName.value = perm.permissionName;
  editingPermissionCode.value = perm.permissionCode;
}

function cancelEditPermission() {
  editingPermissionId.value = null;
  editingPermissionName.value = "";
  editingPermissionCode.value = "";
}

async function updatePermission(permissionId) {
  const permName = editingPermissionName.value.trim();
  const permCode = editingPermissionCode.value.trim().toUpperCase();

  if (!permName || !permCode) {
    showMessage("權限名稱與代碼均不能為空", "error");
    return;
  }

  permissionUpdating.value = true;
  try {
    const response = await fetch(`${PERM_API_URL}/${permissionId}`, {
      method: "PUT",
      headers: getAuthHeaders(),
      body: JSON.stringify({
        permissionName: permName,
        permissionCode: permCode,
      }),
    });

    if (response.status === 409) {
      const data = await response.json().catch(() => ({}));
      showMessage(data.message || "這個權限名稱或代碼已經存在", "error");
      return;
    }
    if (!response.ok) {
      showMessage("修改權限種類失敗", "error");
      return;
    }

    cancelEditPermission();
    await loadPermissions();
    await loadEmployees();
    showMessage("權限種類修改成功", "success");
  } catch (error) {
    console.error("修改權限失敗：", error);
    showMessage("修改權限種類失敗", "error");
  } finally {
    permissionUpdating.value = false;
  }
}

async function deletePermission(perm) {
  if (!window.confirm(`確定要刪除權限種類「${perm.permissionName} (${perm.permissionCode})」嗎？`)) {
    return;
  }

  try {
    const response = await fetch(`${PERM_API_URL}/${perm.permissionId}`, {
      method: "DELETE",
      headers: getAuthHeaders(),
    });

    if (response.status === 409) {
      let errorMessage = "此權限種類仍有員工使用，請先移除員工權限後再刪除";
      try {
        const data = await response.json();
        if (data.message) errorMessage = data.message;
      } catch {}
      showMessage(errorMessage, "error");
      return;
    }
    if (!response.ok) {
      showMessage("刪除權限種類失敗", "error");
      return;
    }

    form.permissionIds = form.permissionIds.filter((id) => id !== perm.permissionId);
    await loadPermissions();
    await loadEmployees();
    showMessage("權限種類刪除成功", "success");
  } catch (error) {
    console.error("刪除權限失敗：", error);
    showMessage("刪除權限種類失敗", "error");
  }
}

// 部門 CRUD
function openDepartmentModal() {
  newDepartmentName.value = "";
  cancelEditDepartment();
  departmentModalOpen.value = true;
  setTimeout(() => {
    document.getElementById("newDepartmentName")?.focus();
  }, 50);
}

function closeDepartmentModal() {
  departmentModalOpen.value = false;
  newDepartmentName.value = "";
  cancelEditDepartment();
}

async function addDepartment() {
  const deptName = newDepartmentName.value.trim();

  if (!deptName) {
    showMessage("請輸入部門名稱", "error");
    return;
  }

  departmentSaving.value = true;
  try {
    const response = await fetch(DEPT_API_URL, {
      method: "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify({
        departmentName: deptName,
      }),
    });

    if (response.status === 409) {
      const data = await response.json().catch(() => ({}));
      showMessage(data.message || "這個部門名稱已經存在", "error");
      return;
    }
    if (!response.ok) {
      showMessage("新增部門失敗", "error");
      return;
    }

    newDepartmentName.value = "";
    await loadDepartments();
    showMessage("部門新增成功", "success");
  } catch (error) {
    console.error("新增部門失敗：", error);
    showMessage("新增部門失敗", "error");
  } finally {
    departmentSaving.value = false;
  }
}

function startEditDepartment(dept) {
  editingDepartmentId.value = dept.id;
  editingDepartmentName.value = dept.name;
}

function cancelEditDepartment() {
  editingDepartmentId.value = null;
  editingDepartmentName.value = "";
}

async function updateDepartment(departmentId) {
  const deptName = editingDepartmentName.value.trim();

  if (!deptName) {
    showMessage("部門名稱不能為空", "error");
    return;
  }

  departmentUpdating.value = true;
  try {
    const response = await fetch(`${DEPT_API_URL}/${departmentId}`, {
      method: "PUT",
      headers: getAuthHeaders(),
      body: JSON.stringify({
        departmentName: deptName,
      }),
    });

    if (response.status === 409) {
      const data = await response.json().catch(() => ({}));
      showMessage(data.message || "這個部門名稱已經存在", "error");
      return;
    }
    if (!response.ok) {
      showMessage("修改部門失敗", "error");
      return;
    }

    cancelEditDepartment();
    await loadDepartments();
    await loadEmployees();
    showMessage("部門修改成功", "success");
  } catch (error) {
    console.error("修改部門失敗：", error);
    showMessage("修改部門失敗", "error");
  } finally {
    departmentUpdating.value = false;
  }
}

async function deleteDepartment(dept) {
  if (!window.confirm(`確定要刪除部門「${dept.name}」嗎？`)) {
    return;
  }

  try {
    const response = await fetch(`${DEPT_API_URL}/${dept.id}`, {
      method: "DELETE",
      headers: getAuthHeaders(),
    });

    if (response.status === 409) {
      let errorMessage = "此部門仍有員工使用，請先調整員工所屬部門後再刪除";
      try {
        const data = await response.json();
        if (data.message) errorMessage = data.message;
      } catch {}
      showMessage(errorMessage, "error");
      return;
    }
    if (!response.ok) {
      showMessage("刪除部門失敗", "error");
      return;
    }

    if (form.departmentId === dept.id) {
      form.departmentId = departments.value.length > 0 ? departments.value[0].id : "";
    }
    await loadDepartments();
    await loadEmployees();
    showMessage("部門刪除成功", "success");
  } catch (error) {
    console.error("刪除部門失敗：", error);
    showMessage("刪除部門失敗", "error");
  }
}

// 匯出 / 匯入邏輯
function openExportModal() {
  if (selectedEmployeeIds.value.length > 0) {
    exportInitialScope.value = "selected";
  } else if (keyword.value || selectedDepartment.value || selectedStatus.value) {
    exportInitialScope.value = "filtered";
  } else {
    exportInitialScope.value = "all";
  }
  exportModalOpen.value = true;
}

function closeExportModal() {
  exportModalOpen.value = false;
}

async function handleExport({ scope, customParams }) {
  exporting.value = true;
  try {
    const params = new URLSearchParams();

    if (scope === "filtered") {
      if (keyword.value.trim()) params.append("keyword", keyword.value.trim());
      if (selectedDepartment.value) params.append("departmentId", selectedDepartment.value);
      if (selectedStatus.value) params.append("status", selectedStatus.value);
    } else if (scope === "selected") {
      if (selectedEmployeeIds.value.length === 0) {
        showMessage("請先勾選要匯出的員工", "error");
        exporting.value = false;
        return;
      }
      params.append("ids", selectedEmployeeIds.value.join(","));
    } else if (scope === "custom") {
      if (customParams.minId) params.append("minId", customParams.minId);
      if (customParams.maxId) params.append("maxId", customParams.maxId);
      if (customParams.limit) params.append("limit", customParams.limit);
      if (customParams.offset) params.append("offset", customParams.offset);
      if (keyword.value.trim()) params.append("keyword", keyword.value.trim());
      if (selectedDepartment.value) params.append("departmentId", selectedDepartment.value);
      if (selectedStatus.value) params.append("status", selectedStatus.value);
    }

    const queryString = params.toString() ? `?${params.toString()}` : "";
    const url = `${API_URL}/export${queryString}`;

    const token = localStorage.getItem("token");
    const headers = {};
    if (token) headers.Authorization = "Bearer " + token;

    const response = await fetch(url, { method: "GET", headers });

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有員工管理權限", "error");
      return;
    }
    if (!response.ok) {
      const err = await response.json().catch(() => ({}));
      showMessage(err.message || "匯出員工失敗", "error");
      return;
    }

    const blob = await response.blob();
    const downloadUrl = window.URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = downloadUrl;
    const now = new Date();
    const dateStr = now.toISOString().slice(0, 10).replace(/-/g, "");
    link.download = `employees_${dateStr}.json`;
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(downloadUrl);

    closeExportModal();
    showMessage("員工資料 JSON 匯出成功！", "success");
  } catch (error) {
    console.error("匯出錯誤：", error);
    showMessage("匯出員工資料失敗", "error");
  } finally {
    exporting.value = false;
  }
}

function openImportModal() {
  importResult.value = null;
  importModalOpen.value = true;
}

function closeImportModal() {
  importModalOpen.value = false;
  importResult.value = null;
}

async function handleImport({ mode, file, jsonText }) {
  importResult.value = null;
  const token = localStorage.getItem("token");

  try {
    importing.value = true;
    let response;

    if (mode === "file") {
      const formData = new FormData();
      formData.append("file", file);
      const headers = {};
      if (token) headers.Authorization = "Bearer " + token;
      response = await fetch(`${API_URL}/import`, {
        method: "POST",
        headers,
        body: formData,
      });
    } else {
      const headers = { "Content-Type": "application/json" };
      if (token) headers.Authorization = "Bearer " + token;
      response = await fetch(`${API_URL}/import`, {
        method: "POST",
        headers,
        body: jsonText.trim(),
      });
    }

    if (response.status === 401 || response.status === 403) {
      showMessage("登入狀態失效或沒有員工管理權限", "error");
      return;
    }

    const data = await response.json().catch(() => ({}));

    if (!response.ok) {
      showMessage(data.message || "匯入失敗", "error");
      importResult.value = data;
      return;
    }

    importResult.value = data;
    const isSuccess = (data.failureCount ?? 0) === 0;
    showMessage(
      `匯入完成！成功 ${data.successCount ?? 0} 筆，失敗 ${data.failureCount ?? 0} 筆`,
      isSuccess ? "success" : "error"
    );

    await loadDepartments();
    await loadEmployees();
  } catch (error) {
    console.error("匯入錯誤：", error);
    showMessage("匯入失敗：" + error.message, "error");
  } finally {
    importing.value = false;
  }
}

onMounted(async () => {
  await Promise.all([loadDepartments(), loadEmployees(), loadPermissions()]);
});
</script>

<style scoped>
.employee-manage-page {
  width: 100%;
}

.perms-td {
  position: relative;
}

.employee-perms-container {
  position: relative;
  display: block;
  width: 100%;
  cursor: default;
}

.employee-perms-container:hover {
  z-index: 120;
}

.employee-perms-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  max-height: 44px;
  overflow: hidden;
  align-content: flex-start;
}

/* 懸停向下平滑浮現 Popover / Tooltip */
.perms-hover-popover {
  position: absolute;
  top: calc(100% + 8px);
  left: 50%;
  transform: translateX(-50%) translateY(-6px);
  min-width: 200px;
  max-width: 280px;
  background-color: #ffffff;
  border: 1px solid #dfd7cb;
  border-radius: 8px;
  box-shadow: 0 10px 25px rgba(74, 59, 42, 0.18), 0 3px 8px rgba(0, 0, 0, 0.08);
  opacity: 0;
  visibility: hidden;
  transition: opacity 0.2s ease, transform 0.2s ease, visibility 0.2s;
  pointer-events: none;
  z-index: 999;
  text-align: left;
}

.employee-perms-container:hover .perms-hover-popover {
  opacity: 1;
  visibility: visible;
  transform: translateX(-50%) translateY(0);
}

.perms-hover-popover::after {
  content: "";
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  border-width: 0 6px 6px;
  border-style: solid;
  border-color: transparent transparent #f7f3ec transparent;
}

.perms-hover-popover::before {
  content: "";
  position: absolute;
  bottom: 100%;
  left: 50%;
  transform: translateX(-50%);
  border-width: 0 7px 7px;
  border-style: solid;
  border-color: transparent transparent #dfd7cb transparent;
  margin-bottom: 1px;
}

.perms-popover-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 7px 10px;
  background-color: #f7f3ec;
  border-bottom: 1px solid #ece4d7;
  border-top-left-radius: 7px;
  border-top-right-radius: 7px;
  font-size: 12px;
  font-weight: 600;
  color: #554432;
}

.perms-popover-count {
  font-size: 11px;
  color: #9b7435;
  background-color: #eee4d5;
  padding: 1px 6px;
  border-radius: 10px;
}

.perms-popover-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  padding: 8px 10px;
  max-height: 180px;
  overflow-y: auto;
}

.perms-popover-tags::-webkit-scrollbar {
  width: 4px;
}

.perms-popover-tags::-webkit-scrollbar-thumb {
  background-color: #d8d0c5;
  border-radius: 4px;
}

.perm-badge {
  display: inline-block;
  padding: 2px 7px;
  border-radius: 4px;
  background-color: #f6efe2;
  color: #8a5d24;
  font-size: 11px;
  font-weight: 500;
  border: 1px solid #ebdcc5;
  white-space: nowrap;
}

.perm-more-badge {
  display: inline-block;
  padding: 2px 6px;
  border-radius: 4px;
  background-color: #f1e5d4;
  color: #8a5d24;
  font-size: 11px;
  font-weight: 600;
  border: 1px solid #dfcfb8;
  white-space: nowrap;
}

.perm-section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.perm-header-actions {
  display: flex;
  gap: 8px;
}

.link-action-btn {
  background: none;
  border: none;
  color: #9b7435;
  font-size: 12px;
  cursor: pointer;
  padding: 2px 6px;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.link-action-btn:hover {
  background-color: #f3ede2;
  color: #6f5328;
}

.perm-selector-wrapper {
  margin-bottom: 12px;
}

.perm-empty-tip {
  color: #999;
  font-size: 13px;
  padding: 12px;
  background-color: #fbf9f5;
  border-radius: 6px;
  text-align: center;
}

.perm-checkbox-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(180px, 1fr));
  gap: 10px;
}

.perm-checkbox-card {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  padding: 10px 12px;
  border: 1.5px solid #e8e2d7;
  border-radius: 8px;
  background-color: #fdfdfc;
  cursor: pointer;
  transition: all 0.2s;
  user-select: none;
}

.perm-checkbox-card:hover {
  border-color: #b58a46;
  background-color: #fbf8f2;
}

.perm-checkbox-card.active {
  border-color: #b58a46;
  background-color: #fdf8ef;
  box-shadow: 0 0 0 1px #b58a46;
}

.perm-checkbox-card input[type="checkbox"] {
  margin-top: 3px;
  accent-color: #b58a46;
  cursor: pointer;
}

.perm-card-content {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.perm-card-name {
  font-size: 13px;
  font-weight: 600;
  color: #4a3b2a;
}

.perm-card-code {
  font-size: 11px;
  color: #8c7b6d;
  font-family: monospace;
}

/* 權限種類管理 Modal */
.category-modal {
  position: fixed;
  inset: 0;
  z-index: 2000;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 20px;
  background-color: rgba(47, 42, 36, 0.55);
}

.category-manage-card {
  width: min(650px, 94vw);
  max-height: 82vh;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background-color: white;
  border-radius: 14px;
  box-shadow: 0 18px 55px rgba(0, 0, 0, 0.25);
}

.category-modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background-color: #4a3b2a;
  color: white;
}

.category-modal-header h2 {
  margin: 0;
  font-size: 21px;
}

.category-modal-header p {
  margin: 5px 0 0;
  color: rgba(255, 255, 255, 0.7);
  font-size: 12px;
}

.category-create-area {
  padding: 22px 24px 18px;
}

.category-create-area label {
  display: block;
  margin-bottom: 9px;
  color: #554536;
  font-size: 13px;
  font-weight: bold;
}

.perm-create-row {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.perm-create-row input {
  flex: 1;
  min-width: 140px;
  padding: 11px 13px;
  border: 1px solid #d8d0c5;
  border-radius: 7px;
  font-size: 14px;
}

.perm-create-row input:focus,
.category-edit-input:focus {
  outline: none;
  border-color: #b58a46;
  box-shadow: 0 0 0 3px rgba(181, 138, 70, 0.13);
}

.category-divider {
  height: 1px;
  margin: 0 24px;
  background-color: #eee8df;
}

.category-list-area {
  flex: 1;
  overflow-y: auto;
  padding: 18px 24px;
}

.category-list-title {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  color: #574b40;
  font-weight: bold;
}

.category-count {
  padding: 4px 9px;
  border-radius: 15px;
  background-color: #f2ece3;
  color: #9b7435;
  font-size: 11px;
}

.category-empty {
  padding: 30px;
  text-align: center;
  color: #999;
}

.category-manage-row {
  min-height: 58px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 15px;
  padding: 10px 2px;
  border-bottom: 1px solid #eee9e2;
}

.category-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.category-id {
  width: 42px;
  color: #aaa198;
  font-size: 12px;
}

.perm-info-row {
  flex: 1;
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.perm-title {
  color: #51463b;
  font-size: 14px;
}

.perm-code-tag {
  padding: 2px 7px;
  border-radius: 4px;
  background-color: #f3ede2;
  color: #8c6832;
  font-size: 11px;
  font-family: monospace;
}

.perm-edit-row {
  flex: 1;
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.category-actions {
  display: flex;
  gap: 7px;
  white-space: nowrap;
}

.category-actions button {
  padding: 6px 11px;
  border: none;
  border-radius: 5px;
  font-size: 12px;
  cursor: pointer;
  transition: 0.2s;
}

.category-edit-button {
  background-color: #edf2fb;
  color: #3d6092;
}

.category-edit-button:hover {
  background-color: #dce7f7;
}

.category-delete-button {
  background-color: #fdebea;
  color: #b24842;
}

.category-delete-button:hover {
  background-color: #f8d8d5;
}

.category-save-button {
  background-color: #e5f6eb;
  color: #257641;
}

.category-save-button:hover {
  background-color: #d3eddd;
}

.category-cancel-button {
  background-color: #eeeae4;
  color: #6f655a;
}

.category-cancel-button:hover {
  background-color: #e3ddd4;
}

.category-edit-input {
  flex: 1;
  min-width: 110px;
  padding: 9px 11px;
  border: 1px solid #d8d0c5;
  border-radius: 6px;
  font-size: 14px;
}

.category-modal-footer {
  display: flex;
  justify-content: flex-end;
  padding: 15px 24px;
  border-top: 1px solid #eee8df;
}

.admin-icon-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  vertical-align: middle;
}

.admin-icon-btn .lucide-icon {
  flex-shrink: 0;
}
</style>
