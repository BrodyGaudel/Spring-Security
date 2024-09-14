package org.mounanga.securityservice.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mounanga.securityservice.dto.UpdatePasswordRequestDTO;
import org.mounanga.securityservice.dto.UserRequestDTO;
import org.mounanga.securityservice.dto.UserResponseDTO;
import org.mounanga.securityservice.dto.UserRoleRequestDTO;
import org.mounanga.securityservice.entity.Role;
import org.mounanga.securityservice.entity.User;
import org.mounanga.securityservice.exception.FieldError;
import org.mounanga.securityservice.exception.FieldValidationException;
import org.mounanga.securityservice.exception.RoleNotFoundException;
import org.mounanga.securityservice.exception.UserNotFoundException;
import org.mounanga.securityservice.repository.ProfileRepository;
import org.mounanga.securityservice.repository.RoleRepository;
import org.mounanga.securityservice.repository.UserRepository;
import org.mounanga.securityservice.service.UserService;
import org.mounanga.securityservice.util.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, ProfileRepository profileRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    @Override
    public UserResponseDTO createUser(@NotNull UserRequestDTO userRequestDTO) {
        log.info("In createUser()");
        checkFieldUniquenessBeforeSave(userRequestDTO.getEmail(), userRequestDTO.getUsername(), userRequestDTO.getPersonalIdentificationNumber());
        User user = Mappers.from(userRequestDTO);
        user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        User savedUser = userRepository.save(user);
        log.info("user saved with id '{}' at '{}' by '{}'", savedUser.getId(), savedUser.getCreatedDate(), savedUser.getCreatedBy());
        return Mappers.from(savedUser);
    }

    @Transactional
    @Override
    public UserResponseDTO updateUser(Long id, @NotNull UserRequestDTO userRequestDTO) {
        log.info("In updateUser()");
        User user = findUserById(id);
        checkFieldUniquenessBeforeUpdate(user, userRequestDTO.getEmail(), userRequestDTO.getUsername(), userRequestDTO.getPersonalIdentificationNumber());
        updateUserField(user,userRequestDTO);
        User updatedUser = userRepository.save(user);
        log.info("user with id '{}' updated at '{}' by '{}'", updatedUser.getId(), updatedUser.getLastModifiedDate(), updatedUser.getLastModifiedBy());
        return Mappers.from(updatedUser);
    }

    @Override
    public void deleteUserById(Long id) {
        log.info("In deleteUserById()");
        userRepository.deleteById(id);
        log.info("user deleted successfully");
    }

    @Transactional
    @Override
    public UserResponseDTO addRoleToUser(@NotNull UserRoleRequestDTO userRoleRequestDTO) {
        log.info("In addRoleToUser()");
        User user = findUserById(userRoleRequestDTO.userId());
        Role role = findRoleById(userRoleRequestDTO.roleId());
        user.getRoles().add(role);
        User updatedUser = userRepository.save(user);
        log.info("role '{}' added successfully to user with id {}", role.getName(), updatedUser.getId());
        return Mappers.from(updatedUser);
    }

    @Transactional
    @Override
    public UserResponseDTO removeRoleFromUser(@NotNull UserRoleRequestDTO userRoleRequestDTO) {
        log.info("In removeRoleFromUser()");
        User user = findUserById(userRoleRequestDTO.userId());
        Role role = findRoleById(userRoleRequestDTO.roleId());
        user.getRoles().remove(role);
        User updatedUser = userRepository.save(user);
        log.info("role '{}' removed successfully from user with id {}", role.getName(), updatedUser.getId());
        return Mappers.from(updatedUser);
    }

    @Transactional
    @Override
    public UserResponseDTO updateUserPassword(String username, @NotNull UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        log.info("In updateUserPassword()");
        User user = findUserByUsername(username);
        if (!passwordEncoder.matches(updatePasswordRequestDTO.oldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(updatePasswordRequestDTO.newPassword()));
        User updatedUser = userRepository.save(user);
        log.info("password updated successfully for user with id '{}'", updatedUser.getId());
        return Mappers.from(updatedUser);
    }

    @Override
    public UserResponseDTO getUserById(Long id) {
        log.info("In getUserById()");
        User user = findUserById(id);
        log.info("user with id '{}' found", user.getId());
        return Mappers.from(user);
    }

    @Override
    public UserResponseDTO getUserByUsername(String username) {
        log.info("In getUserByUsername()");
        User user = findUserByUsername(username);
        log.info("user with username '{}' found", username);
        return Mappers.from(user);
    }

    @Override
    public UserResponseDTO getUserByEmail(String email) {
        log.info("In getUserByEmail()");
        User user = userRepository.findByEmail(email)
                .orElseThrow( () -> new UserNotFoundException(String.format("User with email '%s' not found", email)));
        log.info("user with email '{}' found", user.getEmail());
        return Mappers.from(user);
    }

    @Override
    public List<UserResponseDTO> getUsers(int page, int size) {
        log.info("In getUsers() with page & size");
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.findAll(pageable);
        log.info("{} users found", users.getTotalElements());
        return Mappers.from(users.getContent());
    }

    @Override
    public List<UserResponseDTO> getUsers() {
        log.info("In getUsers()");
        List<User> users = userRepository.findAll();
        log.info("{} users found.", users.size());
        return Mappers.from(users);
    }

    @Override
    public List<UserResponseDTO> searchUsers(String keyword, int page, int size) {
        log.info("In searchUsers()");
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = userRepository.search("%"+keyword+"%", pageable);
        log.info("{} users found. with query '{}'", users.getTotalElements(), keyword);
        return Mappers.from(users.getContent());
    }

    // PRIVATE METHODS

    private void checkFieldUniquenessBeforeSave(String email, String username, String personalIdentificationNumber) {
        List<FieldError> errors = new ArrayList<>();
        if(userRepository.existsByUsername(username)){
            errors.add(new FieldError("username", "Username is already in use"));
        }
        if(userRepository.existsByEmail(email)){
            errors.add(new FieldError("email", "Email is already in use"));
        }
        if(profileRepository.existsByPersonalIdentificationNumber(personalIdentificationNumber)){
            errors.add(new FieldError("personalIdentificationNumber", "PersonalIdentificationNumber is already in use"));
        }
        if(!errors.isEmpty()){
            throw new FieldValidationException(errors, "problem of uniqueness of e-mail, personal identification number or username fields");
        }
    }

    private void checkFieldUniquenessBeforeUpdate(@NotNull User user, String email, String username, String personalIdentificationNumber) {
        List<FieldError> errors = new ArrayList<>();
        if(!user.getEmail().equals(email) && userRepository.existsByEmail(email)){
                errors.add(new FieldError("email", "Email is already in use"));
        }

        if(!user.getUsername().equals(username) && userRepository.existsByUsername(username)){
                errors.add(new FieldError("username", "Username is already in use"));
        }

        if(!user.getProfile().getPersonalIdentificationNumber().equals(personalIdentificationNumber) && profileRepository.existsByPersonalIdentificationNumber(personalIdentificationNumber)){
                errors.add(new FieldError("personalIdentificationNumber", "PersonalIdentificationNumber is already in use"));
        }

        if(!errors.isEmpty()){
            throw new FieldValidationException(errors, "problem of uniqueness of e-mail, personal identification number or username fields");
        }
    }

    private void updateUserField(@NotNull User user, @NotNull UserRequestDTO userRequestDTO) {
        user.getProfile().setPersonalIdentificationNumber(userRequestDTO.getPersonalIdentificationNumber());
        user.getProfile().setFirstname(userRequestDTO.getFirstname());
        user.getProfile().setLastname(userRequestDTO.getLastname());
        user.getProfile().setPlaceOfBirth(userRequestDTO.getPlaceOfBirth());
        user.getProfile().setDateOfBirth(userRequestDTO.getDateOfBirth());
        user.getProfile().setNationality(userRequestDTO.getNationality());
        user.getProfile().setGender(userRequestDTO.getGender());
        user.setUsername(userRequestDTO.getUsername());
        user.setEmail(userRequestDTO.getEmail());
    }

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow( () -> new UserNotFoundException(String.format("user with id '%s' not found", id)));
    }

    private Role findRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow( () -> new RoleNotFoundException(String.format("role with id '%s' not found", id)));
    }

    private User findUserByUsername(String username){
        return userRepository.findByUsername(username)
                .orElseThrow( () -> new UserNotFoundException(String.format("user with username '%s' not found", username)));
    }
}
