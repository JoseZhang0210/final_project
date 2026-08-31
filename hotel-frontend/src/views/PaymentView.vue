<template>
  <div class="payment-page">
    <div class="payment-shell">
      <!-- =========================
           左側：訂單摘要
           ========================= -->
      <aside class="order-summary">
        <div class="brand-area">
          <div class="brand-mark">
            SH
          </div>

          <div>
            <div class="brand-name">
              星澄飯店
            </div>

            <div class="brand-subtitle">
              STARLIGHT HOTEL
            </div>
          </div>
        </div>

        <div class="summary-title">
          訂單摘要
        </div>

        <div class="order-number">
          訂單編號
          <strong>
            #{{ orderId }}
          </strong>
        </div>

        <div class="summary-divider"></div>

        <div class="summary-row">
          <span>
            商品金額
          </span>

          <strong>
            NT$
            {{ formatPrice(orderAmount) }}
          </strong>
        </div>

        <div class="summary-row muted">
          <span>
            優惠折抵
          </span>

          <span>
            NT$
            {{ formatPrice(discountAmount) }}
          </span>
        </div>

        <div class="summary-divider"></div>

        <div class="summary-total">
          <span>
            應付金額
          </span>

          <strong>
            NT$
            {{ formatPrice(finalAmount) }}
          </strong>
        </div>

        <div class="security-note">
          <span class="security-icon">
            🔒
          </span>

          本頁為期末專題模擬付款流程，不會進行真實扣款。
        </div>
      </aside>

      <!-- =========================
           右側：付款區
           ========================= -->
      <main class="payment-content">
        <template v-if="!paymentSuccess">
          <div class="payment-header">
            <span class="payment-eyebrow">
              SECURE CHECKOUT
            </span>

            <h1>
              完成付款
            </h1>

            <p>
              選擇付款方式後確認付款，即可完成本次訂單。
            </p>
          </div>

          <!-- =========================
               付款方式
               ========================= -->
          <section class="payment-section">
            <h2>
              付款方式
            </h2>

            <div class="method-grid">
              <button
                type="button"
                class="method-card"
                :class="{
                  active: paymentMethod === '信用卡',
                }"
                @click="paymentMethod = '信用卡'"
              >
                <div class="method-radio">
                  <span
                    v-if="paymentMethod === '信用卡'"
                    class="radio-dot"
                  ></span>
                </div>

                <div class="method-icon">
                  💳
                </div>

                <div class="method-info">
                  <strong>
                    信用卡
                  </strong>

                  <span>
                    VISA / MasterCard / JCB
                  </span>
                </div>
              </button>

              <button
                type="button"
                class="method-card"
                :class="{
                  active: paymentMethod === 'LINE PAY',
                }"
                @click="paymentMethod = 'LINE PAY'"
              >
                <div class="method-radio">
                  <span
                    v-if="paymentMethod === 'LINE PAY'"
                    class="radio-dot"
                  ></span>
                </div>

                <div class="method-icon line-pay-icon">
                  LINE
                </div>

                <div class="method-info">
                  <strong>
                    LINE Pay
                  </strong>

                  <span>
                    使用行動支付快速完成
                  </span>
                </div>
              </button>

              <button
                type="button"
                class="method-card"
                :class="{
                  active: paymentMethod === '現金',
                }"
                @click="paymentMethod = '現金'"
              >
                <div class="method-radio">
                  <span
                    v-if="paymentMethod === '現金'"
                    class="radio-dot"
                  ></span>
                </div>

                <div class="method-icon">
                  💵
                </div>

                <div class="method-info">
                  <strong>
                    現金
                  </strong>

                  <span>
                    模擬櫃檯現金付款
                  </span>
                </div>
              </button>
            </div>
          </section>

          <!-- =========================
               信用卡展示
               ========================= -->
          <section
            v-if="paymentMethod === '信用卡'"
            class="payment-section"
          >
            <h2>
              信用卡資料
            </h2>

            <div class="credit-card-preview">
              <div class="credit-card-top">
                <span class="chip">
                  ▰
                </span>

                <span class="card-brand">
                  STARLIGHT
                </span>
              </div>

              <div class="card-number">
                •••• &nbsp; •••• &nbsp; •••• &nbsp; 4242
              </div>

              <div class="credit-card-bottom">
                <div>
                  <span>
                    CARD HOLDER
                  </span>

                  <strong>
                    HOTEL GUEST
                  </strong>
                </div>

                <div>
                  <span>
                    EXPIRES
                  </span>

                  <strong>
                    12 / 29
                  </strong>
                </div>
              </div>
            </div>

            <div class="fake-form">
              <div class="fake-field full">
                <label>
                  信用卡卡號
                </label>

                <div class="fake-input">
                  •••• •••• •••• 4242
                </div>
              </div>

              <div class="fake-field full">
                <label>
                  持卡人姓名
                </label>

                <div class="fake-input">
                  HOTEL GUEST
                </div>
              </div>

              <div class="fake-field">
                <label>
                  有效期限
                </label>

                <div class="fake-input">
                  12 / 29
                </div>
              </div>

              <div class="fake-field">
                <label>
                  安全碼
                </label>

                <div class="fake-input">
                  •••
                </div>
              </div>
            </div>
          </section>

          <!-- =========================
               LINE Pay 展示
               ========================= -->
          <section
            v-else-if="paymentMethod === 'LINE PAY'"
            class="payment-section payment-demo-box"
          >
            <div class="demo-icon line-demo">
              LINE
            </div>

            <div>
              <h2>
                LINE Pay
              </h2>

              <p>
                確認付款後啟動 LINE Pay 授權成功。
              </p>
            </div>
          </section>

          <!-- =========================
               現金展示
               ========================= -->
          <section
            v-else
            class="payment-section payment-demo-box"
          >
            <div class="demo-icon">
              💵
            </div>

            <div>
              <h2>
                現金付款
              </h2>

              <p>
                確認後請至櫃檯完成付款。
              </p>
            </div>
          </section>

          <!-- =========================
               付款按鈕
               ========================= -->
          <button
            type="button"
            class="pay-button"
            :disabled="paying"
            @click="pay"
          >
            <template v-if="paying">
              付款處理中...
            </template>

            <template v-else>
              確認付款
              <span>
                NT$
                {{ formatPrice(finalAmount) }}
              </span>
            </template>
          </button>

          <div class="payment-footer">
            <span>
              🔒
            </span>

            付款資料僅供專題流程展示使用
          </div>
        </template>

        <!-- =========================
             付款成功
             ========================= -->
        <template v-else>
          <div class="success-area">
            <div class="success-icon">
              ✓
            </div>

            <span class="payment-eyebrow">
              PAYMENT SUCCESS
            </span>

            <h1>
              付款成功
            </h1>

            <p class="success-description">
              您的訂單已完成付款，可以前往「我的訂單」查看購買紀錄。
            </p>

            <div
              v-if="payment"
              class="success-detail"
            >
              <div class="success-row">
                <span>
                  訂單編號
                </span>

                <strong>
                  #{{ orderId }}
                </strong>
              </div>

              <div class="success-row">
                <span>
                  Payment ID
                </span>

                <strong>
                  {{ payment.paymentId }}
                </strong>
              </div>

              <div class="success-row">
                <span>
                  交易編號
                </span>

                <strong>
                  {{ payment.transactionId || "-" }}
                </strong>
              </div>

              <div class="success-row">
                <span>
                  付款方式
                </span>

                <strong>
                  {{ paymentMethod }}
                </strong>
              </div>

              <div class="success-row">
                <span>
                  付款狀態
                </span>

                <strong class="paid-status">
                  {{ payment.paymentStatus }}
                </strong>
              </div>

              <div class="success-row total-row">
                <span>
                  付款金額
                </span>

                <strong>
                  NT$
                  {{ formatPrice(payment.totalPrice) }}
                </strong>
              </div>
            </div>

            <button
              type="button"
              class="my-orders-button"
              @click="goMyOrders"
            >
              查看我的訂單
            </button>

            <button
              type="button"
              class="back-shop-button"
              @click="goProducts"
            >
              繼續逛商城
            </button>
          </div>
        </template>
      </main>
    </div>
  </div>
