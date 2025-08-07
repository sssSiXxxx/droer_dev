import request from '@/utils/request';
import { AxiosPromise } from 'axios';
import { ProjectAuditVO, ProjectAuditForm, ProjectAuditQuery } from '@/api/osc/projectAudit/types';

/**
 * 查询项目审核列表
 * @param query
 * @returns {*}
 */

export const listProjectAudit = (query?: ProjectAuditQuery): AxiosPromise<ProjectAuditVO[]> => {
  return request({
    url: '/osc/projectAudit/list',
    method: 'get',
    params: query
  });
};

/**
 * 查询项目审核详细
 * @param auditId
 */
export const getProjectAudit = (auditId: string | number): AxiosPromise<ProjectAuditVO> => {
  return request({
    url: '/osc/projectAudit/' + auditId,
    method: 'get'
  });
};

/**
 * 新增项目审核
 * @param data
 */
export const addProjectAudit = (data: ProjectAuditForm) => {
  return request({
    url: '/osc/projectAudit',
    method: 'post',
    data: data
  });
};

/**
 * 修改项目审核
 * @param data
 */
export const updateProjectAudit = (data: ProjectAuditForm) => {
  return request({
    url: '/osc/projectAudit',
    method: 'put',
    data: data
  });
};

/**
 * 删除项目审核
 * @param auditId
 */
export const delProjectAudit = (auditId: string | number | Array<string | number>) => {
  return request({
    url: '/osc/projectAudit/' + auditId,
    method: 'delete'
  });
};
