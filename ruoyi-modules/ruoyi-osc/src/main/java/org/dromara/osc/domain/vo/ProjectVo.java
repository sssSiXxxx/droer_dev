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

    /**
     * 技术栈
     */
    @ExcelProperty(value = "技术栈")
    private String techStack;

    /**
     * 编程语言
     */
    @ExcelProperty(value = "编程语言")
    private String programmingLanguage;



    /**
     * 核心贡献者
     */
    @ExcelProperty(value = "核心贡献者")
    private String coreContributors;

    /**
     * 联系方式
     */
    @ExcelProperty(value = "联系方式")
    private String contactInfo;

    /**
     * 版本信息
     */
    @ExcelProperty(value = "版本信息")
    private String versionInfo;

    /**
     * Star数
     */
    @ExcelProperty(value = "Star数")
    private Integer starCount;

    /**
     * Fork数
     */
    @ExcelProperty(value = "Fork数")
    private Integer forkCount;

    /**
     * Issues数
     */
    @ExcelProperty(value = "Issues数")
    private Integer issuesCount;

    /**
     * PR数
     */
    @ExcelProperty(value = "PR数")
    private Integer prCount;

    /**
     * README链接
     */
    @ExcelProperty(value = "README链接")
    private String readmeUrl;

    /**
     * Wiki链接
     */
    @ExcelProperty(value = "Wiki链接")
    private String wikiUrl;

    /**
     * API文档链接
     */
    @ExcelProperty(value = "API文档链接")
    private String apiDocUrl;

    /**
     * 最后提交时间
     */
    @ExcelProperty(value = "最后提交时间")
    private Date lastCommitTime;

    /**
     * 项目负责人用户ID
     */
    private Long userId;

    /**
     * 申请类型 (personal: 个人项目, community: 社区项目)
     */
    @ExcelProperty(value = "申请类型")
    private String applicationType;

    /**
     * 申请状态 (draft: 草稿, pending: 待审核, approved: 已通过, rejected: 已拒绝)
     */
    @ExcelProperty(value = "申请状态")
    private String applicationStatus;

    /**
     * 开源协议
     */
    @ExcelProperty(value = "开源协议")
    private String license;

    /**
     * 申请理由 (个人项目)
     */
    @ExcelProperty(value = "申请理由")
    private String applicationReason;

    /**
     * 预期贡献 (个人项目)
     */
    @ExcelProperty(value = "预期贡献")
    private String contribution;

    /**
     * 项目现状 (个人项目)
     */
    @ExcelProperty(value = "项目现状")
    private String currentStatus;

    /**
     * 升级理由 (社区项目)
     */
    @ExcelProperty(value = "升级理由")
    private String upgradeReason;

    /**
     * 社区影响 (社区项目)
     */
    @ExcelProperty(value = "社区影响")
    private String communityImpact;

    /**
     * 联系邮箱
     */
    @ExcelProperty(value = "联系邮箱")
    private String contactEmail;

    /**
     * 联系电话
     */
    @ExcelProperty(value = "联系电话")
    private String contactPhone;

    /**
     * 备注信息
     */
    @ExcelProperty(value = "备注信息")
    private String remarks;

    /**
     * 创建部门
     */
    private Long createDept;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    @ExcelProperty(value = "创建时间")
    private Date createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    @ExcelProperty(value = "更新时间")
    private Date updateTime;

    /**
     * Watch数
     */
    @ExcelProperty(value = "Watch数")
    private Integer watchCount;

    /**
     * 项目大小
     */
    @ExcelProperty(value = "项目大小")
    private String projectSize;

    /**
     * 最后同步时间
     */
    @ExcelProperty(value = "最后同步时间")
    private Date lastSyncTime;

    /**
     * 审核意见
     */
    @ExcelProperty(value = "审核意见")
    private String auditOpinion;

    /**
     * 审核时间
     */
    @ExcelProperty(value = "审核时间")
    private Date auditTime;

    /**
     * 审核人
     */
    @ExcelProperty(value = "审核人")
    private Long auditUser;

}
