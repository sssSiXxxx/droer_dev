package org.dromara.system.domain.bo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.dromara.common.core.constant.RegexConstants;
import org.dromara.common.core.xss.Xss;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.sensitive.annotation.Sensitive;
import org.dromara.common.sensitive.core.SensitiveStrategy;

/**
 * 个人信息业务处理
 *
 * @author Michelle.Chung
 */

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class SysUserProfileBo extends BaseEntity {

    /**
     * 用户昵称
     */
    @Xss(message = "用户昵称不能包含脚本字符")
    @Size(min = 0, max = 30, message = "用户昵称长度不能超过{max}个字符")
    private String nickName;

    /**
     * 用户邮箱
     */
    @Sensitive(strategy = SensitiveStrategy.EMAIL)
    @Email(message = "邮箱格式不正确")
    @Size(min = 0, max = 50, message = "邮箱长度不能超过{max}个字符")
    private String email;

    /**
     * 手机号码
     */
    @Pattern(regexp = RegexConstants.MOBILE, message = "手机号格式不正确")
    @Sensitive(strategy = SensitiveStrategy.PHONE)
    private String phonenumber;

    /**
     * 用户性别（0男 1女 2未知）
     */
    private String sex;

    /**
     * Gitee账号
     */
    @Size(max = 50, message = "Gitee账号长度不能超过{max}个字符")
    private String giteeAccount;

    /**
     * GitHub用户名
     */
    @Size(max = 100, message = "GitHub用户名长度不能超过{max}个字符")
    private String githubUsername;

    /**
     * 微信号
     */
    @Size(max = 50, message = "微信号长度不能超过{max}个字符")
    private String wechat;

    /**
     * QQ号
     */
    @Size(max = 20, message = "QQ号长度不能超过{max}个字符")
    private String qq;

    /**
     * 个人博客
     */
    @Size(max = 255, message = "个人博客链接长度不能超过{max}个字符")
    private String blog;

    /**
     * 公司/组织
     */
    @Size(max = 100, message = "公司名称长度不能超过{max}个字符")
    private String company;

    /**
     * 职位
     */
    @Size(max = 100, message = "职位名称长度不能超过{max}个字符")
    private String position;

    /**
     * 地区
     */
    @Size(max = 100, message = "地区名称长度不能超过{max}个字符")
    private String location;

    /**
     * 个人简介
     */
    @Size(max = 500, message = "个人简介长度不能超过{max}个字符")
    private String bio;

    /**
     * 技能标签
     */
    @Size(max = 500, message = "技能标签长度不能超过{max}个字符")
    private String skills;

    /**
     * 工作年限
     */
    private Integer experienceYears;

}
