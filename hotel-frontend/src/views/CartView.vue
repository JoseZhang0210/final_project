<template>
  <div class="cart-page">
    <div class="cart-container">

      <h1>購物車</h1>

      <!-- =====================================================
           空購物車
           ===================================================== -->
      <div
        v-if="cartItems.length === 0"
        class="empty-cart"
      >
        購物車目前沒有商品
      </div>


      <!-- =====================================================
           購物車內容
           ===================================================== -->
      <div v-else>

        <!-- 商品 -->
        <div
          v-for="item in cartItems"
          :key="item.productId"
          class="cart-item"
        >

          <!-- 商品資訊 -->
          <div class="item-info">

            <h3>
              {{ item.productName }}
            </h3>

            <p>
              單價：
              NT$
              {{ formatPrice(item.price) }}
            </p>

          </div>


          <!-- 數量 -->
          <div class="quantity-control">

            <button
              type="button"
              :disabled="
                Number(item.quantity) <= 1
              "
              @click="
                decreaseQuantity(item)
              "
            >
              −
            </button>


            <span>
              {{ item.quantity }}
            </span>


            <button
              type="button"
              :disabled="
                Number(item.quantity) >=
                Number(item.stock)
              "
              @click="
                increaseQuantity(item)
              "
            >
              ＋
            </button>

          </div>


          <!-- 小計 -->
          <div class="subtotal">
            小計：
            NT$
            {{
              formatPrice(
                Number(item.price) *
                Number(item.quantity)
              )
            }}
          </div>


          <!-- 刪除 -->
          <button
            type="button"
            class="delete-button"
            @click="
              removeItem(item.productId)
            "
          >
            刪除
          </button>

        </div>


        <!-- =====================================================
             優惠券
             ===================================================== -->
        <div class="coupon-section">

          <div class="coupon-header">
            <div>
              <h2>優惠券</h2>
              <p>
                輸入優惠券代碼即可套用活動優惠
              </p>
            </div>
          </div>


          <div class="coupon-input-row">

            <input
              v-model="couponCode"
              type="text"
              class="coupon-input"
              placeholder="請輸入優惠券代碼"
              :disabled="
                couponLoading ||
                !!appliedCouponCode
              "
              @keyup.enter="applyCoupon"
            />


            <!-- 尚未套用 -->
            <button
              v-if="!appliedCouponCode"
              type="button"
              class="apply-coupon-button"
              :disabled="couponLoading"
              @click="applyCoupon"
            >
              {{
                couponLoading
                  ? "套用中..."
                  : "套用優惠券"
              }}
            </button>


            <!-- 已套用 -->
            <button
              v-else
              type="button"
              class="remove-coupon-button"
              @click="removeCoupon"
            >
              取消優惠
            </button>

          </div>


          <!-- 訊息 -->
          <div
            v-if="couponMessage"
            class="coupon-message"
            :class="{
              success:
                !!appliedCouponCode
            }"
          >
            {{ couponMessage }}
          </div>


          <!-- 已套用優惠券 -->
          <div
            v-if="appliedCouponCode"
            class="applied-coupon"
          >
            <div>
              已套用優惠：
              <strong>
                {{ couponName }}
              </strong>
            </div>

            <div class="coupon-code-text">
              優惠碼：
              {{ appliedCouponCode }}
            </div>
          </div>

        </div>


        <!-- =====================================================
             金額摘要
             ===================================================== -->
        <div class="cart-summary">

          <!-- 商品金額 -->
          <div class="summary-row">
            <span>
              商品金額
            </span>

            <span>
              NT$
              {{
                formatPrice(
                  totalAmount
                )
              }}
            </span>
          </div>


          <!-- 優惠折抵 -->
          <div
            v-if="
              discountAmount > 0
            "
            class="
              summary-row
              discount-row
            "
          >
            <span>
              優惠折抵
            </span>

            <span>
              - NT$
              {{
                formatPrice(
                  discountAmount
                )
              }}
            </span>
          </div>


          <!-- 套用優惠券名稱 -->
          <div
            v-if="appliedCouponCode"
            class="
              summary-row
              coupon-summary-row
            "
          >
            <span>
              使用優惠券
            </span>

            <span>
              {{ couponName }}
            </span>
          </div>


          <!-- 分隔線 -->
          <div class="summary-divider"></div>


          <!-- 最終金額 -->
          <div class="summary-total">

            <span>
              總金額
            </span>

            <strong>
              NT$
              {{
                formatPrice(
                  payableAmount
                )
              }}
            </strong>

          </div>
          <div class="checkout-notice">
            ※ 訂單付款完成後將無法取消或修改，
            請於付款前確認商品與數量。
          </div>

          <!-- 結帳 -->
          <button
            type="button"
            class="checkout-button"
            :disabled="
              submitting ||
              couponLoading
            "
            @click="submitOrder"
          >
            {{
              submitting
                ? "建立訂單中..."
                : "確認結帳"
            }}
          </button>

        </div>

      </div>

    </div>
  </div>
