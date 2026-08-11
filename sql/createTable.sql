USE [finalproject]
GO
/****** 物件:  Table [dbo].[account]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
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
/****** 物件:  Table [dbo].[booking]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[booking](
	[booking_id] [int] IDENTITY(1,1) NOT NULL,
	[booking_order_id] [int] NOT NULL,
	[check_in_date] [datetime] NOT NULL,
	[check_out_date] [datetime] NOT NULL,
	[guest_num] [int] NOT NULL,
	[booking_status] [nvarchar](50) NOT NULL,
	[room_id] [int] NULL,
	[room_type_id] [int] NOT NULL,
 CONSTRAINT [PK__booking__5DE3A5B16E4B0B5A] PRIMARY KEY CLUSTERED 
(
	[booking_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** 物件:  Table [dbo].[booking_order]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[booking_order](
	[booking_order_id] [int] NOT NULL,
	[member_id] [int] NOT NULL,
	[total_price] [int] NOT NULL,
	[order_status] [nvarchar](50) NOT NULL,
	[create_at] [datetime] NOT NULL,
	[payment_id] [int] NOT NULL,
 CONSTRAINT [PK_booking_order] PRIMARY KEY CLUSTERED 
(
	[booking_order_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** 物件:  Table [dbo].[category]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[category](
	[category_id] [int] NOT NULL,
	[category_name] [nvarchar](50) NULL,
	[parent_category] [int] NULL,
 CONSTRAINT [PK_category] PRIMARY KEY CLUSTERED 
(
	[category_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** 物件:  Table [dbo].[department]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[department](
	[department_id] [int] IDENTITY(1,1) NOT NULL,
	[department_name] [varchar](50) NOT NULL,
 CONSTRAINT [PK__departme__C2232422D5FF8FCD] PRIMARY KEY CLUSTERED 
(
	[department_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ__departme__226ED157968C41E7] UNIQUE NONCLUSTERED 
(
	[department_name] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** 物件:  Table [dbo].[employee]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[employee](
	[employee_id] [int] IDENTITY(1,1) NOT NULL,
	[department_id] [int] NOT NULL,
	[account_id] [int] NOT NULL,
	[position] [varchar](50) NOT NULL,
	[is_admin] [bit] NOT NULL,
 CONSTRAINT [PK__employee__C52E0BA8B3F6391D] PRIMARY KEY CLUSTERED 
(
	[employee_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ__employee__46A222CC22732DA3] UNIQUE NONCLUSTERED 
(
	[account_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** 物件:  Table [dbo].[employee_permission]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[employee_permission](
	[permission_id] [int] NOT NULL,
	[employee_id] [int] NOT NULL,
 CONSTRAINT [PK_employee_permission] PRIMARY KEY CLUSTERED 
(
	[permission_id] ASC,
	[employee_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** 物件:  Table [dbo].[image]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[image](
	[image_id] [int] NOT NULL,
	[path] [nvarchar](50) NOT NULL,
	[image_desc] [nchar](10) NULL,
 CONSTRAINT [PK_image] PRIMARY KEY CLUSTERED 
(
	[image_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** 物件:  Table [dbo].[member]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[member](
	[member_id] [int] IDENTITY(1,1) NOT NULL,
	[account_id] [int] NOT NULL,
 CONSTRAINT [PK__member__B29B85343DF336B3] PRIMARY KEY CLUSTERED 
(
	[member_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ__member__46A222CC89E0140C] UNIQUE NONCLUSTERED 
(
	[account_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** 物件:  Table [dbo].[order]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[order](
	[order_id] [int] NOT NULL,
	[member_id] [int] NOT NULL,
	[order_date] [datetime] NOT NULL,
	[is_ordered] [bit] NOT NULL,
	[payment_id] [int] NOT NULL,
 CONSTRAINT [PK_order] PRIMARY KEY CLUSTERED 
(
	[order_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** 物件:  Table [dbo].[order_item]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[order_item](
	[order_id] [int] NOT NULL,
	[product_id] [int] NOT NULL,
	[quantity] [int] NOT NULL,
 CONSTRAINT [PK_order_item] PRIMARY KEY CLUSTERED 
(
	[order_id] ASC,
	[product_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** 物件:  Table [dbo].[payment]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[payment](
	[payment_id] [int] NOT NULL,
	[payment_method] [nvarchar](50) NULL,
	[payment_time] [datetime] NULL,
	[total_price] [int] NOT NULL,
	[payment_status] [nvarchar](50) NOT NULL,
	[member_id] [int] NULL,
 CONSTRAINT [PK_payment] PRIMARY KEY CLUSTERED 
(
	[payment_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** 物件:  Table [dbo].[permission]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[permission](
	[permission_id] [int] NOT NULL,
	[permission_code] [nvarchar](50) NOT NULL,
	[permission_name] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK_permission] PRIMARY KEY CLUSTERED 
(
	[permission_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** 物件:  Table [dbo].[product]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[product](
	[product_id] [int] NOT NULL,
	[product_name] [nvarchar](50) NOT NULL,
	[category_id] [int] NOT NULL,
	[description] [nvarchar](50) NOT NULL,
	[price] [int] NOT NULL,
	[stock] [int] NOT NULL,
	[image_id] [int] NULL,
	[status] [nvarchar](50) NULL,
 CONSTRAINT [PK_product] PRIMARY KEY CLUSTERED 
(
	[product_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** 物件:  Table [dbo].[profile]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
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
	[updated_at] [datetime] NULL,
 CONSTRAINT [PK__user_pro__AEBB701FB4CA65F5] PRIMARY KEY CLUSTERED 
(
	[profile_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY],
 CONSTRAINT [UQ__user_pro__46A222CC77765872] UNIQUE NONCLUSTERED 
(
	[account_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** 物件:  Table [dbo].[rental]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[rental](
	[rental_id] [int] NOT NULL,
	[venue_id] [int] NOT NULL,
	[member_id] [int] NOT NULL,
	[event_name] [nvarchar](50) NOT NULL,
	[rental_date] [datetime] NOT NULL,
	[guest_count] [int] NOT NULL,
	[payment_id] [int] NOT NULL,
	[rental_status] [nvarchar](50) NULL,
 CONSTRAINT [PK_rental] PRIMARY KEY CLUSTERED 
(
	[rental_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO

/****** 物件:  Table [dbo].[restaurant]    指令碼日期: 2026/8/11 下午 10:10:11 ******/
CREATE TABLE [dbo].[restaurant] (
    [restaurant_id]   INT IDENTITY(1,1) NOT NULL,
    [restaurant_name] NVARCHAR(100) NOT NULL,
    [address]         NVARCHAR(200) NULL,
    [phone]           VARCHAR(20) NULL,
    [capacity]        INT NULL,
    [description]     NVARCHAR(MAX) NULL,

    CONSTRAINT [PK_restaurant]
        PRIMARY KEY CLUSTERED ([restaurant_id] ASC)
);
GO
	
