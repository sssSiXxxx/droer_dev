// 项目阶段查询参数
export interface ProjectPhaseQuery {
  pageNum?: number;
  pageSize?: number;
  projectId?: string | number;
  phaseName?: string;
  status?: string;
  startTime?: string;
  endTime?: string;
  priority?: number;
  ownerId?: string | number;
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
  actualStartTime?: string;
  actualEndTime?: string;
  status?: string;
  progress?: number;
  ownerId?: string | number;
  priority?: number;
  createBy?: string | number;
  createTime?: string;
  updateBy?: string | number;
  updateTime?: string;
  remark?: string;
}

// 项目阶段视图对象（包含关联信息）
export interface ProjectPhaseVo extends ProjectPhaseForm {
  projectName?: string;
  ownerName?: string;
  createByName?: string;
  updateByName?: string;
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

// 甘特图数据项
export interface GanttItem {
  phaseId: string | number;
  phaseName: string;
  startTime: string;
  endTime: string;
  actualStartTime?: string;
  actualEndTime?: string;
  status: string;
  progress: number;
  priority: number;
  dependencies?: (string | number)[];
}

// 阶段状态选项
export interface PhaseStatusOption {
  label: string;
  value: string;
  type: 'success' | 'info' | 'warning' | 'danger';
}

// 优先级选项
export interface PriorityOption {
  label: string;
  value: number;
  type: 'success' | 'info' | 'warning' | 'danger';
}

// 批量操作数据
export interface BatchUpdateData {
  phaseIds: (string | number)[];
  status?: string;
  ownerId?: string | number;
  priority?: number;
}

// 进度更新数据
export interface ProgressUpdateData {
  phaseId: string | number;
  progress: number;
  remark?: string;
}

// 阶段状态枚举
export enum PhaseStatus {
  NOT_STARTED = '0',
  IN_PROGRESS = '1', 
  COMPLETED = '2',
  PAUSED = '3',
  DELAYED = '4'
}

// 优先级枚举
export enum PhasePriority {
  LOW = 1,
  MEDIUM = 2,
  HIGH = 3
}
