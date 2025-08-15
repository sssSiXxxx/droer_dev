package org.dromara.osc.service;

import org.dromara.osc.domain.vo.ContributionVo;
import org.dromara.osc.domain.bo.ContributionBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 贡献统计Service接口
 *
 * @author lmq
 * @date 2025-08-15
 */
public interface IContributionService {

    /**
     * 查询贡献统计
     *
     * @param contributionId 主键
     * @return 贡献统计
     */
    ContributionVo queryById(Long contributionId);

    /**
     * 分页查询贡献统计列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 贡献统计分页列表
     */
    TableDataInfo<ContributionVo> queryPageList(ContributionBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的贡献统计列表
     *
     * @param bo 查询条件
     * @return 贡献统计列表
     */
    List<ContributionVo> queryList(ContributionBo bo);

    /**
     * 新增贡献统计
     *
     * @param bo 贡献统计
     * @return 是否新增成功
     */
    Boolean insertByBo(ContributionBo bo);

    /**
     * 修改贡献统计
     *
     * @param bo 贡献统计
     * @return 是否修改成功
     */
    Boolean updateByBo(ContributionBo bo);

    /**
     * 校验并批量删除贡献统计信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}
