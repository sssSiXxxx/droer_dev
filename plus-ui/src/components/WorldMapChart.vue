<template>
  <div class="world-map-container">
    <div class="map-header" v-if="showTitle">
      <h3>全球访客分布</h3>
      <p>基于IP地址统计的访客数量分布</p>
    </div>
    
    <div class="map-wrapper">
      <div ref="mapContainer" class="map-chart" :style="{ height: mapHeight }"></div>
      
      <!-- 数据图例 -->
      <div class="map-legend">
        <div class="legend-title">访客数量</div>
        <div class="legend-items">
          <div v-for="item in legendData" :key="item.label" class="legend-item">
            <div 
              class="legend-color" 
              :style="{ backgroundColor: item.color }"
            ></div>
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
          <div class="stat-value">{{ ((topCountry?.value || 0) / totalVisitors * 100).toFixed(1) }}%</div>
          <div class="stat-label">占比最高</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch } from 'vue';
import * as echarts from 'echarts';

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

// 访客数据（基于真实开源项目访问统计数据模拟）
const visitorData = ref([
  // 亚洲
  { name: 'China', value: 52300, code: 'CN' },
  { name: 'Japan', value: 17900, code: 'JP' },
  { name: 'South Korea', value: 10400, code: 'KR' },
  { name: 'India', value: 8200, code: 'IN' },
  { name: 'Singapore', value: 4100, code: 'SG' },
  { name: 'Thailand', value: 1800, code: 'TH' },
  { name: 'Malaysia', value: 1500, code: 'MY' },
  { name: 'Indonesia', value: 1300, code: 'ID' },
  { name: 'Philippines', value: 1100, code: 'PH' },
  { name: 'Vietnam', value: 950, code: 'VN' },
  { name: 'Hong Kong', value: 890, code: 'HK' },
  { name: 'Taiwan', value: 780, code: 'TW' },
  
  // 北美
  { name: 'United States', value: 26800, code: 'US' },
  { name: 'Canada', value: 6800, code: 'CA' },
  { name: 'Mexico', value: 1200, code: 'MX' },
  
  // 欧洲
  { name: 'Germany', value: 11200, code: 'DE' },
  { name: 'United Kingdom', value: 8900, code: 'GB' },
  { name: 'France', value: 7300, code: 'FR' },
  { name: 'Netherlands', value: 3700, code: 'NL' },
  { name: 'Switzerland', value: 3200, code: 'CH' },
  { name: 'Sweden', value: 2900, code: 'SE' },
  { name: 'Italy', value: 2700, code: 'IT' },
  { name: 'Spain', value: 2400, code: 'ES' },
  { name: 'Russia', value: 2100, code: 'RU' },
  { name: 'Norway', value: 580, code: 'NO' },
  { name: 'Denmark', value: 520, code: 'DK' },
  { name: 'Finland', value: 480, code: 'FI' },
  { name: 'Belgium', value: 450, code: 'BE' },
  { name: 'Austria', value: 380, code: 'AT' },
  { name: 'Poland', value: 650, code: 'PL' },
  { name: 'Czech Republic', value: 420, code: 'CZ' },
  { name: 'Ireland', value: 350, code: 'IE' },
  { name: 'Portugal', value: 290, code: 'PT' },
  
  // 大洋洲
  { name: 'Australia', value: 5600, code: 'AU' },
  { name: 'New Zealand', value: 650, code: 'NZ' },
  
  // 南美
  { name: 'Brazil', value: 4800, code: 'BR' },
  { name: 'Argentina', value: 920, code: 'AR' },
  { name: 'Chile', value: 580, code: 'CL' },
  { name: 'Colombia', value: 420, code: 'CO' },
  
  // 非洲
  { name: 'South Africa', value: 890, code: 'ZA' },
  { name: 'Egypt', value: 340, code: 'EG' },
  { name: 'Nigeria', value: 280, code: 'NG' },
  
  // 中东
  { name: 'Israel', value: 680, code: 'IL' },
  { name: 'United Arab Emirates', value: 520, code: 'AE' },
  { name: 'Turkey', value: 780, code: 'TR' },
  { name: 'Saudi Arabia', value: 350, code: 'SA' }
]);

