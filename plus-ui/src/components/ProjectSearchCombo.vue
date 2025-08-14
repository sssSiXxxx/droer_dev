<template>
  <div class="project-search-combo">
    <div class="search-container">
      <!-- 主搜索框 -->
      <div class="main-search">
        <el-input
          v-model="searchQuery"
          placeholder="搜索项目名称或描述..."
          clearable
          size="large"
          @input="handleSearchInput"
          @clear="handleClear"
          @keyup.enter="handleSearch"
          class="search-input"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
          <template #suffix>
            <el-button
              type="primary"
              :loading="searching"
              @click="handleSearch"
              size="small"
            >
              搜索
            </el-button>
          </template>
        </el-input>

        <!-- 搜索建议下拉 -->
        <div v-if="showSuggestions && suggestions.length > 0" class="suggestions-dropdown">
          <div class="suggestions-header">
            <span>搜索建议</span>
            <el-button type="text" size="small" @click="closeSuggestions">
              <el-icon><Close /></el-icon>
            </el-button>
          </div>
          <div class="suggestions-list">
            <div
              v-for="suggestion in suggestions"
              :key="suggestion"
              class="suggestion-item"
              @click="selectSuggestion(suggestion)"
            >
              <el-icon><Search /></el-icon>
              <span>{{ suggestion }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 快速筛选选择器 -->
      <div class="quick-filters">
        <el-select
          v-model="selectedLanguage"
          placeholder="编程语言"
          clearable
          size="large"
          @change="handleLanguageChange"
          style="width: 140px"
        >
          <el-option
            v-for="lang in availableLanguages"
            :key="lang"
            :label="lang"
            :value="lang"
          >
            <div class="language-option">
              <div 
                class="language-dot" 
                :style="{ backgroundColor: getLanguageColor(lang) }"
              ></div>
              <span>{{ lang }}</span>
            </div>
          </el-option>
        </el-select>

        <el-select
          v-model="selectedSort"
          placeholder="排序"
          size="large"
          @change="handleSortChange"
          style="width: 120px"
        >
          <el-option label="Stars ↓" value="stars-desc" />
          <el-option label="Stars ↑" value="stars-asc" />
          <el-option label="Forks ↓" value="forks-desc" />
          <el-option label="最新" value="updated-desc" />
          <el-option label="名称" value="name-asc" />
        </el-select>

        <el-select
          v-model="selectedStarRange"
          placeholder="Star范围"
          clearable
          size="large"
          @change="handleStarRangeChange"
          style="width: 140px"
        >
          <el-option label="1000+ Stars" value="1000+" />
          <el-option label="500+ Stars" value="500+" />
          <el-option label="100+ Stars" value="100+" />
          <el-option label="10+ Stars" value="10+" />
          <el-option label="< 10 Stars" value="0-9" />
        </el-select>
      </div>
    </div>

    <!-- 热门推荐标签 -->
    <div class="hot-tags" v-if="!searchQuery && hotTags.length > 0">
      <span class="tags-label">热门搜索:</span>
      <el-tag
        v-for="tag in hotTags"
        :key="tag"
        type="info"
        effect="plain"
        size="small"
        @click="quickSearch(tag)"
        class="hot-tag"
      >
        {{ tag }}
      </el-tag>
    </div>

    <!-- 搜索结果统计 -->
    <div v-if="searchResults.length > 0 || searching" class="search-stats">
      <div class="stats-info">
        <span v-if="!searching">
          找到 <strong>{{ searchResults.length }}</strong> 个项目
        </span>
        <span v-else>搜索中...</span>
      </div>
      <div class="view-options">
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button value="list">列表</el-radio-button>
          <el-radio-button value="grid">网格</el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 搜索结果展示 -->
    <div v-if="searchResults.length > 0" class="search-results" :class="viewMode">
      <transition-group name="fade" tag="div" class="results-container">
        <div
          v-for="project in displayResults"
          :key="project.id"
          class="result-item"
          @click="openProject(project)"
        >
          <div class="project-info">
            <div class="project-header">
              <h4 class="project-name" v-html="highlightText(project.name)"></h4>
              <div class="project-stats">
                <span class="stat-item">
                  <el-icon><Star /></el-icon>
                  {{ formatNumber(project.stargazers_count) }}
                </span>
                <span class="stat-item">
                  <el-icon><Share /></el-icon>
                  {{ formatNumber(project.forks_count) }}
                </span>
              </div>
            </div>
            <p class="project-desc" v-html="highlightText(project.description)"></p>
            <div class="project-meta">
              <span v-if="project.language" class="language">
                <div 
                  class="language-dot" 
                  :style="{ backgroundColor: getLanguageColor(project.language) }"
                ></div>
                {{ project.language }}
              </span>
              <span class="update-time">
                更新于 {{ formatTime(project.updated_at) }}
              </span>
            </div>
          </div>
        </div>
      </transition-group>

      <!-- 加载更多 -->
      <div v-if="hasMore" class="load-more">
        <el-button @click="loadMore" :loading="loadingMore">
          加载更多
        </el-button>
      </div>
    </div>

    <!-- 无结果状态 -->
    <div v-if="!searching && searchQuery && searchResults.length === 0" class="no-results">
      <el-empty description="没有找到相关项目">
        <el-button type="primary" @click="clearSearch">清除搜索条件</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue';
import { Search, Close, Star, Share } from '@element-plus/icons-vue';
import { 
  searchProjects, 
  getProjectSuggestions, 
  advancedSearchProjects,
  type ProjectInfo 
} from '@/api/community-enhanced';
import { debounce } from 'lodash-es';

// Props
interface Props {
  placeholder?: string;
  maxResults?: number;
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '搜索项目...',
  maxResults: 20
});

