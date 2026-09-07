<template>
  <div>

    <!-- =====================================================
         Dashboard 標題
         ===================================================== -->
    <div class="dashboard-title">
      <div>
        <h1>Dashboard</h1>

        <p>
          歡迎使用星澄飯店後台管理系統
        </p>
      </div>
    </div>


    <!-- =====================================================
         基本統計卡片
         ===================================================== -->
    <div class="stat-grid">

      <div class="stat-card">
        <div class="stat-icon">
          🛍
        </div>

        <div>
          <span>
            商品數量
          </span>

          <strong>
            {{ productCount }}
          </strong>
        </div>
      </div>


      <div class="stat-card">
        <div class="stat-icon">
          🍽
        </div>

        <div>
          <span>
            餐廳數量
          </span>

          <strong>
            {{ restaurantCount }}
          </strong>
        </div>
      </div>


      <div class="stat-card">
        <div class="stat-icon">
          📅
        </div>

        <div>
          <span>
            餐廳訂位
          </span>

          <strong>
            {{ reservationCount }}
          </strong>
        </div>
      </div>


      <div class="stat-card">
        <div class="stat-icon">
          👤
        </div>

        <div>
          <span>
            會員人數
          </span>

          <strong>
            {{ memberCount }}
          </strong>
        </div>
      </div>

    </div>


    <!-- =====================================================
         本月訂單營運摘要
         ===================================================== -->
    <div class="order-stat-grid">

      <div class="order-stat-card">
        <span>
          {{ currentYear }} 年
          {{ currentMonth }} 月完成訂單
        </span>

        <strong>
          {{ currentMonthOrderCount }}

          <small>
            筆
          </small>
        </strong>
      </div>


      <div class="order-stat-card">
        <span>
          {{ currentYear }} 年
          {{ currentMonth }} 月訂單營業額
        </span>

        <strong>
          NT$
          {{
            formatPrice(
              currentMonthRevenue
            )
          }}
        </strong>
      </div>


      <div class="order-stat-card">
        <span>
          {{ currentYear }} 年
          {{ currentMonth }} 月平均客單價
        </span>

        <strong>
          NT$
          {{
            formatPrice(
              currentMonthAverage
            )
          }}
        </strong>
      </div>

    </div>


    <!-- =====================================================
         最近 12 個月訂單統計
         ===================================================== -->
    <section
      class="
        dashboard-card
        order-chart-card
      "
    >

      <div
        class="
          card-title
          chart-title
        "
      >
        <div>
          <h2>
            完成訂單營運統計
          </h2>

          <p>
            顯示最近 12 個月，
            僅統計 COMPLETED 已完成訂單
          </p>
        </div>
      </div>


      <!-- Loading -->
      <div
        v-if="orderStatsLoading"
        class="chart-message"
      >
        訂單統計讀取中...
      </div>


      <!-- Error -->
      <div
        v-else-if="orderStatsError"
        class="chart-error"
      >
        {{ orderStatsError }}
      </div>


      <!-- 圖表 -->
      <div v-else>

        <!-- =================================================
             每月完成訂單數
             ================================================= -->
        <div class="chart-section">

          <div class="chart-section-header">

            <div class="chart-section-title">
              最近 12 個月完成訂單數量
            </div>

            <div class="chart-unit">
              單位：筆
            </div>

          </div>


          <div class="bar-chart">

            <div
              v-for="item in last12MonthsStats"
              :key="
                `${item.year}-${item.month}-count`
              "
              class="bar-column"
            >

              <div class="bar-value">
                {{ item.orderCount }}
              </div>


              <div class="bar-area">

                <div
                  class="
                    bar
                    count-bar
                  "
                  :class="{
                    'zero-bar':
                      item.orderCount === 0
                  }"
                  :style="{
                    height:
                      getCountBarHeight(
                        item.orderCount
                      )
                  }"
                  :title="
                    `${item.year} 年 ${item.month} 月：${item.orderCount} 筆`
                  "
                ></div>

              </div>


              <div class="bar-label">

                <span>
                  {{ item.year }}
                </span>

                <strong>
                  {{
                    String(
                      item.month
                    ).padStart(
                      2,
                      '0'
                    )
                  }}
                  月
                </strong>

              </div>

            </div>

          </div>

        </div>


        <!-- =================================================
             每月完成訂單營業額
             ================================================= -->
        <div
          class="
            chart-section
            revenue-section
          "
        >

          <div class="chart-section-header">

            <div class="chart-section-title">
              最近 12 個月完成訂單營業額
            </div>

            <div class="chart-unit">
              單位：NT$
            </div>

          </div>


          <div class="bar-chart">

            <div
              v-for="item in last12MonthsStats"
              :key="
                `${item.year}-${item.month}-revenue`
              "
              class="bar-column"
            >

              <div
                class="
                  bar-value
                  revenue-value
                "
              >
                NT$
                {{
                  formatCompactPrice(
                    item.totalRevenue
                  )
                }}
              </div>


              <div class="bar-area">

                <div
                  class="
                    bar
                    revenue-bar
                  "
                  :class="{
                    'zero-bar':
                      item.totalRevenue === 0
                  }"
                  :style="{
                    height:
                      getRevenueBarHeight(
                        item.totalRevenue
                      )
                  }"
                  :title="
                    `${item.year} 年 ${item.month} 月：NT$${formatPrice(item.totalRevenue)}`
                  "
                ></div>

              </div>


              <div class="bar-label">

                <span>
                  {{ item.year }}
                </span>

                <strong>
                  {{
                    String(
                      item.month
                    ).padStart(
                      2,
                      '0'
                    )
                  }}
                  月
                </strong>

              </div>

            </div>

          </div>

        </div>

      </div>

    </section>


    <!-- =====================================================
         指定月份商品銷售統計
         ===================================================== -->
    <section
      class="
        dashboard-card
        product-sales-card
      "
    >

      <!-- 標題 -->
      <div class="product-sales-header">

        <div>
          <h2>
            商品月銷售統計
          </h2>

          <p>
            查詢指定月份已完成訂單中的商品銷售數量與銷售額
          </p>
        </div>

      </div>


      <!-- ===================================================
           查詢條件
           =================================================== -->
      <div class="product-sales-filter">

        <!-- 年份 -->
        <div class="filter-group">

          <label>
            年份
          </label>

          <select
            v-model.number="
              selectedSalesYear
            "
          >

            <option
              v-for="
                year in salesYearOptions
              "
              :key="year"
              :value="year"
            >
              {{ year }} 年
            </option>

          </select>

        </div>


        <!-- 月份 -->
        <div class="filter-group">

          <label>
            月份
          </label>

          <select
            v-model.number="
              selectedSalesMonth
            "
          >

            <option
              v-for="month in 12"
              :key="month"
              :value="month"
            >
              {{ month }} 月
            </option>

          </select>

        </div>


        <!-- 查詢按鈕 -->
        <button
          class="
            sales-search-button
          "
          :disabled="
            productSalesLoading
          "
          @click="
            loadMonthlyProductSales
          "
        >

          {{
            productSalesLoading
              ? "查詢中..."
              : "查詢"
          }}

        </button>

      </div>


      <!-- ===================================================
           查詢年月
           =================================================== -->
      <div class="selected-month-title">

        <strong>
          {{ selectedSalesYear }} 年
          {{ selectedSalesMonth }} 月
        </strong>

        商品銷售統計

      </div>


      <!-- Loading -->
      <div
        v-if="
          productSalesLoading
        "
        class="
          chart-message
        "
      >
        商品銷售資料讀取中...
      </div>


      <!-- Error -->
      <div
        v-else-if="
          productSalesError
        "
        class="
          chart-error
        "
      >
        {{ productSalesError }}
      </div>


      <!-- 無資料 -->
      <div
        v-else-if="
          monthlyProductSales.length
          ===
          0
        "
        class="
          empty-sales
        "
      >

        <div class="empty-sales-icon">
          📦
        </div>

        <strong>
          此月份沒有已完成的商品訂單
        </strong>

        <p>
          請選擇其他月份查詢
        </p>

      </div>


      <!-- ===================================================
           商品資料
           =================================================== -->
      <div v-else>

        <!-- ===============================================
             商品摘要
             =============================================== -->
        <div class="product-sales-summary">

          <div class="sales-summary-card">

            <span>
              銷售商品種類
            </span>

            <strong>
              {{
                monthlyProductSales.length
              }}

              <small>
                種
              </small>
            </strong>

          </div>


          <div class="sales-summary-card">

            <span>
              商品總銷售數量
            </span>

            <strong>
              {{
                selectedMonthTotalQuantity
              }}

              <small>
                件
              </small>
            </strong>

          </div>


          <div class="sales-summary-card">

            <span>
              商品總銷售額
            </span>

            <strong>
              NT$
              {{
                formatPrice(
                  selectedMonthTotalSales
                )
              }}
            </strong>

          </div>

        </div>


        <!-- ===============================================
             商品銷售排行
             =============================================== -->
        <div class="product-ranking">

          <div class="ranking-title">

            <h3>
              商品銷售排行
            </h3>

            <span>
              依銷售數量由高到低排序
            </span>

          </div>


          <div class="ranking-list">

            <div
              v-for="
                (item, index)
                in
                monthlyProductSales
              "
              :key="
                `ranking-${item.productId}`
              "
              class="
                ranking-item
              "
            >

              <!-- 排名 -->
              <div
                class="
                  ranking-number
                "
                :class="{
                  top1:
                    index === 0,

                  top2:
                    index === 1,

                  top3:
                    index === 2
                }"
              >
                {{ index + 1 }}
              </div>


              <!-- 商品 -->
              <div class="ranking-product">

                <div
                  class="
                    ranking-product-header
                  "
                >

                  <strong>
                    {{
                      item.productName
                    }}
                  </strong>

                  <span>
                    {{
                      item.quantitySold
                    }}
                    件
                  </span>

                </div>


                <div
                  class="
                    ranking-bar-background
                  "
                >

                  <div
                    class="
                      ranking-bar-fill
                    "
                    :style="{
                      width:
                        getProductSalesBarWidth(
                          item.quantitySold
                        )
                    }"
                  ></div>

                </div>

              </div>


              <!-- 銷售額 -->
              <div class="ranking-sales">

                <small>
                  銷售額
                </small>

                <strong>
                  NT$
                  {{
                    formatPrice(
                      item.salesAmount
                    )
                  }}
                </strong>

              </div>

            </div>

          </div>

        </div>


        <!-- ===============================================
             商品明細表
             =============================================== -->
        <div
          class="
            product-sales-table-wrapper
          "
        >

          <table
            class="
              product-sales-table
            "
          >

            <thead>

              <tr>

                <th>
                  排名
                </th>

                <th>
                  商品名稱
                </th>

                <th>
                  銷售數量
                </th>

                <th>
                  銷售額
                </th>

              </tr>

            </thead>


            <tbody>

              <tr
                v-for="
                  (item, index)
                  in
                  monthlyProductSales
                "
                :key="
                  `table-${item.productId}`
                "
              >

                <td>
                  {{ index + 1 }}
                </td>


                <td
                  class="
                    product-name-cell
                  "
                >
                  {{
                    item.productName
                  }}
                </td>


                <td>
                  {{
                    item.quantitySold
                  }}
                  件
                </td>


                <td
                  class="
                    sales-amount-cell
                  "
                >
                  NT$
                  {{
                    formatPrice(
                      item.salesAmount
                    )
                  }}
                </td>

              </tr>

            </tbody>


            <tfoot>

              <tr>

                <td colspan="2">
                  合計
                </td>

                <td>
                  {{
                    selectedMonthTotalQuantity
                  }}
                  件
                </td>

                <td>
                  NT$
                  {{
                    formatPrice(
                      selectedMonthTotalSales
                    )
                  }}
                </td>

              </tr>

            </tfoot>

          </table>

        </div>

      </div>

    </section>


    <!-- =====================================================
         下方 Dashboard
         ===================================================== -->
    <div class="dashboard-grid">

      <!-- 快速管理 -->
      <section
        class="
          dashboard-card
        "
      >

        <div class="card-title">
          <h2>
            快速管理
          </h2>
        </div>


        <div class="quick-grid">

          <RouterLink
            to="/admin/products"
            class="quick-item"
          >
            🛍

            <span>
              商品管理
            </span>
          </RouterLink>


          <RouterLink
            to="/admin/orders"
            class="quick-item"
          >
            📦

            <span>
              訂單管理
            </span>
          </RouterLink>


          <RouterLink
            to="/admin/coupons"
            class="quick-item"
          >
            🎟

            <span>
              優惠券管理
            </span>
          </RouterLink>


          <RouterLink
            to="/admin/restaurants"
            class="quick-item"
          >
            🍽

            <span>
              餐廳管理
            </span>
          </RouterLink>


          <RouterLink
            to="/admin/restaurant-times"
            class="quick-item"
          >
            🕒

            <span>
              時段管理
            </span>
          </RouterLink>


          <RouterLink
            to="/admin/reservations"
            class="quick-item"
          >
            📅

            <span>
              訂位管理
            </span>
          </RouterLink>

        </div>

      </section>


      <!-- 系統資訊 -->
      <section
        class="
          dashboard-card
        "
      >

        <div class="card-title">

          <h2>
            系統資訊
          </h2>

        </div>


        <div class="system-info">

          <p>
            系統名稱

            <span>
              星澄飯店管理系統
            </span>
          </p>


          <p>
            後端服務

            <span>
              Spring Boot
            </span>
          </p>


          <p>
            前端框架

            <span>
              Vue 3
            </span>
          </p>


          <p>
            資料庫

            <span>
              SQL Server
            </span>
          </p>


          <p>
            訂單統計

            <span>
              最近 12 個月
            </span>
          </p>


          <p>
            商品分析

            <span>
              月銷售統計
            </span>
          </p>

        </div>

      </section>

    </div>

  </div>
