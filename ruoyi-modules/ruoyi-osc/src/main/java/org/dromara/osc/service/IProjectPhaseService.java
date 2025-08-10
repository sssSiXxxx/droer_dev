package org.dromara.osc.service;

import org.dromara.osc.domain.vo.ProjectPhaseVo;
import org.dromara.osc.domain.bo.ProjectPhaseBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 进度追踪Service接口
 *
 * @author lmq
 * @date 2025-08-10
 */
public interface IProjectPhaseService {

    /**
     * 查询进度追踪
     *
     * @param phaseId 主键
     * @return 进度追踪
     */
    ProjectPhaseVo queryById(Long phaseId);

    /**
     * 分页查询进度追踪列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 进度追踪分页列表
     */
    TableDataInfo<ProjectPhaseVo> queryPageList(ProjectPhaseBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的进度追踪列表
     *
     * @param bo 查询条件
     * @return 进度追踪列表
     */
    List<ProjectPhaseVo> queryList(ProjectPhaseBo bo);

    /**
     * 新增进度追踪
     *
     * @param bo 进度追踪
     * @return 是否新增成功
     */
    Boolean insertByBo(ProjectPhaseBo bo);

    /**
     * 修改进度追踪
     *
     * @param bo 进度追踪
     * @return 是否修改成功
     */
    Boolean updateByBo(ProjectPhaseBo bo);

    /**
     * 校验并批量删除进度追踪信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
