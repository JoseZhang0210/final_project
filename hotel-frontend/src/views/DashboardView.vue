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
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"/>
            <path d="M3 6h18"/>
            <path d="M16 10a4 4 0 0 1-8 0"/>
          </svg>
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
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M18 2v6a3 3 0 0 1-3 3 3 3 0 0 1-3-3V2"/>
            <path d="M15 2v14"/>
            <path d="M15 20v2"/>
            <path d="M5 2v4a3 3 0 0 0 3 3 3 3 0 0 0 3-3V2"/>
            <path d="M8 2v14"/>
            <path d="M8 20v2"/>
          </svg>
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
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <rect width="18" height="18" x="3" y="4" rx="2" ry="2"/>
            <line x1="16" x2="16" y1="2" y2="6"/>
            <line x1="8" x2="8" y1="2" y2="6"/>
            <line x1="3" x2="21" y1="10" y2="10"/>
            <path d="m9 16 2 2 4-4"/>
          </svg>
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
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
            <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
            <circle cx="9" cy="7" r="4"/>
            <path d="M22 21v-2a4 4 0 0 0-3-3.87"/>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
          </svg>
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


    <!-- 今日待辦與異常中心 -->
    <section class="dashboard-card operations-center" aria-labelledby="operations-title">
      <header class="operations-header">
        <div>
          <div class="operations-title-row">
            <h2 id="operations-title">今日待辦與異常中心</h2>
            <span v-if="!operationsLoading" class="operations-total-badge">
              {{ operationsTotal }} 項今日重點
            </span>
          </div>
          <p>掌握今日住宿動態、待處理工作與需要留意的庫存狀況</p>
        </div>

        <div class="operations-actions">
          <span v-if="operationsUpdatedAt" class="operations-updated-at">
            更新於 {{ operationsUpdatedAt }}
          </span>
          <button
            type="button"
            class="operations-refresh-button"
            :disabled="operationsLoading"
            aria-label="重新整理今日待辦與異常資料"
            @click="loadOperationsCenter"
          >
            <svg
              :class="{ spinning: operationsLoading }"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              aria-hidden="true"
            >
              <path d="M20 11a8.1 8.1 0 0 0-15.5-2M4 4v5h5" />
              <path d="M4 13a8.1 8.1 0 0 0 15.5 2M20 20v-5h-5" />
            </svg>
            {{ operationsLoading ? "更新中" : "重新整理" }}
          </button>
        </div>
      </header>

      <div v-if="operationsLoading && !operationsLoaded" class="operations-loading" role="status">
        正在整理今日營運資料…
      </div>

      <template v-else>
        <div v-if="operationsFailedSources" class="operations-warning" role="status">
          部分資料暫時無法讀取（{{ operationsFailedSources }}/5），其餘項目仍可正常查看。
        </div>

        <div class="operations-grid">
          <RouterLink to="/admin/room-booking?filter=stay_today" class="operation-item operation-info">
            <span class="operation-icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 21h18M5 21V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v16M9 9h6M9 13h6" />
                <path d="m9 17 3-3 3 3" />
              </svg>
            </span>
            <span class="operation-content">
              <span class="operation-label">今日入住</span>
              <strong>{{ todayCheckInCount }}</strong>
              <small>{{ todayCheckInCount ? "查看今日入住名單" : "今日沒有入住安排" }}</small>
            </span>
          </RouterLink>

          <RouterLink to="/admin/room-booking" class="operation-item operation-info">
            <span class="operation-icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M3 21h18M5 21V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2v16M9 9h6M9 13h6" />
                <path d="m15 17-3 3-3-3" />
              </svg>
            </span>
            <span class="operation-content">
              <span class="operation-label">今日退房</span>
              <strong>{{ todayCheckOutCount }}</strong>
              <small>{{ todayCheckOutCount ? "確認退房處理進度" : "今日沒有退房安排" }}</small>
            </span>
          </RouterLink>

          <RouterLink to="/admin/orders" class="operation-item" :class="pendingOrderCount ? 'operation-warning-item' : 'operation-clear'">
            <span class="operation-icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M6 2h9l3 3v17H6zM9 10h6M9 14h6M9 18h4" />
              </svg>
            </span>
            <span class="operation-content">
              <span class="operation-label">待處理訂單</span>
              <strong>{{ pendingOrderCount }}</strong>
              <small>{{ pendingOrderCount ? "尚有訂單等待處理" : "訂單皆已處理" }}</small>
            </span>
          </RouterLink>

          <RouterLink to="/admin/room-task" class="operation-item" :class="unfinishedRoomTaskCount ? 'operation-warning-item' : 'operation-clear'">
            <span class="operation-icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 11 12 14 22 4" />
                <path d="M21 12v7a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h11" />
              </svg>
            </span>
            <span class="operation-content">
              <span class="operation-label">未完成房務</span>
              <strong>{{ unfinishedRoomTaskCount }}</strong>
              <small>{{ unfinishedRoomTaskCount ? "有房務工單尚未完成" : "房務工作皆已完成" }}</small>
            </span>
          </RouterLink>

          <RouterLink to="/admin/products" class="operation-item" :class="lowStockProductCount ? 'operation-danger' : 'operation-clear'">
            <span class="operation-icon" aria-hidden="true">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z" />
                <path d="m3.3 7 8.7 5 8.7-5M12 22V12" />
              </svg>
            </span>
            <span class="operation-content">
              <span class="operation-label">低庫存商品</span>
              <strong>{{ lowStockProductCount }}</strong>
              <small>{{ lowStockProductCount ? `庫存 ${lowStockThreshold} 件以下，建議補貨` : "目前庫存狀況正常" }}</small>
            </span>
          </RouterLink>
        </div>
      </template>
    </section>


    <!-- =====================================================
         快速管理與系統資訊
         ===================================================== -->
    <div class="dashboard-grid top-dashboard-grid">
      <!-- 快速管理 -->
      <section class="dashboard-card">
        <div class="card-title">
          <h2>快速管理</h2>
        </div>

        <div class="quick-grid">
          <RouterLink to="/admin/products" class="quick-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="quick-icon" aria-hidden="true">
              <path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z"/>
              <path d="M3 6h18"/>
              <path d="M16 10a4 4 0 0 1-8 0"/>
            </svg>
            <span>商品管理</span>
          </RouterLink>

          <RouterLink to="/admin/orders" class="quick-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="quick-icon" aria-hidden="true">
              <path d="m16.5 9.4-9-5.19M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
              <polyline points="3.27 6.96 12 12.01 20.73 6.96"/>
              <line x1="12" x2="12" y1="22.08" y2="12"/>
            </svg>
            <span>訂單管理</span>
          </RouterLink>

          <RouterLink to="/admin/coupons" class="quick-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="quick-icon" aria-hidden="true">
              <path d="M2 9a3 3 0 0 1 0 6v2a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-2a3 3 0 0 1 0-6V7a2 2 0 0 0-2-2H4a2 2 0 0 0-2 2Z"/>
              <path d="M13 5v2"/>
              <path d="M13 17v2"/>
              <path d="M13 11v2"/>
            </svg>
            <span>優惠券管理</span>
          </RouterLink>

          <RouterLink to="/admin/restaurants" class="quick-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="quick-icon" aria-hidden="true">
              <path d="M18 2v6a3 3 0 0 1-3 3 3 3 0 0 1-3-3V2"/>
              <path d="M15 2v14"/>
              <path d="M15 20v2"/>
              <path d="M5 2v4a3 3 0 0 0 3 3 3 3 0 0 0 3-3V2"/>
              <path d="M8 2v14"/>
              <path d="M8 20v2"/>
            </svg>
            <span>餐廳管理</span>
          </RouterLink>

          <RouterLink to="/admin/rental" class="quick-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="quick-icon" aria-hidden="true">
              <path d="M6 22V4a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v18Z"/>
              <path d="M6 12H4a2 2 0 0 0-2 2v8h4"/>
              <path d="M18 9h2a2 2 0 0 1 2 2v11h-4"/>
              <path d="M10 6h4"/>
              <path d="M10 10h4"/>
              <path d="M10 14h4"/>
              <path d="M10 18h4"/>
            </svg>
            <span>場地租借管理</span>
          </RouterLink>

          <RouterLink to="/admin/reservations" class="quick-item">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="quick-icon" aria-hidden="true">
              <rect width="18" height="18" x="3" y="4" rx="2" ry="2"/>
              <line x1="16" x2="16" y1="2" y2="6"/>
              <line x1="8" x2="8" y1="2" y2="6"/>
              <line x1="3" x2="21" y1="10" y2="10"/>
              <path d="M8 14h.01"/>
              <path d="M12 14h.01"/>
              <path d="M16 14h.01"/>
              <path d="M8 18h.01"/>
              <path d="M12 18h.01"/>
              <path d="M16 18h.01"/>
            </svg>
            <span>訂位管理</span>
          </RouterLink>
        </div>
      </section>

      <!-- 系統資訊 -->
      <section class="dashboard-card">
        <div class="card-title">
          <h2>系統資訊</h2>
        </div>

        <div class="system-info">
          <p>
            系統名稱
            <span>星澄飯店管理系統</span>
          </p>

          <p>
            後端服務
            <span>Spring Boot</span>
          </p>

          <p>
            前端框架
            <span>Vue 3</span>
          </p>

          <p>
            資料庫
            <span>SQL Server</span>
          </p>

          <p>
            訂單統計
            <span>最近 12 個月</span>
          </p>

          <p>
            商品分析
            <span>月銷售統計</span>
          </p>
        </div>
      </section>
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
      class="dashboard-card analytics-card"
      :class="{ collapsed: !analyticsExpanded }"
    >
      <header class="analytics-card-header">
        <div>
          <h2>營運數據分析</h2>
          <p>整合訂單趨勢與商品銷售表現</p>
        </div>

        <div class="analytics-header-actions">
          <div class="analytics-tabs" role="group" aria-label="營運分析類型">
            <button
              type="button"
              class="analytics-tab"
              :class="{ active: activeAnalyticsTab === 'orders' }"
              :aria-pressed="activeAnalyticsTab === 'orders'"
              @click="activeAnalyticsTab = 'orders'; analyticsExpanded = true"
            >
              訂單趨勢
            </button>

            <button
              type="button"
              class="analytics-tab"
              :class="{ active: activeAnalyticsTab === 'products' }"
              :aria-pressed="activeAnalyticsTab === 'products'"
              @click="activeAnalyticsTab = 'products'; analyticsExpanded = true"
            >
              商品月銷售
            </button>

            <button
              type="button"
              class="analytics-tab"
              :class="{ active: activeAnalyticsTab === 'members' }"
              :aria-pressed="activeAnalyticsTab === 'members'"
              @click="activeAnalyticsTab = 'members'; analyticsExpanded = true; ensureMemberDemographicsLoaded()"
            >
              會員分佈畫像
            </button>
          </div>

          <button
            type="button"
            class="analytics-collapse-button"
            :aria-expanded="analyticsExpanded"
            aria-controls="analytics-content"
            @click="analyticsExpanded = !analyticsExpanded"
          >
            <span>{{ analyticsExpanded ? "收合" : "展開" }}</span>
            <svg
              class="analytics-toggle-icon"
              :class="{ open: analyticsExpanded }"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
              aria-hidden="true"
            >
              <path d="m6 9 6 6 6-6" />
            </svg>
          </button>
        </div>
      </header>

      <div v-show="analyticsExpanded" id="analytics-content">
      <div
        v-show="activeAnalyticsTab === 'orders'"
        id="orders-panel"
        class="analytics-section analytics-panel"
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


      <div
        class="analytics-body"
      >

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
      </div>
      </div>


    <!-- =====================================================
         指定月份商品銷售統計
         ===================================================== -->
      <div
        v-show="activeAnalyticsTab === 'products'"
        id="products-panel"
        class="analytics-section analytics-panel product-analytics-section"
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


      <div
        class="analytics-body"
      >

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
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" class="empty-icon-svg" aria-hidden="true">
            <path d="m16.5 9.4-9-5.19M21 16V8a2 2 0 0 0-1-1.73l-7-4a2 2 0 0 0-2 0l-7 4A2 2 0 0 0 3 8v8a2 2 0 0 0 1 1.73l7 4a2 2 0 0 0 2 0l7-4A2 2 0 0 0 21 16z"/>
            <polyline points="3.27 6.96 12 12.01 20.73 6.96"/>
            <line x1="12" x2="12" y1="22.08" y2="12"/>
          </svg>
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
      </div>
      </div>

      <!-- =====================================================
           會員分佈畫像（地區與年齡層）
           ===================================================== -->
      <div
        v-show="activeAnalyticsTab === 'members'"
        id="members-panel"
        class="analytics-section analytics-panel member-analytics-section"
      >
        <!-- 標題與操作按鈕 -->
        <div class="member-analytics-header">
          <div>
            <h2>會員來源地區與年齡層分佈</h2>
            <p>分析會員人口特徵、客源縣市佔比與年齡世代結構</p>
          </div>

          <button
            type="button"
            class="member-refresh-button"
            :disabled="memberDemographicsLoading"
            @click="loadMemberDemographics"
          >
            <svg
              :class="{ spinning: memberDemographicsLoading }"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
              stroke-linecap="round"
              stroke-linejoin="round"
            >
              <path d="M20 11a8.1 8.1 0 0 0-15.5-2M4 4v5h5" />
              <path d="M4 13a8.1 8.1 0 0 0 15.5 2M20 20v-5h-5" />
            </svg>
            {{ memberDemographicsLoading ? "更新中..." : "重新整理" }}
          </button>
        </div>

        <div class="analytics-body">
          <!-- Loading -->
          <div v-if="memberDemographicsLoading && !memberDemographicsLoaded" class="chart-message">
            會員人口統計資料讀取中...
          </div>

          <!-- Error -->
          <div v-else-if="memberDemographicsError" class="chart-error">
            <p>{{ memberDemographicsError }}</p>
            <button class="retry-button" @click="loadMemberDemographics">重試</button>
          </div>

          <!-- 空資料 -->
          <div v-else-if="memberDemographics.totalMembers === 0" class="empty-sales">
            <div class="empty-sales-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" class="empty-icon-svg" aria-hidden="true">
                <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                <circle cx="9" cy="7" r="4"/>
                <line x1="17" x2="22" y1="8" y2="13"/>
                <line x1="22" x2="17" y1="8" y2="13"/>
              </svg>
            </div>
            <strong>目前尚無會員資料</strong>
            <p>待有新會員註冊後將自動產生人口統計圖表</p>
          </div>

          <!-- 視覺化內容 -->
          <div v-else class="member-content-wrapper">
            <!-- 摘要指標卡 (3大核心指標) -->
            <div class="member-summary-grid">
              <div class="member-summary-card">
                <div class="summary-card-header">
                  <span class="summary-label">會員總人數</span>
                  <span class="summary-card-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                      <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/>
                      <circle cx="9" cy="7" r="4"/>
                      <path d="M22 21v-2a4 4 0 0 0-3-3.87"/>
                      <path d="M16 3.13a4 4 0 0 1 0 7.75"/>
                    </svg>
                  </span>
                </div>
                <strong class="summary-value">
                  {{ memberDemographics.totalMembers }}
                  <small>位</small>
                </strong>
                <span class="summary-hint">目前系統中已註冊會員</span>
              </div>

              <div class="member-summary-card">
                <div class="summary-card-header">
                  <span class="summary-label">主要客源縣市</span>
                  <span class="summary-card-icon highlight-city-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                      <path d="M20 10c0 4.993-5.539 10.193-7.399 11.799a1 1 0 0 1-1.202 0C9.539 20.193 4 14.993 4 10a8 8 0 0 1 16 0Z"/>
                      <circle cx="12" cy="10" r="3"/>
                    </svg>
                  </span>
                </div>
                <strong class="summary-value highlight-city">
                  {{ memberDemographics.topCity }}
                </strong>
                <span class="summary-hint">佔比約 {{ memberDemographics.topCityPercentage }}%</span>
              </div>

              <div class="member-summary-card">
                <div class="summary-card-header">
                  <span class="summary-label">主力年齡客群</span>
                  <span class="summary-card-icon highlight-age-icon">
                    <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true">
                      <circle cx="12" cy="12" r="10"/>
                      <polyline points="12 6 12 12 16 14"/>
                    </svg>
                  </span>
                </div>
                <strong class="summary-value highlight-age">
                  {{ memberDemographics.topAgeGroup }}
                </strong>
                <span class="summary-hint">佔比約 {{ memberDemographics.topAgeGroupPercentage }}%</span>
              </div>
            </div>

            <!-- 主圖表網格 -->
            <div class="member-charts-grid">
              <!-- 左側：來源地區排行 -->
              <div class="member-chart-box city-distribution-box">
                <div class="chart-box-header">
                  <div class="chart-box-title">
                    <span class="chart-box-icon">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="chart-box-icon-svg" aria-hidden="true">
                        <path d="M20 10c0 4.993-5.539 10.193-7.399 11.799a1 1 0 0 1-1.202 0C9.539 20.193 4 14.993 4 10a8 8 0 0 1 16 0Z"/>
                        <circle cx="12" cy="10" r="3"/>
                      </svg>
                    </span>
                    <h3>會員來源地區排行</h3>
                  </div>
                  <span class="chart-box-unit">單位：人 / 佔比</span>
                </div>

                <div v-if="memberDemographics.cityDistribution.length === 0" class="chart-empty-inline">
                  暫無地區資料
                </div>

                <div v-else class="city-ranking-list">
                  <div
                    v-for="(item, index) in memberDemographics.cityDistribution"
                    :key="`city-${item.city}`"
                    class="city-ranking-item"
                  >
                    <div
                      class="city-rank-badge"
                      :class="{
                        'rank-gold': index === 0,
                        'rank-silver': index === 1,
                        'rank-bronze': index === 2,
                      }"
                    >
                      {{ index + 1 }}
                    </div>

                    <div class="city-info-col">
                      <div class="city-name-row">
                        <span class="city-name">{{ item.city }}</span>
                        <div class="city-stats">
                          <strong class="city-count">{{ item.count }} <small>人</small></strong>
                          <span class="city-pct">{{ item.percentage }}%</span>
                        </div>
                      </div>

                      <div class="city-bar-bg">
                        <div
                          class="city-bar-fill"
                          :class="{
                            'fill-top1': index === 0,
                            'fill-top2': index === 1,
                            'fill-top3': index === 2,
                          }"
                          :style="{ width: getCityBarWidth(item.count) }"
                        ></div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>

              <!-- 右側：年齡層分佈柱狀圖與性別比例 -->
              <div class="member-chart-box age-distribution-box">
                <div class="chart-box-header">
                  <div class="chart-box-title">
                    <span class="chart-box-icon">
                      <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="chart-box-icon-svg" aria-hidden="true">
                        <path d="M18 20V10"/>
                        <path d="M12 20V4"/>
                        <path d="M6 20v-6"/>
                      </svg>
                    </span>
                    <h3>年齡層世代分佈</h3>
                  </div>
                  <span class="chart-box-unit">單位：人</span>
                </div>

                <div v-if="memberDemographics.ageDistribution.length === 0" class="chart-empty-inline">
                  暫無年齡資料
                </div>

                <div v-else class="age-chart-container">
                  <!-- 年齡柱狀圖 -->
                  <div class="age-bar-chart">
                    <div
                      v-for="item in memberDemographics.ageDistribution"
                      :key="`age-${item.groupName}`"
                      class="age-bar-column"
                    >
                      <div class="age-bar-value">
                        {{ item.count }}
                      </div>

                      <div class="age-bar-area">
                        <div
                          class="age-bar"
                          :class="{ 'zero-bar': item.count === 0 }"
                          :style="{ height: getAgeBarHeight(item.count) }"
                          :title="`${item.groupName}：${item.count} 人 (${item.percentage}%)`"
                        ></div>
                      </div>

                      <div class="age-bar-label">
                        <strong>{{ item.groupName }}</strong>
                        <small>{{ item.percentage }}%</small>
                      </div>
                    </div>
                  </div>

                  <!-- 性別結構比例 -->
                  <div class="gender-section">
                    <div class="gender-header">
                      <span class="gender-title">性別結構比例</span>
                    </div>

                    <!-- 分段比例條 -->
                    <div class="gender-segmented-bar">
                      <div
                        v-for="g in memberDemographics.genderDistribution"
                        :key="`segment-${g.gender}`"
                        class="gender-segment"
                        :class="`gender-${g.gender.toLowerCase()}`"
                        :style="{ width: `${g.percentage}%` }"
                        :title="`${g.label}：${g.count} 人 (${g.percentage}%)`"
                      ></div>
                    </div>

                    <!-- 性別細項指標 -->
                    <div class="gender-legend-grid">
                      <div
                        v-for="g in memberDemographics.genderDistribution"
                        :key="`legend-${g.gender}`"
                        class="gender-legend-item"
                      >
                        <div class="gender-legend-header">
                          <span class="gender-indicator" :class="`indicator-${g.gender.toLowerCase()}`"></span>
                          <span class="gender-label">{{ g.label }}</span>
                        </div>
                        <div class="gender-legend-values">
                          <strong>{{ g.count }} <small>人</small></strong>
                          <span>{{ g.percentage }}%</span>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      </div>
    </section>


  </div>
