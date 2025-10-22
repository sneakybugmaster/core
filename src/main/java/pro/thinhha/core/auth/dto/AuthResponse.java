package pro.thinhha.core.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * DTO for authentication response containing JWT token and user information.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {

    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserDTO user;

    /**
     * Convenience constructor for access token only.
     */
    public AuthResponse(String accessToken, UserDTO user) {
        this.accessToken = accessToken;
        this.user = user;
        this.tokenType = "Bearer";
    }

    /**
     * Convenience constructor with expiration.
     */
    public AuthResponse(String accessToken, Long expiresIn, UserDTO user) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.user = user;
        this.tokenType = "Bearer";
    }
}