</template>


<script setup>

import {
  computed,
  onMounted,
  ref,
} from "vue";


// =====================================================
// 基本 Dashboard 統計
// =====================================================

const productCount =
  ref("—");


const restaurantCount =
  ref("—");


const reservationCount =
  ref("—");


const memberCount =
  ref("—");


// =====================================================
// 現在日期
// =====================================================

const now =
  new Date();


const currentYear =
  now.getFullYear();


const currentMonth =
  now.getMonth() + 1;


// =====================================================
// 每月訂單統計
// =====================================================

const monthlyOrderStats =
  ref([]);


const orderStatsLoading =
  ref(false);


const orderStatsError =
  ref("");


// =====================================================
// 商品月銷售統計
// =====================================================

const monthlyProductSales =
  ref([]);


const productSalesLoading =
  ref(false);


const productSalesError =
  ref("");


// 預設查詢現在年份
const selectedSalesYear =
  ref(
    currentYear
  );


// 預設查詢現在月份
const selectedSalesMonth =
  ref(
    currentMonth
  );


// =====================================================
// 年份選項
//
// 現在年份往前 5 年
// =====================================================

const salesYearOptions =
  computed(() => {

    const years =
      [];


    for (
      let i = 0;
      i < 6;
      i++
    ) {

      years.push(
        currentYear - i
      );
    }


    return years;
  });


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
// 取得單一統計數字
// =====================================================

