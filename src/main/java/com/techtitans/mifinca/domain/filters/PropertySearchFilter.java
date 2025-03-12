package com.techtitans.mifinca.domain.filters;

import java.util.UUID;


import lombok.Builder;

@Builder
public record PropertySearchFilter (
    UUID ownerId,
    String nameText,
    String department,
    Integer nRooms,
    Integer nPeople,
    Double minPrice,
    Double maxPrice,
    int page
){}
