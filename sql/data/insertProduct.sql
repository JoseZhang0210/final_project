USE [finalproject]
GO
SET IDENTITY_INSERT [dbo].[Product] ON 
GO
INSERT [dbo].[Product] ([Product_ID], [Product_Name], [Category_ID], [Description], [Price], [Stock], [ImageURL], [Status]) VALUES (1, N'飯店馬克杯', 1, N'飯店限定陶瓷馬克杯', CAST(350.00 AS Decimal(10, 2)), 60, N'', N'ACTIVE')
GO
INSERT [dbo].[Product] ([Product_ID], [Product_Name], [Category_ID], [Description], [Price], [Stock], [ImageURL], [Status]) VALUES (2, N'飯店保溫瓶', 1, N'不鏽鋼保溫瓶', CAST(599.00 AS Decimal(10, 2)), 20, NULL, N'ACTIVE')
GO
INSERT [dbo].[Product] ([Product_ID], [Product_Name], [Category_ID], [Description], [Price], [Stock], [ImageURL], [Status]) VALUES (3, N'飯店帆布袋', 1, N'飯店紀念帆布袋', CAST(299.00 AS Decimal(10, 2)), 25, NULL, N'ACTIVE')
GO
INSERT [dbo].[Product] ([Product_ID], [Product_Name], [Category_ID], [Description], [Price], [Stock], [ImageURL], [Status]) VALUES (4, N'飯店鑰匙圈', 1, N'飯店造型紀念鑰匙圈', CAST(150.00 AS Decimal(10, 2)), 50, NULL, N'ACTIVE')
GO
INSERT [dbo].[Product] ([Product_ID], [Product_Name], [Category_ID], [Description], [Price], [Stock], [ImageURL], [Status]) VALUES (5, N'飯店明信片', 1, N'飯店風景紀念明信片', CAST(80.00 AS Decimal(10, 2)), 100, NULL, N'ACTIVE')
GO
INSERT [dbo].[Product] ([Product_ID], [Product_Name], [Category_ID], [Description], [Price], [Stock], [ImageURL], [Status]) VALUES (6, N'飯店浴袍', 2, N'柔軟舒適飯店浴袍', CAST(899.00 AS Decimal(10, 2)), 15, NULL, N'ACTIVE')
GO
INSERT [dbo].[Product] ([Product_ID], [Product_Name], [Category_ID], [Description], [Price], [Stock], [ImageURL], [Status]) VALUES (7, N'牙刷組', 2, N'客房盥洗牙刷組', CAST(50.00 AS Decimal(10, 2)), 100, NULL, N'ACTIVE')
GO
INSERT [dbo].[Product] ([Product_ID], [Product_Name], [Category_ID], [Description], [Price], [Stock], [ImageURL], [Status]) VALUES (8, N'刮鬍刀', 2, N'一次性刮鬍刀', CAST(80.00 AS Decimal(10, 2)), 80, NULL, N'ACTIVE')
GO
SET IDENTITY_INSERT [dbo].[Product] OFF
GO