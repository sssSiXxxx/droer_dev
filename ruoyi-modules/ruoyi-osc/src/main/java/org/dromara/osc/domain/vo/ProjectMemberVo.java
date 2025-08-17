package org.dromara.osc.domain.vo;

import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.common.translation.annotation.Translation;
import org.dromara.common.translation.constant.TransConstant;
import org.dromara.osc.domain.ProjectMember;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 项目成员关联视图对象 os_project_member
 *
 * @author lmq
 * @date 2025-08-17
 */
@Data
@AutoMapper(target = ProjectMember.class, reverseConvertGenerate = false)
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
    private Long projectId;

    /**
     * 成员ID
     */
    private Long memberId;

    /**
     * 角色
     */
    private String role;

    /**
     * 加入时间
     */
    private Date joinTime;

    /**
     * 权限级别
     */
    private Integer permissionLevel;

    /**
     * 是否活跃
     */
    private String isActive;

    /**
     * 贡献度评分
     */
    private Integer contributionScore;

    /**
     * 备注
     */
    private String remark;

    // 扩展字段，用于前端显示
    /**
     * 项目名称
     */
    private String projectName;

    /**
     * 项目代码
     */
    private String projectCode;

    /**
     * 成员名称
     */
    private String memberName;

    /**
     * 成员昵称
     */
    private String memberNickname;

    /**
     * 成员邮箱
     */
    private String memberEmail;

    /**
     * 成员头像
     */
    @Translation(type = TransConstant.OSS_ID_TO_URL, mapper = "memberAvatar", other = "")
    private String memberAvatar;

    /**
     * Gitee账号
     */
    private String giteeAccount;

    /**
     * GitHub账号
     */
    private String githubAccount;

    /**
     * 贡献记录数
     */
    private Integer contributionCount;

    /**
     * 总贡献点数
     */
    private Integer totalPoints;
}
