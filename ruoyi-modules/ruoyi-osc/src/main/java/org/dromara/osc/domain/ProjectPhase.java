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
     * 状态（0进行中 1已完成 2已取消）
     */
    private String status;


}
