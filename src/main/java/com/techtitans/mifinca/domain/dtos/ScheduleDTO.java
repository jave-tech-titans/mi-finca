package com.techtitans.mifinca.domain.dtos;

import java.time.LocalDate;

public record ScheduleDTO(
    LocalDate startDate,
    LocalDate endDate
) {}
