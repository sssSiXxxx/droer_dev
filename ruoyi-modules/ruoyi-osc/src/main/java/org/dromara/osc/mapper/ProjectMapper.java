package org.dromara.osc.mapper;

import org.dromara.osc.domain.Project;
import org.dromara.osc.domain.vo.ProjectVo;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

/**
 * 项目列表Mapper接口
 *
 * @author lmq
 * @date 2025-08-02
 */
@Mapper
public interface ProjectMapper extends BaseMapperPlus<Project, ProjectVo> {
    
    /**
     * 查询用户申请记录（关联审核表获取审核意见）
     */
    @Select("<script>" +
            "SELECT " +
            "p.project_id, p.project_name, p.project_code, p.description, p.repository_url, p.website_url, " +
            "p.tech_stack, p.contact_info, p.status, p.application_type, p.application_status, p.license, " +
            "p.application_reason, p.contribution, p.current_status, p.upgrade_reason, p.community_impact, " +
            "p.contact_email, p.contact_phone, p.remarks, p.star_count, p.fork_count, p.issues_count, p.pr_count, " +
            "p.create_time, p.update_time, p.create_by, p.update_by, " +
            "a.audit_opinion, a.audit_time, a.audit_user " +
            "FROM os_project p " +
            "LEFT JOIN os_project_audit a ON p.project_id = a.project_id " +
            "WHERE p.create_by = #{createBy} " +
            "AND p.application_status != 'deleted' " +
            "AND p.application_type IN ('personal', 'community') " +
            "<if test='projectName != null and projectName != \"\"'>" +
            "AND p.project_name LIKE CONCAT('%', #{projectName}, '%') " +
            "</if>" +
            "<if test='applicationType != null and applicationType != \"\"'>" +
            "AND p.application_type = #{applicationType} " +
            "</if>" +
            "<if test='applicationStatus != null and applicationStatus != \"\"'>" +
            "AND p.application_status = #{applicationStatus} " +
            "</if>" +
            "ORDER BY p.create_time DESC" +
            "</script>")
    List<ProjectVo> selectApplicationRecords(@Param("createBy") Long createBy, 
                                            @Param("projectName") String projectName,
                                            @Param("applicationType") String applicationType, 
                                            @Param("applicationStatus") String applicationStatus);

    /**
     * 分页查询用户申请记录（关联审核表获取审核意见）
     */
    @Select("<script>" +
            "SELECT " +
            "p.project_id, p.project_name, p.project_code, p.description, p.repository_url, p.website_url, " +
            "p.tech_stack, p.contact_info, p.status, p.application_type, p.application_status, p.license, " +
            "p.application_reason, p.contribution, p.current_status, p.upgrade_reason, p.community_impact, " +
            "p.contact_email, p.contact_phone, p.remarks, p.star_count, p.fork_count, p.issues_count, p.pr_count, " +
            "p.create_time, p.update_time, p.create_by, p.update_by, " +
            "a.audit_opinion, a.audit_time, a.audit_user " +
            "FROM os_project p " +
            "LEFT JOIN os_project_audit a ON p.project_id = a.project_id " +
            "WHERE p.create_by = #{createBy} " +
            "AND p.application_status != 'deleted' " +
            "AND p.application_type IN ('personal', 'community') " +
            "<if test='projectName != null and projectName != \"\"'>" +
            "AND p.project_name LIKE CONCAT('%', #{projectName}, '%') " +
            "</if>" +
            "<if test='applicationType != null and applicationType != \"\"'>" +
            "AND p.application_type = #{applicationType} " +
            "</if>" +
            "<if test='applicationStatus != null and applicationStatus != \"\"'>" +
            "AND p.application_status = #{applicationStatus} " +
            "</if>" +
            "ORDER BY p.create_time DESC" +
            "</script>")
    Page<ProjectVo> selectApplicationRecordsPage(IPage<ProjectVo> page,
                                                @Param("createBy") Long createBy, 
                                                @Param("projectName") String projectName,
                                                @Param("applicationType") String applicationType, 
                                                @Param("applicationStatus") String applicationStatus);
}
