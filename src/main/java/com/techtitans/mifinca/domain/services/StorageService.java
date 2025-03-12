package com.techtitans.mifinca.domain.services;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techtitans.mifinca.domain.entities.FileEntity;
import com.techtitans.mifinca.domain.entities.PropertyEntity;
import com.techtitans.mifinca.domain.exceptions.ApiError;
import com.techtitans.mifinca.domain.exceptions.ApiException;
import com.techtitans.mifinca.repository.FileRepository;

@Service
public class StorageService {

    @Autowired
    private FileRepository repo;

    private static final String MAIN_DIRECTORY = "files"; 

    public void saveFile(PropertyEntity prop,String path, InputStream file){
        writeFile(path, file);
        repo.save(FileEntity.builder()
            .createdAt(LocalDateTime.now())
            .property(prop)
            .url(path)
            .build()
        );
    }

    private void writeFile(String path, InputStream file){
         try {
            Path baseDir = Paths.get(System.getProperty("user.dir"), MAIN_DIRECTORY);
            Files.createDirectories(baseDir);
            Path fullPath = baseDir.resolve(path);
            Files.createDirectories(fullPath.getParent());
            try (OutputStream outputStream = new FileOutputStream(fullPath.toFile())) {
                file.transferTo(outputStream);
            }
        } catch (Exception e) {
            throw new ApiException(ApiError.UNABLE_TO_STORE_IMAGE);
        }
    }
    
}
