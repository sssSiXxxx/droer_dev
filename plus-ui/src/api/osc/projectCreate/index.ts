import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ProjectCreateVO, ProjectCreateForm, ProjectCreateQuery } from '@/api/osc/projectCreate/types';

/**
 * 查询项目创建列表
 * @param query
 * @returns {*}
 */

export const listProjectCreate = (query?: ProjectCreateQuery): AxiosPromise<ProjectCreateVO[]> => {
  return request({
    url: '/osc/projectCreate/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询项目创建详细
 * @param projectId
 */
export const getProjectCreate = (projectId: string | number): AxiosPromise<ProjectCreateVO> => {
  return request({
    url: '/osc/projectCreate/' + projectId,
    method: 'get'
  });
};

/**
 * 新增项目创建
 * @param data
 */
export const addProjectCreate = (data: ProjectCreateForm) => {
  return request({
    url: '/osc/projectCreate',
    method: 'post',
    data: data
  });
};

/**
 * 修改项目创建
 * @param data
 */
export const updateProjectCreate = (data: ProjectCreateForm) => {
  return request({
    url: '/osc/projectCreate',
    method: 'put',
    data: data
  });
};

/**
 * 删除项目创建
 * @param projectId
 */
export const delProjectCreate = (projectId: string | number | Array<string | number>) => {
  return request({
    url: '/osc/projectCreate/' + projectId,
    method: 'delete'
  });
};
