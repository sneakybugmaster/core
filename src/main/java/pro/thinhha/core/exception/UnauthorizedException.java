package pro.thinhha.core.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when authentication is required but not provided or invalid.
 */
public class UnauthorizedException extends BaseException {

    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }

    public UnauthorizedException() {
        super("Authentication required", "UNAUTHORIZED", HttpStatus.UNAUTHORIZED);
    }
}
