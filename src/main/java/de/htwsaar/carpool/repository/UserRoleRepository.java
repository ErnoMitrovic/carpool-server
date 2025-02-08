package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {

    /**
     * Finds a UserRole by its name.
     *
     * @param name the name of the message status
     * @return an Optional containing the found UserRole or empty if not found
     */
    Optional<UserRole> findByName(String name);

}
