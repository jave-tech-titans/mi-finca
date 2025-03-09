package com.techtitans.mifinca.transport;

import com.techtitans.mifinca.domain.dtos.AcceptRentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.CancelRentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.CreateRentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.RentalRequestFilterDTO;
import com.techtitans.mifinca.domain.dtos.RentalRequestTenantFilterDTO;
import com.techtitans.mifinca.domain.services.RentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rental-requests")
public class RentalController {

    @Autowired
    private RentalService rentalService;

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

    @PostMapping
    public void createRentalRequest(@RequestBody CreateRentalRequestDTO body) {
        rentalService.createRentalRequest(body);
    }

    @GetMapping("/tenant")
    public List<?> getTenantRentalRequests(@RequestBody RentalRequestTenantFilterDTO body) {
        return rentalService.getTenantRentalRequests(body);
    }
}