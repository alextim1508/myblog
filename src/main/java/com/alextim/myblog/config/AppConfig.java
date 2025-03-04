package com.alextim.myblog.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan("com.alextim.myblog")
@PropertySource("classpath:application.properties")
public class AppConfig {
}
