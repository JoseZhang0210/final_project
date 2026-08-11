CREATE TABLE [account] (
	[account_id] INTEGER IDENTITY,
	[username] VARCHAR(50) NOT NULL,
	[password] VARCHAR(255) NOT NULL,
	[status] VARCHAR(20) NOT NULL,
	PRIMARY KEY([account_id])
);
GO

CREATE TABLE [department] (
	[department_id] INTEGER IDENTITY,
	[department_name] VARCHAR(50) NOT NULL,
	PRIMARY KEY([department_id])
);
GO

CREATE TABLE [employee] (
	[employee_id] INTEGER IDENTITY,
	[department_id] INTEGER NOT NULL,
	[account_id] INTEGER NOT NULL,
	[position] VARCHAR(50) NOT NULL,
	PRIMARY KEY([employee_id])
);
GO

CREATE TABLE [permission] (
	[permission_id] INTEGER,
	[permission_code] VARCHAR(50) NOT NULL,
	[permission_name] VARCHAR(50) NOT NULL,
	PRIMARY KEY([permission_id])
);
GO

CREATE TABLE [employee_permission] (
	[permission_id] INTEGER NOT NULL,
	[employee_id] INTEGER NOT NULL,
	PRIMARY KEY([permission_id], [employee_id])
);
GO

CREATE TABLE [member] (
	[member_id] INTEGER IDENTITY,
	[account_id] INTEGER NOT NULL,
	PRIMARY KEY([member_id])
);
GO

CREATE TABLE [profile] (
	[profile_id] INTEGER IDENTITY,
	[account_id] INTEGER NOT NULL,
	[name] VARCHAR(50) NOT NULL,
	[email] VARCHAR(100),
	[phone] VARCHAR(20),
	[zipcode] VARCHAR(10),
	[city] VARCHAR(50),
	[district] VARCHAR(50),
	[address] VARCHAR(200),
	[created_at] DATETIME NOT NULL,
	[birthday] DATE,
	[gender] VARCHAR(10),
	[updated_at] DATETIME,
	PRIMARY KEY([profile_id])
);
GO

CREATE TABLE [image] (
	[image_id] INTEGER,
	[path] VARCHAR(50) NOT NULL,
	[image_desc] VARCHAR(10),
	PRIMARY KEY([image_id])
);
GO

CREATE TABLE [category] (
	[category_id] INTEGER,
	[category_name] VARCHAR(50),
	PRIMARY KEY([category_id])
);
GO

CREATE TABLE [product] (
	[product_id] INTEGER,
	[product_name] VARCHAR(50) NOT NULL,
	[category_id] INTEGER NOT NULL,
	[description] VARCHAR(50) NOT NULL,
	[price] INTEGER NOT NULL,
	[stock] INTEGER NOT NULL,
	[image_id] INTEGER,
	[status] VARCHAR(50),
	PRIMARY KEY([product_id])
);
GO

CREATE TABLE [payment] (
	[payment_id] INTEGER,
	[payment_method] VARCHAR(50),
	[payment_time] DATETIME,
	[total_price] INTEGER NOT NULL,
	[payment_status] VARCHAR(50) NOT NULL,
	[member_id] INTEGER,
	PRIMARY KEY([payment_id])
);
GO

CREATE TABLE [order] (
	[order_id] INTEGER,
	[member_id] INTEGER NOT NULL,
	[order_date] DATETIME NOT NULL,
	[is_ordered] BIT NOT NULL,
	[payment_id] INTEGER NOT NULL,
	PRIMARY KEY([order_id])
);
GO

CREATE TABLE [order_item] (
	[order_id] INTEGER NOT NULL,
	[product_id] INTEGER NOT NULL,
	[quantity] INTEGER NOT NULL,
	PRIMARY KEY([order_id], [product_id])
);
GO

CREATE TABLE [room_type] (
	[room_type_id] INTEGER IDENTITY,
	[type_name] VARCHAR(50) NOT NULL,
	[bed_type] VARCHAR(50) NOT NULL,
	[description] VARCHAR(50),
	[price_per_night] INTEGER NOT NULL,
	[capacity] INTEGER NOT NULL,
	[image_id] INTEGER,
	PRIMARY KEY([room_type_id])
);
GO

CREATE TABLE [room] (
	[room_id] INTEGER IDENTITY,
	[room_number] INTEGER NOT NULL,
	[room_type_id] INTEGER NOT NULL,
	[floor] INTEGER NOT NULL,
	[status] VARCHAR(50) NOT NULL,
	PRIMARY KEY([room_id])
);
GO

CREATE TABLE [room_task] (
	[task_id] INTEGER IDENTITY,
	[room_id] INTEGER NOT NULL,
	[employee_id] INTEGER NOT NULL,
	[remark] VARCHAR(50),
	[priority] VARCHAR(50) NOT NULL,
	[create_at] DATETIME NOT NULL,
	[completed_at] DATETIME,
	[task_type] VARCHAR(50) NOT NULL,
	[task_status] VARCHAR(50) NOT NULL,
	PRIMARY KEY([task_id])
);
GO

CREATE TABLE [booking_order] (
	[booking_order_id] INTEGER,
	[member_id] INTEGER NOT NULL,
	[booking_total_price] INTEGER NOT NULL,
	[order_status] VARCHAR(50) NOT NULL,
	[create_at] DATETIME NOT NULL,
	[payment_id] INTEGER NOT NULL,
	PRIMARY KEY([booking_order_id])
);
GO

