import request from '@/utils/request';
import { ProjectPhaseQuery, ProjectPhaseForm, ProjectPhaseStatistics } from './types';
import { AxiosPromise } from 'axios';

// 查询项目阶段列表
export function listProjectPhase(query: ProjectPhaseQuery) {
  return request({
    url: '/osc/projectPhase/list',
    method: 'get',
    params: query
  });
}

// 查询项目阶段详细信息
export function getProjectPhase(phaseId: string | number): AxiosPromise<{ data: ProjectPhaseForm }> {
  return request({
    url: `/osc/projectPhase/${phaseId}`,
    method: 'get'
  });
}

// 新增项目阶段
export function addProjectPhase(data: ProjectPhaseForm) {
  return request({
    url: '/osc/projectPhase',
    method: 'post',
    data: data
  });
}

// 修改项目阶段
export function updateProjectPhase(data: ProjectPhaseForm) {
  return request({
    url: '/osc/projectPhase',
    method: 'put',
    data: data
  });
}

// 删除项目阶段
export function delProjectPhase(phaseId: string | number) {
  return request({
    url: `/osc/projectPhase/${phaseId}`,
    method: 'delete'
  });
}

// 获取项目阶段统计数据
export function getPhaseStatistics(projectId: string | number): AxiosPromise<{ data: ProjectPhaseStatistics }> {
  return request({
    url: `/osc/projectPhase/statistics/${projectId}`,
    method: 'get'
  });
}

// 导出项目阶段数据
export function exportProjectPhase(query?: ProjectPhaseQuery) {
  return request({
    url: '/osc/projectPhase/export',
    method: 'post',
    params: query,
    responseType: 'blob'
  });
}

// 完成项目阶段
export function completeProjectPhase(phaseId: string | number) {
  return request({
    url: `/osc/projectPhase/complete/${phaseId}`,
    method: 'put'
  });
}

// 暂停项目阶段
export function pauseProjectPhase(phaseId: string | number) {
  return request({
    url: `/osc/projectPhase/pause/${phaseId}`,
    method: 'put'
  });
}

// 恢复项目阶段
export function resumeProjectPhase(phaseId: string | number) {
  return request({
    url: `/osc/projectPhase/resume/${phaseId}`,
    method: 'put'
  });
}