</template>


<script setup>
import {
  computed,
  onMounted,
  ref,
} from "vue";

import {
  useRouter,
} from "vue-router";


const router =
  useRouter();


// =====================================================
// 購物車
// =====================================================

const cartItems =
  ref([]);

const submitting =
  ref(false);


// =====================================================
// 優惠券
// =====================================================

const couponCode =
  ref("");

const appliedCouponCode =
  ref("");

const couponName =
  ref("");

const discountAmount =
  ref(0);

const finalAmount =
  ref(0);

const couponMessage =
  ref("");

const couponLoading =
  ref(false);


// =====================================================
// JWT Header
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
// 載入購物車
// =====================================================

function loadCart() {

  const savedCart =
    localStorage.getItem(
      "cart"
    );


  if (!savedCart) {

    cartItems.value = [];

    return;
  }


  try {

    const parsedCart =
      JSON.parse(
        savedCart
      );


    if (
      !Array.isArray(
        parsedCart
      )
    ) {

      console.warn(
        "購物車資料不是陣列：",
        parsedCart
      );

      cartItems.value = [];

      return;
    }


    cartItems.value =
      parsedCart.map(
        (item) => ({
          ...item,

          productId:
            Number(
              item.productId
            ),

          price:
            Number(
              item.price ?? 0
            ),

          quantity:
            Math.max(
              1,
              Number(
                item.quantity ?? 1
              )
            ),

          stock:
            Number(
              item.stock ?? 0
            ),
        })
      );


  } catch (error) {

    console.error(
      "購物車資料錯誤：",
      error
    );

    cartItems.value = [];
  }
}


// =====================================================
// 儲存購物車
// =====================================================

function saveCart() {

  localStorage.setItem(
    "cart",
    JSON.stringify(
      cartItems.value
    )
  );
}


// =====================================================
// 清除目前優惠券結果
//
// 購物車內容有變動時，
// 原優惠券計算結果可能已經不正確。
// =====================================================

function resetCoupon() {

  couponCode.value = "";

  appliedCouponCode.value = "";

  couponName.value = "";

  discountAmount.value = 0;

  finalAmount.value =
    totalAmount.value;

  couponMessage.value = "";
}


// =====================================================
// 增加數量
// =====================================================

function increaseQuantity(item) {

  const quantity =
    Number(
      item.quantity ?? 1
    );

  const stock =
    Number(
      item.stock ?? 0
    );


  if (
    quantity >= stock
  ) {
    return;
  }


  item.quantity =
    quantity + 1;


  saveCart();

  // 商品金額改變
  // 優惠券必須重新套用
  resetCoupon();
}


// =====================================================
// 減少數量
// =====================================================

function decreaseQuantity(item) {

  const quantity =
    Number(
      item.quantity ?? 1
    );


  if (
    quantity <= 1
  ) {
    return;
  }


  item.quantity =
    quantity - 1;


  saveCart();

  resetCoupon();
}


// =====================================================
// 刪除商品
// =====================================================

function removeItem(productId) {

  cartItems.value =
    cartItems.value.filter(
      (item) =>
        Number(
          item.productId
        ) !==
        Number(
          productId
        )
    );


  saveCart();

  resetCoupon();
}


// =====================================================
// 商品總金額
// =====================================================

const totalAmount =
  computed(() => {

    return cartItems.value.reduce(
      (
        total,
        item
      ) => {

        return (
          total +
          Number(
            item.price ?? 0
          ) *
          Number(
            item.quantity ?? 0
          )
        );
      },
      0
    );
  });


