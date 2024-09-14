package org.mounanga.securityservice.service;

import org.mounanga.securityservice.dto.RoleRequestDTO;
import org.mounanga.securityservice.dto.RoleResponseDTO;

import java.util.List;

public interface RoleService {
    RoleResponseDTO findRoleByName(String roleName);
    RoleResponseDTO findRoleById(Long id);
    List<RoleResponseDTO> findAllRoles();
    List<RoleResponseDTO> findAllRoles(int page, int size);
    List<RoleResponseDTO> searchAllRoles(String keyword, int page, int size);

    void deleteRoleById(Long id);
    RoleResponseDTO saveRole(RoleRequestDTO role);
    RoleResponseDTO updateRole(Long id, RoleRequestDTO roleRequestDTO);
}
