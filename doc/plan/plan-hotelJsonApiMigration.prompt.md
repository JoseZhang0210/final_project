# 計劃：從 Hotel 專案中完全移除 Thymeleaf 並轉換為 REST API + 靜態前端

## TL;DR
將混合架構（Thymeleaf MVC + REST API）轉換為純 REST API 後端 + 靜態前端分離架構。移除 pom.xml 中的 Thymeleaf 依賴，轉換 7 個返回視圖的 controllers 為純 REST API，**所有資料透過 JSON 格式傳遞**。前端採用靜態 HTML + Vanilla JavaScript（階段 1），完全相容於未來的 Vue.js 遷移。此架構確保前後端完全解耦，便於獨立開發和 Vue 集成。

## 當前狀態分析
- **Thymeleaf 依賴**：spring-boot-starter-thymeleaf (v4.1.0)
- **模板數量**：20 個 HTML 檔案在 src/main/resources/templates/
- **受影響 Controllers**：7 個（HomeController, ProductController, VenueController, RentalController, BookingOrderController, RoomTypeController, RestaurantPageController）
- **已是 REST API 的 Controllers**：8 個（不需要修改）
- **模板位置**：
  - 根目錄：index.html, register.html
  - roombooking/：5 個檔案
  - products/：3 個檔案
  - venues/：3 個檔案
  - rentals/：3 個檔案
  - members/：1 個檔案
  - restaurants/：1 個檔案
  - reservations/：1 個檔案
  - restaurant-times/：1 個檔案

## 遷移計劃

### 階段 1：依賴管理與配置清理
1. **從 pom.xml 移除 Thymeleaf 依賴**
   - 刪除 `<dependency>org.springframework.boot:spring-boot-starter-thymeleaf</dependency>`
   - 保留其他依賴（web, data-jpa, security 等）

2. **清理 application.properties**
   - 檢查並移除所有 `spring.thymeleaf.*` 配置項
   - 確保 CORS 配置適用於跨域 API 呼叫

3. **檢查 RootAppConfig.java 和 SecurityConfig.java**
   - 驗證沒有 Thymeleaf 特定的 bean 配置
   - 如需要，添加 CORS 配置以支持前端請求

### 階段 2：Controller 架構轉換 - JSON API 設計
對以下 7 個 controllers 進行轉換，所有端點返回 JSON 資料：

**核心 API 設計原則：**
- 所有資料傳遞格式：**JSON**
- 標準 HTTP 方法：GET（查詢）、POST（建立）、PUT（更新）、DELETE（刪除）
- 統一的 JSON 回應格式
- 支持分頁、排序、篩選
- 前端透過 fetch API 或 axios 呼叫，確保 Vue.js 遷移時無縫銜接

**HomeController**
- 轉換方法：
  - `GET /api/home/dashboard` - 返回首頁資料（JSON）
  - `GET /api/home/register-info` - 返回註冊頁面初始資料
  - `GET /api/roombooking/config` - 返回房間預訂配置資料
- JSON 回應範例：`{ "status": "success", "data": {...}, "message": "" }`

**ProductController**
- 轉換端點：
  - `GET /api/products` - 取得產品列表（支持分頁、排序）
  - `GET /api/products/{id}` - 取得單個產品詳情
  - `POST /api/products` - 建立新產品（JSON 請求體）
  - `PUT /api/products/{id}` - 更新產品（JSON 請求體）
  - `DELETE /api/products/{id}` - 刪除產品
- 建立 `ProductDTO` 類，定義標準的 JSON 結構

**VenueController**
- 轉換端點：
  - `GET /api/venues` - 取得場地列表
  - `POST /api/venues` - 建立場地
  - `PUT /api/venues/{id}` - 更新場地
  - `DELETE /api/venues/{id}` - 刪除場地
- 建立 `VenueDTO`

**RentalController**
- 轉換端點：
  - `GET /api/rentals` - 取得租賃列表
  - `POST /api/rentals` - 建立租賃
  - `PUT /api/rentals/{id}` - 更新租賃
  - `DELETE /api/rentals/{id}` - 刪除租賃
