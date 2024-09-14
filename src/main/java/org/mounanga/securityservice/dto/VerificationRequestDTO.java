package org.mounanga.securityservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record VerificationRequestDTO(
        @NotBlank(message="field 'email' is mandatory: it can not be blank")
        @Email(message = "email is not well formated")
        String email) {
}
