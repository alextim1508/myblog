package com.alextim.myblog.config;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan("com.alextim.myblog")
@PropertySource("classpath:application.properties")
public class AppConfig {
}
