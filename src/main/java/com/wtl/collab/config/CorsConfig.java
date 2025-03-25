package com.wtl.collab.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("http://localhost:*") // Allow all local ports
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .allowCredentials(true) // Allow cookies
                .allowedHeaders("*");
    }
}
