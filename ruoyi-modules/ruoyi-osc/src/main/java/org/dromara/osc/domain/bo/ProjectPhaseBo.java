package org.dromara.osc.domain.bo;

import org.dromara.osc.domain.ProjectPhase;
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
 * 进度追踪业务对象 os_project_phase
 *
 * @author lmq
 * @date 2025-08-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = ProjectPhase.class, reverseConvertGenerate = false)
public class ProjectPhaseBo extends BaseEntity {

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 阶段ID
     */
    private Long phaseId;

    /**
     * 阶段名称
     */
    @NotBlank(message = "阶段名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String phaseName;

    /**
     * 阶段代码
     */
    private String phaseCode;

    /**
     * 任务描述
     */
    @NotBlank(message = "任务描述不能为空", groups = { AddGroup.class, EditGroup.class })
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
