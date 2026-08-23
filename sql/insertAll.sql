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
('admin',    '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
('reception', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
('housekeeping', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
('restaurant', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
('member01', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
('member02', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1'),
('member03', '$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', '1');
GO


/* =========================================================
   3. permission
   ========================================================= */
INSERT INTO permission (permission_id, permission_code, permission_name)
VALUES
(1, N'ROOM_MANAGE', N'房間管理'),
(2, N'BOOKING_MANAGE', N'訂房管理'),
(3, N'RESTAURANT_MANAGE', N'餐廳管理'),
(4, N'MEMBER_MANAGE', N'會員管理'),
(5, N'ORDER_MANAGE', N'訂單管理');
GO


/* =========================================================
   4. employee
   ========================================================= */
INSERT INTO employee
    (department_id, account_id, position)
VALUES
(1, 1, '經理'),
(1, 2, '櫃檯人員'),
(2, 3, '房務人員'),
(3, 4, '餐飲人員');
GO


/* =========================================================
   5. employee_permission
   注意：
   目前你的 FK 是 employee.account_id
   所以下面的 employee_id 使用 1、2、3、4
   ========================================================= */
INSERT INTO employee_permission (permission_id, employee_id)
VALUES
(1, 1),
(2, 1),
(3, 1),
(4, 1),
(5, 1),

(1, 2),
(2, 2),
(4, 2),

(1, 3),

(3, 4),
(5, 4);
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
INSERT INTO image (image_id, path, image_desc)
VALUES
(1, N'/images/room/deluxe.jpg', N'豪華房'),
(2, N'/images/room/double.jpg', N'雙人房'),
(3, N'/images/room/family.jpg', N'家庭房'),
(4, N'/images/product/breakfast.jpg', N'早餐'),
(5, N'/images/product/dinner.jpg', N'晚餐');
GO


/* =========================================================
   9. room_type
   ========================================================= */
INSERT INTO room_type
    (type_name, bed_type, description, price_per_night, capacity, image_id)
VALUES
(N'豪華雙人房', N'一大床', N'舒適雙人房型', 3800, 2, 1),
(N'精緻雙人房', N'兩小床', N'適合雙人入住', 3200, 2, 2),
(N'家庭房', N'兩大床', N'適合家庭入住', 5200, 4, 3);
GO


/* =========================================================
   10. room
   ========================================================= */
INSERT INTO room
    (room_number, room_type_id, floor, status)
VALUES
(301, 1, 3, N'可入住'),
(302, 1, 3, N'已入住'),
(303, 2, 3, N'可入住'),
(401, 2, 4, N'清潔中'),
(402, 3, 4, N'可入住'),
(403, 3, 4, N'維修中');
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
   23. room_task
   ========================================================= */
INSERT INTO room_task
    (room_id, employee_id, remark, priority,
     created_at, completed_at, task_type, task_status)
VALUES
(2, 3, N'退房後清潔房間', N'高',
 '2026-08-20 11:30:00', NULL, N'房間清潔', N'待處理'),

(4, 3, N'完成客房清潔', N'中',
 '2026-08-20 10:00:00', '2026-08-20 11:00:00',
 N'房間清潔', N'已完成'),

(6, 3, N'檢查浴室設備', N'高',
 '2026-08-20 09:00:00', NULL,
 N'設備檢查', N'處理中');
GO
