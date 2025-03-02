package com.alextim.myblog.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;

@Configuration
@ActiveProfiles("test")
@ComponentScan("com.alextim.myblog")
@PropertySource("classpath:application-test.properties")
public class AppTestConfig {
}