</template>

<script setup>
import {
  onMounted,
  ref,
} from "vue";

import {
  useRoute,
  useRouter,
} from "vue-router";

import {
  getAuthHeaders,
} from "@/utils/auth";

const route = useRoute();

const router = useRouter();

// =====================================================
// 訂單 ID
// =====================================================

const orderId =
  Number(route.params.orderId);

// =====================================================
// 付款狀態
// =====================================================

const paymentMethod =
  ref("信用卡");

const payment =
  ref(null);

const paymentSuccess =
  ref(false);

const paying =
  ref(false);

// =====================================================
// 訂單金額
// =====================================================

const orderAmount =
  ref(0);

const discountAmount =
  ref(0);

const finalAmount =
  ref(0);

// =====================================================
// 價格格式
// =====================================================

function formatPrice(value) {
  return Number(
    value ?? 0,
  ).toLocaleString("zh-TW");
}

// =====================================================
// 取得目前訂單
// GET /api/orders/{orderId}
// 如果你的後端目前沒有這支 API，
// 下面 catch 後仍然可以正常使用付款功能。
// =====================================================

async function loadOrder() {
  if (!orderId) {
    return;
  }

  try {
    const response =
      await fetch(
        `/api/orders/${orderId}`,
        {
          method: "GET",

          headers:
            getAuthHeaders(),
        },
      );

    if (!response.ok) {
      console.warn(
        "目前無法取得訂單詳細資料：",
        response.status,
      );

      return;
    }

    const order =
      await response.json();

    console.log(
      "付款頁訂單資料：",
      order,
    );

    orderAmount.value =
      Number(
        order.originalAmount ??
          order.finalAmount ??
          0,
      );

    discountAmount.value =
      Number(
        order.discountAmount ??
          0,
      );

    finalAmount.value =
      Number(
        order.finalAmount ??
          order.originalAmount ??
          0,
      );

  } catch (error) {
    console.warn(
      "讀取付款訂單失敗：",
      error,
    );
  }
}

