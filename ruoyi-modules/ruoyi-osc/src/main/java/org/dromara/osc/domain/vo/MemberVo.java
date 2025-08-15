package org.dromara.osc.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.osc.domain.Member;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 社区成员视图对象 os_member
 *
 * @author lmq
 * @date 2025-08-15
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Member.class)
public class MemberVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 成员ID
     */
    @ExcelProperty(value = "成员ID")
    private Long memberId;

    /**
     * 关联用户ID
     */
    @ExcelProperty(value = "关联用户ID")
    private Long userId;

    /**
     * 成员名称
     */
    @ExcelProperty(value = "成员名称")
    private String memberName;

    /**
     * 昵称
     */
    @ExcelProperty(value = "昵称")
    private String nickname;

    /**
     * 头像
     */
    @ExcelProperty(value = "头像")
    private String avatar;

    /**
     * 邮箱
     */
    @ExcelProperty(value = "邮箱")
    private String email;

    /**
     * GitHub ID
     */
    @ExcelProperty(value = "GitHub ID")
    private String githubId;

    /**
     * Gitee ID
     */
    @ExcelProperty(value = "Gitee ID")
    private String giteeId;

    /**
     * 状态（0正常 1禁用）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_normal_disable")
    private String status;

    /**
     * 加入时间
     */
    @ExcelProperty(value = "加入时间")
    private Date joinTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;

}
