<template>
  <el-dialog title="选择项目模板" v-model="dialogVisible" width="800px" append-to-body>
    <div class="template-grid">
      <el-card
        v-for="template in templates"
        :key="template.id"
        class="template-card"
        :class="{ 'is-selected': selectedTemplate?.id === template.id }"
        @click="selectTemplate(template)"
      >
        <div class="template-icon">
          <el-icon :size="32">
            <Monitor v-if="template.icon === 'Monitor'" />
            <Platform v-else-if="template.icon === 'Platform'" />
            <Cellphone v-else-if="template.icon === 'Mobile'" />
            <Connection v-else-if="template.icon === 'Connection'" />
            <DataLine v-else-if="template.icon === 'DataLine'" />
            <Tools v-else-if="template.icon === 'Tools'" />
          </el-icon>
        </div>
        <div class="template-info">
          <h3>{{ template.name }}</h3>
          <p>{{ template.description }}</p>
        </div>
        <div class="template-tags">
          <el-tag v-for="tag in template.tags" :key="tag" size="small" class="mx-1">
            {{ tag }}
          </el-tag>
        </div>
      </el-card>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmSelect" :disabled="!selectedTemplate"> 使用此模板 </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue';
import { Monitor, Platform, Cellphone, Connection, DataLine, Cpu, Tools, Share } from '@element-plus/icons-vue';

interface Template {
  id: number;
  name: string;
  description: string;
  icon: string;
  tags: string[];
  config: {
    techStack: string[];
    programmingLanguage: string[];
    description: string;
  };
}

const templates: Template[] = [
  {
    id: 1,
    name: 'Web应用模板',
    description: '包含前后端分离的Web应用基础架构',
    icon: 'Monitor',
    tags: ['Web', '前后端分离', 'RESTful API'],
    config: {
      techStack: ['Vue', 'Spring Boot', 'MySQL', 'Redis', 'Nginx', 'Docker'],
      programmingLanguage: ['TypeScript', 'Java'],
      description: `这是一个现代化的Web应用项目，采用前后端分离架构。

主要特点：
- 前端：Vue 3 + TypeScript + Element Plus
- 后端：Spring Boot + MyBatis-Plus
- 数据库：MySQL + Redis
- 部署：Docker + Nginx

核心功能：
1. 用户认证与授权
2. RBAC权限管理
3. 业务模块化设计
4. 文件上传下载
5. 数据导入导出
6. 系统监控面板
7. 操作日志记录
8. 定时任务调度

技术亮点：
- 前端组件化开发
- RESTful API设计
- 数据库读写分离
- 缓存多级优化
- 容器化部署方案

欢迎开发者参与贡献，一起打造企业级Web应用！`
    }
  },
  {
    id: 2,
    name: '微服务模板',
    description: '基于Spring Cloud的微服务架构模板',
    icon: 'Platform',
    tags: ['微服务', '分布式', '高可用'],
    config: {
      techStack: ['Spring Cloud', 'Nacos', 'Redis', 'RocketMQ', 'Sentinel', 'Seata'],
      programmingLanguage: ['Java'],
      description: `这是一个基于Spring Cloud生态的微服务架构项目。

架构特点：
1. 服务注册与发现 (Nacos)
2. 配置中心 (Nacos Config)
3. 服务网关 (Spring Cloud Gateway)
4. 负载均衡 (Spring Cloud LoadBalancer)
5. 服务熔断 (Sentinel)
6. 分布式事务 (Seata)
7. 消息队列 (RocketMQ)
8. 分布式缓存 (Redis Cluster)
9. 链路追踪 (SkyWalking)
10. 统一认证 (OAuth2 + JWT)

最佳实践：
- 领域驱动设计 (DDD)
- 服务网格化管理
- 容器化部署
- 自动化运维
- 监控告警体系

适用场景：
- 大型分布式系统
- 高并发业务场景
- 复杂业务领域
- 敏捷开发团队

欢迎对微服务架构感兴趣的开发者加入！`
    }
  },
  {
    id: 3,
    name: '移动应用模板',
    description: '移动应用开发模板',
    icon: 'Mobile',
    tags: ['移动端', 'APP', '跨平台'],
    config: {
      techStack: ['Flutter', 'Firebase', 'GraphQL', 'WebSocket', 'SQLite'],
      programmingLanguage: ['Dart'],
      description: `这是一个基于Flutter的跨平台移动应用项目。

技术特点：
1. 跨平台开发 (iOS & Android)
2. 响应式UI设计
3. 状态管理 (Provider/Riverpod)
4. 路由管理 (Go Router)
5. 网络请求 (Dio + GraphQL)
6. 实时通信 (WebSocket)
7. 本地存储 (SQLite + Hive)
8. 推送通知 (Firebase)

功能特性：
- Material Design 3
- 深色模式支持
- 多语言国际化
- 离线数据同步
- 文件上传下载
- 图片预览编辑
- 视频播放器
- 地图定位服务

性能优化：
- 懒加载和预加载
- 图片缓存管理
- 网络请求优化
- 内存泄漏检测

欢迎移动开发者加入，打造精美的移动应用！`
    }
  },
  {
    id: 4,
    name: '中间件模板',
    description: '开源中间件项目模板',
    icon: 'Connection',
    tags: ['中间件', '高性能', '可扩展'],
    config: {
      techStack: ['Netty', 'RocketMQ', 'ZooKeeper', 'Prometheus', 'Grafana'],
      programmingLanguage: ['Java'],
      description: `这是一个高性能的中间件项目，专注于解决分布式系统通信问题。

核心特性：
1. 高性能网络通信 (Netty)
2. 服务注册发现 (ZooKeeper)
3. 消息序列化 (Protocol Buffers)
4. 负载均衡算法
5. 熔断限流机制
6. 请求重试策略
7. 异步编程模型
8. 监控指标采集

性能指标：
- 低延迟 (P99 < 10ms)
- 高吞吐量 (10w+ TPS)
- 连接池优化
- 内存池管理
- GC调优方案

可靠性保证：
- 故障自动恢复
- 集群选主机制
- 数据一致性
- 消息可靠性
- 监控告警

欢迎系统底层开发者参与，一起探索高性能编程的奥秘！`
    }
  },
  {
    id: 5,
    name: '数据处理模板',
    description: '大数据处理项目模板',
    icon: 'DataLine',
    tags: ['大数据', '实时计算', '数据分析'],
    config: {
      techStack: ['Spark', 'Kafka', 'Elasticsearch', 'Flink', 'ClickHouse', 'Airflow'],
      programmingLanguage: ['Scala', 'Java', 'Python'],
      description: `这是一个企业级大数据处理平台项目。

系统架构：
1. 数据采集层 (Kafka, Flume)
2. 存储层 (HDFS, HBase)
3. 计算层 (Spark, Flink)
4. 查询层 (ClickHouse, Elasticsearch)
5. 可视化层 (Superset)
6. 调度层 (Airflow)

数据处理能力：
- 批处理 (Spark Batch)
- 流处理 (Spark Streaming)
- 实时计算 (Flink)
- 即席查询 (Presto)
- 数据仓库 (Hive)
- 数据湖 (Delta Lake)

应用场景：
- 用户行为分析
- 实时监控预警
- 智能推荐系统
- 风险控制模型
- 报表统计分析

欢迎大数据开发者加入，探索数据价值！`
    }
  },
  {
    id: 6,
    name: '系统工具模板',
    description: '系统工具开发模板',
    icon: 'Tools',
    tags: ['工具', '系统优化', '性能监控'],
    config: {
      techStack: ['Spring Boot', 'Vue', 'Prometheus', 'Grafana', 'ELK'],
      programmingLanguage: ['Java', 'TypeScript'],
      description: `这是一个专注于系统监控和性能优化的工具平台。

主要功能：
1. 系统监控
   - CPU/内存/磁盘监控
   - JVM性能分析
   - GC日志分析
   - 线程堆栈分析
   - 数据库性能监控

2. 日志管理
   - 分布式日志收集
   - 实时日志检索
   - 日志告警规则
   - 异常统计分析
   - 操作审计日志

3. 性能优化
   - 性能瓶颈分析
   - SQL语句优化
   - 缓存命中率分析
   - 代码热点分析
   - 内存泄漏检测

4. 系统诊断
   - 健康检查
   - 链路追踪
   - 依赖分析
   - 配置中心
   - 调用统计

欢迎系统优化爱好者加入，提升系统性能！`
    }
  }
];

