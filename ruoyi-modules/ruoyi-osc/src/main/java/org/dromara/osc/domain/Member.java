package org.dromara.osc.domain;

import org.dromara.common.mybatis.core.domain.BaseEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 社区成员对象 os_member
 *
 * @author lmq
 * @date 2025-08-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("os_member")
public class Member extends BaseEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 成员ID
     */
    @TableId(value = "member_id")
    private Long memberId;

    /**
     * 关联用户ID
     */
    private Long userId;

    /**
     * 成员名称
     */
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