async function fetchCount(
  url
) {

  try {

    const response =
      await fetch(
        url,
        {
          method:
            "GET",

          headers:
            getAuthHeaders(),
        }
      );


    if (!response.ok) {

      return "—";
    }


    const data =
      await response.json();


    if (
      Array.isArray(
        data
      )
    ) {

      return data.length;
    }


    return (
      data.total ??
      data.count ??
      "—"
    );


  } catch (error) {

    console.error(
      `統計 API 讀取失敗：${url}`,
      error
    );


    return "—";
  }
}


// =====================================================
// 讀取每月訂單統計
//
// GET
// /api/orders/statistics/monthly
// =====================================================

async function loadMonthlyOrderStatistics() {

  orderStatsLoading.value =
    true;


  orderStatsError.value =
    "";


  try {

    const response =
      await fetch(
        "/api/orders/statistics/monthly",
        {
          method:
            "GET",

          headers:
            getAuthHeaders(),
        }
      );


    if (
      response.status === 401
      ||
      response.status === 403
    ) {

      throw new Error(
        "沒有權限讀取訂單統計"
      );
    }


    if (!response.ok) {

      const errorText =
        await response.text();


      console.error(
        "每月訂單統計 API 錯誤：",
        errorText
      );


      throw new Error(
        `訂單統計讀取失敗 (${response.status})`
      );
    }


    const data =
      await response.json();


    console.log(
      "每月訂單統計：",
      data
    );


    if (
      !Array.isArray(
        data
      )
    ) {

      monthlyOrderStats.value =
        [];

      return;
    }


    monthlyOrderStats.value =
      data

        .map(
          (item) => ({

            year:
              Number(
                item.year
              ),

            month:
              Number(
                item.month
              ),

            orderCount:
              Number(
                item.orderCount
                ??
                0
              ),

            totalRevenue:
              Number(
                item.totalRevenue
                ??
                0
              ),
          })
        )

        .filter(
          (item) =>

            Number.isFinite(
              item.year
            )

            &&

            Number.isFinite(
              item.month
            )
        )

        .sort(
          (a, b) => {

            const aValue =
              a.year * 100
              +
              a.month;


            const bValue =
              b.year * 100
              +
              b.month;


            return (
              aValue
              -
              bValue
            );
          }
        );


  } catch (error) {

    console.error(
      "讀取每月訂單統計失敗：",
      error
    );


    orderStatsError.value =
      error.message
      ||
      "訂單統計讀取失敗";


    monthlyOrderStats.value =
      [];


  } finally {

    orderStatsLoading.value =
      false;
  }
}


