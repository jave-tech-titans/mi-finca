package com.techtitans.mifinca.domain.entities;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.techtitans.mifinca.domain.dtos.CreatePropertyDTO;


@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE accounts SET status = 1 WHERE id=?")
@SQLRestriction("status = 0")
@Table(name = "properties")  
public class PropertyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String department;
    private String enterType;
    private String description;
    private int numberRooms;
    private int numberBathrooms;
    private boolean isPetFriendly;
    private boolean hasPool;
    private boolean hasAsador;
    private double nightPrice;
    @OneToMany(mappedBy = "property")
    private List<FileEntity> pictures;

    @ManyToOne
    @JoinColumn(name = "account_id") 
    private AccountEntity user;

    private byte status;

    //for auditoring
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    //temporary a factory constructor for DTO
    public static PropertyEntity fromCreateDTO(CreatePropertyDTO dto){
            var prop = new PropertyEntity();
            prop.setName(dto.name());
            prop.setDepartment(dto.department());
            prop.setEnterType(dto.enterType());
            prop.setDescription(dto.description());
            prop.setNumberRooms(dto.numberRooms());
            prop.setNumberBathrooms(dto.numberBathrooms());
            prop.setPetFriendly(dto.isPetFriendly());
            prop.setHasPool(dto.hasPool());
            prop.setHasAsador(dto.hasAsador());
            prop.setNightPrice(dto.nightPrice());
            return prop;
    }
}
