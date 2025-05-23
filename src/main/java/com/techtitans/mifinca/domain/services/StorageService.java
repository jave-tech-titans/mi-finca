package com.techtitans.mifinca.domain.services;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import com.techtitans.mifinca.domain.entities.FileEntity;
import com.techtitans.mifinca.domain.entities.PropertyEntity;
import com.techtitans.mifinca.domain.exceptions.ApiError;
import com.techtitans.mifinca.domain.exceptions.ApiException;
import com.techtitans.mifinca.repository.FileRepository;

@Service
public class StorageService {

    private FileRepository repo;
    private String domain;

    private static final String MAIN_DIRECTORY = "files"; 

    public StorageService(FileRepository repo, @Value("${general.backend-domain}") String domain){
        this.repo = repo;
        this.domain = domain;
    }

    public void saveFile(PropertyEntity prop,String path, InputStream file){
        writeFile(path, file);
        repo.save(FileEntity.builder()
            .createdAt(LocalDateTime.now())
            .property(prop)
            .url(path)
            .build()
        );
    }

     public Resource getFile(String relativePath) throws MalformedURLException {
        Path filePath = Paths.get(MAIN_DIRECTORY).resolve(relativePath).normalize();
        if (!Files.exists(filePath)) {
            throw new ApiException(ApiError.FILE_NOT_FOUND);
        }
        return new UrlResource(filePath.toUri());
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

    public String getUrl(FileEntity file){
        return domain + "/api/v1/files/" + file.getUrl();
    }

    public List<String> getPicturesOfProperty(UUID propertyId){
        List<FileEntity> pictures = repo.findByPropertyId(propertyId);
        List<String> urls = new ArrayList<>();
        for(FileEntity ent: pictures){
            urls.add(getUrl(ent));
        }   
        return urls;
    }
    
}
