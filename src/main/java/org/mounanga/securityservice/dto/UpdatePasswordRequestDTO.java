package org.mounanga.securityservice.dto;

public record UpdatePasswordRequestDTO(String oldPassword, String newPassword) {
}
