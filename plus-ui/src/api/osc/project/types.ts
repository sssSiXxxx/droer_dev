export interface ProjectVO {
  projectId?: number | string;
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
  userId?: number | string;
  contactInfo?: string;
  versionInfo?: string;
  starCount?: number;
  forkCount?: number;
  watchCount?: number;
  issuesCount?: number;
  prCount?: number;
  readmeUrl?: string;
  wikiUrl?: string;
  apiDocUrl?: string;
  lastCommitTime?: string;
  lastSyncTime?: string;
  createBy?: number | string;
  createTime?: string;
  updateBy?: number | string;
  updateTime?: string;
  // 孵化申请字段
  applicationType?: string;
  applicationStatus?: string;
  license?: string;
  applicationReason?: string;
  contribution?: string;
  currentStatus?: string;
  upgradeReason?: string;
  communityImpact?: string;
  contactEmail?: string;
  contactPhone?: string;
  remarks?: string;
}

export interface ProjectForm {
  projectId?: number | string;
  projectName?: string;
  projectCode?: string;
  description?: string;
  repositoryUrl?: string;
  websiteUrl?: string;
  logoUrl?: string;
  status?: string;
  remark?: string;
  userId?: number | string;
  starCount?: number;
  forkCount?: number;
  issuesCount?: number;
  prCount?: number;
  readmeUrl?: string;
  wikiUrl?: string;
  apiDocUrl?: string;
  lastCommitTime?: string;
  lastSyncTime?: string;
  applicationType?: string;
  applicationStatus?: string;
  license?: string;
  applicationReason?: string;
  contribution?: string;
  currentStatus?: string;
  upgradeReason?: string;
  communityImpact?: string;
  contactEmail?: string;
  contactPhone?: string;
  remarks?: string;
}

export interface ProjectQuery {
  pageNum?: number;
  pageSize?: number;
  projectName?: string;
  projectCode?: string;
  status?: string;
  description?: string;
  userId?: number;
  createBy?: number | string;
  orderByColumn?: string;
  isAsc?: string;
  params?: any;
  // 孵化申请查询字段
  applicationType?: string;
  applicationStatus?: string;
}