// Emits
const emit = defineEmits<{
  search: [results: ProjectInfo[], query: string];
  select: [project: ProjectInfo];
}>();

// 响应式数据
const searchQuery = ref('');
const searching = ref(false);
const suggestions = ref<string[]>([]);
const showSuggestions = ref(false);
const searchResults = ref<ProjectInfo[]>([]);
const displayResults = ref<ProjectInfo[]>([]);

// 筛选条件
const selectedLanguage = ref('');
const selectedSort = ref('stars-desc');
const selectedStarRange = ref('');
const viewMode = ref<'list' | 'grid'>('list');

// 分页
const pageSize = 10;
const currentPage = ref(1);
const loadingMore = ref(false);

// 热门标签
const hotTags = ref(['hutool', 'Sa-Token', 'forest', 'MaxKey', 'LiteFlow', 'TLog', 'Dynamic-Tp']);

// 可用编程语言
const availableLanguages = ref(['Java', 'JavaScript', 'TypeScript', 'Python', 'Go', 'Vue', 'C++', 'C#', 'PHP']);

// 计算属性
const hasMore = computed(() => 
  searchResults.value.length > displayResults.value.length
);

// 防抖搜索输入
const debouncedSearch = debounce(async (query: string) => {
  if (query.trim().length > 0) {
    await getSuggestions(query);
    showSuggestions.value = true;
  } else {
    suggestions.value = [];
    showSuggestions.value = false;
  }
}, 300);

// 监听搜索输入
const handleSearchInput = (value: string) => {
  if (value.trim()) {
    debouncedSearch(value);
  } else {
    closeSuggestions();
  }
};

// 获取搜索建议
const getSuggestions = async (query: string) => {
  try {
    const results = await getProjectSuggestions(query, 8);
    suggestions.value = results;
  } catch (error) {
    console.error('获取搜索建议失败:', error);
  }
};

// 选择建议
const selectSuggestion = (suggestion: string) => {
  searchQuery.value = suggestion;
  closeSuggestions();
  handleSearch();
};

// 关闭建议
const closeSuggestions = () => {
  showSuggestions.value = false;
  suggestions.value = [];
};

// 执行搜索
const handleSearch = async () => {
  if (!searchQuery.value.trim() && !selectedLanguage.value && !selectedStarRange.value) {
    return;
  }

  searching.value = true;
  closeSuggestions();
  
  try {
    let results: ProjectInfo[] = [];
    
    // 构建高级搜索选项
    const searchOptions = {
      query: searchQuery.value.trim(),
      language: selectedLanguage.value || undefined,
      limit: props.maxResults
    };
    
    // 处理Star范围
    if (selectedStarRange.value) {
      if (selectedStarRange.value === '1000+') {
        searchOptions.minStars = 1000;
      } else if (selectedStarRange.value === '500+') {
        searchOptions.minStars = 500;
      } else if (selectedStarRange.value === '100+') {
        searchOptions.minStars = 100;
      } else if (selectedStarRange.value === '10+') {
        searchOptions.minStars = 10;
      } else if (selectedStarRange.value === '0-9') {
        searchOptions.maxStars = 9;
      }
    }
    
    // 处理排序
    const [sortField, sortOrder] = selectedSort.value.split('-');
    searchOptions.sortBy = sortField as any;
    searchOptions.sortOrder = sortOrder as any;
    
    // 执行高级搜索
    results = await advancedSearchProjects(searchOptions);
    
    searchResults.value = results;
    currentPage.value = 1;
    updateDisplayResults();
    
    emit('search', results, searchQuery.value);
    
    console.log(`✅ 搜索完成: "${searchQuery.value}", 找到 ${results.length} 个结果`);
  } catch (error) {
    console.error('搜索失败:', error);
    searchResults.value = [];
  } finally {
    searching.value = false;
  }
};

// 更新显示结果
const updateDisplayResults = () => {
  const endIndex = currentPage.value * pageSize;
  displayResults.value = searchResults.value.slice(0, endIndex);
};

