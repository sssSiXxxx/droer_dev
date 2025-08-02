package org.dromara.osc.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;

import java.io.Serial;

/**
 * 项目列表对象 os_project
 *
 * @author lmq
 * @date 2025-08-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("os_project")
public class Project extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 项目ID
     */
    @TableId(value = "project_id")
    private Long projectId;

    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 项目描述
     */
    private String description;

    /**
     * 代码仓库
     */
    private String repositoryUrl;

    /**
     * 项目网站
     */
    private String websiteUrl;

    /**
     * 项目Logo
     */
    private String logoUrl;

    /**
     * 项目状态
     */
    private String status;

    /**
     * 备注
     */
    private String remark;


}
