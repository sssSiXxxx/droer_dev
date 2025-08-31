export interface ProjectVO {
  projectId?: number;
  projectName?: string;
  projectCode?: string;
  description?: string;
  repositoryUrl?: string;
  websiteUrl?: string;
  logoUrl?: string;
  logoUrlUrl?: string;
  status?: string;
  remark?: string;
  techStack?: string;
  programmingLanguage?: string;
  coreContributors?: string;
  maintainer?: string;
  contactInfo?: string;
  versionInfo?: string;
  starCount?: number;
  forkCount?: number;
  issuesCount?: number;
  prCount?: number;
  readmeUrl?: string;
  wikiUrl?: string;
  apiDocUrl?: string;
  lastCommitTime?: string;
  createDept?: number;
  createBy?: number;
  createTime?: string;
  updateBy?: number;
  updateTime?: string;
}

export interface ProjectForm {
  projectId?: number;
  projectName?: string;
  projectCode?: string;
  description?: string;
  repositoryUrl?: string;
  websiteUrl?: string;
  logoUrl?: string;
  status?: string;
  remark?: string;
  maintainer?: string;
  userId?: number;
  starCount?: number;
  forkCount?: number;
  issuesCount?: number;
  prCount?: number;
  readmeUrl?: string;
  wikiUrl?: string;
  apiDocUrl?: string;
  lastCommitTime?: string;
}

export interface ProjectQuery {
  pageNum?: number;
  pageSize?: number;
  projectName?: string;
  projectCode?: string;
  status?: string;
  techStack?: string;
  maintainer?: string;
  createBy?: number | string;
  params?: any;
}
