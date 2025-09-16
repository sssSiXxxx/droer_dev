package org.dromara.osc.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.ibatis.annotations.Select;
import org.dromara.osc.domain.ProjectAudit;
import org.dromara.osc.domain.vo.ProjectAuditVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 项目审核Mapper接口
 *
 * @author lmq
 * @date 2025-08-07
 */
public interface ProjectAuditMapper extends BaseMapperPlus<ProjectAudit, ProjectAuditVo> {

    /**
     * 查询审核项目详情（关联项目表）
     * 只查询待审核状态的项目，不管是否有审核记录
     */
    @Select("SELECT " +
            "COALESCE(a.audit_id, 0) as audit_id, " +
            "p.project_id, " +
            "COALESCE(a.audit_status, '0') as audit_status, " +
            "a.audit_user, a.audit_time, a.audit_opinion, " +
            "COALESCE(a.create_time, p.create_time) as create_time, " +
            "p.project_name, p.project_code, p.description, p.repository_url, p.website_url, p.tech_stack, " +
            "p.contact_info, p.status, p.application_type, p.application_status, p.license, " +
            "p.application_reason, p.contribution, p.current_status, p.upgrade_reason, p.community_impact, " +
            "p.contact_email, p.contact_phone, p.remarks, p.star_count, p.fork_count, p.issues_count, p.pr_count " +
            "FROM os_project p " +
            "LEFT JOIN os_project_audit a ON p.project_id = a.project_id " +
            "WHERE p.application_status = 'pending' " +
            "AND p.application_type IN ('personal', 'community') " +
            "AND p.application_status != 'deleted' " +
            "ORDER BY COALESCE(a.create_time, p.create_time) DESC")
    List<ProjectAuditVo> selectPendingAuditProjects();

    /**
     * 根据条件查询审核项目详情（关联项目表）
     * 只查询待审核状态的项目，支持条件筛选
     */
    @Select("<script>" +
            "SELECT " +
            "COALESCE(a.audit_id, 0) as audit_id, " +
            "p.project_id, " +
            "COALESCE(a.audit_status, '0') as audit_status, " +
            "a.audit_user, a.audit_time, a.audit_opinion, " +
            "COALESCE(a.create_time, p.create_time) as create_time, " +
            "p.project_name, p.project_code, p.description, p.repository_url, p.website_url, p.tech_stack, " +
            "p.contact_info, p.status, p.application_type, p.application_status, p.license, " +
            "p.application_reason, p.contribution, p.current_status, p.upgrade_reason, p.community_impact, " +
            "p.contact_email, p.contact_phone, p.remarks, p.star_count, p.fork_count, p.issues_count, p.pr_count " +
            "FROM os_project p " +
            "LEFT JOIN os_project_audit a ON p.project_id = a.project_id " +
            "WHERE p.application_status = 'pending' " +
            "AND p.application_type IN ('personal', 'community') " +
            "AND p.application_status != 'deleted' " +
            "<if test='projectName != null and projectName != \"\"'>" +
            "AND p.project_name LIKE CONCAT('%', #{projectName}, '%') " +
            "</if>" +
            "<if test='applicationType != null and applicationType != \"\"'>" +
            "AND p.application_type = #{applicationType} " +
            "</if>" +
            "<if test='auditStatus != null and auditStatus != \"\"'>" +
            "AND COALESCE(a.audit_status, '0') = #{auditStatus} " +
            "</if>" +
            "ORDER BY COALESCE(a.create_time, p.create_time) DESC" +
            "</script>")
    List<ProjectAuditVo> selectAuditProjectsByCondition(@Param("projectName") String projectName, 
                                                        @Param("applicationType") String applicationType, 
                                                        @Param("auditStatus") String auditStatus);

    /**
     * 查询所有审核记录（用于审核记录页面）
     * 包括已审核通过、已拒绝的项目
     */
    @Select("<script>" +
            "SELECT " +
            "a.audit_id, " +
            "p.project_id, " +
            "a.audit_status, " +
            "a.audit_user, a.audit_time, a.audit_opinion, " +
            "a.create_time, " +
            "p.project_name, p.project_code, p.description, p.repository_url, p.website_url, p.tech_stack, " +
            "p.contact_info, p.status, p.application_type, p.application_status, p.license, " +
            "p.application_reason, p.contribution, p.current_status, p.upgrade_reason, p.community_impact, " +
            "p.contact_email, p.contact_phone, p.remarks, p.star_count, p.fork_count, p.issues_count, p.pr_count " +
            "FROM os_project_audit a " +
            "INNER JOIN os_project p ON a.project_id = p.project_id " +
            "WHERE a.audit_status IN ('1', '2') " +
            "AND p.application_type IN ('personal', 'community') " +
            "AND p.application_status != 'deleted' " +
            "<if test='projectName != null and projectName != \"\"'>" +
            "AND p.project_name LIKE CONCAT('%', #{projectName}, '%') " +
            "</if>" +
            "<if test='applicationType != null and applicationType != \"\"'>" +
            "AND p.application_type = #{applicationType} " +
            "</if>" +
            "<if test='auditStatus != null and auditStatus != \"\"'>" +
            "AND a.audit_status = #{auditStatus} " +
            "</if>" +
            "ORDER BY a.audit_time DESC" +
            "</script>")
    List<ProjectAuditVo> selectAllAuditRecords(@Param("projectName") String projectName, 
                                               @Param("applicationType") String applicationType, 
                                               @Param("auditStatus") String auditStatus);
}
