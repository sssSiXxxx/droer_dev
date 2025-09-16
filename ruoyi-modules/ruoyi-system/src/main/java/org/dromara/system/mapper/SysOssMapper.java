package org.dromara.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Select;
import org.dromara.common.mybatis.core.mapper.BaseMapperPlus;
import org.dromara.system.domain.SysOss;
import org.dromara.system.domain.vo.SysOssVo;

import java.util.List;

/**
 * 文件上传 数据层
 *
 * @author Lion Li
 */
public interface SysOssMapper extends BaseMapperPlus<SysOss, SysOssVo> {

    /**
     * 查询OSS文件列表并关联项目名称
     */
    @Select("SELECT o.*, p.project_name as projectName " +
            "FROM sys_oss o " +
            "LEFT JOIN os_project p ON o.project_id = p.project_id " +
            "ORDER BY o.create_time DESC")
    List<SysOssVo> selectOssWithProjectName();
    
    /**
     * 分页查询OSS文件列表并关联项目名称
     */
    @Select("SELECT o.*, p.project_name as projectName " +
            "FROM sys_oss o " +
            "LEFT JOIN os_project p ON o.project_id = p.project_id " +
            "${ew.customSqlSegment}")
    Page<SysOssVo> selectOssPageWithProjectName(Page<SysOssVo> page, LambdaQueryWrapper<SysOss> ew);
}
