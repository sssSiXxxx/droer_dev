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
              <el-button type="primary" size="small" @click="openLink('https://dromara.org/')">官网首页</el-button>
              <el-button type="success" size="small" @click="openLink('https://gitee.com/dromara')">Gitee 主页</el-button>
              <el-button type="info" size="small" @click="openLink('https://dromara.org/zh-cn/docs/')">文档中心</el-button>
              <el-button type="warning" size="small" @click="openLink('https://jq.qq.com/?_wv=1027&k=5yqR5QO')">加入QQ群</el-button>
            </div>
          </div>
        </div>
      </div>
    </header>

    <!-- 统计区域 -->
    <div class="stats-container">
      <div class="stats-section">
        <div class="stat-item" v-for="(stat, index) in statsData" :key="index">
          <div class="stat-icon" :style="{ color: stat.color, backgroundColor: stat.bg }">
            <el-icon><component :is="stat.icon" /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stat.value }}</div>
            <div class="stat-title">{{ stat.title }}</div>
            <div class="stat-change" :class="stat.change.includes('+') ? 'positive' : 'negative'">
              {{ stat.change }}
            </div>
          </div>
        </div>
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
                  <p>最近一周的活动统计</p>
                </div>
                <el-select v-model="selectedTimeRange" size="small" style="width: 120px;">
                  <el-option label="最近7天" value="7" />
                  <el-option label="最近30天" value="30" />
                  <el-option label="最近90天" value="90" />
                </el-select>
              </div>
              <div class="chart-container">
                <v-chart class="chart" :option="chartOption" />
              </div>
            </div>
          </div>
          
          <div class="chart-sidebar">
            <!-- 日活跃用户 -->
            <div class="sidebar-card">
              <div class="card-header">
                <div class="header-content">
                  <div class="header-left">
                    <h3>日活跃用户</h3>
                    <p>今日统计</p>
                  </div>
                  <div class="header-right">
                    <div class="users-number">2.1k</div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 待办事项 -->
            <div class="sidebar-card">
              <div class="card-header">
                <h3>待办事项</h3>
                <p>当前任务</p>
              </div>
              <div class="card-content">
                <div class="todo-list">
                  <div class="todo-item" v-for="(item, index) in todoItems" :key="index">
                    <div class="todo-checkbox" :class="{ completed: item.completed }">
                      <div class="checkbox-inner" v-if="item.completed"></div>
                    </div>
                    <span class="todo-text" :class="{ completed: item.completed }">{{ item.task }}</span>
                  </div>
                </div>
              </div>
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
            <p>最受欢迎的开源项目</p>
          </div>
          <div class="card-content">
            <div class="hot-projects-list">
              <div class="project-item" v-for="(project, index) in hotProjects" :key="index">
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
            </div>
          </div>
        </div>

        <!-- 本周贡献榜 -->
        <div class="chart-card-small">
          <div class="card-header">
            <h3>本周贡献榜</h3>
            <p>活跃贡献者排行</p>
          </div>
          <div class="card-content">
            <div class="contributors-list">
              <div class="contributor-item" v-for="(contributor, index) in weeklyContributors" :key="index">
                <div class="contributor-rank" :class="getRankClass(index)">
                  {{ index + 1 }}
                </div>
                <div class="contributor-avatar">
                  <img 
                    :src="contributor.avatar_url" 
                    :alt="contributor.name"
                    @error="handleAvatarError"
                  />
                </div>
                <div class="contributor-info">
                  <div class="contributor-name">{{ contributor.name }}</div>
                  <div class="contributor-project">{{ contributor.login }}</div>
                  <div class="contribution-count">
                    <el-icon><Trophy /></el-icon>
                    {{ contributor.contributions }} 贡献
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 技术栈饼图 -->
        <div class="chart-card-small">
          <div class="card-header">
            <h3>技术栈分布</h3>
            <p>社区项目技术栈统计</p>
          </div>
          <div class="card-content">
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

      <!-- 下半部分 - 3列布局 -->
      <div class="bottom-grid">
        <!-- 项目版本 -->
        <div class="grid-card">
          <div class="card-header">
            <h3>项目版本</h3>
            <p>各版本使用情况分布</p>
          </div>
          <div class="card-content">
            <div class="version-list">
              <div class="version-item" v-for="(version, index) in appVersions" :key="index">
                <div class="version-header">
                  <span class="version-name">{{ version.name }}</span>
                  <span class="version-users">{{ version.users }}</span>
                </div>
                <div class="progress-bar">
                  <div class="progress-fill" :style="{ width: version.usage + '%' }"></div>
                </div>
                <div class="version-percentage">{{ version.usage }}%</div>
              </div>
            </div>
          </div>
        </div>

        <!-- 最新动态 -->
        <div class="grid-card">
          <div class="card-header">
            <h3>最新动态</h3>
            <p>社区活动</p>
          </div>
          <div class="card-content">
            <div class="activity-list">
              <div class="activity-item" v-for="(activity, index) in recentActivities" :key="index">
                <div class="activity-dot"></div>
                <div class="activity-content">
                  <h4>{{ activity.title }}</h4>
                  <p>{{ activity.description }}</p>
                  <span class="activity-time">{{ activity.time }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 访客分布 -->
        <div class="grid-card">
          <div class="card-header">
            <h3>访客分布</h3>
            <p>地理位置统计</p>
          </div>
          <div class="card-content">
            <div class="visitor-stats">
              <div class="visitor-total">125.7k 访问量来自 69 个国家</div>
              <div class="visitor-list">
                <div class="visitor-item" v-for="(location, index) in visitorLocations" :key="index">
                  <span class="visitor-country">{{ location.countryName }}</span>
                  <div class="visitor-info">
                    <span class="visitor-count">{{ location.count }}</span>
                    <span class="visitor-percentage">{{ location.percentage }}%</span>
                  </div>
                </div>
              </div>
              <div class="visitor-map">
                <el-icon class="map-icon"><Globe /></el-icon>
              </div>
            </div>
          </div>
        </div>
      </div>


    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { LineChart, PieChart } from 'echarts/charts'
import {
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
} from 'echarts/components'
import VChart from 'vue-echarts'
import { Star, Share, Link, Trophy, Document, Setting, User, Monitor, Calendar, Bell, Connection, Refresh, Download, Warning } from '@element-plus/icons-vue'
import { getHotProjects, getWeeklyContributors, getCommunityStats, type ProjectInfo, type ContributorInfo } from '@/api/community'

// 注册 ECharts 组件
use([
  CanvasRenderer,
  LineChart,
  PieChart,
  TitleComponent,
  TooltipComponent,
  LegendComponent,
  GridComponent
])

// 响应式数据
const hotProjects = ref<ProjectInfo[]>([])
const weeklyContributors = ref<ContributorInfo[]>([])
const projectsLoading = ref(false)
const contributorsLoading = ref(false)

// 统计数据
const totalProjects = ref(0)
const totalStars = ref(0)
const totalContributors = ref(0)

// 错误信息
const errorMessage = ref('')

// 社区公告数据
const announcements = ref([
  {
    date: '2024-07-31',
    title: '系统升级通知',
    content: '为提供更好的服务体验，系统将于8月5日进行升级维护，届时部分功能可能暂时不可用。'
  },
  {
    date: '2024-07-28',
    title: 'Dromara社区月度活动',
    content: '8月线上技术分享会将于8月15日晚8点举行，主题为"微服务架构最佳实践"，欢迎参与。'
  },
  {
    date: '2024-07-25',
    title: '新项目加入公告',
    content: '热烈欢迎"FastRequest"项目正式加入Dromara开源社区，这是一款高效的API调试工具。'
  }
])

// 技术栈数据
const techStack = ref([
  { name: 'Java', value: 45, color: '#F59E0B' },
  { name: 'JavaScript', value: 25, color: '#10B981' },
  { name: 'Go', value: 15, color: '#3B82F6' },
  { name: 'Python', value: 10, color: '#8B5CF6' },
  { name: 'Others', value: 5, color: '#EF4444' }
])

// 项目版本数据
const appVersions = ref([
  { name: 'Sa-Token v1.37.0', usage: 85, users: '12.3k' },
  { name: 'Hutool v5.8.25', usage: 72, users: '9.8k' },
  { name: 'Forest v1.5.36', usage: 58, users: '7.2k' },
  { name: 'TLog v1.5.0', usage: 45, users: '5.1k' },
  { name: 'Dynamic-Tp v1.1.6', usage: 32, users: '3.8k' },
  { name: 'Jpom v2.10.42', usage: 28, users: '2.9k' }
])



// 顶级贡献者数据
const topContributors = ref([
  { name: 'looly (Hutool)', progress: 95 },
  { name: 'click33 (Sa-Token)', progress: 88 },
  { name: 'bryan31 (Forest)', progress: 72 },
  { name: 'yanhom (Dynamic-Tp)', progress: 58 },
  { name: 'xiaoymin (Knife4j)', progress: 45 }
])

// 最新动态数据
const recentActivities = ref([
  {
    title: "Sa-Token发布新版本v1.37.0",
    description: "修复了多个安全漏洞，增强了JWT支持，优化了性能表现。",
    time: "2小时前"
  },
  {
    title: "Hutool工具类库获得新贡献",
    description: "社区成员提交了新的工具方法，用于处理日期和时间格式化。",
    time: "4小时前"
  },
  {
    title: "Forest HTTP客户端框架性能优化",
    description: "通过连接池优化和缓存机制改进，网络请求性能提升30%。",
    time: "6小时前"
  },
  {
    title: "TLog分布式日志追踪系统更新",
    description: "新增了链路追踪功能，支持更细粒度的性能监控。",
    time: "8小时前"
  }
])

// 访客位置数据
const visitorLocations = ref([
  { country: 'China', countryName: '中国', percentage: 35, count: '52.3k' },
  { country: 'United States', countryName: '美国', percentage: 18, count: '26.8k' },
  { country: 'Japan', countryName: '日本', percentage: 12, count: '17.9k' },
  { country: 'Germany', countryName: '德国', percentage: 8, count: '11.2k' },
  { country: 'South Korea', countryName: '韩国', percentage: 7, count: '10.4k' },
  { country: 'Others', countryName: '其他', percentage: 20, count: '29.8k' }
])

// 待办事项数据
const todoItems = ref([
  { task: '安排核心团队会议', completed: false },
  { task: '审核 Hutool 的 PR', completed: false },
  { task: '更新项目文档', completed: true },
  { task: '准备月度社区报告', completed: false },
  { task: '检查系统性能监控', completed: false },
  { task: '回复用户反馈邮件', completed: true },
  { task: '更新技术文档', completed: false },
  { task: '准备下周会议议程', completed: false }
])

// 统计数据
const statsData = ref([
  { 
    title: '项目总数', 
    value: '45', 
    change: '+3 From last Week', 
    icon: 'Connection', 
    color: '#2563EB', 
    bg: '#EFF6FF' 
  },
  { 
    title: '活跃贡献者', 
    value: '1,235', 
    change: '+8% From last Week', 
    icon: 'User', 
    color: '#16A34A', 
    bg: '#F0FDF4' 
  },
  { 
    title: 'Star总数', 
    value: '25,840', 
    change: '+12% From last Week', 
    icon: 'Star', 
    color: '#CA8A04', 
    bg: '#FFFBEB' 
  },
  { 
    title: 'PR合并数', 
    value: '4,567', 
    change: '+15% From last Week', 
    icon: 'Refresh', 
    color: '#9333EA', 
    bg: '#FAF5FF' 
  },
  { 
    title: '下载量', 
    value: '2,315K', 
    change: '+5% From last Week', 
    icon: 'Download', 
    color: '#4F46E5', 
    bg: '#EEF2FF' 
  },
  { 
    title: 'Issue处理', 
    value: '1,845', 
    change: '+2% From last Week', 
    icon: 'Warning', 
    color: '#DC2626', 
    bg: '#FEF2F2' 
  }
])

// 快速入口
const quickEntries = ref([
  { title: '项目文档', icon: 'Document', link: 'https://dromara.org/zh-cn/docs/' },
  { title: '系统设置', icon: 'Setting', link: '/system/user' },
  { title: '个人中心', icon: 'User', link: '/user/profile' },
  { title: '系统监控', icon: 'Monitor', link: '/monitor/server' },
  { title: '日程安排', icon: 'Calendar', link: '#' },
  { title: '消息通知', icon: 'Bell', link: '#' }
])

// 时间范围选择
const selectedTimeRange = ref('7')

// 社区活跃度数据
const communityActivity = ref([
  { name: 'Dec 01', commits: 120, issues: 45, prs: 38 },
  { name: 'Dec 02', commits: 150, issues: 52, prs: 41 },
  { name: 'Dec 03', commits: 180, issues: 38, prs: 45 },
  { name: 'Dec 04', commits: 165, issues: 62, prs: 52 },
  { name: 'Dec 05', commits: 195, issues: 48, prs: 38 },
  { name: 'Dec 06', commits: 210, issues: 55, prs: 48 }
])

// 技术栈饼图配置
const techChartOption = computed(() => ({
  tooltip: {
    trigger: 'item',
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: '#e2e8f0',
    borderWidth: 1,
    textStyle: {
      color: '#374151'
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
          fontWeight: 'bold'
        }
      },
      labelLine: {
        show: false
      },
      data: techStack.value.map(item => ({
        value: item.value,
        name: item.name,
        itemStyle: {
          color: item.color
        }
      }))
    }
  ]
}))

