
/*
 * 开源社区管理系统数据库脚本
 * 
 * 版本: 1.0.0
 * 日期: 2025-07-23

 * 
 * 说明:
 * 1. 本脚本创建开源社区管理系统所需的所有表结构
 * 2. 不包含外键约束，关联关系在代码层面维护
 */

-- ----------------------------
-- 1. 项目管理相关表
-- ----------------------------

-- 项目表
CREATE TABLE `os_project` (
  `project_id` bigint NOT NULL AUTO_INCREMENT COMMENT '项目ID',
  `project_name` varchar(100) NOT NULL COMMENT '项目名称',
  `project_code` varchar(50) NOT NULL COMMENT '项目代号',
  `description` text COMMENT '项目描述',
  `repository_url` varchar(255) COMMENT '代码仓库URL',
  `website_url` varchar(255) COMMENT '项目网站',
  `logo_url` varchar(255) COMMENT '项目Logo',
  `status` char(1) DEFAULT '0' COMMENT '状态（0草稿 1审核中 2已通过 3已拒绝 4已暂停）',
  `create_dept` bigint COMMENT '创建部门',
  `create_by` bigint COMMENT '创建者',
  `create_time` datetime COMMENT '创建时间',
  `update_by` bigint COMMENT '更新者',
  `update_time` datetime COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`project_id`),
  INDEX `idx_project_status` (`status`),
  INDEX `idx_project_create_by` (`create_by`),
  INDEX `idx_project_create_dept` (`create_dept`)
) COMMENT='开源项目表';

ALTER TABLE os_project MODIFY COLUMN project_code varchar(50) NULL COMMENT '项目代号';
ALTER TABLE os_project ADD COLUMN tech_stack VARCHAR(500) COMMENT '技术栈';
ALTER TABLE os_project ADD COLUMN programming_language VARCHAR(200) COMMENT '编程语言';
ALTER TABLE os_project ADD COLUMN star_count INT DEFAULT 0 COMMENT 'Star数';
ALTER TABLE os_project ADD COLUMN fork_count INT DEFAULT 0 COMMENT 'Fork数';
ALTER TABLE os_project ADD COLUMN core_contributors TEXT COMMENT '核心贡献者';
ALTER TABLE os_project ADD COLUMN contact_info VARCHAR(500) COMMENT '联系方式';
ALTER TABLE os_project ADD COLUMN version_info VARCHAR(100) COMMENT '版本信息';
ALTER TABLE os_project ADD COLUMN issues_count INT DEFAULT 0 COMMENT 'Issues数';
ALTER TABLE os_project ADD COLUMN pr_count INT DEFAULT 0 COMMENT 'PR数';
ALTER TABLE os_project ADD COLUMN readme_url VARCHAR(500) COMMENT 'README链接';
ALTER TABLE os_project ADD COLUMN wiki_url VARCHAR(500) COMMENT 'Wiki链接';
ALTER TABLE os_project ADD COLUMN api_doc_url VARCHAR(500) COMMENT 'API文档链接';
ALTER TABLE os_project ADD COLUMN last_commit_time DATETIME COMMENT '最后提交时间';

-- 项目审核表
CREATE TABLE `os_project_audit` (
  `audit_id` bigint NOT NULL AUTO_INCREMENT COMMENT '审核ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `audit_status` char(1) NOT NULL COMMENT '审核状态（0待审核 1通过 2拒绝 3退回修改）',
  `audit_user` bigint COMMENT '审核人',
  `audit_time` datetime COMMENT '审核时间',
  `audit_opinion` varchar(500) COMMENT '审核意见',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`audit_id`),
  INDEX `idx_audit_project` (`project_id`),
  INDEX `idx_audit_user` (`audit_user`)
) COMMENT='项目审核表';


ALTER TABLE os_project_audit 
ADD COLUMN create_dept bigint DEFAULT NULL COMMENT '创建部门',
ADD COLUMN create_by bigint DEFAULT NULL COMMENT '创建者',
ADD COLUMN update_by bigint DEFAULT NULL COMMENT '更新者';


-- 孵化阶段表
CREATE TABLE `os_project_phase` (
  `phase_id` bigint NOT NULL AUTO_INCREMENT COMMENT '阶段ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `phase_name` varchar(50) NOT NULL COMMENT '阶段名称',
  `phase_code` varchar(20) NOT NULL COMMENT '阶段代码',
  `description` varchar(500) COMMENT '阶段描述',
  `start_time` datetime COMMENT '开始时间',
  `end_time` datetime COMMENT '结束时间',
  `status` char(1) DEFAULT '0' COMMENT '状态（0进行中 1已完成 2已取消）',
  `create_by` bigint COMMENT '创建者',
  `create_time` datetime COMMENT '创建时间',
  `update_by` bigint COMMENT '更新者',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`phase_id`),
  INDEX `idx_phase_project` (`project_id`)
) COMMENT='项目孵化阶段表';

