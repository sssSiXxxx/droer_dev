<template>
  <div class="community-home">
    <!-- 顶部：LOGO、社区名、简介、快捷入口按钮 -->
    <header class="community-header">
      <div class="header-content">
        <div class="logo-section">
          <img class="community-logo" src="/logo.png" alt="Dromara LOGO" />
          <div class="community-info">
            <h1>Dromara 开源社区</h1>
            <p class="slogan">国产开源生态聚合平台</p>
            <p class="desc">Dromara 致力于打造中国领先的开源生态，聚合优质国产项目，推动中国开源力量！</p>
            <div class="quick-links">
              <el-button type="danger" size="small" @click="openLink('https://dromara.org/')">官网首页</el-button>
              <el-button type="success" size="small" @click="openLink('https://gitee.com/dromara')">Gitee 主页</el-button>
              <el-button type="info" size="small" @click="openLink('https://dromara.org/zh-cn/docs/')">文档中心</el-button>
              <el-button type="warning" size="small" @click="openLink('https://jq.qq.com/?_wv=1027&k=5yqR5QO')">加入QQ群</el-button>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- 统计区域 - 使用实时统计组件 -->
    <div class="stats-container">
      <RealtimeStats :compact="true" :show-header="false" ref="realtimeStatsRef" />
    </div>

    <!-- 搜索区域 -->
    <div class="search-section">
      <div class="search-container">
        <h2>探索优秀项目</h2>
        <p>搜索和发现 Dromara 社区的开源项目</p>
        <ProjectSearchCombo placeholder="搜索项目名称或描述..." :max-results="20" @search="handleProjectSearch" @select="handleProjectSelect" />
      </div>
    </div>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <!-- 社区活跃度趋势图表 -->
      <div class="chart-section">
        <div class="chart-layout">
          <div class="chart-main">
            <div class="chart-card">
              <div class="chart-header">
                <div class="chart-title">
                  <h3>社区活跃度趋势</h3>
                  <p>实时数据同步，最近一周的活动统计</p>
                </div>
                <div class="chart-controls">
                  <el-select v-model="selectedTimeRange" size="small" style="width: 120px" @change="handleTimeRangeChange">
                    <el-option label="最近7天" value="7" />
                    <el-option label="最近30天" value="30" />
                    <el-option label="最近90天" value="90" />
                  </el-select>
                  <el-button size="small" @click="refreshChartData" :loading="chartLoading">
                    <el-icon><Refresh /></el-icon>
                  </el-button>
                </div>
              </div>
              <div class="chart-container" v-loading="chartLoading">
                <v-chart class="chart" :option="chartOption" />
              </div>
            </div>
          </div>

          <div class="chart-sidebar">
            <!-- 待办事项侧边栏 -->
            <div class="sidebar-card todo-sidebar-card">
              <TodoSidebar />
            </div>
          </div>
        </div>
      </div>

      <!-- 三个图表在同一行展示 -->
      <div class="charts-row">
        <!-- 热门项目 -->
        <div class="chart-card-small">
          <div class="card-header">
            <h3>热门项目</h3>
            <p>实时同步最受欢迎的开源项目</p>
            <el-button size="small" text @click="refreshHotProjects" :loading="projectsLoading">
              <el-icon><Refresh /></el-icon>
            </el-button>
          </div>
          <div class="card-content" v-loading="projectsLoading">
            <div class="hot-projects-list">
              <div class="project-item" v-for="(project, index) in hotProjects" :key="index" @click="openProjectLink(project)">
                <div class="project-rank">{{ index + 1 }}</div>
                <div class="project-info">
                  <div class="project-name">{{ project.name }}</div>
                  <div class="project-desc">{{ project.description }}</div>
                  <div class="project-stats">
                    <span class="stat-item">
                      <el-icon><Star /></el-icon>
                      {{ formatNumber(project.stargazers_count) }}
                    </span>
                    <span class="stat-item">
                      <el-icon><Share /></el-icon>
                      {{ formatNumber(project.forks_count) }}
                    </span>
                    <span class="language-dot" :style="{ backgroundColor: getLanguageColor(project.language) }"></span>
                    <span class="language">{{ project.language }}</span>
                  </div>
                </div>
              </div>
              <div v-if="hotProjects.length === 0 && !projectsLoading" class="empty-state">
                <el-icon><Warning /></el-icon>
                <p>暂无数据</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 本周贡献榜 -->
        <div class="chart-card-small">
          <div class="card-header">
            <h3>本周贡献榜</h3>
            <p>实时更新的活跃贡献者排行</p>
            <el-button size="small" text @click="refreshContributors" :loading="contributorsLoading">
              <el-icon><Refresh /></el-icon>
            </el-button>
          </div>
          <div class="card-content" v-loading="contributorsLoading">
            <div class="contributors-list">
              <div class="contributor-item" v-for="(contributor, index) in weeklyContributors" :key="index" @click="openContributorLink(contributor)">
                <div class="contributor-rank" :class="getRankClass(index)">
                  {{ index + 1 }}
                </div>
                <div class="contributor-avatar">
                  <!-- 强制显示首字母头像 -->
                  <div class="avatar-letter" :style="getAvatarStyle(contributor)">
                    {{ getInitials(contributor.name || contributor.login || 'U') }}
                  </div>
                </div>
                <div class="contributor-info">
                  <div class="contributor-name">{{ contributor.name || contributor.login }}</div>
                  <div class="contributor-project">{{ contributor.login }}</div>
                  <div class="contribution-count">
                    <el-icon><Trophy /></el-icon>
                    {{ contributor.contributions }} 贡献
                  </div>
                </div>
              </div>
              <div v-if="weeklyContributors.length === 0 && !contributorsLoading" class="empty-state">
                <el-icon><Warning /></el-icon>
                <p>暂无数据</p>
              </div>
            </div>
          </div>
        </div>

        <!-- 技术栈饼图 -->
        <div class="chart-card-small">
          <div class="card-header">
            <h3>技术栈分布</h3>
            <p>实时统计社区项目技术栈分布</p>
            <el-button size="small" text @click="refreshTechStats" :loading="techStatsLoading">
              <el-icon><Refresh /></el-icon>
            </el-button>
          </div>
          <div class="card-content" v-loading="techStatsLoading">
            <div class="tech-chart-container">
              <v-chart class="tech-chart" :option="techChartOption" />
            </div>
            <div class="tech-legend">
              <div class="tech-legend-item" v-for="(tech, index) in techStack" :key="index">
                <div class="legend-color" :style="{ backgroundColor: tech.color }"></div>
                <span class="legend-name">{{ tech.name }}</span>
                <span class="legend-value">{{ tech.value }}%</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed, watch, nextTick } from 'vue';
