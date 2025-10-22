package pro.thinhha.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Configuration for JPA auditing.
 * Enable automatic population of createdBy, updatedBy, createdAt, updatedAt fields.
 * Uses userId (Long) instead of username (String) for better referential integrity.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return new AuditorAwareImpl();
    }

    /**
     * Implementation that retrieves the current authenticated user's ID from SecurityContext.
     * Returns null if no authenticated user is found (allows system to handle default).
     */
    public static class AuditorAwareImpl implements AuditorAware<Long> {

        @Override
        public Optional<Long> getCurrentAuditor() {
            // Get current user from SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated() ||
                    "anonymousUser".equals(authentication.getPrincipal())) {
                return Optional.empty();
            }

            // Get the User object from authentication principal
            Object principal = authentication.getPrincipal();

            // Check if principal is a User entity (from our auth module)
            if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
                try {
                    // Try to cast to our User entity which has getId() method
                    // This works because our User entity implements UserDetails
                    Object user = principal;
                    Long userId = (Long) user.getClass().getMethod("getId").invoke(user);
                    return Optional.ofNullable(userId);
                } catch (Exception e) {
                    // If reflection fails, return empty (system will handle)
                    return Optional.empty();
                }
            }

            return Optional.empty();
        }
    }
}