// =====================================================
// 取得真正本月統計
// =====================================================

const currentMonthStats =
  computed(() => {

    const found =
      monthlyOrderStats.value
        .find(
          (item) =>

            Number(
              item.year
            )
              ===
            currentYear

            &&

            Number(
              item.month
            )
              ===
            currentMonth
        );


    if (found) {

      return found;
    }


    return {

      year:
        currentYear,

      month:
        currentMonth,

      orderCount:
        0,

      totalRevenue:
        0,
    };
  });


// =====================================================
// 本月完成訂單數
// =====================================================

const currentMonthOrderCount =
  computed(() => {

    return Number(
      currentMonthStats.value
        .orderCount
      ??
      0
    );
  });


// =====================================================
// 本月營業額
// =====================================================

const currentMonthRevenue =
  computed(() => {

    return Number(
      currentMonthStats.value
        .totalRevenue
      ??
      0
    );
  });


// =====================================================
// 本月平均客單價
// =====================================================

const currentMonthAverage =
  computed(() => {

    const count =
      currentMonthOrderCount.value;


    const revenue =
      currentMonthRevenue.value;


    if (
      count <= 0
    ) {

      return 0;
    }


    return Math.round(
      revenue
      /
      count
    );
  });


// =====================================================
// 最近 12 個月完整資料
//
// 沒有訂單的月份補 0
// =====================================================

const last12MonthsStats =
  computed(() => {

    const result =
      [];


    for (
      let offset = 11;
      offset >= 0;
      offset--
    ) {

      const date =
        new Date(
          currentYear,
          currentMonth
            -
            1
            -
            offset,
          1
        );


      const year =
        date.getFullYear();


      const month =
        date.getMonth()
        +
        1;


      const found =
        monthlyOrderStats.value
          .find(
            (item) =>

              Number(
                item.year
              )
                ===
              year

              &&

              Number(
                item.month
              )
                ===
              month
          );


      result.push({

        year,

        month,

        orderCount:
          found
            ?
            Number(
              found.orderCount
              ??
              0
            )
            :
            0,

        totalRevenue:
          found
            ?
            Number(
              found.totalRevenue
              ??
              0
            )
            :
            0,
      });
    }


    return result;
  });


// =====================================================
// 最近 12 個月最大訂單數
// =====================================================

