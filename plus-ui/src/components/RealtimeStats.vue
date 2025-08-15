<template>
  <div class="realtime-stats">
    <!-- 统计数据刷新状态 -->
    <div class="stats-header" v-if="showHeader">
      <div class="stats-title">
        <h3>社区实时统计</h3>
        <p class="last-updated">最后更新: {{ formatTime(lastUpdated) }}</p>
      </div>
      <div class="stats-actions">
        <el-button 
          type="primary" 
          :loading="loading" 
          size="small" 
          @click="refreshData"
          :disabled="loading"
        >
          <el-icon><Refresh /></el-icon>
          {{ loading ? '更新中...' : '刷新数据' }}
        </el-button>
        <el-switch
          v-model="autoRefresh"
          inline-prompt
          active-text="自动"
          inactive-text="手动"
          @change="toggleAutoRefresh"
        />
      </div>
    </div>

    <!-- 统计卡片网格 -->
    <div class="stats-grid" :class="{ 'compact': compact }">
      <div 
        class="stat-card" 
        v-for="(stat, index) in statsData" 
        :key="stat.key"
        :class="[stat.type, { 'loading': loading }]"
        :style="{ animationDelay: `${index * 0.1}s` }"
      >
        <div class="stat-icon" :style="{ background: stat.gradient }">
          <el-icon>
            <component :is="stat.icon" />
          </el-icon>
        </div>
        
        <div class="stat-content">
          <div class="stat-value">
            <span v-if="!loading">{{ stat.value.toLocaleString() }}</span>
            <el-skeleton-item v-else variant="text" style="width: 60px" />
          </div>
          <div class="stat-label">{{ stat.label }}</div>
          <div class="stat-change" :class="stat.changeType">
            <el-icon v-if="stat.changeType === 'increase'"><TrendCharts /></el-icon>
            <el-icon v-else-if="stat.changeType === 'decrease'"><Bottom /></el-icon>
            <span>{{ stat.change }}</span>
          </div>
        </div>

        <div class="stat-trend" v-if="stat.trend">
          <mini-chart :data="stat.trend" :color="stat.color" />
        </div>
      </div>
    </div>

    <!-- 数据源状态 -->
    <div class="data-source-status" v-if="showStatus">
      <el-collapse v-model="activeCollapse" accordion>
        <el-collapse-item title="数据源状态" name="status">
          <div class="source-grid">
            <div 
              class="source-item" 
              v-for="source in dataSources" 
              :key="source.name"
              :class="source.status"
            >
              <div class="source-indicator" :class="source.status"></div>
              <div class="source-info">
                <div class="source-name">{{ source.name }}</div>
                <div class="source-details">
                  <span class="source-time">{{ formatTime(source.lastUpdate) }}</span>
                  <span class="source-cache">缓存: {{ source.cacheStatus }}</span>
                </div>
              </div>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch } from 'vue';
import { 
  Refresh, 
  TrendCharts, 
  Bottom, 
  Connection,
  User,
  Star,
  Share,
  Download,
  Warning
} from '@element-plus/icons-vue';
import { getDashboardData, refreshAllData, getDataUpdateStatus, type CommunityStats } from '@/api/community-enhanced';

// Props
interface Props {
  compact?: boolean;
  showHeader?: boolean;
  showStatus?: boolean;
  autoRefreshInterval?: number;
}

const props = withDefaults(defineProps<Props>(), {
  compact: false,
  showHeader: true,
  showStatus: false,
  autoRefreshInterval: 5 * 60 * 1000 // 5分钟
});

// 响应式数据
const loading = ref(false);
const lastUpdated = ref(new Date());
const autoRefresh = ref(false);
const activeCollapse = ref('');

// 统计数据
const stats = ref<CommunityStats>({
  totalProjects: 0,
  totalStars: 0,
  totalContributors: 0,
  totalForks: 0,
  activeProjects: 0,
  newProjects: 0,
  lastUpdated: new Date().toISOString()
});

// 数据源状态
const dataSources = ref([
  { name: 'Gitee API', status: 'active', lastUpdate: new Date(), cacheStatus: '5分钟' },
  { name: '项目统计', status: 'active', lastUpdate: new Date(), cacheStatus: '5分钟' },
  { name: '贡献者数据', status: 'active', lastUpdate: new Date(), cacheStatus: '15分钟' },
]);

// 自动刷新定时器
let refreshTimer: NodeJS.Timeout | null = null;

