package com.alextim.myblog.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(
        basePackages = {"com.alextim.myblog"},
        excludeFilters = {
            @ComponentScan.Filter(type= FilterType.ASSIGNABLE_TYPE, value=AppConfig.class)
        }
)
public class AppTestConfig {
}
