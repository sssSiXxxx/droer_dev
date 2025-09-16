-- ----------------------------
-- 0、warm-flow-all.sql，地址：https://gitee.com/dromara/warm-flow/blob/master/sql/mysql/warm-flow-all.sql
-- ----------------------------
CREATE TABLE `flow_definition`
(
    `id`              bigint          NOT NULL COMMENT '主键id',
    `flow_code`       varchar(40)     NOT NULL COMMENT '流程编码',
    `flow_name`       varchar(100)    NOT NULL COMMENT '流程名称',
    `category`        varchar(100)             DEFAULT NULL COMMENT '流程类别',
    `version`         varchar(20)     NOT NULL COMMENT '流程版本',
    `is_publish`      tinyint(1)      NOT NULL DEFAULT '0' COMMENT '是否发布�?未发�?1已发�?9失效�?,
    `form_custom`     char(1)                  DEFAULT 'N' COMMENT '审批表单是否自定义（Y�?N否）',
    `form_path`       varchar(100)             DEFAULT NULL COMMENT '审批表单路径',
    `activity_status` tinyint(1)      NOT NULL DEFAULT '1' COMMENT '流程激活状态（0挂起 1激活）',
    `listener_type`   varchar(100)             DEFAULT NULL COMMENT '监听器类�?,
    `listener_path`   varchar(400)             DEFAULT NULL COMMENT '监听器路�?,
    `ext`             varchar(500)             DEFAULT NULL COMMENT '业务详情 存业务表对象json字符�?,
    `create_time`     datetime                 DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime                 DEFAULT NULL COMMENT '更新时间',
    `del_flag`        char(1)                  DEFAULT '0' COMMENT '删除标志',
    `tenant_id`       varchar(40)              DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT ='流程定义�?;

CREATE TABLE `flow_node`
(
    `id`              bigint        NOT NULL COMMENT '主键id',
    `node_type`       tinyint(1)      NOT NULL COMMENT '节点类型�?开始节�?1中间节点 2结束节点 3互斥网关 4并行网关�?,
    `definition_id`   bigint          NOT NULL COMMENT '流程定义id',
    `node_code`       varchar(100)    NOT NULL COMMENT '流程节点编码',
    `node_name`       varchar(100)  DEFAULT NULL COMMENT '流程节点名称',
    `permission_flag` varchar(200)  DEFAULT NULL COMMENT '权限标识（权限类�?权限标识，可以多个，用@@隔开)',
    `node_ratio`      decimal(6, 3) DEFAULT NULL COMMENT '流程签署比例�?,
    `coordinate`      varchar(100)  DEFAULT NULL COMMENT '坐标',
    `any_node_skip`   varchar(100)  DEFAULT NULL COMMENT '任意结点跳转',
    `listener_type`   varchar(100)  DEFAULT NULL COMMENT '监听器类�?,
    `listener_path`   varchar(400)  DEFAULT NULL COMMENT '监听器路�?,
    `handler_type`    varchar(100)  DEFAULT NULL COMMENT '处理器类�?,
    `handler_path`    varchar(400)  DEFAULT NULL COMMENT '处理器路�?,
    `form_custom`     char(1)       DEFAULT 'N' COMMENT '审批表单是否自定义（Y�?N否）',
    `form_path`       varchar(100)  DEFAULT NULL COMMENT '审批表单路径',
    `version`         varchar(20)     NOT NULL COMMENT '版本',
    `create_time`     datetime      DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime      DEFAULT NULL COMMENT '更新时间',
    `ext`             text          COMMENT '节点扩展属�?,
    `del_flag`        char(1)       DEFAULT '0' COMMENT '删除标志',
    `tenant_id`       varchar(40)   DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT ='流程节点�?;

CREATE TABLE `flow_skip`
(
    `id`             bigint       NOT NULL COMMENT '主键id',
    `definition_id`  bigint          NOT NULL COMMENT '流程定义id',
    `now_node_code`  varchar(100)    NOT NULL COMMENT '当前流程节点的编�?,
    `now_node_type`  tinyint(1)   DEFAULT NULL COMMENT '当前节点类型�?开始节�?1中间节点 2结束节点 3互斥网关 4并行网关�?,
    `next_node_code` varchar(100)    NOT NULL COMMENT '下一个流程节点的编码',
    `next_node_type` tinyint(1)   DEFAULT NULL COMMENT '下一个节点类型（0开始节�?1中间节点 2结束节点 3互斥网关 4并行网关�?,
    `skip_name`      varchar(100) DEFAULT NULL COMMENT '跳转名称',
    `skip_type`      varchar(40)  DEFAULT NULL COMMENT '跳转类型（PASS审批通过 REJECT退回）',
    `skip_condition` varchar(200) DEFAULT NULL COMMENT '跳转条件',
    `coordinate`     varchar(100) DEFAULT NULL COMMENT '坐标',
    `create_time`    datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`    datetime     DEFAULT NULL COMMENT '更新时间',
    `del_flag`       char(1)      DEFAULT '0' COMMENT '删除标志',
    `tenant_id`      varchar(40)  DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT ='节点跳转关联�?;

CREATE TABLE `flow_instance`
(
    `id`              bigint      NOT NULL COMMENT '主键id',
    `definition_id`   bigint      NOT NULL COMMENT '对应flow_definition表的id',
    `business_id`     varchar(40) NOT NULL COMMENT '业务id',
    `node_type`       tinyint(1)  NOT NULL COMMENT '节点类型�?开始节�?1中间节点 2结束节点 3互斥网关 4并行网关�?,
    `node_code`       varchar(40) NOT NULL COMMENT '流程节点编码',
    `node_name`       varchar(100)         DEFAULT NULL COMMENT '流程节点名称',
    `variable`        text COMMENT '任务变量',
    `flow_status`     varchar(20) NOT NULL COMMENT '流程状态（0待提�?1审批�?2审批通过 4终止 5作废 6撤销 8已完�?9已退�?10失效 11拿回�?,
    `activity_status` tinyint(1)  NOT NULL DEFAULT '1' COMMENT '流程激活状态（0挂起 1激活）',
    `def_json`        text COMMENT '流程定义json',
    `create_by`       varchar(64)          DEFAULT '' COMMENT '创建�?,
    `create_time`     datetime             DEFAULT NULL COMMENT '创建时间',
    `update_time`     datetime             DEFAULT NULL COMMENT '更新时间',
    `ext`             varchar(500)         DEFAULT NULL COMMENT '扩展字段，预留给业务系统使用',
    `del_flag`        char(1)              DEFAULT '0' COMMENT '删除标志',
    `tenant_id`       varchar(40)          DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT ='流程实例�?;

CREATE TABLE `flow_task`
(
    `id`            bigint       NOT NULL COMMENT '主键id',
    `definition_id` bigint       NOT NULL COMMENT '对应flow_definition表的id',
    `instance_id`   bigint       NOT NULL COMMENT '对应flow_instance表的id',
    `node_code`     varchar(100) NOT NULL COMMENT '节点编码',
    `node_name`     varchar(100) DEFAULT NULL COMMENT '节点名称',
    `node_type`     tinyint(1)   NOT NULL COMMENT '节点类型�?开始节�?1中间节点 2结束节点 3互斥网关 4并行网关�?,
    `flow_status`   varchar(20)  NOT NULL COMMENT '流程状态（0待提�?1审批�?2审批通过 4终止 5作废 6撤销 8已完�?9已退�?10失效 11拿回�?,
    `form_custom`   char(1)      DEFAULT 'N' COMMENT '审批表单是否自定义（Y�?N否）',
    `form_path`     varchar(100) DEFAULT NULL COMMENT '审批表单路径',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '更新时间',
    `del_flag`      char(1)      DEFAULT '0' COMMENT '删除标志',
    `tenant_id`     varchar(40)  DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT ='待办任务�?;

CREATE TABLE `flow_his_task`
(
    `id`               bigint(20)                   NOT NULL COMMENT '主键id',
    `definition_id`    bigint(20)                   NOT NULL COMMENT '对应flow_definition表的id',
    `instance_id`      bigint(20)                   NOT NULL COMMENT '对应flow_instance表的id',
    `task_id`          bigint(20)                   NOT NULL COMMENT '对应flow_task表的id',
    `node_code`        varchar(100)                 DEFAULT NULL COMMENT '开始节点编�?,
    `node_name`        varchar(100)                 DEFAULT NULL COMMENT '开始节点名�?,
    `node_type`        tinyint(1)                   DEFAULT NULL COMMENT '开始节点类型（0开始节�?1中间节点 2结束节点 3互斥网关 4并行网关�?,
    `target_node_code` varchar(200)                 DEFAULT NULL COMMENT '目标节点编码',
    `target_node_name` varchar(200)                 DEFAULT NULL COMMENT '结束节点名称',
    `approver`         varchar(40)                  DEFAULT NULL COMMENT '审批�?,
    `cooperate_type`   tinyint(1)                   NOT NULL DEFAULT '0' COMMENT '协作方式(1审批 2转办 3委派 4会签 5票签 6加签 7减签)',
    `collaborator`     varchar(40)                  DEFAULT NULL COMMENT '协作�?,
    `skip_type`        varchar(10)                  NOT NULL COMMENT '流转类型（PASS通过 REJECT退�?NONE无动作）',
    `flow_status`      varchar(20)                  NOT NULL COMMENT '流程状态（0待提�?1审批�?2审批通过 4终止 5作废 6撤销 8已完�?9已退�?10失效 11拿回�?,
    `form_custom`      char(1)                      DEFAULT 'N' COMMENT '审批表单是否自定义（Y�?N否）',
    `form_path`        varchar(100)                 DEFAULT NULL COMMENT '审批表单路径',
    `message`          varchar(500)                 DEFAULT NULL COMMENT '审批意见',
    `variable`         TEXT                         DEFAULT NULL COMMENT '任务变量',
    `ext`              TEXT                         DEFAULT NULL COMMENT '业务详情 存业务表对象json字符�?,
    `create_time`      datetime                     DEFAULT NULL COMMENT '任务开始时�?,
    `update_time`      datetime                     DEFAULT NULL COMMENT '审批完成时间',
    `del_flag`         char(1)                      DEFAULT '0' COMMENT '删除标志',
    `tenant_id`        varchar(40)                  DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB COMMENT ='历史任务记录�?;


CREATE TABLE `flow_user`
(
    `id`           bigint      NOT NULL COMMENT '主键id',
    `type`         char(1)         NOT NULL COMMENT '人员类型�?待办任务的审批人权限 2待办任务的转办人权限 3待办任务的委托人权限�?,
    `processed_by` varchar(80) DEFAULT NULL COMMENT '权限�?,
    `associated`   bigint          NOT NULL COMMENT '任务表id',
    `create_time`  datetime    DEFAULT NULL COMMENT '创建时间',
    `create_by`    varchar(80) DEFAULT NULL COMMENT '创建�?,
    `update_time`  datetime    DEFAULT NULL COMMENT '更新时间',
    `del_flag`     char(1)     DEFAULT '0' COMMENT '删除标志',
    `tenant_id`    varchar(40) DEFAULT NULL COMMENT '租户id',
    PRIMARY KEY (`id`) USING BTREE,
    KEY `user_processed_type` (`processed_by`, `type`),
    KEY `user_associated` (`associated`) USING BTREE
) ENGINE = InnoDB COMMENT ='流程用户�?;

-- ----------------------------
-- 流程分类�?
-- ----------------------------
create table flow_category
(
    category_id   bigint(20)  not null comment '流程分类ID',
    tenant_id     varchar(20) default '000000' comment '租户编号',
    parent_id     bigint(20)   default 0 comment '父流程分类id',
    ancestors     varchar(500) default '' comment '祖级列表',
    category_name varchar(30) not null comment '流程分类名称',
    order_num     int(4)       default 0 comment '显示顺序',
    del_flag      char(1)      default '0' comment '删除标志�?代表存在 1代表删除�?,
    create_dept   bigint(20)  null comment '创建部门',
    create_by     bigint(20)  null comment '创建�?,
    create_time   datetime    null comment '创建时间',
    update_by     bigint(20)  null comment '更新�?,
    update_time   datetime    null comment '更新时间',
    primary key (category_id)
) engine = innodb comment = '流程分类';

-- Dromara 开源社区一体化管理系统 - 工作流分类数�?
-- 1. 开源项目管理流�?
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (100, 0, '0', '开源项目管�?, 0, '0', 103, 1, NOW(), null, null);

-- 1.1 项目申请流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (101, '000000', 100, '0,100', '项目申请', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (102, 101, '0,100,101', '新项目申�?, 0, '0', 103, 1, NOW(), null, null);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (103, '000000', 101, '0,100,101', '项目升级申请', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (104, '000000', 101, '0,100,101', '项目迁移申请', 2, '0', 103, 1, NOW(), NULL, NULL);

-- 1.2 项目审核流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (105, '000000', 100, '0,100', '项目审核', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (106, 105, '0,100,105', '技术审�?, 0, '0', 103, 1, NOW(), null, null);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (107, '000000', 105, '0,100,105', '安全审核', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (108, '000000', 105, '0,100,105', '合规审核', 2, '0', 103, 1, NOW(), NULL, NULL);

-- 1.3 项目发布流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (109, '000000', 100, '0,100', '项目发布', 2, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (110, '000000', 109, '0,100,109', '版本发布', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (111, '000000', 109, '0,100,109', '文档发布', 1, '0', 103, 1, NOW(), NULL, NULL);

-- 2. 社区成员管理流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (200, '000000', 0, '0', '社区成员管理', 1, '0', 103, 1, NOW(), NULL, NULL);

-- 2.1 成员申请流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (201, '000000', 200, '0,200', '成员申请', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (202, 201, '0,200,201', '开发者申�?, 0, '0', 103, 1, NOW(), null, null);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (203, 201, '0,200,201', '维护者申�?, 1, '0', 103, 1, NOW(), null, null);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (204, 201, '0,200,201', '贡献者申�?, 2, '0', 103, 1, NOW(), null, null);

-- 2.2 成员权限管理
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (205, '000000', 200, '0,200', '权限管理', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (206, '000000', 205, '0,200,205', '权限申请', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (207, '000000', 205, '0,200,205', '权限变更', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (208, '000000', 205, '0,200,205', '权限撤销', 2, '0', 103, 1, NOW(), NULL, NULL);

-- 3. 贡献管理流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (300, '000000', 0, '0', '贡献管理', 2, '0', 103, 1, NOW(), NULL, NULL);

-- 3.1 代码贡献流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (301, '000000', 300, '0,300', '代码贡献', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (302, '000000', 301, '0,300,301', 'Pull Request', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (303, '000000', 301, '0,300,301', '代码审查', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (304, '000000', 301, '0,300,301', '代码合并', 2, '0', 103, 1, NOW(), NULL, NULL);

-- 3.2 文档贡献流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (305, '000000', 300, '0,300', '文档贡献', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (306, '000000', 305, '0,300,305', '文档编写', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (307, '000000', 305, '0,300,305', '文档审核', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (308, '000000', 305, '0,300,305', '文档发布', 2, '0', 103, 1, NOW(), NULL, NULL);

-- 3.3 问题反馈流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (309, '000000', 300, '0,300', '问题反馈', 2, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (310, '000000', 309, '0,300,309', 'Bug 报告', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (311, '000000', 309, '0,300,309', '功能建议', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (312, '000000', 309, '0,300,309', '问题处理', 2, '0', 103, 1, NOW(), NULL, NULL);

-- 4. 活动管理流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (400, '000000', 0, '0', '活动管理', 3, '0', 103, 1, NOW(), NULL, NULL);

-- 4.1 活动申请流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (401, '000000', 400, '0,400', '活动申请', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (402, 401, '0,400,401', '技术分�?, 0, '0', 103, 1, NOW(), null, null);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (403, 401, '0,400,401', '开源大�?, 1, '0', 103, 1, NOW(), null, null);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (404, 401, '0,400,401', '黑客�?, 2, '0', 103, 1, NOW(), null, null);

-- 4.2 活动组织流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (405, '000000', 400, '0,400', '活动组织', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (406, '000000', 405, '0,400,405', '活动策划', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (407, '000000', 405, '0,400,405', '活动执行', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (408, '000000', 405, '0,400,405', '活动总结', 2, '0', 103, 1, NOW(), NULL, NULL);

-- 5. 财务管理流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (500, '000000', 0, '0', '财务管理', 4, '0', 103, 1, NOW(), NULL, NULL);

-- 5.1 资金申请流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (501, '000000', 500, '0,500', '资金申请', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (502, '000000', 501, '0,500,501', '项目资金', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (503, '000000', 501, '0,500,501', '活动资金', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (504, '000000', 501, '0,500,501', '奖励资金', 2, '0', 103, 1, NOW(), NULL, NULL);

-- 5.2 财务审核流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (505, '000000', 500, '0,500', '财务审核', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (506, '000000', 505, '0,500,505', '预算审核', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (507, '000000', 505, '0,500,505', '报销审核', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (508, '000000', 505, '0,500,505', '发票审核', 2, '0', 103, 1, NOW(), NULL, NULL);

-- 6. 合规管理流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (600, '000000', 0, '0', '合规管理', 5, '0', 103, 1, NOW(), NULL, NULL);

-- 6.1 许可证管�?
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (601, 600, '0,600', '许可证管�?, 0, '0', 103, 1, NOW(), null, null);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (602, 601, '0,600,601', '许可证申�?, 0, '0', 103, 1, NOW(), null, null);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (603, 601, '0,600,601', '许可证变�?, 1, '0', 103, 1, NOW(), null, null);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (604, 601, '0,600,601', '许可证审�?, 2, '0', 103, 1, NOW(), null, null);

-- 6.2 安全合规
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (605, '000000', 600, '0,600', '安全合规', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (606, '000000', 605, '0,600,605', '安全审计', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (607, '000000', 605, '0,600,605', '漏洞修复', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (608, 605, '0,600,605', '合规检�?, 2, '0', 103, 1, NOW(), null, null);

-- 7. 基础设施管理流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (700, '000000', 0, '0', '基础设施管理', 6, '0', 103, 1, NOW(), NULL, NULL);

-- 7.1 服务器管�?
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (701, 700, '0,700', '服务器管�?, 0, '0', 103, 1, NOW(), null, null);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (702, 701, '0,700,701', '服务器申�?, 0, '0', 103, 1, NOW(), null, null);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (703, 701, '0,700,701', '服务器维�?, 1, '0', 103, 1, NOW(), null, null);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (704, 701, '0,700,701', '服务器扩�?, 2, '0', 103, 1, NOW(), null, null);

-- 7.2 域名管理
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (705, '000000', 700, '0,700', '域名管理', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (706, '000000', 705, '0,700,705', '域名申请', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (707, '000000', 705, '0,700,705', '域名续费', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (708, '000000', 705, '0,700,705', '域名转移', 2, '0', 103, 1, NOW(), NULL, NULL);

-- 8. 社区治理流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (800, '000000', 0, '0', '社区治理', 7, '0', 103, 1, NOW(), NULL, NULL);

-- 8.1 决策流程
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (801, '000000', 800, '0,800', '决策流程', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (802, '000000', 801, '0,800,801', '政策制定', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (803, '000000', 801, '0,800,801', '规则修改', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (804, '000000', 801, '0,800,801', '重大决策', 2, '0', 103, 1, NOW(), NULL, NULL);

-- 8.2 争议处理
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (805, '000000', 800, '0,800', '争议处理', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (806, '000000', 805, '0,800,805', '争议申诉', 0, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (807, '000000', 805, '0,800,805', '调解处理', 1, '0', 103, 1, NOW(), NULL, NULL);
INSERT INTO flow_category (category_id, tenant_id, parent_id, ancestors, category_name, order_num, del_flag, create_dept, create_by, create_time, update_by, update_time) VALUES (808, '000000', 805, '0,800,805', '仲裁决定', 2, '0', 103, 1, NOW(), NULL, NULL);

-- ----------------------------
-- 请假单信�?
-- ----------------------------
create table test_leave
(
    id          bigint(20)   not null comment 'id',
    leave_type  varchar(255) not null comment '请假类型',
    start_date  datetime     not null comment '开始时�?,
    end_date    datetime     not null comment '结束时间',
    leave_days  int(10)      not null comment '请假天数',
    remark      varchar(255) null comment '请假原因',
    status      varchar(255) null comment '状�?,
    create_dept bigint       null comment '创建部门',
    create_by   bigint       null comment '创建�?,
    create_time datetime     null comment '创建时间',
    update_by   bigint       null comment '更新�?,
    update_time datetime     null comment '更新时间',
    PRIMARY KEY (id) USING BTREE
) ENGINE = InnoDB COMMENT = '请假申请�?;

insert into sys_menu values ('11616', '工作�?, '0', '6', 'workflow', '', '', '1', '0', 'M', '0', '0', '', 'workflow', 103, 1, NOW(),NULL, NULL, '');
insert into sys_menu values ('11618', '我的任务', '0', '7', 'task', '', '', '1', '0', 'M', '0', '0', '', 'my-task', 103, 1, NOW(), NULL, NULL, '');
insert into sys_menu values ('11619', '我的待办', '11618', '2', 'taskWaiting', 'workflow/task/taskWaiting', '', '1', '1', 'C', '0', '0', '', 'waiting', 103, 1, NOW(), NULL, NULL, '');
insert into sys_menu values ('11632', '我的已办', '11618', '3', 'taskFinish', 'workflow/task/taskFinish', '', '1', '1', 'C', '0', '0', '', 'finish', 103, 1, NOW(), NULL, NULL, '');
insert into sys_menu values ('11633', '我的抄�?, '11618', '4', 'taskCopyList', 'workflow/task/taskCopyList', '', '1', '1', 'C', '0', '0', '', 'my-copy', 103, 1, NOW(), NULL, NULL, '');
insert into sys_menu values ('11620', '流程定义', '11616', '3', 'processDefinition', 'workflow/processDefinition/index', '', '1', '1', 'C', '0', '0', '', 'process-definition', 103, 1, NOW(), NULL, NULL, '');
insert into sys_menu values ('11621', '流程实例', '11630', '1', 'processInstance', 'workflow/processInstance/index', '', '1', '1', 'C', '0', '0', '', 'tree-table', 103, 1, NOW(), NULL, NULL, '');
insert into sys_menu values ('11622', '流程分类', '11616', '1', 'category', 'workflow/category/index', '', '1', '0', 'C', '0', '0', 'workflow:category:list', 'category', 103, 1, NOW(), NULL, NULL, '');
insert into sys_menu values ('11629', '我发起的', '11618', '1', 'myDocument', 'workflow/task/myDocument', '', '1', '1', 'C', '0', '0', '', 'guide', 103, 1, NOW(), NULL, NULL, '');
insert into sys_menu values ('11630', '流程监控', '11616', '4', 'monitor', '', '', '1', '0', 'M', '0', '0', '', 'monitor', 103, 1, NOW(), NULL, NULL, '');
insert into sys_menu values ('11631', '待办任务', '11630', '2', 'allTaskWaiting', 'workflow/task/allTaskWaiting', '', '1', '1', 'C', '0', '0', '', 'waiting', 103, 1, NOW(), NULL, NULL, '');
insert into sys_menu values ('11700', '流程设计', '11616', '5', 'design/index',   'workflow/processDefinition/design', '', 1, 1, 'C', '1', '0', 'workflow:leave:edit', '#', 103, 1, NOW(), null, null, '');
insert into sys_menu values ('11701', '请假申请', '11616', '6', 'leaveEdit/index', 'workflow/leave/leaveEdit', '', 1, 1, 'C', '1', '0', 'workflow:leave:edit', '#', 103, 1, NOW(), null, null, '');
-- 流程分类管理相关按钮
insert into sys_menu values ('11623', '流程分类查询', '11622', '1', '#', '', '', 1, 0, 'F', '0', '0', 'workflow:category:query', '#', 103, 1,NOW(), null, null, '');
insert into sys_menu values ('11624', '流程分类新增', '11622', '2', '#', '', '', 1, 0, 'F', '0', '0', 'workflow:category:add', '#', 103, 1,NOW(), null, null, '');
insert into sys_menu values ('11625', '流程分类修改', '11622', '3', '#', '', '', 1, 0, 'F', '0', '0', 'workflow:category:edit', '#', 103, 1,NOW(), null, null, '');
insert into sys_menu values ('11626', '流程分类删除', '11622', '4', '#', '', '', 1, 0, 'F', '0', '0', 'workflow:category:remove', '#', 103,1, NOW(), null, null, '');
insert into sys_menu values ('11627', '流程分类导出', '11622', '5', '#', '', '', 1, 0, 'F', '0', '0', 'workflow:category:export', '#', 103,1, NOW(), null, null, '');
-- 请假测试相关按钮
insert into sys_menu VALUES (11638, '请假申请',     5,    1, 'leave', 'workflow/leave/index', '', 1, 0, 'C', '0', '0', 'workflow:leave:list', '#', 103, 1, NOW(), NULL, NULL, '请假申请菜单');
insert into sys_menu VALUES (11639, '请假申请查询', 11638, 1, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:leave:query', '#', 103, 1, NOW(), NULL, NULL, '');
insert into sys_menu VALUES (11640, '请假申请新增', 11638, 2, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:leave:add', '#', 103, 1, NOW(), NULL, NULL, '');
insert into sys_menu VALUES (11641, '请假申请修改', 11638, 3, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:leave:edit', '#', 103, 1, NOW(), NULL, NULL, '');
insert into sys_menu VALUES (11642, '请假申请删除', 11638, 4, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:leave:remove', '#', 103, 1, NOW(), NULL, NULL, '');
insert into sys_menu VALUES (11643, '请假申请导出', 11638, 5, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:leave:export', '#', 103, 1, NOW(), NULL, NULL, '');

INSERT INTO sys_dict_type VALUES (13, '业务状�?, 'wf_business_status', 103, 1, NOW(), NULL, NULL, '业务状态列�?);
INSERT INTO sys_dict_type VALUES (14, '表单类型', 'wf_form_type', 103, 1, NOW(), NULL, NULL, '表单类型列表');
INSERT INTO sys_dict_type VALUES (15, '任务状�?, 'wf_task_status', 103, 1, NOW(), NULL, NULL, '任务状�?);
INSERT INTO sys_dict_data VALUES (39, 1, '已撤销', 'cancel', 'wf_business_status', '', 'danger', 'N', 103, 1, NOW(), NULL, NULL,'已撤销');
INSERT INTO sys_dict_data VALUES (40, 2, '草稿', 'draft', 'wf_business_status', '', 'info', 'N', 103, 1, NOW(), NULL, NULL, '草稿');
INSERT INTO sys_dict_data VALUES (41, 3, '待审�?, 'waiting', 'wf_business_status', '', 'primary', 'N', 103, 1, NOW(), NULL, NULL,'待审�?);
INSERT INTO sys_dict_data VALUES (42, 4, '已完�?, 'finish', 'wf_business_status', '', 'success', 'N', 103, 1, NOW(), NULL, NULL,'已完�?);
INSERT INTO sys_dict_data VALUES (43, 5, '已作�?, 'invalid', 'wf_business_status', '', 'danger', 'N', 103, 1, NOW(), NULL, NULL,'已作�?);
INSERT INTO sys_dict_data VALUES (44, 6, '已退�?, 'back', 'wf_business_status', '', 'danger', 'N', 103, 1, NOW(), NULL, NULL,'已退�?);
INSERT INTO sys_dict_data VALUES (45, 7, '已终�?, 'termination', 'wf_business_status', '', 'danger', 'N', 103, 1, NOW(), NULL,NULL, '已终�?);
INSERT INTO sys_dict_data VALUES (46, 1, '自定义表�?, 'static', 'wf_form_type', '', 'success', 'N', 103, 1, NOW(), NULL, NULL,'自定义表�?);
INSERT INTO sys_dict_data VALUES (47, 2, '动态表�?, 'dynamic', 'wf_form_type', '', 'primary', 'N', 103, 1, NOW(), NULL, NULL,'动态表�?);
INSERT INTO sys_dict_data VALUES (48, 1, '撤销', 'cancel', 'wf_task_status', '', 'danger', 'N', 103, 1, NOW(), NULL, NULL, '撤销');
INSERT INTO sys_dict_data VALUES (49, 2, '通过', 'pass', 'wf_task_status', '', 'success', 'N', 103, 1, NOW(), NULL, NULL, '通过');
INSERT INTO sys_dict_data VALUES (50, 3, '待审�?, 'waiting', 'wf_task_status', '', 'primary', 'N', 103, 1, NOW(), NULL, NULL, '待审�?);
INSERT INTO sys_dict_data VALUES (51, 4, '作废', 'invalid', 'wf_task_status', '', 'danger', 'N', 103, 1, NOW(), NULL, NULL, '作废');
INSERT INTO sys_dict_data VALUES (52, 5, '退�?, 'back', 'wf_task_status', '', 'danger', 'N', 103, 1, NOW(), NULL, NULL, '退�?);
INSERT INTO sys_dict_data VALUES (53, 6, '终止', 'termination', 'wf_task_status', '', 'danger', 'N', 103, 1, NOW(), NULL, NULL, '终止');
INSERT INTO sys_dict_data VALUES (54, 7, '转办', 'transfer', 'wf_task_status', '', 'primary', 'N', 103, 1, NOW(), NULL, NULL, '转办');
INSERT INTO sys_dict_data VALUES (55, 8, '委托', 'depute', 'wf_task_status', '', 'primary', 'N', 103, 1, NOW(), NULL, NULL, '委托');
INSERT INTO sys_dict_data VALUES (56, 9, '抄�?, 'copy', 'wf_task_status', '', 'primary', 'N', 103, 1, NOW(), NULL, NULL, '抄�?);
INSERT INTO sys_dict_data VALUES (57, 10, '加签', 'sign', 'wf_task_status', '', 'primary', 'N', 103, 1, NOW(), NULL, NULL, '加签');
INSERT INTO sys_dict_data VALUES (58, 11, '减签', 'sign_off', 'wf_task_status', '', 'danger', 'N', 103, 1, NOW(), NULL, NULL, '减签');
INSERT INTO sys_dict_data VALUES (59, 11, '超时', 'timeout', 'wf_task_status', '', 'danger', 'N', 103, 1, NOW(), NULL, NULL, '超时');

