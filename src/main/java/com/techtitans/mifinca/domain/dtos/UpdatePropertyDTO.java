package com.techtitans.mifinca.domain.dtos;

import lombok.Builder;

@Builder
public record UpdatePropertyDTO (
    String name,
    String department,
    String enterType,
    String description,
    Integer numberRooms,
    Integer numberBathrooms,
    Boolean isPetFriendly,
    Boolean hasPool,
    Boolean hasAsador,
    Double nightPrice
){}