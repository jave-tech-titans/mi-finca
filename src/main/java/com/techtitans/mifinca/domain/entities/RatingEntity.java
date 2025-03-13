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
@SQLDelete(sql = "UPDATE ratings SET status = 1 WHERE id=?")
@SQLRestriction("status = 0")
@Table(name = "ratings") 
public class RatingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int rating;     //from 1 to 5
    private String comment;
    private String type;    //if it was the landlord the one rated or the user
    @ManyToOne
    @JoinColumn(name="schedule_id")
    private ScheduleEntity scheduling;


    //for auditoring
    private LocalDateTime createdAt;

    private byte status;
}