-- 项目文档表
CREATE TABLE `os_project_document` (
  `document_id` bigint NOT NULL AUTO_INCREMENT COMMENT '文档ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `title` varchar(100) NOT NULL COMMENT '文档标题',
  `content` text COMMENT '文档内容',
  `doc_type` char(1) DEFAULT '0' COMMENT '文档类型（0普通文档 1API文档 2使用手册 3开发指南）',
  `version` varchar(20) COMMENT '版本号',
  `status` char(1) DEFAULT '0' COMMENT '状态（0草稿 1已发布 2已归档）',
  `create_by` bigint COMMENT '创建者',
  `create_time` datetime COMMENT '创建时间',
  `update_by` bigint COMMENT '更新者',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`document_id`),
  INDEX `idx_document_project` (`project_id`)
) COMMENT='项目文档表';

-- ----------------------------
-- 2. 人员管理相关表
-- ----------------------------

-- 成员表
CREATE TABLE `os_member` (
  `member_id` bigint NOT NULL AUTO_INCREMENT COMMENT '成员ID',
  `user_id` bigint COMMENT '关联用户ID',
  `member_name` varchar(50) NOT NULL COMMENT '成员名称',
  `nickname` varchar(50) COMMENT '昵称',
  `avatar` varchar(255) COMMENT '头像',
  `email` varchar(100) COMMENT '邮箱',
  `github_id` varchar(100) COMMENT 'GitHub ID',
  `gitee_id` varchar(100) COMMENT 'Gitee ID',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1禁用）',
  `join_time` datetime COMMENT '加入时间',
  `create_dept` bigint COMMENT '创建部门',
  `create_by` bigint COMMENT '创建者',
  `create_time` datetime COMMENT '创建时间',
  `update_by` bigint COMMENT '更新者',
  `update_time` datetime COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`member_id`),
  INDEX `idx_member_user` (`user_id`),
  INDEX `idx_member_status` (`status`),
  INDEX `idx_member_create_dept` (`create_dept`)
) COMMENT='社区成员表';

-- 技能表
CREATE TABLE `os_skill` (
  `skill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '技能ID',
  `skill_name` varchar(50) NOT NULL COMMENT '技能名称',
  `skill_type` char(1) DEFAULT '0' COMMENT '技能类型（0编程语言 1框架 2工具 3领域知识）',
  `description` varchar(500) COMMENT '技能描述',
  `create_by` bigint COMMENT '创建者',
  `create_time` datetime COMMENT '创建时间',
  `update_by` bigint COMMENT '更新者',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`skill_id`)
) COMMENT='技能表';

-- 成员技能关联表
CREATE TABLE `os_member_skill` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `member_id` bigint NOT NULL COMMENT '成员ID',
  `skill_id` bigint NOT NULL COMMENT '技能ID',
  `proficiency` int DEFAULT '1' COMMENT '熟练度（1-5）',
  `create_time` datetime COMMENT '创建时间',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`id`),
  INDEX `idx_member_skill` (`member_id`, `skill_id`)
) COMMENT='成员技能关联表';

-- 项目成员关联表
CREATE TABLE `os_project_member` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `member_id` bigint NOT NULL COMMENT '成员ID',
  `role` char(1) DEFAULT '0' COMMENT '角色（0普通成员 1项目负责人 2核心开发者 3维护者 4贡献者）',
  `join_time` datetime COMMENT '加入时间',
  `permission_level` int DEFAULT '1' COMMENT '权限级别（1-5，数字越大权限越高）',
  `is_active` char(1) DEFAULT '1' COMMENT '是否活跃（0非活跃 1活跃）',
  `contribution_score` int DEFAULT '0' COMMENT '贡献度评分（1-100）',
  `remark` varchar(500) COMMENT '备注',
  `create_dept` bigint COMMENT '创建部门',
  `create_by` bigint COMMENT '创建者',
  `create_time` datetime COMMENT '创建时间',
  `update_by` bigint COMMENT '更新者',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_project_member` (`project_id`, `member_id`),
  INDEX `idx_project_member` (`project_id`, `member_id`),
  INDEX `idx_member_project` (`member_id`, `project_id`),
  INDEX `idx_role` (`role`),
  INDEX `idx_is_active` (`is_active`)
) COMMENT='项目成员关联表';

-- 贡献记录表
CREATE TABLE `os_contribution` (
  `contribution_id` bigint NOT NULL AUTO_INCREMENT COMMENT '贡献ID',
  `member_id` bigint NOT NULL COMMENT '成员ID',
  `project_id` bigint NOT NULL COMMENT '项目ID',
  `contribution_type` char(1) DEFAULT '0' COMMENT '贡献类型（0代码提交 1问题修复 2文档贡献 3回答问题 4其他）',
  `content` varchar(500) COMMENT '贡献内容',
  `url` varchar(255) COMMENT '相关链接',
  `contribution_time` datetime COMMENT '贡献时间',
  `points` int DEFAULT '0' COMMENT '贡献点数',
  `create_dept` bigint COMMENT '创建部门',
  `create_by` bigint COMMENT '创建者',
  `create_time` datetime COMMENT '创建时间',
  `update_by` bigint COMMENT '更新者',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`contribution_id`),
  INDEX `idx_contribution_member` (`member_id`),
  INDEX `idx_contribution_project` (`project_id`),
  INDEX `idx_contribution_create_dept` (`create_dept`)
) COMMENT='贡献记录表';