import { use } from 'echarts/core';
import { CanvasRenderer } from 'echarts/renderers';
import { LineChart, PieChart } from 'echarts/charts';
import { TitleComponent, TooltipComponent, LegendComponent, GridComponent } from 'echarts/components';
import VChart from 'vue-echarts';
import {
  Star,
  Share,
  Link,
  Trophy,
  Document,
  Setting,
  User,
  Monitor,
  Calendar,
  Bell,
  Connection,
  Refresh,
  Download,
  Warning
} from '@element-plus/icons-vue';
import {
  getDashboardData,
  refreshAllData,
  getTrendingData,
  getProjectStats,
  getContributorStats,
  getTechStackStats,
  type ProjectInfo,
  type ContributorInfo,
  type DashboardData
} from '@/api/community-enhanced';
import RealtimeStats from '@/components/RealtimeStats.vue';
import ProjectSearchCombo from '@/components/ProjectSearchCombo.vue';
import TodoSidebar from '@/components/TodoSidebar.vue';
import { todoNotificationService } from '@/utils/todoNotification';

// 注册 ECharts 组件
use([CanvasRenderer, LineChart, PieChart, TitleComponent, TooltipComponent, LegendComponent, GridComponent]);

// 组件引用
const realtimeStatsRef = ref();

// 响应式数据
const hotProjects = ref<ProjectInfo[]>([]);
const weeklyContributors = ref<ContributorInfo[]>([]);
const dashboardData = ref<DashboardData | null>(null);

// 加载状态
const projectsLoading = ref(false);
const contributorsLoading = ref(false);
const dataLoading = ref(false);
const chartLoading = ref(false);
const techStatsLoading = ref(false);

// 统计数据
const totalProjects = ref(0);
const totalStars = ref(0);
const totalContributors = ref(0);

// 趋势数据
const trendingData = ref({
  dates: [] as string[],
  commits: [] as number[],
  issues: [] as number[],
  pullRequests: [] as number[],
  releases: [] as number[],
  contributors: [] as number[]
});

// 错误信息
const errorMessage = ref('');

// 技术栈数据 - 使用正确的编程语言颜色（GitHub官方色值）
const techStack = ref([
  { name: 'Java', value: 65, color: '#b07219' }, // Java官方颜色
  { name: 'JavaScript', value: 15, color: '#f1e05a' }, // JS官方颜色
  { name: 'TypeScript', value: 8, color: '#3178c6' }, // TS官方颜色
  { name: 'Go', value: 5, color: '#00ADD8' }, // Go官方颜色
  { name: 'Vue', value: 4, color: '#41b883' }, // Vue官方颜色
  { name: 'Python', value: 3, color: '#3572A5' } // Python官方颜色
]);

// 时间范围选择
const selectedTimeRange = ref('7');

// 社区活跃度数据 - 使用真实趋势数据
const communityActivity = computed(() => {
  if (!trendingData.value.dates.length) {
    // 默认数据
    return [
      { name: 'Dec 01', commits: 120, issues: 45, prs: 38 },
      { name: 'Dec 02', commits: 150, issues: 52, prs: 41 },
      { name: 'Dec 03', commits: 180, issues: 38, prs: 45 },
      { name: 'Dec 04', commits: 165, issues: 62, prs: 52 },
      { name: 'Dec 05', commits: 195, issues: 48, prs: 38 },
      { name: 'Dec 06', commits: 210, issues: 55, prs: 48 }
    ];
  }

  return trendingData.value.dates.map((date, index) => ({
    name: formatDateLabel(date),
    commits: trendingData.value.commits[index] || 0,
    issues: trendingData.value.issues[index] || 0,
    prs: trendingData.value.pullRequests[index] || 0
  }));
});

// 格式化日期标签
const formatDateLabel = (dateStr: string): string => {
  const date = new Date(dateStr);
  return date.toLocaleDateString('zh-CN', { month: 'short', day: '2-digit' });
};

// 技术栈饼图配置 - 恢复为真实颜色
const techChartOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: '#ddd',
    borderWidth: 1,
    textStyle: {
      color: '#333'
    },
    formatter: '{a} <br/>{b}: {c} ({d}%)'
  },
  series: [
    {
      name: '技术栈',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['50%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: {
        borderRadius: 10,
        borderColor: '#fff',
        borderWidth: 2
      },
      label: {
        show: false,
        position: 'center'
      },
      emphasis: {
        label: {
          show: true,
          fontSize: '18',
          fontWeight: 'bold',
          color: '#333'
        }
      },
      labelLine: {
        show: false
      },
      data: techStack.value.map((item) => ({
        value: item.value,
        name: item.name,
        itemStyle: {
          color: item.color
        }
      }))
    }
  ]
}));

