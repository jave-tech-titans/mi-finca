package com.techtitans.mifinca.domain.services;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techtitans.mifinca.domain.entities.Roles;
import com.techtitans.mifinca.repository.RatingRepository;

@Service
public class RatingService {
    
    @Autowired
    private RatingRepository repo;

    public Double getPropertyRating(UUID propertyId){
        return repo.getPropertyRating(propertyId, Roles.LANDLORD_ROLE);
    }
}
