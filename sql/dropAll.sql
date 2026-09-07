USE [finalproject];
GO

-- 停用所有外部鍵約束，方便刪除
EXEC sp_MSforeachtable "ALTER TABLE ? NOCHECK CONSTRAINT all";

-- 刪除所有表格
EXEC sp_MSforeachtable "DROP TABLE ?";
GO
