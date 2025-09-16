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
import org.dromara.osc.domain.bo.MemberBo;
import org.dromara.osc.domain.vo.MemberVo;
import org.dromara.osc.domain.Member;
import org.dromara.osc.mapper.MemberMapper;
import org.dromara.osc.service.IMemberService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 社区成员Service业务层处理
 *
 * @author lmq
 * @date 2025-08-15
 */
@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements IMemberService {

    private final MemberMapper baseMapper;

    /**
     * 查询社区成员
     */
    @Override
    public MemberVo queryById(Long memberId) {
        return baseMapper.selectVoById(memberId);
    }

    /**
     * 分页查询社区成员列表
     */
    @Override
    public TableDataInfo<MemberVo> queryPageList(MemberBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Member> lqw = buildQueryWrapper(bo);
        Page<MemberVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的社区成员列表
     */
    @Override
    public List<MemberVo> queryList(MemberBo bo) {
        LambdaQueryWrapper<Member> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Member> buildQueryWrapper(MemberBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Member> lqw = Wrappers.lambdaQuery();
        lqw.eq(bo.getMemberId() != null, Member::getMemberId, bo.getMemberId());
        lqw.eq(bo.getUserId() != null, Member::getUserId, bo.getUserId());
        lqw.like(StringUtils.isNotBlank(bo.getMemberName()), Member::getMemberName, bo.getMemberName());
        lqw.like(StringUtils.isNotBlank(bo.getNickname()), Member::getNickname, bo.getNickname());
        lqw.like(StringUtils.isNotBlank(bo.getEmail()), Member::getEmail, bo.getEmail());
        lqw.like(StringUtils.isNotBlank(bo.getGithubId()), Member::getGithubId, bo.getGithubId());
        lqw.like(StringUtils.isNotBlank(bo.getGiteeId()), Member::getGiteeId, bo.getGiteeId());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), Member::getStatus, bo.getStatus());
        lqw.eq(bo.getJoinTime() != null, Member::getJoinTime, bo.getJoinTime());
        return lqw;
    }

    /**
     * 新增社区成员
     */
    @Override
    public Boolean insertByBo(MemberBo bo) {
        Member add = MapstructUtils.convert(bo, Member.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setMemberId(add.getMemberId());
        }
        return flag;
    }

    /**
     * 修改社区成员
     */
    @Override
    public Boolean updateByBo(MemberBo bo) {
        Member update = MapstructUtils.convert(bo, Member.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Member entity) {
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 批量删除社区成员
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if (isValid) {
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 根据用户ID查询社区成员信息
     */
    @Override
    public MemberVo queryByUserId(Long userId) {
        LambdaQueryWrapper<Member> lqw = Wrappers.lambdaQuery();
        lqw.eq(Member::getUserId, userId);
        return baseMapper.selectVoOne(lqw);
    }

    /**
     * 同步Gitee用户信息
     */
    @Override
    public Boolean syncGiteeInfo(MemberBo bo) {
        // 检查是否已存在该用户的成员记录
        LambdaQueryWrapper<Member> lqw = Wrappers.lambdaQuery();
        lqw.eq(Member::getUserId, bo.getUserId());
        Member existingMember = baseMapper.selectOne(lqw);
        
        if (existingMember != null) {
            // 更新现有记录
            existingMember.setGiteeId(bo.getGiteeId());
            existingMember.setAvatar(bo.getAvatar());
            existingMember.setMemberName(bo.getMemberName());
            existingMember.setNickname(bo.getNickname());
            existingMember.setEmail(bo.getEmail());
            existingMember.setStatus(bo.getStatus());
            return baseMapper.updateById(existingMember) > 0;
        } else {
            // 创建新记录
            Member newMember = MapstructUtils.convert(bo, Member.class);
            validEntityBeforeSave(newMember);
            return baseMapper.insert(newMember) > 0;
        }
    }
}
