package org.dromara.osc.controller;

import java.util.List;
import java.util.ArrayList;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.dromara.osc.domain.vo.ProjectVo;
import org.dromara.osc.domain.vo.ProjectImportVo;
import org.dromara.osc.domain.bo.ProjectBo;
import org.dromara.osc.service.IProjectService;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * 项目列表
 *
 * @author lmq
 * @date 2025-08-02
 */
@Validated
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/osc/project")
public class ProjectController extends BaseController {

    private final IProjectService projectService;

    /**
     * 查询项目列表列表
     */
    @SaCheckPermission("osc:project:list")
    @GetMapping("/list")
    public TableDataInfo<ProjectVo> list(ProjectBo bo, PageQuery pageQuery) {
        return projectService.queryPageList(bo, pageQuery);
    }

    /**
     * 查询项目列表（用于OSS文件上传，无需权限）
     */
    @GetMapping("/oss/list")
    public TableDataInfo<ProjectVo> listForOss(ProjectBo bo, PageQuery pageQuery) {
        return projectService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出项目列表列表
     */
    @SaCheckPermission("osc:project:export")
    @Log(title = "项目列表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProjectBo bo, HttpServletResponse response) {
        List<ProjectVo> list = projectService.queryList(bo);
        ExcelUtil.exportExcel(list, "项目列表", ProjectVo.class, response);
    }

    /**
     * 获取项目列表详细信息
     *
     * @param projectId 主键
     */
    @SaCheckPermission("osc:project:query")
    @GetMapping("/{projectId}")
    public R<ProjectVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long projectId) {
        return R.ok(projectService.queryById(projectId));
    }

    /**
     * 新增项目列表
     */
    @SaCheckPermission("osc:project:add")
    @Log(title = "项目列表", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProjectBo bo) {
        return toAjax(projectService.insertByBo(bo));
    }

    /**
     * 修改项目列表
     */
    @SaCheckPermission("osc:project:edit")
    @Log(title = "项目列表", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProjectBo bo) {
        return toAjax(projectService.updateByBo(bo));
    }

    /**
     * 删除项目列表
     *
     * @param projectIds 主键串
     */
    @SaCheckPermission("osc:project:remove")
    @Log(title = "项目列表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{projectIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] projectIds) {
        return toAjax(projectService.deleteWithValidByIds(List.of(projectIds), true));
    }

    /**
     * 导入项目数据
     */
    @Log(title = "项目列表", businessType = BusinessType.IMPORT)
    @RepeatSubmit()
    @PostMapping("/importData")
    public R<String> importData(MultipartFile file, boolean updateSupport) throws Exception {
        log.info("接收到导入请求，文件大小：{} bytes，updateSupport：{}", file.getSize(), updateSupport);
        try {
            projectService.importData(file, updateSupport);
            log.info("导入处理完成");
            return R.ok("导入成功");
        } catch (Exception e) {
            log.error("导入过程中发生错误：{}", e.getMessage(), e);
            return R.fail("导入失败：" + e.getMessage());
        }
    }

    /**
     * 下载导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new ArrayList<>(), "项目数据", ProjectImportVo.class, response);
    }
}
