export interface ProjectMemberVO {
  /**
   * ID
   */
  id?: string | number;

  /**
   * 项目ID
   */
  projectId: string | number;

  /**
   * 项目名称
   */
  projectName?: string;

  /**
   * 成员ID
   */
  memberId: string | number;

  /**
   * 成员名称
   */
  memberName?: string;

  /**
   * 项目角色
   */
  role: string;

  /**
   * 加入时间
   */
  joinTime: string;

}

export interface ProjectMemberForm extends BaseEntity {
  /**
   * ID
   */
  id?: string | number;

  /**
   * 项目ID
   */
  projectId?: string | number;

  /**
   * 成员ID
   */
  memberId?: string | number;

  /**
   * 项目角色
   */
  role?: string;

  /**
   * 加入时间
   */
  joinTime?: string;

}

export interface ProjectMemberQuery extends PageQuery {

  /**
   * 项目ID
   */
  projectId?: string | number;

  /**
   * 项目名称
   */
  projectName?: string;

  /**
   * 成员ID
   */
  memberId?: string | number;

  /**
   * 成员名称
   */
  memberName?: string;

  /**
   * 项目角色
   */
  role?: string;

  /**
   * 加入时间
   */
  joinTime?: string;

    /**
     * 日期范围参数
     */
    params?: any;
}



