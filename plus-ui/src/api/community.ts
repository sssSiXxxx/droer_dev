import request from '@/utils/request';
import axios from 'axios';

const GITEE_TOKEN = '53434be0c12a8cf0120d4750d252c1ab'; // Gitee私人令牌

// 创建专门用于外部API的axios实例
const externalRequest = axios.create({
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json;charset=utf-8'
  }
});

// 项目信息接口
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

// 贡献者信息接口
export interface ContributorInfo {
  id: number;
  login: string;
  name: string;
  avatar_url: string;
  html_url: string;
  contributions: number;
  type: string;
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
  },
  {
    id: 6,
    name: 'Akali',
    full_name: 'dromara/Akali',
    description: '轻量级分布式任务调度/分布式事务',
    html_url: 'https://gitee.com/dromara/Akali',
    stargazers_count: 578,
    forks_count: 151,
    language: 'Java',
    updated_at: '2024-01-10T10:30:00Z',
    avatar_url: 'https://gitee.com/dromara/Akali/avatar'
  },
  {
    id: 7,
    name: 'auto-table',
    full_name: 'dromara/auto-table',
    description: '根据 Java 实体类，自动生成数据库表结构',
    html_url: 'https://gitee.com/dromara/auto-table',
    stargazers_count: 117,
    forks_count: 43,
    language: 'Java',
    updated_at: '2024-01-09T09:15:00Z',
    avatar_url: 'https://gitee.com/dromara/auto-table/avatar'
  },
  {
    id: 8,
    name: 'binlog4j',
    full_name: 'dromara/binlog4j',
    description: '基于 Java 的轻量级 binlog 客户端',
    html_url: 'https://gitee.com/dromara/binlog4j',
    stargazers_count: 527,
    forks_count: 185,
    language: 'Java',
    updated_at: '2024-01-08T08:45:00Z',
    avatar_url: 'https://gitee.com/dromara/binlog4j/avatar'
  },
  {
    id: 9,
    name: 'FastRequest',
    full_name: 'dromara/FastRequest',
    description: 'API调试工具，支持接口调试、Mock数据',
    html_url: 'https://gitee.com/dromara/FastRequest',
    stargazers_count: 450,
    forks_count: 120,
    language: 'Java',
    updated_at: '2024-01-07T07:30:00Z',
    avatar_url: 'https://gitee.com/dromara/FastRequest/avatar'
  },
  {
    id: 10,
    name: 'LiteFlow',
    full_name: 'dromara/LiteFlow',
    description: '轻量级规则引擎，让复杂业务逻辑变得简单',
    html_url: 'https://gitee.com/dromara/LiteFlow',
    stargazers_count: 3200,
    forks_count: 680,
    language: 'Java',
    updated_at: '2024-01-06T06:20:00Z',
    avatar_url: 'https://gitee.com/dromara/LiteFlow/avatar'
  },
  {
    id: 11,
    name: 'TLog',
    full_name: 'dromara/TLog',
    description: '轻量级分布式日志追踪系统',
    html_url: 'https://gitee.com/dromara/TLog',
    stargazers_count: 1800,
    forks_count: 420,
    language: 'Java',
    updated_at: '2024-01-05T05:10:00Z',
    avatar_url: 'https://gitee.com/dromara/TLog/avatar'
  },
  {
    id: 12,
    name: 'Dynamic-Tp',
    full_name: 'dromara/Dynamic-Tp',
    description: '基于配置中心的轻量级动态线程池',
    html_url: 'https://gitee.com/dromara/Dynamic-Tp',
    stargazers_count: 2100,
    forks_count: 380,
    language: 'Java',
    updated_at: '2024-01-04T04:00:00Z',
    avatar_url: 'https://gitee.com/dromara/Dynamic-Tp/avatar'
  },
  {
    id: 13,
    name: 'MendMix',
    full_name: 'dromara/MendMix',
    description: '开源分布式事务解决方案',
    html_url: 'https://gitee.com/dromara/MendMix',
    stargazers_count: 890,
    forks_count: 210,
    language: 'Java',
    updated_at: '2024-01-03T03:30:00Z',
    avatar_url: 'https://gitee.com/dromara/MendMix/avatar'
  },
  {
    id: 14,
    name: 'Cubic',
    full_name: 'dromara/Cubic',
    description: '一站式问题定位平台',
    html_url: 'https://gitee.com/dromara/Cubic',
    stargazers_count: 650,
    forks_count: 150,
    language: 'Java',
    updated_at: '2024-01-02T02:15:00Z',
    avatar_url: 'https://gitee.com/dromara/Cubic/avatar'
  },
  {
    id: 15,
    name: 'Jpom',
    full_name: 'dromara/Jpom',
    description: '简而轻的低侵入式在线构建、自动部署、日常运维、项目监控软件',
    html_url: 'https://gitee.com/dromara/Jpom',
    stargazers_count: 4200,
    forks_count: 850,
    language: 'Java',
    updated_at: '2024-01-01T01:00:00Z',
    avatar_url: 'https://gitee.com/dromara/Jpom/avatar'
  },
  {
    id: 16,
    name: 'Sms4j',
    full_name: 'dromara/Sms4j',
    description: '短信聚合框架，支持多种短信服务商',
    html_url: 'https://gitee.com/dromara/Sms4j',
    stargazers_count: 380,
    forks_count: 95,
    language: 'Java',
    updated_at: '2023-12-31T00:45:00Z',
    avatar_url: 'https://gitee.com/dromara/Sms4j/avatar'
  },
  {
    id: 17,
    name: 'Vue-Element-Plus-Admin',
    full_name: 'dromara/Vue-Element-Plus-Admin',
    description: '基于Vue3、Element Plus的后台管理系统',
    html_url: 'https://gitee.com/dromara/Vue-Element-Plus-Admin',
    stargazers_count: 1200,
    forks_count: 280,
    language: 'Vue',
    updated_at: '2023-12-30T00:30:00Z',
    avatar_url: 'https://gitee.com/dromara/Vue-Element-Plus-Admin/avatar'
  },
  {
    id: 18,
    name: 'GoView',
    full_name: 'dromara/GoView',
    description: '基于Vue3的低代码数据可视化开发平台',
    html_url: 'https://gitee.com/dromara/GoView',
    stargazers_count: 950,
    forks_count: 220,
    language: 'Vue',
    updated_at: '2023-12-29T00:15:00Z',
    avatar_url: 'https://gitee.com/dromara/GoView/avatar'
  },
  {
    id: 19,
    name: 'HertzBeat',
    full_name: 'dromara/HertzBeat',
    description: '易用友好的开源监控告警系统',
    html_url: 'https://gitee.com/dromara/HertzBeat',
    stargazers_count: 2800,
    forks_count: 520,
    language: 'Java',
    updated_at: '2023-12-28T00:00:00Z',
    avatar_url: 'https://gitee.com/dromara/HertzBeat/avatar'
  },
  {
    id: 20,
    name: 'Soul',
    full_name: 'dromara/Soul',
    description: '高性能API网关',
    html_url: 'https://gitee.com/dromara/Soul',
    stargazers_count: 7500,
    forks_count: 1200,
    language: 'Java',
    updated_at: '2023-12-27T23:45:00Z',
    avatar_url: 'https://gitee.com/dromara/Soul/avatar'
  }
];

