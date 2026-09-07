USE [finalproject]
GO

/****** 物件:  Table [dbo].[account] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[account](
	[account_id] [int] IDENTITY(1,1) NOT NULL,
	[username] [varchar](50) NOT NULL,
	[password] [varchar](255) NOT NULL,
	[status] [varchar](20) NOT NULL,
 CONSTRAINT [PK__account__46A222CD1984288D] PRIMARY KEY CLUSTERED 
(
	[account_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ__account__F3DBC572356CBDC7] UNIQUE NONCLUSTERED 
(
	[username] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[booking] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[booking](
	[booking_id] [int] IDENTITY(1,1) NOT NULL,
	[booking_price] [int] NOT NULL,
	[check_in_date] [datetime] NOT NULL,
	[check_out_date] [datetime] NOT NULL,
	[guest_num] [int] NOT NULL,
	[booking_status] [nvarchar](20) NOT NULL,
	[room_id] [int] NULL,
	[room_type_id] [int] NOT NULL,
	[member_id] [int] NOT NULL,
	[created_at] [datetime] NOT NULL,
 CONSTRAINT [PK__booking__5DE3A5B16E4B0B5A] PRIMARY KEY CLUSTERED 
(
	[booking_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[booking_payment] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[booking_payment](
	[payment_id] [int] IDENTITY(1,1) NOT NULL,
	[booking_id] [int] NOT NULL,
	[amount] [int] NOT NULL,
	[payment_method] [nvarchar](50) NULL,
	[payment_status] [nvarchar](20) NOT NULL,
	[transaction_id] [nvarchar](50) NULL,
	[created_at] [datetime2] NOT NULL,
	[paid_at] [datetime2] NULL,
 CONSTRAINT [PK_booking_payment] PRIMARY KEY CLUSTERED 
(
	[payment_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[category] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[category](
	[category_id] INT IDENTITY(1,1) NOT NULL,
	[category_name] [nvarchar](50) NULL,
 CONSTRAINT [PK_category] PRIMARY KEY CLUSTERED 
(
	[category_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[department] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[department](
	[department_id] [int] IDENTITY(1,1) NOT NULL,
	[department_name] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK__departme__C2232422F090623A] PRIMARY KEY CLUSTERED 
(
	[department_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[employee] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[employee](
	[employee_id] [int] IDENTITY(1,1) NOT NULL,
	[department_id] [int] NOT NULL,
	[account_id] [int] NOT NULL,
	[position] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK__employee__C52E0BA88F0B8D0D] PRIMARY KEY CLUSTERED 
(
	[employee_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ__employee__F3DBC57221DFB4A0] UNIQUE NONCLUSTERED 
(
	[account_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[employee_permission] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[employee_permission](
	[employee_id] [int] NOT NULL,
	[permission_id] [int] NOT NULL,
 CONSTRAINT [PK_employee_permission] PRIMARY KEY CLUSTERED 
(
	[employee_id] ASC,
	[permission_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[room_image] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[room_image](
	[image_id] [int] IDENTITY(1,1) NOT NULL,
	[path] [varchar](255) NOT NULL,
	[image_description] [nvarchar](255) NULL,
	[room_type_id] [int] NOT NULL,
 CONSTRAINT [PK_room_image] PRIMARY KEY CLUSTERED 
(
	[image_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[member] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[member](
	[member_id] [int] IDENTITY(1,1) NOT NULL,
	[account_id] [int] NOT NULL,
 CONSTRAINT [PK__member__B29B8534C15A42D7] PRIMARY KEY CLUSTERED 
(
	[member_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ__member__F3DBC57242E6179B] UNIQUE NONCLUSTERED 
(
	[account_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[payment] (商城購物付款紀錄) ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[payment](
	[payment_id] [int] IDENTITY(1, 1) NOT NULL,
	[member_id] [int] NULL,
	[payment_method] [nvarchar](50) NULL,
	[transaction_id] [nvarchar](100) NULL,
	[total_price] [int] NOT NULL,
	[payment_status] [nvarchar](20) NOT NULL CONSTRAINT [DF_payment_status] DEFAULT (N'PENDING'),
	[payment_time] [datetime] NULL,
	[created_at] [datetime] NOT NULL CONSTRAINT [DF_payment_created_at] DEFAULT (GETDATE()),
 CONSTRAINT [PK_payment] PRIMARY KEY CLUSTERED ([payment_id] ASC),
 CONSTRAINT [CK_payment_status] CHECK (
	[payment_status] IN (N'PENDING', N'PAID', N'FAILED', N'REFUNDED')
 ),
 CONSTRAINT [CK_payment_total_price] CHECK ([total_price] >= 0)
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[coupon] (商城優惠券) ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[coupon](
	[coupon_id] [int] IDENTITY(1, 1) NOT NULL,
	[coupon_code] [nvarchar](50) NOT NULL,
	[coupon_name] [nvarchar](100) NOT NULL,
	[discount_type] [nvarchar](20) NOT NULL,
	[discount_value] [int] NOT NULL,
	[minimum_amount] [int] NOT NULL CONSTRAINT [DF_coupon_minimum_amount] DEFAULT ((0)),
	[start_date] [datetime] NOT NULL,
	[end_date] [datetime] NOT NULL,
	[status] [nvarchar](20) NOT NULL CONSTRAINT [DF_coupon_status] DEFAULT (N'ACTIVE'),
 CONSTRAINT [PK_coupon] PRIMARY KEY CLUSTERED ([coupon_id] ASC),
 CONSTRAINT [UQ_coupon_code] UNIQUE ([coupon_code]),
 CONSTRAINT [CK_coupon_discount_type] CHECK ([discount_type] IN (N'PERCENT', N'FIXED')),
 CONSTRAINT [CK_coupon_status] CHECK ([status] IN (N'ACTIVE', N'INACTIVE')),
 CONSTRAINT [CK_coupon_value] CHECK ([discount_value] > 0 AND [minimum_amount] >= 0),
 CONSTRAINT [CK_coupon_date] CHECK ([end_date] > [start_date])
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[order] (商城訂單) ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[order](
	[order_id] [int] IDENTITY(1, 1) NOT NULL,
	[member_id] [int] NOT NULL,
	[order_date] [datetime] NOT NULL CONSTRAINT [DF_order_order_date] DEFAULT (GETDATE()),
	[original_amount] [int] NOT NULL CONSTRAINT [DF_order_original_amount] DEFAULT ((0)),
	[discount_amount] [int] NOT NULL CONSTRAINT [DF_order_discount_amount] DEFAULT ((0)),
	[final_amount] [int] NOT NULL CONSTRAINT [DF_order_final_amount] DEFAULT ((0)),
	[coupon_id] [int] NULL,
	[payment_id] [int] NULL,
	[order_status] [nvarchar](20) NOT NULL CONSTRAINT [DF_order_status] DEFAULT (N'PENDING'),
 CONSTRAINT [PK_order] PRIMARY KEY CLUSTERED ([order_id] ASC),
 CONSTRAINT [CK_order_status] CHECK (
	[order_status] IN (N'PENDING', N'COMPLETED', N'CANCELLED')
 ),
 CONSTRAINT [CK_order_amount] CHECK (
	[original_amount] >= 0
	AND [discount_amount] >= 0
	AND [final_amount] >= 0
	AND [discount_amount] <= [original_amount]
 )
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[order_item] (商城訂單明細) ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[order_item](
	[order_id] [int] NOT NULL,
	[product_id] [int] NOT NULL,
	[quantity] [int] NOT NULL,
	[unit_price] [int] NOT NULL,
	[subtotal] [int] NOT NULL,
 CONSTRAINT [PK_order_item] PRIMARY KEY CLUSTERED ([order_id] ASC, [product_id] ASC),
 CONSTRAINT [CK_order_item_quantity] CHECK ([quantity] > 0),
 CONSTRAINT [CK_order_item_price] CHECK ([unit_price] >= 0 AND [subtotal] >= 0)
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[permission] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[permission](
	[permission_id] [int] IDENTITY(1,1) NOT NULL,
	[permission_code] [nvarchar](50) NOT NULL,
	[permission_name] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_permission] PRIMARY KEY CLUSTERED 
(
	[permission_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[product] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[product](
    [product_id] INT IDENTITY(1,1) NOT NULL,
    [product_name] [nvarchar](50) NOT NULL,
    [category_id] [int] NOT NULL,
    [description] NVARCHAR(255) NULL,
    [price] [int] NOT NULL,
    [stock] [int] NOT NULL,
    [ImageURL] NVARCHAR(255),
    [status] [nvarchar](50) NULL,
CONSTRAINT [PK_product] PRIMARY KEY CLUSTERED 
(
    [product_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[profile] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[profile](
	[profile_id] [int] IDENTITY(1,1) NOT NULL,
	[account_id] [int] NOT NULL,
	[name] [varchar](50) NOT NULL,
	[email] [varchar](100) NULL,
	[phone] [varchar](20) NULL,
	[zipcode] [varchar](10) NULL,
	[city] [varchar](50) NULL,
	[district] [varchar](50) NULL,
	[address] [varchar](200) NULL,
	[created_at] [datetime] NOT NULL,
	[birthday] [date] NULL,
	[gender] [varchar](10) NULL,
	[updated_at] [datetime] NOT NULL,
 CONSTRAINT [PK__user_pro__AEBB701F9EBA1518] PRIMARY KEY CLUSTERED 
(
	[profile_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ__user_pro__F3DBC572AA1DDAA5] UNIQUE NONCLUSTERED 
(
	[account_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[rental] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[rental](
	[rental_id] [int] IDENTITY(1,1) NOT NULL,
	[venue_id] [int] NOT NULL,
	[member_id] [int] NOT NULL,
	[event_name] [nvarchar](50) NOT NULL,
	[rental_date] [datetime] NOT NULL,
	[guest_count] [int] NOT NULL,
	[payment_id] [int] NOT NULL,
	[rental_status] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_rental] PRIMARY KEY CLUSTERED 
(
	[rental_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[restaurant] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[restaurant] (
    [restaurant_id] INT IDENTITY(1,1) NOT NULL,
    [restaurant_name] NVARCHAR(50) NOT NULL,
    [address] NVARCHAR(100) NOT NULL,
    [phone] VARCHAR(20) NOT NULL,
    [capacity] INT NOT NULL,
    [description] NVARCHAR(255) NULL,
    CONSTRAINT [PK_restaurant] PRIMARY KEY CLUSTERED ([restaurant_id] ASC)
);
GO

/****** 物件:  Table [dbo].[restaurant_time] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[restaurant_time] (
    [time_id] INT IDENTITY(1,1) NOT NULL,
    [restaurant_id] INT NOT NULL,
    [meal_type] NVARCHAR(20) NOT NULL,
    [open_time] TIME(0) NOT NULL,
    [close_time] TIME(0) NOT NULL,
    CONSTRAINT [PK_restaurant_time] PRIMARY KEY CLUSTERED ([time_id] ASC)
);
GO

/****** 物件:  Table [dbo].[reservation] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[reservation] (
    [reservation_id]   INT IDENTITY(1,1) NOT NULL,
    [member_id]        INT NULL,
    [contact_name]     NVARCHAR(50) NULL,
    [contact_phone]    VARCHAR(20) NULL,
    [restaurant_id]    INT NOT NULL,
    [reservation_date] DATE NOT NULL,
    [time_id]          INT NOT NULL,
    [people_count]     INT NOT NULL,
    [status]           NVARCHAR(20) NOT NULL
        CONSTRAINT [DF_reservation_status] DEFAULT (N'已訂位'),
    [create_time]      DATETIME NOT NULL
        CONSTRAINT [DF_reservation_create_time] DEFAULT (GETDATE()),

    CONSTRAINT [PK_reservation]
        PRIMARY KEY CLUSTERED ([reservation_id] ASC),

    CONSTRAINT [FK_reservation_member]
        FOREIGN KEY ([member_id])
        REFERENCES [dbo].[member]([member_id]),

    CONSTRAINT [FK_reservation_restaurant]
        FOREIGN KEY ([restaurant_id])
        REFERENCES [dbo].[restaurant]([restaurant_id]),

    CONSTRAINT [FK_reservation_restaurant_time]
        FOREIGN KEY ([time_id])
        REFERENCES [dbo].[restaurant_time]([time_id]),

    CONSTRAINT [CK_reservation_member_or_contact]
        CHECK (
            [member_id] IS NOT NULL
            OR (
                [contact_name] IS NOT NULL
                AND [contact_phone] IS NOT NULL
            )
        )
);
GO

/****** 物件:  Table [dbo].[room] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[room](
	[room_id] [int] IDENTITY(1,1) NOT NULL,
	[room_number] [NVARCHAR](20) NOT NULL,
	[room_type_id] [int] NOT NULL,
	[floor] [int] NOT NULL,
	[room_status] [nvarchar](20) NOT NULL,
 CONSTRAINT [PK__room__19675A8A9D13DCA5] PRIMARY KEY CLUSTERED 
(
	[room_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ__room__FE22F61BD1367F20] UNIQUE NONCLUSTERED 
(
	[room_number] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[room_task] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[room_task](
	[task_id] [int] IDENTITY(1,1) NOT NULL,
	[room_id] [int] NOT NULL,
	[employee_id] [int] NOT NULL,
	[priority] [nvarchar](20) NOT NULL,
	[task_type] [nvarchar](20) NOT NULL,
	[task_status] [nvarchar](20) NOT NULL,
	[remark] [nvarchar](100) NULL,
	[created_at] [datetime] NOT NULL,
	[completed_at] [datetime] NULL,
 CONSTRAINT [PK__room_tas__0492148D9F40B673] PRIMARY KEY CLUSTERED 
(
	[task_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[room_type] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[room_type](
	[room_type_id] [int] IDENTITY(1,1) NOT NULL,
	[type_name] [nvarchar](20) NOT NULL,
	[bed_type] [nvarchar](20) NOT NULL,
	[capacity] [int] NOT NULL,
	[room_description] [nvarchar](100) NULL,
	[price_per_night] [int] NOT NULL,
	[available_rooms] [int] NOT NULL,
 CONSTRAINT [PK_room_type] PRIMARY KEY CLUSTERED 
(
	[room_type_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[venue] ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[venue](
	[venue_id] [int] NOT NULL,
	[venue_name] [nvarchar](50) NOT NULL,
	[capacity] [int] NOT NULL,
	[price_per_day] [int] NOT NULL,
	[venue_status] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_venue] PRIMARY KEY CLUSTERED 
(
	[venue_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[rental_payment] (場地租借付款紀錄) ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[rental_payment](
    [payment_id] [int] IDENTITY(1,1) NOT NULL,
    [payment_method] [nvarchar](50) NULL,
    [payment_time] [datetime2] NULL,
    [total_price] [int] NOT NULL,
    [payment_status] [nvarchar](20) NOT NULL,
    [member_id] [int] NULL,
 CONSTRAINT [PK_rental_payment] PRIMARY KEY CLUSTERED ([payment_id] ASC)
) ON [PRIMARY]
GO

-- 預設約束 (Default Constraints)
ALTER TABLE [dbo].[account] ADD CONSTRAINT [DF__account__status__4F7CD00D] DEFAULT ('ACTIVE') FOR [status]
GO
ALTER TABLE [dbo].[profile] ADD CONSTRAINT [DF__user_prof__creat__5FB337D6] DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[profile] ADD CONSTRAINT [DF__user_prof__updat__60A75C0F] DEFAULT (getdate()) FOR [updated_at]
GO

-- 外鍵約束 (Foreign Key Constraints)
ALTER TABLE [dbo].[booking] WITH CHECK ADD CONSTRAINT [FK_booking_member] FOREIGN KEY([member_id])
REFERENCES [dbo].[member] ([member_id])
GO
ALTER TABLE [dbo].[booking] CHECK CONSTRAINT [FK_booking_member]
GO

ALTER TABLE [dbo].[booking_payment] WITH CHECK ADD CONSTRAINT [FK_booking_payment_booking] FOREIGN KEY([booking_id])
REFERENCES [dbo].[booking] ([booking_id])
GO
ALTER TABLE [dbo].[booking_payment] CHECK CONSTRAINT [FK_booking_payment_booking]
GO

ALTER TABLE [dbo].[booking] WITH CHECK ADD CONSTRAINT [FK_booking_room] FOREIGN KEY([room_id])
REFERENCES [dbo].[room] ([room_id])
GO
ALTER TABLE [dbo].[booking] CHECK CONSTRAINT [FK_booking_room]
GO

ALTER TABLE [dbo].[booking] WITH CHECK ADD CONSTRAINT [FK_booking_room_type] FOREIGN KEY([room_type_id])
REFERENCES [dbo].[room_type] ([room_type_id])
GO
ALTER TABLE [dbo].[booking] CHECK CONSTRAINT [FK_booking_room_type]
GO

ALTER TABLE [dbo].[employee] WITH CHECK ADD CONSTRAINT [FK_employee_account] FOREIGN KEY([account_id])
REFERENCES [dbo].[account] ([account_id])
GO
ALTER TABLE [dbo].[employee] CHECK CONSTRAINT [FK_employee_account]
GO

ALTER TABLE [dbo].[employee] WITH CHECK ADD CONSTRAINT [FK_employee_department] FOREIGN KEY([department_id])
REFERENCES [dbo].[department] ([department_id])
GO
ALTER TABLE [dbo].[employee] CHECK CONSTRAINT [FK_employee_department]
GO

ALTER TABLE [dbo].[employee_permission] WITH CHECK ADD CONSTRAINT [FK_employee_permission_employee] FOREIGN KEY([employee_id])
REFERENCES [dbo].[employee] ([employee_id])
GO
ALTER TABLE [dbo].[employee_permission] CHECK CONSTRAINT [FK_employee_permission_employee]
GO

ALTER TABLE [dbo].[employee_permission] WITH CHECK ADD CONSTRAINT [FK_employee_permission_permission] FOREIGN KEY([permission_id])
REFERENCES [dbo].[permission] ([permission_id])
GO
ALTER TABLE [dbo].[employee_permission] CHECK CONSTRAINT [FK_employee_permission_permission]
GO

ALTER TABLE [dbo].[member] WITH CHECK ADD CONSTRAINT [FK_member_account] FOREIGN KEY([account_id])
REFERENCES [dbo].[account] ([account_id])
GO
ALTER TABLE [dbo].[member] CHECK CONSTRAINT [FK_member_account]
GO

ALTER TABLE [dbo].[payment] WITH CHECK ADD CONSTRAINT [FK_payment_member] FOREIGN KEY([member_id])
REFERENCES [dbo].[member] ([member_id])
GO
ALTER TABLE [dbo].[payment] CHECK CONSTRAINT [FK_payment_member]
GO

ALTER TABLE [dbo].[order] WITH CHECK ADD CONSTRAINT [FK_order_member] FOREIGN KEY([member_id])
REFERENCES [dbo].[member] ([member_id])
GO
ALTER TABLE [dbo].[order] CHECK CONSTRAINT [FK_order_member]
GO

ALTER TABLE [dbo].[order] WITH CHECK ADD CONSTRAINT [FK_order_payment] FOREIGN KEY([payment_id])
REFERENCES [dbo].[payment] ([payment_id])
GO
ALTER TABLE [dbo].[order] CHECK CONSTRAINT [FK_order_payment]
GO

ALTER TABLE [dbo].[order] WITH CHECK ADD CONSTRAINT [FK_order_coupon] FOREIGN KEY([coupon_id])
REFERENCES [dbo].[coupon] ([coupon_id])
GO
ALTER TABLE [dbo].[order] CHECK CONSTRAINT [FK_order_coupon]
GO

ALTER TABLE [dbo].[order_item] WITH CHECK ADD CONSTRAINT [FK_order_item_order] FOREIGN KEY([order_id])
REFERENCES [dbo].[order] ([order_id])
GO
ALTER TABLE [dbo].[order_item] CHECK CONSTRAINT [FK_order_item_order]
GO

ALTER TABLE [dbo].[order_item] WITH CHECK ADD CONSTRAINT [FK_order_item_product] FOREIGN KEY([product_id])
REFERENCES [dbo].[product] ([product_id])
GO
ALTER TABLE [dbo].[order_item] CHECK CONSTRAINT [FK_order_item_product]
GO

ALTER TABLE [dbo].[profile] WITH CHECK ADD CONSTRAINT [FK_user_profile_account] FOREIGN KEY([account_id])
REFERENCES [dbo].[account] ([account_id])
GO
ALTER TABLE [dbo].[profile] CHECK CONSTRAINT [FK_user_profile_account]
GO

ALTER TABLE [dbo].[rental_payment] WITH CHECK ADD CONSTRAINT [FK_rental_payment_member] FOREIGN KEY([member_id])
REFERENCES [dbo].[member] ([member_id])
GO
ALTER TABLE [dbo].[rental_payment] CHECK CONSTRAINT [FK_rental_payment_member]
GO

ALTER TABLE [dbo].[rental] WITH CHECK ADD CONSTRAINT [FK_rental_member] FOREIGN KEY([member_id])
REFERENCES [dbo].[member] ([member_id])
GO
ALTER TABLE [dbo].[rental] CHECK CONSTRAINT [FK_rental_member]
GO

ALTER TABLE [dbo].[rental] WITH CHECK ADD CONSTRAINT [FK_rental_rental_payment] FOREIGN KEY([payment_id])
REFERENCES [dbo].[rental_payment] ([payment_id])
GO
ALTER TABLE [dbo].[rental] CHECK CONSTRAINT [FK_rental_rental_payment]
GO

ALTER TABLE [dbo].[rental] WITH CHECK ADD CONSTRAINT [FK_rental_venue] FOREIGN KEY([venue_id])
REFERENCES [dbo].[venue] ([venue_id])
GO
ALTER TABLE [dbo].[rental] CHECK CONSTRAINT [FK_rental_venue]
GO

ALTER TABLE [dbo].[room] WITH CHECK ADD CONSTRAINT [FK_room_room_type] FOREIGN KEY([room_type_id])
REFERENCES [dbo].[room_type] ([room_type_id])
GO
ALTER TABLE [dbo].[room] CHECK CONSTRAINT [FK_room_room_type]
GO

ALTER TABLE [dbo].[room_task] WITH CHECK ADD CONSTRAINT [FK_room_task_employee] FOREIGN KEY([employee_id])
REFERENCES [dbo].[employee] ([employee_id])
GO
ALTER TABLE [dbo].[room_task] CHECK CONSTRAINT [FK_room_task_employee]
GO

ALTER TABLE [dbo].[room_task] WITH CHECK ADD CONSTRAINT [FK_room_task_room] FOREIGN KEY([room_id])
REFERENCES [dbo].[room] ([room_id])
GO
ALTER TABLE [dbo].[room_task] CHECK CONSTRAINT [FK_room_task_room]
GO

ALTER TABLE [dbo].[room_image] WITH CHECK ADD CONSTRAINT [FK_room_image_room_type] FOREIGN KEY([room_type_id])
REFERENCES [dbo].[room_type] ([room_type_id])
GO
ALTER TABLE [dbo].[room_image] CHECK CONSTRAINT [FK_room_image_room_type]
GO