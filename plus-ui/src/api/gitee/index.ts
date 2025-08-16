import request from '@/utils/request';

// Gitee API 基础配置
const GITEE_API_BASE = 'https://gitee.com/api/v5';
const GITEE_TOKEN = '53434be0c12a8cf0120d4750d252c1ab'; // Gitee私人令牌

/**
 * 获取Gitee用户信息
 * @param username Gitee用户名
 */
export function getGiteeUserInfo(username: string) {
  return request({
    url: `${GITEE_API_BASE}/users/${username}`,
    method: 'get',
    params: { access_token: GITEE_TOKEN }
  });
}

/**
 * 获取用户的仓库列表
 * @param username Gitee用户名
 * @param params 查询参数
 */
export function getGiteeRepositories(username: string, params?: any) {
  return request({
    url: `${GITEE_API_BASE}/users/${username}/repos`,
    method: 'get',
    params: { ...params, access_token: GITEE_TOKEN }
  });
}

/**
 * 获取用户的贡献记录
 * @param username Gitee用户名
 * @param params 查询参数
 */
export function getGiteeContributions(username: string, params?: any) {
  return request({
    url: `${GITEE_API_BASE}/users/${username}/events`,
    method: 'get',
    params: { ...params, access_token: GITEE_TOKEN }
  });
}

/**
 * 获取仓库的提交记录
 * @param owner 仓库所有者
 * @param repo 仓库名
 * @param params 查询参数
 */
export function getRepositoryCommits(owner: string, repo: string, params?: any) {
  return request({
    url: `${GITEE_API_BASE}/repos/${owner}/${repo}/commits`,
    method: 'get',
    params: { ...params, access_token: GITEE_TOKEN }
  });
}

/**
 * 获取用户的Pull Requests
 * @param username Gitee用户名
 * @param params 查询参数
 */
export function getGiteePullRequests(username: string, params?: any) {
  return request({
    url: `${GITEE_API_BASE}/users/${username}/pull_requests`,
    method: 'get',
    params: { ...params, access_token: GITEE_TOKEN }
  });
}

/**
 * 获取用户的Issues
 * @param username Gitee用户名
 * @param params 查询参数
 */
export function getGiteeIssues(username: string, params?: any) {
  return request({
    url: `${GITEE_API_BASE}/users/${username}/issues`,
    method: 'get',
    params: { ...params, access_token: GITEE_TOKEN }
  });
}

/**
 * 获取用户的统计信息
 * @param username Gitee用户名
 */
export function getGiteeUserStats(username: string) {
  return request({
    url: `${GITEE_API_BASE}/users/${username}/stats`,
    method: 'get',
    params: { access_token: GITEE_TOKEN }
  });
}

/**
 * 同步Gitee用户信息到本地
 * @param data 用户数据
 */
export function syncGiteeUserInfo(data: any) {
  return request({
    url: '/osc/member/syncGiteeInfo',
    method: 'post',
    data
  });
}

/**
 * 获取本地贡献记录
 * @param userId 用户ID
 */
export function getLocalContributions(userId: number) {
  return request({
    url: '/osc/contribution/list',
    method: 'get',
    params: { 
      userId: userId,
      pageNum: 1,
      pageSize: 1000
    }
  });
}

/**
 * 添加贡献记录
 * @param data 贡献数据
 */
export function addContribution(data: any) {
  return request({
    url: '/osc/contribution/add',
    method: 'post',
    data
  });
}

/**
 * 更新贡献记录
 * @param data 贡献数据
 */
export function updateContribution(data: any) {
  return request({
    url: '/osc/contribution/edit',
    method: 'put',
    data
  });
}

/**
 * 删除贡献记录
 * @param contributionId 贡献ID
 */
export function deleteContribution(contributionId: number) {
  return request({
    url: `/osc/contribution/remove/${contributionId}`,
    method: 'delete'
  });
}

/**
 * 获取成员信息
 * @param userId 用户ID
 */
export function getMemberInfo(userId: number) {
  return request({
    url: '/osc/member/byUserId',
    method: 'get',
    params: { userId }
  });
}

/**
 * 创建或更新成员信息
 * @param data 成员数据
 */
export function saveMemberInfo(data: any) {
  return request({
    url: '/osc/member/save',
    method: 'post',
    data
  });
}
