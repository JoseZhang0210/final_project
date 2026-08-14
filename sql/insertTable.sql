USE [finalproject];
GO

-- Seed data for a newly created schema. The explicit keys preserve all FK relationships.
SET NOCOUNT ON;
GO

SET IDENTITY_INSERT dbo.account ON;
INSERT INTO dbo.account (account_id, username, password, status) VALUES
    (1, 'admin.demo', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE'),
    (2, 'member.demo', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ACTIVE');
SET IDENTITY_INSERT dbo.account OFF;

SET IDENTITY_INSERT dbo.department ON;
INSERT INTO dbo.department (department_id, department_name) VALUES
    (1, 'Front Desk'), (2, 'Housekeeping');
SET IDENTITY_INSERT dbo.department OFF;

SET IDENTITY_INSERT dbo.employee ON;
INSERT INTO dbo.employee (employee_id, department_id, account_id, position, is_admin) VALUES
    (1, 1, 1, 'Manager', 1), (2, 2, 2, 'Attendant', 0);
SET IDENTITY_INSERT dbo.employee OFF;

SET IDENTITY_INSERT dbo.member ON;
INSERT INTO dbo.member (member_id, account_id) VALUES (1, 1), (2, 2);
SET IDENTITY_INSERT dbo.member OFF;

SET IDENTITY_INSERT dbo.profile ON;
INSERT INTO dbo.profile (profile_id, account_id, name, email, phone, zipcode, city, district, address, created_at, birthday, gender, updated_at) VALUES
    (1, 1, 'Admin Demo', 'admin@example.com', '0911000001', '100', 'Taipei', 'Zhongzheng', '1 Demo Road', GETDATE(), '1990-01-15', 'OTHER', GETDATE()),
    (2, 2, 'Member Demo', 'member@example.com', '0911000002', '220', 'New Taipei', 'Banqiao', '2 Demo Road', GETDATE(), '1995-05-20', 'OTHER', GETDATE());
SET IDENTITY_INSERT dbo.profile OFF;

INSERT INTO dbo.permission (permission_id, permission_code, permission_name) VALUES
    (1, N'ADMIN_MANAGE', N'Administrator management'),
    (2, N'ROOM_MANAGE', N'Room management');
INSERT INTO dbo.employee_permission (permission_id, employee_id) VALUES (1, 1), (2, 2);

INSERT INTO dbo.image (image_id, path, image_desc) VALUES
    (1, N'/images/deluxe.jpg', N'Deluxe'),
    (2, N'/images/suite.jpg', N'Suite');
INSERT INTO dbo.category (category_id, category_name, parent_category) VALUES
    (1, N'Food', NULL), (2, N'Drinks', NULL);
INSERT INTO dbo.product (product_id, product_name, category_id, description, price, stock, image_id, status) VALUES
    (1, N'Breakfast Set', 1, N'Continental breakfast', 250, 30, 1, N'ACTIVE'),
    (2, N'Coffee', 2, N'Hot Americano', 120, 50, 2, N'ACTIVE');

SET IDENTITY_INSERT dbo.room_type ON;
INSERT INTO dbo.room_type (room_type_id, type_name, bed_type, description, price_per_night, capacity, image_id) VALUES
    (1, N'Deluxe', N'Queen', N'City view room', 4200, 2, 1),
    (2, N'Executive Suite', N'King', N'Separate living area', 6800, 3, 2);
SET IDENTITY_INSERT dbo.room_type OFF;
SET IDENTITY_INSERT dbo.room ON;
INSERT INTO dbo.room (room_id, room_number, room_type_id, floor, status) VALUES
    (1, 101, 1, 1, N'AVAILABLE'), (2, 201, 2, 2, N'AVAILABLE');
SET IDENTITY_INSERT dbo.room OFF;

INSERT INTO dbo.venue (venue_id, venue_name, capacity, price_per_day, venue_status) VALUES
    (1, N'Garden Hall', 80, 12000, N'AVAILABLE'),
    (2, N'Rooftop Terrace', 50, 9000, N'AVAILABLE');

SET IDENTITY_INSERT dbo.restaurant ON;
INSERT INTO dbo.restaurant (restaurant_id, restaurant_name, address, phone, capacity, description) VALUES
    (1, N'Azure Restaurant', N'1 Demo Road', '02-55550001', 100, N'All-day dining'),
    (2, N'Sky Lounge', N'2 Demo Road', '02-55550002', 60, N'Light meals and drinks');
SET IDENTITY_INSERT dbo.restaurant OFF;
SET IDENTITY_INSERT dbo.restaurant_time ON;
INSERT INTO dbo.restaurant_time (time_id, restaurant_id, meal_type, open_time, close_time) VALUES
    (1, 1, N'BREAKFAST', '07:00:00', '10:00:00'),
    (2, 2, N'DINNER', '17:30:00', '22:00:00');
SET IDENTITY_INSERT dbo.restaurant_time OFF;

INSERT INTO dbo.payment (payment_id, payment_method, payment_time, total_price, payment_status, member_id) VALUES
    (1, N'CREDIT_CARD', GETDATE(), 8400, N'PAID', 1),
    (2, N'CASH', GETDATE(), 9000, N'PAID', 2);
INSERT INTO dbo.[order] (order_id, member_id, order_date, is_ordered, payment_id) VALUES
    (1, 1, GETDATE(), 1, 1), (2, 2, GETDATE(), 1, 2);
INSERT INTO dbo.order_item (order_id, product_id, quantity) VALUES (1, 1, 2), (2, 2, 3);
INSERT INTO dbo.booking_order (booking_order_id, member_id, booking_total_price, order_status, created_at, payment_id) VALUES
    (1, 1, 8400, N'CONFIRMED', GETDATE(), 1),
    (2, 2, 13600, N'CONFIRMED', GETDATE(), 2);
SET IDENTITY_INSERT dbo.booking ON;
INSERT INTO dbo.booking (booking_id, booking_order_id, check_in_date, check_out_date, guest_num, booking_status, room_id, room_type_id) VALUES
    (1, 1, '2026-09-01', '2026-09-03', 2, N'CONFIRMED', 1, 1),
    (2, 2, '2026-09-10', '2026-09-12', 3, N'CONFIRMED', 2, 2);
SET IDENTITY_INSERT dbo.booking OFF;
INSERT INTO dbo.rental (rental_id, venue_id, member_id, event_name, rental_date, guest_count, payment_id, rental_status) VALUES
    (1, 1, 1, N'Product Launch', '2026-09-05', 60, 1, N'CONFIRMED'),
    (2, 2, 2, N'Birthday Party', '2026-09-15', 40, 2, N'CONFIRMED');
INSERT INTO dbo.reservation (member_id, contact_name, contact_phone, restaurant_id, reservation_date, time_id, people_count, status, create_time) VALUES
    (1, N'Admin Demo', '0911000001', 1, '2026-09-02', 1, 2, N'CONFIRMED', GETDATE()),
    (2, N'Member Demo', '0911000002', 2, '2026-09-11', 2, 3, N'CONFIRMED', GETDATE());
SET IDENTITY_INSERT dbo.room_task ON;
INSERT INTO dbo.room_task (task_id, room_id, employee_id, remark, priority, created_at, completed_at, task_type, task_status) VALUES
    (1, 1, 1, N'Inspect before check-in', N'HIGH', GETDATE(), NULL, N'INSPECTION', N'OPEN'),
    (2, 2, 2, N'Restock towels', N'NORMAL', GETDATE(), NULL, N'HOUSEKEEPING', N'OPEN');
SET IDENTITY_INSERT dbo.room_task OFF;
GO
