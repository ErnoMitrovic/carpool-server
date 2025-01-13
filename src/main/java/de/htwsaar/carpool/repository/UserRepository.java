package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.CarpoolUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<CarpoolUser, Integer> {
}
