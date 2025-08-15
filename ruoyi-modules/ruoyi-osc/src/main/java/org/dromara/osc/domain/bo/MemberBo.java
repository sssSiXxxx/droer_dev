package org.dromara.osc.domain.bo;

import org.dromara.osc.domain.Member;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;

/**
 * 社区成员业务对象 os_member
 *
 * @author lmq
 * @date 2025-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = Member.class, reverseConvertGenerate = false)
public class MemberBo extends BaseEntity {

    /**
     * 成员ID
     */
    @NotNull(message = "成员ID不能为空", groups = { EditGroup.class })
    private Long memberId;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 成员名称
     */
    @NotBlank(message = "成员名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String memberName;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * GitHub ID
     */
    private String githubId;

    /**
     * Gitee ID
     */
    private String giteeId;

    /**
     * 状态（0正常 1禁用）
     */
    @NotBlank(message = "状态不能为空", groups = { AddGroup.class, EditGroup.class })
    private String status;

    /**
     * 加入时间
     */
    private Date joinTime;

    /**
     * 备注
     */
    private String remark;

}
