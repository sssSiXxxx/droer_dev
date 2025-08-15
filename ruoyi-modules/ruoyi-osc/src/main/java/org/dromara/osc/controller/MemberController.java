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
import org.dromara.osc.domain.vo.MemberVo;
import org.dromara.osc.domain.bo.MemberBo;
import org.dromara.osc.service.IMemberService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 社区成员
 *
 * @author lmq
 * @date 2025-08-15
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/osc/member")
public class MemberController extends BaseController {

    private final IMemberService memberService;

    /**
     * 查询社区成员列表
     */
    @SaCheckPermission("osc:member:list")
    @GetMapping("/list")
    public TableDataInfo<MemberVo> list(MemberBo bo, PageQuery pageQuery) {
        return memberService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出社区成员列表
     */
    @SaCheckPermission("osc:member:export")
    @Log(title = "社区成员", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MemberBo bo, HttpServletResponse response) {
        List<MemberVo> list = memberService.queryList(bo);
        ExcelUtil.exportExcel(list, "社区成员", MemberVo.class, response);
    }

    /**
     * 获取社区成员详细信息
     *
     * @param memberId 主键
     */
    @SaCheckPermission("osc:member:query")
    @GetMapping("/{memberId}")
    public R<MemberVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long memberId) {
        return R.ok(memberService.queryById(memberId));
    }

    /**
     * 新增社区成员
     */
    @SaCheckPermission("osc:member:add")
    @Log(title = "社区成员", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MemberBo bo) {
        return toAjax(memberService.insertByBo(bo));
    }

    /**
     * 修改社区成员
     */
    @SaCheckPermission("osc:member:edit")
    @Log(title = "社区成员", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MemberBo bo) {
        return toAjax(memberService.updateByBo(bo));
    }

    /**
     * 删除社区成员
     *
     * @param memberIds 主键串
     */
    @SaCheckPermission("osc:member:remove")
    @Log(title = "社区成员", businessType = BusinessType.DELETE)
    @DeleteMapping("/{memberIds}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                               @PathVariable Long[] memberIds) {
        return toAjax(memberService.deleteWithValidByIds(List.of(memberIds), true));
    }
}