// 社区活跃度图表配置
const chartOption = computed(() => ({
  tooltip: {
    trigger: 'axis',
    backgroundColor: 'rgba(255, 255, 255, 0.95)',
    borderColor: '#e2e8f0',
    borderWidth: 1,
    textStyle: {
      color: '#374151'
    },
    formatter: function(params: any) {
      let result = params[0].name + '<br/>'
      params.forEach((param: any) => {
        result += param.marker + param.seriesName + ': ' + param.value + '<br/>'
      })
      return result
    }
  },
  legend: {
    data: ['提交数', 'Issue数', 'PR数'],
    textStyle: {
      color: '#6B7280'
    },
    top: 10
  },
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis: {
    type: 'category',
    boundaryGap: false,
    data: communityActivity.value.map(item => item.name),
    axisLine: {
      lineStyle: {
        color: '#E5E7EB'
      }
    },
    axisLabel: {
      color: '#9CA3AF'
    }
  },
  yAxis: {
    type: 'value',
    axisLine: {
      lineStyle: {
        color: '#E5E7EB'
      }
    },
    axisLabel: {
      color: '#9CA3AF'
    },
    splitLine: {
      lineStyle: {
        color: '#F3F4F6'
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
            { offset: 0, color: 'rgba(16, 185, 129, 0.8)' },
            { offset: 1, color: 'rgba(16, 185, 129, 0.1)' }
          ]
        }
      },
      lineStyle: {
        color: '#10B981',
        width: 2
      },
      itemStyle: {
        color: '#10B981'
      },
      data: communityActivity.value.map(item => item.commits),
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
            { offset: 0, color: 'rgba(59, 130, 246, 0.8)' },
            { offset: 1, color: 'rgba(59, 130, 246, 0.1)' }
          ]
        }
      },
      lineStyle: {
        color: '#3B82F6',
        width: 2
      },
      itemStyle: {
        color: '#3B82F6'
      },
      data: communityActivity.value.map(item => item.issues),
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
            { offset: 0, color: 'rgba(139, 92, 246, 0.8)' },
            { offset: 1, color: 'rgba(139, 92, 246, 0.1)' }
          ]
        }
      },
      lineStyle: {
        color: '#8B5CF6',
        width: 2
      },
      itemStyle: {
        color: '#8B5CF6'
      },
      data: communityActivity.value.map(item => item.prs),
      smooth: true
    }
  ]
}))

