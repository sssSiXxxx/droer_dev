export interface ProjectMemberVO {
  id?: number;
  projectId?: number;
  memberId?: number;
  role?: string;
  joinTime?: string;
  permissionLevel?: number;
  isActive?: string;
  contributionScore?: number;
  remark?: string;
  projectName?: string;
  projectCode?: string;
  memberName?: string;
  memberNickname?: string;
  memberEmail?: string;
  memberAvatar?: string;
  giteeAccount?: string;
  githubAccount?: string;
  contributionCount?: number;
  totalPoints?: number;
  createDept?: number;
  createBy?: number;
  createTime?: string;
  updateBy?: number;
  updateTime?: string;
}

export interface ProjectMemberForm {
  id?: number;
  projectId?: number;
  memberId?: number;
  role?: string;
  joinTime?: string;
  permissionLevel?: number;
  isActive?: string;
  contributionScore?: number;
  remark?: string;
}

export interface ProjectMemberQuery {
  pageNum?: number;
  pageSize?: number;
  projectId?: number;
  memberId?: number;
  projectName?: string;
  memberName?: string;
  role?: string;
  permissionLevel?: number;
  isActive?: string;
  contributionScore?: number;
  joinTime?: string;
  beginJoinTime?: string;
  endJoinTime?: string;
  params?: any;
}



