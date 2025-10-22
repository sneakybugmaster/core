package pro.thinhha.core.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import pro.thinhha.core.auth.dto.UserDTO;
import pro.thinhha.core.auth.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * REST controller for user management operations.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Get current authenticated user's profile.
     *
     * GET /api/users/me
     *
     * @return current user information
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        UserDTO user = userService.getCurrentUser();
        return ResponseEntity.ok(user);
    }

    /**
     * Get user by ID.
     *
     * GET /api/users/{id}
     *
     * @param id the user ID
     * @return user information
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Get user by username.
     *
     * GET /api/users/username/{username}
     *
     * @param username the username
     * @return user information
     */
    @GetMapping("/username/{username}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        UserDTO user = userService.getUserByUsername(username);
        return ResponseEntity.ok(user);
    }

    /**
     * Get all users with pagination.
     *
     * GET /api/users
     *
     * @param pageable pagination and sorting parameters
     * @return page of users
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Update user information.
     *
     * PUT /api/users/{id}
     *
     * @param id      the user ID
     * @param request the update request
     * @return updated user information
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO request) {
        UserDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Change user password.
     *
     * PUT /api/users/{id}/password
     *
     * @param id      the user ID
     * @param request the password change request
     * @return success message
     */
    @PutMapping("/{id}/password")
    @PreAuthorize("#id == authentication.principal.id")
    public ResponseEntity<Map<String, String>> changePassword(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        String oldPassword = request.get("oldPassword");
        String newPassword = request.get("newPassword");
        userService.changePassword(id, oldPassword, newPassword);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Password changed successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Delete user (soft delete).
     *
     * DELETE /api/users/{id}
     *
     * @param id the user ID
     * @return success message
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Restore soft-deleted user.
     *
     * PUT /api/users/{id}/restore
     *
     * @param id the user ID
     * @return success message
     */
    @PutMapping("/{id}/restore")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Map<String, String>> restoreUser(@PathVariable Long id) {
        userService.restoreUser(id);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User restored successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Assign roles to a user.
     *
     * PUT /api/users/{id}/roles
     *
     * @param id    the user ID
     * @param roles the roles to assign
     * @return updated user information
     */
    @PutMapping("/{id}/roles")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDTO> assignRoles(@PathVariable Long id, @RequestBody Set<String> roles) {
        UserDTO updatedUser = userService.assignRoles(id, roles);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Get all available roles.
     *
     * GET /api/users/roles/all
     *
     * @return list of role names
     */
    @GetMapping("/roles/all")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<String>> getAllRoles() {
        List<String> roles = userService.getAllRoles();
        return ResponseEntity.ok(roles);
    }
}
