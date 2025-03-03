package com.techtitans.mifinca.domain.entities;

import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE accounts SET status = 1 WHERE id=?")
@Where(clause = "status = 0")
@Table(
    name = "accounts",
    uniqueConstraints =  @UniqueConstraint(columnNames = "email")
)   //we add the unqiue constraint at db layer just in case, howeber I'll keep it in service also
public class AccountEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String names;
    private String lastNames;
    private String email;
    private String password;
    private String number;
}
