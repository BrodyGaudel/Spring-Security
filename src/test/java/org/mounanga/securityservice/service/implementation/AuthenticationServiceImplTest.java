package org.mounanga.securityservice.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mounanga.securityservice.configuration.ApplicationProperties;
import org.mounanga.securityservice.dto.LoginRequestDTO;
import org.mounanga.securityservice.dto.LoginResponseDTO;
import org.mounanga.securityservice.dto.MailDTO;
import org.mounanga.securityservice.entity.User;

import org.mounanga.securityservice.exception.UserNotEnabledException;
import org.mounanga.securityservice.exception.UserNotFoundException;
import org.mounanga.securityservice.repository.UserRepository;
import org.mounanga.securityservice.security.JwtService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class AuthenticationServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ApplicationProperties properties;

    @Mock
    private JwtService jwtService;

    @Mock
    private MailingService mailingService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    @BeforeEach
    void setUp() {
        authenticationService = new AuthenticationServiceImpl(authenticationManager,userRepository, properties, jwtService, mailingService);
    }

    @Test
    void testAuthenticateSuccess() {
        String username = "testUser";
        String password = "password";
        LoginRequestDTO request = new LoginRequestDTO(username, password);
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticationResponse = mock(Authentication.class);

        when(authenticationManager.authenticate(authenticationRequest)).thenReturn(authenticationResponse);
        when(authenticationResponse.isAuthenticated()).thenReturn(true);

        User mockUser = mock(User.class);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(mockUser.isEnabled()).thenReturn(true);
        when(properties.getJwtSecret()).thenReturn("jwtSecret");
        when(properties.getJwtExpiration()).thenReturn(1000L * 60 * 60);
        when(jwtService.generateToken(mockUser, "jwtSecret", 1000L * 60 * 60)).thenReturn("jwtToken");

        LoginResponseDTO response = authenticationService.authenticate(request);

        assertNotNull(response);
        assertEquals("jwtToken", response.jwt());

        verify(mailingService, times(1)).send(any(MailDTO.class));
    }

    @Test
    void testAuthenticateUserNotEnabled() {
        String username = "testUser";
        String password = "password";
        LoginRequestDTO request = new LoginRequestDTO(username, password);
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticationResponse = mock(Authentication.class);

        when(authenticationManager.authenticate(authenticationRequest)).thenReturn(authenticationResponse);
        when(authenticationResponse.isAuthenticated()).thenReturn(true);

        User mockUser = mock(User.class);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(mockUser));
        when(mockUser.isEnabled()).thenReturn(false);

        assertThrows(UserNotEnabledException.class, () -> authenticationService.authenticate(request));

        verify(mailingService, times(0)).send(any(MailDTO.class));
    }

    @Test
    void testAuthenticateAuthenticationFailed() {
        // Given
        String username = "testUser";
        String password = "wrongPassword";
        LoginRequestDTO request = new LoginRequestDTO(username, password);
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(username, password);

        when(authenticationManager.authenticate(authenticationRequest)).thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(request));

        verify(mailingService, times(0)).send(any(MailDTO.class));
    }


    @Test
    void testAuthenticate_UserNotFound() {
        String username = "nonExistentUser";
        String password = "password";
        LoginRequestDTO request = new LoginRequestDTO(username, password);
        Authentication authenticationRequest = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticationResponse = mock(Authentication.class);

        when(authenticationManager.authenticate(authenticationRequest)).thenReturn(authenticationResponse);
        when(authenticationResponse.isAuthenticated()).thenReturn(true);

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> authenticationService.authenticate(request));

        verify(mailingService, times(0)).send(any(MailDTO.class));
    }
}