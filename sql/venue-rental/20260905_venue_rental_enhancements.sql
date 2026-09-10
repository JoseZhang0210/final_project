-- 在目前連線的目標資料庫執行，執行者須先確認部署環境。
SET XACT_ABORT ON; -- 執行錯誤時回滾交易。
SET QUOTED_IDENTIFIER ON; -- SQL Server 的篩選索引要求啟用識別碼引號規則。
SET ANSI_NULLS ON; -- 篩選索引使用標準空值比較。
SET ANSI_PADDING ON; -- 保留標準字串填補行為。
SET ANSI_WARNINGS ON; -- 不忽略資料轉換警告。
SET ARITHABORT ON; -- 算術錯誤需終止運算以符合索引要求。
SET CONCAT_NULL_YIELDS_NULL ON; -- 空值字串串接維持標準行為。
SET NUMERIC_ROUNDABORT OFF; -- 使用篩選索引要求的數值捨入設定。
BEGIN TRANSACTION; -- 欄位與索引一併提交。
IF COL_LENGTH('dbo.venue','image_url') IS NULL -- 已有欄位不重複新增。
ALTER TABLE dbo.venue ADD image_url NVARCHAR(1000) NULL; -- 場地單一圖片網址。
IF COL_LENGTH('dbo.rental_payment','merchant_trade_no') IS NULL -- 保留既有交易欄位。
ALTER TABLE dbo.rental_payment ADD merchant_trade_no VARCHAR(20) NULL; -- 特店交易識別。
IF COL_LENGTH('dbo.rental_payment','ecpay_trade_no') IS NULL -- 保留既有綠界識別。
ALTER TABLE dbo.rental_payment ADD ecpay_trade_no VARCHAR(20) NULL; -- 綠界交易識別。
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID('dbo.rental_payment') AND name='UX_rental_payment_merchant_trade_no') -- 索引不存在才建立。
EXEC('CREATE UNIQUE INDEX UX_rental_payment_merchant_trade_no ON dbo.rental_payment(merchant_trade_no) WHERE merchant_trade_no IS NOT NULL'); -- 動態批次可辨識本交易新增欄位。
IF NOT EXISTS (SELECT 1 FROM sys.indexes WHERE object_id=OBJECT_ID('dbo.rental_payment') AND name='UX_rental_payment_ecpay_trade_no') -- 綠界編號也不可重複綁定。
EXEC('CREATE UNIQUE INDEX UX_rental_payment_ecpay_trade_no ON dbo.rental_payment(ecpay_trade_no) WHERE ecpay_trade_no IS NOT NULL'); -- 舊空值資料不受限制。
COMMIT; -- 全部成功才提交。
