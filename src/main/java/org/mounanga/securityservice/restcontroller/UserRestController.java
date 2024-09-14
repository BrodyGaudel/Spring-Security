package org.mounanga.securityservice.restcontroller;

import org.mounanga.securityservice.dto.UpdatePasswordRequestDTO;
import org.mounanga.securityservice.dto.UserRequestDTO;
import org.mounanga.securityservice.dto.UserResponseDTO;
import org.mounanga.securityservice.dto.UserRoleRequestDTO;
import org.mounanga.securityservice.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @PostMapping("/create")
    public UserResponseDTO createUser(@RequestBody UserRequestDTO userRequestDTO) {
        return userService.createUser(userRequestDTO);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @PutMapping("/update/{id}")
    public UserResponseDTO updateUser(@PathVariable Long id, @RequestBody UserRequestDTO userRequestDTO) {
        return userService.updateUser(id, userRequestDTO);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteUserById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @PostMapping("/roles/add")
    public UserResponseDTO addRoleToUser(@RequestBody UserRoleRequestDTO userRoleRequestDTO) {
        return userService.addRoleToUser(userRoleRequestDTO);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @PostMapping("/roles/remove")
    public UserResponseDTO removeRoleFromUser(@RequestBody UserRoleRequestDTO userRoleRequestDTO) {
        return userService.removeRoleFromUser(userRoleRequestDTO);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN,USER')")
    @PutMapping("/password/update")
    public UserResponseDTO updateUserPassword(@RequestBody UpdatePasswordRequestDTO updatePasswordRequestDTO) {
        String username = getCurrentUsername();
        return userService.updateUserPassword(username, updatePasswordRequestDTO);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN,USER')")
    @GetMapping("/get/{id}")
    public UserResponseDTO getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN,USER')")
    @GetMapping("/username")
    public UserResponseDTO getUserByUsername() {
        String username = getCurrentUsername();
        return userService.getUserByUsername(username);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN,USER')")
    @GetMapping("/email/{email}")
    public UserResponseDTO getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @GetMapping("/list/page")
    public List<UserResponseDTO> getUsers(@RequestParam(name = "page", defaultValue = "0") int page,
                                          @RequestParam(name = "size", defaultValue = "9") int size) {
        return userService.getUsers(page, size);
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @GetMapping("/list")
    public List<UserResponseDTO> getUsers() {
        return userService.getUsers();
    }

    @PreAuthorize("hasAnyAuthority('ADMIN','SUPER_ADMIN')")
    @GetMapping("/search")
    public List<UserResponseDTO> searchUsers(@RequestParam(name = "keyword", defaultValue = " ") String keyword,
                                             @RequestParam(name = "page", defaultValue = "0") int page,
                                             @RequestParam(name = "size", defaultValue = "9") int size) {
        return userService.searchUsers(keyword, page, size);
    }


    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails userDetails) {
            return userDetails.getUsername();
        }
        return principal.toString();
    }
}

