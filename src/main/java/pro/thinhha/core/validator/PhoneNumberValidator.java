package pro.thinhha.core.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pro.thinhha.core.util.StringUtil;

/**
 * Validator implementation for @PhoneNumber annotation.
 */
public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    @Override
    public void initialize(PhoneNumber constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.trim().isEmpty()) {
            return true; // Use @NotNull or @NotBlank for null/empty checks
        }
        return StringUtil.isValidPhone(value);
    }
}
