-- 插入测试项目数据（如果不存在）
INSERT IGNORE INTO os_project (project_id, project_name, project_code, description, status, create_time) VALUES
(1, 'RuoYi-Vue-Plus项目', 'RVP001', '基于RuoYi-Vue-Plus的企业级管理系统', '1', NOW()),
(2, 'Dromara社区管理系统', 'DCS001', 'Dromara开源社区管理平台', '1', NOW());

-- 插入测试项目成员数据（如果不存在）
INSERT IGNORE INTO os_project_member (project_id, user_id, role, permission_level, is_active, contribution_score, join_time, create_time) VALUES
(1, 1, '1', 5, '1', 90, NOW(), NOW()),
(1, 2, '2', 4, '1', 85, NOW(), NOW()),
(1, 3, '3', 3, '1', 70, NOW(), NOW()),
(1, 4, '4', 2, '1', 60, NOW(), NOW()),
(2, 1, '3', 3, '1', 80, NOW(), NOW()),
(2, 5, '1', 5, '1', 95, NOW(), NOW());

-- 插入测试项目阶段数据（如果表存在且有progress字段）
INSERT IGNORE INTO os_project_phase (project_id, phase_name, phase_code, description, status, start_time, end_time, progress, priority, create_time) VALUES
(1, '需求分析', 'REQ_ANALYSIS', '项目需求收集和分析', '2', '2024-01-01 09:00:00', '2024-01-15 18:00:00', 100, 3, NOW()),
(1, '系统设计', 'SYS_DESIGN', '系统架构和详细设计', '1', '2024-01-10 09:00:00', '2024-01-30 18:00:00', 75, 3, NOW()),
(1, '编码实现', 'DEVELOPMENT', '核心功能开发', '1', '2024-01-20 09:00:00', '2024-03-01 18:00:00', 45, 2, NOW()),
(1, '测试验证', 'TESTING', '功能测试和bug修复', '0', '2024-02-15 09:00:00', '2024-03-15 18:00:00', 0, 2, NOW());