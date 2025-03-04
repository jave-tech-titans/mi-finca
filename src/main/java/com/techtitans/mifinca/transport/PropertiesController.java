package com.techtitans.mifinca.transport;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.techtitans.mifinca.domain.dtos.CreatePropertyDTO;
import com.techtitans.mifinca.domain.services.AccountService;
import com.techtitans.mifinca.domain.services.PropertiesService;

@RestController
@RequestMapping("/properties")
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


}
