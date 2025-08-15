export interface ContributionVO {
  /**
   * 贡献ID
   */
  contributionId?: string | number;

  /**
   * 成员ID
   */
  memberId: string | number;

  /**
   * 成员名称
   */
  memberName: string;

  /**
   * 项目ID
   */
  projectId: string | number;

  /**
   * 项目名称
   */
  projectName: string;

  /**
   * 贡献类型（0代码提交 1问题修复 2文档贡献 3回答问题 4其他）
   */
  contributionType: string;

  /**
   * 贡献内容
   */
  content: string;

  /**
   * 贡献时间
   */
  contributionTime: string;

  /**
   * 贡献点数
   */
  points: number;

}

export interface ContributionForm extends BaseEntity {
  /**
   * 贡献ID
   */
  contributionId?: string | number;

  /**
   * 成员ID
   */
  memberId?: string | number;

  /**
   * 项目ID
   */
  projectId?: string | number;

  /**
   * 贡献类型（0代码提交 1问题修复 2文档贡献 3回答问题 4其他）
   */
  contributionType?: string;

  /**
   * 贡献内容
   */
  content?: string;

  /**
   * 相关链接
   */
  url?: string;

  /**
   * 贡献时间
   */
  contributionTime?: string;

  /**
   * 贡献点数
   */
  points?: number;

}

export interface ContributionQuery extends PageQuery {

  /**
   * 成员ID
   */
  memberId?: string | number;

  /**
   * 成员名称
   */
  memberName?: string;

  /**
   * 项目ID
   */
  projectId?: string | number;

  /**
   * 项目名称
   */
  projectName?: string;

  /**
   * 贡献类型（0代码提交 1问题修复 2文档贡献 3回答问题 4其他）
   */
  contributionType?: string;

  /**
   * 贡献内容
   */
  content?: string;

    /**
     * 日期范围参数
     */
    params?: any;
}



