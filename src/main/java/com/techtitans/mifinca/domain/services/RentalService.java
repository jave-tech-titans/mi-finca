package com.techtitans.mifinca.domain.services;

import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.dtos.CreateRatingDTO;
import com.techtitans.mifinca.domain.dtos.CreateRentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.OwnerRentaRequestDTO;
import com.techtitans.mifinca.domain.dtos.RentalRequestDTO;
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

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RentalService {

    private ScheduleRepository repo;
    private PropertiesService propertiesService;
    private RatingService ratingService;

    public RentalService(
        ScheduleRepository repo,
        PropertiesService propertiesService,
        RatingService ratingService
    ){
        this.repo = repo;
        this.propertiesService = propertiesService;
        this.ratingService = ratingService;
    }

    public List<ScheduleDTO> getPropertySchedules(UUID propertyId,SchedulesSearchFilter filter){
        propertiesService.checkPropertyExists(propertyId);
        List<ScheduleEntity> schedulesEntity = repo.findAllByPropertyId(propertyId,filter.month(), filter.year(), 10);

        List<ScheduleDTO> schedulesDTOS = new ArrayList<>();
        for(ScheduleEntity entity : schedulesEntity){
            schedulesDTOS.add(new ScheduleDTO(entity.getStartDate(), entity.getEndDate()));
        }
        return schedulesDTOS;
    }

    public List<RentalRequestDTO> getRentalRequests(Integer page, AuthDTO authDTO) {
        int limit = 10;
        int offset = page*limit - limit;
        List<ScheduleEntity> schedules;
        if(authDTO.role().equals(Roles.USER_ROLE)){
            schedules= repo.findRequestsByUserId(authDTO.userId(), limit, offset);
        }else{
            schedules=repo.findRequestsByOwnerId(authDTO.userId(), limit, offset);
        }

        List<RentalRequestDTO> dtos = new ArrayList<>();
        for(ScheduleEntity ent : schedules){
            String status = checkScheduleStatus(ent);
            //if the request is completed, it may be rated by 1 of themalready, we're going to check that
            if(status.equals(ScheduleStatus.COMPLETED)) status = checkIfRatedState(ent, Roles.USER_ROLE);
            dtos.add(new RentalRequestDTO(
                ent.getId(), ent.getProperty().getId(), ent.getProperty().getName(), 
                ent.getStartDate(), ent.getEndDate(), status, ent.getCreatedAt(), ent.getPrice()
            ));
        }
        return dtos;
    }

    public List<OwnerRentaRequestDTO> getOwnerRentalRequests(Integer page, AuthDTO authDTO) {
        if(!authDTO.role().equals(Roles.LANDLORD_ROLE)){
            throw new ApiException(ApiError.UNATHORIZED_TO_REQUEST);
        }
        int limit = 10;
        int offset = page*limit - limit;
        List<ScheduleEntity> schedules= repo.findRequestsByOwnerId(authDTO.userId(), limit, offset);

        List<OwnerRentaRequestDTO> dtos = new ArrayList<>();
        for(ScheduleEntity ent : schedules){
            String status = checkScheduleStatus(ent);
            if(status.equals(ScheduleStatus.COMPLETED)) status = checkIfRatedState(ent, Roles.USER_ROLE);
            dtos.add(new OwnerRentaRequestDTO(
                ent.getId(), ent.getProperty().getId(), ent.getUser().getId(),
                ent.getUser().getNames(), ent.getProperty().getName(), ent.getStartDate(),
                ent.getEndDate(), ent.getCreatedAt(), status, ent.getPrice()
            ));
        }
        return dtos;
    }

    public void acceptRequest(UUID requestId, AuthDTO authDTO) {
        ScheduleEntity schedule = checkIfAbleToEditStatus(requestId, authDTO);
        schedule.setScStatus(ScheduleStatus.APPROVED);
        repo.save(schedule);
    }

    public void cancelRequest(UUID requestId, AuthDTO authDTO) {
        ScheduleEntity schedule = checkIfAbleToEditStatus(requestId, authDTO);
        schedule.setScStatus(ScheduleStatus.DENIED);
        repo.save(schedule);
    }

    public void createRentalRequest(UUID propertyId,CreateRentalRequestDTO dto, AuthDTO authDTO) {
        //if user is  a landlord he cant do this
        if(authDTO.role().equals(Roles.LANDLORD_ROLE)){
            throw new ApiException(ApiError.UNATHORIZED_TO_REQUEST);
        }
        if(dto.startDate() == null || dto.endDate() == null){
            throw new ApiException(ApiError.EMPTY_FIELDS);
        }

        //checking if the number of people is in the range
        if(!propertiesService.validatePropertyGuests(propertyId, dto.nGuests())){
            throw new ApiException(ApiError.INVALID_PARAMETERS);
        }
        //check that the dates are correct
        if(dto.startDate().isBefore(LocalDate.now()) || dto.endDate().isBefore(dto.startDate())){
            throw new ApiException(ApiError.INVALID_SCHEDULE_DATES);
        }

        //we're going to check the availability of the property in the selected dates
        int year = dto.startDate().getYear();
        List<ScheduleEntity> schedules = repo.findAllByPropertyId(propertyId, null, year, 100);
        for(ScheduleEntity sch : schedules){
            if( 
                (!sch.getScStatus().equals(ScheduleStatus.DENIED) )&&  //if the sch is not canceled or denied and it collisions
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
            .numberPersons(dto.nGuests())
            .property(PropertyEntity.builder().id(propertyId).build())
            .user(AccountEntity.builder().id(authDTO.userId()).build())
            .price(propertiesService.getPropertyPrice(propertyId, ChronoUnit.DAYS.between(dto.startDate(), dto.endDate())))
            .build();
        repo.save(newSchedule);
    }

    private String checkScheduleStatus(ScheduleEntity entity){
        if(entity.getScStatus().equals(ScheduleStatus.APPROVED) && entity.getStartDate().isBefore(LocalDate.now())){
            return ScheduleStatus.LOST;
        }
        if(!entity.getScStatus().equals(ScheduleStatus.PAID)){
            return entity.getScStatus();
        }
        if(entity.getEndDate().isBefore(LocalDate.now())){
            return ScheduleStatus.COMPLETED;
        }
        if(entity.getStartDate().isBefore(LocalDate.now()) && entity.getEndDate().isAfter(LocalDate.now())){
            return ScheduleStatus.IN_COURSE;
        }
        return entity.getScStatus();
    }

    private ScheduleEntity checkIfAbleToEditStatus(UUID requestId, AuthDTO authDTO){
        //check request existance
        ScheduleEntity schedule = repo.findById(requestId).orElse(null);
        if(schedule == null){
            throw new ApiException(ApiError.REQUEST_NOT_FOUND);
        }
        //check if the owner is the owner
        if(!schedule.getProperty().getOwner().getId().equals(authDTO.userId())){
            throw new ApiException(ApiError.UNATHORIZED_TO_EDIT_REQUEST);
        }
        //check the current status of the request
        if(!schedule.getScStatus().equals(ScheduleStatus.REQUESTED)){
            throw new ApiException(ApiError.UNABLE_TO_EDIT_REQUEST);
        }
        return schedule;
    }

    private String checkIfRatedState(ScheduleEntity ent, String role){
        if(ent.getRatings().size() == 0){
            return ScheduleStatus.COMPLETED;
        }
        if(ent.getRatings().get(0).getType().equals(role)){
            return ScheduleStatus.RATED;
        }
        return ScheduleStatus.COMPLETED;
    }

    public ScheduleEntity getRentalRequestForPayment(UUID scheduleId){
        //check request existance
        ScheduleEntity schedule = repo.findById(scheduleId).orElse(null);
        if(schedule == null){
            throw new ApiException(ApiError.REQUEST_NOT_FOUND);
        }
        String status = checkScheduleStatus(schedule);
        if(status.equals(ScheduleStatus.APPROVED)){
            return schedule;
        }
        throw new ApiException(ApiError.REQUEST_ISNT_IN_PAYMENT);
    
    }

    public ScheduleEntity getRentalRequestForRating(UUID scheduleId){
        ScheduleEntity schedule = repo.findById(scheduleId).orElse(null);
        if(schedule == null){
            throw new ApiException(ApiError.REQUEST_NOT_FOUND);
        }
        String status = checkScheduleStatus(schedule);
        if(status.equals(ScheduleStatus.RATED)){
            throw new ApiException(ApiError.ALREADY_RATED);
        }
        if(!status.equals(ScheduleStatus.COMPLETED)){
            throw new ApiException(ApiError.CANT_RATE_YET);
        }
        return schedule;
    }

    public void updatePaidRentalRequest(UUID scheduleId){
        ScheduleEntity sch = getRentalRequestForPayment(scheduleId);
        sch.setScStatus(ScheduleStatus.PAID);
        repo.save(sch);
    }

    public void addRating(UUID requestId, CreateRatingDTO dto, AuthDTO authDTO){
        //this method takes care of checking if the shcedule can be rated yet
        ScheduleEntity sch = getRentalRequestForRating(requestId);
        int nRates = sch.getRatings().size();
        var rating = ratingService.addRating(sch, dto, authDTO);

        //checking if with this rating both landlord and user have now rated the scheduling
        if(nRates == 1){
            sch.setScStatus(ScheduleStatus.RATED);
            repo.save(sch);
        }
        sch.getRatings().add(rating);
    }
}