package org.mounanga.securityservice.dto;

import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private Boolean enabled;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime lastModifiedDate;
    private String lastModifiedBy;
    private Boolean passwordMustBeModified;
    private ProfileResponseDTO profile;
}
