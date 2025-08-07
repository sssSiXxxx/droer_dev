package org.dromara.osc.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collection;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.osc.domain.Project;
import org.dromara.osc.service.IProjectService;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.dromara.osc.domain.bo.ProjectAuditBo;
import org.dromara.osc.domain.vo.ProjectAuditVo;
import org.dromara.osc.domain.ProjectAudit;
import org.dromara.osc.mapper.ProjectAuditMapper;
import org.dromara.osc.service.IProjectAuditService;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.dromara.osc.domain.bo.ProjectBo;

/**
 * 项目审核Service业务层处理
 *
 * @author Lion Li
 * @date 2023-08-06
 */
@RequiredArgsConstructor
@Service
public class ProjectAuditServiceImpl extends ServiceImpl<ProjectAuditMapper, ProjectAudit> implements IProjectAuditService {

    private final ProjectAuditMapper baseMapper;
    private final IProjectService projectService;

    /**
     * 查询项目审核分页列表
     */
    @Override
    public TableDataInfo<ProjectAuditVo> queryPageList(ProjectAuditBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ProjectAudit> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoPage(pageQuery.build(), lqw, ProjectAuditVo.class);
    }

    /**
     * 查询项目审核
     */
    @Override
    public ProjectAuditVo queryById(Long auditId) {
        return baseMapper.selectVoById(auditId);
    }

    /**
     * 查询项目审核列表
     */
    @Override
    public List<ProjectAuditVo> queryList(ProjectAuditBo bo) {
        LambdaQueryWrapper<ProjectAudit> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ProjectAudit> buildQueryWrapper(ProjectAuditBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ProjectAudit> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getProjectId() != null, ProjectAudit::getProjectId, bo.getProjectId());
        lqw.eq(StringUtils.isNotBlank(bo.getAuditStatus()), ProjectAudit::getAuditStatus, bo.getAuditStatus());
        lqw.eq(bo.getAuditUser() != null, ProjectAudit::getAuditUser, bo.getAuditUser());
        lqw.orderByDesc(ProjectAudit::getCreateTime);
        return lqw;
    }

    /**
     * 新增项目审核
     */
    @Override
    public Boolean insertByBo(ProjectAuditBo bo) {
        ProjectAudit add = MapstructUtils.convert(bo, ProjectAudit.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setAuditId(add.getAuditId());
        }
        return flag;
    }

    /**
     * 修改项目审核
     */
    @Override
    public Boolean updateByBo(ProjectAuditBo bo) {
        ProjectAudit update = MapstructUtils.convert(bo, ProjectAudit.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ProjectAudit entity) {
        // 做一些数据校验工作
    }

    /**
     * 批量删除项目审核
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验
        }
        return removeBatchByIds(ids);
    }

    /**
     * 审核项目
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean audit(ProjectAuditBo bo) {
        // 1. 创建审核记录
        ProjectAudit audit = MapstructUtils.convert(bo, ProjectAudit.class);
        audit.setAuditUser(LoginHelper.getUserId());
        audit.setAuditTime(new Date());
        
        // 2. 更新项目状态
        ProjectBo projectBo = new ProjectBo();
        projectBo.setProjectId(bo.getProjectId());
        projectBo.setStatus(bo.getAuditStatus());
        boolean updateResult = projectService.updateByBo(projectBo);
        
        if (!updateResult) {
            return false;
        }
        
        // 3. 保存审核记录
        return save(audit);
    }
}