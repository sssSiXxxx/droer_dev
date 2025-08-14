import request from '@/utils/request';
import axios from 'axios';

// Gitee API配置
const GITEE_API_BASE = 'https://gitee.com/api/v5';
const DROMARA_ORG = 'dromara';

// 创建专门用于Gitee API的axios实例
const giteeRequest = axios.create({
  baseURL: GITEE_API_BASE,
  timeout: 15000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 接口定义
export interface ProjectInfo {
  id: number;
  name: string;
  full_name: string;
  description: string;
  html_url: string;
  stargazers_count: number;
  forks_count: number;
  language: string;
  updated_at: string;
  avatar_url: string;
}

export interface ContributorInfo {
  id: number;
  login: string;
  name: string;
  avatar_url: string;
  html_url: string;
  contributions: number;
  type: string;
}

export interface CommunityStats {
  totalProjects: number;
  totalStars: number;
  totalContributors: number;
  totalForks: number;
  activeProjects: number;
  newProjects: number;
  lastUpdated: string;
}

export interface DashboardData {
  stats: CommunityStats;
  hotProjects: ProjectInfo[];
  weeklyContributors: ContributorInfo[];
  trendingData: {
    dates: string[];
    stars: number[];
    projects: number[];
    contributions: number[];
  };
}

// 缓存配置
interface CacheItem {
  data: any;
  timestamp: number;
  ttl: number;
}

const cache = new Map<string, CacheItem>();

// 缓存工具函数
const setCache = (key: string, data: any, ttl: number = 5 * 60 * 1000) => {
  cache.set(key, {
    data,
    timestamp: Date.now(),
    ttl
  });
};

const getCache = (key: string): any => {
  const item = cache.get(key);
  if (!item) return null;
  
  if (Date.now() - item.timestamp > item.ttl) {
    cache.delete(key);
    return null;
  }
  
  return item.data;
};

// Dromara主要项目列表（按热度排序）
const DROMARA_PROJECTS = [
  'hutool', 'Sa-Token', 'forest', 'MaxKey', 'easy-es', 'LiteFlow', 
  'TLog', 'Dynamic-Tp', 'Jpom', 'HertzBeat', 'Soul', 'FastRequest',
  'MendMix', 'Cubic', 'Sms4j', 'Vue-Element-Plus-Admin', 'GoView',
  'Akali', 'auto-table', 'binlog4j'
];

// 获取Gitee组织信息
export const getOrganizationInfo = async () => {
  const cacheKey = 'org-info';
  const cached = getCache(cacheKey);
  if (cached) return cached;

  try {
    console.log('🔍 正在获取Dromara组织信息...');
    const response = await giteeRequest.get(`/orgs/${DROMARA_ORG}`);
    const orgInfo = response.data;
    
    const result = {
      name: orgInfo.name || 'Dromara',
      description: orgInfo.description || '致力于微服务云原生解决方案的组织',
      public_repos: orgInfo.public_repos || 50,
      followers: orgInfo.followers || 8000,
      html_url: orgInfo.html_url,
      avatar_url: orgInfo.avatar_url
    };
    
    setCache(cacheKey, result, 30 * 60 * 1000); // 30分钟缓存
    console.log('✅ 成功获取组织信息');
    return result;
  } catch (error) {
    console.warn('⚠️ 获取组织信息失败，使用默认数据:', error);
    return {
      name: 'Dromara',
      description: '致力于微服务云原生解决方案的组织',
      public_repos: 50,
      followers: 8000
    };
  }
};

// 获取Dromara组织下的所有仓库
export const getOrganizationRepos = async (): Promise<ProjectInfo[]> => {
  const cacheKey = 'org-repos';
  const cached = getCache(cacheKey);
  if (cached) return cached;

  try {
    console.log('🔍 正在获取Dromara组织仓库列表...');
    const response = await giteeRequest.get(`/orgs/${DROMARA_ORG}/repos`, {
      params: {
        type: 'all',
        sort: 'updated',
        per_page: 100
      }
    });
    
    const repos = response.data;
    const projects: ProjectInfo[] = repos.map((repo: any) => ({
      id: repo.id,
      name: repo.name,
      full_name: repo.full_name,
      description: repo.description || '暂无描述',
      html_url: repo.html_url,
      stargazers_count: repo.stargazers_count || 0,
      forks_count: repo.forks_count || 0,
      language: repo.language || 'Unknown',
      updated_at: repo.updated_at,
      avatar_url: repo.owner?.avatar_url || ''
    }));
    
    setCache(cacheKey, projects, 10 * 60 * 1000); // 10分钟缓存
    console.log(`✅ 成功获取 ${projects.length} 个仓库信息`);
    return projects;
  } catch (error) {
    console.error('❌ 获取仓库列表失败:', error);
    return [];
  }
};

// 获取热门项目（按星标数排序）
export const getHotProjects = async (limit: number = 20): Promise<ProjectInfo[]> => {
  try {
    const allRepos = await getOrganizationRepos();
    
    // 按星标数排序并取前N个
    const hotProjects = allRepos
      .filter(repo => repo.stargazers_count > 0)
      .sort((a, b) => b.stargazers_count - a.stargazers_count)
      .slice(0, limit);
    
    console.log(`✅ 获取热门项目 ${hotProjects.length} 个`);
    return hotProjects;
  } catch (error) {
    console.error('❌ 获取热门项目失败:', error);
    return [];
  }
};

// 获取特定项目的贡献者
export const getProjectContributors = async (projectName: string): Promise<ContributorInfo[]> => {
  const cacheKey = `contributors-${projectName}`;
  const cached = getCache(cacheKey);
  if (cached) return cached;

  try {
    const response = await giteeRequest.get(`/repos/${DROMARA_ORG}/${projectName}/contributors`, {
      params: {
        per_page: 20
      }
    });
    
    const contributors: ContributorInfo[] = response.data.map((contributor: any) => ({
      id: contributor.id,
      login: contributor.login,
      name: contributor.name || contributor.login,
      avatar_url: contributor.avatar_url,
      html_url: contributor.html_url,
      contributions: contributor.contributions,
      type: contributor.type
    }));
    
    setCache(cacheKey, contributors, 30 * 60 * 1000); // 30分钟缓存
    return contributors;
  } catch (error) {
    console.warn(`⚠️ 获取项目 ${projectName} 贡献者失败:`, error);
    return [];
  }
};

// 获取周贡献榜（汇总多个项目的贡献者）
export const getWeeklyContributors = async (): Promise<ContributorInfo[]> => {
  const cacheKey = 'weekly-contributors';
  const cached = getCache(cacheKey);
  if (cached) return cached;

  try {
    console.log('🔍 正在获取本周贡献榜...');
    
    // 获取热门项目的贡献者
    const topProjects = DROMARA_PROJECTS.slice(0, 10); // 取前10个热门项目
    const contributorsMap = new Map<string, ContributorInfo>();
    
    // 并发获取多个项目的贡献者
    const contributorPromises = topProjects.map(project => 
      getProjectContributors(project).catch(() => [])
    );
    
    const allContributorsArrays = await Promise.all(contributorPromises);
    
    // 合并贡献者数据
    allContributorsArrays.forEach(contributors => {
      contributors.forEach(contributor => {
        const existingContributor = contributorsMap.get(contributor.login);
        if (existingContributor) {
          existingContributor.contributions += contributor.contributions;
        } else {
          contributorsMap.set(contributor.login, { ...contributor });
        }
      });
    });
    
    // 转换为数组并按贡献排序
    const weeklyContributors = Array.from(contributorsMap.values())
      .sort((a, b) => b.contributions - a.contributions)
      .slice(0, 20);
    
    setCache(cacheKey, weeklyContributors, 15 * 60 * 1000); // 15分钟缓存
    console.log(`✅ 获取本周贡献榜 ${weeklyContributors.length} 个贡献者`);
    return weeklyContributors;
  } catch (error) {
    console.error('❌ 获取本周贡献榜失败:', error);
    return [];
  }
};

// 获取社区统计数据
export const getCommunityStats = async (): Promise<CommunityStats> => {
  const cacheKey = 'community-stats';
  const cached = getCache(cacheKey);
  if (cached) return cached;

  try {
    console.log('🔍 正在计算社区统计数据...');
    
    const [orgInfo, allRepos, weeklyContributors] = await Promise.all([
      getOrganizationInfo(),
      getOrganizationRepos(),
      getWeeklyContributors()
    ]);
    
    // 计算统计数据
    const totalProjects = allRepos.length;
    const totalStars = allRepos.reduce((sum, repo) => sum + repo.stargazers_count, 0);
    const totalForks = allRepos.reduce((sum, repo) => sum + repo.forks_count, 0);
    const totalContributors = weeklyContributors.length;
    
    // 计算活跃项目（最近30天有更新的）
    const thirtyDaysAgo = new Date();
    thirtyDaysAgo.setDate(thirtyDaysAgo.getDate() - 30);
    const activeProjects = allRepos.filter(repo => 
      new Date(repo.updated_at) > thirtyDaysAgo
    ).length;
    
    // 计算新项目（最近90天创建的）
    const ninetyDaysAgo = new Date();
    ninetyDaysAgo.setDate(ninetyDaysAgo.getDate() - 90);
    const newProjects = allRepos.filter(repo => 
      new Date(repo.updated_at) > ninetyDaysAgo
    ).length;
    
    const stats: CommunityStats = {
      totalProjects,
      totalStars,
      totalContributors,
      totalForks,
      activeProjects,
      newProjects,
      lastUpdated: new Date().toISOString()
    };
    
    setCache(cacheKey, stats, 5 * 60 * 1000); // 5分钟缓存
    console.log('✅ 社区统计数据计算完成:', stats);
    return stats;
  } catch (error) {
    console.error('❌ 获取社区统计数据失败:', error);
    return {
      totalProjects: 0,
      totalStars: 0,
      totalContributors: 0,
      totalForks: 0,
      activeProjects: 0,
      newProjects: 0,
      lastUpdated: new Date().toISOString()
    };
  }
};

// 获取趋势数据（模拟最近7天的数据）
export const getTrendingData = async () => {
  const cacheKey = 'trending-data';
  const cached = getCache(cacheKey);
  if (cached) return cached;

  try {
    console.log('🔍 正在生成趋势数据...');
    
    const stats = await getCommunityStats();
    const dates: string[] = [];
    const stars: number[] = [];
    const projects: number[] = [];
    const contributions: number[] = [];
    
    // 生成最近7天的日期
    for (let i = 6; i >= 0; i--) {
      const date = new Date();
      date.setDate(date.getDate() - i);
      dates.push(date.toISOString().split('T')[0]);
      
      // 模拟数据变化趋势
      const baseStars = Math.max(0, stats.totalStars - 500 + i * 50);
      const baseProjects = Math.max(0, stats.totalProjects - 5 + Math.floor(i / 2));
      const baseContributions = Math.max(0, stats.totalContributors - 100 + i * 10);
      
      stars.push(baseStars + Math.floor(Math.random() * 100));
      projects.push(baseProjects + Math.floor(Math.random() * 3));
      contributions.push(baseContributions + Math.floor(Math.random() * 50));
    }
    
    const trendingData = { dates, stars, projects, contributions };
    setCache(cacheKey, trendingData, 60 * 60 * 1000); // 1小时缓存
    console.log('✅ 趋势数据生成完成');
    return trendingData;
  } catch (error) {
    console.error('❌ 生成趋势数据失败:', error);
    return {
      dates: [],
      stars: [],
      projects: [],
      contributions: []
    };
  }
};

// 获取完整的仪表盘数据
export const getDashboardData = async (): Promise<DashboardData> => {
  try {
    console.log('🚀 开始获取仪表盘完整数据...');
    
    const [stats, hotProjects, weeklyContributors, trendingData] = await Promise.all([
      getCommunityStats(),
      getHotProjects(20),
      getWeeklyContributors(),
      getTrendingData()
    ]);
    
    const dashboardData: DashboardData = {
      stats,
      hotProjects,
      weeklyContributors,
      trendingData
    };
    
    console.log('🎉 仪表盘数据获取完成!');
    return dashboardData;
  } catch (error) {
    console.error('❌ 获取仪表盘数据失败:', error);
    throw error;
  }
};

// 强制刷新所有缓存
export const refreshAllData = async () => {
  console.log('🔄 正在强制刷新所有数据...');
  cache.clear();
  return await getDashboardData();
};

// 获取数据更新状态
export const getDataUpdateStatus = () => {
  const cacheKeys = ['community-stats', 'org-repos', 'weekly-contributors'];
  const status = cacheKeys.map(key => {
    const item = cache.get(key);
    return {
      key,
      cached: !!item,
      age: item ? Date.now() - item.timestamp : 0,
      ttl: item ? item.ttl : 0
    };
  });
  
  return status;
};

// 搜索项目（支持部分匹配）
export const searchProjects = async (query: string, limit: number = 20): Promise<ProjectInfo[]> => {
  try {
    if (!query.trim()) {
      return await getHotProjects(limit);
    }

    console.log(`🔍 正在搜索项目: "${query}"`);
    
    const allRepos = await getOrganizationRepos();
    const queryLower = query.toLowerCase();
    
    // 搜索匹配项目名称或描述
    const matchedProjects = allRepos.filter(repo => {
      const nameMatch = repo.name.toLowerCase().includes(queryLower);
      const descMatch = repo.description.toLowerCase().includes(queryLower);
      const fullNameMatch = repo.full_name.toLowerCase().includes(queryLower);
      
      return nameMatch || descMatch || fullNameMatch;
    });
    
    // 按相关度排序（名称匹配优先，然后按星标数）
    const sortedResults = matchedProjects.sort((a, b) => {
      const aNameMatch = a.name.toLowerCase().includes(queryLower);
      const bNameMatch = b.name.toLowerCase().includes(queryLower);
      
      if (aNameMatch && !bNameMatch) return -1;
      if (!aNameMatch && bNameMatch) return 1;
      
      // 如果都匹配名称或都不匹配，按星标数排序
      return b.stargazers_count - a.stargazers_count;
    });
    
    const results = sortedResults.slice(0, limit);
    console.log(`✅ 找到 ${results.length} 个匹配项目`);
    return results;
  } catch (error) {
    console.error('❌ 搜索项目失败:', error);
    return [];
  }
};

// 获取项目搜索建议（自动完成）
export const getProjectSuggestions = async (query: string, limit: number = 10): Promise<string[]> => {
  try {
    if (!query.trim()) return [];
    
    const allRepos = await getOrganizationRepos();
    const queryLower = query.toLowerCase();
    
    const suggestions = allRepos
      .filter(repo => repo.name.toLowerCase().includes(queryLower))
      .map(repo => repo.name)
      .slice(0, limit);
    
    return suggestions;
  } catch (error) {
    console.error('❌ 获取搜索建议失败:', error);
    return [];
  }
};

// 高级搜索（支持多个条件）
export const advancedSearchProjects = async (options: {
  query?: string;
  language?: string;
  minStars?: number;
  maxStars?: number;
  sortBy?: 'stars' | 'forks' | 'updated' | 'name';
  sortOrder?: 'asc' | 'desc';
  limit?: number;
}): Promise<ProjectInfo[]> => {
  try {
    console.log('🔍 正在执行高级搜索:', options);
    
    const allRepos = await getOrganizationRepos();
    let filteredRepos = [...allRepos];
    
    // 按查询字符串过滤
    if (options.query?.trim()) {
      const queryLower = options.query.toLowerCase();
      filteredRepos = filteredRepos.filter(repo => 
        repo.name.toLowerCase().includes(queryLower) ||
        repo.description.toLowerCase().includes(queryLower)
      );
    }
    
    // 按编程语言过滤
    if (options.language) {
      filteredRepos = filteredRepos.filter(repo => 
        repo.language?.toLowerCase() === options.language?.toLowerCase()
      );
    }
    
    // 按星标数范围过滤
    if (options.minStars !== undefined) {
      filteredRepos = filteredRepos.filter(repo => repo.stargazers_count >= options.minStars!);
    }
    if (options.maxStars !== undefined) {
      filteredRepos = filteredRepos.filter(repo => repo.stargazers_count <= options.maxStars!);
    }
    
    // 排序
    const sortBy = options.sortBy || 'stars';
    const sortOrder = options.sortOrder || 'desc';
    
    filteredRepos.sort((a, b) => {
      let aValue: any, bValue: any;
      
      switch (sortBy) {
        case 'stars':
          aValue = a.stargazers_count;
          bValue = b.stargazers_count;
          break;
        case 'forks':
          aValue = a.forks_count;
          bValue = b.forks_count;
          break;
        case 'updated':
          aValue = new Date(a.updated_at).getTime();
          bValue = new Date(b.updated_at).getTime();
          break;
        case 'name':
          aValue = a.name.toLowerCase();
          bValue = b.name.toLowerCase();
          break;
        default:
          aValue = a.stargazers_count;
          bValue = b.stargazers_count;
      }
      
      if (sortOrder === 'asc') {
        return aValue > bValue ? 1 : -1;
      } else {
        return aValue < bValue ? 1 : -1;
      }
    });
    
    const results = filteredRepos.slice(0, options.limit || 20);
    console.log(`✅ 高级搜索找到 ${results.length} 个结果`);
    return results;
  } catch (error) {
    console.error('❌ 高级搜索失败:', error);
    return [];
  }
};

export default {
  getOrganizationInfo,
  getOrganizationRepos,
  getHotProjects,
  getWeeklyContributors,
  getCommunityStats,
  getTrendingData,
  getDashboardData,
  refreshAllData,
  getDataUpdateStatus,
  searchProjects,
  getProjectSuggestions,
  advancedSearchProjects
};