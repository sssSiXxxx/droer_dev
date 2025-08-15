package org.dromara.osc.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.osc.domain.Contribution;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 贡献统计视图对象 os_contribution
 *
 * @author lmq
 * @date 2025-08-15
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Contribution.class)
public class ContributionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 成员ID
     */
    @ExcelProperty(value = "成员ID")
    private Long memberId;

    /**
     * 成员名称
     */
    @ExcelProperty(value = "成员名称")
    private String memberName;

    /**
     * 项目ID
     */
    @ExcelProperty(value = "项目ID")
    private Long projectId;

    /**
     * 项目名称
     */
    @ExcelProperty(value = "项目名称")
    private String projectName;

    /**
     * 贡献类型（0代码提交 1问题修复 2文档贡献 3回答问题 4其他）
     */
    @ExcelProperty(value = "贡献类型", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "osc_user_cbt")
    private String contributionType;

    /**
     * 贡献内容
     */
    @ExcelProperty(value = "贡献内容")
    private String content;

    /**
     * 贡献时间
     */
    @ExcelProperty(value = "贡献时间")
    private Date contributionTime;

    /**
     * 贡献点数
     */
    @ExcelProperty(value = "贡献点数")
    private Long points;


}
