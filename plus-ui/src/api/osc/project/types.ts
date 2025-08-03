export interface ProjectVO {
  /**
   * 项目ID
   */
  projectId: number;

  /**
   * 项目名称
   */
  projectName: string;

  /**
   * 项目编号
   */
  projectCode: string;

  /**
   * 项目描述
   */
  description: string;

  /**
   * 代码仓库
   */
  repositoryUrl: string;

  /**
   * 项目网站
   */
  websiteUrl: string;

  /**
   * 项目Logo
   */
  logoUrl: string;

  /**
   * 项目LogoUrl
   */
  logoUrlUrl: string;
  /**
   * 项目状态
   */
  status: string;

  /**
   * 备注
   */
  remark: string;

}

export interface ProjectForm extends BaseEntity {
  /**
   * 项目ID
   */
  projectId?: number;

  /**
   * 项目名称
   */
  projectName?: string;

  /**
   * 项目编号（可选）
   */
  projectCode?: string;

  /**
   * 项目描述
   */
  description?: string;

  /**
   * 代码仓库
   */
  repositoryUrl?: string;

  /**
   * 项目网站
   */
  websiteUrl?: string;

  /**
   * 项目Logo
   */
  logoUrl?: string;

  /**
   * 项目状态
   */
  status?: string;

  /**
   * 备注
   */
  remark?: string;

}

export interface ProjectQuery extends PageQuery {

  /**
   * 项目名称
   */
  projectName?: string;

  /**
   * 项目状态
   */
  status?: string;

    /**
     * 日期范围参数
     */
    params?: any;
}



