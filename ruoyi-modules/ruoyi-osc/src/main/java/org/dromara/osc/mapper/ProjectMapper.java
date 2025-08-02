package org.dromara.osc.mapper;

import org.dromara.osc.domain.Project;
import org.dromara.osc.domain.vo.ProjectVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目列表Mapper接口
 *
 * @author lmq
 * @date 2025-08-02
 */
@Mapper
public interface ProjectMapper extends BaseMapperPlus<Project, ProjectVo> {

}
