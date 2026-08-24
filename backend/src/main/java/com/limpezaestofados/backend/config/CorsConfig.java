package com.limpezaestofados.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Implementa a interface WebMvcConfigurer para configurar o CORS 
// (Cross-Origin Resource Sharing) server-side, permitindo que o frontend acesse recursos do backend de diferentes origens (domínios).
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Configuração de desenvolvimento: aceita qualquer origem local.
        // Antes de produção, restringir para a origem real do frontend.
        registry.addMapping("/api/**")
                .allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}