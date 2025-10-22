package pro.thinhha.core.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

/**
 * Base entity class with Long ID and soft delete support.
 * This is a convenience class that extends GenericSoftDeleteEntity<Long, Long>.
 *
 * For other ID types, you can extend GenericSoftDeleteEntity directly:
 * <pre>
 * {@code
 * // UUID ID
 * @Entity
 * public class Product extends GenericSoftDeleteEntity<UUID, UUID> { }
 *
 * // String ID
 * @Entity
 * public class Product extends GenericSoftDeleteEntity<String, Long> { }
 * }
 * </pre>
 *
 * Or use this class for Long-based IDs:
 * <pre>
 * {@code
 * @Entity
 * public class Product extends SoftDeleteEntity { }
 * }
 * </pre>
 */
@Getter
@Setter
@MappedSuperclass
public abstract class SoftDeleteEntity extends GenericSoftDeleteEntity<Long, Long> {
    // Inherits all functionality from GenericSoftDeleteEntity
}
