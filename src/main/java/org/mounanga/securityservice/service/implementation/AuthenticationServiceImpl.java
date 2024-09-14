package org.mounanga.securityservice.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mounanga.securityservice.configuration.ApplicationProperties;
import org.mounanga.securityservice.dto.LoginRequestDTO;
import org.mounanga.securityservice.dto.LoginResponseDTO;
import org.mounanga.securityservice.dto.MailDTO;
import org.mounanga.securityservice.entity.User;
import org.mounanga.securityservice.exception.UserNotAuthenticatedException;
import org.mounanga.securityservice.exception.UserNotEnabledException;
import org.mounanga.securityservice.exception.UserNotFoundException;
import org.mounanga.securityservice.repository.UserRepository;
import org.mounanga.securityservice.security.JwtService;
import org.mounanga.securityservice.service.AuthenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final ApplicationProperties properties;
    private final JwtService jwtService;
    private final MailingService mailingService;

    public AuthenticationServiceImpl(AuthenticationManager authenticationManager, UserRepository userRepository, ApplicationProperties properties, JwtService jwtService, MailingService mailingService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.properties = properties;
        this.jwtService = jwtService;
        this.mailingService = mailingService;
    }

    @Override
    public LoginResponseDTO authenticate(@NotNull LoginRequestDTO request) {
        log.info("In authentication()");

        Authentication authenticationRequest =  new UsernamePasswordAuthenticationToken(request.username(), request.password());
        Authentication authenticationResponse = authenticationManager.authenticate(authenticationRequest);

        if(authenticationResponse.isAuthenticated()) {
            User user = findUserByUsername(request.username());
            if(!user.isEnabled()){
                throw new UserNotEnabledException(String.format("User %s is not enabled", request.username()));
            }
            log.info("Authentication successful");
            sendNotification(user, LocalDateTime.now());
            String jwt = jwtService.generateToken(user, properties.getJwtSecret(), properties.getJwtExpiration());
            return new LoginResponseDTO(jwt, user.getPasswordMustBeModified());
        }else{
            log.error("Authentication failed for user: {}", request.username());
            throw new BadCredentialsException("Bad credentials");
        }
    }

    private void sendNotification(@NotNull User user, @NotNull LocalDateTime now) {
        String body = String.format("Hello %s . You have just contacted yourself. If you are not the originator of this manipulation: please change your password immediately and/or contact an administrator at %s", user.getUsername(), now.toString());
        MailDTO mail = new MailDTO(user.getEmail(), "Connection notification",body);
        mailingService.send(mail);
    }

    private User findUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> {
            log.error("User not found with username or email: {}", username);
            return new UserNotFoundException("User not found");
        });
    }


}