- 建立 `RentalDTO`

**BookingOrderController & RoomTypeController**
- 合併為統一的預訂 API：
  - `GET /api/booking-orders` - 取得預訂列表
  - `POST /api/booking-orders` - 建立預訂
  - `PUT /api/booking-orders/{id}` - 更新預訂
  - `DELETE /api/booking-orders/{id}` - 刪除預訂
  - `GET /api/room-types` - 取得房型列表

**RestaurantPageController**
- 轉換端點：
  - `GET /api/restaurants/list` - 取得餐廳列表
  - `GET /api/reservations/list` - 取得訂位列表
  - `GET /api/restaurant-times/list` - 取得營業時段列表

### 階段 3：前端檔案遷移與轉換
**採用方案：純靜態 HTML + Vanilla JavaScript**（完全相容 Vue.js 遷移）

**轉換步驟：**
1. 建立 `src/main/resources/static/` 資料夾
2. 複製 templates/ 目錄結構到 static/
3. 對每個模板進行轉換：
   - 移除所有 Thymeleaf 標記（`th:*`, `xmlns:th`）
   - 將 `th:text="${variable}"` 轉換為 `<span id="var-name"></span>`，透過 JavaScript 填充
   - 將 `th:href="@{/path}"` 轉換為 `href="/path"`
   - 將 `th:each` 迴圈轉換為 JavaScript 動態生成

**前端檔案結構：**
```
src/main/resources/static/
├── index.html
├── register.html
├── css/
│   └── style.css              # 全域樣式
├── js/
│   ├── api.js                 # 統一的 API 呼叫模組（封裝 fetch）
│   ├── common.js              # 通用工具函數
│   ├── products.js            # 產品頁面邏輯
│   ├── venues.js              # 場地頁面邏輯
│   ├── rentals.js             # 租賃頁面邏輯
│   ├── roombooking.js         # 房間預訂邏輯
│   ├── restaurants.js         # 餐廳頁面邏輯
│   ├── members.js             # 成員頁面邏輯
│   └── reservations.js        # 訂位頁面邏輯
├── products/
│   ├── list.html
│   ├── add.html
│   └── edit.html
├── venues/
│   ├── list.html
│   ├── add.html
│   └── edit.html
├── rentals/
│   ├── list.html
│   ├── add.html
│   └── edit.html
├── roombooking/
│   ├── hotelhome.html
│   ├── bookingcheck.html
│   ├── roomlist.html
│   ├── roomtypeCRUD.html
│   └── serenestay.html
├── restaurants/
│   └── list.html
├── members/
│   └── list.html
├── reservations/
│   └── list.html
└── restaurant-times/
    └── list.html
```

**API 呼叫模組範例（js/api.js）：**
```javascript
// 通用的 API 呼叫函數，完全相容 Vue.js 後續遷移
const API_BASE = '/api';

export const apiCall = async (method, endpoint, data = null) => {
  const options = {
    method,
    headers: { 'Content-Type': 'application/json' }
  };
  
  if (data) options.body = JSON.stringify(data);
  
  const response = await fetch(`${API_BASE}${endpoint}`, options);
  return response.json();
};

export const getProducts = () => apiCall('GET', '/products');
export const createProduct = (data) => apiCall('POST', '/products', data);
export const updateProduct = (id, data) => apiCall('PUT', `/products/${id}`, data);
export const deleteProduct = (id) => apiCall('DELETE', `/products/${id}`);
// ... 其他 API 函數
```

### 階段 4：API 回應格式標準化
**統一的 JSON 回應格式（便於 Vue 集成）：**

成功回應：
```json
{
  "status": "success",
  "code": 200,
  "data": {
    "id": 1,
    "name": "Product Name",
    "description": "..."
  },
  "message": "Operation successful"
}
```

列表回應（支持分頁）：
```json
{
  "status": "success",
  "code": 200,
  "data": {
    "content": [
      { "id": 1, "name": "..." },
      { "id": 2, "name": "..." }
    ],
    "totalElements": 50,
    "totalPages": 5,
    "currentPage": 1,
    "pageSize": 10
  },
  "message": ""
}
```

