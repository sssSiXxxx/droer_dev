import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ProjectVO, ProjectForm, ProjectQuery } from '@/api/osc/project/types';

/**
 * 查询项目列表列表
 * @param query
 * @returns {*}
 */

// 定义分页响应类型
interface TableDataInfo<T> {
  rows: T[];
  total: number;
  code: number;
  msg: string;
}

export const listProject = (query?: ProjectQuery): AxiosPromise<TableDataInfo<ProjectVO>> => {
  return request({
    url: '/osc/project/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询项目列表（用于OSS文件上传，无需权限）
 */
export const listProjectForOss = (query?: ProjectQuery): AxiosPromise<TableDataInfo<ProjectVO>> => {
  return request({
    url: '/osc/project/oss/list',
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

/**
 * 同步项目数据（从Git仓库更新Star、Fork等动态数据）
 */
export const syncProjectData = () => {
  return request({
    url: '/osc/project/sync',
    method: 'post'
  });
};

/**
 * 同步单个项目数据
 * @param projectId
 */
export const syncSingleProject = (projectId: string | number) => {
  return request({
    url: '/osc/project/sync/' + projectId,
    method: 'post'
  });
};