// =====================================================
// 最終應付金額
// =====================================================

const payableAmount =
  computed(() => {

    if (
      appliedCouponCode.value
    ) {

      return Number(
        finalAmount.value ?? 0
      );
    }


    return totalAmount.value;
  });


// =====================================================
// 套用優惠券
//
// GET
// /api/coupons/validate
// ?code=...
// &amount=...
// =====================================================

async function applyCoupon() {

  couponMessage.value =
    "";


  // ==============================
  // 檢查優惠碼
  // ==============================

  if (
    !couponCode.value.trim()
  ) {

    couponMessage.value =
      "請輸入優惠券代碼";

    return;
  }


  // ==============================
  // 購物車檢查
  // ==============================

  if (
    totalAmount.value <= 0
  ) {

    couponMessage.value =
      "購物車目前沒有商品";

    return;
  }


  couponLoading.value =
    true;


  try {

    const code =
      couponCode.value
        .trim()
        .toUpperCase();


    const response =
      await fetch(
        `/api/coupons/validate?code=${encodeURIComponent(
          code
        )}&amount=${totalAmount.value}`,
        {
          method: "GET",

          headers:
            getAuthHeaders(),
        }
      );


    // ==============================
    // JWT
    // ==============================

    if (
      response.status === 401 ||
      response.status === 403
    ) {

      throw new Error(
        "登入已失效，請重新登入"
      );
    }


    // ==============================
    // 優惠券錯誤
    // ==============================

    if (!response.ok) {

      const errorText =
        await response.text();


      console.error(
        "優惠券驗證失敗：",
        errorText
      );


      throw new Error(
        extractErrorMessage(
          errorText
        ) ||
        "優惠券無法使用"
      );
    }


    // ==============================
    // API JSON
    // ==============================

    const data =
      await response.json();


    console.log(
      "優惠券驗證結果：",
      data
    );


    appliedCouponCode.value =
      data.couponCode;

    couponCode.value =
      data.couponCode;

    couponName.value =
      data.couponName;

    discountAmount.value =
      Number(
        data.discountAmount ?? 0
      );

    finalAmount.value =
      Number(
        data.finalAmount ??
        totalAmount.value
      );


    couponMessage.value =
      `${data.couponName} 已成功套用`;


  } catch (error) {

    console.error(
      "優惠券套用失敗：",
      error
    );


    appliedCouponCode.value =
      "";

    couponName.value =
      "";

    discountAmount.value =
      0;

    finalAmount.value =
      totalAmount.value;


    couponMessage.value =
      error.message ||
      "優惠券無法使用";


  } finally {

    couponLoading.value =
      false;
  }
}


// =====================================================
// 嘗試解析後端錯誤訊息
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
// 移除優惠券
// =====================================================

function removeCoupon() {

  resetCoupon();

  couponMessage.value =
    "已取消使用優惠券";
}


// =====================================================
// 價格格式
// =====================================================

function formatPrice(price) {

  return Number(
    price ?? 0
  ).toLocaleString(
    "zh-TW"
  );
}


// =====================================================
// 確認結帳
// =====================================================

