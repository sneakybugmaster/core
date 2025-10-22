package pro.thinhha.core.enums;

import org.springframework.data.domain.Sort;

/**
 * Sort direction enum for pagination.
 */
public enum SortDirection {
    ASC,
    DESC;

    /**
     * Convert to Spring Data Sort.Direction.
     */
    public Sort.Direction toSpringDirection() {
        return this == ASC ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    /**
     * Create from string value.
     */
    public static SortDirection fromString(String value) {
        if (value == null) {
            return ASC;
        }
        try {
            return valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ASC;
        }
    }
}
