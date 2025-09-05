package org.dromara.osc.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 项目审核对象 os_project_audit
 *
 * @author lmq
 * @date 2025-08-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("os_project_audit")
public class ProjectAudit extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 审核ID
     */
    @TableId(value = "audit_id")
    private Long auditId;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 审核状态（0待审核 1通过 2拒绝 3退回修改）
     */
    private String auditStatus;

    /**
     * 审核人
     */
    private Long auditUser;

    /**
     * 审核时间
     */
    private Date auditTime;

    /**
     * 审核意见
     */
    private String auditOpinion;

    /**
     * 是否加入项目列表
     */
    private Boolean joinProjectList;

}
