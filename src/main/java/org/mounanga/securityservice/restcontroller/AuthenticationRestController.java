package org.mounanga.securityservice.restcontroller;

import org.mounanga.securityservice.dto.LoginRequestDTO;
import org.mounanga.securityservice.dto.LoginResponseDTO;
import org.mounanga.securityservice.service.AuthenticationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
public class AuthenticationRestController {

    private final AuthenticationService authenticationService;

    public AuthenticationRestController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public LoginResponseDTO authenticate(@RequestBody LoginRequestDTO loginRequest) {
        return authenticationService.authenticate(loginRequest);
    }
}
