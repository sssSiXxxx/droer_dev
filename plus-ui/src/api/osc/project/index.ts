import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ProjectVO, ProjectForm, ProjectQuery } from '@/api/osc/project/types';

/**
 * 查询项目列表列表
 * @param query
 * @returns {*}
 */

export const listProject = (query?: ProjectQuery): AxiosPromise<ProjectVO[]> => {
  return request({
    url: '/osc/project/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询项目列表详细
 * @param projectId
 */
export const getProject = (projectId: string | number): AxiosPromise<ProjectVO> => {
  return request({
    url: '/osc/project/' + projectId,
    method: 'get'
  });
};

/**
 * 新增项目列表
 * @param data
 */
export const addProject = (data: ProjectForm) => {
  return request({
    url: '/osc/project',
    method: 'post',
    data: data
  });
};

/**
 * 修改项目列表
 * @param data
 */
export const updateProject = (data: ProjectForm) => {
  return request({
    url: '/osc/project',
    method: 'put',
    data: data
  });
};

/**
 * 删除项目列表
 * @param projectId
 */
export const delProject = (projectId: string | number | Array<string | number>) => {
  return request({
    url: '/osc/project/' + projectId,
    method: 'delete'
  });
};
