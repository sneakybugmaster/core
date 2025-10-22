package pro.thinhha.core.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pro.thinhha.core.auth.dto.AuthResponse;
import pro.thinhha.core.auth.dto.LoginRequest;
import pro.thinhha.core.auth.dto.RegisterRequest;
import pro.thinhha.core.auth.service.AuthService;

import java.util.HashMap;
import java.util.Map;

/**
 * REST controller for authentication operations.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user.
     *
     * POST /api/auth/register
     *
     * @param request the registration request
     * @return authentication response with JWT token
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Login user and get JWT token.
     *
     * POST /api/auth/login
     *
     * @param request the login request
     * @return authentication response with JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Logout user.
     *
     * POST /api/auth/logout
     *
     * Note: Since we're using JWT, logout is primarily a client-side operation.
     * The client should delete the token from storage.
     * For server-side token invalidation, implement a token blacklist.
     *
     * @return success message
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        authService.logout();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logged out successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Refresh JWT token.
     *
     * POST /api/auth/refresh
     *
     * @param refreshToken the refresh token
     * @return new authentication response with JWT token
     */
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        AuthResponse response = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    /**
     * Health check endpoint for authentication service.
     *
     * GET /api/auth/health
     *
     * @return health status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Authentication Service");
        return ResponseEntity.ok(response);
    }
}
