IF NOT EXISTS (SELECT name FROM sys.databases WHERE name = 'finalproject')
BEGIN
    CREATE DATABASE finalproject COLLATE Chinese_Taiwan_Stroke_CI_AS;
END;
GO

IF NOT EXISTS (SELECT name FROM sys.server_principals WHERE name = 'hotel_app')
BEGIN
    CREATE LOGIN hotel_app WITH PASSWORD = '123456', CHECK_POLICY = OFF, CHECK_EXPIRATION = OFF;
END;
GO

USE finalproject;
GO

IF NOT EXISTS (SELECT name FROM sys.database_principals WHERE name = 'hotel_app')
BEGIN
    CREATE USER hotel_app FOR LOGIN hotel_app;
    ALTER ROLE db_owner ADD MEMBER hotel_app;
END;
GO