// 社区活跃度图表配置 - 恢复为原来的多彩配置
const chartOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: '#ddd',
    borderWidth: 1,
    textStyle: {
      color: '#333'
    },
    formatter: function (params: any) {
      let result = params[0].name + '<br/>';
      params.forEach((param: any) => {
        result += param.marker + param.seriesName + ': ' + param.value + '<br/>';
      });
      return result;
    }
  },
  legend: {
    data: ['提交数', 'Issue数', 'PR数'],
    textStyle: {
      color: '#666'
    },
    top: 10,
    bottom: 'auto'
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '15%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: communityActivity.value.map((item) => item.name),
    axisLine: {
      lineStyle: {
        color: '#e6e8eb'
      }
    },
    axisLabel: {
      color: '#666'
    }
  },
  yAxis: {
    type: 'value',
    axisLine: {
      lineStyle: {
        color: '#e6e8eb'
      }
    },
    axisLabel: {
      color: '#666'
    },
    splitLine: {
      lineStyle: {
        color: '#f5f5f5'
      }
    }
  },
  series: [
    {
      name: '提交数',
      type: 'line',
      stack: 'Total',
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(24, 144, 255, 0.6)' },
            { offset: 1, color: 'rgba(24, 144, 255, 0.1)' }
          ]
        }
      },
      lineStyle: {
        color: '#1890ff',
        width: 3
      },
      itemStyle: {
        color: '#1890ff'
      },
      data: communityActivity.value.map((item) => item.commits),
      smooth: true
    },
    {
      name: 'Issue数',
      type: 'line',
      stack: 'Total',
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(82, 196, 26, 0.6)' },
            { offset: 1, color: 'rgba(82, 196, 26, 0.1)' }
          ]
        }
      },
      lineStyle: {
        color: '#52c41a',
        width: 3
      },
      itemStyle: {
        color: '#52c41a'
      },
      data: communityActivity.value.map((item) => item.issues),
      smooth: true
    },
    {
      name: 'PR数',
      type: 'line',
      stack: 'Total',
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(250, 173, 20, 0.6)' },
            { offset: 1, color: 'rgba(250, 173, 20, 0.1)' }
          ]
        }
      },
      lineStyle: {
        color: '#faad14',
        width: 3
      },
      itemStyle: {
        color: '#faad14'
      },
      data: communityActivity.value.map((item) => item.prs),
      smooth: true
    }
  ]
}));

// 获取完整仪表盘数据
const fetchDashboardData = async () => {
  dataLoading.value = true;
  errorMessage.value = '';
  try {
    console.log('🚀 正在加载仪表盘数据...');

    const data = await getDashboardData();
    dashboardData.value = data;

    // 更新各个数据项
    hotProjects.value = data.hotProjects;
    weeklyContributors.value = (data.weeklyContributors || []).map((contributor: any) => ({
      ...contributor,
      avatarError: false, // 初始化头像错误状态
      forceInitials: false // 初始化强制首字母状态
    }));
    trendingData.value = data.trendingData;

    // 更新统计数据
    totalProjects.value = data.stats.totalProjects;
    totalStars.value = data.stats.totalStars;
    totalContributors.value = data.stats.totalContributors;

    console.log('✅ 仪表盘数据加载完成');
  } catch (error) {
    console.error('❌ 加载仪表盘数据失败:', error);
    errorMessage.value = '获取数据失败，请稍后重试';
  } finally {
    dataLoading.value = false;
  }
};

// 刷新所有数据
const refreshAllDataAndUI = async () => {
  try {
    console.log('🔄 正在强制刷新所有数据...');
    const data = await refreshAllData();
    dashboardData.value = data;

    hotProjects.value = data.hotProjects;
    weeklyContributors.value = (data.weeklyContributors || []).map((contributor: any) => ({
      ...contributor,
      avatarError: false, // 初始化头像错误状态
      forceInitials: false // 初始化强制首字母状态
    }));
    trendingData.value = data.trendingData;

    totalProjects.value = data.stats.totalProjects;
    totalStars.value = data.stats.totalStars;
    totalContributors.value = data.stats.totalContributors;

    // 同步刷新统计组件
    if (realtimeStatsRef.value) {
      await realtimeStatsRef.value.refreshData();
    }

    console.log('✅ 数据刷新完成');
  } catch (error) {
    console.error('❌ 刷新数据失败:', error);
  }
};

// 刷新热门项目
const refreshHotProjects = async () => {
  projectsLoading.value = true;
  try {
    const data = await getProjectStats();
    hotProjects.value = data.hotProjects || [];
    console.log('✅ 热门项目数据刷新完成');
  } catch (error) {
    console.error('❌ 刷新热门项目失败:', error);
  } finally {
    projectsLoading.value = false;
  }
};

// 刷新贡献者排行
const refreshContributors = async () => {
  contributorsLoading.value = true;
  try {
    const data = await getContributorStats();
    weeklyContributors.value = (data.weeklyContributors || []).map((contributor: any) => ({
      ...contributor,
      avatarError: false, // 初始化头像错误状态
      forceInitials: false // 初始化强制首字母状态
    }));
    console.log('✅ 贡献者排行数据刷新完成');
  } catch (error) {
    console.error('❌ 刷新贡献者排行失败:', error);
  } finally {
    contributorsLoading.value = false;
  }
};

// 刷新技术栈统计
const refreshTechStats = async () => {
  techStatsLoading.value = true;
  try {
    const data = await getTechStackStats();
    if (data.techStack) {
      techStack.value = data.techStack.map((item: any) => ({
        name: item.name,
        value: item.value,
        color: item.color || getLanguageColor(item.name) // 优先使用API返回的颜色，备用本地计算
      }));
    }
    console.log('✅ 技术栈统计数据刷新完成');
  } catch (error) {
    console.error('❌ 刷新技术栈统计失败:', error);
  } finally {
    techStatsLoading.value = false;
  }
};

