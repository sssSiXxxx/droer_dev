import request from '@/utils/request';
import { ProjectMemberQuery, ProjectMemberForm } from './types';

// 查询项目成员关联列表
export const listProjectMember = (query: ProjectMemberQuery) => {
  return request({
    url: '/osc/projectMember/list',
    method: 'get',
    params: query
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
    data: data
  });
};

// 修改项目成员关联
export const updateProjectMember = (data: ProjectMemberForm) => {
  return request({
    url: '/osc/projectMember',
    method: 'put',
    data: data
  });
};

// 删除项目成员关联
export const delProjectMember = (id: string | number | Array<string | number>) => {
  return request({
    url: `/osc/projectMember/${id}`,
    method: 'delete'
  });
};

// 根据项目ID查询成员列表
export const getProjectMembers = (projectId: number) => {
  return request({
    url: `/osc/projectMember/project/${projectId}`,
    method: 'get'
  });
};

// 根据成员ID查询项目列表
export const getMemberProjects = (memberId: number) => {
  return request({
    url: `/osc/projectMember/member/${memberId}`,
    method: 'get'
  });
};

// 批量添加成员到项目
export const batchAddMembers = (projectId: number, memberIds: number[], role: string) => {
  return request({
    url: '/osc/projectMember/batchAdd',
    method: 'post',
    params: {
      projectId,
      memberIds,
      role
    }
  });
};

// 从项目中移除成员
export const removeMember = (projectId: number, memberId: number) => {
  return request({
    url: '/osc/projectMember/remove',
    method: 'delete',
    params: {
      projectId,
      memberId
    }
  });
};

// 更新成员角色
export const updateMemberRole = (projectId: number, memberId: number, role: string) => {
  return request({
    url: '/osc/projectMember/role',
    method: 'put',
    params: {
      projectId,
      memberId,
      role
    }
  });
};

// 更新成员活跃状态
export const updateMemberActiveStatus = (projectId: number, memberId: number, isActive: string) => {
  return request({
    url: '/osc/projectMember/active',
    method: 'put',
    params: {
      projectId,
      memberId,
      isActive
    }
  });
};

// 计算成员贡献度评分
export const calculateContributionScore = (projectId: number, memberId: number) => {
  return request({
    url: '/osc/projectMember/contribution',
    method: 'get',
    params: {
      projectId,
      memberId
    }
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

// 获取成员项目统计信息
export const getMemberProjectStats = (memberId: number) => {
  return request({
    url: `/osc/projectMember/memberStats/${memberId}`,
    method: 'get'
  });
};

// 导出项目成员关联
export const exportProjectMember = (query: ProjectMemberQuery) => {
  return request({
    url: '/osc/projectMember/export',
    method: 'post',
    data: query,
    responseType: 'blob'
  });
};

// 导入项目成员关联
export const importProjectMember = (file: File, updateSupport: boolean = false) => {
  const formData = new FormData();
  formData.append('file', file);
  formData.append('updateSupport', updateSupport.toString());

  return request({
    url: '/osc/projectMember/importData',
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
