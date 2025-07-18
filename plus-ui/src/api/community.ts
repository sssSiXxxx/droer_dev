import request from '@/utils/request'

const GITEE_TOKEN = '53434be0c12a8cf0120d4750d252c1ab'; // Gitee私人令牌

// 项目信息接口
export interface ProjectInfo {
  id: number
  name: string
  full_name: string
  description: string
  html_url: string
  stargazers_count: number
  forks_count: number
  language: string
  updated_at: string
  avatar_url: string
}

// 贡献者信息接口
export interface ContributorInfo {
  id: number
  login: string
  name: string
  avatar_url: string
  html_url: string
  contributions: number
  type: string
}

// 模拟数据 - 当 API 调用失败时使用
const mockProjects: ProjectInfo[] = [
  {
    id: 1,
    name: 'hutool',
    full_name: 'dromara/hutool',
    description: '🍬A set of tools that keep Java sweet.',
    html_url: 'https://gitee.com/dromara/hutool',
    stargazers_count: 28500,
    forks_count: 7200,
    language: 'Java',
    updated_at: '2024-01-15T10:30:00Z',
    avatar_url: 'https://gitee.com/dromara/hutool/avatar'
  },
  {
    id: 2,
    name: 'Sa-Token',
    full_name: 'dromara/Sa-Token',
    description: '这可能是史上功能最全的Java权限认证框架！',
    html_url: 'https://gitee.com/dromara/Sa-Token',
    stargazers_count: 15800,
    forks_count: 2900,
    language: 'Java',
    updated_at: '2024-01-14T15:20:00Z',
    avatar_url: 'https://gitee.com/dromara/Sa-Token/avatar'
  },
  {
    id: 3,
    name: 'forest',
    full_name: 'dromara/forest',
    description: '🌲轻量级HTTP客户端框架Forest',
    html_url: 'https://gitee.com/dromara/forest',
    stargazers_count: 5200,
    forks_count: 1100,
    language: 'Java',
    updated_at: '2024-01-13T09:15:00Z',
    avatar_url: 'https://gitee.com/dromara/forest/avatar'
  },
  {
    id: 4,
    name: 'MaxKey',
    full_name: 'dromara/MaxKey',
    description: '🔐业界领先的身份管理和访问管理产品',
    html_url: 'https://gitee.com/dromara/MaxKey',
    stargazers_count: 3800,
    forks_count: 950,
    language: 'Java',
    updated_at: '2024-01-12T14:45:00Z',
    avatar_url: 'https://gitee.com/dromara/MaxKey/avatar'
  },
  {
    id: 5,
    name: 'easy-es',
    full_name: 'dromara/easy-es',
    description: '🔗傻瓜级ElasticSearch搜索引擎ORM框架',
    html_url: 'https://gitee.com/dromara/easy-es',
    stargazers_count: 2600,
    forks_count: 520,
    language: 'Java',
    updated_at: '2024-01-11T11:30:00Z',
    avatar_url: 'https://gitee.com/dromara/easy-es/avatar'
  }
]

const mockContributors: ContributorInfo[] = [
  {
    id: 1,
    login: 'looly',
    name: 'looly',
    avatar_url: 'https://gitee.com/assets/no_portrait-2b772d6b.png',
    html_url: 'https://gitee.com/looly',
    contributions: 1256,
    type: 'User'
  },
  {
    id: 2,
    login: 'click33',
    name: 'kong',
    avatar_url: 'https://gitee.com/assets/no_portrait-2b772d6b.png',
    html_url: 'https://gitee.com/click33',
    contributions: 892,
    type: 'User'
  },
  {
    id: 3,
    login: 'shimingxy',
    name: 'shimingxy',
    avatar_url: 'https://gitee.com/assets/no_portrait-2b772d6b.png',
    html_url: 'https://gitee.com/shimingxy',
    contributions: 673,
    type: 'User'
  },
  {
    id: 4,
    login: 'maxkey',
    name: 'maxkey',
    avatar_url: 'https://gitee.com/assets/no_portrait-2b772d6b.png',
    html_url: 'https://gitee.com/maxkey',
    contributions: 445,
    type: 'User'
  },
  {
    id: 5,
    login: 'xpc1024',
    name: 'xpc1024',
    avatar_url: 'https://gitee.com/assets/no_portrait-2b772d6b.png',
    html_url: 'https://gitee.com/xpc1024',
    contributions: 367,
    type: 'User'
  }
]

