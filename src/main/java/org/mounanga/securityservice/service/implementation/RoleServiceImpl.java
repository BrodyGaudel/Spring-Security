package org.mounanga.securityservice.service.implementation;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.mounanga.securityservice.dto.RoleRequestDTO;
import org.mounanga.securityservice.dto.RoleResponseDTO;
import org.mounanga.securityservice.entity.Role;
import org.mounanga.securityservice.exception.FieldError;
import org.mounanga.securityservice.exception.FieldValidationException;
import org.mounanga.securityservice.exception.RoleNotFoundException;
import org.mounanga.securityservice.repository.RoleRepository;
import org.mounanga.securityservice.service.RoleService;
import org.mounanga.securityservice.util.Mappers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class RoleServiceImpl implements RoleService {

    private static final String ROLE_NOT_FOUND = "Role not found";

    private final RoleRepository roleRepository;

    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public RoleResponseDTO findRoleByName(String roleName) {
        log.info("In findRoleByName()");
        Role role = roleRepository.findByName(roleName).orElseThrow(() -> new RoleNotFoundException(ROLE_NOT_FOUND));
        log.info("role with name '{}' found", roleName);
        return Mappers.from(role);
    }

    @Override
    public RoleResponseDTO findRoleById(Long id) {
        log.info("In findRoleById()");
        Role role = roleRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(ROLE_NOT_FOUND));
        log.info("role with id '{}' found", id);
        return Mappers.from(role);
    }

    @Override
    public List<RoleResponseDTO> findAllRoles() {
        log.info("In findAllRoles()");
        List<Role> roles = roleRepository.findAll();
        log.info("roles found");
        return roles.stream().map(Mappers::from).toList();
    }

    @Override
    public List<RoleResponseDTO> findAllRoles(int page, int size) {
        log.info("In findAllRoles() by page");
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> roles = roleRepository.findAll(pageable);
        log.info("{} roles found", roles.getTotalElements());
        return roles.getContent().stream().map(Mappers::from).toList();
    }

    @Override
    public List<RoleResponseDTO> searchAllRoles(String keyword, int page, int size) {
        log.info("In searchAllRoles()");
        Pageable pageable = PageRequest.of(page, size);
        Page<Role> roles = roleRepository.search("%"+keyword+"%", pageable);
        log.info("{} roles found.", roles.getTotalElements());
        return roles.getContent().stream().map(Mappers::from).toList();
    }

    @Override
    public void deleteRoleById(Long id) {
        log.info("In deleteRoleById()");
        roleRepository.deleteById(id);
        log.info("role with id '{}' deleted", id);
    }

    @Transactional
    @Override
    public RoleResponseDTO saveRole(RoleRequestDTO role) {
        log.info("In saveRole()");
        Role roleEntity = Mappers.from(role);
        if(roleRepository.existsByName(roleEntity.getName())) {
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("name", "Name is already in use"));
            throw new FieldValidationException(errors, "problem of uniqueness of name fields");
        }
        Role savedRole = roleRepository.save(roleEntity);
        log.info("role has been saved with id '{}'", savedRole.getId());
        return Mappers.from(savedRole);
    }

    @Transactional
    @Override
    public RoleResponseDTO updateRole(Long id, @NotNull RoleRequestDTO roleRequestDTO) {
        log.info("In updateRole()");
        Role role = roleRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(ROLE_NOT_FOUND));
        if(!role.getName().equals(roleRequestDTO.name()) && roleRepository.existsByName(roleRequestDTO.name())) {
            List<FieldError> errors = new ArrayList<>();
            errors.add(new FieldError("name", "Name is already in use"));
            throw new FieldValidationException(errors, "problem of uniqueness of name fields");
        }
        role.setName(roleRequestDTO.name());
        role.setDescription(roleRequestDTO.description());
        Role updatedRole = roleRepository.save(role);
        log.info("role with id {} has been updated", updatedRole.getId());
        return Mappers.from(updatedRole);
    }
}
