package pro.thinhha.core.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Generic base entity class with audit fields.
 * Supports any ID type (Long, UUID, String, etc.) and audit field type.
 *
 * Usage examples:
 * <pre>
 * {@code
 * // Long ID with Long audit fields
 * @Entity
 * public class Product extends GenericBaseEntity<Long, Long> { }
 *
 * // UUID ID with UUID audit fields
 * @Entity
 * public class Order extends GenericBaseEntity<UUID, UUID> { }
 *
 * // UUID ID with Long audit fields (mixed)
 * @Entity
 * public class Customer extends GenericBaseEntity<UUID, Long> { }
 * }
 * </pre>
 *
 * @param <ID> the type of the primary key (Long, UUID, String, etc.)
 * @param <AUDIT> the type of audit fields (createdBy, updatedBy - typically same as ID)
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class GenericBaseEntity<ID extends Serializable, AUDIT extends Serializable> implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private ID id;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private AUDIT createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private AUDIT updatedBy;

    @Version
    @Column(name = "version")
    private Long version;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
