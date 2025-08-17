package org.dromara.osc.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.dromara.osc.domain.bo.ProjectMemberBo;
import org.dromara.osc.domain.vo.ProjectMemberVo;
import org.dromara.osc.domain.ProjectMember;
import org.dromara.osc.mapper.ProjectMemberMapper;
import org.dromara.osc.service.IProjectMemberService;


import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;

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
     * 查询项目成员关联列表
     */
    @Override
    public TableDataInfo<ProjectMemberVo> queryPageList(ProjectMemberBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<ProjectMember> lqw = buildQueryWrapper(bo);
        Page<ProjectMemberVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询项目成员关联列表
     */
    @Override
    public List<ProjectMemberVo> queryList(ProjectMemberBo bo) {
        LambdaQueryWrapper<ProjectMember> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<ProjectMember> buildQueryWrapper(ProjectMemberBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ProjectMember> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getProjectId() != null, ProjectMember::getProjectId, bo.getProjectId());
        lqw.eq(bo.getMemberId() != null, ProjectMember::getMemberId, bo.getMemberId());
        lqw.eq(StringUtils.isNotBlank(bo.getRole()), ProjectMember::getRole, bo.getRole());
        lqw.eq(bo.getPermissionLevel() != null, ProjectMember::getPermissionLevel, bo.getPermissionLevel());
        lqw.eq(StringUtils.isNotBlank(bo.getIsActive()), ProjectMember::getIsActive, bo.getIsActive());
        lqw.eq(bo.getContributionScore() != null, ProjectMember::getContributionScore, bo.getContributionScore());
        lqw.between(params.get("beginJoinTime") != null && params.get("endJoinTime") != null,
            ProjectMember::getJoinTime, params.get("beginJoinTime"), params.get("endJoinTime"));
        return lqw;
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
        // 设置默认值
        if (entity.getJoinTime() == null) {
            entity.setJoinTime(new Date());
        }
        if (entity.getPermissionLevel() == null) {
            entity.setPermissionLevel(1);
        }
        if (StringUtils.isBlank(entity.getIsActive())) {
            entity.setIsActive("1");
        }
        if (entity.getContributionScore() == null) {
            entity.setContributionScore(0);
        }
    }

    /**
     * 批量删除项目成员关联
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            // 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteBatchIds(ids) > 0;
    }



    /**
     * 根据项目ID查询项目成员列表
     */
    @Override
    public List<ProjectMemberVo> queryByProjectId(Long projectId) {
        LambdaQueryWrapper<ProjectMember> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProjectMember::getProjectId, projectId);
        lqw.orderByDesc(ProjectMember::getJoinTime);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 根据成员ID查询参与的项目列表
     */
    @Override
    public List<ProjectMemberVo> queryByMemberId(Long memberId) {
        LambdaQueryWrapper<ProjectMember> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProjectMember::getMemberId, memberId);
        lqw.orderByDesc(ProjectMember::getJoinTime);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 批量添加项目成员
     */
    @Override
    public Boolean batchAddMembers(Long projectId, List<Long> memberIds, String role) {
        List<ProjectMember> members = new ArrayList<>();
        Date now = new Date();

        for (Long memberId : memberIds) {
            // 检查是否已存在
            LambdaQueryWrapper<ProjectMember> lqw = Wrappers.lambdaQuery();
            lqw.eq(ProjectMember::getProjectId, projectId);
            lqw.eq(ProjectMember::getMemberId, memberId);
            if (baseMapper.selectCount(lqw) > 0) {
                continue; // 跳过已存在的成员
            }

            ProjectMember member = new ProjectMember();
            member.setProjectId(projectId);
            member.setMemberId(memberId);
            member.setRole(role);
            member.setJoinTime(now);
            member.setPermissionLevel(getDefaultPermissionLevel(role));
            member.setIsActive("1");
            member.setContributionScore(0);
            members.add(member);
        }

        if (!members.isEmpty()) {
            return baseMapper.insertBatch(members);
        }
        return true;
    }

    /**
     * 移除项目成员
     */
    @Override
    public Boolean removeMember(Long projectId, Long memberId) {
        LambdaQueryWrapper<ProjectMember> lqw = Wrappers.lambdaQuery();
        lqw.eq(ProjectMember::getProjectId, projectId);
        lqw.eq(ProjectMember::getMemberId, memberId);
        return baseMapper.delete(lqw) > 0;
    }

    /**
     * 更新成员角色
     */
    @Override
    public Boolean updateMemberRole(Long projectId, Long memberId, String role) {
        LambdaQueryWrapper<ProjectMember> lqw = Wrappers.lambdaQuery();
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
        LambdaQueryWrapper<ProjectMember> lqw = Wrappers.lambdaQuery();
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
        // 基于贡献记录、代码提交、问题修复等
        return 50; // 默认返回50分
    }

    /**
     * 获取项目成员统计信息
     */
    @Override
    public Object getProjectMemberStats(Long projectId) {
        Map<String, Object> stats = new HashMap<>();

        // 总成员数
        LambdaQueryWrapper<ProjectMember> totalLqw = Wrappers.lambdaQuery();
        totalLqw.eq(ProjectMember::getProjectId, projectId);
        stats.put("totalMembers", baseMapper.selectCount(totalLqw));

        // 活跃成员数
        LambdaQueryWrapper<ProjectMember> activeLqw = Wrappers.lambdaQuery();
        activeLqw.eq(ProjectMember::getProjectId, projectId);
        activeLqw.eq(ProjectMember::getIsActive, "1");
        stats.put("activeMembers", baseMapper.selectCount(activeLqw));

        // 按角色统计
        List<ProjectMemberVo> members = queryByProjectId(projectId);
        Map<String, Long> roleStats = members.stream()
            .collect(java.util.stream.Collectors.groupingBy(
                ProjectMemberVo::getRole,
                java.util.stream.Collectors.counting()
            ));
        stats.put("roleStats", roleStats);

        return stats;
    }

    /**
     * 根据角色获取默认权限级别
     */
    private Integer getDefaultPermissionLevel(String role) {
        switch (role) {
            case "1": // 项目负责人
                return 5;
            case "2": // 核心开发者
                return 4;
            case "3": // 维护者
                return 3;
            case "4": // 贡献者
                return 2;
            default: // 普通成员
                return 1;
        }
    }
}
