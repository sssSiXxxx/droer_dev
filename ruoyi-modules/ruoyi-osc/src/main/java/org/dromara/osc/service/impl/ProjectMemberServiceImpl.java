package org.dromara.osc.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.osc.domain.bo.ProjectMemberBo;
import org.dromara.osc.domain.vo.ProjectMemberVo;
import org.dromara.osc.domain.ProjectMember;
import org.dromara.osc.mapper.ProjectMemberMapper;
import org.dromara.osc.service.IProjectMemberService;

import java.util.List;
import java.util.Map;
import java.util.Collection;

/**
 * 人员项目管理Service业务层处理
 *
 * @author lmq
 * @date 2025-08-15
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectMemberServiceImpl implements IProjectMemberService {

    private final ProjectMemberMapper baseMapper;

    /**
     * 查询人员项目管理
     *
     * @param id 主键
     * @return 人员项目管理
     */
    @Override
    public ProjectMemberVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询人员项目管理列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 人员项目管理分页列表
     */
    @Override
    public TableDataInfo<ProjectMemberVo> queryPageList(ProjectMemberBo bo, PageQuery pageQuery) {
        ProjectMember query = MapstructUtils.convert(bo, ProjectMember.class);
        List<ProjectMemberVo> list = baseMapper.selectProjectMemberList(query);
        
        // 手动分页
        int start = (pageQuery.getPageNum() - 1) * pageQuery.getPageSize();
        int end = Math.min(start + pageQuery.getPageSize(), list.size());
        List<ProjectMemberVo> pageList = list.subList(start, end);
        
        Page<ProjectMemberVo> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        page.setRecords(pageList);
        page.setTotal(list.size());
        
        return TableDataInfo.build(page);
    }

    /**
     * 查询符合条件的人员项目管理列表
     *
     * @param bo 查询条件
     * @return 人员项目管理列表
     */
    @Override
    public List<ProjectMemberVo> queryList(ProjectMemberBo bo) {
        ProjectMember query = MapstructUtils.convert(bo, ProjectMember.class);
        return baseMapper.selectProjectMemberList(query);
    }

    private LambdaQueryWrapper<ProjectMember> buildQueryWrapper(ProjectMemberBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<ProjectMember> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(ProjectMember::getId);
        lqw.like(bo.getProjectId() != null, ProjectMember::getProjectId, bo.getProjectId());
        lqw.like(bo.getMemberId() != null, ProjectMember::getMemberId, bo.getMemberId());
        lqw.eq(StringUtils.isNotBlank(bo.getRole()), ProjectMember::getRole, bo.getRole());
        lqw.eq(bo.getJoinTime() != null, ProjectMember::getJoinTime, bo.getJoinTime());
        return lqw;
    }

    /**
     * 新增人员项目管理
     *
     * @param bo 人员项目管理
     * @return 是否新增成功
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
     * 修改人员项目管理
     *
     * @param bo 人员项目管理
     * @return 是否修改成功
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
    private void validEntityBeforeSave(ProjectMember entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除人员项目管理信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}
