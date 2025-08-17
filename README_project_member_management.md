# 项目成员关联管理功能

## 功能概述

项目成员关联管理是Dromara开源社区一体化管理系统的核心功能之一，用于管理项目与成员之间的关联关系，包括角色分配、权限管理、贡献度评估等功能。

## 主要特性

### 1. 完整的CRUD操作
- **新增项目成员**：支持单个或批量添加项目成员
- **修改成员信息**：更新成员角色、权限级别、活跃状态等
- **删除成员**：支持单个或批量删除项目成员
- **查询成员**：支持多条件查询和分页显示

### 2. 角色权限管理
- **5种角色类型**：
  - 普通成员（权限级别1）
  - 项目负责人（权限级别5）
  - 核心开发者（权限级别4）
  - 维护者（权限级别3）
  - 贡献者（权限级别2）

### 3. 贡献度评估
- **贡献度评分**：1-100分的评分体系
- **贡献统计**：显示贡献记录数和总贡献点数
- **可视化展示**：进度条和颜色编码显示贡献度

### 4. 高级功能
- **批量操作**：支持批量添加、删除、修改成员
- **数据导入导出**：支持Excel格式的数据导入导出
- **活跃状态管理**：可控制成员的活跃状态
- **跨项目关联**：支持一个成员参与多个项目

## 技术架构

### 后端架构
```
ruoyi-modules/ruoyi-osc/
├── domain/
│   ├── ProjectMember.java          # 实体类
│   ├── bo/ProjectMemberBo.java     # 业务对象
│   └── vo/ProjectMemberVo.java     # 视图对象
├── mapper/
│   └── ProjectMemberMapper.java    # 数据访问层
├── service/
│   ├── IProjectMemberService.java  # 服务接口
│   └── impl/ProjectMemberServiceImpl.java # 服务实现
├── controller/
│   └── ProjectMemberController.java # 控制器
└── listener/
    └── ProjectMemberImportListener.java # 导入监听器
```

### 前端架构
```
plus-ui/src/
├── api/osc/projectMember/
│   ├── index.ts                    # API接口
│   └── types.ts                    # 类型定义
└── views/osc/projectMember/
    └── index.vue                   # 管理页面
```

## 数据库设计

### 核心表结构
```sql
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
```

## API接口

### 基础CRUD接口
- `GET /osc/projectMember/list` - 查询项目成员列表
- `GET /osc/projectMember/{id}` - 获取项目成员详情
- `POST /osc/projectMember` - 新增项目成员
- `PUT /osc/projectMember` - 修改项目成员
- `DELETE /osc/projectMember/{ids}` - 删除项目成员

### 业务接口
- `GET /osc/projectMember/project/{projectId}` - 根据项目ID查询成员
- `GET /osc/projectMember/member/{memberId}` - 根据成员ID查询项目
- `POST /osc/projectMember/batchAdd` - 批量添加项目成员
- `POST /osc/projectMember/removeMember` - 移除项目成员
- `POST /osc/projectMember/updateRole` - 更新成员角色
- `POST /osc/projectMember/updateActiveStatus` - 更新活跃状态
- `GET /osc/projectMember/contributionScore` - 计算贡献度评分
- `GET /osc/projectMember/stats/{projectId}` - 获取统计信息

### 数据导入导出
- `POST /osc/projectMember/export` - 导出项目成员数据
- `POST /osc/projectMember/importData` - 导入项目成员数据
- `POST /osc/projectMember/importTemplate` - 下载导入模板

## 前端功能

### 主要页面组件
1. **搜索区域**：支持按项目、成员、角色、活跃状态、时间范围等条件搜索
2. **数据表格**：展示项目成员信息，支持排序、分页、多选等操作
3. **新增/编辑对话框**：完整的表单验证和数据提交
4. **角色修改对话框**：快速修改成员角色
5. **导入对话框**：支持Excel文件导入

### 特色功能
- **可视化展示**：头像、标签、进度条等丰富的UI组件
- **实时状态切换**：活跃状态开关，实时更新
- **贡献度可视化**：进度条和颜色编码显示贡献度
- **角色标签**：不同颜色标签区分角色类型
- **权限级别评分**：星级评分显示权限级别

## 部署说明

### 1. 数据库初始化
```sql
-- 执行数据库脚本
source script/sql/os_community.sql;
source script/sql/dict_data.sql;
source script/sql/dromara_project_members.sql;
```

### 2. 后端编译
```bash
mvn clean compile -DskipTests
```

### 3. 前端启动
```bash
cd plus-ui
npm install
npm run dev
```

### 4. 访问地址
- 前端地址：`http://localhost:3000`
- 后端地址：`http://localhost:8080`

## 使用指南

### 1. 添加项目成员
1. 点击"新增"按钮
2. 选择项目和成员
3. 设置角色和权限级别
4. 配置活跃状态和贡献度评分
5. 保存提交

### 2. 批量操作
1. 选择多个项目成员
2. 点击相应的批量操作按钮
3. 确认操作

### 3. 角色管理
1. 在表格中点击"角色"按钮
2. 选择新的角色
3. 确认修改

### 4. 数据导入
1. 点击"导入"按钮
2. 下载模板文件
3. 填写数据后上传
4. 确认导入

## 扩展开发

### 1. 自定义角色
可以在数据字典中添加新的角色类型：
```sql
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark) 
VALUES (20006, 6, '自定义角色', '5', 'osc_project_member_role', '', 'info', 'N', '0', 1, NOW(), NULL, NULL, '自定义角色描述');
```

### 2. 贡献度算法
可以在`ProjectMemberServiceImpl.calculateContributionScore`方法中实现自定义的贡献度计算算法。

### 3. 权限扩展
可以基于`permission_level`字段实现更细粒度的权限控制。

## 注意事项

1. **唯一性约束**：项目ID和成员ID的组合必须唯一
2. **权限控制**：不同角色有不同的操作权限
3. **数据一致性**：删除项目或成员时需要考虑关联数据
4. **性能优化**：大量数据时建议使用分页查询
5. **数据备份**：重要数据建议定期备份

## 常见问题

### Q1: 如何修改成员角色？
A1: 在表格中点击"角色"按钮，选择新角色后确认即可。

### Q2: 如何批量添加成员？
A2: 使用"批量添加"功能，选择项目、成员列表和角色后提交。

### Q3: 贡献度评分是如何计算的？
A3: 目前使用默认算法，可以根据实际需求在服务层自定义计算逻辑。

### Q4: 如何导出数据？
A4: 点击"导出"按钮，系统会生成Excel文件供下载。

### Q5: 如何导入数据？
A5: 下载模板文件，填写数据后通过"导入"功能上传。
