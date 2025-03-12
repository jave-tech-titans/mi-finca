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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.techtitans.mifinca.domain.dtos.CreatePropertyDTO;
import com.techtitans.mifinca.domain.dtos.UpdatePropertyDTO;


@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@SQLDelete(sql = "UPDATE properties SET status = 1 WHERE id=?")
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
    private AccountEntity owner;

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

    public void updateWithDTO(UpdatePropertyDTO dto){
        if(dto.name() != null){
            this.name = dto.name();
        }
        if(dto.department() != null){
            this.department = dto.department();
        }
        if(dto.enterType() != null){
            this.enterType = dto.enterType();
        }
        if(dto.description() != null){
            this.description = dto.description();
        }
        if(dto.numberRooms() != null){
            this.numberRooms = dto.numberRooms();
        }
        if(dto.numberBathrooms() != null){
            this.numberBathrooms = dto.numberBathrooms();
        }
        if(dto.isPetFriendly() != null){
            this.isPetFriendly = dto.isPetFriendly();
        }
        if(dto.hasPool() != null){
            this.hasPool = dto.hasPool();
        }
        if(dto.hasAsador() != null){
            this.hasAsador = dto.hasAsador();
        }
        if(dto.nightPrice() != null){
            this.nightPrice = dto.nightPrice();
        }
    }
}
