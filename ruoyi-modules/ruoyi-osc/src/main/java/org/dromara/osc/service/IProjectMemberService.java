package org.dromara.osc.service;

import org.dromara.osc.domain.vo.ProjectMemberVo;
import org.dromara.osc.domain.bo.ProjectMemberBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 项目成员关联Service接口
 *
 * @author lmq
 * @date 2025-08-17
 */
public interface IProjectMemberService {

    /**
     * 查询项目成员关联
     *
     * @param id 主键
     * @return 项目成员关联
     */
    ProjectMemberVo queryById(Long id);

    /**
     * 分页查询项目成员关联列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 项目成员关联分页列表
     */
    TableDataInfo<ProjectMemberVo> queryPageList(ProjectMemberBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的项目成员关联列表
     *
     * @param bo 查询条件
     * @return 项目成员关联列表
     */
    List<ProjectMemberVo> queryList(ProjectMemberBo bo);

    /**
     * 新增项目成员关联
     *
     * @param bo 项目成员关联
     * @return 是否新增成功
     */
    Boolean insertByBo(ProjectMemberBo bo);

    /**
     * 修改项目成员关联
     *
     * @param bo 项目成员关联
     * @return 是否修改成功
     */
    Boolean updateByBo(ProjectMemberBo bo);

    /**
     * 校验并批量删除项目成员关联信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);



    /**
     * 根据项目ID查询项目成员列表
     *
     * @param projectId 项目ID
     * @return 项目成员列表
     */
    List<ProjectMemberVo> queryByProjectId(Long projectId);

    /**
     * 根据成员ID查询参与的项目列表
     *
     * @param memberId 成员ID
     * @return 项目列表
     */
    List<ProjectMemberVo> queryByMemberId(Long memberId);

    /**
     * 批量添加项目成员
     *
     * @param projectId  项目ID
     * @param memberIds  成员ID列表
     * @param role       角色
     * @return 是否成功
     */
    Boolean batchAddMembers(Long projectId, List<Long> memberIds, String role);

    /**
     * 移除项目成员
     *
     * @param projectId 项目ID
     * @param memberId  成员ID
     * @return 是否成功
     */
    Boolean removeMember(Long projectId, Long memberId);

    /**
     * 更新成员角色
     *
     * @param projectId 项目ID
     * @param memberId  成员ID
     * @param role      新角色
     * @return 是否成功
     */
    Boolean updateMemberRole(Long projectId, Long memberId, String role);

    /**
     * 更新成员活跃状态
     *
     * @param projectId 项目ID
     * @param memberId  成员ID
     * @param isActive  是否活跃
     * @return 是否成功
     */
    Boolean updateMemberActiveStatus(Long projectId, Long memberId, String isActive);

    /**
     * 计算成员贡献度评分
     *
     * @param projectId 项目ID
     * @param memberId  成员ID
     * @return 贡献度评分
     */
    Integer calculateContributionScore(Long projectId, Long memberId);

    /**
     * 获取项目成员统计信息
     *
     * @param projectId 项目ID
     * @return 统计信息
     */
    Object getProjectMemberStats(Long projectId);
}