// 获取真实语言颜色 - 与techStack数据保持一致
const getLanguageColor = (language: string): string => {
  const colors: Record<string, string> = {
    'Java': '#b07219', // Java官方颜色
    'JavaScript': '#f1e05a', // JS官方颜色
    'TypeScript': '#3178c6', // TS官方颜色
    'Go': '#00ADD8', // Go官方颜色
    'Vue': '#41b883', // Vue官方颜色
    'Python': '#3572A5', // Python官方颜色
    'C++': '#00599c',
    'C#': '#239120',
    'PHP': '#777bb4',
    'React': '#61dafb',
    'Shell': '#89e051'
  };
  return colors[language] || '#6b7280';
};

// 时间范围变化处理
const handleTimeRangeChange = async (value: string) => {
  console.log('🕒 时间范围变化:', value);
  await refreshChartData();
};

// 刷新图表数据
const refreshChartData = async () => {
  chartLoading.value = true;
  try {
    const data = await getTrendingData({
      days: parseInt(selectedTimeRange.value)
    });
    trendingData.value = data;
    console.log('✅ 图表数据刷新完成');
  } catch (error) {
    console.error('❌ 刷新图表数据失败:', error);
  } finally {
    chartLoading.value = false;
  }
};

// 项目搜索处理
const handleProjectSearch = (results: ProjectInfo[], query: string) => {
  console.log(`🔍 搜索结果: "${query}" 找到 ${results.length} 个项目`);
};

// 项目选择处理
const handleProjectSelect = (project: ProjectInfo) => {
  console.log('📂 选择项目:', project.name);
  openProjectLink(project);
};

// 打开项目链接
const openProjectLink = (project: ProjectInfo) => {
  if (project.html_url) {
    window.open(project.html_url, '_blank');
  }
};

// 打开贡献者链接
const openContributorLink = (contributor: ContributorInfo) => {
  if (contributor.html_url) {
    window.open(contributor.html_url, '_blank');
  }
};

// 打开链接
const openLink = (url: string) => {
  window.open(url, '_blank');
};

// 格式化数字
const formatNumber = (num: number): string => {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k';
  }
  return num.toString();
};

// 获取排名样式
const getRankClass = (index: number): string => {
  if (index === 0) return 'gold';
  if (index === 1) return 'silver';
  if (index === 2) return 'bronze';
  return '';
};

// 验证头像 URL 是否有效 - 简化版本，更宽松的验证
const isValidAvatarUrl = (url: string): boolean => {
  if (!url || url.trim() === '') return false;

  // 简化验证：只要不是明显无效的URL就认为有效
  const trimmedUrl = url.trim();

  // 检查基本的URL格式
  return trimmedUrl.startsWith('http://') ||
         trimmedUrl.startsWith('https://') ||
         trimmedUrl.startsWith('/') ||
         trimmedUrl.includes('avatar') ||
         trimmedUrl.includes('gitee.com');
};

// 处理头像加载失败
const handleAvatarError = (contributor: any, index?: number) => {
  console.log('头像加载失败:', contributor.name || contributor.login, contributor.avatar_url);
  // 标记头像加载失败，触发响应式更新
  contributor.avatarError = true;

  // 强制触发响应式更新
  if (index !== undefined) {
    // 更新特定索引的贡献者
    weeklyContributors.value[index] = { ...contributor, avatarError: true };
  } else {
    // 强制更新整个列表
    weeklyContributors.value = [...weeklyContributors.value];
  }

  // 强制重新渲染
  nextTick(() => {
    console.log('头像错误状态更新完成，将显示首字母头像');
  });
};

// 获取用户名首字母
const getInitials = (name: string): string => {
  if (!name || name.trim() === '') return '?';

  const trimmedName = name.trim();

  // 处理中文名
  if (/[\u4e00-\u9fa5]/.test(trimmedName)) {
    // 中文名取第一个字
    return trimmedName.charAt(0);
  }

  // 处理英文名或其他字符
  const words = trimmedName.split(/[\s\-_.]+/).filter(word => word.length > 0);

  if (words.length === 0) {
    return trimmedName.charAt(0).toUpperCase();
  } else if (words.length === 1) {
    // 单个单词，取第一个字符
    return words[0].charAt(0).toUpperCase();
  } else {
    // 多个单词，取前两个单词的首字母
    const firstChar = words[0].charAt(0).toUpperCase();
    const secondChar = words[1].charAt(0).toUpperCase();
    return firstChar + secondChar;
  }
};

// 获取头像样式（基于用户名生成颜色）
const getAvatarStyle = (contributor: any) => {
  const name = contributor.name || contributor.login || '';

  // GitHub 风格的低饱和配色（纯色而非渐变）
  const colorCombinations = [
    { bg: '#68dcc6', color: '#fff' },  // teal
    { bg: '#49dc86', color: '#fff' },  // green
    { bg: '#81c1ea', color: '#fff' },  // blue
    { bg: '#d284f3', color: '#fff' },  // purple
    { bg: '#f3a661', color: '#fff' },  // orange
    { bg: '#f8695a', color: '#fff' },  // red
    { bg: '#eecf59', color: '#fff' },  // yellow (文字仍用白色)
    { bg: '#4cf6d2', color: '#fff' },  // dark teal
    { bg: '#449bef', color: '#fff' },  // dark gray-blue
  ];

  // 基于用户名计算哈希值选择颜色
  let hash = 0;
  for (let i = 0; i < name.length; i++) {
    const char = name.charCodeAt(i);
    hash = ((hash << 5) - hash) + char;
    hash = hash & hash; // 转换为32位整数
  }

  const colorIndex = Math.abs(hash) % colorCombinations.length;
  const colors = colorCombinations[colorIndex];

  return {
    background: colors.bg,
    color: colors.color
  };
};

