package com.techtitans.mifinca.domain.entities;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@SQLDelete(sql = "UPDATE schedules SET status = 1 WHERE id=?")
@SQLRestriction("status = 0")
@Table(name = "schedules")  
public class ScheduleEntity {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate startDate;
    private LocalDate endDate;
    private int numberPersons;
    private String scStatus;    //this is the business logic status, not the table one
    private double price;

    @ManyToOne
    @JoinColumn(name="user_id")
    private AccountEntity user;
    @ManyToOne
    @JoinColumn(name="property_id")
    private PropertyEntity property;

    @OneToMany(mappedBy ="scheduling")
    private List<RatingEntity> ratings;


    //for auditoring
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private byte status;
}
