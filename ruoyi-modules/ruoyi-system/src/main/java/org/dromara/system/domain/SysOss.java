package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.mybatis.core.domain.BaseEntity;

/**
 * OSS对象存储对象
 *
 * @author Lion Li
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_oss")
public class SysOss extends BaseEntity {

    /**
     * 对象存储主键
     */
    @TableId(value = "oss_id")
    private Long ossId;

    /**
     * 租户ID
     */
    private Long tenantId;

    /**
     * 所属项目ID
     */
    private Long projectId;

    /**
     * 所属项目名称（非数据库字段，用于显示）
     */
    private String projectName;

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 原名
     */
    private String originalName;

    /**
     * 文件后缀名
     */
    private String fileSuffix;

    /**
     * URL地址
     */
    private String url;

    /**
     * 扩展字段
     */
    private String ext1;

    /**
     * 服务商
     */
    private String service;

    /**
     * 文件大小（字节）
     */
    private Long size;

    /**
     * 文档类型（logo: Logo图片, requirement: 需求文档, help: 帮助文档, design: 设计文档, api: 接口文档, other: 其他文档）
     */
    private String fileType;

}
