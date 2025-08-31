package org.dromara.osc.domain.bo;

import org.dromara.osc.domain.Project;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;
import java.util.Date;

/**
 * 项目列表业务对象 os_project
 *
 * @author lmq
 * @date 2025-08-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Project.class, reverseConvertGenerate = false)
public class ProjectBo extends BaseEntity {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空", groups = { EditGroup.class })
    private Long projectId;

    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String projectName;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 项目描述
     */
    @NotBlank(message = "项目描述不能为空", groups = { AddGroup.class, EditGroup.class })
    private String description;

    /**
     * 代码仓库
     */
    @NotBlank(message = "代码仓库不能为空", groups = { AddGroup.class, EditGroup.class })
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
    @NotBlank(message = "项目状态不能为空", groups = { AddGroup.class, EditGroup.class })
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


}
