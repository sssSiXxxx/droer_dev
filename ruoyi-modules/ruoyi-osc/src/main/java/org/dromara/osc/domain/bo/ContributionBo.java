package org.dromara.osc.domain.bo;

import org.dromara.osc.domain.Contribution;
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
 * 贡献统计业务对象 os_contribution
 *
 * @author lmq
 * @date 2025-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Contribution.class, reverseConvertGenerate = false)
public class ContributionBo extends BaseEntity {
    /**
     * 贡献ID
     */
    @NotNull(message = "贡献ID不能为空", groups = { EditGroup.class })
    private Long contributionId;

    /**
     * 成员ID
     */
    @NotNull(message = "成员ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long memberId;

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID不能为空", groups = { AddGroup.class, EditGroup.class })
    private Long projectId;

    /**
     * 成员名称（查询条件）
     */
    private String memberName;

    /**
     * 项目名称（查询条件）
     */
    private String projectName;

    /**
     * 贡献类型（0代码提交 1问题修复 2文档贡献 3回答问题 4其他）
     */
    @NotBlank(message = "贡献类型（0代码提交 1问题修复 2文档贡献 3回答问题 4其他）不能为空", groups = { AddGroup.class, EditGroup.class })
    private String contributionType;

    /**
     * 贡献内容
     */
    @NotBlank(message = "贡献内容不能为空", groups = { AddGroup.class, EditGroup.class })
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