// 分页拉取所有项目
export const getAllProjects = async (): Promise<ProjectInfo[]> => {
  const all: ProjectInfo[] = [];
  let page = 1;
  const per_page = 100;
  while (true) {
    const res = await request.get('/gitee-api/orgs/dromara/repos', {
      params: {
        page,
        per_page,
        sort: 'stars',
        order: 'desc',
        type: 'all',
        access_token: GITEE_TOKEN
      }
    });
    const list: ProjectInfo[] = res.data || [];
    all.push(...list);
    if (list.length < per_page) break;
    page++;
  }
  return all;
};

// 拉取所有项目的所有贡献者（去重）
export const getAllContributors = async (projects: ProjectInfo[]): Promise<ContributorInfo[]> => {
  const contributorMap = new Map<string, ContributorInfo>();
  for (const project of projects) {
    try {
      const res = await request.get(`/gitee-api/repos/dromara/${project.name}/contributors`, {
        params: {
          per_page: 100,
          access_token: GITEE_TOKEN
        }
      });
      const contributors: ContributorInfo[] = res.data || [];
      contributors.forEach(c => {
        if (!contributorMap.has(c.login)) {
          contributorMap.set(c.login, c);
        }
      });
    } catch (e) {
      // 忽略单个项目失败
    }
  }
  return Array.from(contributorMap.values());
};

// 获取社区统计数据（项目总数、总star数、总贡献者数）
export const getCommunityStats = async () => {
  try {
    const projects = await getAllProjects();
    const totalProjects = projects.length;
    const totalStars = projects.reduce((sum, p) => sum + (p.stargazers_count || 0), 0);
    const contributors = await getAllContributors(projects);
    const totalContributors = contributors.length;
    return { totalProjects, totalStars, totalContributors };
  } catch (e) {
    // 失败时返回0
    return { totalProjects: 0, totalStars: 0, totalContributors: 0 };
  }
};

// 获取 Dromara 组织下的热门项目
export const getHotProjects = async (page: number = 1, per_page: number = 20): Promise<ProjectInfo[]> => {
  try {
    // 走Vite代理+token
    const response = await request.get('/gitee-api/orgs/dromara/repos', {
      params: {
        page,
        per_page,
        sort: 'stars',
        order: 'desc',
        type: 'all',
        access_token: GITEE_TOKEN
      }
    })
    return response.data
  } catch (error) {
    console.warn('Gitee API 调用失败，使用模拟数据:', error)
    return mockProjects.slice(0, per_page)
  }
}

// 获取项目贡献者
export const getProjectContributors = async (owner: string, repo: string): Promise<ContributorInfo[]> => {
  try {
    const response = await request.get(`/gitee-api/repos/${owner}/${repo}/contributors`, {
      params: {
        per_page: 100,
        access_token: GITEE_TOKEN
      }
    })
    return response.data
  } catch (error) {
    console.warn(`获取项目 ${repo} 贡献者失败，使用模拟数据:`, error)
    return []
  }
}

// 获取本周贡献榜（汇总多个项目的贡献者）
export const getWeeklyContributors = async (): Promise<ContributorInfo[]> => {
  try {
    const projects = await getHotProjects(1, 5)
    if (projects.length > 0 && projects[0].id !== mockProjects[0].id) {
      const contributorMap = new Map<string, ContributorInfo>()
      for (const project of projects) {
        const contributors = await getProjectContributors('dromara', project.name)
        contributors.forEach(contributor => {
          if (contributorMap.has(contributor.login)) {
            const existing = contributorMap.get(contributor.login)!
            existing.contributions += contributor.contributions
          } else {
            contributorMap.set(contributor.login, { ...contributor })
          }
        })
      }
      const contributors = Array.from(contributorMap.values())
        .sort((a, b) => b.contributions - a.contributions)
        .slice(0, 10)
      return contributors
    }
    return mockContributors
  } catch (error) {
    console.warn('获取本周贡献榜失败，使用模拟数据:', error)
    return mockContributors
  }
}

// 获取组织信息
export const getOrganizationInfo = async () => {
  try {
    const response = await request.get('/gitee-api/orgs/dromara', {
      params: { access_token: GITEE_TOKEN }
    })
    return response.data
  } catch (error) {
    console.warn('获取组织信息失败:', error)
    return {
      name: 'Dromara',
      description: '致力于微服务云原生解决方案的组织',
      public_repos: 50,
      followers: 8000
    }
  }
}