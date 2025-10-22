package pro.thinhha.core.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Utility class for security-related operations.
 */
public final class SecurityUtil {

    private SecurityUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Get the current authenticated user's ID.
     * Works with any User entity that has a getId() method.
     *
     * @param <T> the ID type (Long, UUID, etc.)
     * @return Optional containing the user ID, or empty if not authenticated
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        // Check if principal is a UserDetails implementation
        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            try {
                // Use reflection to call getId() method
                Object user = principal;
                T userId = (T) user.getClass().getMethod("getId").invoke(user);
                return Optional.ofNullable(userId);
            } catch (Exception e) {
                // If reflection fails, return empty
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    /**
     * Get the current authenticated username.
     *
     * @return Optional containing the username, or empty if not authenticated
     */
    public static Optional<String> getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        return Optional.of(authentication.getName());
    }

    /**
     * Get the current authenticated user object.
     *
     * @param <T> the user type
     * @return Optional containing the user object, or empty if not authenticated
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {
            return Optional.of((T) principal);
        }

        return Optional.empty();
    }

    /**
     * Check if there is an authenticated user.
     *
     * @return true if authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() &&
                !"anonymousUser".equals(authentication.getPrincipal());
    }
}
