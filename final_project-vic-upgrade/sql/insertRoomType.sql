USE finalproject;

-- 1. 修改欄位型態為 NVARCHAR（支援中文字元）
ALTER TABLE room_type ALTER COLUMN type_name NVARCHAR(100);
ALTER TABLE room_type ALTER COLUMN bed_type NVARCHAR(100);
ALTER TABLE room_type ALTER COLUMN description NVARCHAR(500);

-- 2. 清空原本的問號資料
DELETE FROM room_type;
DBCC CHECKIDENT ('room_type', RESEED, 0);

-- 3. 開啟 IDENTITY 手動寫入
SET IDENTITY_INSERT room_type ON;

-- 4. 重新寫入中文資料
INSERT INTO room_type
    (room_type_id, type_name, bed_type, description, price_per_night, capacity)
VALUES
    (1, N'標準海景雙人房', N'1張雙人床', N'含雙人早餐，擁有獨立海景陽台', 3500, 2),
    (2, N'標準山景雙人房', N'1張雙人床', N'含雙人早餐，享受靜謐山景', 3000, 2),
    (3, N'雅緻海景雙床房', N'2張單人床', N'含雙人早餐，海景客房，適合商務或好友', 3800, 2),
    (4, N'雅緻山景雙床房', N'2張單人床', N'含雙人早餐，山景客房，適合商務或好友', 3300, 2),
    (5, N'溫馨海景家庭房', N'2張雙人床', N'含四人早餐，家庭出遊首選海景房', 5800, 4),
    (6, N'溫馨山景家庭房', N'2張雙人床', N'含四人早餐，空間寬敞，綠意山景', 5200, 4),
    (7, N'行政海景尊榮套房', N'1張加大雙人床', N'含雙人早餐與行政酒廊權益，高樓層無敵海景', 8800, 2),
    (8, N'行政山景尊榮套房', N'1張加大雙人床', N'含雙人早餐與行政酒廊權益，高樓層環景山景', 8000, 2),
    (9, N'豪華全景海景四人套房', N'2張加大雙人床', N'含四人早餐，獨立會客廳，高樓層雙面海景', 13800, 4),
    (10, N'頂級海景皇家總統套房', N'1張特大雙人床', N'含專屬管家與豪華早餐，獨立露台與私人酒廊', 32000, 2);

-- 5. 關閉 IDENTITY 並設定下一次新增從 11 開始
SET IDENTITY_INSERT room_type OFF;
DBCC CHECKIDENT ('room_type', RESEED, 10);