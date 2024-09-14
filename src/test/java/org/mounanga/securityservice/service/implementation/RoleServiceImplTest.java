package org.mounanga.securityservice.service.implementation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mounanga.securityservice.dto.RoleRequestDTO;
import org.mounanga.securityservice.dto.RoleResponseDTO;
import org.mounanga.securityservice.entity.Role;
import org.mounanga.securityservice.exception.FieldError;
import org.mounanga.securityservice.exception.FieldValidationException;
import org.mounanga.securityservice.exception.RoleNotFoundException;
import org.mounanga.securityservice.repository.RoleRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@SpringBootTest
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private RoleServiceImpl roleService;

    Role role;
    private RoleRequestDTO roleRequestDTO;
    private Role roleEntity;

    @BeforeEach
    void setUp() {
        roleService = new RoleServiceImpl(roleRepository);
        role = new Role();
        role.setName("ADMIN");
        role.setDescription("Admin");
        role.setId(1L);
        roleRequestDTO = new RoleRequestDTO("ROLE_USER", "ROLE_USER");
        roleEntity = new Role();
        roleEntity.setName("ROLE_USER");

    }

    @Test
    void testFindRoleByName(){
        String roleName = "ADMIN";
        when(roleRepository.findByName(anyString())).thenReturn(Optional.of(role));
        RoleResponseDTO response = roleService.findRoleByName(roleName);
        assertNotNull(response);
        assertEquals(roleName, response.name());
    }

    @Test
    void testFindRoleByNameThrowsRoleNotFoundException(){
        String roleName = "ADMIN";
        when(roleRepository.findByName(anyString())).thenReturn(Optional.empty());
        assertThrows(RoleNotFoundException.class, () -> roleService.findRoleByName(roleName));
    }

    @Test
    void testFindRoleById(){
        Long roleId = 1L;
        when(roleRepository.findById(any(Long.class))).thenReturn(Optional.of(role));
        RoleResponseDTO response = roleService.findRoleById(roleId);
        assertNotNull(response);
        assertEquals(roleId, response.id());
    }

    @Test
    void testFindRoleByIdThrowsRoleNotFoundException(){
        Long roleId = 1L;
        when(roleRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        assertThrows(RoleNotFoundException.class, () -> roleService.findRoleById(roleId));
    }

    @Test
    void testFindAllRoles(){
        List<Role> roles = List.of(role);
        when(roleRepository.findAll()).thenReturn(roles);
        List<RoleResponseDTO> response = roleService.findAllRoles();
        assertNotNull(response);
        assertEquals(roles.size(), response.size());
    }

    @Test
    void testFindAllRolesByPages(){
        int page = 0;
        int size = 1;
        List<Role> roles = List.of(role);
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> rolePage = new PageImpl<>(roles);
        when(roleRepository.findAll(pageable)).thenReturn(rolePage);
        List<RoleResponseDTO> response = roleService.findAllRoles(page, size);
        assertNotNull(response);
        assertEquals(roles.size(), response.size());
    }

    @Test
    void testSearchRoles(){
        String keyword = "admin";
        int page = 0;
        int size = 1;
        List<Role> roles = List.of(role);
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> rolePage = new PageImpl<>(roles);
        when(roleRepository.search("%"+keyword+"%", pageable)).thenReturn(rolePage);

        List<RoleResponseDTO> response = roleService.searchAllRoles(keyword, page, size);
        assertNotNull(response);
        assertEquals(roles.size(), response.size());
    }

    @Test
    void testDeleteRoleByIdSuccess() {

        Long roleId = 1L;
        roleService.deleteRoleById(roleId);
        verify(roleRepository, times(1)).deleteById(roleId);
    }

    @Test
    void testDeleteRoleByIdNonExistentRole() {
        Long roleId = 1L;
        doThrow(new IllegalArgumentException("Role not found")).when(roleRepository).deleteById(roleId);

        assertThrows(IllegalArgumentException.class, () -> roleService.deleteRoleById(roleId));

        verify(roleRepository, times(1)).deleteById(roleId);
    }

    @Test
    void testSaveRoleSuccess() {

        when(roleRepository.existsByName(roleEntity.getName())).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(roleEntity);

        RoleResponseDTO responseDTO = roleService.saveRole(roleRequestDTO);

        assertNotNull(responseDTO);
        assertEquals("ROLE_USER", responseDTO.name());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void testSaveRoleNameAlreadyExists() {
        when(roleRepository.existsByName(roleEntity.getName())).thenReturn(true);

        FieldValidationException exception = assertThrows(FieldValidationException.class, () -> roleService.saveRole(roleRequestDTO));

        List<FieldError> errors = exception.getFieldErrors();
        assertEquals(1, errors.size());
        assertEquals("name", errors.getFirst().field());
        assertEquals("Name is already in use", errors.getFirst().message());

        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testUpdateRoleSuccess() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(roleEntity));
        when(roleRepository.existsByName(roleRequestDTO.name())).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(roleEntity);

        RoleResponseDTO responseDTO = roleService.updateRole(1L, roleRequestDTO);

        assertNotNull(responseDTO);
        assertEquals("ROLE_USER", responseDTO.name());
        assertEquals("ROLE_USER", responseDTO.description());
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void testUpdateRoleRoleNotFound() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        RoleNotFoundException exception = assertThrows(RoleNotFoundException.class, () -> roleService.updateRole(1L, roleRequestDTO));
        assertEquals("Role not found", exception.getMessage());
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    void testUpdateRoleNameAlreadyExists() {
        roleEntity.setDescription("DESCRIPTION");
        roleEntity.setName("NAME");
        roleEntity.setId(1L);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(roleEntity));
        when(roleRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(FieldValidationException.class, () -> roleService.updateRole(1L, roleRequestDTO));
    }
}