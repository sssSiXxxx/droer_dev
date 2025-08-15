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

-- 贡献统计菜单
-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1956364860340125698, '贡献统计', '1942216939709456385', '1', 'contribution', 'osc/contribution/index', 1, 0, 'C', '0', '0', 'osc:contribution:list', '#', 103, 1, sysdate(), null, null, '贡献统计菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1956364860340125699, '贡献统计查询', 1956364860340125698, '1',  '#', '', 1, 0, 'F', '0', '0', 'osc:contribution:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1956364860340125700, '贡献统计新增', 1956364860340125698, '2',  '#', '', 1, 0, 'F', '0', '0', 'osc:contribution:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1956364860340125701, '贡献统计修改', 1956364860340125698, '3',  '#', '', 1, 0, 'F', '0', '0', 'osc:contribution:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1956364860340125702, '贡献统计删除', 1956364860340125698, '4',  '#', '', 1, 0, 'F', '0', '0', 'osc:contribution:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1956364860340125703, '贡献统计导出', 1956364860340125698, '5',  '#', '', 1, 0, 'F', '0', '0', 'osc:contribution:export',       '#', 103, 1, sysdate(), null, null, '');

-- 人员项目管理菜单
-- 菜单 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1956377635485433858, '人员项目管理', '1942216939709456385', '1', 'projectMember', 'osc/projectMember/index', 1, 0, 'C', '0', '0', 'osc:projectMember:list', '#', 103, 1, sysdate(), null, null, '人员项目管理菜单');

-- 按钮 SQL
insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1956377635485433859, '人员项目管理查询', 1956377635485433858, '1',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectMember:query',        '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1956377635485433860, '人员项目管理新增', 1956377635485433858, '2',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectMember:add',          '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1956377635485433861, '人员项目管理修改', 1956377635485433858, '3',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectMember:edit',         '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1956377635485433862, '人员项目管理删除', 1956377635485433858, '4',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectMember:remove',       '#', 103, 1, sysdate(), null, null, '');

insert into sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_dept, create_by, create_time, update_by, update_time, remark)
values(1956377635485433863, '人员项目管理导出', 1956377635485433858, '5',  '#', '', 1, 0, 'F', '0', '0', 'osc:projectMember:export',       '#', 103, 1, sysdate(), null, null, '');