</template>


<script setup>

import {
  computed,
  onMounted,
  ref,
} from "vue";


const analyticsExpanded = ref(true);

const activeAnalyticsTab = ref("orders");



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
// 今日待辦與異常中心
// =====================================================

const lowStockThreshold = 5;

const todayCheckInCount = ref(0);
const todayCheckOutCount = ref(0);
const pendingOrderCount = ref(0);
const unfinishedRoomTaskCount = ref(0);
const lowStockProductCount = ref(0);
const operationsLoading = ref(false);
const operationsLoaded = ref(false);
const operationsFailedSources = ref(0);
const operationsUpdatedAt = ref("");

const operationsTotal = computed(() =>
  todayCheckInCount.value
  + todayCheckOutCount.value
  + pendingOrderCount.value
  + unfinishedRoomTaskCount.value
  + lowStockProductCount.value
);


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


function getLocalDateKey(date = new Date()) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}


async function fetchOperationList(url) {
  const response = await fetch(url, {
    method: "GET",
    headers: getAuthHeaders(),
  });

  if (!response.ok) {
    throw new Error(`${url} 讀取失敗 (${response.status})`);
  }

  const data = await response.json();
  return Array.isArray(data) ? data : [];
}


async function loadOperationsCenter() {
  operationsLoading.value = true;
  operationsFailedSources.value = 0;

  const requests = await Promise.allSettled([
    fetchOperationList("/api/bookings"),
    fetchOperationList("/api/orders"),
    fetchOperationList("/api/roomtask"),
    fetchOperationList("/api/products"),
  ]);

  const [bookingsResult, ordersResult, tasksResult, productsResult] = requests;
  const today = getLocalDateKey();

  if (bookingsResult.status === "fulfilled") {
    const validBookings = bookingsResult.value.filter((booking) => {
      const status = booking.bookingStatus ?? booking.booking_status;
      return status !== "已取消";
    });

    todayCheckInCount.value = validBookings.filter((booking) =>
      String(booking.checkInDate ?? booking.check_in_date ?? "").startsWith(today)
    ).length;

    todayCheckOutCount.value = validBookings.filter((booking) =>
      String(booking.checkOutDate ?? booking.check_out_date ?? "").startsWith(today)
    ).length;
  } else {
    operationsFailedSources.value += 2;
    console.error(bookingsResult.reason);
  }

  if (ordersResult.status === "fulfilled") {
    pendingOrderCount.value = ordersResult.value.filter((order) =>
      (order.orderStatus ?? order.order_status) === "PENDING"
    ).length;
  } else {
    operationsFailedSources.value += 1;
    console.error(ordersResult.reason);
  }

  if (tasksResult.status === "fulfilled") {
    unfinishedRoomTaskCount.value = tasksResult.value.filter((task) => {
      const status = task.taskStatus ?? task.task_status;
      return status !== "已完成" && status !== "已取消";
    }).length;
  } else {
    operationsFailedSources.value += 1;
    console.error(tasksResult.reason);
  }

  if (productsResult.status === "fulfilled") {
    lowStockProductCount.value = productsResult.value.filter((product) => {
      const stock = Number(product.stock ?? 0);
      return stock <= lowStockThreshold && product.status !== "DISCONTINUED";
    }).length;
  } else {
    operationsFailedSources.value += 1;
    console.error(productsResult.reason);
  }

  operationsUpdatedAt.value = new Intl.DateTimeFormat("zh-TW", {
    hour: "2-digit",
    minute: "2-digit",
    hour12: false,
  }).format(new Date());
  operationsLoaded.value = true;
  operationsLoading.value = false;
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
// 會員人口統計分佈
// =====================================================

const memberDemographics = ref({
  totalMembers: 0,
  profileCount: 0,
  profileCompletionRate: 0,
  topCity: "—",
  topCityPercentage: 0,
  topAgeGroup: "—",
  topAgeGroupPercentage: 0,
  cityDistribution: [],
  ageDistribution: [],
  genderDistribution: [],
});

const memberDemographicsLoading = ref(false);
const memberDemographicsError = ref("");
const memberDemographicsLoaded = ref(false);

const maxCityCount = computed(() => {
  if (!memberDemographics.value.cityDistribution || memberDemographics.value.cityDistribution.length === 0) {
    return 1;
  }
  const max = Math.max(...memberDemographics.value.cityDistribution.map((item) => Number(item.count ?? 0)));
  return Math.max(1, max);
});

const maxAgeCount = computed(() => {
  if (!memberDemographics.value.ageDistribution || memberDemographics.value.ageDistribution.length === 0) {
    return 1;
  }
  const max = Math.max(...memberDemographics.value.ageDistribution.map((item) => Number(item.count ?? 0)));
  return Math.max(1, max);
});

function getCityBarWidth(count) {
  const value = Number(count ?? 0);
  if (value <= 0) return "0%";
  const percentage = (value / maxCityCount.value) * 100;
  return `${Math.max(4, Math.min(100, percentage))}%`;
}

function getAgeBarHeight(count) {
  const value = Number(count ?? 0);
  if (value <= 0) return "4px";
  const percentage = (value / maxAgeCount.value) * 100;
  return `${Math.max(8, Math.min(100, percentage))}%`;
}

async function loadMemberDemographics() {
  memberDemographicsLoading.value = true;
  memberDemographicsError.value = "";

  try {
    const response = await fetch("/api/members/statistics/demographics", {
      method: "GET",
      headers: getAuthHeaders(),
    });

    if (response.status === 401 || response.status === 403) {
      throw new Error("沒有權限讀取會員統計資料");
    }

    if (!response.ok) {
      const errorText = await response.text();
      console.error("會員統計 API 錯誤：", errorText);
      throw new Error(`會員統計讀取失敗 (${response.status})`);
    }

    const data = await response.json();
    console.log("會員統計資料：", data);

    memberDemographics.value = {
      totalMembers: Number(data.totalMembers ?? 0),
      profileCount: Number(data.profileCount ?? 0),
      profileCompletionRate: Number(data.profileCompletionRate ?? 0),
      topCity: data.topCity ?? "—",
      topCityPercentage: Number(data.topCityPercentage ?? 0),
      topAgeGroup: data.topAgeGroup ?? "—",
      topAgeGroupPercentage: Number(data.topAgeGroupPercentage ?? 0),
      cityDistribution: Array.isArray(data.cityDistribution) ? data.cityDistribution : [],
      ageDistribution: Array.isArray(data.ageDistribution) ? data.ageDistribution : [],
      genderDistribution: Array.isArray(data.genderDistribution) ? data.genderDistribution : [],
    };
    memberDemographicsLoaded.value = true;
  } catch (error) {
    console.error("讀取會員統計失敗：", error);
    memberDemographicsError.value = error.message || "會員統計資料讀取失敗";
  } finally {
    memberDemographicsLoading.value = false;
  }
}

function ensureMemberDemographicsLoaded() {
  if (!memberDemographicsLoaded.value && !memberDemographicsLoading.value) {
    loadMemberDemographics();
  }
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

        loadOperationsCenter(),

        loadMemberDemographics(),
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

  color: #7b5a27;

  border-radius: 12px;
}


.stat-icon svg {
  width: 28px;

  height: 28px;
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
   今日待辦與異常中心
   ===================================================== */

.operations-center {
  margin-bottom: 28px;
  padding: 0;
  overflow: hidden;
}

.operations-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 22px 26px;
  border-bottom: 1px solid #eee5d9;
  background: linear-gradient(135deg, #fff 0%, #fcfaf6 100%);
}

.operations-title-row {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

.operations-title-row h2 {
  margin: 0;
  color: #5d431f;
  font-size: 21px;
}

.operations-header p {
  margin: 6px 0 0;
  color: #766f68;
  font-size: 13px;
}

.operations-total-badge {
  padding: 4px 9px;
  color: #76551f;
  background: #f4ead9;
  border-radius: 999px;
  font-size: 12px;
  font-weight: 700;
}

.operations-actions {
  display: flex;
  align-items: center;
  gap: 14px;
  flex: 0 0 auto;
}

.operations-updated-at {
  color: #766f68;
  font-size: 12px;
}

.operations-refresh-button {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  min-height: 42px;
  padding: 0 14px;
  color: #5d431f;
  background: #fff;
  border: 1px solid #dfcfb8;
  border-radius: 10px;
  cursor: pointer;
  font: inherit;
  font-size: 13px;
  font-weight: 700;
  transition: border-color 0.2s ease, background-color 0.2s ease;
}

.operations-refresh-button:hover:not(:disabled) {
  background: #f8f2e8;
  border-color: #b58a46;
}

.operations-refresh-button:focus-visible,
.operation-item:focus-visible {
  outline: 3px solid rgba(181, 138, 70, 0.35);
  outline-offset: 3px;
}

.operations-refresh-button:disabled {
  cursor: wait;
  opacity: 0.65;
}

.operations-refresh-button svg {
  width: 17px;
  height: 17px;
}

.operations-refresh-button svg.spinning {
  animation: operations-spin 0.8s linear infinite;
}

.operations-loading {
  min-height: 150px;
  display: grid;
  place-items: center;
  color: #766f68;
}

.operations-warning {
  margin: 18px 24px 0;
  padding: 11px 14px;
  color: #7a4c05;
  background: #fff7e6;
  border: 1px solid #f0d59d;
  border-radius: 9px;
  font-size: 13px;
}

.operations-grid {
  display: grid;
  grid-template-columns: repeat(5, minmax(0, 1fr));
  gap: 14px;
  padding: 20px 24px 24px;
}

.operation-item {
  display: grid;
  grid-template-columns: 42px minmax(0, 1fr);
  align-items: center;
  gap: 12px;
  min-height: 112px;
  padding: 17px;
  color: #4a3b2a;
  background: #fbfaf8;
  border: 1px solid #e9e0d5;
  border-radius: 12px;
  text-decoration: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease, background-color 0.2s ease;
}

.operation-item:hover {
  background: #fff;
  border-color: #cdb184;
  box-shadow: 0 8px 18px rgba(82, 61, 30, 0.09);
}

.operation-icon {
  display: grid;
  place-items: center;
  width: 42px;
  height: 42px;
  color: #7b5a27;
  background: #f3eadc;
  border-radius: 10px;
}

.operation-icon svg {
  width: 22px;
  height: 22px;
}

.operation-content {
  min-width: 0;
}

.operation-label,
.operation-content small {
  display: block;
}

.operation-label {
  color: #62584e;
  font-size: 13px;
  font-weight: 700;
}

.operation-content strong {
  display: block;
  margin: 3px 0;
  color: #40301e;
  font-size: 27px;
  line-height: 1.1;
}

.operation-content small {
  overflow: hidden;
  color: #766f68;
  font-size: 11px;
  line-height: 1.4;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.operation-warning-item {
  background: #fffaf1;
  border-color: #ead3a9;
}

.operation-warning-item .operation-icon {
  color: #8a5a0a;
  background: #fae9c7;
}

.operation-danger {
  background: #fff8f6;
  border-color: #ecc9c0;
}

.operation-danger .operation-icon {
  color: #a13f2f;
  background: #f8dfd9;
}

.operation-clear .operation-icon {
  color: #3d7352;
  background: #e5f1e9;
}

@keyframes operations-spin {
  to {
    transform: rotate(360deg);
  }
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
   可收合營運分析
   ===================================================== */

.analytics-card {
  margin-bottom: 28px;
  padding: 0;
  overflow: hidden;
}


.analytics-card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 22px 26px;
  border-bottom: 1px solid #e8dfd2;
  background: linear-gradient(135deg, #fff 0%, #fcfaf6 100%);
}


.analytics-card.collapsed .analytics-card-header {
  border-bottom-color: transparent;
}


.analytics-card-header h2 {
  margin: 0 0 5px;
  color: #5d431f;
  font-size: 21px;
}


.analytics-card-header p {
  margin: 0;
  color: #8b8176;
  font-size: 13px;
}


.analytics-header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  flex: 0 0 auto;
}


.analytics-tabs {
  display: inline-flex;
  gap: 4px;
  padding: 4px;
  border: 1px solid #e5dac9;
  border-radius: 11px;
  background: #f5f0e8;
}


.analytics-tab,
.analytics-collapse-button {
  min-height: 40px;
  border: 0;
  font: inherit;
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
}


.analytics-tab {
  padding: 9px 15px;
  border-radius: 8px;
  background: transparent;
  color: #786b5d;
  transition:
    background-color 0.2s ease,
    box-shadow 0.2s ease,
    color 0.2s ease;
}


.analytics-tab:hover {
  color: #5d431f;
  background: rgba(255, 255, 255, 0.65);
}


.analytics-tab.active {
  color: #fff;
  background: #95691f;
  box-shadow: 0 3px 9px rgba(111, 83, 40, 0.2);
}


.analytics-collapse-button {
  min-width: 86px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  padding: 9px 13px;
  border: 1px solid #d9c5a5;
  border-radius: 9px;
  background: #fff;
  color: #6f5328;
  transition:
    background-color 0.2s ease,
    border-color 0.2s ease,
    box-shadow 0.2s ease;
}


.analytics-collapse-button:hover {
  border-color: #b58a46;
  background: #f8f2e9;
  box-shadow: 0 3px 10px rgba(111, 83, 40, 0.1);
}


.analytics-tab:focus-visible,
.analytics-collapse-button:focus-visible {
  outline: 3px solid rgba(181, 138, 70, 0.3);
  outline-offset: 2px;
}


.analytics-toggle-icon {
  width: 17px;
  height: 17px;
  transition: transform 0.2s ease;
}


.analytics-toggle-icon.open {
  transform: rotate(180deg);
}


.analytics-section {
  padding: 26px;
}


.analytics-card .chart-title,
.analytics-card .product-sales-header {
  margin-bottom: 0;
}


.analytics-body {
  margin-top: 24px;
}


/* =====================================================
   訂單圖表
   ===================================================== */

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


.top-dashboard-grid {
  margin-bottom: 28px;
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

  font-size: 15px;

  text-decoration: none;

  transition: 0.25s;
}


.quick-icon {
  width: 32px;

  height: 32px;

  color: #7b5a27;

  stroke: #7b5a27;

  transition: transform 0.25s ease, color 0.25s ease;
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


.quick-item:hover .quick-icon {
  color: #b58a46;

  stroke: #b58a46;

  transform: scale(1.08);
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

@media (max-width: 1300px) {
  .operations-grid {
    grid-template-columns: repeat(3, minmax(0, 1fr));
  }
}

@media (
  max-width: 1000px
) {

  .operations-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

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

  .operations-header {
    align-items: stretch;
    flex-direction: column;
    gap: 16px;
    padding: 20px;
  }

  .operations-actions {
    justify-content: space-between;
  }

  .analytics-card-header {
    align-items: stretch;
    flex-direction: column;
    gap: 16px;
    padding: 20px;
  }


  .analytics-header-actions {
    align-items: stretch;
    flex-direction: column;
  }


  .analytics-tabs {
    display: flex;
  }


  .analytics-tab {
    flex: 1;
  }


  .analytics-collapse-button {
    width: 100%;
  }

  .analytics-section {
    padding: 20px;
  }


  .analytics-card .chart-title,
  .analytics-card .product-sales-header {
    align-items: flex-start;
    gap: 16px;
  }

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


@media (prefers-reduced-motion: reduce) {
  .analytics-tab,
  .analytics-collapse-button,
  .analytics-toggle-icon,
  .operations-refresh-button,
  .operation-item {
    transition: none;
  }

  .operations-refresh-button svg.spinning {
    animation: none;
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


  .operations-grid {
    grid-template-columns: 1fr;
    padding: 16px;
  }


  .operations-actions {
    align-items: stretch;
    flex-direction: column;
  }


  .operations-refresh-button {
    width: 100%;
  }
}

/* =====================================================
   會員分佈畫像視覺化樣式
   ===================================================== */

.member-analytics-section {
  padding-top: 10px;
}

.member-analytics-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 16px;
  margin-bottom: 24px;
}

.member-analytics-header h2 {
  color: #4a3b2a;
  font-size: 20px;
  margin-bottom: 4px;
}

.member-analytics-header p {
  color: #777777;
  font-size: 14px;
}

.member-refresh-button {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  background: #fdfaf6;
  border: 1px solid #e2d7c8;
  border-radius: 8px;
  color: #6f5328;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

.member-refresh-button:hover:not(:disabled) {
  background: #f4ece1;
  border-color: #c69a55;
  color: #4a3b2a;
}

.member-refresh-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.member-refresh-button svg {
  width: 16px;
  height: 16px;
}

.member-refresh-button svg.spinning {
  animation: spin 1s linear infinite;
}

.member-content-wrapper {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

/* 摘要指標卡 (3大核心指標) */
.member-summary-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.member-summary-card {
  display: flex;
  flex-direction: column;
  padding: 18px 20px;
  background: #faf6f0;
  border: 1px solid #ebdccb;
  border-radius: 12px;
  transition: transform 0.2s ease, box-shadow 0.2s ease;
}

.member-summary-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 14px rgba(111, 83, 40, 0.08);
}

.summary-card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.member-summary-card .summary-label {
  color: #7a6a57;
  font-size: 13px;
  font-weight: 500;
  margin: 0;
}

.summary-card-icon {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 32px;
  height: 32px;
  background: #eee4d7;
  color: #7b5a27;
  border-radius: 8px;
}

.summary-card-icon svg {
  width: 17px;
  height: 17px;
}

.highlight-city-icon {
  background: #faeed9;
  color: #c69a55;
}

.highlight-age-icon {
  background: #e4edf8;
  color: #315f94;
}

.member-summary-card .summary-value {
  color: #4a3b2a;
  font-size: 24px;
  font-weight: bold;
  line-height: 1.2;
}

.member-summary-card .summary-value small {
  font-size: 14px;
  font-weight: normal;
  color: #777777;
  margin-left: 2px;
}

.member-summary-card .summary-value.highlight-city {
  color: #c69a55;
}

.member-summary-card .summary-value.highlight-age {
  color: #315f94;
}

.member-summary-card .summary-hint {
  color: #999999;
  font-size: 12px;
  margin-top: 6px;
}

/* 主圖表網格 (雙欄) */
.member-charts-grid {
  display: grid;
  grid-template-columns: 1fr 1.15fr;
  gap: 24px;
  align-items: stretch;
}

.member-chart-box {
  display: flex;
  flex-direction: column;
  padding: 22px;
  background: #ffffff;
  border: 1px solid #eee7dd;
  border-radius: 14px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.03);
}

.chart-box-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 12px;
  border-bottom: 1px solid #f3ece4;
}

.chart-box-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.chart-box-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  color: #7b5a27;
}

.chart-box-icon-svg {
  width: 18px;
  height: 18px;
  color: #7b5a27;
}

.empty-icon-svg {
  width: 48px;
  height: 48px;
  color: #bdae9c;
  stroke: #bdae9c;
  margin-bottom: 8px;
}

.chart-box-title h3 {
  color: #4a3b2a;
  font-size: 16px;
  font-weight: bold;
  margin: 0;
}

.chart-box-unit {
  color: #999999;
  font-size: 12px;
}

.chart-empty-inline {
  padding: 30px;
  text-align: center;
  color: #999999;
  font-size: 14px;
}

/* 左側：地區排行進度圖 */
.city-ranking-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
  max-height: 420px;
  overflow-y: auto;
  padding-right: 6px;
}

.city-ranking-item {
  display: flex;
  align-items: center;
  gap: 14px;
}

.city-rank-badge {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 28px;
  height: 28px;
  min-width: 28px;
  background: #e9e4dc;
  color: #666666;
  border-radius: 8px;
  font-size: 12px;
  font-weight: bold;
}

.city-rank-badge.rank-gold {
  background: linear-gradient(135deg, #f5d061, #e5a93b);
  color: #ffffff;
  box-shadow: 0 2px 6px rgba(229, 169, 59, 0.35);
}

.city-rank-badge.rank-silver {
  background: linear-gradient(135deg, #d3d8dc, #a4adb5);
  color: #ffffff;
  box-shadow: 0 2px 6px rgba(164, 173, 181, 0.35);
}

.city-rank-badge.rank-bronze {
  background: linear-gradient(135deg, #dfa375, #bd7a49);
  color: #ffffff;
  box-shadow: 0 2px 6px rgba(189, 122, 73, 0.35);
}

.city-info-col {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.city-name-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.city-name {
  color: #4a3b2a;
  font-size: 14px;
  font-weight: 600;
}

.city-stats {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.city-count {
  color: #4a3b2a;
  font-size: 13px;
}

.city-count small {
  color: #888888;
  font-size: 11px;
}

.city-pct {
  color: #888888;
  font-size: 12px;
  min-width: 44px;
  text-align: right;
}

.city-bar-bg {
  width: 100%;
  height: 8px;
  background: #f1ece4;
  border-radius: 999px;
  overflow: hidden;
}

.city-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #b08d57, #cfa86e);
  border-radius: 999px;
  transition: width 0.4s ease;
}

.city-bar-fill.fill-top1 {
  background: linear-gradient(90deg, #c69a55, #dfb774);
}

.city-bar-fill.fill-top2 {
  background: linear-gradient(90deg, #6389b5, #88aed9);
}

.city-bar-fill.fill-top3 {
  background: linear-gradient(90deg, #7c9d74, #9ec196);
}

/* 右側：年齡層分佈柱狀圖 */
.age-chart-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
  flex: 1;
}

.age-bar-chart {
  display: flex;
  align-items: flex-end;
  justify-content: space-around;
  gap: 12px;
  min-height: 180px;
  padding: 10px 0 0;
  border-bottom: 1px solid #eee7dd;
}

.age-bar-column {
  display: flex;
  flex: 1;
  flex-direction: column;
  justify-content: flex-end;
  align-items: center;
  min-width: 48px;
}

.age-bar-value {
  margin-bottom: 6px;
  color: #6f5328;
  font-size: 12px;
  font-weight: bold;
}

.age-bar-area {
  display: flex;
  align-items: flex-end;
  width: 32px;
  height: 120px;
}

.age-bar {
  width: 100%;
  min-height: 4px;
  background: linear-gradient(180deg, #5b84b1, #2b5586);
  border-radius: 6px 6px 0 0;
  transition: height 0.35s ease;
}

.age-bar-label {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2px;
  margin-top: 8px;
  text-align: center;
}

.age-bar-label strong {
  color: #4a3b2a;
  font-size: 12px;
  white-space: nowrap;
}

.age-bar-label small {
  color: #888888;
  font-size: 11px;
}

/* 性別結構比例 */
.gender-section {
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding-top: 8px;
}

.gender-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.gender-title {
  color: #4a3b2a;
  font-size: 13px;
  font-weight: 600;
}

.gender-segmented-bar {
  display: flex;
  width: 100%;
  height: 12px;
  background: #ede7de;
  border-radius: 999px;
  overflow: hidden;
  box-shadow: inset 0 1px 3px rgba(0, 0, 0, 0.08);
}

.gender-segment {
  height: 100%;
  transition: width 0.4s ease;
}

.gender-segment.gender-m {
  background: #315f94;
}

.gender-segment.gender-f {
  background: #d9776f;
}

.gender-segment.gender-other {
  background: #a09b93;
}

.gender-legend-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}

.gender-legend-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 10px 12px;
  background: #faf6f0;
  border-radius: 8px;
}

.gender-legend-header {
  display: flex;
  align-items: center;
  gap: 6px;
}

.gender-indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.indicator-m {
  background: #315f94;
}

.indicator-f {
  background: #d9776f;
}

.indicator-other {
  background: #a09b93;
}

.gender-label {
  color: #666666;
  font-size: 12px;
}

.gender-legend-values {
  display: flex;
  justify-content: space-between;
  align-items: baseline;
}

.gender-legend-values strong {
  color: #4a3b2a;
  font-size: 14px;
}

.gender-legend-values strong small {
  font-size: 11px;
  font-weight: normal;
  color: #888888;
}

.gender-legend-values span {
  color: #888888;
  font-size: 12px;
}

/* 響應式佈局適配 */
@media (max-width: 1024px) {
  .member-summary-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .member-charts-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 600px) {
  .member-summary-grid {
    grid-template-columns: 1fr;
  }

  .gender-legend-grid {
    grid-template-columns: 1fr;
  }

  .age-bar-column {
    min-width: 38px;
  }

  .age-bar-label strong {
    font-size: 11px;
  }
}
</style>
