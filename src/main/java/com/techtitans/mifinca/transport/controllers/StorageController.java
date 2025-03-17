package com.techtitans.mifinca.transport.controllers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techtitans.mifinca.domain.services.StorageService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/files")
public class StorageController {
    private StorageService storageService;

    public StorageController(StorageService storageService){
        this.storageService = storageService;
    }

    @GetMapping("/**")
    public ResponseEntity<Resource> serveFile(HttpServletRequest request) throws Exception{
        String fullPath = request.getRequestURI();
        String postfix = fullPath.replace("/api/v1/files/", "");
        Resource file = storageService.getFile(postfix);
        Path filePath = Paths.get(file.getURI());
        //get the content type, it will be always images, but png or jpeg?
        String contentType = Files.probeContentType(filePath);
        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
            .body(file);
    }
}
