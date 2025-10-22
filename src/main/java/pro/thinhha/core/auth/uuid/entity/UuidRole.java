package pro.thinhha.core.auth.uuid.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import pro.thinhha.core.entity.UuidBaseEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Role entity with UUID primary key for role-based access control (RBAC).
 *
 * Use this instead of the Long-based Role entity when you want UUID-based IDs.
 */
@Entity
@Table(name = "roles", indexes = {
        @Index(name = "idx_role_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UuidRole extends UuidBaseEntity {

    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name must not exceed 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(length = 255)
    private String description;

    @ManyToMany(mappedBy = "roles")
    @Builder.Default
    private Set<UuidUser> users = new HashSet<>();

    /**
     * Convenience constructor for creating a role with just a name.
     */
    public UuidRole(String name) {
        this.name = name;
    }

    /**
     * Convenience constructor for creating a role with name and description.
     */
    public UuidRole(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
