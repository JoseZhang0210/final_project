<template>
  <div class="coupon-admin-page">
    <div class="container">

      <div class="page-header">
        <div>
          <h1>優惠券管理</h1>
          <p>
            管理優惠券代碼、折扣內容、有效期間與啟用狀態
          </p>
        </div>

        <button
          type="button"
          class="create-button"
          @click="openCreateForm"
        >
          新增優惠券
        </button>
      </div>


      <!-- =====================================================
           表單
           ===================================================== -->
      <div
        v-if="showForm"
        class="coupon-form-card"
      >
        <h2>
          {{
            editingCouponId
              ? "修改優惠券"
              : "新增優惠券"
          }}
        </h2>


        <div class="form-grid">

          <div class="form-group">
            <label>
              優惠券代碼
            </label>

            <input
              v-model="form.couponCode"
              type="text"
              placeholder="例如 SAVE200"
            />
          </div>


          <div class="form-group">
            <label>
              優惠券名稱
            </label>

            <input
              v-model="form.couponName"
              type="text"
              placeholder="例如 滿兩千現折兩百"
            />
          </div>


          <div class="form-group">
            <label>
              折扣類型
            </label>

            <select
              v-model="form.discountType"
            >
              <option value="FIXED">
                固定金額
              </option>

              <option value="PERCENT">
                百分比
              </option>
            </select>
          </div>


          <div class="form-group">
            <label>
              折扣值
            </label>

            <input
              v-model.number="
                form.discountValue
              "
              type="number"
              min="1"
            />

            <small
              v-if="
                form.discountType ===
                'PERCENT'
              "
            >
              九折請輸入 10，代表折抵 10%
            </small>
          </div>


          <div class="form-group">
            <label>
              最低消費
            </label>

            <input
              v-model.number="
                form.minimumAmount
              "
              type="number"
              min="0"
            />
          </div>


          <div class="form-group">
            <label>
              狀態
            </label>

            <select
              v-model="form.status"
            >
              <option value="ACTIVE">
                啟用
              </option>

              <option value="INACTIVE">
                停用
              </option>
            </select>
          </div>


          <div class="form-group">
            <label>
              開始時間
            </label>

            <input
              v-model="form.startDate"
              type="datetime-local"
            />
          </div>


          <div class="form-group">
            <label>
              結束時間
            </label>

            <input
              v-model="form.endDate"
              type="datetime-local"
            />
          </div>

        </div>


        <div class="form-actions">

          <button
            type="button"
            class="save-button"
            :disabled="saving"
            @click="saveCoupon"
          >
            {{
              saving
                ? "儲存中..."
                : "儲存"
            }}
          </button>


          <button
            type="button"
            class="cancel-button"
            @click="closeForm"
          >
            取消
          </button>

        </div>
      </div>


      <!-- =====================================================
           Loading
           ===================================================== -->
      <div
        v-if="loading"
        class="loading"
      >
        優惠券資料載入中...
      </div>


      <!-- =====================================================
           Table
           ===================================================== -->
      <div
        v-else
        class="table-wrapper"
      >

        <table>

          <thead>
            <tr>
              <th>ID</th>
              <th>優惠碼</th>
              <th>優惠名稱</th>
              <th>類型</th>
              <th>折扣</th>
              <th>最低消費</th>
              <th>有效期間</th>
              <th>狀態</th>
              <th>操作</th>
            </tr>
          </thead>


          <tbody>

            <tr
              v-for="coupon in coupons"
              :key="coupon.couponId"
            >

              <td>
                {{ coupon.couponId }}
              </td>


              <td class="coupon-code">
                {{ coupon.couponCode }}
              </td>


              <td>
                {{ coupon.couponName }}
              </td>


              <td>
                {{
                  getDiscountTypeText(
                    coupon.discountType
                  )
                }}
              </td>


              <td>
                {{
                  getDiscountText(
                    coupon
                  )
                }}
              </td>


              <td>
                NT$
                {{
                  formatPrice(
                    coupon.minimumAmount
                  )
                }}
              </td>


              <td class="date-column">
                <div>
                  {{
                    formatDate(
                      coupon.startDate
                    )
                  }}
                </div>

                <div>
                  ～
                </div>

                <div>
                  {{
                    formatDate(
                      coupon.endDate
                    )
                  }}
                </div>
              </td>


              <td>
                <span
                  class="status-badge"
                  :class="
                    coupon.status ===
                    'ACTIVE'
                      ? 'active'
                      : 'inactive'
                  "
                >
                  {{
                    coupon.status ===
                    "ACTIVE"
                      ? "啟用"
                      : "停用"
                  }}
                </span>
              </td>


              <td>
                <div class="action-buttons">

                  <button
                    type="button"
                    class="edit-button"
                    @click="
                      openEditForm(
                        coupon
                      )
                    "
                  >
                    修改
                  </button>


                  <button
                    v-if="
                      coupon.status ===
                      'ACTIVE'
                    "
                    type="button"
                    class="disable-button"
                    @click="
                      changeStatus(
                        coupon,
                        'INACTIVE'
                      )
                    "
                  >
                    停用
                  </button>


                  <button
                    v-else
                    type="button"
                    class="enable-button"
                    @click="
                      changeStatus(
                        coupon,
                        'ACTIVE'
                      )
                    "
                  >
                    啟用
                  </button>

                </div>
              </td>

            </tr>


            <tr
              v-if="
                coupons.length === 0
              "
            >
              <td
                colspan="9"
                class="empty-row"
              >
                目前沒有優惠券資料
              </td>
            </tr>

          </tbody>

        </table>

      </div>

    </div>
  </div>