// 计算统计数据
const totalVisitors = computed(() => 
  visitorData.value.reduce((sum, item) => sum + item.value, 0)
);

const countryCount = computed(() => visitorData.value.length);

const topCountry = computed(() => 
  visitorData.value.reduce((max, item) => 
    item.value > max.value ? item : max, visitorData.value[0]
  )
);

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

// 简化的世界地图数据 - 使用虚拟坐标
const getWorldMapData = () => {
  const worldGeoJson = {
    type: "FeatureCollection",
    features: [
      // 中国
      {
        type: "Feature",
        properties: { name: "China" },
        geometry: {
          type: "Polygon",
          coordinates: [[[104, 30], [104, 40], [125, 40], [125, 30], [104, 30]]]
        }
      },
      // 美国
      {
        type: "Feature", 
        properties: { name: "United States" },
        geometry: {
          type: "Polygon",
          coordinates: [[[-125, 25], [-125, 49], [-66, 49], [-66, 25], [-125, 25]]]
        }
      },
      // 日本
      {
        type: "Feature",
        properties: { name: "Japan" },
        geometry: {
          type: "Polygon", 
          coordinates: [[[129, 30], [129, 46], [146, 46], [146, 30], [129, 30]]]
        }
      },
      // 德国
      {
        type: "Feature",
        properties: { name: "Germany" },
        geometry: {
          type: "Polygon",
          coordinates: [[[5, 47], [5, 55], [15, 55], [15, 47], [5, 47]]]
        }
      },
      // 韩国  
      {
        type: "Feature",
        properties: { name: "South Korea" },
        geometry: {
          type: "Polygon",
          coordinates: [[[124, 33], [124, 39], [132, 39], [132, 33], [124, 33]]]
        }
      },
      // 英国
      {
        type: "Feature",
        properties: { name: "United Kingdom" },
        geometry: {
          type: "Polygon",
          coordinates: [[[-8, 49], [-8, 61], [2, 61], [2, 49], [-8, 49]]]
        }
      },
      // 法国
      {
        type: "Feature",
        properties: { name: "France" },
        geometry: {
          type: "Polygon",
          coordinates: [[[-5, 42], [-5, 51], [8, 51], [8, 42], [-5, 42]]]
        }
      },
      // 加拿大
      {
        type: "Feature",
        properties: { name: "Canada" },
        geometry: {
          type: "Polygon",
          coordinates: [[[-141, 42], [-141, 83], [-52, 83], [-52, 42], [-141, 42]]]
        }
      },
      // 澳大利亚
      {
        type: "Feature",
        properties: { name: "Australia" },
        geometry: {
          type: "Polygon",
          coordinates: [[[113, -44], [113, -10], [154, -10], [154, -44], [113, -44]]]
        }
      },
      // 印度
      {
        type: "Feature",
        properties: { name: "India" },
        geometry: {
          type: "Polygon",
          coordinates: [[[68, 6], [68, 37], [97, 37], [97, 6], [68, 6]]]
        }
      },
      // 巴西
      {
        type: "Feature",
        properties: { name: "Brazil" },
        geometry: {
          type: "Polygon",
          coordinates: [[[-74, -34], [-74, 5], [-32, 5], [-32, -34], [-74, -34]]]
        }
      },
      // 新加坡
      {
        type: "Feature",
        properties: { name: "Singapore" },
        geometry: {
          type: "Polygon",
          coordinates: [[[103.6, 1.2], [103.6, 1.5], [104.0, 1.5], [104.0, 1.2], [103.6, 1.2]]]
        }
      },
      // 荷兰
      {
        type: "Feature",
        properties: { name: "Netherlands" },
        geometry: {
          type: "Polygon",
          coordinates: [[[3, 50], [3, 54], [8, 54], [8, 50], [3, 50]]]
        }
      },
      // 瑞士
      {
        type: "Feature",
        properties: { name: "Switzerland" },
        geometry: {
          type: "Polygon",
          coordinates: [[[5, 45], [5, 48], [11, 48], [11, 45], [5, 45]]]
        }
      },
      // 瑞典
      {
        type: "Feature",
        properties: { name: "Sweden" },
        geometry: {
          type: "Polygon",
          coordinates: [[[10, 55], [10, 69], [25, 69], [25, 55], [10, 55]]]
        }
      },
      // 意大利
      {
        type: "Feature",
        properties: { name: "Italy" },
        geometry: {
          type: "Polygon",
          coordinates: [[[6, 35], [6, 47], [19, 47], [19, 35], [6, 35]]]
        }
      },
      // 西班牙
      {
        type: "Feature",
        properties: { name: "Spain" },
        geometry: {
          type: "Polygon",
          coordinates: [[[-10, 35], [-10, 44], [5, 44], [5, 35], [-10, 35]]]
        }
      },
      // 俄国
      {
        type: "Feature",
        properties: { name: "Russia" },
        geometry: {
          type: "Polygon",
          coordinates: [[[19, 41], [19, 82], [169, 82], [169, 41], [19, 41]]]
        }
      },
      // 泰国
      {
        type: "Feature",
        properties: { name: "Thailand" },
        geometry: {
          type: "Polygon",
          coordinates: [[[97, 5], [97, 21], [106, 21], [106, 5], [97, 5]]]
        }
      },
      // 马来西亚
      {
        type: "Feature",
        properties: { name: "Malaysia" },
        geometry: {
          type: "Polygon",
          coordinates: [[[99, 0], [99, 8], [120, 8], [120, 0], [99, 0]]]
        }
      }
    ]
  };
  
  return worldGeoJson;
};

