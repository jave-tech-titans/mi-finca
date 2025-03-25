package com.techtitans.mifinca.rental;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.dtos.CreateRatingDTO;
import com.techtitans.mifinca.domain.dtos.CreateRentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.OwnerRentaRequestDTO;
import com.techtitans.mifinca.domain.dtos.RentalRequestDTO;
import com.techtitans.mifinca.domain.dtos.ScheduleDTO;
import com.techtitans.mifinca.domain.entities.AccountEntity;
import com.techtitans.mifinca.domain.entities.PropertyEntity;
import com.techtitans.mifinca.domain.entities.RatingEntity;
import com.techtitans.mifinca.domain.entities.Roles;
import com.techtitans.mifinca.domain.entities.ScheduleEntity;
import com.techtitans.mifinca.domain.entities.ScheduleStatus;
import com.techtitans.mifinca.domain.exceptions.ApiError;
import com.techtitans.mifinca.domain.exceptions.ApiException;
import com.techtitans.mifinca.domain.services.PropertiesService;
import com.techtitans.mifinca.domain.services.RatingService;
import com.techtitans.mifinca.domain.services.RentalService;
import com.techtitans.mifinca.domain.services.StorageService;
import com.techtitans.mifinca.repository.FileRepository;
import com.techtitans.mifinca.repository.PropertyRepository;
import com.techtitans.mifinca.repository.RatingRepository;
import com.techtitans.mifinca.repository.ScheduleRepository;
import com.techtitans.mifinca.transport.controllers.RentalController;

import io.jsonwebtoken.lang.Collections;

@ExtendWith(MockitoExtension.class)
class RenalControllerTest {
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private FileRepository fileRepository;  
    @Mock
    private RatingRepository ratingRepository;  

    private PropertiesService propertiesService;
    private PropertiesService spyPropertiesService;
    private RatingService ratingService;
    private RentalService rentalService;
    private RentalController rentalController;

    private UUID propertyId;

    @BeforeEach
    void setUp() {
        //services
        StorageService storageService = new StorageService(fileRepository, "http://localhost:9090");
        ratingService = new RatingService(ratingRepository);
        propertiesService = new PropertiesService(propertyRepository, storageService, ratingService);

        spyPropertiesService = Mockito.spy(propertiesService);
        rentalService = new RentalService(scheduleRepository, spyPropertiesService, ratingService);

        //controller
        rentalController = new RentalController(rentalService);

        propertyId = UUID.randomUUID();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                    GET SCHEDULES TESTS                                                                //////////////////////////

    @Test
    void getPropertySchedules_Success() {
        PropertyEntity existingProperty = PropertyEntity.builder()
            .id(propertyId)
            .build();
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(existingProperty));

        ScheduleEntity schedule1 = ScheduleEntity.builder()
            .id(UUID.randomUUID())
            .startDate(LocalDate.of(2025, 3, 20))
            .endDate(LocalDate.of(2025, 3, 22))
            .property(existingProperty)
            .build();

        ScheduleEntity schedule2 = ScheduleEntity.builder()
            .id(UUID.randomUUID())
            .startDate(LocalDate.of(2025, 4, 1))
            .endDate(LocalDate.of(2025, 4, 3))
            .property(existingProperty)
            .build();

        Integer month = 3;
        Integer year = 2025;

        when(scheduleRepository.findAllByPropertyId(propertyId, month, year, 10))
            .thenReturn(List.of(schedule1, schedule2));

        List<ScheduleDTO> result = rentalController.getPropertySchedules(propertyId, month, year);

        assertNotNull(result);
        assertEquals(2, result.size());

        ScheduleDTO dto1 = result.get(0);
        assertEquals(LocalDate.of(2025, 3, 20), dto1.startDate());
        assertEquals(LocalDate.of(2025, 3, 22), dto1.endDate());

        ScheduleDTO dto2 = result.get(1);
        assertEquals(LocalDate.of(2025, 4, 1), dto2.startDate());
        assertEquals(LocalDate.of(2025, 4, 3), dto2.endDate());
    }

