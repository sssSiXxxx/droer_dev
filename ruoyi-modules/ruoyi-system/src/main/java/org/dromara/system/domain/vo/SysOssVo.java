package org.dromara.system.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.SysOss;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * OSS对象存储视图对象
 *
 * @author lmq
 */
@Data
@AutoMapper(target = SysOss.class)
public class SysOssVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 对象存储主键
     */
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

    /**
     * 创建部门
     */
    private Long createDept;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新者
     */
    private Long updateBy;

    /**
     * 更新时间
     */
    private Date updateTime;

}