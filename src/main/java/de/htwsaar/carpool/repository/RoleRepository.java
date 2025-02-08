package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    /**
     * Find a role by its name
     * @param name the name of the role
     * @return the role
     */
    Role findByName(String name);
}