    @Test
    void getPropertySchedules_EmptyList() {
        PropertyEntity existingProperty = PropertyEntity.builder()
            .id(propertyId)
            .build();
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.of(existingProperty));
        when(scheduleRepository.findAllByPropertyId(propertyId, null, null, 10))
            .thenReturn(Collections.emptyList());

        List<ScheduleDTO> result = rentalController.getPropertySchedules(propertyId, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getPropertySchedules_PropertyNotFound() {
        when(propertyRepository.findById(propertyId)).thenReturn(Optional.empty());

        ApiException ex = assertThrows(ApiException.class,
            () -> rentalController.getPropertySchedules(propertyId, null, null)
        );
        assertEquals(ApiError.PROPERTY_NOT_FOUND, ex.getError());

        verify(scheduleRepository, never()).findAllByPropertyId(any(), any(), any(), anyInt());
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                    GET OWNER REQUESTS TESTS                                                                //////////////////////////
    
    @Test
    void getOwnerRentalRequests_UnauthorizedIfNotLandlord() {
        AuthDTO authDTO = new AuthDTO(UUID.randomUUID(), "USER"); // Not a landlord
        ApiException ex = assertThrows(ApiException.class,
            () -> rentalController.getOwnerRentalRequests(1, authDTO)
        );
        assertEquals(ApiError.UNATHORIZED_TO_REQUEST, ex.getError());
        verify(scheduleRepository, never()).findRequestsByOwnerId(any(), anyInt(), anyInt());
    }

    @Test
    void getOwnerRentalRequests_SuccessWithSchedules() {
        UUID landlordId = UUID.randomUUID();
        AuthDTO authDTO = new AuthDTO(landlordId, "LANDLORD");

        int page = 2;
        int limit = 10;
        int offset = 10;

        ScheduleEntity schedule1 = ScheduleEntity.builder()
                .id(UUID.randomUUID())
                .scStatus(ScheduleStatus.PAID) 
                .startDate(LocalDate.now().minusDays(3)) 
                .endDate(LocalDate.now().minusDays(1))   
                .createdAt(LocalDateTime.now().minusDays(5))
                .price(200.0)
                .property(PropertyEntity.builder()
                        .id(UUID.randomUUID())
                        .name("Test Property")
                        .build())
                .user(AccountEntity.builder()
                        .id(UUID.randomUUID())
                        .names("John Doe")
                        .build())
                .ratings(Collections.emptyList()) 
                .build();

        ScheduleEntity schedule2 = ScheduleEntity.builder()
                .id(UUID.randomUUID())
                .scStatus(ScheduleStatus.APPROVED)
                .startDate(LocalDate.now().minusDays(10))
                .endDate(LocalDate.now().minusDays(9))
                .createdAt(LocalDateTime.now().minusDays(12))
                .price(300.0)
                .property(PropertyEntity.builder()
                        .id(UUID.randomUUID())
                        .name("Another Property")
                        .build())
                .user(AccountEntity.builder()
                        .id(UUID.randomUUID())
                        .names("Jane Smith")
                        .build())
                .ratings(Collections.emptyList())
                .build();

        when(scheduleRepository.findRequestsByOwnerId(landlordId, limit, offset))
                .thenReturn(List.of(schedule1, schedule2));

        List<OwnerRentaRequestDTO> result = rentalController.getOwnerRentalRequests(page, authDTO);
        assertNotNull(result);
        assertEquals(2, result.size());

        OwnerRentaRequestDTO dto1 = result.get(0);
        assertEquals(schedule1.getId(), dto1.id());
        assertEquals(schedule1.getProperty().getId(), dto1.propertyId());
        assertEquals(schedule1.getUser().getId(), dto1.userId());
        assertEquals(schedule1.getUser().getNames(), dto1.userName());
        assertEquals(schedule1.getProperty().getName(), dto1.propertyName());
        assertEquals(schedule1.getStartDate(), dto1.startDate());
        assertEquals(schedule1.getEndDate(), dto1.endDate());
        assertEquals(schedule1.getCreatedAt(), dto1.requestedAt());
        assertEquals(200.0, dto1.price());
        assertEquals(ScheduleStatus.COMPLETED, dto1.status());

        OwnerRentaRequestDTO dto2 = result.get(1);
        assertEquals(schedule2.getId(), dto2.id());
        assertEquals(ScheduleStatus.LOST, dto2.status());
    }

    @Test
    void getOwnerRentalRequests_EmptyList() {
        UUID landlordId = UUID.randomUUID();
        AuthDTO authDTO = new AuthDTO(landlordId, "LANDLORD");
        when(scheduleRepository.findRequestsByOwnerId(landlordId, 10, 0)).thenReturn(Collections.emptyList());
        List<OwnerRentaRequestDTO> result = rentalController.getOwnerRentalRequests(1, authDTO);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                    GET USER REQUESTS TESTS                                                                //////////////////////////
    
    @Test
    void getUserRentalRequests_Success_AsUser() {
        int page = 2;
        int limit = 10;
        int offset = 10;
        AuthDTO userAuth = new AuthDTO(UUID.randomUUID(), Roles.USER_ROLE);
        ScheduleEntity schedule1 = ScheduleEntity.builder()
                .id(UUID.randomUUID())
                .scStatus(ScheduleStatus.PAID)
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().minusDays(2))
                .createdAt(LocalDateTime.now().minusDays(7))
                .price(500.0)
                .property(PropertyEntity.builder()
                        .id(UUID.randomUUID())
                        .name("User Property 1")
                        .build())
                .ratings(Collections.emptyList())
                .build();

        ScheduleEntity schedule2 = ScheduleEntity.builder()
                .id(UUID.randomUUID())
                .scStatus(ScheduleStatus.APPROVED)
                .startDate(LocalDate.now().minusDays(3))
                .endDate(LocalDate.now().minusDays(1))
                .createdAt(LocalDateTime.now().minusDays(5))
                .price(700.0)
                .property(PropertyEntity.builder()
                        .id(UUID.randomUUID())
                        .name("User Property 2")
                        .build())
                .ratings(Collections.emptyList())
                .build();
        when(scheduleRepository.findRequestsByUserId(userAuth.userId(), limit, offset))
                .thenReturn(List.of(schedule1, schedule2));
        List<RentalRequestDTO> result = rentalController.getUserRentalRequests(page, userAuth);
        assertNotNull(result);
        assertEquals(2, result.size());
        RentalRequestDTO dto1 = result.get(0);
        assertEquals(schedule1.getId(), dto1.id());
        assertEquals(schedule1.getProperty().getId(), dto1.propertyId());
        assertEquals("User Property 1", dto1.propertyName());
        assertEquals(ScheduleStatus.COMPLETED, dto1.status());
        assertEquals(500.0, dto1.price());

        RentalRequestDTO dto2 = result.get(1);
        assertEquals(schedule2.getId(), dto2.id());
        assertEquals("User Property 2", dto2.propertyName());
        assertEquals(ScheduleStatus.LOST, dto2.status());
        assertEquals(700.0, dto2.price());
    }

    @Test
    void getUserRentalRequests_Success_AsLandlord_Empty() {
        AuthDTO landlordAuth = new AuthDTO(UUID.randomUUID(), Roles.LANDLORD_ROLE);

        when(scheduleRepository.findRequestsByOwnerId(landlordAuth.userId(), 10, 0))
                .thenReturn(Collections.emptyList());
        List<RentalRequestDTO> result = rentalController.getUserRentalRequests(1, landlordAuth);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getUserRentalRequests_CompletedBecomesRated() {
        AuthDTO userAuth = new AuthDTO(UUID.randomUUID(), Roles.USER_ROLE);
        when(scheduleRepository.findRequestsByUserId(any(), anyInt(), anyInt()))
                .thenReturn(List.of(buildCompletedWithRating()));
        List<RentalRequestDTO> result = rentalController.getUserRentalRequests(1, userAuth);

        assertNotNull(result);
        assertEquals(1, result.size());
        RentalRequestDTO dto = result.get(0);
        assertEquals(ScheduleStatus.RATED, dto.status());
    }

 
    private ScheduleEntity buildCompletedWithRating() {
        return ScheduleEntity.builder()
                .id(UUID.randomUUID())
                .scStatus(ScheduleStatus.PAID)  
                .startDate(LocalDate.now().minusDays(5))
                .endDate(LocalDate.now().minusDays(3))
                .ratings(List.of(RatingEntity.builder()
                        .id(UUID.randomUUID())
                        .type(Roles.USER_ROLE)
                        .build()))
                .property(PropertyEntity.builder()
                        .id(UUID.randomUUID())
                        .name("Rated Property")
                        .build())
                .price(1200.0)
                .createdAt(LocalDateTime.now().minusDays(10))
                .build();
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                    ACCEPT REQUESTS TESTS                                                                //////////////////////////
     
    @Test
    void acceptRentalRequest_Success() {
        UUID requestId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        ScheduleEntity schedule = ScheduleEntity.builder()
                .id(requestId)
                .scStatus(ScheduleStatus.REQUESTED)
                .property(PropertyEntity.builder()
                        .owner(AccountEntity.builder().id(ownerId).build())
                        .build())
                .build();

        when(scheduleRepository.findById(requestId)).thenReturn(Optional.of(schedule));
        when(scheduleRepository.save(any(ScheduleEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        AuthDTO authDTO = new AuthDTO(ownerId, "LANDLORD");
        assertDoesNotThrow(() -> rentalController.acceptRentalRequest(requestId, authDTO));
        verify(scheduleRepository, times(1)).save(schedule);
        assertEquals(ScheduleStatus.APPROVED, schedule.getScStatus());
    }

    @Test
    void acceptRentalRequest_RequestNotFound() {
        UUID requestId = UUID.randomUUID();
        when(scheduleRepository.findById(requestId)).thenReturn(Optional.empty());

        UUID userId = UUID.randomUUID();
        AuthDTO auth = new AuthDTO(userId, "LANDLORD");
        
        ApiException ex = assertThrows(ApiException.class, () -> 
            rentalController.acceptRentalRequest(requestId, auth)
        );
        
        assertEquals(ApiError.REQUEST_NOT_FOUND, ex.getError());

        verify(scheduleRepository, never()).save(any(ScheduleEntity.class));
    }

    @Test
    void acceptRentalRequest_UserNotOwner() {
        UUID requestId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        ScheduleEntity schedule = ScheduleEntity.builder()
                .id(requestId)
                .scStatus(ScheduleStatus.REQUESTED)
                .property(PropertyEntity.builder()
                        .owner(AccountEntity.builder().id(ownerId).build())
                        .build())
                .build();

        when(scheduleRepository.findById(requestId)).thenReturn(Optional.of(schedule));

        AuthDTO invalidAuth = new AuthDTO(otherUserId, "LANDLORD");
        ApiException ex = assertThrows(ApiException.class,
            () -> rentalController.acceptRentalRequest(requestId, invalidAuth)
        );
        assertEquals(ApiError.UNATHORIZED_TO_EDIT_REQUEST, ex.getError());
        verify(scheduleRepository, never()).save(any(ScheduleEntity.class));
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                    CANCEL REQUESTS TESTS                                                                //////////////////////////

    @Test
    void cancelRentalRequest_Success() {
        UUID requestId = UUID.randomUUID();
        UUID ownerId = UUID.randomUUID();

        ScheduleEntity schedule = ScheduleEntity.builder()
                .id(requestId)
                .scStatus(ScheduleStatus.REQUESTED)
                .property(PropertyEntity.builder()
                        .owner(AccountEntity.builder().id(ownerId).build())
                        .build())
                .build();

        when(scheduleRepository.findById(requestId)).thenReturn(Optional.of(schedule));
        when(scheduleRepository.save(any(ScheduleEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        AuthDTO authDTO = new AuthDTO(ownerId, "LANDLORD");

        assertDoesNotThrow(() -> rentalController.cancelRentalRequest(requestId, authDTO));
        verify(scheduleRepository, times(1)).save(schedule);
        assertEquals(ScheduleStatus.DENIED, schedule.getScStatus());
    }

    @Test
    void cancelRentalRequest_RequestNotFound() {
        UUID requestId = UUID.randomUUID();
        when(scheduleRepository.findById(requestId)).thenReturn(Optional.empty());
        UUID userId = UUID.randomUUID();
        AuthDTO auth = new AuthDTO(userId ,"LANDLORD");
        ApiException ex = assertThrows(ApiException.class,
            () -> rentalController.cancelRentalRequest(requestId, auth)
        );
        assertEquals(ApiError.REQUEST_NOT_FOUND, ex.getError());
        verify(scheduleRepository, never()).save(any(ScheduleEntity.class));
    }

    @Test
    void cancelRentalRequest_UserNotOwner() {
        UUID requestId = UUID.randomUUID();
        UUID actualOwnerId = UUID.randomUUID();
        UUID otherUserId = UUID.randomUUID();

        ScheduleEntity schedule = ScheduleEntity.builder()
                .id(requestId)
                .scStatus(ScheduleStatus.REQUESTED)
                .property(PropertyEntity.builder()
                        .owner(AccountEntity.builder().id(actualOwnerId).build())
                        .build())
                .build();

        when(scheduleRepository.findById(requestId)).thenReturn(Optional.of(schedule));

        AuthDTO invalidAuth = new AuthDTO(otherUserId, "LANDLORD");

        ApiException ex = assertThrows(ApiException.class,
            () -> rentalController.cancelRentalRequest(requestId, invalidAuth)
        );
        assertEquals(ApiError.UNATHORIZED_TO_EDIT_REQUEST, ex.getError());
        verify(scheduleRepository, never()).save(any(ScheduleEntity.class));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                    CREATE REQUESTS TESTS                                                                //////////////////////////
    
    @Test
    void createRentalRequest_Success() {
        UUID propId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AuthDTO authDTO = new AuthDTO(userId, Roles.USER_ROLE);
        LocalDate startDate = LocalDate.now().plusDays(5);
        LocalDate endDate = LocalDate.now().plusDays(7);

        doReturn(true).when(spyPropertiesService).validatePropertyGuests(propId, 4);
        doReturn(300.0).when(spyPropertiesService).getPropertyPrice(propId, 2L);
        when(scheduleRepository.findAllByPropertyId(propId, null, startDate.getYear(), 100))
            .thenReturn(Collections.emptyList());

        CreateRentalRequestDTO body = new CreateRentalRequestDTO(
            startDate, endDate, 4
        );

        when(scheduleRepository.save(any(ScheduleEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        assertDoesNotThrow(() -> rentalController.createRentalRequest(propId, body, authDTO));
        verify(scheduleRepository).save(any(ScheduleEntity.class));
    }

    @Test
    void createRentalRequest_UserIsLandlord_ThrowsUnauthorized() {
        UUID propId = UUID.randomUUID();
        AuthDTO authDTO = new AuthDTO(UUID.randomUUID(), Roles.LANDLORD_ROLE); 

        CreateRentalRequestDTO body = new CreateRentalRequestDTO(
            LocalDate.now().plusDays(2),
            LocalDate.now().plusDays(5),
            2
        );
        ApiException ex = assertThrows(ApiException.class,
            () -> rentalController.createRentalRequest(propId, body, authDTO)
        );
        assertEquals(ApiError.UNATHORIZED_TO_REQUEST, ex.getError());
        verify(scheduleRepository, never()).save(any());
    }

    @Test
    void createRentalRequest_Collision_ThrowsInvalidScheduleDates() {
        UUID propId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AuthDTO authDTO = new AuthDTO(userId, Roles.USER_ROLE);

        doReturn(true).when(spyPropertiesService).validatePropertyGuests(propId, 3);

        ScheduleEntity existing = ScheduleEntity.builder()
            .id(UUID.randomUUID())
            .scStatus(ScheduleStatus.REQUESTED)
            .startDate(LocalDate.now().plusDays(4))
            .endDate(LocalDate.now().plusDays(6))
            .build();

        when(scheduleRepository.findAllByPropertyId(propId, null, LocalDate.now().plusDays(5).getYear(), 100))
            .thenReturn(List.of(existing));

        CreateRentalRequestDTO body = new CreateRentalRequestDTO(
            LocalDate.now().plusDays(5),
            LocalDate.now().plusDays(7),
            3
        );

        ApiException ex = assertThrows(ApiException.class,
            () -> rentalController.createRentalRequest(propId, body, authDTO)
        );
        assertEquals(ApiError.INVALID_SCHEDULE_DATES, ex.getError());
        verify(scheduleRepository, never()).save(any());
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                    CREATE REVIEW TESTS                                                                //////////////////////////
    
    @Test
    void rateTenant_Success_FirstRating() {
        UUID scheduleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        AuthDTO auth = new AuthDTO(userId, Roles.USER_ROLE);

        ScheduleEntity schedule = ScheduleEntity.builder()
            .id(scheduleId)
            .scStatus(ScheduleStatus.PAID)
            .startDate(LocalDate.now().minusDays(5))
            .endDate(LocalDate.now().minusDays(2))
            .user(AccountEntity.builder().id(userId).build()) 
            .ratings(new ArrayList<>()) 
            .build();
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(ratingRepository.save(any(RatingEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        CreateRatingDTO dto = new CreateRatingDTO(5, "Great experience!");

        assertDoesNotThrow(() -> rentalController.rateTenant(scheduleId, dto, auth));
        assertEquals(1, schedule.getRatings().size());
        RatingEntity newRating = schedule.getRatings().get(0);
        assertEquals("Great experience!", newRating.getComment());
        assertEquals(5, newRating.getRating());
        assertEquals(Roles.LANDLORD_ROLE, newRating.getType());
        assertNotEquals(ScheduleStatus.RATED, schedule.getScStatus());
    }

    @Test
    void rateTenant_Success_SecondRating() {
        UUID scheduleId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();  
        UUID ownerId = UUID.randomUUID();
        AuthDTO auth = new AuthDTO(ownerId, Roles.LANDLORD_ROLE);
        RatingEntity existingRating = RatingEntity.builder()
            .id(UUID.randomUUID())
            .type(Roles.LANDLORD_ROLE) 
            .build();

        ScheduleEntity schedule = ScheduleEntity.builder()
            .id(scheduleId)
            .scStatus(ScheduleStatus.PAID)
            .startDate(LocalDate.now().minusDays(10))
            .endDate(LocalDate.now().minusDays(5))
            .ratings(new ArrayList<>(List.of(existingRating)))
            .property(PropertyEntity.builder()
                    .owner(AccountEntity.builder().id(ownerId).build())
                    .build())
            .user(AccountEntity.builder().id(userId).build())
            .build();
        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        when(ratingRepository.save(any(RatingEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        when(scheduleRepository.save(any(ScheduleEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        CreateRatingDTO dto = new CreateRatingDTO(4, "User was nice.");
        assertDoesNotThrow(() -> rentalController.rateTenant(scheduleId, dto, auth));
        assertEquals(2, schedule.getRatings().size());
        assertEquals(ScheduleStatus.RATED, schedule.getScStatus());
    }

    @Test
    void rateTenant_NotCompleted_ThrowsCantRateYet() {
        UUID scheduleId = UUID.randomUUID();
        AuthDTO auth = new AuthDTO(UUID.randomUUID(), Roles.USER_ROLE);
        ScheduleEntity schedule = ScheduleEntity.builder()
            .id(scheduleId)
            .scStatus(ScheduleStatus.APPROVED)
            .startDate(LocalDate.now().minusDays(1))
            .endDate(LocalDate.now().plusDays(2)) 
            .build();

        when(scheduleRepository.findById(scheduleId)).thenReturn(Optional.of(schedule));
        CreateRatingDTO dto = new CreateRatingDTO(3, "Not finished yet");
        ApiException ex = assertThrows(ApiException.class,
            () -> rentalController.rateTenant(scheduleId, dto, auth)
        );
        assertEquals(ApiError.CANT_RATE_YET, ex.getError());
        verify(ratingRepository, never()).save(any());
        verify(scheduleRepository, never()).save(any());
    }
}
