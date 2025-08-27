package org.dromara.osc.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.osc.domain.ProjectMember;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 项目成员关联视图对象 os_project_member
 *
 * @author lmq
 * @date 2025-08-17
 */
@Data
@AutoMapper(target = ProjectMember.class)
public class ProjectMemberVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目代码
     */
    private String projectCode;

    /**
     * 成员ID
     */
    private Long memberId;

    /**
     * 成员名称
     */
    private String memberName;

    /**
     * 成员邮箱
     */
    private String memberEmail;

    /**
     * 角色
     */
    private String role;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 权限级别
     */
    private Integer permissionLevel;

    /**
     * 是否活跃
     */
    private String isActive;

    /**
     * 贡献度评分
     */
    private Integer contributionScore;

    /**
     * 加入时间
     */
    private LocalDateTime joinTime;

    /**
     * 备注
     */
    private String remark;

}