// =====================================================
// 付款
// =====================================================

async function pay() {
  if (!orderId) {
    alert(
      "訂單編號錯誤",
    );

    return;
  }

  paying.value = true;

  try {
    // ==========================
    // 1. 建立 Payment
    // ==========================

    const createResponse =
      await fetch(
        `/api/payments/order/${orderId}?paymentMethod=${encodeURIComponent(
          paymentMethod.value,
        )}`,
        {
          method: "POST",

          headers:
            getAuthHeaders(),
        },
      );

    if (
      createResponse.status === 401 ||
      createResponse.status === 403
    ) {
      throw new Error(
        "登入已失效，請重新登入",
      );
    }

    if (!createResponse.ok) {
      const message =
        await createResponse.text();

      throw new Error(
        message ||
          `建立付款資料失敗：${createResponse.status}`,
      );
    }

    const createdPayment =
      await createResponse.json();

    console.log(
      "付款資料建立成功：",
      createdPayment,
    );

    // ==========================
    // 2. 模擬付款成功
    // ==========================

    const confirmResponse =
      await fetch(
        `/api/payments/${createdPayment.paymentId}/confirm`,
        {
          method: "PUT",

          headers:
            getAuthHeaders(),
        },
      );

    if (
      confirmResponse.status === 401 ||
      confirmResponse.status === 403
    ) {
      throw new Error(
        "登入已失效，請重新登入",
      );
    }

    if (!confirmResponse.ok) {
      const message =
        await confirmResponse.text();

      throw new Error(
        message ||
          `付款確認失敗：${confirmResponse.status}`,
      );
    }

    payment.value =
      await confirmResponse.json();

    paymentSuccess.value =
      true;

    console.log(
      "模擬付款成功：",
      payment.value,
    );

  } catch (error) {
    console.error(
      "付款失敗：",
      error,
    );

    alert(
      error.message ||
        "付款失敗",
    );

  } finally {
    paying.value = false;
  }
}

