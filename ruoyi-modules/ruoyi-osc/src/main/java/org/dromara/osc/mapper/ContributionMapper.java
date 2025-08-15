package org.dromara.osc.mapper;

import org.dromara.osc.domain.Contribution;
import org.dromara.osc.domain.vo.ContributionVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;

import java.util.List;

/**
 * 贡献统计Mapper接口
 *
 * @author lmq
 * @date 2025-08-15
 */
public interface ContributionMapper extends BaseMapperPlus<Contribution, ContributionVo> {

    /**
     * 查询贡献统计列表（包含成员名称和项目名称）
     *
     * @param contribution 贡献统计
     * @return 贡献统计集合
     */
    List<ContributionVo> selectContributionList(Contribution contribution);

}
