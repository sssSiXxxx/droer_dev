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
import org.dromara.osc.domain.bo.ProjectPhaseBo;
import org.dromara.osc.domain.vo.ProjectPhaseVo;
import org.dromara.osc.domain.ProjectPhase;
import org.dromara.osc.mapper.ProjectPhaseMapper;
import org.dromara.osc.service.IProjectPhaseService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 进度追踪Service业务层处理
 *
 * @author lmq
 * @date 2025-08-10
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectPhaseServiceImpl implements IProjectPhaseService {

    private final ProjectPhaseMapper baseMapper;

    /**
     * 查询进度追踪
     *
     * @param phaseId 主键
     * @return 进度追踪
     */
    @Override
    public ProjectPhaseVo queryById(Long phaseId){
        return baseMapper.selectVoById(phaseId);
    }

    /**
     * 分页查询进度追踪列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 进度追踪分页列表
     */
    @Override
    public TableDataInfo<ProjectPhaseVo> queryPageList(ProjectPhaseBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ProjectPhase> lqw = buildQueryWrapper(bo);
        Page<ProjectPhaseVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的进度追踪列表
     *
     * @param bo 查询条件
     * @return 进度追踪列表
     */
    @Override
    public List<ProjectPhaseVo> queryList(ProjectPhaseBo bo) {
        LambdaQueryWrapper<ProjectPhase> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ProjectPhase> buildQueryWrapper(ProjectPhaseBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ProjectPhase> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ProjectPhase::getPhaseId);
        lqw.like(bo.getProjectId() != null, ProjectPhase::getProjectId, bo.getProjectId());
        lqw.like(StringUtils.isNotBlank(bo.getPhaseName()), ProjectPhase::getPhaseName, bo.getPhaseName());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), ProjectPhase::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增进度追踪
     *
     * @param bo 进度追踪
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ProjectPhaseBo bo) {
        ProjectPhase add = MapstructUtils.convert(bo, ProjectPhase.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setPhaseId(add.getPhaseId());
        }
        return flag;
    }

    /**
     * 修改进度追踪
     *
     * @param bo 进度追踪
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ProjectPhaseBo bo) {
        ProjectPhase update = MapstructUtils.convert(bo, ProjectPhase.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ProjectPhase entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除进度追踪信息
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