CREATE TABLE [booking] (
	[booking_id] INTEGER IDENTITY,
	[booking_order_id] INTEGER NOT NULL,
	[check_in_date] DATETIME NOT NULL,
	[check_out_date] DATETIME NOT NULL,
	[guest_num] INTEGER NOT NULL,
	[booking_status] VARCHAR(50) NOT NULL,
	[room_id] INTEGER,
	[room_type_id] INTEGER NOT NULL,
	PRIMARY KEY([booking_id])
);
GO

CREATE TABLE [venue] (
	[venue_id] INTEGER,
	[venue_name] VARCHAR(50) NOT NULL,
	[capacity] INTEGER NOT NULL,
	[price_per_day] INTEGER NOT NULL,
	[venue_status] VARCHAR(50) NOT NULL,
	PRIMARY KEY([venue_id])
);
GO

CREATE TABLE [rental] (
	[rental_id] INTEGER,
	[venue_id] INTEGER NOT NULL,
	[member_id] INTEGER NOT NULL,
	[event_name] VARCHAR(50) NOT NULL,
	[rental_date] DATETIME NOT NULL,
	[guest_count] INTEGER NOT NULL,
	[payment_id] INTEGER NOT NULL,
	[rental_status] VARCHAR(50),
	PRIMARY KEY([rental_id])
);
GO

CREATE TABLE [restaurant] (
	[restaurant_id] INTEGER NOT NULL,
	[restaurant_name] VARCHAR(100) NOT NULL,
	[address] VARCHAR(200) NOT NULL,
	[phone] VARCHAR(20) NOT NULL,
	[capacity] INTEGER NOT NULL,
	[description] VARCHAR(50) NOT NULL,
	PRIMARY KEY([restaurant_id])
);
GO

CREATE TABLE [reservation] (
	[reservation_id] INTEGER NOT NULL,
	[member_id] INTEGER NOT NULL,
	[restaurant_id] INTEGER NOT NULL,
	[time_id] INTEGER NOT NULL,
	[reservation_date] DATETIME NOT NULL,
	[people_count] INTEGER NOT NULL,
	[status] VARCHAR(20) NOT NULL,
	[create_time] DATETIME NOT NULL,
	PRIMARY KEY([reservation_id])
);
GO

CREATE TABLE [restaurant_time] (
	[time_id] INTEGER NOT NULL,
	[restaurant_id] INTEGER NOT NULL,
	[meal_type] NVARCHAR(20) NOT NULL,
	[open_time] TIME NOT NULL,
	[close_time] TIME NOT NULL,
	PRIMARY KEY([time_id])
);
GO


ALTER TABLE [employee]
ADD FOREIGN KEY([department_id])
REFERENCES [department]([department_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [employee]
ADD FOREIGN KEY([account_id])
REFERENCES [account]([account_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [employee_permission]
ADD FOREIGN KEY([permission_id])
REFERENCES [permission]([permission_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [employee_permission]
ADD FOREIGN KEY([employee_id])
REFERENCES [employee]([employee_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [member]
ADD FOREIGN KEY([account_id])
REFERENCES [account]([account_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [profile]
ADD FOREIGN KEY([account_id])
REFERENCES [account]([account_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [product]
ADD FOREIGN KEY([category_id])
REFERENCES [category]([category_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [payment]
ADD FOREIGN KEY([member_id])
REFERENCES [member]([member_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [reservation]
ADD FOREIGN KEY([member_id])
REFERENCES [member]([member_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [reservation]
ADD FOREIGN KEY([restaurant_id])
REFERENCES [restaurant]([restaurant_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [booking]
ADD FOREIGN KEY([booking_order_id])
REFERENCES [booking_order]([booking_order_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [booking]
ADD FOREIGN KEY([room_id])
REFERENCES [room]([room_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [booking]
ADD FOREIGN KEY([room_type_id])
REFERENCES [room_type]([room_type_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [booking_order]
ADD FOREIGN KEY([member_id])
REFERENCES [member]([member_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [booking_order]
ADD FOREIGN KEY([payment_id])
REFERENCES [payment]([payment_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [room_task]
ADD FOREIGN KEY([room_id])
REFERENCES [room]([room_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [room_task]
ADD FOREIGN KEY([employee_id])
REFERENCES [employee]([employee_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [room]
ADD FOREIGN KEY([room_type_id])
REFERENCES [room_type]([room_type_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [room_type]
ADD FOREIGN KEY([image_id])
REFERENCES [image]([image_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [order_item]
ADD FOREIGN KEY([product_id])
REFERENCES [product]([product_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [order]
ADD FOREIGN KEY([payment_id])
REFERENCES [payment]([payment_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [order]
ADD FOREIGN KEY([member_id])
REFERENCES [member]([member_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [order_item]
ADD FOREIGN KEY([order_id])
REFERENCES [order]([order_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [rental]
ADD FOREIGN KEY([venue_id])
REFERENCES [venue]([venue_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [rental]
ADD FOREIGN KEY([member_id])
REFERENCES [member]([member_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [rental]
ADD FOREIGN KEY([payment_id])
REFERENCES [payment]([payment_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [restaurant_time]
ADD FOREIGN KEY([time_id])
REFERENCES [reservation]([time_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
ALTER TABLE [restaurant]
ADD FOREIGN KEY([restaurant_id])
REFERENCES [restaurant_time]([restaurant_id])
ON UPDATE NO ACTION ON DELETE NO ACTION;
GO
