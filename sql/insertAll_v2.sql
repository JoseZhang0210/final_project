USE [finalproject]
GO
SET IDENTITY_INSERT [dbo].[rental_payment] ON 
GO
INSERT [dbo].[rental_payment] ([payment_id], [payment_method], [payment_time], [total_price], [payment_status], [member_id]) VALUES (1, NULL, NULL, 50000, N'待付款', 1)
GO
INSERT [dbo].[rental_payment] ([payment_id], [payment_method], [payment_time], [total_price], [payment_status], [member_id]) VALUES (2, NULL, NULL, 12000, N'待付款', 1)
GO
SET IDENTITY_INSERT [dbo].[rental_payment] OFF
GO
SET IDENTITY_INSERT [dbo].[account] ON 
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (1, N'admin01', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (2, N'admin02', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (3, N'frontdesk01', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (4, N'frontdesk02', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (5, N'frontdesk03', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (6, N'frontdesk04', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (7, N'frontdesk05', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (8, N'frontdesk06', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (9, N'frontdesk07', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (10, N'frontdesk08', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (11, N'housekeeping01', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (12, N'housekeeping02', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (13, N'housekeeping03', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (14, N'housekeeping04', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (15, N'housekeeping05', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (16, N'housekeeping06', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (17, N'housekeeping07', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (18, N'housekeeping08', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (19, N'housekeeping09', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (20, N'housekeeping10', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (21, N'housekeeping11', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (22, N'housekeeping12', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (23, N'housekeeping13', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (24, N'housekeeping14', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (25, N'housekeeping15', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (26, N'housekeeping16', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (27, N'housekeeping17', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (28, N'housekeeping18', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (29, N'fnb01', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (30, N'fnb02', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (31, N'fnb03', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (32, N'fnb04', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (33, N'fnb05', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (34, N'fnb06', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (35, N'fnb07', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (36, N'fnb08', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (37, N'fnb09', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (38, N'fnb10', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (39, N'fnb11', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (40, N'fnb12', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (41, N'fnb13', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (42, N'fnb14', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (43, N'fnb15', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (44, N'fnb16', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (45, N'fnb17', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (46, N'customer01', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (47, N'customer02', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (48, N'customer03', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (49, N'customer04', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (50, N'customer05', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (51, N'customer06', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (52, N'customer07', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (53, N'customer08', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (54, N'customer09', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (55, N'customer10', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (56, N'customer11', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (57, N'customer12', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (58, N'customer13', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (59, N'customer14', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (60, N'customer15', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (61, N'customer16', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (62, N'customer17', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (63, N'customer18', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (64, N'customer19', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (65, N'customer20', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (66, N'customer21', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (67, N'customer22', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (68, N'customer23', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (69, N'customer24', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (70, N'customer25', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (71, N'customer26', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (72, N'customer27', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (73, N'customer28', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (74, N'customer29', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (75, N'customer30', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (76, N'customer31', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (77, N'customer32', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (78, N'customer33', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (79, N'customer34', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (80, N'customer35', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (81, N'customer36', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (82, N'customer37', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (83, N'customer38', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (84, N'customer39', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (85, N'customer40', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (86, N'customer41', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (87, N'customer42', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (88, N'customer43', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (89, N'customer44', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (90, N'customer45', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (91, N'customer46', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (92, N'customer47', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (93, N'customer48', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (94, N'customer49', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (95, N'customer50', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (96, N'customer51', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (97, N'customer52', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (98, N'customer53', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (99, N'customer54', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (100, N'customer55', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (101, N'customer56', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (102, N'customer57', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (103, N'customer58', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (104, N'customer59', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
INSERT [dbo].[account] ([account_id], [username], [password], [status]) VALUES (105, N'customer60', N'$2b$10$yuWhzVEDpGIaJA3UKpTY5.vNPT9XD517KyLRzYeAjtC3zlf08BJ6C', N'1')
GO
SET IDENTITY_INSERT [dbo].[account] OFF
GO
SET IDENTITY_INSERT [dbo].[member] ON 
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (1, 46)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (2, 47)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (3, 48)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (4, 49)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (5, 50)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (6, 51)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (7, 52)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (8, 53)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (9, 54)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (10, 55)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (11, 56)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (12, 57)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (13, 58)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (14, 59)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (15, 60)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (16, 61)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (17, 62)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (18, 63)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (19, 64)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (20, 65)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (21, 66)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (22, 67)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (23, 68)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (24, 69)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (25, 70)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (26, 71)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (27, 72)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (28, 73)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (29, 74)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (30, 75)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (31, 76)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (32, 77)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (33, 78)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (34, 79)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (35, 80)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (36, 81)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (37, 82)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (38, 83)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (39, 84)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (40, 85)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (41, 86)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (42, 87)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (43, 88)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (44, 89)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (45, 90)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (46, 91)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (47, 92)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (48, 93)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (49, 94)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (50, 95)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (51, 96)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (52, 97)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (53, 98)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (54, 99)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (55, 100)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (56, 101)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (57, 102)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (58, 103)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (59, 104)
GO
INSERT [dbo].[member] ([member_id], [account_id]) VALUES (60, 105)
GO
SET IDENTITY_INSERT [dbo].[member] OFF
GO
INSERT [dbo].[venue] ([venue_id], [venue_name], [capacity], [price_per_day], [venue_status]) VALUES (1, N'宴會廳 A', 300, 50000, N'可預約')
GO
INSERT [dbo].[venue] ([venue_id], [venue_name], [capacity], [price_per_day], [venue_status]) VALUES (2, N'會議室 A', 50, 12000, N'可預約')
GO
INSERT [dbo].[venue] ([venue_id], [venue_name], [capacity], [price_per_day], [venue_status]) VALUES (3, N'會議室 B', 30, 8000, N'可預約');
GO
SET IDENTITY_INSERT [dbo].[rental] ON 
GO
INSERT [dbo].[rental] ([rental_id], [venue_id], [member_id], [event_name], [rental_date], [guest_count], [payment_id], [rental_status]) VALUES (1, 1, 1, N'公司尾牙', CAST(N'2026-12-20T18:00:00.000' AS DateTime), 200, 1, N'已確認')
GO
INSERT [dbo].[rental] ([rental_id], [venue_id], [member_id], [event_name], [rental_date], [guest_count], [payment_id], [rental_status]) VALUES (2, 2, 2, N'公司會議', CAST(N'2026-09-15T09:00:00.000' AS DateTime), 40, 2, N'已確認')
GO
SET IDENTITY_INSERT [dbo].[rental] OFF
GO
SET IDENTITY_INSERT [dbo].[department] ON 
GO
INSERT [dbo].[department] ([department_id], [department_name]) VALUES (4, N'行政部')
GO
INSERT [dbo].[department] ([department_id], [department_name]) VALUES (2, N'客房部')
GO
INSERT [dbo].[department] ([department_id], [department_name]) VALUES (3, N'餐飲部')
GO
INSERT [dbo].[department] ([department_id], [department_name]) VALUES (1, N'櫃檯部')
GO
SET IDENTITY_INSERT [dbo].[department] OFF
GO
SET IDENTITY_INSERT [dbo].[employee] ON 
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (1, 4, 1, N'總經理')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (2, 4, 2, N'行政人資主管')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (3, 1, 3, N'櫃檯主管')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (4, 1, 4, N'櫃檯專員(早班)')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (5, 1, 5, N'櫃檯專員(早班)')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (6, 1, 6, N'櫃檯專員(晚班)')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (7, 1, 7, N'櫃檯專員(晚班)')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (8, 1, 8, N'櫃檯專員(大夜)')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (9, 1, 9, N'禮賓接待員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (10, 1, 10, N'車隊接送員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (11, 2, 11, N'房務主管')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (12, 2, 12, N'房務領班')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (13, 2, 13, N'房務專員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (14, 2, 14, N'房務專員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (15, 2, 15, N'房務專員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (16, 2, 16, N'房務專員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (17, 2, 17, N'房務專員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (18, 2, 18, N'房務專員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (19, 2, 19, N'房務專員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (20, 2, 20, N'房務專員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (21, 2, 21, N'房務專員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (22, 2, 22, N'房務專員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (23, 2, 23, N'房務專員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (24, 2, 24, N'房務專員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (25, 2, 25, N'公設清潔員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (26, 2, 26, N'布巾洗滌員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (27, 2, 27, N'機電維修員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (28, 2, 28, N'水電維修員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (29, 3, 29, N'餐飲主管')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (30, 3, 30, N'主廚')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (31, 3, 31, N'副廚')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (32, 3, 32, N'砧板/冷盤廚師')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (33, 3, 33, N'熱炒/西餐廚師')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (34, 3, 34, N'點心/烘焙師')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (35, 3, 35, N'餐飲組長')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (36, 3, 36, N'餐飲服務員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (37, 3, 37, N'餐飲服務員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (38, 3, 38, N'餐飲服務員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (39, 3, 39, N'餐飲服務員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (40, 3, 40, N'餐飲服務員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (41, 3, 41, N'餐飲服務員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (42, 3, 42, N'吧檯調酒師')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (43, 3, 43, N'吧檯助理')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (44, 3, 44, N'洗碗員')
GO
INSERT [dbo].[employee] ([employee_id], [department_id], [account_id], [position]) VALUES (45, 3, 45, N'備料清潔員')
GO
SET IDENTITY_INSERT [dbo].[employee] OFF
GO
SET IDENTITY_INSERT [dbo].[profile] ON 
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (1, 5, N'王小明', N'ming@example.com', N'0912345678', N'320', N'桃園市', N'中壢區', N'中央西路一段100號', CAST(N'2026-08-01T10:00:00.000' AS DateTime), CAST(N'1998-05-12' AS Date), N'男', CAST(N'2026-08-01T10:00:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (2, 6, N'陳小華', N'hua@example.com', N'0923456789', N'320', N'桃園市', N'中壢區', N'中美路200號', CAST(N'2026-08-02T11:00:00.000' AS DateTime), CAST(N'1995-08-20' AS Date), N'女', CAST(N'2026-08-02T11:00:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (3, 7, N'林大偉', N'david@example.com', N'0934567890', N'330', N'桃園市', N'桃園區', N'中正路300號', CAST(N'2026-08-03T14:00:00.000' AS DateTime), CAST(N'2000-03-15' AS Date), N'男', CAST(N'2026-08-03T14:00:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (4, 8, N'張雅婷', N'yating@example.com', N'0945678901', N'330', N'桃園市', N'桃園區', N'民生路120號', CAST(N'2026-08-04T09:30:00.000' AS DateTime), CAST(N'1997-07-22' AS Date), N'女', CAST(N'2026-08-04T09:30:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (5, 9, N'李承翰', N'han@example.com', N'0956789012', N'320', N'桃園市', N'中壢區', N'延平路88號', CAST(N'2026-08-05T13:20:00.000' AS DateTime), CAST(N'1999-11-03' AS Date), N'男', CAST(N'2026-08-05T13:20:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (6, 10, N'黃詩涵', N'shihan@example.com', N'0967890123', N'324', N'桃園市', N'平鎮區', N'環南路50號', CAST(N'2026-08-06T15:10:00.000' AS DateTime), CAST(N'1996-02-18' AS Date), N'女', CAST(N'2026-08-06T15:10:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (7, 11, N'吳俊傑', N'junjie@example.com', N'0978901234', N'324', N'桃園市', N'平鎮區', N'中豐路160號', CAST(N'2026-08-07T10:40:00.000' AS DateTime), CAST(N'1994-09-25' AS Date), N'男', CAST(N'2026-08-07T10:40:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (8, 12, N'周怡君', N'yijun@example.com', N'0989012345', N'300', N'新竹市', N'東區', N'光復路一段50號', CAST(N'2026-08-08T11:30:00.000' AS DateTime), CAST(N'1998-12-10' AS Date), N'女', CAST(N'2026-08-08T11:30:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (9, 13, N'許家豪', N'jiahao@example.com', N'0901234567', N'300', N'新竹市', N'北區', N'中正路180號', CAST(N'2026-08-09T14:20:00.000' AS DateTime), CAST(N'1993-04-08' AS Date), N'男', CAST(N'2026-08-09T14:20:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (10, 14, N'鄭惠文', N'huiwen@example.com', N'0911234567', N'302', N'新竹縣', N'竹北市', N'縣政二路100號', CAST(N'2026-08-10T09:10:00.000' AS DateTime), CAST(N'2001-06-30' AS Date), N'女', CAST(N'2026-08-10T09:10:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (11, 15, N'蔡明哲', N'mingzhe@example.com', N'0921234567', N'302', N'新竹縣', N'竹北市', N'光明六路200號', CAST(N'2026-08-11T16:00:00.000' AS DateTime), CAST(N'1992-01-15' AS Date), N'男', CAST(N'2026-08-11T16:00:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (12, 16, N'楊淑芬', N'shufen@example.com', N'0931234567', N'300', N'新竹市', N'東區', N'建功一路30號', CAST(N'2026-08-12T10:15:00.000' AS DateTime), CAST(N'1990-10-05' AS Date), N'女', CAST(N'2026-08-12T10:15:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (13, 17, N'曾冠宇', N'guanyu@example.com', N'0941234567', N'330', N'桃園市', N'桃園區', N'春日路260號', CAST(N'2026-08-13T13:45:00.000' AS DateTime), CAST(N'1997-03-21' AS Date), N'男', CAST(N'2026-08-13T13:45:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (14, 18, N'劉欣怡', N'xinyi@example.com', N'0951234567', N'333', N'桃園市', N'龜山區', N'文化一路80號', CAST(N'2026-08-14T11:05:00.000' AS DateTime), CAST(N'1999-08-14' AS Date), N'女', CAST(N'2026-08-14T11:05:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (15, 19, N'郭志偉', N'zhiwei@example.com', N'0961234567', N'333', N'桃園市', N'龜山區', N'復興一路120號', CAST(N'2026-08-15T15:25:00.000' AS DateTime), CAST(N'1995-05-19' AS Date), N'男', CAST(N'2026-08-15T15:25:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (16, 20, N'謝佩珊', N'peishan@example.com', N'0971234567', N'334', N'桃園市', N'八德區', N'介壽路一段90號', CAST(N'2026-08-16T09:50:00.000' AS DateTime), CAST(N'1996-11-27' AS Date), N'女', CAST(N'2026-08-16T09:50:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (17, 21, N'何俊宏', N'junhong@example.com', N'0981234567', N'334', N'桃園市', N'八德區', N'廣福路150號', CAST(N'2026-08-17T12:10:00.000' AS DateTime), CAST(N'1991-07-06' AS Date), N'男', CAST(N'2026-08-17T12:10:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (18, 22, N'徐雅雯', N'yawen@example.com', N'0902234567', N'335', N'桃園市', N'大溪區', N'中正東路70號', CAST(N'2026-08-18T14:35:00.000' AS DateTime), CAST(N'2000-02-12' AS Date), N'女', CAST(N'2026-08-18T14:35:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (19, 23, N'羅偉倫', N'weilun@example.com', N'0912234567', N'335', N'桃園市', N'大溪區', N'和平路110號', CAST(N'2026-08-19T10:25:00.000' AS DateTime), CAST(N'1994-06-23' AS Date), N'男', CAST(N'2026-08-19T10:25:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (20, 24, N'林佳蓉', N'jiarong@example.com', N'0922234567', N'336', N'桃園市', N'復興區', N'中正路25號', CAST(N'2026-08-20T13:15:00.000' AS DateTime), CAST(N'1998-09-17' AS Date), N'女', CAST(N'2026-08-20T13:15:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (21, 25, N'張志豪', N'zhihao@example.com', N'0932234567', N'337', N'桃園市', N'大園區', N'中山南路130號', CAST(N'2026-08-21T09:40:00.000' AS DateTime), CAST(N'1993-12-01' AS Date), N'男', CAST(N'2026-08-21T09:40:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (22, 26, N'林怡萱', N'yixuan@example.com', N'0942234567', N'337', N'桃園市', N'大園區', N'和平西路75號', CAST(N'2026-08-22T11:55:00.000' AS DateTime), CAST(N'2001-04-16' AS Date), N'女', CAST(N'2026-08-22T11:55:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (23, 27, N'陳柏宇', N'boyu@example.com', N'0952234567', N'338', N'桃園市', N'蘆竹區', N'南山路100號', CAST(N'2026-08-23T15:05:00.000' AS DateTime), CAST(N'1997-10-29' AS Date), N'男', CAST(N'2026-08-23T15:05:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (24, 28, N'王思妤', N'siyu@example.com', N'0962234567', N'338', N'桃園市', N'蘆竹區', N'南崁路220號', CAST(N'2026-08-24T10:35:00.000' AS DateTime), CAST(N'1999-01-09' AS Date), N'女', CAST(N'2026-08-24T10:35:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (25, 29, N'黃柏勳', N'boxun@example.com', N'0972234567', N'320', N'桃園市', N'中壢區', N'新生路180號', CAST(N'2026-08-25T13:50:00.000' AS DateTime), CAST(N'1996-05-26' AS Date), N'男', CAST(N'2026-08-25T13:50:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (26, 30, N'吳佳玲', N'jialing@example.com', N'0982234567', N'320', N'桃園市', N'中壢區', N'慈惠三街60號', CAST(N'2026-08-26T09:20:00.000' AS DateTime), CAST(N'1995-03-11' AS Date), N'女', CAST(N'2026-08-26T09:20:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (27, 31, N'李冠廷', N'guanting@example.com', N'0903234567', N'320', N'桃園市', N'中壢區', N'環中東路240號', CAST(N'2026-08-01T14:10:00.000' AS DateTime), CAST(N'1998-07-03' AS Date), N'男', CAST(N'2026-08-01T14:10:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (28, 32, N'陳怡安', N'yian@example.com', N'0913234567', N'324', N'桃園市', N'平鎮區', N'金陵路88號', CAST(N'2026-08-02T10:05:00.000' AS DateTime), CAST(N'2000-11-18' AS Date), N'女', CAST(N'2026-08-02T10:05:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (29, 33, N'周建宏', N'jianhong@example.com', N'0923234567', N'324', N'桃園市', N'平鎮區', N'育達路150號', CAST(N'2026-08-03T15:30:00.000' AS DateTime), CAST(N'1992-08-07' AS Date), N'男', CAST(N'2026-08-03T15:30:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (30, 34, N'許芳瑜', N'fangyu@example.com', N'0933234567', N'325', N'桃園市', N'龍潭區', N'中正路90號', CAST(N'2026-08-04T11:20:00.000' AS DateTime), CAST(N'1997-02-25' AS Date), N'女', CAST(N'2026-08-04T11:20:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (31, 35, N'鄭凱文', N'kaiwen@example.com', N'0943234567', N'325', N'桃園市', N'龍潭區', N'北龍路170號', CAST(N'2026-08-05T13:00:00.000' AS DateTime), CAST(N'1994-12-14' AS Date), N'男', CAST(N'2026-08-05T13:00:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (32, 36, N'蔡宜庭', N'yiting@example.com', N'0953234567', N'326', N'桃園市', N'楊梅區', N'大成路120號', CAST(N'2026-08-06T09:45:00.000' AS DateTime), CAST(N'2001-09-08' AS Date), N'女', CAST(N'2026-08-06T09:45:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (33, 37, N'何宗翰', N'zonghan@example.com', N'0963234567', N'326', N'桃園市', N'楊梅區', N'新成路200號', CAST(N'2026-08-07T16:15:00.000' AS DateTime), CAST(N'1993-05-30' AS Date), N'男', CAST(N'2026-08-07T16:15:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (34, 38, N'謝欣妤', N'xinyu@example.com', N'0973234567', N'327', N'桃園市', N'新屋區', N'中山西路100號', CAST(N'2026-08-08T10:50:00.000' AS DateTime), CAST(N'1999-06-12' AS Date), N'女', CAST(N'2026-08-08T10:50:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (35, 39, N'林俊佑', N'junyou@example.com', N'0983234567', N'327', N'桃園市', N'新屋區', N'中正路50號', CAST(N'2026-08-09T14:45:00.000' AS DateTime), CAST(N'1995-01-28' AS Date), N'男', CAST(N'2026-08-09T14:45:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (36, 40, N'張瑞芳', N'ruifang@example.com', N'0904234567', N'328', N'桃園市', N'觀音區', N'中山路180號', CAST(N'2026-08-10T11:35:00.000' AS DateTime), CAST(N'1996-10-21' AS Date), N'女', CAST(N'2026-08-10T11:35:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (37, 41, N'王建國', N'jianguo@example.com', N'0914234567', N'328', N'桃園市', N'觀音區', N'大觀路90號', CAST(N'2026-08-11T13:25:00.000' AS DateTime), CAST(N'1989-04-05' AS Date), N'男', CAST(N'2026-08-11T13:25:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (38, 42, N'劉佳穎', N'jiaying@example.com', N'0924234567', N'330', N'桃園市', N'桃園區', N'成功路100號', CAST(N'2026-08-12T09:15:00.000' AS DateTime), CAST(N'2000-08-19' AS Date), N'女', CAST(N'2026-08-12T09:15:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (39, 43, N'黃冠霖', N'guanlin@example.com', N'0934234567', N'330', N'桃園市', N'桃園區', N'復興路250號', CAST(N'2026-08-13T15:40:00.000' AS DateTime), CAST(N'1997-11-05' AS Date), N'男', CAST(N'2026-08-13T15:40:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (40, 44, N'吳佩蓉', N'peirong@example.com', N'0944234567', N'333', N'桃園市', N'龜山區', N'萬壽路80號', CAST(N'2026-08-14T10:30:00.000' AS DateTime), CAST(N'1998-03-22' AS Date), N'女', CAST(N'2026-08-14T10:30:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (41, 45, N'陳威廷', N'weiting@example.com', N'0954234567', N'333', N'桃園市', N'龜山區', N'自強東路140號', CAST(N'2026-08-15T14:00:00.000' AS DateTime), CAST(N'1994-07-17' AS Date), N'男', CAST(N'2026-08-15T14:00:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (42, 46, N'林欣怡', N'hsinyi@example.com', N'0964234567', N'334', N'桃園市', N'八德區', N'忠勇街60號', CAST(N'2026-08-16T11:10:00.000' AS DateTime), CAST(N'2001-02-03' AS Date), N'女', CAST(N'2026-08-16T11:10:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (43, 47, N'張凱翔', N'kaixiang@example.com', N'0974234567', N'334', N'桃園市', N'八德區', N'介壽路二段300號', CAST(N'2026-08-17T16:30:00.000' AS DateTime), CAST(N'1996-09-12' AS Date), N'男', CAST(N'2026-08-17T16:30:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (44, 48, N'楊雅婷', N'yangting@example.com', N'0984234567', N'335', N'桃園市', N'大溪區', N'員林路120號', CAST(N'2026-08-18T09:35:00.000' AS DateTime), CAST(N'1999-12-25' AS Date), N'女', CAST(N'2026-08-18T09:35:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (45, 49, N'郭俊傑', N'junjie2@example.com', N'0905234567', N'335', N'桃園市', N'大溪區', N'慈湖路180號', CAST(N'2026-08-19T13:05:00.000' AS DateTime), CAST(N'1992-06-18' AS Date), N'男', CAST(N'2026-08-19T13:05:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (46, 50, N'徐婉婷', N'wanting@example.com', N'0915234567', N'336', N'桃園市', N'復興區', N'三民路30號', CAST(N'2026-08-20T10:45:00.000' AS DateTime), CAST(N'1997-04-27' AS Date), N'女', CAST(N'2026-08-20T10:45:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (47, 51, N'羅子軒', N'zixuan@example.com', N'0925234567', N'337', N'桃園市', N'大園區', N'航站南路50號', CAST(N'2026-08-21T14:25:00.000' AS DateTime), CAST(N'2000-10-16' AS Date), N'男', CAST(N'2026-08-21T14:25:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (48, 52, N'謝宜蓁', N'yizhen@example.com', N'0935234567', N'337', N'桃園市', N'大園區', N'中華路90號', CAST(N'2026-08-22T11:50:00.000' AS DateTime), CAST(N'1995-08-03' AS Date), N'女', CAST(N'2026-08-22T11:50:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (49, 53, N'何明哲', N'mingzhe2@example.com', N'0945234567', N'338', N'桃園市', N'蘆竹區', N'忠孝西路110號', CAST(N'2026-08-23T15:15:00.000' AS DateTime), CAST(N'1993-03-29' AS Date), N'男', CAST(N'2026-08-23T15:15:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (50, 54, N'蔡佳穎', N'jiaying2@example.com', N'0955234567', N'338', N'桃園市', N'蘆竹區', N'南竹路160號', CAST(N'2026-08-24T09:55:00.000' AS DateTime), CAST(N'1998-11-11' AS Date), N'女', CAST(N'2026-08-24T09:55:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (51, 55, N'鄭宇翔', N'yuxiang@example.com', N'0965234567', N'320', N'桃園市', N'中壢區', N'中山東路100號', CAST(N'2026-08-25T13:40:00.000' AS DateTime), CAST(N'1996-01-20' AS Date), N'男', CAST(N'2026-08-25T13:40:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (52, 56, N'周怡萱', N'yixuan2@example.com', N'0975234567', N'320', N'桃園市', N'中壢區', N'實踐路80號', CAST(N'2026-08-26T10:20:00.000' AS DateTime), CAST(N'2001-05-06' AS Date), N'女', CAST(N'2026-08-26T10:20:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (53, 57, N'許博翔', N'boxiang@example.com', N'0985234567', N'324', N'桃園市', N'平鎮區', N'振興路130號', CAST(N'2026-08-01T15:00:00.000' AS DateTime), CAST(N'1994-09-14' AS Date), N'男', CAST(N'2026-08-01T15:00:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (54, 58, N'李佳玲', N'jialing2@example.com', N'0906234567', N'324', N'桃園市', N'平鎮區', N'廣德街70號', CAST(N'2026-08-02T11:40:00.000' AS DateTime), CAST(N'1999-07-28' AS Date), N'女', CAST(N'2026-08-02T11:40:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (55, 59, N'吳宗憲', N'zongxian@example.com', N'0916234567', N'325', N'桃園市', N'龍潭區', N'龍元路100號', CAST(N'2026-08-03T14:15:00.000' AS DateTime), CAST(N'1991-12-09' AS Date), N'男', CAST(N'2026-08-03T14:15:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (56, 60, N'林詩婷', N'shiting@example.com', N'0926234567', N'325', N'桃園市', N'龍潭區', N'東龍路150號', CAST(N'2026-08-04T09:25:00.000' AS DateTime), CAST(N'1997-06-04' AS Date), N'女', CAST(N'2026-08-04T09:25:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (57, 61, N'黃志豪', N'zhihao2@example.com', N'0936234567', N'326', N'桃園市', N'楊梅區', N'瑞溪路80號', CAST(N'2026-08-05T16:05:00.000' AS DateTime), CAST(N'1995-10-19' AS Date), N'男', CAST(N'2026-08-05T16:05:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (58, 62, N'陳怡君', N'yijun2@example.com', N'0946234567', N'326', N'桃園市', N'楊梅區', N'新農街120號', CAST(N'2026-08-06T10:10:00.000' AS DateTime), CAST(N'2000-01-31' AS Date), N'女', CAST(N'2026-08-06T10:10:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (59, 63, N'王俊凱', N'junkai@example.com', N'0956234567', N'327', N'桃園市', N'新屋區', N'中興路60號', CAST(N'2026-08-07T13:35:00.000' AS DateTime), CAST(N'1993-08-22' AS Date), N'男', CAST(N'2026-08-07T13:35:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (60, 64, N'張淑貞', N'shuzhen@example.com', N'0966234567', N'327', N'桃園市', N'新屋區', N'永安路90號', CAST(N'2026-08-08T11:25:00.000' AS DateTime), CAST(N'1990-05-17' AS Date), N'女', CAST(N'2026-08-08T11:25:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (61, 65, N'林家豪', N'jiahao2@example.com', N'0976234567', N'328', N'桃園市', N'觀音區', N'草漯路130號', CAST(N'2026-08-09T15:50:00.000' AS DateTime), CAST(N'1998-02-08' AS Date), N'男', CAST(N'2026-08-09T15:50:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (62, 66, N'吳佳蓉', N'jiarong2@example.com', N'0986234567', N'328', N'桃園市', N'觀音區', N'大同路50號', CAST(N'2026-08-10T09:05:00.000' AS DateTime), CAST(N'1996-12-20' AS Date), N'女', CAST(N'2026-08-10T09:05:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (63, 67, N'蔡承恩', N'chengen@example.com', N'0907234567', N'330', N'桃園市', N'桃園區', N'南平路180號', CAST(N'2026-08-11T14:40:00.000' AS DateTime), CAST(N'2001-03-13' AS Date), N'男', CAST(N'2026-08-11T14:40:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (64, 68, N'楊欣怡', N'xinyi2@example.com', N'0917234567', N'330', N'桃園市', N'桃園區', N'大興西路100號', CAST(N'2026-08-12T10:55:00.000' AS DateTime), CAST(N'1999-09-24' AS Date), N'女', CAST(N'2026-08-12T10:55:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (65, 69, N'何冠霖', N'guanlin2@example.com', N'0927234567', N'333', N'桃園市', N'龜山區', N'德明路70號', CAST(N'2026-08-13T13:20:00.000' AS DateTime), CAST(N'1994-11-07' AS Date), N'男', CAST(N'2026-08-13T13:20:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (66, 70, N'徐佳琪', N'jiaqi@example.com', N'0937234567', N'333', N'桃園市', N'龜山區', N'文青路120號', CAST(N'2026-08-14T16:10:00.000' AS DateTime), CAST(N'1998-06-26' AS Date), N'女', CAST(N'2026-08-14T16:10:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (67, 71, N'鄭凱傑', N'kaijie@example.com', N'0947234567', N'334', N'桃園市', N'八德區', N'廣興路90號', CAST(N'2026-08-15T09:30:00.000' AS DateTime), CAST(N'1992-02-15' AS Date), N'男', CAST(N'2026-08-15T09:30:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (68, 72, N'謝雅婷', N'yating2@example.com', N'0957234567', N'334', N'桃園市', N'八德區', N'和平路210號', CAST(N'2026-08-16T12:45:00.000' AS DateTime), CAST(N'1997-10-03' AS Date), N'女', CAST(N'2026-08-16T12:45:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (69, 73, N'羅偉豪', N'weihao@example.com', N'0967234567', N'335', N'桃園市', N'大溪區', N'康莊路140號', CAST(N'2026-08-17T15:20:00.000' AS DateTime), CAST(N'1995-04-11' AS Date), N'男', CAST(N'2026-08-17T15:20:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (70, 74, N'劉怡伶', N'yiling@example.com', N'0977234567', N'335', N'桃園市', N'大溪區', N'仁和路80號', CAST(N'2026-08-18T10:00:00.000' AS DateTime), CAST(N'2000-07-29' AS Date), N'女', CAST(N'2026-08-18T10:00:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (71, 75, N'郭柏廷', N'boting@example.com', N'0987234567', N'336', N'桃園市', N'復興區', N'羅浮路30號', CAST(N'2026-08-19T14:55:00.000' AS DateTime), CAST(N'1993-01-17' AS Date), N'男', CAST(N'2026-08-19T14:55:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (72, 76, N'黃鈺婷', N'yuting@example.com', N'0908234567', N'337', N'桃園市', N'大園區', N'三民路110號', CAST(N'2026-08-20T11:15:00.000' AS DateTime), CAST(N'1996-08-09' AS Date), N'女', CAST(N'2026-08-20T11:15:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (73, 77, N'張哲維', N'zhewei@example.com', N'0918234567', N'337', N'桃園市', N'大園區', N'國際路200號', CAST(N'2026-08-21T16:25:00.000' AS DateTime), CAST(N'1999-05-23' AS Date), N'男', CAST(N'2026-08-21T16:25:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (74, 78, N'陳妍希', N'yanxi@example.com', N'0928234567', N'338', N'桃園市', N'蘆竹區', N'南福街60號', CAST(N'2026-08-22T09:40:00.000' AS DateTime), CAST(N'2001-11-02' AS Date), N'女', CAST(N'2026-08-22T09:40:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (75, 79, N'林昱辰', N'yuchen@example.com', N'0938234567', N'338', N'桃園市', N'蘆竹區', N'大竹路150號', CAST(N'2026-08-23T13:10:00.000' AS DateTime), CAST(N'1997-03-08' AS Date), N'男', CAST(N'2026-08-23T13:10:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (76, 80, N'王郁婷', N'yuting2@example.com', N'0948234567', N'320', N'桃園市', N'中壢區', N'龍東路100號', CAST(N'2026-08-24T15:35:00.000' AS DateTime), CAST(N'1998-12-18' AS Date), N'女', CAST(N'2026-08-24T15:35:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (77, 81, N'李俊豪', N'junhao@example.com', N'0958234567', N'320', N'桃園市', N'中壢區', N'榮民路180號', CAST(N'2026-08-25T10:25:00.000' AS DateTime), CAST(N'1994-06-05' AS Date), N'男', CAST(N'2026-08-25T10:25:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (78, 82, N'周怡婷', N'yiting2@example.com', N'0968234567', N'320', N'桃園市', N'中壢區', N'中北路200號', CAST(N'2026-08-26T14:05:00.000' AS DateTime), CAST(N'1999-09-15' AS Date), N'女', CAST(N'2026-08-26T14:05:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (79, 83, N'許志明', N'zhiming@example.com', N'0978234567', N'324', N'桃園市', N'平鎮區', N'民族路90號', CAST(N'2026-08-01T11:45:00.000' AS DateTime), CAST(N'1991-03-26' AS Date), N'男', CAST(N'2026-08-01T11:45:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (80, 84, N'蔡佩君', N'peijun@example.com', N'0988234567', N'324', N'桃園市', N'平鎮區', N'新富街70號', CAST(N'2026-08-02T15:15:00.000' AS DateTime), CAST(N'1996-10-08' AS Date), N'女', CAST(N'2026-08-02T15:15:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (81, 85, N'吳俊賢', N'junxian@example.com', N'0909234567', N'325', N'桃園市', N'龍潭區', N'中興路160號', CAST(N'2026-08-03T09:20:00.000' AS DateTime), CAST(N'1995-07-19' AS Date), N'男', CAST(N'2026-08-03T09:20:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (82, 86, N'林怡君', N'yijun3@example.com', N'0919234567', N'325', N'桃園市', N'龍潭區', N'龍華路100號', CAST(N'2026-08-04T13:45:00.000' AS DateTime), CAST(N'2000-04-12' AS Date), N'女', CAST(N'2026-08-04T13:45:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (83, 87, N'黃柏翰', N'bohan@example.com', N'0929234567', N'326', N'桃園市', N'楊梅區', N'校前路80號', CAST(N'2026-08-05T16:00:00.000' AS DateTime), CAST(N'1993-09-28' AS Date), N'男', CAST(N'2026-08-05T16:00:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (84, 88, N'張雅君', N'yajun@example.com', N'0939234567', N'326', N'桃園市', N'楊梅區', N'中山北路120號', CAST(N'2026-08-06T10:35:00.000' AS DateTime), CAST(N'1997-12-06' AS Date), N'女', CAST(N'2026-08-06T10:35:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (85, 89, N'陳冠廷', N'guanting2@example.com', N'0949234567', N'327', N'桃園市', N'新屋區', N'中山東路50號', CAST(N'2026-08-07T14:25:00.000' AS DateTime), CAST(N'1998-05-31' AS Date), N'男', CAST(N'2026-08-07T14:25:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (86, 90, N'王怡文', N'yiwen@example.com', N'0959234567', N'327', N'桃園市', N'新屋區', N'民族路100號', CAST(N'2026-08-08T11:05:00.000' AS DateTime), CAST(N'1995-11-16' AS Date), N'女', CAST(N'2026-08-08T11:05:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (87, 91, N'李宗翰', N'zonghan2@example.com', N'0969234567', N'328', N'桃園市', N'觀音區', N'成功路70號', CAST(N'2026-08-09T15:45:00.000' AS DateTime), CAST(N'1992-08-24' AS Date), N'男', CAST(N'2026-08-09T15:45:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (88, 92, N'謝宜庭', N'yiting3@example.com', N'0979234567', N'328', N'桃園市', N'觀音區', N'文化路130號', CAST(N'2026-08-10T09:55:00.000' AS DateTime), CAST(N'1999-02-17' AS Date), N'女', CAST(N'2026-08-10T09:55:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (89, 93, N'何冠宇', N'guanyu2@example.com', N'0989234567', N'330', N'桃園市', N'桃園區', N'同德路90號', CAST(N'2026-08-11T13:30:00.000' AS DateTime), CAST(N'1996-06-21' AS Date), N'男', CAST(N'2026-08-11T13:30:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (90, 94, N'楊淑惠', N'shuhui@example.com', N'0901345678', N'330', N'桃園市', N'桃園區', N'中山路180號', CAST(N'2026-08-12T10:20:00.000' AS DateTime), CAST(N'1990-12-03' AS Date), N'女', CAST(N'2026-08-12T10:20:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (91, 95, N'郭建宏', N'jianhong2@example.com', N'0911345678', N'333', N'桃園市', N'龜山區', N'文化三路150號', CAST(N'2026-08-13T16:05:00.000' AS DateTime), CAST(N'1994-04-19' AS Date), N'男', CAST(N'2026-08-13T16:05:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (92, 96, N'劉佳玲', N'jialing3@example.com', N'0921345678', N'333', N'桃園市', N'龜山區', N'復興北路80號', CAST(N'2026-08-14T11:40:00.000' AS DateTime), CAST(N'1998-09-02' AS Date), N'女', CAST(N'2026-08-14T11:40:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (93, 97, N'鄭志偉', N'zhiwei2@example.com', N'0931345678', N'334', N'桃園市', N'八德區', N'豐德路120號', CAST(N'2026-08-15T14:50:00.000' AS DateTime), CAST(N'1991-06-13' AS Date), N'男', CAST(N'2026-08-15T14:50:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (94, 98, N'吳雅婷', N'yating3@example.com', N'0941345678', N'334', N'桃園市', N'八德區', N'建國路200號', CAST(N'2026-08-16T09:25:00.000' AS DateTime), CAST(N'2000-03-05' AS Date), N'女', CAST(N'2026-08-16T09:25:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (95, 99, N'林志豪', N'zhihao3@example.com', N'0951345678', N'335', N'桃園市', N'大溪區', N'埔頂路100號', CAST(N'2026-08-17T13:15:00.000' AS DateTime), CAST(N'1995-10-27' AS Date), N'男', CAST(N'2026-08-17T13:15:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (96, 100, N'張婉婷', N'wanting2@example.com', N'0961345678', N'335', N'桃園市', N'大溪區', N'介壽路60號', CAST(N'2026-08-18T15:40:00.000' AS DateTime), CAST(N'1997-01-14' AS Date), N'女', CAST(N'2026-08-18T15:40:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (97, 101, N'王俊傑', N'junjie3@example.com', N'0971345678', N'336', N'桃園市', N'復興區', N'澤仁路40號', CAST(N'2026-08-19T10:10:00.000' AS DateTime), CAST(N'1993-07-09' AS Date), N'男', CAST(N'2026-08-19T10:10:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (98, 102, N'陳思妤', N'siyu2@example.com', N'0981345678', N'337', N'桃園市', N'大園區', N'和平西路100號', CAST(N'2026-08-20T14:35:00.000' AS DateTime), CAST(N'1999-11-21' AS Date), N'女', CAST(N'2026-08-20T14:35:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (99, 103, N'黃建霖', N'jianlin@example.com', N'0902345678', N'337', N'桃園市', N'大園區', N'中正東路160號', CAST(N'2026-08-21T11:30:00.000' AS DateTime), CAST(N'1996-05-03' AS Date), N'男', CAST(N'2026-08-21T11:30:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (100, 104, N'李欣妤', N'xinyu3@example.com', N'0912345679', N'338', N'桃園市', N'蘆竹區', N'南昌路90號', CAST(N'2026-08-22T16:20:00.000' AS DateTime), CAST(N'2001-08-12' AS Date), N'女', CAST(N'2026-08-22T16:20:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (101, 105, N'蔡承翰', N'chenghan@example.com', N'0922345678', N'338', N'桃園市', N'蘆竹區', N'五福一路130號', CAST(N'2026-08-23T09:45:00.000' AS DateTime), CAST(N'1994-02-28' AS Date), N'男', CAST(N'2026-08-23T09:45:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (102, 4, N'周雅雯', N'yawen2@example.com', N'0932345678', N'320', N'桃園市', N'中壢區', N'莒光路70號', CAST(N'2026-08-24T13:05:00.000' AS DateTime), CAST(N'1998-10-16' AS Date), N'女', CAST(N'2026-08-24T13:05:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (103, 3, N'許哲維', N'zhewei2@example.com', N'0942345678', N'320', N'桃園市', N'中壢區', N'環西路100號', CAST(N'2026-08-25T15:25:00.000' AS DateTime), CAST(N'1992-11-08' AS Date), N'男', CAST(N'2026-08-25T15:25:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (104, 2, N'楊欣怡', N'xinyi4@example.com', N'0952345678', N'324', N'桃園市', N'平鎮區', N'振興西路80號', CAST(N'2026-08-26T10:50:00.000' AS DateTime), CAST(N'1997-04-24' AS Date), N'女', CAST(N'2026-08-26T10:50:00.000' AS DateTime))
GO
INSERT [dbo].[profile] ([profile_id], [account_id], [name], [email], [phone], [zipcode], [city], [district], [address], [created_at], [birthday], [gender], [updated_at]) VALUES (105, 1, N'羅俊豪', N'junhao2@example.com', N'0962345678', N'325', N'桃園市', N'龍潭區', N'中正路220號', CAST(N'2026-08-26T14:30:00.000' AS DateTime), CAST(N'1995-09-13' AS Date), N'男', CAST(N'2026-08-26T14:30:00.000' AS DateTime))
GO
SET IDENTITY_INSERT [dbo].[profile] OFF
GO
SET IDENTITY_INSERT [dbo].[payment] ON 
GO
INSERT [dbo].[payment] ([payment_id], [payment_method]) VALUES (1, N'Apple PAY')
GO
INSERT [dbo].[payment] ([payment_id], [payment_method]) VALUES (2, N'LINE PAY')
GO
INSERT [dbo].[payment] ([payment_id], [payment_method]) VALUES (3, N'信用卡')
GO
INSERT [dbo].[payment] ([payment_id], [payment_method]) VALUES (4, N'現金')
GO
INSERT [dbo].[payment] ([payment_id], [payment_method]) VALUES (5, N'銀行轉帳')
GO
SET IDENTITY_INSERT [dbo].[payment] OFF
GO
SET IDENTITY_INSERT [dbo].[booking_order] ON 
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (1, 12, 6500, N'訂單完成', CAST(N'2026-08-17T09:15:20.000' AS DateTime), 3)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (2, 45, 9300, N'訂單完成', CAST(N'2026-08-17T14:22:05.000' AS DateTime), 1)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (3, 8, 11000, N'訂單取消', CAST(N'2026-08-17T18:40:12.000' AS DateTime), 4)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (4, 53, 7100, N'訂單完成', CAST(N'2026-08-18T10:05:30.000' AS DateTime), 2)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (5, 27, 14600, N'訂單完成', CAST(N'2026-08-18T13:50:00.000' AS DateTime), 5)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (6, 60, 6800, N'訂單取消', CAST(N'2026-08-18T21:12:45.000' AS DateTime), 3)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (7, 1, 8800, N'訂單完成', CAST(N'2026-08-19T08:30:10.000' AS DateTime), 1)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (8, 39, 12300, N'訂單完成', CAST(N'2026-08-19T11:45:22.000' AS DateTime), 2)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (9, 18, 6500, N'訂單完成', CAST(N'2026-08-19T16:20:00.000' AS DateTime), 4)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (10, 32, 17600, N'訂單取消', CAST(N'2026-08-19T20:05:15.000' AS DateTime), 5)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (11, 5, 9600, N'訂單完成', CAST(N'2026-08-20T09:10:40.000' AS DateTime), 3)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (12, 58, 7300, N'訂單完成', CAST(N'2026-08-20T12:35:00.000' AS DateTime), 2)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (13, 21, 11800, N'訂單完成', CAST(N'2026-08-20T15:50:30.000' AS DateTime), 1)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (14, 44, 6300, N'訂單取消', CAST(N'2026-08-20T19:15:10.000' AS DateTime), 4)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (15, 10, 14000, N'訂單完成', CAST(N'2026-08-21T08:05:25.000' AS DateTime), 5)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (16, 56, 8800, N'訂單完成', CAST(N'2026-08-21T11:20:18.000' AS DateTime), 2)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (17, 30, 10400, N'訂單完成', CAST(N'2026-08-21T14:45:50.000' AS DateTime), 3)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (18, 3, 6800, N'訂單取消', CAST(N'2026-08-21T18:30:00.000' AS DateTime), 1)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (19, 41, 16800, N'訂單完成', CAST(N'2026-08-22T09:40:15.000' AS DateTime), 4)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (20, 15, 9100, N'訂單完成', CAST(N'2026-08-22T13:10:22.000' AS DateTime), 5)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (21, 49, 6500, N'訂單完成', CAST(N'2026-08-22T17:25:00.000' AS DateTime), 2)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (22, 14, 12600, N'訂單取消', CAST(N'2026-08-22T21:00:40.000' AS DateTime), 3)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (23, 35, 7100, N'訂單完成', CAST(N'2026-08-23T08:15:12.000' AS DateTime), 1)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (24, 59, 14600, N'訂單完成', CAST(N'2026-08-23T10:50:35.000' AS DateTime), 4)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (25, 25, 8800, N'訂單完成', CAST(N'2026-08-23T12:20:00.000' AS DateTime), 2)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (26, 7, 11000, N'訂單取消', CAST(N'2026-08-23T14:05:45.000' AS DateTime), 5)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (27, 48, 6800, N'訂單完成', CAST(N'2026-08-23T15:40:10.000' AS DateTime), 3)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (28, 23, 9600, N'訂單完成', CAST(N'2026-08-23T16:30:25.000' AS DateTime), 1)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (29, 52, 17600, N'訂單完成', CAST(N'2026-08-23T17:15:00.000' AS DateTime), 2)
GO
INSERT [dbo].[booking_order] ([booking_order_id], [member_id], [booking_total_price], [order_status], [created_at], [payment_id]) VALUES (30, 19, 7300, N'訂單完成', CAST(N'2026-08-23T18:00:50.000' AS DateTime), 4)
GO
SET IDENTITY_INSERT [dbo].[booking_order] OFF
GO
SET IDENTITY_INSERT [dbo].[room_type] ON 
GO
INSERT [dbo].[room_type] ([room_type_id], [type_name], [bed_type], [capacity], [room_description], [price_per_night]) VALUES (1, N'標準海景雙人房', N'1張雙人床', 2, N'含雙人早餐，擁有獨立海景陽台', 3500)
GO
INSERT [dbo].[room_type] ([room_type_id], [type_name], [bed_type], [capacity], [room_description], [price_per_night]) VALUES (2, N'標準山景雙人房', N'1張雙人床', 2, N'含雙人早餐，享受靜謐山景', 3000)
GO
INSERT [dbo].[room_type] ([room_type_id], [type_name], [bed_type], [capacity], [room_description], [price_per_night]) VALUES (3, N'雅緻海景雙床房', N'2張單人床', 2, N'含雙人早餐，海景客房，適合商務或好友', 3800)
GO
INSERT [dbo].[room_type] ([room_type_id], [type_name], [bed_type], [capacity], [room_description], [price_per_night]) VALUES (4, N'雅緻山景雙床房', N'2張單人床', 2, N'含雙人早餐，山景客房，適合商務或好友', 3300)
GO
INSERT [dbo].[room_type] ([room_type_id], [type_name], [bed_type], [capacity], [room_description], [price_per_night]) VALUES (5, N'溫馨海景家庭房', N'2張雙人床', 4, N'含四人早餐，家庭出遊首選海景房', 5800)
GO
INSERT [dbo].[room_type] ([room_type_id], [type_name], [bed_type], [capacity], [room_description], [price_per_night]) VALUES (6, N'溫馨山景家庭房', N'2張雙人床', 4, N'含四人早餐，空間寬敞，綠意山景', 5200)
GO
INSERT [dbo].[room_type] ([room_type_id], [type_name], [bed_type], [capacity], [room_description], [price_per_night]) VALUES (7, N'行政海景尊榮套房', N'1張加大雙人床', 2, N'含雙人早餐與行政酒廊權益，高樓層無敵海景', 8800)
GO
INSERT [dbo].[room_type] ([room_type_id], [type_name], [bed_type], [capacity], [room_description], [price_per_night]) VALUES (8, N'行政山景尊榮套房', N'1張加大雙人床', 2, N'含雙人早餐與行政酒廊權益，高樓層環景山景', 8000)
GO
INSERT [dbo].[room_type] ([room_type_id], [type_name], [bed_type], [capacity], [room_description], [price_per_night]) VALUES (9, N'豪華全景海景四人套房', N'2張加大雙人床', 4, N'含四人早餐，獨立會客廳，高樓層雙面海景', 13800)
GO
INSERT [dbo].[room_type] ([room_type_id], [type_name], [bed_type], [capacity], [room_description], [price_per_night]) VALUES (10, N'頂級海景皇家總統套房', N'1張特大雙人床', 2, N'含專屬管家與豪華早餐，獨立露台與私人酒廊', 32000)
GO
SET IDENTITY_INSERT [dbo].[room_type] OFF
GO
SET IDENTITY_INSERT [dbo].[room] ON 
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (1, N'10501', 1, 5, N'退房待清潔')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (2, N'10502', 1, 5, N'退房待清潔')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (3, N'10503', 1, 5, N'已入住')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (4, N'10504', 1, 5, N'已入住')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (5, N'10505', 3, 5, N'退房待清潔')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (6, N'10506', 3, 5, N'退房待清潔')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (7, N'10507', 3, 5, N'已入住')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (8, N'10508', 3, 5, N'已入住')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (9, N'10509', 1, 5, N'已入住')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (10, N'10510', 1, 5, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (11, N'10511', 3, 5, N'已入住')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (12, N'10512', 3, 5, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (13, N'20501', 2, 5, N'退房待清潔')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (14, N'20502', 2, 5, N'已入住')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (15, N'20503', 2, 5, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (16, N'20504', 2, 5, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (17, N'20505', 4, 5, N'退房待清潔')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (18, N'20506', 4, 5, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (19, N'20507', 4, 5, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (20, N'20508', 4, 5, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (21, N'20509', 2, 5, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (22, N'20510', 2, 5, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (23, N'20511', 4, 5, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (24, N'20512', 4, 5, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (25, N'10601', 1, 6, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (26, N'10602', 1, 6, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (27, N'10603', 1, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (28, N'10604', 1, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (29, N'10605', 3, 6, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (30, N'10606', 3, 6, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (31, N'10607', 3, 6, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (32, N'10608', 3, 6, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (33, N'10609', 1, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (34, N'10610', 1, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (35, N'10611', 3, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (36, N'10612', 3, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (37, N'20601', 2, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (38, N'20602', 2, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (39, N'20603', 2, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (40, N'20604', 2, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (41, N'20605', 4, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (42, N'20606', 4, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (43, N'20607', 4, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (44, N'20608', 4, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (45, N'20609', 2, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (46, N'20610', 2, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (47, N'20611', 4, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (48, N'20612', 4, 6, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (49, N'10701', 5, 7, N'退房待清潔')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (50, N'10702', 5, 7, N'退房待清潔')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (51, N'10703', 5, 7, N'已入住')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (52, N'10704', 5, 7, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (53, N'10705', 5, 7, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (54, N'10706', 5, 7, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (55, N'10707', 5, 7, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (56, N'10708', 5, 7, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (57, N'10709', 5, 7, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (58, N'10710', 5, 7, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (59, N'10711', 5, 7, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (60, N'10712', 5, 7, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (61, N'20701', 6, 7, N'已入住')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (62, N'20702', 6, 7, N'已入住')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (63, N'20703', 6, 7, N'已入住')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (64, N'20704', 6, 7, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (65, N'20705', 6, 7, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (66, N'20706', 6, 7, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (67, N'20707', 6, 7, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (68, N'20708', 6, 7, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (69, N'20709', 6, 7, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (70, N'20710', 6, 7, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (71, N'20711', 6, 7, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (72, N'20712', 6, 7, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (73, N'10801', 1, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (74, N'10802', 1, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (75, N'10803', 1, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (76, N'10804', 1, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (77, N'10805', 3, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (78, N'10806', 3, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (79, N'10807', 3, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (80, N'10808', 3, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (81, N'10809', 5, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (82, N'10810', 5, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (83, N'10811', 5, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (84, N'10812', 5, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (85, N'20801', 2, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (86, N'20802', 2, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (87, N'20803', 2, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (88, N'20804', 2, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (89, N'20805', 4, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (90, N'20806', 4, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (91, N'20807', 4, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (92, N'20808', 4, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (93, N'20809', 6, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (94, N'20810', 6, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (95, N'20811', 6, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (96, N'20812', 6, 8, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (97, N'10901', 7, 9, N'退房待清潔')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (98, N'10902', 7, 9, N'已入住')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (99, N'10903', 7, 9, N'已入住')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (100, N'10904', 7, 9, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (101, N'10905', 7, 9, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (102, N'10906', 7, 9, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (103, N'20901', 8, 9, N'退房待清潔')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (104, N'20902', 8, 9, N'已入住')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (105, N'20903', 8, 9, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (106, N'20904', 8, 9, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (107, N'20905', 8, 9, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (108, N'20906', 8, 9, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (109, N'31001', 9, 10, N'已預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (110, N'31002', 9, 10, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (111, N'31003', 9, 10, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (112, N'31004', 9, 10, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (113, N'31005', 9, 10, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (114, N'31006', 9, 10, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (115, N'31007', 9, 10, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (116, N'31008', 9, 10, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (117, N'31009', 9, 10, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (118, N'31010', 9, 10, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (119, N'31011', 10, 10, N'可預訂')
GO
INSERT [dbo].[room] ([room_id], [room_number], [room_type_id], [floor], [room_status]) VALUES (120, N'31012', 10, 10, N'可預訂')
GO
SET IDENTITY_INSERT [dbo].[room] OFF
GO
SET IDENTITY_INSERT [dbo].[booking] ON 
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (2, 1, 3500, CAST(N'2026-08-19T15:00:00.000' AS DateTime), CAST(N'2026-08-20T11:00:00.000' AS DateTime), 2, N'已退房', 1, 1)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (3, 1, 3000, CAST(N'2026-08-19T15:00:00.000' AS DateTime), CAST(N'2026-08-20T11:00:00.000' AS DateTime), 2, N'已退房', 13, 2)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (4, 2, 3500, CAST(N'2026-08-18T15:00:00.000' AS DateTime), CAST(N'2026-08-19T11:00:00.000' AS DateTime), 2, N'已退房', 2, 1)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (5, 2, 5800, CAST(N'2026-08-18T15:00:00.000' AS DateTime), CAST(N'2026-08-19T11:00:00.000' AS DateTime), 4, N'已退房', 49, 5)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (6, 3, 5800, CAST(N'2026-08-20T15:00:00.000' AS DateTime), CAST(N'2026-08-21T11:00:00.000' AS DateTime), 4, N'已取消', NULL, 5)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (7, 3, 5200, CAST(N'2026-08-20T15:00:00.000' AS DateTime), CAST(N'2026-08-21T11:00:00.000' AS DateTime), 4, N'已取消', NULL, 6)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (8, 4, 3800, CAST(N'2026-08-21T15:00:00.000' AS DateTime), CAST(N'2026-08-22T11:00:00.000' AS DateTime), 2, N'已退房', 5, 3)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (9, 4, 3300, CAST(N'2026-08-21T15:00:00.000' AS DateTime), CAST(N'2026-08-22T11:00:00.000' AS DateTime), 2, N'已退房', 17, 4)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (10, 5, 8800, CAST(N'2026-08-20T15:00:00.000' AS DateTime), CAST(N'2026-08-21T11:00:00.000' AS DateTime), 2, N'已退房', 97, 7)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (11, 5, 5800, CAST(N'2026-08-20T15:00:00.000' AS DateTime), CAST(N'2026-08-21T11:00:00.000' AS DateTime), 4, N'已退房', 50, 5)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (12, 6, 3500, CAST(N'2026-08-22T15:00:00.000' AS DateTime), CAST(N'2026-08-23T11:00:00.000' AS DateTime), 2, N'已取消', NULL, 1)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (13, 6, 3300, CAST(N'2026-08-22T15:00:00.000' AS DateTime), CAST(N'2026-08-23T11:00:00.000' AS DateTime), 2, N'已取消', NULL, 4)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (14, 7, 5000, CAST(N'2026-08-21T15:00:00.000' AS DateTime), CAST(N'2026-08-22T11:00:00.000' AS DateTime), 2, N'已退房', 103, 8)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (15, 7, 3800, CAST(N'2026-08-21T15:00:00.000' AS DateTime), CAST(N'2026-08-22T11:00:00.000' AS DateTime), 2, N'已退房', 6, 3)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (16, 8, 8800, CAST(N'2026-08-22T15:00:00.000' AS DateTime), CAST(N'2026-08-23T11:00:00.000' AS DateTime), 2, N'已入住', 98, 7)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (17, 8, 3500, CAST(N'2026-08-22T15:00:00.000' AS DateTime), CAST(N'2026-08-23T11:00:00.000' AS DateTime), 2, N'已入住', 3, 1)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (18, 9, 3500, CAST(N'2026-08-22T15:00:00.000' AS DateTime), CAST(N'2026-08-23T11:00:00.000' AS DateTime), 2, N'已入住', 4, 1)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (19, 9, 3000, CAST(N'2026-08-22T15:00:00.000' AS DateTime), CAST(N'2026-08-23T11:00:00.000' AS DateTime), 2, N'已入住', 14, 2)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (20, 10, 8800, CAST(N'2026-08-23T15:00:00.000' AS DateTime), CAST(N'2026-08-24T11:00:00.000' AS DateTime), 2, N'已取消', NULL, 7)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (21, 10, 8800, CAST(N'2026-08-23T15:00:00.000' AS DateTime), CAST(N'2026-08-24T11:00:00.000' AS DateTime), 2, N'已取消', NULL, 7)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (22, 11, 5800, CAST(N'2026-08-22T15:00:00.000' AS DateTime), CAST(N'2026-08-23T11:00:00.000' AS DateTime), 4, N'已入住', 51, 5)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (23, 11, 3800, CAST(N'2026-08-22T15:00:00.000' AS DateTime), CAST(N'2026-08-23T11:00:00.000' AS DateTime), 2, N'已入住', 7, 3)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (24, 12, 3800, CAST(N'2026-08-23T15:00:00.000' AS DateTime), CAST(N'2026-08-24T11:00:00.000' AS DateTime), 2, N'已入住', 8, 3)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (25, 12, 3500, CAST(N'2026-08-23T15:00:00.000' AS DateTime), CAST(N'2026-08-24T11:00:00.000' AS DateTime), 2, N'已入住', 9, 1)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (26, 13, 8000, CAST(N'2026-08-23T15:00:00.000' AS DateTime), CAST(N'2026-08-24T11:00:00.000' AS DateTime), 2, N'已入住', 104, 8)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (27, 13, 3800, CAST(N'2026-08-23T15:00:00.000' AS DateTime), CAST(N'2026-08-24T11:00:00.000' AS DateTime), 2, N'已入住', 11, 3)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (28, 14, 3300, CAST(N'2026-08-24T15:00:00.000' AS DateTime), CAST(N'2026-08-25T11:00:00.000' AS DateTime), 2, N'已取消', NULL, 4)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (29, 14, 3000, CAST(N'2026-08-24T15:00:00.000' AS DateTime), CAST(N'2026-08-25T11:00:00.000' AS DateTime), 2, N'已取消', NULL, 2)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (30, 15, 8800, CAST(N'2026-08-23T15:00:00.000' AS DateTime), CAST(N'2026-08-24T11:00:00.000' AS DateTime), 2, N'已入住', 99, 7)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (31, 15, 5200, CAST(N'2026-08-23T15:00:00.000' AS DateTime), CAST(N'2026-08-24T11:00:00.000' AS DateTime), 4, N'已入住', 61, 6)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (32, 16, 5000, CAST(N'2026-08-24T15:00:00.000' AS DateTime), CAST(N'2026-08-25T11:00:00.000' AS DateTime), 2, N'待入住', 105, 8)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (33, 16, 3800, CAST(N'2026-08-24T15:00:00.000' AS DateTime), CAST(N'2026-08-25T11:00:00.000' AS DateTime), 2, N'待入住', 12, 3)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (34, 17, 5200, CAST(N'2026-08-23T15:00:00.000' AS DateTime), CAST(N'2026-08-24T11:00:00.000' AS DateTime), 4, N'已入住', 62, 6)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (35, 17, 5200, CAST(N'2026-08-23T15:00:00.000' AS DateTime), CAST(N'2026-08-24T11:00:00.000' AS DateTime), 4, N'已入住', 63, 6)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (36, 18, 3500, CAST(N'2026-08-25T15:00:00.000' AS DateTime), CAST(N'2026-08-26T11:00:00.000' AS DateTime), 2, N'已取消', NULL, 1)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (37, 18, 3300, CAST(N'2026-08-25T15:00:00.000' AS DateTime), CAST(N'2026-08-26T11:00:00.000' AS DateTime), 2, N'已取消', NULL, 4)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (38, 19, 13800, CAST(N'2026-08-24T15:00:00.000' AS DateTime), CAST(N'2026-08-25T11:00:00.000' AS DateTime), 4, N'待入住', 109, 9)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (39, 19, 3000, CAST(N'2026-08-24T15:00:00.000' AS DateTime), CAST(N'2026-08-25T11:00:00.000' AS DateTime), 2, N'待入住', 15, 2)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (40, 20, 5800, CAST(N'2026-08-25T15:00:00.000' AS DateTime), CAST(N'2026-08-26T11:00:00.000' AS DateTime), 4, N'待入住', 52, 5)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (41, 20, 3300, CAST(N'2026-08-25T15:00:00.000' AS DateTime), CAST(N'2026-08-26T11:00:00.000' AS DateTime), 2, N'待入住', 18, 4)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (42, 21, 3500, CAST(N'2026-08-24T15:00:00.000' AS DateTime), CAST(N'2026-08-25T11:00:00.000' AS DateTime), 2, N'待入住', 10, 1)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (43, 21, 3000, CAST(N'2026-08-24T15:00:00.000' AS DateTime), CAST(N'2026-08-25T11:00:00.000' AS DateTime), 2, N'待入住', 16, 2)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (44, 22, 8800, CAST(N'2026-08-26T15:00:00.000' AS DateTime), CAST(N'2026-08-27T11:00:00.000' AS DateTime), 2, N'已取消', NULL, 7)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (45, 22, 3800, CAST(N'2026-08-26T15:00:00.000' AS DateTime), CAST(N'2026-08-27T11:00:00.000' AS DateTime), 2, N'已取消', NULL, 3)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (46, 23, 3800, CAST(N'2026-08-25T15:00:00.000' AS DateTime), CAST(N'2026-08-26T11:00:00.000' AS DateTime), 2, N'待入住', 29, 3)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (47, 23, 3300, CAST(N'2026-08-25T15:00:00.000' AS DateTime), CAST(N'2026-08-26T11:00:00.000' AS DateTime), 2, N'待入住', 19, 4)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (48, 24, 8800, CAST(N'2026-08-26T15:00:00.000' AS DateTime), CAST(N'2026-08-27T11:00:00.000' AS DateTime), 2, N'待入住', 100, 7)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (49, 24, 5800, CAST(N'2026-08-26T15:00:00.000' AS DateTime), CAST(N'2026-08-27T11:00:00.000' AS DateTime), 4, N'待入住', 53, 5)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (50, 25, 5000, CAST(N'2026-08-25T15:00:00.000' AS DateTime), CAST(N'2026-08-26T11:00:00.000' AS DateTime), 2, N'待入住', 106, 8)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (51, 25, 3800, CAST(N'2026-08-25T15:00:00.000' AS DateTime), CAST(N'2026-08-26T11:00:00.000' AS DateTime), 2, N'待入住', 30, 3)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (52, 26, 5800, CAST(N'2026-08-27T15:00:00.000' AS DateTime), CAST(N'2026-08-28T11:00:00.000' AS DateTime), 4, N'已取消', NULL, 5)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (53, 26, 5200, CAST(N'2026-08-27T15:00:00.000' AS DateTime), CAST(N'2026-08-28T11:00:00.000' AS DateTime), 4, N'已取消', NULL, 6)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (54, 27, 3500, CAST(N'2026-08-26T15:00:00.000' AS DateTime), CAST(N'2026-08-27T11:00:00.000' AS DateTime), 2, N'待入住', 25, 1)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (55, 27, 3300, CAST(N'2026-08-26T15:00:00.000' AS DateTime), CAST(N'2026-08-27T11:00:00.000' AS DateTime), 2, N'待入住', 20, 4)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (56, 28, 5800, CAST(N'2026-08-27T15:00:00.000' AS DateTime), CAST(N'2026-08-28T11:00:00.000' AS DateTime), 4, N'待入住', 54, 5)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (57, 28, 3800, CAST(N'2026-08-27T15:00:00.000' AS DateTime), CAST(N'2026-08-28T11:00:00.000' AS DateTime), 2, N'待入住', 31, 3)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (58, 29, 8800, CAST(N'2026-08-26T15:00:00.000' AS DateTime), CAST(N'2026-08-27T11:00:00.000' AS DateTime), 2, N'待入住', 101, 7)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (59, 29, 8800, CAST(N'2026-08-26T15:00:00.000' AS DateTime), CAST(N'2026-08-27T11:00:00.000' AS DateTime), 2, N'待入住', 102, 7)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (60, 30, 3800, CAST(N'2026-08-27T15:00:00.000' AS DateTime), CAST(N'2026-08-28T11:00:00.000' AS DateTime), 2, N'待入住', 32, 3)
GO
INSERT [dbo].[booking] ([booking_id], [booking_order_id], [booking_price], [check_in_date], [check_out_date], [guest_num], [booking_status], [room_id], [room_type_id]) VALUES (61, 30, 3500, CAST(N'2026-08-27T15:00:00.000' AS DateTime), CAST(N'2026-08-28T11:00:00.000' AS DateTime), 2, N'待入住', 26, 1)
GO
SET IDENTITY_INSERT [dbo].[booking] OFF
GO
SET IDENTITY_INSERT [dbo].[permission] ON 
GO
INSERT [dbo].[permission] ([permission_id], [permission_code], [permission_name]) VALUES (1, N'ROOM_MANAGE', N'房間管理')
GO
INSERT [dbo].[permission] ([permission_id], [permission_code], [permission_name]) VALUES (2, N'BOOKING_MANAGE', N'訂房管理')
GO
INSERT [dbo].[permission] ([permission_id], [permission_code], [permission_name]) VALUES (3, N'RESTAURANT_MANAGE', N'餐廳管理')
GO
INSERT [dbo].[permission] ([permission_id], [permission_code], [permission_name]) VALUES (4, N'MEMBER_MANAGE', N'會員管理')
GO
INSERT [dbo].[permission] ([permission_id], [permission_code], [permission_name]) VALUES (5, N'ORDER_MANAGE', N'訂單管理')
GO
SET IDENTITY_INSERT [dbo].[permission] OFF
GO
SET IDENTITY_INSERT [dbo].[employee_permission] ON 
GO
INSERT [dbo].[employee_permission] ([permission_id], [employee_id]) VALUES (1, 1)
GO
INSERT [dbo].[employee_permission] ([permission_id], [employee_id]) VALUES (1, 2)
GO
INSERT [dbo].[employee_permission] ([permission_id], [employee_id]) VALUES (1, 3)
GO
INSERT [dbo].[employee_permission] ([permission_id], [employee_id]) VALUES (1, 4)
GO
INSERT [dbo].[employee_permission] ([permission_id], [employee_id]) VALUES (2, 1)
GO
INSERT [dbo].[employee_permission] ([permission_id], [employee_id]) VALUES (2, 2)
GO
INSERT [dbo].[employee_permission] ([permission_id], [employee_id]) VALUES (2, 3)
GO
INSERT [dbo].[employee_permission] ([permission_id], [employee_id]) VALUES (3, 1)
GO
INSERT [dbo].[employee_permission] ([permission_id], [employee_id]) VALUES (3, 5)
GO
INSERT [dbo].[employee_permission] ([permission_id], [employee_id]) VALUES (3, 6)
GO
INSERT [dbo].[employee_permission] ([permission_id], [employee_id]) VALUES (4, 1)
GO
INSERT [dbo].[employee_permission] ([permission_id], [employee_id]) VALUES (4, 2)
GO
INSERT [dbo].[employee_permission] ([permission_id], [employee_id]) VALUES (5, 1)
GO
INSERT [dbo].[employee_permission] ([permission_id], [employee_id]) VALUES (5, 2)
GO
INSERT [dbo].[employee_permission] ([permission_id], [employee_id]) VALUES (5, 5)
GO
SET IDENTITY_INSERT [dbo].[employee_permission] OFF
GO
SET IDENTITY_INSERT [dbo].[room_task] ON 
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (1, 1, 13, N'緊急', N'退房清潔', N'進行中', N'客人已退房，需優先清潔整備', CAST(N'2026-08-23T11:15:00.000' AS DateTime), NULL)
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (2, 2, 14, N'緊急', N'退房清潔', N'待處理', N'客人已退房，待清潔', CAST(N'2026-08-23T11:20:00.000' AS DateTime), NULL)
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (3, 5, 15, N'重要', N'退房清潔', N'進行中', N'退房清潔中', CAST(N'2026-08-23T11:30:00.000' AS DateTime), NULL)
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (4, 6, 16, N'重要', N'退房清潔', N'待處理', N'待清潔房型', CAST(N'2026-08-23T11:35:00.000' AS DateTime), NULL)
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (5, 13, 17, N'緊急', N'退房清潔', N'待處理', N'待退房清潔', CAST(N'2026-08-23T11:40:00.000' AS DateTime), NULL)
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (6, 17, 18, N'重要', N'退房清潔', N'進行中', N'進行退房打掃', CAST(N'2026-08-23T11:45:00.000' AS DateTime), NULL)
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (7, 49, 19, N'緊急', N'退房清潔', N'待處理', N'待退房清潔', CAST(N'2026-08-23T12:00:00.000' AS DateTime), NULL)
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (8, 50, 20, N'重要', N'退房清潔', N'已完成', N'已完成清潔與備品更換', CAST(N'2026-08-23T11:10:00.000' AS DateTime), CAST(N'2026-08-23T12:30:00.000' AS DateTime))
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (9, 97, 21, N'緊急', N'退房清潔', N'待處理', N'待退房清潔', CAST(N'2026-08-23T12:15:00.000' AS DateTime), NULL)
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (10, 103, 22, N'重要', N'退房清潔', N'待處理', N'待退房清潔', CAST(N'2026-08-23T12:30:00.000' AS DateTime), NULL)
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (11, 3, 23, N'一般', N'日常清潔', N'進行中', N'房客要求簡短打掃', CAST(N'2026-08-23T13:30:00.000' AS DateTime), NULL)
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (12, 7, 24, N'一般', N'日常清潔', N'待處理', N'續住清潔', CAST(N'2026-08-23T14:00:00.000' AS DateTime), NULL)
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (13, 9, 26, N'一般', N'補充備品', N'待處理', N'補充毛巾與浴巾', CAST(N'2026-08-23T14:30:00.000' AS DateTime), NULL)
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (14, 15, 25, N'一般', N'日常清潔', N'進行中', N'續住房間打掃與整備', CAST(N'2026-08-23T14:45:00.000' AS DateTime), NULL)
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (15, 51, 11, N'一般', N'日常清潔', N'已完成', N'日常清潔已完成', CAST(N'2026-08-23T10:00:00.000' AS DateTime), CAST(N'2026-08-23T11:00:00.000' AS DateTime))
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (16, 61, 12, N'一般', N'補充備品', N'進行中', N'補充礦泉水與盥洗用品', CAST(N'2026-08-23T15:00:00.000' AS DateTime), NULL)
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (17, 4, 27, N'緊急', N'設備維修', N'進行中', N'冷氣不冷', CAST(N'2026-08-23T16:00:00.000' AS DateTime), NULL)
GO
INSERT [dbo].[room_task] ([task_id], [room_id], [employee_id], [priority], [task_type], [task_status], [remark], [created_at], [completed_at]) VALUES (18, 14, 28, N'重要', N'設備維修', N'待處理', N'馬桶堵塞', CAST(N'2026-08-23T16:30:00.000' AS DateTime), NULL)
GO
SET IDENTITY_INSERT [dbo].[room_task] OFF
GO
SET IDENTITY_INSERT [dbo].[order] ON 
GO
INSERT [dbo].[order] ([order_id], [member_id], [order_date], [is_ordered], [payment_id]) VALUES (1, 1, CAST(N'2026-08-10T15:20:00.000' AS DateTime), 1, 1)
GO
INSERT [dbo].[order] ([order_id], [member_id], [order_date], [is_ordered], [payment_id]) VALUES (2, 2, CAST(N'2026-08-11T12:10:00.000' AS DateTime), 1, 2)
GO
INSERT [dbo].[order] ([order_id], [member_id], [order_date], [is_ordered], [payment_id]) VALUES (3, 1, CAST(N'2026-08-13T18:50:00.000' AS DateTime), 1, 4)
GO
SET IDENTITY_INSERT [dbo].[order] OFF
GO
SET IDENTITY_INSERT [dbo].[restaurant] ON 
GO
INSERT [dbo].[restaurant] ([restaurant_id], [restaurant_name], [address], [phone], [capacity], [description]) VALUES (1, N'雲澄自助餐廳', N'桃園市中壢區中央西路100號', N'03-1234567', 120, N'提供中西式自助餐')
GO
INSERT [dbo].[restaurant] ([restaurant_id], [restaurant_name], [address], [phone], [capacity], [description]) VALUES (2, N'景觀咖啡廳', N'桃園市中壢區中央西路100號', N'03-1234568', 60, N'提供咖啡及下午茶')
GO
SET IDENTITY_INSERT [dbo].[restaurant] OFF
GO
SET IDENTITY_INSERT [dbo].[restaurant_time] ON 
GO
INSERT [dbo].[restaurant_time] ([time_id], [restaurant_id], [meal_type], [open_time], [close_time]) VALUES (1, 1, N'早餐', CAST(N'07:00:00' AS Time), CAST(N'10:00:00' AS Time))
GO
INSERT [dbo].[restaurant_time] ([time_id], [restaurant_id], [meal_type], [open_time], [close_time]) VALUES (2, 1, N'午餐', CAST(N'11:30:00' AS Time), CAST(N'14:00:00' AS Time))
GO
INSERT [dbo].[restaurant_time] ([time_id], [restaurant_id], [meal_type], [open_time], [close_time]) VALUES (3, 1, N'晚餐', CAST(N'17:30:00' AS Time), CAST(N'21:00:00' AS Time))
GO
INSERT [dbo].[restaurant_time] ([time_id], [restaurant_id], [meal_type], [open_time], [close_time]) VALUES (4, 2, N'下午茶', CAST(N'14:00:00' AS Time), CAST(N'17:00:00' AS Time))
GO
SET IDENTITY_INSERT [dbo].[restaurant_time] OFF
GO
SET IDENTITY_INSERT [dbo].[reservation] ON 
GO
INSERT [dbo].[reservation] ([reservation_id], [member_id], [contact_name], [contact_phone], [restaurant_id], [reservation_date], [time_id], [people_count], [status], [create_time]) VALUES (1, 1, N'王小明', N'0912345678', 1, CAST(N'2026-08-21' AS Date), 3, 2, N'已訂位', CAST(N'2026-08-15T10:00:00.000' AS DateTime))
GO
INSERT [dbo].[reservation] ([reservation_id], [member_id], [contact_name], [contact_phone], [restaurant_id], [reservation_date], [time_id], [people_count], [status], [create_time]) VALUES (2, 2, N'陳小華', N'0923456789', 1, CAST(N'2026-08-22' AS Date), 1, 3, N'已訂位', CAST(N'2026-08-16T11:30:00.000' AS DateTime))
GO
INSERT [dbo].[reservation] ([reservation_id], [member_id], [contact_name], [contact_phone], [restaurant_id], [reservation_date], [time_id], [people_count], [status], [create_time]) VALUES (3, NULL, N'張先生', N'0945678901', 2, CAST(N'2026-08-23' AS Date), 4, 2, N'已訂位', CAST(N'2026-08-17T15:00:00.000' AS DateTime))
GO
SET IDENTITY_INSERT [dbo].[reservation] OFF
GO
SET IDENTITY_INSERT [dbo].[product] ON 
GO
INSERT [dbo].[product] ([product_id], [product_name], [category_id], [description], [price], [stock], [ImageURL], [status]) VALUES (1, N'飯店馬克杯', 1, N'飯店限定陶瓷馬克杯', 350, 60, N'https://www.ikea.com.tw/dairyfarm/tw/images/425/0642527_PE701233_S5.jpg', N'ACTIVE')
GO
INSERT [dbo].[product] ([product_id], [product_name], [category_id], [description], [price], [stock], [ImageURL], [status]) VALUES (2, N'飯店保溫瓶', 1, N'不鏽鋼保溫瓶', 599, 20, N'https://pcm.trplus.com.tw/1000x1000/sys-master/productImages/h61/hf0/12482803433502/000000000014380795-gallery-01-20250512120526166.jpg', N'ACTIVE')
GO
INSERT [dbo].[product] ([product_id], [product_name], [category_id], [description], [price], [stock], [ImageURL], [status]) VALUES (3, N'飯店帆布袋', 1, N'飯店紀念帆布袋', 299, 25, N'https://img.pchome.com.tw/cs/items/DIBF8QA900FUMYK/l000001_1684827319.jpg?width=640', N'ACTIVE')
GO
INSERT [dbo].[product] ([product_id], [product_name], [category_id], [description], [price], [stock], [ImageURL], [status]) VALUES (4, N'飯店鑰匙圈', 1, N'飯店造型紀念鑰匙圈', 150, 50, N'https://ethergifts.com.tw/cdn/shop/files/Stainless-Steel-Round-Key-Ring_1.jpg?v=1744870780', N'ACTIVE')
GO
INSERT [dbo].[product] ([product_id], [product_name], [category_id], [description], [price], [stock], [ImageURL], [status]) VALUES (5, N'飯店明信片', 1, N'飯店風景紀念明信片', 80, 100, N'https://www.hsabc.com.tw/upload/Product/F_20180425073935Pw0Qv9.JPG', N'ACTIVE')
GO
INSERT [dbo].[product] ([product_id], [product_name], [category_id], [description], [price], [stock], [ImageURL], [status]) VALUES (6, N'飯店浴袍', 2, N'柔軟舒適飯店浴袍', 899, 15, N'https://www.ikea.com.tw/dairyfarm/tw/images/355/1135518_PE879084_S4.jpg', N'ACTIVE')
GO
INSERT [dbo].[product] ([product_id], [product_name], [category_id], [description], [price], [stock], [ImageURL], [status]) VALUES (7, N'牙刷組', 2, N'客房盥洗牙刷組', 50, 100, N'https://cdn-general.cybassets.com/media/W1siZiIsIjEzODk2L3Byb2R1Y3RzLzUxODQxNjc4LzE3MzMzNjg5NzlfOGM3NjMyZDFjMTljNTA4ODNiMmQuanBlZyJdLFsicCIsInRodW1iIiwiNjAweDYwMCJdXQ.jpeg?sha=653de3478f6602ad', N'ACTIVE')
GO
INSERT [dbo].[product] ([product_id], [product_name], [category_id], [description], [price], [stock], [ImageURL], [status]) VALUES (8, N'刮鬍刀', 2, N'一次性刮鬍刀', 80, 80, N'https://down-tw.img.susercontent.com/file/dec7c4ec0fe46e252f62ed9c8978a85b', N'ACTIVE')
GO
SET IDENTITY_INSERT [dbo].[product] OFF
GO
SET IDENTITY_INSERT [dbo].[order_item] ON 
GO
INSERT [dbo].[order_item] ([order_id], [product_id], [quantity]) VALUES (1, 1, 4)
GO
INSERT [dbo].[order_item] ([order_id], [product_id], [quantity]) VALUES (1, 2, 1)
GO
INSERT [dbo].[order_item] ([order_id], [product_id], [quantity]) VALUES (2, 1, 2)
GO
INSERT [dbo].[order_item] ([order_id], [product_id], [quantity]) VALUES (3, 1, 1)
GO
SET IDENTITY_INSERT [dbo].[order_item] OFF
GO
SET IDENTITY_INSERT [dbo].[room_image] ON 
GO
INSERT [dbo].[room_image] ([image_id], [path], [image_description], [room_type_id]) VALUES (2, N'/uploads/images/room/roomtype.1.jpg', N'海景標準雙人房', 1)
GO
INSERT [dbo].[room_image] ([image_id], [path], [image_description], [room_type_id]) VALUES (3, N'/uploads/images/room/roomtype.2.jpg', N'山景標準雙人房', 2)
GO
INSERT [dbo].[room_image] ([image_id], [path], [image_description], [room_type_id]) VALUES (4, N'/uploads/images/room/roomtype.3.jpg', N'海景雅緻雙床房', 3)
GO
INSERT [dbo].[room_image] ([image_id], [path], [image_description], [room_type_id]) VALUES (5, N'/uploads/images/room/roomtype.4.jpg', N'山景雅緻雙床房', 4)
GO
INSERT [dbo].[room_image] ([image_id], [path], [image_description], [room_type_id]) VALUES (6, N'/uploads/images/room/roomtype.5.jpg', N'海景溫馨家庭房', 5)
GO
INSERT [dbo].[room_image] ([image_id], [path], [image_description], [room_type_id]) VALUES (7, N'/uploads/images/room/roomtype.6.jpg', N'山景溫馨家庭房', 6)
GO
INSERT [dbo].[room_image] ([image_id], [path], [image_description], [room_type_id]) VALUES (8, N'/uploads/images/room/roomtype.7.jpg', N'海景行政尊榮套房', 7)
GO
INSERT [dbo].[room_image] ([image_id], [path], [image_description], [room_type_id]) VALUES (9, N'/uploads/images/room/roomtype.8.jpg', N'山景行政尊榮套房', 8)
GO
INSERT [dbo].[room_image] ([image_id], [path], [image_description], [room_type_id]) VALUES (10, N'/uploads/images/room/roomtype.9.jpg', N'海景豪華全景四人套房', 9)
GO
INSERT [dbo].[room_image] ([image_id], [path], [image_description], [room_type_id]) VALUES (11, N'/uploads/images/room/roomtype.10.jpg', N'海景頂級皇家總統套房', 10)
GO
SET IDENTITY_INSERT [dbo].[room_image] OFF
GO
SET IDENTITY_INSERT [dbo].[category] ON 
GO
INSERT [dbo].[category] ([category_id], [category_name]) VALUES (1, N'客房備品')
GO
INSERT [dbo].[category] ([category_id], [category_name]) VALUES (2, N'紀念商品')
GO
INSERT [dbo].[category] ([category_id], [category_name]) VALUES (3, N'餐飲商品')
GO
SET IDENTITY_INSERT [dbo].[category] OFF
GO
