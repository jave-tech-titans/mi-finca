package com.techtitans.mifinca.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.techtitans.mifinca.domain.entities.PropertyEntity;

public interface PropertyRepository extends JpaRepository<PropertyEntity,UUID>{
    
}
