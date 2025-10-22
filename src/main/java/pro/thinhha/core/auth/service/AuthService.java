package pro.thinhha.core.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pro.thinhha.core.auth.config.JwtProperties;
import pro.thinhha.core.auth.dto.AuthResponse;
import pro.thinhha.core.auth.dto.LoginRequest;
import pro.thinhha.core.auth.dto.RegisterRequest;
import pro.thinhha.core.auth.dto.UserDTO;
import pro.thinhha.core.auth.entity.Role;
import pro.thinhha.core.auth.entity.User;
import pro.thinhha.core.auth.repository.RoleRepository;
import pro.thinhha.core.auth.repository.UserRepository;
import pro.thinhha.core.enums.Status;
import pro.thinhha.core.exception.BusinessException;

import java.util.HashSet;
import java.util.Set;

/**
 * Service for handling authentication operations (registration, login, logout).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    /**
     * Register a new user.
     *
     * @param request the registration request
     * @return authentication response with JWT token
     */
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user: {}", request.getUsername());

        // Check if username already exists
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BusinessException("Username is already taken");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email is already in use");
        }

        // Create new user
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .status(Status.ACTIVE)
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(new HashSet<>())
                .build();

        // Assign default role (USER)
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    log.info("Creating default ROLE_USER");
                    Role newRole = new Role("ROLE_USER", "Default user role");
                    return roleRepository.save(newRole);
                });

        user.addRole(userRole);

        // Save user
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        // Generate JWT token
        String token = jwtTokenProvider.generateTokenFromUsername(
                savedUser.getUsername(),
                savedUser.getRoles().stream()
                        .map(Role::getName)
                        .toList()
        );

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration())
                .user(UserDTO.fromEntity(savedUser))
                .build();
    }

    /**
     * Authenticate user and generate JWT token.
     *
     * @param request the login request
     * @return authentication response with JWT token
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        log.info("User login attempt: {}", request.getUsernameOrEmail());

        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(),
                        request.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(authentication);

        // Get user details
        User user = (User) authentication.getPrincipal();
        log.info("User logged in successfully: {}", user.getUsername());

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration())
                .user(UserDTO.fromEntity(user))
                .build();
    }

    /**
     * Logout user (client-side operation - invalidate token on client).
     * For server-side token invalidation, implement a token blacklist.
     */
    public void logout() {
        SecurityContextHolder.clearContext();
        log.info("User logged out successfully");
    }

    /**
     * Refresh JWT token.
     *
     * @param refreshToken the refresh token
     * @return new authentication response with JWT token
     */
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new BusinessException("Invalid refresh token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found"));

        String newAccessToken = jwtTokenProvider.generateTokenFromUsername(
                user.getUsername(),
                user.getRoles().stream()
                        .map(Role::getName)
                        .toList()
        );

        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getUsername());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getExpiration())
                .user(UserDTO.fromEntity(user))
                .build();
    }
}
