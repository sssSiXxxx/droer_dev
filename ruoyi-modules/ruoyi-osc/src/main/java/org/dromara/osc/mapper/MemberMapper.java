package org.dromara.osc.mapper;

import org.dromara.osc.domain.Member;
import org.dromara.osc.domain.vo.MemberVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;

/**
 * 社区成员Mapper接口
 *
 * @author lmq
 * @date 2025-08-15
 */
@Mapper
public interface MemberMapper extends BaseMapperPlus<Member, MemberVo> {

}
