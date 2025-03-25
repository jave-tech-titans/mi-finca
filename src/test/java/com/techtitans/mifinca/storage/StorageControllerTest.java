package com.techtitans.mifinca.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.techtitans.mifinca.domain.exceptions.ApiError;
import com.techtitans.mifinca.domain.exceptions.ApiException;
import com.techtitans.mifinca.domain.services.StorageService;
import com.techtitans.mifinca.transport.controllers.StorageController;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
class StorageControllerTest {
    
    private StorageService storageService;
    private StorageController storageController;

    @BeforeEach
    void setUp() {
        storageService = mock(StorageService.class);
        storageController = new StorageController(storageService);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                     GET  PICTIRE TESTS                                                                      //////////////////////////
    
    @AfterEach
    void tearDown() {
        // Cleanup is handled in the test if temporary files are created.
    }

    @Test
    void serveFile_Success() throws Exception {
        String relativePath = "dummy.jpg";
        
        Path tempFile = Files.createTempFile("dummy", ".jpg");
        Files.write(tempFile, "dummy content".getBytes());
        Resource resource = new UrlResource(tempFile.toUri());
        
        when(storageService.getFile(relativePath)).thenReturn(resource);
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/files/" + relativePath);
        
        ResponseEntity<Resource> response = storageController.serveFile(request);
        
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        Path filePath = Paths.get(resource.getURI());
        String expectedContentType = Files.probeContentType(filePath);
        assertEquals(MediaType.parseMediaType(expectedContentType), response.getHeaders().getContentType());
        
        String expectedHeader = "attachment; filename=\"" + resource.getFilename() + "\"";
        assertEquals(expectedHeader, response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION));
        
        assertEquals(resource, response.getBody());
    
        Files.deleteIfExists(tempFile);
    }

    @Test
    void serveFile_FileNotFound_ThrowsException() throws Exception {
        String relativePath = "nonexistent.jpg";
        when(storageService.getFile(relativePath)).thenThrow(new ApiException(ApiError.FILE_NOT_FOUND));
        
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/api/v1/files/" + relativePath);
    
        ApiException ex = assertThrows(ApiException.class, () -> storageController.serveFile(request));
        assertEquals(ApiError.FILE_NOT_FOUND, ex.getError());
    }
}
