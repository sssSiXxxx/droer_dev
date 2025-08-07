package org.dromara.osc.domain.vo;

import org.dromara.osc.domain.ProjectAudit;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 项目审核视图对象
 *
 * @author Lion Li
 * @date 2023-08-06
 */
@Data
@AutoMapper(target = ProjectAudit.class)
public class ProjectAuditVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 审核ID
     */
    private Long auditId;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目代号
     */
    private String projectCode;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 仓库地址
     */
    private String repositoryUrl;

    /**
     * 技术栈
     */
    private String techStack;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 审核状态
     */
    @ExcelDictFormat(dictType = "project_audit_status")
    private String auditStatus;

    /**
     * 审核人
     */
    private Long auditUser;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核意见
     */
    private String auditOpinion;
}