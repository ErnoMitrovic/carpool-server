package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<UserRole, Integer> {
    /**
     * Find a role by its name
     * @param name the name of the role
     * @return the role
     */
    UserRole findByName(String name);
}
