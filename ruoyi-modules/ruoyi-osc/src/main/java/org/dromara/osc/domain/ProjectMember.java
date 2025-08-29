package org.dromara.osc.domain;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * 项目成员关联对象 os_project_member
 *
 * @author lmq
 * @date 2025-08-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("os_project_member")
public class ProjectMember extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Long id;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 成员ID (对应数据库user_id字段)
     */
    @TableField("user_id")
    private Long memberId;

    /**
     * 角色 (0:普通成员, 1:项目负责人, 2:核心开发者, 3:维护者, 4:贡献者)
     */
    private String role;

    /**
     * 权限级别 (1-5)
     */
    private Integer permissionLevel;

    /**
     * 是否活跃 (0:非活跃, 1:活跃)
     */
    private String isActive;

    /**
     * 贡献度评分 (0-100)
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