const mockContributors: ContributorInfo[] = [
  {
    id: 1,
    login: 'looly',
    name: 'looly',
    avatar_url: 'https://foruda.gitee.com/avatar/1676931283921363972/812675_looly_1660296579.png',
    html_url: 'https://gitee.com/looly',
    contributions: 1256,
    type: 'User'
  },
  {
    id: 2,
    login: 'click33',
    name: 'kong',
    avatar_url: 'https://foruda.gitee.com/avatar/1676906219947351575/342237_click33_1629796763.png',
    html_url: 'https://gitee.com/click33',
    contributions: 892,
    type: 'User'
  },
  {
    id: 3,
    login: 'shimingxy',
    name: 'shimingxy',
    avatar_url: 'https://foruda.gitee.com/avatar/1676944767901252402/1102362_shimingxy_1578940308.png',
    html_url: 'https://gitee.com/shimingxy',
    contributions: 673,
    type: 'User'
  },
  {
    id: 4,
    login: 'maxkey',
    name: 'maxkey',
    avatar_url: 'https://foruda.gitee.com/avatar/1677022544584087390/4835367_maxkey_1578975358.png',
    html_url: 'https://gitee.com/maxkey',
    contributions: 445,
    type: 'User'
  },
  {
    id: 5,
    login: 'xpc1024',
    name: 'xpc1024',
    avatar_url: 'https://foruda.gitee.com/avatar/1676931283921363972/812675_xpc1024_1660296579.png',
    html_url: 'https://gitee.com/xpc1024',
    contributions: 367,
    type: 'User'
  },
  {
    id: 6,
    login: 'tangzc',
    name: 'tangzc',
    avatar_url: 'https://foruda.gitee.com/avatar/1676906219947351575/342237_tangzc_1629796763.png',
    html_url: 'https://gitee.com/tangzc',
    contributions: 298,
    type: 'User'
  },
  {
    id: 7,
    login: 'yu199195',
    name: 'yu199195',
    avatar_url: 'https://foruda.gitee.com/avatar/1676944767901252402/1102362_yu199195_1578940308.png',
    html_url: 'https://gitee.com/yu199195',
    contributions: 245,
    type: 'User'
  },
  {
    id: 8,
    login: 'bryan31',
    name: 'bryan31',
    avatar_url: 'https://foruda.gitee.com/avatar/1677022544584087390/4835367_bryan31_1578975358.png',
    html_url: 'https://gitee.com/bryan31',
    contributions: 198,
    type: 'User'
  },
  {
    id: 9,
    login: 'Jmysy',
    name: 'Jmysy',
    avatar_url: 'https://foruda.gitee.com/avatar/1676931283921363972/812675_Jmysy_1660296579.png',
    html_url: 'https://gitee.com/Jmysy',
    contributions: 156,
    type: 'User'
  },
  {
    id: 10,
    login: 'minfc',
    name: 'minfc',
    avatar_url: 'https://foruda.gitee.com/avatar/1676906219947351575/342237_minfc_1629796763.png',
    html_url: 'https://gitee.com/minfc',
    contributions: 134,
    type: 'User'
  },
  {
    id: 11,
    login: 'lizhian',
    name: 'lizhian',
    avatar_url: 'https://foruda.gitee.com/avatar/1676944767901252402/1102362_lizhian_1578940308.png',
    html_url: 'https://gitee.com/lizhian',
    contributions: 112,
    type: 'User'
  },
  {
    id: 12,
    login: 'ideaedi',
    name: 'ideaedi',
    avatar_url: 'https://foruda.gitee.com/avatar/1677022544584087390/4835367_ideaedi_1578975358.png',
    html_url: 'https://gitee.com/ideaedi',
    contributions: 98,
    type: 'User'
  },
  {
    id: 13,
    login: 'dromara-admin',
    name: 'dromara-admin',
    avatar_url: 'https://foruda.gitee.com/avatar/1676931283921363972/812675_dromara-admin_1660296579.png',
    html_url: 'https://gitee.com/dromara-admin',
    contributions: 87,
    type: 'User'
  },
  {
    id: 14,
    login: 'dromara-bot',
    name: 'dromara-bot',
    avatar_url: 'https://foruda.gitee.com/avatar/1676906219947351575/342237_dromara-bot_1629796763.png',
    html_url: 'https://gitee.com/dromara-bot',
    contributions: 76,
    type: 'User'
  },
  {
    id: 15,
    login: 'dromara-helper',
    name: 'dromara-helper',
    avatar_url: 'https://foruda.gitee.com/avatar/1676944767901252402/1102362_dromara-helper_1578940308.png',
    html_url: 'https://gitee.com/dromara-helper',
    contributions: 65,
    type: 'User'
  },
  {
    id: 16,
    login: 'dromara-core',
    name: 'dromara-core',
    avatar_url: 'https://foruda.gitee.com/avatar/1677022544584087390/4835367_dromara-core_1578975358.png',
    html_url: 'https://gitee.com/dromara-core',
    contributions: 54,
    type: 'User'
  },
  {
    id: 17,
    login: 'dromara-dev',
    name: 'dromara-dev',
    avatar_url: 'https://foruda.gitee.com/avatar/1676931283921363972/812675_dromara-dev_1660296579.png',
    html_url: 'https://gitee.com/dromara-dev',
    contributions: 43,
    type: 'User'
  },
  {
    id: 18,
    login: 'dromara-test',
    name: 'dromara-test',
    avatar_url: 'https://foruda.gitee.com/avatar/1676906219947351575/342237_dromara-test_1629796763.png',
    html_url: 'https://gitee.com/dromara-test',
    contributions: 32,
    type: 'User'
  },
  {
    id: 19,
    login: 'dromara-docs',
    name: 'dromara-docs',
    avatar_url: 'https://foruda.gitee.com/avatar/1676944767901252402/1102362_dromara-docs_1578940308.png',
    html_url: 'https://gitee.com/dromara-docs',
    contributions: 21,
    type: 'User'
  },
  {
    id: 20,
    login: 'dromara-community',
    name: 'dromara-community',
    avatar_url: 'https://foruda.gitee.com/avatar/1677022544584087390/4835367_dromara-community_1578975358.png',
    html_url: 'https://gitee.com/dromara-community',
    contributions: 15,
    type: 'User'
  }
];