/****** 物件:  Table [dbo].[restaurant_time]    指令碼日期: 2026/8/11 下午 10:09:11 ******/
CREATE TABLE [dbo].[restaurant_time] (
    [time_id]       INT IDENTITY(1,1) NOT NULL,
    [restaurant_id] INT NOT NULL,
    [meal_type]     NVARCHAR(20) NOT NULL,
    [open_time]     TIME(7) NOT NULL,
    [close_time]    TIME(7) NOT NULL,

    CONSTRAINT [PK_restaurant_time]
        PRIMARY KEY CLUSTERED ([time_id] ASC),

    CONSTRAINT [FK_restaurant_time_restaurant]
        FOREIGN KEY ([restaurant_id])
        REFERENCES [dbo].[restaurant]([restaurant_id])
);
GO

/****** 物件:  Table [dbo].[reservation]    指令碼日期: 2026/8/11 下午 10:09:11 ******/
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

/*
  會員模組整合完成後，再執行下列外鍵。

  ALTER TABLE [dbo].[reservation]
  ADD CONSTRAINT [FK_reservation_member]
  FOREIGN KEY ([member_id])
  REFERENCES [dbo].[member]([member_id]);
*/
/****** 物件:  Table [dbo].[room]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[room](
	[room_id] [int] IDENTITY(1,1) NOT NULL,
	[room_number] [int] NOT NULL,
	[room_type_id] [int] NOT NULL,
	[floor] [int] NOT NULL,
	[status] [nvarchar](50) NOT NULL,
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
/****** 物件:  Table [dbo].[room_task]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[room_task](
	[task_id] [int] IDENTITY(1,1) NOT NULL,
	[room_id] [int] NOT NULL,
	[employee_id] [int] NOT NULL,
	[remark] [nvarchar](50) NULL,
	[priority] [nvarchar](50) NOT NULL,
	[create_at] [datetime] NOT NULL,
	[completed_at] [datetime] NULL,
	[task_type] [nvarchar](50) NOT NULL,
	[task_status] [nvarchar](50) NOT NULL,
 CONSTRAINT [PK__room_tas__0492148D9F40B673] PRIMARY KEY CLUSTERED 
(
	[task_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** 物件:  Table [dbo].[room_type]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[room_type](
	[room_type_id] [int] IDENTITY(1,1) NOT NULL,
	[type_name] [nvarchar](50) NOT NULL,
	[bed_type] [nvarchar](50) NOT NULL,
	[description] [nvarchar](50) NULL,
	[price_per_night] [int] NOT NULL,
	[capacity] [int] NOT NULL,
	[image_id] [int] NULL,
 CONSTRAINT [PK_room_type] PRIMARY KEY CLUSTERED 
(
	[room_type_id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON, OPTIMIZE_FOR_SEQUENTIAL_KEY = OFF) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** 物件:  Table [dbo].[venue]    指令碼日期: 2026/7/29 下午 04:42:11 ******/
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
ALTER TABLE [dbo].[account] ADD  CONSTRAINT [DF__account__status__4F7CD00D]  DEFAULT ('ACTIVE') FOR [status]
GO
ALTER TABLE [dbo].[employee] ADD  CONSTRAINT [DF__employee__is_adm__5629CD9C]  DEFAULT ((0)) FOR [is_admin]
GO
ALTER TABLE [dbo].[profile] ADD  CONSTRAINT [DF__user_prof__creat__5FB337D6]  DEFAULT (getdate()) FOR [created_at]
GO
ALTER TABLE [dbo].[profile] ADD  CONSTRAINT [DF__user_prof__updat__60A75C0F]  DEFAULT (getdate()) FOR [updated_at]
GO
ALTER TABLE [dbo].[booking]  WITH CHECK ADD  CONSTRAINT [FK_booking_booking_order] FOREIGN KEY([booking_order_id])
REFERENCES [dbo].[booking_order] ([booking_order_id])
GO
ALTER TABLE [dbo].[booking] CHECK CONSTRAINT [FK_booking_booking_order]
GO
ALTER TABLE [dbo].[booking]  WITH CHECK ADD  CONSTRAINT [FK_booking_room] FOREIGN KEY([room_id])
REFERENCES [dbo].[room] ([room_id])
GO
ALTER TABLE [dbo].[booking] CHECK CONSTRAINT [FK_booking_room]
GO
ALTER TABLE [dbo].[booking]  WITH CHECK ADD  CONSTRAINT [FK_booking_room_type1] FOREIGN KEY([room_type_id])
REFERENCES [dbo].[room_type] ([room_type_id])
GO
ALTER TABLE [dbo].[booking] CHECK CONSTRAINT [FK_booking_room_type1]
GO
ALTER TABLE [dbo].[booking_order]  WITH CHECK ADD  CONSTRAINT [FK_booking_order_member] FOREIGN KEY([member_id])
REFERENCES [dbo].[member] ([member_id])
GO
ALTER TABLE [dbo].[booking_order] CHECK CONSTRAINT [FK_booking_order_member]
GO
ALTER TABLE [dbo].[booking_order]  WITH CHECK ADD  CONSTRAINT [FK_booking_order_payment] FOREIGN KEY([payment_id])
REFERENCES [dbo].[payment] ([payment_id])
GO
ALTER TABLE [dbo].[booking_order] CHECK CONSTRAINT [FK_booking_order_payment]
GO
ALTER TABLE [dbo].[category]  WITH CHECK ADD  CONSTRAINT [FK_category_category] FOREIGN KEY([parent_category])
REFERENCES [dbo].[category] ([category_id])
GO
ALTER TABLE [dbo].[category] CHECK CONSTRAINT [FK_category_category]
GO
ALTER TABLE [dbo].[employee]  WITH CHECK ADD  CONSTRAINT [FK_employee_account] FOREIGN KEY([account_id])
REFERENCES [dbo].[account] ([account_id])
GO
ALTER TABLE [dbo].[employee] CHECK CONSTRAINT [FK_employee_account]
GO
ALTER TABLE [dbo].[employee]  WITH CHECK ADD  CONSTRAINT [FK_employee_department] FOREIGN KEY([department_id])
REFERENCES [dbo].[department] ([department_id])
GO
ALTER TABLE [dbo].[employee] CHECK CONSTRAINT [FK_employee_department]
GO
ALTER TABLE [dbo].[employee_permission]  WITH CHECK ADD  CONSTRAINT [FK_employee_permission_employee] FOREIGN KEY([employee_id])
REFERENCES [dbo].[employee] ([account_id])
GO
ALTER TABLE [dbo].[employee_permission] CHECK CONSTRAINT [FK_employee_permission_employee]
GO
ALTER TABLE [dbo].[employee_permission]  WITH CHECK ADD  CONSTRAINT [FK_employee_permission_permission] FOREIGN KEY([permission_id])
REFERENCES [dbo].[permission] ([permission_id])
GO
ALTER TABLE [dbo].[employee_permission] CHECK CONSTRAINT [FK_employee_permission_permission]
GO
ALTER TABLE [dbo].[member]  WITH CHECK ADD  CONSTRAINT [FK_member_account] FOREIGN KEY([account_id])
REFERENCES [dbo].[account] ([account_id])
GO
ALTER TABLE [dbo].[member] CHECK CONSTRAINT [FK_member_account]
GO
ALTER TABLE [dbo].[order]  WITH CHECK ADD  CONSTRAINT [FK_order_member] FOREIGN KEY([member_id])
REFERENCES [dbo].[member] ([member_id])
GO
ALTER TABLE [dbo].[order] CHECK CONSTRAINT [FK_order_member]
GO
ALTER TABLE [dbo].[order]  WITH CHECK ADD  CONSTRAINT [FK_order_payment] FOREIGN KEY([payment_id])
REFERENCES [dbo].[payment] ([payment_id])
GO
ALTER TABLE [dbo].[order] CHECK CONSTRAINT [FK_order_payment]
GO
ALTER TABLE [dbo].[order_item]  WITH CHECK ADD  CONSTRAINT [FK_order_item_order] FOREIGN KEY([order_id])
REFERENCES [dbo].[order] ([order_id])
GO
ALTER TABLE [dbo].[order_item] CHECK CONSTRAINT [FK_order_item_order]
GO
ALTER TABLE [dbo].[order_item]  WITH CHECK ADD  CONSTRAINT [FK_order_item_product] FOREIGN KEY([product_id])
REFERENCES [dbo].[product] ([product_id])
GO
ALTER TABLE [dbo].[order_item] CHECK CONSTRAINT [FK_order_item_product]
GO
ALTER TABLE [dbo].[payment]  WITH CHECK ADD  CONSTRAINT [FK_payment_member] FOREIGN KEY([member_id])
REFERENCES [dbo].[member] ([member_id])
GO
ALTER TABLE [dbo].[payment] CHECK CONSTRAINT [FK_payment_member]
GO
ALTER TABLE [dbo].[product]  WITH CHECK ADD  CONSTRAINT [FK_product_category] FOREIGN KEY([category_id])
REFERENCES [dbo].[category] ([category_id])
GO
ALTER TABLE [dbo].[product] CHECK CONSTRAINT [FK_product_category]
GO
ALTER TABLE [dbo].[product]  WITH CHECK ADD  CONSTRAINT [FK_product_image] FOREIGN KEY([image_id])
REFERENCES [dbo].[image] ([image_id])
GO
ALTER TABLE [dbo].[product] CHECK CONSTRAINT [FK_product_image]
GO
ALTER TABLE [dbo].[profile]  WITH CHECK ADD  CONSTRAINT [FK_user_profile_account] FOREIGN KEY([account_id])
REFERENCES [dbo].[account] ([account_id])
GO
ALTER TABLE [dbo].[profile] CHECK CONSTRAINT [FK_user_profile_account]
GO
ALTER TABLE [dbo].[rental]  WITH CHECK ADD  CONSTRAINT [FK_rental_member] FOREIGN KEY([member_id])
REFERENCES [dbo].[member] ([member_id])
GO
ALTER TABLE [dbo].[rental] CHECK CONSTRAINT [FK_rental_member]
GO
ALTER TABLE [dbo].[rental]  WITH CHECK ADD  CONSTRAINT [FK_rental_payment] FOREIGN KEY([payment_id])
REFERENCES [dbo].[payment] ([payment_id])
GO
ALTER TABLE [dbo].[rental] CHECK CONSTRAINT [FK_rental_payment]
GO
ALTER TABLE [dbo].[rental]  WITH CHECK ADD  CONSTRAINT [FK_rental_venue] FOREIGN KEY([venue_id])
REFERENCES [dbo].[venue] ([venue_id])
GO
ALTER TABLE [dbo].[rental] CHECK CONSTRAINT [FK_rental_venue]
GO
ALTER TABLE [dbo].[reservation]  WITH CHECK ADD  CONSTRAINT [FK_reservation_member] FOREIGN KEY([member_id])
REFERENCES [dbo].[member] ([account_id])
GO
ALTER TABLE [dbo].[reservation] CHECK CONSTRAINT [FK_reservation_member]
GO
ALTER TABLE [dbo].[reservation]  WITH CHECK ADD  CONSTRAINT [FK_reservation_restaurant] FOREIGN KEY([restaurant_id])
REFERENCES [dbo].[restaurant] ([restaurant_id])
GO
ALTER TABLE [dbo].[reservation] CHECK CONSTRAINT [FK_reservation_restaurant]
GO
ALTER TABLE [dbo].[room]  WITH CHECK ADD  CONSTRAINT [FK_room_room_type] FOREIGN KEY([room_type_id])
REFERENCES [dbo].[room_type] ([room_type_id])
GO
ALTER TABLE [dbo].[room] CHECK CONSTRAINT [FK_room_room_type]
GO
ALTER TABLE [dbo].[room_task]  WITH CHECK ADD  CONSTRAINT [FK_room_task_employee] FOREIGN KEY([employee_id])
REFERENCES [dbo].[employee] ([employee_id])
GO
ALTER TABLE [dbo].[room_task] CHECK CONSTRAINT [FK_room_task_employee]
GO
ALTER TABLE [dbo].[room_task]  WITH CHECK ADD  CONSTRAINT [FK_room_task_room] FOREIGN KEY([room_id])
REFERENCES [dbo].[room] ([room_id])
GO
ALTER TABLE [dbo].[room_task] CHECK CONSTRAINT [FK_room_task_room]
GO
ALTER TABLE [dbo].[room_type]  WITH CHECK ADD  CONSTRAINT [FK_room_type_image] FOREIGN KEY([image_id])
REFERENCES [dbo].[image] ([image_id])
GO
ALTER TABLE [dbo].[room_type] CHECK CONSTRAINT [FK_room_type_image]
GO
