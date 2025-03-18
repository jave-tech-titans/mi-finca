package com.techtitans.mifinca.payment;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.techtitans.mifinca.domain.dtos.AuthDTO;
import com.techtitans.mifinca.domain.dtos.CreatePaymentDTO;
import com.techtitans.mifinca.domain.entities.AccountEntity;
import com.techtitans.mifinca.domain.entities.PaymentEntity;
import com.techtitans.mifinca.domain.entities.ScheduleEntity;
import com.techtitans.mifinca.domain.entities.ScheduleStatus;
import com.techtitans.mifinca.domain.exceptions.ApiError;
import com.techtitans.mifinca.domain.exceptions.ApiException;
import com.techtitans.mifinca.domain.services.PaymentService;
import com.techtitans.mifinca.domain.services.PropertiesService;
import com.techtitans.mifinca.domain.services.RatingService;
import com.techtitans.mifinca.domain.services.RentalService;
import com.techtitans.mifinca.repository.FileRepository;
import com.techtitans.mifinca.repository.PaymentRepository;
import com.techtitans.mifinca.repository.PropertyRepository;
import com.techtitans.mifinca.repository.RatingRepository;
import com.techtitans.mifinca.repository.ScheduleRepository;
import com.techtitans.mifinca.transport.controllers.PaymentController;

@ExtendWith(MockitoExtension.class)
public class PaymentControllerTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ScheduleRepository scheduleRepository;

    @Mock
    private PropertyRepository propertyRepository;
    @Mock
    private FileRepository fileRepository;    
    @Mock
    private RatingRepository ratingRepository;    

    private PropertiesService propertiesService;
    private RatingService ratingService;

    private RentalService rentalService;
    private RentalService rentalServiceSpy;

    private PaymentService paymentService;
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        propertiesService = Mockito.mock(PropertiesService.class);
        ratingService = Mockito.mock(RatingService.class);
        rentalService = new RentalService(scheduleRepository, propertiesService, ratingService);
        rentalServiceSpy = Mockito.spy(rentalService);
        paymentService = new PaymentService(paymentRepository, rentalServiceSpy);
        paymentController = new PaymentController(paymentService);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                     CREATE PAYMENT TESTS                                                                      //////////////////////////
    
    @Test
    void paySchedule_Success() {
        UUID requestId = UUID.randomUUID();
        UUID scheduleUserId = UUID.randomUUID();
        ScheduleEntity schedule = ScheduleEntity.builder()
            .id(requestId)
            .scStatus(ScheduleStatus.APPROVED)
            .startDate(LocalDate.now().plusDays(1))
            .endDate(LocalDate.now().plusDays(2))
            .price(1500.0)
            .user(AccountEntity.builder().id(scheduleUserId).build())
            .build();

        when(scheduleRepository.findById(requestId)).thenReturn(Optional.of(schedule));
        doNothing().when(rentalServiceSpy).UpdatePaidRentalRequest(requestId);
        CreatePaymentDTO paymentDTO = new CreatePaymentDTO("Bancolombia", 1234567890L);
        AuthDTO auth = new AuthDTO(scheduleUserId, "USER");
        when(paymentRepository.save(any(PaymentEntity.class))).thenAnswer(inv -> inv.getArgument(0));
        assertDoesNotThrow(() -> paymentController.paySchedule(requestId, paymentDTO, auth));
        verify(paymentRepository, times(1)).save(any(PaymentEntity.class));
        verify(rentalServiceSpy, times(1)).UpdatePaidRentalRequest(requestId);
    }

    @Test
    void paySchedule_Unauthorized_UserMismatch() {
        UUID requestId = UUID.randomUUID();
        UUID scheduleUserId = UUID.randomUUID();
        UUID differentUserId = UUID.randomUUID();
        ScheduleEntity schedule = ScheduleEntity.builder()
            .id(requestId)
            .scStatus(ScheduleStatus.APPROVED)
            .startDate(LocalDate.now().plusDays(1))
            .endDate(LocalDate.now().plusDays(2))
            .price(1500.0)
            .user(AccountEntity.builder().id(scheduleUserId).build())
            .build();

        when(scheduleRepository.findById(requestId)).thenReturn(Optional.of(schedule));
        CreatePaymentDTO paymentDTO = new CreatePaymentDTO("Bancolombia", 1234567890L);
        AuthDTO auth = new AuthDTO(differentUserId, "USER");
        ApiException ex = assertThrows(ApiException.class,
            () -> paymentController.paySchedule(requestId, paymentDTO, auth)
        );
        assertEquals(ApiError.USER_IS_NOT_THE_REQUEST_ONE, ex.getError());
        verify(paymentRepository, never()).save(any());
        verify(rentalServiceSpy, never()).UpdatePaidRentalRequest(any());
    }

    @Test
    void paySchedule_InvalidParameters_NullAccountOrInvalidBank() {
        UUID requestId = UUID.randomUUID();
        UUID scheduleUserId = UUID.randomUUID();
        ScheduleEntity schedule = ScheduleEntity.builder()
            .id(requestId)
            .scStatus(ScheduleStatus.APPROVED)
            .startDate(LocalDate.now().plusDays(1))
            .endDate(LocalDate.now().plusDays(2))
            .price(1500.0)
            .user(AccountEntity.builder().id(scheduleUserId).build())
            .build();

        when(scheduleRepository.findById(requestId)).thenReturn(Optional.of(schedule));
        CreatePaymentDTO invalidDTO1 = new CreatePaymentDTO("Bancolombia", null);
        CreatePaymentDTO invalidDTO2 = new CreatePaymentDTO("InvalidBank", 1234567890L);

        AuthDTO auth = new AuthDTO(scheduleUserId, "USER");
        ApiException ex1 = assertThrows(ApiException.class,
            () -> paymentController.paySchedule(requestId, invalidDTO1, auth)
        );
        assertEquals(ApiError.INVALID_PARAMETERS, ex1.getError());

        ApiException ex2 = assertThrows(ApiException.class,
            () -> paymentController.paySchedule(requestId, invalidDTO2, auth)
        );
        assertEquals(ApiError.INVALID_PARAMETERS, ex2.getError());
        verify(paymentRepository, never()).save(any());
        verify(rentalServiceSpy, never()).UpdatePaidRentalRequest(any());
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                     GET BANKS TESTS                                                                      //////////////////////////

     @Test
    public void getBanks_Success() {

        var result = paymentController.getBanks();
        assertTrue(result.contains("Bancolombia"));
        assertTrue(result.contains("Davivienda"));
        assertTrue(result.contains("NuBank"));
    }
}