// 监听时间范围变化
watch(selectedTimeRange, async (newValue) => {
  await refreshChartData();
});

// 页面加载时获取数据
onMounted(async () => {
  console.log('🚀 首页开始加载...');

  // 并行加载所有数据以提高性能
  await Promise.all([
    fetchDashboardData(),
    refreshHotProjects(),
    refreshContributors(),
    refreshTechStats(),
    refreshChartData() // 添加图表数据加载
  ]);

  console.log('✅ 首页数据加载完成');

  // 初始化待办事项通知服务
  try {
    await todoNotificationService.requestPermission();
    console.log('✅ 待办事项通知服务已启动');
  } catch (error) {
    console.warn('⚠️ 通知服务启动失败:', error);
  }
  
  // 监听来自项目列表的数据刷新事件
  const handleDashboardRefresh = (event: CustomEvent) => {
    console.log('🔔 收到首页数据刷新通知:', event.detail);
    if (event.detail?.source === 'project-sync') {
      console.log('📊 项目列表触发的数据更新，开始刷新首页...');
      // 刷新所有数据
      refreshAllDataAndUI();
    }
  };
  
  window.addEventListener('dashboard-refresh', handleDashboardRefresh);
  
  // 页面卸载时移除事件监听
  onUnmounted(() => {
    window.removeEventListener('dashboard-refresh', handleDashboardRefresh);
  });
});

// 自动刷新数据（每5分钟）
let autoRefreshTimer: NodeJS.Timeout | null = null;

const startAutoRefresh = () => {
  autoRefreshTimer = setInterval(
    async () => {
      console.log('🔄 自动刷新数据...');
      await refreshAllDataAndUI();
    },
    5 * 60 * 1000
  ); // 5分钟
};

const stopAutoRefresh = () => {
  if (autoRefreshTimer) {
    clearInterval(autoRefreshTimer);
    autoRefreshTimer = null;
  }
};

// 启动自动刷新
onMounted(() => {
  startAutoRefresh();
});

// 清理定时器
onUnmounted(() => {
  stopAutoRefresh();
});
</script>

<style scoped>
/* 统一浅绿色主题设计 */
.community-header {
  background: linear-gradient(135deg, #9fdbcf 0%, #9fdbcf 50%, #9fdbcf 100%);
  color: #065f46;
  padding: 60px 0;
  position: relative;
  overflow: hidden;
  border-bottom: 1px solid #10b981;
}

.community-home {
  min-height: 100vh;
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
  padding: 0;
  margin: 0;
  width: 100%;
}

.community-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1000 100" fill="rgba(16,185,129,0.1)"><polygon points="0,0 1000,0 1000,100"/></svg>');
  background-size: 100% 100%;
  opacity: 0.2;
}

.header-content {
  max-width: 100%;
  margin: 0 auto;
  padding: 0 40px;
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  z-index: 1;
  margin-bottom: 40px;
}

.logo-section {
  display: flex;
  align-items: center;
  gap: 30px;
  flex: 1;
}

.community-logo {
  width: 100px;
  height: 100px;
  border-radius: 20px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  transition: transform 0.3s ease;
}

.community-logo:hover {
  transform: scale(1.05);
}

.community-info {
  flex: 1;
}

.community-info h1 {
  margin: 0 0 12px 0;
  font-size: 36px;
  font-weight: 700;
  color: #065f46;
  text-shadow: 0 2px 4px rgba(16, 185, 129, 0.2);
}

.slogan {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #047857;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(16, 185, 129, 0.1);
}

.desc {
  margin: 0 0 25px 0;
  font-size: 16px;
  color: #059669;
  line-height: 1.6;
  max-width: 600px;
}

.quick-links .el-button {
  border-radius: 20px;
  padding: 8px 20px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.2);
}

.quick-links .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(16, 185, 129, 0.3);
}

.quick-links .el-button--primary {
  background: linear-gradient(135deg, #10b981, #059669);
  border-color: #10b981;
}

.quick-links .el-button--success {
  background: linear-gradient(135deg, #22c55e, #16a34a);
  border-color: #22c55e;
}

.quick-links {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.stats-container {
  background: linear-gradient(135deg, #f0fdf4 0%, #dcfce7 100%);
  padding: 40px 0;
  margin: 0;
  border-radius: 0;
  position: relative;
  width: 100%;
}

.stats-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, rgba(16, 185, 129, 0.3) 50%, transparent 100%);
}

.main-content {
  max-width: 100%;
  margin: 0;
  padding: 40px 40px 0;
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
}

.bottom-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-top: 32px;
}

.grid-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #f1f5f9;
  transition: all 0.3s ease;
}

.grid-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.bottom-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
  margin-top: 24px;
}

.row-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #f1f5f9;
  transition: all 0.3s ease;
}

.row-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.section-card {
  background: white;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  height: 100%;
  border: 1px solid #ebeef5;
  margin: 0;
}

.section-header {
  margin-bottom: 30px;
  text-align: center;
}

.section-header h2 {
  margin: 0 0 10px 0;
  font-size: 22px;
  font-weight: 600;
  color: #303133;
}

.section-header p {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: 20px;
  max-height: 600px;
  overflow-y: auto;
  padding-right: 10px;
}

.project-card {
  border: 1px solid #ebeef5;
  border-radius: 4px;
  padding: 15px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #ffffff;
}

.project-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  border-color: #b4e4d9;
}

.project-header {
  display: flex;
  flex-direction: column;
  margin-bottom: 15px;
}

.project-name-highlight {
  margin-bottom: 10px;
  position: relative;
  padding-left: 12px;
}

.project-name-highlight::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background: #b4e4d9;
  border-radius: 2px;
}

