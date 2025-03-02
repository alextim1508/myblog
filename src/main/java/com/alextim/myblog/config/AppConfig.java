package com.alextim.myblog.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("prod")
@ComponentScan("com.alextim.myblog")
@PropertySource("classpath:application.properties")
public class AppConfig {
}
