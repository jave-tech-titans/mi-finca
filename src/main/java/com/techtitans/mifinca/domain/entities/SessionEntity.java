package com.techtitans.mifinca.domain.entities;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity 
@Getter
@Setter 
@Builder
@SQLDelete(sql = "UPDATE sessions SET status 1 WHRE id=?")
@SQLRestriction("status = 0")
@Table(name = "sessions")
public class SessionEntity {

    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID token;
    private LocalDateTime expiresAt;

    @ManyToOne 
    @JoinColumn(name = "account_id") 
    private AccountEntity user;

    //for auditoring
    private LocalDateTime createdAt;
    
}
