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
              <h2>🔥 热门项目</h2>
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
                  <div class="project-avatar">
                    <img :src="project.avatar_url" :alt="project.name">
                  </div>
                  <div class="project-info">
                    <h3>{{ project.name }}</h3>
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
        </el-col>

        <!-- 右栏：本周贡献榜 -->
        <el-col :span="8">
          <div class="section-card">
            <div class="section-header">
              <h2>👑 贡献榜</h2>
              <p>本周活跃贡献者</p>
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
                  <img :src="contributor.avatar_url" :alt="contributor.name || contributor.login">
                </div>
                <div class="contributor-info">
                  <h4>{{ contributor.name || contributor.login }}</h4>
                  <p>@{{ contributor.login }}</p>
                  <div class="contribution-count">
                    <el-icon><Trophy /></el-icon>
                    <span>{{ contributor.contributions }} 贡献</span>
                  </div>
                </div>
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
import { Star, Share, Link, Trophy } from '@element-plus/icons-vue'
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

// 获取热门项目
const fetchHotProjects = async () => {
  projectsLoading.value = true
  errorMessage.value = ''
  try {
    const projects = await getHotProjects(1, 15)
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

// 页面加载时获取数据
onMounted(() => {
  fetchHotProjects()
  fetchWeeklyContributors()
  fetchCommunityStats()
})
</script>

<style scoped>
.community-home {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 0;
}

.community-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  border-radius: 12px;
  padding: 30px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  height: 100%;
}

.section-header {
  margin-bottom: 30px;
  text-align: center;
}

.section-header h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #2c3e50;
}

.section-header p {
  margin: 0;
  color: #7f8c8d;
  font-size: 14px;
}

.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.project-card {
  border: 1px solid #e8e8e8;
  border-radius: 8px;
  padding: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #fafafa;
}

.project-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
  border-color: #3498db;
}

.project-header {
  display: flex;
  align-items: flex-start;
  gap: 15px;
  margin-bottom: 15px;
}

.project-avatar {
  width: 50px;
  height: 50px;
  border-radius: 8px;
  overflow: hidden;
  flex-shrink: 0;
}

.project-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
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
  gap: 15px;
}

.contributor-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 15px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  background: #f8f9fa;
}

.contributor-item:hover {
  background: #e9ecef;
  transform: translateX(5px);
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
  background: linear-gradient(45deg, #FFD700, #FFA500);
}

.rank-badge.silver {
  background: linear-gradient(45deg, #C0C0C0, #A8A8A8);
}

.rank-badge.bronze {
  background: linear-gradient(45deg, #CD7F32, #B8860B);
}

.contributor-avatar {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}

.contributor-avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.contributor-info {
  flex: 1;
}

.contributor-info h4 {
  margin: 0 0 5px 0;
  font-size: 16px;
  color: #2c3e50;
  font-weight: 600;
}

.contributor-info p {
  margin: 0 0 8px 0;
  font-size: 14px;
  color: #7f8c8d;
}

.contribution-count {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #e67e22;
  font-weight: 600;
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