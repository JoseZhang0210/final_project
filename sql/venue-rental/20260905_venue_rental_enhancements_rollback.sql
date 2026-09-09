-- 在目前連線的目標資料庫回退，執行前須另行核准並備份新欄位資料。
SET XACT_ABORT ON; -- 錯誤時不留下部分回退。
BEGIN TRANSACTION; -- 索引與欄位一併回退。
DROP INDEX IF EXISTS UX_rental_payment_merchant_trade_no ON dbo.rental_payment; -- 只移除本次特店編號索引。
DROP INDEX IF EXISTS UX_rental_payment_ecpay_trade_no ON dbo.rental_payment; -- 只移除本次綠界編號索引。
IF COL_LENGTH('dbo.venue','image_url') IS NOT NULL -- 避免重複回退出錯。
ALTER TABLE dbo.venue DROP COLUMN image_url; -- 只移除新增圖片欄位，不刪除場地。
IF COL_LENGTH('dbo.rental_payment','merchant_trade_no') IS NOT NULL -- 僅處理存在的新增欄位。
ALTER TABLE dbo.rental_payment DROP COLUMN merchant_trade_no; -- 此步會失去特店編號，需先備份。
IF COL_LENGTH('dbo.rental_payment','ecpay_trade_no') IS NOT NULL -- 僅處理存在的新增欄位。
ALTER TABLE dbo.rental_payment DROP COLUMN ecpay_trade_no; -- 此步會失去綠界編號，需先備份。
COMMIT; -- 不刪除任何資料列或資料表。
