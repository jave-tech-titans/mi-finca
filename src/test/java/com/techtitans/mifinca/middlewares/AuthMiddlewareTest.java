package com.techtitans.mifinca.middlewares;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.services.AuthService;
import com.techtitans.mifinca.transport.middlewares.AuthMiddleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
public class AuthMiddlewareTest {

    private AuthMiddleware authMiddleware;
    private AuthService authService = mock(AuthService.class);
    private HttpServletRequest request = mock(HttpServletRequest.class);
    private HttpServletResponse response = mock(HttpServletResponse.class);

    @BeforeEach
    void setUp() {
        authMiddleware = new AuthMiddleware(authService);
    }

    @Test
    void preHandle_MissingAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        boolean result = authMiddleware.preHandle(request, response, new Object());
        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(request, never()).setAttribute(eq("auth"), any());
    }

    @Test
    void preHandle_InvalidAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Token abcdef");
        boolean result = authMiddleware.preHandle(request, response, new Object());
        assertFalse(result);
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(request, never()).setAttribute(eq("auth"), any());
    }

    @Test
    void preHandle_ValidAuthorizationHeader() throws Exception {
        String token = "valid-token";
        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        AuthDTO dummyAuth = new AuthDTO(UUID.randomUUID(), "USER");
        when(authService.extractJWTPayload(token)).thenReturn(dummyAuth);
        boolean result = authMiddleware.preHandle(request, response, new Object());
        assertTrue(result);
        verify(request).setAttribute("auth", dummyAuth);
        verify(response, never()).setStatus(anyInt());
    }
}
