package org.dromara.workflow.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.dromara.workflow.domain.FlowCategory;
import org.dromara.workflow.domain.vo.FlowCategoryVo;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 流程分类Mapper接口
 *
 * @author may
 * @date 2023-06-27
 */
public interface FlwCategoryMapper extends BaseMapper<FlowCategory> {

    /**
     * 统计指定流程分类ID的分类数量
     *
     * @param categoryId 流程分类ID
     * @return 该流程分类ID的分类数量
     */
    default long countCategoryById(Long categoryId) {
        return this.selectCount(new LambdaQueryWrapper<FlowCategory>().eq(FlowCategory::getCategoryId, categoryId));
    }

    /**
     * 根据父流程分类ID查询其所有子流程分类的列表
     *
     * @param parentId 父流程分类ID
     * @return 包含子流程分类的列表
     */
    default List<FlowCategory> selectListByParentId(Long parentId) {
        return this.selectList(new LambdaQueryWrapper<FlowCategory>()
            .select(FlowCategory::getCategoryId));
    }

    /**
     * 根据父流程分类ID查询包括父ID及其所有子流程分类ID的列表
     *
     * @param parentId 父流程分类ID
     * @return 包含父ID和子流程分类ID的列表
     */
    default List<Long> selectCategoryIdsByParentId(Long parentId) {
        return Stream.concat(
            this.selectListByParentId(parentId).stream()
                .map(FlowCategory::getCategoryId),
            Stream.of(parentId)
        ).collect(Collectors.toList());
    }

}