// 获取热门项目
const fetchHotProjects = async () => {
  projectsLoading.value = true
  errorMessage.value = ''
  try {
    const projects = await getHotProjects(1, 20)
    hotProjects.value = projects
    if (projects.length > 0) {
      console.log('✅ 成功获取热门项目:', projects.length, '个')
    }
  } catch (error) {
    console.error('❌ 获取热门项目失败:', error)
    errorMessage.value = '获取热门项目失败，请检查网络连接'
  } finally {
    projectsLoading.value = false
  }
}

// 获取贡献榜
const fetchWeeklyContributors = async () => {
  contributorsLoading.value = true
  try {
    const contributors = await getWeeklyContributors()
    weeklyContributors.value = contributors
    if (contributors.length > 0) {
      console.log('✅ 成功获取贡献榜:', contributors.length, '个贡献者')
    }
  } catch (error) {
    console.error('❌ 获取贡献榜失败:', error)
  } finally {
    contributorsLoading.value = false
  }
}

// 获取社区统计数据
const fetchCommunityStats = async () => {
  try {
    const stats = await getCommunityStats()
    totalProjects.value = stats.totalProjects
    totalStars.value = stats.totalStars
    totalContributors.value = stats.totalContributors
    console.log('✅ 社区统计:', stats)
  } catch (e) {
    totalProjects.value = 0
    totalStars.value = 0
    totalContributors.value = 0
  }
}

