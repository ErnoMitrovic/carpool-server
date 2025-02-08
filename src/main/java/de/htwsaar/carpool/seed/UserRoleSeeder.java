package de.htwsaar.carpool.seed;

import de.htwsaar.carpool.domain.user.UserRole;
import de.htwsaar.carpool.repository.UserRoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRoleSeeder {

    private final UserRoleRepository userRoleRepository;

    /**
     * Seeds the `message_status` table with predefined values in the {@link UserRole}` enum.
     */
    @PostConstruct
    public void seedUserRoles() {
        for (UserRole statusValue : UserRole.values()) {
            // Check if the status already exists to avoid duplicates
            if (userRoleRepository.findByName(statusValue.name()).isEmpty()) {
                de.htwsaar.carpool.model.UserRole status = new de.htwsaar.carpool.model.UserRole();
                status.setName(statusValue.name());
                userRoleRepository.save(status);
            }
        }
    }
}
