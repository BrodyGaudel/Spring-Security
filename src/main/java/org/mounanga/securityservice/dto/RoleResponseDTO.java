package org.mounanga.securityservice.dto;

import java.time.LocalDateTime;

public record RoleResponseDTO(Long id,
                              String name,
                              String description,
                              LocalDateTime createdDate,
                              String createdBy,
                              LocalDateTime lastModifiedDate,
                              String lastModifiedBy) {
}
