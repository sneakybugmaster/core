package pro.thinhha.core.constant;

/**
 * Common error messages.
 */
public final class ErrorMessages {

    private ErrorMessages() {
        throw new UnsupportedOperationException("This is a constant class and cannot be instantiated");
    }

    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String VALIDATION_FAILED = "Validation failed";
    public static final String UNAUTHORIZED = "Authentication required";
    public static final String FORBIDDEN = "Access denied";
    public static final String INTERNAL_ERROR = "An unexpected error occurred";
    public static final String BAD_REQUEST = "Invalid request";

    // Field validation messages
    public static final String FIELD_REQUIRED = "This field is required";
    public static final String FIELD_INVALID = "This field contains an invalid value";
    public static final String EMAIL_INVALID = "Invalid email format";
    public static final String PHONE_INVALID = "Invalid phone number format";
}
