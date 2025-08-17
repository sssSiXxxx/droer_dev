package org.dromara.osc.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.web.core.BaseController;
import org.dromara.osc.domain.bo.ProjectMemberBo;
import org.dromara.osc.domain.vo.ProjectMemberVo;
import org.dromara.osc.service.IProjectMemberService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 项目成员关联
 *
 * @author lmq
 * @date 2025-08-17
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/osc/projectMember")
public class ProjectMemberController extends BaseController {

    private final IProjectMemberService projectMemberService;

    /**
     * 查询项目成员关联列表
     */
    @SaCheckPermission("osc:projectMember:list")
    @GetMapping("/list")
    public TableDataInfo<ProjectMemberVo> list(ProjectMemberBo bo, PageQuery pageQuery) {
        return projectMemberService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出项目成员关联列表
     */
    @SaCheckPermission("osc:projectMember:export")
    @Log(title = "项目成员关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ProjectMemberBo bo, HttpServletResponse response) {
        List<ProjectMemberVo> list = projectMemberService.queryList(bo);
        ExcelUtil.exportExcel(list, "项目成员关联", ProjectMemberVo.class, response);
    }

    /**
     * 获取项目成员关联详细信息
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
     * 新增项目成员关联
     */
    @SaCheckPermission("osc:projectMember:add")
    @Log(title = "项目成员关联", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ProjectMemberBo bo) {
        return toAjax(projectMemberService.insertByBo(bo));
    }

    /**
     * 修改项目成员关联
     */
    @SaCheckPermission("osc:projectMember:edit")
    @Log(title = "项目成员关联", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ProjectMemberBo bo) {
        return toAjax(projectMemberService.updateByBo(bo));
    }

    /**
     * 删除项目成员关联
     *
     * @param ids 主键串
     */
    @SaCheckPermission("osc:projectMember:remove")
    @Log(title = "项目成员关联", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                         @PathVariable Long[] ids) {
        return toAjax(projectMemberService.deleteWithValidByIds(List.of(ids), true));
    }



    /**
     * 获取导入模板
     */
    @PostMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) {
        ExcelUtil.exportExcel(new java.util.ArrayList<>(), "项目成员关联", ProjectMemberVo.class, response);
    }

    /**
     * 根据项目ID查询项目成员列表
     */
    @SaCheckPermission("osc:projectMember:list")
    @GetMapping("/project/{projectId}")
    public R<List<ProjectMemberVo>> getProjectMembers(@NotNull(message = "项目ID不能为空")
                                                      @PathVariable Long projectId) {
        return R.ok(projectMemberService.queryByProjectId(projectId));
    }

    /**
     * 根据成员ID查询参与的项目列表
     */
    @SaCheckPermission("osc:projectMember:list")
    @GetMapping("/member/{memberId}")
    public R<List<ProjectMemberVo>> getMemberProjects(@NotNull(message = "成员ID不能为空")
                                                      @PathVariable Long memberId) {
        return R.ok(projectMemberService.queryByMemberId(memberId));
    }

    /**
     * 批量添加项目成员
     */
    @SaCheckPermission("osc:projectMember:add")
    @Log(title = "批量添加项目成员", businessType = BusinessType.INSERT)
    @PostMapping("/batchAdd")
    public R<Void> batchAddMembers(@RequestParam Long projectId,
                                   @RequestParam List<Long> memberIds,
                                   @RequestParam String role) {
        return toAjax(projectMemberService.batchAddMembers(projectId, memberIds, role));
    }

    /**
     * 移除项目成员
     */
    @SaCheckPermission("osc:projectMember:edit")
    @Log(title = "移除项目成员", businessType = BusinessType.UPDATE)
    @PostMapping("/removeMember")
    public R<Void> removeMember(@RequestParam Long projectId,
                                @RequestParam Long memberId) {
        return toAjax(projectMemberService.removeMember(projectId, memberId));
    }

    /**
     * 更新成员角色
     */
    @SaCheckPermission("osc:projectMember:edit")
    @Log(title = "更新成员角色", businessType = BusinessType.UPDATE)
    @PostMapping("/updateRole")
    public R<Void> updateMemberRole(@RequestParam Long projectId,
                                    @RequestParam Long memberId,
                                    @RequestParam String role) {
        return toAjax(projectMemberService.updateMemberRole(projectId, memberId, role));
    }

    /**
     * 更新成员活跃状态
     */
    @SaCheckPermission("osc:projectMember:edit")
    @Log(title = "更新成员活跃状态", businessType = BusinessType.UPDATE)
    @PostMapping("/updateActiveStatus")
    public R<Void> updateMemberActiveStatus(@RequestParam Long projectId,
                                            @RequestParam Long memberId,
                                            @RequestParam String isActive) {
        return toAjax(projectMemberService.updateMemberActiveStatus(projectId, memberId, isActive));
    }

    /**
     * 计算成员贡献度评分
     */
    @SaCheckPermission("osc:projectMember:query")
    @GetMapping("/contributionScore")
    public R<Integer> calculateContributionScore(@RequestParam Long projectId,
                                                @RequestParam Long memberId) {
        return R.ok(projectMemberService.calculateContributionScore(projectId, memberId));
    }

    /**
     * 获取项目成员统计信息
     */
    @SaCheckPermission("osc:projectMember:query")
    @GetMapping("/stats/{projectId}")
    public R<Object> getProjectMemberStats(@NotNull(message = "项目ID不能为空")
                                          @PathVariable Long projectId) {
        return R.ok(projectMemberService.getProjectMemberStats(projectId));
    }
}