-- ----------------------------
-- 3. 社区管理相关表
-- ----------------------------

-- 社区活动表
CREATE TABLE `os_activity` (
  `activity_id` bigint NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `activity_name` varchar(100) NOT NULL COMMENT '活动名称',
  `description` text COMMENT '活动描述',
  `activity_type` char(1) DEFAULT '0' COMMENT '活动类型（0线上活动 1线下活动 2混合活动）',
  `start_time` datetime COMMENT '开始时间',
  `end_time` datetime COMMENT '结束时间',
  `location` varchar(255) COMMENT '活动地点',
  `online_url` varchar(255) COMMENT '线上链接',
  `status` char(1) DEFAULT '0' COMMENT '状态（0筹备中 1进行中 2已结束 3已取消）',
  `create_by` bigint COMMENT '创建者',
  `create_time` datetime COMMENT '创建时间',
  `update_by` bigint COMMENT '更新者',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`activity_id`),
  INDEX `idx_activity_status` (`status`),
  INDEX `idx_activity_time` (`start_time`, `end_time`)
) COMMENT='社区活动表';

-- 社区公告表
CREATE TABLE `os_announcement` (
  `announcement_id` bigint NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `title` varchar(100) NOT NULL COMMENT '公告标题',
  `content` text COMMENT '公告内容',
  `announcement_type` char(1) DEFAULT '0' COMMENT '公告类型（0普通公告 1重要公告 2紧急公告）',
  `status` char(1) DEFAULT '0' COMMENT '状态（0草稿 1已发布 2已过期）',
  `publish_time` datetime COMMENT '发布时间',
  `expire_time` datetime COMMENT '过期时间',
  `create_by` bigint COMMENT '创建者',
  `create_time` datetime COMMENT '创建时间',
  `update_by` bigint COMMENT '更新者',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`announcement_id`),
  INDEX `idx_announcement_status` (`status`),
  INDEX `idx_announcement_time` (`publish_time`, `expire_time`)
) COMMENT='社区公告表';

-- 社区资源表
CREATE TABLE `os_resource` (
  `resource_id` bigint NOT NULL AUTO_INCREMENT COMMENT '资源ID',
  `resource_name` varchar(100) NOT NULL COMMENT '资源名称',
  `resource_type` char(1) DEFAULT '0' COMMENT '资源类型（0文档 1视频 2工具 3其他）',
  `description` varchar(500) COMMENT '资源描述',
  `url` varchar(255) COMMENT '资源链接',
  `file_path` varchar(255) COMMENT '文件路径',
  `status` char(1) DEFAULT '0' COMMENT '状态（0正常 1禁用）',
  `create_by` bigint COMMENT '创建者',
  `create_time` datetime COMMENT '创建时间',
  `update_by` bigint COMMENT '更新者',
  `update_time` datetime COMMENT '更新时间',
  PRIMARY KEY (`resource_id`),
  INDEX `idx_resource_type` (`resource_type`),
  INDEX `idx_resource_status` (`status`)
) COMMENT='社区资源表';

-- ----------------------------
-- 4. 统计分析相关表
-- ----------------------------

-- 统计数据表
CREATE TABLE `os_statistics` (
  `stat_id` bigint NOT NULL AUTO_INCREMENT COMMENT '统计ID',
  `stat_type` varchar(50) NOT NULL COMMENT '统计类型',
  `stat_key` varchar(100) NOT NULL COMMENT '统计键',
  `stat_value` varchar(255) NOT NULL COMMENT '统计值',
  `stat_time` datetime COMMENT '统计时间',
  `create_time` datetime COMMENT '创建时间',
  PRIMARY KEY (`stat_id`),
  INDEX `idx_stat_type` (`stat_type`),
  INDEX `idx_stat_time` (`stat_time`)
) COMMENT='统计数据表';

-- 修改sys_oss表，添加项目关联和文档类型字段
ALTER TABLE sys_oss
ADD COLUMN size bigint DEFAULT NULL COMMENT '文件大小（字节）',
ADD COLUMN file_type varchar(20) DEFAULT 'other' COMMENT '文档类型（logo: Logo图片, requirement: 需求文档, help: 帮助文档, design: 设计文档, api: 接口文档, other: 其他文档）';

-- 添加外键约束
ALTER TABLE sys_oss
ADD CONSTRAINT fk_oss_project FOREIGN KEY (project_id) REFERENCES os_project (project_id) ON DELETE SET NULL;