async function submitOrder() {

  // ==============================
  // 1. 購物車檢查
  // ==============================

  if (
    cartItems.value.length === 0
  ) {

    alert(
      "購物車沒有商品"
    );

    return;
  }


  // ==============================
  // 2. 登入檢查 (memberId 由後端 JWT 解析)
  // ==============================

  const token =
    localStorage.getItem(
      "token"
    );

  if (!token) {
    alert(
      "請使用會員帳號登入後再結帳"
    );

    router.push("/login");
    return;
  }


  // ==============================
  // 3. 數量檢查
  // ==============================

  const invalidItem =
    cartItems.value.find(
      (item) => {

        const quantity =
          Number(
            item.quantity ?? 0
          );

        const stock =
          Number(
            item.stock ?? 0
          );


        return (
          quantity <= 0 ||
          quantity > stock
        );
      }
    );


  if (invalidItem) {

    alert(
      `${invalidItem.productName} 的購買數量不正確，請重新確認`
    );

    return;
  }


  submitting.value =
    true;


  try {

    // ==============================
    // 4. Request JSON
    // ==============================

    const requestBody = {

      couponCode:
        appliedCouponCode.value ||
        null,

      items:
        cartItems.value.map(
          (item) => ({

            productId:
              item.productId,

            quantity:
              item.quantity,
          })
        ),
    };


    console.log(
      "準備送出的訂單：",
      requestBody
    );


    // ==============================
    // 5. 建立訂單
    //
    // 後端會再次驗證優惠券
    // ==============================

    const response =
      await fetch(
        "/api/orders",
        {
          method: "POST",

          headers:
            getAuthHeaders(),

          body:
            JSON.stringify(
              requestBody
            ),
        }
      );


    console.log(
      "建立訂單 API status：",
      response.status
    );


    // ==============================
    // 6. JWT
    // ==============================

    if (
      response.status === 401 ||
      response.status === 403
    ) {

      throw new Error(
        "登入已失效，請重新登入"
      );
    }


    // ==============================
    // 7. 後端錯誤
    // ==============================

    if (!response.ok) {

      const errorText =
        await response.text();


      console.error(
        "建立訂單後端錯誤：",
        errorText
      );


      throw new Error(
        extractErrorMessage(
          errorText
        ) ||
        `建立訂單失敗 (${response.status})`
      );
    }


    // ==============================
    // 8. 建立成功
    // ==============================

    const order =
      await response.json();


    console.log(
      "建立訂單成功：",
      order
    );


    if (
      !order?.orderId
    ) {

      throw new Error(
        "訂單已建立，但沒有取得 orderId"
      );
    }


    // ==============================
    // 9. 清空購物車
    // ==============================

    cartItems.value = [];

    localStorage.removeItem(
      "cart"
    );


    // 清除優惠券
    resetCoupon();


    // ==============================
    // 10. PaymentView
    // ==============================

    await router.push(
      `/payment/${order.orderId}`
    );


  } catch (error) {

    console.error(
      "結帳失敗：",
      error
    );


    alert(
      error.message ||
      "結帳失敗"
    );


  } finally {

    submitting.value =
      false;
  }
}


// =====================================================
// 初始化
// =====================================================

onMounted(() => {

  loadCart();
});

</script>


<style scoped>
/* =====================================================
   Page
   ===================================================== */

.cart-page {
  min-height: 100vh;

  padding:
    40px 0;

  background-color:
    #f5f5f5;
}


.cart-container {
  width:
    min(1000px, 92%);

  margin: auto;
}


.cart-container h1 {
  margin-bottom: 24px;

  color:
    #4a3b2a;
}


/* =====================================================
   商品
   ===================================================== */

.cart-item {
  display: grid;

  grid-template-columns:
    1fr auto auto auto;

  align-items:
    center;

  gap: 24px;

  margin-bottom:
    12px;

  padding:
    20px;

  background-color:
    white;

  border:
    1px solid #eeeeee;

  border-radius:
    8px;
}


.item-info h3 {
  margin:
    0 0 8px;

  color:
    #333333;
}


.item-info p {
  margin: 0;

  color:
    #777777;
}


/* =====================================================
   數量
   ===================================================== */

.quantity-control {
  display: flex;

  align-items:
    center;

  border:
    1px solid #dddddd;

  border-radius:
    5px;

  overflow:
    hidden;
}


.quantity-control button {
  width: 38px;

  height: 36px;

  border: none;

  background-color:
    #f7f7f7;

  cursor: pointer;
}


.quantity-control button:hover:not(:disabled) {
  background-color:
    #eeeeee;
}


.quantity-control button:disabled {
  color:
    #cccccc;

  cursor:
    not-allowed;
}


.quantity-control span {
  min-width:
    42px;

  text-align:
    center;
}


/* =====================================================
   小計
   ===================================================== */

.subtotal {
  min-width:
    150px;

  color:
    #b3443c;

  font-weight:
    bold;
}


/* =====================================================
   刪除
   ===================================================== */

.delete-button {
  padding:
    8px 14px;

  border:
    1px solid #b3443c;

  border-radius:
    5px;

  background-color:
    white;

  color:
    #b3443c;

  cursor:
    pointer;
}


.delete-button:hover {
  background-color:
    #fde9e7;
}


/* =====================================================
   優惠券
   ===================================================== */

