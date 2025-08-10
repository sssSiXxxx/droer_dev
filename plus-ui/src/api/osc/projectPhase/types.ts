// 项目阶段查询参数
export interface ProjectPhaseQuery {
  pageNum?: number;
  pageSize?: number;
  projectId?: string | number;
  phaseName?: string;
  status?: string;
  startTime?: string;
  endTime?: string;
}

// 项目阶段表单数据
export interface ProjectPhaseForm {
  phaseId?: string | number;
  projectId?: string | number;
  phaseName?: string;
  phaseCode?: string;
  description?: string;
  startTime?: string;
  endTime?: string;
  status?: string;
  createBy?: string | number;
  createTime?: string;
  updateBy?: string | number;
  updateTime?: string;
  remark?: string;
}

// 项目阶段统计数据
export interface ProjectPhaseStatistics {
  totalPhases: number;
  completedPhases: number;
  inProgressPhases: number;
  delayedPhases: number;
  upcomingPhases: number;
  completionRate: number;
  averageDelay: number;
}