</template>


<script setup>
import {
  onMounted,
  reactive,
  ref,
} from "vue";


// =====================================================
// State
// =====================================================

const coupons =
  ref([]);

const loading =
  ref(false);

const saving =
  ref(false);

const showForm =
  ref(false);

const editingCouponId =
  ref(null);


// =====================================================
// Form
// =====================================================

const form =
  reactive({
    couponCode: "",
    couponName: "",
    discountType: "FIXED",
    discountValue: 100,
    minimumAmount: 0,
    startDate: "",
    endDate: "",
    status: "ACTIVE",
  });


// =====================================================
// Authorization
// =====================================================

function getAuthHeaders() {

  const token =
    localStorage.getItem(
      "token"
    );

  const headers = {
    "Content-Type":
      "application/json",
  };


  if (token) {

    headers.Authorization =
      "Bearer " + token;
  }


  return headers;
}


// =====================================================
// 載入全部優惠券
// =====================================================

async function loadCoupons() {

  loading.value = true;


  try {

    const response =
      await fetch(
        "/api/coupons",
        {
          method: "GET",
          headers:
            getAuthHeaders(),
        }
      );


    if (
      response.status === 401 ||
      response.status === 403
    ) {

      throw new Error(
        "沒有權限查看優惠券資料"
      );
    }


    if (!response.ok) {

      throw new Error(
        "載入優惠券失敗"
      );
    }


    coupons.value =
      await response.json();


  } catch (error) {

    console.error(
      "載入優惠券錯誤：",
      error
    );

    alert(
      error.message ||
      "載入優惠券失敗"
    );


  } finally {

    loading.value = false;
  }
}


// =====================================================
// 新增表單
// =====================================================

function openCreateForm() {

  editingCouponId.value =
    null;


  resetForm();


  showForm.value =
    true;
}


// =====================================================
// 修改表單
// =====================================================

function openEditForm(
  coupon
) {

  editingCouponId.value =
    coupon.couponId;


  form.couponCode =
    coupon.couponCode ?? "";

  form.couponName =
    coupon.couponName ?? "";

  form.discountType =
    coupon.discountType ??
    "FIXED";

  form.discountValue =
    Number(
      coupon.discountValue ?? 0
    );

  form.minimumAmount =
    Number(
      coupon.minimumAmount ?? 0
    );

  form.startDate =
    toDateTimeLocal(
      coupon.startDate
    );

  form.endDate =
    toDateTimeLocal(
      coupon.endDate
    );

  form.status =
    coupon.status ??
    "ACTIVE";


  showForm.value =
    true;


  window.scrollTo({
    top: 0,
    behavior: "smooth",
  });
}


// =====================================================
// 關閉表單
// =====================================================

function closeForm() {

  showForm.value =
    false;

  editingCouponId.value =
    null;

  resetForm();
}


// =====================================================
// Reset
// =====================================================

function resetForm() {

  form.couponCode = "";

  form.couponName = "";

  form.discountType =
    "FIXED";

  form.discountValue =
    100;

  form.minimumAmount =
    0;

  form.startDate = "";

  form.endDate = "";

  form.status =
    "ACTIVE";
}


// =====================================================
// 儲存優惠券
// =====================================================

