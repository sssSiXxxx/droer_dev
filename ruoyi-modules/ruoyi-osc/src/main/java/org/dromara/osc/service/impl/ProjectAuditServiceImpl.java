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
import lombok.extern.slf4j.Slf4j;
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
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 项目审核Service业务层处理
 *
 * @author Lion Li
 * @date 2023-08-06
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ProjectAuditServiceImpl extends ServiceImpl<ProjectAuditMapper, ProjectAudit> implements IProjectAuditService {

    private final ProjectAuditMapper baseMapper;
    private final IProjectService projectService;

    /**
     * 查询项目审核分页列表
     */
    @Override
    public TableDataInfo<ProjectAuditVo> queryPageList(ProjectAuditBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ProjectAudit> lqw = buildQueryWrapper(bo);
        Page<ProjectAuditVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw, ProjectAuditVo.class);
        return TableDataInfo.build(result);
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
        
        // 由于需要关联查询项目名称，这里使用自定义SQL条件
        if (StringUtils.isNotBlank(bo.getProjectName())) {
            lqw.apply("EXISTS (SELECT 1 FROM os_project p WHERE p.project_id = os_project_audit.project_id AND p.project_name LIKE {0})", 
                     "%" + bo.getProjectName() + "%");
        }
        
        lqw.orderByDesc(ProjectAudit::getCreateTime);
        return lqw;
    }

    /**
     * 新增项目审核
     */
    @Override
    public Boolean insertByBo(ProjectAuditBo bo) {
        // 检查是否已存在该项目的审核记录
        LambdaQueryWrapper<ProjectAudit> checkWrapper = Wrappers.lambdaQuery();
        checkWrapper.eq(ProjectAudit::getProjectId, bo.getProjectId());
        ProjectAudit existingAudit = baseMapper.selectOne(checkWrapper);
        
        if (existingAudit != null) {
            // 如果已存在，则更新现有记录
            bo.setAuditId(existingAudit.getAuditId());
            return updateByBo(bo);
        } else {
            // 如果不存在，则插入新记录
            ProjectAudit add = MapstructUtils.convert(bo, ProjectAudit.class);
            
            // 自动设置审核人为当前用户
            if (add.getAuditUser() == null) {
                add.setAuditUser(LoginHelper.getUserId());
            }
            
            // 设置审核时间
            if (add.getAuditTime() == null) {
                add.setAuditTime(new Date());
            }
            
            validEntityBeforeSave(add);
            boolean flag = baseMapper.insert(add) > 0;
            if (flag) {
                bo.setAuditId(add.getAuditId());
            }
            return flag;
        }
    }

    /**
     * 修改项目审核
     */
    @Override
    public Boolean updateByBo(ProjectAuditBo bo) {
        ProjectAudit update = MapstructUtils.convert(bo, ProjectAudit.class);
        
        // 自动设置审核人为当前用户
        if (update.getAuditUser() == null) {
            update.setAuditUser(LoginHelper.getUserId());
        }
        
        // 设置审核时间
        if (update.getAuditTime() == null) {
            update.setAuditTime(new Date());
        }
        
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
        // 1. 检查是否已存在该项目的审核记录
        LambdaQueryWrapper<ProjectAudit> checkWrapper = Wrappers.lambdaQuery();
        checkWrapper.eq(ProjectAudit::getProjectId, bo.getProjectId());
        ProjectAudit existingAudit = baseMapper.selectOne(checkWrapper);
        
        // 2. 更新项目状态
        ProjectBo projectBo = new ProjectBo();
        projectBo.setProjectId(bo.getProjectId());
        
        // 审核状态映射：审核通过(1) -> 项目进行中(2)，审核驳回(2) -> 项目已驳回(5)
        String projectStatus;
        if ("1".equals(bo.getAuditStatus())) {
            projectStatus = "2"; // 审核通过 -> 进行中
        } else if ("2".equals(bo.getAuditStatus())) {
            projectStatus = "5"; // 审核驳回 -> 已驳回
        } else {
            projectStatus = bo.getAuditStatus(); // 其他状态保持不变
        }
        
        projectBo.setStatus(projectStatus);
        log.info("审核项目：projectId={}, auditStatus={}, projectStatus={}", 
                bo.getProjectId(), bo.getAuditStatus(), projectStatus);
        
        boolean updateResult = projectService.updateByBo(projectBo);
        
        if (!updateResult) {
            return false;
        }
        
        // 3. 处理审核记录
        if (existingAudit != null) {
            // 如果已存在，则更新现有记录
            bo.setAuditId(existingAudit.getAuditId());
            ProjectAudit update = MapstructUtils.convert(bo, ProjectAudit.class);
            update.setAuditUser(LoginHelper.getUserId());
            update.setAuditTime(new Date());
            return baseMapper.updateById(update) > 0;
        } else {
            // 如果不存在，则创建新记录
            ProjectAudit audit = MapstructUtils.convert(bo, ProjectAudit.class);
            audit.setAuditUser(LoginHelper.getUserId());
            audit.setAuditTime(new Date());
            return save(audit);
        }
    }
}