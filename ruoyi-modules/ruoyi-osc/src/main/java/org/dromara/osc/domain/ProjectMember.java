package org.dromara.osc.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.Date;

/**
 * 项目成员关联对象 os_project_member
 *
 * @author lmq
 * @date 2025-08-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("os_project_member")
@AutoMapper(target = org.dromara.osc.domain.vo.ProjectMemberVo.class)
public class ProjectMember extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 成员ID
     */
    private Long memberId;

    /**
     * 角色（0普通成员 1项目负责人 2核心开发者 3维护者 4贡献者）
     */
    private String role;

    /**
     * 加入时间
     */
    private Date joinTime;

    /**
     * 权限级别（1-5，数字越大权限越高）
     */
    private Integer permissionLevel;

    /**
     * 是否活跃（0非活跃 1活跃）
     */
    private String isActive;

    /**
     * 贡献度评分（1-100）
     */
    private Integer contributionScore;

    /**
     * 备注
     */
    private String remark;
}
