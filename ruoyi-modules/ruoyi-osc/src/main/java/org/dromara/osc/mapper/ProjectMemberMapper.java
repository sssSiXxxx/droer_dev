package org.dromara.osc.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.osc.domain.ProjectMember;
import org.dromara.osc.domain.vo.ProjectMemberVo;

import java.util.List;

/**
 * 项目成员关联Mapper接口
 *
 * @author lmq
 * @date 2025-08-17
 */
public interface ProjectMemberMapper extends BaseMapperPlus<ProjectMember, ProjectMemberVo> {

    /**
     * 查询项目成员关联列表
     */
    List<ProjectMemberVo> queryList(@Param("bo") org.dromara.osc.domain.bo.ProjectMemberBo bo);

    /**
     * 分页查询项目成员关联列表
     */
    Page<ProjectMemberVo> queryPageList(@Param("page") Page<ProjectMemberVo> page, @Param("bo") org.dromara.osc.domain.bo.ProjectMemberBo bo);

    /**
     * 根据ID查询项目成员关联详情
     */
    ProjectMemberVo queryById(@Param("id") Long id);

    /**
     * 根据项目ID查询成员列表
     */
    List<ProjectMemberVo> queryByProjectId(@Param("projectId") Long projectId);

    /**
     * 根据成员ID查询参与的项目
     */
    List<ProjectMemberVo> queryByMemberId(@Param("memberId") Long memberId);

}