錯誤回應：
```json
{
  "status": "error",
  "code": 400,
  "data": null,
  "message": "Validation error: field name is required"
}
```

為每個轉換的 controller 建立相應的 DTO：
- `ProductDTO`, `VenueDTO`, `RentalDTO`, `RoomTypeDTO`, `BookingDTO`, 等
- 放在 `src/main/java/com/hotel/dto/` 目錄

### 階段 5：CORS 與跨域配置
1. **CORS 配置**
   - 在 SecurityConfig 或新建 WebConfig 中添加 CORS bean
   - 配置 `allowedOrigins`, `allowedMethods`, `allowedHeaders`
   - 支持前端 fetch/axios 跨域請求

2. **認證與授權**
   - 如使用 Session，確保前端在請求時包含 Cookie
   - 如遷移到 JWT，需要調整認證流程

### 階段 6：刪除冗餘檔案
1. **刪除 templates/ 目錄**（遷移後）
2. **刪除 webapp/ 目錄**（如果不再使用）
3. 清理 target/ 目錄（編譯後自動）

### 階段 7：構建與測試
1. **Maven 構建**：`mvn clean install`
2. **驗證無依賴錯誤**
3. **啟動應用**：`mvn spring-boot:run`
4. **測試所有 API 端點**（使用 Postman 或 curl）
5. **驗證前端頁面加載**（從 http://localhost:8080/ 訪問）
6. **驗證 CORS 跨域請求**

## 關鍵檔案

### 需要修改的檔案
- `pom.xml` — 移除 spring-boot-starter-thymeleaf 依賴
- `src/main/resources/application.properties` — 清理 thymeleaf 配置
- `src/main/java/com/hotel/config/RootAppConfig.java` — 驗證/添加必要配置
- `src/main/java/com/hotel/config/SecurityConfig.java` — 添加 CORS 配置
- `src/main/java/com/hotel/controller/HomeController.java` — 轉換為 REST API，返回 JSON
- `src/main/java/com/hotel/controller/ProductController.java` — 轉換為 REST API，返回 JSON
- `src/main/java/com/hotel/controller/VenueController.java` — 轉換為 REST API，返回 JSON
- `src/main/java/com/hotel/controller/RentalController.java` — 轉換為 REST API，返回 JSON
- `src/main/java/com/hotel/controller/BookingOrderController.java` — 轉換為 REST API，返回 JSON
- `src/main/java/com/hotel/controller/RoomTypeController.java` — 轉換為 REST API，返回 JSON
- `src/main/java/com/hotel/controller/RestaurantPageController.java` — 轉換為 REST API，返回 JSON

### 需要建立的檔案
- `src/main/java/com/hotel/dto/` — 新建 DTO 目錄，包含所有資料傳輸物件
- `src/main/java/com/hotel/config/WebConfig.java` — CORS 配置（如需要）
- `src/main/resources/static/` — 靜態前端檔案（HTML, CSS, JS）
- `src/main/resources/static/js/api.js` — 統一的 API 呼叫模組
- `src/main/resources/static/js/common.js` — 通用工具函數
- `src/main/resources/static/css/style.css` — 全域樣式

### 需要刪除的檔案/目錄
- `src/main/resources/templates/` — 所有 Thymeleaf 模板
- `src/main/webapp/` — webapp 目錄（如已遷移）

### 參考現有的 REST Controllers（不需修改）
- `AccountController` — 已使用 @RestController，返回 JSON
- `BookingController` — 已使用 @RestController，返回 JSON
- `ImageController` — 已使用 @RestController，返回 JSON
- `MemberAdminController` — 已使用 @RestController，返回 JSON
- `ReservationController` — 已使用 @RestController，返回 JSON
- `RestaurantController` — 已使用 @RestController，返回 JSON
- `RestaurantTimeController` — 已使用 @RestController，返回 JSON
- `RoomController` — 已使用 @RestController，返回 JSON

## 驗證檢查清單

