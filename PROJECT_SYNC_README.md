# 项目数据同步功能说明

## 功能概述

本功能实现了从Git仓库（Gitee/GitHub）自动同步项目动态数据的能力，包括Star数、Fork数、Issues数、Watch数等实时统计信息，并将这些数据存储到数据库中以提供给首页展示。

## 主要特性

### 1. 自动数据同步
- 支持Gitee和GitHub仓库数据同步
- 自动解析仓库URL并调用对应平台API
- 获取实时的Star、Fork、Issues、Watch数据
- 支持单个项目和批量项目同步

### 2. 数据库持久化
- 同步的数据直接存储到数据库
- 记录最后同步时间便于监控
- 支持增量更新，避免重复同步

### 3. 前端界面集成
- 在项目列表页面添加"同步数据"按钮
- 同步完成后自动刷新项目列表和首页统计
- 实时反馈同步进度和结果

### 4. 首页统计更新
- 同步项目数据后自动触发首页数据刷新
- 统计数据实时更新
- 优化布局确保六个统计卡片在一行显示

## 数据库变更

### 新增字段

执行以下SQL添加新字段：

```sql
-- 添加Watch数（关注数）字段
ALTER TABLE os_project ADD COLUMN watch_count INT DEFAULT 0 COMMENT 'Watch数（关注数）';

-- 添加项目大小字段
ALTER TABLE os_project ADD COLUMN project_size VARCHAR(50) DEFAULT '0' COMMENT '项目大小（KB）';

-- 添加最后同步时间字段  
ALTER TABLE os_project ADD COLUMN last_sync_time DATETIME NULL COMMENT '最后同步时间';

-- 创建索引以提高同步查询性能
CREATE INDEX idx_project_repository_url ON os_project(repository_url);
CREATE INDEX idx_project_last_sync_time ON os_project(last_sync_time);
```

## API接口

### 1. 同步所有项目数据
```
POST /osc/project/sync
```
- 同步所有有代码仓库地址的项目
- 返回更新的项目数量
- 需要 `osc:project:sync` 权限

### 2. 同步单个项目数据  
```
POST /osc/project/sync/{projectId}
```
- 同步指定项目的数据
- 返回同步结果状态
- 需要 `osc:project:sync` 权限

## 使用方式

### 管理员操作
1. 进入项目列表页面
2. 点击"同步数据"按钮
3. 系统自动同步所有项目数据
4. 同步完成后刷新首页统计

### 技术实现

#### 后端实现
- **ProjectController**: 提供同步API接口
- **ProjectService**: 实现同步业务逻辑
- **RestTemplate**: 调用Git平台API
- **ObjectMapper**: 解析JSON响应数据

#### 前端实现
- **项目列表页面**: 添加同步按钮和进度提示
- **首页组件**: 监听数据刷新事件
- **RealtimeStats**: 优化统计卡片布局

## 支持的Git平台

### Gitee
- API地址: `https://gitee.com/api/v5/repos/{owner}/{repo}`
- 支持获取: star_count, forks_count, open_issues_count, watchers_count

### GitHub  
- API地址: `https://api.github.com/repos/{owner}/{repo}`
- 支持获取: stargazers_count, forks_count, open_issues_count, watchers_count

## 错误处理

### 常见错误及解决方案

1. **API频率限制**
   - 添加1秒延迟避免频繁请求
   - 支持重试机制

2. **仓库URL解析失败**
   - 记录错误日志但不中断整体同步
   - 支持多种URL格式

3. **网络连接超时**
   - 设置10秒连接超时，30秒读取超时
   - 记录失败项目便于后续重试

## 性能优化

1. **批量处理**: 支持批量同步多个项目
2. **增量更新**: 只更新有变化的字段
3. **索引优化**: 为常用查询字段添加索引
4. **缓存机制**: 前端缓存数据减少重复请求

## 监控和日志

- 详细的同步日志记录
- 同步成功/失败统计
- 最后同步时间追踪
- 前端操作反馈

## 权限控制

- 需要 `osc:project:sync` 权限才能执行同步操作
- 只有管理员可以触发数据同步
- 普通用户只能查看同步结果

## 未来扩展

1. **定时同步**: 可配置定时任务自动同步
2. **更多平台**: 支持GitLab等其他平台
3. **数据分析**: 基于历史数据生成趋势分析
4. **告警机制**: 同步失败时发送告警通知