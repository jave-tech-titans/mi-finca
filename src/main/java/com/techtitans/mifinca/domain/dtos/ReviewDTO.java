package com.techtitans.mifinca.domain.dtos;

public record ReviewDTO(Long renter_id, Long tenant_id, Double rating, String comment, Long rental_id ) {
    
}
