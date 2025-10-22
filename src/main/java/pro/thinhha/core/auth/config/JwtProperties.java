package pro.thinhha.core.auth.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for JWT token management.
 *
 * Configure these properties in your application.properties or application.yml:
 *
 * app.jwt.secret=your-256-bit-secret-key-here
 * app.jwt.expiration=86400000
 * app.jwt.refresh-expiration=604800000
 */
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
@Getter
@Setter
public class JwtProperties {

    /**
     * Secret key for signing JWT tokens.
     * IMPORTANT: Must be at least 256 bits (32 characters) for HS256 algorithm.
     * In production, use a strong random key and store it securely (e.g., environment variable).
     */
    private String secret = "default-secret-key-change-this-in-production-must-be-at-least-256-bits";

    /**
     * JWT token expiration time in milliseconds.
     * Default: 86400000 ms (24 hours)
     */
    private long expiration = 86400000L;

    /**
     * Refresh token expiration time in milliseconds.
     * Default: 604800000 ms (7 days)
     */
    private long refreshExpiration = 604800000L;

    /**
     * Token type/prefix used in Authorization header.
     * Default: "Bearer"
     */
    private String tokenPrefix = "Bearer ";

    /**
     * Header name for JWT token.
     * Default: "Authorization"
     */
    private String headerName = "Authorization";
}
