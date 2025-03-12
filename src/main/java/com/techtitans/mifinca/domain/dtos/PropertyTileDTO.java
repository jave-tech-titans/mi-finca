package com.techtitans.mifinca.domain.dtos;

public record PropertyTileDTO (
    String name,
    String department,
    String imageUrl,
    int nRooms,
    int nPeople,
    double price
){}
