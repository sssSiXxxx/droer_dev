package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.*;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.core.constant.SystemConstants;
import org.dromara.common.mybatis.core.domain.BaseEntity;

import java.util.Date;

/**
 * 用户对象 sys_user
 *
 * @author Lion Li
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("sys_user")
@AutoMapper(target = org.dromara.system.domain.vo.SysUserVo.class)
public class SysUser extends BaseEntity {

    /**
     * 用户ID
     */
    @TableId(value = "user_id")
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户账号
     */
    private String userName;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 用户类型（sys_user系统用户）
     */
    private String userType;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 手机号码
     */
    private String phonenumber;

    /**
     * Gitee账号
     */
    private String giteeAccount;

    /**
     * 用户性别
     */
    private String sex;

    /**
     * 用户头像
     */
    private Long avatar;

    /**
     * 密码
     */
    @TableField(
        insertStrategy = FieldStrategy.NOT_EMPTY,
        updateStrategy = FieldStrategy.NOT_EMPTY,
        whereStrategy = FieldStrategy.NOT_EMPTY
    )
    private String password;

    /**
     * 帐号状态（0正常 1停用）
     */
    private String status;

    /**
     * 删除标志（0代表存在 1代表删除）
     */
    @TableLogic
    private String delFlag;

    /**
     * 最后登录IP
     */
    private String loginIp;

    /**
     * 最后登录时间
     */
    private Date loginDate;

    /**
     * 备注
     */
    private String remark;

    /**
     * GitHub用户名
     */
    private String githubUsername;

    /**
     * 微信号
     */
    private String wechat;

    /**
     * QQ号
     */
    private String qq;

    /**
     * 个人博客
     */
    private String blog;

    /**
     * 公司/组织
     */
    private String company;

    /**
     * 职位
     */
    private String position;

    /**
     * 地区
     */
    private String location;

    /**
     * 个人简介
     */
    private String bio;

    /**
     * 技能标签
     */
    private String skills;

    /**
     * 工作年限
     */
    private Integer experienceYears;


    public SysUser(Long userId) {
        this.userId = userId;
    }

    public boolean isSuperAdmin() {
        return SystemConstants.SUPER_ADMIN_ID.equals(this.userId);
    }

}
