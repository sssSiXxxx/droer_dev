import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ProjectMemberVO, ProjectMemberForm, ProjectMemberQuery } from '@/api/osc/projectMember/types';

/**
 * 查询人员项目管理列表
 * @param query
 * @returns {*}
 */

export const listProjectMember = (query?: ProjectMemberQuery): AxiosPromise<ProjectMemberVO[]> => {
  return request({
    url: '/osc/projectMember/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询人员项目管理详细
 * @param id
 */
export const getProjectMember = (id: string | number): AxiosPromise<ProjectMemberVO> => {
  return request({
    url: '/osc/projectMember/' + id,
    method: 'get'
  });
};

/**
 * 新增人员项目管理
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
 * 修改人员项目管理
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
 * 删除人员项目管理
 * @param id
 */
export const delProjectMember = (id: string | number | Array<string | number>) => {
  return request({
    url: '/osc/projectMember/' + id,
    method: 'delete'
  });
};
