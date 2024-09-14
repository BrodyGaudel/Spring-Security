package org.mounanga.securityservice.restcontroller;

import org.mounanga.securityservice.dto.RoleRequestDTO;
import org.mounanga.securityservice.dto.RoleResponseDTO;
import org.mounanga.securityservice.service.RoleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleRestController {

    private final RoleService roleService;

    public RoleRestController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/name/{roleName}")
    public RoleResponseDTO findRoleByName(@PathVariable String roleName) {
        return roleService.findRoleByName(roleName);
    }

    @GetMapping("/id/{id}")
    public RoleResponseDTO findRoleById(@PathVariable Long id) {
        return roleService.findRoleById(id);
    }

    @GetMapping("/list")
    public List<RoleResponseDTO> findAllRoles() {
        return roleService.findAllRoles();
    }

    @GetMapping("/list/page")
    public List<RoleResponseDTO> findAllRoles(@RequestParam(name = "page", defaultValue = "0") int page,
                                              @RequestParam(name = "size", defaultValue = "9") int size) {
        return roleService.findAllRoles(page, size);
    }

    @GetMapping("/search")
    public List<RoleResponseDTO> searchAllRoles(@RequestParam(name = "keyword", defaultValue = " ") String keyword,
                                                @RequestParam(name = "page", defaultValue = "0") int page,
                                                @RequestParam(name = "size", defaultValue = "9") int size) {
        return roleService.searchAllRoles(keyword, page, size);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteRoleById(@PathVariable Long id) {
        roleService.deleteRoleById(id);
    }

    @PostMapping("/create")
    public RoleResponseDTO saveRole(@RequestBody RoleRequestDTO role) {
        return roleService.saveRole(role);
    }

    @PutMapping("/update/{id}")
    public RoleResponseDTO updateRole(@PathVariable Long id, @RequestBody RoleRequestDTO roleRequestDTO) {
        return roleService.updateRole(id, roleRequestDTO);
    }
}

