package pro.thinhha.core.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.thinhha.core.auth.dto.UserDTO;
import pro.thinhha.core.auth.entity.Role;
import pro.thinhha.core.auth.entity.User;
import pro.thinhha.core.auth.repository.RoleRepository;
import pro.thinhha.core.auth.repository.UserRepository;
import pro.thinhha.core.exception.BusinessException;
import pro.thinhha.core.exception.ResourceNotFoundException;
import pro.thinhha.core.exception.UnauthorizedException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing user operations.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Get the currently authenticated user.
     *
     * @return UserDTO of the authenticated user
     */
    @Transactional(readOnly = true)
    public UserDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UnauthorizedException("User is not authenticated");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        return UserDTO.fromEntity(user);
    }

    /**
     * Get user by ID.
     *
     * @param id the user ID
     * @return UserDTO
     */
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        return UserDTO.fromEntity(user);
    }

    /**
     * Get user by username.
     *
     * @param username the username
     * @return UserDTO
     */
    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return UserDTO.fromEntity(user);
    }

    /**
     * Get all users with pagination.
     *
     * @param pageable pagination information
     * @return page of UserDTO
     */
    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserDTO::fromEntity);
    }

    /**
     * Update user information.
     *
     * @param id      the user ID
     * @param request the update request
     * @return updated UserDTO
     */
    @Transactional
    public UserDTO updateUser(Long id, UserDTO request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Update fields
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getStatus() != null) {
            user.setStatus(request.getStatus());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", updatedUser.getUsername());

        return UserDTO.fromEntity(updatedUser);
    }

    /**
     * Change user password.
     *
     * @param id          the user ID
     * @param oldPassword the old password
     * @param newPassword the new password
     */
    @Transactional
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BusinessException("Old password is incorrect");
        }

        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed successfully for user: {}", user.getUsername());
    }

    /**
     * Soft delete a user.
     *
     * @param id the user ID
     */
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String deletedBy = authentication != null ? authentication.getName() : "system";

        user.delete(deletedBy);
        userRepository.save(user);
        log.info("User soft deleted: {}", user.getUsername());
    }

    /**
     * Restore a soft-deleted user.
     *
     * @param id the user ID
     */
    @Transactional
    public void restoreUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        user.restore();
        userRepository.save(user);
        log.info("User restored: {}", user.getUsername());
    }

    /**
     * Assign roles to a user.
     *
     * @param id        the user ID
     * @param roleNames the role names to assign
     * @return updated UserDTO
     */
    @Transactional
    public UserDTO assignRoles(Long id, Set<String> roleNames) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        Set<Role> roles = roleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName)))
                .collect(Collectors.toSet());

        // Clear existing roles and add new ones
        user.getRoles().clear();
        roles.forEach(user::addRole);

        User updatedUser = userRepository.save(user);
        log.info("Roles assigned to user {}: {}", updatedUser.getUsername(), roleNames);

        return UserDTO.fromEntity(updatedUser);
    }

    /**
     * Get all available roles.
     *
     * @return list of role names
     */
    @Transactional(readOnly = true)
    public List<String> getAllRoles() {
        return roleRepository.findAll().stream()
                .map(Role::getName)
                .collect(Collectors.toList());
    }
}
