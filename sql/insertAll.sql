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
    ('fnb17',          '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),

 -- 46 ~ 105: 顧客會員 (Customer / Guest)
    ('customer01', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer02', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer03', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer04', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer05', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer06', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer07', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer08', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer09', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer10', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer11', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer12', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer13', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer14', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer15', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer16', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer17', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer18', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer19', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer20', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer21', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer22', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer23', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer24', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer25', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer26', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer27', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer28', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer29', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer30', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer31', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer32', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer33', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer34', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer35', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer36', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer37', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer38', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer39', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer40', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer41', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer42', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer43', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer44', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer45', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer46', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer47', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer48', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer49', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer50', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer51', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer52', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer53', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer54', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer55', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer56', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer57', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer58', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer59', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
    ('customer60', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1');


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
INSERT INTO employee (department_id, account_id, position)
VALUES
    -- 行政管理部 (department_id = 4, account_id 1~2)
    (4, 1, N'總經理'),         -- Emp 1
    (4, 2, N'行政人資主管'),   -- Emp 2

    -- 櫃檯/前台部 (department_id = 1, account_id 3~10)
    (1, 3, N'櫃檯主管'),       -- Emp 3
    (1, 4, N'櫃檯專員(早班)'), -- Emp 4
    (1, 5, N'櫃檯專員(早班)'), -- Emp 5
    (1, 6, N'櫃檯專員(晚班)'), -- Emp 6
    (1, 7, N'櫃檯專員(晚班)'), -- Emp 7
    (1, 8, N'櫃檯專員(大夜)'), -- Emp 8
    (1, 9, N'禮賓接待員'),     -- Emp 9
    (1, 10, N'車隊接送員'),    -- Emp 10

    -- 客房/房務與維修部 (department_id = 2, account_id 11~28)
    (2, 11, N'房務主管'),      -- Emp 11
    (2, 12, N'房務領班'),      -- Emp 12
    (2, 13, N'房務專員'),      -- Emp 13
    (2, 14, N'房務專員'),      -- Emp 14
    (2, 15, N'房務專員'),      -- Emp 15
    (2, 16, N'房務專員'),      -- Emp 16
    (2, 17, N'房務專員'),      -- Emp 17
    (2, 18, N'房務專員'),      -- Emp 18
    (2, 19, N'房務專員'),      -- Emp 19
    (2, 20, N'房務專員'),      -- Emp 20
    (2, 21, N'房務專員'),      -- Emp 21
    (2, 22, N'房務專員'),      -- Emp 22
    (2, 23, N'房務專員'),      -- Emp 23
    (2, 24, N'房務專員'),      -- Emp 24
    (2, 25, N'公設清潔員'),    -- Emp 25
    (2, 26, N'布巾洗滌員'),    -- Emp 26
    (2, 27, N'機電維修員'),    -- Emp 27
    (2, 28, N'水電維修員'),    -- Emp 28

    -- 餐飲部 (department_id = 3, account_id 29~45)
    (3, 29, N'餐飲主管'),      -- Emp 29
    (3, 30, N'主廚'),          -- Emp 30
    (3, 31, N'副廚'),          -- Emp 31
    (3, 32, N'砧板/冷盤廚師'),-- Emp 32
    (3, 33, N'熱炒/西餐廚師'),-- Emp 33
    (3, 34, N'點心/烘焙師'),  -- Emp 34
    (3, 35, N'餐飲組長'),      -- Emp 35
    (3, 36, N'餐飲服務員'),    -- Emp 36
    (3, 37, N'餐飲服務員'),    -- Emp 37
    (3, 38, N'餐飲服務員'),    -- Emp 38
    (3, 39, N'餐飲服務員'),    -- Emp 39
    (3, 40, N'餐飲服務員'),    -- Emp 40
    (3, 41, N'餐飲服務員'),    -- Emp 41
    (3, 42, N'吧檯調酒師'),    -- Emp 42
    (3, 43, N'吧檯助理'),      -- Emp 43
    (3, 44, N'洗碗員'),        -- Emp 44
    (3, 45, N'備料清潔員');    -- Emp 45
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
INSERT INTO member (account_id) VALUES
(46),
(47),
(48),
(49),
(50),
(51),
(52),
(53),
(54),
(55),
(56),
(57),
(58),
(59),
(60),
(61),
(62),
(63),
(64),
(65),
(66),
(67),
(68),
(69),
(70),
(71),
(72),
(73),
(74),
(75),
(76),
(77),
(78),
(79),
(80),
(81),
(82),
(83),
(84),
(85),
(86),
(87),
(88),
(89),
(90),
(91),
(92),
(93),
(94),
(95),
(96),
(97),
(98),
(99),
(100),
(101),
(102),
(103),
(104),
(105);
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
    ( payment_method)
VALUES
( N'Apple PAY'),
( N'LINE PAY'),
( N'信用卡'),
( N'現金'),
( N'銀行轉帳');
GO


/* =========================================================
   14. booking_order
   ========================================================= */
INSERT INTO booking_order (member_id, total_price, order_status, created_at, payment_id) VALUES
(12, 6500, N'訂單完成', '2026-08-17 09:15:20', 3),
(45, 9300, N'訂單完成', '2026-08-17 14:22:05', 1),
(8, 11000, N'訂單取消', '2026-08-17 18:40:12', 4),
(53, 7100, N'訂單完成', '2026-08-18 10:05:30', 2),
(27, 14600, N'訂單完成', '2026-08-18 13:50:00', 5),
(60, 6800, N'訂單取消', '2026-08-18 21:12:45', 3),
(1, 8800, N'訂單完成', '2026-08-19 08:30:10', 1),
(39, 12300, N'訂單完成', '2026-08-19 11:45:22', 2),
(18, 6500, N'訂單完成', '2026-08-19 16:20:00', 4),
(32, 17600, N'訂單取消', '2026-08-19 20:05:15', 5),
(5, 9600, N'訂單完成', '2026-08-20 09:10:40', 3),
(58, 7300, N'訂單完成', '2026-08-20 12:35:00', 2),
(21, 11800, N'訂單完成', '2026-08-20 15:50:30', 1),
(44, 6300, N'訂單取消', '2026-08-20 19:15:10', 4),
(10, 14000, N'訂單完成', '2026-08-21 08:05:25', 5),
(56, 8800, N'訂單完成', '2026-08-21 11:20:18', 2),
(30, 10400, N'訂單完成', '2026-08-21 14:45:50', 3),
(3, 6800, N'訂單取消', '2026-08-21 18:30:00', 1),
(41, 16800, N'訂單完成', '2026-08-22 09:40:15', 4),
(15, 9100, N'訂單完成', '2026-08-22 13:10:22', 5),
(49, 6500, N'訂單完成', '2026-08-22 17:25:00', 2),
(14, 12600, N'訂單取消', '2026-08-22 21:00:40', 3),
(35, 7100, N'訂單完成', '2026-08-23 08:15:12', 1),
(59, 14600, N'訂單完成', '2026-08-23 10:50:35', 4),
(25, 8800, N'訂單完成', '2026-08-23 12:20:00', 2),
(7, 11000, N'訂單取消', '2026-08-23 14:05:45', 5),
(48, 6800, N'訂單完成', '2026-08-23 15:40:10', 3),
(23, 9600, N'訂單完成', '2026-08-23 16:30:25', 1),
(52, 17600, N'訂單完成', '2026-08-23 17:15:00', 2),
(19, 7300, N'訂單完成', '2026-08-23 18:00:50', 4);
GO


/* =========================================================
   15. booking
   ========================================================= */
INSERT INTO booking (booking_order_id, booking_price, check_in_date, check_out_date, guest_num, booking_status, room_id, room_type_id) VALUES
-- Order 1 (Total: 6500) -> room_type 1 (room_id 1), room_type 2 (room_id 13)
(1, 3500, '2026-08-19 15:00:00', '2026-08-20 11:00:00', 2, N'已退房', 1, 1),
(1, 3000, '2026-08-19 15:00:00', '2026-08-20 11:00:00', 2, N'已退房', 13, 2),

-- Order 2 (Total: 9300) -> room_type 1 (room_id 2), room_type 5 (room_id 49)
(2, 3500, '2026-08-18 15:00:00', '2026-08-19 11:00:00', 2, N'已退房', 2, 1),
(2, 5800, '2026-08-18 15:00:00', '2026-08-19 11:00:00', 4, N'已退房', 49, 5),

-- Order 3 (Total: 11000, Cancelled)
(3, 5800, '2026-08-20 15:00:00', '2026-08-21 11:00:00', 4, N'已取消', NULL, 5),
(3, 5200, '2026-08-20 15:00:00', '2026-08-21 11:00:00', 4, N'已取消', NULL, 6),

-- Order 4 (Total: 7100) -> room_type 3 (room_id 5), room_type 4 (room_id 17)
(4, 3800, '2026-08-21 15:00:00', '2026-08-22 11:00:00', 2, N'已退房', 5, 3),
(4, 3300, '2026-08-21 15:00:00', '2026-08-22 11:00:00', 2, N'已退房', 17, 4),

-- Order 5 (Total: 14600) -> room_type 7 (room_id 97), room_type 5 (room_id 50)
(5, 8800, '2026-08-20 15:00:00', '2026-08-21 11:00:00', 2, N'已退房', 97, 7),
(5, 5800, '2026-08-20 15:00:00', '2026-08-21 11:00:00', 4, N'已退房', 50, 5),

-- Order 6 (Total: 6800, Cancelled)
(6, 3500, '2026-08-22 15:00:00', '2026-08-23 11:00:00', 2, N'已取消', NULL, 1),
(6, 3300, '2026-08-22 15:00:00', '2026-08-23 11:00:00', 2, N'已取消', NULL, 4),

-- Order 7 (Total: 8800) -> room_type 8 (room_id 103), room_type 3 (room_id 6)
(7, 5000, '2026-08-21 15:00:00', '2026-08-22 11:00:00', 2, N'已退房', 103, 8),
(7, 3800, '2026-08-21 15:00:00', '2026-08-22 11:00:00', 2, N'已退房', 6, 3),

-- Order 8 (Total: 12300) -> room_type 7 (room_id 98), room_type 1 (room_id 3)
(8, 8800, '2026-08-22 15:00:00', '2026-08-23 11:00:00', 2, N'已入住', 98, 7),
(8, 3500, '2026-08-22 15:00:00', '2026-08-23 11:00:00', 2, N'已入住', 3, 1),

-- Order 9 (Total: 6500) -> room_type 1 (room_id 4), room_type 2 (room_id 14)
(9, 3500, '2026-08-22 15:00:00', '2026-08-23 11:00:00', 2, N'已入住', 4, 1),
(9, 3000, '2026-08-22 15:00:00', '2026-08-23 11:00:00', 2, N'已入住', 14, 2),

-- Order 10 (Total: 17600, Cancelled)
(10, 8800, '2026-08-23 15:00:00', '2026-08-24 11:00:00', 2, N'已取消', NULL, 7),
(10, 8800, '2026-08-23 15:00:00', '2026-08-24 11:00:00', 2, N'已取消', NULL, 7),

-- Order 11 (Total: 9600) -> room_type 5 (room_id 51), room_type 3 (room_id 7)
(11, 5800, '2026-08-22 15:00:00', '2026-08-23 11:00:00', 4, N'已入住', 51, 5),
(11, 3800, '2026-08-22 15:00:00', '2026-08-23 11:00:00', 2, N'已入住', 7, 3),

-- Order 12 (Total: 7300) -> room_type 3 (room_id 8), room_type 1 (room_id 9)
(12, 3800, '2026-08-23 15:00:00', '2026-08-24 11:00:00', 2, N'已入住', 8, 3),
(12, 3500, '2026-08-23 15:00:00', '2026-08-24 11:00:00', 2, N'已入住', 9, 1),

-- Order 13 (Total: 11800) -> room_type 8 (room_id 104), room_type 3 (room_id 11)
(13, 8000, '2026-08-23 15:00:00', '2026-08-24 11:00:00', 2, N'已入住', 104, 8),
(13, 3800, '2026-08-23 15:00:00', '2026-08-24 11:00:00', 2, N'已入住', 11, 3),

-- Order 14 (Total: 6300, Cancelled)
(14, 3300, '2026-08-24 15:00:00', '2026-08-25 11:00:00', 2, N'已取消', NULL, 4),
(14, 3000, '2026-08-24 15:00:00', '2026-08-25 11:00:00', 2, N'已取消', NULL, 2),

-- Order 15 (Total: 14000) -> room_type 7 (room_id 99), room_type 6 (room_id 61)
(15, 8800, '2026-08-23 15:00:00', '2026-08-24 11:00:00', 2, N'已入住', 99, 7),
(15, 5200, '2026-08-23 15:00:00', '2026-08-24 11:00:00', 4, N'已入住', 61, 6),

-- Order 16 (Total: 8800) -> room_type 8 (room_id 105), room_type 3 (room_id 12)
(16, 5000, '2026-08-24 15:00:00', '2026-08-25 11:00:00', 2, N'待入住', 105, 8),
(16, 3800, '2026-08-24 15:00:00', '2026-08-25 11:00:00', 2, N'待入住', 12, 3),

-- Order 17 (Total: 10400) -> room_type 6 (room_id 62), room_type 6 (room_id 63)
(17, 5200, '2026-08-23 15:00:00', '2026-08-24 11:00:00', 4, N'已入住', 62, 6),
(17, 5200, '2026-08-23 15:00:00', '2026-08-24 11:00:00', 4, N'已入住', 63, 6),

-- Order 18 (Total: 6800, Cancelled)
(18, 3500, '2026-08-25 15:00:00', '2026-08-26 11:00:00', 2, N'已取消', NULL, 1),
(18, 3300, '2026-08-25 15:00:00', '2026-08-26 11:00:00', 2, N'已取消', NULL, 4),

-- Order 19 (Total: 16800) -> room_type 9 (room_id 109), room_type 2 (room_id 15)
(19, 13800, '2026-08-24 15:00:00', '2026-08-25 11:00:00', 4, N'待入住', 109, 9),
(19, 3000, '2026-08-24 15:00:00', '2026-08-25 11:00:00', 2, N'待入住', 15, 2),

-- Order 20 (Total: 9100) -> room_type 5 (room_id 52), room_type 4 (room_id 18)
(20, 5800, '2026-08-25 15:00:00', '2026-08-26 11:00:00', 4, N'待入住', 52, 5),
(20, 3300, '2026-08-25 15:00:00', '2026-08-26 11:00:00', 2, N'待入住', 18, 4),

-- Order 21 (Total: 6500) -> room_type 1 (room_id 10), room_type 2 (room_id 16)
(21, 3500, '2026-08-24 15:00:00', '2026-08-25 11:00:00', 2, N'待入住', 10, 1),
(21, 3000, '2026-08-24 15:00:00', '2026-08-25 11:00:00', 2, N'待入住', 16, 2),

-- Order 22 (Total: 12600, Cancelled)
(22, 8800, '2026-08-26 15:00:00', '2026-08-27 11:00:00', 2, N'已取消', NULL, 7),
(22, 3800, '2026-08-26 15:00:00', '2026-08-27 11:00:00', 2, N'已取消', NULL, 3),

-- Order 23 (Total: 7100) -> room_type 3 (room_id 29), room_type 4 (room_id 19)
(23, 3800, '2026-08-25 15:00:00', '2026-08-26 11:00:00', 2, N'待入住', 29, 3),
(23, 3300, '2026-08-25 15:00:00', '2026-08-26 11:00:00', 2, N'待入住', 19, 4),

-- Order 24 (Total: 14600) -> room_type 7 (room_id 100), room_type 5 (room_id 53)
(24, 8800, '2026-08-26 15:00:00', '2026-08-27 11:00:00', 2, N'待入住', 100, 7),
(24, 5800, '2026-08-26 15:00:00', '2026-08-27 11:00:00', 4, N'待入住', 53, 5),

-- Order 25 (Total: 8800) -> room_type 8 (room_id 106), room_type 3 (room_id 30)
(25, 5000, '2026-08-25 15:00:00', '2026-08-26 11:00:00', 2, N'待入住', 106, 8),
(25, 3800, '2026-08-25 15:00:00', '2026-08-26 11:00:00', 2, N'待入住', 30, 3),

-- Order 26 (Total: 11000, Cancelled)
(26, 5800, '2026-08-27 15:00:00', '2026-08-28 11:00:00', 4, N'已取消', NULL, 5),
(26, 5200, '2026-08-27 15:00:00', '2026-08-28 11:00:00', 4, N'已取消', NULL, 6),

-- Order 27 (Total: 6800) -> room_type 1 (room_id 25), room_type 4 (room_id 20)
(27, 3500, '2026-08-26 15:00:00', '2026-08-27 11:00:00', 2, N'待入住', 25, 1),
(27, 3300, '2026-08-26 15:00:00', '2026-08-27 11:00:00', 2, N'待入住', 20, 4),

-- Order 28 (Total: 9600) -> room_type 5 (room_id 54), room_type 3 (room_id 31)
(28, 5800, '2026-08-27 15:00:00', '2026-08-28 11:00:00', 4, N'待入住', 54, 5),
(28, 3800, '2026-08-27 15:00:00', '2026-08-28 11:00:00', 2, N'待入住', 31, 3),

-- Order 29 (Total: 17600) -> room_type 7 (room_id 101), room_type 7 (room_id 102)
(29, 8800, '2026-08-26 15:00:00', '2026-08-27 11:00:00', 2, N'待入住', 101, 7),
(29, 8800, '2026-08-26 15:00:00', '2026-08-27 11:00:00', 2, N'待入住', 102, 7),

-- Order 30 (Total: 7300) -> room_type 3 (room_id 32), room_type 1 (room_id 26)
(30, 3800, '2026-08-27 15:00:00', '2026-08-28 11:00:00', 2, N'待入住', 32, 3),
(30, 3500, '2026-08-27 15:00:00', '2026-08-28 11:00:00', 2, N'待入住', 26, 1);
GO

/* =========================================================
   10. room
   ========================================================= */
TRUNCATE TABLE room;
GO

INSERT INTO room (room_number, room_type_id, floor, room_status) VALUES
(N'10501', 1, 5, N'退房待清潔'),
(N'10502', 1, 5, N'退房待清潔'),
(N'10503', 1, 5, N'已入住'),
(N'10504', 1, 5, N'已入住'),
(N'10505', 3, 5, N'退房待清潔'),
(N'10506', 3, 5, N'退房待清潔'),
(N'10507', 3, 5, N'已入住'),
(N'10508', 3, 5, N'已入住'),
(N'10509', 1, 5, N'已入住'),
(N'10510', 1, 5, N'已預訂'),
(N'10511', 3, 5, N'已入住'),
(N'10512', 3, 5, N'已預訂'),
(N'20501', 2, 5, N'退房待清潔'),
(N'20502', 2, 5, N'已入住'),
(N'20503', 2, 5, N'已預訂'),
(N'20504', 2, 5, N'已預訂'),
(N'20505', 4, 5, N'退房待清潔'),
(N'20506', 4, 5, N'已預訂'),
(N'20507', 4, 5, N'已預訂'),
(N'20508', 4, 5, N'已預訂'),
(N'20509', 2, 5, N'可預訂'),
(N'20510', 2, 5, N'可預訂'),
(N'20511', 4, 5, N'可預訂'),
(N'20512', 4, 5, N'可預訂'),
(N'10601', 1, 6, N'已預訂'),
(N'10602', 1, 6, N'已預訂'),
(N'10603', 1, 6, N'可預訂'),
(N'10604', 1, 6, N'可預訂'),
(N'10605', 3, 6, N'已預訂'),
(N'10606', 3, 6, N'已預訂'),
(N'10607', 3, 6, N'已預訂'),
(N'10608', 3, 6, N'已預訂'),
(N'10609', 1, 6, N'可預訂'),
(N'10610', 1, 6, N'可預訂'),
(N'10611', 3, 6, N'可預訂'),
(N'10612', 3, 6, N'可預訂'),
(N'20601', 2, 6, N'可預訂'),
(N'20602', 2, 6, N'可預訂'),
(N'20603', 2, 6, N'可預訂'),
(N'20604', 2, 6, N'可預訂'),
(N'20605', 4, 6, N'可預訂'),
(N'20606', 4, 6, N'可預訂'),
(N'20607', 4, 6, N'可預訂'),
(N'20608', 4, 6, N'可預訂'),
(N'20609', 2, 6, N'可預訂'),
(N'20610', 2, 6, N'可預訂'),
(N'20611', 4, 6, N'可預訂'),
(N'20612', 4, 6, N'可預訂'),
(N'10701', 5, 7, N'退房待清潔'),
(N'10702', 5, 7, N'退房待清潔'),
(N'10703', 5, 7, N'已入住'),
(N'10704', 5, 7, N'已預訂'),
(N'10705', 5, 7, N'已預訂'),
(N'10706', 5, 7, N'已預訂'),
(N'10707', 5, 7, N'可預訂'),
(N'10708', 5, 7, N'可預訂'),
(N'10709', 5, 7, N'可預訂'),
(N'10710', 5, 7, N'可預訂'),
(N'10711', 5, 7, N'可預訂'),
(N'10712', 5, 7, N'可預訂'),
(N'20701', 6, 7, N'已入住'),
(N'20702', 6, 7, N'已入住'),
(N'20703', 6, 7, N'已入住'),
(N'20704', 6, 7, N'可預訂'),
(N'20705', 6, 7, N'可預訂'),
(N'20706', 6, 7, N'可預訂'),
(N'20707', 6, 7, N'可預訂'),
(N'20708', 6, 7, N'可預訂'),
(N'20709', 6, 7, N'可預訂'),
(N'20710', 6, 7, N'可預訂'),
(N'20711', 6, 7, N'可預訂'),
(N'20712', 6, 7, N'可預訂'),
(N'10801', 1, 8, N'可預訂'),
(N'10802', 1, 8, N'可預訂'),
(N'10803', 1, 8, N'可預訂'),
(N'10804', 1, 8, N'可預訂'),
(N'10805', 3, 8, N'可預訂'),
(N'10806', 3, 8, N'可預訂'),
(N'10807', 3, 8, N'可預訂'),
(N'10808', 3, 8, N'可預訂'),
(N'10809', 5, 8, N'可預訂'),
(N'10810', 5, 8, N'可預訂'),
(N'10811', 5, 8, N'可預訂'),
(N'10812', 5, 8, N'可預訂'),
(N'20801', 2, 8, N'可預訂'),
(N'20802', 2, 8, N'可預訂'),
(N'20803', 2, 8, N'可預訂'),
(N'20804', 2, 8, N'可預訂'),
(N'20805', 4, 8, N'可預訂'),
(N'20806', 4, 8, N'可預訂'),
(N'20807', 4, 8, N'可預訂'),
(N'20808', 4, 8, N'可預訂'),
(N'20809', 6, 8, N'可預訂'),
(N'20810', 6, 8, N'可預訂'),
(N'20811', 6, 8, N'可預訂'),
(N'20812', 6, 8, N'可預訂'),
(N'10901', 7, 9, N'退房待清潔'),
(N'10902', 7, 9, N'已入住'),
(N'10903', 7, 9, N'已入住'),
(N'10904', 7, 9, N'已預訂'),
(N'10905', 7, 9, N'已預訂'),
(N'10906', 7, 9, N'已預訂'),
(N'20901', 8, 9, N'退房待清潔'),
(N'20902', 8, 9, N'已入住'),
(N'20903', 8, 9, N'已預訂'),
(N'20904', 8, 9, N'已預訂'),
(N'20905', 8, 9, N'可預訂'),
(N'20906', 8, 9, N'可預訂'),
(N'31001', 9, 10, N'已預訂'),
(N'31002', 9, 10, N'可預訂'),
(N'31003', 9, 10, N'可預訂'),
(N'31004', 9, 10, N'可預訂'),
(N'31005', 9, 10, N'可預訂'),
(N'31006', 9, 10, N'可預訂'),
(N'31007', 9, 10, N'可預訂'),
(N'31008', 9, 10, N'可預訂'),
(N'31009', 9, 10, N'可預訂'),
(N'31010', 9, 10, N'可預訂'),
(N'31011', 10, 10, N'可預訂'),
(N'31012', 10, 10, N'可預訂');
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
INSERT INTO room_task (
    room_id, 
    employee_id, 
    priority, 
    task_type, 
    task_status, 
    remark, 
    created_at,
    completed_at
) VALUES
-- 1. 退房清潔任務（退房時間 11:00 後陸續建立）
(1,   13, N'緊急', N'退房清潔', N'進行中', N'客人已退房，需優先清潔整備', '2026-08-23 11:15:00', NULL),
(2,   14, N'緊急', N'退房清潔', N'待處理', N'客人已退房，待清潔',         '2026-08-23 11:20:00', NULL),
(5,   15, N'重要', N'退房清潔', N'進行中', N'退房清潔中',               '2026-08-23 11:30:00', NULL),
(6,   16, N'重要', N'退房清潔', N'待處理', N'待清潔房型',               '2026-08-23 11:35:00', NULL),
(13,  17, N'緊急', N'退房清潔', N'待處理', N'待退房清潔',               '2026-08-23 11:40:00', NULL),
(17,  18, N'重要', N'退房清潔', N'進行中', N'進行退房打掃',             '2026-08-23 11:45:00', NULL),
(49,  19, N'緊急', N'退房清潔', N'待處理', N'待退房清潔',               '2026-08-23 12:00:00', NULL),
(50,  20, N'重要', N'退房清潔', N'已完成', N'已完成清潔與備品更換',     '2026-08-23 11:10:00', '2026-08-23 12:30:00'),
(97,  21, N'緊急', N'退房清潔', N'待處理', N'待退房清潔',               '2026-08-23 12:15:00', NULL),
(103, 22, N'重要', N'退房清潔', N'待處理', N'待退房清潔',               '2026-08-23 12:30:00', NULL),

-- 2. 續住日常清潔與補充備品（包含 employee 25 支援房間清潔）
(3,   23, N'一般', N'日常清潔', N'進行中', N'房客要求簡短打掃',         '2026-08-23 13:30:00', NULL),
(7,   24, N'一般', N'日常清潔', N'待處理', N'續住清潔',                 '2026-08-23 14:00:00', NULL),
(9,   26, N'一般', N'補充備品', N'待處理', N'補充毛巾與浴巾',           '2026-08-23 14:30:00', NULL),
(15,  25, N'一般', N'日常清潔', N'進行中', N'續住房間打掃與整備',       '2026-08-23 14:45:00', NULL),
(51,  11, N'一般', N'日常清潔', N'已完成', N'日常清潔已完成',           '2026-08-23 10:00:00', '2026-08-23 11:00:00'),
(61,  12, N'一般', N'補充備品', N'進行中', N'補充礦泉水與盥洗用品',     '2026-08-23 15:00:00', NULL),

-- 3. 設備維修任務（房客回報維修）
(4,   27, N'緊急', N'設備維修', N'進行中', N'冷氣不冷',                 '2026-08-23 16:00:00', NULL),
(14,  28, N'重要', N'設備維修', N'待處理', N'馬桶堵塞',                 '2026-08-23 16:30:00', NULL);
GO