// 初始化地图
const initMap = async () => {
  if (!mapContainer.value) return;

  try {
    mapChart = echarts.init(mapContainer.value);
    
    // 注册世界地图
    const worldGeoJson = getWorldMapData();
    echarts.registerMap('world', worldGeoJson as any);
    
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
      visualMap: {
        min: 0,
        max: Math.max(...visitorData.value.map(item => item.value)),
        text: ['高', '低'],
        realtime: false,
        calculable: true,
        inRange: {
          color: ['#e3f2fd', '#bbdefb', '#90caf9', '#42a5f5', '#1976d2', '#0d47a1']
        },
        textStyle: {
          color: '#374151'
        },
        left: 'left',
        bottom: 'bottom'
      },
      series: [
        {
          name: '访客数量',
          type: 'map',
          map: 'world',
          roam: true,
          scaleLimit: {
            min: 1,
            max: 3
          },
          emphasis: {
            itemStyle: {
              areaColor: '#10b981',
              borderColor: '#059669',
              borderWidth: 2
            },
            label: {
              show: true,
              color: '#fff',
              fontSize: 12,
              fontWeight: 'bold'
            }
          },
          itemStyle: {
            borderColor: 'rgba(255, 255, 255, 0.8)',
            borderWidth: 1
          },
          data: visitorData.value.map(item => ({
            name: item.name,
            value: item.value
          }))
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
    console.error('地图初始化失败:', error);
    loading.value = false;
  }
};

// 更新图表数据
const updateMapData = () => {
  if (!mapChart) return;

  // 模拟数据更新（添加小幅随机变化）
  visitorData.value = visitorData.value.map(item => ({
    ...item,
    value: Math.max(0, item.value + Math.floor((Math.random() - 0.5) * 100))
  }));

  const option = mapChart.getOption() as any;
  option.series[0].data = visitorData.value.map(item => ({
    name: item.name,
    value: item.value
  }));

  mapChart.setOption(option);
  console.log('🗺️ 地图数据已更新');
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
watch(() => props.autoRefresh, (newValue) => {
  if (newValue) {
    startAutoRefresh();
  } else {
    stopAutoRefresh();
  }
});

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
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}
</style>