-- 检查项目表当前结构
DESCRIBE os_project;

-- 如果maintainer字段还存在，执行删除
-- ALTER TABLE os_project DROP COLUMN maintainer;