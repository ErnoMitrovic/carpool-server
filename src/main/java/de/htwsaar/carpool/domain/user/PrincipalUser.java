package de.htwsaar.carpool.domain.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PrincipalUser(@NotNull Integer id, @NotBlank String email, @NotBlank String role) {
}
