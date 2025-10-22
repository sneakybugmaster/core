package pro.thinhha.core.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Base entity class with UUID primary key and soft delete support.
 * This is a convenience class that extends GenericSoftDeleteEntity<UUID, UUID>.
 *
 * You can also extend GenericSoftDeleteEntity directly for more flexibility:
 * <pre>
 * {@code
 * // UUID ID with UUID audit
 * @Entity
 * public class Product extends GenericSoftDeleteEntity<UUID, UUID> { }
 *
 * // UUID ID with Long audit
 * @Entity
 * public class Product extends GenericSoftDeleteEntity<UUID, Long> { }
 * }
 * </pre>
 *
 * Or use this convenience class:
 * <pre>
 * {@code
 * @Entity
 * public class Product extends UuidSoftDeleteEntity {
 *     private String name;
 *     private BigDecimal price;
 * }
 * }
 * </pre>
 */
@Getter
@Setter
@MappedSuperclass
public abstract class UuidSoftDeleteEntity extends GenericSoftDeleteEntity<UUID, UUID> {
    // Inherits all functionality from GenericSoftDeleteEntity
}
