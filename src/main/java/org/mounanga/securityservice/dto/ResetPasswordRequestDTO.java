package org.mounanga.securityservice.dto;

public record ResetPasswordRequestDTO(String code, String email, String newPassword) {
}