const maxOrderCount =
  computed(() => {

    return Math.max(
      1,

      ...last12MonthsStats.value
        .map(
          (item) =>
            Number(
              item.orderCount
              ??
              0
            )
        )
    );
  });


// =====================================================
// 最近 12 個月最大營業額
// =====================================================

const maxRevenue =
  computed(() => {

    return Math.max(
      1,

      ...last12MonthsStats.value
        .map(
          (item) =>
            Number(
              item.totalRevenue
              ??
              0
            )
        )
    );
  });


// =====================================================
// 訂單圖高度
// =====================================================

function getCountBarHeight(
  count
) {

  const value =
    Number(
      count
      ??
      0
    );


  if (
    value <= 0
  ) {

    return "3px";
  }


  const percentage =
    (
      value
      /
      maxOrderCount.value
    )
    *
    100;


  return (
    Math.max(
      8,
      percentage
    )
    +
    "%"
  );
}


// =====================================================
// 營業額圖高度
// =====================================================

function getRevenueBarHeight(
  revenue
) {

  const value =
    Number(
      revenue
      ??
      0
    );


  if (
    value <= 0
  ) {

    return "3px";
  }


  const percentage =
    (
      value
      /
      maxRevenue.value
    )
    *
    100;


  return (
    Math.max(
      8,
      percentage
    )
    +
    "%"
  );
}


// =====================================================
// 查詢指定月份商品銷售
//
// GET
// /api/orders/statistics/products
// ?year=2026
// &month=9
// =====================================================

async function loadMonthlyProductSales() {

  productSalesLoading.value =
    true;


  productSalesError.value =
    "";


  monthlyProductSales.value =
    [];


  try {

    const url =
      "/api/orders/statistics/products"
      +
      `?year=${selectedSalesYear.value}`
      +
      `&month=${selectedSalesMonth.value}`;


    const response =
      await fetch(
        url,
        {
          method:
            "GET",

          headers:
            getAuthHeaders(),
        }
      );


    if (
      response.status === 401
      ||
      response.status === 403
    ) {

      throw new Error(
        "沒有權限讀取商品銷售統計"
      );
    }


    if (!response.ok) {

      const errorText =
        await response.text();


      console.error(
        "商品銷售統計 API 錯誤：",
        errorText
      );


      throw new Error(
        `商品銷售統計讀取失敗 (${response.status})`
      );
    }


    const data =
      await response.json();


    console.log(
      "商品月銷售統計：",
      data
    );


    if (
      !Array.isArray(
        data
      )
    ) {

      monthlyProductSales.value =
        [];

      return;
    }


    monthlyProductSales.value =
      data

        .map(
          (item) => ({

            productId:
              Number(
                item.productId
              ),

            productName:
              item.productName
              ??
              "未知商品",

            quantitySold:
              Number(
                item.quantitySold
                ??
                0
              ),

            salesAmount:
              Number(
                item.salesAmount
                ??
                0
              ),
          })
        )

        .sort(
          (a, b) => {

            if (
              b.quantitySold
              !==
              a.quantitySold
            ) {

              return (
                b.quantitySold
                -
                a.quantitySold
              );
            }


            return (
              b.salesAmount
              -
              a.salesAmount
            );
          }
        );


  } catch (error) {

    console.error(
      "讀取商品銷售統計失敗：",
      error
    );


    productSalesError.value =
      error.message
      ||
      "商品銷售統計讀取失敗";


    monthlyProductSales.value =
      [];


  } finally {

    productSalesLoading.value =
      false;
  }
}


// =====================================================
// 指定月份商品總銷售件數
// =====================================================

const selectedMonthTotalQuantity =
  computed(() => {

    return monthlyProductSales.value
      .reduce(
        (
          total,
          item
        ) => {

          return (
            total
            +
            Number(
              item.quantitySold
              ??
              0
            )
          );
        },
        0
      );
  });


// =====================================================
// 指定月份商品總銷售額
// =====================================================

const selectedMonthTotalSales =
  computed(() => {

    return monthlyProductSales.value
      .reduce(
        (
          total,
          item
        ) => {

          return (
            total
            +
            Number(
              item.salesAmount
              ??
              0
            )
          );
        },
        0
      );
  });


// =====================================================
// 熱銷商品最大數量
// =====================================================

const maxProductQuantity =
  computed(() => {

    if (
      monthlyProductSales.value.length
      ===
      0
    ) {

      return 1;
    }


    return Math.max(
      1,

      ...monthlyProductSales.value
        .map(
          (item) =>
            Number(
              item.quantitySold
              ??
              0
            )
        )
    );
  });


// =====================================================
// 熱銷商品長條寬度
// =====================================================

function getProductSalesBarWidth(
  quantity
) {

  const value =
    Number(
      quantity
      ??
      0
    );


  if (
    value <= 0
  ) {

    return "0%";
  }


  const percentage =
    (
      value
      /
      maxProductQuantity.value
    )
    *
    100;


  return (
    percentage
    +
    "%"
  );
}


// =====================================================
// 金額格式
//
// 47500
// →
// 47,500
// =====================================================

function formatPrice(
  price
) {

  return Number(
    price
    ??
    0
  ).toLocaleString(
    "zh-TW"
  );
}


