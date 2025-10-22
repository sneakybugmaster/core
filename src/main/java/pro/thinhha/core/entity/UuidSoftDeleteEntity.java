package pro.thinhha.core.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base entity class with UUID primary key and soft delete support.
 * Extend this class for entities that should use UUID IDs and be soft-deleted instead of hard-deleted.
 *
 * Example usage:
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
public abstract class UuidSoftDeleteEntity extends UuidBaseEntity {

    @Column(name = "deleted")
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", columnDefinition = "UUID")
    private UUID deletedBy;

    /**
     * Soft delete this entity.
     */
    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    /**
     * Soft delete this entity with user ID.
     *
     * @param deletedBy the UUID of the user who deleted this entity
     */
    public void delete(UUID deletedBy) {
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
