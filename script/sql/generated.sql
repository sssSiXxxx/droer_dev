-- 项目列表菜单
-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1951620635643928577, '项目列表', '1942218689354981377', '1', 'project', 'osc/project/index', 1, 0, 'C', '0', '0', 'osc:project:list', '#', 103, 1, sysdate(), null, null, '项目列表菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1951620635643928578, '项目列表查询', 1951620635643928577, '1',  '#', '', 1, 0, 'F', '0', '0', 'osc:project:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1951620635643928579, '项目列表新增', 1951620635643928577, '2',  '#', '', 1, 0, 'F', '0', '0', 'osc:project:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1951620635643928580, '项目列表修改', 1951620635643928577, '3',  '#', '', 1, 0, 'F', '0', '0', 'osc:project:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1951620635643928581, '项目列表删除', 1951620635643928577, '4',  '#', '', 1, 0, 'F', '0', '0', 'osc:project:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1951620635643928582, '项目列表导出', 1951620635643928577, '5',  '#', '', 1, 0, 'F', '0', '0', 'osc:project:export',       '#', 103, 1, sysdate(), null, null, '');

-- 项目审核菜单
-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1953145869056430082, '项目审核', '1942218689354981377', '1', 'projectAudit', 'osc/projectAudit/index', 1, 0, 'C', '0', '0', 'osc:projectAudit:list', '#', 103, 1, sysdate(), null, null, '项目审核菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1953145869056430083, '项目审核查询', 1953145869056430082, '1',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectAudit:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1953145869056430084, '项目审核新增', 1953145869056430082, '2',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectAudit:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1953145869056430085, '项目审核修改', 1953145869056430082, '3',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectAudit:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1953145869056430086, '项目审核删除', 1953145869056430082, '4',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectAudit:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1953145869056430087, '项目审核导出', 1953145869056430082, '5',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectAudit:export',       '#', 103, 1, sysdate(), null, null, '');

-- 项目创建菜单
-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1953318585432576002, '项目创建', '1942218689354981377', '1', 'projectCreate', 'osc/projectCreate/index', 1, 0, 'C', '0', '0', 'osc:projectCreate:list', '#', 103, 1, sysdate(), null, null, '项目创建菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1953318585432576003, '项目创建查询', 1953318585432576002, '1',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectCreate:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1953318585432576004, '项目创建新增', 1953318585432576002, '2',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectCreate:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1953318585432576005, '项目创建修改', 1953318585432576002, '3',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectCreate:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1953318585432576006, '项目创建删除', 1953318585432576002, '4',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectCreate:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1953318585432576007, '项目创建导出', 1953318585432576002, '5',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectCreate:export',       '#', 103, 1, sysdate(), null, null, '');


--进度追踪菜单
-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1954519360355749889, '进度追踪', '1942218689354981377', '1', 'projectPhase', 'osc/projectPhase/index', 1, 0, 'C', '0', '0', 'osc:projectPhase:list', '#', 103, 1, sysdate(), null, null, '进度追踪菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1954519360355749890, '进度追踪查询', 1954519360355749889, '1',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectPhase:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1954519360355749891, '进度追踪新增', 1954519360355749889, '2',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectPhase:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1954519360355749892, '进度追踪修改', 1954519360355749889, '3',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectPhase:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1954519360355749893, '进度追踪删除', 1954519360355749889, '4',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectPhase:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1954519360355749894, '进度追踪导出', 1954519360355749889, '5',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectPhase:export',       '#', 103, 1, sysdate(), null, null, '');