// =====================================================
// 我的訂單
// =====================================================

function goMyOrders() {
  router.push(
    "/my-orders",
  );
}

// =====================================================
// 商城
// =====================================================

function goProducts() {
  router.push(
    "/products",
  );
}

// =====================================================
// 初始化
// =====================================================

onMounted(() => {
  loadOrder();
});
</script>

<style scoped>
.payment-page {
  min-height: 100vh;
  padding: 60px 20px;

  font-family:
    Arial,
    "Microsoft JhengHei",
    sans-serif;

  background:
    linear-gradient(
      135deg,
      #f6f2eb 0%,
      #ebe2d4 100%
    );
}

.payment-shell {
  width: min(
    1080px,
    100%
  );

  min-height: 660px;

  margin: 0 auto;

  display: grid;

  grid-template-columns:
    360px 1fr;

  overflow: hidden;

  border-radius: 20px;

  background: #ffffff;

  box-shadow:
    0 22px 60px
      rgba(
        79,
        59,
        35,
        0.14
      );
}

/* =========================
   左側摘要
   ========================= */

.order-summary {
  padding: 42px 34px;

  color: #ffffff;

  background:
    linear-gradient(
      160deg,
      #3e3226,
      #6d5437
    );
}

.brand-area {
  display: flex;

  align-items: center;

  gap: 14px;

  margin-bottom: 60px;
}

.brand-mark {
  width: 46px;

  height: 46px;

  display: flex;

  align-items: center;

  justify-content: center;

  border:
    1px solid
    rgba(
      255,
      255,
      255,
      0.45
    );

  border-radius: 50%;

  font-family: serif;

  font-size: 17px;

  letter-spacing: 1px;
}

.brand-name {
  font-size: 19px;

  font-weight: bold;

  letter-spacing: 2px;
}

.brand-subtitle {
  margin-top: 3px;

  font-size: 10px;

  letter-spacing: 2px;

  opacity: 0.72;
}

.summary-title {
  margin-bottom: 18px;

  font-size: 25px;

  font-weight: bold;
}

.order-number {
  display: flex;

  justify-content:
    space-between;

  color:
    rgba(
      255,
      255,
      255,
      0.72
    );
}

.order-number strong {
  color: #ffffff;
}

.summary-divider {
  height: 1px;

  margin: 25px 0;

  background:
    rgba(
      255,
      255,
      255,
      0.16
    );
}

.summary-row {
  display: flex;

  justify-content:
    space-between;

  margin-bottom: 17px;
}

.summary-row.muted {
  color:
    rgba(
      255,
      255,
      255,
      0.65
    );
}

.summary-total {
  display: flex;

  align-items:
    flex-end;

  justify-content:
    space-between;
}

.summary-total span {
  font-size: 16px;
}

.summary-total strong {
  font-size: 28px;

  color: #e8c98f;
}

.security-note {
  margin-top: 80px;

  padding: 18px;

  border:
    1px solid
    rgba(
      255,
      255,
      255,
      0.15
    );

  border-radius: 10px;

  color:
    rgba(
      255,
      255,
      255,
      0.7
    );

  font-size: 12px;

  line-height: 1.7;
}

.security-icon {
  margin-right: 5px;
}

/* =========================
   右側
   ========================= */

.payment-content {
  padding: 48px 54px;
}

.payment-eyebrow {
  color: #a57b3d;

  font-size: 11px;

  font-weight: bold;

  letter-spacing: 3px;
}

