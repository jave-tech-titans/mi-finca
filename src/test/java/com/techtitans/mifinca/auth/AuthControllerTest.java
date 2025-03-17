package com.techtitans.mifinca.auth;


import com.techtitans.mifinca.domain.dtos.AccessTokenDTO;
import com.techtitans.mifinca.domain.dtos.LoginDTO;
import com.techtitans.mifinca.domain.dtos.RegisterAccountDTO;
import com.techtitans.mifinca.domain.entities.AccountEntity;
import com.techtitans.mifinca.domain.exceptions.ApiError;
import com.techtitans.mifinca.domain.exceptions.ApiException;
import com.techtitans.mifinca.domain.services.AccountService;
import com.techtitans.mifinca.domain.services.AuthService;
import com.techtitans.mifinca.domain.services.ConfirmationService;
import com.techtitans.mifinca.domain.services.CryptService;
import com.techtitans.mifinca.domain.services.EmailService;
import com.techtitans.mifinca.repository.AccountRepository;
import com.techtitans.mifinca.repository.ConfirmationRepository;
import com.techtitans.mifinca.repository.SessionRepository;
import com.techtitans.mifinca.transport.controllers.AuthController;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock 
    private SessionRepository sessionRepository;

    @Mock 
    private ConfirmationRepository confirmRepository;

    @Mock 
    private EmailService emailService;


    private ConfirmationService confirmationService;

    private CryptService cryptService;

    private AuthService authService;

    private AccountService accountService;

    private AuthController authController;

    private RegisterAccountDTO validDto;
    private RegisterAccountDTO invalidEmailDto;
    private RegisterAccountDTO shortPasswordDto;
    private AccountEntity validEntity;

    @BeforeEach
    void setUp() {
        cryptService = new CryptService("asdfgcdetgfcfrtd");
        confirmationService = new ConfirmationService(confirmRepository, emailService);
        authService = new AuthService("3cfa76ef14937c1c0ea519f8fc057a80fcd04a7420f8e8bcd0a7567c272e007b",36000000, 360000, sessionRepository);
        accountService = new AccountService(accountRepository, confirmationService, cryptService, authService);
        authController = new AuthController(accountService,authService);

        validDto = new RegisterAccountDTO("John", "Doe", "john.doe@example.com", "password123", "34234234", "USER");
        validEntity = AccountEntity
            .builder()
            .id(UUID.randomUUID())
            .email(validDto.email())
            .hash(cryptService.encryptAES(validDto.password()))
            .isActive(true)
            .role("USER")
            .build();
        invalidEmailDto = new RegisterAccountDTO("John", "Doe", "invalidemail", "password123", "34234234", "USER");
        shortPasswordDto = new RegisterAccountDTO("John", "Doe", "john.doe@example.com", "123","34234234", "USER");
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///                     REGISTER TESTS                                                                      //////////////////////////


    @Test
    public void createAccount_Success() {
        when(accountRepository.findByEmail(validDto.email())).thenReturn(java.util.Optional.empty());
        when(accountRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        assertDoesNotThrow(() -> authController.createAccount(validDto));
        verify(accountRepository, times(1)).save(any());
    }

    @Test
    public void createAccount_InvalidEmail() {
        ApiException exception = assertThrows(ApiException.class, () -> authController.createAccount(invalidEmailDto));
        assertEquals(ApiError.INVALID_EMAIL, exception.getError());
    }

    @Test
    public void createAccount_ShortPassword() {
        ApiException exception = assertThrows(ApiException.class, () -> authController.createAccount(shortPasswordDto));
        assertEquals(ApiError.PASSWORD_TO_SHORT, exception.getError());
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    ////////                     LOGIN TESTS                                                   ////////////////////////////////////////
    
    @Test 
    public void login_Success(){
        when(accountRepository.findByEmail(validDto.email())).thenReturn(Optional.of(validEntity));
        when(sessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        LoginDTO inputDTO = new LoginDTO(validDto.email(), validDto.password());
        var result = authController.login(inputDTO);
        assertEquals(result.getClass(), AccessTokenDTO.class);
    }
    
    @Test 
    public void login_InvalidPassword(){
        when(accountRepository.findByEmail(validDto.email())).thenReturn(Optional.of(validEntity));
        LoginDTO inputDTO = new LoginDTO(validDto.email(), "invalid password");
        ApiException ex = assertThrows(ApiException.class, ()->authController.login(inputDTO));
        assertEquals(ApiError.INCORRECT_PASSWORD, ex.getError());
    }

}
