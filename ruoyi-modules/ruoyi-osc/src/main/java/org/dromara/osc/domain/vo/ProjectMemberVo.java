package org.dromara.osc.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.osc.domain.ProjectMember;
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
 * 人员项目管理视图对象 os_project_member
 *
 * @author lmq
 * @date 2025-08-15
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = ProjectMember.class)
public class ProjectMemberVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    private Long id;

    /**
     * 项目ID
     */
    @ExcelProperty(value = "项目ID")
    private Long projectId;

    /**
     * 项目名称
     */
    @ExcelProperty(value = "项目名称")
    private String projectName;

    /**
     * 成员ID
     */
    @ExcelProperty(value = "成员ID")
    private Long memberId;

    /**
     * 成员名称
     */
    @ExcelProperty(value = "成员名称")
    private String memberName;

    /**
     * 项目角色
     */
    @ExcelProperty(value = "项目角色")
    private String role;

    /**
     * 加入时间
     */
    @ExcelProperty(value = "加入时间")
    private Date joinTime;


}
