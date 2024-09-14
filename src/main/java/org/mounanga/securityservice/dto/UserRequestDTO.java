package org.mounanga.securityservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.*;
import org.mounanga.securityservice.enums.Gender;
import org.mounanga.securityservice.util.validation.Password;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class UserRequestDTO {

    @NotBlank(message = "field 'username' is mandatory: it can not be blank")
    private String username;

    @Email(message = "field 'email' is not well formated")
    private String email;

    @Password
    private String password;

    @NotBlank(message = "field 'firstname' is mandatory: it can not be blank")
    private String firstname;

    @NotBlank(message = "field 'lastname' is mandatory: it can not be blank")
    private String lastname;

    @NotBlank(message = "field 'place of birth' is mandatory: it can not be blank")
    private String placeOfBirth;

    @NotNull(message = "field 'date of birth' is mandatory: it can not be null")
    @Past(message = "date must be in past")
    private LocalDate dateOfBirth;

    @NotBlank(message = "field 'nationality' is mandatory: it can not be blank")
    private String nationality;

    @NotNull(message = "field 'gender' is mandatory: it can not be null or blank")
    private Gender gender;

    @NotBlank(message = "field 'personal Identification Number' is mandatory: it can not be blank")
    private String personalIdentificationNumber;
}
