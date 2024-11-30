package com.jeremieferreira.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfigurer {
	
	@Value("file:${files.location}public/")
	private String filesLocation;
	
	@Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**").allowedOrigins("http://localhost:4200")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS");
            }
            
            @Override
            public void addResourceHandlers(ResourceHandlerRegistry registry) {
            	registry.addResourceHandler("/resources/**")
            			.addResourceLocations(filesLocation);
            }
        };
    }
}
