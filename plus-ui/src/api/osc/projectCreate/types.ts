export interface ProjectCreateVO {
  /**
   * 项目ID
   */
  projectId: string | number;

  /**
   * 项目名称
   */
  projectName: string;

  /**
   * 项目描述
   */
  description: string;

  /**
   * 代码仓库URL
   */
  repositoryUrl: string;

  /**
   * 项目网站
   */
  websiteUrl: string;

  /**
   * 项目Logo
   */
  logoUrl: string;

  /**
   * 状态
   */
  status: string;

  /**
   * 备注
   */
  remark: string;

  /**
   * 技术栈
   */
  techStack: string;

  /**
   * 编程语言
   */
  programmingLanguage: string;

  /**
   * 最后提交时间
   */
  lastCommitTime: string;

  /**
   * Star数
   */
  starCount: number;

  /**
   * Fork数
   */
  forkCount: number;

  /**
   * 版本信息
   */
  versionInfo: string;

  /**
   * Issues数
   */
  issuesCount: number;

  /**
   * PR数
   */
  prCount: number;

  /**
   * README链接
   */
  readmeUrl: string;

  /**
   * Wiki链接
   */
  wikiUrl: string;

  /**
   * API文档链接
   */
  apiDocUrl: string;
}

export interface ProjectCreateForm extends BaseEntity {
  /**
   * 项目ID
   */
  projectId?: string | number;

  /**
   * 项目名称
   */
  projectName?: string;

  /**
   * 项目描述
   */
  description?: string;

  /**
   * 代码仓库URL
   */
  repositoryUrl?: string;

  /**
   * 项目网站
   */
  websiteUrl?: string;

  /**
   * 项目Logo
   */
  logoUrl?: string;

  /**
   * 状态
   */
  status?: string;

  /**
   * 备注
   */
  remark?: string;

  /**
   * 技术栈
   */
  techStack?: string;

  /**
   * 编程语言
   */
  programmingLanguage?: string;

  /**
   * 最后提交时间
   */
  lastCommitTime?: string;

  /**
   * Star数
   */
  starCount?: number;

  /**
   * Fork数
   */
  forkCount?: number;

  /**
   * 核心贡献者
   */
  coreContributors?: string;

  /**
   * 联系方式
   */
  contactInfo?: string;

  /**
   * 版本信息
   */
  versionInfo?: string;

  /**
   * Issues数
   */
  issuesCount?: number;

  /**
   * PR数
   */
  prCount?: number;

  /**
   * README链接
   */
  readmeUrl?: string;

  /**
   * Wiki链接
   */
  wikiUrl?: string;

  /**
   * API文档链接
   */
  apiDocUrl?: string;
}

export interface ProjectCreateQuery extends PageQuery {
  /**
   * 项目名称
   */
  projectName?: string;

  /**
   * 项目描述
   */
  description?: string;

  /**
   * 状态
   */
  status?: string;

  /**
   * 技术栈
   */
  techStack?: string;

  /**
   * 编程语言
   */
  programmingLanguage?: string;

  /**
   * 日期范围参数
   */
  params?: any;
}
