package org.dromara.osc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * OSC模块配置
 *
 * @author lmq
 * @date 2025-08-03
 */
@Configuration
@ComponentScan(basePackages = {
    "org.dromara.osc.domain",
    "org.dromara.osc.mapper",
    "org.dromara.osc.service",
    "org.dromara.osc.controller"
})
public class OscConfig {
}