const props = defineProps<{
  modelValue: boolean;
}>();

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void;
  (e: 'select', template: Template): void;
}>();

const dialogVisible = ref(props.modelValue);
const selectedTemplate = ref<Template | null>(null);

const selectTemplate = (template: Template) => {
  selectedTemplate.value = template;
};

const confirmSelect = () => {
  if (selectedTemplate.value) {
    emit('select', selectedTemplate.value);
    dialogVisible.value = false;
  }
};

watch(
  () => props.modelValue,
  (val) => {
    dialogVisible.value = val;
  }
);

watch(
  () => dialogVisible.value,
  (val) => {
    emit('update:modelValue', val);
    if (!val) {
      selectedTemplate.value = null;
    }
  }
);
</script>

<style scoped>
.template-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 10px;
}

.template-card {
  cursor: pointer;
  transition: all 0.3s;
  border: 2px solid transparent;

  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  }

  &.is-selected {
    border-color: var(--el-color-primary);
    background-color: var(--el-color-primary-light-9);
  }
}

.template-icon {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;

  .el-icon {
    color: var(--el-color-primary);
  }
}

.template-info {
  text-align: center;
  margin-bottom: 16px;

  h3 {
    margin: 0 0 8px;
    font-size: 16px;
    font-weight: 500;
  }

  p {
    margin: 0;
    font-size: 14px;
    color: var(--el-text-color-secondary);
    line-height: 1.4;
  }
}

.template-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  justify-content: center;
}

.mx-1 {
  margin: 0 4px;
}

.dialog-footer {
  text-align: right;
}
</style>
