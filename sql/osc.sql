-- ----------------------------
-- 更新 sys_oss 表结构，添加项目相关字段
-- ----------------------------

-- 为 sys_oss 表添加缺失的字段
ALTER TABLE sys_oss ADD COLUMN IF NOT EXISTS project_id bigint COMMENT '所属项目ID';
ALTER TABLE sys_oss ADD COLUMN IF NOT EXISTS file_type varchar(50) DEFAULT 'other' COMMENT '文档类型（logo: Logo图片, requirement: 需求文档, help: 帮助文档, design: 设计文档, api: 接口文档, other: 其他文档）';

-- 如果 size 字段不存在，则添加
ALTER TABLE sys_oss ADD COLUMN IF NOT EXISTS size bigint DEFAULT 0 COMMENT '文件大小（字节）';

-- 添加索引
CREATE INDEX IF NOT EXISTS idx_sys_oss_project_id ON sys_oss(project_id);
CREATE INDEX IF NOT EXISTS idx_sys_oss_file_type ON sys_oss(file_type);