.coupon-section {
  margin-top:
    24px;

  padding:
    22px;

  background-color:
    white;

  border:
    1px solid #eeeeee;

  border-radius:
    8px;
}


.coupon-header h2 {
  margin:
    0 0 5px;

  color:
    #4a3b2a;

  font-size:
    19px;
}


.coupon-header p {
  margin:
    0 0 16px;

  color:
    #888888;

  font-size:
    13px;
}


.coupon-input-row {
  display: flex;

  align-items:
    center;

  gap: 10px;

  flex-wrap:
    wrap;
}


.coupon-input {
  flex: 1;

  min-width:
    220px;

  padding:
    11px 13px;

  border:
    1px solid #cccccc;

  border-radius:
    6px;

  font-size:
    14px;

  outline:
    none;
}


.coupon-input:focus {
  border-color:
    #b58a46;
}


.coupon-input:disabled {
  background-color:
    #f5f5f5;
}


.apply-coupon-button,
.remove-coupon-button {
  padding:
    11px 18px;

  border-radius:
    6px;

  font-size:
    14px;

  font-weight:
    bold;

  cursor:
    pointer;
}


.apply-coupon-button {
  border:
    none;

  background-color:
    #b58a46;

  color:
    white;
}


.apply-coupon-button:hover:not(:disabled) {
  background-color:
    #8f692f;
}


.apply-coupon-button:disabled {
  background-color:
    #cccccc;

  cursor:
    not-allowed;
}


.remove-coupon-button {
  border:
    1px solid #b3443c;

  background-color:
    white;

  color:
    #b3443c;
}


.remove-coupon-button:hover {
  background-color:
    #fde9e7;
}


.coupon-message {
  margin-top:
    10px;

  color:
    #b3443c;

  font-size:
    13px;
}


.coupon-message.success {
  color:
    #257641;
}


.applied-coupon {
  margin-top:
    12px;

  padding:
    10px 12px;

  border-radius:
    6px;

  background-color:
    #f0f8f3;

  color:
    #257641;

  font-size:
    13px;
}


.coupon-code-text {
  margin-top:
    4px;

  color:
    #666666;
}


/* =====================================================
   Summary
   ===================================================== */

.cart-summary {
  margin-top:
    24px;

  padding:
    24px;

  background-color:
    white;

  border-radius:
    8px;
}


.summary-row,
.summary-total {
  display: flex;

  justify-content:
    space-between;

  align-items:
    center;

  gap: 30px;
}


.summary-row {
  margin-bottom:
    10px;

  color:
    #666666;

  font-size:
    14px;
}


.discount-row {
  color:
    #257641;

  font-weight:
    bold;
}


.coupon-summary-row {
  color:
    #95691f;
}


.summary-divider {
  margin:
    15px 0;

  border-top:
    1px solid #eeeeee;
}


.summary-total {
  font-size:
    18px;
}


.summary-total strong {
  color:
    #b3443c;

  font-size:
    24px;
}


/* =====================================================
   結帳
   ===================================================== */

.checkout-button {
  display:
    block;

  margin:
    20px 0 0 auto;

  padding:
    12px 28px;

  border:
    none;

  border-radius:
    6px;

  background-color:
    #b58a46;

  color:
    white;

  font-size:
    16px;

  font-weight:
    bold;

  cursor:
    pointer;
}


.checkout-button:hover:not(:disabled) {
  background-color:
    #8f692f;
}


.checkout-button:disabled {
  background-color:
    #cccccc;

  cursor:
    not-allowed;
}


/* =====================================================
   Empty
   ===================================================== */

.empty-cart {
  padding:
    60px;

  background-color:
    white;

  border-radius:
    8px;

  text-align:
    center;

  color:
    #888888;
}


/* =====================================================
   RWD
   ===================================================== */

@media (
  max-width: 760px
) {

  .cart-item {
    grid-template-columns:
      1fr;

    gap:
      14px;
  }


  .coupon-input-row {
    align-items:
      stretch;

    flex-direction:
      column;
  }


  .coupon-input {
    width:
      100%;

    min-width:
      0;
  }


  .apply-coupon-button,
  .remove-coupon-button {
    width:
      100%;
  }


  .cart-summary {
    text-align:
      left;
  }


  .checkout-button {
    width:
      100%;

    margin-left:
      0;
  }
}
</style>