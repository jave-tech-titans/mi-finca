package com.techtitans.mifinca.transport.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.dtos.CreateRatingDTO;
import com.techtitans.mifinca.domain.dtos.CreateRentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.OwnerRentaRequestDTO;
import com.techtitans.mifinca.domain.dtos.RentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.ScheduleDTO;
import com.techtitans.mifinca.domain.filters.SchedulesSearchFilter;
import com.techtitans.mifinca.domain.services.RentalService;

@RestController
@RequestMapping("/api/v1/rental")
public class RentalController {

    private RentalService rentalService;

    public RentalController(RentalService rentalService){
        this.rentalService = rentalService;
    }

    @GetMapping("/properties/{property-id}/schedules")
    public List<ScheduleDTO> getPropertySchedules(
        @PathVariable("property-id") UUID propertyId,
        @RequestParam(required = false) Integer month,
        @RequestParam(required = false) Integer year
    ){
        return rentalService.getPropertySchedules(propertyId, new SchedulesSearchFilter(month, year));
    }

    @GetMapping("/owner/requests")
    public List<OwnerRentaRequestDTO> getOwnerRentalRequests(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestAttribute("auth") AuthDTO authDTO
    ){
        return rentalService.getOwnerRentalRequests(page, authDTO);
    }

    @GetMapping("/requests")
    public List<RentalRequestDTO> getUserRentalRequests(
        @RequestParam(defaultValue = "1") Integer page,
        @RequestAttribute("auth") AuthDTO authDTO
    ) {
        return rentalService.getRentalRequests(page, authDTO);
    }

    @PatchMapping("/{request-id}/accept")
    public void acceptRentalRequest(
        @PathVariable("request-id") UUID requestId, 
        @RequestAttribute("auth") AuthDTO authDTO
    ) {
        rentalService.acceptRequest(requestId, authDTO);
    }

    @PatchMapping("/{request-id}/deny")
    public void cancelRentalRequest(
        @PathVariable("request-id") UUID requestId, 
        @RequestAttribute("auth") AuthDTO authDTO
    ) {
        rentalService.cancelRequest(requestId, authDTO);
    }

    @PostMapping("/properties/{property-id}/requests")
    public void createRentalRequest(
        @PathVariable("property-id") UUID propertyId,
        @RequestBody CreateRentalRequestDTO body, 
        @RequestAttribute("auth") AuthDTO authDTO
    ) {
        rentalService.createRentalRequest(propertyId,body, authDTO);
    }

    @PostMapping("/requests/{request-id}/ratings")
    public void createRating(
        @PathVariable("request-id") UUID requestId,
        @RequestBody CreateRatingDTO body,
        @RequestAttribute("auth") AuthDTO authDTO
    ) {
        rentalService.addRating(requestId, body, authDTO);
    }
}