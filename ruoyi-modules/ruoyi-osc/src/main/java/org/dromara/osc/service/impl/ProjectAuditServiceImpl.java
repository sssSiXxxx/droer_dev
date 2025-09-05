package org.dromara.osc.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.ArrayList;

import com.baomidou.mybatisplus.extension.activerecord.AbstractModel;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.osc.domain.Project;
import org.dromara.osc.mapper.ProjectMapper;
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
    private final ProjectMapper projectMapper;
    private final IProjectService projectService;

    /**
     * 查询项目审核分页列表
     */
    @Override
    public TableDataInfo<ProjectAuditVo> queryPageList(ProjectAuditBo bo, PageQuery pageQuery) {
        try {
            // 提取查询参数
            String projectName = bo.getProjectName();
            String auditStatus = bo.getAuditStatus();
            String applicationType = null;
            
            // 从params中获取applicationType
            Map<String, Object> params = bo.getParams();
            if (params != null && params.containsKey("applicationType")) {
                applicationType = (String) params.get("applicationType");
            }

            // 使用自定义SQL查询，关联项目表获取完整数据
            List<ProjectAuditVo> allResults = baseMapper.selectAuditProjectsByCondition(projectName, applicationType, auditStatus);
            
            // 手动分页处理
            int pageNum = pageQuery.getPageNum();
            int pageSize = pageQuery.getPageSize();
            int total = allResults.size();
            int startIndex = (pageNum - 1) * pageSize;
            int endIndex = Math.min(startIndex + pageSize, total);
            
            List<ProjectAuditVo> pageResults = new ArrayList<>();
            if (startIndex < total) {
                pageResults = allResults.subList(startIndex, endIndex);
            }

            // 构建分页结果
            TableDataInfo<ProjectAuditVo> result = new TableDataInfo<>();
            result.setRows(pageResults);
            result.setTotal((long) total);
            result.setCode(200);
            result.setMsg("查询成功");
            
            return result;
            
        } catch (Exception e) {
            log.error("查询项目审核列表失败", e);
            TableDataInfo<ProjectAuditVo> errorResult = new TableDataInfo<>();
            errorResult.setMsg("查询失败：" + e.getMessage());
            errorResult.setCode(500);
            return errorResult;
        }
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

        // 2. 获取项目信息
        Project existingProject = projectMapper.selectById(bo.getProjectId());
        if (existingProject == null) {
            throw new RuntimeException("项目不存在，projectId=" + bo.getProjectId());
        }

        // 3. 审核状态映射
        String projectStatus;
        String applicationStatus;

        if ("1".equals(bo.getAuditStatus())) {
            projectStatus = "2";         // 审核通过 -> 进行中
            applicationStatus = "approved";
        } else if ("2".equals(bo.getAuditStatus())) {
            projectStatus = "5";         // 审核驳回 -> 已驳回
            applicationStatus = "rejected";
        } else {
            projectStatus = bo.getAuditStatus();
            applicationStatus = "pending";
        }

        // 4. 不同申请类型逻辑
        if ("community".equals(existingProject.getApplicationType())) {
            // 社区项目：只更新，不新增
            existingProject.setStatus(projectStatus);
            existingProject.setApplicationStatus(applicationStatus);
            projectMapper.updateById(existingProject);

            log.info("社区项目审核：projectId={}, 审核状态={}, applicationStatus={}",
                    bo.getProjectId(), bo.getAuditStatus(), applicationStatus);

        } else if ("personal".equals(existingProject.getApplicationType())) {
            // 个人项目：根据 joinProjectList 判断是否加入列表
            if ("1".equals(bo.getAuditStatus()) && Boolean.TRUE.equals(bo.getJoinProjectList())) {
                existingProject.setStatus("2");
                existingProject.setApplicationStatus("approved");
                projectMapper.updateById(existingProject);
                log.info("个人项目审核通过并加入项目列表：{}", existingProject.getProjectName());
            } else {
                existingProject.setStatus(projectStatus);
                existingProject.setApplicationStatus(applicationStatus);
                projectMapper.updateById(existingProject);
                log.info("个人项目审核未通过或未选择加入项目列表：{}", existingProject.getProjectName());
            }
        } else {
            // 其他类型：直接更新
            existingProject.setStatus(projectStatus);
            existingProject.setApplicationStatus(applicationStatus);
            projectMapper.updateById(existingProject);
            log.info("其他类型项目审核：{}", existingProject.getProjectName());
        }

        // 5. 审核记录表
        if (existingAudit != null) {
            // 已存在则更新
            bo.setAuditId(existingAudit.getAuditId());
            ProjectAudit update = MapstructUtils.convert(bo, ProjectAudit.class);
            update.setAuditUser(LoginHelper.getUserId());
            update.setAuditTime(new Date());
            return baseMapper.updateById(update) > 0;
        } else {
            // 不存在则插入
            ProjectAudit audit = MapstructUtils.convert(bo, ProjectAudit.class);
            audit.setAuditUser(LoginHelper.getUserId());
            audit.setAuditTime(new Date());
            return baseMapper.insert(audit) > 0;
        }
    }

}
