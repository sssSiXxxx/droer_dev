package org.dromara.osc.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.osc.domain.bo.ContributionBo;
import org.dromara.osc.domain.vo.ContributionVo;
import org.dromara.osc.domain.Contribution;
import org.dromara.osc.mapper.ContributionMapper;
import org.dromara.osc.service.IContributionService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 贡献统计Service业务层处理
 *
 * @author lmq
 * @date 2025-08-15
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ContributionServiceImpl implements IContributionService {

    private final ContributionMapper baseMapper;

    /**
     * 查询贡献统计
     *
     * @param contributionId 主键
     * @return 贡献统计
     */
    @Override
    public ContributionVo queryById(Long contributionId){
        return baseMapper.selectVoById(contributionId);
    }

    /**
     * 分页查询贡献统计列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 贡献统计分页列表
     */
    @Override
    public TableDataInfo<ContributionVo> queryPageList(ContributionBo bo, PageQuery pageQuery) {
        // 使用自定义查询方法，包含成员名称和项目名称
        Contribution contribution = MapstructUtils.convert(bo, Contribution.class);
        List<ContributionVo> list = baseMapper.selectContributionList(contribution);
        
        // 手动分页
        int total = list.size();
        int start = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
        int end = Math.min(start + pageQuery.getPageSize(), total);
        List<ContributionVo> pageList = list.subList(start, end);
        
        Page<ContributionVo> result = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize(), total);
        result.setRecords(pageList);
        
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的贡献统计列表
     *
     * @param bo 查询条件
     * @return 贡献统计列表
     */
    @Override
    public List<ContributionVo> queryList(ContributionBo bo) {
        LambdaQueryWrapper<Contribution> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Contribution> buildQueryWrapper(ContributionBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Contribution> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(Contribution::getContributionId);
        lqw.like(bo.getMemberId() != null, Contribution::getMemberId, bo.getMemberId());
        lqw.like(bo.getProjectId() != null, Contribution::getProjectId, bo.getProjectId());
        lqw.eq(StringUtils.isNotBlank(bo.getContributionType()), Contribution::getContributionType, bo.getContributionType());
        lqw.eq(StringUtils.isNotBlank(bo.getContent()), Contribution::getContent, bo.getContent());
        return lqw;
    }

    /**
     * 新增贡献统计
     *
     * @param bo 贡献统计
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ContributionBo bo) {
        Contribution add = MapstructUtils.convert(bo, Contribution.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setContributionId(add.getContributionId());
        }
        return flag;
    }

    /**
     * 修改贡献统计
     *
     * @param bo 贡献统计
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ContributionBo bo) {
        Contribution update = MapstructUtils.convert(bo, Contribution.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Contribution entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除贡献统计信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
