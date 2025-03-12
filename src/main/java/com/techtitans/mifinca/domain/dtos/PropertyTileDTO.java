package com.techtitans.mifinca.domain.dtos;

import java.util.UUID;

public record PropertyTileDTO (
    UUID id,
    String name,
    String department,
    String imageUrl,
    int nRooms,
    int nPeople,
    double price,
    Double rating
){}