.payment-header h1,
.success-area h1 {
  margin:
    10px 0
    10px;

  color: #352c24;

  font-size: 32px;
}

.payment-header p {
  margin: 0;

  color: #84796e;

  line-height: 1.7;
}

.payment-section {
  margin-top: 35px;
}

.payment-section h2 {
  margin-bottom: 15px;

  color: #45392f;

  font-size: 17px;
}

/* =========================
   付款方式
   ========================= */

.method-grid {
  display: grid;

  gap: 10px;
}

.method-card {
  width: 100%;

  min-height: 74px;

  display: grid;

  grid-template-columns:
    24px 42px 1fr;

  align-items: center;

  gap: 12px;

  padding: 14px 16px;

  border:
    1px solid
    #ddd5ca;

  border-radius: 10px;

  background: #ffffff;

  text-align: left;

  cursor: pointer;

  transition: 0.2s;
}

.method-card:hover {
  border-color:
    #b58a46;

  background:
    #fcfaf6;
}

.method-card.active {
  border-color:
    #b58a46;

  background:
    #fbf7ef;

  box-shadow:
    0 0 0 2px
      rgba(
        181,
        138,
        70,
        0.08
      );
}

.method-radio {
  width: 18px;

  height: 18px;

  display: flex;

  align-items: center;

  justify-content: center;

  border:
    1px solid
    #b7aa9a;

  border-radius: 50%;
}

.method-card.active
.method-radio {
  border-color:
    #b58a46;
}

.radio-dot {
  width: 9px;

  height: 9px;

  border-radius: 50%;

  background:
    #b58a46;
}

.method-icon {
  font-size: 24px;
}

.line-pay-icon,
.line-demo {
  color: #ffffff;

  border-radius: 6px;

  background: #24c25e;

  font-size: 10px;

  font-weight: bold;

  text-align: center;
}

.line-pay-icon {
  padding: 8px 3px;
}

.method-info {
  display: flex;

  flex-direction: column;

  gap: 4px;
}

.method-info strong {
  color: #3e332a;

  font-size: 14px;
}

.method-info span {
  color: #91877d;

  font-size: 12px;
}

/* =========================
   信用卡
   ========================= */

.credit-card-preview {
  max-width: 390px;

  min-height: 190px;

  box-sizing: border-box;

  padding: 25px;

  border-radius: 16px;

  color: #ffffff;

  background:
    linear-gradient(
      135deg,
      #3e3227,
      #806241
    );

  box-shadow:
    0 18px 35px
      rgba(
        66,
        47,
        29,
        0.22
      );
}

.credit-card-top,
.credit-card-bottom {
  display: flex;

  align-items: center;

  justify-content:
    space-between;
}

.chip {
  color: #e5c587;

  font-size: 30px;
}

.card-brand {
  font-family: serif;

  letter-spacing: 2px;

  opacity: 0.85;
}

.card-number {
  margin:
    30px 0
    25px;

  font-size: 20px;

  letter-spacing: 2px;
}

.credit-card-bottom span {
  display: block;

  margin-bottom: 4px;

  font-size: 8px;

  letter-spacing: 1.5px;

  opacity: 0.6;
}

.credit-card-bottom strong {
  font-size: 11px;

  letter-spacing: 1px;
}

/* =========================
   假欄位
   ========================= */

.fake-form {
  display: grid;

  grid-template-columns:
    1fr 1fr;

  gap: 14px;

  margin-top: 22px;
}

.fake-field.full {
  grid-column:
    1 / -1;
}

.fake-field label {
  display: block;

  margin-bottom: 7px;

  color: #71665b;

  font-size: 12px;

  font-weight: bold;
}

.fake-input {
  padding: 12px 14px;

  border:
    1px solid
    #ded7cf;

  border-radius: 8px;

  color: #80776e;

  background: #f8f7f5;

  font-size: 14px;
}

/* =========================
   其他付款法
   ========================= */

