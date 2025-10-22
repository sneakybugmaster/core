package pro.thinhha.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Base entity class with UUID primary key and UUID audit fields.
 * This is a convenience class that extends GenericBaseEntity<UUID, UUID>.
 *
 * You can also extend GenericBaseEntity directly for more flexibility:
 * <pre>
 * {@code
 * // UUID ID with UUID audit
 * @Entity
 * public class Product extends GenericBaseEntity<UUID, UUID> { }
 *
 * // UUID ID with Long audit
 * @Entity
 * public class Product extends GenericBaseEntity<UUID, Long> { }
 * }
 * </pre>
 *
 * Or use this convenience class:
 * <pre>
 * {@code
 * @Entity
 * public class Product extends UuidBaseEntity {
 *     private String name;
 *     private BigDecimal price;
 * }
 * }
 * </pre>
 */
@Getter
@Setter
@MappedSuperclass
public abstract class UuidBaseEntity extends GenericBaseEntity<UUID, UUID> {

    @Override
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    public UUID getId() {
        return super.getId();
    }
}
