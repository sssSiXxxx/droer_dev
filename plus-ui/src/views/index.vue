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
        
        <div class="stats-section">
          <div class="stat-item">
            <div class="stat-number">{{ totalProjects }}</div>
            <div class="stat-label">项目数量</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ totalStars }}</div>
            <div class="stat-label">总 Star 数</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">{{ totalContributors }}</div>
            <div class="stat-label">贡献者</div>
          </div>
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <div class="main-content">
      <el-row :gutter="24">
        <!-- 左栏：热门项目 -->
        <el-col :span="16">
          <div class="section-card">
            <div class="section-header">
              <h2>热门项目</h2>
              <p>按 Star 数排序的社区热门项目</p>
            </div>
            
            <div class="project-grid" v-loading="projectsLoading">
              <div 
                v-for="project in hotProjects" 
                :key="project.id"
                class="project-card"
                @click="openLink(project.html_url)"
              >
                <div class="project-header">
                  <div class="project-name-highlight">
                    <span class="project-name">{{ project.name }}</span>
                  </div>
                  <div class="project-info">
                    <p class="project-desc">{{ project.description || '暂无描述' }}</p>
                  </div>
                </div>
                
                <div class="project-stats">
                  <div class="stat">
                    <el-icon><Star /></el-icon>
                    <span>{{ formatNumber(project.stargazers_count) }}</span>
                  </div>
                  <div class="stat">
                    <el-icon><Share /></el-icon>
                    <span>{{ formatNumber(project.forks_count) }}</span>
                  </div>
                  <div class="stat language" v-if="project.language">
                    <span class="language-dot" :style="{ backgroundColor: getLanguageColor(project.language) }"></span>
                    <span>{{ project.language }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 新增：社区公告 -->
          <div class="section-card" style="margin-top: 20px;">
            <div class="section-header">
              <h2>社区公告</h2>
              <p>最新社区动态与公告信息</p>
            </div>
            
            <div class="announcement-list">
              <div class="announcement-item" v-for="(item, index) in announcements" :key="index">
                <div class="announcement-date">{{ item.date }}</div>
                <div class="announcement-content">
                  <h4>{{ item.title }}</h4>
                  <p>{{ item.content }}</p>
                </div>
              </div>
            </div>
          </div>
        </el-col>

        <!-- 右栏：本周贡献榜 -->
        <el-col :span="8">
          <div class="section-card">
            <div class="section-header">
              <h2>本周贡献榜</h2>
              <p>最近7天活跃贡献者</p>
            </div>
            
            <div class="contributor-list" v-loading="contributorsLoading">
              <div 
                v-for="(contributor, index) in weeklyContributors" 
                :key="contributor.id"
                class="contributor-item"
                @click="openLink(contributor.html_url)"
              >
                <div class="rank-badge" :class="getRankClass(index)">
                  {{ index + 1 }}
                </div>
                <div class="contributor-avatar">
                  <img 
                    :src="contributor.avatar_url" 
                    :alt="contributor.name || contributor.login"
                    @error="handleAvatarError"
                    :onerror="'this.src=\'https://gitee.com/assets/no_portrait-2b772d6b.png\''"
                  >
                </div>
                <div class="contributor-info">
                  <h4>{{ contributor.name || contributor.login }}</h4>
                  <p>@{{ contributor.login }}</p>
                  <div class="contribution-count">
                    <el-icon><Trophy /></el-icon>
                    <span>本周贡献: {{ contributor.contributions }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- 新增：快速入口 -->
          <div class="section-card" style="margin-top: 20px;">
            <div class="section-header">
              <h2>快速入口</h2>
              <p>常用功能与链接</p>
            </div>
            
            <div class="quick-entry-grid">
              <div class="quick-entry-item" v-for="(item, index) in quickEntries" :key="index" @click="openLink(item.link)">
                <el-icon class="quick-entry-icon"><component :is="item.icon" /></el-icon>
                <span class="quick-entry-title">{{ item.title }}</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { Star, Share, Link, Trophy, Document, Setting, User, Monitor, Calendar, Bell } from '@element-plus/icons-vue'
import { getHotProjects, getWeeklyContributors, getCommunityStats, type ProjectInfo, type ContributorInfo } from '@/api/community'

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

// 快速入口
const quickEntries = ref([
  { title: '项目文档', icon: 'Document', link: 'https://dromara.org/zh-cn/docs/' },
  { title: '系统设置', icon: 'Setting', link: '/system/user' },
  { title: '个人中心', icon: 'User', link: '/user/profile' },
  { title: '系统监控', icon: 'Monitor', link: '/monitor/server' },
  { title: '日程安排', icon: 'Calendar', link: '#' },
  { title: '消息通知', icon: 'Bell', link: '#' }
])

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
.community-home {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 0;
}

.community-header {
  background: #409eff;
  color: white;
  padding: 60px 0;
  position: relative;
  overflow: hidden;
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
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
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
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.slogan {
  margin: 0 0 8px 0;
  font-size: 18px;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 500;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.2);
}

.desc {
  margin: 0 0 25px 0;
  font-size: 16px;
  color: rgba(255, 255, 255, 0.8);
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

.stats-section {
  display: flex;
  gap: 40px;
  align-items: center;
}

.stat-item {
  text-align: center;
  padding: 20px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s ease;
  min-width: 100px;
}

.stat-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-3px);
}

.stat-number {
  font-size: 28px;
  font-weight: 700;
  color: white;
  margin-bottom: 8px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.stat-label {
  font-size: 14px;
  color: rgba(255, 255, 255, 0.8);
  font-weight: 500;
}

.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-card {
  background: white;
  border-radius: 4px;
  padding: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
  height: 100%;
  border: 1px solid #ebeef5;
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
  border-color: #409eff;
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
  background: #409eff;
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
  color: #409eff;
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
  color: #409eff;
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
  
  .stats-section {
    justify-content: center;
    gap: 20px;
  }
  
  .stat-item {
    min-width: 80px;
    padding: 15px;
  }
  
  .stat-number {
    font-size: 24px;
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
}
</style>