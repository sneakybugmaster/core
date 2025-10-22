package pro.thinhha.core.auth.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import pro.thinhha.core.entity.BaseEntity;

import java.util.HashSet;
import java.util.Set;

/**
 * Role entity for role-based access control (RBAC).
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
public class Role extends BaseEntity {

    @NotBlank(message = "Role name is required")
    @Size(max = 50, message = "Role name must not exceed 50 characters")
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Size(max = 255, message = "Description must not exceed 255 characters")
    @Column(length = 255)
    private String description;

    @ManyToMany(mappedBy = "roles")
    @Builder.Default
    private Set<User> users = new HashSet<>();

    /**
     * Convenience constructor for creating a role with just a name.
     */
    public Role(String name) {
        this.name = name;
    }

    /**
     * Convenience constructor for creating a role with name and description.
     */
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
