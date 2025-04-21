package com.techtitans.mifinca.middlewares;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.techtitans.mifinca.domain.exceptions.ApiError;
import com.techtitans.mifinca.domain.exceptions.ApiException;
import com.techtitans.mifinca.transport.middlewares.ErrorsMiddleware;

@ExtendWith(MockitoExtension.class)
class ErrorsMiddlewareTest {

    private ErrorsMiddleware middleware;

    @BeforeEach
    void setUp() {
        middleware = new ErrorsMiddleware();
    }

    @Test
    void handleGeneralException_GenericException() {
        RuntimeException ex = new RuntimeException("Generic error occurred");
        ResponseEntity<Map<String, Object>> response = middleware.handleGeneralException(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertEquals("Generic error occurred", body.get("ERROR"));
        assertTrue(body.get("TIMESTAMP") instanceof LocalDateTime);
    }

    @Test
    void handleGeneralException_ApiException() {
        ApiException apiEx = new ApiException(ApiError.EMPTY_FIELDS);

        ResponseEntity<Map<String, Object>> response = middleware.handleGeneralException(apiEx);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue(body.get("TIMESTAMP") instanceof LocalDateTime);
    }
}