export interface MemberVO {
  /**
   * 成员ID
   */
  memberId: string | number;

  /**
   * 关联用户ID
   */
  userId?: string | number;

  /**
   * 成员名称
   */
  memberName: string;

  /**
   * 昵称
   */
  nickname?: string;

  /**
   * 头像
   */
  avatar?: string;

  /**
   * 邮箱
   */
  email?: string;

  /**
   * GitHub ID
   */
  githubId?: string;

  /**
   * Gitee ID
   */
  giteeId?: string;

  /**
   * 状态（0正常 1禁用）
   */
  status: string;

  /**
   * 加入时间
   */
  joinTime?: string;

  /**
   * 备注
   */
  remark?: string;

  /**
   * 创建时间
   */
  createTime?: string;

  /**
   * 更新时间
   */
  updateTime?: string;
}

export interface MemberForm extends BaseEntity {
  /**
   * 成员ID
   */
  memberId?: string | number;

  /**
   * 关联用户ID
   */
  userId?: string | number;

  /**
   * 成员名称
   */
  memberName?: string;

  /**
   * 昵称
   */
  nickname?: string;

  /**
   * 头像
   */
  avatar?: string;

  /**
   * 邮箱
   */
  email?: string;

  /**
   * GitHub ID
   */
  githubId?: string;

  /**
   * Gitee ID
   */
  giteeId?: string;

  /**
   * 状态（0正常 1禁用）
   */
  status?: string;

  /**
   * 加入时间
   */
  joinTime?: string;

  /**
   * 备注
   */
  remark?: string;
}

export interface MemberQuery extends PageQuery {
  /**
   * 成员ID
   */
  memberId?: string | number;

  /**
   * 关联用户ID
   */
  userId?: string | number;

  /**
   * 成员名称
   */
  memberName?: string;

  /**
   * 昵称
   */
  nickname?: string;

  /**
   * 邮箱
   */
  email?: string;

  /**
   * GitHub ID
   */
  githubId?: string;

  /**
   * Gitee ID
   */
  giteeId?: string;

  /**
   * 状态（0正常 1禁用）
   */
  status?: string;

  /**
   * 加入时间
   */
  joinTime?: string;

  /**
   * 日期范围参数
   */
  params?: any;
}
