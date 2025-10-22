package pro.thinhha.core.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import pro.thinhha.core.exception.BusinessException;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Utility class for programmatic validation.
 */
public final class ValidationUtil {

    private ValidationUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static final Validator validator = factory.getValidator();

    /**
     * Validate an object and return constraint violations.
     */
    public static <T> Set<ConstraintViolation<T>> validate(T object) {
        return validator.validate(object);
    }

    /**
     * Validate an object and throw exception if invalid.
     */
    public static <T> void validateAndThrow(T object) {
        Set<ConstraintViolation<T>> violations = validate(object);
        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            throw new BusinessException("Validation failed: " + errors, "VALIDATION_ERROR");
        }
    }

    /**
     * Validate a specific property of an object.
     */
    public static <T> Set<ConstraintViolation<T>> validateProperty(T object, String propertyName) {
        return validator.validateProperty(object, propertyName);
    }

    /**
     * Check if an object is valid.
     */
    public static <T> boolean isValid(T object) {
        return validate(object).isEmpty();
    }
}