// 加载更多
const loadMore = () => {
  if (hasMore.value && !loadingMore.value) {
    loadingMore.value = true;
    currentPage.value++;
    updateDisplayResults();
    loadingMore.value = false;
  }
};

// 筛选条件变化处理
const handleLanguageChange = () => {
  if (searchQuery.value || selectedLanguage.value) {
    handleSearch();
  }
};

const handleSortChange = () => {
  if (searchResults.value.length > 0) {
    handleSearch();
  }
};

const handleStarRangeChange = () => {
  if (searchQuery.value || selectedStarRange.value) {
    handleSearch();
  }
};

// 快速搜索
const quickSearch = (tag: string) => {
  searchQuery.value = tag;
  handleSearch();
};

// 清除搜索
const clearSearch = () => {
  searchQuery.value = '';
  selectedLanguage.value = '';
  selectedStarRange.value = '';
  searchResults.value = [];
  displayResults.value = [];
  currentPage.value = 1;
};

// 清除处理
const handleClear = () => {
  clearSearch();
  closeSuggestions();
};

// 打开项目
const openProject = (project: ProjectInfo) => {
  emit('select', project);
  window.open(project.html_url, '_blank');
};

// 工具函数
const formatNumber = (num: number): string => {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k';
  }
  return num.toString();
};

const formatTime = (dateStr: string): string => {
  const date = new Date(dateStr);
  const now = new Date();
  const diffDays = Math.floor((now.getTime() - date.getTime()) / (24 * 60 * 60 * 1000));
  
  if (diffDays === 0) return '今天';
  if (diffDays === 1) return '昨天';
  if (diffDays < 7) return `${diffDays}天前`;
  if (diffDays < 30) return `${Math.floor(diffDays / 7)}周前`;
  return `${Math.floor(diffDays / 30)}个月前`;
};

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
  };
  return colors[language] || '#666';
};

const highlightText = (text: string): string => {
  if (!searchQuery.value.trim()) return text;
  
  const query = searchQuery.value.trim();
  const regex = new RegExp(`(${query})`, 'gi');
  return text.replace(regex, '<mark class="highlight">$1</mark>');
};

// 监听搜索查询变化
watch(searchQuery, (newValue) => {
  if (!newValue.trim()) {
    clearSearch();
  }
});
</script>

<style scoped>
.project-search-combo {
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
}

.search-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
  margin-bottom: 20px;
}

.main-search {
  position: relative;
}

.search-input :deep(.el-input__inner) {
  padding-right: 100px;
}

.suggestions-dropdown {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
  border: 1px solid #e2e8f0;
  z-index: 1000;
  max-height: 300px;
  overflow-y: auto;
}

.suggestions-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  border-bottom: 1px solid #f1f5f9;
  font-size: 14px;
  font-weight: 600;
  color: #6b7280;
}

.suggestions-list {
  padding: 8px 0;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 16px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.suggestion-item:hover {
  background: #f9fafb;
}

.quick-filters {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
}

.language-option {
  display: flex;
  align-items: center;
  gap: 8px;
}

.language-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.hot-tags {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  margin-bottom: 20px;
}

.tags-label {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.hot-tag {
  cursor: pointer;
  transition: all 0.2s ease;
}

.hot-tag:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.search-stats {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px solid #e2e8f0;
  margin-bottom: 20px;
}

.stats-info {
  font-size: 14px;
  color: #6b7280;
}

.search-results.grid .results-container {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.search-results.list .results-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.result-item {
  background: white;
  border-radius: 12px;
  padding: 20px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.result-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  border-color: #3b82f6;
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.project-name {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.project-stats {
  display: flex;
  gap: 16px;
  align-items: center;
  flex-shrink: 0;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 14px;
  color: #6b7280;
}

.project-desc {
  margin: 0 0 16px 0;
  font-size: 14px;
  color: #6b7280;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.project-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  color: #9ca3af;
}

.language {
  display: flex;
  align-items: center;
  gap: 6px;
}

.load-more {
  text-align: center;
  margin-top: 24px;
}

.no-results {
  text-align: center;
  padding: 40px 0;
}

:deep(.highlight) {
  background: #fef3c7;
  padding: 2px 4px;
  border-radius: 4px;
  font-weight: 600;
}

.fade-enter-active,
.fade-leave-active {
  transition: all 0.3s ease;
}

.fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-container {
    gap: 12px;
  }
  
  .quick-filters {
    justify-content: center;
  }
  
  .search-stats {
    flex-direction: column;
    gap: 12px;
    align-items: flex-start;
  }
  
  .search-results.grid .results-container {
    grid-template-columns: 1fr;
  }
  
  .project-header {
    flex-direction: column;
    gap: 8px;
    align-items: flex-start;
  }
  
  .project-stats {
    align-self: flex-end;
  }
}
</style>