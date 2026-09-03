-- =========================================================
USE [finalproject];
GO
   /* =========================================================
    1. department
    ========================================================= */
INSERT INTO department (department_name)
VALUES (N'櫃檯部'),
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
VALUES -- 1 ~ 2: 行政管理部 (Admin)
   (
      'admin01',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'admin02',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   -- 3 ~ 10: 櫃檯/前台部 (Front Desk)
   (
      'frontdesk01',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'frontdesk02',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'frontdesk03',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'frontdesk04',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'frontdesk05',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'frontdesk06',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'frontdesk07',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'frontdesk08',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   -- 11 ~ 28: 客房/房務與維修部 (Housekeeping)
   (
      'housekeeping01',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping02',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping03',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping04',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping05',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping06',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping07',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping08',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping09',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping10',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping11',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping12',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping13',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping14',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping15',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping16',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping17',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'housekeeping18',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   -- 29 ~ 45: 餐飲部 (F&B)
   (
      'fnb01',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'fnb02',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'fnb03',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'fnb04',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'fnb05',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'fnb06',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'fnb07',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'fnb08',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'fnb09',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'fnb10',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'fnb11',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'fnb12',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'fnb13',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'fnb14',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'fnb15',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'fnb16',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'fnb17',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   -- 46 ~ 105: 顧客會員 (Customer / Guest)
   (
      'customer01',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer02',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer03',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer04',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer05',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer06',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer07',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer08',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer09',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer10',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer11',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer12',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer13',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer14',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer15',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer16',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer17',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer18',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer19',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer20',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer21',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer22',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer23',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer24',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer25',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer26',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer27',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer28',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer29',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer30',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer31',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer32',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer33',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer34',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer35',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer36',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer37',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer38',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer39',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer40',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer41',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer42',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer43',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer44',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer45',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer46',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer47',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer48',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer49',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer50',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer51',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer52',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer53',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer54',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer55',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer56',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer57',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer58',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer59',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   ),
   (
      'customer60',
      '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C',
      '1'
   );
GO
   /* =========================================================
    3. permission
    ========================================================= */
INSERT INTO permission (permission_code, permission_name)
VALUES (N 'ROOM_MANAGE', N'房間管理'),
   (N 'BOOKING_MANAGE', N'訂房管理'),
   (N 'RESTAURANT_MANAGE', N'餐廳管理'),
   (N 'MEMBER_MANAGE', N'會員管理'),
   (N 'ORDER_MANAGE', N'訂單管理');
GO
   /* =========================================================
    4. employee
    ========================================================= */
INSERT INTO employee (department_id, account_id, position) << << << < HEAD
VALUES -- 行政管理部 (department_id = 4, account_id 1~2)
   (4, 1, N'總經理'),
   -- Emp 1
   (4, 2, N'行政人資主管'),
   -- Emp 2
   -- 櫃檯/前台部 (department_id = 1, account_id 3~10)
   (1, 3, N'櫃檯主管'),
   -- Emp 3
   (1, 4, N'櫃檯專員(早班)'),
   -- Emp 4
   (1, 5, N'櫃檯專員(早班)'),
   -- Emp 5
   (1, 6, N'櫃檯專員(晚班)'),
   -- Emp 6
   (1, 7, N'櫃檯專員(晚班)'),
   -- Emp 7
   (1, 8, N'櫃檯專員(大夜)'),
   -- Emp 8
   (1, 9, N'禮賓接待員'),
   -- Emp 9
   (1, 10, N'車隊接送員'),
   -- Emp 10
   -- 客房/房務與維修部 (department_id = 2, account_id 11~28)
   (2, 11, N'房務主管'),
   -- Emp 11
   (2, 12, N'房務領班'),
   -- Emp 12
   (2, 13, N'房務專員'),
   -- Emp 13
   (2, 14, N'房務專員'),
   -- Emp 14
   (2, 15, N'房務專員'),
   -- Emp 15
   (2, 16, N'房務專員'),
   -- Emp 16
   (2, 17, N'房務專員'),
   -- Emp 17
   (2, 18, N'房務專員'),
   -- Emp 18
   (2, 19, N'房務專員'),
   -- Emp 19
   (2, 20, N'房務專員'),
   -- Emp 20
   (2, 21, N'房務專員'),
   -- Emp 21
   (2, 22, N'房務專員'),
   -- Emp 22
   (2, 23, N'房務專員'),
   -- Emp 23
   (2, 24, N'房務專員'),
   -- Emp 24
   (2, 25, N'公設清潔員'),
   -- Emp 25
   (2, 26, N'布巾洗滌員'),
   -- Emp 26
   (2, 27, N'機電維修員'),
   -- Emp 27
   (2, 28, N'水電維修員'),
   -- Emp 28
   -- 餐飲部 (department_id = 3, account_id 29~45)
   (3, 29, N'餐飲主管'),
   -- Emp 29
   (3, 30, N'主廚'),
   -- Emp 30
   (3, 31, N'副廚'),
   -- Emp 31
   (3, 32, N'砧板/冷盤廚師'),
   -- Emp 32
   (3, 33, N'熱炒/西餐廚師'),
   -- Emp 33
   (3, 34, N'點心/烘焙師'),
   -- Emp 34
   (3, 35, N'餐飲組長'),
   -- Emp 35
   (3, 36, N'餐飲服務員'),
   -- Emp 36
   (3, 37, N'餐飲服務員'),
   -- Emp 37
   (3, 38, N'餐飲服務員'),
   -- Emp 38
   (3, 39, N'餐飲服務員'),
   -- Emp 39
   (3, 40, N'餐飲服務員'),
   -- Emp 40
   (3, 41, N'餐飲服務員'),
   -- Emp 41
   (3, 42, N'吧檯調酒師'),
   -- Emp 42
   (3, 43, N'吧檯助理'),
   -- Emp 43
   (3, 44, N'洗碗員'),
   -- Emp 44
   (3, 45, N'備料清潔員');
-- Emp 45
GO
   /* =========================================================
    5. employee_permission
    permission_id 對應 permission.permission_id；employee_id 對應 employee.employee_id
    ========================================================= */
INSERT INTO employee_permission (permission_id, employee_id)
VALUES -- Emp 1: 總經理 / 管理員 (擁有全部 5 項權限)
   (1, 1),
   -- 房間管理
   (2, 1),
   -- 訂房管理
   (3, 1),
   -- 餐廳管理
   (4, 1),
   -- 會員管理
   (5, 1),
   -- 訂單管理
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
VALUES (46),
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
INSERT INTO profile (
      account_id,
      name,
      email,
      phone,
      zipcode,
      city,
      district,
      address,
      created_at,
      birthday,
      gender,
      updated_at
   )
VALUES (
      5,
      '王小明',
      'ming@example.com',
      '0912345678',
      '320',
      '桃園市',
      '中壢區',
      '中央西路一段100號',
      '2026-08-01 10:00:00',
      '1998-05-12',
      '男',
      '2026-08-01 10:00:00'
   ),
   (
      6,
      '陳小華',
      'hua@example.com',
      '0923456789',
      '320',
      '桃園市',
      '中壢區',
      '中美路200號',
      '2026-08-02 11:00:00',
      '1995-08-20',
      '女',
      '2026-08-02 11:00:00'
   ),
   (
      7,
      '林大偉',
      'david@example.com',
      '0934567890',
      '330',
      '桃園市',
      '桃園區',
      '中正路300號',
      '2026-08-03 14:00:00',
      '2000-03-15',
      '男',
      '2026-08-03 14:00:00'
   ),
   (
      8,
      '張雅婷',
      'yating@example.com',
      '0945678901',
      '330',
      '桃園市',
      '桃園區',
      '民生路120號',
      '2026-08-04 09:30:00',
      '1997-07-22',
      '女',
      '2026-08-04 09:30:00'
   ),
   (
      9,
      '李承翰',
      'han@example.com',
      '0956789012',
      '320',
      '桃園市',
      '中壢區',
      '延平路88號',
      '2026-08-05 13:20:00',
      '1999-11-03',
      '男',
      '2026-08-05 13:20:00'
   ),
   (
      10,
      '黃詩涵',
      'shihan@example.com',
      '0967890123',
      '324',
      '桃園市',
      '平鎮區',
      '環南路50號',
      '2026-08-06 15:10:00',
      '1996-02-18',
      '女',
      '2026-08-06 15:10:00'
   ),
   (
      11,
      '吳俊傑',
      'junjie@example.com',
      '0978901234',
      '324',
      '桃園市',
      '平鎮區',
      '中豐路160號',
      '2026-08-07 10:40:00',
      '1994-09-25',
      '男',
      '2026-08-07 10:40:00'
   ),
   (
      12,
      '周怡君',
      'yijun@example.com',
      '0989012345',
      '300',
      '新竹市',
      '東區',
      '光復路一段50號',
      '2026-08-08 11:30:00',
      '1998-12-10',
      '女',
      '2026-08-08 11:30:00'
   ),
   (
      13,
      '許家豪',
      'jiahao@example.com',
      '0901234567',
      '300',
      '新竹市',
      '北區',
      '中正路180號',
      '2026-08-09 14:20:00',
      '1993-04-08',
      '男',
      '2026-08-09 14:20:00'
   ),
   (
      14,
      '鄭惠文',
      'huiwen@example.com',
      '0911234567',
      '302',
      '新竹縣',
      '竹北市',
      '縣政二路100號',
      '2026-08-10 09:10:00',
      '2001-06-30',
      '女',
      '2026-08-10 09:10:00'
   ),
   (
      15,
      '蔡明哲',
      'mingzhe@example.com',
      '0921234567',
      '302',
      '新竹縣',
      '竹北市',
      '光明六路200號',
      '2026-08-11 16:00:00',
      '1992-01-15',
      '男',
      '2026-08-11 16:00:00'
   ),
   (
      16,
      '楊淑芬',
      'shufen@example.com',
      '0931234567',
      '300',
      '新竹市',
      '東區',
      '建功一路30號',
      '2026-08-12 10:15:00',
      '1990-10-05',
      '女',
      '2026-08-12 10:15:00'
   ),
   (
      17,
      '曾冠宇',
      'guanyu@example.com',
      '0941234567',
      '330',
      '桃園市',
      '桃園區',
      '春日路260號',
      '2026-08-13 13:45:00',
      '1997-03-21',
      '男',
      '2026-08-13 13:45:00'
   ),
   (
      18,
      '劉欣怡',
      'xinyi@example.com',
      '0951234567',
      '333',
      '桃園市',
      '龜山區',
      '文化一路80號',
      '2026-08-14 11:05:00',
      '1999-08-14',
      '女',
      '2026-08-14 11:05:00'
   ),
   (
      19,
      '郭志偉',
      'zhiwei@example.com',
      '0961234567',
      '333',
      '桃園市',
      '龜山區',
      '復興一路120號',
      '2026-08-15 15:25:00',
      '1995-05-19',
      '男',
      '2026-08-15 15:25:00'
   ),
   (
      20,
      '謝佩珊',
      'peishan@example.com',
      '0971234567',
      '334',
      '桃園市',
      '八德區',
      '介壽路一段90號',
      '2026-08-16 09:50:00',
      '1996-11-27',
      '女',
      '2026-08-16 09:50:00'
   ),
   (
      21,
      '何俊宏',
      'junhong@example.com',
      '0981234567',
      '334',
      '桃園市',
      '八德區',
      '廣福路150號',
      '2026-08-17 12:10:00',
      '1991-07-06',
      '男',
      '2026-08-17 12:10:00'
   ),
   (
      22,
      '徐雅雯',
      'yawen@example.com',
      '0902234567',
      '335',
      '桃園市',
      '大溪區',
      '中正東路70號',
      '2026-08-18 14:35:00',
      '2000-02-12',
      '女',
      '2026-08-18 14:35:00'
   ),
   (
      23,
      '羅偉倫',
      'weilun@example.com',
      '0912234567',
      '335',
      '桃園市',
      '大溪區',
      '和平路110號',
      '2026-08-19 10:25:00',
      '1994-06-23',
      '男',
      '2026-08-19 10:25:00'
   ),
   (
      24,
      '林佳蓉',
      'jiarong@example.com',
      '0922234567',
      '336',
      '桃園市',
      '復興區',
      '中正路25號',
      '2026-08-20 13:15:00',
      '1998-09-17',
      '女',
      '2026-08-20 13:15:00'
   ),
   (
      25,
      '張志豪',
      'zhihao@example.com',
      '0932234567',
      '337',
      '桃園市',
      '大園區',
      '中山南路130號',
      '2026-08-21 09:40:00',
      '1993-12-01',
      '男',
      '2026-08-21 09:40:00'
   ),
   (
      26,
      '林怡萱',
      'yixuan@example.com',
      '0942234567',
      '337',
      '桃園市',
      '大園區',
      '和平西路75號',
      '2026-08-22 11:55:00',
      '2001-04-16',
      '女',
      '2026-08-22 11:55:00'
   ),
   (
      27,
      '陳柏宇',
      'boyu@example.com',
      '0952234567',
      '338',
      '桃園市',
      '蘆竹區',
      '南山路100號',
      '2026-08-23 15:05:00',
      '1997-10-29',
      '男',
      '2026-08-23 15:05:00'
   ),
   (
      28,
      '王思妤',
      'siyu@example.com',
      '0962234567',
      '338',
      '桃園市',
      '蘆竹區',
      '南崁路220號',
      '2026-08-24 10:35:00',
      '1999-01-09',
      '女',
      '2026-08-24 10:35:00'
   ),
   (
      29,
      '黃柏勳',
      'boxun@example.com',
      '0972234567',
      '320',
      '桃園市',
      '中壢區',
      '新生路180號',
      '2026-08-25 13:50:00',
      '1996-05-26',
      '男',
      '2026-08-25 13:50:00'
   ),
   (
      30,
      '吳佳玲',
      'jialing@example.com',
      '0982234567',
      '320',
      '桃園市',
      '中壢區',
      '慈惠三街60號',
      '2026-08-26 09:20:00',
      '1995-03-11',
      '女',
      '2026-08-26 09:20:00'
   ),
   (
      31,
      '李冠廷',
      'guanting@example.com',
      '0903234567',
      '320',
      '桃園市',
      '中壢區',
      '環中東路240號',
      '2026-08-01 14:10:00',
      '1998-07-03',
      '男',
      '2026-08-01 14:10:00'
   ),
   (
      32,
      '陳怡安',
      'yian@example.com',
      '0913234567',
      '324',
      '桃園市',
      '平鎮區',
      '金陵路88號',
      '2026-08-02 10:05:00',
      '2000-11-18',
      '女',
      '2026-08-02 10:05:00'
   ),
   (
      33,
      '周建宏',
      'jianhong@example.com',
      '0923234567',
      '324',
      '桃園市',
      '平鎮區',
      '育達路150號',
      '2026-08-03 15:30:00',
      '1992-08-07',
      '男',
      '2026-08-03 15:30:00'
   ),
   (
      34,
      '許芳瑜',
      'fangyu@example.com',
      '0933234567',
      '325',
      '桃園市',
      '龍潭區',
      '中正路90號',
      '2026-08-04 11:20:00',
      '1997-02-25',
      '女',
      '2026-08-04 11:20:00'
   ),
   (
      35,
      '鄭凱文',
      'kaiwen@example.com',
      '0943234567',
      '325',
      '桃園市',
      '龍潭區',
      '北龍路170號',
      '2026-08-05 13:00:00',
      '1994-12-14',
      '男',
      '2026-08-05 13:00:00'
   ),
   (
      36,
      '蔡宜庭',
      'yiting@example.com',
      '0953234567',
      '326',
      '桃園市',
      '楊梅區',
      '大成路120號',
      '2026-08-06 09:45:00',
      '2001-09-08',
      '女',
      '2026-08-06 09:45:00'
   ),
   (
      37,
      '何宗翰',
      'zonghan@example.com',
      '0963234567',
      '326',
      '桃園市',
      '楊梅區',
      '新成路200號',
      '2026-08-07 16:15:00',
      '1993-05-30',
      '男',
      '2026-08-07 16:15:00'
   ),
   (
      38,
      '謝欣妤',
      'xinyu@example.com',
      '0973234567',
      '327',
      '桃園市',
      '新屋區',
      '中山西路100號',
      '2026-08-08 10:50:00',
      '1999-06-12',
      '女',
      '2026-08-08 10:50:00'
   ),
   (
      39,
      '林俊佑',
      'junyou@example.com',
      '0983234567',
      '327',
      '桃園市',
      '新屋區',
      '中正路50號',
      '2026-08-09 14:45:00',
      '1995-01-28',
      '男',
      '2026-08-09 14:45:00'
   ),
   (
      40,
      '張瑞芳',
      'ruifang@example.com',
      '0904234567',
      '328',
      '桃園市',
      '觀音區',
      '中山路180號',
      '2026-08-10 11:35:00',
      '1996-10-21',
      '女',
      '2026-08-10 11:35:00'
   ),
   (
      41,
      '王建國',
      'jianguo@example.com',
      '0914234567',
      '328',
      '桃園市',
      '觀音區',
      '大觀路90號',
      '2026-08-11 13:25:00',
      '1989-04-05',
      '男',
      '2026-08-11 13:25:00'
   ),
   (
      42,
      '劉佳穎',
      'jiaying@example.com',
      '0924234567',
      '330',
      '桃園市',
      '桃園區',
      '成功路100號',
      '2026-08-12 09:15:00',
      '2000-08-19',
      '女',
      '2026-08-12 09:15:00'
   ),
   (
      43,
      '黃冠霖',
      'guanlin@example.com',
      '0934234567',
      '330',
      '桃園市',
      '桃園區',
      '復興路250號',
      '2026-08-13 15:40:00',
      '1997-11-05',
      '男',
      '2026-08-13 15:40:00'
   ),
   (
      44,
      '吳佩蓉',
      'peirong@example.com',
      '0944234567',
      '333',
      '桃園市',
      '龜山區',
      '萬壽路80號',
      '2026-08-14 10:30:00',
      '1998-03-22',
      '女',
      '2026-08-14 10:30:00'
   ),
   (
      45,
      '陳威廷',
      'weiting@example.com',
      '0954234567',
      '333',
      '桃園市',
      '龜山區',
      '自強東路140號',
      '2026-08-15 14:00:00',
      '1994-07-17',
      '男',
      '2026-08-15 14:00:00'
   ),
   (
      46,
      '林欣怡',
      'hsinyi@example.com',
      '0964234567',
      '334',
      '桃園市',
      '八德區',
      '忠勇街60號',
      '2026-08-16 11:10:00',
      '2001-02-03',
      '女',
      '2026-08-16 11:10:00'
   ),
   (
      47,
      '張凱翔',
      'kaixiang@example.com',
      '0974234567',
      '334',
      '桃園市',
      '八德區',
      '介壽路二段300號',
      '2026-08-17 16:30:00',
      '1996-09-12',
      '男',
      '2026-08-17 16:30:00'
   ),
   (
      48,
      '楊雅婷',
      'yangting@example.com',
      '0984234567',
      '335',
      '桃園市',
      '大溪區',
      '員林路120號',
      '2026-08-18 09:35:00',
      '1999-12-25',
      '女',
      '2026-08-18 09:35:00'
   ),
   (
      49,
      '郭俊傑',
      'junjie2@example.com',
      '0905234567',
      '335',
      '桃園市',
      '大溪區',
      '慈湖路180號',
      '2026-08-19 13:05:00',
      '1992-06-18',
      '男',
      '2026-08-19 13:05:00'
   ),
   (
      50,
      '徐婉婷',
      'wanting@example.com',
      '0915234567',
      '336',
      '桃園市',
      '復興區',
      '三民路30號',
      '2026-08-20 10:45:00',
      '1997-04-27',
      '女',
      '2026-08-20 10:45:00'
   ),
   (
      51,
      '羅子軒',
      'zixuan@example.com',
      '0925234567',
      '337',
      '桃園市',
      '大園區',
      '航站南路50號',
      '2026-08-21 14:25:00',
      '2000-10-16',
      '男',
      '2026-08-21 14:25:00'
   ),
   (
      52,
      '謝宜蓁',
      'yizhen@example.com',
      '0935234567',
      '337',
      '桃園市',
      '大園區',
      '中華路90號',
      '2026-08-22 11:50:00',
      '1995-08-03',
      '女',
      '2026-08-22 11:50:00'
   ),
   (
      53,
      '何明哲',
      'mingzhe2@example.com',
      '0945234567',
      '338',
      '桃園市',
      '蘆竹區',
      '忠孝西路110號',
      '2026-08-23 15:15:00',
      '1993-03-29',
      '男',
      '2026-08-23 15:15:00'
   ),
   (
      54,
      '蔡佳穎',
      'jiaying2@example.com',
      '0955234567',
      '338',
      '桃園市',
      '蘆竹區',
      '南竹路160號',
      '2026-08-24 09:55:00',
      '1998-11-11',
      '女',
      '2026-08-24 09:55:00'
   ),
   (
      55,
      '鄭宇翔',
      'yuxiang@example.com',
      '0965234567',
      '320',
      '桃園市',
      '中壢區',
      '中山東路100號',
      '2026-08-25 13:40:00',
      '1996-01-20',
      '男',
      '2026-08-25 13:40:00'
   ),
   (
      56,
      '周怡萱',
      'yixuan2@example.com',
      '0975234567',
      '320',
      '桃園市',
      '中壢區',
      '實踐路80號',
      '2026-08-26 10:20:00',
      '2001-05-06',
      '女',
      '2026-08-26 10:20:00'
   ),
   (
      57,
      '許博翔',
      'boxiang@example.com',
      '0985234567',
      '324',
      '桃園市',
      '平鎮區',
      '振興路130號',
      '2026-08-01 15:00:00',
      '1994-09-14',
      '男',
      '2026-08-01 15:00:00'
   ),
   (
      58,
      '李佳玲',
      'jialing2@example.com',
      '0906234567',
      '324',
      '桃園市',
      '平鎮區',
      '廣德街70號',
      '2026-08-02 11:40:00',
      '1999-07-28',
      '女',
      '2026-08-02 11:40:00'
   ),
   (
      59,
      '吳宗憲',
      'zongxian@example.com',
      '0916234567',
      '325',
      '桃園市',
      '龍潭區',
      '龍元路100號',
      '2026-08-03 14:15:00',
      '1991-12-09',
      '男',
      '2026-08-03 14:15:00'
   ),
   (
      60,
      '林詩婷',
      'shiting@example.com',
      '0926234567',
      '325',
      '桃園市',
      '龍潭區',
      '東龍路150號',
      '2026-08-04 09:25:00',
      '1997-06-04',
      '女',
      '2026-08-04 09:25:00'
   ),
   (
      61,
      '黃志豪',
      'zhihao2@example.com',
      '0936234567',
      '326',
      '桃園市',
      '楊梅區',
      '瑞溪路80號',
      '2026-08-05 16:05:00',
      '1995-10-19',
      '男',
      '2026-08-05 16:05:00'
   ),
   (
      62,
      '陳怡君',
      'yijun2@example.com',
      '0946234567',
      '326',
      '桃園市',
      '楊梅區',
      '新農街120號',
      '2026-08-06 10:10:00',
      '2000-01-31',
      '女',
      '2026-08-06 10:10:00'
   ),
   (
      63,
      '王俊凱',
      'junkai@example.com',
      '0956234567',
      '327',
      '桃園市',
      '新屋區',
      '中興路60號',
      '2026-08-07 13:35:00',
      '1993-08-22',
      '男',
      '2026-08-07 13:35:00'
   ),
   (
      64,
      '張淑貞',
      'shuzhen@example.com',
      '0966234567',
      '327',
      '桃園市',
      '新屋區',
      '永安路90號',
      '2026-08-08 11:25:00',
      '1990-05-17',
      '女',
      '2026-08-08 11:25:00'
   ),
   (
      65,
      '林家豪',
      'jiahao2@example.com',
      '0976234567',
      '328',
      '桃園市',
      '觀音區',
      '草漯路130號',
      '2026-08-09 15:50:00',
      '1998-02-08',
      '男',
      '2026-08-09 15:50:00'
   ),
   (
      66,
      '吳佳蓉',
      'jiarong2@example.com',
      '0986234567',
      '328',
      '桃園市',
      '觀音區',
      '大同路50號',
      '2026-08-10 09:05:00',
      '1996-12-20',
      '女',
      '2026-08-10 09:05:00'
   ),
   (
      67,
      '蔡承恩',
      'chengen@example.com',
      '0907234567',
      '330',
      '桃園市',
      '桃園區',
      '南平路180號',
      '2026-08-11 14:40:00',
      '2001-03-13',
      '男',
      '2026-08-11 14:40:00'
   ),
   (
      68,
      '楊欣怡',
      'xinyi2@example.com',
      '0917234567',
      '330',
      '桃園市',
      '桃園區',
      '大興西路100號',
      '2026-08-12 10:55:00',
      '1999-09-24',
      '女',
      '2026-08-12 10:55:00'
   ),
   (
      69,
      '何冠霖',
      'guanlin2@example.com',
      '0927234567',
      '333',
      '桃園市',
      '龜山區',
      '德明路70號',
      '2026-08-13 13:20:00',
      '1994-11-07',
      '男',
      '2026-08-13 13:20:00'
   ),
   (
      70,
      '徐佳琪',
      'jiaqi@example.com',
      '0937234567',
      '333',
      '桃園市',
      '龜山區',
      '文青路120號',
      '2026-08-14 16:10:00',
      '1998-06-26',
      '女',
      '2026-08-14 16:10:00'
   ),
   (
      71,
      '鄭凱傑',
      'kaijie@example.com',
      '0947234567',
      '334',
      '桃園市',
      '八德區',
      '廣興路90號',
      '2026-08-15 09:30:00',
      '1992-02-15',
      '男',
      '2026-08-15 09:30:00'
   ),
   (
      72,
      '謝雅婷',
      'yating2@example.com',
      '0957234567',
      '334',
      '桃園市',
      '八德區',
      '和平路210號',
      '2026-08-16 12:45:00',
      '1997-10-03',
      '女',
      '2026-08-16 12:45:00'
   ),
   (
      73,
      '羅偉豪',
      'weihao@example.com',
      '0967234567',
      '335',
      '桃園市',
      '大溪區',
      '康莊路140號',
      '2026-08-17 15:20:00',
      '1995-04-11',
      '男',
      '2026-08-17 15:20:00'
   ),
   (
      74,
      '劉怡伶',
      'yiling@example.com',
      '0977234567',
      '335',
      '桃園市',
      '大溪區',
      '仁和路80號',
      '2026-08-18 10:00:00',
      '2000-07-29',
      '女',
      '2026-08-18 10:00:00'
   ),
   (
      75,
      '郭柏廷',
      'boting@example.com',
      '0987234567',
      '336',
      '桃園市',
      '復興區',
      '羅浮路30號',
      '2026-08-19 14:55:00',
      '1993-01-17',
      '男',
      '2026-08-19 14:55:00'
   ),
   (
      76,
      '黃鈺婷',
      'yuting@example.com',
      '0908234567',
      '337',
      '桃園市',
      '大園區',
      '三民路110號',
      '2026-08-20 11:15:00',
      '1996-08-09',
      '女',
      '2026-08-20 11:15:00'
   ),
   (
      77,
      '張哲維',
      'zhewei@example.com',
      '0918234567',
      '337',
      '桃園市',
      '大園區',
      '國際路200號',
      '2026-08-21 16:25:00',
      '1999-05-23',
      '男',
      '2026-08-21 16:25:00'
   ),
   (
      78,
      '陳妍希',
      'yanxi@example.com',
      '0928234567',
      '338',
      '桃園市',
      '蘆竹區',
      '南福街60號',
      '2026-08-22 09:40:00',
      '2001-11-02',
      '女',
      '2026-08-22 09:40:00'
   ),
   (
      79,
      '林昱辰',
      'yuchen@example.com',
      '0938234567',
      '338',
      '桃園市',
      '蘆竹區',
      '大竹路150號',
      '2026-08-23 13:10:00',
      '1997-03-08',
      '男',
      '2026-08-23 13:10:00'
   ),
   (
      80,
      '王郁婷',
      'yuting2@example.com',
      '0948234567',
      '320',
      '桃園市',
      '中壢區',
      '龍東路100號',
      '2026-08-24 15:35:00',
      '1998-12-18',
      '女',
      '2026-08-24 15:35:00'
   ),
   (
      81,
      '李俊豪',
      'junhao@example.com',
      '0958234567',
      '320',
      '桃園市',
      '中壢區',
      '榮民路180號',
      '2026-08-25 10:25:00',
      '1994-06-05',
      '男',
      '2026-08-25 10:25:00'
   ),
   (
      82,
      '周怡婷',
      'yiting2@example.com',
      '0968234567',
      '320',
      '桃園市',
      '中壢區',
      '中北路200號',
      '2026-08-26 14:05:00',
      '1999-09-15',
      '女',
      '2026-08-26 14:05:00'
   ),
   (
      83,
      '許志明',
      'zhiming@example.com',
      '0978234567',
      '324',
      '桃園市',
      '平鎮區',
      '民族路90號',
      '2026-08-01 11:45:00',
      '1991-03-26',
      '男',
      '2026-08-01 11:45:00'
   ),
   (
      84,
      '蔡佩君',
      'peijun@example.com',
      '0988234567',
      '324',
      '桃園市',
      '平鎮區',
      '新富街70號',
      '2026-08-02 15:15:00',
      '1996-10-08',
      '女',
      '2026-08-02 15:15:00'
   ),
   (
      85,
      '吳俊賢',
      'junxian@example.com',
      '0909234567',
      '325',
      '桃園市',
      '龍潭區',
      '中興路160號',
      '2026-08-03 09:20:00',
      '1995-07-19',
      '男',
      '2026-08-03 09:20:00'
   ),
   (
      86,
      '林怡君',
      'yijun3@example.com',
      '0919234567',
      '325',
      '桃園市',
      '龍潭區',
      '龍華路100號',
      '2026-08-04 13:45:00',
      '2000-04-12',
      '女',
      '2026-08-04 13:45:00'
   ),
   (
      87,
      '黃柏翰',
      'bohan@example.com',
      '0929234567',
      '326',
      '桃園市',
      '楊梅區',
      '校前路80號',
      '2026-08-05 16:00:00',
      '1993-09-28',
      '男',
      '2026-08-05 16:00:00'
   ),
   (
      88,
      '張雅君',
      'yajun@example.com',
      '0939234567',
      '326',
      '桃園市',
      '楊梅區',
      '中山北路120號',
      '2026-08-06 10:35:00',
      '1997-12-06',
      '女',
      '2026-08-06 10:35:00'
   ),
   (
      89,
      '陳冠廷',
      'guanting2@example.com',
      '0949234567',
      '327',
      '桃園市',
      '新屋區',
      '中山東路50號',
      '2026-08-07 14:25:00',
      '1998-05-31',
      '男',
      '2026-08-07 14:25:00'
   ),
   (
      90,
      '王怡文',
      'yiwen@example.com',
      '0959234567',
      '327',
      '桃園市',
      '新屋區',
      '民族路100號',
      '2026-08-08 11:05:00',
      '1995-11-16',
      '女',
      '2026-08-08 11:05:00'
   ),
   (
      91,
      '李宗翰',
      'zonghan2@example.com',
      '0969234567',
      '328',
      '桃園市',
      '觀音區',
      '成功路70號',
      '2026-08-09 15:45:00',
      '1992-08-24',
      '男',
      '2026-08-09 15:45:00'
   ),
   (
      92,
      '謝宜庭',
      'yiting3@example.com',
      '0979234567',
      '328',
      '桃園市',
      '觀音區',
      '文化路130號',
      '2026-08-10 09:55:00',
      '1999-02-17',
      '女',
      '2026-08-10 09:55:00'
   ),
   (
      93,
      '何冠宇',
      'guanyu2@example.com',
      '0989234567',
      '330',
      '桃園市',
      '桃園區',
      '同德路90號',
      '2026-08-11 13:30:00',
      '1996-06-21',
      '男',
      '2026-08-11 13:30:00'
   ),
   (
      94,
      '楊淑惠',
      'shuhui@example.com',
      '0901345678',
      '330',
      '桃園市',
      '桃園區',
      '中山路180號',
      '2026-08-12 10:20:00',
      '1990-12-03',
      '女',
      '2026-08-12 10:20:00'
   ),
   (
      95,
      '郭建宏',
      'jianhong2@example.com',
      '0911345678',
      '333',
      '桃園市',
      '龜山區',
      '文化三路150號',
      '2026-08-13 16:05:00',
      '1994-04-19',
      '男',
      '2026-08-13 16:05:00'
   ),
   (
      96,
      '劉佳玲',
      'jialing3@example.com',
      '0921345678',
      '333',
      '桃園市',
      '龜山區',
      '復興北路80號',
      '2026-08-14 11:40:00',
      '1998-09-02',
      '女',
      '2026-08-14 11:40:00'
   ),
   (
      97,
      '鄭志偉',
      'zhiwei2@example.com',
      '0931345678',
      '334',
      '桃園市',
      '八德區',
      '豐德路120號',
      '2026-08-15 14:50:00',
      '1991-06-13',
      '男',
      '2026-08-15 14:50:00'
   ),
   (
      98,
      '吳雅婷',
      'yating3@example.com',
      '0941345678',
      '334',
      '桃園市',
      '八德區',
      '建國路200號',
      '2026-08-16 09:25:00',
      '2000-03-05',
      '女',
      '2026-08-16 09:25:00'
   ),
   (
      99,
      '林志豪',
      'zhihao3@example.com',
      '0951345678',
      '335',
      '桃園市',
      '大溪區',
      '埔頂路100號',
      '2026-08-17 13:15:00',
      '1995-10-27',
      '男',
      '2026-08-17 13:15:00'
   ),
   (
      100,
      '張婉婷',
      'wanting2@example.com',
      '0961345678',
      '335',
      '桃園市',
      '大溪區',
      '介壽路60號',
      '2026-08-18 15:40:00',
      '1997-01-14',
      '女',
      '2026-08-18 15:40:00'
   ),
   (
      101,
      '王俊傑',
      'junjie3@example.com',
      '0971345678',
      '336',
      '桃園市',
      '復興區',
      '澤仁路40號',
      '2026-08-19 10:10:00',
      '1993-07-09',
      '男',
      '2026-08-19 10:10:00'
   ),
   (
      102,
      '陳思妤',
      'siyu2@example.com',
      '0981345678',
      '337',
      '桃園市',
      '大園區',
      '和平西路100號',
      '2026-08-20 14:35:00',
      '1999-11-21',
      '女',
      '2026-08-20 14:35:00'
   ),
   (
      103,
      '黃建霖',
      'jianlin@example.com',
      '0902345678',
      '337',
      '桃園市',
      '大園區',
      '中正東路160號',
      '2026-08-21 11:30:00',
      '1996-05-03',
      '男',
      '2026-08-21 11:30:00'
   ),
   (
      104,
      '李欣妤',
      'xinyu3@example.com',
      '0912345679',
      '338',
      '桃園市',
      '蘆竹區',
      '南昌路90號',
      '2026-08-22 16:20:00',
      '2001-08-12',
      '女',
      '2026-08-22 16:20:00'
   ),
   (
      105,
      '蔡承翰',
      'chenghan@example.com',
      '0922345678',
      '338',
      '桃園市',
      '蘆竹區',
      '五福一路130號',
      '2026-08-23 09:45:00',
      '1994-02-28',
      '男',
      '2026-08-23 09:45:00'
   ),
   (
      4,
      '周雅雯',
      'yawen2@example.com',
      '0932345678',
      '320',
      '桃園市',
      '中壢區',
      '莒光路70號',
      '2026-08-24 13:05:00',
      '1998-10-16',
      '女',
      '2026-08-24 13:05:00'
   ),
   (
      3,
      '許哲維',
      'zhewei2@example.com',
      '0942345678',
      '320',
      '桃園市',
      '中壢區',
      '環西路100號',
      '2026-08-25 15:25:00',
      '1992-11-08',
      '男',
      '2026-08-25 15:25:00'
   ),
   (
      2,
      '楊欣怡',
      'xinyi4@example.com',
      '0952345678',
      '324',
      '桃園市',
      '平鎮區',
      '振興西路80號',
      '2026-08-26 10:50:00',
      '1997-04-24',
      '女',
      '2026-08-26 10:50:00'
   ),
   (
      1,
      '羅俊豪',
      'junhao2@example.com',
      '0962345678',
      '325',
      '桃園市',
      '龍潭區',
      '中正路220號',
      '2026-08-26 14:30:00',
      '1995-09-13',
      '男',
      '2026-08-26 14:30:00'
   );
GO
   /* =========================================================
    9. room_type
    ========================================================= */
INSERT INTO room_type (
      type_name,
      bed_type,
      capacity,
      room_description,
      price_per_night
   )
VALUES (N'標準海景雙人房', N'1張雙人床', 2, N'含雙人早餐，擁有獨立海景陽台', 3500),
   (N'標準山景雙人房', N'1張雙人床', 2, N'含雙人早餐，享受靜謐山景', 3000),
   (
      N'雅緻海景雙床房',
      N'2張單人床',
      2,
      N'含雙人早餐，海景客房，適合商務或好友',
      3800
   ),
   (
      N'雅緻山景雙床房',
      N'2張單人床',
      2,
      N'含雙人早餐，山景客房，適合商務或好友',
      3300
   ),
   (
      N'溫馨海景家庭房',
      N'2張雙人床',
      4,
      N'含四人早餐，家庭出遊首選海景房',
      5800
   ),
   (
      N'溫馨山景家庭房',
      N'2張雙人床',
      4,
      N'含四人早餐，空間寬敞，綠意山景',
      5200
   ),
   (
      N'行政海景尊榮套房',
      N'1張加大雙人床',
      2,
      N'含雙人早餐與行政酒廊權益，高樓層無敵海景',
      8800
   ),
   (
      N'行政山景尊榮套房',
      N'1張加大雙人床',
      2,
      N'含雙人早餐與行政酒廊權益，高樓層環景山景',
      8000
   ),
   (
      N'豪華全景海景四人套房',
      N'2張加大雙人床',
      4,
      N'含四人早餐，獨立會客廳，高樓層雙面海景',
      13800
   ),
   (
      N'頂級海景皇家總統套房',
      N'1張特大雙人床',
      2,
      N'含專屬管家與豪華早餐，獨立露台與私人酒廊',
      32000
   );
GO
   /* =========================================================
    10. room
    ========================================================= */
INSERT INTO room (room_number, room_type_id, floor, room_status)
VALUES (N'10501', 1, 5, N'退房待清潔'),
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
    8. room_image
    ========================================================= */
INSERT INTO [dbo].[room_image] ([path], [image_description], [room_type_id])
VALUES (
      N'/uploads/images/room/roomtype.1.jpg',
      N'海景標準雙人房',
      1
   ),
   (
      N'/uploads/images/room/roomtype.2.jpg',
      N'山景標準雙人房',
      2
   ),
   (
      N'/uploads/images/room/roomtype.3.jpg',
      N'海景雅緻雙床房',
      3
   ),
   (
      N'/uploads/images/room/roomtype.4.jpg',
      N'山景雅緻雙床房',
      4
   ),
   (
      N'/uploads/images/room/roomtype.5.jpg',
      N'海景溫馨家庭房',
      5
   ),
   (
      N'/uploads/images/room/roomtype.6.jpg',
      N'山景溫馨家庭房',
      6
   ),
   (
      N'/uploads/images/room/roomtype.7.jpg',
      N'海景行政尊榮套房',
      7
   ),
   (
      N'/uploads/images/room/roomtype.8.jpg',
      N'山景行政尊榮套房',
      8
   ),
   (
      N'/uploads/images/room/roomtype.9.jpg',
      N'海景豪華全景四人套房',
      9
   ),
   (
      N'/uploads/images/room/roomtype.10.jpg',
      N'海景頂級皇家總統套房',
      10
   );
GO
   /* =========================================================
    11. category
    ========================================================= */
INSERT [dbo].[Category] ([Category_Name])
VALUES (N'客房備品')
GO
INSERT [dbo].[Category] ([Category_Name])
VALUES (N'紀念商品')
GO
INSERT [dbo].[Category] ([Category_Name])
VALUES (N'餐飲商品')
GO
   /* =========================================================
    12. product
    注意：product_id 為 IDENTITY，讓 SQL Server 自動產生 1~8
    資料庫欄位為 ImageURL
    ========================================================= */
INSERT INTO dbo.Product (
      Product_Name,
      Category_ID,
      Description,
      Price,
      Stock,
      ImageURL,
      Status
   )
VALUES (
      N'飯店馬克杯',
      1,
      N'飯店限定陶瓷馬克杯',
      350,
      60,
      NULL,
      N'ACTIVE'
   ),
   (
      N'飯店保溫瓶',
      1,
      N'不鏽鋼保溫瓶',
      599,
      20,
      NULL,
      N'ACTIVE'
   ),
   (
      N'飯店帆布袋',
      1,
      N'飯店紀念帆布袋',
      299,
      25,
      NULL,
      N'ACTIVE'
   ),
   (
      N'飯店鑰匙圈',
      1,
      N'飯店造型紀念鑰匙圈',
      150,
      50,
      NULL,
      N'ACTIVE'
   ),
   (
      N'飯店明信片',
      1,
      N'飯店風景紀念明信片',
      80,
      100,
      NULL,
      N'ACTIVE'
   ),
   (
      N'飯店浴袍',
      2,
      N'柔軟舒適飯店浴袍',
      899,
      15,
      NULL,
      N'ACTIVE'
   ),
   (
      N'牙刷組',
      2,
      N'客房盥洗牙刷組',
      50,
      100,
      NULL,
      N'ACTIVE'
   ),
   (
      N'刮鬍刀',
      2,
      N'一次性刮鬍刀',
      80,
      80,
      NULL,
      N'ACTIVE'
   );
GO
   /* =========================================================
    13. payment
    ========================================================= */
INSERT INTO dbo.payment (
      member_id,
      payment_method,
      transaction_id,
      total_price,
      payment_status,
      payment_time
   )
VALUES (
      1,
      N'信用卡',
      N'TEST-PAY-001',
      7600,
      N'PAID',
      '2026-08-10 15:30:00'
   ),
   (
      2,
      N 'LINE PAY',
      N'TEST-PAY-002',
      3200,
      N'PAID',
      '2026-08-11 12:20:00'
   ),
   (
      3,
      N'信用卡',
      N'TEST-PAY-003',
      5200,
      N 'PENDING',
      NULL
   ),
   (
      1,
      N'現金',
      N'TEST-PAY-004',
      450,
      N'PAID',
      '2026-08-13 19:00:00'
   ),
   (
      4,
      N'信用卡',
      N'TEST-PAY-005',
      14600,
      N'PAID',
      '2026-08-18 13:45:00'
   );
GO
   /* =========================================================
    14. booking_order
    ========================================================= */
INSERT INTO booking_order (
      member_id,
      booking_total_price,
      order_status,
      created_at,
      payment_id
   )
VALUES (12, 6500, N'訂單完成', '2026-08-17 09:15:20', 3),
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
INSERT INTO booking (
      booking_order_id,
      booking_price,
      check_in_date,
      check_out_date,
      guest_num,
      booking_status,
      room_id,
      room_type_id
   )
VALUES -- Order 1 (Total: 6500) -> room_type 1 (room_id 1), room_type 2 (room_id 13)
   (
      1,
      3500,
      '2026-08-19 15:00:00',
      '2026-08-20 11:00:00',
      2,
      N'已退房',
      1,
      1
   ),
   (
      1,
      3000,
      '2026-08-19 15:00:00',
      '2026-08-20 11:00:00',
      2,
      N'已退房',
      13,
      2
   ),
   -- Order 2 (Total: 9300) -> room_type 1 (room_id 2), room_type 5 (room_id 49)
   (
      2,
      3500,
      '2026-08-18 15:00:00',
      '2026-08-19 11:00:00',
      2,
      N'已退房',
      2,
      1
   ),
   (
      2,
      5800,
      '2026-08-18 15:00:00',
      '2026-08-19 11:00:00',
      4,
      N'已退房',
      49,
      5
   ),
   -- Order 3 (Total: 11000, Cancelled)
   (
      3,
      5800,
      '2026-08-20 15:00:00',
      '2026-08-21 11:00:00',
      4,
      N'已取消',
      NULL,
      5
   ),
   (
      3,
      5200,
      '2026-08-20 15:00:00',
      '2026-08-21 11:00:00',
      4,
      N'已取消',
      NULL,
      6
   ),
   -- Order 4 (Total: 7100) -> room_type 3 (room_id 5), room_type 4 (room_id 17)
   (
      4,
      3800,
      '2026-08-21 15:00:00',
      '2026-08-22 11:00:00',
      2,
      N'已退房',
      5,
      3
   ),
   (
      4,
      3300,
      '2026-08-21 15:00:00',
      '2026-08-22 11:00:00',
      2,
      N'已退房',
      17,
      4
   ),
   -- Order 5 (Total: 14600) -> room_type 7 (room_id 97), room_type 5 (room_id 50)
   (
      5,
      8800,
      '2026-08-20 15:00:00',
      '2026-08-21 11:00:00',
      2,
      N'已退房',
      97,
      7
   ),
   (
      5,
      5800,
      '2026-08-20 15:00:00',
      '2026-08-21 11:00:00',
      4,
      N'已退房',
      50,
      5
   ),
   -- Order 6 (Total: 6800, Cancelled)
   (
      6,
      3500,
      '2026-08-22 15:00:00',
      '2026-08-23 11:00:00',
      2,
      N'已取消',
      NULL,
      1
   ),
   (
      6,
      3300,
      '2026-08-22 15:00:00',
      '2026-08-23 11:00:00',
      2,
      N'已取消',
      NULL,
      4
   ),
   -- Order 7 (Total: 8800) -> room_type 8 (room_id 103), room_type 3 (room_id 6)
   (
      7,
      5000,
      '2026-08-21 15:00:00',
      '2026-08-22 11:00:00',
      2,
      N'已退房',
      103,
      8
   ),
   (
      7,
      3800,
      '2026-08-21 15:00:00',
      '2026-08-22 11:00:00',
      2,
      N'已退房',
      6,
      3
   ),
   -- Order 8 (Total: 12300) -> room_type 7 (room_id 98), room_type 1 (room_id 3)
   (
      8,
      8800,
      '2026-08-22 15:00:00',
      '2026-08-23 11:00:00',
      2,
      N'已入住',
      98,
      7
   ),
   (
      8,
      3500,
      '2026-08-22 15:00:00',
      '2026-08-23 11:00:00',
      2,
      N'已入住',
      3,
      1
   ),
   -- Order 9 (Total: 6500) -> room_type 1 (room_id 4), room_type 2 (room_id 14)
   (
      9,
      3500,
      '2026-08-22 15:00:00',
      '2026-08-23 11:00:00',
      2,
      N'已入住',
      4,
      1
   ),
   (
      9,
      3000,
      '2026-08-22 15:00:00',
      '2026-08-23 11:00:00',
      2,
      N'已入住',
      14,
      2
   ),
   -- Order 10 (Total: 17600, Cancelled)
   (
      10,
      8800,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      2,
      N'已取消',
      NULL,
      7
   ),
   (
      10,
      8800,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      2,
      N'已取消',
      NULL,
      7
   ),
   -- Order 11 (Total: 9600) -> room_type 5 (room_id 51), room_type 3 (room_id 7)
   (
      11,
      5800,
      '2026-08-22 15:00:00',
      '2026-08-23 11:00:00',
      4,
      N'已入住',
      51,
      5
   ),
   (
      11,
      3800,
      '2026-08-22 15:00:00',
      '2026-08-23 11:00:00',
      2,
      N'已入住',
      7,
      3
   ),
   -- Order 12 (Total: 7300) -> room_type 3 (room_id 8), room_type 1 (room_id 9)
   (
      12,
      3800,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      2,
      N'已入住',
      8,
      3
   ),
   (
      12,
      3500,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      2,
      N'已入住',
      9,
      1
   ),
   -- Order 13 (Total: 11800) -> room_type 8 (room_id 104), room_type 3 (room_id 11)
   (
      13,
      8000,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      2,
      N'已入住',
      104,
      8
   ),
   (
      13,
      3800,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      2,
      N'已入住',
      11,
      3
   ),
   -- Order 14 (Total: 6300, Cancelled)
   (
      14,
      3300,
      '2026-08-24 15:00:00',
      '2026-08-25 11:00:00',
      2,
      N'已取消',
      NULL,
      4
   ),
   (
      14,
      3000,
      '2026-08-24 15:00:00',
      '2026-08-25 11:00:00',
      2,
      N'已取消',
      NULL,
      2
   ),
   -- Order 15 (Total: 14000) -> room_type 7 (room_id 99), room_type 6 (room_id 61)
   (
      15,
      8800,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      2,
      N'已入住',
      99,
      7
   ),
   (
      15,
      5200,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      4,
      N'已入住',
      61,
      6
   ),
   -- Order 16 (Total: 8800) -> room_type 8 (room_id 105), room_type 3 (room_id 12)
   (
      16,
      5000,
      '2026-08-24 15:00:00',
      '2026-08-25 11:00:00',
      2,
      N'待入住',
      105,
      8
   ),
   (
      16,
      3800,
      '2026-08-24 15:00:00',
      '2026-08-25 11:00:00',
      2,
      N'待入住',
      12,
      3
   ),
   -- Order 17 (Total: 10400) -> room_type 6 (room_id 62), room_type 6 (room_id 63)
   (
      17,
      5200,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      4,
      N'已入住',
      62,
      6
   ),
   (
      17,
      5200,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      4,
      N'已入住',
      63,
      6
   ),
   -- Order 18 (Total: 6800, Cancelled)
   (
      18,
      3500,
      '2026-08-25 15:00:00',
      '2026-08-26 11:00:00',
      2,
      N'已取消',
      NULL,
      1
   ),
   (
      18,
      3300,
      '2026-08-25 15:00:00',
      '2026-08-26 11:00:00',
      2,
      N'已取消',
      NULL,
      4
   ),
   -- Order 19 (Total: 16800) -> room_type 9 (room_id 109), room_type 2 (room_id 15)
   (
      19,
      13800,
      '2026-08-24 15:00:00',
      '2026-08-25 11:00:00',
      4,
      N'待入住',
      109,
      9
   ),
   (
      19,
      3000,
      '2026-08-24 15:00:00',
      '2026-08-25 11:00:00',
      2,
      N'待入住',
      15,
      2
   ),
   -- Order 20 (Total: 9100) -> room_type 5 (room_id 52), room_type 4 (room_id 18)
   (
      20,
      5800,
      '2026-08-25 15:00:00',
      '2026-08-26 11:00:00',
      4,
      N'待入住',
      52,
      5
   ),
   (
      20,
      3300,
      '2026-08-25 15:00:00',
      '2026-08-26 11:00:00',
      2,
      N'待入住',
      18,
      4
   ),
   -- Order 21 (Total: 6500) -> room_type 1 (room_id 10), room_type 2 (room_id 16)
   (
      21,
      3500,
      '2026-08-24 15:00:00',
      '2026-08-25 11:00:00',
      2,
      N'待入住',
      10,
      1
   ),
   (
      21,
      3000,
      '2026-08-24 15:00:00',
      '2026-08-25 11:00:00',
      2,
      N'待入住',
      16,
      2
   ),
   -- Order 22 (Total: 12600, Cancelled)
   (
      22,
      8800,
      '2026-08-26 15:00:00',
      '2026-08-27 11:00:00',
      2,
      N'已取消',
      NULL,
      7
   ),
   (
      22,
      3800,
      '2026-08-26 15:00:00',
      '2026-08-27 11:00:00',
      2,
      N'已取消',
      NULL,
      3
   ),
   -- Order 23 (Total: 7100) -> room_type 3 (room_id 29), room_type 4 (room_id 19)
   (
      23,
      3800,
      '2026-08-25 15:00:00',
      '2026-08-26 11:00:00',
      2,
      N'待入住',
      29,
      3
   ),
   (
      23,
      3300,
      '2026-08-25 15:00:00',
      '2026-08-26 11:00:00',
      2,
      N'待入住',
      19,
      4
   ),
   -- Order 24 (Total: 14600) -> room_type 7 (room_id 100), room_type 5 (room_id 53)
   (
      24,
      8800,
      '2026-08-26 15:00:00',
      '2026-08-27 11:00:00',
      2,
      N'待入住',
      100,
      7
   ),
   (
      24,
      5800,
      '2026-08-26 15:00:00',
      '2026-08-27 11:00:00',
      4,
      N'待入住',
      53,
      5
   ),
   -- Order 25 (Total: 8800) -> room_type 8 (room_id 106), room_type 3 (room_id 30)
   (
      25,
      5000,
      '2026-08-25 15:00:00',
      '2026-08-26 11:00:00',
      2,
      N'待入住',
      106,
      8
   ),
   (
      25,
      3800,
      '2026-08-25 15:00:00',
      '2026-08-26 11:00:00',
      2,
      N'待入住',
      30,
      3
   ),
   -- Order 26 (Total: 11000, Cancelled)
   (
      26,
      5800,
      '2026-08-27 15:00:00',
      '2026-08-28 11:00:00',
      4,
      N'已取消',
      NULL,
      5
   ),
   (
      26,
      5200,
      '2026-08-27 15:00:00',
      '2026-08-28 11:00:00',
      4,
      N'已取消',
      NULL,
      6
   ),
   -- Order 27 (Total: 6800) -> room_type 1 (room_id 25), room_type 4 (room_id 20)
   (
      27,
      3500,
      '2026-08-26 15:00:00',
      '2026-08-27 11:00:00',
      2,
      N'待入住',
      25,
      1
   ),
   (
      27,
      3300,
      '2026-08-26 15:00:00',
      '2026-08-27 11:00:00',
      2,
      N'待入住',
      20,
      4
   ),
   -- Order 28 (Total: 9600) -> room_type 5 (room_id 54), room_type 3 (room_id 31)
   (
      28,
      5800,
      '2026-08-27 15:00:00',
      '2026-08-28 11:00:00',
      4,
      N'待入住',
      54,
      5
   ),
   (
      28,
      3800,
      '2026-08-27 15:00:00',
      '2026-08-28 11:00:00',
      2,
      N'待入住',
      31,
      3
   ),
   -- Order 29 (Total: 17600) -> room_type 7 (room_id 101), room_type 7 (room_id 102)
   (
      29,
      8800,
      '2026-08-26 15:00:00',
      '2026-08-27 11:00:00',
      2,
      N'待入住',
      101,
      7
   ),
   (
      29,
      8800,
      '2026-08-26 15:00:00',
      '2026-08-27 11:00:00',
      2,
      N'待入住',
      102,
      7
   ),
   -- Order 30 (Total: 7300) -> room_type 3 (room_id 32), room_type 1 (room_id 26)
   (
      30,
      3800,
      '2026-08-27 15:00:00',
      '2026-08-28 11:00:00',
      2,
      N'待入住',
      32,
      3
   ),
   (
      30,
      3500,
      '2026-08-27 15:00:00',
      '2026-08-28 11:00:00',
      2,
      N'待入住',
      26,
      1
   );
== == == =
VALUES -- 行政管理部 (department_id = 4, account_id 1~2)
   (4, 1, N'總經理'),
   -- Emp 1
   (4, 2, N'行政人資主管'),
   -- Emp 2
   -- 櫃檯/前台部 (department_id = 1, account_id 3~10)
   (1, 3, N'櫃檯主管'),
   -- Emp 3
   (1, 4, N'櫃檯專員(早班)'),
   -- Emp 4
   (1, 5, N'櫃檯專員(早班)'),
   -- Emp 5
   (1, 6, N'櫃檯專員(晚班)'),
   -- Emp 6
   (1, 7, N'櫃檯專員(晚班)'),
   -- Emp 7
   (1, 8, N'櫃檯專員(大夜)'),
   -- Emp 8
   (1, 9, N'禮賓接待員'),
   -- Emp 9
   (1, 10, N'車隊接送員'),
   -- Emp 10
   -- 客房/房務與維修部 (department_id = 2, account_id 11~28)
   (2, 11, N'房務主管'),
   -- Emp 11
   (2, 12, N'房務領班'),
   -- Emp 12
   (2, 13, N'房務專員'),
   -- Emp 13
   (2, 14, N'房務專員'),
   -- Emp 14
   (2, 15, N'房務專員'),
   -- Emp 15
   (2, 16, N'房務專員'),
   -- Emp 16
   (2, 17, N'房務專員'),
   -- Emp 17
   (2, 18, N'房務專員'),
   -- Emp 18
   (2, 19, N'房務專員'),
   -- Emp 19
   (2, 20, N'房務專員'),
   -- Emp 20
   (2, 21, N'房務專員'),
   -- Emp 21
   (2, 22, N'房務專員'),
   -- Emp 22
   (2, 23, N'房務專員'),
   -- Emp 23
   (2, 24, N'房務專員'),
   -- Emp 24
   (2, 25, N'公設清潔員'),
   -- Emp 25
   (2, 26, N'布巾洗滌員'),
   -- Emp 26
   (2, 27, N'機電維修員'),
   -- Emp 27
   (2, 28, N'水電維修員'),
   -- Emp 28
   -- 餐飲部 (department_id = 3, account_id 29~45)
   (3, 29, N'餐飲主管'),
   -- Emp 29
   (3, 30, N'主廚'),
   -- Emp 30
   (3, 31, N'副廚'),
   -- Emp 31
   (3, 32, N'砧板/冷盤廚師'),
   -- Emp 32
   (3, 33, N'熱炒/西餐廚師'),
   -- Emp 33
   (3, 34, N'點心/烘焙師'),
   -- Emp 34
   (3, 35, N'餐飲組長'),
   -- Emp 35
   (3, 36, N'餐飲服務員'),
   -- Emp 36
   (3, 37, N'餐飲服務員'),
   -- Emp 37
   (3, 38, N'餐飲服務員'),
   -- Emp 38
   (3, 39, N'餐飲服務員'),
   -- Emp 39
   (3, 40, N'餐飲服務員'),
   -- Emp 40
   (3, 41, N'餐飲服務員'),
   -- Emp 41
   (3, 42, N'吧檯調酒師'),
   -- Emp 42
   (3, 43, N'吧檯助理'),
   -- Emp 43
   (3, 44, N'洗碗員'),
   -- Emp 44
   (3, 45, N'備料清潔員');
-- Emp 45
GO
   /* =========================================================
    5. employee_permission
    注意：
    目前你的 FK 是 employee.account_id
    所以下面的 employee_id 使用 1、2、3、4
    ========================================================= */
SET IDENTITY_INSERT [dbo].[employee_permission] ON;
GO
INSERT INTO employee_permission (permission_id, employee_id)
VALUES -- Emp 1: 總經理 / 管理員 (擁有全部 5 項權限)
   (1, 1),
   -- 房間管理
   (2, 1),
   -- 訂房管理
   (3, 1),
   -- 餐廳管理
   (4, 1),
   -- 會員管理
   (5, 1),
   -- 訂單管理
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
VALUES (46),
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
INSERT INTO profile (
      account_id,
      name,
      email,
      phone,
      zipcode,
      city,
      district,
      address,
      created_at,
      birthday,
      gender,
      updated_at
   )
VALUES (
      5,
      '王小明',
      'ming@example.com',
      '0912345678',
      '320',
      '桃園市',
      '中壢區',
      '中央西路一段100號',
      '2026-08-01 10:00:00',
      '1998-05-12',
      '男',
      '2026-08-01 10:00:00'
   ),
   (
      6,
      '陳小華',
      'hua@example.com',
      '0923456789',
      '320',
      '桃園市',
      '中壢區',
      '中美路200號',
      '2026-08-02 11:00:00',
      '1995-08-20',
      '女',
      '2026-08-02 11:00:00'
   ),
   (
      7,
      '林大偉',
      'david@example.com',
      '0934567890',
      '330',
      '桃園市',
      '桃園區',
      '中正路300號',
      '2026-08-03 14:00:00',
      '2000-03-15',
      '男',
      '2026-08-03 14:00:00'
   ),
   (
      8,
      '張雅婷',
      'yating@example.com',
      '0945678901',
      '330',
      '桃園市',
      '桃園區',
      '民生路120號',
      '2026-08-04 09:30:00',
      '1997-07-22',
      '女',
      '2026-08-04 09:30:00'
   ),
   (
      9,
      '李承翰',
      'han@example.com',
      '0956789012',
      '320',
      '桃園市',
      '中壢區',
      '延平路88號',
      '2026-08-05 13:20:00',
      '1999-11-03',
      '男',
      '2026-08-05 13:20:00'
   ),
   (
      10,
      '黃詩涵',
      'shihan@example.com',
      '0967890123',
      '324',
      '桃園市',
      '平鎮區',
      '環南路50號',
      '2026-08-06 15:10:00',
      '1996-02-18',
      '女',
      '2026-08-06 15:10:00'
   ),
   (
      11,
      '吳俊傑',
      'junjie@example.com',
      '0978901234',
      '324',
      '桃園市',
      '平鎮區',
      '中豐路160號',
      '2026-08-07 10:40:00',
      '1994-09-25',
      '男',
      '2026-08-07 10:40:00'
   ),
   (
      12,
      '周怡君',
      'yijun@example.com',
      '0989012345',
      '300',
      '新竹市',
      '東區',
      '光復路一段50號',
      '2026-08-08 11:30:00',
      '1998-12-10',
      '女',
      '2026-08-08 11:30:00'
   ),
   (
      13,
      '許家豪',
      'jiahao@example.com',
      '0901234567',
      '300',
      '新竹市',
      '北區',
      '中正路180號',
      '2026-08-09 14:20:00',
      '1993-04-08',
      '男',
      '2026-08-09 14:20:00'
   ),
   (
      14,
      '鄭惠文',
      'huiwen@example.com',
      '0911234567',
      '302',
      '新竹縣',
      '竹北市',
      '縣政二路100號',
      '2026-08-10 09:10:00',
      '2001-06-30',
      '女',
      '2026-08-10 09:10:00'
   ),
   (
      15,
      '蔡明哲',
      'mingzhe@example.com',
      '0921234567',
      '302',
      '新竹縣',
      '竹北市',
      '光明六路200號',
      '2026-08-11 16:00:00',
      '1992-01-15',
      '男',
      '2026-08-11 16:00:00'
   ),
   (
      16,
      '楊淑芬',
      'shufen@example.com',
      '0931234567',
      '300',
      '新竹市',
      '東區',
      '建功一路30號',
      '2026-08-12 10:15:00',
      '1990-10-05',
      '女',
      '2026-08-12 10:15:00'
   ),
   (
      17,
      '曾冠宇',
      'guanyu@example.com',
      '0941234567',
      '330',
      '桃園市',
      '桃園區',
      '春日路260號',
      '2026-08-13 13:45:00',
      '1997-03-21',
      '男',
      '2026-08-13 13:45:00'
   ),
   (
      18,
      '劉欣怡',
      'xinyi@example.com',
      '0951234567',
      '333',
      '桃園市',
      '龜山區',
      '文化一路80號',
      '2026-08-14 11:05:00',
      '1999-08-14',
      '女',
      '2026-08-14 11:05:00'
   ),
   (
      19,
      '郭志偉',
      'zhiwei@example.com',
      '0961234567',
      '333',
      '桃園市',
      '龜山區',
      '復興一路120號',
      '2026-08-15 15:25:00',
      '1995-05-19',
      '男',
      '2026-08-15 15:25:00'
   ),
   (
      20,
      '謝佩珊',
      'peishan@example.com',
      '0971234567',
      '334',
      '桃園市',
      '八德區',
      '介壽路一段90號',
      '2026-08-16 09:50:00',
      '1996-11-27',
      '女',
      '2026-08-16 09:50:00'
   ),
   (
      21,
      '何俊宏',
      'junhong@example.com',
      '0981234567',
      '334',
      '桃園市',
      '八德區',
      '廣福路150號',
      '2026-08-17 12:10:00',
      '1991-07-06',
      '男',
      '2026-08-17 12:10:00'
   ),
   (
      22,
      '徐雅雯',
      'yawen@example.com',
      '0902234567',
      '335',
      '桃園市',
      '大溪區',
      '中正東路70號',
      '2026-08-18 14:35:00',
      '2000-02-12',
      '女',
      '2026-08-18 14:35:00'
   ),
   (
      23,
      '羅偉倫',
      'weilun@example.com',
      '0912234567',
      '335',
      '桃園市',
      '大溪區',
      '和平路110號',
      '2026-08-19 10:25:00',
      '1994-06-23',
      '男',
      '2026-08-19 10:25:00'
   ),
   (
      24,
      '林佳蓉',
      'jiarong@example.com',
      '0922234567',
      '336',
      '桃園市',
      '復興區',
      '中正路25號',
      '2026-08-20 13:15:00',
      '1998-09-17',
      '女',
      '2026-08-20 13:15:00'
   ),
   (
      25,
      '張志豪',
      'zhihao@example.com',
      '0932234567',
      '337',
      '桃園市',
      '大園區',
      '中山南路130號',
      '2026-08-21 09:40:00',
      '1993-12-01',
      '男',
      '2026-08-21 09:40:00'
   ),
   (
      26,
      '林怡萱',
      'yixuan@example.com',
      '0942234567',
      '337',
      '桃園市',
      '大園區',
      '和平西路75號',
      '2026-08-22 11:55:00',
      '2001-04-16',
      '女',
      '2026-08-22 11:55:00'
   ),
   (
      27,
      '陳柏宇',
      'boyu@example.com',
      '0952234567',
      '338',
      '桃園市',
      '蘆竹區',
      '南山路100號',
      '2026-08-23 15:05:00',
      '1997-10-29',
      '男',
      '2026-08-23 15:05:00'
   ),
   (
      28,
      '王思妤',
      'siyu@example.com',
      '0962234567',
      '338',
      '桃園市',
      '蘆竹區',
      '南崁路220號',
      '2026-08-24 10:35:00',
      '1999-01-09',
      '女',
      '2026-08-24 10:35:00'
   ),
   (
      29,
      '黃柏勳',
      'boxun@example.com',
      '0972234567',
      '320',
      '桃園市',
      '中壢區',
      '新生路180號',
      '2026-08-25 13:50:00',
      '1996-05-26',
      '男',
      '2026-08-25 13:50:00'
   ),
   (
      30,
      '吳佳玲',
      'jialing@example.com',
      '0982234567',
      '320',
      '桃園市',
      '中壢區',
      '慈惠三街60號',
      '2026-08-26 09:20:00',
      '1995-03-11',
      '女',
      '2026-08-26 09:20:00'
   ),
   (
      31,
      '李冠廷',
      'guanting@example.com',
      '0903234567',
      '320',
      '桃園市',
      '中壢區',
      '環中東路240號',
      '2026-08-01 14:10:00',
      '1998-07-03',
      '男',
      '2026-08-01 14:10:00'
   ),
   (
      32,
      '陳怡安',
      'yian@example.com',
      '0913234567',
      '324',
      '桃園市',
      '平鎮區',
      '金陵路88號',
      '2026-08-02 10:05:00',
      '2000-11-18',
      '女',
      '2026-08-02 10:05:00'
   ),
   (
      33,
      '周建宏',
      'jianhong@example.com',
      '0923234567',
      '324',
      '桃園市',
      '平鎮區',
      '育達路150號',
      '2026-08-03 15:30:00',
      '1992-08-07',
      '男',
      '2026-08-03 15:30:00'
   ),
   (
      34,
      '許芳瑜',
      'fangyu@example.com',
      '0933234567',
      '325',
      '桃園市',
      '龍潭區',
      '中正路90號',
      '2026-08-04 11:20:00',
      '1997-02-25',
      '女',
      '2026-08-04 11:20:00'
   ),
   (
      35,
      '鄭凱文',
      'kaiwen@example.com',
      '0943234567',
      '325',
      '桃園市',
      '龍潭區',
      '北龍路170號',
      '2026-08-05 13:00:00',
      '1994-12-14',
      '男',
      '2026-08-05 13:00:00'
   ),
   (
      36,
      '蔡宜庭',
      'yiting@example.com',
      '0953234567',
      '326',
      '桃園市',
      '楊梅區',
      '大成路120號',
      '2026-08-06 09:45:00',
      '2001-09-08',
      '女',
      '2026-08-06 09:45:00'
   ),
   (
      37,
      '何宗翰',
      'zonghan@example.com',
      '0963234567',
      '326',
      '桃園市',
      '楊梅區',
      '新成路200號',
      '2026-08-07 16:15:00',
      '1993-05-30',
      '男',
      '2026-08-07 16:15:00'
   ),
   (
      38,
      '謝欣妤',
      'xinyu@example.com',
      '0973234567',
      '327',
      '桃園市',
      '新屋區',
      '中山西路100號',
      '2026-08-08 10:50:00',
      '1999-06-12',
      '女',
      '2026-08-08 10:50:00'
   ),
   (
      39,
      '林俊佑',
      'junyou@example.com',
      '0983234567',
      '327',
      '桃園市',
      '新屋區',
      '中正路50號',
      '2026-08-09 14:45:00',
      '1995-01-28',
      '男',
      '2026-08-09 14:45:00'
   ),
   (
      40,
      '張瑞芳',
      'ruifang@example.com',
      '0904234567',
      '328',
      '桃園市',
      '觀音區',
      '中山路180號',
      '2026-08-10 11:35:00',
      '1996-10-21',
      '女',
      '2026-08-10 11:35:00'
   ),
   (
      41,
      '王建國',
      'jianguo@example.com',
      '0914234567',
      '328',
      '桃園市',
      '觀音區',
      '大觀路90號',
      '2026-08-11 13:25:00',
      '1989-04-05',
      '男',
      '2026-08-11 13:25:00'
   ),
   (
      42,
      '劉佳穎',
      'jiaying@example.com',
      '0924234567',
      '330',
      '桃園市',
      '桃園區',
      '成功路100號',
      '2026-08-12 09:15:00',
      '2000-08-19',
      '女',
      '2026-08-12 09:15:00'
   ),
   (
      43,
      '黃冠霖',
      'guanlin@example.com',
      '0934234567',
      '330',
      '桃園市',
      '桃園區',
      '復興路250號',
      '2026-08-13 15:40:00',
      '1997-11-05',
      '男',
      '2026-08-13 15:40:00'
   ),
   (
      44,
      '吳佩蓉',
      'peirong@example.com',
      '0944234567',
      '333',
      '桃園市',
      '龜山區',
      '萬壽路80號',
      '2026-08-14 10:30:00',
      '1998-03-22',
      '女',
      '2026-08-14 10:30:00'
   ),
   (
      45,
      '陳威廷',
      'weiting@example.com',
      '0954234567',
      '333',
      '桃園市',
      '龜山區',
      '自強東路140號',
      '2026-08-15 14:00:00',
      '1994-07-17',
      '男',
      '2026-08-15 14:00:00'
   ),
   (
      46,
      '林欣怡',
      'hsinyi@example.com',
      '0964234567',
      '334',
      '桃園市',
      '八德區',
      '忠勇街60號',
      '2026-08-16 11:10:00',
      '2001-02-03',
      '女',
      '2026-08-16 11:10:00'
   ),
   (
      47,
      '張凱翔',
      'kaixiang@example.com',
      '0974234567',
      '334',
      '桃園市',
      '八德區',
      '介壽路二段300號',
      '2026-08-17 16:30:00',
      '1996-09-12',
      '男',
      '2026-08-17 16:30:00'
   ),
   (
      48,
      '楊雅婷',
      'yangting@example.com',
      '0984234567',
      '335',
      '桃園市',
      '大溪區',
      '員林路120號',
      '2026-08-18 09:35:00',
      '1999-12-25',
      '女',
      '2026-08-18 09:35:00'
   ),
   (
      49,
      '郭俊傑',
      'junjie2@example.com',
      '0905234567',
      '335',
      '桃園市',
      '大溪區',
      '慈湖路180號',
      '2026-08-19 13:05:00',
      '1992-06-18',
      '男',
      '2026-08-19 13:05:00'
   ),
   (
      50,
      '徐婉婷',
      'wanting@example.com',
      '0915234567',
      '336',
      '桃園市',
      '復興區',
      '三民路30號',
      '2026-08-20 10:45:00',
      '1997-04-27',
      '女',
      '2026-08-20 10:45:00'
   ),
   (
      51,
      '羅子軒',
      'zixuan@example.com',
      '0925234567',
      '337',
      '桃園市',
      '大園區',
      '航站南路50號',
      '2026-08-21 14:25:00',
      '2000-10-16',
      '男',
      '2026-08-21 14:25:00'
   ),
   (
      52,
      '謝宜蓁',
      'yizhen@example.com',
      '0935234567',
      '337',
      '桃園市',
      '大園區',
      '中華路90號',
      '2026-08-22 11:50:00',
      '1995-08-03',
      '女',
      '2026-08-22 11:50:00'
   ),
   (
      53,
      '何明哲',
      'mingzhe2@example.com',
      '0945234567',
      '338',
      '桃園市',
      '蘆竹區',
      '忠孝西路110號',
      '2026-08-23 15:15:00',
      '1993-03-29',
      '男',
      '2026-08-23 15:15:00'
   ),
   (
      54,
      '蔡佳穎',
      'jiaying2@example.com',
      '0955234567',
      '338',
      '桃園市',
      '蘆竹區',
      '南竹路160號',
      '2026-08-24 09:55:00',
      '1998-11-11',
      '女',
      '2026-08-24 09:55:00'
   ),
   (
      55,
      '鄭宇翔',
      'yuxiang@example.com',
      '0965234567',
      '320',
      '桃園市',
      '中壢區',
      '中山東路100號',
      '2026-08-25 13:40:00',
      '1996-01-20',
      '男',
      '2026-08-25 13:40:00'
   ),
   (
      56,
      '周怡萱',
      'yixuan2@example.com',
      '0975234567',
      '320',
      '桃園市',
      '中壢區',
      '實踐路80號',
      '2026-08-26 10:20:00',
      '2001-05-06',
      '女',
      '2026-08-26 10:20:00'
   ),
   (
      57,
      '許博翔',
      'boxiang@example.com',
      '0985234567',
      '324',
      '桃園市',
      '平鎮區',
      '振興路130號',
      '2026-08-01 15:00:00',
      '1994-09-14',
      '男',
      '2026-08-01 15:00:00'
   ),
   (
      58,
      '李佳玲',
      'jialing2@example.com',
      '0906234567',
      '324',
      '桃園市',
      '平鎮區',
      '廣德街70號',
      '2026-08-02 11:40:00',
      '1999-07-28',
      '女',
      '2026-08-02 11:40:00'
   ),
   (
      59,
      '吳宗憲',
      'zongxian@example.com',
      '0916234567',
      '325',
      '桃園市',
      '龍潭區',
      '龍元路100號',
      '2026-08-03 14:15:00',
      '1991-12-09',
      '男',
      '2026-08-03 14:15:00'
   ),
   (
      60,
      '林詩婷',
      'shiting@example.com',
      '0926234567',
      '325',
      '桃園市',
      '龍潭區',
      '東龍路150號',
      '2026-08-04 09:25:00',
      '1997-06-04',
      '女',
      '2026-08-04 09:25:00'
   ),
   (
      61,
      '黃志豪',
      'zhihao2@example.com',
      '0936234567',
      '326',
      '桃園市',
      '楊梅區',
      '瑞溪路80號',
      '2026-08-05 16:05:00',
      '1995-10-19',
      '男',
      '2026-08-05 16:05:00'
   ),
   (
      62,
      '陳怡君',
      'yijun2@example.com',
      '0946234567',
      '326',
      '桃園市',
      '楊梅區',
      '新農街120號',
      '2026-08-06 10:10:00',
      '2000-01-31',
      '女',
      '2026-08-06 10:10:00'
   ),
   (
      63,
      '王俊凱',
      'junkai@example.com',
      '0956234567',
      '327',
      '桃園市',
      '新屋區',
      '中興路60號',
      '2026-08-07 13:35:00',
      '1993-08-22',
      '男',
      '2026-08-07 13:35:00'
   ),
   (
      64,
      '張淑貞',
      'shuzhen@example.com',
      '0966234567',
      '327',
      '桃園市',
      '新屋區',
      '永安路90號',
      '2026-08-08 11:25:00',
      '1990-05-17',
      '女',
      '2026-08-08 11:25:00'
   ),
   (
      65,
      '林家豪',
      'jiahao2@example.com',
      '0976234567',
      '328',
      '桃園市',
      '觀音區',
      '草漯路130號',
      '2026-08-09 15:50:00',
      '1998-02-08',
      '男',
      '2026-08-09 15:50:00'
   ),
   (
      66,
      '吳佳蓉',
      'jiarong2@example.com',
      '0986234567',
      '328',
      '桃園市',
      '觀音區',
      '大同路50號',
      '2026-08-10 09:05:00',
      '1996-12-20',
      '女',
      '2026-08-10 09:05:00'
   ),
   (
      67,
      '蔡承恩',
      'chengen@example.com',
      '0907234567',
      '330',
      '桃園市',
      '桃園區',
      '南平路180號',
      '2026-08-11 14:40:00',
      '2001-03-13',
      '男',
      '2026-08-11 14:40:00'
   ),
   (
      68,
      '楊欣怡',
      'xinyi2@example.com',
      '0917234567',
      '330',
      '桃園市',
      '桃園區',
      '大興西路100號',
      '2026-08-12 10:55:00',
      '1999-09-24',
      '女',
      '2026-08-12 10:55:00'
   ),
   (
      69,
      '何冠霖',
      'guanlin2@example.com',
      '0927234567',
      '333',
      '桃園市',
      '龜山區',
      '德明路70號',
      '2026-08-13 13:20:00',
      '1994-11-07',
      '男',
      '2026-08-13 13:20:00'
   ),
   (
      70,
      '徐佳琪',
      'jiaqi@example.com',
      '0937234567',
      '333',
      '桃園市',
      '龜山區',
      '文青路120號',
      '2026-08-14 16:10:00',
      '1998-06-26',
      '女',
      '2026-08-14 16:10:00'
   ),
   (
      71,
      '鄭凱傑',
      'kaijie@example.com',
      '0947234567',
      '334',
      '桃園市',
      '八德區',
      '廣興路90號',
      '2026-08-15 09:30:00',
      '1992-02-15',
      '男',
      '2026-08-15 09:30:00'
   ),
   (
      72,
      '謝雅婷',
      'yating2@example.com',
      '0957234567',
      '334',
      '桃園市',
      '八德區',
      '和平路210號',
      '2026-08-16 12:45:00',
      '1997-10-03',
      '女',
      '2026-08-16 12:45:00'
   ),
   (
      73,
      '羅偉豪',
      'weihao@example.com',
      '0967234567',
      '335',
      '桃園市',
      '大溪區',
      '康莊路140號',
      '2026-08-17 15:20:00',
      '1995-04-11',
      '男',
      '2026-08-17 15:20:00'
   ),
   (
      74,
      '劉怡伶',
      'yiling@example.com',
      '0977234567',
      '335',
      '桃園市',
      '大溪區',
      '仁和路80號',
      '2026-08-18 10:00:00',
      '2000-07-29',
      '女',
      '2026-08-18 10:00:00'
   ),
   (
      75,
      '郭柏廷',
      'boting@example.com',
      '0987234567',
      '336',
      '桃園市',
      '復興區',
      '羅浮路30號',
      '2026-08-19 14:55:00',
      '1993-01-17',
      '男',
      '2026-08-19 14:55:00'
   ),
   (
      76,
      '黃鈺婷',
      'yuting@example.com',
      '0908234567',
      '337',
      '桃園市',
      '大園區',
      '三民路110號',
      '2026-08-20 11:15:00',
      '1996-08-09',
      '女',
      '2026-08-20 11:15:00'
   ),
   (
      77,
      '張哲維',
      'zhewei@example.com',
      '0918234567',
      '337',
      '桃園市',
      '大園區',
      '國際路200號',
      '2026-08-21 16:25:00',
      '1999-05-23',
      '男',
      '2026-08-21 16:25:00'
   ),
   (
      78,
      '陳妍希',
      'yanxi@example.com',
      '0928234567',
      '338',
      '桃園市',
      '蘆竹區',
      '南福街60號',
      '2026-08-22 09:40:00',
      '2001-11-02',
      '女',
      '2026-08-22 09:40:00'
   ),
   (
      79,
      '林昱辰',
      'yuchen@example.com',
      '0938234567',
      '338',
      '桃園市',
      '蘆竹區',
      '大竹路150號',
      '2026-08-23 13:10:00',
      '1997-03-08',
      '男',
      '2026-08-23 13:10:00'
   ),
   (
      80,
      '王郁婷',
      'yuting2@example.com',
      '0948234567',
      '320',
      '桃園市',
      '中壢區',
      '龍東路100號',
      '2026-08-24 15:35:00',
      '1998-12-18',
      '女',
      '2026-08-24 15:35:00'
   ),
   (
      81,
      '李俊豪',
      'junhao@example.com',
      '0958234567',
      '320',
      '桃園市',
      '中壢區',
      '榮民路180號',
      '2026-08-25 10:25:00',
      '1994-06-05',
      '男',
      '2026-08-25 10:25:00'
   ),
   (
      82,
      '周怡婷',
      'yiting2@example.com',
      '0968234567',
      '320',
      '桃園市',
      '中壢區',
      '中北路200號',
      '2026-08-26 14:05:00',
      '1999-09-15',
      '女',
      '2026-08-26 14:05:00'
   ),
   (
      83,
      '許志明',
      'zhiming@example.com',
      '0978234567',
      '324',
      '桃園市',
      '平鎮區',
      '民族路90號',
      '2026-08-01 11:45:00',
      '1991-03-26',
      '男',
      '2026-08-01 11:45:00'
   ),
   (
      84,
      '蔡佩君',
      'peijun@example.com',
      '0988234567',
      '324',
      '桃園市',
      '平鎮區',
      '新富街70號',
      '2026-08-02 15:15:00',
      '1996-10-08',
      '女',
      '2026-08-02 15:15:00'
   ),
   (
      85,
      '吳俊賢',
      'junxian@example.com',
      '0909234567',
      '325',
      '桃園市',
      '龍潭區',
      '中興路160號',
      '2026-08-03 09:20:00',
      '1995-07-19',
      '男',
      '2026-08-03 09:20:00'
   ),
   (
      86,
      '林怡君',
      'yijun3@example.com',
      '0919234567',
      '325',
      '桃園市',
      '龍潭區',
      '龍華路100號',
      '2026-08-04 13:45:00',
      '2000-04-12',
      '女',
      '2026-08-04 13:45:00'
   ),
   (
      87,
      '黃柏翰',
      'bohan@example.com',
      '0929234567',
      '326',
      '桃園市',
      '楊梅區',
      '校前路80號',
      '2026-08-05 16:00:00',
      '1993-09-28',
      '男',
      '2026-08-05 16:00:00'
   ),
   (
      88,
      '張雅君',
      'yajun@example.com',
      '0939234567',
      '326',
      '桃園市',
      '楊梅區',
      '中山北路120號',
      '2026-08-06 10:35:00',
      '1997-12-06',
      '女',
      '2026-08-06 10:35:00'
   ),
   (
      89,
      '陳冠廷',
      'guanting2@example.com',
      '0949234567',
      '327',
      '桃園市',
      '新屋區',
      '中山東路50號',
      '2026-08-07 14:25:00',
      '1998-05-31',
      '男',
      '2026-08-07 14:25:00'
   ),
   (
      90,
      '王怡文',
      'yiwen@example.com',
      '0959234567',
      '327',
      '桃園市',
      '新屋區',
      '民族路100號',
      '2026-08-08 11:05:00',
      '1995-11-16',
      '女',
      '2026-08-08 11:05:00'
   ),
   (
      91,
      '李宗翰',
      'zonghan2@example.com',
      '0969234567',
      '328',
      '桃園市',
      '觀音區',
      '成功路70號',
      '2026-08-09 15:45:00',
      '1992-08-24',
      '男',
      '2026-08-09 15:45:00'
   ),
   (
      92,
      '謝宜庭',
      'yiting3@example.com',
      '0979234567',
      '328',
      '桃園市',
      '觀音區',
      '文化路130號',
      '2026-08-10 09:55:00',
      '1999-02-17',
      '女',
      '2026-08-10 09:55:00'
   ),
   (
      93,
      '何冠宇',
      'guanyu2@example.com',
      '0989234567',
      '330',
      '桃園市',
      '桃園區',
      '同德路90號',
      '2026-08-11 13:30:00',
      '1996-06-21',
      '男',
      '2026-08-11 13:30:00'
   ),
   (
      94,
      '楊淑惠',
      'shuhui@example.com',
      '0901345678',
      '330',
      '桃園市',
      '桃園區',
      '中山路180號',
      '2026-08-12 10:20:00',
      '1990-12-03',
      '女',
      '2026-08-12 10:20:00'
   ),
   (
      95,
      '郭建宏',
      'jianhong2@example.com',
      '0911345678',
      '333',
      '桃園市',
      '龜山區',
      '文化三路150號',
      '2026-08-13 16:05:00',
      '1994-04-19',
      '男',
      '2026-08-13 16:05:00'
   ),
   (
      96,
      '劉佳玲',
      'jialing3@example.com',
      '0921345678',
      '333',
      '桃園市',
      '龜山區',
      '復興北路80號',
      '2026-08-14 11:40:00',
      '1998-09-02',
      '女',
      '2026-08-14 11:40:00'
   ),
   (
      97,
      '鄭志偉',
      'zhiwei2@example.com',
      '0931345678',
      '334',
      '桃園市',
      '八德區',
      '豐德路120號',
      '2026-08-15 14:50:00',
      '1991-06-13',
      '男',
      '2026-08-15 14:50:00'
   ),
   (
      98,
      '吳雅婷',
      'yating3@example.com',
      '0941345678',
      '334',
      '桃園市',
      '八德區',
      '建國路200號',
      '2026-08-16 09:25:00',
      '2000-03-05',
      '女',
      '2026-08-16 09:25:00'
   ),
   (
      99,
      '林志豪',
      'zhihao3@example.com',
      '0951345678',
      '335',
      '桃園市',
      '大溪區',
      '埔頂路100號',
      '2026-08-17 13:15:00',
      '1995-10-27',
      '男',
      '2026-08-17 13:15:00'
   ),
   (
      100,
      '張婉婷',
      'wanting2@example.com',
      '0961345678',
      '335',
      '桃園市',
      '大溪區',
      '介壽路60號',
      '2026-08-18 15:40:00',
      '1997-01-14',
      '女',
      '2026-08-18 15:40:00'
   ),
   (
      101,
      '王俊傑',
      'junjie3@example.com',
      '0971345678',
      '336',
      '桃園市',
      '復興區',
      '澤仁路40號',
      '2026-08-19 10:10:00',
      '1993-07-09',
      '男',
      '2026-08-19 10:10:00'
   ),
   (
      102,
      '陳思妤',
      'siyu2@example.com',
      '0981345678',
      '337',
      '桃園市',
      '大園區',
      '和平西路100號',
      '2026-08-20 14:35:00',
      '1999-11-21',
      '女',
      '2026-08-20 14:35:00'
   ),
   (
      103,
      '黃建霖',
      'jianlin@example.com',
      '0902345678',
      '337',
      '桃園市',
      '大園區',
      '中正東路160號',
      '2026-08-21 11:30:00',
      '1996-05-03',
      '男',
      '2026-08-21 11:30:00'
   ),
   (
      104,
      '李欣妤',
      'xinyu3@example.com',
      '0912345679',
      '338',
      '桃園市',
      '蘆竹區',
      '南昌路90號',
      '2026-08-22 16:20:00',
      '2001-08-12',
      '女',
      '2026-08-22 16:20:00'
   ),
   (
      105,
      '蔡承翰',
      'chenghan@example.com',
      '0922345678',
      '338',
      '桃園市',
      '蘆竹區',
      '五福一路130號',
      '2026-08-23 09:45:00',
      '1994-02-28',
      '男',
      '2026-08-23 09:45:00'
   ),
   (
      4,
      '周雅雯',
      'yawen2@example.com',
      '0932345678',
      '320',
      '桃園市',
      '中壢區',
      '莒光路70號',
      '2026-08-24 13:05:00',
      '1998-10-16',
      '女',
      '2026-08-24 13:05:00'
   ),
   (
      3,
      '許哲維',
      'zhewei2@example.com',
      '0942345678',
      '320',
      '桃園市',
      '中壢區',
      '環西路100號',
      '2026-08-25 15:25:00',
      '1992-11-08',
      '男',
      '2026-08-25 15:25:00'
   ),
   (
      2,
      '楊欣怡',
      'xinyi4@example.com',
      '0952345678',
      '324',
      '桃園市',
      '平鎮區',
      '振興西路80號',
      '2026-08-26 10:50:00',
      '1997-04-24',
      '女',
      '2026-08-26 10:50:00'
   ),
   (
      1,
      '羅俊豪',
      'junhao2@example.com',
      '0962345678',
      '325',
      '桃園市',
      '龍潭區',
      '中正路220號',
      '2026-08-26 14:30:00',
      '1995-09-13',
      '男',
      '2026-08-26 14:30:00'
   );
GO
   /* =========================================================
    8. room_image
    ========================================================= */
INSERT INTO [dbo].[room_image] ([path], [image_description], [room_type_id])
VALUES (
      N'/uploads/images/room/roomtype.1.jpg',
      N'海景標準雙人房',
      1
   ),
   (
      N'/uploads/images/room/roomtype.2.jpg',
      N'山景標準雙人房',
      2
   ),
   (
      N'/uploads/images/room/roomtype.3.jpg',
      N'海景雅緻雙床房',
      3
   ),
   (
      N'/uploads/images/room/roomtype.4.jpg',
      N'山景雅緻雙床房',
      4
   ),
   (
      N'/uploads/images/room/roomtype.5.jpg',
      N'海景溫馨家庭房',
      5
   ),
   (
      N'/uploads/images/room/roomtype.6.jpg',
      N'山景溫馨家庭房',
      6
   ),
   (
      N'/uploads/images/room/roomtype.7.jpg',
      N'海景行政尊榮套房',
      7
   ),
   (
      N'/uploads/images/room/roomtype.8.jpg',
      N'山景行政尊榮套房',
      8
   ),
   (
      N'/uploads/images/room/roomtype.9.jpg',
      N'海景豪華全景四人套房',
      9
   ),
   (
      N'/uploads/images/room/roomtype.10.jpg',
      N'海景頂級皇家總統套房',
      10
   );
GO
   /* =========================================================
    9. room_type
    ========================================================= */
INSERT INTO room_type (
      type_name,
      bed_type,
      capacity,
      room_description,
      price_per_night
   )
VALUES (N'標準海景雙人房', N'1張雙人床', 2, N'含雙人早餐，擁有獨立海景陽台', 3500),
   (N'標準山景雙人房', N'1張雙人床', 2, N'含雙人早餐，享受靜謐山景', 3000),
   (
      N'雅緻海景雙床房',
      N'2張單人床',
      2,
      N'含雙人早餐，海景客房，適合商務或好友',
      3800
   ),
   (
      N'雅緻山景雙床房',
      N'2張單人床',
      2,
      N'含雙人早餐，山景客房，適合商務或好友',
      3300
   ),
   (
      N'溫馨海景家庭房',
      N'2張雙人床',
      4,
      N'含四人早餐，家庭出遊首選海景房',
      5800
   ),
   (
      N'溫馨山景家庭房',
      N'2張雙人床',
      4,
      N'含四人早餐，空間寬敞，綠意山景',
      5200
   ),
   (
      N'行政海景尊榮套房',
      N'1張加大雙人床',
      2,
      N'含雙人早餐與行政酒廊權益，高樓層無敵海景',
      8800
   ),
   (
      N'行政山景尊榮套房',
      N'1張加大雙人床',
      2,
      N'含雙人早餐與行政酒廊權益，高樓層環景山景',
      8000
   ),
   (
      N'豪華全景海景四人套房',
      N'2張加大雙人床',
      4,
      N'含四人早餐，獨立會客廳，高樓層雙面海景',
      13800
   ),
   (
      N'頂級海景皇家總統套房',
      N'1張特大雙人床',
      2,
      N'含專屬管家與豪華早餐，獨立露台與私人酒廊',
      32000
   );
GO
   /* =========================================================
    11. category
    ========================================================= */
INSERT [dbo].[Category] ([Category_Name])
VALUES (N'客房備品')
GO
INSERT [dbo].[Category] ([Category_Name])
VALUES (N'紀念商品')
GO
INSERT [dbo].[Category] ([Category_Name])
VALUES (N'餐飲商品')
GO
   /* =========================================================
    12. product
    ========================================================= */
SET IDENTITY_INSERT [dbo].[Product] ON
GO
INSERT [dbo].[Product] (
      [Product_ID],
      [Product_Name],
      [Category_ID],
      [Description],
      [Price],
      [Stock],
      [ImageURL],
      [Status]
   )
VALUES (
      1,
      N'飯店馬克杯',
      1,
      N'飯店限定陶瓷馬克杯',
      CAST(350.00 AS Decimal(10, 2)),
      60,
      N'',
      N'ACTIVE'
   )
GO
INSERT [dbo].[Product] (
      [Product_ID],
      [Product_Name],
      [Category_ID],
      [Description],
      [Price],
      [Stock],
      [ImageURL],
      [Status]
   )
VALUES (
      2,
      N'飯店保溫瓶',
      1,
      N'不鏽鋼保溫瓶',
      CAST(599.00 AS Decimal(10, 2)),
      20,
      NULL,
      N'ACTIVE'
   )
GO
INSERT [dbo].[Product] (
      [Product_ID],
      [Product_Name],
      [Category_ID],
      [Description],
      [Price],
      [Stock],
      [ImageURL],
      [Status]
   )
VALUES (
      3,
      N'飯店帆布袋',
      1,
      N'飯店紀念帆布袋',
      CAST(299.00 AS Decimal(10, 2)),
      25,
      NULL,
      N'ACTIVE'
   )
GO
INSERT [dbo].[Product] (
      [Product_ID],
      [Product_Name],
      [Category_ID],
      [Description],
      [Price],
      [Stock],
      [ImageURL],
      [Status]
   )
VALUES (
      4,
      N'飯店鑰匙圈',
      1,
      N'飯店造型紀念鑰匙圈',
      CAST(150.00 AS Decimal(10, 2)),
      50,
      NULL,
      N'ACTIVE'
   )
GO
INSERT [dbo].[Product] (
      [Product_ID],
      [Product_Name],
      [Category_ID],
      [Description],
      [Price],
      [Stock],
      [ImageURL],
      [Status]
   )
VALUES (
      5,
      N'飯店明信片',
      1,
      N'飯店風景紀念明信片',
      CAST(80.00 AS Decimal(10, 2)),
      100,
      NULL,
      N'ACTIVE'
   )
GO
INSERT [dbo].[Product] (
      [Product_ID],
      [Product_Name],
      [Category_ID],
      [Description],
      [Price],
      [Stock],
      [ImageURL],
      [Status]
   )
VALUES (
      6,
      N'飯店浴袍',
      2,
      N'柔軟舒適飯店浴袍',
      CAST(899.00 AS Decimal(10, 2)),
      15,
      NULL,
      N'ACTIVE'
   )
GO
INSERT [dbo].[Product] (
      [Product_ID],
      [Product_Name],
      [Category_ID],
      [Description],
      [Price],
      [Stock],
      [ImageURL],
      [Status]
   )
VALUES (
      7,
      N'牙刷組',
      2,
      N'客房盥洗牙刷組',
      CAST(50.00 AS Decimal(10, 2)),
      100,
      NULL,
      N'ACTIVE'
   )
GO
INSERT [dbo].[Product] (
      [Product_ID],
      [Product_Name],
      [Category_ID],
      [Description],
      [Price],
      [Stock],
      [ImageURL],
      [Status]
   )
VALUES (
      8,
      N'刮鬍刀',
      2,
      N'一次性刮鬍刀',
      CAST(80.00 AS Decimal(10, 2)),
      80,
      NULL,
      N'ACTIVE'
   )
GO
SET IDENTITY_INSERT [dbo].[Product] OFF
GO
   /* =========================================================
    13. payment
    ========================================================= */
INSERT INTO payment (payment_method)
VALUES (N'Apple PAY'),
   (N 'LINE PAY'),
   (N'信用卡'),
   (N'現金'),
   (N'銀行轉帳');
GO
   /* =========================================================
    14. booking_order
    ========================================================= */
INSERT INTO booking_order (
      member_id,
      booking_total_price,
      order_status,
      created_at,
      payment_id
   )
VALUES (12, 6500, N'訂單完成', '2026-08-17 09:15:20', 3),
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
INSERT INTO booking (
      booking_order_id,
      booking_price,
      check_in_date,
      check_out_date,
      guest_num,
      booking_status,
      room_id,
      room_type_id
   )
VALUES -- Order 1 (Total: 6500) -> room_type 1 (room_id 1), room_type 2 (room_id 13)
   (
      1,
      3500,
      '2026-08-19 15:00:00',
      '2026-08-20 11:00:00',
      2,
      N'已退房',
      1,
      1
   ),
   (
      1,
      3000,
      '2026-08-19 15:00:00',
      '2026-08-20 11:00:00',
      2,
      N'已退房',
      13,
      2
   ),
   -- Order 2 (Total: 9300) -> room_type 1 (room_id 2), room_type 5 (room_id 49)
   (
      2,
      3500,
      '2026-08-18 15:00:00',
      '2026-08-19 11:00:00',
      2,
      N'已退房',
      2,
      1
   ),
   (
      2,
      5800,
      '2026-08-18 15:00:00',
      '2026-08-19 11:00:00',
      4,
      N'已退房',
      49,
      5
   ),
   -- Order 3 (Total: 11000, Cancelled)
   (
      3,
      5800,
      '2026-08-20 15:00:00',
      '2026-08-21 11:00:00',
      4,
      N'已取消',
      NULL,
      5
   ),
   (
      3,
      5200,
      '2026-08-20 15:00:00',
      '2026-08-21 11:00:00',
      4,
      N'已取消',
      NULL,
      6
   ),
   -- Order 4 (Total: 7100) -> room_type 3 (room_id 5), room_type 4 (room_id 17)
   (
      4,
      3800,
      '2026-08-21 15:00:00',
      '2026-08-22 11:00:00',
      2,
      N'已退房',
      5,
      3
   ),
   (
      4,
      3300,
      '2026-08-21 15:00:00',
      '2026-08-22 11:00:00',
      2,
      N'已退房',
      17,
      4
   ),
   -- Order 5 (Total: 14600) -> room_type 7 (room_id 97), room_type 5 (room_id 50)
   (
      5,
      8800,
      '2026-08-20 15:00:00',
      '2026-08-21 11:00:00',
      2,
      N'已退房',
      97,
      7
   ),
   (
      5,
      5800,
      '2026-08-20 15:00:00',
      '2026-08-21 11:00:00',
      4,
      N'已退房',
      50,
      5
   ),
   -- Order 6 (Total: 6800, Cancelled)
   (
      6,
      3500,
      '2026-08-22 15:00:00',
      '2026-08-23 11:00:00',
      2,
      N'已取消',
      NULL,
      1
   ),
   (
      6,
      3300,
      '2026-08-22 15:00:00',
      '2026-08-23 11:00:00',
      2,
      N'已取消',
      NULL,
      4
   ),
   -- Order 7 (Total: 8800) -> room_type 8 (room_id 103), room_type 3 (room_id 6)
   (
      7,
      5000,
      '2026-08-21 15:00:00',
      '2026-08-22 11:00:00',
      2,
      N'已退房',
      103,
      8
   ),
   (
      7,
      3800,
      '2026-08-21 15:00:00',
      '2026-08-22 11:00:00',
      2,
      N'已退房',
      6,
      3
   ),
   -- Order 8 (Total: 12300) -> room_type 7 (room_id 98), room_type 1 (room_id 3)
   (
      8,
      8800,
      '2026-08-22 15:00:00',
      '2026-08-23 11:00:00',
      2,
      N'已入住',
      98,
      7
   ),
   (
      8,
      3500,
      '2026-08-22 15:00:00',
      '2026-08-23 11:00:00',
      2,
      N'已入住',
      3,
      1
   ),
   -- Order 9 (Total: 6500) -> room_type 1 (room_id 4), room_type 2 (room_id 14)
   (
      9,
      3500,
      '2026-08-22 15:00:00',
      '2026-08-23 11:00:00',
      2,
      N'已入住',
      4,
      1
   ),
   (
      9,
      3000,
      '2026-08-22 15:00:00',
      '2026-08-23 11:00:00',
      2,
      N'已入住',
      14,
      2
   ),
   -- Order 10 (Total: 17600, Cancelled)
   (
      10,
      8800,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      2,
      N'已取消',
      NULL,
      7
   ),
   (
      10,
      8800,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      2,
      N'已取消',
      NULL,
      7
   ),
   -- Order 11 (Total: 9600) -> room_type 5 (room_id 51), room_type 3 (room_id 7)
   (
      11,
      5800,
      '2026-08-22 15:00:00',
      '2026-08-23 11:00:00',
      4,
      N'已入住',
      51,
      5
   ),
   (
      11,
      3800,
      '2026-08-22 15:00:00',
      '2026-08-23 11:00:00',
      2,
      N'已入住',
      7,
      3
   ),
   -- Order 12 (Total: 7300) -> room_type 3 (room_id 8), room_type 1 (room_id 9)
   (
      12,
      3800,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      2,
      N'已入住',
      8,
      3
   ),
   (
      12,
      3500,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      2,
      N'已入住',
      9,
      1
   ),
   -- Order 13 (Total: 11800) -> room_type 8 (room_id 104), room_type 3 (room_id 11)
   (
      13,
      8000,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      2,
      N'已入住',
      104,
      8
   ),
   (
      13,
      3800,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      2,
      N'已入住',
      11,
      3
   ),
   -- Order 14 (Total: 6300, Cancelled)
   (
      14,
      3300,
      '2026-08-24 15:00:00',
      '2026-08-25 11:00:00',
      2,
      N'已取消',
      NULL,
      4
   ),
   (
      14,
      3000,
      '2026-08-24 15:00:00',
      '2026-08-25 11:00:00',
      2,
      N'已取消',
      NULL,
      2
   ),
   -- Order 15 (Total: 14000) -> room_type 7 (room_id 99), room_type 6 (room_id 61)
   (
      15,
      8800,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      2,
      N'已入住',
      99,
      7
   ),
   (
      15,
      5200,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      4,
      N'已入住',
      61,
      6
   ),
   -- Order 16 (Total: 8800) -> room_type 8 (room_id 105), room_type 3 (room_id 12)
   (
      16,
      5000,
      '2026-08-24 15:00:00',
      '2026-08-25 11:00:00',
      2,
      N'待入住',
      105,
      8
   ),
   (
      16,
      3800,
      '2026-08-24 15:00:00',
      '2026-08-25 11:00:00',
      2,
      N'待入住',
      12,
      3
   ),
   -- Order 17 (Total: 10400) -> room_type 6 (room_id 62), room_type 6 (room_id 63)
   (
      17,
      5200,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      4,
      N'已入住',
      62,
      6
   ),
   (
      17,
      5200,
      '2026-08-23 15:00:00',
      '2026-08-24 11:00:00',
      4,
      N'已入住',
      63,
      6
   ),
   -- Order 18 (Total: 6800, Cancelled)
   (
      18,
      3500,
      '2026-08-25 15:00:00',
      '2026-08-26 11:00:00',
      2,
      N'已取消',
      NULL,
      1
   ),
   (
      18,
      3300,
      '2026-08-25 15:00:00',
      '2026-08-26 11:00:00',
      2,
      N'已取消',
      NULL,
      4
   ),
   -- Order 19 (Total: 16800) -> room_type 9 (room_id 109), room_type 2 (room_id 15)
   (
      19,
      13800,
      '2026-08-24 15:00:00',
      '2026-08-25 11:00:00',
      4,
      N'待入住',
      109,
      9
   ),
   (
      19,
      3000,
      '2026-08-24 15:00:00',
      '2026-08-25 11:00:00',
      2,
      N'待入住',
      15,
      2
   ),
   -- Order 20 (Total: 9100) -> room_type 5 (room_id 52), room_type 4 (room_id 18)
   (
      20,
      5800,
      '2026-08-25 15:00:00',
      '2026-08-26 11:00:00',
      4,
      N'待入住',
      52,
      5
   ),
   (
      20,
      3300,
      '2026-08-25 15:00:00',
      '2026-08-26 11:00:00',
      2,
      N'待入住',
      18,
      4
   ),
   -- Order 21 (Total: 6500) -> room_type 1 (room_id 10), room_type 2 (room_id 16)
   (
      21,
      3500,
      '2026-08-24 15:00:00',
      '2026-08-25 11:00:00',
      2,
      N'待入住',
      10,
      1
   ),
   (
      21,
      3000,
      '2026-08-24 15:00:00',
      '2026-08-25 11:00:00',
      2,
      N'待入住',
      16,
      2
   ),
   -- Order 22 (Total: 12600, Cancelled)
   (
      22,
      8800,
      '2026-08-26 15:00:00',
      '2026-08-27 11:00:00',
      2,
      N'已取消',
      NULL,
      7
   ),
   (
      22,
      3800,
      '2026-08-26 15:00:00',
      '2026-08-27 11:00:00',
      2,
      N'已取消',
      NULL,
      3
   ),
   -- Order 23 (Total: 7100) -> room_type 3 (room_id 29), room_type 4 (room_id 19)
   (
      23,
      3800,
      '2026-08-25 15:00:00',
      '2026-08-26 11:00:00',
      2,
      N'待入住',
      29,
      3
   ),
   (
      23,
      3300,
      '2026-08-25 15:00:00',
      '2026-08-26 11:00:00',
      2,
      N'待入住',
      19,
      4
   ),
   -- Order 24 (Total: 14600) -> room_type 7 (room_id 100), room_type 5 (room_id 53)
   (
      24,
      8800,
      '2026-08-26 15:00:00',
      '2026-08-27 11:00:00',
      2,
      N'待入住',
      100,
      7
   ),
   (
      24,
      5800,
      '2026-08-26 15:00:00',
      '2026-08-27 11:00:00',
      4,
      N'待入住',
      53,
      5
   ),
   -- Order 25 (Total: 8800) -> room_type 8 (room_id 106), room_type 3 (room_id 30)
   (
      25,
      5000,
      '2026-08-25 15:00:00',
      '2026-08-26 11:00:00',
      2,
      N'待入住',
      106,
      8
   ),
   (
      25,
      3800,
      '2026-08-25 15:00:00',
      '2026-08-26 11:00:00',
      2,
      N'待入住',
      30,
      3
   ),
   -- Order 26 (Total: 11000, Cancelled)
   (
      26,
      5800,
      '2026-08-27 15:00:00',
      '2026-08-28 11:00:00',
      4,
      N'已取消',
      NULL,
      5
   ),
   (
      26,
      5200,
      '2026-08-27 15:00:00',
      '2026-08-28 11:00:00',
      4,
      N'已取消',
      NULL,
      6
   ),
   -- Order 27 (Total: 6800) -> room_type 1 (room_id 25), room_type 4 (room_id 20)
   (
      27,
      3500,
      '2026-08-26 15:00:00',
      '2026-08-27 11:00:00',
      2,
      N'待入住',
      25,
      1
   ),
   (
      27,
      3300,
      '2026-08-26 15:00:00',
      '2026-08-27 11:00:00',
      2,
      N'待入住',
      20,
      4
   ),
   -- Order 28 (Total: 9600) -> room_type 5 (room_id 54), room_type 3 (room_id 31)
   (
      28,
      5800,
      '2026-08-27 15:00:00',
      '2026-08-28 11:00:00',
      4,
      N'待入住',
      54,
      5
   ),
   (
      28,
      3800,
      '2026-08-27 15:00:00',
      '2026-08-28 11:00:00',
      2,
      N'待入住',
      31,
      3
   ),
   -- Order 29 (Total: 17600) -> room_type 7 (room_id 101), room_type 7 (room_id 102)
   (
      29,
      8800,
      '2026-08-26 15:00:00',
      '2026-08-27 11:00:00',
      2,
      N'待入住',
      101,
      7
   ),
   (
      29,
      8800,
      '2026-08-26 15:00:00',
      '2026-08-27 11:00:00',
      2,
      N'待入住',
      102,
      7
   ),
   -- Order 30 (Total: 7300) -> room_type 3 (room_id 32), room_type 1 (room_id 26)
   (
      30,
      3800,
      '2026-08-27 15:00:00',
      '2026-08-28 11:00:00',
      2,
      N'待入住',
      32,
      3
   ),
   (
      30,
      3500,
      '2026-08-27 15:00:00',
      '2026-08-28 11:00:00',
      2,
      N'待入住',
      26,
      1
   );
>> >> >> > a1a75996ad225216fcf2795e933e2c45374de82f
GO
   /* =========================================================
    10. room
    ========================================================= */
   TRUNCATE TABLE room;
GO
INSERT INTO room (room_number, room_type_id, floor, room_status)
VALUES (N'10501', 1, 5, N'退房待清潔'),
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
INSERT INTO restaurant (
      restaurant_name,
      address,
      phone,
      capacity,
      description
   )
VALUES (
      N'雲澄自助餐廳',
      N'桃園市中壢區中央西路100號',
      '03-1234567',
      120,
      N'提供中西式自助餐'
   ),
   (
      N'景觀咖啡廳',
      N'桃園市中壢區中央西路100號',
      '03-1234568',
      60,
      N'提供咖啡及下午茶'
   );
GO
   /* =========================================================
    17. restaurant_time
    ========================================================= */
INSERT INTO restaurant_time (restaurant_id, meal_type, open_time, close_time)
VALUES (1, N'早餐', '07:00', '10:00'),
   (1, N'午餐', '11:30', '14:00'),
   (1, N'晚餐', '17:30', '21:00'),
   (2, N'下午茶', '14:00', '17:00');
GO << << << < HEAD
   /* =========================================================
    20. rental_payment 供 rental 使用的 payment
    payment_id 為 IDENTITY，先建立 rental_payment 1、2，再建立 rental
    ========================================================= */
INSERT INTO dbo.rental_payment (
      payment_method,
      payment_time,
      total_price,
      payment_status,
      member_id
   )
VALUES (NULL, NULL, 50000, N'待付款', 1),
   (NULL, NULL, 12000, N'待付款', 2);
GO
   /* =========================================================
    21. rental
    rental_id 為 IDENTITY，讓 SQL Server 自動產生
    rental.payment_id 需先存在於 rental_payment
    ========================================================= */
INSERT INTO rental (
      venue_id,
      member_id,
      event_name,
      rental_date,
      guest_count,
      payment_id,
      rental_status
   )
VALUES (
      1,
      1,
      N'公司尾牙',
      '2026-12-20 18:00:00',
      200,
      1,
      N'已確認'
   ),
   (
      2,
      2,
      N'公司會議',
      '2026-09-15 09:00:00',
      40,
      2,
      N'已確認'
   );
GO
   /* =========================================================
    22. order
    ========================================================= */
INSERT INTO dbo.[order] (
      member_id,
      order_date,
      original_amount,
      discount_amount,
      final_amount,
      coupon_id,
      payment_id,
      order_status
   )
VALUES (
      1,
      '2026-08-10 15:20:00',
      1999,
      -- 原價：350*4 + 599*1
      199,
      -- 折扣
      1800,
      -- 最終金額
      NULL,
      -- 目前尚未建立優惠券可先 NULL
      NULL,
      -- Payment 建立後再回填
      N'COMPLETED'
   ),
   (
      2,
      '2026-08-11 12:10:00',
      700,
      0,
      700,
      NULL,
      NULL,
      N 'PENDING'
   ),
   (
      1,
      '2026-08-13 18:50:00',
      350,
      50,
      300,
      NULL,
      NULL,
      N 'CANCELLED'
   );
GO
   /* =========================================================
    23. order_item
    ========================================================= */
INSERT INTO dbo.order_item (
      order_id,
      product_id,
      quantity,
      unit_price,
      subtotal
   )
VALUES -- 訂單 1
   (1, 1, 4, 350, 1400),
   (1, 2, 1, 599, 599),
   -- 訂單 2
   (2, 1, 2, 350, 700),
   -- 訂單 3
   (3, 1, 1, 350, 350);
GO
   /* =========================================================
    24. room_task 依照房務員工id （11~28） 給予隨機任務 對應人員
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
   )
VALUES -- 1. 退房清潔任務（退房時間 11:00 後陸續建立）
   (
      1,
      13,
      N'緊急',
      N'退房清潔',
      N'進行中',
      N'客人已退房，需優先清潔整備',
      '2026-08-23 11:15:00',
      NULL
   ),
   (
      2,
      14,
      N'緊急',
      N'退房清潔',
      N'待處理',
      N'客人已退房，待清潔',
      '2026-08-23 11:20:00',
      NULL
   ),
   (
      5,
      15,
      N'重要',
      N'退房清潔',
      N'進行中',
      N'退房清潔中',
      '2026-08-23 11:30:00',
      NULL
   ),
   (
      6,
      16,
      N'重要',
      N'退房清潔',
      N'待處理',
      N'待清潔房型',
      '2026-08-23 11:35:00',
      NULL
   ),
   (
      13,
      17,
      N'緊急',
      N'退房清潔',
      N'待處理',
      N'待退房清潔',
      '2026-08-23 11:40:00',
      NULL
   ),
   (
      17,
      18,
      N'重要',
      N'退房清潔',
      N'進行中',
      N'進行退房打掃',
      '2026-08-23 11:45:00',
      NULL
   ),
   (
      49,
      19,
      N'緊急',
      N'退房清潔',
      N'待處理',
      N'待退房清潔',
      '2026-08-23 12:00:00',
      NULL
   ),
   (
      50,
      20,
      N'重要',
      N'退房清潔',
      N'已完成',
      N'已完成清潔與備品更換',
      '2026-08-23 11:10:00',
      '2026-08-23 12:30:00'
   ),
   (
      97,
      21,
      N'緊急',
      N'退房清潔',
      N'待處理',
      N'待退房清潔',
      '2026-08-23 12:15:00',
      NULL
   ),
   (
      103,
      22,
      N'重要',
      N'退房清潔',
      N'待處理',
      N'待退房清潔',
      '2026-08-23 12:30:00',
      NULL
   ),
   -- 2. 續住日常清潔與補充備品（包含 employee 25 支援房間清潔）
   (
      3,
      23,
      N'一般',
      N'日常清潔',
      N'進行中',
      N'房客要求簡短打掃',
      '2026-08-23 13:30:00',
      NULL
   ),
   (
      7,
      24,
      N'一般',
      N'日常清潔',
      N'待處理',
      N'續住清潔',
      '2026-08-23 14:00:00',
      NULL
   ),
   (
      9,
      26,
      N'一般',
      N'補充備品',
      N'待處理',
      N'補充毛巾與浴巾',
      '2026-08-23 14:30:00',
      NULL
   ),
   (
      15,
      25,
      N'一般',
      N'日常清潔',
      N'進行中',
      N'續住房間打掃與整備',
      '2026-08-23 14:45:00',
      NULL
   ),
   (
      51,
      11,
      N'一般',
      N'日常清潔',
      N'已完成',
      N'日常清潔已完成',
      '2026-08-23 10:00:00',
      '2026-08-23 11:00:00'
   ),
   (
      61,
      12,
      N'一般',
      N'補充備品',
      N'進行中',
      N'補充礦泉水與盥洗用品',
      '2026-08-23 15:00:00',
      NULL
   ),
   -- 3. 設備維修任務（房客回報維修）
   (
      4,
      27,
      N'緊急',
      N'設備維修',
      N'進行中',
      N'冷氣不冷',
      '2026-08-23 16:00:00',
      NULL
   ),
   (
      14,
      28,
      N'重要',
      N'設備維修',
      N'待處理',
      N'馬桶堵塞',
      '2026-08-23 16:30:00',
      NULL
   );
== == == =
/* =========================================================
 18. reservation
 ========================================================= */
INSERT INTO reservation (
      member_id,
      contact_name,
      contact_phone,
      restaurant_id,
      reservation_date,
      time_id,
      people_count,
      status,
      create_time
   )
VALUES (
      1,
      N'王小明',
      '0912345678',
      1,
      '2026-08-21',
      3,
      2,
      N'已訂位',
      '2026-08-15 10:00:00'
   ),
   (
      2,
      N'陳小華',
      '0923456789',
      1,
      '2026-08-22',
      1,
      3,
      N'已訂位',
      '2026-08-16 11:30:00'
   ),
   (
      NULL,
      N'張先生',
      '0945678901',
      2,
      '2026-08-23',
      4,
      2,
      N'已訂位',
      '2026-08-17 15:00:00'
   );
GO
   /* =========================================================
    19. venue
    ========================================================= */
INSERT INTO venue (
      venue_id,
      venue_name,
      capacity,
      price_per_day,
      venue_status
   )
VALUES (1, N'宴會廳 A', 300, 50000, N'可預約'),
   (2, N'會議室 A', 50, 12000, N'可預約'),
   (3, N'會議室 B', 30, 8000, N'可預約');
GO
   /* =========================================================
    20. rental
    ========================================================= */
SET IDENTITY_INSERT [dbo].[employee_permission] OFF;
GO
SET IDENTITY_INSERT [dbo].[rental] ON;
GO
INSERT INTO rental (
      rental_id,
      venue_id,
      member_id,
      event_name,
      rental_date,
      guest_count,
      payment_id,
      rental_status
   )
VALUES (
      1,
      1,
      1,
      N'公司尾牙',
      '2026-12-20 18:00:00',
      200,
      1,
      N'已確認'
   ),
   (
      2,
      2,
      2,
      N'公司會議',
      '2026-09-15 09:00:00',
      40,
      2,
      N'已確認'
   );
GO
   /* =========================================================
    21. order
    ========================================================= */
SET IDENTITY_INSERT [dbo].[rental] OFF;
GO
SET IDENTITY_INSERT [dbo].[order] ON;
GO
INSERT INTO [order] (
      order_id,
      member_id,
      order_date,
      is_ordered,
      payment_id
   )
VALUES (1, 1, '2026-08-10 15:20:00', 1, 1),
   (2, 2, '2026-08-11 12:10:00', 1, 2),
   (3, 1, '2026-08-13 18:50:00', 1, 4);
GO
   /* =========================================================
    22. order_item
    ========================================================= */
SET IDENTITY_INSERT [dbo].[order] OFF;
GO
SET IDENTITY_INSERT [dbo].[order_item] ON;
GO
INSERT INTO order_item (order_id, product_id, quantity)
VALUES (1, 1, 4),
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
   )
VALUES -- 1. 退房清潔任務（退房時間 11:00 後陸續建立）
   (
      1,
      13,
      N'緊急',
      N'退房清潔',
      N'進行中',
      N'客人已退房，需優先清潔整備',
      '2026-08-23 11:15:00',
      NULL
   ),
   (
      2,
      14,
      N'緊急',
      N'退房清潔',
      N'待處理',
      N'客人已退房，待清潔',
      '2026-08-23 11:20:00',
      NULL
   ),
   (
      5,
      15,
      N'重要',
      N'退房清潔',
      N'進行中',
      N'退房清潔中',
      '2026-08-23 11:30:00',
      NULL
   ),
   (
      6,
      16,
      N'重要',
      N'退房清潔',
      N'待處理',
      N'待清潔房型',
      '2026-08-23 11:35:00',
      NULL
   ),
   (
      13,
      17,
      N'緊急',
      N'退房清潔',
      N'待處理',
      N'待退房清潔',
      '2026-08-23 11:40:00',
      NULL
   ),
   (
      17,
      18,
      N'重要',
      N'退房清潔',
      N'進行中',
      N'進行退房打掃',
      '2026-08-23 11:45:00',
      NULL
   ),
   (
      49,
      19,
      N'緊急',
      N'退房清潔',
      N'待處理',
      N'待退房清潔',
      '2026-08-23 12:00:00',
      NULL
   ),
   (
      50,
      20,
      N'重要',
      N'退房清潔',
      N'已完成',
      N'已完成清潔與備品更換',
      '2026-08-23 11:10:00',
      '2026-08-23 12:30:00'
   ),
   (
      97,
      21,
      N'緊急',
      N'退房清潔',
      N'待處理',
      N'待退房清潔',
      '2026-08-23 12:15:00',
      NULL
   ),
   (
      103,
      22,
      N'重要',
      N'退房清潔',
      N'待處理',
      N'待退房清潔',
      '2026-08-23 12:30:00',
      NULL
   ),
   -- 2. 續住日常清潔與補充備品（包含 employee 25 支援房間清潔）
   (
      3,
      23,
      N'一般',
      N'日常清潔',
      N'進行中',
      N'房客要求簡短打掃',
      '2026-08-23 13:30:00',
      NULL
   ),
   (
      7,
      24,
      N'一般',
      N'日常清潔',
      N'待處理',
      N'續住清潔',
      '2026-08-23 14:00:00',
      NULL
   ),
   (
      9,
      26,
      N'一般',
      N'補充備品',
      N'待處理',
      N'補充毛巾與浴巾',
      '2026-08-23 14:30:00',
      NULL
   ),
   (
      15,
      25,
      N'一般',
      N'日常清潔',
      N'進行中',
      N'續住房間打掃與整備',
      '2026-08-23 14:45:00',
      NULL
   ),
   (
      51,
      11,
      N'一般',
      N'日常清潔',
      N'已完成',
      N'日常清潔已完成',
      '2026-08-23 10:00:00',
      '2026-08-23 11:00:00'
   ),
   (
      61,
      12,
      N'一般',
      N'補充備品',
      N'進行中',
      N'補充礦泉水與盥洗用品',
      '2026-08-23 15:00:00',
      NULL
   ),
   -- 3. 設備維修任務（房客回報維修）
   (
      4,
      27,
      N'緊急',
      N'設備維修',
      N'進行中',
      N'冷氣不冷',
      '2026-08-23 16:00:00',
      NULL
   ),
   (
      14,
      28,
      N'重要',
      N'設備維修',
      N'待處理',
      N'馬桶堵塞',
      '2026-08-23 16:30:00',
      NULL
   );
GO
   /* =========================================================
    24. rental_payment 供 rental 使用的 payment
    ========================================================= */
SET IDENTITY_INSERT [dbo].[rental_payment] ON
GO
INSERT [dbo].[rental_payment] (
      [payment_id],
      [payment_method],
      [payment_time],
      [total_price],
      [payment_status],
      [member_id]
   )
VALUES (1, NULL, NULL, 50000, N'待付款', 1)
GO
INSERT [dbo].[rental_payment] (
      [payment_id],
      [payment_method],
      [payment_time],
      [total_price],
      [payment_status],
      [member_id]
   )
VALUES (2, NULL, NULL, 12000, N'待付款', 1)
GO
SET IDENTITY_INSERT [dbo].[rental_payment] OFF >> >> >> > a1a75996ad225216fcf2795e933e2c45374de82f
GO
   /* =========================================================
    25. coupon 一些優惠券
    ========================================================= */
INSERT INTO dbo.coupon (
      coupon_code,
      coupon_name,
      discount_type,
      discount_value,
      minimum_amount,
      start_date,
      end_date,
      status
   )
VALUES (
      N 'ANNIVERSARY90',
      N'飯店滿周歲活動',
      N 'PERCENT',
      10,
      0,
      '2026-09-01T00:00:00',
      '2026-12-31T23:59:59',
      N'ACTIVE'
   );
INSERT INTO dbo.coupon (
      coupon_code,
      coupon_name,
      discount_type,
      discount_value,
      minimum_amount,
      start_date,
      end_date,
      status
   )
VALUES (
      N'SAVE200',
      N'滿兩千現折兩百',
      N'FIXED',
      200,
      2000,
      '2026-09-01T00:00:00',
      '2026-12-31T23:59:59',
      N'ACTIVE'
   );
INSERT INTO dbo.coupon (
      coupon_code,
      coupon_name,
      discount_type,
      discount_value,
      minimum_amount,
      start_date,
      end_date,
      status
   )
VALUES (
      N'SUMMER100',
      N'夏季優惠－百元折價券',
      N'FIXED',
      100,
      0,
      '2026-09-01T00:00:00',
      '2026-12-31T23:59:59',
      N 'INACTIVE'
   );