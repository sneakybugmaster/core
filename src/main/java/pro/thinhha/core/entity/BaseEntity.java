package pro.thinhha.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Base entity class with Long ID and Long audit fields.
 * This is a convenience class that extends GenericBaseEntity<Long, Long>.
 *
 * For other ID types, you can extend GenericBaseEntity directly:
 * <pre>
 * {@code
 * // UUID ID
 * @Entity
 * public class Product extends GenericBaseEntity<UUID, UUID> { }
 *
 * // String ID
 * @Entity
 * public class Product extends GenericBaseEntity<String, Long> { }
 * }
 * </pre>
 *
 * Or use this class for Long-based IDs:
 * <pre>
 * {@code
 * @Entity
 * public class Product extends BaseEntity { }
 * }
 * </pre>
 */
@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity extends GenericBaseEntity<Long, Long> {

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    public Long getId() {
        return super.getId();
    }
}
