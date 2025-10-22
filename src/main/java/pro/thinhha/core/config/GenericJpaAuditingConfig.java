package pro.thinhha.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import pro.thinhha.core.util.SecurityUtil;

import java.io.Serializable;
import java.util.Optional;

/**
 * Generic JPA auditing configuration that works with any ID type.
 * This configuration automatically detects the user ID type and uses it for audit fields.
 *
 * This is enabled by default. To use type-specific auditing (Long or UUID), set:
 * app.auditing.id-type=long
 * or
 * app.auditing.id-type=uuid
 *
 * For custom ID types, this generic configuration will work automatically.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "genericAuditorProvider")
@ConditionalOnProperty(name = "app.auditing.enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnMissingBean(name = {"auditorProvider", "uuidAuditorProvider"})
public class GenericJpaAuditingConfig {

    /**
     * Creates a generic auditor provider that works with any Serializable ID type.
     * Uses reflection to extract the user ID from the authenticated user.
     *
     * @param <ID> the ID type (Long, UUID, String, etc.)
     * @return AuditorAware instance
     */
    @Bean
    public <ID extends Serializable> AuditorAware<ID> genericAuditorProvider() {
        return new GenericAuditorAwareImpl<>();
    }

    /**
     * Generic implementation that retrieves the current authenticated user's ID.
     * Works with any Serializable ID type.
     *
     * @param <ID> the ID type
     */
    public static class GenericAuditorAwareImpl<ID extends Serializable> implements AuditorAware<ID> {

        @Override
        public Optional<ID> getCurrentAuditor() {
            return SecurityUtil.getCurrentUserId();
        }
    }
}
