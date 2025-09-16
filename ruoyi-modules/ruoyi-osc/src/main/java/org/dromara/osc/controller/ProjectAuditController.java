package org.dromara.osc.controller;

import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.osc.domain.vo.ProjectAuditVo;
import org.dromara.osc.domain.bo.ProjectAuditBo;
import org.dromara.osc.service.IProjectAuditService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.apache.commons.lang3.StringUtils;

/**
 * 项目审核
 *
 * @author lmq
 * @date 2025-08-07
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/osc/projectAudit")
public class ProjectAuditController extends BaseController {

    private final IProjectAuditService projectAuditService;

    /**
     * 查询项目审核列表
     */
    @SaCheckPermission("osc:projectAudit:list")
    @GetMapping("/list")
    public TableDataInfo<ProjectAuditVo> list(ProjectAuditBo bo, PageQuery pageQuery) {
        // 参数映射：将前端的applicationStatus映射为后端的auditStatus
        Map<String, Object> params = bo.getParams();
        if (params != null && params.containsKey("applicationStatus") && StringUtils.isBlank(bo.getAuditStatus())) {
            String applicationStatus = (String) params.get("applicationStatus");
            // 将applicationStatus状态映射为auditStatus
            if ("pending".equals(applicationStatus)) {
                bo.setAuditStatus("0"); // 待审核
            } else if ("approved".equals(applicationStatus)) {
                bo.setAuditStatus("1"); // 通过
            } else if ("rejected".equals(applicationStatus)) {
                bo.setAuditStatus("2"); // 拒绝
            } else if (applicationStatus != null && applicationStatus.contains("approved,rejected")) {
                // 审核记录查询，不设置auditStatus，让Service层判断
                // bo.setAuditStatus(null);
            }
        }
        return projectAuditService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出项目审核列表
     */
    @SaCheckPermission("osc:projectAudit:export")
    @Log(title = "项目审核", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProjectAuditBo bo, HttpServletResponse response) {
        List<ProjectAuditVo> list = projectAuditService.queryList(bo);
        ExcelUtil.exportExcel(list, "项目审核", ProjectAuditVo.class, response);
    }

    /**
     * 获取项目审核详细信息
     *
     * @param auditId 主键
     */
    @SaCheckPermission("osc:projectAudit:query")
    @GetMapping("/{auditId}")
    public R<ProjectAuditVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long auditId) {
        return R.ok(projectAuditService.queryById(auditId));
    }

    /**
     * 新增项目审核
     */
    @SaCheckPermission("osc:projectAudit:add")
    @Log(title = "项目审核", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProjectAuditBo bo) {
        return toAjax(projectAuditService.insertByBo(bo));
    }

    /**
     * 修改项目审核
     */
    @SaCheckPermission("osc:projectAudit:edit")
    @Log(title = "项目审核", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProjectAuditBo bo) {
        return toAjax(projectAuditService.updateByBo(bo));
    }

    /**
     * 删除项目审核
     *
     * @param auditIds 主键串
     */
    @SaCheckPermission("osc:projectAudit:remove")
    @Log(title = "项目审核", businessType = BusinessType.DELETE)
    @DeleteMapping("/{auditIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] auditIds) {
        return toAjax(projectAuditService.deleteWithValidByIds(List.of(auditIds), true));
    }

    /**
     * 审核项目
     */
    @SaCheckPermission("osc:projectAudit:audit")
    @Log(title = "项目审核", businessType = BusinessType.UPDATE)
    @PostMapping("/audit")
    public R<Void> audit(@RequestBody ProjectAuditBo bo) {
        return toAjax(projectAuditService.audit(bo));
    }
}
