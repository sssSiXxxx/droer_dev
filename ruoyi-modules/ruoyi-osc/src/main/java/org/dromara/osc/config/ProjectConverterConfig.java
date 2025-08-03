package org.dromara.osc.config;

import org.dromara.osc.domain.Project;
import org.dromara.osc.domain.vo.ProjectVo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Project 转换器配置
 *
 * @author lmq
 * @date 2025-08-03
 */
@Configuration
public class ProjectConverterConfig {

    @Bean
    public org.dromara.osc.domain.ProjectToProjectVoMapper projectToProjectVoMapper() {
        return new org.dromara.osc.domain.ProjectToProjectVoMapperImpl();
    }
}