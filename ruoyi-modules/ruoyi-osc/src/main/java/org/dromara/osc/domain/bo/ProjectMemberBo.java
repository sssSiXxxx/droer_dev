package org.dromara.osc.domain.bo;

import org.dromara.osc.domain.ProjectMember;
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
 * 人员项目管理业务对象 os_project_member
 *
 * @author lmq
 * @date 2025-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ProjectMember.class, reverseConvertGenerate = false)
public class ProjectMemberBo extends BaseEntity {
    /**
     * ID
     */
    private Long id;

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long projectId;

    /**
     * 成员ID
     */
    @NotNull(message = "成员ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long memberId;

    /**
     * 项目角色
     */
    @NotBlank(message = "项目角色不能为空", groups = { AddGroup.class, EditGroup.class })
    private String role;

    /**
     * 加入时间
     */
    private Date joinTime;

}
