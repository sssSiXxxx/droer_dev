package org.dromara.osc.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 贡献统计对象 os_contribution
 *
 * @author lmq
 * @date 2025-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("os_contribution")
public class Contribution extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 贡献ID
     */
    @TableId(value = "contribution_id")
    private Long contributionId;

    /**
     * 成员ID (对应数据库user_id字段)
     */
    @TableField("user_id")
    private Long memberId;

    /**
     * 项目ID
     */
    private Long projectId;

    /**
     * 成员名称（非数据库字段）
     */
    @TableField(exist = false)
    private String memberName;

    /**
     * 项目名称（非数据库字段）
     */
    @TableField(exist = false)
    private String projectName;

    /**
     * 贡献类型（0代码提交 1问题修复 2文档贡献 3回答问题 4其他）
     */
    private String contributionType;

    /**
     * 贡献内容
     */
    private String content;

    /**
     * 相关链接
     */
    private String url;

    /**
     * 贡献时间
     */
    private Date contributionTime;

    /**
     * 贡献点数
     */
    private Long points;


}
