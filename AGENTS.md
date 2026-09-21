# AGENTS.md - AI Coding Agent Development Guidelines

These rules apply to AI coding agents working in this repository.

## 核心開發規範

1. 先讀懂、再修改。
   - 修改前先閱讀相關既有實作與實際流程。
   - 不可僅依檔名猜測架構。

2. 優先重用既有實作。
   - 優先使用既有 Service、Repository、API、Helper、Component 與專案既有模式。
   - 不重複實作已存在功能。

3. 優先修正 Root Cause。
   - 先診斷共同根因，再處理表面症狀。
   - 修改共用邏輯前，確認相關呼叫端。

4. 遵守 YAGNI 與最小變更原則。
   - 僅實作任務明確要求的內容。
   - 不新增未要求的功能、抽象層、設定或預留架構。

5. 不任意新增相依套件。
   - 未經明確要求，不新增 Maven、npm 或其他第三方 Dependency。

6. 最小化 Changed Files。
   - 僅修改任務真正需要的檔案。
   - 不進行無關格式化、重新命名或重構。

7. 尊重團隊模組邊界。
   - 不任意修改或重構其他組員負責的模組。
   - 若任務確實需要跨模組修改，必須先說明原因。

## Security 與資料完整性

不得為了縮短程式碼而省略或弱化：

- Authentication
- Authorization
- Ownership Check
- Input Validation
- Payment Verification
- Transaction Boundary
- Idempotency
- Data Integrity Error Handling

禁止硬編碼或暴露：

- 帳號
- 密碼
- Token
- API Key
- Private Key
- Production Secret

## Git 分支與防呆規範

修改前：

1. 同步最新 `origin/main`。
2. 確認目前工作的 Branch 是建立於最新 `main`。
3. 使用獨立 Feature / Fix Branch。
4. 禁止直接修改、Commit 或 Merge 到 `main`。

建立 PR 前必須實際執行：

```bash
git status --short
git diff --name-only origin/main...HEAD
git diff --stat origin/main...HEAD
git diff --check
```

建立 PR 時：

- PR 標題必須使用繁體中文
- PR 說明必須使用繁體中文
- 技術名詞、檔名、指令可保留英文
- 不要使用簡體中文