// 打开链接
const openLink = (url: string) => {
  window.open(url, '_blank')
}

// 格式化数字
const formatNumber = (num: number): string => {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

// 获取编程语言颜色
const getLanguageColor = (language: string): string => {
  const colors: Record<string, string> = {
    'Java': '#b07219',
    'JavaScript': '#f1e05a',
    'TypeScript': '#2b7489',
    'Python': '#3572A5',
    'Go': '#00ADD8',
    'C++': '#f34b7d',
    'C#': '#239120',
    'PHP': '#4F5D95',
    'Vue': '#4FC08D',
    'React': '#61DAFB'
  }
  return colors[language] || '#666'
}

// 获取排名样式
const getRankClass = (index: number): string => {
  if (index === 0) return 'gold'
  if (index === 1) return 'silver'
  if (index === 2) return 'bronze'
  return ''
}

// 处理头像加载失败
const handleAvatarError = (event: Event) => {
  const target = event.target as HTMLImageElement;
  target.src = 'https://gitee.com/assets/no_portrait-2b772d6b.png';
};

// 页面加载时获取数据
onMounted(async () => {
  // 并行加载所有数据以提高性能
  await Promise.all([
    fetchHotProjects(),
    fetchWeeklyContributors(),
    fetchCommunityStats()
  ])
  console.log('首页数据加载完成')
})
</script>

<style scoped>
/* 配色方案选择 - 取消注释您喜欢的配色 */

/* 方案1：浅灰绿色渐变（当前使用） */
.community-header {
  background: linear-gradient(135deg, #B4E4D9 0%, #8FD3C7 100%);
  color: #2A3F54;
  padding: 60px 0;
  position: relative;
  overflow: hidden;
}

/* 方案2：浅绿色渐变 */
/* .community-header {
  background: linear-gradient(135deg, #56ab2f 0%, #a8e6cf 100%);
  color: white;
  padding: 60px 0;
  position: relative;
  overflow: hidden;
} */

/* 方案3：浅橙色渐变 */
/* .community-header {
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  color: white;
  padding: 60px 0;
  position: relative;
  overflow: hidden;
} */

/* 方案4：浅青色渐变 */
/* .community-header {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  color: white;
  padding: 60px 0;
  position: relative;
  overflow: hidden;
} */

/* 方案5：浅粉色渐变 */
/* .community-header {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: white;
  padding: 60px 0;
  position: relative;
  overflow: hidden;
} */

.community-home {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 0;
}

.community-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1000 100" fill="rgba(255,255,255,0.1)"><polygon points="0,0 1000,0 1000,100"/></svg>');
  background-size: 100% 100%;
  opacity: 0.1;
}

.header-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
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
  color: #2A3F54;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.slogan {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: #4A6B7F;
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.desc {
  margin: 0 0 25px 0;
  font-size: 16px;
  color: #5A7A8F;
  line-height: 1.6;
  max-width: 600px;
}

.quick-links {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.quick-links .el-button {
  border-radius: 20px;
  padding: 8px 20px;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.quick-links .el-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
}

.stats-container {
  background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
  padding: 40px 0;
  margin: 20px 0;
  border-radius: 0;
  position: relative;
}

.stats-container::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent 0%, rgba(180, 228, 217, 0.3) 50%, transparent 100%);
}

