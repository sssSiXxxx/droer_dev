package org.dromara.osc.service;

import org.dromara.osc.domain.ProjectAudit;
import org.dromara.osc.domain.bo.ProjectAuditBo;
import org.dromara.osc.domain.vo.ProjectAuditVo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 项目审核Service接口
 *
 * @author Lion Li
 * @date 2023-08-06
 */
public interface IProjectAuditService extends IService<ProjectAudit> {

    /**
     * 查询项目审核分页列表
     */
    TableDataInfo<ProjectAuditVo> queryPageList(ProjectAuditBo bo, PageQuery pageQuery);

    /**
     * 查询项目审核
     */
    ProjectAuditVo queryById(Long auditId);

    /**
     * 查询项目审核列表
     */
    List<ProjectAuditVo> queryList(ProjectAuditBo bo);

    /**
     * 新增项目审核
     */
    Boolean insertByBo(ProjectAuditBo bo);

    /**
     * 修改项目审核
     */
    Boolean updateByBo(ProjectAuditBo bo);

    /**
     * 校验并批量删除项目审核信息
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 审核项目
     */
    Boolean audit(ProjectAuditBo bo);
}