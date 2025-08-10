package org.dromara.osc.controller;

import java.util.List;

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
import org.dromara.osc.domain.vo.ProjectPhaseVo;
import org.dromara.osc.domain.bo.ProjectPhaseBo;
import org.dromara.osc.service.IProjectPhaseService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 进度追踪
 *
 * @author lmq
 * @date 2025-08-10
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/osc/projectPhase")
public class ProjectPhaseController extends BaseController {

    private final IProjectPhaseService projectPhaseService;

    /**
     * 查询进度追踪列表
     */
    @SaCheckPermission("osc:projectPhase:list")
    @GetMapping("/list")
    public TableDataInfo<ProjectPhaseVo> list(ProjectPhaseBo bo, PageQuery pageQuery) {
        return projectPhaseService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出进度追踪列表
     */
    @SaCheckPermission("osc:projectPhase:export")
    @Log(title = "进度追踪", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProjectPhaseBo bo, HttpServletResponse response) {
        List<ProjectPhaseVo> list = projectPhaseService.queryList(bo);
        ExcelUtil.exportExcel(list, "进度追踪", ProjectPhaseVo.class, response);
    }

    /**
     * 获取进度追踪详细信息
     *
     * @param phaseId 主键
     */
    @SaCheckPermission("osc:projectPhase:query")
    @GetMapping("/{phaseId}")
    public R<ProjectPhaseVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long phaseId) {
        return R.ok(projectPhaseService.queryById(phaseId));
    }

    /**
     * 新增进度追踪
     */
    @SaCheckPermission("osc:projectPhase:add")
    @Log(title = "进度追踪", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProjectPhaseBo bo) {
        return toAjax(projectPhaseService.insertByBo(bo));
    }

    /**
     * 修改进度追踪
     */
    @SaCheckPermission("osc:projectPhase:edit")
    @Log(title = "进度追踪", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProjectPhaseBo bo) {
        return toAjax(projectPhaseService.updateByBo(bo));
    }

    /**
     * 删除进度追踪
     *
     * @param phaseIds 主键串
     */
    @SaCheckPermission("osc:projectPhase:remove")
    @Log(title = "进度追踪", businessType = BusinessType.DELETE)
    @DeleteMapping("/{phaseIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] phaseIds) {
        return toAjax(projectPhaseService.deleteWithValidByIds(List.of(phaseIds), true));
    }
}
