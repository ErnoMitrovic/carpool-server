package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.CarpoolUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<CarpoolUser, Long> {
    /**
     * Check if a user exists by email
     * @param email user email
     * @return true if user exists, false otherwise
     */
    boolean existsByEmail(String email);

    /**
     * Find a user by email
     * @param email user email
     * @return the user
     */
    Optional<CarpoolUser> findByEmail(String email);
}