.project-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.project-info h3 {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #2c3e50;
  font-weight: 600;
}

.project-desc {
  margin: 0;
  font-size: 14px;
  color: #7f8c8d;
  line-height: 1.4;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.project-stats {
  display: flex;
  gap: 15px;
  align-items: center;
}

.stat {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  color: #7f8c8d;
}

.language {
  margin-left: auto;
}

.language-dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  display: inline-block;
}

.contributor-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
  max-height: 500px;
  overflow-y: auto;
  padding-right: 10px;
}

.announcement-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.announcement-item {
  display: flex;
  padding: 12px;
  border-radius: 4px;
  background: #ffffff;
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
}

.announcement-item:hover {
  background: #f5f7fa;
  border-color: #c6e2ff;
}

.announcement-date {
  flex-shrink: 0;
  width: 80px;
  color: #909399;
  font-size: 13px;
}

.announcement-content {
  flex: 1;
}

.announcement-content h4 {
  margin: 0 0 8px 0;
  font-size: 15px;
  color: #303133;
  font-weight: 600;
}

.announcement-content p {
  margin: 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
}

.quick-entry-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.quick-entry-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px;
  border-radius: 4px;
  background: #ffffff;
  border: 1px solid #ebeef5;
  transition: all 0.3s ease;
  cursor: pointer;
}

.quick-entry-item:hover {
  background: #f5f7fa;
  border-color: #c6e2ff;
  transform: translateY(-2px);
}

.quick-entry-icon {
  font-size: 24px;
  margin-bottom: 8px;
  color: #b4e4d9;
}

.quick-entry-title {
  font-size: 14px;
  color: #303133;
}

.contributor-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 12px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #ffffff;
  border: 1px solid #ebeef5;
  margin-bottom: 8px;
}

.contributor-item:hover {
  background: #f5f7fa;
  transform: translateX(5px);
  border-color: #c6e2ff;
}

.rank-badge {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
  color: white;
  background: #95a5a6;
  flex-shrink: 0;
}

.rank-badge.gold {
  background: #f0c53f;
}

.rank-badge.silver {
  background: #c0c0c0;
}

.rank-badge.bronze {
  background: #cd7f32;
}

.contributor-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  border: 2px solid #f0f0f0;
  background: #f5f5f5;
  display: flex;
  align-items: center;
  justify-content: center;
}

.contributor-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.contributor-avatar img:hover {
  transform: scale(1.1);
  transition: transform 0.3s ease;
}

.contributor-info {
  flex: 1;
}

.contributor-info h4 {
  margin: 0 0 5px 0;
  font-size: 15px;
  color: #303133;
  font-weight: 600;
}

.contributor-info p {
  margin: 0 0 8px 0;
  font-size: 13px;
  color: #909399;
}

.contribution-count {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #b4e4d9;
  font-weight: 500;
}

/* 自定义滚动条样式 */
.project-grid::-webkit-scrollbar,
.contributor-list::-webkit-scrollbar {
  width: 6px;
}

.project-grid::-webkit-scrollbar-track,
.contributor-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.project-grid::-webkit-scrollbar-thumb,
.contributor-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.project-grid::-webkit-scrollbar-thumb:hover,
.contributor-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

@media (max-width: 768px) {
  .header-content {
    flex-direction: column;
    gap: 30px;
    text-align: center;
  }

  .logo-section {
    flex-direction: column;
    gap: 20px;
  }

  .stats-container {
    padding: 30px 0;
  }

  .stats-section {
    justify-content: center;
    gap: 15px;
  }

  .stat-item {
    min-width: 160px;
    padding: 14px 16px;
  }

  .stat-value {
    font-size: 22px;
  }

  .stat-title {
    font-size: 11px;
  }

  .stat-change {
    font-size: 9px;
  }

  .quick-links {
    justify-content: center;
  }

  .community-info h1 {
    font-size: 28px;
  }

  .slogan {
    font-size: 16px;
  }

  .desc {
    font-size: 14px;
  }

  .project-grid {
    grid-template-columns: 1fr;
  }

  .main-content {
    padding: 0 10px;
  }

  .content-grid {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .section-card {
    padding: 15px;
  }
}

/* 图表区域样式 */
.chart-section {
  margin-bottom: 32px;
}

.chart-layout {
  display: flex;
  gap: 24px;
}

.chart-main {
  flex: 1;
}

.chart-sidebar {
  display: flex;
  flex-direction: column;
  width: 320px;
  flex-shrink: 0;
  height: 520px;
}

.sidebar-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #f1f5f9;
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
}

.sidebar-card:last-child {
  padding-bottom: 24px;
}

.sidebar-card:last-child .card-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.sidebar-card {
  height: 100%;
}

.sidebar-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 侧边栏待办事项样式优化 */
.sidebar-card .todo-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
  overflow-y: auto;
  padding: 6px 8px 16px 0;
  max-height: 440px;
}

/* 自定义滚动条样式 */
.sidebar-card .todo-list::-webkit-scrollbar {
  width: 6px;
}

.sidebar-card .todo-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.sidebar-card .todo-list::-webkit-scrollbar-thumb {
  background: #10b981;
  border-radius: 3px;
}

.sidebar-card .todo-list::-webkit-scrollbar-thumb:hover {
  background: #059669;
}

.sidebar-card .todo-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 10px;
  border-radius: 8px;
  background: #f9fafb;
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
  min-height: 28px;
}

.sidebar-card .todo-item:hover {
  background: #f3f4f6;
  transform: translateX(4px);
  border-color: #10b981;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.15);
}

.sidebar-card .todo-checkbox {
  width: 16px;
  height: 16px;
  border: 2px solid #d1d5db;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.sidebar-card .todo-checkbox.completed {
  background: #10b981;
  border-color: #10b981;
  box-shadow: 0 2px 6px rgba(16, 185, 129, 0.3);
}

