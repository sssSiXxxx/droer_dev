package org.dromara.osc.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.springframework.stereotype.Service;
import org.dromara.osc.domain.bo.ProjectMemberBo;
import org.dromara.osc.domain.vo.ProjectMemberVo;
import org.dromara.osc.domain.ProjectMember;
import org.dromara.osc.mapper.ProjectMemberMapper;
import org.dromara.osc.service.IProjectMemberService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 项目成员关联Service业务层处理
 *
 * @author lmq
 * @date 2025-08-17
 */
@RequiredArgsConstructor
@Service
public class ProjectMemberServiceImpl implements IProjectMemberService {

    private final ProjectMemberMapper baseMapper;

    /**
     * 查询项目成员关联
     */
    @Override
    public ProjectMemberVo queryById(Long id) {
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询项目成员关联列表
     */
    @Override
    public TableDataInfo<ProjectMemberVo> queryPageList(ProjectMemberBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ProjectMember> lqw = Wrappers.lambdaQuery(ProjectMember.class);
        lqw.eq(bo.getProjectId() != null, ProjectMember::getProjectId, bo.getProjectId());
        lqw.eq(bo.getMemberId() != null, ProjectMember::getMemberId, bo.getMemberId());
        lqw.eq(StringUtils.isNotBlank(bo.getRole()), ProjectMember::getRole, bo.getRole());
        lqw.eq(StringUtils.isNotBlank(bo.getIsActive()), ProjectMember::getIsActive, bo.getIsActive());
        lqw.orderByDesc(ProjectMember::getCreateTime);

        Page<ProjectMemberVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询项目成员关联列表
     */
    @Override
    public List<ProjectMemberVo> queryList(ProjectMemberBo bo) {
        LambdaQueryWrapper<ProjectMember> lqw = Wrappers.lambdaQuery(ProjectMember.class);
        lqw.eq(bo.getProjectId() != null, ProjectMember::getProjectId, bo.getProjectId());
        lqw.eq(bo.getMemberId() != null, ProjectMember::getMemberId, bo.getMemberId());
        lqw.eq(StringUtils.isNotBlank(bo.getRole()), ProjectMember::getRole, bo.getRole());
        lqw.eq(StringUtils.isNotBlank(bo.getIsActive()), ProjectMember::getIsActive, bo.getIsActive());
        lqw.orderByDesc(ProjectMember::getCreateTime);

        return baseMapper.selectVoList(lqw);
    }

    /**
     * 新增项目成员关联
     */
    @Override
    public Boolean insertByBo(ProjectMemberBo bo) {
        ProjectMember add = MapstructUtils.convert(bo, ProjectMember.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改项目成员关联
     */
    @Override
    public Boolean updateByBo(ProjectMemberBo bo) {
        ProjectMember update = MapstructUtils.convert(bo, ProjectMember.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(ProjectMember entity) {
        // 检查项目成员关联是否已存在
        QueryWrapper<ProjectMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("project_id", entity.getProjectId());
        queryWrapper.eq("member_id", entity.getMemberId());
        if (entity.getId() != null) {
            queryWrapper.ne("id", entity.getId());
        }
        if (baseMapper.selectCount(queryWrapper) > 0) {
            throw new RuntimeException("该成员已在此项目中存在");
        }

        // 设置默认值
        if (entity.getJoinTime() == null) {
            entity.setJoinTime(LocalDateTime.now());
        }
        if (entity.getPermissionLevel() == null) {
            entity.setPermissionLevel(getDefaultPermissionLevel(entity.getRole()));
        }
        if (entity.getIsActive() == null) {
            entity.setIsActive("1");
        }
        if (entity.getContributionScore() == null) {
            entity.setContributionScore(0);
        }
    }

    /**
     * 批量删除项目成员关联信息
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 这里可以添加业务校验逻辑
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }

    /**
     * 根据项目ID查询项目成员列表
     */
    @Override
    public List<ProjectMemberVo> queryByProjectId(Long projectId) {
        LambdaQueryWrapper<ProjectMember> lqw = Wrappers.lambdaQuery(ProjectMember.class);
        lqw.eq(ProjectMember::getProjectId, projectId);
        lqw.orderByDesc(ProjectMember::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 根据成员ID查询参与的项目列表
     */
    @Override
    public List<ProjectMemberVo> queryByMemberId(Long memberId) {
        LambdaQueryWrapper<ProjectMember> lqw = Wrappers.lambdaQuery(ProjectMember.class);
        lqw.eq(ProjectMember::getMemberId, memberId);
        lqw.orderByDesc(ProjectMember::getCreateTime);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 批量添加项目成员
     */
    @Override
    public Boolean batchAddMembers(Long projectId, List<Long> memberIds, String role) {
        if (CollUtil.isEmpty(memberIds)) {
            return false;
        }

        List<ProjectMember> members = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (Long memberId : memberIds) {
            ProjectMember member = new ProjectMember();
            member.setProjectId(projectId);
            member.setMemberId(memberId);
            member.setRole(role);
            member.setPermissionLevel(getDefaultPermissionLevel(role));
            member.setIsActive("1");
            member.setContributionScore(0);
            member.setJoinTime(now);
            members.add(member);
        }

        // 批量插入
        for (ProjectMember member : members) {
            baseMapper.insert(member);
        }
        return true;
    }

    /**
     * 移除项目成员
     */
    @Override
    public Boolean removeMember(Long projectId, Long memberId) {
        LambdaQueryWrapper<ProjectMember> lqw = Wrappers.lambdaQuery(ProjectMember.class);
        lqw.eq(ProjectMember::getProjectId, projectId);
        lqw.eq(ProjectMember::getMemberId, memberId);
        return baseMapper.delete(lqw) > 0;
    }

    /**
     * 更新成员角色
     */
    @Override
    public Boolean updateMemberRole(Long projectId, Long memberId, String role) {
        LambdaQueryWrapper<ProjectMember> lqw = Wrappers.lambdaQuery(ProjectMember.class);
        lqw.eq(ProjectMember::getProjectId, projectId);
        lqw.eq(ProjectMember::getMemberId, memberId);
        
        ProjectMember update = new ProjectMember();
        update.setRole(role);
        update.setPermissionLevel(getDefaultPermissionLevel(role));
        
        return baseMapper.update(update, lqw) > 0;
    }

    /**
     * 更新成员活跃状态
     */
    @Override
    public Boolean updateMemberActiveStatus(Long projectId, Long memberId, String isActive) {
        LambdaQueryWrapper<ProjectMember> lqw = Wrappers.lambdaQuery(ProjectMember.class);
        lqw.eq(ProjectMember::getProjectId, projectId);
        lqw.eq(ProjectMember::getMemberId, memberId);
        
        ProjectMember update = new ProjectMember();
        update.setIsActive(isActive);
        
        return baseMapper.update(update, lqw) > 0;
    }

    /**
     * 计算成员贡献度评分
     */
    @Override
    public Integer calculateContributionScore(Long projectId, Long memberId) {
        // 这里可以实现复杂的贡献度计算逻辑
        // 暂时返回一个简单的评分
        return 75;
    }

    /**
     * 获取项目成员统计信息
     */
    @Override
    public Map<String, Object> getProjectMemberStats(Long projectId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 查询项目成员总数
        LambdaQueryWrapper<ProjectMember> lqw = Wrappers.lambdaQuery(ProjectMember.class);
        lqw.eq(ProjectMember::getProjectId, projectId);
        long totalMembers = baseMapper.selectCount(lqw);
        
        // 查询活跃成员数
        lqw.eq(ProjectMember::getIsActive, "1");
        long activeMembers = baseMapper.selectCount(lqw);
        
        // 按角色统计
        List<ProjectMemberVo> members = queryByProjectId(projectId);
        Map<String, Long> roleStats = new HashMap<>();
        for (ProjectMemberVo member : members) {
            String role = member.getRole();
            roleStats.put(role, roleStats.getOrDefault(role, 0L) + 1);
        }
        
        stats.put("totalMembers", totalMembers);
        stats.put("activeMembers", activeMembers);
        stats.put("roleStats", roleStats);
        
        return stats;
    }

    /**
     * 获取项目成员可视化数据
     */
    @Override
    public Map<String, Object> getProjectMemberVisualization(Long projectId) {
        Map<String, Object> visualization = new HashMap<>();
        
        List<ProjectMemberVo> members = queryByProjectId(projectId);
        
        // 角色分布数据
        Map<String, Long> roleDistribution = new HashMap<>();
        for (ProjectMemberVo member : members) {
            String role = member.getRole();
            roleDistribution.put(role, roleDistribution.getOrDefault(role, 0L) + 1);
        }
        
        // 活跃度数据
        long activeCount = members.stream()
            .filter(member -> "1".equals(member.getIsActive()))
            .count();
        
        visualization.put("roleDistribution", roleDistribution);
        visualization.put("activeCount", activeCount);
        visualization.put("totalCount", members.size());
        
        return visualization;
    }

    /**
     * 获取成员参与的项目数量统计
     */
    @Override
    public Map<String, Object> getMemberProjectStats(Long memberId) {
        Map<String, Object> stats = new HashMap<>();
        
        List<ProjectMemberVo> projects = queryByMemberId(memberId);
        
        // 参与项目总数
        long totalProjects = projects.size();
        
        // 活跃项目数（成员状态为活跃的项目）
        long activeProjects = projects.stream()
            .filter(project -> "1".equals(project.getIsActive()))
            .count();
        
        // 角色分布
        Map<String, Long> roleDistribution = new HashMap<>();
        for (ProjectMemberVo project : projects) {
            String role = project.getRole();
            roleDistribution.put(role, roleDistribution.getOrDefault(role, 0L) + 1);
        }
        
        stats.put("totalProjects", totalProjects);
        stats.put("activeProjects", activeProjects);
        stats.put("roleDistribution", roleDistribution);
        
        return stats;
    }

    /**
     * 根据角色获取默认权限级别
     */
    private Integer getDefaultPermissionLevel(String role) {
        switch (role) {
            case "0": return 5; // 项目负责人
            case "1": return 4; // 开发负责人
            case "2": return 3; // 开发人员
            case "3": return 2; // 测试人员
            case "4": return 1; // 观察者
            default: return 1;
        }
    }
}