.stats-section {
  display: flex;
  gap: 20px;
  align-items: center;
  flex-wrap: nowrap;
  justify-content: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  position: relative;
  z-index: 1;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px 18px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 12px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(180, 228, 217, 0.2);
  transition: all 0.3s ease;
  min-width: 160px;
  flex: 1;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.95);
  transform: translateY(-2px);
  border-color: rgba(180, 228, 217, 0.5);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}

.stat-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-value {
  font-size: 22px;
  font-weight: 700;
  color: #2A3F54;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.1);
}

.stat-title {
  font-size: 11px;
  color: #4A6B7F;
  font-weight: 500;
}

.stat-change {
  font-size: 9px;
  font-weight: 500;
}

.stat-change.positive {
  color: #16A34A;
}

.stat-change.negative {
  color: #DC2626;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0;
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
  border: 1px solid #F1F5F9;
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
  border: 1px solid #F1F5F9;
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
  border-color: #B4E4D9;
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
  background: #B4E4D9;
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
  color: #B4E4D9;
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
  color: #B4E4D9;
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
  gap: 24px;
  width: 320px;
  flex-shrink: 0;
  height: 300px;
}

.sidebar-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #F1F5F9;
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

.sidebar-card:first-child {
  height: 90px;
}

.sidebar-card:last-child {
  height: 320px;
}

