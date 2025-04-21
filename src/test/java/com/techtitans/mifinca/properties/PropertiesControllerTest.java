package com.techtitans.mifinca.properties;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.dtos.CreatePropertyDTO;
import com.techtitans.mifinca.domain.dtos.FullPropertyDTO;
import com.techtitans.mifinca.domain.dtos.PropertyTileDTO;
import com.techtitans.mifinca.domain.dtos.UpdatePropertyDTO;
import com.techtitans.mifinca.domain.entities.AccountEntity;
import com.techtitans.mifinca.domain.entities.FileEntity;
import com.techtitans.mifinca.domain.entities.PropertyEntity;
import com.techtitans.mifinca.domain.entities.Roles;
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
class PropertiesControllerTest {

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
        storageService = new StorageService(fileRepo, "http://localhost:9090");
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
    void postProperty_Success(){
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
    void postProperty_NotOwner(){
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
    ///                     GET PROPERTIES TEST                                                                  //////////////////////////
    
    @Test 
    void getProperties_FilterSuccess(){
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


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                     GET PROPERTY TEST                                                                  //////////////////////////
    
    @Test
    void getProperty_Success() {
        UUID propId = UUID.randomUUID();
        double mockRating = 4.5;
        AccountEntity owner = AccountEntity.builder()
            .id(UUID.randomUUID())
            .build();

        PropertyEntity propertyEntity = PropertyEntity.builder()
            .id(propId)
            .name("Finca 1")
            .department("Meta")
            .description("A beautiful property")
            .enterType("ENTERTYPE")
            .hasAsador(true)
            .hasPool(false)
            .isPetFriendly(true)
            .numberBathrooms(2)
            .numberRooms(3)
            .nightPrice(1200.0)
            .owner(owner)
            .build();

        when(repo.findById(propId)).thenReturn(Optional.of(propertyEntity));
        when(ratingRepo.getPropertyRating(propId, Roles.LANDLORD_ROLE)).thenReturn(mockRating);
        when(fileRepo.findByPropertyId(propId)).thenReturn(List.of(
            FileEntity.builder().url("picture1.jpg").build(),
            FileEntity.builder().url("picture2.jpg").build()
        ));

        FullPropertyDTO result = controller.getProperty(propId);

        assertNotNull(result);
        assertEquals(propId, result.id());
        assertEquals("Finca 1", result.name());
        assertEquals("Meta", result.department());
        assertEquals("A beautiful property", result.description());
        assertEquals("ENTERTYPE", result.enterType());
        assertTrue(result.hasAsador());
        assertFalse(result.hasPool());
        assertTrue(result.isPetFriendly());
        assertEquals(2, result.numberBathrooms());
        assertEquals(3, result.numberRooms());
        assertEquals(1200.0, result.nightPrice());
        assertEquals(owner.getId(), result.ownerId());
        assertEquals(mockRating, result.rating());
        assertEquals(2, result.pictures().size());
        assertEquals("http://localhost:9090/api/v1/files/picture1.jpg", result.pictures().get(0));
        assertEquals("http://localhost:9090/api/v1/files/picture2.jpg", result.pictures().get(1));
    }

    @Test
    void getProperty_NotFound() {
        UUID propId = UUID.randomUUID();
        when(repo.findById(propId)).thenReturn(Optional.empty());
        ApiException thrown = assertThrows(ApiException.class, 
            () -> controller.getProperty(propId)
        );
        assertEquals(ApiError.PROPERTY_NOT_FOUND, thrown.getError());
    }

    @Test
    void getProperty_RatingIsNull() {
        UUID propId = UUID.randomUUID();
        PropertyEntity propertyEntity = PropertyEntity.builder()
            .id(propId)
            .name("Finca Null Rating")
            .department("Unknown")
            .owner(AccountEntity.builder().id(UUID.randomUUID()).build())
            .build();

        when(repo.findById(propId)).thenReturn(Optional.of(propertyEntity));
        when(ratingRepo.getPropertyRating(propId, Roles.LANDLORD_ROLE)).thenReturn(null);
        when(fileRepo.findByPropertyId(propId)).thenReturn(Collections.emptyList());

        FullPropertyDTO result = controller.getProperty(propId);

        assertNotNull(result);
        assertNull(result.rating());
        assertEquals(0, result.pictures().size());
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                     UPDATE PROPERTY TEST                                                                  //////////////////////////

    @Test
    void updateProperty_Success() {
        UUID propId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        PropertyEntity existingProperty = PropertyEntity.builder()
            .id(propId)
            .owner(AccountEntity.builder().id(ownerId).build())
            .department("OldDept")
            .build();

        when(repo.findById(propId)).thenReturn(Optional.of(existingProperty));
        when(repo.save(any(PropertyEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdatePropertyDTO updateDTO = new UpdatePropertyDTO(
            "Updated Name", 
            "Meta",         
            "new type",           
            "new desc",      
            10,           
            10,          
            true,          
            true,         
            true,          
            50000.0     
        );

        AuthDTO authDTO = new AuthDTO(ownerId, "LANDLORD");

        assertDoesNotThrow(() -> controller.updateProperty(propId, updateDTO, authDTO));

        verify(repo, times(1)).save(any(PropertyEntity.class));
        ArgumentCaptor<PropertyEntity> captor = ArgumentCaptor.forClass(PropertyEntity.class);
        verify(repo).save(captor.capture());
        PropertyEntity savedEntity = captor.getValue();
        assertEquals("Updated Name", savedEntity.getName());
        assertEquals("Meta", savedEntity.getDepartment());
    }

    @Test
    void updateProperty_InvalidDepartment() {
        UUID propId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        PropertyEntity existingProperty = PropertyEntity.builder()
            .id(propId)
            .owner(AccountEntity.builder().id(ownerId).build())
            .department("OldDept")
            .build();

        when(repo.findById(propId)).thenReturn(Optional.of(existingProperty));

        UpdatePropertyDTO updateDTO = new UpdatePropertyDTO(
            "Updated Name", 
            "INVALID DEP",         
            "new type",           
            "new desc",      
            10,           
            10,          
            true,          
            true,         
            true,          
            50000.0     
        );

        AuthDTO authDTO = new AuthDTO(ownerId, "LANDLORD");

        ApiException ex = assertThrows(ApiException.class,
            () -> controller.updateProperty(propId, updateDTO, authDTO));
        assertEquals(ApiError.INVALID_DEPARTMENT, ex.getError());

        verify(repo, never()).save(any(PropertyEntity.class));
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                     DEACTIVATE PROPERTY TEST                                                                  //////////////////////////
    
    @Test
    void deactivateProperty_Success() {
        UUID propId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        PropertyEntity existingProperty = PropertyEntity.builder()
                .id(propId)
                .owner(AccountEntity.builder().id(ownerId).build())
                .build();

        when(repo.findById(propId)).thenReturn(Optional.of(existingProperty));
        AuthDTO validAuth = new AuthDTO(ownerId, "LANDLORD");
        assertDoesNotThrow(() -> controller.deactivateProperty(propId, validAuth));

        verify(repo, times(1)).deleteById(propId);
    }

    @Test
    void deactivateProperty_UserNotOwner() {
        UUID propId = UUID.randomUUID();
        UUID actualOwnerId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();
        PropertyEntity existingProperty = PropertyEntity.builder()
                .id(propId)
                .owner(AccountEntity.builder().id(actualOwnerId).build())
                .build();

        when(repo.findById(propId)).thenReturn(Optional.of(existingProperty));
        AuthDTO invalidAuth = new AuthDTO(otherUserId, "USER");
        ApiException ex = assertThrows(ApiException.class,
                () -> controller.deactivateProperty(propId, invalidAuth));

        assertEquals(ApiError.UNATHORIZED_TO_EDIT_PROPERTY, ex.getError());

        verify(repo, never()).deleteById(any());
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                     GET DEPARTMENTS TEST                                                                //////////////////////////
    
    @Test
    void getDepartments_Success() {
       var result = controller.getDepartments();

       assertTrue(result.contains("Meta"));
       assertTrue(result.contains("Amazonas"));
       assertTrue(result.contains("Antioquia"));
       assertTrue(result.contains("Córdoba"));
       assertTrue(result.contains("Putumayo"));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                     UPLOAD PICTURE TESTS                                                                //////////////////////////
     
    @Test
    void uploadPicture_Success() throws Exception {
        UUID propId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        PropertyEntity property = PropertyEntity.builder()
            .id(propId)
            .owner(AccountEntity.builder().id(ownerId).build())
            .build();
        when(repo.findById(propId)).thenReturn(Optional.of(property));

        MockMultipartFile multipartFile = new MockMultipartFile(
            "picture", "test.jpg", "image/jpeg", "dummy content".getBytes()
        );
        AuthDTO auth = new AuthDTO(ownerId, "LANDLORD");
        controller.uploadPicture(propId, multipartFile, auth);

        assertDoesNotThrow(()->controller.uploadPicture(propId, multipartFile, auth));
    }

    @Test
    void uploadPicture_Unauthorized(){
        UUID propId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        PropertyEntity property = PropertyEntity.builder()
            .id(propId)
            .owner(AccountEntity.builder().id(UUID.randomUUID()).build()) 
            .build();
        when(repo.findById(propId)).thenReturn(Optional.of(property));

        MockMultipartFile multipartFile = new MockMultipartFile(
            "picture", "test.jpg", "image/jpeg", "dummy content".getBytes()
        );
        AuthDTO auth = new AuthDTO(ownerId, "LANDLORD"); 

        ApiException ex = assertThrows(ApiException.class,
            () -> controller.uploadPicture(propId, multipartFile, auth)
        );
        assertEquals(ApiError.UNATHORIZED_TO_EDIT_PROPERTY, ex.getError());
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                     GET MY PROPERTIES                                                                  //////////////////////////
    
    @Test
    void getMyProperties_Success() {
        UUID ownerId = UUID.randomUUID();
        AuthDTO auth = new AuthDTO(ownerId, "LANDLORD");
        int page = 1;  // default page
        UUID propId1 = UUID.randomUUID();
        PropertyEntity property1 = PropertyEntity.builder()
            .id(propId1)
            .name("Finca 1")
            .department("Meta")
            .numberRooms(3)
            .nightPrice(1200)
            .pictures(new ArrayList<>()) 
            .build();

        UUID propId2 = UUID.randomUUID();
        FileEntity fileEntity = FileEntity.builder()
            .id(UUID.randomUUID())
            .url("picture1.jpg")
            .build();
        List<FileEntity> pictures = new ArrayList<>();
        pictures.add(fileEntity);
        PropertyEntity property2 = PropertyEntity.builder()
            .id(propId2)
            .name("Finca 2")
            .department("Meta")
            .numberRooms(4)
            .nightPrice(1500)
            .pictures(pictures)
            .build();

        when(repo.findAllWithFilters(
                any(), any(), any(), any(), any(), any(), eq(ownerId), eq(0), eq(10)
        )).thenReturn(List.of(property1, property2));

        when(ratingRepo.getPropertyRating(propId1, Roles.LANDLORD_ROLE)).thenReturn(4.5);
        when(ratingRepo.getPropertyRating(propId2, Roles.LANDLORD_ROLE)).thenReturn(null);

        List<PropertyTileDTO> result = controller.getMyProperties(auth, page);

        assertNotNull(result);
        assertEquals(2, result.size());

        PropertyTileDTO dto1 = result.get(0);
        assertEquals(propId1, dto1.id());
        assertEquals("Finca 1", dto1.name());
        assertEquals("Meta", dto1.department());
        assertEquals(staticImage, dto1.imageUrl());
        assertEquals(3, dto1.nRooms());
        assertEquals(12, dto1.nPeople()); // 3 rooms * 4 persons each
        assertEquals(1200, dto1.price());
        assertEquals(4.5, dto1.rating());

        PropertyTileDTO dto2 = result.get(1);
        assertEquals(propId2, dto2.id());
        assertEquals("Finca 2", dto2.name());
        assertEquals("Meta", dto2.department());
        String expectedUrl = "http://localhost:9090" + "/api/v1/files/" + fileEntity.getUrl();
        assertEquals(expectedUrl, dto2.imageUrl());
        assertEquals(4, dto2.nRooms());
        assertEquals(16, dto2.nPeople());
        assertEquals(1500, dto2.price());
        assertNull(dto2.rating());
    }

    @Test
    void getMyProperties_EmptyList() {
        UUID ownerId = UUID.randomUUID();
        AuthDTO auth = new AuthDTO(ownerId, "LANDLORD");
        int page = 1;
        when(repo.findAllWithFilters(
                any(), any(), any(), any(), any(), any(), eq(ownerId), eq(0), eq(10)
        )).thenReturn(Collections.emptyList());

        List<PropertyTileDTO> result = controller.getMyProperties(auth, page);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
