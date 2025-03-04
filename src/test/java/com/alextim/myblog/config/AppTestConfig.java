package com.alextim.myblog.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ActiveProfiles("test")
@ComponentScan(
        basePackages = {"com.alextim.myblog"},
        excludeFilters = {
            @ComponentScan.Filter(type= FilterType.ASSIGNABLE_TYPE, value=AppConfig.class)
        }
)
@PropertySource("classpath:application-test.properties")
public class AppTestConfig {
}
