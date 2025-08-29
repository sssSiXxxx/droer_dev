-- 为 os_project_phase 表添加缺失字段
ALTER TABLE os_project_phase 
ADD COLUMN progress int DEFAULT 0 COMMENT '进度百分比(0-100)',
ADD COLUMN actual_start_time datetime COMMENT '实际开始时间',
ADD COLUMN actual_end_time datetime COMMENT '实际结束时间', 
ADD COLUMN owner_id bigint COMMENT '负责人ID',
ADD COLUMN priority int DEFAULT 2 COMMENT '优先级(1低 2中 3高)';