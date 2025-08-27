-- Dromara社区完整数据导入脚本
-- 生成时间: 2025/8/26 21:27:08
-- GitHub项目: 98 个
-- 社区成员: 104 位 (如API失败则为核心成员)

-- 清空现有数据 (谨慎执行)
-- DELETE FROM osc_contributions;
-- DELETE FROM os_project_member;  
-- DELETE FROM osc_members;
-- DELETE FROM osc_projects;

-- 项目数据插入
INSERT INTO osc_projects (project_name, description, github_url, homepage, language, category, status, stars, forks, issues, created_time, updated_time, topics, license) VALUES
('domain-admin', '域名SSL证书监测平台、SSL证书申请自动续签。Domain and SSL Cert monitor System. ', 'https://github.com/dromara/domain-admin', 'https://mouday.github.io/domain-admin/', 'Python', '监控系统', 'active', 2273, 325, 89, '2022-09-23T08:54:53Z', '2025-08-26T12:56:36Z', 'domain,ssl,ssl-cert', 'MIT License'),
('Sa-Token', '一个轻量级 Java 权限认证框架，让鉴权变得简单、优雅！—— 登录认证、权限认证、分布式Session会话、微服务网关鉴权、单点登录、OAuth2.0', 'https://github.com/dromara/Sa-Token', 'https://sa-token.cc', 'Java', '权限认证', 'active', 18107, 2811, 121, '2020-02-03T10:34:29Z', '2025-08-26T12:42:56Z', 'aouth2,authorization,java,springcloud,sso,token', 'Apache License 2.0'),
('dante-cloud', 'Dante Cloud 国内首个支持阻塞式和响应式服务并行的微服务平台。采用领域驱动模型(DDD)设计思想，以「高质量代码、低安全漏洞」为核心，高度模块化和组件化设计，支持智能电视、IoT等物联网设备认证，满足国家三级等保要求、支持接口国密数字信封加解密等系列安全体系的多租户微服务解决方案。独创的“一套代码实现微服务和单体两种架构灵活切换”的企业级应用系统。可灵活切换 Spring Cloud Alibaba、Spring Cloud Tencent 和 Spring Cloud 原生组件。🔝 🔝 点个star 持续关注更新！', 'https://github.com/dromara/dante-cloud', 'https://www.herodotus.vip', 'Java', '权限认证', 'active', 862, 192, 2, '2021-05-19T00:14:32Z', '2025-08-26T11:13:09Z', 'dante-cloud,docker,elk,jetcache,knife4j,kubernates,microservice,nacos,postgresql,seata,sentinel,skywalking,spring-authorization-server,spring-boot,spring-cloud,spring-cloud-alibaba,spring-cloud-gateway,spring-cloud-oauth2,spring-data-jpa,spring-security-oauth2', 'Apache License 2.0'),
('RuoYi-Vue-Plus', '基于RuoYi-Vue集成 Lombok+Mybatis-Plus+Undertow+knife4j+Hutool+Feign 重写所有原生业务 定期与RuoYi-Vue同步', 'https://github.com/dromara/RuoYi-Vue-Plus', 'https://plus-doc.dromara.org', 'Java', '工具库', 'active', 1656, 464, 2, '2021-06-23T06:32:20Z', '2025-08-26T09:51:00Z', 'docker,mybatis,oss,springboot,vue', 'MIT License'),
('dax-pay', '免费开源的支付网关，支持支付宝、微信、云闪付等通道，提供收单、退款、对账、分账等功能，通过HTTP方式进行调用，不与其他系统产生耦合关联，可以快速集成到各种系统中，提供可视化界面进行管理，便于实现统一的支付信息管理。', 'https://github.com/dromara/dax-pay', 'https://daxpay.dromara.org/', 'Java', 'HTTP客户端', 'active', 663, 128, 2, '2024-02-16T13:08:06Z', '2025-08-26T09:26:16Z', '', 'Apache License 2.0'),
('cubic', '一站式问题定位平台，分布式实例监控、线程栈监控、线程池监控、动态arthas命令集、依赖分析等等等，助你快速定位问题', 'https://github.com/dromara/cubic', 'http://cubic.jiagoujishu.com', 'Java', '监控系统', 'active', 515, 100, 2, '2020-05-20T03:14:35Z', '2025-08-26T09:22:00Z', 'agent,monitor', 'Apache License 2.0'),
('forest', 'A high-level and lightweight declarative HTTP client framework for Java. it makes sending HTTP requests in Java easier.', 'https://github.com/dromara/forest', '', 'Java', 'HTTP客户端', 'active', 1852, 231, 78, '2017-04-21T10:53:29Z', '2025-08-26T09:19:41Z', 'declarative-http-client,fegin,http,http-client,https,java-http-client,request,rest,restful,retrofit,socks', 'MIT License'),
('dynamic-tp', '🔥🔥🔥轻量级动态线程池，内置监控告警功能，集成三方中间件线程池管理，基于主流配置中心（已支持Nacos、Apollo，Zookeeper、Consul、Etcd，可通过SPI自定义实现）。Lightweight dynamic threadpool, with monitoring and alarming functions, base on popular config centers (already support Nacos、Apollo、Zookeeper、Consul, can be customized through SPI).', 'https://github.com/dromara/dynamic-tp', 'https://dynamictp.cn', 'Java', '监控系统', 'active', 4539, 853, 21, '2022-01-07T16:35:27Z', '2025-08-26T07:57:14Z', 'apollo,consul,dubbo,dynamic-threadpool,grpc,java,micrometer,monitor,nacos,rocketmq,spring,springboot,springcloud,thread,threadpool,threadpoolexecutor,zookeeper', 'Apache License 2.0'),
('liteflow', 'Lightweight, fast, stable, and programmable component-based rule engine/process engine. Component reuse, synchronous/asynchronous orchestration, dynamic orchestration, multi-language scripting support, complex nested rules, hot deployment, smooth refreshing. Let you improve your development efficiency!', 'https://github.com/dromara/liteflow', '', 'Java', '规则引擎', 'active', 3475, 477, 52, '2020-03-25T16:49:22Z', '2025-08-26T07:33:18Z', 'component,dsl,flow-engine,hot-reload,java-rule,java-rule-engine,rule-engine,rules-engine,workflow-engine', 'Apache License 2.0'),
('ujcms', 'Java开源网站内容管理系统(java cms)。使用SpringBoot、MyBatis、Spring Security、Lucene、FreeMarker、TypeScript、Vue3、ElementPlus等技术开发。', 'https://github.com/dromara/ujcms', 'https://www.ujcms.com', 'Java', '权限认证', 'active', 101, 46, 11, '2021-01-08T10:54:59Z', '2025-08-26T07:17:44Z', 'cms,element-plus,java,mybatis,vue3', 'Apache License 2.0'),
('orion-visor', '一款高颜值、现代化的自动化运维及轻量堡垒机，提供全面的服务器智能运维解决方案。支持资产管理分组、提供多协议访问(SSH、SFTP、RDP、VNC)、文件上传下载、在线编辑、命令批量执行、多主机文件分发和计划任务配置(通过 cron 表达式)等功能，确保高效安全的运维体验。适用于 Linux 和 Windows 系统的运维管理。', 'https://github.com/dromara/orion-visor', 'https://visor.orionsec.cn', 'Java', '其他', 'active', 912, 151, 8, '2023-06-20T08:01:55Z', '2025-08-26T06:55:49Z', 'ansible,devops,docker,java,jsch,linux,ops,rdp,sftp,shell,ssh,terminal,tty,tunnel,vnc,vue,webshell,xterm', 'Apache License 2.0'),
('yft-design', 'yft-design is a powerful, visually stunning online design tool built with Vue3, fabric.js, and Element Plus. 基于fabric.js的开源版【稿定设计】。一款美观且功能强大的在线设计工具，具备海报设计和图片编辑功能。适用于多种场景，如海报生成、电商产品图制作、文章长图设计、视频/公众号封面编辑等 。', 'https://github.com/dromara/yft-design', 'https://yft.design', 'TypeScript', '工具库', 'active', 1389, 279, 17, '2023-05-25T03:42:07Z', '2025-08-26T06:22:45Z', 'canvas-editor,clipper,element-plus,fabric-editor,fabricjs,image-crop,online-design,online-editor,pdf-editor,pdf-parser,poster-design,psd-editor,psd-parse,text2path,vue3-fabric', 'MIT License'),
('northstar', '国内最优秀的基于JAVA的AI开源量化交易平台，秒替文华、MC、金字塔。具备历史回放、策略研发、模拟交易、实盘交易等功能。兼顾全自动与半自动的使用场景。', 'https://github.com/dromara/northstar', '', 'Java', '量化交易', 'active', 444, 150, 0, '2021-07-24T03:46:00Z', '2025-08-26T06:07:56Z', 'btc,ctp,equity,futures,trader', 'GNU General Public License v3.0'),
('x-file-storage', '一行代码将文件存储到 本地、FTP、SFTP、WebDAV、谷歌云存储、阿里云OSS、华为云OBS、七牛云Kodo、腾讯云COS、百度云 BOS、又拍云USS、MinIO、 AWS S3、FastDFS、 Azure Blob Storage、金山云 KS3、美团云 MSS、京东云 OSS、天翼云 OOS、移动云 EOS、沃云 OSS、 网易数帆 NOS、Ucloud US3、青云 QingStor、平安云 OBS、首云 OSS、IBM COS、其它兼容 S3 协议的平台。后续即将支持 Samba、NFS', 'https://github.com/dromara/x-file-storage', 'https://x-file-storage.xuyanwu.cn/', 'Java', '微服务框架', 'active', 2057, 303, 12, '2020-12-16T06:38:58Z', '2025-08-26T05:28:54Z', 'bos,file-storage,file-upload,ftp,java,kodo,minio,oss,s3,sftp,spring,uss,webdav', 'Apache License 2.0'),
('lamp-cloud', 'lamp-cloud 支持jdk21、jdk17、jdk11、jdk8，ta基于 SpringCloud + SpringBoot 开发的微服务中后台快速开发平台，专注于多租户(SaaS架构)解决方案，亦可作为普通项目（非SaaS架构）的基础开发框架使用，目前已实现插拔式数据库隔离、SCHEMA隔离、字段隔离 等租户隔离方案。', 'https://github.com/dromara/lamp-cloud', 'http://tangyh.top', 'Java', '微服务框架', 'active', 5679, 1750, 4, '2018-01-17T02:51:04Z', '2025-08-26T04:46:20Z', 'admin,cloud,eureka,gateway,hystrix,java,jwt,mybatis,nacos,seata,spring,spring-cloud,springboot,springcloud,xss,zuul', 'Apache License 2.0'),
('wgai', '开箱即用的JAVA AI 图片、视频语音识别&OCR平台AI合集包含旦不仅限于(车牌识别、安全帽识别、开门关门、常用类物识别等) 图片和视频识别 可自主 融合了AI图像识别opencv、yolo、ocr、esayAI内核识别;AI智能客服、AI语言模型、 无任何第三方API接口可定制化自主离线化部署并自主化行业化使用 避免占用内存、GPU消耗训练与识别分开使用;', 'https://github.com/dromara/wgai', 'http://116.198.227.105/', 'Java', '其他', 'active', 170, 37, 0, '2024-06-05T12:04:43Z', '2025-08-26T04:29:29Z', '', 'Apache License 2.0'),
('skyeye', '智能办公OA系统[SpringBoot2-快速开发平台]，适用于医院，学校，中小型企业等机构的管理。Activiti5.22+动态表单实现零java代码即可做到复杂业务的流程实施，同时包含文件在线操作、日志、考勤、CRM、ERP进销存、项目、拖拽式生成问卷、日程、笔记、计划、行政等多种复杂业务功能。同时，可进行授权二开。', 'https://github.com/dromara/skyeye', '', 'Java', '微服务框架', 'active', 1081, 273, 7, '2019-05-19T07:08:40Z', '2025-08-26T03:40:07Z', 'approval-process,cms,crm,ehr,erp,hr,layui,mysql,oa,privileges,redis,skyeye,springboot,springboot2,springcloud-vue,websocket', 'MIT License'),
('binlog4j', '', 'https://github.com/dromara/binlog4j', '', 'Java', '日志追踪', 'active', 45, 6, 2, '2023-08-28T04:45:25Z', '2025-08-26T03:08:32Z', '', 'Apache License 2.0'),
('MaxKey', 'Dromara 🗝️MaxKey SSO ,Leading-Edge IAM-IDaas(Identity and Access Management) Product , Under Apache-2.0 is free ，业界领先的IAM-IDaas身份管理和认证产品，遵循Apache-2.0开源免费，支持OAuth2.x、OpenID Connect、SAML2.0、CAS、JWT、SCIM等SSO标准协议，基于RBAC统一权限控制，实现用户生命周期管理，开源、安全、合规、自主可控。', 'https://github.com/dromara/MaxKey', 'https://www.maxkey.top', 'Java', '权限认证', 'active', 1760, 375, 2, '2016-11-16T03:06:50Z', '2025-08-26T02:45:18Z', 'active-directory,authentication,cas,cloud-identity,iam,identity,jwt,kerberos,ldap,mfa,multi-tenancy,oauth,oauth2,openid-connect,rbac,saml,scim-2,single-sign-on,sso,topt', 'Apache License 2.0'),
('mayfly-go', 'Browser-based management platform of machine, database (mysql pgsql oracle sqlserver Gauss sqlite), redis(standalone sentinel cluster), mongo, Es unified management and operation platform. (web版linux(终端 文件 脚本 进程)、数据库(mysql pgsql oracle sqlserver 高斯 达梦 sqlite)、数据同步、redis(单机 哨兵 集群)、mongo、Es统一管理操作平台)', 'https://github.com/dromara/mayfly-go', '', 'Go', '其他', 'active', 2133, 452, 22, '2021-04-16T07:26:13Z', '2025-08-26T02:21:41Z', 'element-plus,gin,golang,gorm,linux,mongodb,mysql,redis,vue3', 'Apache License 2.0'),
('easy-query', 'java/kotlin high performance lightweight solution for jdbc query,support oltp and olap query,一款java下面支持强类型、轻量级、高性能的ORM,致力于解决jdbc查询,拥有对象模型筛选、隐式子查询、隐式join', 'https://github.com/dromara/easy-query', 'http://www.easy-query.com', 'Java', '其他', 'active', 668, 61, 51, '2022-09-27T02:42:07Z', '2025-08-26T01:14:06Z', 'java,jdbc,join,kotlin,mysql,orm,sharding', 'Apache License 2.0'),
('mica-mqtt-docs', 'Dromara mica-mqtt 低延迟、高性能的 java mqtt 物联网组件。', 'https://github.com/dromara/mica-mqtt-docs', 'https://mica-mqtt.dreamlu.net', 'TypeScript', '其他', 'active', 2, 0, 0, '2024-12-17T08:17:39Z', '2025-08-26T01:09:34Z', 'java,mqtt,spring', 'Apache License 2.0'),
('warm-flow', 'Warm-Flow国产工作流引擎🎉，其特点简洁轻量，五脏俱全，可扩展，是一个可通过jar引入设计器的工作流。。解决flowable和activities复杂、学习成本高和集成难等痛点。', 'https://github.com/dromara/warm-flow', 'http://www.warm-flow.cn', 'Java', '规则引擎', 'active', 361, 52, 2, '2024-01-02T09:13:48Z', '2025-08-26T01:09:22Z', 'workflow', 'Apache License 2.0'),
('payment-spring-boot', '微信支付V3支付，支持微信优惠券，代金券、商家券、公众号支付、微信小程序支付、分账、支付分、商家券、合单支付、先享卡、电商收付通等全部微信支付功能API，同时满足服务商、商户开发需求。一键集成，上手快，欢迎star。', 'https://github.com/dromara/payment-spring-boot', '', 'HTML', '支付网关', 'active', 831, 180, 15, '2020-12-01T15:50:07Z', '2025-08-25T19:46:14Z', 'java,payment,spring-boot,wechat-app,wechat-pay,wechat-sdk', 'Apache License 2.0'),
('RuoYi-Cloud-Plus', '重写RuoYi-Cloud所有功能 整合 SpringCloudAlibaba Dubbo3.0 Sa-Token Mybatis-Plus MQ OSS ES Xxl-Job Docker 全方位升级 定期同步', 'https://github.com/dromara/RuoYi-Cloud-Plus', 'https://plus-doc.dromara.org', 'Java', '权限认证', 'active', 1009, 301, 1, '2021-06-23T06:50:18Z', '2025-08-25T12:13:14Z', 'docker,dubbo,mybatis-plus,oss,springcloud,springcloudalibaba', 'MIT License'),
('go-view', 'GoView 说明文档，GoView 是一个低代码数据可视化开发平台，将图表或页面元素封装为基础组件，无需编写代码即可完成业务需求。 它的技术栈为：Vue3 + TypeScript4 + Vite2 + NaiveUI + ECharts5 + Axios + Pinia2 + PlopJS', 'https://github.com/dromara/go-view', '', 'Vue', '数据可视化', 'active', 786, 148, 8, '2022-05-04T13:27:25Z', '2025-08-25T08:44:37Z', '', 'MIT License'),
('mica-mqtt', 'java mqtt 基于 java aio 实现，开源、简单、易用、低延迟、高性能百万级 java mqtt client 组件和 java mqtt broker 服务。降低自研 iot 物联网平台难度。🔝🔝 记得右上角点个star 关注更新！', 'https://github.com/dromara/mica-mqtt', 'https://mica-mqtt.dreamlu.net', 'Java', 'HTTP客户端', 'active', 388, 70, 2, '2020-09-13T06:30:06Z', '2025-08-25T08:30:50Z', 'java,mqtt,mqtt-broker,mqtt-client', 'Apache License 2.0'),
('dongle', 'A simple, semantic and developer-friendly crypto package for golang', 'https://github.com/dromara/dongle', 'https://dongle.go-pkg.com', 'Go', '其他', 'active', 996, 70, 4, '2021-08-11T07:11:54Z', '2025-08-25T08:06:08Z', '3des,aes,base100,base16,base32,base45,base58,base62,base64,base85,base91,des,hash,hmac,md5,rsa', 'MIT License'),
('SMS4J', '让简单的事情回归简单的本质。 SMS4J为短信聚合框架，帮您轻松集成多家短信服务，解决接入多个短信SDK的繁琐流程。 目前已接入数家常见的短信服务商，后续将会继续集成。后续的版本中我们还将推出更多的相关功能 你的 ⭐️ ⭐️⭐️Star⭐️⭐️ ⭐️，是我的动力！如果你觉得还不错，请点上一颗小星星', 'https://github.com/dromara/SMS4J', 'https://sms4j.com', 'Java', '其他', 'active', 1173, 148, 17, '2023-03-24T03:06:15Z', '2025-08-25T04:28:31Z', 'java,sms,solon,springboot', 'Apache License 2.0'),
('electron-egg', 'A simple, cross platform, enterprise desktop software development framework', 'https://github.com/dromara/electron-egg', 'https://www.kaka996.com', 'JavaScript', '桌面应用', 'active', 2319, 315, 17, '2020-11-02T02:21:12Z', '2025-08-25T03:24:18Z', 'electron,electron-app,electron-egg,nodejs', 'Apache License 2.0'),
('Omega-AI', 'Omega-AI：基于java打造的深度学习框架，帮助你快速搭建神经网络，实现模型推理与训练，引擎支持自动求导，多线程与GPU运算，GPU支持CUDA，CUDNN。', 'https://github.com/dromara/Omega-AI', '', 'Java', '其他', 'active', 495, 61, 8, '2019-08-06T15:39:10Z', '2025-08-25T02:08:39Z', 'ai,deeplearning,diffusion,llm,neural-network,yolo', 'Apache License 2.0'),
('neutrino-proxy', '中微子代理（内网穿透）', 'https://github.com/dromara/neutrino-proxy', '', 'Java', '其他', 'active', 630, 112, 8, '2022-06-10T10:22:15Z', '2025-08-25T01:59:04Z', '', 'MIT License'),
('carbon', 'A simple, semantic and developer-friendly  time  package for golang', 'https://github.com/dromara/carbon', 'https://carbon.go-pkg.com', 'Go', '其他', 'active', 5116, 257, 3, '2020-09-07T09:07:35Z', '2025-08-24T20:14:34Z', 'calendar,carbon,date,datetime,gorm,hebrew,i18n,jalaali,julian,lunar,persian,time,timezone,xorm', 'MIT License'),
('easy-es', 'A foolproof Elasticsearch ORM framework that is easy to use, requires minimal coding, and is highly expandable...', 'https://github.com/dromara/easy-es', '', 'Java', 'HTTP客户端', 'active', 1537, 238, 95, '2021-12-01T09:05:38Z', '2025-08-24T15:26:52Z', 'elasticsearch,java,orm,resthighlevelclient', 'Apache License 2.0'),
('Jpom', '【dromara】🚀简而轻的低侵入式在线构建、自动部署、日常运维、项目监控软件', 'https://github.com/dromara/Jpom', 'https://jpom.dromara.org/', 'Java', 'HTTP客户端', 'active', 1820, 351, 16, '2019-01-04T05:38:56Z', '2025-08-22T16:32:01Z', 'ci-cd,docker,java,jpom,ssh-client', 'Other'),
('sureness', 'Dromara Sureness A efficient security framework focus on protection of API. ', 'https://github.com/dromara/sureness', 'https://sureness.dromara.org', 'Java', '权限认证', 'active', 876, 158, 22, '2019-01-21T15:35:21Z', '2025-08-22T13:23:47Z', 'authentication,authorization,basic-auth,digest,framework,javalin,jwt,ktor,library,quarkus,restful-api,shiro,spring,spring-security,springboot', 'Apache License 2.0'),
('fast-request', 'IntelliJ IDEA plugin Fast Request', 'https://github.com/dromara/fast-request', 'https://api-buddy.com/en', 'Java', 'IDE插件', 'active', 508, 124, 8, '2021-06-10T03:53:55Z', '2025-08-22T06:39:44Z', 'code-generation,idea-plugin,intellij-idea,jax-rs,kotlin,spring-mvc,springboot', 'GNU Affero General Public License v3.0'),
('issues-translate-action', 'The action for translating Non-English issues content to English.', 'https://github.com/dromara/issues-translate-action', '', 'TypeScript', '其他', 'active', 215, 31, 13, '2020-11-10T14:46:27Z', '2025-08-22T06:37:20Z', 'action,issues,translation,translator', 'MIT License'),
('plus-doc', '', 'https://github.com/dromara/plus-doc', '', 'HTML', '其他', 'active', 14, 13, 0, '2023-03-31T08:18:00Z', '2025-08-22T01:50:04Z', '', ''),
('tianai-captcha', 'captcha captcha captcha 可能是java界最好的开源行为验证码  [滑块验证码、点选验证码、行为验证码、旋转验证码， 滑动验证码]', 'https://github.com/dromara/tianai-captcha', '', 'Java', '其他', 'active', 903, 119, 4, '2021-04-19T00:49:17Z', '2025-08-21T18:38:45Z', '', 'Apache License 2.0'),
('mendmix-cloud', 'Mendmix定位是一站式分布式开发架构开源解决方案及云原生架构技术底座。Mendmix提供了数据库、缓存、消息中间件、分布式定时任务、安全框架、网关以及主流产商云服务快速集成能力。基于Mendmix可以不用关注技术细节快速搭建高并发高可用基于微服务的分布式架构。', 'https://github.com/dromara/mendmix-cloud', 'http://www.jeesuite.com/', 'Java', '权限认证', 'active', 706, 291, 2, '2016-07-03T05:38:18Z', '2025-08-21T14:01:15Z', 'cloud-native,distributed,framework,kafka,mybatis,redis,security,spring-web,springboot,springcloud', 'Apache License 2.0'),
('disjob', 'A distributed job scheduling and distributed computing framework', 'https://github.com/dromara/disjob', 'http://ponfee.cn:8000/', 'Java', '其他', 'active', 346, 44, 2, '2022-10-06T08:44:24Z', '2025-08-21T09:53:34Z', 'discovery,distributed,job,registry,scheduler,task', 'Apache License 2.0'),
('myth', 'Reliable messages resolve distributed transactions', 'https://github.com/dromara/myth', 'https://dromara.org', 'Java', '微服务框架', 'active', 1492, 600, 58, '2017-12-01T06:33:28Z', '2025-08-21T03:29:15Z', 'activemq,aop,dubbo,kafka,kryo,motan,protobuf,rabbitmq,rocketmq,soa,spring,springboot,springcloud,translation', 'Apache License 2.0'),
('mybatis-jpa-extra', '扩展MyBatis JPA支持，简化CUID操作，增强SELECT分页查询。Extended MyBatis JPA support , Simplify CUID operations and enhance SELECT pagination queries.', 'https://github.com/dromara/mybatis-jpa-extra', '', 'Java', '身份管理', 'active', 17, 8, 0, '2018-06-02T01:13:14Z', '2025-08-21T03:09:11Z', 'cuid,database,jpa,maxkey,mybatis,pagination,plugin', 'Apache License 2.0'),
('auto-table', '根据 Java 实体，自动映射成数据库的表结构。', 'https://github.com/dromara/auto-table', 'https://autotable.tangzc.com', 'Java', '其他', 'active', 26, 4, 4, '2024-03-01T05:22:26Z', '2025-08-21T01:44:15Z', '', 'Apache License 2.0'),
('mybatis-plus-ext', 'mybatis-plus框架的拓展包，在框架原有基础上做了进一步的轻度封装，增强内容：多数据源自动建表、数据自动填充、自动关联查询、冗余数据自动更新、动态查询条件等。', 'https://github.com/dromara/mybatis-plus-ext', 'https://www.yuque.com/dontang/codewiki/gzqgd8', 'Java', '其他', 'active', 71, 10, 1, '2021-03-12T15:26:05Z', '2025-08-20T14:15:58Z', 'java-17,java-8,mybatis,mybatis-plus,springboot3', 'Apache License 2.0'),
('koalas-rpc', '企业生产级百亿日PV高可用可拓展的RPC框架。理论上并发数量接近服务器带宽，客户端采用thrift协议，服务端支持netty和thrift的TThreadedSelectorServer半同步半异步线程模型，支持动态扩容，服务上下线，权重动态，可用性配置，泛化调用，页面流量统计，泛化调用等，支持trace跟踪等，天然接入cat支持数据大盘展示等，持续为个人以及中小型公司提供可靠的RPC框架技术方案', 'https://github.com/dromara/koalas-rpc', '', 'Java', '线程池', 'active', 102, 32, 12, '2019-06-21T09:34:38Z', '2025-08-20T14:12:48Z', '', 'Apache License 2.0'),
('WeMQ', 'WeMQ是一款面向物联网设备运营商的开源物联网设备调试系统，提供完整的物联网设备调试方案，集成设备管理、MQTT服务器管理、客户管理等功能，自研Nmqs通信层组件，实现了连接信息的加密，保证了数据的安全性。', 'https://github.com/dromara/WeMQ', 'https://wemq.nicholasld.cn', 'HTML', '其他', 'active', 42, 7, 0, '2023-05-22T18:03:41Z', '2025-08-20T06:24:16Z', '', 'Apache License 2.0'),
('TestHub', '一款基于流程编排的自动化测试工具', 'https://github.com/dromara/TestHub', '', 'Java', '其他', 'active', 77, 11, 1, '2023-08-02T06:54:08Z', '2025-08-20T05:33:39Z', '', 'Apache License 2.0'),
('distribute-transaction', '分布式事务书籍随书源码', 'https://github.com/dromara/distribute-transaction', '', 'Java', '分布式事务', 'active', 113, 66, 3, '2021-07-10T09:13:59Z', '2025-08-18T23:00:15Z', '', 'Apache License 2.0'),
('MilvusPlus', '', 'https://github.com/dromara/MilvusPlus', '', 'Java', '其他', 'active', 43, 8, 4, '2024-05-09T04:23:50Z', '2025-08-17T16:23:47Z', '', 'Apache License 2.0'),
('hmily', 'Distributed transaction solutions', 'https://github.com/dromara/hmily', 'https://dromara.org', 'Java', '微服务框架', 'active', 4144, 1407, 55, '2017-09-28T06:29:01Z', '2025-08-17T16:01:25Z', 'dubbo,motan,spring,springcloud,tac,tansaction,tcc,xa', 'Apache License 2.0'),
('TLog', 'Lightweight distributed log label tracking framwork', 'https://github.com/dromara/TLog', '', 'Java', 'DevOps', 'active', 582, 114, 22, '2020-09-16T07:58:37Z', '2025-08-17T15:58:35Z', 'distributed-tracing,dubbo,dubbox,log4j-log4j2-logback,spring-cloud', 'MIT License'),
('raincat', '强一致分布式事务框架', 'https://github.com/dromara/raincat', 'https://dromara.org/', 'Java', '微服务框架', 'active', 1908, 660, 33, '2017-09-01T08:06:20Z', '2025-08-17T13:56:51Z', 'dubbo,jdk8,netty,soa,spi,spring,spring-cloud,tcc,transaction', 'GNU Lesser General Public License v3.0'),
('RedisFront', 'RedisFront is a cross-platform Redis client tool developed with Java Swing, compatible with major operating systems (Windows, macOS,Linux). Its visualized interface enables Redis data management and server monitoring, suitable for development debugging and production operations.', 'https://github.com/dromara/RedisFront', '', 'Java', '工具库', 'active', 321, 26, 4, '2022-07-19T07:04:55Z', '2025-08-16T07:16:28Z', 'javaswing,redis,redis-gui,redis-gui-client,swing-gui', 'Apache License 2.0'),
('gobrs-async', 'Gobrs-Async (异步任务 编排框架) 是一款功能强大、配置灵活、带有全链路异常回调、内存优化、异常状态管理于一身的高性能异步编排框架。为企业提供在复杂应用场景下动态任务编排的能力。 针对于复杂场景下，异步线程复杂性、任务依赖性、异常状态难控制性； Gobrs-Async 为此而生。', 'https://github.com/dromara/gobrs-async', '', 'Java', '其他', 'active', 507, 138, 10, '2022-03-06T07:52:23Z', '2025-08-14T08:43:50Z', '', 'Apache License 2.0'),
('CloudEon', 'CloudEon uses Kubernetes to install and deploy open-source big data components, enabling the containerized operation of an open-source big data platform. This allows you to reduce your focus on underlying resource management and maintenance.', 'https://github.com/dromara/CloudEon', 'https://docs.cloudeon.top/en/dev/', 'FreeMarker', 'DevOps', 'active', 476, 115, 66, '2023-01-29T02:00:55Z', '2025-08-13T11:24:25Z', 'bigdata,cloudnative,doris,hadoop,hdfs,kubernetes,yarn', 'Apache License 2.0'),
('dataCompare', 'big data comparison and data profiling platform: low code，data comparison and data profiling', 'https://github.com/dromara/dataCompare', 'https://www.reddit.com/r/bigdata/comments/zlm2mw/design_and_practice_of_open_source_big_data/', 'Java', '其他', 'active', 274, 64, 7, '2022-07-28T05:40:05Z', '2025-08-13T11:23:59Z', 'bigdata,doris,hive,low-code,spark', 'Apache License 2.0'),
('stream-query', '', 'https://github.com/dromara/stream-query', '', 'Java', '其他', 'active', 106, 28, 0, '2022-05-20T16:33:08Z', '2025-08-13T11:23:49Z', '', 'Other'),
('J2EEFAST', 'J2eeFAST 是一个 Java EE 企业级快速开发平台，永久免费、真开源,拒绝标题党。基于经典技术组合（Spring Boot、Spring MVC、Apache Shiro、MyBatis-Plus、Freemarker、Bootstrap、AdminLTE）采用经典开发模式，让初学者能够更快的入门并投入到团队开发中去。 在线代码生成功能，包括核心模块如：组织机构、角色用户、菜单及按钮授权、数据权限、系统参数、内容管理、license认证,BPM工作流等。采用松耦合设计；界面无刷新，一键换肤；众多账号安全设置，密码策略；在线定时任务配置；支持多数据源；支持读写分离、分库分表.', 'https://github.com/dromara/J2EEFAST', 'http://www.j2eefast.com', 'Java', '其他', 'active', 98, 38, 10, '2020-03-29T01:39:17Z', '2025-08-13T11:21:30Z', '', 'GNU Affero General Public License v3.0'),
('hodor', 'A distributed scheduling job framework supporting DAG workflow for big data and regular jobs, providing programmable job types across different languages.', 'https://github.com/dromara/hodor', '', 'Java', '规则引擎', 'active', 154, 58, 9, '2019-12-17T09:20:56Z', '2025-08-13T11:21:07Z', 'bigdata,flink,java,kettle,scheduler,spark,springtask,xxljob', 'Apache License 2.0'),
('rsmedia', 'audio/video toolkit based FFmpeg 6.x, 7.x supported for multimedia with Hardware Acceleration.', 'https://github.com/dromara/rsmedia', '', 'Rust', '工具库', 'active', 38, 5, 2, '2024-10-07T09:38:34Z', '2025-08-12T01:29:48Z', 'audio,audio-streaming,encoder-decoder,ffmpeg,hardware-acceleration,hardware-accelerator,media,multimedia,mux,muxer,video,video-streaming', 'BSD 3-Clause "New" or "Revised" License'),
('tlog-homepage', '', 'https://github.com/dromara/tlog-homepage', '', 'JavaScript', '日志追踪', 'active', 2, 9, 6, '2022-09-12T08:15:03Z', '2025-07-31T06:20:06Z', '', 'MIT License'),
('dromara.github.io', 'Dromara Official Website', 'https://github.com/dromara/dromara.github.io', 'https://dromara.org/', 'TypeScript', 'DevOps', 'active', 43, 82, 1, '2021-01-19T15:29:14Z', '2025-07-25T02:51:06Z', '', ''),
('stream-query-docs', '文档', 'https://github.com/dromara/stream-query-docs', 'https://vampireachao.github.io/stream-query-docs/#/', 'HTML', '其他', 'active', 2, 2, 0, '2022-05-29T15:00:51Z', '2025-07-23T06:56:24Z', '', ''),
('incubator', '孵化器web站点', 'https://github.com/dromara/incubator', '', 'Unknown', '其他', 'active', 3, 1, 3, '2024-12-13T13:55:51Z', '2025-06-30T07:22:03Z', '', 'Apache License 2.0'),
('ujcms-cp', 'UJCMS-CP是UJCMS的后台前端项目。使用TypeScript、Vue3、ElementPlus、TailwindCSS开发。', 'https://github.com/dromara/ujcms-cp', 'https://www.ujcms.com', 'Vue', '其他', 'active', 17, 14, 0, '2022-01-09T14:30:35Z', '2025-06-26T16:26:08Z', 'cms,element-plus,java,vue3', 'Apache License 2.0'),
('northstar-monitor', '', 'https://github.com/dromara/northstar-monitor', '', 'JavaScript', '监控系统', 'active', 2, 2, 0, '2021-07-24T03:38:09Z', '2025-06-20T07:02:56Z', '', 'Apache License 2.0'),
('dynamic-tp-admin', '🔥🔥🔥轻量级动态线程池 dynamictp 管理端。Lightweight dynamic threadpool admin.', 'https://github.com/dromara/dynamic-tp-admin', 'https://dynamictp.cn/', 'Unknown', '线程池', 'active', 0, 2, 1, '2025-04-04T15:14:39Z', '2025-06-11T07:36:47Z', '', 'Apache License 2.0'),
('droer', '', 'https://github.com/dromara/droer', '', 'Unknown', '其他', 'active', 0, 0, 0, '2025-04-25T09:13:45Z', '2025-04-25T09:13:45Z', '', ''),
('Jinx', 'Spring-boot框架采用netty取代tomcat 来做http服务', 'https://github.com/dromara/Jinx', '', 'Java', 'HTTP客户端', 'active', 298, 93, 2, '2017-08-07T10:14:17Z', '2025-04-10T10:10:37Z', '', 'Apache License 2.0'),
('starter-carpack', 'The starter template of carpack.', 'https://github.com/dromara/starter-carpack', '', 'TypeScript', '其他', 'active', 1, 0, 0, '2024-06-26T14:12:16Z', '2025-04-04T04:02:31Z', '', 'MIT License'),
('jcudax', '以java sdk方式提供的一个cuda自定义扩展', 'https://github.com/dromara/jcudax', '', 'Java', '其他', 'active', 2, 1, 0, '2025-01-09T08:35:42Z', '2025-04-04T04:02:29Z', '', 'Apache License 2.0'),
('electron-egg-docs', 'docs ', 'https://github.com/dromara/electron-egg-docs', '', 'JavaScript', '桌面应用', 'active', 1, 0, 0, '2024-12-26T07:56:38Z', '2025-04-04T04:02:29Z', '', 'Apache License 2.0'),
('dromara-webhooks', '', 'https://github.com/dromara/dromara-webhooks', '', 'Unknown', '其他', 'active', 1, 0, 0, '2024-12-07T15:23:24Z', '2025-04-04T04:02:28Z', '', ''),
('coscon', 'CosCon2024 Offical Website.', 'https://github.com/dromara/coscon', '', 'Vue', '其他', 'active', 1, 0, 0, '2024-10-05T06:13:53Z', '2025-04-04T04:02:27Z', '', ''),
('carpack', '📦 The packer of Newcar to package animation into Windows, MacOS, Linux, Android, IOS with Tauri.', 'https://github.com/dromara/carpack', '', 'TypeScript', '其他', 'active', 11, 0, 0, '2024-06-26T13:40:12Z', '2025-04-04T04:02:27Z', '', 'Apache License 2.0'),
('cloudeon-docs', '', 'https://github.com/dromara/cloudeon-docs', 'https://cloudeon.dromara.org/', 'HTML', '微服务框架', 'active', 1, 1, 0, '2024-12-18T06:13:26Z', '2025-04-04T04:02:27Z', '', ''),
('.github', '', 'https://github.com/dromara/.github', '', 'Unknown', '其他', 'active', 1, 1, 0, '2023-07-06T03:41:42Z', '2025-04-04T04:02:24Z', '', ''),
('shenyu', 'Apache ShenYu is a Java native API Gateway for service proxy, protocol conversion and API governance.', 'https://github.com/dromara/shenyu', 'https://shenyu.apache.org/', 'Unknown', '其他', 'active', 6, 0, 0, '2023-05-25T07:49:33Z', '2025-01-06T05:14:38Z', '', 'Apache License 2.0'),
('athena', '', 'https://github.com/dromara/athena', '', 'Java', '其他', 'active', 24, 13, 0, '2020-09-04T09:32:09Z', '2024-11-08T09:12:32Z', '', 'Apache License 2.0'),
('open-capacity-platform', '', 'https://github.com/dromara/open-capacity-platform', '', 'JavaScript', 'DevOps', 'active', 15, 5, 0, '2019-09-12T06:53:11Z', '2024-09-12T06:41:03Z', '', 'Apache License 2.0'),
('QuantumultX', '同步和更新大佬脚本库，更新懒人配置', 'https://github.com/dromara/QuantumultX', '', 'Unknown', '量化交易', 'active', 1, 1, 0, '2023-04-29T10:22:46Z', '2024-06-30T22:52:24Z', '', 'GNU General Public License v3.0'),
('ChatGPT-1', 'This project is a plugin that supports ChatGPT running on JetBrains series IDE.', 'https://github.com/dromara/ChatGPT-1', 'https://plugins.jetbrains.com/plugin/20603-chatgpt', 'Unknown', 'IDE插件', 'active', 1, 0, 0, '2023-05-25T14:13:49Z', '2024-06-30T22:37:33Z', '', 'Other'),
('newcar-local-template', 'The Local Mode Template of Newcar', 'https://github.com/dromara/newcar-local-template', '', 'JavaScript', '其他', 'active', 1, 0, 0, '2024-04-20T12:57:59Z', '2024-05-05T09:58:28Z', '', ''),
('Newcar-Animation-Studio', 'The visual studio of Newcar, which makes the animation producing more rapid.', 'https://github.com/dromara/Newcar-Animation-Studio', '', 'Vue', 'DevOps', 'active', 1, 0, 0, '2024-04-22T04:03:09Z', '2024-04-24T07:14:23Z', '', ''),
('canvaskit-ts', 'This version''s CanvasKit-WASM move the CanvasKit namespace and auto-init it.', 'https://github.com/dromara/canvaskit-ts', '', 'Unknown', '其他', 'active', 1, 0, 0, '2024-04-18T01:35:57Z', '2024-04-21T13:13:13Z', '', 'Apache License 2.0'),
('nacos', 'an easy-to-use dynamic service discovery, configuration and service management platform for building cloud native applications.', 'https://github.com/dromara/nacos', 'https://nacos.io', 'Unknown', '微服务框架', 'active', 1, 1, 0, '2022-09-13T09:42:38Z', '2024-01-08T15:52:43Z', '', 'Apache License 2.0'),
('hmily-admin', '', 'https://github.com/dromara/hmily-admin', '', 'Java', '分布式事务', 'active', 7, 9, 0, '2020-08-10T03:21:37Z', '2023-06-20T10:34:01Z', '', ''),
('hmily-admin-helm-chart', '', 'https://github.com/dromara/hmily-admin-helm-chart', '', 'Smarty', '数据可视化', 'active', 2, 2, 1, '2022-11-09T07:36:28Z', '2022-11-15T10:30:29Z', '', 'Apache License 2.0'),
('open-giteye-api', '', 'https://github.com/dromara/open-giteye-api', '', 'Java', '其他', 'active', 2, 2, 0, '2021-12-30T03:24:51Z', '2022-09-13T09:10:38Z', '', 'GNU General Public License v2.0'),
('gateway-book', '', 'https://github.com/dromara/gateway-book', '', 'Unknown', '其他', 'active', 2, 1, 0, '2022-06-08T14:03:56Z', '2022-06-09T04:18:37Z', '', 'Apache License 2.0'),
('transaction-book', 'distributed transaction book', 'https://github.com/dromara/transaction-book', '', 'Unknown', '分布式事务', 'active', 12, 0, 12, '2020-05-28T04:19:31Z', '2022-03-19T09:01:58Z', '', ''),
('website', 'Dromara Official Website (Old)', 'https://github.com/dromara/website', 'https://dromara.org', 'JavaScript', 'DevOps', 'active', 7, 11, 3, '2021-01-15T11:40:32Z', '2021-06-14T19:54:23Z', '', 'Apache License 2.0'),
('soul-doc', '', 'https://github.com/dromara/soul-doc', '', 'Unknown', '其他', 'active', 1, 0, 0, '2018-07-23T13:12:38Z', '2021-06-14T19:53:31Z', '', ''),
('soul-benchmark', 'Soul performance test', 'https://github.com/dromara/soul-benchmark', '', 'Unknown', '其他', 'active', 3, 0, 0, '2021-03-24T03:11:48Z', '2021-06-14T19:53:23Z', '', 'Apache License 2.0'),
('skyway', '', 'https://github.com/dromara/skyway', '', 'Unknown', '其他', 'active', 2, 1, 5, '2019-06-05T06:40:22Z', '2021-06-14T19:53:15Z', '', 'Apache License 2.0'),
('hmily-dashboard', '', 'https://github.com/dromara/hmily-dashboard', '', 'Vue', '分布式事务', 'active', 2, 6, 1, '2020-08-05T08:07:54Z', '2021-06-14T19:50:41Z', '', '');

