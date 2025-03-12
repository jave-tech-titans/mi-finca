package com.techtitans.mifinca.domain.services;

import com.techtitans.mifinca.domain.dtos.AcceptRentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.dtos.CancelRentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.CreateRentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.RentalRequestFilterDTO;
import com.techtitans.mifinca.domain.dtos.RentalRequestTenantFilterDTO;
import com.techtitans.mifinca.domain.dtos.ScheduleDTO;
import com.techtitans.mifinca.domain.entities.AccountEntity;
import com.techtitans.mifinca.domain.entities.PropertyEntity;
import com.techtitans.mifinca.domain.entities.Roles;
import com.techtitans.mifinca.domain.entities.ScheduleEntity;
import com.techtitans.mifinca.domain.entities.ScheduleStatus;
import com.techtitans.mifinca.domain.exceptions.ApiError;
import com.techtitans.mifinca.domain.exceptions.ApiException;
import com.techtitans.mifinca.domain.filters.SchedulesSearchFilter;
import com.techtitans.mifinca.repository.ScheduleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RentalService {

    @Autowired
    private ScheduleRepository repo;

    @Autowired
    private PropertiesService propertiesService;

    public List<ScheduleDTO> getPropertySchedules(UUID propertyId,SchedulesSearchFilter filter){
        propertiesService.checkPropertyExists(propertyId);
        List<ScheduleEntity> schedulesEntity = repo.findAllByPropertyId(propertyId,filter.month(), filter.year(), 10);

        List<ScheduleDTO> schedulesDTOS = new ArrayList<>();
        for(ScheduleEntity entity : schedulesEntity){
            schedulesDTOS.add(new ScheduleDTO(entity.getStartDate(), entity.getEndDate()));
        }
        return schedulesDTOS;
    }

    public List<?> getRentalRequests(RentalRequestFilterDTO body) {
        return List.of(); // Temporal
    }

    public void acceptRequest(Long request_id, AcceptRentalRequestDTO body) {
        // aceptar solicitud
    }

    public void cancelRequest(Long request_id, CancelRentalRequestDTO body) {
        // cancelar solicitud
    }

    public void createRentalRequest(UUID propertyId,CreateRentalRequestDTO dto, AuthDTO authDTO) {
        if(dto.startDate() == null || dto.endDate() == null){
            throw new ApiException(ApiError.EMPTY_FIELDS);
        }

        //checking if the number of people is in the range
        if(!propertiesService.validatePropertyGuests(propertyId, dto.nGuests())){
            throw new ApiException(ApiError.INVALID_PARAMETERS);
        }
        //if user is  a landlord he cant do this
        if(authDTO.role().equals(Roles.LANDLORD_ROLE)){
            throw new ApiException(ApiError.UNATHORIZED_TO_REQUEST);
        }
        //check that the dates are correct
        if(dto.startDate().isBefore(LocalDate.now()) || dto.endDate().isBefore(dto.startDate())){
            throw new ApiException(ApiError.INVALID_SCHEDULE_DATES);
        }

        //we're going to check the availability of the property in the selected dates
        int year = dto.startDate().getYear();
        List<ScheduleEntity> schedules = repo.findAllByPropertyId(propertyId, null, year, 100);
        System.out.println(schedules.size());
        for(ScheduleEntity sch : schedules){
            if( 
                (!sch.getScStatus().equals(ScheduleStatus.CANCELED) && !sch.getScStatus().equals(ScheduleStatus.DENIED) )&&  //if the sch is not canceled or denied and it collisions
                ((dto.startDate().isAfter(sch.getStartDate()) && dto.startDate().isBefore(sch.getEndDate())) ||
                (dto.endDate().isAfter(sch.getStartDate()) && dto.endDate().isBefore(sch.getStartDate())) ||
                dto.startDate().equals(sch.getStartDate()) || dto.endDate().equals(sch.getEndDate()))
            ){
                throw new ApiException(ApiError.INVALID_SCHEDULE_DATES);
            }
        }

        //if no collision was found, then we schedule
        ScheduleEntity newSchedule = ScheduleEntity.builder()
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .scStatus(ScheduleStatus.REQUESTED)
            .startDate(dto.startDate())
            .endDate(dto.endDate())
            .property(PropertyEntity.builder().id(propertyId).build())
            .user(AccountEntity.builder().id(authDTO.userId()).build())
            .build();
        repo.save(newSchedule);
    }

    public List<?> getTenantRentalRequests(RentalRequestTenantFilterDTO body) {
        return List.of(); // Temporal
    }
}