package pro.thinhha.core.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import pro.thinhha.core.util.SecurityUtil;

import java.util.Optional;

/**
 * Configuration for JPA auditing with Long-based user IDs.
 * Enable automatic population of createdBy, updatedBy, createdAt, updatedAt fields.
 * Uses userId (Long) instead of username (String) for better referential integrity.
 *
 * This is the default auditing configuration. To use UUID-based auditing instead, set:
 * app.auditing.id-type=uuid
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
@ConditionalOnProperty(name = "app.auditing.id-type", havingValue = "long", matchIfMissing = true)
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<Long> auditorProvider() {
        return new AuditorAwareImpl();
    }

    /**
     * Implementation that retrieves the current authenticated user's ID from SecurityContext.
     * Returns empty if no authenticated user is found.
     */
    public static class AuditorAwareImpl implements AuditorAware<Long> {

        @Override
        public Optional<Long> getCurrentAuditor() {
            return SecurityUtil.getCurrentUserId();
        }
    }
}
