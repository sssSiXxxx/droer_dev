package org.dromara.osc.service;

import org.dromara.osc.domain.vo.ProjectVo;
import org.dromara.osc.domain.bo.ProjectBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

/**
 * 项目列表Service接口
 *
 * @author lmq
 * @date 2025-08-02
 */
public interface IProjectService {

    /**
     * 查询项目列表
     *
     * @param projectId 主键
     * @return 项目列表
     */
    ProjectVo queryById(Long projectId);

    /**
     * 分页查询项目列表列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 项目列表分页列表
     */
    TableDataInfo<ProjectVo> queryPageList(ProjectBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的项目列表列表
     *
     * @param bo 查询条件
     * @return 项目列表列表
     */
    List<ProjectVo> queryList(ProjectBo bo);

    /**
     * 新增项目列表
     *
     * @param bo 项目列表
     * @return 是否新增成功
     */
    Boolean insertByBo(ProjectBo bo);

    /**
     * 修改项目列表
     *
     * @param bo 项目列表
     * @return 是否修改成功
     */
    Boolean updateByBo(ProjectBo bo);

    /**
     * 校验并批量删除项目列表信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 导入项目数据
     *
     * @param file          导入文件
     * @param updateSupport 是否更新已存在的数据
     * @throws Exception 导入异常
     */
    void importData(MultipartFile file, boolean updateSupport) throws Exception;
}
