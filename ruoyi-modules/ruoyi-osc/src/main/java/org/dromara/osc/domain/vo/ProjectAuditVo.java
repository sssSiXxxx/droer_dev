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
     * 项目网站
     */
    private String websiteUrl;

    /**
     * 技术栈
     */
    private String techStack;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 项目状态
     */
    private String status;

    /**
     * 申请类型 (personal: 个人项目, community: 社区项目)
     */
    private String applicationType;

    /**
     * 申请状态 (draft: 草稿, pending: 待审核, approved: 已通过, rejected: 已拒绝)
     */
    private String applicationStatus;

    /**
     * 开源协议
     */
    private String license;

    /**
     * 申请理由 (个人项目)
     */
    private String applicationReason;

    /**
     * 预期贡献 (个人项目)
     */
    private String contribution;

    /**
     * 项目现状 (个人项目)
     */
    private String currentStatus;

    /**
     * 升级理由 (社区项目)
     */
    private String upgradeReason;

    /**
     * 社区影响 (社区项目)
     */
    private String communityImpact;

    /**
     * 联系邮箱
     */
    private String contactEmail;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 备注信息
     */
    private String remarks;

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

    /**
     * 是否加入项目列表
     */
    private Boolean joinProjectList;
}
