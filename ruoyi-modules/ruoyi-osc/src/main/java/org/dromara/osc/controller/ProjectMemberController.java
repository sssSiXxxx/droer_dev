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
import org.dromara.osc.domain.vo.ProjectMemberVo;
import org.dromara.osc.domain.bo.ProjectMemberBo;
import org.dromara.osc.service.IProjectMemberService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 人员项目管理
 *
 * @author lmq
 * @date 2025-08-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/osc/projectMember")
public class ProjectMemberController extends BaseController {

    private final IProjectMemberService projectMemberService;

    /**
     * 查询人员项目管理列表
     */
    @SaCheckPermission("osc:projectMember:list")
    @GetMapping("/list")
    public TableDataInfo<ProjectMemberVo> list(ProjectMemberBo bo, PageQuery pageQuery) {
        return projectMemberService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出人员项目管理列表
     */
    @SaCheckPermission("osc:projectMember:export")
    @Log(title = "人员项目管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProjectMemberBo bo, HttpServletResponse response) {
        List<ProjectMemberVo> list = projectMemberService.queryList(bo);
        ExcelUtil.exportExcel(list, "人员项目管理", ProjectMemberVo.class, response);
    }

    /**
     * 获取人员项目管理详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("osc:projectMember:query")
    @GetMapping("/{id}")
    public R<ProjectMemberVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(projectMemberService.queryById(id));
    }

    /**
     * 新增人员项目管理
     */
    @SaCheckPermission("osc:projectMember:add")
    @Log(title = "人员项目管理", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProjectMemberBo bo) {
        return toAjax(projectMemberService.insertByBo(bo));
    }

    /**
     * 修改人员项目管理
     */
    @SaCheckPermission("osc:projectMember:edit")
    @Log(title = "人员项目管理", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProjectMemberBo bo) {
        return toAjax(projectMemberService.updateByBo(bo));
    }

    /**
     * 删除人员项目管理
     *
     * @param ids 主键串
     */
    @SaCheckPermission("osc:projectMember:remove")
    @Log(title = "人员项目管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(projectMemberService.deleteWithValidByIds(List.of(ids), true));
    }
}
