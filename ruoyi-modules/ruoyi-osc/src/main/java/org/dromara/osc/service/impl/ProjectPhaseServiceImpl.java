package org.dromara.osc.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.osc.domain.bo.ProjectPhaseBo;
import org.dromara.osc.domain.vo.ProjectPhaseVo;
import org.dromara.osc.domain.ProjectPhase;
import org.dromara.osc.mapper.ProjectPhaseMapper;
import org.dromara.osc.service.IProjectPhaseService;

import java.util.*;
import java.util.stream.Collectors;

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
        lqw.orderByAsc(ProjectPhase::getStartTime);
        lqw.eq(bo.getProjectId() != null, ProjectPhase::getProjectId, bo.getProjectId());
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
        // 设置默认值
        if (add.getStatus() == null) {
            add.setStatus("0"); // 默认未开始
        }
        if (add.getProgress() == null) {
            add.setProgress(0); // 默认进度为0
        }
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
     * 为项目创建标准孵化阶段模板
     *
     * @param projectId 项目ID
     * @return 是否创建成功
     */
    @Override
    public Boolean createStandardPhases(Long projectId) {
        try {
            // 检查项目是否已存在阶段
            long existingCount = baseMapper.selectCount(
                Wrappers.<ProjectPhase>lambdaQuery()
                    .eq(ProjectPhase::getProjectId, projectId)
            );
            
            if (existingCount > 0) {
                log.warn("项目 {} 已存在阶段，跳过创建标准模板", projectId);
                return false;
            }
            
            // 标准开源项目孵化阶段
            List<ProjectPhase> standardPhases = Arrays.asList(
                createPhaseTemplate(projectId, "项目立项", "INIT", "确定项目目标、技术栈选择、初步需求分析", 1, 14, 1),
                createPhaseTemplate(projectId, "技术调研", "RESEARCH", "技术可行性分析、架构设计、依赖库调研", 2, 21, 2), 
                createPhaseTemplate(projectId, "原型开发", "PROTOTYPE", "核心功能原型开发、技术验证", 3, 28, 3),
                createPhaseTemplate(projectId, "MVP开发", "MVP", "最小可用产品开发、核心功能实现", 4, 42, 3),
                createPhaseTemplate(projectId, "Alpha测试", "ALPHA", "内部测试、Bug修复、功能完善", 5, 21, 2),
                createPhaseTemplate(projectId, "Beta测试", "BETA", "公开测试、用户反馈收集、性能优化", 6, 28, 2),
                createPhaseTemplate(projectId, "正式发布", "RELEASE", "文档完善、发布准备、上线部署", 7, 14, 3),
                createPhaseTemplate(projectId, "社区建设", "COMMUNITY", "用户社区建设、贡献者招募、生态扩展", 8, 90, 1),
                createPhaseTemplate(projectId, "持续维护", "MAINTAIN", "Bug修复、功能迭代、版本更新", 9, 365, 1)
            );
            
            // 计算每个阶段的实际开始和结束时间
            Date baseDate = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(baseDate);
            
            for (int i = 0; i < standardPhases.size(); i++) {
                ProjectPhase phase = standardPhases.get(i);
                
                // 设置开始时间
                phase.setStartTime(cal.getTime());
                
                // 设置结束时间（加上持续天数）
                cal.add(Calendar.DAY_OF_MONTH, getDurationDays(phase.getPhaseCode()));
                phase.setEndTime(cal.getTime());
                
                // 第一个阶段设为进行中，其他设为未开始
                if (i == 0) {
                    phase.setStatus("1");
                    phase.setProgress(10);
                    phase.setActualStartTime(baseDate);
                } else {
                    phase.setStatus("0");
                    phase.setProgress(0);
                }
                
                // 为下一个阶段准备（间隔1天）
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            // 批量插入阶段
            standardPhases.forEach(phase -> {
                baseMapper.insert(phase);
            });
            
            log.info("为项目 {} 创建了 {} 个标准孵化阶段", projectId, standardPhases.size());
            return true;
        } catch (Exception e) {
            log.error("创建标准阶段失败", e);
            return false;
        }
    }
    
    /**
     * 创建阶段模板
     */
    private ProjectPhase createPhaseTemplate(Long projectId, String phaseName, String phaseCode, 
                                           String description, int order, int durationDays, int priority) {
        ProjectPhase phase = new ProjectPhase();
        phase.setProjectId(projectId);
        phase.setPhaseName(phaseName);
        phase.setPhaseCode(phaseCode);
        phase.setDescription(description);
        phase.setPriority(priority);
        return phase;
    }
    
    /**
     * 根据阶段代码获取持续天数
     */
    private int getDurationDays(String phaseCode) {
        switch (phaseCode) {
            case "INIT": return 14;
            case "RESEARCH": return 21;
            case "PROTOTYPE": return 28;
            case "MVP": return 42;
            case "ALPHA": return 21;
            case "BETA": return 28;
            case "RELEASE": return 14;
            case "COMMUNITY": return 90;
            case "MAINTAIN": return 365;
            default: return 30;
        }
    }

    
    /**
     * 获取下一个阶段
     *
     * @param projectId 项目ID
     * @param currentPhaseId 当前阶段ID
     * @return 下一个阶段
     */
    @Override
    public ProjectPhaseVo getNextPhase(Long projectId, Long currentPhaseId) {
        // 获取当前阶段
        ProjectPhase currentPhase = baseMapper.selectById(currentPhaseId);
        if (currentPhase == null) {
            return null;
        }
        
        // 查找下一个阶段（按开始时间排序）
        List<ProjectPhase> phases = baseMapper.selectList(
            Wrappers.<ProjectPhase>lambdaQuery()
                .eq(ProjectPhase::getProjectId, projectId)
                .gt(ProjectPhase::getStartTime, currentPhase.getStartTime())
                .orderByAsc(ProjectPhase::getStartTime)
        );
        
        if (!phases.isEmpty()) {
            return MapstructUtils.convert(phases.get(0), ProjectPhaseVo.class);
        }
        return null;
    }

    /**
     * 自动推进到下一阶段
     *
     * @param phaseId 当前阶段ID
     * @return 是否成功
     */
    @Override
    public Boolean advanceToNextPhase(Long phaseId) {
        try {
            ProjectPhase currentPhase = baseMapper.selectById(phaseId);
            if (currentPhase == null || !"2".equals(currentPhase.getStatus())) {
                log.warn("阶段 {} 未完成，无法推进到下一阶段", phaseId);
                return false;
            }
            
            // 获取下一个阶段
            ProjectPhaseVo nextPhase = getNextPhase(currentPhase.getProjectId(), phaseId);
            if (nextPhase == null) {
                log.info("项目 {} 已完成所有阶段", currentPhase.getProjectId());
                return true;
            }
            
            // 启动下一个阶段
            return baseMapper.update(null,
                Wrappers.<ProjectPhase>lambdaUpdate()
                    .set(ProjectPhase::getStatus, "1")
                    .set(ProjectPhase::getActualStartTime, new Date())
                    .set(ProjectPhase::getProgress, 5)
                    .eq(ProjectPhase::getPhaseId, nextPhase.getPhaseId())
                    .eq(ProjectPhase::getStatus, "0") // 只能启动未开始的阶段
            ) > 0;
        } catch (Exception e) {
            log.error("推进到下一阶段失败", e);
            return false;
        }
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ProjectPhase entity){
        // 数据校验：开始时间不能晚于结束时间
        if (entity.getStartTime() != null && entity.getEndTime() != null) {
            if (entity.getStartTime().after(entity.getEndTime())) {
                throw new IllegalArgumentException("开始时间不能晚于结束时间");
            }
        }
        
        // 进度值校验
        if (entity.getProgress() != null) {
            if (entity.getProgress() < 0 || entity.getProgress() > 100) {
                throw new IllegalArgumentException("进度值必须在0-100之间");
            }
        }
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
            // 检查是否有正在进行的阶段
            long inProgressCount = baseMapper.selectCount(
                Wrappers.<ProjectPhase>lambdaQuery()
                    .in(ProjectPhase::getPhaseId, ids)
                    .eq(ProjectPhase::getStatus, "1")
            );
            if (inProgressCount > 0) {
                throw new IllegalStateException("无法删除正在进行的阶段");
            }
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 获取项目阶段统计数据
     *
     * @param projectId 项目ID
     * @return 统计数据
     */
    @Override
    public Map<String, Object> getPhaseStatistics(Long projectId) {
        List<ProjectPhase> phases = baseMapper.selectList(
            Wrappers.<ProjectPhase>lambdaQuery()
                .eq(ProjectPhase::getProjectId, projectId)
        );

        Map<String, Object> statistics = new HashMap<>();
        
        // 总阶段数
        int totalPhases = phases.size();
        statistics.put("totalPhases", totalPhases);
        
        if (totalPhases == 0) {
            // 如果没有阶段，返回默认值
            statistics.put("completedPhases", 0);
            statistics.put("inProgressPhases", 0);
            statistics.put("delayedPhases", 0);
            statistics.put("upcomingPhases", 0);
            statistics.put("completionRate", 0);
            statistics.put("averageDelay", 0);
            return statistics;
        }

        Date now = new Date();
        
        // 按状态分组统计
        Map<String, List<ProjectPhase>> groupedByStatus = phases.stream()
            .collect(Collectors.groupingBy(ProjectPhase::getStatus));

        int completedPhases = groupedByStatus.getOrDefault("2", Collections.emptyList()).size();
        int inProgressPhases = groupedByStatus.getOrDefault("1", Collections.emptyList()).size();
        int pausedPhases = groupedByStatus.getOrDefault("3", Collections.emptyList()).size();

        // 计算延期阶段数（结束时间已过但未完成的）
        long delayedPhases = phases.stream()
            .filter(p -> !"2".equals(p.getStatus()) && p.getEndTime() != null && p.getEndTime().before(now))
            .count();

        // 计算即将开始的阶段数（未来7天内开始的）
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        Date nextWeek = cal.getTime();
        
        long upcomingPhases = phases.stream()
            .filter(p -> "0".equals(p.getStatus()) && p.getStartTime() != null && 
                    p.getStartTime().after(now) && p.getStartTime().before(nextWeek))
            .count();

        // 计算完成率
        int completionRate = totalPhases > 0 ? (completedPhases * 100 / totalPhases) : 0;

        // 计算平均延期天数
        List<ProjectPhase> delayedPhasesList = phases.stream()
            .filter(p -> !"2".equals(p.getStatus()) && p.getEndTime() != null && p.getEndTime().before(now))
            .collect(Collectors.toList());
            
        int averageDelay = 0;
        if (!delayedPhasesList.isEmpty()) {
            long totalDelayDays = delayedPhasesList.stream()
                .mapToLong(p -> {
                    long diffInMillies = now.getTime() - p.getEndTime().getTime();
                    return diffInMillies / (24 * 60 * 60 * 1000);
                })
                .sum();
            averageDelay = (int) (totalDelayDays / delayedPhasesList.size());
        }

        statistics.put("completedPhases", completedPhases);
        statistics.put("inProgressPhases", inProgressPhases + pausedPhases);
        statistics.put("delayedPhases", (int) delayedPhases);
        statistics.put("upcomingPhases", (int) upcomingPhases);
        statistics.put("completionRate", completionRate);
        statistics.put("averageDelay", averageDelay);

        return statistics;
    }

    /**
     * 完成阶段
     *
     * @param phaseId 阶段ID
     * @return 是否成功
     */
    @Override
    public Boolean completePhase(Long phaseId) {
        return baseMapper.update(null,
            Wrappers.<ProjectPhase>lambdaUpdate()
                .set(ProjectPhase::getStatus, "2")
                .set(ProjectPhase::getProgress, 100)
                .set(ProjectPhase::getActualEndTime, new Date())
                .eq(ProjectPhase::getPhaseId, phaseId)
        ) > 0;
    }

    /**
     * 暂停阶段
     *
     * @param phaseId 阶段ID
     * @return 是否成功
     */
    @Override
    public Boolean pausePhase(Long phaseId) {
        return baseMapper.update(null,
            Wrappers.<ProjectPhase>lambdaUpdate()
                .set(ProjectPhase::getStatus, "3")
                .eq(ProjectPhase::getPhaseId, phaseId)
                .eq(ProjectPhase::getStatus, "1") // 只能暂停进行中的阶段
        ) > 0;
    }

    /**
     * 恢复阶段
     *
     * @param phaseId 阶段ID
     * @return 是否成功
     */
    @Override
    public Boolean resumePhase(Long phaseId) {
        return baseMapper.update(null,
            Wrappers.<ProjectPhase>lambdaUpdate()
                .set(ProjectPhase::getStatus, "1")
                .eq(ProjectPhase::getPhaseId, phaseId)
                .eq(ProjectPhase::getStatus, "3") // 只能恢复暂停的阶段
        ) > 0;
    }

    /**
     * 更新阶段进度
     *
     * @param phaseId  阶段ID
     * @param progress 进度百分比
     * @return 是否成功
     */
    @Override
    public Boolean updateProgress(Long phaseId, Integer progress) {
        if (progress < 0 || progress > 100) {
            throw new IllegalArgumentException("进度值必须在0-100之间");
        }

        LambdaUpdateWrapper<ProjectPhase> updateWrapper = Wrappers.<ProjectPhase>lambdaUpdate()
            .set(ProjectPhase::getProgress, progress)
            .eq(ProjectPhase::getPhaseId, phaseId);

        // 如果进度为100%，自动设置为已完成状态
        if (progress == 100) {
            updateWrapper.set(ProjectPhase::getStatus, "2")
                .set(ProjectPhase::getActualEndTime, new Date());
        } 
        // 如果进度大于0且状态为未开始，自动设置为进行中
        else if (progress > 0) {
            ProjectPhase phase = baseMapper.selectOne(
                Wrappers.<ProjectPhase>lambdaQuery()
                    .eq(ProjectPhase::getPhaseId, phaseId)
            );
            if (phase != null && "0".equals(phase.getStatus())) {
                updateWrapper.set(ProjectPhase::getStatus, "1")
                    .set(ProjectPhase::getActualStartTime, new Date());
            }
        }

        return baseMapper.update(null, updateWrapper) > 0;
    }
}
