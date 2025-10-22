package pro.thinhha.core.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception thrown when user doesn't have permission to access a resource.
 */
public class ForbiddenException extends BaseException {

    public ForbiddenException(String message) {
        super(message, "FORBIDDEN", HttpStatus.FORBIDDEN);
    }

    public ForbiddenException() {
        super("Access denied", "FORBIDDEN", HttpStatus.FORBIDDEN);
    }
}
