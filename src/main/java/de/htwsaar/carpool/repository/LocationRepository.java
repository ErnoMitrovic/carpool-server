package de.htwsaar.carpool.repository;

import de.htwsaar.carpool.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationRepository extends JpaRepository<Location, Integer> {
}
