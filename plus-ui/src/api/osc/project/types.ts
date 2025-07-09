export interface ProjectVO {
  /**
   * 项目ID
   */
  projectId: string | number;

  /**
   * 项目名称
   */
  projectName: string;

  /**
   * 项目状态（1进行中 2已完成 3已归档）
   */
  projectStatus: number;

  /**
   * 备注
   */
  remark: string;

  /**
   * 项目难度（基础、进阶）
   */
  difficulty: string;

  /**
   * 项目描述
   */
  projectDescription: string;

  /**
   * Gitee仓库地址
   */
  giteeRepoUrl: string;

  /**
   * 项目负责人ID
   */
  projectLeader: number;

}

export interface ProjectForm extends BaseEntity {
}

export interface ProjectQuery extends PageQuery {

  /**
   * 项目名称
   */
  projectName?: string;

  /**
   * 项目状态（1进行中 2已完成 3已归档）
   */
  projectStatus?: number;

  /**
   * 项目难度（基础、进阶）
   */
  difficulty?: string;

  /**
   * Gitee仓库地址
   */
  giteeRepoUrl?: string;

  /**
   * 项目负责人ID
   */
  projectLeader?: number;

    /**
     * 日期范围参数
     */
    params?: any;
}



