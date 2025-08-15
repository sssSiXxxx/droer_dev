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
import org.dromara.osc.domain.vo.ContributionVo;
import org.dromara.osc.domain.bo.ContributionBo;
import org.dromara.osc.service.IContributionService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 贡献统计
 *
 * @author lmq
 * @date 2025-08-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/osc/contribution")
public class ContributionController extends BaseController {

    private final IContributionService contributionService;

    /**
     * 查询贡献统计列表
     */
    @SaCheckPermission("osc:contribution:list")
    @GetMapping("/list")
    public TableDataInfo<ContributionVo> list(ContributionBo bo, PageQuery pageQuery) {
        return contributionService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出贡献统计列表
     */
    @SaCheckPermission("osc:contribution:export")
    @Log(title = "贡献统计", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(ContributionBo bo, HttpServletResponse response) {
        List<ContributionVo> list = contributionService.queryList(bo);
        ExcelUtil.exportExcel(list, "贡献统计", ContributionVo.class, response);
    }

    /**
     * 获取贡献统计详细信息
     *
     * @param contributionId 主键
     */
    @SaCheckPermission("osc:contribution:query")
    @GetMapping("/{contributionId}")
    public R<ContributionVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long contributionId) {
        return R.ok(contributionService.queryById(contributionId));
    }

    /**
     * 新增贡献统计
     */
    @SaCheckPermission("osc:contribution:add")
    @Log(title = "贡献统计", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody ContributionBo bo) {
        return toAjax(contributionService.insertByBo(bo));
    }

    /**
     * 修改贡献统计
     */
    @SaCheckPermission("osc:contribution:edit")
    @Log(title = "贡献统计", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody ContributionBo bo) {
        return toAjax(contributionService.updateByBo(bo));
    }

    /**
     * 删除贡献统计
     *
     * @param contributionIds 主键串
     */
    @SaCheckPermission("osc:contribution:remove")
    @Log(title = "贡献统计", businessType = BusinessType.DELETE)
    @DeleteMapping("/{contributionIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] contributionIds) {
        return toAjax(contributionService.deleteWithValidByIds(List.of(contributionIds), true));
    }
}
