package de.htwsaar.carpool.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.io.Serializable;

/**
 * Register either a driver or a passenger
 * @param name
 * @param email
 * @param phone
 * @param universityId
 */
@Builder
public record RegisterUserRequest (
        @NotNull
        String name,
        @Email @NotNull
        String email,
        @NotEmpty
        String password,
        @NotNull @Size(max = 15)
        String phone,
        @NotNull
        Long universityId
) implements Serializable {
}
