package com.techtitans.mifinca.domain.services;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.dtos.CreateRatingDTO;
import com.techtitans.mifinca.domain.entities.RatingEntity;
import com.techtitans.mifinca.domain.entities.Roles;
import com.techtitans.mifinca.domain.entities.ScheduleEntity;
import com.techtitans.mifinca.domain.exceptions.ApiError;
import com.techtitans.mifinca.domain.exceptions.ApiException;
import com.techtitans.mifinca.repository.RatingRepository;

@Service
public class RatingService {
    
    private RatingRepository repo;

    public RatingService(RatingRepository repo){
        this.repo = repo;
    }
  
    public Double getPropertyRating(UUID propertyId){
        return repo.getPropertyRating(propertyId, Roles.LANDLORD_ROLE);
    }

    public RatingEntity addRating(ScheduleEntity sch, CreateRatingDTO dto, AuthDTO authDTO){
        //if the user has nothign to do with the schedule
        if(!authDTO.userId().equals(sch.getUser().getId()) && !authDTO.userId().equals(sch.getProperty().getOwner().getId())){
            throw new ApiException(ApiError.UNATHORIZED_TO_RATE);
        }
        String ratingWho = authDTO.userId().equals(sch.getUser().getId()) ? Roles.LANDLORD_ROLE : Roles.USER_ROLE;
        //checking if we already rated
        int nRates = sch.getRatings().size();
        if(nRates == 1 && sch.getRatings().get(0).getType().equals(ratingWho)){
            throw new ApiException(ApiError.ALREADY_RATED);
        }

        //if not rated yet, then store the rating
        RatingEntity rating = RatingEntity.builder()
            .comment(dto.comments())
            .createdAt(LocalDateTime.now())
            .rating(dto.rating())
            .type(ratingWho)
            .scheduling(sch)
            .build();
        
        return repo.save(rating);
    }
}