async function saveCoupon() {

  if (
    !validateForm()
  ) {
    return;
  }


  saving.value =
    true;


  try {

    const body = {

      couponCode:
        form.couponCode
          .trim()
          .toUpperCase(),

      couponName:
        form.couponName
          .trim(),

      discountType:
        form.discountType,

      discountValue:
        Number(
          form.discountValue
        ),

      minimumAmount:
        Number(
          form.minimumAmount
        ),

      startDate:
        normalizeDateTime(
          form.startDate
        ),

      endDate:
        normalizeDateTime(
          form.endDate
        ),

      status:
        form.status,
    };


    let url =
      "/api/coupons";

    let method =
      "POST";


    if (
      editingCouponId.value
    ) {

      url =
        `/api/coupons/${editingCouponId.value}`;

      method =
        "PUT";
    }


    const response =
      await fetch(
        url,
        {
          method,

          headers:
            getAuthHeaders(),

          body:
            JSON.stringify(
              body
            ),
        }
      );


    if (!response.ok) {

      const errorText =
        await response.text();


      throw new Error(
        extractErrorMessage(
          errorText
        ) ||
        "儲存優惠券失敗"
      );
    }


    alert(
      editingCouponId.value
        ? "優惠券修改成功"
        : "優惠券新增成功"
    );


    closeForm();

    await loadCoupons();


  } catch (error) {

    console.error(
      "優惠券儲存錯誤：",
      error
    );

    alert(
      error.message ||
      "優惠券儲存失敗"
    );


  } finally {

    saving.value =
      false;
  }
}


// =====================================================
// 狀態切換
// =====================================================

async function changeStatus(
  coupon,
  status
) {

  const actionText =
    status === "ACTIVE"
      ? "啟用"
      : "停用";


  const confirmed =
    window.confirm(
      `確定要${actionText}優惠券「${coupon.couponName}」嗎？`
    );


  if (!confirmed) {
    return;
  }


  try {

    const response =
      await fetch(
        `/api/coupons/${coupon.couponId}/status?status=${status}`,
        {
          method: "PUT",

          headers:
            getAuthHeaders(),
        }
      );


    if (!response.ok) {

      const errorText =
        await response.text();


      throw new Error(
        extractErrorMessage(
          errorText
        ) ||
        `${actionText}優惠券失敗`
      );
    }


    await loadCoupons();


  } catch (error) {

    console.error(
      "更新優惠券狀態失敗：",
      error
    );

    alert(
      error.message ||
      "更新優惠券狀態失敗"
    );
  }
}


// =====================================================
// 表單驗證
// =====================================================

function validateForm() {

  if (
    !form.couponCode.trim()
  ) {

    alert(
      "請輸入優惠券代碼"
    );

    return false;
  }


  if (
    !form.couponName.trim()
  ) {

    alert(
      "請輸入優惠券名稱"
    );

    return false;
  }


  if (
    Number(
      form.discountValue
    ) <= 0
  ) {

    alert(
      "折扣值必須大於 0"
    );

    return false;
  }


  if (
    form.discountType ===
      "PERCENT"
    &&
    Number(
      form.discountValue
    ) >= 100
  ) {

    alert(
      "百分比折扣值必須小於 100"
    );

    return false;
  }


  if (
    Number(
      form.minimumAmount
    ) < 0
  ) {

    alert(
      "最低消費不可小於 0"
    );

    return false;
  }


  if (
    !form.startDate ||
    !form.endDate
  ) {

    alert(
      "請設定開始與結束時間"
    );

    return false;
  }


  if (
    new Date(
      form.endDate
    ) <=
    new Date(
      form.startDate
    )
  ) {

    alert(
      "結束時間必須晚於開始時間"
    );

    return false;
  }


  return true;
}


// =====================================================
// 顯示格式
// =====================================================

function getDiscountTypeText(
  type
) {

  if (
    type === "FIXED"
  ) {

    return "固定金額";
  }


  if (
    type === "PERCENT"
  ) {

    return "百分比";
  }


  return type;
}


function getDiscountText(
  coupon
) {

  if (
    coupon.discountType ===
    "FIXED"
  ) {

    return (
      "NT$" +
      formatPrice(
        coupon.discountValue
      )
    );
  }


  if (
    coupon.discountType ===
    "PERCENT"
  ) {

    return (
      `折抵 ${coupon.discountValue}%`
    );
  }


  return "-";
}


