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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE pending_creations SET status = 1 WHERE id=?")
@SQLRestriction("status = 0")
@Table(name = "pending_creations")  
public class ConfirmationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID token;
    private byte status;

    @OneToOne
    @JoinColumn(name = "account_id")
    private AccountEntity account;
    private LocalDateTime createdAt;


    public ConfirmationEntity(UUID token, AccountEntity account, LocalDateTime createdAt){
        this.account = account;
        this.token = token;
        this.createdAt = createdAt;
    }
    
}
