package pro.thinhha.core.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.thinhha.core.auth.entity.Role;

import java.util.Optional;

/**
 * Repository interface for Role entity.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Find a role by its name.
     *
     * @param name the role name to search for
     * @return an Optional containing the role if found
     */
    Optional<Role> findByName(String name);

    /**
     * Check if a role name already exists.
     *
     * @param name the role name to check
     * @return true if the role exists, false otherwise
     */
    boolean existsByName(String name);
}
