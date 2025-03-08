package com.techtitans.mifinca.domain.dtos;

public record CreateRentalRequestDTO(Long property_id, Long tenant_id, String start_date, String end_date, Integer guests ) {
    
}
