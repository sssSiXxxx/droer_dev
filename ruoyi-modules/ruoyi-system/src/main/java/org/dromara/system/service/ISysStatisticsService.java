package org.dromara.system.service;

import java.util.Map;

/**
 * 系统统计Service接口
 * 
 * @author system
 */
public interface ISysStatisticsService {

    /**
     * 获取社区统计数据
     */
    Map<String, Object> getCommunityStatistics();

    /**
     * 获取项目统计数据
     */
    Map<String, Object> getProjectStatistics();

    /**
     * 获取用户统计数据
     */
    Map<String, Object> getUserStatistics();
}