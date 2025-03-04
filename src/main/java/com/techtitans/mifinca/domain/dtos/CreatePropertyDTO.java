package com.techtitans.mifinca.domain.dtos;

public record CreatePropertyDTO (
    String name,
    String department,
    String enterType,
    String description,
    int numberRooms,
    int numberBathrooms,
    boolean isPetFriendly,
    boolean hasPool,
    boolean hasAsador,
    double nightPrice
){}
