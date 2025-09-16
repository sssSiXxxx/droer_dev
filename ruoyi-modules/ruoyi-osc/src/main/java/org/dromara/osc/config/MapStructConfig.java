package org.dromara.osc.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * MapStruct 转换器配置
 *
 * @author lmq
 * @date 2025-08-03
 */
@Configuration
@ComponentScan(basePackages = {
    "org.dromara.osc.domain"
})
public class MapStructConfig {
}