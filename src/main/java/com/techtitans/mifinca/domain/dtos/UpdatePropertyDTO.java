package com.techtitans.mifinca.domain.dtos;

public record UpdatePropertyDTO (
    String name,
    String department,
    String municipality,
    String access_type,
    String description,
    Integer numberRooms,
    Integer numberBathrooms,
    boolean isPetFriendly,
    boolean hasPool,
    boolean hasAsador,
    double nightPrice
){}