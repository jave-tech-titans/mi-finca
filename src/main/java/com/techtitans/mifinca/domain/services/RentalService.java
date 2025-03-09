package com.techtitans.mifinca.domain.services;

import com.techtitans.mifinca.domain.dtos.AcceptRentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.CancelRentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.CreateRentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.RentalRequestFilterDTO;
import com.techtitans.mifinca.domain.dtos.RentalRequestTenantFilterDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RentalService {

    public List<?> getRentalRequests(RentalRequestFilterDTO body) {
        return List.of(); // Temporal
    }

    public void acceptRequest(Long request_id, AcceptRentalRequestDTO body) {
        // aceptar solicitud
    }

    public void cancelRequest(Long request_id, CancelRentalRequestDTO body) {
        // cancelar solicitud
    }

    public void createRentalRequest(CreateRentalRequestDTO body) {
        // crear solicitud
    }

    public List<?> getTenantRentalRequests(RentalRequestTenantFilterDTO body) {
        return List.of(); // Temporal
    }
}