.payment-demo-box {
  display: flex;

  align-items: center;

  gap: 18px;

  padding: 25px;

  border:
    1px solid
    #e0d8cf;

  border-radius: 12px;

  background: #faf8f5;
}

.demo-icon {
  width: 56px;

  height: 56px;

  display: flex;

  align-items: center;

  justify-content: center;

  border-radius: 10px;

  background: #eee8df;

  font-size: 27px;
}

.line-demo {
  background: #24c25e;

  font-size: 12px;
}

.payment-demo-box h2 {
  margin:
    0 0 6px;
}

.payment-demo-box p {
  margin: 0;

  color: #81786f;

  font-size: 13px;
}

/* =========================
   付款按鈕
   ========================= */

.pay-button {
  width: 100%;

  display: flex;

  justify-content:
    space-between;

  margin-top: 32px;

  padding: 16px 20px;

  border: none;

  border-radius: 9px;

  color: #ffffff;

  background:
    #a67c3d;

  font-size: 15px;

  font-weight: bold;

  cursor: pointer;

  transition: 0.2s;
}

.pay-button:hover {
  background:
    #88632f;

  transform:
    translateY(-1px);
}

.pay-button:disabled {
  opacity: 0.55;

  cursor:
    not-allowed;

  transform: none;
}

.payment-footer {
  margin-top: 13px;

  color: #9a9187;

  font-size: 11px;

  text-align: center;
}

/* =========================
   成功畫面
   ========================= */

.success-area {
  max-width: 540px;

  margin:
    45px auto 0;

  text-align: center;
}

.success-icon {
  width: 72px;

  height: 72px;

  display: flex;

  align-items: center;

  justify-content: center;

  margin:
    0 auto
    24px;

  border-radius: 50%;

  color: #ffffff;

  background: #4c8964;

  font-size: 35px;

  font-weight: bold;
}

.success-description {
  margin:
    0 auto
    28px;

  color: #80766d;

  line-height: 1.7;
}

.success-detail {
  margin-top: 30px;

  padding: 10px 22px;

  border:
    1px solid
    #e2ddd6;

  border-radius: 12px;

  background: #faf9f7;

  text-align: left;
}

.success-row {
  display: flex;

  justify-content:
    space-between;

  padding: 14px 0;

  border-bottom:
    1px solid
    #e6e1da;

  color: #71685f;

  font-size: 14px;
}

.success-row:last-child {
  border-bottom: none;
}

.success-row strong {
  color: #3e352d;
}

.paid-status {
  color: #41825b
    !important;
}

.total-row {
  font-size: 16px;
}

.total-row strong {
  color: #9a7033;

  font-size: 18px;
}

.my-orders-button {
  width: 100%;

  margin-top: 25px;

  padding: 14px;

  border: none;

  border-radius: 8px;

  color: #ffffff;

  background: #a67c3d;

  font-size: 15px;

  font-weight: bold;

  cursor: pointer;
}

.back-shop-button {
  width: 100%;

  margin-top: 10px;

  padding: 13px;

  border:
    1px solid
    #b99969;

  border-radius: 8px;

  color: #896733;

  background: #ffffff;

  cursor: pointer;
}

/* =========================
   RWD
   ========================= */

@media (
  max-width: 820px
) {
  .payment-page {
    padding: 20px 12px;
  }

  .payment-shell {
    grid-template-columns:
      1fr;
  }

  .order-summary {
    padding: 30px;
  }

  .brand-area {
    margin-bottom: 35px;
  }

  .security-note {
    margin-top: 35px;
  }

  .payment-content {
    padding: 35px 28px;
  }
}

@media (
  max-width: 520px
) {
  .payment-content {
    padding: 30px 20px;
  }

  .order-summary {
    padding: 26px 20px;
  }

  .fake-form {
    grid-template-columns:
      1fr;
  }

  .fake-field.full {
    grid-column: auto;
  }

  .summary-total strong {
    font-size: 23px;
  }

  .card-number {
    font-size: 16px;
  }
}
</style>