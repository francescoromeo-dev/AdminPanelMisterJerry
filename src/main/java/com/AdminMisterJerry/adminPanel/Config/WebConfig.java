package com.AdminMisterJerry.adminPanel.Config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Configura Spring per servire le immagini dalla cartella public/images/
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:public/images/");
    }
}
