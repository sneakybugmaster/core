package pro.thinhha.core.annotation;

import java.lang.annotation.*;

/**
 * Annotation to specify the ID type for an entity.
 * This is optional and mainly for documentation purposes.
 * The actual ID type is determined by the generic parameter in BaseEntity.
 *
 * Example:
 * <pre>
 * {@code
 * @Entity
 * @IdType(UUID.class)
 * public class Product extends BaseEntity<UUID> {
 *     // entity fields
 * }
 * }
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IdType {
    /**
     * The ID type class (e.g., Long.class, UUID.class)
     */
    Class<?> value();
}
