package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.CarpoolUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<CarpoolUser, String> {
}
