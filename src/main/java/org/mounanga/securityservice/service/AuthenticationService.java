package org.mounanga.securityservice.service;

import org.mounanga.securityservice.dto.LoginRequestDTO;
import org.mounanga.securityservice.dto.LoginResponseDTO;

public interface AuthenticationService {

    LoginResponseDTO authenticate(LoginRequestDTO request);
}
