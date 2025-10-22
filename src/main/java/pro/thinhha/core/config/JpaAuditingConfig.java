package pro.thinhha.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

/**
 * Configuration for JPA auditing.
 * Enable automatic population of createdBy, updatedBy, createdAt, updatedAt fields.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }

    /**
     * Default implementation that returns "system" as the auditor.
     * Override this in your application to provide actual user information.
     */
    public static class AuditorAwareImpl implements AuditorAware<String> {

        @Override
        public Optional<String> getCurrentAuditor() {
            // TODO: Get current user from SecurityContext or session
            // For now, return "system" as default
            return Optional.of("system");
        }
    }
}
