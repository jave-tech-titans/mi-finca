package com.techtitans.mifinca.domain.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record OwnerRentaRequestDTO(
    UUID id,
    UUID propertyId,
    UUID userId,
    String userName,
    String propertyName,
    LocalDate startDate,
    LocalDate endDate,
    LocalDateTime requestedAt,
    String status,
    double price
) {}
