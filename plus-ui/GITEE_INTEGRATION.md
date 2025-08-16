# Gitee 集成功能说明

## 功能概述

本系统已集成Gitee API，实现了用户贡献记录的可视化展示和管理功能。主要特性包括：

### 1. Gitee账号关联
- 用户可以在个人信息中添加Gitee账号
- 支持一键同步Gitee用户信息
- 自动获取用户头像、仓库数量等基本信息

### 2. 贡献记录同步
- 自动同步Gitee上的贡献记录（提交、Issues、Pull Requests等）
- 支持手动添加贡献记录
- 贡献记录分类管理（代码提交、问题修复、文档贡献等）

### 3. 贡献可视化
- 贡献日历热力图（类似GitHub的贡献图）
- 贡献统计面板
- 贡献趋势分析

## 技术实现

### API集成
- 使用Gitee官方API v5
- 支持用户信息、仓库、事件等数据获取
- 错误处理和重试机制

### 数据库设计
- `os_member` 表：存储成员信息，关联Gitee账号
- `os_contribution` 表：存储贡献记录
- `os_project` 表：项目信息

### 前端组件
- `ContributionCalendar.vue`：贡献日历组件
- `Gitee API`：API调用封装
- 用户管理界面增强

## 使用说明

### 1. 添加Gitee账号
1. 进入用户管理界面
2. 编辑用户信息
3. 在"Gitee账号"字段输入用户名
4. 点击"同步"按钮获取Gitee信息

### 2. 同步贡献记录
1. 在用户信息页面点击"同步Gitee"按钮
2. 系统自动获取最近的贡献记录
3. 贡献记录会显示在贡献统计面板中

### 3. 查看贡献日历
- 贡献日历显示过去一年的贡献情况
- 颜色深浅表示贡献数量
- 点击日期可查看当天的详细贡献

### 4. 手动管理贡献
1. 点击"管理贡献"按钮
2. 可以添加、编辑、删除贡献记录
3. 支持设置贡献类型、内容、点数等

## API接口

### Gitee API
- `GET /api/v5/users/{username}` - 获取用户信息
- `GET /api/v5/users/{username}/events` - 获取用户事件
- `GET /api/v5/users/{username}/repos` - 获取用户仓库

### 本地API
- `POST /osc/member/syncGiteeInfo` - 同步Gitee信息
- `GET /osc/contribution/list` - 获取贡献记录
- `POST /osc/contribution/add` - 添加贡献记录
- `PUT /osc/contribution/edit` - 编辑贡献记录
- `DELETE /osc/contribution/remove/{id}` - 删除贡献记录

## 配置说明

### 环境变量
```bash
# Gitee API配置（可选）
VITE_GITEE_API_TOKEN=your_gitee_token
```

### 权限配置
- `system:user:edit` - 编辑用户信息
- `osc:contribution:add` - 添加贡献记录
- `osc:contribution:edit` - 编辑贡献记录
- `osc:contribution:remove` - 删除贡献记录

## 测试页面

访问 `/system/gitee-test` 可以测试Gitee API功能：
- 测试用户信息获取
- 测试贡献记录获取
- 测试贡献日历显示

## 注意事项

1. **API限制**：Gitee API有访问频率限制，建议合理使用
2. **数据同步**：贡献记录同步可能需要一定时间
3. **隐私保护**：只获取公开的Gitee信息
4. **错误处理**：网络错误或API限制时会显示相应提示

## 扩展功能

### 计划中的功能
- [ ] 贡献排行榜
- [ ] 贡献趋势图表
- [ ] 团队贡献统计
- [ ] 贡献报告导出
- [ ] 自动同步定时任务

### 自定义配置
- [ ] 贡献点数规则配置
- [ ] 贡献类型自定义
- [ ] 日历显示样式配置
- [ ] 同步频率设置

## 技术支持

如有问题，请参考：
- [Gitee API文档](https://gitee.com/api/v5/swagger)
- [项目文档](https://gitee.com/dromara/open-giteye-api)
- [系统日志](系统管理 > 日志管理)
