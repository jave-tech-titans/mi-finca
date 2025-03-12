package com.techtitans.mifinca.domain.dtos;

import java.time.LocalDate;

public record CreateRentalRequestDTO(
    LocalDate startDate, 
    LocalDate endDate, 
    Integer nGuests
) {}
