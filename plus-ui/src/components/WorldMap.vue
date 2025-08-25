<template>
  <div class="world-map-container">
    <div class="map-header" v-if="showTitle">
      <h3>访客分布统计</h3>
      <p>基于IP地址统计的访客数量分布</p>
    </div>

    <div class="map-wrapper">
      <div ref="mapContainer" class="map-chart" :style="{ height: mapHeight }"></div>

      <!-- 数据图例 -->
      <div class="map-legend">
        <div class="legend-title">访客数量</div>
        <div class="legend-items">
          <div v-for="item in legendData" :key="item.label" class="legend-item">
            <div class="legend-color" :style="{ backgroundColor: item.color }"></div>
            <span class="legend-text">{{ item.label }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 统计信息 -->
    <div class="map-stats" v-if="showStats">
      <div class="stats-grid">
        <div class="stat-item">
          <div class="stat-value">{{ totalVisitors.toLocaleString() }}</div>
          <div class="stat-label">总访客数</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ countryCount }}</div>
          <div class="stat-label">覆盖国家</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ topCountry?.name || '--' }}</div>
          <div class="stat-label">访问最多</div>
        </div>
        <div class="stat-item">
          <div class="stat-value">{{ (((topCountry?.value || 0) / totalVisitors) * 100).toFixed(1) }}%</div>
          <div class="stat-label">占比最高</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch } from 'vue';
import * as echarts from 'echarts';
import 'echarts/lib/chart/scatter';
import 'echarts/lib/component/tooltip';

// Props定义
interface Props {
  showTitle?: boolean;
  showStats?: boolean;
  mapHeight?: string;
  autoRefresh?: boolean;
  refreshInterval?: number; // 秒
}

const props = withDefaults(defineProps<Props>(), {
  showTitle: true,
  showStats: true,
  mapHeight: '400px',
  autoRefresh: false,
  refreshInterval: 300 // 5分钟
});

// 响应式数据
const mapContainer = ref<HTMLElement>();
let mapChart: echarts.ECharts | null = null;
const loading = ref(true);
let refreshTimer: NodeJS.Timeout | null = null;

// 访客数据（模拟真实数据）
const visitorData = ref([
  { name: 'China', value: 52300, code: 'CN' },
  { name: 'United States', value: 26800, code: 'US' },
  { name: 'Japan', value: 17900, code: 'JP' },
  { name: 'Germany', value: 11200, code: 'DE' },
  { name: 'South Korea', value: 10400, code: 'KR' },
  { name: 'United Kingdom', value: 8900, code: 'GB' },
  { name: 'France', value: 7300, code: 'FR' },
  { name: 'Canada', value: 6800, code: 'CA' },
  { name: 'Australia', value: 5600, code: 'AU' },
  { name: 'India', value: 5200, code: 'IN' },
  { name: 'Brazil', value: 4800, code: 'BR' },
  { name: 'Singapore', value: 4100, code: 'SG' },
  { name: 'Netherlands', value: 3700, code: 'NL' },
  { name: 'Switzerland', value: 3200, code: 'CH' },
  { name: 'Sweden', value: 2900, code: 'SE' },
  { name: 'Italy', value: 2700, code: 'IT' },
  { name: 'Spain', value: 2400, code: 'ES' },
  { name: 'Russia', value: 2100, code: 'RU' },
  { name: 'Thailand', value: 1800, code: 'TH' },
  { name: 'Malaysia', value: 1500, code: 'MY' },
  { name: 'Indonesia', value: 1300, code: 'ID' },
  { name: 'Philippines', value: 1100, code: 'PH' },
  { name: 'Vietnam', value: 950, code: 'VN' },
  { name: 'Hong Kong', value: 890, code: 'HK' },
  { name: 'Taiwan', value: 780, code: 'TW' },
  { name: 'New Zealand', value: 650, code: 'NZ' },
  { name: 'Norway', value: 580, code: 'NO' },
  { name: 'Denmark', value: 520, code: 'DK' },
  { name: 'Finland', value: 480, code: 'FI' },
  { name: 'Belgium', value: 450, code: 'BE' }
]);

// 计算统计数据
const totalVisitors = computed(() => visitorData.value.reduce((sum, item) => sum + item.value, 0));

const countryCount = computed(() => visitorData.value.length);

const topCountry = computed(() => visitorData.value.reduce((max, item) => (item.value > max.value ? item : max), visitorData.value[0]));

// 图例数据
const legendData = computed(() => [
  { label: '50k+', color: '#0d47a1' },
  { label: '20k-50k', color: '#1976d2' },
  { label: '10k-20k', color: '#42a5f5' },
  { label: '5k-10k', color: '#90caf9' },
  { label: '1k-5k', color: '#bbdefb' },
  { label: '<1k', color: '#e3f2fd' }
]);

// 获取颜色映射
const getColorForValue = (value: number): string => {
  if (value >= 50000) return '#0d47a1';
  if (value >= 20000) return '#1976d2';
  if (value >= 10000) return '#42a5f5';
  if (value >= 5000) return '#90caf9';
  if (value >= 1000) return '#bbdefb';
  return '#e3f2fd';
};

