// 项目成员关联VO
export interface ProjectMemberVO {
  id?: number;
  projectId?: number;
  projectName?: string;
  projectCode?: string;
  memberId?: number;
  memberName?: string;
  memberEmail?: string;
  role?: string;
  roleName?: string;
  permissionLevel?: number;
  isActive?: string;
  contributionScore?: number;
  joinTime?: string;
  remark?: string;
  createTime?: string;
  updateTime?: string;
}

// 项目成员关联查询参数
export interface ProjectMemberQuery {
  projectId?: number;
  memberId?: number;
  role?: string;
  isActive?: string;
  projectName?: string;
  memberName?: string;
  beginJoinTime?: string;
  endJoinTime?: string;
  pageNum?: number;
  pageSize?: number;
}

// 项目成员关联表单
export interface ProjectMemberForm {
  id?: number;
  projectId?: number;
  memberId?: number;
  role?: string;
  permissionLevel?: number;
  isActive?: string;
  contributionScore?: number;
  joinTime?: string;
  remark?: string;
}

// 角色选项
export interface RoleOption {
  value: string;
  label: string;
  color: string;
}

// 可视化数据
export interface VisualizationData {
  roleData: RoleData[];
  totalMembers: number;
  activeMembers: number;
}

// 角色数据
export interface RoleData {
  roleCode: string;
  roleName: string;
  memberCount: number;
  members: ProjectMemberVO[];
  color: string;
}

// 统计信息
export interface StatsData {
  totalMembers: number;
  activeMembers: number;
  roleStats: Record<string, number>;
}
