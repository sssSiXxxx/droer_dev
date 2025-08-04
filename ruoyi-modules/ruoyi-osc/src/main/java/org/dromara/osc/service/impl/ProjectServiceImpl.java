package org.dromara.osc.service.impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.osc.domain.bo.ProjectBo;
import org.dromara.osc.domain.vo.ProjectVo;
import org.dromara.osc.domain.vo.ProjectImportVo;
import org.dromara.osc.domain.Project;
import org.dromara.osc.mapper.ProjectMapper;
import org.dromara.osc.service.IProjectService;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import org.springframework.web.multipart.MultipartFile;
import org.dromara.common.excel.utils.ExcelUtil;

/**
 * 项目列表Service业务层处理
 *
 * @author lmq
 * @date 2025-08-02
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class ProjectServiceImpl implements IProjectService {

    private final ProjectMapper baseMapper;

    /**
     * 查询项目列表
     *
     * @param projectId 主键
     * @return 项目列表
     */
    @Override
    public ProjectVo queryById(Long projectId){
        return baseMapper.selectVoById(projectId);
    }

    /**
     * 分页查询项目列表列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 项目列表分页列表
     */
    @Override
    public TableDataInfo<ProjectVo> queryPageList(ProjectBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<Project> lqw = buildQueryWrapper(bo);
        Page<ProjectVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的项目列表列表
     *
     * @param bo 查询条件
     * @return 项目列表列表
     */
    @Override
    public List<ProjectVo> queryList(ProjectBo bo) {
        LambdaQueryWrapper<Project> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<Project> buildQueryWrapper(ProjectBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<Project> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(Project::getProjectId);
        lqw.like(StringUtils.isNotBlank(bo.getProjectName()), Project::getProjectName, bo.getProjectName());
        lqw.eq(StringUtils.isNotBlank(bo.getStatus()), Project::getStatus, bo.getStatus());
        return lqw;
    }

    /**
     * 新增项目列表
     *
     * @param bo 项目列表
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(ProjectBo bo) {
        log.info("开始插入项目：{}", bo.getProjectName());
        Project add = MapstructUtils.convert(bo, Project.class);
        validEntityBeforeSave(add);
        
        log.info("转换后的Project对象：projectName={}, description={}, status={}", 
                add.getProjectName(), add.getDescription(), add.getStatus());
        
        boolean flag = baseMapper.insert(add) > 0;
        log.info("数据库插入结果：{}", flag);
        
        if (flag) {
            bo.setProjectId(add.getProjectId());
            log.info("插入成功，生成的ID：{}", add.getProjectId());
        } else {
            log.error("插入失败");
        }
        return flag;
    }
    
    /**
     * 直接插入项目（绕过验证）
     */
    private Boolean insertProjectDirectly(ProjectBo bo) {
        log.info("直接插入项目：{}", bo.getProjectName());
        Project add = MapstructUtils.convert(bo, Project.class);
        
        log.info("转换后的Project对象：projectName={}, description={}, status={}", 
                add.getProjectName(), add.getDescription(), add.getStatus());
        
        boolean flag = baseMapper.insert(add) > 0;
        log.info("数据库插入结果：{}", flag);
        
        if (flag) {
            bo.setProjectId(add.getProjectId());
            log.info("插入成功，生成的ID：{}", add.getProjectId());
        } else {
            log.error("插入失败");
        }
        return flag;
    }

    /**
     * 修改项目列表
     *
     * @param bo 项目列表
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(ProjectBo bo) {
        Project update = MapstructUtils.convert(bo, Project.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(Project entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除项目列表信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }

    /**
     * 导入项目数据
     *
     * @param file          导入文件
     * @param updateSupport 是否更新已存在的数据
     * @throws Exception 导入异常
     */
    @Override
    public void importData(MultipartFile file, boolean updateSupport) throws Exception {
        log.info("开始导入项目数据，文件大小：{} bytes", file.getSize());
        
        List<ProjectImportVo> list = ExcelUtil.importExcel(file.getInputStream(), ProjectImportVo.class);
        log.info("Excel解析完成，共解析到 {} 条数据", list.size());
        
        if (list == null || list.isEmpty()) {
            log.warn("Excel文件中没有解析到任何数据");
            return;
        }
        
        int successCount = 0;
        int errorCount = 0;
        int skipCount = 0;
        
        for (int i = 0; i < list.size(); i++) {
            ProjectImportVo importVo = list.get(i);
            try {
                log.info("处理第 {} 条数据：{}", i + 1, importVo.getProjectName());
                
                // 打印详细的数据信息
                log.info("项目名称: {}", importVo.getProjectName());
                log.info("项目描述: {}", importVo.getDescription());
                log.info("代码仓库: {}", importVo.getRepositoryUrl());
                log.info("项目网站: {}", importVo.getWebsiteUrl());
                log.info("项目状态: {}", importVo.getStatus());
                log.info("备注: {}", importVo.getRemark());
                
                // 数据验证 - 只验证项目名称
                if (StringUtils.isBlank(importVo.getProjectName())) {
                    log.warn("第 {} 条数据项目名称为空，跳过此条数据", i + 1);
                    errorCount++;
                    continue;
                }
                
                // 检查项目名称是否已存在
                LambdaQueryWrapper<Project> checkLqw = Wrappers.lambdaQuery();
                checkLqw.eq(Project::getProjectName, importVo.getProjectName());
                Project existingProject = baseMapper.selectOne(checkLqw);
                
                if (existingProject != null) {
                    log.info("项目名称 '{}' 已存在，跳过此条数据", importVo.getProjectName());
                    skipCount++;
                    continue;
                }
                
                // 创建ProjectBo对象
                ProjectBo bo = new ProjectBo();
                bo.setProjectName(importVo.getProjectName());
                bo.setProjectCode(""); // 设置空的projectCode
                bo.setDescription(StringUtils.isNotBlank(importVo.getDescription()) ? importVo.getDescription() : "暂无描述");
                bo.setRepositoryUrl(importVo.getRepositoryUrl());
                bo.setWebsiteUrl(importVo.getWebsiteUrl());
                bo.setStatus(StringUtils.isNotBlank(importVo.getStatus()) ? importVo.getStatus() : "1");
                bo.setRemark(importVo.getRemark());
                
                log.info("准备插入数据：{}", bo.getProjectName());
                
                // 直接插入新数据（绕过验证）
                boolean insertResult = insertProjectDirectly(bo);
                log.info("新增结果：{}", insertResult);
                
                if (insertResult) {
                    successCount++;
                } else {
                    errorCount++;
                }
                
            } catch (Exception e) {
                log.error("处理第 {} 条项目数据时出错：{}", i + 1, e.getMessage(), e);
                errorCount++;
            }
        }
        
        log.info("导入完成，成功：{} 条，跳过重复：{} 条，失败：{} 条", successCount, skipCount, errorCount);
    }
}