// =====================================================
// 圖表簡化金額
//
// 47500
// →
// 47.5K
// =====================================================

function formatCompactPrice(
  price
) {

  const value =
    Number(
      price
      ??
      0
    );


  if (
    value >= 1000000
  ) {

    return (
      (
        value
        /
        1000000
      )
        .toFixed(1)
        .replace(
          ".0",
          ""
        )
      +
      "M"
    );
  }


  if (
    value >= 1000
  ) {

    return (
      (
        value
        /
        1000
      )
        .toFixed(1)
        .replace(
          ".0",
          ""
        )
      +
      "K"
    );
  }


  return value
    .toLocaleString(
      "zh-TW"
    );
}


// =====================================================
// 初始化
// =====================================================

onMounted(
  async () => {

    const results =
      await Promise.all([

        fetchCount(
          "/api/products"
        ),

        fetchCount(
          "/api/restaurant"
        ),

        fetchCount(
          "/api/reservations"
        ),

        fetchCount(
          "/api/members"
        ),

        loadMonthlyOrderStatistics(),

        loadMonthlyProductSales(),
      ]);


    productCount.value =
      results[0];


    restaurantCount.value =
      results[1];


    reservationCount.value =
      results[2];


    memberCount.value =
      results[3];

  }
);

</script>


<style scoped>

/* =====================================================
   Dashboard 標題
   ===================================================== */

.dashboard-title {
  margin-bottom: 28px;
}


.dashboard-title h1 {
  margin-bottom: 6px;

  color: #4a3b2a;

  font-size: 30px;
}


.dashboard-title p {
  color: #777777;
}


/* =====================================================
   基本統計
   ===================================================== */

.stat-grid {
  display: grid;

  grid-template-columns:
    repeat(4, 1fr);

  gap: 20px;

  margin-bottom: 28px;
}


.stat-card {
  display: flex;

  align-items: center;

  gap: 18px;

  padding: 24px;

  background: white;

  border-radius: 14px;

  box-shadow:
    0 6px 20px
    rgba(0, 0, 0, 0.06);
}


.stat-icon {
  display: flex;

  justify-content: center;

  align-items: center;

  width: 58px;

  height: 58px;

  background: #f3eadc;

  border-radius: 12px;

  font-size: 28px;
}


.stat-card span {
  color: #777777;

  font-size: 14px;
}


.stat-card strong {
  display: block;

  margin-top: 5px;

  color: #6f5328;

  font-size: 28px;
}


/* =====================================================
   本月訂單摘要
   ===================================================== */

.order-stat-grid {
  display: grid;

  grid-template-columns:
    repeat(3, 1fr);

  gap: 20px;

  margin-bottom: 28px;
}


.order-stat-card {
  padding: 22px;

  background: white;

  border-radius: 14px;

  box-shadow:
    0 6px 20px
    rgba(0, 0, 0, 0.06);
}


.order-stat-card span {
  color: #777777;

  font-size: 14px;
}


.order-stat-card strong {
  display: block;

  margin-top: 8px;

  color: #6f5328;

  font-size: 26px;
}


.order-stat-card small {
  color: #888888;

  font-size: 13px;
}


/* =====================================================
   Dashboard Card
   ===================================================== */

.dashboard-card {
  padding: 26px;

  background: white;

  border-radius: 14px;

  box-shadow:
    0 6px 20px
    rgba(0, 0, 0, 0.06);
}


.card-title h2 {
  margin-bottom: 20px;

  color: #6f5328;

  font-size: 21px;
}


/* =====================================================
   訂單圖表
   ===================================================== */

.order-chart-card {
  margin-bottom: 28px;
}


.chart-title {
  display: flex;

  justify-content: space-between;

  align-items: flex-start;
}


.chart-title h2 {
  margin-bottom: 5px;
}


.chart-title p {
  margin: 0;

  color: #999999;

  font-size: 13px;
}


.chart-section {
  margin-top: 25px;
}


.revenue-section {
  margin-top: 40px;

  padding-top: 30px;

  border-top:
    1px solid
    #eee7dd;
}


.chart-section-header {
  display: flex;

  justify-content: space-between;

  align-items: center;

  gap: 20px;

  margin-bottom: 20px;
}


.chart-section-title {
  color: #4a3b2a;

  font-size: 15px;

  font-weight: bold;
}


.chart-unit {
  color: #999999;

  font-size: 12px;
}


/* =====================================================
   長條圖
   ===================================================== */

.bar-chart {
  display: flex;

  align-items: flex-end;

  gap: 12px;

  min-height: 250px;

  padding:
    15px 10px 0;

  overflow-x: auto;

  border-bottom:
    1px solid
    #dddddd;
}


.bar-column {
  display: flex;

  flex: 1;

  flex-direction: column;

  justify-content: flex-end;

  align-items: center;

  min-width: 58px;

  max-width: 95px;
}


.bar-value {
  min-height: 25px;

  margin-bottom: 7px;

  color: #6f5328;

  font-size: 12px;

  font-weight: bold;

  white-space: nowrap;
}


.revenue-value {
  font-size: 11px;
}


