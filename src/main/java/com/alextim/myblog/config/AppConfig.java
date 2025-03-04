package com.alextim.myblog;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configuration
//@Profile("prod123")
@ComponentScan("com.alextim.myblog")
@PropertySource("classpath:application.properties")
@EnableWebMvc
public class AppConfig {
}
