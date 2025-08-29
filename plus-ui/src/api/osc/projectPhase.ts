import request from '@/utils/request';

export interface ProjectPhaseVO {
  phaseId?: number;
  projectId?: number;
  projectName?: string;
  phaseName?: string;
  phaseCode?: string;
  description?: string;
  startTime?: string;
  endTime?: string;
  actualStartTime?: string;
  actualEndTime?: string;
  status?: string;
  progress?: number;
  ownerId?: number;
  ownerName?: string;
  priority?: number;
  createTime?: string;
  updateTime?: string;
}

export interface ProjectPhaseForm {
  phaseId?: number;
  projectId?: number;
  phaseName?: string;
  phaseCode?: string;
  description?: string;
  startTime?: string;
  endTime?: string;
  status?: string;
  progress?: number;
  ownerId?: number;
  priority?: number;
}

export interface ProjectPhaseQuery extends PageQuery {
  projectId?: number;
  phaseName?: string;
  status?: string;
  priority?: number;
}

// 查询项目阶段列表
export const listProjectPhase = (params: ProjectPhaseQuery) => {
  return request({
    url: '/osc/projectPhase/list',
    method: 'get',
    params
  });
};

// 查询项目阶段详细
export const getProjectPhase = (phaseId: number | string) => {
  return request({
    url: `/osc/projectPhase/${phaseId}`,
    method: 'get'
  });
};

// 新增项目阶段
export const addProjectPhase = (data: ProjectPhaseForm) => {
  return request({
    url: '/osc/projectPhase',
    method: 'post',
    data
  });
};

// 修改项目阶段
export const updateProjectPhase = (data: ProjectPhaseForm) => {
  return request({
    url: '/osc/projectPhase',
    method: 'put',
    data
  });
};

// 删除项目阶段
export const delProjectPhase = (phaseId: number | string | Array<number | string>) => {
  return request({
    url: `/osc/projectPhase/${phaseId}`,
    method: 'delete'
  });
};

// 获取项目阶段统计
export const getPhaseStatistics = (projectId: number) => {
  return request({
    url: `/osc/projectPhase/stats/${projectId}`,
    method: 'get'
  });
};

// 完成阶段
export const completeProjectPhase = (phaseId: number) => {
  return request({
    url: `/osc/projectPhase/complete/${phaseId}`,
    method: 'put'
  });
};

// 暂停阶段
export const pauseProjectPhase = (phaseId: number) => {
  return request({
    url: `/osc/projectPhase/pause/${phaseId}`,
    method: 'put'
  });
};

// 恢复阶段
export const resumeProjectPhase = (phaseId: number) => {
  return request({
    url: `/osc/projectPhase/resume/${phaseId}`,
    method: 'put'
  });
};

// 更新进度
export const updatePhaseProgress = (phaseId: number, progress: number) => {
  return request({
    url: `/osc/projectPhase/progress/${phaseId}`,
    method: 'put',
    data: { progress }
  });
};

// 创建标准阶段
export const createStandardPhases = (projectId: number) => {
  return request({
    url: `/osc/projectPhase/standard/${projectId}`,
    method: 'post'
  });
};

// 推进到下一阶段
export const advanceToNextPhase = (phaseId: number) => {
  return request({
    url: `/osc/projectPhase/advance/${phaseId}`,
    method: 'put'
  });
};

// 获取下一阶段信息
export const getNextPhase = (projectId: number, currentPhaseId: number) => {
  return request({
    url: `/osc/projectPhase/next/${projectId}/${currentPhaseId}`,
    method: 'get'
  });
};

// 导出阶段数据
export const exportProjectPhase = (params: ProjectPhaseQuery) => {
  return request({
    url: '/osc/projectPhase/export',
    method: 'post',
    data: params,
    responseType: 'blob'
  });
};

// 批量更新阶段状态
export const batchUpdatePhaseStatus = (phaseIds: number[], status: string) => {
  return request({
    url: '/osc/projectPhase/batch/status',
    method: 'put',
    data: { phaseIds, status }
  });
};