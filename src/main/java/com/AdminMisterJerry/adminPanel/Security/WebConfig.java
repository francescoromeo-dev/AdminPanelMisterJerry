package com.AdminMisterJerry.adminPanel.Security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // RIMUOVERE QUESTA CONFIGURAZIONE - Le immagini ora vengono servite dal database
        // Non abbiamo più bisogno di servire file statici da una cartella locale
        
        // Manteniamo solo la configurazione per i file CSS e JS statici se necessari
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
                
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
    }
}