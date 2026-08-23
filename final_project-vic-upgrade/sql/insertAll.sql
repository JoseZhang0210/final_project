USE [finalproject];
GO

/* =========================================================
   1. department
   ========================================================= */
INSERT INTO department (department_name)
VALUES
(N'櫃檯部'),
(N'客房部'),
(N'餐飲部'),
(N'行政部');
GO


/* =========================================================
   2. account
   =========================================================
   密碼皆以 BCrypt 測試值表示
   原始測試密碼可統一設定為：123456
   ========================================================= */
INSERT INTO account (username, password, status)
VALUES
    -- 1 ~ 2: 行政管理部 (Admin)
    ('admin01',       '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('admin02',       '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),

    -- 3 ~ 10: 櫃檯/前台部 (Front Desk)
    ('frontdesk01',   '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('frontdesk02',   '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('frontdesk03',   '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('frontdesk04',   '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('frontdesk05',   '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('frontdesk06',   '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('frontdesk07',   '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('frontdesk08',   '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),

    -- 11 ~ 28: 客房/房務與維修部 (Housekeeping)
    ('housekeeping01', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping02', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping03', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping04', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping05', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping06', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping07', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping08', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping09', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping10', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping11', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping12', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping13', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping14', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping15', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping16', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping17', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('housekeeping18', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),

    -- 29 ~ 45: 餐飲部 (F&B)
    ('fnb01',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('fnb02',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('fnb03',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('fnb04',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('fnb05',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('fnb06',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('fnb07',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('fnb08',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('fnb09',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('fnb10',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('fnb11',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('fnb12',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('fnb13',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('fnb14',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('fnb15',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('fnb16',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('fnb17',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1');
GO


/* =========================================================
   3. permission
   ========================================================= */
INSERT INTO permission ( permission_code, permission_name)
VALUES
( N'ROOM_MANAGE', N'房間管理'),
( N'BOOKING_MANAGE', N'訂房管理'),
( N'RESTAURANT_MANAGE', N'餐廳管理'),
( N'MEMBER_MANAGE', N'會員管理'),
( N'ORDER_MANAGE', N'訂單管理');
GO


/* =========================================================
   4. employee
   ========================================================= */
INSERT INTO employee (department_id, account_id, position, is_admin)
VALUES
    -- 行政管理部 (department_id = 4, account_id 1~2)
    (4, 1, N'總經理', 1),         -- Emp 1
    (4, 2, N'行政人資主管', 0),   -- Emp 2

    -- 櫃檯/前台部 (department_id = 1, account_id 3~10)
    (1, 3, N'櫃檯主管', 0),       -- Emp 3
    (1, 4, N'櫃檯專員(早班)', 0), -- Emp 4
    (1, 5, N'櫃檯專員(早班)', 0), -- Emp 5
    (1, 6, N'櫃檯專員(晚班)', 0), -- Emp 6
    (1, 7, N'櫃檯專員(晚班)', 0), -- Emp 7
    (1, 8, N'櫃檯專員(大夜)', 0), -- Emp 8
    (1, 9, N'禮賓接待員', 0),     -- Emp 9
    (1, 10, N'車隊接送員', 0),    -- Emp 10

    -- 客房/房務與維修部 (department_id = 2, account_id 11~28)
    (2, 11, N'房務主管', 0),      -- Emp 11
    (2, 12, N'房務領班', 0),      -- Emp 12
    (2, 13, N'房務專員', 0),      -- Emp 13
    (2, 14, N'房務專員', 0),      -- Emp 14
    (2, 15, N'房務專員', 0),      -- Emp 15
    (2, 16, N'房務專員', 0),      -- Emp 16
    (2, 17, N'房務專員', 0),      -- Emp 17
    (2, 18, N'房務專員', 0),      -- Emp 18
    (2, 19, N'房務專員', 0),      -- Emp 19
    (2, 20, N'房務專員', 0),      -- Emp 20
    (2, 21, N'房務專員', 0),      -- Emp 21
    (2, 22, N'房務專員', 0),      -- Emp 22
    (2, 23, N'房務專員', 0),      -- Emp 23
    (2, 24, N'房務專員', 0),      -- Emp 24
    (2, 25, N'公設清潔員', 0),    -- Emp 25
    (2, 26, N'布巾洗滌員', 0),    -- Emp 26
    (2, 27, N'機電維修員', 0),    -- Emp 27
    (2, 28, N'水電維修員', 0),    -- Emp 28

    -- 餐飲部 (department_id = 3, account_id 29~45)
    (3, 29, N'餐飲主管', 0),      -- Emp 29
    (3, 30, N'主廚', 0),          -- Emp 30
    (3, 31, N'副廚', 0),          -- Emp 31
    (3, 32, N'砧板/冷盤廚師', 0),-- Emp 32
    (3, 33, N'熱炒/西餐廚師', 0),-- Emp 33
    (3, 34, N'點心/烘焙師', 0),  -- Emp 34
    (3, 35, N'餐飲組長', 0),      -- Emp 35
    (3, 36, N'餐飲服務員', 0),    -- Emp 36
    (3, 37, N'餐飲服務員', 0),    -- Emp 37
    (3, 38, N'餐飲服務員', 0),    -- Emp 38
    (3, 39, N'餐飲服務員', 0),    -- Emp 39
    (3, 40, N'餐飲服務員', 0),    -- Emp 40
    (3, 41, N'餐飲服務員', 0),    -- Emp 41
    (3, 42, N'吧檯調酒師', 0),    -- Emp 42
    (3, 43, N'吧檯助理', 0),      -- Emp 43
    (3, 44, N'洗碗員', 0),        -- Emp 44
    (3, 45, N'備料清潔員', 0);    -- Emp 45
GO


/* =========================================================
   5. employee_permission
   注意：
   目前你的 FK 是 employee.account_id
   所以下面的 employee_id 使用 1、2、3、4
   ========================================================= */
INSERT INTO employee_permission (employee_id)
VALUES
    -- Emp 1: 總經理 / 管理員 (擁有全部 5 項權限)
    (1, 1), -- 房間管理
    (2, 1), -- 訂房管理
    (3, 1), -- 餐廳管理
    (4, 1), -- 會員管理
    (5, 1), -- 訂單管理

    -- Emp 2: 櫃檯主管 (房間、訂房、會員、訂單)
    (1, 2),
    (2, 2),
    (4, 2),
    (5, 2),

    -- Emp 3: 櫃檯專員 (房間、訂房)
    (1, 3),
    (2, 3),

    -- Emp 4: 房務人員 / 維修員 (僅房間管理)
    (1, 4),

    -- Emp 5: 餐飲部主管 (餐廳、訂單)
    (3, 5),
    (5, 5),

    -- Emp 6: 餐飲部服務員 (僅餐廳管理)
    (3, 6);
GO


/* =========================================================
   6. member
   ========================================================= */
INSERT INTO member (account_id)
VALUES
(5),
(6),
(7);
GO


/* =========================================================
   7. profile
   ========================================================= */
INSERT INTO profile
    (account_id, name, email, phone, zipcode, city,
     district, address, created_at, birthday, gender, updated_at)
VALUES
(5, '王小明', 'ming@example.com', '0912345678',
 '320', '桃園市', '中壢區', '中央西路一段100號',
 '2026-08-01 10:00:00', '1998-05-12', '男',
 '2026-08-01 10:00:00'),

(6, '陳小華', 'hua@example.com', '0923456789',
 '320', '桃園市', '中壢區', '中美路200號',
 '2026-08-02 11:00:00', '1995-08-20', '女',
 '2026-08-02 11:00:00'),

(7, '林大偉', 'david@example.com', '0934567890',
 '330', '桃園市', '桃園區', '中正路300號',
 '2026-08-03 14:00:00', '2000-03-15', '男',
 '2026-08-03 14:00:00');
GO


/* =========================================================
   8. image
   ========================================================= */
INSERT INTO [dbo].[image] 
    ([path], [image_description], [room_type_id]) 
VALUES
    (N'/uploads/images/room/roomtype.1.jpg', N'海景標準雙人房', 1),
    (N'/uploads/images/room/roomtype.2.jpg', N'山景標準雙人房', 2),
    (N'/uploads/images/room/roomtype.3.jpg', N'海景雅緻雙床房', 3),
    (N'/uploads/images/room/roomtype.4.jpg', N'山景雅緻雙床房', 4),
    (N'/uploads/images/room/roomtype.5.jpg', N'海景溫馨家庭房', 5),
    (N'/uploads/images/room/roomtype.6.jpg', N'山景溫馨家庭房', 6),
    (N'/uploads/images/room/roomtype.7.jpg', N'海景行政尊榮套房', 7),
    (N'/uploads/images/room/roomtype.8.jpg', N'山景行政尊榮套房', 8),
    (N'/uploads/images/room/roomtype.9.jpg', N'海景豪華全景四人套房', 9),
    (N'/uploads/images/room/roomtype.10.jpg', N'海景頂級皇家總統套房', 10);
GO


/* =========================================================
   9. room_type
   ========================================================= */
INSERT INTO room_type
    (type_name, bed_type, capacity, room_description, price_per_night)
VALUES
    (N'標準海景雙人房', N'1張雙人床', 2, N'含雙人早餐，擁有獨立海景陽台', 3500),
    (N'標準山景雙人房', N'1張雙人床', 2, N'含雙人早餐，享受靜謐山景', 3000),
    (N'雅緻海景雙床房', N'2張單人床', 2, N'含雙人早餐，海景客房，適合商務或好友', 3800),
    (N'雅緻山景雙床房', N'2張單人床', 2, N'含雙人早餐，山景客房，適合商務或好友', 3300),
    (N'溫馨海景家庭房', N'2張雙人床', 4, N'含四人早餐，家庭出遊首選海景房', 5800),
    (N'溫馨山景家庭房', N'2張雙人床', 4, N'含四人早餐，空間寬敞，綠意山景', 5200),
    (N'行政海景尊榮套房', N'1張加大雙人床', 2, N'含雙人早餐與行政酒廊權益，高樓層無敵海景', 8800),
    (N'行政山景尊榮套房', N'1張加大雙人床', 2, N'含雙人早餐與行政酒廊權益，高樓層環景山景', 8000),
    (N'豪華全景海景四人套房', N'2張加大雙人床', 4, N'含四人早餐，獨立會客廳，高樓層雙面海景', 13800),
    (N'頂級海景皇家總統套房', N'1張特大雙人床', 2, N'含專屬管家與豪華早餐，獨立露台與私人酒廊', 32000);
GO


/* =========================================================
   10. room
   ========================================================= */
INSERT INTO room
    (room_number, room_type_id, floor, room_status)
VALUES
    -- ==================== 5 樓 (24間：標準與雅緻房型) ====================
    -- 1 區 (房型 1, 3)
    (10501, 1, 5, N'可預訂'),
    (10502, 1, 5, N'可預訂'),
    (10503, 1, 5, N'可預訂'),
    (10504, 1, 5, N'可預訂'),
    (10505, 3, 5, N'可預訂'),
    (10506, 3, 5, N'可預訂'),
    (10507, 3, 5, N'可預訂'),
    (10508, 3, 5, N'可預訂'),
    (10509, 1, 5, N'可預訂'),
    (10510, 1, 5, N'可預訂'),
    (10511, 3, 5, N'可預訂'),
    (10512, 3, 5, N'可預訂'),
    -- 2 區 (房型 2, 4)
    (20501, 2, 5, N'可預訂'),
    (20502, 2, 5, N'可預訂'),
    (20503, 2, 5, N'可預訂'),
    (20504, 2, 5, N'可預訂'),
    (20505, 4, 5, N'可預訂'),
    (20506, 4, 5, N'可預訂'),
    (20507, 4, 5, N'可預訂'),
    (20508, 4, 5, N'可預訂'),
    (20509, 2, 5, N'可預訂'),
    (20510, 2, 5, N'可預訂'),
    (20511, 4, 5, N'可預訂'),
    (20512, 4, 5, N'可預訂'),

    -- ==================== 6 樓 (24間：標準與雅緻房型) ====================
    -- 1 區 (房型 1, 3)
    (10601, 1, 6, N'可預訂'),
    (10602, 1, 6, N'可預訂'),
    (10603, 1, 6, N'可預訂'),
    (10604, 1, 6, N'可預訂'),
    (10605, 3, 6, N'可預訂'),
    (10606, 3, 6, N'可預訂'),
    (10607, 3, 6, N'可預訂'),
    (10608, 3, 6, N'可預訂'),
    (10609, 1, 6, N'可預訂'),
    (10610, 1, 6, N'可預訂'),
    (10611, 3, 6, N'可預訂'),
    (10612, 3, 6, N'可預訂'),
    -- 2 區 (房型 2, 4)
    (20601, 2, 6, N'可預訂'),
    (20602, 2, 6, N'可預訂'),
    (20603, 2, 6, N'可預訂'),
    (20604, 2, 6, N'可預訂'),
    (20605, 4, 6, N'可預訂'),
    (20606, 4, 6, N'可預訂'),
    (20607, 4, 6, N'可預訂'),
    (20608, 4, 6, N'可預訂'),
    (20609, 2, 6, N'可預訂'),
    (20610, 2, 6, N'可預訂'),
    (20611, 4, 6, N'可預訂'),
    (20612, 4, 6, N'可預訂'),

    -- ==================== 7 樓 (24間：溫馨家庭房型) ====================
    -- 1 區 (房型 5)
    (10701, 5, 7, N'可預訂'),
    (10702, 5, 7, N'可預訂'),
    (10703, 5, 7, N'可預訂'),
    (10704, 5, 7, N'可預訂'),
    (10705, 5, 7, N'可預訂'),
    (10706, 5, 7, N'可預訂'),
    (10707, 5, 7, N'可預訂'),
    (10708, 5, 7, N'可預訂'),
    (10709, 5, 7, N'可預訂'),
    (10710, 5, 7, N'可預訂'),
    (10711, 5, 7, N'可預訂'),
    (10712, 5, 7, N'可預訂'),
    -- 2 區 (房型 6)
    (20701, 6, 7, N'可預訂'),
    (20702, 6, 7, N'可預訂'),
    (20703, 6, 7, N'可預訂'),
    (20704, 6, 7, N'可預訂'),
    (20705, 6, 7, N'可預訂'),
    (20706, 6, 7, N'可預訂'),
    (20707, 6, 7, N'可預訂'),
    (20708, 6, 7, N'可預訂'),
    (20709, 6, 7, N'可預訂'),
    (20710, 6, 7, N'可預訂'),
    (20711, 6, 7, N'可預訂'),
    (20712, 6, 7, N'可預訂'),

    -- ==================== 8 樓 (24間：一般套房綜合區) ====================
    -- 1 區 (房型 1, 3, 5)
    (10801, 1, 8, N'可預訂'),
    (10802, 1, 8, N'可預訂'),
    (10803, 1, 8, N'可預訂'),
    (10804, 1, 8, N'可預訂'),
    (10805, 3, 8, N'可預訂'),
    (10806, 3, 8, N'可預訂'),
    (10807, 3, 8, N'可預訂'),
    (10808, 3, 8, N'可預訂'),
    (10809, 5, 8, N'可預訂'),
    (10810, 5, 8, N'可預訂'),
    (10811, 5, 8, N'可預訂'),
    (10812, 5, 8, N'可預訂'),
    -- 2 區 (房型 2, 4, 6)
    (20801, 2, 8, N'可預訂'),
    (20802, 2, 8, N'可預訂'),
    (20803, 2, 8, N'可預訂'),
    (20804, 2, 8, N'可預訂'),
    (20805, 4, 8, N'可預訂'),
    (20806, 4, 8, N'可預訂'),
    (20807, 4, 8, N'可預訂'),
    (20808, 4, 8, N'可預訂'),
    (20809, 6, 8, N'可預訂'),
    (20810, 6, 8, N'可預訂'),
    (20811, 6, 8, N'可預訂'),
    (20812, 6, 8, N'可預訂'),

    -- ==================== 9 樓 (12間：行政尊榮套房) ====================
    -- 1 區 (房型 7)
    (10901, 7, 9, N'可預訂'),
    (10902, 7, 9, N'可預訂'),
    (10903, 7, 9, N'可預訂'),
    (10904, 7, 9, N'可預訂'),
    (10905, 7, 9, N'可預訂'),
    (10906, 7, 9, N'可預訂'),
    -- 2 區 (房型 8)
    (20901, 8, 9, N'可預訂'),
    (20902, 8, 9, N'可預訂'),
    (20903, 8, 9, N'可預訂'),
    (20904, 8, 9, N'可預訂'),
    (20905, 8, 9, N'可預訂'),
    (20906, 8, 9, N'可預訂'),

    -- ==================== 10 樓 (12間：頂級與豪華套房) ====================
    -- 3 區 (房型 9, 10)
    (31001, 9, 10, N'可預訂'),
    (31002, 9, 10, N'可預訂'),
    (31003, 9, 10, N'可預訂'),
    (31004, 9, 10, N'可預訂'),
    (31005, 9, 10, N'可預訂'),
    (31006, 9, 10, N'可預訂'),
    (31007, 9, 10, N'可預訂'),
    (31008, 9, 10, N'可預訂'),
    (31009, 9, 10, N'可預訂'),
    (31010, 9, 10, N'可預訂'),
    (31011, 10, 10, N'可預訂'),
    (31012, 10, 10, N'可預訂');
GO


/* =========================================================
   11. category
   ========================================================= */
INSERT INTO category
    (category_id, category_name, parent_category)
VALUES
(1, N'餐飲', NULL),
(2, N'早餐', 1),
(3, N'晚餐', 1),
(4, N'客房用品', NULL);
GO


/* =========================================================
   12. product
   ========================================================= */
INSERT INTO product
    (product_id, product_name, category_id, description,
     price, stock, image_id, status)
VALUES
(1, N'自助早餐券', 2, N'飯店早餐自助餐券', 450, 100, 4, N'上架'),
(2, N'精緻晚餐券', 3, N'飯店餐廳晚餐券', 800, 80, 5, N'上架'),
(3, N'礦泉水', 4, N'客房瓶裝水', 50, 200, NULL, N'上架'),
(4, N'迎賓水果', 4, N'客房迎賓水果', 300, 50, NULL, N'上架');
GO


/* =========================================================
   13. payment
   ========================================================= */
INSERT INTO payment
    (payment_id, payment_method, payment_time,
     total_price, payment_status, member_id)
VALUES
(1, N'信用卡', '2026-08-10 15:30:00', 7600, N'已付款', 1),
(2, N'LINE PAY', '2026-08-11 12:20:00', 3200, N'已付款', 2),
(3, N'信用卡', '2026-08-12 18:10:00', 5200, N'待付款', 3),
(4, N'現金', '2026-08-13 19:00:00', 450, N'已付款', 1);
GO


/* =========================================================
   14. booking_order
   ========================================================= */
INSERT INTO booking_order
    (booking_order_id, member_id, booking_total_price,
     order_status, created_at, payment_id)
VALUES
(1, 1, 7600, N'已確認', '2026-08-10 15:25:00', 1),
(2, 2, 3200, N'已確認', '2026-08-11 12:15:00', 2),
(3, 3, 5200, N'待付款', '2026-08-12 18:05:00', 3);
GO


/* =========================================================
   15. booking
   ========================================================= */
INSERT INTO booking
    (booking_order_id, check_in_date, check_out_date,
     guest_num, booking_status, room_id, room_type_id)
VALUES
(1, '2026-08-20 15:00:00', '2026-08-22 11:00:00',
 2, N'已確認', 2, 1),

(2, '2026-08-25 15:00:00', '2026-08-26 11:00:00',
 2, N'已確認', 3, 2),

(3, '2026-09-01 15:00:00', '2026-09-03 11:00:00',
 4, N'待付款', NULL, 3);
GO


/* =========================================================
   16. restaurant
   ========================================================= */
INSERT INTO restaurant
    (restaurant_name, address, phone, capacity, description)
VALUES
(N'雲澄自助餐廳', N'桃園市中壢區中央西路100號',
 '03-1234567', 120, N'提供中西式自助餐'),

(N'景觀咖啡廳', N'桃園市中壢區中央西路100號',
 '03-1234568', 60, N'提供咖啡及下午茶');
GO


/* =========================================================
   17. restaurant_time
   ========================================================= */
INSERT INTO restaurant_time
    (restaurant_id, meal_type, open_time, close_time)
VALUES
(1, N'早餐', '07:00', '10:00'),
(1, N'午餐', '11:30', '14:00'),
(1, N'晚餐', '17:30', '21:00'),
(2, N'下午茶', '14:00', '17:00');
GO


/* =========================================================
   18. reservation
   ========================================================= */
INSERT INTO reservation
    (member_id, contact_name, contact_phone,
     restaurant_id, reservation_date, time_id,
     people_count, status, create_time)
VALUES
(1, N'王小明', '0912345678',
 1, '2026-08-21', 3,
 2, N'已訂位', '2026-08-15 10:00:00'),

(2, N'陳小華', '0923456789',
 1, '2026-08-22', 1,
 3, N'已訂位', '2026-08-16 11:30:00'),

(NULL, N'張先生', '0945678901',
 2, '2026-08-23', 4,
 2, N'已訂位', '2026-08-17 15:00:00');
GO


/* =========================================================
   19. venue
   ========================================================= */
INSERT INTO venue
    (venue_id, venue_name, capacity, price_per_day, venue_status)
VALUES
(1, N'宴會廳 A', 300, 50000, N'可預約'),
(2, N'會議室 A', 50, 12000, N'可預約'),
(3, N'會議室 B', 30, 8000, N'可預約');
GO


/* =========================================================
   20. rental
   ========================================================= */
INSERT INTO rental
    (rental_id, venue_id, member_id, event_name,
     rental_date, guest_count, payment_id, rental_status)
VALUES
(1, 1, 1, N'公司尾牙',
 '2026-12-20 18:00:00', 200, 1, N'已確認'),

(2, 2, 2, N'公司會議',
 '2026-09-15 09:00:00', 40, 2, N'已確認');
GO


/* =========================================================
   21. order
   ========================================================= */
INSERT INTO [order]
    (order_id, member_id, order_date, is_ordered, payment_id)
VALUES
(1, 1, '2026-08-10 15:20:00', 1, 1),
(2, 2, '2026-08-11 12:10:00', 1, 2),
(3, 1, '2026-08-13 18:50:00', 1, 4);
GO


/* =========================================================
   22. order_item
   ========================================================= */
INSERT INTO order_item
    (order_id, product_id, quantity)
VALUES
(1, 1, 4),
(1, 2, 1),
(2, 1, 2),
(3, 1, 1);
GO


/* =========================================================
   23. room_task 依照房務員工id （11~28） 給予隨機任務 對應人員
   ========================================================= */
-- INSERT INTO room_task (
--     room_id, 
--     employee_id, 
--     priority,       
--     task_type,      
--     task_status,
--     remark, 
--     created_at, 
--     completed_at
-- )
-- SELECT 
--     room_id,
--     employee_id,
--     priority,       
--     task_type,      
--     task_status,
    
--     -- 依據任務類型給予對應的備註說明
--     CASE 
--         WHEN rand_remark_flag < 30 THEN NULL
--         WHEN task_type = N'設備維修' THEN 
--             CASE rand_remark_idx
--                 WHEN 0 THEN N'馬桶堵塞'
--                 WHEN 1 THEN N'冷氣不冷'
--                 WHEN 2 THEN N'電視無法開機'
--                 ELSE N'蓮蓬頭漏水'
--             END
--         WHEN task_type = N'退房清潔' THEN 
--             CASE (rand_remark_idx % 2)
--                 WHEN 0 THEN N'退房深度清潔'
--                 ELSE N'窗簾脫軌'
--             END
--         WHEN task_type = N'補充備品' THEN 
--             CASE (rand_remark_idx % 2)
--                 WHEN 0 THEN N'補充沐浴乳'
--                 ELSE N'換洗毛巾備品'
--             END
--         ELSE N'續住日常整理'
--     END AS remark,
    
--     created_at,
    
--     -- 完成時間邏輯
--     CASE 
--         WHEN task_status = N'已完成' THEN DATEADD(MINUTE, rand_complete_min, created_at)
--         ELSE NULL 
--     END AS completed_at

-- FROM (
--     SELECT TOP (60)
--         -- 120 間房間隨機挑選
--         (ABS(CHECKSUM(NEWID())) % 120) + 1 AS room_id,
        
--         -- 1. 任務類型判定 (15% 維修單，85% 清潔備品單)
--         -- 2. 員工ID精準鎖定：
--         --    - 維修單 -> 機電/水電維修員 (Emp 27, 28)
--         --    - 清潔單 -> 12 位房務專員 (Emp 13 ~ 24)
--         CASE 
--             WHEN rand_type_flag < 15 THEN (ABS(CHECKSUM(NEWID())) % 2) + 27
--             ELSE (ABS(CHECKSUM(NEWID())) % 12) + 13
--         END AS employee_id,

--         CASE 
--             WHEN rand_type_flag < 15 THEN N'設備維修'
--             ELSE 
--                 CASE (ABS(CHECKSUM(NEWID())) % 3)
--                     WHEN 0 THEN N'退房清潔'
--                     WHEN 1 THEN N'續住清潔'
--                     ELSE N'補充備品'
--                 END
--         END AS task_type,

--         -- 優先權 (一般、重要、緊急)
--         CASE (ABS(CHECKSUM(NEWID())) % 3)
--             WHEN 0 THEN N'一般'
--             WHEN 1 THEN N'重要'
--             ELSE N'緊急'
--         END AS priority,
        
--         -- 任務狀態 (待處理、進行中、已完成、已取消)
--         CASE (ABS(CHECKSUM(NEWID())) % 4)
--             WHEN 0 THEN N'待處理'
--             WHEN 1 THEN N'進行中'
--             WHEN 2 THEN N'已完成'
--             ELSE N'已取消'
--         END AS task_status,
        
--         -- 創建時間 (近 7 天內)
--         DATEADD(MINUTE, - (ABS(CHECKSUM(NEWID())) % 10080), GETDATE()) AS created_at,
        
--         -- 亂數輔助欄位
--         ABS(CHECKSUM(NEWID())) % 100 AS rand_type_flag,
--         ABS(CHECKSUM(NEWID())) % 100 AS rand_remark_flag,
--         ABS(CHECKSUM(NEWID())) % 4 AS rand_remark_idx,
--         (ABS(CHECKSUM(NEWID())) % 120) + 30 AS rand_complete_min

--     FROM sys.all_objects a
--     CROSS JOIN sys.all_objects b
-- ) AS RawTasks;