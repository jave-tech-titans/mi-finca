package com.techtitans.mifinca.domain.services;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.dtos.CreatePropertyDTO;
import com.techtitans.mifinca.domain.dtos.PropertyTileDTO;
import com.techtitans.mifinca.domain.entities.AccountEntity;
import com.techtitans.mifinca.domain.entities.PropertyEntity;
import com.techtitans.mifinca.domain.entities.Roles;
import com.techtitans.mifinca.domain.exceptions.ApiError;
import com.techtitans.mifinca.domain.exceptions.ApiException;
import com.techtitans.mifinca.domain.filters.PropertySearchFilter;
import com.techtitans.mifinca.repository.PropertyRepository;
import com.techtitans.mifinca.utils.Helpers;

@Service
public class PropertiesService {
    @Autowired
    private PropertyRepository repo; 

    @Autowired
    private StorageService storageService;

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

    //method to retrieve all the valid departments based on a third party API
    public List<String> retrieveDepartments(){
        var departments = new ArrayList<String>();
        departments.addAll(getDepartments());
        return departments;
    }

    //method to create a new property
    public void createProperty(CreatePropertyDTO dto, AuthDTO authDTO){
        //if user is not a landlord then he cant create a property
        if(!authDTO.role().equals(Roles.landlordRole())){
            throw new ApiException(ApiError.UNATHORIZED_TO_POST_PROPERTY);
        }
        if(!Helpers.validateStrings(List.of(
            dto.name() == null ? "" : dto.name(), 
            dto.department() == null ? "" : dto.department(),
            dto.enterType() == null ? "" : dto.enterType(), 
            dto.description() == null ? "" : dto.description()
        ))){
            throw new ApiException(ApiError.EMPTY_FIELDS);
        }
        if(!getDepartments().contains(dto.department())){
            throw new ApiException(ApiError.INVALID_DEPARTMENT);
        }
        if(dto.numberBathrooms()<0 || dto.numberRooms() <0 || dto.nightPrice() <0 ){
            throw new ApiException(ApiError.INVALID_PARAMETERS);
        }
        //if everything was fine, then we create the property
        var property = PropertyEntity.fromCreateDTO(dto);
        property.setCreatedAt(LocalDateTime.now());
        property.setUpdatedAt(LocalDateTime.now());
        property.setOwner(AccountEntity.fromId(authDTO.userId()));
        repo.save(property);
    }

    //method to add pictures to a property
    public void uploadPicture(UUID propertyId, String picName, InputStream file, AuthDTO auth){
        PropertyEntity prop = repo.findById(propertyId).orElse(null);
        //checking if  the user owns the property, if not then F
        if(!prop.getOwner().getId().equals(auth.userId())){
            throw new ApiException(ApiError.UNATHORIZED_TO_EDIT_PROPERTY);
        }
        //if the user was the owner, then upload picture
        Path path = Paths.get("properties", propertyId.toString(), picName);
        storageService.saveFile(prop, path.toString(), file);
    }

    //method to retrieve properties
    public List<PropertyTileDTO> getProperties(PropertySearchFilter filter){
        Integer minRooms = null, maxRooms=null;
        if(filter.nPeople() != null){
            maxRooms = filter.nPeople()/2;    //we are saying that at much we will leave only 2 persons per room, not less
            minRooms = filter.nPeople()/4; //we are saying that at much we will fit 4 persons per room, not more
        }
        if(filter.nRooms() != null){
            maxRooms = filter.nRooms();
            minRooms = filter.nRooms();
        }
        
        int limit = 10;
        int offset = filter.page()*limit - limit;
        final var properties = repo.findAllWithFilters(
            filter.nameText(), 
            filter.department(), 
            minRooms, 
            maxRooms, 
            filter.minPrice(), 
            filter.maxPrice(),
            filter.ownerId(),
            offset,
            limit
        );

        List<PropertyTileDTO> dtos = new ArrayList<>();
        for(PropertyEntity entity : properties){
            String imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSp5tDd3SPc1pivi7vtApxl81a9zQh_rUehVw&s";
            if(entity.getPictures().size()>0){
                imageUrl = storageService.getUrl(entity.getPictures().get(0));
            }
            var dto = new PropertyTileDTO(
                entity.getName(), entity.getDepartment(), 
                imageUrl, entity.getNumberRooms(), 
                entity.getNumberRooms()*4,
                entity.getNightPrice()
            );
            dtos.add(dto);
        }
        return dtos;
    }
}
