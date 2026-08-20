INSERT INTO room_task (
    room_id, 
    employee_id, 
    task_type,
    task_status,
    priority, 
    remark, 
    created_at, 
    completed_at
)
SELECT 
    room_id,
    employee_id,
    task_type,
    task_status,
    priority,
    
    -- remark: 30% 機率允許 NULL (因為 Schema 只有 remark 設定可 NULL)
    CASE 
        WHEN rand_remark_flag < 30 THEN NULL
        WHEN task_type = N'設備維修' THEN 
            CASE rand_remark_idx
                WHEN 0 THEN N'馬桶堵塞'
                WHEN 1 THEN N'冷氣不冷'
                WHEN 2 THEN N'電視無法開機'
                ELSE N'蓮蓬頭漏水'
            END
        WHEN task_type = N'退房清潔' THEN 
            CASE (rand_remark_idx % 2)
                WHEN 0 THEN N'退房深度清潔'
                ELSE N'窗簾脫軌'
            END
        WHEN task_type = N'補充備品' THEN 
            CASE (rand_remark_idx % 2)
                WHEN 0 THEN N'補充沐浴乳'
                ELSE N'換洗毛巾備品'
            END
        ELSE N'續住日常整理'
    END AS remark,
    
    created_at,
    
    -- completed_at: 只有 task_status 為「已完成」時才計算時間，其餘強制為 NULL
    CASE 
        WHEN task_status = N'已完成' THEN DATEADD(MINUTE, rand_complete_min, created_at)
        ELSE NULL 
    END AS completed_at

FROM (
    SELECT TOP (60)
        -- room_id: 1 ~ 120
        (ABS(CHECKSUM(NEWID())) % 120) + 1 AS room_id,
        
        -- employee_id: 2 ~ 6
        (ABS(CHECKSUM(NEWID())) % 5) + 2 AS employee_id,
        
        -- 1. task_type (百分之百絕不為 NULL)
        CASE (ABS(CHECKSUM(NEWID())) % 4)
            WHEN 0 THEN N'退房清潔'
            WHEN 1 THEN N'續住清潔'
            WHEN 2 THEN N'設備維修'
            ELSE N'補充備品'
        END AS task_type,
        
        -- 2. task_status (百分之百絕不為 NULL)
        CASE (ABS(CHECKSUM(NEWID())) % 4)
            WHEN 0 THEN N'待處理'
            WHEN 1 THEN N'進行中'
            WHEN 2 THEN N'已完成'
            ELSE N'已取消'
        END AS task_status,

        -- 3. priority (百分之百絕不為 NULL)
        CASE (ABS(CHECKSUM(NEWID())) % 3)
            WHEN 0 THEN N'低'
            WHEN 1 THEN N'普通'
            ELSE N'緊急'
        END AS priority,
        
        -- 4. created_at (近 7 天內隨機時間)
        DATEADD(MINUTE, - (ABS(CHECKSUM(NEWID())) % 10080), GETDATE()) AS created_at,
        
        -- 輔助亂數欄位
        ABS(CHECKSUM(NEWID())) % 100 AS rand_remark_flag,
        ABS(CHECKSUM(NEWID())) % 4 AS rand_remark_idx,
        (ABS(CHECKSUM(NEWID())) % 120) + 30 AS rand_complete_min

    FROM sys.all_objects a
    CROSS JOIN sys.all_objects b
) AS RawTasks;