-- 成员数据插入
INSERT INTO osc_members (member_name, real_name, email, github_username, gitee_username, avatar_url, role, join_date, bio, company, location, blog) VALUES
('dreamlu', '如梦技术', '', 'dreamlu', 'dreamlu', 'https://foruda.gitee.com/avatar/1676894305066885804/372_dreamlu_1578913784.jpg', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/dreamlu'),
('loolly_admin', 'Looly', '', 'loolly_admin', 'loolly_admin', 'https://foruda.gitee.com/avatar/1740020978884039932/1463_loolly_admin_1740020978.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/loolly_admin'),
('westboy', 'westboy', '', 'westboy', 'westboy', 'https://foruda.gitee.com/avatar/1676895398424774760/8157_westboy_1578914542.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/westboy'),
('vakinge', 'vakinge', '', 'vakinge', 'vakinge', 'https://foruda.gitee.com/avatar/1667098645382111493/12388_vakinge_1667098645.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/vakinge'),
('kings', 'kings', '', 'kings', 'kings', 'https://foruda.gitee.com/avatar/1706089834385383191/56562_kings_1706089834.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/kings'),
('tonycody', '小四', '', 'tonycody', 'tonycody', 'https://foruda.gitee.com/avatar/1676897909265764146/58166_tonycody_1578915774.jpg', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/tonycody'),
('zendwang', 'zendwang', '', 'zendwang', 'zendwang', 'https://foruda.gitee.com/avatar/1676898413470663632/63756_wangzhenxian_1616491358.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/zendwang'),
('mcc', 'sixh', '', 'mcc', 'mcc', 'https://foruda.gitee.com/avatar/1676902368762610217/117284_mcc_1578918283.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/mcc'),
('fupengfei', 'ponfee', '', 'fupengfei', 'fupengfei', 'https://foruda.gitee.com/avatar/1694105299211698761/329178_fupengfei_1694105299.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/fupengfei'),
('fhs-opensource', 'fhs_opensource', '', 'fhs-opensource', 'fhs-opensource', 'https://foruda.gitee.com/avatar/1676906111105611922/339743_fhs-opensource_1602843222.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/fhs-opensource'),
('tangzc', '唐振超', '', 'tangzc', 'tangzc', 'https://foruda.gitee.com/avatar/1676906219947351575/342237_tangzc_1629796763.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/tangzc'),
('jingshuishenliu2025', '静水深流', '', 'jingshuishenliu2025', 'jingshuishenliu2025', 'https://foruda.gitee.com/avatar/1730346828060722345/409467_linkwechat_admin_1730346828.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/jingshuishenliu2025'),
('vanlin', 'VanLin', '', 'vanlin', 'vanlin', 'https://foruda.gitee.com/avatar/1715057338382990360/492356_vanlin_1715057338.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/vanlin'),
('fengzhenbing', 'fengzhenbing', '', 'fengzhenbing', 'fengzhenbing', 'https://foruda.gitee.com/avatar/1676915885934878248/495101_fengzhenbing_1614570031.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/fengzhenbing'),
('GIotEE', '喵呀', '', 'GIotEE', 'GIotEE', 'https://foruda.gitee.com/avatar/1690452517926820237/524686_bootx_1690452517.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/GIotEE'),
('a1234567891', '张玉龙', '', 'a1234567891', 'a1234567891', 'https://foruda.gitee.com/avatar/1676918535001791096/536094_a1234567891_1578926933.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/a1234567891'),
('KuafuRace', 'kuafuRace', '', 'KuafuRace', 'KuafuRace', 'https://foruda.gitee.com/avatar/1676918941650020316/544375_gouguoyin_1651719851.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/KuafuRace'),
('asgc', '傲世孤尘', '', 'asgc', 'asgc', 'https://foruda.gitee.com/avatar/1676525395592062227/591084_asgc_1676525395.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/asgc'),
('zyplayer', '暮光：城中城', '', 'zyplayer', 'zyplayer', 'https://foruda.gitee.com/avatar/1676921858704922679/596905_zyplayer_1578928715.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/zyplayer'),
('iangellove', 'iangellove', '', 'iangellove', 'iangellove', 'https://foruda.gitee.com/avatar/1676923203008238028/627943_iangellove_1619077330.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/iangellove'),
('alexzchen', 'alexzchen', '', 'alexzchen', 'alexzchen', 'https://foruda.gitee.com/avatar/1676926804935886406/727421_alexzchen_1578931550.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/alexzchen'),
('wallace5303', '哆啦好梦', '', 'wallace5303', 'wallace5303', 'https://foruda.gitee.com/avatar/1676927800717239547/744074_wallace5303_1640262078.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/wallace5303'),
('herodotus', '码匠君', '', 'herodotus', 'herodotus', 'https://foruda.gitee.com/avatar/1751631951748676421/751495_herodotus_1751631951.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/herodotus'),
('bwcx-jzy', '蒋小小', '', 'bwcx-jzy', 'bwcx-jzy', 'https://foruda.gitee.com/avatar/1706672302340167190/804942_bwcx-jzy_1706672302.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/bwcx-jzy'),
('yu199195', 'xiaoyu', '', 'yu199195', 'yu199195', 'https://foruda.gitee.com/avatar/1676931283921363972/812675_yu199195_1660296579.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/yu199195'),
('jeecp', 'owen', '', 'jeecp', 'jeecp', 'https://foruda.gitee.com/avatar/1676933281571625072/869801_jeecp_1578934392.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/jeecp'),
('tomgs', 'tincopper', '', 'tomgs', 'tomgs', 'https://foruda.gitee.com/avatar/1690855981478315624/892157_tomgs_1690855981.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/tomgs'),
('liangziqiang', '你好强哥', '', 'liangziqiang', 'liangziqiang', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/liangziqiang'),
('felord', 'felord', '', 'felord', 'felord', 'https://foruda.gitee.com/avatar/1676938569977342810/975445_felord_1578937289.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/felord'),
('huangxfchn', 'huangxfchn', '', 'huangxfchn', 'huangxfchn', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/huangxfchn'),
('godzt', '姚海陆', '', 'godzt', 'godzt', 'https://foruda.gitee.com/avatar/1676941287724757458/1029371_godzt_1649816811.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/godzt'),
('bryan31', '铂赛东', '', 'bryan31', 'bryan31', 'https://foruda.gitee.com/avatar/1676944767901252402/1102362_bryan31_1578940308.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/bryan31'),
('qianglu', 'QIANGLU', '', 'qianglu', 'qianglu', 'https://foruda.gitee.com/avatar/1676952769106099996/1168339_qianglu_1617169246.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/qianglu'),
('Yandex', 'nuo-promise', '', 'Yandex', 'Yandex', 'https://foruda.gitee.com/avatar/1676954095479113279/1175683_yandex_1606116054.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/Yandex'),
('inrgihc', 'inrgihc', '', 'inrgihc', 'inrgihc', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/inrgihc'),
('dt_flys', '公子骏', '', 'dt_flys', 'dt_flys', 'https://foruda.gitee.com/avatar/1676957136150845817/1216742_dt_flys_1594991700.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/dt_flys'),
('zuihou111', '最后', '', 'zuihou111', 'zuihou111', 'https://foruda.gitee.com/avatar/1676957321934470618/1219829_zuihou111_1578946597.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/zuihou111'),
('objs', 'Coder慌', '', 'objs', 'objs', 'https://foruda.gitee.com/avatar/1676958287157246212/1240250_objs_1644925867.jpeg', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/objs'),
('Redick01', 'Redick01', '', 'Redick01', 'Redick01', 'https://foruda.gitee.com/avatar/1676962426700784231/1335840_redick01_1661158577.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/Redick01'),
('moremind', 'moremind', '', 'moremind', 'moremind', 'https://foruda.gitee.com/avatar/1676964554358099777/1365152_finen_1578949847.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/moremind'),
('zhangleiG', '坏人', '', 'zhangleiG', 'zhangleiG', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/zhangleiG'),
('failedgoddess', '失败女神', '', 'failedgoddess', 'failedgoddess', 'https://foruda.gitee.com/avatar/1683714617241410716/1414930_failedgoddess_1683714617.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/failedgoddess'),
('ZhuGeZiFang', '诸葛子房', '', 'ZhuGeZiFang', 'ZhuGeZiFang', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/ZhuGeZiFang'),
('songyinyin', '读钓', '', 'songyinyin', 'songyinyin', 'https://foruda.gitee.com/avatar/1676969848008081157/1456978_songyinyin_1587295673.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/songyinyin'),
('nicholasld', 'NicholasLD', '', 'nicholasld', 'nicholasld', 'https://foruda.gitee.com/avatar/1676971452024617103/1488757_nicholasld_1655137708.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/nicholasld'),
('xsxgit', 'xsx', '', 'xsxgit', 'xsxgit', 'https://foruda.gitee.com/avatar/1676971693571605871/1494292_xsxgit_1585187915.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/xsxgit'),
('qq1398371419', '进击的阿晨', '', 'qq1398371419', 'qq1398371419', 'https://foruda.gitee.com/avatar/1749125395203371227/1515623_qq1398371419_1749125395.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/qq1398371419'),
('xuejm', '薛家明', '', 'xuejm', 'xuejm', 'https://foruda.gitee.com/avatar/1676974027785281049/1519719_xuejm_1634608353.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/xuejm'),
('doc_wei01_admin', 'Skyeye云源码', '', 'doc_wei01_admin', 'doc_wei01_admin', 'https://foruda.gitee.com/avatar/1730249804348798791/1541735_doc_wei01_admin_1730249804.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/doc_wei01_admin'),
('mouday', 'mouday', '', 'mouday', 'mouday', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/mouday'),
('handy-git', 'handy', '', 'handy-git', 'handy-git', 'https://foruda.gitee.com/avatar/1678377314939642686/1604115_handy-git_1678377314.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/handy-git'),
('tydhot', 'tydhot', '', 'tydhot', 'tydhot', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/tydhot'),
('Memorydoc', 'Memorydoc', '', 'Memorydoc', 'Memorydoc', 'https://foruda.gitee.com/avatar/1676981753803923892/1653758_memorydoc_1653891637.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/Memorydoc'),
('kevinhuangwl', 'Huangwl', '', 'kevinhuangwl', 'kevinhuangwl', 'https://foruda.gitee.com/avatar/1676982815949374247/1676852_kevinhuangwl_1616854583.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/kevinhuangwl'),
('lijiahangmax', '李佳航', '', 'lijiahangmax', 'lijiahangmax', 'https://foruda.gitee.com/avatar/1676983889645660201/1699294_lijiahangmax_1645258598.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/lijiahangmax'),
('click33', '刘潇', '', 'click33', 'click33', 'https://foruda.gitee.com/avatar/1676987703742511520/1766140_sz6_1578959462.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/click33'),
('JavaLionLi', '疯狂的狮子Li', '', 'JavaLionLi', 'JavaLionLi', 'https://foruda.gitee.com/avatar/1676987709652631782/1766278_javalionli_1585201115.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/JavaLionLi'),
('tomsun28', 'tomsun28', '', 'tomsun28', 'tomsun28', 'https://foruda.gitee.com/avatar/1711073216787514034/1767651_tomsun28_1711073216.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/tomsun28'),
('zhouhuanOGP', '周周', '', 'zhouhuanOGP', 'zhouhuanOGP', 'https://foruda.gitee.com/avatar/1676990597501273962/1816537_zhouhuanogp_1584234850.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/zhouhuanOGP'),
('liupeiqiang', '码农小易', '', 'liupeiqiang', 'liupeiqiang', 'https://foruda.gitee.com/avatar/1743086285118444812/1907773_liupeiqiang_1743086285.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/liupeiqiang'),
('tianai', '天爱有情', '', 'tianai', 'tianai', 'https://foruda.gitee.com/avatar/1676995180811602204/1916658_tianai_1641465224.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/tianai'),
('wggh_admin', 'WG', '', 'wggh_admin', 'wggh_admin', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/wggh_admin'),
('ly-chn', 'ly-chn', '', 'ly-chn', 'ly-chn', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/ly-chn'),
('atuptown', 'uptown', '', 'atuptown', 'atuptown', 'https://foruda.gitee.com/avatar/1705377370047642966/2060071_atuptown_1705377369.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/atuptown'),
('min290', '晓华', '', 'min290', 'min290', 'https://foruda.gitee.com/avatar/1730251364089836489/2218307_min290_1730251364.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/min290'),
('phial3', 'phial3', '', 'phial3', 'phial3', 'https://foruda.gitee.com/avatar/1747749641609395119/2302883_phial3_1747749641.jpeg', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/phial3'),
('qiuyueovo', 'qiuyue', '', 'qiuyueovo', 'qiuyueovo', 'https://foruda.gitee.com/avatar/1677014899681958962/2320996_bmlt_1605510460.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/qiuyueovo'),
('javpower', 'xgc', '', 'javpower', 'javpower', 'https://foruda.gitee.com/avatar/1713758700287870478/2334850_giteeclass_1713758700.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/javpower'),
('appleOfGray', 'AppleOfGray', '', 'appleOfGray', 'appleOfGray', 'https://foruda.gitee.com/avatar/1677018141408428533/3040392_appleofgray_1586265491.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/appleOfGray'),
('Jmysy', '就眠儀式', '', 'Jmysy', 'Jmysy', 'https://foruda.gitee.com/avatar/1677022544584087390/4835367_jmysy_1578975358.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/Jmysy'),
('sxwdmjy', 'danmo', '', 'sxwdmjy', 'sxwdmjy', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/sxwdmjy'),
('michelle1028', 'MichelleChung', '', 'michelle1028', 'michelle1028', 'https://foruda.gitee.com/avatar/1672740202395173029/4959041_michelle1028_1672740202.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/michelle1028'),
('MTrun', '奔跑的面条', '', 'MTrun', 'MTrun', 'https://foruda.gitee.com/avatar/1677029116973847167/4964818_mtrun_1653229420.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/MTrun'),
('XYW1171736840', '梦想i', '', 'XYW1171736840', 'XYW1171736840', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/XYW1171736840'),
('yao_kai_kai', 'kyao', '', 'yao_kai_kai', 'yao_kai_kai', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/yao_kai_kai'),
('ldp_dpsmax', '唯一解', '', 'ldp_dpsmax', 'ldp_dpsmax', 'https://foruda.gitee.com/avatar/1677041627321383894/5223723_ldp_dpsmax_1582882114.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/ldp_dpsmax'),
('Zhenln', 'Zhen', '', 'Zhenln', 'Zhenln', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/Zhenln'),
('g1879', 'g1879', '', 'g1879', 'g1879', 'https://foruda.gitee.com/avatar/1677050218352204239/5397123_g1879_1578986013.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/g1879'),
('li_nan11', '李楠', '', 'li_nan11', 'li_nan11', 'https://foruda.gitee.com/avatar/1685264509200648390/5512675_li_nan11_1685264509.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/li_nan11'),
('KamToHung', 'KamToHung', '', 'KamToHung', 'KamToHung', 'https://foruda.gitee.com/avatar/1705597079772058414/5528672_kamtohung_1705597079.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/KamToHung'),
('maxkeytop_admin', 'MaxKeyTop', '', 'maxkeytop_admin', 'maxkeytop_admin', 'https://foruda.gitee.com/avatar/1666757759121936664/5531506_maxkeytop_admin_1666757757.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/maxkeytop_admin'),
('tuohai666', 'tuohai666', '', 'tuohai666', 'tuohai666', 'https://foruda.gitee.com/avatar/1749785998960431013/5691751_tuohai666_1749785998.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/tuohai666'),
('VampireAchao', '阿超', '', 'VampireAchao', 'VampireAchao', 'https://foruda.gitee.com/avatar/1677076528653834572/7413431_vampireachao_1613450829.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/VampireAchao'),
('Xsssd', 'sssd', '', 'Xsssd', 'Xsssd', 'https://foruda.gitee.com/avatar/1700253320904237504/7616720_xsssd_1700253320.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/Xsssd'),
('ceilzcx', '星辰', '', 'ceilzcx', 'ceilzcx', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/ceilzcx'),
('learning-code', '学习代码的小白', '', 'learning-code', 'learning-code', 'https://foruda.gitee.com/avatar/1677115157834538455/8145789_learning-code_1645161177.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/learning-code'),
('LiYiMing666', 'lym', '', 'LiYiMing666', 'LiYiMing666', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/LiYiMing666'),
('dengliming0', 'dengliming', '', 'dengliming0', 'dengliming0', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/dengliming0'),
('ujcms', 'ujcms', '', 'ujcms', 'ujcms', 'https://foruda.gitee.com/avatar/1712646508433948472/8551103_ujcms_1712646508.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/ujcms'),
('liangliyun', 'liangli', '', 'liangliyun', 'liangliyun', 'https://gitee.com/assets/no_portrait.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/liangliyun'),
('MR-wind', '风如歌', '', 'MR-wind', 'MR-wind', 'https://foruda.gitee.com/avatar/1681565947955686253/8830369_mr-wind_1681565947.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/MR-wind'),
('drwave', 'Acbox', '', 'drwave', 'drwave', 'https://foruda.gitee.com/avatar/1681740171067104297/8876737_drwave_1681740171.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/drwave'),
('iajie', '阿杰', '', 'iajie', 'iajie', 'https://foruda.gitee.com/avatar/1677153687622344891/8886246_iajie_1625646843.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/iajie'),
('jia-hao-li', '嘉豪李', '', 'jia-hao-li', 'jia-hao-li', 'https://foruda.gitee.com/avatar/1700550824114332994/9283704_jia-hao-li_1700550824.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/jia-hao-li'),
('cizaii', 'Cizaii', '', 'cizaii', 'cizaii', 'https://foruda.gitee.com/avatar/1668072321526905225/9529147_zverify_1668072320.jpeg', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/cizaii'),
('zhou-shusheng', '淞筱', '', 'zhou-shusheng', 'zhou-shusheng', 'https://foruda.gitee.com/avatar/1740486547076741678/10051478_zhou-shusheng_1740486547.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/zhou-shusheng'),
('easy-es', 'elasticsearch', '', 'easy-es', 'easy-es', 'https://foruda.gitee.com/avatar/1677197391032175114/10085871_easy-es_1660916645.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/easy-es'),
('rigangxia', 'xiarigang', '', 'rigangxia', 'rigangxia', 'https://foruda.gitee.com/avatar/1739778214148551913/10137130_rigangxia_1739778214.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/rigangxia'),
('zqr10159', 'logicz', '', 'zqr10159', 'zqr10159', 'https://foruda.gitee.com/avatar/1694351456934300364/10163205_zqr10159_1694351456.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/zqr10159'),
('yanhom', 'yanhom', '', 'yanhom', 'yanhom', 'https://foruda.gitee.com/avatar/1677205338720808443/10317579_yanhom_1641919654.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/yanhom'),
('morestrive', 'Nevermore', '', 'morestrive', 'morestrive', 'https://foruda.gitee.com/avatar/1679642971088242866/10677820_morestrive_1679642971.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/morestrive'),
('dongfeng407', '东风', '', 'dongfeng407', 'dongfeng407', 'https://foruda.gitee.com/avatar/1700050958359874096/10900922_dongfeng407_1700050958.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/dongfeng407'),
('Pandas886', 'Pandas886', '', 'Pandas886', 'Pandas886', 'https://foruda.gitee.com/avatar/1681047857177084214/12623134_pandas886_1681047857.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/Pandas886'),
('vinci-897', 'vinci', '', 'vinci-897', 'vinci-897', 'https://foruda.gitee.com/avatar/1700483729067941252/13727968_vinci-897_1700483729.png', 'contributor', '2025-08-26', '', '', '', 'https://gitee.com/vinci-897');

-- 项目成员关联数据
INSERT INTO os_project_member (project_id, member_id, role, join_time, contribution_score) VALUES
((SELECT project_id FROM osc_projects WHERE project_name = 'Sa-Token'), (SELECT member_id FROM osc_members WHERE member_name = 'click33'), 'maintainer', '2021-01-01', 512),
((SELECT project_id FROM osc_projects WHERE project_name = 'dax-pay'), (SELECT member_id FROM osc_members WHERE member_name = 'xxm1995'), 'maintainer', '2021-01-01', 533),
((SELECT project_id FROM osc_projects WHERE project_name = 'forest'), (SELECT member_id FROM osc_members WHERE member_name = 'gongjun'), 'maintainer', '2021-01-01', 441),
((SELECT project_id FROM osc_projects WHERE project_name = 'northstar'), (SELECT member_id FROM osc_members WHERE member_name = 'KevinHuangwl'), 'maintainer', '2021-01-01', 437),
((SELECT project_id FROM osc_projects WHERE project_name = 'lamp-cloud'), (SELECT member_id FROM osc_members WHERE member_name = 'zuihou'), 'maintainer', '2021-01-01', 477),
((SELECT project_id FROM osc_projects WHERE project_name = 'MaxKey'), (SELECT member_id FROM osc_members WHERE member_name = 'shimingxy'), 'maintainer', '2021-01-01', 247),
((SELECT project_id FROM osc_projects WHERE project_name = 'electron-egg'), (SELECT member_id FROM osc_members WHERE member_name = 'wallace5303'), 'maintainer', '2021-01-01', 379),
((SELECT project_id FROM osc_projects WHERE project_name = 'Jpom'), (SELECT member_id FROM osc_members WHERE member_name = 'jiangzeyin'), 'maintainer', '2021-01-01', 385),
((SELECT project_id FROM osc_projects WHERE project_name = 'fast-request'), (SELECT member_id FROM osc_members WHERE member_name = 'kings1990'), 'maintainer', '2021-01-01', 376),
((SELECT project_id FROM osc_projects WHERE project_name = 'hmily'), (SELECT member_id FROM osc_members WHERE member_name = 'xiaoyu'), 'maintainer', '2021-01-01', 165),
((SELECT project_id FROM osc_projects WHERE project_name = 'TLog'), (SELECT member_id FROM osc_members WHERE member_name = 'bryan31'), 'maintainer', '2021-01-01', 408),

-- 补充剩余项目的主要维护者关联
-- domain-admin
((SELECT project_id FROM osc_projects WHERE project_name = 'domain-admin'), (SELECT member_id FROM osc_members WHERE member_name = 'mouday'), 'maintainer', '2022-09-23', 325),

-- dante-cloud 
((SELECT project_id FROM osc_projects WHERE project_name = 'dante-cloud'), (SELECT member_id FROM osc_members WHERE member_name = 'herodotus-ecosystem'), 'maintainer', '2021-05-19', 192),

-- RuoYi-Vue-Plus
((SELECT project_id FROM osc_projects WHERE project_name = 'RuoYi-Vue-Plus'), (SELECT member_id FROM osc_members WHERE member_name = 'JavaLionLi'), 'maintainer', '2021-06-23', 464),

-- cubic 
((SELECT project_id FROM osc_projects WHERE project_name = 'cubic'), (SELECT member_id FROM osc_members WHERE member_name = 'dromara'), 'maintainer', '2020-05-20', 100),

-- dynamic-tp
((SELECT project_id FROM osc_projects WHERE project_name = 'dynamic-tp'), (SELECT member_id FROM osc_members WHERE member_name = 'yanhom1314'), 'maintainer', '2022-01-07', 853),

-- liteflow
((SELECT project_id FROM osc_projects WHERE project_name = 'liteflow'), (SELECT member_id FROM osc_members WHERE member_name = 'bryan31'), 'maintainer', '2020-03-25', 477),

-- ujcms
((SELECT project_id FROM osc_projects WHERE project_name = 'ujcms'), (SELECT member_id FROM osc_members WHERE member_name = 'yangming0543'), 'maintainer', '2021-01-08', 46),

-- orion-visor
((SELECT project_id FROM osc_projects WHERE project_name = 'orion-visor'), (SELECT member_id FROM osc_members WHERE member_name = 'lijiahangmax'), 'maintainer', '2023-06-20', 151),

-- yft-design
((SELECT project_id FROM osc_projects WHERE project_name = 'yft-design'), (SELECT member_id FROM osc_members WHERE member_name = 'dromara'), 'maintainer', '2023-05-25', 279),

-- x-file-storage
((SELECT project_id FROM osc_projects WHERE project_name = 'x-file-storage'), (SELECT member_id FROM osc_members WHERE member_name = '1171736840'), 'maintainer', '2020-12-16', 303),

-- wgai
((SELECT project_id FROM osc_projects WHERE project_name = 'wgai'), (SELECT member_id FROM osc_members WHERE member_name = 'yx2124538'), 'maintainer', '2024-06-05', 37),

-- skyeye
((SELECT project_id FROM osc_projects WHERE project_name = 'skyeye'), (SELECT member_id FROM osc_members WHERE member_name = 'doitwmr'), 'maintainer', '2019-05-19', 273),

-- binlog4j
((SELECT project_id FROM osc_projects WHERE project_name = 'binlog4j'), (SELECT member_id FROM osc_members WHERE member_name = 'dromara'), 'maintainer', '2023-08-28', 6),

-- mayfly-go
((SELECT project_id FROM osc_projects WHERE project_name = 'mayfly-go'), (SELECT member_id FROM osc_members WHERE member_name = 'may-fly'), 'maintainer', '2021-04-16', 452),

-- easy-query
((SELECT project_id FROM osc_projects WHERE project_name = 'easy-query'), (SELECT member_id FROM osc_members WHERE member_name = 'xuejmnet'), 'maintainer', '2022-09-27', 61),

-- mica-mqtt-docs
((SELECT project_id FROM osc_projects WHERE project_name = 'mica-mqtt-docs'), (SELECT member_id FROM osc_members WHERE member_name = 'ChunMengLu'), 'maintainer', '2024-12-17', 0),

-- warm-flow
((SELECT project_id FROM osc_projects WHERE project_name = 'warm-flow'), (SELECT member_id FROM osc_members WHERE member_name = 'minliuhua'), 'maintainer', '2024-01-02', 52),

-- payment-spring-boot
((SELECT project_id FROM osc_projects WHERE project_name = 'payment-spring-boot'), (SELECT member_id FROM osc_members WHERE member_name = 'jjche-team'), 'maintainer', '2020-12-01', 180),

-- RuoYi-Cloud-Plus
((SELECT project_id FROM osc_projects WHERE project_name = 'RuoYi-Cloud-Plus'), (SELECT member_id FROM osc_members WHERE member_name = 'JavaLionLi'), 'maintainer', '2021-06-23', 301),

-- go-view
((SELECT project_id FROM osc_projects WHERE project_name = 'go-view'), (SELECT member_id FROM osc_members WHERE member_name = 'MTrun'), 'maintainer', '2022-05-04', 148),

-- mica-mqtt
((SELECT project_id FROM osc_projects WHERE project_name = 'mica-mqtt'), (SELECT member_id FROM osc_members WHERE member_name = 'ChunMengLu'), 'maintainer', '2020-09-13', 70),

-- dongle
((SELECT project_id FROM osc_projects WHERE project_name = 'dongle'), (SELECT member_id FROM osc_members WHERE member_name = 'gouguoyin'), 'maintainer', '2021-08-11', 70),

-- SMS4J
((SELECT project_id FROM osc_projects WHERE project_name = 'SMS4J'), (SELECT member_id FROM osc_members WHERE member_name = 'NotFound403'), 'maintainer', '2023-03-24', 148),

-- Omega-AI
((SELECT project_id FROM osc_projects WHERE project_name = 'Omega-AI'), (SELECT member_id FROM osc_members WHERE member_name = 'iangellove'), 'maintainer', '2019-08-06', 61),

-- neutrino-proxy
((SELECT project_id FROM osc_projects WHERE project_name = 'neutrino-proxy'), (SELECT member_id FROM osc_members WHERE member_name = 'aoshiguchen'), 'maintainer', '2022-06-10', 112),

-- carbon
((SELECT project_id FROM osc_projects WHERE project_name = 'carbon'), (SELECT member_id FROM osc_members WHERE member_name = 'gouguoyin'), 'maintainer', '2020-09-07', 257),

-- easy-es
((SELECT project_id FROM osc_projects WHERE project_name = 'easy-es'), (SELECT member_id FROM osc_members WHERE member_name = 'xpc1024'), 'maintainer', '2021-12-01', 238),

-- sureness
((SELECT project_id FROM osc_projects WHERE project_name = 'sureness'), (SELECT member_id FROM osc_members WHERE member_name = 'tomsun28'), 'maintainer', '2019-01-21', 158),

-- issues-translate-action
((SELECT project_id FROM osc_projects WHERE project_name = 'issues-translate-action'), (SELECT member_id FROM osc_members WHERE member_name = 'tomsun28'), 'maintainer', '2020-11-10', 31),

-- plus-doc
((SELECT project_id FROM osc_projects WHERE project_name = 'plus-doc'), (SELECT member_id FROM osc_members WHERE member_name = 'JavaLionLi'), 'maintainer', '2023-03-31', 13),

-- tianai-captcha
((SELECT project_id FROM osc_projects WHERE project_name = 'tianai-captcha'), (SELECT member_id FROM osc_members WHERE member_name = 'tianaiyouqing'), 'maintainer', '2021-04-19', 119),

-- mendmix-cloud
((SELECT project_id FROM osc_projects WHERE project_name = 'mendmix-cloud'), (SELECT member_id FROM osc_members WHERE member_name = 'vakinge'), 'maintainer', '2016-07-03', 291),

-- disjob
((SELECT project_id FROM osc_projects WHERE project_name = 'disjob'), (SELECT member_id FROM osc_members WHERE member_name = 'dromara'), 'maintainer', '2022-10-06', 44),

-- myth
((SELECT project_id FROM osc_projects WHERE project_name = 'myth'), (SELECT member_id FROM osc_members WHERE member_name = 'xiaoyu'), 'maintainer', '2017-12-01', 600),

-- mybatis-jpa-extra
((SELECT project_id FROM osc_projects WHERE project_name = 'mybatis-jpa-extra'), (SELECT member_id FROM osc_members WHERE member_name = 'shimingxy'), 'maintainer', '2018-06-02', 8),

-- auto-table
((SELECT project_id FROM osc_projects WHERE project_name = 'auto-table'), (SELECT member_id FROM osc_members WHERE member_name = 'tangzc'), 'maintainer', '2024-03-01', 4),

-- 给热门项目添加更多开发者
-- Sa-Token 添加更多核心开发者  
((SELECT project_id FROM osc_projects WHERE project_name = 'Sa-Token'), (SELECT member_id FROM osc_members WHERE member_name = 'konglinhao'), '1', '2021-03-15', 156),
((SELECT project_id FROM osc_projects WHERE project_name = 'Sa-Token'), (SELECT member_id FROM osc_members WHERE member_name = 'noear'), '1', '2021-04-20', 142),

-- dynamic-tp 添加更多开发者
((SELECT project_id FROM osc_projects WHERE project_name = 'dynamic-tp'), (SELECT member_id FROM osc_members WHERE member_name = 'KamTo'), '2', '2022-02-15', 98),
((SELECT project_id FROM osc_projects WHERE project_name = 'dynamic-tp'), (SELECT member_id FROM osc_members WHERE member_name = 'Bryan2018'), '2', '2022-03-10', 87),

-- forest 添加更多开发者
((SELECT project_id FROM osc_projects WHERE project_name = 'forest'), (SELECT member_id FROM osc_members WHERE member_name = 'ChunMengLu'), '2', '2018-01-10', 89),
((SELECT project_id FROM osc_projects WHERE project_name = 'forest'), (SELECT member_id FROM osc_members WHERE member_name = 'tomsun28'), '4', '2018-05-20', 67),

-- lamp-cloud 添加更多开发者
((SELECT project_id FROM osc_projects WHERE project_name = 'lamp-cloud'), (SELECT member_id FROM osc_members WHERE member_name = 'JavaLionLi'), '4', '2019-02-20', 234),
((SELECT project_id FROM osc_projects WHERE project_name = 'lamp-cloud'), (SELECT member_id FROM osc_members WHERE member_name = 'click33'), '4', '2019-03-15', 178),

-- hmily 添加更多开发者
((SELECT project_id FROM osc_projects WHERE project_name = 'hmily'), (SELECT member_id FROM osc_members WHERE member_name = 'yu199195'), '2', '2017-10-15', 123),
((SELECT project_id FROM osc_projects WHERE project_name = 'hmily'), (SELECT member_id FROM osc_members WHERE member_name = 'dromara'), '4', '2018-02-10', 98);

-- 示例贡献记录
INSERT INTO osc_contributions (member_id, project_id, contribution_type, content, contribution_time, points) VALUES
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 0), 1, 'bug_fix', '优化了性能', '2025-03-23', 5),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 2), 1, 'bug_fix', '添加了新功能', '2025-05-08', 9),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 0), 1, 'bug_fix', '更新了文档', '2025-05-08', 3),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 2), 2, 'feature', '优化了性能', '2025-03-23', 10),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 4), 2, 'code', '修复了重要Bug', '2025-05-18', 9),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 0), 2, 'review', '更新了文档', '2025-01-03', 4),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 0), 3, 'review', '修复了重要Bug', '2024-10-01', 1),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 1), 3, 'doc', '修复了重要Bug', '2025-06-16', 5),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 1), 3, 'bug_fix', '重构了代码', '2024-09-30', 3),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 4), 4, 'feature', '更新了文档', '2025-03-27', 7),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 3), 4, 'feature', '优化了性能', '2024-10-12', 8),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 2), 4, 'review', '重构了代码', '2025-03-27', 6),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 2), 5, 'review', '添加了新功能', '2025-06-07', 9),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 0), 5, 'doc', '修复了重要Bug', '2025-07-07', 7),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 3), 5, 'review', '优化了性能', '2025-02-02', 2),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 1), 6, 'bug_fix', '重构了代码', '2025-07-01', 9),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 2), 6, 'feature', '添加了新功能', '2025-07-25', 8),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 1), 6, 'feature', '更新了文档', '2025-02-28', 8),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 4), 7, 'code', '重构了代码', '2025-03-24', 3),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 0), 7, 'feature', '更新了文档', '2024-09-26', 6),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 1), 7, 'feature', '添加了新功能', '2025-04-28', 3),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 0), 8, 'code', '修复了重要Bug', '2025-06-16', 1),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 3), 8, 'review', '添加了新功能', '2025-05-30', 4),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 0), 8, 'feature', '优化了性能', '2025-06-16', 2),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 1), 9, 'bug_fix', '添加了新功能', '2025-08-19', 7),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 2), 9, 'doc', '修复了重要Bug', '2025-02-10', 7),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 1), 9, 'code', '更新了文档', '2025-05-21', 3),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 1), 10, 'doc', '重构了代码', '2025-08-05', 10),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 4), 10, 'code', '优化了性能', '2024-09-27', 9),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 4), 10, 'bug_fix', '更新了文档', '2025-08-13', 1),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 4), 11, 'review', '优化了性能', '2024-10-12', 7),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 3), 11, 'feature', '更新了文档', '2025-08-15', 6),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 3), 11, 'doc', '修复了重要Bug', '2025-05-29', 1),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 3), 12, 'code', '优化了性能', '2025-02-09', 2),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 0), 12, 'review', '修复了重要Bug', '2024-11-14', 10),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 1), 12, 'doc', '优化了性能', '2025-02-22', 9),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 2), 13, 'doc', '优化了性能', '2024-11-16', 8),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 2), 13, 'feature', '修复了重要Bug', '2025-02-01', 9),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 1), 13, 'doc', '修复了重要Bug', '2025-07-07', 5),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 2), 14, 'bug_fix', '修复了重要Bug', '2025-04-23', 6),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 2), 14, 'doc', '修复了重要Bug', '2025-07-15', 7),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 2), 14, 'feature', '优化了性能', '2025-02-25', 1),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 2), 15, 'review', '修复了重要Bug', '2024-10-30', 3),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 0), 15, 'review', '修复了重要Bug', '2025-03-13', 10),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 2), 15, 'bug_fix', '优化了性能', '2025-03-16', 9),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 2), 16, 'doc', '重构了代码', '2024-10-22', 3),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 0), 16, 'review', '更新了文档', '2025-08-07', 3),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 1), 16, 'feature', '修复了重要Bug', '2025-06-04', 10),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 1), 17, 'bug_fix', '添加了新功能', '2024-08-29', 6),
((SELECT member_id FROM osc_members LIMIT 1 OFFSET 2), 17, 'feature', '优化了性能', '2025-01-11', 1);


-- 数据统计
-- 项目总数: 98
-- 成员总数: 104
-- GitHub Token状态: 已配置
-- Gitee Token状态: 已配置

-- 使用说明:
-- 1. 确保所有相关表已创建
-- 2. 根据需要决定是否清空现有数据
-- 3. 按顺序执行INSERT语句
-- 4. 验证数据完整性

SELECT 
    (SELECT COUNT(*) FROM osc_projects) as '项目数量',
    (SELECT COUNT(*) FROM osc_members) as '成员数量',
    (SELECT COUNT(*) FROM os_project_member) as '关联关系',
    (SELECT COUNT(*) FROM osc_contributions) as '贡献记录';
