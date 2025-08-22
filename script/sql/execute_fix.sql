-- 执行数据库修复脚本
-- 请在MySQL客户端中执行以下命令

USE `ry-vue`;

-- 添加缺失的字段到 os_project_member 表
ALTER TABLE `os_project_member` 
ADD COLUMN IF NOT EXISTS `permission_level` int DEFAULT 1 COMMENT '权限级别（1-5，数字越大权限越高）';

ALTER TABLE `os_project_member` 
ADD COLUMN IF NOT EXISTS `is_active` char(1) DEFAULT '1' COMMENT '是否活跃（0非活跃 1活跃）';

ALTER TABLE `os_project_member` 
ADD COLUMN IF NOT EXISTS `contribution_score` int DEFAULT 0 COMMENT '贡献度评分（1-100）';

ALTER TABLE `os_project_member` 
ADD COLUMN IF NOT EXISTS `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）';

ALTER TABLE `os_project_member` 
ADD COLUMN IF NOT EXISTS `tenant_id` bigint DEFAULT 0 COMMENT '租户编号';

-- 为现有数据设置默认值
UPDATE `os_project_member` SET `permission_level` = 1 WHERE `permission_level` IS NULL;
UPDATE `os_project_member` SET `is_active` = '1' WHERE `is_active` IS NULL;
UPDATE `os_project_member` SET `contribution_score` = 0 WHERE `contribution_score` IS NULL;
UPDATE `os_project_member` SET `del_flag` = '0' WHERE `del_flag` IS NULL;
UPDATE `os_project_member` SET `tenant_id` = 0 WHERE `tenant_id` IS NULL;

-- 验证表结构
DESCRIBE `os_project_member`;
