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
    @NotBlank(message = "项目编号不能为空", groups = { AddGroup.class, EditGroup.class })
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


}
