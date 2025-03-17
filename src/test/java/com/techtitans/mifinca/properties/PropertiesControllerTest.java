package com.techtitans.mifinca.properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.dtos.CreatePropertyDTO;
import com.techtitans.mifinca.domain.dtos.PropertyTileDTO;
import com.techtitans.mifinca.domain.entities.PropertyEntity;
import com.techtitans.mifinca.domain.exceptions.ApiError;
import com.techtitans.mifinca.domain.exceptions.ApiException;
import com.techtitans.mifinca.domain.services.PropertiesService;
import com.techtitans.mifinca.domain.services.RatingService;
import com.techtitans.mifinca.domain.services.StorageService;
import com.techtitans.mifinca.repository.FileRepository;
import com.techtitans.mifinca.repository.PropertyRepository;
import com.techtitans.mifinca.repository.RatingRepository;
import com.techtitans.mifinca.transport.controllers.PropertiesController;

@ExtendWith(MockitoExtension.class)
public class PropertiesControllerTest {

    @Mock
    private PropertyRepository repo;
    @Mock 
    private FileRepository fileRepo;
    @Mock 
    private RatingRepository ratingRepo;

    
    private StorageService storageService;
    private RatingService ratingService;
    private PropertiesService propService;
    private PropertiesController controller;

    private AuthDTO ownerValidAuth;
    private AuthDTO userValidAuth;

    private String staticImage="https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcSp5tDd3SPc1pivi7vtApxl81a9zQh_rUehVw&s";
    
    
    @BeforeEach
    void setUp(){
        //injecting dependencies
        storageService = new StorageService(fileRepo, "localhost:9090");
        ratingService = new RatingService(ratingRepo);
        propService = new PropertiesService(repo, storageService, ratingService);
        controller = new PropertiesController(propService);

        //initializing data
        ownerValidAuth = new AuthDTO(UUID.randomUUID(), "LANDLORD");
        userValidAuth = new AuthDTO(UUID.randomUUID(), "USER");
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                     POST PROPERTY TEST                                                                  //////////////////////////
    
    @Test 
    public void postProperty_Success(){
        //mocking repo response
        when(repo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        CreatePropertyDTO createPropertyDTO = new CreatePropertyDTO(
            "prueba", "Meta", "por la calle", 
            "es bonita", 6, 6, 
            true, false, true, 5000
        );
        //assert
        assertDoesNotThrow(() ->  controller.postProperty(createPropertyDTO, ownerValidAuth));
    }

    @Test 
    public void postProperty_NotOwner(){
        CreatePropertyDTO createPropertyDTO = new CreatePropertyDTO(
            "prueba", "Meta", "por la calle", 
            "es bonita", 6, 6, 
            true, false, true, 5000
        );
        ApiException ex = assertThrows(ApiException.class, ()->controller.postProperty(createPropertyDTO, userValidAuth));
        assertEquals(ApiError.UNATHORIZED_TO_POST_PROPERTY, ex.getError());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                     POST PROPERTY TEST                                                                  //////////////////////////
    
    @Test 
    public void getProperties_FilterSuccess(){
        //expected
        UUID[] ids = new UUID[]{UUID.randomUUID(),UUID.randomUUID(),UUID.randomUUID(),UUID.randomUUID(),UUID.randomUUID()};
        var expected = List.of(
            new PropertyTileDTO(ids[0], "Finca 1","Meta",  staticImage,2, 8, 1200, null),
            new PropertyTileDTO(ids[1], "Finca 2","Meta",  staticImage,5, 20, 1500, null),
            new PropertyTileDTO(ids[2], "Finca 3","Meta",  staticImage,6, 24, 3500, null),
            new PropertyTileDTO(ids[3], "Finca 4","Meta",  staticImage,1, 4, 4555, null),
            new PropertyTileDTO(ids[4], "Finca 5","Meta",  staticImage,4, 16, 4999, null)
        );
        //imitating repo response
        var repoReturn = List.of(
            PropertyEntity.builder().id(ids[0]).name("Finca 1").pictures(new ArrayList<>()).department("Meta").numberRooms(2).nightPrice(1200).build(),
            PropertyEntity.builder().id(ids[1]).name("Finca 2").pictures(new ArrayList<>()).department("Meta").numberRooms(5).nightPrice(1500).build(),
            PropertyEntity.builder().id(ids[2]).name("Finca 3").pictures(new ArrayList<>()).department("Meta").numberRooms(6).nightPrice(3500).build(),
            PropertyEntity.builder().id(ids[3]).name("Finca 4").pictures(new ArrayList<>()).department("Meta").numberRooms(1).nightPrice(4555).build(),
            PropertyEntity.builder().id(ids[4]).name("Finca 5").pictures(new ArrayList<>()).department("Meta").numberRooms(4).nightPrice(4999).build()
        );

        when(ratingRepo.getPropertyRating(any(), eq("LANDLORD"))).thenReturn(null);
        when(repo.findAllWithFilters(null, null, null, null, 1000.0, 5000.0, null, 0, 10)).thenReturn(repoReturn);

        var properties = controller.getProperties(null, null, null, null, 1000.0, 5000.0, 1);
        
        assertEquals(expected, properties);
    }

}
