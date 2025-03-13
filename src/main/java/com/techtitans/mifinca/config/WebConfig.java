package com.techtitans.mifinca.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.techtitans.mifinca.transport.middlewares.AuthMiddleware;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private AuthMiddleware authMiddleware;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //here we suscribe the endpoints with the middleware
        registry.addInterceptor(authMiddleware)
                .addPathPatterns("/api/v1/properties/**")
                .addPathPatterns("/api/v1/rental/**")
                .addPathPatterns("/api/v1/payments/**");
            //.excludePathPatterns("/api/public/**");
    }
}