// 初始化地图
const initMap = async () => {
  if (!mapContainer.value) return;

  try {
    mapChart = echarts.init(mapContainer.value);

    // 使用简化的散点图替代地图，避免地图数据依赖问题
    const option: echarts.EChartsOption = {
      tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(255, 255, 255, 0.95)',
        borderColor: '#e2e8f0',
        borderWidth: 1,
        textStyle: {
          color: '#374151',
          fontSize: 14
        },
        formatter: (params: any) => {
          if (params.data) {
            const percentage = ((params.data.value / totalVisitors.value) * 100).toFixed(2);
            return `
              <div style="padding: 8px;">
                <div style="font-weight: 600; margin-bottom: 4px;">${params.data.name}</div>
                <div style="color: #6b7280;">访客数: <strong>${params.data.value.toLocaleString()}</strong></div>
                <div style="color: #6b7280;">占比: <strong>${percentage}%</strong></div>
              </div>
            `;
          }
          return `${params.name}: 暂无数据`;
        }
      },
      grid: {
        left: '10%',
        right: '10%',
        top: '10%',
        bottom: '10%'
      },
      xAxis: {
        type: 'category',
        data: visitorData.value.map((item) => item.name),
        axisLabel: {
          rotate: 45,
          fontSize: 10
        },
        axisTick: {
          show: false
        },
        axisLine: {
          show: false
        }
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          fontSize: 12
        },
        axisTick: {
          show: false
        },
        axisLine: {
          show: false
        },
        splitLine: {
          lineStyle: {
            color: '#f0f0f0'
          }
        }
      },
      series: [
        {
          type: 'scatter',
          data: visitorData.value.map((item) => ({
            name: item.name,
            value: item.value,
            itemStyle: {
              color: getColorForValue(item.value),
              borderColor: 'rgba(255, 255, 255, 0.8)',
              borderWidth: 1
            }
          })),
          symbolSize: (value: any) => {
            const maxValue = Math.max(...visitorData.value.map((item) => item.value));
            return Math.max(8, (value / maxValue) * 30);
          },
          emphasis: {
            itemStyle: {
              borderColor: '#10b981',
              borderWidth: 2,
              shadowColor: 'rgba(16, 185, 129, 0.5)',
              shadowBlur: 15
            }
          }
        }
      ],
      animation: true,
      animationDuration: 1000,
      animationEasing: 'cubicOut'
    };

    mapChart.setOption(option);
    loading.value = false;

    // 监听窗口大小变化
    const handleResize = () => {
      mapChart?.resize();
    };
    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
    };
  } catch (error) {
    console.error('图表初始化失败:', error);
    loading.value = false;
  }
};

// 更新图表数据
const updateMapData = () => {
  if (!mapChart) return;

  // 模拟数据更新（添加小幅随机变化）
  visitorData.value = visitorData.value.map((item) => ({
    ...item,
    value: Math.max(0, item.value + Math.floor((Math.random() - 0.5) * 100))
  }));

  const option = mapChart.getOption() as any;
  option.series[0].data = visitorData.value.map((item) => ({
    name: item.name,
    value: item.value,
    itemStyle: {
      color: getColorForValue(item.value),
      borderColor: 'rgba(255, 255, 255, 0.8)',
      borderWidth: 1
    }
  }));

  mapChart.setOption(option);
  console.log('📊 图表数据已更新');
};

// 开始自动刷新
const startAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer);
  }

  refreshTimer = setInterval(() => {
    updateMapData();
  }, props.refreshInterval * 1000);
};

// 停止自动刷新
const stopAutoRefresh = () => {
  if (refreshTimer) {
    clearInterval(refreshTimer);
    refreshTimer = null;
  }
};

// 监听自动刷新属性变化
watch(
  () => props.autoRefresh,
  (newValue) => {
    if (newValue) {
      startAutoRefresh();
    } else {
      stopAutoRefresh();
    }
  }
);

// 生命周期
onMounted(async () => {
  await initMap();

  if (props.autoRefresh) {
    startAutoRefresh();
  }
});

onUnmounted(() => {
  stopAutoRefresh();
  if (mapChart) {
    mapChart.dispose();
    mapChart = null;
  }
});

// 暴露给父组件的方法
defineExpose({
  updateData: updateMapData,
  refreshData: () => {
    updateMapData();
  }
});
</script>

<style scoped>
.world-map-container {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
  transition: all 0.3s ease;
  overflow: hidden;
}

.world-map-container:hover {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
}

.map-header {
  margin-bottom: 24px;
  text-align: center;
}

.map-header h3 {
  margin: 0 0 8px 0;
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: -0.025em;
}

.map-header p {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.map-wrapper {
  position: relative;
  margin-bottom: 24px;
}

.map-chart {
  width: 100%;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
}

.map-legend {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 8px;
  padding: 16px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  min-width: 120px;
}

.legend-title {
  font-size: 13px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 12px;
  text-align: center;
}

.legend-items {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.legend-color {
  width: 16px;
  height: 12px;
  border-radius: 2px;
  flex-shrink: 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.legend-text {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
}

.map-stats {
  border-top: 1px solid #e5e7eb;
  padding-top: 20px;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(120px, 1fr));
  gap: 20px;
}

.stat-item {
  text-align: center;
  padding: 16px;
  background: linear-gradient(135deg, #f9fafb 0%, #f3f4f6 100%);
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  transition: all 0.3s ease;
}

.stat-item:hover {
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
  border-color: #d1fae5;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.1);
}

.stat-value {
  font-size: 20px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
  letter-spacing: -0.025em;
}

.stat-label {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .world-map-container {
    padding: 16px;
  }

  .map-chart {
    height: 300px !important;
  }

  .map-legend {
    position: relative;
    top: auto;
    right: auto;
    margin-top: 16px;
    width: 100%;
  }

  .legend-items {
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: center;
  }

  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 12px;
  }

  .stat-item {
    padding: 12px;
  }

  .stat-value {
    font-size: 16px;
  }
}

@media (max-width: 480px) {
  .map-chart {
    height: 250px !important;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }
}

/* 加载状态样式 */
.map-chart::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  animation: shimmer 2s infinite;
  z-index: 1;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}
</style>
