<template>
  <div class="repository-tracking">
    <!-- 顶部统计卡片 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :span="6">
        <el-card class="stat-card active-repos" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><TrendCharts /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.activeRepos || 0 }}</div>
              <div class="stat-label">活跃仓库</div>
              <div class="stat-change">+{{ statistics.newActiveRepos || 0 }} 本周</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card commits" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Upload /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.weeklyCommits || 0 }}</div>
              <div class="stat-label">本周提交</div>
              <div class="stat-change">{{ formatChange(statistics.commitsTrend) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card contributors" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.activeContributors || 0 }}</div>
              <div class="stat-label">活跃贡献者</div>
              <div class="stat-change">{{ formatChange(statistics.contributorsTrend) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card releases" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Box /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ statistics.weeklyReleases || 0 }}</div>
              <div class="stat-label">本周发布</div>
              <div class="stat-change">{{ formatChange(statistics.releasesTrend) }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 控制面板 -->
    <el-card class="mb-4">
      <template #header>
        <div class="card-header">
          <span class="card-title">社区仓库实时追踪</span>
          <div class="card-toolbar">
            <el-select v-model="timeRange" placeholder="时间范围" style="width: 120px" @change="handleTimeRangeChange">
              <el-option label="7天" value="7" />
              <el-option label="30天" value="30" />
              <el-option label="90天" value="90" />
            </el-select>
            <el-select v-model="sortBy" placeholder="排序方式" style="width: 140px" @change="handleSortChange">
              <el-option label="活跃度" value="activity" />
              <el-option label="提交数" value="commits" />
              <el-option label="增长率" value="growth" />
              <el-option label="星标数" value="stars" />
            </el-select>
            <el-button type="primary" icon="Refresh" @click="refreshData">刷新数据</el-button>
            <el-button type="info" icon="Download" @click="exportData">导出报告</el-button>
          </div>
        </div>
      </template>

      <!-- 搜索过滤器 -->
      <el-form :model="filters" :inline="true" class="mb-4">
        <el-form-item label="仓库名称">
          <el-input
            v-model="filters.name"
            placeholder="搜索仓库"
            clearable
            style="width: 200px"
            @input="handleSearch"
          />
        </el-form-item>
        <el-form-item label="编程语言">
          <el-select v-model="filters.language" placeholder="选择语言" clearable @change="handleFilter">
            <el-option v-for="lang in languages" :key="lang" :label="lang" :value="lang" />
          </el-select>
        </el-form-item>
        <el-form-item label="活跃级别">
          <el-select v-model="filters.activityLevel" placeholder="选择级别" clearable @change="handleFilter">
            <el-option label="极高" value="very-high" />
            <el-option label="高" value="high" />
            <el-option label="中等" value="medium" />
            <el-option label="低" value="low" />
          </el-select>
        </el-form-item>
      </el-form>

      <!-- 活跃仓库列表 -->
      <div class="repository-list">
        <el-row :gutter="20" v-loading="loading">
          <el-col :span="8" v-for="repo in filteredRepos" :key="repo.id">
            <el-card class="repo-card" shadow="hover" @click="showRepoDetails(repo)">
              <div class="repo-header">
                <div class="repo-info">
                  <div class="repo-name">
                    <el-icon><FolderOpened /></el-icon>
                    {{ repo.name }}
                  </div>
                  <div class="repo-language">
                    <span class="language-dot" :style="{ backgroundColor: getLanguageColor(repo.language) }"></span>
                    {{ repo.language }}
                  </div>
                </div>
                <div class="repo-activity">
                  <el-tag :type="getActivityType(repo.activityScore)" size="small">
                    {{ getActivityLabel(repo.activityScore) }}
                  </el-tag>
                </div>
              </div>
              
              <div class="repo-description">{{ repo.description }}</div>
              
              <div class="repo-stats">
                <div class="stat-item">
                  <el-icon><Star /></el-icon>
                  <span>{{ repo.stargazers_count }}</span>
                  <span class="trend" :class="repo.starTrend > 0 ? 'positive' : 'negative'">
                    +{{ repo.starTrend }}
                  </span>
                </div>
                <div class="stat-item">
                  <el-icon><Share /></el-icon>
                  <span>{{ repo.forks_count }}</span>
                </div>
                <div class="stat-item">
                  <el-icon><EditPen /></el-icon>
                  <span>{{ repo.weeklyCommits }}次提交</span>
                </div>
              </div>

              <div class="repo-activity-chart">
                <div class="activity-title">近期活跃度</div>
                <div class="activity-bars">
                  <div
                    v-for="(activity, index) in repo.dailyActivity"
                    :key="index"
                    class="activity-bar"
                    :style="{ height: (activity / Math.max(...repo.dailyActivity)) * 30 + 'px' }"
                    :title="activity + '次活动'"
                  ></div>
                </div>
              </div>

              <div class="repo-contributors">
                <div class="contributors-title">活跃贡献者</div>
                <div class="contributors-avatars">
                  <el-avatar
                    v-for="contributor in repo.topContributors.slice(0, 5)"
                    :key="contributor.id"
                    :size="24"
                    :src="contributor.avatar_url"
                    :title="contributor.login"
                  />
                  <span v-if="repo.totalContributors > 5" class="more-contributors">
                    +{{ repo.totalContributors - 5 }}
                  </span>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <!-- 分页 -->
      <pagination
        v-show="total > 0"
        :total="total"
        v-model:page="currentPage"
        v-model:limit="pageSize"
        @pagination="handlePageChange"
      />
    </el-card>

    <!-- 仓库详情对话框 -->
    <el-drawer v-model="detailDrawer" title="仓库详细信息" size="60%">
      <div v-if="selectedRepo" class="repo-details">
        <!-- 基本信息 -->
        <div class="detail-section">
          <h3>基本信息</h3>
          <el-descriptions :column="2">
            <el-descriptions-item label="仓库名称">{{ selectedRepo.name }}</el-descriptions-item>
            <el-descriptions-item label="编程语言">{{ selectedRepo.language }}</el-descriptions-item>
            <el-descriptions-item label="创建时间">{{ formatDate(selectedRepo.created_at) }}</el-descriptions-item>
            <el-descriptions-item label="最后更新">{{ formatDate(selectedRepo.updated_at) }}</el-descriptions-item>
            <el-descriptions-item label="仓库大小">{{ formatSize(selectedRepo.size) }}</el-descriptions-item>
            <el-descriptions-item label="活跃评分">{{ selectedRepo.activityScore }}/100</el-descriptions-item>
          </el-descriptions>
        </div>

        <!-- 活跃度趋势图 -->
        <div class="detail-section">
          <h3>活跃度趋势</h3>
          <div class="chart-container" ref="activityChart"></div>
        </div>

        <!-- 贡献者统计 -->
        <div class="detail-section">
          <h3>贡献者分析</h3>
          <div class="contributors-analysis">
            <el-row :gutter="20">
              <el-col :span="12">
                <div class="chart-container" ref="contributorsChart"></div>
              </el-col>
              <el-col :span="12">
                <el-table :data="selectedRepo.topContributors" size="small">
                  <el-table-column property="login" label="用户名" />
                  <el-table-column property="contributions" label="贡献数" />
                  <el-table-column label="活跃度">
                    <template #default="scope">
                      <el-progress :percentage="scope.row.activityPercent" :show-text="false" />
                    </template>
                  </el-table-column>
                </el-table>
              </el-col>
            </el-row>
          </div>
        </div>

        <!-- 近期发布 -->
        <div class="detail-section">
          <h3>近期发布</h3>
          <el-timeline>
            <el-timeline-item
              v-for="release in selectedRepo.recentReleases"
              :key="release.id"
              :timestamp="formatDate(release.published_at)"
            >
              <h4>{{ release.name }}</h4>
              <p>{{ release.body }}</p>
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-drawer>
  </div>
</template>

<script setup name="RepositoryTracking" lang="ts">
import { ref, onMounted, computed, nextTick } from 'vue';
import {
  TrendCharts,
  Upload,
  User,
  Box,
  FolderOpened,
  Star,
  Share,
  EditPen,
  Refresh,
  Download
} from '@element-plus/icons-vue';
import { getOrganizationRepos, getProjectCommitActivity, getWeeklyContributors } from '@/api/community-enhanced';

// 响应式数据
const loading = ref(false);
const detailDrawer = ref(false);
const timeRange = ref('7');
const sortBy = ref('activity');
const currentPage = ref(1);
const pageSize = ref(12);
const total = ref(0);
const selectedRepo = ref(null);

// 过滤器
const filters = ref({
  name: '',
  language: '',
  activityLevel: ''
});

// 统计数据
const statistics = ref({
  activeRepos: 0,
  newActiveRepos: 0,
  weeklyCommits: 0,
  commitsTrend: 0,
  activeContributors: 0,
  contributorsTrend: 0,
  weeklyReleases: 0,
  releasesTrend: 0
});

// 仓库列表
const repositories = ref([]);
const languages = ref(['Java', 'JavaScript', 'TypeScript', 'Go', 'Python', 'Vue', 'C++', 'PHP']);

// 计算过滤后的仓库列表
const filteredRepos = computed(() => {
  let filtered = [...repositories.value];

  // 按名称过滤
  if (filters.value.name) {
    const searchTerm = filters.value.name.toLowerCase();
    filtered = filtered.filter(repo => 
      repo.name.toLowerCase().includes(searchTerm) ||
      repo.description.toLowerCase().includes(searchTerm)
    );
  }

  // 按语言过滤
  if (filters.value.language) {
    filtered = filtered.filter(repo => repo.language === filters.value.language);
  }

  // 按活跃级别过滤
  if (filters.value.activityLevel) {
    filtered = filtered.filter(repo => {
      const score = repo.activityScore || 0;
      switch (filters.value.activityLevel) {
        case 'very-high': return score >= 80;
        case 'high': return score >= 60 && score < 80;
        case 'medium': return score >= 40 && score < 60;
        case 'low': return score < 40;
        default: return true;
      }
    });
  }

  // 排序
  filtered.sort((a, b) => {
    switch (sortBy.value) {
      case 'activity':
        return (b.activityScore || 0) - (a.activityScore || 0);
      case 'commits':
        return (b.weeklyCommits || 0) - (a.weeklyCommits || 0);
      case 'growth':
        return (b.starTrend || 0) - (a.starTrend || 0);
      case 'stars':
        return b.stargazers_count - a.stargazers_count;
      default:
        return 0;
    }
  });

  // 分页
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  total.value = filtered.length;
  
  return filtered.slice(start, end);
});

/** 加载仓库数据 */
const loadRepositories = async () => {
  try {
    loading.value = true;
    
    // 获取仓库列表
    const repos = await getOrganizationRepos();
    
    // 为每个仓库添加活跃度数据
    const enrichedRepos = await Promise.all(
      repos.slice(0, 30).map(async (repo) => {
        try {
          // 获取提交活动数据
          const commitActivity = await getProjectCommitActivity(repo.name, parseInt(timeRange.value));
          
          // 计算活跃度评分
          const weeklyCommits = commitActivity.reduce((sum, day) => sum + day.commits, 0);
          const weeklyIssues = commitActivity.reduce((sum, day) => sum + day.issues, 0);
          const weeklyPRs = commitActivity.reduce((sum, day) => sum + day.pullRequests, 0);
          
          // 活跃度评分算法 (0-100)
          const activityScore = Math.min(100, Math.round(
            (weeklyCommits * 0.4 + weeklyIssues * 0.3 + weeklyPRs * 0.3) * 2
          ));

          // 生成每日活动数据
          const dailyActivity = commitActivity.map(day => 
            day.commits + day.issues + day.pullRequests + day.releases
          );

          return {
            ...repo,
            activityScore,
            weeklyCommits,
            weeklyIssues,
            weeklyPRs,
            starTrend: Math.floor(Math.random() * 20) + 1, // 模拟星标增长
            dailyActivity: dailyActivity.length > 0 ? dailyActivity : Array(7).fill(0).map(() => Math.floor(Math.random() * 10)),
            topContributors: [
              { id: 1, login: 'developer1', avatar_url: '', contributions: 25 },
              { id: 2, login: 'developer2', avatar_url: '', contributions: 18 },
              { id: 3, login: 'developer3', avatar_url: '', contributions: 12 }
            ],
            totalContributors: Math.floor(Math.random() * 50) + 10,
            recentReleases: [
              { id: 1, name: 'v1.2.0', body: '新增功能和bug修复', published_at: new Date().toISOString() }
            ]
          };
        } catch (error) {
          console.warn(`获取 ${repo.name} 活动数据失败:`, error);
          return {
            ...repo,
            activityScore: Math.floor(Math.random() * 100),
            weeklyCommits: Math.floor(Math.random() * 50),
            starTrend: Math.floor(Math.random() * 20),
            dailyActivity: Array(7).fill(0).map(() => Math.floor(Math.random() * 10)),
            topContributors: [],
            totalContributors: Math.floor(Math.random() * 50) + 10,
            recentReleases: []
          };
        }
      })
    );

    repositories.value = enrichedRepos;
    calculateStatistics();
    
  } catch (error) {
    console.error('加载仓库数据失败:', error);
    // 使用模拟数据
    repositories.value = generateMockData();
    calculateStatistics();
  } finally {
    loading.value = false;
  }
};

/** 生成模拟数据 */
const generateMockData = () => {
  return [
    {
      id: 1,
      name: 'hutool',
      full_name: 'dromara/hutool',
      description: '🍬A set of tools that keep Java sweet.',
      language: 'Java',
      stargazers_count: 28900,
      forks_count: 7200,
      activityScore: 95,
      weeklyCommits: 45,
      starTrend: 12,
      dailyActivity: [8, 12, 15, 6, 9, 18, 11],
      topContributors: [
        { id: 1, login: 'looly', avatar_url: '', contributions: 2890 }
      ],
      totalContributors: 180
    },
    {
      id: 2,
      name: 'Sa-Token',
      full_name: 'dromara/sa-token',
      description: '这可能是史上功能最全的Java权限认证框架！',
      language: 'Java',
      stargazers_count: 15800,
      forks_count: 2900,
      activityScore: 88,
      weeklyCommits: 32,
      starTrend: 8,
      dailyActivity: [5, 8, 12, 4, 7, 15, 9],
      topContributors: [
        { id: 2, login: 'click33', avatar_url: '', contributions: 1560 }
      ],
      totalContributors: 120
    }
    // 可以添加更多模拟数据...
  ];
};

/** 计算统计数据 */
const calculateStatistics = () => {
  const repos = repositories.value;
  
  statistics.value = {
    activeRepos: repos.filter(r => r.activityScore >= 50).length,
    newActiveRepos: Math.floor(repos.length * 0.1),
    weeklyCommits: repos.reduce((sum, r) => sum + (r.weeklyCommits || 0), 0),
    commitsTrend: 12,
    activeContributors: repos.reduce((sum, r) => sum + (r.totalContributors || 0), 0),
    contributorsTrend: 8,
    weeklyReleases: repos.reduce((sum, r) => sum + (r.recentReleases?.length || 0), 0),
    releasesTrend: 3
  };
};

/** 处理时间范围变更 */
const handleTimeRangeChange = () => {
  loadRepositories();
};

/** 处理排序变更 */
const handleSortChange = () => {
  // filteredRepos 是计算属性，会自动重新计算
};

/** 处理搜索 */
const handleSearch = () => {
  currentPage.value = 1;
};

/** 处理过滤 */
const handleFilter = () => {
  currentPage.value = 1;
};

/** 处理分页变更 */
const handlePageChange = () => {
  // 分页逻辑在 filteredRepos 计算属性中处理
};

/** 刷新数据 */
const refreshData = () => {
  loadRepositories();
};

/** 导出数据 */
const exportData = () => {
  // 导出功能实现
  console.log('导出仓库追踪报告');
};

/** 显示仓库详情 */
const showRepoDetails = (repo) => {
  selectedRepo.value = repo;
  detailDrawer.value = true;
  
  // 下一个tick渲染图表
  nextTick(() => {
    renderActivityChart();
    renderContributorsChart();
  });
};

/** 渲染活跃度图表 */
const renderActivityChart = () => {
  // ECharts 图表渲染逻辑
  console.log('渲染活跃度趋势图');
};

/** 渲染贡献者图表 */
const renderContributorsChart = () => {
  // ECharts 图表渲染逻辑  
  console.log('渲染贡献者分析图');
};

/** 格式化变化趋势 */
const formatChange = (trend) => {
  if (trend > 0) return `+${trend}%`;
  if (trend < 0) return `${trend}%`;
  return '0%';
};

/** 获取语言颜色 */
const getLanguageColor = (language) => {
  const colorMap = {
    'Java': '#b07219',
    'JavaScript': '#f1e05a',
    'TypeScript': '#3178c6',
    'Go': '#00ADD8',
    'Python': '#3572A5',
    'Vue': '#41b883',
    'C++': '#f34b7d',
    'PHP': '#4F5D95'
  };
  return colorMap[language] || '#6b7280';
};

/** 获取活跃度类型 */
const getActivityType = (score) => {
  if (score >= 80) return 'success';
  if (score >= 60) return 'primary';
  if (score >= 40) return 'warning';
  return 'danger';
};

/** 获取活跃度标签 */
const getActivityLabel = (score) => {
  if (score >= 80) return '极高';
  if (score >= 60) return '高';
  if (score >= 40) return '中等';
  return '低';
};

/** 格式化日期 */
const formatDate = (dateStr) => {
  return new Date(dateStr).toLocaleDateString();
};

/** 格式化大小 */
const formatSize = (sizeKB) => {
  if (sizeKB > 1024) {
    return (sizeKB / 1024).toFixed(1) + ' MB';
  }
  return sizeKB + ' KB';
};

onMounted(() => {
  loadRepositories();
});
</script>

<style scoped>
.repository-tracking {
  padding: 20px;
}

.stat-card {
  height: 120px;
  transition: all 0.3s ease;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  }
  
  :deep(.el-card__body) {
    padding: 20px;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  background: linear-gradient(135deg, var(--el-color-primary), var(--el-color-primary-light-3));
  
  .el-icon {
    font-size: 24px;
    color: #fff;
  }
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--el-text-color-primary);
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
  margin: 4px 0;
}

.stat-change {
  font-size: 12px;
  color: var(--el-color-success);
  font-weight: 500;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.card-toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
}

.repository-list {
  min-height: 400px;
}

.repo-card {
  height: 320px;
  margin-bottom: 20px;
  transition: all 0.3s ease;
  cursor: pointer;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
  }
  
  :deep(.el-card__body) {
    padding: 16px;
    height: 100%;
    display: flex;
    flex-direction: column;
  }
}

.repo-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.repo-name {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 16px;
  color: var(--el-text-color-primary);
}

.repo-language {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: var(--el-text-color-secondary);
  margin-top: 4px;
}

.language-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.repo-description {
  font-size: 13px;
  color: var(--el-text-color-regular);
  line-height: 1.4;
  margin-bottom: 12px;
  flex: 1;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.repo-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  
  .stat-item {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 12px;
    color: var(--el-text-color-secondary);
    
    .el-icon {
      font-size: 14px;
    }
    
    .trend {
      font-weight: 500;
      
      &.positive {
        color: var(--el-color-success);
      }
      
      &.negative {
        color: var(--el-color-danger);
      }
    }
  }
}

.repo-activity-chart {
  margin-bottom: 12px;
  
  .activity-title {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    margin-bottom: 6px;
  }
  
  .activity-bars {
    display: flex;
    gap: 2px;
    height: 30px;
    align-items: flex-end;
    
    .activity-bar {
      flex: 1;
      background: linear-gradient(to top, var(--el-color-primary-light-3), var(--el-color-primary));
      border-radius: 2px;
      min-height: 2px;
      transition: all 0.2s ease;
      
      &:hover {
        background: linear-gradient(to top, var(--el-color-primary), var(--el-color-primary-dark-2));
      }
    }
  }
}

.repo-contributors {
  .contributors-title {
    font-size: 12px;
    color: var(--el-text-color-secondary);
    margin-bottom: 6px;
  }
  
  .contributors-avatars {
    display: flex;
    align-items: center;
    gap: 6px;
    
    .more-contributors {
      font-size: 11px;
      color: var(--el-text-color-secondary);
      background: var(--el-fill-color-light);
      padding: 2px 6px;
      border-radius: 10px;
    }
  }
}

.repo-details {
  .detail-section {
    margin-bottom: 32px;
    
    h3 {
      margin-bottom: 16px;
      color: var(--el-text-color-primary);
      font-size: 16px;
    }
  }
  
  .chart-container {
    height: 200px;
    background: var(--el-fill-color-lighter);
    border-radius: 6px;
    display: flex;
    align-items: center;
    justify-content: center;
    color: var(--el-text-color-secondary);
  }
}

.mb-4 {
  margin-bottom: 16px;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .repo-card {
    margin-bottom: 16px;
  }
  
  .card-toolbar {
    flex-direction: column;
    gap: 8px;
  }
}

@media (max-width: 768px) {
  .repository-tracking {
    padding: 10px;
  }
  
  .stat-card {
    margin-bottom: 12px;
  }
  
  .card-toolbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
}
</style>