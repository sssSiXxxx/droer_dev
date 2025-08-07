package org.dromara.osc.domain.bo;

import org.dromara.osc.domain.ProjectAudit;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 项目审核业务对象 os_project_audit
 *
 * @author lmq
 * @date 2025-08-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ProjectAudit.class, reverseConvertGenerate = false)
public class ProjectAuditBo extends BaseEntity {
    /**
     * 审核ID
     */
    @NotNull(message = "审核ID不能为空", groups = { EditGroup.class })
    private Long auditId;

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long projectId;

    /**
     * 审核状态（
     */
    @NotBlank(message = "审核状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String auditStatus;

    /**
     * 审核人
     */
    @NotNull(message = "审核人不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long auditUser;

    /**
     * 审核意见
     */
    @NotBlank(message = "审核意见不能为空", groups = { AddGroup.class, EditGroup.class })
    private String auditOpinion;

}
