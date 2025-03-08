package com.techtitans.mifinca.domain.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techtitans.mifinca.domain.dtos.CreatePropertyDTO;
import com.techtitans.mifinca.domain.entities.AccountEntity;
import com.techtitans.mifinca.domain.entities.PropertyEntity;
import com.techtitans.mifinca.repository.PropertyRepository;
import com.techtitans.mifinca.utils.Helpers;

@Service
public class PropertiesService {
    @Autowired
    private PropertyRepository repo; 

    //caching departments
    private Set<String> departments;

    //private method to get departments, if already loaded return them, if not retrieve them from API
    private Set<String> getDepartments(){
        if(departments != null){
            return departments;
        }
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api-colombia.com/api/v1/Department"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
               ObjectMapper mapper = new ObjectMapper();

               List<JsonNode> nodeList = mapper.readValue(response.body(), new TypeReference<List<JsonNode>>() {});
               return nodeList.stream().map((node)->node.get("name").toString().trim().replace("\"", "")).collect(Collectors.toSet());
            } else {
                return null;
            }
        } catch (Exception e) {
           return null;
        }
    }

    public List<String> retrieveDepartments(){
        var departments = new ArrayList<String>();
        departments.addAll(getDepartments());
        return departments;
    }

    public void createProperty(CreatePropertyDTO dto, UUID userId){
        if(!Helpers.validateStrings(List.of(dto.name(), dto.department(), dto.municipality(), dto.description()))){
            throw new RuntimeException("EMPTY_FIELDS");
        }

        if(!getDepartments().contains(dto.department())){
            throw new RuntimeException("INVALID_DEPARTMENT");
        }
        if(dto.numberBathrooms()<0 || dto.numberRooms() <0 || dto.nightPrice() <0 ){
            throw new RuntimeException("INVALID_PARAMETERS");
        }
        
        var property = PropertyEntity.fromCreateDTO(dto);
        property.setUser(AccountEntity.fromId(userId));
        repo.save(property);
    }
}
