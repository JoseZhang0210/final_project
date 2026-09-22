# 星澄飯店管理與線上服務系統 (Starlight Hotel)
> EEIT 23 第一組 期末專題成果

[![Full-Stack CI/CD](https://github.com/JoseZhang0210/final_project/actions/workflows/cicd.yml/badge.svg)](https://github.com/JoseZhang0210/final_project/actions/workflows/cicd.yml)

本專案為前後端分離架構的綜合飯店服務與營運管理系統，涵蓋前台旅客線上預約服務（訂房、訂位、周邊商城、場地租借與會員中心）以及後台員工營運管理（房態房務、商品庫存、訂單管理、細部權限控制與營運儀表板）。

---

## 技術棧

### 後端 (hotel-backend)
- **核心框架**：Java 21、Spring Boot 3
- **資料庫存取**：Spring Data JPA (Hibernate)、Microsoft SQL Server
- **安全性與驗證**：Spring Security、JJWT Token 認證機制
- **第三方整合**：綠界金流 (ECPay)、Spring Mail (Gmail SMTP 密碼重設與通知)、SMS 簡訊服務
- **輔助工具**：Lombok、Spring Boot Actuator

### 前端 (hotel-frontend)
- **核心框架**：Vue 3 (Composition API)、Vite
- **狀態管理與路由**：Pinia、Vue Router
- **網路請求**：Axios (攔截器整合 JWT 自動攜帶與 Token 刷新)
- **UI 與元件**：Vue Datepicker (`@vuepic/vue-datepicker`)

### 容器化與 DevOps
- **容器管理**：Docker、Docker Compose
- **持續整合與交付 (CI/CD)**：GitHub Actions（自動化單元測試、前端建置、GHCR 容器映像發布、本地 Self-hosted Runner 自動部署）
- **外部連線與穿透**：ngrok Tunnel

---

## 系統核心模組

| 系統模組 | 前台旅客功能 | 後台營運與員工功能 |
|---|---|---|
| **會員與權限管理** | 會員註冊、登入、Google 第三方驗證、忘記密碼 (Email 驗證重設)、個人資料與密碼修改、個人各項訂單查詢 | 員工管理 (CRUD)、會員管理、部門職務設定、細部功能權限控管 (RBAC) |
| **客房預訂與房務** | 房型瀏覽與篩選、即時房況搜尋、線上訂房流程、綠界金流/信用卡結帳、訂房紀錄查詢與取消 | 房型與房型圖片維護、房間狀態管理、房務清潔與設備維修任務指派 (RoomTask)、訂房訂單審核與匯出 |
| **餐廳與訂位系統** | 餐廳介紹、菜單瀏覽、線上時段預約、用餐人數與餐期選擇 | 餐廳資訊維護、餐期時段設定 (RestaurantTime)、預約訂單管理 (Reservation CRUD)、簡訊發送通知 |
| **周邊商城系統** | 商品瀏覽、分類檢索、商品評價與願望清單、購物車、線上結帳與付款 | 商品與分類管理 (CRUD)、商品圖片上傳、商品訂單狀態追蹤、庫存管理、訂單明細匯出 |
| **場地租借系統** | 多功能場地與會議室介紹、線上預約租借、會員租借紀錄追蹤 | 場地資訊與圖片維護 (Venue / VenueImage)、租借申請審核、租借付款記錄管理 (RentalPayment) |
| **行銷與營運數據** | 優惠券領取、結帳折扣碼折抵 (訂房與商城) | 優惠券建立與折抵規則設定、營運數據統計儀表板 (Dashboard) |

---

## 專案目錄結構

```text
final_project/
├── .github/workflows/         # GitHub Actions CI/CD 與本地部署工作流程
│   ├── cicd.yml               # 全端測試、打包與 GHCR 映像發布
│   └── deploy-local.yml       # 本地 Self-hosted Runner 自動拉取與重啟

├── .env.example               # 環境變數設定範本 (複製為 .env 使用)
├── docker-compose.yml         # Docker 容器編排 (SQL Server、Backend、ngrok)
├── init-db.sql                # SQL Server 資料庫初始化腳本
├── hotel-backend/             # Spring Boot 後端專案
│   ├── src/main/java/         # 控制器、業務邏輯、資料模型與安全過濾器
│   │   └── com/hotel/
│   │       ├── config/        # 安全設定 (SecurityConfig)、WebMvc、CORS 配置
│   │       ├── controller/    # REST API 控制器 (Auth, Booking, Product, Room, etc.)
│   │       ├── model/         # Entity 與 DTO 定義
│   │       ├── repository/    # Spring Data JPA 資料存取層
│   │       ├── service/       # 核心商業邏輯實作
│   │       └── util/          # 工具類 (MailUtil, JwtUtil, etc.)
│   ├── src/main/resources/    # application.properties 與靜態資源
│   └── pom.xml                # Maven 依賴設定
├── hotel-frontend/            # Vue 3 前端專案
│   ├── src/
│   │   ├── api/               # 後端 API 請求封裝
│   │   ├── layouts/           # 前後台版型 (MainLayout, AdminLayout, MemberLayout)
│   │   ├── router/            # 路由定義與權限守衛 (router/index.js)
│   │   ├── stores/            # Pinia 狀態管理 (auth, cart 等)
│   │   └── views/             # 頁面元件 (顧客前台、會員中心、後台管理)
│   ├── package.json           # 前端套件依賴
│   └── vite.config.js         # Vite 開發代理與打包設定
└── doc/                       # 專案規格文件、網站架構圖 (Sitemap) 與初期提案
```

---

## 環境建置與啟動

### 1. 環境變數設定 (.env)
專案支援透過根目錄的 `.env` 檔案管理機密資訊（如 Google 應用程式密碼、ngrok token）。
在專案根目錄複製範本並填入設定：
```sh
cp .env.example .env
```
主要參數說明：
- `NGROK_AUTHTOKEN`：ngrok 穿透 Authtoken。
- `MAIL_USERNAME`：Gmail 寄件信箱（預設：`xingchenghotel2026@gmail.com`）。
- `MAIL_PASSWORD`：Google 應用程式密碼（16 位字元，用於寄送會員驗證信與通知信）。

> 💡 後端 Spring Boot 在本地啟動或透過 Docker Compose 啟動時，均會自動載入 `.env` 變數。

---

### 2. 方式 A：Docker 一鍵啟動 (推薦)
若已安裝 Docker Desktop，可直接使用 Docker Compose 啟動所有後端服務與資料庫：
```sh
# 啟動 SQL Server、資料庫初始化、Spring Boot 後端及 ngrok
docker compose up -d
```
服務啟動後：
- 後端 API：`http://localhost:8082` (本機映射埠號)
- 資料庫 SQL Server：`localhost:1433`

接著啟動前端：
```sh
cd hotel-frontend
npm install
npm run dev
```

---

### 3. 方式 B：本地手動啟動 (開發除錯)

#### 步驟 1：資料庫配置
1. 確認本機已安裝 Microsoft SQL Server。
2. 建立名稱為 `finalproject` 的資料庫。
3. 執行資料表建立與初始化種子資料腳本（或讓 Spring Boot `ddl-auto=update` 自動建立）。

#### 步驟 2：後端啟動 (Spring Boot)
1. 進入 `hotel-backend` 目錄。
2. 確認根目錄 `.env` 已正確配置（或直接在 `application.properties` 修改）。
3. 透過 Maven 啟動後端（預設監聽埠號 `8081`）：
   ```sh
   # Windows
   .\mvnw.cmd spring-boot:run
   # Linux / macOS
   ./mvnw spring-boot:run
   ```

#### 步驟 3：前端啟動 (Vue 3 + Vite)
1. 進入 `hotel-frontend` 目錄。
2. 安裝相依套件並啟動開發伺服器：
   ```sh
   npm install
   npm run dev
   ```
3. 前端開發伺服器啟動後（預設 `http://localhost:5173`），Vite 會自動將 `/api` 與 `/uploads` 請求代理至後端 `http://localhost:8081`。

---

## 自動化測試與 CI/CD

專案整合 GitHub Actions 實現全自動化測試與容器映像發布：

### 1. 本地執行單元測試
在提交程式碼前，可在本機快速執行驗證：
```sh
# 後端執行 34+ 個核心業務單元測試
cd hotel-backend
./mvnw test

# 前端執行編譯與打包檢查
cd ../hotel-frontend
npm run build
```

### 2. CI/CD 自動化流程
- **Pull Request / Push 觸發 (CI)**：
  - **後端**：安裝相依、執行單元測試，並完成 Spring Boot JAR 打包驗證。
  - **前端**：安裝相依套件並執行 Vite 生產環境編譯打包。
- **合併至 `main` 分支 (CD)**：
  - 前後端 CI 通過後，自動透過 Docker Buildx 建置後端映像檔，並推送至 GitHub Container Registry (`ghcr.io/josezhang0210/final_project-backend`)，自動標註 `:latest` 與 commit SHA。

---

## 設計文件與初始提案

<details>
<summary> 展開檢視設計圖與功能清單 </summary>
<br>

- [專案功能整理與系統架構分析報告](./doc/PROJECT_FUNCTION_REPORT.md)
- [功能清單詳細說明](./doc/function_List.md)
- 顧客端網站架構圖：`./doc/sitemap_client.drawio.svg`
- 員工端網站架構圖：`./doc/sitemap_employee.drawio.svg`

#### 初始提案方案
- 工廠方案：`./doc/proposal/工廠.drawio.svg`
- 飯店方案：`./doc/proposal/飯店.drawio.svg`
- 虛擬貨幣交易平台：`./doc/proposal/虛擬貨幣.jpg`
- 電子書商城：`./doc/proposal/書店.drawio.svg`

</details>

---

## 小組成員

感謝所有小組成員共同協作完成此專題：

<a href="https://github.com/josezhang0210/final_project/graphs/contributors">
  <img src="https://contrib.rocks/image?repo=josezhang0210/final_project" />
</a>

Made with [contrib.rocks](https://contrib.rocks).
