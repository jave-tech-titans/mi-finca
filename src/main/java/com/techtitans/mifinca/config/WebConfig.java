package com.techtitans.mifinca.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.techtitans.mifinca.transport.middlewares.AuthMiddleware;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private AuthMiddleware authMiddleware;

    public WebConfig(AuthMiddleware authMiddleware){
        this.authMiddleware = authMiddleware;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**") 
                        .allowedOrigins("*") 
                        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //here we suscribe the endpoints with the middleware
        registry.addInterceptor(authMiddleware)
                .addPathPatterns("/api/v1/properties/**")
                .addPathPatterns("/api/v1/rental/**")
                .addPathPatterns("/api/v1/payments/**");
    }
}
