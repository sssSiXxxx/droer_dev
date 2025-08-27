package org.dromara.system.service;

import java.util.List;
import java.util.Map;

/**
 * 社区数据Service接口
 * 
 * @author system
 */
public interface ISysCommunityDataService {

    /**
     * 获取热门项目排行
     */
    List<Map<String, Object>> getHotProjects(Integer limit);

    /**
     * 获取技术栈分布
     */
    List<Map<String, Object>> getTechStackDistribution();

    /**
     * 获取活跃贡献者排行
     */
    List<Map<String, Object>> getActiveContributors(Integer limit);

    /**
     * 获取社区活跃度趋势
     */
    Map<String, Object> getActivityTrend(Integer days);

    /**
     * 获取编程语言分布
     */
    List<Map<String, Object>> getLanguageDistribution();

    /**
     * 同步Gitee数据到活跃度趋势
     */
    Map<String, Object> syncGiteeActivityData();

    /**
     * 获取Gitee同步状态
     */
    Map<String, Object> getGiteeSyncStatus();
}