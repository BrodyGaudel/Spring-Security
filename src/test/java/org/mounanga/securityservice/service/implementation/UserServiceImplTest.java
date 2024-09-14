package org.mounanga.securityservice.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mounanga.securityservice.dto.UpdatePasswordRequestDTO;
import org.mounanga.securityservice.dto.UserRequestDTO;
import org.mounanga.securityservice.dto.UserResponseDTO;
import org.mounanga.securityservice.dto.UserRoleRequestDTO;
import org.mounanga.securityservice.entity.Profile;
import org.mounanga.securityservice.entity.Role;
import org.mounanga.securityservice.entity.User;
import org.mounanga.securityservice.enums.Gender;
import org.mounanga.securityservice.exception.FieldValidationException;
import org.mounanga.securityservice.exception.RoleNotFoundException;
import org.mounanga.securityservice.exception.UserNotFoundException;
import org.mounanga.securityservice.repository.ProfileRepository;
import org.mounanga.securityservice.repository.RoleRepository;
import org.mounanga.securityservice.repository.UserRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserRequestDTO userRequestDTO;
    private Role role;
    private UserRoleRequestDTO userRoleRequestDTO;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, profileRepository, roleRepository, passwordEncoder);
        Profile profile = Profile.builder().firstname("Brody").lastname("MOUNANGA").dateOfBirth(LocalDate.of(1994,1,1))
                .placeOfBirth("Gabon").nationality("Gabon").gender(Gender.M).id(1L).personalIdentificationNumber("222222222")
                .build();
        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().id(2L).name("ADMIN").description("ADMIN").build());
        roles.add(Role.builder().id(3L).name("USER").description("USER").build());

        user = User.builder().id(1L).email("user@app.com").username("username").password("password").enabled(Boolean.TRUE).passwordMustBeModified(Boolean.TRUE)
                .profile(profile).roles(roles)
                .build();
        userRequestDTO = UserRequestDTO.builder()
                .firstname("John").lastname("Doe").dateOfBirth(LocalDate.of(1994,1,1))
                .placeOfBirth("Gabon").email("user@app.com").username("password").username("username").gender(Gender.M)
                .personalIdentificationNumber("123456789").nationality("Gabon")
                .build();
        role = Role.builder().id(1L).name("SUPER_ADMIN").description("SUPER_ADMIN").build();
        userRoleRequestDTO = new UserRoleRequestDTO(1L, 1L);
    }

    @Test
    void testGetUserById(){
        Long id = 1L;
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.of(user));
        UserResponseDTO response = userService.getUserById(id);
        assertNotNull(response);
        assertEquals(id, response.getId());
    }

    @Test
    void testGetUserByIdThrowsUserNotFoundException(){
        Long id = 1L;
        when(userRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    void testGetUserByEmail(){
        String email = "user@app.com";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        UserResponseDTO response = userService.getUserByEmail(email);
        assertNotNull(response);
        assertEquals(email, response.getEmail());
    }

    @Test
    void testGetUserByEmailThrowsUserNotFoundException(){
        String email = "user@app.com";
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserByEmail(email));
    }

    @Test
    void testGetUserByUsername(){
        String username = "username";
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        UserResponseDTO response = userService.getUserByUsername(username);
        assertNotNull(response);
        assertEquals(username, response.getUsername());
    }

    @Test
    void testGetUserByUsernameThrowsUserNotFoundException(){
        String username = "username";
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> userService.getUserByUsername(username));
    }

    @Test
    void testGetUsers(){
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserResponseDTO> response = userService.getUsers();
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void testGetUsersByPage(){
        int page = 0;
        int size = 1;
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(userPage);
        List<UserResponseDTO> response = userService.getUsers(page, size);
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void testSearchUsers(){
        int page = 0;
        int size = 1;
        String keyword = "brody";
        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = new PageImpl<>(List.of(user));
        when(userRepository.search("%"+keyword+"%",pageable)).thenReturn(userPage);
        List<UserResponseDTO> response = userService.searchUsers(keyword, page, size);
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    void testCreateUser(){
        when(userRepository.existsByUsername(userRequestDTO.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(userRequestDTO.getEmail())).thenReturn(false);
        when(profileRepository.existsByPersonalIdentificationNumber(userRequestDTO.getPersonalIdentificationNumber())).thenReturn(false);
        when(passwordEncoder.encode(userRequestDTO.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponseDTO response = userService.createUser(userRequestDTO);
        assertNotNull(response);
        assertEquals(1L, response.getId());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUserFailureDueToNonUniqueFields() {
        when(userRepository.existsByUsername(userRequestDTO.getUsername())).thenReturn(true);
        when(userRepository.existsByEmail(userRequestDTO.getEmail())).thenReturn(false);
        when(profileRepository.existsByPersonalIdentificationNumber(userRequestDTO.getPersonalIdentificationNumber())).thenReturn(false);

        FieldValidationException exception = assertThrows(FieldValidationException.class, () -> userService.createUser(userRequestDTO));

        assertTrue(exception.getMessage().contains("problem of uniqueness"));
        assertEquals(1, exception.getFieldErrors().size());
        assertEquals("username", exception.getFieldErrors().getFirst().field());
        assertEquals("Username is already in use", exception.getFieldErrors().getFirst().message());
    }

    @Test
    void testUpdateUser(){
        userRequestDTO.setEmail("updated@test.com");
        userRequestDTO.setUsername("updatedUser");
        userRequestDTO.setPersonalIdentificationNumber("987654321");
        userRequestDTO.setFirstname("UpdatedFirstname");
        userRequestDTO.setLastname("UpdatedLastname");
        userRequestDTO.setPassword("password123");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(userRequestDTO.getEmail())).thenReturn(false);
        when(userRepository.existsByUsername(userRequestDTO.getUsername())).thenReturn(false);
        when(profileRepository.existsByPersonalIdentificationNumber(userRequestDTO.getPersonalIdentificationNumber())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.updateUser(1L, userRequestDTO);
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("updated@test.com", user.getEmail());
        assertEquals("updatedUser", user.getUsername());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void testDeleteUserByIdSuccess() {
        Long userId = 1L;
        userService.deleteUserById(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void testDeleteUserByIdNotFound() {
        doThrow(new UserNotFoundException("User not found")).when(userRepository).deleteById(1L);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(1L));
        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testAddRoleToUserSuccess() {
        when(userRepository.findById(userRoleRequestDTO.userId())).thenReturn(Optional.of(user));
        when(roleRepository.findById(userRoleRequestDTO.roleId())).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.addRoleToUser(userRoleRequestDTO);

        assertTrue(user.getRoles().contains(role));
        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        verify(userRepository).save(user);
    }

    @Test
    void testAddRoleToUserUserNotFound() {
        when(userRepository.findById(userRoleRequestDTO.userId())).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.addRoleToUser(userRoleRequestDTO));
        assertEquals("user with id '1' not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testAddRoleToUserRoleNotFound() {
        when(userRepository.findById(userRoleRequestDTO.userId())).thenReturn(Optional.of(user));
        when(roleRepository.findById(userRoleRequestDTO.roleId())).thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () -> userService.addRoleToUser(userRoleRequestDTO));
        assertEquals("role with id '1' not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRemoveRoleFromUserSuccess() {
        when(userRepository.findById(userRoleRequestDTO.userId())).thenReturn(Optional.of(user));
        when(roleRepository.findById(userRoleRequestDTO.roleId())).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponseDTO response = userService.removeRoleFromUser(userRoleRequestDTO);
        assertFalse(user.getRoles().contains(role));
        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        verify(userRepository).save(user);
    }

    @Test
    void testRemoveRoleFromUserRoleNotFound() {
        when(userRepository.findById(userRoleRequestDTO.userId())).thenReturn(Optional.of(user));
        when(roleRepository.findById(userRoleRequestDTO.roleId())).thenReturn(Optional.empty());
        assertThrows(RoleNotFoundException.class, () -> userService.removeRoleFromUser(userRoleRequestDTO));
    }

    @Test
    void testRemoveRoleFromUserUserNotFound() {
        when(userRepository.findById(userRoleRequestDTO.userId())).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.removeRoleFromUser(userRoleRequestDTO));
        assertEquals("user with id '1' not found", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserPasswordSuccess() {
        // Given
        UpdatePasswordRequestDTO updatePasswordRequestDTO = new UpdatePasswordRequestDTO("oldPassword", "newPassword");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("oldPassword", user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserResponseDTO responseDTO = userService.updateUserPassword("testUser", updatePasswordRequestDTO);

        // Then
        assertNotNull(responseDTO);
        assertEquals("username", responseDTO.getUsername());
        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(user);
    }

    @Test
    void testUpdateUserPasswordIncorrectOldPassword() {
        // Given
        UpdatePasswordRequestDTO updatePasswordRequestDTO = new UpdatePasswordRequestDTO("wrongOldPassword", "newPassword");

        when(userRepository.findByUsername("testUser")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongOldPassword", user.getPassword())).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> userService.updateUserPassword("testUser", updatePasswordRequestDTO));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUpdateUserPassword_UserNotFound() {
        // Given
        String username = "testUser";
        UpdatePasswordRequestDTO updatePasswordRequestDTO = new UpdatePasswordRequestDTO("oldPassword", "newPassword");

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());


        assertThrows(UserNotFoundException.class, () -> userService.updateUserPassword(username, updatePasswordRequestDTO));

        verify(userRepository, never()).save(any(User.class));
    }




}