.sidebar-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 侧边栏活跃用户样式优化 */
.sidebar-card .header-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  width: 100%;
}

.sidebar-card .header-left {
  display: flex;
  flex-direction: column;
}

.sidebar-card .header-right {
  display: flex;
  align-items: center;
}

.sidebar-card .users-number {
  font-size: 28px;
  font-weight: 800;
  color: #059669;
  letter-spacing: -0.025em;
}



/* 侧边栏待办事项样式优化 */
.sidebar-card .todo-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
  overflow-y: auto;
  padding: 6px 8px 16px 0;
  max-height: 220px;
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
  background: #10B981;
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
  background: #F9FAFB;
  transition: all 0.3s ease;
  border: 1px solid #E2E8F0;
  min-height: 28px;
}

.sidebar-card .todo-item:hover {
  background: #F3F4F6;
  transform: translateX(4px);
  border-color: #10B981;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.15);
}

.sidebar-card .todo-checkbox {
  width: 16px;
  height: 16px;
  border: 2px solid #D1D5DB;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.sidebar-card .todo-checkbox.completed {
  background: #10B981;
  border-color: #10B981;
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
  color: #9CA3AF;
  text-decoration: line-through;
}

.chart-card {
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
  border: 1px solid #F1F5F9;
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
  color: #1F2937;
}

.chart-title p {
  margin: 0;
  font-size: 14px;
  color: #6B7280;
}

