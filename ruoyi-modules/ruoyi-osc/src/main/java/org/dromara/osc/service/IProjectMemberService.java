package org.dromara.osc.service;

import org.dromara.osc.domain.vo.ProjectMemberVo;
import org.dromara.osc.domain.bo.ProjectMemberBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 人员项目管理Service接口
 *
 * @author lmq
 * @date 2025-08-15
 */
public interface IProjectMemberService {

    /**
     * 查询人员项目管理
     *
     * @param id 主键
     * @return 人员项目管理
     */
    ProjectMemberVo queryById(Long id);

    /**
     * 分页查询人员项目管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 人员项目管理分页列表
     */
    TableDataInfo<ProjectMemberVo> queryPageList(ProjectMemberBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的人员项目管理列表
     *
     * @param bo 查询条件
     * @return 人员项目管理列表
     */
    List<ProjectMemberVo> queryList(ProjectMemberBo bo);

    /**
     * 新增人员项目管理
     *
     * @param bo 人员项目管理
     * @return 是否新增成功
     */
    Boolean insertByBo(ProjectMemberBo bo);

    /**
     * 修改人员项目管理
     *
     * @param bo 人员项目管理
     * @return 是否修改成功
     */
    Boolean updateByBo(ProjectMemberBo bo);

    /**
     * 校验并批量删除人员项目管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
