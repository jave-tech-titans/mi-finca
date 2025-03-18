package com.techtitans.mifinca.transport.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.dtos.CreatePropertyDTO;
import com.techtitans.mifinca.domain.dtos.FullPropertyDTO;
import com.techtitans.mifinca.domain.dtos.PropertyTileDTO;
import com.techtitans.mifinca.domain.dtos.UpdatePropertyDTO;
import com.techtitans.mifinca.domain.filters.PropertySearchFilter;
import com.techtitans.mifinca.domain.services.PropertiesService;

@RestController
@RequestMapping("/api/v1/properties")
public class PropertiesController {
    
    private PropertiesService service;

    public PropertiesController(PropertiesService service){
        this.service = service;
    }


    @GetMapping("/departments")
    public List<String> getDepartments(){
        return service.retrieveDepartments();
    } 

    @PostMapping
    public void postProperty(@RequestBody CreatePropertyDTO body, @RequestAttribute("auth") AuthDTO authDTO){
        service.createProperty(body, authDTO);
    } 

    @GetMapping("/{property-id}")
    public FullPropertyDTO getProperty(@PathVariable("property-id") UUID propertyId){
        return service.getProperty(propertyId);
    }


    @GetMapping("/mine")
    public List<PropertyTileDTO> getMyProperties(
        @RequestAttribute("auth") AuthDTO auth,
        @RequestParam(defaultValue = "1") int page
    ){
        return service.getProperties(PropertySearchFilter
            .builder()
            .ownerId(auth.userId())
            .page(page)
            .build()
        );
    }

    @GetMapping
    public List<PropertyTileDTO> getProperties(
        @RequestParam(required =false) String name,
        @RequestParam(required =false) String department, 
        @RequestParam(required =false) Integer nRooms,
        @RequestParam(required =false) Integer nPeople,
        @RequestParam(required =false) Double minPrice,
        @RequestParam(required =false) Double maxPrice,
        @RequestParam(defaultValue = "1") int page
    ) {
        return service.getProperties(PropertySearchFilter
            .builder()
            .nameText(name)
            .department(department)
            .nRooms(nRooms)
            .nPeople(nPeople)
            .minPrice(minPrice)
            .maxPrice(maxPrice)
            .page(page).build()
        );
    }

    @PutMapping("/{property-id}")
    public void updateProperty(@PathVariable("property-id") UUID propertyId, @RequestBody UpdatePropertyDTO body, @RequestAttribute("auth") AuthDTO authDTO) {
        service.updateProperty(propertyId, body, authDTO);
    }

    @PostMapping("/{property-id}/pictures")
    public void uploadPicture(
        @PathVariable("property-id") UUID propertyId, 
        @RequestParam("picture") MultipartFile picture, 
        @RequestAttribute("auth") AuthDTO authDTO
    ) throws MalformedURLException, IOException{
        String picName = picture.getOriginalFilename();
        var fileStream = picture.getInputStream();
        service.uploadPicture(propertyId, picName, fileStream, authDTO);
    }

    @DeleteMapping("/{property-id}")
    public void deactivateProperty(@PathVariable("property-id") UUID propertyId, @RequestAttribute("auth") AuthDTO authDTO) {
        service.deleteProperty(propertyId, authDTO);
    }
}