.sidebar-card .checkbox-inner {
  width: 6px;
  height: 6px;
  background: white;
  border-radius: 50%;
}

.sidebar-card .todo-text {
  font-size: 12px;
  color: #374151;
  font-weight: 500;
  transition: all 0.3s ease;
  line-height: 1.4;
  flex: 1;
  word-break: break-word;
}

.sidebar-card .todo-text.completed {
  color: #9ca3af;
  text-decoration: line-through;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #f1f5f9;
  transition: all 0.3s ease;
}

.chart-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.chart-title h3 {
  margin: 0 0 4px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.chart-title p {
  margin: 0;
  font-size: 14px;
  color: #6b7280;
}

.chart-container {
  height: 400px;
  position: relative;
  overflow: hidden;
}

.chart {
  width: 100%;
  height: 100%;
}

/* 三个图表在同一行展示的样式 */
.charts-row {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
  margin-bottom: 32px;
}

.chart-card-small {
  background: white;
  border-radius: 20px;
  padding: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  border: 1px solid #f1f5f9;
  transition: all 0.3s ease;
  height: 420px;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
}

.chart-card-small::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #10b981, #22c55e, #16a34a);
  border-radius: 20px 20px 0 0;
}

.chart-card-small:hover {
  box-shadow: 0 8px 32px rgba(16, 185, 129, 0.12);
  transform: translateY(-4px);
  border-color: #10b981;
}

.card-header {
  margin-bottom: 20px;
  position: relative;
  z-index: 1;
}

.card-header h3 {
  margin: 0 0 6px 0;
  font-size: 18px;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: -0.025em;
}

.card-header p {
  margin: 0;
  font-size: 13px;
  color: #6b7280;
  font-weight: 500;
}

.card-content {
  flex: 1;
  overflow: hidden;
}

/* 热门项目列表样式 */
.hot-projects-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
  overflow-y: auto;
  padding-right: 8px;
}

.project-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #e2e8f0;
}

.project-item:hover {
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
  transform: translateX(6px);
  border-color: #10b981;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.15);
}

.project-rank {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
}

.project-info {
  flex: 1;
  min-width: 0;
}

.project-name {
  font-size: 15px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  letter-spacing: -0.025em;
}

.project-desc {
  font-size: 13px;
  color: #6b7280;
  line-height: 1.5;
  margin-bottom: 10px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.project-stats {
  display: flex;
  align-items: center;
  gap: 12px;
  flex-wrap: wrap;
}

.project-stats .stat-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
}

.language-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.language {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
}

/* 贡献者列表样式 */
.contributors-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
  overflow-y: auto;
  padding-right: 8px;
}

.contributor-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #e2e8f0;
}

.contributor-item:hover {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  transform: translateX(6px);
  border-color: #f59e0b;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.15);
}

.contributor-rank {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  font-weight: 800;
  color: white;
  flex-shrink: 0;
  box-shadow: 0 3px 10px rgba(0, 0, 0, 0.3);
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.5);
  border: 2px solid rgba(255, 255, 255, 0.2);
  background: linear-gradient(135deg, #64748b, #475569);
}

