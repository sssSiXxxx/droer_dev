package org.dromara.osc.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;
import java.util.Date;

/**
 * 项目列表对象 os_project
 *
 * @author lmq
 * @date 2025-08-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("os_project")
@AutoMapper(target = org.dromara.osc.domain.vo.ProjectVo.class)
public class Project extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @TableId(value = "project_id", type = IdType.AUTO)
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 代码仓库
     */
    private String repositoryUrl;

    /**
     * 项目网站
     */
    private String websiteUrl;

    /**
     * 项目Logo
     */
    private String logoUrl;

    /**
     * 项目状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 技术栈
     */
    private String techStack;

    /**
     * 编程语言
     */
    private String programmingLanguage;



    /**
     * 核心贡献者
     */
    private String coreContributors;

    /**
     * 联系方式
     */
    private String contactInfo;

    /**
     * 版本信息
     */
    private String versionInfo;

    /**
     * Star数
     */
    private Integer starCount;

    /**
     * Fork数
     */
    private Integer forkCount;

    /**
     * Issues数
     */
    private Integer issuesCount;

    /**
     * PR数
     */
    private Integer prCount;

    /**
     * README链接
     */
    private String readmeUrl;

    /**
     * Wiki链接
     */
    private String wikiUrl;

    /**
     * API文档链接
     */
    private String apiDocUrl;

    /**
     * 最后提交时间
     */
    private Date lastCommitTime;

    /**
     * 项目负责人用户ID
     */
    private Long userId;

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
     * Watch数（关注数）
     */
    private Integer watchCount;

    /**
     * 项目大小（KB）
     */
    private String projectSize;

    /**
     * 最后同步时间
     */
    private Date lastSyncTime;

}
