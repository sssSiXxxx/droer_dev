package org.dromara.osc.domain.vo;

import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;
import org.dromara.osc.domain.Project;
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
 * 项目列表视图对象 os_project
 *
 * @author lmq
 * @date 2025-08-02
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = Project.class, reverseConvertGenerate = false)
public class ProjectVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    @ExcelProperty(value = "项目名称")
    private String projectName;

    /**
     * 项目编号
     */
    @ExcelProperty(value = "项目编号")
    private String projectCode;

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
     * 项目Logo
     */
    @ExcelProperty(value = "项目Logo")
    private String logoUrl;

    /**
     * 项目LogoUrl
     */
    @Translation(type = TransConstant.OSS_ID_TO_URL, mapper = "logoUrl", other = "")
    private String logoUrlUrl;
    /**
     * 项目状态
     */
    @ExcelProperty(value = "项目状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "osc_project_status", readConverterExp = "", separator = "")
    private String status;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