.bar-area {
  display: flex;

  align-items: flex-end;

  width: 38px;

  height: 180px;
}


.bar {
  width: 100%;

  min-height: 3px;

  border-radius:
    6px 6px 0 0;

  transition:
    height 0.35s ease;
}


.count-bar {
  background:
    linear-gradient(
      180deg,
      #c69a55,
      #95691f
    );
}


.revenue-bar {
  background:
    linear-gradient(
      180deg,
      #6389b5,
      #315f94
    );
}


.zero-bar {
  opacity: 0.22;
}


.bar-label {
  display: flex;

  flex-direction: column;

  align-items: center;

  gap: 2px;

  margin-top: 10px;

  padding-bottom: 10px;

  color: #888888;

  font-size: 10px;

  white-space: nowrap;
}


.bar-label strong {
  color: #555555;

  font-size: 11px;
}


/* =====================================================
   共用訊息
   ===================================================== */

.chart-message,
.chart-error {
  padding: 50px;

  text-align: center;
}


.chart-message {
  color: #888888;
}


.chart-error {
  color: #b3443c;

  background: #fde9e7;

  border-radius: 8px;
}


/* =====================================================
   商品月銷售統計
   ===================================================== */

.product-sales-card {
  margin-bottom: 28px;
}


.product-sales-header {
  display: flex;

  justify-content: space-between;

  align-items: flex-start;

  margin-bottom: 24px;
}


.product-sales-header h2 {
  margin:
    0 0 5px;

  color: #6f5328;

  font-size: 21px;
}


.product-sales-header p {
  margin: 0;

  color: #999999;

  font-size: 13px;
}


/* =====================================================
   商品查詢條件
   ===================================================== */

.product-sales-filter {
  display: flex;

  align-items: flex-end;

  gap: 15px;

  padding: 18px;

  margin-bottom: 22px;

  background: #faf7f2;

  border:
    1px solid
    #eee4d7;

  border-radius: 10px;
}


.filter-group {
  display: flex;

  flex-direction: column;

  gap: 6px;
}


.filter-group label {
  color: #6f5328;

  font-size: 13px;

  font-weight: bold;
}


.filter-group select {
  min-width: 130px;

  height: 38px;

  padding:
    0 12px;

  color: #4a3b2a;

  background: white;

  border:
    1px solid
    #d8c9b8;

  border-radius: 6px;

  font-size: 14px;

  outline: none;
}


.filter-group select:focus {
  border-color: #b58a46;
}


.sales-search-button {
  height: 38px;

  padding:
    0 22px;

  color: white;

  background: #8a6732;

  border: none;

  border-radius: 6px;

  font-size: 14px;

  font-weight: bold;

  cursor: pointer;

  transition: 0.2s;
}


.sales-search-button:hover:not(:disabled) {
  background: #6f5328;

  transform:
    translateY(-1px);
}


.sales-search-button:disabled {
  opacity: 0.55;

  cursor: not-allowed;
}


.selected-month-title {
  margin-bottom: 20px;

  color: #777777;

  font-size: 14px;
}


.selected-month-title strong {
  color: #6f5328;

  font-size: 17px;
}


/* =====================================================
   商品摘要
   ===================================================== */

.product-sales-summary {
  display: grid;

  grid-template-columns:
    repeat(3, 1fr);

  gap: 15px;

  margin-bottom: 28px;
}


.sales-summary-card {
  padding: 17px;

  background: #faf7f2;

  border:
    1px solid
    #eee4d7;

  border-radius: 10px;
}


.sales-summary-card span {
  color: #888888;

  font-size: 13px;
}


.sales-summary-card strong {
  display: block;

  margin-top: 7px;

  color: #6f5328;

  font-size: 21px;
}


.sales-summary-card small {
  color: #888888;

  font-size: 12px;
}


/* =====================================================
   商品無資料
   ===================================================== */

.empty-sales {
  padding:
    50px 20px;

  color: #888888;

  text-align: center;
}


.empty-sales-icon {
  margin-bottom: 12px;

  font-size: 38px;
}


.empty-sales strong {
  display: block;

  margin-bottom: 5px;

  color: #666666;
}


.empty-sales p {
  margin: 0;

  font-size: 13px;
}


/* =====================================================
   商品排行
   ===================================================== */

.product-ranking {
  padding-top: 5px;

  margin-bottom: 30px;
}


.ranking-title {
  display: flex;

  justify-content: space-between;

  align-items: center;

  margin-bottom: 18px;
}


.ranking-title h3 {
  margin: 0;

  color: #4a3b2a;

  font-size: 16px;
}


.ranking-title span {
  color: #999999;

  font-size: 12px;
}


.ranking-list {
  display: flex;

  flex-direction: column;

  gap: 12px;
}


.ranking-item {
  display: grid;

  grid-template-columns:
    38px 1fr 130px;

  align-items: center;

  gap: 15px;

  padding:
    13px 15px;

  background: #fcfaf7;

  border:
    1px solid
    #eee7dd;

  border-radius: 9px;
}


