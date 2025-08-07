export interface ProjectAuditVO {
  /**
   * 审核ID
   */
  auditId?: number;

  /**
   * 项目ID
   */
  projectId?: number;

  /**
   * 项目名称
   */
  projectName?: string;

  /**
   * 审核状态（0待审核 1通过 2拒绝 3退回修改）
   */
  auditStatus: string;

  /**
   * 审核人
   */
  auditUser: number;

  /**
   * 审核时间
   */
  auditTime?: Date;

  /**
   * 审核意见
   */
  auditOpinion: string;
}

export interface ProjectAuditForm extends BaseEntity {
  /**
   * 审核ID
   */
  auditId?: number;

  /**
   * 项目ID
   */
  projectId?: number;

  /**
   * 项目名称
   */
  projectName?: string;

  /**
   * 审核状态（0待审核 1通过 2拒绝 3退回修改）
   */
  auditStatus?: string;

  /**
   * 审核人
   */
  auditUser?: number;

  /**
   * 审核意见
   */
  auditOpinion?: string;
}

export interface ProjectAuditQuery extends PageQuery {
  /**
   * 项目名称
   */
  projectName?: string;

  /**
   * 审核状态（0待审核 1通过 2拒绝 3退回修改）
   */
  auditStatus?: string;

  /**
   * 审核人
   */
  auditUser?: number;

  /**
   * 日期范围参数
   */
  params?: any;
}