package org.dromara.osc.mapper;

import org.dromara.osc.domain.ProjectMember;
import org.dromara.osc.domain.vo.ProjectMemberVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;

import java.util.List;

/**
 * 人员项目管理Mapper接口
 *
 * @author lmq
 * @date 2025-08-15
 */
@Mapper
public interface ProjectMemberMapper extends BaseMapperPlus<ProjectMember, ProjectMemberVo> {

    /**
     * 查询项目成员列表（包含项目名称和成员名称）
     */
    List<ProjectMemberVo> selectProjectMemberList(@Param("bo") ProjectMember bo);

}
