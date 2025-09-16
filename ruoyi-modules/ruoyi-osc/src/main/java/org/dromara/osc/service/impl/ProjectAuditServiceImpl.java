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
 * @author lmq
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
            String applicationStatus = null;

            // 从params中获取参数
            Map<String, Object> params = bo.getParams();
            if (params != null) {
                if (params.containsKey("applicationType")) {
                    applicationType = (String) params.get("applicationType");
                }
                if (params.containsKey("applicationStatus")) {
                    applicationStatus = (String) params.get("applicationStatus");
                }
            }

            log.info("查询审核列表 - projectName: {}, auditStatus: {}, applicationType: {}, applicationStatus: {}",
                    projectName, auditStatus, applicationType, applicationStatus);

            List<ProjectAuditVo> allResults;

            // 判断查询类型：如果applicationStatus包含approved或rejected，说明是查询审核记录
            if (applicationStatus != null && (applicationStatus.contains("approved") || applicationStatus.contains("rejected"))) {
                // 查询审核记录：查询已经有审核结果的记录
                log.info("查询审核记录模式，applicationStatus: {}", applicationStatus);
                allResults = baseMapper.selectAllAuditRecords(projectName, applicationType, auditStatus);
                log.info("查询审核记录，结果数量: {}", allResults.size());
                // 打印前几条记录的详细信息
                if (allResults.size() > 0) {
                    for (int i = 0; i < Math.min(3, allResults.size()); i++) {
                        ProjectAuditVo vo = allResults.get(i);
                        log.info("审核记录 {}: projectId={}, projectName={}, auditStatus={}, applicationStatus={}, auditTime={}",
                                i, vo.getProjectId(), vo.getProjectName(), vo.getAuditStatus(), vo.getApplicationStatus(), vo.getAuditTime());
                    }
                }
            } else {
                // 查询待审核项目：查询pending状态的项目
                log.info("查询待审核项目模式");
                allResults = baseMapper.selectAuditProjectsByCondition(projectName, applicationType, auditStatus);
                log.info("查询待审核项目，结果数量: {}", allResults.size());
            }

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
        log.info("开始审核项目，projectId={}, auditStatus={}", bo.getProjectId(), bo.getAuditStatus());

        // 1. 检查是否已存在该项目的审核记录
        LambdaQueryWrapper<ProjectAudit> checkWrapper = Wrappers.lambdaQuery();
        checkWrapper.eq(ProjectAudit::getProjectId, bo.getProjectId());
        ProjectAudit existingAudit = baseMapper.selectOne(checkWrapper);

        // 2. 获取项目信息
        Project existingProject = projectMapper.selectById(bo.getProjectId());
        if (existingProject == null) {
            throw new RuntimeException("项目不存在，projectId=" + bo.getProjectId());
        }

        log.info("项目当前状态：applicationType={}, applicationStatus={}, status={}",
                existingProject.getApplicationType(), existingProject.getApplicationStatus(), existingProject.getStatus());

        // 3. 审核状态映射 - 修正状态映射逻辑
        String projectStatus;
        String applicationStatus;

        if ("1".equals(bo.getAuditStatus())) {
            // 审核通过
            projectStatus = "2";         // 进行中
            applicationStatus = "approved";
        } else if ("2".equals(bo.getAuditStatus())) {
            // 审核驳回
            projectStatus = "5";         // 已驳回
            applicationStatus = "rejected";
        } else if ("3".equals(bo.getAuditStatus())) {
            // 审核拒绝
            projectStatus = "5";         // 已驳回
            applicationStatus = "rejected";
        } else {
            // 其他状态保持待审核
            projectStatus = "1";         // 待审核
            applicationStatus = "pending";
        }

        log.info("审核后目标状态：projectStatus={}, applicationStatus={}", projectStatus, applicationStatus);

        // 4. 不同申请类型逻辑
        if ("community".equals(existingProject.getApplicationType())) {
            // 社区项目：只更新现有项目状态，不创建新项目
            existingProject.setStatus(projectStatus);
            existingProject.setApplicationStatus(applicationStatus);
            projectMapper.updateById(existingProject);

            log.info("社区项目审核完成：projectId={}, 最终状态={}({})",
                    bo.getProjectId(), projectStatus, applicationStatus);

        } else if ("personal".equals(existingProject.getApplicationType())) {
            // 个人项目：根据 joinProjectList 判断是否加入列表
            if ("1".equals(bo.getAuditStatus())) {
                // 审核通过
                if (Boolean.TRUE.equals(bo.getJoinProjectList())) {
                    // 通过且加入项目列表
                    existingProject.setStatus("2");  // 进行中
                    existingProject.setApplicationStatus("approved");
                    log.info("个人项目审核通过并加入项目列表：{}", existingProject.getProjectName());
                } else {
                    // 只通过不加入列表
                    existingProject.setStatus("2");  // 进行中但不在项目列表显示
                    existingProject.setApplicationStatus("approved");
                    log.info("个人项目审核通过但不加入项目列表：{}", existingProject.getProjectName());
                }
            } else {
                // 审核不通过
                existingProject.setStatus(projectStatus);
                existingProject.setApplicationStatus(applicationStatus);
                log.info("个人项目审核未通过：{}", existingProject.getProjectName());
            }
            projectMapper.updateById(existingProject);

        } else {
            // 其他类型或管理员直接创建的项目：直接更新
            existingProject.setStatus(projectStatus);
            existingProject.setApplicationStatus(applicationStatus);
            projectMapper.updateById(existingProject);
            log.info("其他类型项目审核完成：{}", existingProject.getProjectName());
        }

        // 5. 审核记录表处理 - 确保审核记录正确保存
        ProjectAudit auditRecord;
        if (existingAudit != null) {
            // 已存在则更新
            bo.setAuditId(existingAudit.getAuditId());
            auditRecord = MapstructUtils.convert(bo, ProjectAudit.class);
            auditRecord.setAuditId(existingAudit.getAuditId());
            auditRecord.setAuditUser(LoginHelper.getUserId());
            auditRecord.setAuditTime(new Date());
            boolean result = baseMapper.updateById(auditRecord) > 0;
            log.info("更新审核记录：auditId={}, result={}", existingAudit.getAuditId(), result);
            return result;
        } else {
            // 不存在则插入
            auditRecord = MapstructUtils.convert(bo, ProjectAudit.class);
            auditRecord.setAuditUser(LoginHelper.getUserId());
            auditRecord.setAuditTime(new Date());
            boolean result = baseMapper.insert(auditRecord) > 0;
            log.info("新增审核记录：projectId={}, result={}", bo.getProjectId(), result);
            return result;
        }
    }

}
