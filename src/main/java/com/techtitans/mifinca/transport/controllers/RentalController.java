package com.techtitans.mifinca.transport.controllers;

import com.techtitans.mifinca.domain.dtos.AcceptRentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.dtos.CancelRentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.CreateRentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.RentalRequestFilterDTO;
import com.techtitans.mifinca.domain.dtos.RentalRequestTenantFilterDTO;
import com.techtitans.mifinca.domain.dtos.ScheduleDTO;
import com.techtitans.mifinca.domain.filters.SchedulesSearchFilter;
import com.techtitans.mifinca.domain.services.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/rental")
public class RentalController {

    @Autowired
    private RentalService rentalService;

    @GetMapping("/properties/{property-id}/schedules")
    public List<ScheduleDTO> getPropertySchedules(
        @PathVariable("property-id") UUID propertyId,
        @RequestParam(required = false) Integer month,
        @RequestParam(required = false) Integer year
    ){
        return rentalService.getPropertySchedules(propertyId, new SchedulesSearchFilter(month, year));
    }

    @GetMapping
    public List<?> getRentalRequests(@RequestBody RentalRequestFilterDTO body) {
        return rentalService.getRentalRequests(body);
    }

    @PatchMapping("/{request_id}/accept")
    public void acceptRentalRequest(@PathVariable Long request_id, @RequestBody AcceptRentalRequestDTO body) {
        rentalService.acceptRequest(request_id, body);
    }

    @PatchMapping("/{request_id}/cancel")
    public void cancelRentalRequest(@PathVariable Long request_id, @RequestBody CancelRentalRequestDTO body) {
        rentalService.cancelRequest(request_id, body);
    }

    @PostMapping("/properties/{property-id}/requests")
    public void createRentalRequest(
        @PathVariable("property-id") UUID propertyId,
        @RequestBody CreateRentalRequestDTO body, 
        @RequestAttribute("auth") AuthDTO authDTO
    ) {
        rentalService.createRentalRequest(propertyId,body, authDTO);
    }

    @GetMapping("/tenant")
    public List<?> getTenantRentalRequests(@RequestBody RentalRequestTenantFilterDTO body) {
        return rentalService.getTenantRentalRequests(body);
    }
}