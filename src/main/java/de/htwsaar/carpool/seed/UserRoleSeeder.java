package de.htwsaar.carpool.seed;

import de.htwsaar.carpool.domain.user.UserRoleValue;
import de.htwsaar.carpool.repository.UserRoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRoleSeeder {

    private final UserRoleRepository userRoleRepository;

    /**
     * Seeds the `message_status` table with predefined values in the {@link UserRoleValue}` enum.
     */
    @PostConstruct
    public void seedUserRoles() {
        for (UserRoleValue statusValue : UserRoleValue.values()) {
            // Check if the status already exists to avoid duplicates
            if (userRoleRepository.findByName(statusValue.name()).isEmpty()) {
                de.htwsaar.carpool.model.UserRole status = new de.htwsaar.carpool.model.UserRole();
                status.setName(statusValue.name());
                userRoleRepository.save(status);
            }
        }
    }
}
