package pro.thinhha.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import pro.thinhha.core.util.SecurityUtil;

import java.util.Optional;
import java.util.UUID;

/**
 * Configuration for JPA auditing with UUID-based user IDs.
 * Enable automatic population of createdBy, updatedBy, createdAt, updatedAt fields with UUID values.
 *
 * To use this instead of the Long-based auditing, set in application.properties:
 * app.auditing.id-type=uuid
 *
 * Or disable the default Long-based auditing by excluding JpaAuditingConfig.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "uuidAuditorProvider")
@ConditionalOnProperty(name = "app.auditing.id-type", havingValue = "uuid")
public class UuidJpaAuditingConfig {

    @Bean
    public AuditorAware<UUID> uuidAuditorProvider() {
        return new UuidAuditorAwareImpl();
    }

    /**
     * Implementation that retrieves the current authenticated user's UUID from SecurityContext.
     * Returns empty if no authenticated user is found.
     */
    public static class UuidAuditorAwareImpl implements AuditorAware<UUID> {

        @Override
        public Optional<UUID> getCurrentAuditor() {
            return SecurityUtil.getCurrentUserId();
        }
    }
}
