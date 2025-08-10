package org.dromara.osc.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.osc.domain.ProjectPhase;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 进度追踪视图对象 os_project_phase
 *
 * @author lmq
 * @date 2025-08-10
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ProjectPhase.class)
public class ProjectPhaseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @ExcelProperty(value = "项目ID")
    private Long projectId;

    /**
     * 阶段名称
     */
    @ExcelProperty(value = "阶段名称")
    private String phaseName;

    /**
     * 阶段代码
     */
    @ExcelProperty(value = "阶段代码")
    private String phaseCode;

    /**
     * 任务描述
     */
    @ExcelProperty(value = "任务描述")
    private String description;

    /**
     * 开始时间
     */
    @ExcelProperty(value = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @ExcelProperty(value = "结束时间")
    private Date endTime;

    /**
     * 状态（0进行中 1已完成 2已取消）
     */
    @ExcelProperty(value = "状态", converter = ExcelDictConvert.class)
    @ExcelDictFormat(dictType = "sys_phase_status")
    private String status;


}
