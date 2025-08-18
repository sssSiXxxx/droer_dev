package org.dromara.osc.domain.bo;

import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.core.validate.QueryGroup;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * 项目成员关联业务对象 os_project_member
 *
 * @author lmq
 * @date 2025-08-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ProjectMemberBo extends BaseEntity {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = { EditGroup.class })
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
     * 角色
     */
    @NotBlank(message = "角色不能为空", groups = { AddGroup.class, EditGroup.class })
    @Pattern(regexp = "^[0-4]$", message = "角色只能是0-4之间的数字", groups = { AddGroup.class, EditGroup.class })
    private String role;

    /**
     * 权限级别
     */
    @Min(value = 1, message = "权限级别最小为1", groups = { AddGroup.class, EditGroup.class })
    @Max(value = 5, message = "权限级别最大为5", groups = { AddGroup.class, EditGroup.class })
    private Integer permissionLevel;

    /**
     * 是否活跃
     */
    @Pattern(regexp = "^[01]$", message = "是否活跃只能是0或1", groups = { AddGroup.class, EditGroup.class })
    private String isActive;

    /**
     * 贡献度评分
     */
    @Min(value = 0, message = "贡献度评分最小为0", groups = { AddGroup.class, EditGroup.class })
    @Max(value = 100, message = "贡献度评分最大为100", groups = { AddGroup.class, EditGroup.class })
    private Integer contributionScore;

    /**
     * 加入时间
     */
    private LocalDateTime joinTime;

    /**
     * 备注
     */
    @Size(max = 500, message = "备注长度不能超过500个字符", groups = { AddGroup.class, EditGroup.class })
    private String remark;

    // 查询条件
    /**
     * 项目名称（模糊查询）
     */
    private String projectName;

    /**
     * 成员名称（模糊查询）
     */
    private String memberName;

    /**
     * 开始加入时间
     */
    private LocalDateTime beginJoinTime;

    /**
     * 结束加入时间
     */
    private LocalDateTime endJoinTime;

}
