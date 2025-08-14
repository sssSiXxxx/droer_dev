package org.dromara.osc.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 进度追踪对象 os_project_phase
 *
 * @author lmq
 * @date 2025-08-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("os_project_phase")
public class ProjectPhase extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 阶段ID
     */
    @TableId(value = "phase_id")
    private Long phaseId;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 阶段名称
     */
    private String phaseName;

    /**
     * 阶段代码
     */
    private String phaseCode;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 状态（0未开始 1进行中 2已完成 3已暂停 4已延期）
     */
    private String status;

    /**
     * 进度百分比（0-100）
     */
    private Integer progress;

    /**
     * 实际开始时间
     */
    private Date actualStartTime;

    /**
     * 实际结束时间
     */
    private Date actualEndTime;

    /**
     * 负责人ID
     */
    private Long ownerId;

    /**
     * 优先级（1低 2中 3高）
     */
    private Integer priority;
}
