package com.techtitans.mifinca.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Builder
@Getter 
@Setter 
@AllArgsConstructor
@NoArgsConstructor
@Table(name="files")
public class FileEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String url;
    @ManyToOne
    @JoinColumn(name="property_id")
    private PropertyEntity property;

    //for auditoring
    private LocalDateTime createdAt;
}