// 获取 Dromara 组织下的热门项目 - 暂时使用模拟数据
export const getHotProjects = async (page: number = 1, per_page: number = 20): Promise<ProjectInfo[]> => {
  try {
    console.log('获取热门项目列表...');
    console.log('使用模拟数据展示热门项目');

    // 暂时直接返回模拟数据，避免API访问问题
    return mockProjects.slice(0, per_page);
  } catch (error) {
    console.warn('获取热门项目失败，使用模拟数据:', error);
    return mockProjects.slice(0, per_page);
  }
};

// 获取本周贡献榜 - 暂时使用模拟数据
export const getWeeklyContributors = async (): Promise<ContributorInfo[]> => {
  try {
    console.log('获取本周贡献榜数据...');
    console.log('使用模拟数据展示本周贡献榜');

    // 使用模拟数据，但调整贡献值以模拟本周数据
    const weeklyMockData = mockContributors.map((contributor) => ({
      ...contributor,
      // 随机生成1-20之间的贡献值，模拟本周贡献
      contributions: Math.floor(Math.random() * 20) + 1
    }));

    // 重新按贡献排序并返回前20个
    return weeklyMockData.sort((a, b) => b.contributions - a.contributions).slice(0, 20);
  } catch (error) {
    console.warn('获取本周贡献榜失败，使用模拟数据:', error);
    return mockContributors.slice(0, 20);
  }
};

// 获取社区统计数据 - 暂时使用模拟数据
export const getCommunityStats = async () => {
  try {
    // 计算模拟数据的统计信息
    const totalProjects = mockProjects.length;
    const totalStars = mockProjects.reduce((sum, p) => sum + (p.stargazers_count || 0), 0);
    const totalContributors = mockContributors.length;

    return { totalProjects, totalStars, totalContributors };
  } catch (e) {
    // 失败时返回0
    return { totalProjects: 0, totalStars: 0, totalContributors: 0 };
  }
};

// 获取组织信息 - 暂时使用模拟数据
export const getOrganizationInfo = async () => {
  try {
    return {
      name: 'Dromara',
      description: '致力于微服务云原生解决方案的组织',
      public_repos: 50,
      followers: 8000
    };
  } catch (error) {
    console.warn('获取组织信息失败:', error);
    return {
      name: 'Dromara',
      description: '致力于微服务云原生解决方案的组织',
      public_repos: 50,
      followers: 8000
    };
  }
};
