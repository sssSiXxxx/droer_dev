package org.dromara.system.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.dromara.common.core.domain.R;
import org.dromara.system.service.ISysStatisticsService;

import java.util.Map;

/**
 * 系统统计Controller
 * 
 * @author system
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/system/stats")
public class SysStatisticsController {

    private final ISysStatisticsService statisticsService;

    /**
     * 获取社区统计数据
     */
    @GetMapping("/community")
    public R<Map<String, Object>> getCommunityStats() {
        try {
            log.info("获取社区统计数据");
            Map<String, Object> stats = statisticsService.getCommunityStatistics();
            return R.ok(stats);
        } catch (Exception e) {
            log.error("获取社区统计数据失败", e);
            return R.fail("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取项目统计数据
     */
    @GetMapping("/projects")
    public R<Map<String, Object>> getProjectStats() {
        try {
            Map<String, Object> stats = statisticsService.getProjectStatistics();
            return R.ok(stats);
        } catch (Exception e) {
            log.error("获取项目统计数据失败", e);
            return R.fail("获取项目统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户统计数据
     */
    @GetMapping("/users")
    public R<Map<String, Object>> getUserStats() {
        try {
            Map<String, Object> stats = statisticsService.getUserStatistics();
            return R.ok(stats);
        } catch (Exception e) {
            log.error("获取用户统计数据失败", e);
            return R.fail("获取用户统计数据失败: " + e.getMessage());
        }
    }
}