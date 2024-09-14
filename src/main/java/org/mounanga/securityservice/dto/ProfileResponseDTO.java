package org.mounanga.securityservice.dto;

import lombok.*;
import org.mounanga.securityservice.enums.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ProfileResponseDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String placeOfBirth;
    private LocalDate dateOfBirth;
    private String nationality;
    private Gender gender;
    private String personalIdentificationNumber;
    private LocalDateTime createdDate;
    private String createdBy;
    private LocalDateTime lastModifiedDate;
    private String lastModifiedBy;
}
