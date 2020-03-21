package com.dghysc.hy.config;

import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

@org.springframework.context.annotation.Configuration
public class Configuration implements WebMvcConfigurer {
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
