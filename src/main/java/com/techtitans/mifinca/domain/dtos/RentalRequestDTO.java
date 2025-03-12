package com.techtitans.mifinca.domain.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record RentalRequestDTO(
    UUID id,
    UUID propertyId,
    String propertyName,
    LocalDate startDate,
    LocalDate endDate,
    String status,
    LocalDateTime requestedAt
){
    
}
