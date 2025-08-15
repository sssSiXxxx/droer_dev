package org.dromara.osc.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 人员项目管理对象 os_project_member
 *
 * @author lmq
 * @date 2025-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("os_project_member")
public class ProjectMember extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id")
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
     * 项目角色
     */
    private String role;

    /**
     * 加入时间
     */
    private Date joinTime;


}
