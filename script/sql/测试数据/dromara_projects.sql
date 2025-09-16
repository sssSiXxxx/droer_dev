 -- Dromara社区项目数据
-- 基于Dromara社区实际项目创建示例数据

-- 插入Dromara社区项目数据
INSERT INTO os_project (
  project_name,
  description,
  repository_url,
  website_url,
  logo_url,
  status,
  create_dept,
  create_by,
  create_time,
  remark
) VALUES 
-- 1. Hutool - Java工具类库
(
  'Hutool',
  '🍬A set of tools that keep Java sweet. Hutool是一个小而全的Java工具类库，通过静态方法封装，降低相关API的学习成本，提高工作效率，使Java语言也可以"甜甜的"。',
  'https://gitee.com/dromara/hutool',
  'https://hutool.cn',
  'https://gitee.com/dromara/hutool/avatar',
  '2', -- 已通过
  103,
  1,
  NOW(),
  'Java工具类库，提供丰富的工具方法，简化开发工作'
),

-- 2. Sa-Token - 权限认证框架
(
  'Sa-Token',
  '这可能是史上功能最全的Java权限认证框架！Sa-Token 是一个轻量级 Java 权限认证框架，主要解决：登录认证、权限认证、Session会话、单点登录、OAuth2.0、微服务网关鉴权等一系列权限相关问题。',
  'https://gitee.com/dromara/Sa-Token',
  'https://sa-token.dev33.cn',
  'https://gitee.com/dromara/Sa-Token/avatar',
  '2', -- 已通过
  103,
  1,
  NOW(),
  '轻量级Java权限认证框架，功能全面，使用简单'
),

-- 3. Forest - HTTP客户端框架
(
  'Forest',
  '🌲轻量级HTTP客户端框架Forest，让Java发送HTTP请求不再难。Forest是一个开源的Java HTTP客户端框架，它能够将HTTP的请求调用绑定到Java接口上，从而简化HTTP请求的调用过程。',
  'https://gitee.com/dromara/forest',
  'https://forest.dtflyx.com',
  'https://gitee.com/dromara/forest/avatar',
  '2', -- 已通过
  103,
  1,
  NOW(),
  '轻量级HTTP客户端框架，简化HTTP请求调用'
),

-- 4. MaxKey - 身份管理平台
(
  'MaxKey',
  '🔐业界领先的身份管理和访问管理产品MaxKey，提供单点登录(SSO)、身份认证、权限管理、用户管理、组织管理、应用管理、安全审计等功能。',
  'https://gitee.com/dromara/MaxKey',
  'https://www.maxkey.top',
  'https://gitee.com/dromara/MaxKey/avatar',
  '2', -- 已通过
  103,
  1,
  NOW(),
  '企业级身份管理和访问管理平台，功能完善'
),

-- 5. Easy-Es - ElasticSearch ORM框架
(
  'Easy-Es',
  '🔗傻瓜级ElasticSearch搜索引擎ORM框架Easy-Es，让ElasticSearch的使用变得简单，就像使用MyBatis一样简单。',
  'https://gitee.com/dromara/easy-es',
  'https://easy-es.cn',
  'https://gitee.com/dromara/easy-es/avatar',
  '1', -- 审核中
  103,
  1,
  NOW(),
  'ElasticSearch ORM框架，简化ES操作，提升开发效率'
);

-- 验证数据插入
SELECT 
  project_id,
  project_name,
  status,
  create_time
FROM os_project 
ORDER BY project_id;