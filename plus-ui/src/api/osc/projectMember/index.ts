import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ProjectMemberForm, ProjectMemberQuery, ProjectMemberVO } from './types';

/**
 * 查询项目成员关联列表
 * @param query
 */
export const listProjectMember = (query: ProjectMemberQuery): AxiosPromise<ProjectMemberVO[]> => {
  return request({
    url: '/osc/projectMember/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询项目成员关联详细
 * @param id
 */
export const getProjectMember = (id: string | number): AxiosPromise<ProjectMemberVO> => {
  return request({
    url: '/osc/projectMember/' + id,
    method: 'get'
  });
};

/**
 * 新增项目成员关联
 * @param data
 */
export const addProjectMember = (data: ProjectMemberForm) => {
  return request({
    url: '/osc/projectMember',
    method: 'post',
    data: data
  });
};

/**
 * 修改项目成员关联
 * @param data
 */
export const updateProjectMember = (data: ProjectMemberForm) => {
  return request({
    url: '/osc/projectMember',
    method: 'put',
    data: data
  });
};

/**
 * 删除项目成员关联
 * @param id
 */
export const delProjectMember = (id: string | number | Array<string | number>) => {
  return request({
    url: '/osc/projectMember/' + id,
    method: 'delete'
  });
};

/**
 * 导出项目成员关联
 * @param query
 */
export const exportProjectMember = (query: ProjectMemberQuery) => {
  return request({
    url: '/osc/projectMember/export',
    method: 'post',
    data: query
  });
};

/**
 * 导入项目成员数据
 * @param file
 * @param updateSupport
 */
export const importProjectMember = (file: File, updateSupport: boolean) => {
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

/**
 * 获取导入模板
 */
export const importTemplate = () => {
  return request({
    url: '/osc/projectMember/importTemplate',
    method: 'post'
  });
};

/**
 * 根据项目ID查询项目成员列表
 * @param projectId
 */
export const getProjectMembers = (projectId: string | number): AxiosPromise<ProjectMemberVO[]> => {
  return request({
    url: '/osc/projectMember/project/' + projectId,
    method: 'get'
  });
};

/**
 * 根据成员ID查询参与的项目列表
 * @param memberId
 */
export const getMemberProjects = (memberId: string | number): AxiosPromise<ProjectMemberVO[]> => {
  return request({
    url: '/osc/projectMember/member/' + memberId,
    method: 'get'
  });
};

/**
 * 批量添加项目成员
 * @param projectId
 * @param memberIds
 * @param role
 */
export const batchAddMembers = (projectId: string | number, memberIds: (string | number)[], role: string) => {
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

/**
 * 移除项目成员
 * @param projectId
 * @param memberId
 */
export const removeMember = (projectId: string | number, memberId: string | number) => {
  return request({
    url: '/osc/projectMember/removeMember',
    method: 'post',
    params: {
      projectId,
      memberId
    }
  });
};

/**
 * 更新成员角色
 * @param projectId
 * @param memberId
 * @param role
 */
export const updateMemberRole = (projectId: string | number, memberId: string | number, role: string) => {
  return request({
    url: '/osc/projectMember/updateRole',
    method: 'post',
    params: {
      projectId,
      memberId,
      role
    }
  });
};

/**
 * 更新成员活跃状态
 * @param projectId
 * @param memberId
 * @param isActive
 */
export const updateMemberActiveStatus = (projectId: string | number, memberId: string | number, isActive: string) => {
  return request({
    url: '/osc/projectMember/updateActiveStatus',
    method: 'post',
    params: {
      projectId,
      memberId,
      isActive
    }
  });
};

/**
 * 计算成员贡献度评分
 * @param projectId
 * @param memberId
 */
export const calculateContributionScore = (projectId: string | number, memberId: string | number): AxiosPromise<number> => {
  return request({
    url: '/osc/projectMember/contributionScore',
    method: 'get',
    params: {
      projectId,
      memberId
    }
  });
};

/**
 * 获取项目成员统计信息
 * @param projectId
 */
export const getProjectMemberStats = (projectId: string | number): AxiosPromise<any> => {
  return request({
    url: '/osc/projectMember/stats/' + projectId,
    method: 'get'
  });
};
