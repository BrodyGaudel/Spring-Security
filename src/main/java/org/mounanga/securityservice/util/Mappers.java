package org.mounanga.securityservice.util;

import org.jetbrains.annotations.NotNull;
import org.mounanga.securityservice.dto.*;
import org.mounanga.securityservice.entity.Role;
import org.mounanga.securityservice.entity.User;
import org.mounanga.securityservice.entity.Profile;

import java.util.ArrayList;
import java.util.List;

public class Mappers {

    private Mappers(){
        super();
    }

    public static UserResponseDTO from(final User user) {
        if(user == null){
            return null;
        }
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdBy(user.getCreatedBy())
                .createdDate(user.getCreatedDate())
                .lastModifiedBy(user.getLastModifiedBy())
                .lastModifiedDate(user.getLastModifiedDate())
                .enabled(user.getEnabled())
                .passwordMustBeModified(user.getPasswordMustBeModified())
                .profile(profileResponseDTOFromProfile(user.getProfile()))
                .build();
    }


    public static @NotNull User from(final @NotNull UserRequestDTO userRequestDTO) {
        final User user = new User();
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
        user.setPassword(userRequestDTO.getPassword());
        user.setPasswordMustBeModified(Boolean.TRUE);
        user.setEnabled(Boolean.TRUE);
        user.setProfile(profileFromUserRequestDTO(userRequestDTO));
        return user;
    }

    public static List<UserResponseDTO> from(final @NotNull List<User> users) {
        return users.stream()
                .map(Mappers::from)
                .toList();
    }

    public static Role from(final @NotNull RoleRequestDTO roleRequestDTO) {
        return Role.builder()
                .description(roleRequestDTO.description())
                .name(roleRequestDTO.name())
                .build();
    }

    public static RoleResponseDTO from(final Role role) {
        if(role == null){
            return null;
        }
        return new RoleResponseDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                role.getCreatedDate(),
                role.getCreatedBy(),
                role.getLastModifiedDate(),
                role.getLastModifiedBy()
        );
    }


    private static @NotNull Profile profileFromUserRequestDTO(final @NotNull UserRequestDTO userRequestDTO){
        final Profile profile = new Profile();
        profile.setFirstname(userRequestDTO.getFirstname());
        profile.setLastname(userRequestDTO.getLastname());
        profile.setPlaceOfBirth(userRequestDTO.getPlaceOfBirth());
        profile.setDateOfBirth(userRequestDTO.getDateOfBirth());
        profile.setNationality(userRequestDTO.getNationality());
        profile.setGender(userRequestDTO.getGender());
        profile.setPersonalIdentificationNumber(userRequestDTO.getPersonalIdentificationNumber());
        return profile;
    }

    private static ProfileResponseDTO profileResponseDTOFromProfile(final Profile profile) {
        if(profile == null) {
            return null;
        }
        return ProfileResponseDTO.builder()
                .id(profile.getId())
                .firstname(profile.getFirstname())
                .lastname(profile.getLastname())
                .placeOfBirth(profile.getPlaceOfBirth())
                .dateOfBirth(profile.getDateOfBirth())
                .nationality(profile.getNationality())
                .gender(profile.getGender())
                .personalIdentificationNumber(profile.getPersonalIdentificationNumber())
                .createdDate(profile.getCreatedDate())
                .createdBy(profile.getCreatedBy())
                .lastModifiedDate(profile.getLastModifiedDate())
                .lastModifiedBy(profile.getLastModifiedBy())
                .build();
    }
}
