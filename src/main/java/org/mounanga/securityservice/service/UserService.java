package org.mounanga.securityservice.service;


import org.mounanga.securityservice.dto.*;

import java.util.List;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    UserResponseDTO updateUser(Long id, UserRequestDTO userRequestDTO);
    void deleteUserById(Long id);
    UserResponseDTO addRoleToUser(UserRoleRequestDTO userRoleRequestDTO);
    UserResponseDTO removeRoleFromUser(UserRoleRequestDTO userRoleRequestDTO);
    UserResponseDTO updateUserPassword(String username, UpdatePasswordRequestDTO updatePasswordRequestDTO);

    UserResponseDTO getUserById(Long id);
    UserResponseDTO getUserByUsername(String username);
    UserResponseDTO getUserByEmail(String email);
    List<UserResponseDTO> getUsers(int page, int size);
    List<UserResponseDTO> getUsers();
    List<UserResponseDTO> searchUsers(String keyword, int page, int size);
}
