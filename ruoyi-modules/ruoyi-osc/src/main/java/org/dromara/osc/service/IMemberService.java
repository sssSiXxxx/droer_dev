package org.dromara.osc.service;

import org.dromara.osc.domain.vo.MemberVo;
import org.dromara.osc.domain.bo.MemberBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 社区成员Service接口
 *
 * @author lmq
 * @date 2025-08-15
 */
public interface IMemberService {

    /**
     * 查询社区成员
     *
     * @param memberId 主键
     * @return 社区成员
     */
    MemberVo queryById(Long memberId);

    /**
     * 分页查询社区成员列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 社区成员分页列表
     */
    TableDataInfo<MemberVo> queryPageList(MemberBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的社区成员列表
     *
     * @param bo 查询条件
     * @return 社区成员列表
     */
    List<MemberVo> queryList(MemberBo bo);

    /**
     * 新增社区成员
     *
     * @param bo 社区成员
     * @return 是否新增成功
     */
    Boolean insertByBo(MemberBo bo);

    /**
     * 修改社区成员
     *
     * @param bo 社区成员
     * @return 是否修改成功
     */
    Boolean updateByBo(MemberBo bo);

    /**
     * 校验并批量删除社区成员信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

}
