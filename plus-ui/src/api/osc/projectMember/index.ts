import request from '@/utils/request';

export interface ProjectMemberVO {
  id?: number;
  projectId?: number;
  projectName?: string;
  projectCode?: string;
  memberId?: number;
  memberName?: string;
  memberEmail?: string;
  role?: string;
  roleName?: string;
  permissionLevel?: number;
  isActive?: string;
  contributionScore?: number;
  joinTime?: string;
  remark?: string;
  createTime?: string;
  updateTime?: string;
}

export interface ProjectMemberForm {
  id?: number;
  projectId?: number;
  memberId?: number;
  role?: string;
  permissionLevel?: number;
  isActive?: string;
  contributionScore?: number;
  joinTime?: string;
  remark?: string;
}

export interface ProjectMemberQuery extends PageQuery {
  projectId?: number;
  memberId?: number;
  role?: string;
  isActive?: string;
  projectName?: string;
  memberName?: string;
  beginJoinTime?: string;
  endJoinTime?: string;
}

// 查询项目成员关联列表
export const listProjectMember = (params: ProjectMemberQuery) => {
  return request({
    url: '/osc/projectMember/list',
    method: 'get',
    params
  });
};

// 查询项目成员关联详细
export const getProjectMember = (id: number | string) => {
  return request({
    url: `/osc/projectMember/${id}`,
    method: 'get'
  });
};

// 新增项目成员关联
export const addProjectMember = (data: ProjectMemberForm) => {
  return request({
    url: '/osc/projectMember',
    method: 'post',
    data
  });
};

// 修改项目成员关联
export const updateProjectMember = (data: ProjectMemberForm) => {
  return request({
    url: '/osc/projectMember',
    method: 'put',
    data
  });
};

// 删除项目成员关联
export const delProjectMember = (id: number | string | Array<number | string>) => {
  return request({
    url: `/osc/projectMember/${id}`,
    method: 'delete'
  });
};

// 查询项目成员列表
export const getProjectMembers = (projectId: number) => {
  return request({
    url: `/osc/projectMember/project/${projectId}`,
    method: 'get'
  });
};

// 查询成员参与的项目列表
export const getMemberProjects = (memberId: number) => {
  return request({
    url: `/osc/projectMember/member/${memberId}`,
    method: 'get'
  });
};

// 批量添加项目成员
export const batchAddMembers = (projectId: number, memberIds: number[], role: string) => {
  return request({
    url: '/osc/projectMember/batch/add',
    method: 'post',
    data: { projectId, memberIds, role }
  });
};

// 移除项目成员
export const removeMember = (projectId: number, memberId: number) => {
  return request({
    url: '/osc/projectMember/remove',
    method: 'delete',
    params: { projectId, memberId }
  });
};

// 更新成员角色
export const updateMemberRole = (projectId: number, memberId: number, role: string) => {
  return request({
    url: '/osc/projectMember/role',
    method: 'put',
    data: { projectId, memberId, role }
  });
};

// 更新成员活跃状态
export const updateMemberActiveStatus = (projectId: number, memberId: number, isActive: string) => {
  return request({
    url: '/osc/projectMember/status',
    method: 'put',
    data: { projectId, memberId, isActive }
  });
};

// 计算成员贡献度评分
export const calculateContributionScore = (projectId: number, memberId: number) => {
  return request({
    url: '/osc/projectMember/contribution',
    method: 'get',
    params: { projectId, memberId }
  });
};

// 导出项目成员关联
export const exportProjectMember = (params: ProjectMemberQuery) => {
  return request({
    url: '/osc/projectMember/export',
    method: 'post',
    data: params,
    responseType: 'blob'
  });
};

// 导入项目成员关联
export const importProjectMember = (file: File) => {
  const formData = new FormData();
  formData.append('file', file);
  return request({
    url: '/osc/projectMember/import',
    method: 'post',
    data: formData,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  });
};

// 下载导入模板
export const importTemplate = () => {
  return request({
    url: '/osc/projectMember/importTemplate',
    method: 'post',
    responseType: 'blob'
  });
};

// 获取项目成员统计信息
export const getProjectMemberStats = (projectId: number) => {
  return request({
    url: `/osc/projectMember/stats/${projectId}`,
    method: 'get'
  });
};

// 获取项目成员可视化数据
export const getProjectMemberVisualization = (projectId: number) => {
  return request({
    url: `/osc/projectMember/visualization/${projectId}`,
    method: 'get'
  });
};

// 获取成员项目统计
export const getMemberProjectStats = (memberId: number) => {
  return request({
    url: `/osc/projectMember/member/stats/${memberId}`,
    method: 'get'
  });
};

// 搜索项目
export const searchProjects = (keyword: string) => {
  return request({
    url: '/osc/project/search',
    method: 'get',
    params: { keyword, limit: 20 }
  });
};

// 搜索成员
export const searchMembers = (keyword: string) => {
  return request({
    url: '/osc/member/search',
    method: 'get',
    params: { keyword, limit: 20 }
  });
};
