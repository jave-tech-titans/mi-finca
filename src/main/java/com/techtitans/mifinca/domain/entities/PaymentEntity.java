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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE payments SET status = 1 WHERE id=?")
@SQLRestriction("status = 0")
@Table(name = "payments")  
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; 
    private String bank;
    private Long accountNumber;
    private double value;
    @OneToOne
    @JoinColumn(name="schedule_id") 
    private ScheduleEntity schedule;

    //auditoring
    private LocalDateTime createdAt;
}
