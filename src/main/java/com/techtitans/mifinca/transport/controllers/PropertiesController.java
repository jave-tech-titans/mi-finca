package com.techtitans.mifinca.transport.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techtitans.mifinca.domain.dtos.CreatePropertyDTO;
import com.techtitans.mifinca.domain.dtos.DeactivatePropertyDTO;
import com.techtitans.mifinca.domain.dtos.PhotoUploadDTO;
import com.techtitans.mifinca.domain.dtos.PropertySearchFilterDTO;
import com.techtitans.mifinca.domain.dtos.UpdatePropertyDTO;
import com.techtitans.mifinca.domain.services.AccountService;
import com.techtitans.mifinca.domain.services.PropertiesService;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertiesController {
    
    @Autowired 
    private PropertiesService service;

    //temporary, IS TEMPORARY WHILE WE implement Access Tokens
    @Autowired
    private AccountService usService;   

    @GetMapping("/departments")
    public List<String> getDepartments(){
        return service.retrieveDepartments();
    } 

    @PostMapping
    public void postProperty(@RequestBody CreatePropertyDTO body){
        //only termporary 
        var userId = usService.getRandomUserUUID();
        service.createProperty(body, userId);
    } 

   /*  @GetMapping
    public List<?> getProperties(
        @RequestParam String name,
        @RequestParam String department, 
        @RequestParam String enterType,
        @RequestParam int nRooms,
        @RequestParam int nBathrooms,
        @RequestParam boolean isPetFriendly,
        @RequestParam boolean hasPool,
        @RequestParam boolean hasAsador,
        @RequestParam double minPrice,
        @RequestParam double maxPrice
    ) {
        return service.searchProperties(body);
    }

    @PutMapping("/{property_id}")
    public void updateProperty(@PathVariable Long property_id, @RequestBody UpdatePropertyDTO body) {
        service.updateProperty(property_id, body);
    }

    @PostMapping("/{property_id}/photos")
    public void uploadPhoto(@PathVariable Long property_id, @RequestBody PhotoUploadDTO body) {
        service.uploadPhoto(property_id, body);
    }

    @PatchMapping("/{property_id}/deactivate")
    public void deactivateProperty(@PathVariable Long property_id, @RequestBody DeactivatePropertyDTO body) {
        service.deactivateProperty(property_id, body);
    }

    // Nuevo método para el Endpoint 16
    @GetMapping("/search")
    public List<?> searchProperties(@RequestBody PropertySearchFilterDTO body) {
        return service.searchPropertiesByFilter(body);
    }*/

}