function formatPrice(
  price
) {

  return Number(
    price ?? 0
  ).toLocaleString(
    "zh-TW"
  );
}


function formatDate(
  date
) {

  if (!date) {
    return "-";
  }


  const value =
    new Date(date);


  if (
    Number.isNaN(
      value.getTime()
    )
  ) {

    return date;
  }


  return value
    .toLocaleString(
      "zh-TW",
      {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
        hour12: false,
      }
    );
}


// =====================================================
// datetime-local
// =====================================================

function toDateTimeLocal(
  value
) {

  if (!value) {
    return "";
  }


  return String(value)
    .substring(
      0,
      16
    );
}


function normalizeDateTime(
  value
) {

  if (!value) {
    return null;
  }


  return (
    value.length === 16
      ? value + ":00"
      : value
  );
}


// =====================================================
// Backend error
// =====================================================

function extractErrorMessage(
  text
) {

  if (!text) {
    return "";
  }


  try {

    const data =
      JSON.parse(text);


    return (
      data.message ||
      data.error ||
      ""
    );


  } catch {

    return text;
  }
}


// =====================================================
// 初始化
// =====================================================

onMounted(() => {

  loadCoupons();
});
</script>


<style scoped>
.coupon-admin-page {
  min-height: 100vh;
  padding: 40px 0;
  background-color: #f5f5f5;
}

.container {
  width: min(1280px, 94%);
  margin: auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  margin-bottom: 24px;
}

.page-header h1 {
  margin: 0 0 5px;
  color: #4a3b2a;
}

.page-header p {
  margin: 0;
  color: #888888;
}

.create-button {
  padding: 11px 20px;
  border: none;
  border-radius: 6px;
  background-color: #b58a46;
  color: white;
  font-weight: bold;
  cursor: pointer;
}

.coupon-form-card {
  margin-bottom: 25px;
  padding: 24px;
  background-color: white;
  border: 1px solid #eeeeee;
  border-radius: 8px;
}

.coupon-form-card h2 {
  margin: 0 0 20px;
  color: #4a3b2a;
}

.form-grid {
  display: grid;
  grid-template-columns:
    repeat(
      2,
      minmax(0, 1fr)
    );
  gap: 18px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.form-group label {
  font-weight: bold;
  color: #555555;
}

.form-group input,
.form-group select {
  padding: 10px 12px;
  border: 1px solid #cccccc;
  border-radius: 5px;
  font-size: 14px;
}

.form-group small {
  color: #888888;
}

.form-actions {
  display: flex;
  gap: 10px;
  margin-top: 22px;
}

.save-button,
.cancel-button {
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
}

.save-button {
  border: none;
  background-color: #b58a46;
  color: white;
}

.cancel-button {
  border: 1px solid #aaaaaa;
  background-color: white;
}

.loading {
  padding: 50px;
  background-color: white;
  text-align: center;
}

.table-wrapper {
  overflow-x: auto;
  background-color: white;
  border-radius: 8px;
}

table {
  width: 100%;
  border-collapse: collapse;
}

th,
td {
  padding: 14px 12px;
  border-bottom: 1px solid #eeeeee;
  text-align: left;
  vertical-align: middle;
}

th {
  background-color: #f8f6f2;
  color: #4a3b2a;
}

.coupon-code {
  font-weight: bold;
  color: #95691f;
}

.date-column {
  min-width: 180px;
  font-size: 13px;
}

.status-badge {
  display: inline-block;
  padding: 5px 10px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: bold;
}

.status-badge.active {
  background-color: #e8f6ec;
  color: #257641;
}

.status-badge.inactive {
  background-color: #eeeeee;
  color: #777777;
}

.action-buttons {
  display: flex;
  gap: 7px;
  flex-wrap: wrap;
}

.edit-button,
.enable-button,
.disable-button {
  padding: 7px 12px;
  border-radius: 5px;
  cursor: pointer;
}

.edit-button {
  border: 1px solid #967038;
  background-color: white;
  color: #967038;
}

.enable-button {
  border: 1px solid #257641;
  background-color: white;
  color: #257641;
}

.disable-button {
  border: 1px solid #b3443c;
  background-color: white;
  color: #b3443c;
}

.empty-row {
  padding: 40px;
  text-align: center;
  color: #888888;
}

@media (max-width: 760px) {
  .page-header {
    align-items: stretch;
    flex-direction: column;
  }

  .create-button {
    width: 100%;
  }

  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>