.chart-container {
  height: 300px;
  position: relative;
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
  border: 1px solid #F1F5F9;
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
  background: linear-gradient(90deg, #10B981, #3B82F6, #8B5CF6);
  border-radius: 20px 20px 0 0;
}

.chart-card-small:hover {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  transform: translateY(-4px);
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
  color: #1F2937;
  letter-spacing: -0.025em;
}

.card-header p {
  margin: 0;
  font-size: 13px;
  color: #6B7280;
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
  background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #E2E8F0;
}

.project-item:hover {
  background: linear-gradient(135deg, #F0FDF4 0%, #ECFDF5 100%);
  transform: translateX(6px);
  border-color: #10B981;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.15);
}

.project-rank {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  background: linear-gradient(135deg, #10B981, #059669);
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
  color: #1F2937;
  margin-bottom: 6px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  letter-spacing: -0.025em;
}

.project-desc {
  font-size: 13px;
  color: #6B7280;
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
  color: #6B7280;
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
  color: #6B7280;
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
  background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
  transition: all 0.3s ease;
  cursor: pointer;
  border: 1px solid #E2E8F0;
}

.contributor-item:hover {
  background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%);
  transform: translateX(6px);
  border-color: #F59E0B;
  box-shadow: 0 4px 12px rgba(245, 158, 11, 0.15);
}

.contributor-rank {
  width: 28px;
  height: 28px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 700;
  color: white;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.contributor-rank.gold {
  background: linear-gradient(135deg, #F59E0B, #D97706);
}

.contributor-rank.silver {
  background: linear-gradient(135deg, #6B7280, #4B5563);
}

.contributor-rank.bronze {
  background: linear-gradient(135deg, #D97706, #B45309);
}

.contributor-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
  border: 3px solid #E5E7EB;
  background: #F3F4F6;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.contributor-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.contributor-avatar:hover img {
  transform: scale(1.1);
}

.contributor-info {
  flex: 1;
  min-width: 0;
}

.contributor-name {
  font-size: 15px;
  font-weight: 700;
  color: #1F2937;
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  letter-spacing: -0.025em;
}

.contributor-project {
  font-size: 12px;
  color: #6B7280;
  margin-bottom: 6px;
  font-weight: 500;
}

.contribution-count {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #10B981;
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
  background: #F9FAFB;
  transition: all 0.3s ease;
}

.tech-legend-item:hover {
  background: #F3F4F6;
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
  color: #6B7280;
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
  color: #6B7280;
  font-weight: 500;
}

.version-percentage {
  font-size: 12px;
  color: #6B7280;
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
  border-top: 1px solid #E5E7EB;
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
  color: #6B7280;
}

.progress-label {
  font-size: 11px;
  color: #10B981;
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
  background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
  transition: all 0.3s ease;
  border: 1px solid #E2E8F0;
}

.activity-item:hover {
  background: linear-gradient(135deg, #F0FDF4 0%, #ECFDF5 100%);
  transform: translateX(6px);
  border-color: #10B981;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.15);
}

.activity-dot {
  width: 10px;
  height: 10px;
  background: #10B981;
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
  color: #1F2937;
  letter-spacing: -0.025em;
}

.activity-content p {
  margin: 0 0 10px 0;
  font-size: 13px;
  color: #6B7280;
  line-height: 1.5;
}

.activity-time {
  font-size: 12px;
  color: #9CA3AF;
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
  color: #1F2937;
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
  background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
  transition: all 0.3s ease;
  border: 1px solid #E2E8F0;
}

.visitor-item:hover {
  background: linear-gradient(135deg, #FEF3C7 0%, #FDE68A 100%);
  transform: translateX(4px);
  border-color: #F59E0B;
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
  color: #6B7280;
  font-weight: 500;
}

.visitor-percentage {
  color: #9CA3AF;
  font-weight: 600;
}

.visitor-map {
  height: 100px;
  background: linear-gradient(135deg, #F0FDF4 0%, #ECFDF5 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: 20px;
  border: 1px solid #D1FAE5;
  box-shadow: 0 2px 8px rgba(16, 185, 129, 0.1);
}

.map-icon {
  font-size: 36px;
  color: #10B981;
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
  background: linear-gradient(135deg, #F8FAFC 0%, #F1F5F9 100%);
  transition: all 0.3s ease;
  border: 1px solid #E2E8F0;
}

.todo-item:hover {
  background: linear-gradient(135deg, #F0FDF4 0%, #ECFDF5 100%);
  transform: translateX(6px);
  border-color: #10B981;
  box-shadow: 0 4px 12px rgba(16, 185, 129, 0.15);
}

.todo-checkbox {
  width: 20px;
  height: 20px;
  border: 2px solid #D1D5DB;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  flex-shrink: 0;
}

.todo-checkbox.completed {
  background: linear-gradient(135deg, #10B981, #059669);
  border-color: #10B981;
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
  color: #9CA3AF;
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
  color: #1F2937;
  margin-bottom: 12px;
  letter-spacing: -0.025em;
}

.users-label {
  font-size: 14px;
  color: #6B7280;
  margin-bottom: 24px;
  font-weight: 500;
}

.users-icon {
  width: 80px;
  height: 80px;
  background: linear-gradient(135deg, #F0FDF4 0%, #ECFDF5 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  border: 3px solid #D1FAE5;
  box-shadow: 0 4px 16px rgba(16, 185, 129, 0.2);
}

.users-icon .el-icon {
  font-size: 36px;
  color: #10B981;
}

/* 进度条样式优化 */
.progress-bar {
  width: 100%;
  height: 8px;
  background: #E5E7EB;
  border-radius: 4px;
  overflow: hidden;
  position: relative;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(90deg, #10B981, #059669);
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
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
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