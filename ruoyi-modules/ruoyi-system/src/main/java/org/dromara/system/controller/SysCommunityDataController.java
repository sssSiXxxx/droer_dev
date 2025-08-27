package org.dromara.system.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.dromara.common.core.domain.R;
import org.dromara.system.service.ISysCommunityDataService;

import java.util.List;
import java.util.Map;

/**
 * 社区数据Controller
 * 
 * @author system
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/community")
public class SysCommunityDataController {

    private final ISysCommunityDataService communityDataService;

    /**
     * 获取热门项目排行
     */
    @GetMapping("/hot-projects")
    public R<List<Map<String, Object>>> getHotProjects(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            log.info("获取热门项目排行，限制数量: {}", limit);
            List<Map<String, Object>> hotProjects = communityDataService.getHotProjects(limit);
            return R.ok(hotProjects);
        } catch (Exception e) {
            log.error("获取热门项目排行失败", e);
            return R.fail("获取热门项目排行失败: " + e.getMessage());
        }
    }

    /**
     * 获取技术栈分布
     */
    @GetMapping("/tech-stack")
    public R<List<Map<String, Object>>> getTechStackDistribution() {
        try {
            log.info("获取技术栈分布");
            List<Map<String, Object>> techStack = communityDataService.getTechStackDistribution();
            return R.ok(techStack);
        } catch (Exception e) {
            log.error("获取技术栈分布失败", e);
            return R.fail("获取技术栈分布失败: " + e.getMessage());
        }
    }

    /**
     * 获取活跃贡献者排行
     */
    @GetMapping("/active-contributors")
    public R<List<Map<String, Object>>> getActiveContributors(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            log.info("获取活跃贡献者排行，限制数量: {}", limit);
            List<Map<String, Object>> contributors = communityDataService.getActiveContributors(limit);
            return R.ok(contributors);
        } catch (Exception e) {
            log.error("获取活跃贡献者排行失败", e);
            return R.fail("获取活跃贡献者排行失败: " + e.getMessage());
        }
    }

    /**
     * 获取社区活跃度趋势
     */
    @GetMapping("/activity-trend")
    public R<Map<String, Object>> getActivityTrend(@RequestParam(defaultValue = "7") Integer days) {
        try {
            log.info("获取社区活跃度趋势，天数: {}", days);
            Map<String, Object> trend = communityDataService.getActivityTrend(days);
            return R.ok(trend);
        } catch (Exception e) {
            log.error("获取社区活跃度趋势失败", e);
            return R.fail("获取社区活跃度趋势失败: " + e.getMessage());
        }
    }

    /**
     * 获取编程语言分布
     */
    @GetMapping("/language-distribution")
    public R<List<Map<String, Object>>> getLanguageDistribution() {
        try {
            log.info("获取编程语言分布");
            List<Map<String, Object>> languages = communityDataService.getLanguageDistribution();
            return R.ok(languages);
        } catch (Exception e) {
            log.error("获取编程语言分布失败", e);
            return R.fail("获取编程语言分布失败: " + e.getMessage());
        }
    }

    /**
     * 同步Gitee数据到活跃度趋势
     */
    @GetMapping("/sync-gitee-data")
    public R<Map<String, Object>> syncGiteeActivityData() {
        try {
            log.info("手动触发Gitee数据同步");
            Map<String, Object> syncResult = communityDataService.syncGiteeActivityData();
            return R.ok(syncResult);
        } catch (Exception e) {
            log.error("同步Gitee数据失败", e);
            return R.fail("同步Gitee数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取Gitee同步状态
     */
    @GetMapping("/gitee-sync-status")
    public R<Map<String, Object>> getGiteeSyncStatus() {
        try {
            log.info("获取Gitee同步状态");
            Map<String, Object> status = communityDataService.getGiteeSyncStatus();
            return R.ok(status);
        } catch (Exception e) {
            log.error("获取Gitee同步状态失败", e);
            return R.fail("获取Gitee同步状态失败: " + e.getMessage());
        }
    }
}