// 计算统计数据显示
const statsData = computed(() => [
  {
    key: 'projects',
    label: '项目总数',
    value: stats.value.totalProjects,
    change: `+${stats.value.newProjects} 新增`,
    changeType: 'increase',
    icon: 'Connection',
    color: '#3b82f6',
    gradient: 'linear-gradient(135deg, #3b82f6, #1d4ed8)',
    type: 'primary',
    trend: generateTrendData(stats.value.totalProjects)
  },
  {
    key: 'contributors',
    label: '活跃贡献者',
    value: stats.value.totalContributors,
    change: `${stats.value.totalContributors > 1000 ? '+8%' : '+5%'} 本周`,
    changeType: 'increase',
    icon: 'User',
    color: '#10b981',
    gradient: 'linear-gradient(135deg, #10b981, #059669)',
    type: 'success',
    trend: generateTrendData(stats.value.totalContributors)
  },
  {
    key: 'stars',
    label: 'Star总数',
    value: stats.value.totalStars,
    change: '+12% 本周',
    changeType: 'increase',
    icon: 'Star',
    color: '#f59e0b',
    gradient: 'linear-gradient(135deg, #f59e0b, #d97706)',
    type: 'warning',
    trend: generateTrendData(stats.value.totalStars)
  },
  {
    key: 'forks',
    label: 'Fork总数',
    value: stats.value.totalForks,
    change: '+15% 本周',
    changeType: 'increase',
    icon: 'Share',
    color: '#8b5cf6',
    gradient: 'linear-gradient(135deg, #8b5cf6, #7c3aed)',
    type: 'info',
    trend: generateTrendData(stats.value.totalForks)
  },
  {
    key: 'active',
    label: '活跃项目',
    value: stats.value.activeProjects,
    change: `${stats.value.activeProjects > stats.value.totalProjects * 0.7 ? '高活跃度' : '正常'}`,
    changeType: stats.value.activeProjects > stats.value.totalProjects * 0.7 ? 'increase' : 'stable',
    icon: 'Download',
    color: '#6366f1',
    gradient: 'linear-gradient(135deg, #6366f1, #4f46e5)',
    type: 'primary',
    trend: generateTrendData(stats.value.activeProjects)
  },
  {
    key: 'issues',
    label: 'Issue处理',
    value: Math.floor(stats.value.totalProjects * 25.6), // 模拟issue数量
    change: '+2% 本周',
    changeType: 'increase',
    icon: 'Warning',
    color: '#ef4444',
    gradient: 'linear-gradient(135deg, #ef4444, #dc2626)',
    type: 'danger',
    trend: generateTrendData(Math.floor(stats.value.totalProjects * 25.6))
  }
]);

// 生成趋势数据
function generateTrendData(currentValue: number): number[] {
  const points = 7;
  const data = [];
  const baseValue = Math.max(0, currentValue - 100);
  
  for (let i = 0; i < points; i++) {
    const variation = Math.random() * 40 - 20;
    const value = baseValue + (currentValue - baseValue) * (i / (points - 1)) + variation;
    data.push(Math.max(0, Math.floor(value)));
  }
  
  return data;
}

// 加载数据
const loadData = async (forceRefresh = false) => {
  try {
    loading.value = true;
    console.log('🔄 正在加载社区统计数据...');
    
    const data = forceRefresh ? await refreshAllData() : await getDashboardData();
    stats.value = data.stats;
    lastUpdated.value = new Date(data.stats.lastUpdated);
    
    // 更新数据源状态
    const updateStatus = getDataUpdateStatus();
    dataSources.value = dataSources.value.map((source, index) => ({
      ...source,
      status: 'active',
      lastUpdate: lastUpdated.value,
      cacheStatus: updateStatus[index] ? `${Math.floor(updateStatus[index].age / 1000)}秒前` : '未知'
    }));
    
    console.log('✅ 社区统计数据加载完成');
  } catch (error) {
    console.error('❌ 加载统计数据失败:', error);
    // 更新数据源状态为错误
    dataSources.value = dataSources.value.map(source => ({
      ...source,
      status: 'error'
    }));
  } finally {
    loading.value = false;
  }
};

// 刷新数据
const refreshData = async () => {
  await loadData(true);
};

