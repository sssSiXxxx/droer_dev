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

/**
 * 项目成员关联业务对象 os_project_member
 *
 * @author lmq
 * @date 2025-08-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ProjectMember.class, reverseConvertGenerate = false)
public class ProjectMemberBo extends BaseEntity {

    /**
     * ID
     */
    @NotNull(message = "ID不能为空", groups = { EditGroup.class })
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
     * 角色（0普通成员 1项目负责人 2核心开发者 3维护者 4贡献者）
     */
    @NotBlank(message = "角色不能为空", groups = { AddGroup.class, EditGroup.class })
    private String role;

    /**
     * 加入时间
     */
    private Date joinTime;

    /**
     * 权限级别（1-5，数字越大权限越高）
     */
    @Min(value = 1, message = "权限级别最小为1", groups = { AddGroup.class, EditGroup.class })
    @Max(value = 5, message = "权限级别最大为5", groups = { AddGroup.class, EditGroup.class })
    private Integer permissionLevel;

    /**
     * 是否活跃（0非活跃 1活跃）
     */
    private String isActive;

    /**
     * 贡献度评分（1-100）
     */
    @Min(value = 1, message = "贡献度评分最小为1", groups = { AddGroup.class, EditGroup.class })
    @Max(value = 100, message = "贡献度评分最大为100", groups = { AddGroup.class, EditGroup.class })
    private Integer contributionScore;

    /**
     * 备注
     */
    private String remark;

    // 扩展字段，用于前端显示
    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 成员名称
     */
    private String memberName;

    /**
     * 成员邮箱
     */
    private String memberEmail;

    /**
     * 成员头像
     */
    private String memberAvatar;
}