.contributor-rank.gold {
  background: linear-gradient(135deg, #f59e0b, #d97706);
  box-shadow: 0 3px 10px rgba(245, 158, 11, 0.4);
  border-color: rgba(255, 255, 255, 0.3);
}

.contributor-rank.silver {
  background: linear-gradient(135deg, #94a3b8, #64748b);
  box-shadow: 0 3px 10px rgba(148, 163, 184, 0.4);
  border-color: rgba(255, 255, 255, 0.3);
}

.contributor-rank.bronze {
  background: linear-gradient(135deg, #ea580c, #c2410c);
  box-shadow: 0 3px 10px rgba(234, 88, 12, 0.4);
  border-color: rgba(255, 255, 255, 0.3);
}

/* 头像容器 */
.contributor-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  border: 2px solid #e5e7eb;
  background: #f3f4f6;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 14px;
  color: #fff; /* 字母统一白色 */
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

/* 图片模式 */
.contributor-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.contributor-avatar:hover img {
  transform: scale(1.05);
}

/* 字母模式 */
.contributor-avatar .avatar-letter {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  font-size: 14px;
  font-weight: 600;
}

.contributor-avatar:hover .avatar-letter {
  transform: scale(1.1);
}

.contributor-info {
  flex: 1;
  min-width: 0;
}

.contributor-name {
  font-size: 15px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  letter-spacing: -0.025em;
}

.contributor-project {
  font-size: 12px;
  color: #6b7280;
  margin-bottom: 6px;
  font-weight: 500;
}

.contribution-count {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #10b981;
  font-weight: 600;
}

/* 技术栈图表样式 */
.tech-chart-container {
  height: 200px;
  margin-bottom: 20px;
}

.tech-chart {
  width: 100%;
  height: 100%;
}

.tech-legend {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.tech-legend-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
  padding: 8px 12px;
  border-radius: 8px;
  background: #f9fafb;
  transition: all 0.3s ease;
}

.tech-legend-item:hover {
  background: #f3f4f6;
  transform: translateX(4px);
}

.legend-color {
  width: 14px;
  height: 14px;
  border-radius: 50%;
  flex-shrink: 0;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.legend-name {
  flex: 1;
  color: #374151;
  font-weight: 500;
}

.legend-value {
  color: #6b7280;
  font-weight: 600;
}

/* 版本列表样式 */
.version-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.version-item {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.version-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.version-name {
  font-size: 14px;
  font-weight: 600;
  color: #374151;
}

.version-users {
  font-size: 12px;
  color: #6b7280;
  font-weight: 500;
}

.version-percentage {
  font-size: 12px;
  color: #6b7280;
  text-align: right;
  font-weight: 600;
}

/* 设置列表样式 */
.settings-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.setting-name {
  font-size: 14px;
  color: #374151;
}

/* 社区完善度样式 */
.community-progress {
  padding-top: 20px;
  border-top: 1px solid #e5e7eb;
}

.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.progress-title {
  font-size: 14px;
  font-weight: 500;
  color: #374151;
}

.progress-value {
  font-size: 12px;
  color: #6b7280;
}

.progress-label {
  font-size: 11px;
  color: #10b981;
  margin-top: 4px;
}

/* 贡献者排行样式 */
.contributor-ranking {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.contributor-name {
  font-size: 14px;
  color: #374151;
}

.ranking-progress {
  width: 100%;
}

/* 活动列表样式 */
.activity-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.activity-item {
  display: flex;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
}

.activity-item:hover {
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
  transform: translateX(6px);
  border-color: #71e0bb;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.15);
}

.activity-dot {
  width: 10px;
  height: 10px;
  background: #beecdd;
  border-radius: 50%;
  margin-top: 8px;
  flex-shrink: 0;
  box-shadow: 0 2px 6px rgba(16, 185, 129, 0.3);
}

.activity-content {
  flex: 1;
}

.activity-content h4 {
  margin: 0 0 8px 0;
  font-size: 15px;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: -0.025em;
}

.activity-content p {
  margin: 0 0 10px 0;
  font-size: 13px;
  color: #6b7280;
  line-height: 1.5;
}

.activity-time {
  font-size: 12px;
  color: #9ca3af;
  font-weight: 500;
}

/* 访客统计样式 */
.visitor-stats {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.visitor-total {
  text-align: center;
  font-size: 16px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 20px;
  letter-spacing: -0.025em;
}

.visitor-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.visitor-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  padding: 12px 16px;
  border-radius: 10px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
}

.visitor-item:hover {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  transform: translateX(4px);
  border-color: #f59e0b;
  box-shadow: 0 2px 8px rgba(245, 158, 11, 0.15);
}

.visitor-country {
  color: #374151;
  font-weight: 600;
}

.visitor-info {
  display: flex;
  gap: 12px;
}

.visitor-count {
  color: #6b7280;
  font-weight: 500;
}

.visitor-percentage {
  color: #9ca3af;
  font-weight: 600;
}

.visitor-map {
  height: 100px;
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
  border: 1px solid #d1fae5;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.1);
}

.map-icon {
  font-size: 36px;
  color: #10b981;
}

/* 待办事项样式 */
.todo-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 16px;
  border-radius: 12px;
  background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
  transition: all 0.3s ease;
  border: 1px solid #e2e8f0;
}

.todo-item:hover {
  background: linear-gradient(135deg, #f8fcf9 0%, #f1f6f4 100%);
  transform: translateX(6px);
  border-color: #10b981;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.15);
}

.todo-checkbox {
  width: 20px;
  height: 20px;
  border: 2px solid #d1d5db;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.todo-checkbox.completed {
  background: linear-gradient(135deg, #10b981, #059669);
  border-color: #10b981;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.3);
}

.checkbox-inner {
  width: 8px;
  height: 8px;
  background: white;
  border-radius: 50%;
}

.todo-text {
  font-size: 14px;
  color: #374151;
  font-weight: 500;
  transition: all 0.3s ease;
}

.todo-text.completed {
  color: #9ca3af;
  text-decoration: line-through;
}

/* 活跃用户样式 */
.active-users-stats {
  text-align: center;
  padding: 20px;
}

.users-number {
  font-size: 36px;
  font-weight: 800;
  color: #1f2937;
  margin-bottom: 12px;
  letter-spacing: -0.025em;
}

.users-label {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 24px;
  font-weight: 500;
}

.users-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #f0fdf4 0%, #ecfdf5 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  border: 3px solid #d1fae5;
  box-shadow: 0 4px 16px rgba(16, 185, 129, 0.2);
}

.users-icon .el-icon {
  font-size: 36px;
  color: #10b981;
}

/* 进度条样式优化 */
.progress-bar {
  width: 100%;
  height: 8px;
  background: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #10b981, #059669);
  border-radius: 4px;
  transition: width 0.3s ease;
  position: relative;
}

.progress-fill::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  animation: shimmer 2s infinite;
}

@keyframes shimmer {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(100%);
  }
}

/* 待办事项侧边栏样式 */
.todo-sidebar-card {
  padding: 0;
  overflow: hidden;
  height: 600px;
}

.todo-sidebar-card :deep(.todo-sidebar) {
  height: 100%;
  border-radius: 0;
  box-shadow: none;
}

/* 搜索区域样式 */
.search-section {
  background: white;
  padding: 60px 0;
  margin: 0;
  border-bottom: 1px solid #e2e8f0;
}

.search-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  text-align: center;
}

.search-section h2 {
  margin: 0 0 12px 0;
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
  letter-spacing: -0.025em;
}

.search-section p {
  margin: 0 0 40px 0;
  font-size: 18px;
  color: #6b7280;
  font-weight: 500;
}

@media (max-width: 768px) {
  .chart-layout {
    flex-direction: column;
    gap: 16px;
  }

  .chart-sidebar {
    width: 100%;
    flex-direction: row;
    gap: 16px;
    height: auto;
  }

  .sidebar-card {
    flex: 1;
    padding: 16px;
    height: auto;
    min-height: 180px;
  }

  .chart-container {
    height: 200px;
  }

  .charts-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }

  .chart-card-small {
    height: 350px;
    padding: 16px;
  }

  .bottom-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 16px;
  }

  .grid-card {
    padding: 16px;
  }
}
</style>