// 格式化时间
const formatTime = (date: Date | string): string => {
  const d = new Date(date);
  const now = new Date();
  const diffMs = now.getTime() - d.getTime();
  const diffSeconds = Math.floor(diffMs / 1000);
  const diffMinutes = Math.floor(diffSeconds / 60);
  
  if (diffSeconds < 60) {
    return `${diffSeconds}秒前`;
  } else if (diffMinutes < 60) {
    return `${diffMinutes}分钟前`;
  } else {
    return d.toLocaleString();
  }
};

// 切换自动刷新
const toggleAutoRefresh = (enabled: boolean) => {
  if (enabled) {
    startAutoRefresh();
  } else {
    stopAutoRefresh();
  }
};

// 开始自动刷新
const startAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer);
  }
  
  refreshTimer = setInterval(() => {
    loadData();
  }, props.autoRefreshInterval);
  
  console.log(`✅ 自动刷新已启用，间隔 ${props.autoRefreshInterval / 1000} 秒`);
};

// 停止自动刷新
const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer);
    refreshTimer = null;
    console.log('⏸️ 自动刷新已停止');
  }
};

// 监听autoRefresh变化
watch(autoRefresh, (newValue) => {
  toggleAutoRefresh(newValue);
});

// 页面加载时获取数据
onMounted(async () => {
  await loadData();
});

// 清理定时器
onUnmounted(() => {
  stopAutoRefresh();
});

// 暴露方法给父组件
defineExpose({
  refreshData,
  stats: computed(() => stats.value)
});
</script>

<style scoped>
.realtime-stats {
  width: 100%;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding: 16px 20px;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(10px);
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.stats-title h3 {
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.last-updated {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
}

.stats-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(6, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.stats-grid.compact {
  grid-template-columns: repeat(6, 1fr);
  gap: 12px;
}

.stat-card {
  position: relative;
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.06);
  border: 1px solid rgba(255, 255, 255, 0.1);
  transition: all 0.3s ease;
  overflow: hidden;
  animation: fadeInUp 0.6s ease-out;
  min-height: 120px;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.stat-card.loading {
  opacity: 0.7;
  pointer-events: none;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: var(--gradient, linear-gradient(90deg, #3b82f6, #1d4ed8));
  border-radius: 16px 16px 0 0;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 16px;
  position: relative;
  overflow: hidden;
}

.stat-icon::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.stat-card:hover .stat-icon::before {
  left: 100%;
}

.stat-icon .el-icon {
  font-size: 24px;
  color: white;
  z-index: 1;
}

.stat-content {
  position: relative;
  z-index: 1;
}

.stat-value {
  font-size: 26px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1.2;
  margin-bottom: 6px;
  letter-spacing: -0.025em;
}

.stat-label {
  font-size: 13px;
  color: #6b7280;
  font-weight: 500;
  margin-bottom: 8px;
}

.stat-change {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 600;
}

.stat-change.increase {
  color: #10b981;
}

.stat-change.decrease {
  color: #ef4444;
}

.stat-change.stable {
  color: #6b7280;
}

.stat-trend {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 80px;
  height: 40px;
  opacity: 0.1;
}

.data-source-status {
  margin-top: 24px;
}

.source-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 16px;
  padding: 16px 0;
}

.source-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  background: #f9fafb;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.source-item:hover {
  background: #f3f4f6;
}

.source-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  flex-shrink: 0;
}

.source-indicator.active {
  background: #10b981;
  box-shadow: 0 0 8px rgba(16, 185, 129, 0.4);
}

.source-indicator.error {
  background: #ef4444;
  box-shadow: 0 0 8px rgba(239, 68, 68, 0.4);
}

.source-info {
  flex: 1;
}

.source-name {
  font-size: 14px;
  font-weight: 600;
  color: #1f2937;
  margin-bottom: 4px;
}

.source-details {
  display: flex;
  gap: 8px;
  font-size: 12px;
  color: #6b7280;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .stats-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: 14px;
  }
  
  .stats-grid.compact {
    grid-template-columns: repeat(3, 1fr);
    gap: 12px;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }
  
  .stats-grid.compact {
    grid-template-columns: repeat(2, 1fr);
    gap: 10px;
  }
  
  .stat-card {
    padding: 16px;
    min-height: 100px;
  }
  
  .stat-icon {
    width: 40px;
    height: 40px;
  }
  
  .stat-icon .el-icon {
    font-size: 20px;
  }
  
  .stat-value {
    font-size: 20px;
  }
  
  .stats-header {
    flex-direction: column;
    gap: 16px;
    align-items: flex-start;
  }
  
  .stats-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>