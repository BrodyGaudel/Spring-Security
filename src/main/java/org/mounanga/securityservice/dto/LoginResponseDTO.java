package org.mounanga.securityservice.dto;

public record LoginResponseDTO(String jwt, boolean passwordNeedToBeUpdated) {
}
