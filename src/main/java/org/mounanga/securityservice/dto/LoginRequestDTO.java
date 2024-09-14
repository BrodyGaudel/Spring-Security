package org.mounanga.securityservice.dto;

import jakarta.validation.constraints.NotBlank;
import org.mounanga.securityservice.util.validation.Password;

public record LoginRequestDTO(
        @NotBlank(message="field 'username' is mandatory: it can not be blank")
        String username,

        @Password
        String password) {
}