### 構建與部署
1. ✓ `mvn clean install` 成功，無 Thymeleaf 相關錯誤
2. ✓ 應用啟動無異常
3. ✓ 檢查日誌中無 Thymeleaf 配置警告

### JSON API 功能驗證
1. ✓ 所有 7 個轉換後的 controllers 返回 JSON 資料
2. ✓ `GET /api/products` 返回 JSON 格式的產品列表
3. ✓ `POST /api/products` 接收 JSON 資料並建立產品，返回 JSON 結果
4. ✓ `PUT /api/products/{id}` 更新產品（JSON 請求/回應）
5. ✓ `DELETE /api/products/{id}` 刪除產品
6. ✓ 重複測試 venues, rentals, room-types, booking-orders 等
7. ✓ 驗證所有已有的 REST controllers 仍正常工作
8. ✓ 測試 CORS 請求（確認 Access-Control-Allow-Origin 標頭）

### 前端功能驗證
1. ✓ 訪問 http://localhost:8080/index.html 頁面加載
2. ✓ 前端 JavaScript 成功透過 fetch 呼叫後端 JSON API
3. ✓ CORS 跨域請求成功
4. ✓ 頁面資料正確顯示（來自 JSON API 回應）
5. ✓ 表單提交與 CRUD 操作正常，資料透過 JSON 傳遞
6. ✓ 驗證所有轉換後的頁面功能

### 檔案系統驗證
1. ✓ 確認 templates/ 目錄已刪除或為空
2. ✓ 確認 static/ 目錄包含所有前端檔案
3. ✓ 驗證 pom.xml 不再包含 spring-boot-starter-thymeleaf
4. ✓ 驗證所有 JSON API 端點遵循統一的回應格式

## 決策項與假設

1. **資料傳遞格式：JSON**
   - 所有前後端通訊透過 JSON 格式進行
   - 確保與 Vue.js、React 等現代框架無縫集成
   - 前端 JavaScript 使用 fetch API 或 axios 呼叫 API

2. **前端框架選擇**
   - **階段 1（當前）**：純 HTML + Vanilla JavaScript（簡潔，易維護）
   - **階段 2（未來）**：遷移至 Vue.js 3（與目前的 JSON API 完全相容）
   - 設計完全為 Vue 遷移做好準備

3. **認證方式**
   - 假設：繼續使用 Spring Security session-based 認證
   - 如需移動應用支持，建議遷移至 JWT token-based 認證

4. **資料庫與實體類**
   - 保持不變：所有 entity 和 repository 層保持原狀
   - 僅在 controller 層返回 DTO 而非直接返回 entity

5. **特殊頁面處理**
   - `member-admin.html` 在 static/ 目錄（不在 templates 中），需要驗證是否仍使用

6. **編碼與部署**
   - 應用將以標準 WAR 或 JAR 格式部署
   - Spring Boot 內置 Tomcat 可直接提供 static/ 目錄中的檔案

## 後續考慮事項

1. **Vue.js 遷移計劃**（第二階段）
   - 建立 Vue.js 3 專案（使用 Vite 或 Create Vue）
   - 複用目前的 JSON API
   - 將 static/ 中的 JavaScript 轉換為 Vue 元件
   - 使用 axios 或 fetch 呼叫後端 API
   - 無需修改後端程式碼，完全相容

2. **API 版本管理**
   - 建議為 API 添加版本前綴（如 `/api/v1/`）以支持未來的升級
   - 便於向前相容性

3. **錯誤處理與驗證**
   - 統一的 REST 異常處理器（GlobalExceptionHandler）
   - 請求資料驗證（@Valid, @Validated）
   - 統一的 JSON 錯誤回應格式

4. **測試覆蓋**
   - 編寫 JUnit 單元測試以覆蓋轉換後的 API
   - 編寫集成測試驗證前後端 JSON 交互
   - 編寫前端 JavaScript 測試驗證 API 呼叫

5. **性能優化**
   - 考慮添加快取（Redis）
   - API 回應壓縮（gzip）
   - 分頁優化
