package com.techtitans.mifinca.transport.middlewares;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthMiddleware implements HandlerInterceptor{

    private AuthService authService;

    public AuthMiddleware(AuthService authService){
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_OK);
            return true;
        }
        String authHeader = request.getHeader("Authorization");
        //validating that theres a bearer token in the request
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        //extracting the token and then extrating the payload
        String token = authHeader.substring(7);
        AuthDTO authDto = authService.extractJWTPayload(token);
        request.setAttribute("auth", authDto); 
        return true; 
    }
}
