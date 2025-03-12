package com.techtitans.mifinca.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techtitans.mifinca.domain.entities.FileEntity;

public interface FileRepository extends JpaRepository<FileEntity,UUID>{
    
}
