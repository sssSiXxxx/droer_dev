package org.dromara.osc.domain.vo;

import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 项目导入视图对象
 *
 * @author lmq
 * @date 2025-08-03
 */
@Data
public class ProjectImportVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目名称
     */
    @ExcelProperty(value = "项目名称")
    private String projectName;

    /**
     * 项目描述
     */
    @ExcelProperty(value = "项目描述")
    private String description;

    /**
     * 代码仓库
     */
    @ExcelProperty(value = "代码仓库")
    private String repositoryUrl;

    /**
     * 项目网站
     */
    @ExcelProperty(value = "项目网站")
    private String websiteUrl;

    /**
     * 项目状态
     */
    @ExcelProperty(value = "项目状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "osc_project_status")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;
}