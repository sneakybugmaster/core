package pro.thinhha.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Generic base entity class with soft delete support.
 * Supports any ID type (Long, UUID, String, etc.) and audit field type.
 *
 * Usage examples:
 * <pre>
 * {@code
 * // Long ID with Long audit fields
 * @Entity
 * public class Product extends GenericSoftDeleteEntity<Long, Long> { }
 *
 * // UUID ID with UUID audit fields
 * @Entity
 * public class Order extends GenericSoftDeleteEntity<UUID, UUID> { }
 * }
 * </pre>
 *
 * @param <ID> the type of the primary key (Long, UUID, String, etc.)
 * @param <AUDIT> the type of audit fields (typically same as ID)
 */
@Getter
@Setter
@MappedSuperclass
public abstract class GenericSoftDeleteEntity<ID extends Serializable, AUDIT extends Serializable>
        extends GenericBaseEntity<ID, AUDIT> {

    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private AUDIT deletedBy;

    /**
     * Soft delete this entity.
     */
    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Soft delete this entity with user information.
     *
     * @param deletedBy the ID of the user who deleted this entity
     */
    public void delete(AUDIT deletedBy) {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    /**
     * Restore a soft-deleted entity.
     */
    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
        this.deletedBy = null;
    }
}