.ranking-number {
  display: flex;

  justify-content: center;

  align-items: center;

  width: 30px;

  height: 30px;

  color: #777777;

  background: #eee7dd;

  border-radius: 50%;

  font-size: 13px;

  font-weight: bold;
}


.ranking-number.top1 {
  color: white;

  background: #d6b55d;
}


.ranking-number.top2 {
  color: white;

  background: #a9a9a9;
}


.ranking-number.top3 {
  color: white;

  background: #b8794f;
}


.ranking-product {
  min-width: 0;
}


.ranking-product-header {
  display: flex;

  justify-content: space-between;

  gap: 15px;

  margin-bottom: 7px;
}


.ranking-product-header strong {
  overflow: hidden;

  color: #4a3b2a;

  font-size: 13px;

  text-overflow: ellipsis;

  white-space: nowrap;
}


.ranking-product-header span {
  color: #777777;

  font-size: 12px;

  white-space: nowrap;
}


.ranking-bar-background {
  width: 100%;

  height: 8px;

  overflow: hidden;

  background: #eee7dd;

  border-radius: 20px;
}


.ranking-bar-fill {
  height: 100%;

  background:
    linear-gradient(
      90deg,
      #c69a55,
      #95691f
    );

  border-radius: 20px;

  transition:
    width 0.35s ease;
}


.ranking-sales {
  text-align: right;
}


.ranking-sales small {
  display: block;

  margin-bottom: 3px;

  color: #999999;

  font-size: 10px;
}


.ranking-sales strong {
  color: #6f5328;

  font-size: 13px;
}


/* =====================================================
   商品統計表格
   ===================================================== */

.product-sales-table-wrapper {
  overflow-x: auto;

  border:
    1px solid
    #eee7dd;

  border-radius: 9px;
}


.product-sales-table {
  width: 100%;

  border-collapse: collapse;
}


.product-sales-table th {
  padding:
    13px 15px;

  color: #6f5328;

  background: #f3eadc;

  font-size: 13px;

  text-align: left;
}


.product-sales-table td {
  padding:
    13px 15px;

  color: #666666;

  border-top:
    1px solid
    #eee7dd;

  font-size: 13px;
}


.product-sales-table tbody tr:hover {
  background: #fcfaf7;
}


.product-name-cell {
  color:
    #4a3b2a
    !important;

  font-weight: bold;
}


.sales-amount-cell {
  color:
    #6f5328
    !important;

  font-weight: bold;
}


.product-sales-table tfoot td {
  color: #4a3b2a;

  background: #faf7f2;

  font-weight: bold;
}


/* =====================================================
   下方 Dashboard
   ===================================================== */

.dashboard-grid {
  display: grid;

  grid-template-columns:
    2fr 1fr;

  gap: 24px;
}


/* =====================================================
   快速管理
   ===================================================== */

.quick-grid {
  display: grid;

  grid-template-columns:
    repeat(2, 1fr);

  gap: 15px;
}


.quick-item {
  display: flex;

  flex-direction: column;

  align-items: center;

  gap: 10px;

  padding: 24px;

  color: #4a3b2a;

  background: #faf7f2;

  border:
    1px solid
    #eee4d7;

  border-radius: 12px;

  font-size: 25px;

  text-decoration: none;

  transition: 0.25s;
}


.quick-item span {
  font-size: 15px;

  font-weight: bold;
}


.quick-item:hover {
  border-color: #b58a46;

  transform:
    translateY(-3px);
}


/* =====================================================
   系統資訊
   ===================================================== */

.system-info p {
  display: flex;

  justify-content: space-between;

  gap: 20px;

  padding:
    13px 0;

  color: #777777;

  border-bottom:
    1px solid
    #eee7dd;
}


.system-info span {
  color: #4a3b2a;

  font-weight: bold;

  text-align: right;
}


/* =====================================================
   RWD
   ===================================================== */

@media (
  max-width: 1000px
) {

  .stat-grid {
    grid-template-columns:
      repeat(2, 1fr);
  }


  .order-stat-grid {
    grid-template-columns:
      repeat(3, 1fr);
  }


  .dashboard-grid {
    grid-template-columns:
      1fr;
  }
}


@media (
  max-width: 800px
) {

  .bar-column {
    flex:
      0 0 65px;
  }


  .bar-chart {
    justify-content:
      flex-start;
  }


  .product-sales-filter {
    align-items: stretch;

    flex-direction: column;
  }


  .filter-group select {
    width: 100%;
  }


  .sales-search-button {
    width: 100%;
  }


  .ranking-item {
    grid-template-columns:
      35px 1fr;
  }


  .ranking-sales {
    grid-column: 2;

    text-align: left;
  }
}


@media (
  max-width: 700px
) {

  .order-stat-grid {
    grid-template-columns:
      1fr;
  }


  .product-sales-summary {
    grid-template-columns:
      1fr;
  }


  .chart-section-header {
    align-items:
      flex-start;

    flex-direction:
      column;

    gap: 4px;
  }
}


@media (
  max-width: 600px
) {

  .stat-grid {
    grid-template-columns:
      1fr;
  }


  .quick-grid {
    grid-template-columns:
      1fr;
  }
}

</style>